package client;
import java.util.ArrayList;
import java.util.Timer;
import util.geom.*;
public class ClientMass extends AbstractMass{
    private boolean isControllable;
    private ArrayList<Manoeuvre> manoeuvres;
    private int radius;
    public ClientMass(double m, boolean isControllable, VektorD pos, VektorD vel, int radius, ArrayList<Manoeuvre> manos){
        super(m,pos,vel);
        this.isControllable=isControllable;
        this.manoeuvres=manos;
        this.radius=radius;
    }
    public ClientMass(AbstractMass m, int playerID){
        this(m.m,m.isControllable(playerID),m.pos,m.vel,m.getRadius(),m.getManoeuvres());
    }
    public boolean isControllable(int playerID){
        return isControllable;
    }
    public ArrayList<Manoeuvre> getManoeuvres(){
        return manoeuvres;
    }
    public int getRadius(){
        return radius;
    }
    public void setSpaceTimer(Timer t){}
    public void setManoeuvres(ArrayList<Manoeuvre> manos){
        manoeuvres=manos;
    }
    
    /**
     * Gibt die Auswurfgeschwindigkeit von Masse dieses Schiffes zurück 
     * (=> je größer, desto mehr beschleunigt das Schiff mit dem gleichen Massenauswurf)
     * (Wenn es kein Schiff ist, ist diese Funktion egal)
     */
    public double getOutvel(){
        return 1;
    }
}