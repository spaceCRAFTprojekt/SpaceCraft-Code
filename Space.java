import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import geom.*;
import java.io.Serializable;
import java.io.ObjectStreamException;
/**
 * Ein Weltall
 */
public class Space implements Serializable
{
    ArrayList<Mass>masses = new ArrayList<Mass>(); // hier sind alle Massen (Planeten oder Schiffe) verzeichnet
    transient Timer timer;
    long time; //Alle Zeiten in s
    long inGameTime;
    long inGameDTime; //eine Sekunde in echt => inGameDTime Sekunden im Spiel
    /**
     * Erstellt eine neues Sonnensystem (am Anfang: Sonne, Erde und Mond)
     *
     */
    public Space(long inGameDTime)
    {
        timer=new Timer();
        masses.add(new PlanetS(1000000000L,new VektorL(0,0),new VektorD(0,0),"Erde",5,10,0,timer));
        masses.add(new PlanetS(200000L,new VektorL(50,0),new VektorD(0,65),"Mond",5,2.5,0,timer));
        time=0;
        inGameTime=0;
        this.inGameDTime=inGameDTime;
        calcOrbits(inGameDTime+1);
        timerSetup();
    }
    
    public Object readResolve() throws ObjectStreamException{
        this.timer=new Timer();
        timerSetup();
        calcOrbits(inGameDTime+1);
        return this;
    }
    
    public void timerSetup(){
        timer.schedule(new TimerTask(){
            public void run(){
                time=time+Settings.SPACE_TIMER_PERIOD/1000;
                inGameTime=inGameTime+inGameDTime;
                for (int i=0;i<masses.size();i++){
                    Orbit o=masses.get(i).getOrbit();
                    if (o.getPos(inGameTime)!=null){
                        masses.get(i).setPos(o.getPos(inGameTime));
                    }
                    if (o.getVel(inGameTime)!=null){
                        masses.get(i).setVel(o.getVel(inGameTime));
                    }
                }
                calcOrbits(inGameDTime+1);
            }
        },Settings.SPACE_TIMER_PERIOD,Settings.SPACE_TIMER_PERIOD);
        for (int i=0;i<masses.size();i++){
            masses.get(i).setSpaceTimer(timer);
        }
    }

    /**
     * Gibt die Sandbox in der der Spieler spawnt zurück
     */
    public Sandbox getSpawnPlanet()
    {
        return (masses.get(0)).getSandbox();
    }
    
    /**
     * Berechnet die (Nicht-Kepler-)Orbits aller Objekte in diesem Space ab dem Aufruf dieser Methode für (dtime) Sekunden
     */
    public void calcOrbits(long dtime){ //irgendetwas hier oder in der Verwendung der Orbits ist falsch
        ArrayList<VektorL>[] poss=new ArrayList[masses.size()]; //Positionslisten
        ArrayList<VektorD>[] vels=new ArrayList[masses.size()]; //Geschwindigkeitslisten
        for (int i=0;i<masses.size();i++){
            poss[i]=new ArrayList<VektorL>();
            poss[i].add(masses.get(i).getPos()); //erste Position, Zeit 0
            vels[i]=new ArrayList<VektorD>();
            vels[i].add(masses.get(i).getVel());
        }
        for (double t=0;t<dtime;t=t+Settings.SPACE_CALC_PERIOD_INGAME){
            int k=(int) Math.round(t/Settings.SPACE_CALC_PERIOD_INGAME); //zeitlicher Index in poss und vels
            //k sollte immer kleiner als Double.MAX_VALUE sein
            for (int i=0;i<masses.size();i++){ //Masse, deren Orbit berechnet wird
                double m2=masses.get(i).getMass();
                VektorL pos2=poss[i].get(k);
                if (pos2!=null){
                    VektorD Fg=new VektorD(0,0);
                    for (int j=0;j<masses.size();j++){
                        double m1=masses.get(j).getMass();
                        VektorL pos1=poss[j].get(k);
                        if (pos1.x!=pos2.x || pos1.y!=pos2.y){
                            VektorL posDiff=pos1.subtract(pos2);
                            VektorD Fgj=posDiff.multiply(Settings.G*m1*m2/Math.pow(posDiff.getLength(),3));
                            Fg=Fg.add(Fgj);
                        }
                    }
                    VektorD dx=Fg.multiply(Math.pow(Settings.SPACE_CALC_PERIOD_INGAME,2)).divide(m2).divide(2); //x=1/2*a*t^2
                    if (masses.get(i) instanceof ShipS){
                        ArrayList<OrbitChange> os=((ShipS) masses.get(i)).orbitChanges;
                        for (int j=0;j<os.size();j++){
                            if (t+inGameTime>=os.get(j).t0 && t+inGameTime<os.get(j).t1){
                                dx=dx.add(os.get(j).F.multiply(Math.pow(Settings.SPACE_CALC_PERIOD_INGAME,2)).divide(m2).divide(2));
                            }
                        }
                    }
                    dx=dx.add(vels[i].get(k).multiply(Settings.SPACE_CALC_PERIOD_INGAME));
                    boolean hasCrash=false;
                    
                    //irgendwas hier stimmt nicht
                    for (int j=0;j<masses.size();j++){
                        if (j!=i){ //kein Zusammenstoß mit sich selbst
                            VektorL posDiff=poss[j].get(k).subtract(poss[i].get(k));
                            double sf=0;
                            if (Math.abs(dx.x)>Math.abs(dx.y)){
                                sf=posDiff.x/dx.x;
                            }
                            else if (Math.round(dx.y*1000)!=0){
                                sf=posDiff.y/dx.y;
                            }
                            VektorD dx1=dx.multiply(sf);
                            int r=0;
                            if (masses.get(j) instanceof PlanetS){
                                r=((PlanetS) masses.get(j)).getRadius();
                            }
                            if (dx.getLength()>posDiff.getLength() && Math.pow(dx1.x-posDiff.x,2)+Math.pow(dx1.y-posDiff.y,2)<=Math.pow(r,2)){ 
                                //Zusammenstoß mit einem Planeten, hier sollte im Normalfall toCraft aufgerufen werden
                                poss[i].add(pos2.add(dx1.toLong()));
                                vels[i].add(dx1.toLong().divide(Settings.SPACE_CALC_PERIOD_INGAME));
                                hasCrash=true;
                                System.out.println("crash into planet "+dx1+" "+pos2.add(dx1.toLong()));
                            }
                        }
                    }
                    if (!hasCrash){
                        poss[i].add(pos2.add(dx.toLong()));
                        vels[i].add(dx.toLong().divide(Settings.SPACE_CALC_PERIOD_INGAME));
                        //System.out.println(dx+" "+pos2.add(dx));
                    }
                }
            }
        }
        for (int i=0;i<masses.size();i++){
            Orbit o=new Orbit(poss[i],inGameTime,inGameTime+dtime);
            masses.get(i).setOrbit(o);
        }
    }
}