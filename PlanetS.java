import geom.*;
import java.util.Timer;
import java.io.Serializable;
import java.io.ObjectStreamException;
/**
 * Die Space Variante eines Planeten
 */
public class PlanetS extends Mass implements Serializable
{
    double gravity;
    int radius; // in m
    int lightSource;
    String name;
    PlanetC planetC;
    /**
     * Erstellt einen neuen Planeten
     * @Params:
     * - Masse
     * - Position
     * - Orbit
     * - Name
     * - Radius (m)
     * - Planetanziehungskraft
     * - Licht
     */
    public PlanetS(double m, VektorD pos, VektorD vel, String name, int radius, double gravity, int lightSource, Timer spaceTimer)
    {
        super(m,pos,vel,spaceTimer);
        this.gravity = gravity;
        this.name = name;
        this.lightSource = lightSource;
        this.radius = radius;
        planetC = new PlanetC(new VektorI((radius*2)+100, (radius*2)+100), this, spaceTimer);
    }
    
    public Object readResolve() throws ObjectStreamException{
        //um eine zirkuläre Referenz zwischen dem PlanetC und dem PlanetS zu umgehen, siehe Main
        this.planetC.setPlanetS(this);
        this.planetC.setSpaceTimer(spaceTimer);
        return this;
    }
    
    @Override 
    public Sandbox getSandbox()
    {
        return planetC;
    }
    
    public int getRadius(){
        return radius;
    }
    
    @Override
    public void setSpaceTimer(Timer t){
        super.setSpaceTimer(t);
    }
    
    protected void spaceTimerSetup(){}
    
    /**
     * gibt die Planetenanziehungskraft/Beschleunigung zurück
     */
    public double getGravity()
    {
        return gravity;
    }
}
