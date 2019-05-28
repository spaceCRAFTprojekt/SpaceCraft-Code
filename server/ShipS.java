package server;
import util.geom.*;
import client.Orbit;
import client.Manoeuvre;
import java.util.ArrayList;
import java.util.Timer;
import java.io.Serializable;
import java.io.ObjectStreamException;
/**
 * Die Space Variante eines Raumschiffs
 * Unfertig!
 */
public class ShipS extends Mass implements Serializable
{
    public static final long serialVersionUID=0L;
    public ArrayList<Manoeuvre> manoeuvres = new ArrayList<Manoeuvre>();
    public ArrayList<Integer> ownerIDs=new ArrayList<Integer>(); //wenn diese Liste leer ist, dann ist das Schiff öffentlich
    public ShipC shipC;
    
    /**
     * Erstellt einen neuen Raumschiffs
     * @Params:
     * - Masse
     * - Position
     */
    public ShipS(Main main, double m, VektorD pos, VektorD vel, Timer spaceTimer)
    {
        super(main,m,pos,vel,spaceTimer);
        shipC=new ShipC(main,new VektorI(20,40),this,spaceTimer);
    }
    
    public Object readResolve() throws ObjectStreamException{
        this.shipC.setSpaceTimer(spaceTimer);
        return this;
    }
    
    @Override
    public void setSpaceTimer(Timer t){
        super.setSpaceTimer(t);
    }
    
    protected void spaceTimerSetup(){}
    
    public Sandbox getSandbox(){
        return shipC;
    }
    
    public void setOwner(int playerID){
        if (ownerIDs.indexOf(playerID)==-1)
            ownerIDs.add(playerID);
    }

    public void removeOwner(int playerID){
        if (ownerIDs.indexOf(playerID)!=-1)
            ownerIDs.remove(ownerIDs.indexOf(playerID));
    }

    public boolean isOwner(int playerID){
        return ownerIDs.indexOf(playerID)!=-1 || ownerIDs.size()==0;
    }
}