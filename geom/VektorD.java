package geom;
import java.lang.Math;
import java.io.Serializable;

/**
 * Simple Integer Vektor
 * Bei Veränderung Bitte in der readme Datei erwähnen!!!
 * 0.0.1: erstellt
 * 0.0.2: SDivision und toIntFloor/Ceil ergänzt
 */
public class VektorD implements Serializable
{
    public double x;
    public double y;

    public VektorD(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Vektoraddition
     */
    public VektorD add(VektorD v)
    {
        return new VektorD(v.x+x,v.y+y);
    }
    
    public VektorD subtract(VektorD v){
        return new VektorD(v.x-x,v.y-y);
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
        return new VektorD((x/s),(y/s));
    }
    
    /**
     * Betrag des Vektors
     */
    public double getLength()
    {
        return Math.hypot(x,y);
    }
    
    /**
     * Rundet den Vektor und gibt einen Int-Vektor zurück
     */
    public VektorI toInt()
    {
        return new VektorI((int)x,(int)y);
    }
    
    /**
     * Rundet den Vektor ab und gibt einen Int-Vektor zurück
     */
    public VektorI toIntFloor()
    {
        return new VektorI((int)Math.floor(x),(int)Math.floor(y));
    }
    
    /**
     * Rundet den Vektor auf und gibt einen Int-Vektor zurück
     */
    public VektorI toIntCeil()
    {
        return new VektorI((int)Math.ceil(x),(int)Math.ceil(y));
    }
    
    /**
     * Gibt einen String zurück, der den Vektor beschreibt:
     * VektorD[x;y]
     */
    public String toString()
    {
        return "VektorD["+x+";"+y+"]";
    }
}
