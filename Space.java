import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import geom.*;
import java.io.Serializable;
import java.io.ObjectStreamException;
/**
 * Ein Weltall
 */
public class Space implements Serializable
{
    //ArrayList<Mass>masses = new ArrayList<Mass>(); // hier sind alle Massen (Planeten oder Schiffe) verzeichnet
    static final double G=6.674;
    static long SPACE_TIMER_PERIOD=1000;
    transient Timer timer;
    Mass erde;
    double time; // in h?
    /**
     * Erstellt eine neues Sonnensystem (am Anfang: Sonne, Erde und Mond)
     */
    public Space()
    {
        timer=new Timer();
        timerSetup();
        //masses.add(new PlanetS(1000,new VektorI(0,0), null,"Erde",100,10,0));
        erde = new PlanetS(1000,new VektorL(0,0), null,"Erde",100,10,0,timer);
    }
    
    public Object readResolve() throws ObjectStreamException{
        this.timer=new Timer();
        timerSetup();
        erde.setSpaceTimer(this.timer);
        return this;
    }
    
    public void timerSetup(){
        timer.schedule(new TimerTask(){
            public void run(){
                time=time+1;
            }
        },0,SPACE_TIMER_PERIOD);
    }

    /**
     * Gibt die Sandbox in der der Spieler spawnt zurück
     */
    public Sandbox getSpawnPlanet()
    {
        //return (masses.get(0)).getSandbox();
        return erde.getSandbox();
    }
}
