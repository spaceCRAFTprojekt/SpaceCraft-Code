package client;
import java.util.ArrayList;
/**
 * Tasks kommen vom Server und müssen hier erledigt werden. (Falls jemand das hier hacken will:
 * Wie du vielleicht sehen kannst, stehen in den Player-Klassen ohnehin keine wichtigen Sachen.
 * Du kannst also das Spiel für dich im Wesentlichen nur kaputt machen.)
 * Tasks geben nichts zurück und der Server wartet nie auf den Client.
 */
public class Task{
    /**
     * Liste aller Task-Funktionen - sollte aktualisiert werden, wenn neue dazukommen:
     * Player.synchronize(Player p)
     * Player.showMenu(String menuName, Object[] menuParams)
     */
    public static ArrayList<Task> tasks=new ArrayList<Task>();
    public String todo;
    public Object[] params;
    public int playerID;
    /**
     * recht ähnlich zu Request
     * Übergabewerte der Methode: params, wobei alle primitiven Parameter zu Objekten konvertiert werden (Arrays sind keine primitiven Objekte.).
     * Eigentlich ist retClass (noch) nicht notwendig.
     */
    public Task(int playerID, String todo, Object... params){
        //System.out.println("new Task: "+todo);
        this.todo=todo;
        this.params=params;
        tasks.add(this);
    }
}