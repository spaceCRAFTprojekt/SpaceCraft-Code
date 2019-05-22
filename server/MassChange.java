package server;
import java.io.Serializable;
public class MassChange implements Serializable{
    public static final long serialVersionUID=0L;
    long t0;
    long t1;
    double dMass; //negativ: Verlust, positiv: Zugewinn (eher unrealistisch)
    public MassChange(long t0, long t1, double dMass){
        this.t0=t0;
        this.t1=t1;
        this.dMass=dMass;
    }
}
