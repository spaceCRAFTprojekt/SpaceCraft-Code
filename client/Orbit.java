package client;
import util.geom.*;
import java.util.ArrayList;
import java.io.Serializable;
/**
 * Nur eine Auflistung von Positionen ab einer bestimmten Zeit.
 * Generiert werden muss das Ganze mit dem newtonschen Gravitationsgesetz, und zwar alle Orbits zur
 * selben Zeit in Space (da es ein Mehrkörperproblem ist).
 */
public class Orbit implements Serializable{
    public static final long serialVersionUID=0L;
    public ArrayList<VektorD> pos;
    public ArrayList<Double> mass;
    public long t0;
    public long t1;
    public double dtime;
    public Orbit(ArrayList<VektorD> pos, ArrayList<Double> mass, long t0, long t1, double dtime){
        this.pos=pos;
        this.mass=mass;
        //Zeiten im Spiel, nicht reale Zeiten
        this.t0=t0; //dieser Orbit ist gültig von t0 bis t1
        this.t1=t1;
        this.dtime=dtime; //Zeitdifferenz zwischen zwei Positionen in pos
    }
    public VektorD getPos(long t){
        if (t0>t || t1<=t){
            return null;
        }
        int i=(int) Math.round(((double) t-t0)/dtime);
        return pos.get(i);
    }
    public VektorD getVel(long t){
        if (t0>t || t1<=t){
            return null;
        }
        int i=(int) Math.round((double) (t-t0)/dtime);
        /*
        if (i<0 || i>=pos.size()-1){
            return null;
        }
        else{
            return (pos.get(i+1).subtract(pos.get(i))).divide(dtime);
        }*/
        if (i<1 || i>pos.size()-1){
            return null;
        }
        else{
            return (pos.get(i).subtract(pos.get(i-1))).divide(dtime);
        }
    }
    public double getMass(long t){
        if (t0>t || t1<=t){
            return -1;
        }
        int i=(int) Math.round((double) (t-t0)/dtime);
        return mass.get(i);
    }
    public double getTravelledDistance(long tStart, long tEnd){
        if (t0>tStart || t0>tEnd || t1<=tStart || t1<=tEnd || tEnd<tStart)
            return -1;
        double ret=0;
        int iStart=(int) Math.round((double) (tStart-t0)/dtime);
        int iEnd=(int) Math.round((double) (tEnd-t0)/dtime);
        for (int i=iStart+1;i<iEnd;i++){
            ret=ret+pos.get(i).subtract(pos.get(i-1)).getLength();
        }
        return ret;
    }
}