package server;
import client.*;
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
                        resolveRequest(Request.requests.remove(0));
                    }
                    catch(Exception e){
                        System.out.println("Exception when resolving request: "+e+" "+e.getMessage());
                    }
                }
            }
        },Settings.REQUEST_RESOLVE_PERIOD,Settings.REQUEST_RESOLVE_PERIOD);
    }
    
    public void resolveRequest(Request req) throws NoSuchMethodException,IllegalAccessException,InvocationTargetException,IllegalArgumentException{
        synchronized(req.waitingFor){
            if (req.thread.getState()==Thread.State.WAITING){
                String className=req.todo.substring(0,req.todo.indexOf("."));
                String methodName=req.todo.substring(req.todo.indexOf(".")+1);
                Object[] params=new Object[req.params.length+2];
                params[0]=req.p;
                params[1]=req.waitingFor;
                for (int i=0;i<req.params.length;i++){
                    params[i+2]=req.params[i];
                }
                Class[] parameterTypes=new Class[params.length];
                for (int i=0;i<params.length;i++){
                    parameterTypes[i]=params[i].getClass();
                }
                if (className.equals("Space")){
                    Method method=Space.class.getMethod(methodName,parameterTypes);
                    method.invoke(main.getSpace(),params);
                }
                else if (className.equals("Main")){
                    Method method=Main.class.getMethod(methodName,parameterTypes);
                    method.invoke(main,params);
                }
                else if (className.equals("Sandbox")){
                    boolean onPlanet=(boolean) params[2];
                    int sandboxIndex=(int) params[3];
                    if (onPlanet){
                        Method method=PlanetC.class.getMethod(methodName,parameterTypes);
                        method.invoke(PlanetC.planetCs.get(sandboxIndex),params);
                    }
                    else{
                        Method method=ShipC.class.getMethod(methodName,parameterTypes);
                        method.invoke(ShipC.shipCs.get(sandboxIndex),params);
                    }
                }
                //hier können auch noch weitere Klassen folgen
                else{
                    throw new IllegalArgumentException("className = "+className+", methodName = "+methodName);
                }
                req.waitingFor.notify();
                System.out.println("Notified!");
            }
            else{
                System.out.println("Thread not waiting");
            }
        }
    }
}