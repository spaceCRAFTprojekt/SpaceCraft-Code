package items;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * 
 */
public class CraftItem extends Item implements Serializable
{
    public static final long serialVersionUID=0L;
    private transient String name;
    public CraftItem(int id, String name, BufferedImage inventoryImage, int maxStack){
        super(id, inventoryImage, maxStack);
    }
    public CraftItem(int id, String name, BufferedImage inventoryImage){
        this(id, name, inventoryImage, 99);
    }
}
