import geom.*;
import java.io.File;
public abstract class Settings{
    //Klasse für alle Konstanten. Ob diese dann aber alle einstellbar sind, ist noch nicht sicher.
    static String GAMESAVE_FOLDER="."+File.separator+"gamesaves";
    static final double G=6.674*Math.pow(10,-4); //eigentlich *10^(-11)
    static long PLAYERC_TIMER_PERIOD=30; //in Millisekunden
    static long SPACE_TIMER_PERIOD=1000; //in Millisekunden
    static double SPACE_CALC_PERIOD_INGAME=0.1; //in Sekunden, so oft werden die Bahnen mit dem newtonschen Gravitationsgesetz berechnet
    static VektorI PLAYERC_FIELD_OF_VIEW = new VektorI(29,19);  // Anzahl an Blöcken, die man als Spieler (in Craft) um sich herum sieht
}