import geom.*;
import java.io.Serializable;
public class KOrbit implements OrbitI, Serializable{
    static final double G=Space.G;
    public int t0;
    public int t1;
    public VektorL pos0;
    public VektorL vel0;
    public double m;
    public VektorL pos0t;
    public VektorL vel0t;
    public double mt;
    public KOrbit(int t0, int t1, VektorL pos0, VektorL vel0, double m, VektorL pos0t, VektorL vel0t, double mt){ //mt=Masse des Trabanten, m=Masse des Hauptplaneten/der Sonne
        this.t0=t0;
        this.t1=t1;
        this.pos0=pos0;
        this.vel0=vel0;
        this.m=m;
        this.pos0t=pos0t;
        this.vel0t=vel0t;
        this.mt=mt;
    }
    
    public static double sq(double in){
        return Math.pow(in,2);
    }
    
    public VektorL getPos(int t){
        if (t0<=t && t<=t1){
            
            return new VektorL(0,0);
        }
        else{
            return null;
        }
    }
    
    public VektorL getVel(int t){
        if (t0<=t && t<=t1){
            return new VektorL(0,0);
        }
        else{
            return null;
        }
    }
}