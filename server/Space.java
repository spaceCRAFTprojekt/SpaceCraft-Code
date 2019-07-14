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
import client.Orbit;
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
        PlanetS erde=new PlanetS(main,1000000000L,new VektorD(0,0),new VektorD(10,0),"Erde",250,10,0,timer);
        masses.add(erde);
        PlanetS mond=new PlanetS(main,2000000L,new VektorD(-5000,0),new VektorD(10,5),"Mond",20,10,0,timer);
        masses.add(mond);
        ShipS schiff=new ShipS(main,20L,new VektorD(2000,0),new VektorD(0,10),100,2,timer);
        masses.add(schiff);
        calcOrbits(ClientSettings.SPACE_CALC_TIME); //so lange Zeit, damit man es gut sieht
    }
    
    public Object readResolve() throws ObjectStreamException{
        calcOrbits(ClientSettings.SPACE_CALC_TIME);
        return this;
    }
    
    @Override
    public void timerSetup(){
        super.timerSetup();
        timer.schedule(new TimerTask(){
            public void run(){ //Bewegen von Subsandboxen
                for (int i=0;i<masses.size();i++){
                    Sandbox sb=((Mass) masses.get(i)).getSandbox(); //in diesem serverseitigen Space sind ohnehin alle Masses, nicht nur AbstractMasses
                    for (int j=0;j<sb.getAllSubsandboxes().size();j++){
                        SandboxInSandbox sbisb=sb.getAllSubsandboxes().get(j);
                        sbisb.offset=sb.collisionPoint(sbisb,inGameDTime);
                    }
                }
            }
        },0,ClientSettings.SPACE_TIMER_PERIOD);
        for (int i=0;i<masses.size();i++){
            masses.get(i).setSpaceTimer(timer);
        }
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
            System.out.println("[Server]: at Space.getMass(" + i + "): Schwarzes Loch? Jemand hat versucht einen nicht mehr vorhanden Planeten zu finden");
            return null;
        }
    }
    
    /**
     * Testet, ob in der Zeit dtime Schiffe mit Planeten kollidieren, und fügt dann eventuell Subsandboxen hinzu.
     */
    @Override
    public void handleCollisions(long dtime){
        int numMasses=masses.size(); //Wenn jedes Mal masses.size() aufgerufen wird, kann es asynchrone Fehler geben.
        for (int i=0;i<numMasses;i++){
            if (masses.get(i) instanceof ShipS){
                ShipS ship=(ShipS) masses.get(i);
                for (int j=0;j<numMasses;j++){
                    if (masses.get(j) instanceof PlanetS){
                        PlanetS planet=(PlanetS) masses.get(j);
                        for (double t=inGameTime;t<inGameTime+dtime;t=t+ship.getOrbit().dtime){
                            //ob irgendwann in der Vergangenheit (denn diese Methode wird vor dem Erhöhen 
                            //von inGameTime aufgerufen) ein Zusammenstoß stattgefunden hat
                            try{
                                VektorD dPos=ship.getOrbit().getPos((long) t).subtract(planet.getOrbit().getPos((long) t)); //Positionsunterschied
                                if (((Mass) masses.get(j)).getSandbox().subsandboxIndex(i)==-1 && dPos.getLength()<=planet.getRadius()+50){
                                    //Hinzufügen der Subsandbox natürlich nur ein mal
                                    //mehr als der Radius, da die Schiffe ja auf der Oberfläche anhalten (liegt in calcOrbits)
                                    //bisher haben die Planeten noch keine Drehung
                                    dPos.y=-dPos.y; //Space verwendet ein "normales" mathematisches Koordinatensystem, Craft das Java-y-invertierte
                                    //einfach ein direktes Stürzen auf den Planeten, natürlich nicht ganz richtig so
                                    VektorD vel=dPos.multiply(-1/dPos.getLength());
                                    dPos.x=dPos.x+planet.getSandbox().map.length/2;
                                    dPos.y=dPos.y+planet.getSandbox().map[0].length/2; //Der Mittelpunkt des Planeten in Craft ist nicht bei (0|0)
                                    SandboxInSandbox sbisb=new SandboxInSandbox(i,dPos,vel,ship.getSandbox().getSize());
                                    planet.getSandbox().addSandbox(sbisb);
                                    if (ship.ownerIDs.size()>0){
                                        for (int k=0;k<ship.ownerIDs.size();k++){
                                            int ownerID=ship.ownerIDs.get(k);
                                            if (main.getPlayer(ownerID).getPlayerS().reachedMassIDs.indexOf(j)==-1){
                                                main.getPlayer(ownerID).getPlayerS().reachedMassIDs.add(j);
                                            }
                                            if (main.getPlayer(ownerID).isOnline())
                                                main.newTask(ownerID,"Player.addChatMsg","Eines deiner Schiffe landete auf dem Planet "+planet.name+" an der Stelle ("+((int) dPos.x)+"|"+((int) dPos.y)+").");
                                        }
                                    }
                                    else{ //allgemeines Schiff ohne Besitzer
                                        for (int ownerID=0;ownerID<main.getPlayerNumber();ownerID++){
                                            if (main.getPlayer(ownerID).getPlayerS().reachedMassIDs.indexOf(j)==-1){
                                                main.getPlayer(ownerID).getPlayerS().reachedMassIDs.add(j);
                                            }
                                            if (main.getPlayer(ownerID).isOnline())
                                                main.newTask(ownerID,"Player.addChatMsg","Ein offenes Schiff landete auf dem Planet "+planet.name+" an der Stelle "+dPos.toInt()+".");
                                        }
                                    }
                                    ship.isDrawn=false;
                                }
                            }
                            catch(NullPointerException e){} //manchmal, wenn irgendwas Asynchrones passiert
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Request
     * Siehe ClientSpace.setTime(long time)
     */
    public void setTime(Integer playerID, Long time){
        if (main.getPlayer(playerID).isAdmin()){
            super.setTime(time.longValue());
        }
        else{
            main.noAdminMsg(playerID);
        }
    }
    
    /**
     * Request
     */
    public void setDTime(Integer playerID, Long dtime){
        if(main.getPlayer(playerID).isAdmin()){
            if (dtime.longValue()>=0 && dtime.longValue()<=1000)
                inGameDTime=dtime.longValue();
        }
        else
            main.noAdminMsg(playerID);
    }
    
    /**
     * Request
     */
    public Long getInGameTime(Integer playerID){
        return inGameTime;
    }
    
    /**
     * Request
     */
    public long getInGameDTime(Integer playerID){
        return inGameDTime;
    }
    
    /**
     * Request, gibt den Index der Sandbox zurück, in der die Sandbox mit dem angegebenen Index Subsandbox ist, oder -1,
     * wenn die Sandbox mit dem angegebenen Index nirgendwo Subsandbox ist.
     */
    public Integer getSupersandboxIndex(Integer playerID, Integer subsandboxIndex){
        for (int i=0;i<masses.size();i++){
            if (((Mass) masses.get(i)).getSandbox().isSubsandbox(subsandboxIndex.intValue()))
                return i;
        }
        return -1;
    }
}