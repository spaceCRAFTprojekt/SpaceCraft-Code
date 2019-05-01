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
    int dir = -1;
    //   0     -1, wenn die Richtung egal ist, oder an die Map angepasst werden soll
    // 3   1
    //   2
    /**
     * ...
     */
    public Block(String name, String imageString)
    {
        this.id=Blocks.blocks.size();
        this.img = ImageTools.get('C', imageString);
        this.name = name;
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
    
    public void onRightclick(){
        
    }
    
    public void onLeftClick(){
        
    }
    
    public boolean onBreak(Player p){
        return true;
    }
    
    public void onTimer(){
        
    }
    
    public String toString(){
        return "Block: id = "+this.id+", name = "+this.name;
    }
}
