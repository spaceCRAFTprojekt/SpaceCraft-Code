package server;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import util.geom.*;
import client.ClientSettings;
import client.ClientSpace;
import client.AbstractMass;
import client.SandboxInSandbox;
import client.Task;
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
        ShipS schiff=new ShipS(main,20L,false,new VektorD(2000,0),new VektorD(0,10),timer);
        masses.add(schiff);
        calcOrbits(ClientSettings.SPACE_CALC_TIME); //so lange Zeit, damit man es gut sieht
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
    
    /**
     * Testet, ob Schiffe mit Planeten kollidieren, und fügt dann eventuell Subsandboxen hinzu.
     */
    @Override
    public void handleCollisions(){
        for (int i=0;i<masses.size();i++){
            if (masses.get(i) instanceof ShipS){
                ShipS ship=(ShipS) masses.get(i);
                for (int j=0;j<masses.size();j++){
                    if (masses.get(j) instanceof PlanetS){
                        PlanetS planet=(PlanetS) masses.get(j);
                        for (double t=inGameTime;t<inGameTime+inGameDTime;t=t+ship.getOrbit().dtime){
                            //ob irgendwann in der Vergangenheit (denn diese Methode wird vor dem Erhöhen 
                            //von inGameTime aufgerufen) ein Zusammenstoß stattgefunden hat
                            VektorD dPos=ship.getOrbit().getPos((long) t).subtract(planet.getOrbit().getPos((long) t)); //Positionsunterschied
                            if (!((Mass) masses.get(j)).getSandbox().isSubsandbox(i) && dPos.getLength()<=planet.getRadius()+20){
                                //Hinzufügen der Subsandbox natürlich nur ein mal
                                //etwas mehr als der Radius, da die Schiffe ja auf der Oberfläche anhalten (liegt in calcOrbits)
                                //bisher haben die Planeten noch keine Drehung
                                dPos.y=-dPos.y; //Space verwendet ein "normales" mathematisches Koordinatensystem, Craft das Java-y-invertierte
                                dPos.x=dPos.x+planet.getSandbox().map.length/2;
                                dPos.y=dPos.y+planet.getSandbox().map[0].length/2; //Der Mittelpunkt des Planeten in Craft ist nicht bei (0|0)
                                VektorD vel=ship.getOrbit().getVel((long) t); //falsch (da das Schiff ja anhält), aber bisher ohnehin irrelevant
                                vel.y=-vel.y; //invertiert...
                                SandboxInSandbox sbisb=new SandboxInSandbox(i,dPos,vel);
                                planet.getSandbox().addSandbox(sbisb);
                                if (ship.ownerIDs.size()>0){
                                    for (int k=0;k<ship.ownerIDs.size();k++){
                                        int ownerID=ship.ownerIDs.get(k);
                                        if (main.getPlayer(ownerID).getPlayerS().reachedMassIDs.indexOf(j)==-1){
                                            main.getPlayer(ownerID).getPlayerS().reachedMassIDs.add(j);
                                            if (main.getPlayer(ownerID).isOnline())
                                                main.newTask(ownerID,"Player.addChatMsg","Eines deiner Schiffe landete auf dem Planet "+j+" an der Stelle "+dPos+".");
                                        }
                                    }
                                }
                                else{ //allgemeines Schiff ohne Besitzer
                                    for (int ownerID=0;ownerID<main.getPlayerNumber();ownerID++){
                                        if (main.getPlayer(ownerID).getPlayerS().reachedMassIDs.indexOf(j)==-1){
                                            main.getPlayer(ownerID).getPlayerS().reachedMassIDs.add(j);
                                            if (main.getPlayer(ownerID).isOnline())
                                                main.newTask(ownerID,"Player.addChatMsg","Ein offenes Schiff landete auf dem Planet "+j+" an der Stelle "+dPos+".");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}