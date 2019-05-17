package items;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * 
 */
public abstract class Item implements Serializable
{
    private int id;  // nur die ID ist nicht transient => wird gespeichert
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
    
    public boolean equals(Item item){
        return id == item.id;
    }
    
}
