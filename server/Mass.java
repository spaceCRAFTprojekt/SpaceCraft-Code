package server;
import util.geom.*;
import java.util.Timer;
import java.util.ArrayList;
import java.io.Serializable;
public abstract class Mass implements Serializable
{
    public static final long serialVersionUID=0L;
    protected double m;
    protected VektorD pos; //doubles sind mindestens genauso genau wie longs bis 2^63
    protected VektorD vel;
    protected Orbit o;
    protected transient Timer spaceTimer;

    public Mass(double m, VektorD pos, VektorD vel, Timer spaceTimer){
        this.m = m;
        this.pos = pos;
        this.vel = vel;
        ArrayList<VektorD> poss=new ArrayList<VektorD>();
        poss.add(pos);
        ArrayList<Double> masss=new ArrayList<Double>();
        masss.add(m);
        this.o=new Orbit(poss,masss,0,0);
        this.spaceTimer=spaceTimer;
        spaceTimerSetup();
    }
    
    protected abstract void spaceTimerSetup();
    //Nur hier können neue TimerTasks hinzugefügt werden.
    
    /**
     * Gibt die Sandbox der Masse zurück
     */
    public abstract Sandbox getSandbox();
    
    public double getMass(){
        return m;
    }
    
    public VektorD getPos(){
        return pos;
    }
    
    public VektorD getVel(){
        return vel;
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
    
    public void setSpaceTimer(Timer spaceTimer){
        this.spaceTimer=spaceTimer;
        spaceTimerSetup();
        if (this.getSandbox()!=null){
            this.getSandbox().setSpaceTimer(spaceTimer);
        }
    }
    
    public static Mass sum(Mass m1, Mass m2){
        double mNew=m1.getMass()+m2.getMass();
        return new PlanetS(mNew, m1.getPos().multiply(m1.getMass()).divide(mNew).add(m2.getPos().multiply(m2.getMass()).divide(mNew)),null,"",0,0,0,null);
    }
}