package server;
import util.geom.*;
import java.io.File;
public abstract class Settings{
    //Klasse für alle Konstanten. Ob diese dann aber alle einstellbar sind, ist noch nicht sicher.
    public static String GAMESAVE_FOLDER="."+File.separator+"gamesaves";
    public static int CRAFT_PISTON_PUSH_LIMIT = 4;  // Anzahl der Blöcke, die ein Piston nach vorne bewegen kann
    public static int SERVER_PORT=30000;
    public static long REQUEST_THREAD_TIMEOUT=10000;
}