package items;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import util.*;

/**
 * 
 */
public abstract class Item implements Serializable
{
    public static final long serialVersionUID=0L;
    public int id;  // nur die ID ist nicht transient => wird gespeichert
    private transient int maxStack;
    private transient BufferedImage inventoryImage = null; // falls im Inv ein anderes Bild angezeigt werden soll
    
    
    public Item(int id, BufferedImage inventoryImage, int maxStack){
        this.id = id;
        this.inventoryImage = inventoryImage;
        this.maxStack = maxStack;
    }
    
    public Item(int id, BufferedImage inventoryImage){
        this(id, inventoryImage, 99);
    }
    
    public Item(int id, int maxStack){
        this(id, null, 99);
    }
    
    public Item(int id){
        this(id, null);
    }
    
    public BufferedImage getInventoryImage(){
        return inventoryImage;
    }
    
    public void setInventoryImage(String imgS){
        inventoryImage = ImageTools.get('C',imgS);
    }
    
    public void setInventoryImage(BufferedImage img){
        inventoryImage = img;
    }
    
    public boolean equals(Item item){
        return id == item.id;
    }
    
    public abstract String getName();
}
