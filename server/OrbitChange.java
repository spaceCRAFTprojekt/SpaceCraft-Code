package server;
import geom.*;
import java.io.Serializable;
/**
 * Eine Kraft, die für eine bestimmte Zeit wirkt.
 */
public class OrbitChange implements Serializable
{
    VektorD F;
    long t0; //inGame-Zeiten
    long t1;
    public OrbitChange(VektorD F, long t0, long t1){
        this.F=F;
        this.t0=t0;
        this.t1=t1;
    }
}
