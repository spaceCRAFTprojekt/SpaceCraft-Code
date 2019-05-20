package server;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import util.geom.*;
import java.io.Serializable;
import java.io.ObjectStreamException;
import blocks.*;
/**
 * Die Sandbox eines Planeten
 */
public class PlanetC extends Sandbox implements Serializable
{
    public static final long serialVersionUID=0L;
    public static ArrayList<PlanetC> planetCs=new ArrayList<PlanetC>(); //Tabelle, die alle PlanetCs enthält, muss eigens (de-)serialisiert werden!
    private final int id;
    //Index in der planetCs-Tabelle, eine der wenigen Sachen, die serialisiert wird (außer natürlich bei den PlanetCs in der planetCs-Tabelle selbst)
    
    private transient PlanetS planetS;
    public PlanetC(VektorI size, PlanetS planetS, Timer spaceTimer)
    {
        super(size,spaceTimer);
        id=planetCs.size();
        planetCs.add(id,this);
        this.planetS = planetS;
        setMap(Mapgen.generateMap("earthlike", size, planetS.radius));
    }
    
    public PlanetC(Block[][] map, ArrayList<Sandbox> subsandboxes, PlanetS planetS, Timer spaceTimer){
        super(map,subsandboxes,spaceTimer);
        this.id=planetCs.size();
        planetCs.add(id,this);
        this.planetS=planetS;
    }
    
    @Override
    protected void spaceTimerSetup(){
        
    }
    
    public Object readResolve() throws ObjectStreamException{
        return planetCs.get(id);
    }
    
    public PlanetS getPlanetS(){
        return planetS;
    }
    
    public void setPlanetS(PlanetS p){
        //um ein Problem mit einer zirulären Referenz zu umgehen, siehe PlanetS und Main
        this.planetS=p;
    }
}