package client;
import util.geom.VektorD;
/**
 * Zusammenfassung von einem OrbitChange und einem MassChange,
 * welche nicht als Referenz in einem Schiff steht (nur temporär, im workspace)
 */
public class Manoeuvre{
    int shipIndex;
    OrbitChange oc;
    MassChange mc;
    public Manoeuvre(int shipIndex, VektorD F, double dMass, long t0, long t1){
        this.shipIndex=shipIndex;
        oc=new OrbitChange(F,t0,t1);
        mc=new MassChange(dMass,t0,t1);
    }
} 