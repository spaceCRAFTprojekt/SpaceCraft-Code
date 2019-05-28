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
    private ShipS shipS;

    public ShipC(Main main, VektorI size, ShipS shipS, Timer spaceTimer)
    {
        super(main,size,spaceTimer);
        this.shipS = shipS;
    }
    
    public ShipC(Main main, Block[][] map, Meta[][] meta, ArrayList<SandboxInSandbox> subsandboxes, ShipS shipS, Timer spaceTimer){
        super(main,map,meta,subsandboxes,spaceTimer);
        this.shipS=shipS;
    }
    
    protected void spaceTimerSetup(){}
    
    public ShipS getShipS(){
        return shipS;
    }
}
