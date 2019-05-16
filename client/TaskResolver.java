package client;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.EOFException;
public class TaskResolver{
    Player p;
    ObjectOutputStream out; //eigentlich sinnlos, da Tasks ja nie etwas zurückgeben
    ObjectInputStream in;
    volatile boolean open; //kann nicht einfach p.isOnline() sein, da ja vor dem Login p.isOnline() falsch ist, der Thread aber trotzdem laufen sollte
    public TaskResolver(Player p){
        this.p=p;
        this.out=p.getTaskOut();
        this.in=p.getTaskIn();
        this.open=true;
        new Thread("TaskResolverThread-"+p.getID()){
            public void run(){
                while(true){
                    if (!open){
                        return;
                    }
                    else{
                        try{
                            Task task=(Task) in.readObject();
                            resolveTask(task);
                        }
                        catch(Exception e){
                            if (e instanceof EOFException){}
                            else if (e instanceof InvocationTargetException){
                                System.out.println("InvocationTargetException when resolving task: "+e.getCause());
                            }
                            else{
                                //beim Schließen gibt es aus irgendeinem Grund SocketExceptions (Socket geschlossen), obwohl eigentlich 
                                //erst der TaskResolver geschlossen wird, dann der Socket (das macht aber nicht viel).
                                System.out.println("Exception when resolving task: "+e);
                            }
                        }
                    }
                }
            }
        }.start();
    }
    
    public void resolveTask(Task task) throws NoSuchMethodException,IllegalAccessException,InvocationTargetException,IllegalArgumentException{
        if (ClientSettings.PRINT_COMMUNICATION){
            System.out.println("Resolving Task "+task.todo);
        }
        String className=task.todo.substring(0,task.todo.indexOf("."));
        String methodName=task.todo.substring(task.todo.indexOf(".")+1);
        Object[] params=new Object[task.params.length];
        for (int i=0;i<task.params.length;i++){
            params[i]=task.params[i];
        }
        Class[] parameterTypes=new Class[params.length];
        for (int i=0;i<params.length;i++){
            parameterTypes[i]=params[i].getClass();
        }
        if (className.equals("Player")){
            Method method=Player.class.getMethod(methodName,parameterTypes);
            method.invoke(p,params);
        }
        //hier können auch noch weitere Klassen folgen
        else{
            throw new IllegalArgumentException("className = "+className+", methodName = "+methodName);
        }
    }
    
    public void close(){
        open=false;
    }
}