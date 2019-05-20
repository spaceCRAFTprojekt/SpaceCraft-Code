package server;
import java.util.ArrayList;
import java.util.Timer;
import util.geom.*;
import java.io.Serializable;
import java.io.ObjectStreamException;
import blocks.*;
/**
 * Die Sandbox eines Schiffs
 */
public class ShipC extends Sandbox implements Serializable
{
    public static final long serialVersionUID=0L;
    public static ArrayList<ShipC> shipCs=new ArrayList<ShipC>(); //Tabelle, die alle Schiffen enthält, muss eigens (de-)serialisiert werden!
    private final int id;
    //Index in der ships-Tabelle, eine der wenigen Sachen, die serialisiert wird (außer natürlich bei den Schiffen in der shipCs-Tabelle selbst)
    
    private transient ShipS shipS;

    public ShipC(VektorI size, ShipS shipS, Timer spaceTimer)
    {
        super(size,spaceTimer);
        this.id=shipCs.size();
        shipCs.add(id,this);
        this.shipS = shipS;
    }
    
    public ShipC(Block[][] map, ArrayList<Sandbox> subsandboxes, ShipS shipS, Timer spaceTimer){
        super(map,subsandboxes,spaceTimer);
        this.id=shipCs.size();
        shipCs.add(id,this);
        this.shipS=shipS;
    }
    
    protected void spaceTimerSetup(){}
    
    public Object readResolve() throws ObjectStreamException{
        return shipCs.get(id);
    }
    
    public ShipS getShipS(){
        return shipS;
    }
}
