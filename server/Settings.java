package server;
import util.geom.*;
import java.io.File;
public abstract class Settings{
    //Klasse für alle Konstanten. Ob diese dann aber alle einstellbar sind, ist noch nicht sicher.
    public static String GAMESAVE_FOLDER="."+File.separator+"gamesaves";
    public static double G=6.674*Math.pow(10,-4); //eigentlich *10^(-11)
    public static long SPACE_TIMER_PERIOD=1000; //in Millisekunden
    public static long REQUEST_RESOLVE_PERIOD=1; //in Millisekunden
    public static double SPACE_CALC_PERIOD_INGAME=0.1; //in Sekunden, so oft werden die Bahnen mit dem newtonschen Gravitationsgesetz berechnet
    public static int CRAFT_PISTON_PUSH_LIMIT = 4;  // Anzahl der Blöcke, die ein Piston nach vorne bewegen kann
    public static int SERVER_PORT=30000;
}