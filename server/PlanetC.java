package server;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import util.geom.*;
import java.io.Serializable;
import java.io.ObjectStreamException;
import client.SandboxInSandbox;
import client.ClientSettings;
import blocks.*;
/**
 * Die Sandbox eines Planeten
 */
public class PlanetC extends Sandbox implements Serializable
{
    public static final long serialVersionUID=0L;
    private PlanetS planetS;
    public PlanetC(Main main, VektorI size, PlanetS planetS, Timer spaceTimer)
    {
        super(main,size,spaceTimer);
        this.planetS = planetS;
        setMap(Mapgen.generateMap("earthlike", size, planetS.radius));
    }
    
    public PlanetC(Main main, Block[][] map, Meta[][] meta, ArrayList<SandboxInSandbox> subsandboxes, PlanetS planetS, Timer spaceTimer){
        super(main,map,meta,subsandboxes,spaceTimer);
        this.planetS=planetS;
    }
    
    @Override
    protected void spaceTimerSetup(){
        spaceTimer.schedule(new TimerTask(){
            public void run(){
                handleShipLeaves();
            }
        },0,ClientSettings.SPACE_TIMER_PERIOD);
    }
    
    /**
     * Check, ob eine Subsandbox den Planeten verlässt. Wenn ja, entsprechende ShipS-Anpassung und Entfernen der Subsandbox
     */
    public void handleShipLeaves(){
        int i=0;
        while(i<subsandboxes.size()){
            SandboxInSandbox sbisb=subsandboxes.get(i);
            Sandbox ship=main.getSandbox(sbisb.index);
            if (ship instanceof ShipC && (sbisb.offset.x+sbisb.size.x<0 || sbisb.offset.y+sbisb.size.x<0 || 
                    sbisb.offset.x-sbisb.size.x>getSize().x || sbisb.offset.y-sbisb.size.y>getSize().y)){
                VektorD posInCraft=sbisb.offset.add(ClientSettings.SHIP_SIZE.divide(2).toDouble());
                posInCraft.x=posInCraft.x-getSize().x/2;
                posInCraft.y=posInCraft.y-getSize().y/2; //Jetzt liegt der Mittelpunkt des Planeten bei (0|0)
                
                VektorD posInSpace=posInCraft;
                posInSpace.y=-posInSpace.y; //Space verwendet ein "normales" mathematisches Koordinatensystem, Craft das Java-y-invertierte
                VektorD velInSpace=new VektorD(sbisb.vel.x*10,-sbisb.vel.y);
                posInSpace=posInSpace.add(getMass().getPos());
                velInSpace=velInSpace.add(getMass().getVel());
                
                ship.getMass().setPos(posInSpace);
                ship.getMass().setVel(velInSpace);
                ship.getMass().isDrawn=true;
                main.getSpace().calcOrbits(ClientSettings.SPACE_CALC_TIME);
                subsandboxes.remove(i);
            }
            else{
                i++;
            }
        }
    }
    
    public SandboxInSandbox[] getAllSubsandboxes(Integer playerID, Integer sandboxIndex){
        return super.getAllSubsandboxes(playerID, sandboxIndex);  // 1.6.2019 AK ich wusste mir nicht zu helfen:
        //Exception when resolving request: java.lang.NoSuchMethodException: server.PlanetC.getAllSubsandboxes(java.lang.Integer, java.lang.Integer)
    }
    
    public Mass getMass(){
        return planetS;
    }
}