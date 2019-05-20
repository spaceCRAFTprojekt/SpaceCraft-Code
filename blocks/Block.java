package blocks;

import java.util.ArrayList;
import util.geom.*;
import util.ImageTools;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.io.ObjectStreamException;
import items.*;
/**
 * Eine Block in einer Sandbox. Von jeder Art von Block wird nur ein Block initialisiert und jedes Mal verwendet
 */
public class Block implements Serializable
{
    public static final long serialVersionUID=0L;
    private final int id; //Index in der blocks-Tabelle, das einzige, was serialisiert wird (alle anderen Attribute sollten "transient" sein)
    
    private transient BufferedImage img;
    private transient String name;
    
    public transient boolean walkable = true;  // ob ein Spieler durch z.B.: durch Bäume durchgehen kann
    public transient int light = 0;  // ob der Block Licht ausstrahlt
    public transient boolean light_propagates = true;   // ob der Block lichtdurchlässig ist
    public transient boolean climbable = false;  // ob man an dem Block hochklettern kann (muss walkable false sein)
    private transient Item item = null; // falls der Block ein Item hat

    public transient boolean placement_prediction = true; // ob der Block im normalfall plaziert werden kann
    public transient boolean breakment_prediction = true; //  "  gebreakt " " ; goodest EnglisCh
    // ich hab das jetzt man alles public gemacht, weil man sonst so viele getter braucht. Natürlich kann man dann die Wert später verändern, aber wer macht das schon... ~AK
    /**
     * nur static möglich, nicht in-Game (wegen der Bilder für den Client)
     */
    public Block(int id, String name, String imageString, boolean hasItem)
    {
        this(id, name, hasItem);
        this.img = ImageTools.get('C', imageString);
    }
    
    public void setProperties(){
        
    }
    
    public Block(int id, String name, boolean hasItem)
    {
        this.id=id;
        this.name = name;
        Blocks.blocks.put(id,this);
        setProperties();
        if(hasItem){
            item = new BlockItem(id);
            Items.registerItem(item);
        }
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