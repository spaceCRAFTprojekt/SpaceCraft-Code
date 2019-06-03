package client;
import util.geom.VektorI;
public abstract class ClientSettings{
    public static long PLAYERC_TIMER_PERIOD=30; //in Millisekunden
    public static long SYNCHRONIZE_REQUEST_PERIOD=1000;
    public static double G=6.674*Math.pow(10,-4); //eigentlich *10^(-11)
    public static long SPACE_TIMER_PERIOD=1000; //in Millisekunden
    public static double SPACE_CALC_PERIOD_INGAME=0.1; //in Sekunden, so oft werden die Bahnen mit dem newtonschen Gravitationsgesetz berechnet
    public static int SPACE_GET_ORBIT_ACCURACY=100; //jede 100-ste Orbit-Position wird auch tatsächlich gesendet und gezeichnet
    public static long SPACE_CALC_TIME=2000;
    public static VektorI PLAYERC_FIELD_OF_VIEW = new VektorI(29,19);  // Anzahl an Blöcken, die man als Spieler (in Craft) um sich herum sieht
    public static VektorI PLAYERC_MAPIDCACHE_SIZE = PLAYERC_FIELD_OF_VIEW.multiply(2); //in Blöcken
    public static boolean PRINT_COMMUNICATION=false; //nur weil es nervig ist, ständig Zeug aus-und einkommentieren zu müssen
    public static String SERVER_ADDRESS="localhost";
    public static int SERVER_PORT=30000;
}