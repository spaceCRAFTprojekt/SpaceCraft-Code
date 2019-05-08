package client;
import java.util.ArrayList;
/**
 * Ein Player (Client) sendet nur Requests an den Server => im client-package keine Referenzen auf
 * das Server-package!
 */
public class Request{
    /**
     * Liste aller Request-Funktionen - sollte aktualisiert werden, wenn neue dazukommen:
     * Main.exitIfNoPlayers()
     * Main.exit()
     * Main.login()
     * Main.logout()
     * Main.retrieveBlockImages()
     * Main.returnFromMenu(String menuName, Object[] menuParams)
     * Space.getFocussedMassIndex(VektorD pos, VektorD posToNull, VektorI screenSize, Double scale)
     * Space.getMassPos(Integer index)
     * Space.getAllPos()
     * Space.getAllRadii() <= echter Plural
     * Space.getAllOrbits()
     * Sandbox.leftclickBlock(Boolean onPlanet, Integer sandboxIndex, VektorI sPos)
     * Sandbox.rightclickBlock(Boolean onPlanet, Integer sandboxIndex, VektorI sPos)
     * Sandbox.getMapIDs(Boolean onPlanet, Integer sandboxIndex, VektorI upperLeftCorner, VektorI bottomRightCorner)
     * 
     * (die hier angegebenen Argumente sind nur die aus params, alle Funktionen haben als Übergabewert auch noch den Player p)
     * Bei Sandbox.*-Methoden ist der erste Parameter aus params playerC.onPlanet, der zweite der SandboxIndex.
     * Aus irgendeinem Grund (der, fürchte ich, mit hässlicher Multithreaderei zu tun hat) kann man keine Methoden des Players aufrufen. 
     * (Unter anderem deshalb gibt es überhaupt so viele params)
     */
    public static ArrayList<Request> requests=new ArrayList<Request>();
    public Player p;
    public String todo;
    public Object[] params;
    public Object ret;
    public Class retClass;
    public boolean finished; //Wenn diese Variable auf true gesetzt wird, hört der Thread auf zu warten.
    public Thread thread; //der wartende Thread
    /**
     * Player p stellt den Request, dass der Server todo tut, er übergibt die Parameter params.
     * Konvention: todo=Klassenname+"."+Methodenname
     * ret=irgendein Rückgabewert (der formale Rückgabewert ist void)
     * (    <IrgendeineKlasse> obj = <IrgendeinKonstruktorOderNull>;
     *      Request req = new Request(p, todo, <IrgendeineKlasse>.class, params);
     *      obj = (<CastAufIrgendeineKlasse>) req.ret;
     *      req = null; //nur Code-Stil, da der Request jetzt nutzlos geworden ist
     *      oder so ähnlich
     * )
     * Da alle Request-Methoden also ein Rückgabeobjekt haben müssen, ist hiermit Konvention, dass es bei eigentlichen void-Methoden ein Boolean (Objekt) ist.
     * (wird true, wenn der Request erfolgreich war.)
     * Bis dieser Request notify'd wird (passiert, wenn kein Fehler auftritt, in server.RequestResolver), wartet der Thread.
     * Übergabewerte der Methode im Server: p, params, wobei alle primitiven Parameter zu Objekten konvertiert werden (Arrays sind keine primitiven Objekte.).
     * Eigentlich ist retClass (noch) nicht notwendig.
     */
    public Request(Player p, String todo, Class retClass, Object... params){
        //System.out.println("new Request: "+todo);
        //https://www.javamex.com/tutorials/wait_notify_how_to.shtml
        synchronized(this){
            this.p=p;
            this.todo=todo;
            this.retClass=retClass;
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