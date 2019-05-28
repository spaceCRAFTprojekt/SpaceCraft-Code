package client;
import util.geom.*;
import java.util.ArrayList;
/**
 * (hier keine Unterscheidung zwischen Planeten und Schiffen, da sie clientseitig nicht nötig ist)
 * ziemlich ähnlich zu server.Mass und server.ShipS
 */
public class ClientMass{
    private double m;
    private VektorD pos;
    private VektorD vel;
    private int radius;
    private Orbit o;
    public ArrayList<Manoeuvre> manoeuvres = new ArrayList<Manoeuvre>();

    public ClientMass(double m, VektorD pos, VektorD vel, int radius){
        this.m = m;
        this.pos = pos;
        this.vel = vel;
        this.radius=radius;
        ArrayList<VektorD> poss=new ArrayList<VektorD>();
        poss.add(pos);
        ArrayList<Double> masss=new ArrayList<Double>();
        masss.add(m);
        this.o=new Orbit(poss,masss,0,0,1);
    }

    public ClientMass(double m, VektorD pos, VektorD vel, int radius, ArrayList<Manoeuvre> manoeuvres){
        this(m,pos,vel,radius);
        this.manoeuvres=manoeuvres;
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

    public int getRadius(){
        return radius;
    }

    public static ClientMass sum(ClientMass m1, ClientMass m2){
        double mNew=m1.getMass()+m2.getMass();
        return new ClientMass(mNew, m1.getPos().multiply(m1.getMass()).divide(mNew).add(m2.getPos().multiply(m2.getMass()).divide(mNew)),m1.getVel().multiply(m1.getMass()).divide(mNew).add(m2.getVel().multiply(m2.getMass()).divide(mNew)),0);
    }
} 