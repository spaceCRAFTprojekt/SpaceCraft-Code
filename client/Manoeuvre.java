package client;
import util.geom.VektorD;
import java.io.Serializable;
/**
 * ein Manöver
 */
public class Manoeuvre implements Serializable{
    public static final long serialVersionUID=0L;
    public VektorD dir; //Richtung, in die sich das Schiff bewegen soll
    public boolean rel; //true: Richtung der Kraft immer relativ zur Bewegungsrichtung des Schiffs
    public double outvel; //Auswurfgeschwindigkeit
    public double dMass; //Massenauswurf von dMass von t0 bis t1 (positiv: Massenzunahme!)
    public long t0; //inGame-Zeiten
    public long t1;
    public Manoeuvre(VektorD dir, boolean rel, double dMass, double outvel, long t0, long t1){
        this.dir=dir;
        this.rel=rel;
        this.dMass=dMass;
        this.outvel=outvel;
        this.t0=t0;
        this.t1=t1;
    }
    
    public String toString(){
        return "Manoeuvre: dMass = "+dMass+", t0 = "+t0+", t1 = "+t1;
    }
    
    /**
     * vel: Bewegungsrichtung des betrachteten Schiffes. Wenn rel=false ist, ist es egal.
     */
    public VektorD getForce(VektorD vel){
        VektorD dirAbs; //absolute Richtung (da dir manchmal nur als relative Richtung behandelt wird)
        if (rel){
            double angle=Math.atan2(dir.y,dir.x);
            double velAngle=Math.atan2(vel.y,vel.x);
            //einfache Addition der Winkel
            dirAbs=new VektorD(Math.cos(angle+velAngle),Math.sin(angle+velAngle));
        }
        else{
            dirAbs=dir.multiply(1/dir.getLength()); //Einheitsvektor
        }
        //p=F*t => F=p/t; p=dMass*outvel
        return dirAbs.multiply(-dMass*outvel/ClientSettings.SPACE_CALC_PERIOD_INGAME);
    }
} 