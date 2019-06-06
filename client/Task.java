package client;
import java.util.ArrayList;
import java.io.Serializable;
/**
 * Tasks kommen vom Server und müssen hier erledigt werden. (Falls jemand das hier hacken will:
 * Wie du vielleicht sehen kannst, stehen in den Player-Klassen ohnehin keine wichtigen (=spielentscheidenden) Sachen.
 * Du kannst also das Spiel für dich im Wesentlichen nur kaputt machen.)
 * Tasks geben nichts zurück und der Server wartet nie auf den Client.
 */
public class Task implements Serializable{
    /**
     * Liste aller Task-Funktionen - sollte aktualisiert werden, wenn neue dazukommen:
     * Player.showMenu(String menuName, Object[] menuParams)
     * Player.repaint()
     * Player.addChatMsg(String msg)
     * Player.logoutTask() (bei Main.exit o.Ä.)
     * 
     */
    public static final long serialVersionUID=0L;
    public String todo;
    public Object[] params;
    /**
     * recht ähnlich zu Request
     * Übergabewerte der Methode: params, wobei alle primitiven Parameter zu Objekten konvertiert werden (Arrays sind keine primitiven Objekte.).
     * Das sollte nicht verwendet werden, um einen Task zu erzeugen (würde nirgendwo hin gesendet werden), sondern Main.newTask.
     */
    public Task(String todo, Object... params){
        //System.out.println("new Task: "+todo);
        this.todo=todo;
        this.params=params;
    }
}