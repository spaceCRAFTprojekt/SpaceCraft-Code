package client;
import java.util.ArrayList;
import java.util.Timer;
import util.geom.*;
/**
 * Die Client-Variante einer Masse. Es wird hier nicht zwischen Planeten und Schiffen unterschieden, da es egal ist.
 */
public class ClientMass extends AbstractMass{
    private boolean isControllable;
    private ArrayList<Manoeuvre> manoeuvres;
    private int radius;
    private double outvel;
    private double restMass;
    public ClientMass(double m, boolean isControllable, VektorD pos, VektorD vel, int radius, double outvel, double restMass, ArrayList<Manoeuvre> manos){
        super(m,pos,vel);
        this.isControllable=isControllable;
        this.manoeuvres=manos;
        this.radius=radius;
        this.outvel=outvel;
        this.restMass=restMass;
    }
    public ClientMass(AbstractMass m, int playerID){
        this(m.m,m.isControllable(playerID),m.pos,m.vel,m.getRadius(),m.getOutvel(),m.getRestMass(),m.getManoeuvres());
        this.isDrawn=m.isDrawn;
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
    
    public double getOutvel(){
        return outvel;
    }
    public void setOutvel(double ov){
        outvel=ov;
    }
    
    public double getRestMass(){
        return restMass;
    }
}