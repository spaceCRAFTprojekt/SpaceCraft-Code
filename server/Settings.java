package server;
import util.geom.*;
import java.io.File;
public abstract class Settings{
    //Klasse für alle Konstanten. Ob diese dann aber alle einstellbar sind, ist noch nicht sicher.
    public static String GAMESAVE_FOLDER="."+File.separator+"gamesaves";
    public static double G=6.674*Math.pow(10,-4); //eigentlich *10^(-11)
    public static long SPACE_TIMER_PERIOD=1000; //in Millisekunden
    public static double SPACE_CALC_PERIOD_INGAME=0.1; //in Sekunden, so oft werden die Bahnen mit dem newtonschen Gravitationsgesetz berechnet
    public static int SPACE_GET_ORBIT_ACCURACY=100; //jede 100-ste Orbit-Position wird auch tatsächlich gesendet und gezeichnet
    public static long SPACE_CALC_TIME=2000;
    //in Sekunden (inGame). So lange werden die Orbits jedes Mal (jede SpaceTimerPeriod) berechnet (nicht so viel, wie eigentlich nötig wäre)
    public static int CRAFT_PISTON_PUSH_LIMIT = 4;  // Anzahl der Blöcke, die ein Piston nach vorne bewegen kann
    public static int SERVER_PORT=30000;
    public static long REQUEST_THREAD_TIMEOUT=10000;
}