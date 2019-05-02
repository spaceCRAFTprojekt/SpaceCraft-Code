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
    public ArrayList<OrbitChange> orbitChanges = new ArrayList<OrbitChange>(); //werden nie gelöscht, nicht gut
    
    /**
     * Erstellt einen neuen Raumschiffs
     * @Params:
     * - Masse
     * - Position
     */
    public ShipS(double m, VektorD pos, VektorD vel, Timer spaceTimer)
    {
        super(m,pos,vel,spaceTimer);
    }
    
    public Object readResolve() throws ObjectStreamException{
        //muss noch das ShipS (this) und den spaceTimer des ShipCs setzen
        return this;
    }
    
    protected void spaceTimerSetup(){}
}