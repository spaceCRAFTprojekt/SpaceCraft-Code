package client;
import java.util.ArrayList;
import java.io.Serializable;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
/**
 * Ein Player (Client) sendet nur Requests an den Server => im client-package keine Referenzen auf
 * das Server-package!
 * Wenn ein neuer Socket erstellt wird, muss er zuallererst einen boolean senden, damit der Server weiß, was es für ein Client ist.
 * True steht für Request-Client, false für Task-Client.
 * Liste aller Request-Funktionen - sollte aktualisiert werden, wenn neue dazukommen:
     * Main.exit()
     * Main.exitIfNoPlayers()
     * Main.newPlayer(String name)
     * Main.getPlayer(String name)
     * Main.login()
     * Main.logout()
     * Main.returnFromMenu(String menuName, Object[] menuParams)
     * Main.synchronizePlayerVariable(String varname, Class class, Object value)
     * Main.synchronizePlayerSVariable(String varname, Class class, Object value)
     * Main.synchronizePlayerCVariable(String varname, Class class, Object value) (diese drei setzen Werte von Variablen der Kopie des Players am Server zu dem angegebenen Wert)
     * Main.retrievePlayer() (id wird ja schon mitgegeben) (zur Synchronisierung)
     * Main.writeIntoChat(String message)
     * Main.getChatContent(int numLines)
     * Main.getOtherPlayerTextures(int PlayerID, VektorI upperLeftCorner, VektorI bottomRightCorner)
     * Space.getFocussedMassIndex(VektorD pos, VektorD posToNull, VektorI screenSize, Double scale)
     * Space.getMassPos(Integer index)
     * Space.getAllPos()
     * Space.getAllRadii() <= echter Plural
     * Space.getAllOrbits()
     * Sandbox.breakBlock(Boolean onPlanet, Integer sandboxIndex, VektorI sPos) v0.3.1_AK
     * Sandbox.placeBlock(Boolean onPlanet, Integer sandboxIndex, VektorI sPos, Integer blockID) v0.3.1_AK
     * Sandbox.rightclickBlock(Boolean onPlanet, Integer sandboxIndex, VektorI sPos)
     * Sandbox.getMapIDs(Boolean onPlanet, Integer sandboxIndex, VektorI upperLeftCorner, VektorI bottomRightCorner)
     * Sandbox.getAllSubsandboxTransferData(Boolean onPlanet, Integer sandboxIndex)
     * 
     * (die hier angegebenen Argumente sind nur die aus params, alle Funktionen haben als Übergabewert auch noch die ID des players)
     * Bei Sandbox.*-Methoden ist der erste Parameter aus params playerC.onPlanet, der zweite der SandboxIndex.
 */
public class Request implements Serializable{
    public static final long serialVersionUID=0L;
    public int playerID;
    public String todo;
    public Object[] params;
    public volatile Object ret; //muss wahrscheinlich nicht volatile sein
    public Class retClass;
    /**
     * Der Player mit der gegebenen PlayerID stellt den Request, dass der Server todo tut, er übergibt die Parameter params.
     * Es sollte jedes Mal überprüft werden, ob der Player überhaupt auf dem Client und online ist.
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
     * Übergabewerte der Methode im Server: playerID, params, wobei alle primitiven Parameter zu Objekten konvertiert werden (Arrays sind keine primitiven Objekte.).
     * Über den Nutzen von retClass lässt sich streiten.
     */
    public Request(int playerID, ObjectOutputStream socketOut, ObjectInputStream socketIn, String todo, Class retClass, Object... params){
        if (ClientSettings.PRINT_COMMUNICATION)
            System.out.println("new Request: "+todo);
        if (socketOut!=null && socketIn!=null){
            this.playerID=playerID;
            this.todo=todo;
            this.retClass=retClass;
            this.params=params;
            if (retClass!=null){
                try{
                    synchronized(socketIn){
                        synchronized(socketOut){
                            socketOut.reset();
                            socketOut.writeObject(this);
                            socketOut.flush();
                        }
                        ret=socketIn.readObject();
                    }
                }
                catch(Exception e){
                    System.out.println("Exception when creating request ("+todo+"): "+e);
                }
            }
            else{ //wartet nicht
                try{
                    synchronized(socketOut){
                        socketOut.reset();
                        socketOut.writeUnshared(this);
                        socketOut.flush();
                    }
                }
                catch(Exception e){
                    System.out.println("Exception when creating request ("+todo+"): "+e);
                }
            }
        }
    }
    
    public String toString(){
        return todo;
    }
}