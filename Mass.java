import geom.*;
import java.util.Timer;
import java.io.Serializable;
/**
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class Mass implements Serializable
{
    protected Sandbox sb;
    protected Orbit o;
    protected double m;
    protected VektorL pos;
    protected transient Timer spaceTimer;

    public Mass(double m, VektorL pos, Orbit o, Timer spaceTimer){
        this.m = m;
        this.pos = pos;
        this.o = o;
        this.spaceTimer=spaceTimer;
        spaceTimerSetup();
    }
    
    protected abstract void spaceTimerSetup();
    //Nur hier können neue TimerTasks hinzugefügt werden.
    
    /**
     * Gibt die Sandbox der Masse zurück
     */
    public Sandbox getSandbox(){
        return sb;
    }
    
    public double getMass(){
        return m;
    }
    
    public VektorL getPos(){
        return pos;
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