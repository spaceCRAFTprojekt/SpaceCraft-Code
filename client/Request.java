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
     * Main.exit
     * Sandbox.getPosToPlayer
     * Sandbox.leftclickBlock
     * Sandbox.rightclickBlock
     * Sandbox.getMapIDs
     */
    public static ArrayList<Request> requests=new ArrayList<Request>();
    public Player p;
    public String todo;
    public Object[] params;
    public Object waitingFor; //Auf eine Änderung dieses Werts wird gewartet => sollte in der eigentlichen Funktion im Server notify'd werden (siehe server.RequestResolver)
    /**
     * Player p stellt den Request, dass der Server todo tut, er übergibt die Parameter params.
     * Konvention: todo=Klassenname+"."+Methodenname
     * waitingFor=irgendein Return-Wert
     * Übergabewerte der Methode im Server: p, waitingFor (als ret), params
     * waitingFor muss mindestens ein Attribut haben, damit es funktioniert (darf also auch nicht null sein)
     * Bei Sandbox.*-Methoden ist der erste Parameter aus params playerC.onPlanet, der zweite der SandboxIndex.
     * Eigentlich sind viele params unnötig, da ja der Player mitübergeben wird, aus diesem lassen sich die meisten params auch ziehen.
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