package client;
import util.geom.*;
import java.io.Serializable;
/**
 * Eine Kraft, die für eine bestimmte Zeit wirkt.
 */
public class OrbitChange implements Serializable
{
    public static final long serialVersionUID=0L;
    public VektorD F;
    public long t0; //inGame-Zeiten
    public long t1;
    public OrbitChange(VektorD F, long t0, long t1){
        this.F=F;
        this.t0=t0;
        this.t1=t1;
    }

    public String toString(){
        return "OrbitChange: Force = "+F+", t0 = "+t0+", t1 = "+t1;
    }
}