package client;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
/**
 * recht ähnlich zu server.RequestResolver
 */
public class TaskResolver{
    Player p;
    Timer t;
    public TaskResolver(Player p){
        this.p=p;
        t=new Timer();
        t.schedule(new TimerTask(){
            public void run(){
                while(Task.tasks.size()>0){
                    try{
                        for (int i=0;i<Task.tasks.size();i++){
                            Task task=Task.tasks.get(i);
                            if (task.playerID==p.getID()){
                                Task.tasks.remove(i);
                                resolveTask(task);
                            }
                        }
                    }
                    catch(Exception e){
                        if (e instanceof InvocationTargetException){
                            System.out.println("InvocationTargetException when resolving task: "+e.getCause());
                        }
                        else{
                            System.out.println("Exception when resolving task: "+e);
                        }
                    }
                }
            }
        },ClientSettings.TASK_RESOLVE_PERIOD,ClientSettings.TASK_RESOLVE_PERIOD);
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
            //if (params[i]!=null){
                parameterTypes[i]=params[i].getClass();
            /*}
            else{
                parameterTypes[i]=Object.class;
            }*/
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
}