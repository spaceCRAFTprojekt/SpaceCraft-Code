package client;
import java.util.ArrayList;
import java.lang.reflect.Field;
/**
 * Ein Player (Client) sendet nur Requests an den Server => im client-package keine Referenzen auf
 * das Server-package!
 */
public class Request{
    /**
     * Liste aller Request-Funktionen:
     * Space.getFocussedMassIndex
     * Space.getMassPos
     * Space.getAllMassPos
     * Space.getAllRadii <= echter Plural
     * Space.getAllOrbits
     * Main.exitIfNoPlayers
     * Sandbox.getPosToPlayer
     * Sandbox.leftclickBlock
     * Sandbox.rightclickBlock
     * Sandbox.getMapIDs
     */
    public static ArrayList<Request> requests=new ArrayList<Request>();
    Player p;
    String todo;
    Object[] params;
    Object waitingFor; //Auf eine Änderung dieses Werts wird gewartet => sollte in der eigentlichen Funktion im Server notify'd werden.
    Object oldWaitingFor;
    /**
     * Player p stellt den Request, dass der Server todo tut, er übergibt die Parameter params.
     * Konvention: todo="Space"/"Craft"+"."+Name der Methode im Server.
     * waitingFor=irgendein Return-Wert
     * Übergabewerte der Methode im Server: p, waitingFor (als ret), params
     * waitingFor muss mindestens ein Attribut haben, damit es funktioniert (darf also auch nicht null sein)
     */
    public Request(Player p, String todo, Object waitingFor, Object... params){
        //https://www.javamex.com/tutorials/wait_notify_how_to.shtml
        synchronized(waitingFor){
            this.p=p;
            this.todo=todo;
            this.waitingFor=waitingFor;
            this.params=params;
            Field[] fields=waitingFor.getClass().getFields();
            Object[] attrs=new Object[fields.length]; //irgendetwas hier muss sich ändern, damit der Request beendet wird
            for (int i=0;i<fields.length;i++){
                try{
                    attrs[i]=fields[i].get(waitingFor);
                    System.out.println(attrs[i]);
                }
                catch(IllegalAccessException e){}
            }
            requests.add(this);
            try{
                boolean br=false;
                while(!br){
                    waitingFor.wait();
                    //System.out.println("Waiting...");
                    for (int i=0;i<fields.length;i++){
                        try{
                            if (fields[i].get(waitingFor)!=attrs[i]){
                                br=true;
                            }
                        }
                        catch(IllegalAccessException e){}
                    }
                }
            }
            catch(InterruptedException e){
                //System.out.println("Interrupted!");
            }
        }
    }
}