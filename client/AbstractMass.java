package client;
import util.geom.*;
import client.Orbit;
import java.util.ArrayList;
import java.util.Timer;
import java.io.Serializable;
/**
 * Die Superklasse für alle Massen (also ClientMass, server.PlanetS und server.ShipS)
 * Die abstrakte Klasse server.Mass hat zusätzlich noch die Qualität, dass sie eine Referenz auf
 * eine Sandbox hat und dazu noch einen Timer und eine Referenz auf ein Main-Objekt 
 */
public abstract class AbstractMass implements Serializable
{
    public static final long serialVersionUID=0L;
    protected double m;
    protected VektorD pos; //doubles sind mindestens genauso genau wie longs bis 2^63
    protected VektorD vel;
    protected Orbit o;

    public AbstractMass(double m, VektorD pos, VektorD vel){
        this.m = m;
        this.pos = pos;
        this.vel = vel;
        ArrayList<VektorD> poss=new ArrayList<VektorD>();
        poss.add(pos);
        ArrayList<Double> masss=new ArrayList<Double>();
        masss.add(m);
        this.o=new Orbit(poss,masss,0,0,ClientSettings.SPACE_CALC_PERIOD_INGAME);
    }
    
    public double getMass(){
        return m;
    }
    
    public VektorD getPos(){
        return pos;
    }
    
    public VektorD getVel(){
        return vel;
    }
    
    public void setMass(double mass){
        m=mass;
    }
    
    public void setPos(VektorD pos){
        this.pos=pos;
    }
    
    public void setVel(VektorD vel){
        this.vel=vel;
    }
    
    public Orbit getOrbit(){
        return o;
    }
    
    public void setOrbit(Orbit no){
        o=no;
    }
    
    public abstract int getRadius();
    
    public abstract ArrayList<Manoeuvre> getManoeuvres();
    
    public abstract boolean isControllable(int playerID);
    
    public abstract void setSpaceTimer(Timer t);
    
    public abstract void setManoeuvres(ArrayList<Manoeuvre> manos);
}