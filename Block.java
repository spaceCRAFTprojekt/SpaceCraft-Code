import java.util.ArrayList;
import geom.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.io.ObjectStreamException;
/**
 * Eine Block in einer Sandbox. Von jeder Art von Block wird nur ein Block initialisiert und jedes Mal verwendet
 */
public class Block implements Serializable
{
    private final int id; //Index in der blocks-Tabelle, das einzige, was serialisiert wird (alle anderen Attribute sollten "transient" sein)
    
    private transient BufferedImage img;
    private transient String name;
    
    private transient boolean walkable = true;  // ob ein Spieler durch z.B.: durch Bäume durchgehen kann
    private transient int light = 0;  // ob der Block Licht ausstrahlt
    private transient boolean light_propagates = true;   // ob der Block lichtdurchlässig ist
    private transient boolean climbable = false;  // ob man an dem Block hochklettern kann (muss walkable false sein)
    //private transient Item item = null; // falls der Block ein Item hat
    //private transient BufferedImage inventoryImage = null; // falls im Inv ein anderes Bild angezeigt werden soll
    //private transient Item drops = null;
    
    
    /**
     * ...
     */
    public Block(int id, String name, String imageString)
    {
        this(id, name);
        this.img = ImageTools.get('C', imageString);
    }
    
    public Block(int id, String name)
    {
        this.id=id;
        this.name = name;
        Blocks.blocks.put(id,this);
    }
    
    public Object readResolve() throws ObjectStreamException{
        return Blocks.blocks.get(id);
    }
    
    /**
     * Gibt die Textur des Blocks zurück
     */
    public BufferedImage getImage()
    {
        return img;
    }
    
    /**
     * Setzt eine andere Textur
     */
    public void setImage(BufferedImage img)
    {
        this.img = img;
    }
    
    /**
     * Gibt den Namen des Blocks zurück
     */
    public String getName()
    {
        return name;
    }
    
    public int getID(){
        return id;
    }
    
    public String toString(){
        return "Block: id = "+this.id+", name = "+this.name;
    }
}
