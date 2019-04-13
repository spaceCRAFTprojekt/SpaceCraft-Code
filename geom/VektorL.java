package geom;
import java.lang.Math;
import java.io.Serializable;

/**
 * Simple Long Vektor
 * Bei Veränderung Bitte in der readme Datei erwähnen!!!
 */
public class VektorL implements Serializable
{
    public long x;
    public long y;

    public VektorL(long x, long y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Vektoraddition
     */
    public VektorL add(VektorL v)
    {
        return new VektorL(v.x+x,v.y+y);
    }
    
    public VektorL subtract(VektorL v){
        return new VektorL(v.x-x,v.y-y);
    }
    
    /**
     * SMultiplikation
     */
    public VektorL multiply(double s)
    {
        return new VektorL((int)(s*x),(int)(s*y));
    }
    
    /**
     * SDivision
     */
    public VektorL divide(double s)
    {
        return new VektorL((int)(x/s),(int)(y/s));
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

