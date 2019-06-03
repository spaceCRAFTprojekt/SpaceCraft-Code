package client;
import util.geom.VektorD;
import java.io.Serializable;
/**
 * ein Manöver
 */
public class Manoeuvre implements Serializable{
    public static final long serialVersionUID=0L;
    public VektorD dir; //Richtung, in die sich das Schiff bewegen soll
    public double outvel; //Auswurfgeschwindigkeit
    public double dMass; //Massenauswurf von dMass von t0 bis t1
    public long t0; //inGame-Zeiten
    public long t1;
    public Manoeuvre(VektorD dir, double dMass, double outvel, long t0, long t1){
        this.dir=dir;
        this.dMass=dMass;
        this.outvel=outvel;
        this.t0=t0;
        this.t1=t1;
    }
    
    public String toString(){
        return "Manoeuvre: dMass = "+dMass+", t0 = "+t0+", t1 = "+t1;
    }
    
    public VektorD getForce(){
        //p=F*t => F=p/t; p=dMass*outvel
        return dir.multiply(1/dir.getLength()).multiply(dMass*outvel/ClientSettings.SPACE_CALC_PERIOD_INGAME);
    }
} 