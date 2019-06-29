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
    /**
     * Index in der blocks-Liste, das einzige, was serialisiert wird (alle anderen Attribute sollten "transient" sein)
     */
    private final int id;
    
    private transient BufferedImage img;
    private transient String name;
    /**
     * ob ein Spieler durch z.B.: durch Bäume durchgehen kann
     */
    public transient boolean walkable = true;
    /**
     * ob der Block Licht ausstrahlt (noch nicht implementiert?)
     */
    public transient int light = 0;
    /**
     * ob der Block lichtdurchlässig ist (noch nicht implementiert?)
     */
    public transient boolean light_propagates = true;
    /**
     * ob man an dem Block hochklettern kann (muss walkable false sein) (noch nicht implementiert?)
     */
    public transient boolean climbable = false;
    /**
     * Masse des Blocks, um die Masse von Raketen berechnen zu können
     */
    public transient double mass = 1;
    /**
     * falls der Block ein Item hat
     */
    public transient Item item = null;
    /**
     * ob der Block im normalfall platziert werden kann
     */
    public transient boolean placement_prediction = true;
    /**
     *  "  gebreakt " " ; goodest EnglisCh
     */
    public transient boolean breakment_prediction = true;
    /**
     * ob der Block das zugehörige Item droppt
     */
    public transient boolean drop_prediction = true;
    /**
     * -1: wenn das entsprechende Item (gleiche ID) oder nichts gedroppt werden soll
     * id: wenn ein anderes Item gedroppt werden soll 
     */
    public transient int drop = -1; 
    
    // ich hab das jetzt man alles public gemacht, weil man sonst so viele getter braucht. Natürlich kann man dann die Wert später verändern, aber wer macht das schon... ~AK
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
        synchronized(Blocks.blocks){
            Blocks.blocks.put(id,this);
        }
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