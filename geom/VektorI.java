package geom;
import java.lang.Math;
import java.io.Serializable;

/**
 * Simple Integer Vektor
 * Bei Veränderung Bitte in der readme Datei erwähnen!!!
 * 0.0.2: SDivision ergänzt
 */
public class VektorI implements Serializable
{
    public int x;
    public int y;

    public VektorI(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void set(VektorI v){
        this.x = v.x;
        this.y = v.y;
    }
    
    /**
     * Vektoraddition
     */
    public VektorI add(VektorI v)
    {
        return new VektorI(v.x+x,v.y+y);
    }
    
    public VektorI subtract(VektorI v){
        return new VektorI(x-v.x,y-v.y);
    }
    
    /**
     * SMultiplikation
     */
    public VektorI multiply(double s)
    {
        return new VektorI((int)(s*x),(int)(s*y));
    }
    
    /**
     * SDivision
     */
    public VektorI divide(double s)
    {
        return new VektorI((int)(x/s),(int)(y/s));
    }
    
    /**
     * Betrag des Vektors
     */
    public double getLength()
    {
        return Math.hypot(x,y);
    }
    
    /**
     * Gibt einen Double-Vektor zurück
     */
    public VektorD toDouble()
    {
        return new VektorD((double)x,(double)y);
    }
    
    /**
     * Gibt einen String zurück, der den Vektor beschreibt:
     * VektorI[x;y]
     */
    public String toString()
    {
        return "VektorI["+x+";"+y+"]";
    }
}
