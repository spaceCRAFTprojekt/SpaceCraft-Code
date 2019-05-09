package client;
import java.util.ArrayList;
import java.io.Serializable;
/**
 * Ein Player (Client) sendet nur Requests an den Server => im client-package keine Referenzen auf
 * das Server-package!
 */
public class Request implements Serializable{
    /**
     * Liste aller Request-Funktionen - sollte aktualisiert werden, wenn neue dazukommen:
     * Main.exit()
     * Main.exitIfNoPlayers()
     * Main.newPlayer(String name)
     * Main.login()
     * Main.logout()
     * Main.retrieveBlockImages()
     * Main.returnFromMenu(String menuName, Object[] menuParams)
     * Main.synchronizePlayerVariable(String varname, Class class, Object value)
     * Main.synchronizePlayerSVariable(String varname, Class class, Object value)
     * Main.synchronizePlayerCVariable(String varname, Class class, Object value) (diese drei setzen Werte von Variablen der Kopie des Players am Server zu dem angegebenen Wert)
     * Main.retrievePlayer() (id wird ja schon mitgegeben) (zur Synchronisierung)
     * Space.getFocussedMassIndex(VektorD pos, VektorD posToNull, VektorI screenSize, Double scale)
     * Space.getMassPos(Integer index)
     * Space.getAllPos()
     * Space.getAllRadii() <= echter Plural
     * Space.getAllOrbits()
     * Sandbox.leftclickBlock(Boolean onPlanet, Integer sandboxIndex, VektorI sPos)
     * Sandbox.rightclickBlock(Boolean onPlanet, Integer sandboxIndex, VektorI sPos)
     * Sandbox.getMapIDs(Boolean onPlanet, Integer sandboxIndex, VektorI upperLeftCorner, VektorI bottomRightCorner)
     * 
     * (die hier angegebenen Argumente sind nur die aus params, alle Funktionen haben als Übergabewert auch noch die ID des players)
     * Bei Sandbox.*-Methoden ist der erste Parameter aus params playerC.onPlanet, der zweite der SandboxIndex.
     */
    public static ArrayList<Request> requests=new ArrayList<Request>();
    public int playerID;
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
     * Wenn retClass gleich null ist, dann ist es ein Request, auf dessen Antwort nicht gewartet wird
     * (dieser hat dann auch keinen Rückgabewert) (z.B. Main.synchronizePlayerVariable).
     * Da alle Request-Methoden, auf die gewartet wird, also ein Rückgabeobjekt haben müssen, ist hiermit Konvention, 
     * dass es bei eigentlichen void-Methoden ein Boolean (Objekt) ist (wird true, wenn der Request erfolgreich war).
     * Bis dieser Request notify'd wird (passiert, wenn kein Fehler auftritt, in server.RequestResolver), wartet der Thread sonst.
     * Übergabewerte der Methode im Server: playerID, params, wobei alle primitiven Parameter zu Objekten konvertiert werden (Arrays sind keine primitiven Objekte.).
     * Über den Nutzen von retClass lässt sich streiten.
     */
    public Request(int playerID, String todo, Class retClass, Object... params){
        //System.out.println("new Request: "+todo);
        //https://www.javamex.com/tutorials/wait_notify_how_to.shtml
        if (retClass!=null){
            synchronized(this){
                this.playerID=playerID;
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
        else{ //wartet nicht
            this.playerID=playerID;
            this.todo=todo;
            this.retClass=retClass;
            this.params=params;
            this.thread=Thread.currentThread();
            this.finished=false;
            requests.add(this);
        }
    }
}