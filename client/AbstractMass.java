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
    public boolean isDrawn; //ob diese Masse gerade gezeichnet werden soll (unabhängig
    //davon, ob sie außerhalb vom Bildschirm ist, aber abhängig davon, ob diese Masse mit einem
    //Objekt kollidiert ist)

    public AbstractMass(double m, VektorD pos, VektorD vel){
        this.m = m;
        this.pos = pos;
        this.vel = vel;
        ArrayList<VektorD> poss=new ArrayList<VektorD>();
        poss.add(pos);
        ArrayList<Double> masss=new ArrayList<Double>();
        masss.add(m);
        this.o=new Orbit(poss,masss,0,0,ClientSettings.SPACE_CALC_PERIOD_INGAME);
        this.isDrawn=true;
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
    
    /**
     * In manchen Klassen (server.ShipS) ist diese Methode egal.
     */
    public abstract int getRadius();
    
    /**
     * In manchen Klassen (server.PlanetS) ist diese Methode egal.
     */
    public abstract ArrayList<Manoeuvre> getManoeuvres();
    
    /**
     * In manchen Klassen (server.PlanetS) ist diese Methode egal.
     */
    public abstract boolean isControllable(int playerID);
    
    /**
     * In manchen Klassen (ClientMass) ist diese Methode egal.
     */
    public abstract void setSpaceTimer(Timer t);
    
    /**
     * In manchen Klassen (server.PlanetS) ist diese Methode egal.
     */
    public abstract void setManoeuvres(ArrayList<Manoeuvre> manos);
    
    /**
     * Gibt die Auswurfgeschwindigkeit von Masse dieses Schiffes zurück 
     * (=> je größer, desto mehr beschleunigt das Schiff mit dem gleichen Massenauswurf)
     * In manchen Klassen (server.PlanetS) ist diese Methode egal.
     */
    public abstract double getOutvel();
    
    /**
     * In manchen Klassen (server.PlanetS) ist diese Methode egal.
     */
    public abstract void setOutvel(double ov);
    
    /**
     * Gibt die Restmasse dieses Schiffes zurück (= alle Masse, die nicht ausgeworfen werden kann,
     * da sie kein Treibstoff ist)
     * In manchen Klassen (server.PlanetS) ist diese Methode egal.
     */
    public abstract double getRestMass();
}