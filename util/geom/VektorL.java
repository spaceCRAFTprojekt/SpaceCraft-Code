package util.geom;
import java.lang.Math;
import java.io.Serializable;

/**
 * Simple Long Vektor
 * Bei Veränderung Bitte in der readme Datei erwähnen!!!
 */
public class VektorL implements Serializable
{
    public static final long serialVersionUID=0L;
    public long x;
    public long y;

    public VektorL(long x, long y)
    {
        this.x = x;
        this.y = y;
    }
    
    public void set(VektorL v){
        this.x = v.x;
        this.y = v.y;
    }

    /**
     * Vektoraddition
     */
    public VektorL add(VektorL v)
    {
        return new VektorL(v.x+x,v.y+y);
    }
    
    public VektorL subtract(VektorL v){
        return new VektorL(x-v.x,y-v.y);
    }
    
    /**
     * SMultiplikation
     */
    public VektorD multiply(double s)
    {
        return new VektorD(s*x,s*y);
    }
    
    /**
     * SDivision
     */
    public VektorD divide(double s)
    {
        return new VektorD(x/s,y/s);
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
        return "VektorL["+x+";"+y+"]";
    }
}