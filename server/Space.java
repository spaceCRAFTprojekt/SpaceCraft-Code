package server;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import util.geom.*;
import client.Orbit;
import client.Manoeuvre;
import client.ClientSettings;
import client.ClientSpace;
import client.AbstractMass;
import java.io.Serializable;
import java.io.ObjectStreamException;
/**
 * Ein Weltall
 */
public class Space extends ClientSpace implements Serializable
{
    public static final long serialVersionUID=0L;
    Main main;
    long time; //Alle Zeiten in s
    /**
     * Erstellt eine neues Sonnensystem (am Anfang: Sonne, Erde und Mond)
     *
     */
    public Space(Main main, long inGameDTime)
    {
        super(new ArrayList<AbstractMass>(),0,inGameDTime);
        this.main=main;
        timer=new Timer();
        PlanetS erde=new PlanetS(main,1000000000L,new VektorD(0,0),new VektorD(10,0),"Erde",250,10,0,timer);
        masses.add(erde);
        PlanetS mond=new PlanetS(main,2000000L,new VektorD(-5000,0),new VektorD(10,5),"Mond",20,10,0,timer);
        masses.add(mond);
        ShipS schiff=new ShipS(main,20L,new VektorD(2000,0),new VektorD(0,10),timer);
        masses.add(schiff);
        time=0;
        inGameTime=0;
        this.inGameDTime=inGameDTime;
        calcOrbits(Settings.SPACE_CALC_TIME); //so lange Zeit, damit man es gut sieht
        timerSetup();
    }
    
    public Object readResolve() throws ObjectStreamException{
        this.timer=new Timer();
        timerSetup();
        calcOrbits(inGameDTime*20+1);
        return this;
    }

    /**
     * Gibt die Masse in der der Spieler spawnt zurück
     */
    public Mass getSpawnMass()
    {
        return (Mass) masses.get(0);
    }
    
    /**
     * Gibt, falls vorhanden, die Masse mit index i zurück
     */
    public Mass getMass(int i)
    {
        try{
            return (Mass) masses.get(i);
        }catch(Exception e){
            System.out.println("at Space.getMass(" + i + "): Schwarzes Loch? Jemand hat versucht einen nicht mehr vorhanden Planeten zu finden");
            return null;
        }
    }
}