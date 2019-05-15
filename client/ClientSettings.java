package client;
import util.geom.VektorI;
public abstract class ClientSettings{
    public static long PLAYERC_TIMER_PERIOD=30; //in Millisekunden
    public static long SYNCHRONIZE_REQUEST_PERIOD=1000;
    public static VektorI PLAYERC_FIELD_OF_VIEW = new VektorI(29,19);  // Anzahl an Blöcken, die man als Spieler (in Craft) um sich herum sieht
    public static long TASK_RESOLVE_PERIOD=1;
    public static boolean PRINT_COMMUNICATION=true; //nur weil es nervig ist, ständig Zeug aus-und einkommentieren zu müssen
    public static VektorI INV_SIZE=new VektorI(10,4);  // größe des Spielerinventars  (in Slots)
}