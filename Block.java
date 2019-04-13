import java.util.ArrayList;
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
    private transient String imageString;
    
    /**
     * ...
     */
    public Block(String name, String imageString)
    {
        this.id=Blocks.blocks.size();
        this.img = ImageTools.get('C', imageString);
        this.name = name;
        this.imageString=imageString;
        Blocks.blocks.put(id,this);
    }
    
    public Object readResolve() throws ObjectStreamException{
        return Blocks.blocks.get(id);
    }
    
    /**
     * Gibt die Farbe des Blocks zurück
     */
    public BufferedImage getImage()
    {
        return img;
    }
    
    /**
     * Gibt den Namen des Blocks zurück
     */
    public String getName()
    {
        return name;
    }
    
    public String getImageString(){
        return imageString;
    }
    
    public int getID(){
        return id;
    }
    
    public String toString(){
        return "Block: id = "+this.id+", name = "+this.name+", image: "+this.imageString;
    }
}
