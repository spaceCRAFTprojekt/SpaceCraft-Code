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
     * Main.exitIfNoPlayers()
     * Main.exit()
     * Space.getFocussedMassIndex(VektorD pos, VektorI screenSize, double scale)
     * Space.getMassPos(int index)
     * Space.getAllPos()
     * Space.getAllRadii() <= echter Plural
     * Space.getAllOrbits()
     * Sandbox.leftclickBlock(boolean onPlanet, int sandboxIndex, VektorI sPos)
     * Sandbox.rightclickBlock(boolean onPlanet, int sandboxIndex, VektorI sPos)
     * Sandbox.getMapIDs(boolean onPlanet, int sandboxIndex, VektorI upperLeftCorner, VektorI bottomRightCorner)
     * 
     * (die hier angegebenen Argumente sind nur die aus params, alle Funktionen haben als Übergabewert auch noch Player p, Object ret)
     * Bei Sandbox.*-Methoden ist der erste Parameter aus params playerC.onPlanet, der zweite der SandboxIndex.
     * Eigentlich sind viele params unnötig, da ja der Player mitübergeben wird, aus diesem lassen sich die meisten params auch ziehen.
     */
    public static ArrayList<Request> requests=new ArrayList<Request>();
    public Player p;
    public String todo;
    public Object[] params;
    public Object ret;
    public boolean finished; //Wenn diese Variable auf true gesetzt wird, hört der Thread auf zu warten.
    public Thread thread; //der wartende Thread
    /**
     * Player p stellt den Request, dass der Server todo tut, er übergibt die Parameter params.
     * Konvention: todo=Klassenname+"."+Methodenname
     * ret=irgendein Rückgabewert (der formale Rückgabewert ist void)
     * Die Referenz des eigentlich gewollten Objekts muss immer aktualisiert werden, da alle diese Methoden (Konvention) per Kopie arbeiten.
     * (Das bedeutet auch, dass in jeder Request-Funktion zu Beginn erst einmal ret=<IrgendeinKonstruktor> steht.)
     * (Was in ret enthalten ist ist also eigentlich vollkommen egal, wichtig ist nur die Klasse von ret.)
     * (    <IrgendeineKlasse> obj = <IrgendeinKonstruktorOderNull>;
     *      Request req = new Request(p, todo, obj, params);
     *      obj = (<CastAufIrgendeineKlasse>) req.ret;
     *      req = null; //nur Code-Stil, da der Request jetzt nutzlos geworden ist
     *      oder so ähnlich
     * )
     * Da alle Request-Methoden also ein Rückgabeobjekt haben müssen, ist hiermit Konvention, dass es bei eigentlichen void-Methoden ein Boolean (Objekt) ist.
     * (wird true, wenn der Request erfolgreich war.)
     * Bis dieser Request notify'd wird (passiert, wenn kein Fehler auftritt, in server.RequestResolver), wartet der Thread.
     * Übergabewerte der Methode im Server: p, ret, params, wobei alle primitiven Parameter zu Objekten konvertiert werden (Arrays sind keine primitiven Objekte.).
     */
    public Request(Player p, String todo, Object ret, Object... params){
        //System.out.println("new Request: "+todo);
        //https://www.javamex.com/tutorials/wait_notify_how_to.shtml
        synchronized(this){
            this.p=p;
            this.todo=todo;
            this.ret=ret;
            this.params=params;
            this.thread=Thread.currentThread();
            this.finished=false;
            requests.add(this);
            try{
                boolean br=false;
                while(!br){
                    //System.out.println("Waiting...");
                    this.wait();
                    if (this.finished==true){
                        br=true;
                    }
                }
                //System.out.println("Finished with waiting");
            }
            catch(InterruptedException e){
                //System.out.println("Interrupted");
            }
        }
    }
}