package items;
import java.awt.image.BufferedImage;

/**
 * 
 */
public class CraftItem extends Item
{
    private transient String name;
    
    
    public CraftItem(int id, String name, BufferedImage inventoryImage, int maxStack){
        super(id, inventoryImage, maxStack);
    }
    public CraftItem(int id, String name, BufferedImage inventoryImage){
        this(id, name, inventoryImage, 99);
    }
}
