package server;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import util.geom.*;
import java.io.Serializable;
import java.io.ObjectStreamException;
import client.SandboxInSandbox;
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
        
    }
    
    public SandboxInSandbox[] getAllSubsandboxes(Integer playerID, Integer sandboxIndex){
        return super.getAllSubsandboxes(playerID, sandboxIndex);  // 1.6.2019 AK ich wusste mir nicht zu helfen:
        //Exception when resolving request: java.lang.NoSuchMethodException: server.PlanetC.getAllSubsandboxes(java.lang.Integer, java.lang.Integer)
    }
    
    public Mass getMass(){
        return planetS;
    }
}