package server;
import util.geom.*;
import java.util.ArrayList;
import java.io.Serializable;
/**
 * Nur eine Auflistung von Positionen ab einer bestimmten Zeit.
 * Generiert werden muss das Ganze mit dem newtonschen Gravitationsgesetz, und zwar alle Orbits zur
 * selben Zeit in Space (da es ein Mehrkörperproblem ist).
 */
public class Orbit implements Serializable{
    public ArrayList<VektorD> pos;
    public long t0;
    public long t1;
    public Orbit(ArrayList<VektorD> pos, long t0, long t1){
        this.pos=pos;
        //Zeiten im Spiel, nicht reale Zeiten
        this.t0=t0; //dieser Orbit ist gültig von t0 bis t1
        this.t1=t1;
    }
    public VektorD getPos(long t){
        if (t0>t || t1<=t){
            return null;
        }
        int i=(int) Math.round((t-t0)/Settings.SPACE_CALC_PERIOD_INGAME);
        return pos.get(i);
    }
    public VektorD getVel(long t){
        if (t0>t || t1<=t){
            return null;
        }
        int i=(int) Math.round((t-t0)/Settings.SPACE_CALC_PERIOD_INGAME);
        
        //*
        if (i<0 || i>=pos.size()-1){
            return null;
        }
        else{
            return (pos.get(i+1).subtract(pos.get(i))).divide(Settings.SPACE_CALC_PERIOD_INGAME);
        }//*/
        /*
        if (i<1 || i>=pos.size()){
            System.out.println("no valid getVel");
            return null;
        }
        else{
            return (pos.get(i).toDouble().subtract(pos.get(i-1).toDouble())).divide(Settings.SPACE_CALC_PERIOD_INGAME);
        }//*/
    }
}