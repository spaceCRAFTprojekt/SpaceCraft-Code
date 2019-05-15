package server;
import client.Request;
import client.Task;
import client.ClientSettings;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.EOFException;
public class ServerCreator{
    Main main;
    ArrayList<ObjectOutputStream> taskOutputStreams;
    public ServerCreator(Main main){
        this.main=main;
        this.taskOutputStreams=new ArrayList<ObjectOutputStream>();
        try{
            new Thread("ClientConnectionThread"){
                public ServerSocket server=new ServerSocket(Settings.SERVER_PORT);
                public void run(){
                    while(true){
                        try{
                            Socket client=server.accept();
                            ObjectOutputStream out=new ObjectOutputStream(client.getOutputStream());
                            synchronized(out){
                                out.flush();
                            }
                            ObjectInputStream in=new ObjectInputStream(client.getInputStream());
                            boolean isRequestClient=in.readBoolean(); //sonst: taskClient
                            if (isRequestClient){
                                new Thread("requestClientThread"){
                                    public void run(){
                                        while(true){
                                            //sollte eigentlich aufhören, wenn der Client geschlossen wird, aber das funktioniert aus irgendeinem Grund nicht
                                            try{
                                                Request req=(Request) in.readObject();
                                                if (req.retClass!=null){
                                                    Object ret=resolveRequest(req);
                                                    synchronized(out){
                                                        //das reset() ist notwendig, da sonst eine Referenz geschrieben wird => Übertragung falscher (zu alter) Attribute
                                                        out.reset();
                                                        out.writeObject(ret);
                                                        out.flush();
                                                    }
                                                }
                                                else{
                                                    resolveRequest(req);
                                                }
                                            }
                                            catch(Exception e){
                                                if (e instanceof EOFException){}
                                                else if (e instanceof InvocationTargetException){
                                                    System.out.println("InvocationTargetException when resolving request: "+e.getCause());
                                                }
                                                else{
                                                    System.out.println("Exception when resolving request: "+e);
                                                }
                                            }
                                        }
                                    }
                                }.start();
                            }
                            else{
                                int playerID=in.readInt();
                                taskOutputStreams.add(playerID,out);
                            }
                        }
                        catch(Exception e){
                            System.out.println("Exception when waiting for clients: "+e);
                        }
                    }
                }
            }.start();
        }
        catch(Exception e){}
    }
    
    public Object resolveRequest(Request req) throws NoSuchMethodException,IllegalAccessException,InvocationTargetException,IllegalArgumentException{
        if (ClientSettings.PRINT_COMMUNICATION){
            System.out.println("Resolving Request "+req);
        }
        String className=req.todo.substring(0,req.todo.indexOf("."));
        String methodName=req.todo.substring(req.todo.indexOf(".")+1);
        Object[] params=new Object[req.params.length+1];
        params[0]=req.playerID;
        for (int i=0;i<req.params.length;i++){
            params[i+1]=req.params[i];
        }
        Class[] parameterTypes=new Class[params.length];
        for (int i=0;i<params.length;i++){
            parameterTypes[i]=params[i].getClass();
        }
        if (className.equals("Main")){
            if (methodName.equals("synchronizePlayerVariable") || methodName.equals("synchronizePlayerSVariable") || methodName.equals("synchronizePlayerCVariable")){
                //Diese Methoden nehmen formal Objects als Parameter, das muss auch so in den ParameterTypes stehen
                parameterTypes[3]=Object.class;
            }
            Method method=Main.class.getMethod(methodName,parameterTypes);
            req.ret=method.invoke(main,params);
        }
        else if (className.equals("Space")){
            Method method=Space.class.getMethod(methodName,parameterTypes);
            req.ret=method.invoke(main.getSpace(),params);
        }
        else if (className.equals("Sandbox")){
            boolean onPlanet=(boolean) params[1];
            int sandboxIndex=(int) params[2];
            if (onPlanet){
                Method method=PlanetC.class.getMethod(methodName,parameterTypes);
                req.ret=method.invoke(PlanetC.planetCs.get(sandboxIndex),params);
            }
            else{
                Method method=ShipC.class.getMethod(methodName,parameterTypes);
                req.ret=method.invoke(ShipC.shipCs.get(sandboxIndex),params);
            }
        }
        //hier können auch noch weitere Klassen folgen
        else{
            throw new IllegalArgumentException("className = "+className+", methodName = "+methodName);
        }
        return req.ret;
    }
    
    public void sendTask(int playerID, Task task){
        try{
            ObjectOutputStream tos=taskOutputStreams.get(playerID);
            synchronized(tos){
                tos.reset();
                tos.writeObject(task);
                tos.flush();
            }
        }
        catch(IndexOutOfBoundsException e){
            System.out.println("Player "+playerID+" is not online, so cannot retrieve any tasks.");
        }
        catch(Exception e){
            System.out.println("Exception when sending Task: "+e);
        }
    }
}