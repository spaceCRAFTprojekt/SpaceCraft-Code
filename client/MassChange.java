package client;

import java.io.Serializable;
public class MassChange implements Serializable{
    public static final long serialVersionUID=0L;
    public long t0;
    public long t1;
    public double dMass; //negativ: Verlust, positiv: Zugewinn (eher unrealistisch)
    public MassChange(double dMass, long t0, long t1){
        this.t0=t0;
        this.t1=t1;
        this.dMass=dMass;
    }
}