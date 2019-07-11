package server;
import client.Request;
import client.Task;
import client.ClientSettings;
import java.util.Hashtable;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.EOFException;
/**
 * Diese Klasse erledigt die ganzen Formalia, die mit dem Server zu tun haben:
 * * erstellt einen ServerSocket, der auf Clients wartet, die dann in Request- und Task-Clients eingeteilt werden,
 *   abhängig von dem ersten boolean, den sie senden müssen
 * * Es gibt pro Spieler 2 Sockets/Clients, einen für Tasks (die vom Server ausgehen) und einen für Requests (die vom Client ausgehen).
 * * Der outputStream eines Task-Clients wird in taskOutputStreams mit dem Index der ID des Players geschrieben, 
 *   so dass mit Main.newTask (das intern ServerCreator.sendTask aufruft) Tasks an den Client geschickt werden können.
 *   Für eine vermutlich nicht vollständige Liste aller Tasks siehe client.Task
 * * (ein deutlich wichtigerer) Request-Client bekommt einen eigenen Thread, der nur auf Requests wartet und diese dann ausführt.
 *   Das ist vermutlich größtenteils ziemlich unsicher.
 *   Sollte ein Request-Client lange keinen Request schreiben (das ist der Fall für 2 Request-Clients, die für Player.login und Player.newPlayer
 *   benötigt werden, wird dieser Socket und der dazugehörige Thread geschlossen.
 */
public class ServerCreator{
    Main main;
    ServerSocket server;
    Hashtable<Integer,ObjectOutputStream> taskOutputStreams;
    ArrayList<StoppableThread> threads;
    public ServerCreator(Main main) throws Exception{
        this.main=main;
        this.server=new ServerSocket(Settings.SERVER_PORT);
        this.taskOutputStreams=new Hashtable<Integer,ObjectOutputStream>();
        this.threads=new ArrayList<StoppableThread>();
        StoppableThread clientConnectionThread=new StoppableThread("ClientConnectionThread"){
            public void run(){
                while(!shouldStop){
                    try{
                        Socket client=server.accept();
                        ObjectOutputStream out=new ObjectOutputStream(client.getOutputStream());
                        synchronized(out){
                            out.flush();
                        }
                        ObjectInputStream in=new ObjectInputStream(client.getInputStream());
                        boolean isRequestClient=in.readBoolean(); //sonst: taskClient
                        int playerID=in.readInt();
                        if (isRequestClient){
                            StoppableThread t=new StoppableThread("resolveRequestsThread-"+playerID){
                                long timeOfLastAction=System.currentTimeMillis(); //Wenn das zu lange her ist, dann wird der Thread geschlossen
                                public void run(){
                                    while(!shouldStop){
                                        try{
                                            Request req=(Request) in.readObject();
                                            if ((playerID==-1 && (req.todo.equals("Main.newPlayer") || req.todo.equals("Main.getPlayer")))
                                                    || (main.getPlayer(playerID)!=null && (req.todo.equals("Main.login") || main.getPlayer(playerID).isOnline()))){
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
                                                timeOfLastAction=System.currentTimeMillis();
                                            }
                                            else{ //Jemand versucht, zu betrügen, hier am Server kann nämlich ein Player nur isOnline()=true zurückgeben, 
                                                  //wenn er sich vorher erfolgreich (mit dem richtigen Passwort) eingeloggt hat
                                                  //Ausnahmen sind login und mit playerID -1 Main.newPlayer und Main.getPlayer-Requests (die finden bereits im Login-Menü statt)
                                                System.out.println("[Server]: Sehr Witzig du Betrüger "+req);
                                                client.close();
                                                return;
                                            }
                                        }
                                        catch(Exception e){
                                            if (e instanceof EOFException){}
                                            else if (e instanceof InvocationTargetException){
                                                System.out.println("[Server]: InvocationTargetException when resolving request: "+e.getCause());
                                                e.printStackTrace();
                                            }
                                            else{
                                                System.out.println("[Server]: Exception when resolving request: "+e);
                                            }
                                        }
                                        if (System.currentTimeMillis()-timeOfLastAction>Settings.REQUEST_THREAD_TIMEOUT){
                                            if (playerID!=-1){
                                                main.getPlayer(playerID).logout();
                                                main.newTask(playerID,"logoutTask");
                                            }
                                            return;
                                        }
                                    }
                                }
                            };
                            t.start();
                            threads.add(t);
                        }
                        else{
                            taskOutputStreams.put(playerID,out);
                        }
                    }
                    catch(Exception e){
                        System.out.println("[Server]: Exception when waiting for clients: "+e);
                    }
                }
            }
        };
        clientConnectionThread.start();
        threads.add(clientConnectionThread);
    }
    
    /**
     * Führt Requests aus, die der Server erhält
     */
    public Object resolveRequest(Request req) throws NoSuchMethodException,IllegalAccessException,InvocationTargetException,IllegalArgumentException{
        if (ClientSettings.PRINT_COMMUNICATION){
            System.out.println("[Server]: Resolving Request "+req);
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
            int sandboxIndex=(int) params[1];
            Method method=Sandbox.class.getMethod(methodName,parameterTypes);
            req.ret=method.invoke(main.getSandbox(sandboxIndex),params);
        }
        //hier können auch noch weitere Klassen folgen
        else{
            throw new IllegalArgumentException("className = "+className+", methodName = "+methodName);
        }
        return req.ret;
    }
    
    /**
     * wird von Main.newTask aufgerufen
     */
    public void sendTask(int playerID, Task task){
        if (main.getPlayer(playerID).isOnline()){
            try{
                ObjectOutputStream tos=taskOutputStreams.get(playerID);
                synchronized(tos){
                    tos.reset();
                    tos.writeObject(task);
                    tos.flush();
                }
            }
            catch(Exception e){
                System.out.println("[Server]: Exception when sending Task: "+e);
            }
        }
    }
    
    public static class StoppableThread extends Thread{
        boolean shouldStop=false;
        public StoppableThread(String name){
            super(name);
        }
    }
}