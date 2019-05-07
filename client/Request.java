package client;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.io.Serializable;
/**
 * Ein Player (Client) sendet nur Requests an den Server => im client-package keine Referenzen auf
 * das Server-package!
 */
public class Request{
    /**
     * Liste aller Request-Funktionen - sollte aktualisiert werden, wenn neue dazukommen:
     * Space.getFocussedMassIndex(VektorD pos, VektorI screenSize, double scale)
     * Space.getMassPos(int index)
     * Space.getAllPos()
     * Space.getAllRadii() <= echter Plural
     * Space.getAllOrbits()
     * Main.exitIfNoPlayers()
     * Main.exit()
     * Sandbox.leftclickBlock(boolean onPlanet, int sandboxIndex, VektorI sPos)
     * Sandbox.rightclickBlock(boolean onPlanet, int sandboxIndex, VektorI sPos)
     * Sandbox.getMapIDs(boolean onPlanet, int sandboxIndex, VektorI upperLeftCorner, VektorI bottomRightCorner)
     * 
     * (die hier angegebenen Argumente sind nur die aus params, alle Funktionen haben als Übergabewert auch noch Player p, Object waitingFor(=Object ret))
     * Bei Sandbox.*-Methoden ist der erste Parameter aus params playerC.onPlanet, der zweite der SandboxIndex.
     * Eigentlich sind viele params unnötig, da ja der Player mitübergeben wird, aus diesem lassen sich die meisten params auch ziehen.
     */
    public static ArrayList<Request> requests=new ArrayList<Request>();
    public Player p;
    public String todo;
    public Object[] params;
    public Object waitingFor; //Auf eine Änderung dieses Werts wird gewartet => sollte in der eigentlichen Funktion im Server notify'd werden (siehe server.RequestResolver)
    public Thread thread; //der wartende Thread
    /**
     * Player p stellt den Request, dass der Server todo tut, er übergibt die Parameter params.
     * Konvention: todo=Klassenname+"."+Methodenname
     * waitingFor=irgendein Rückgabewert (der formale Rückgabewert ist void)
     * Bis waitingFor notify'd wird (passiert, wenn kein Fehler auftritt, in server.RequestResolver), wartet der Thread.
     * Übergabewerte der Methode im Server: p, waitingFor (als ret), params
     * waitingFor muss mindestens ein Attribut haben, damit es funktioniert (darf also auch nicht null sein).
     * waitingFor sollte den gleichen Datentyp wie das "returnte" Objekt haben (sonst gibt es vermutlich einen Fehler mit dem Felderabgleich).
     * waitingFor muss sich in der Funktion irgendwie ändern, sonst hört das Programm mit dem Warten nicht auf!
     */
    public Request(Player p, String todo, Object waitingFor, Object... params){
        System.out.println("new Request: "+todo);
        //https://www.javamex.com/tutorials/wait_notify_how_to.shtml
        synchronized(waitingFor){
            this.p=p;
            this.todo=todo;
            this.waitingFor=waitingFor;
            this.params=params;
            this.thread=Thread.currentThread();
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
                    for (int i=0;i<fields.length;i++){
                        try{
                            System.out.println(fields[i].getName()+": "+fields[i].get(waitingFor)+" "+attrs[i]);
                            if (fields[i].get(waitingFor)!=attrs[i]){
                                br=true;
                            }
                        }
                        catch(IllegalAccessException e){
                            System.out.println("Illegal Access!");
                        }
                    }
                }
                System.out.println("Finished with waiting");
            }
            catch(InterruptedException e){
                System.out.println("Interrupted!");
            }
        }
    }
}