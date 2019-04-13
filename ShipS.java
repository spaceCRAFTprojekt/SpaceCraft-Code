import geom.*;
import java.util.ArrayList;
import java.util.Timer;
import java.io.Serializable;
import java.io.ObjectStreamException;
/**
 * Die Space Variante eines Raumschiffs
 */
public class ShipS extends Mass implements Serializable
{
    private ArrayList<OrbitChange> OrbitChanges = new ArrayList<OrbitChange>();
    
    /**
     * Erstellt einen neuen Raumschiffs
     * @Params:
     * - Masse
     * - Position
     */
    public ShipS(double m, VektorL pos, Timer spaceTimer)
    {
        super(m,pos,null,spaceTimer); // steht still => kein Orbit
    }
    
    public Object readResolve() throws ObjectStreamException{
        //muss noch das ShipS (this) und den spaceTimer des ShipCs setzen
        return this;
    }
    
    protected void spaceTimerSetup(){}
}