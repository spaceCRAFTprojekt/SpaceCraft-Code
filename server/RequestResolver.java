package server;
import client.Request;
import client.ClientSettings;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
public class RequestResolver{
    Main main;
    Timer t;
    public RequestResolver(Main main){
        this.main=main;
        t=new Timer();
        t.schedule(new TimerTask(){
            public void run(){
                while(Request.requests.size()>0){
                    try{
                        synchronized(Request.requests){
                            resolveRequest(Request.requests.remove(0));
                        }
                    }
                    catch(Exception e){
                        if (e instanceof InvocationTargetException){
                            System.out.println("InvocationTargetException when resolving request: "+e.getCause());
                        }
                        else{
                            System.out.println("Exception when resolving request: "+e);
                        }
                    }
                }
            }
        },Settings.REQUEST_RESOLVE_PERIOD,Settings.REQUEST_RESOLVE_PERIOD);
    }
    
    public void resolveRequest(Request req) throws NoSuchMethodException,IllegalAccessException,InvocationTargetException,IllegalArgumentException{
        if (ClientSettings.PRINT_COMMUNICATION){
            System.out.println("Resolving Request "+req);
        }
        if (req.retClass!=null){
            synchronized(req){
                if (req.thread.getState()==Thread.State.WAITING){ //Ist das nötig?
                    String className=req.todo.substring(0,req.todo.indexOf("."));
                    String methodName=req.todo.substring(req.todo.indexOf(".")+1);
                    Object[] params=new Object[req.params.length+1];
                    params[0]=req.playerID;
                    for (int i=0;i<req.params.length;i++){
                        params[i+1]=req.params[i];
                    }
                    Class[] parameterTypes=new Class[params.length];
                    for (int i=0;i<params.length;i++){
                        //if (params[i]!=null){
                            parameterTypes[i]=params[i].getClass();
                        /*}
                        else{
                            parameterTypes[i]=Object.class;
                        }*/
                    }
                    if (className.equals("Main")){
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
                    req.finished=true;
                    req.notify();
                    //System.out.println("Notified!");
                }
                else{
                    //System.out.println("Thread not waiting");
                }
            }
        }
        else{
            if (ClientSettings.PRINT_COMMUNICATION){
                System.out.println("(Client is not waiting)");
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
                method.invoke(main,params);
            }
            //hier können auch noch weitere Klassen folgen
            else{
                throw new IllegalArgumentException("className = "+className+", methodName = "+methodName);
            }
            req.finished=true;
        }
    }
}