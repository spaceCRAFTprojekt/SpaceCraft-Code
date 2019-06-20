package server;
import util.geom.*;
import client.Orbit;
import client.AbstractMass;
import java.util.Timer;
import java.util.ArrayList;
import java.io.Serializable;
/**
 * Die serverseitige Version einer Masse (mit main, spaceTimer und einer Sandbox)
 */
public abstract class Mass extends AbstractMass implements Serializable
{
    public static final long serialVersionUID=0L;
    public Main main;
    protected transient Timer spaceTimer;

    public Mass(Main main, double m, VektorD pos, VektorD vel, Timer spaceTimer){
        super(m,pos,vel);
        this.main=main;
        this.spaceTimer=spaceTimer;
        spaceTimerSetup();
    }
    
    /**
     * Nur hier können neue TimerTasks hinzugefügt werden.
     */
    protected abstract void spaceTimerSetup();
    
    /**
     * Gibt die Sandbox der Masse zurÃ¼ck
     */
    public abstract Sandbox getSandbox();
    
    public void setSpaceTimer(Timer spaceTimer){
        this.spaceTimer=spaceTimer;
        spaceTimerSetup();
        if (this.getSandbox()!=null){
            this.getSandbox().setSpaceTimer(spaceTimer);
        }
    }
}