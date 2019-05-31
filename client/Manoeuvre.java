package client;
import util.geom.VektorD;
import java.io.Serializable;
/**
 * ein Manöver
 */
public class Manoeuvre implements Serializable{
    public static final long serialVersionUID=0L;
    public VektorD F;
    public double dMass;
    public long t0; //inGame-Zeiten
    public long t1; //von t0 bis t1 gültig
    public Manoeuvre(VektorD F, double dMass, long t0, long t1){
        this.F=F;
        this.dMass=dMass;
        this.t0=t0;
        this.t1=t1;
    }
    
    public String toString(){
        return "Manoeuvre: Force = "+F+", dMass = "+dMass+", t0 = "+t0+", t1 = "+t1;
    }
} 