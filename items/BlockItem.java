package items;

import blocks.*;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Ein item, das mit einem Block verlinkt ist
 * Achtung : die blockID != id
 */
public class BlockItem extends Item implements Serializable
{
    public static final long serialVersionUID=0L;
    private transient int blockID;
    
    public BlockItem(int id, int blockID){
        super(id);
        this.blockID = blockID;
    } 
    
    public BlockItem(int id, int blockID, BufferedImage inventoryImage){
        super(id, inventoryImage);
        this.blockID = blockID;
    }
    
    @Override
    public BufferedImage getInventoryImage(){
        if(super.getInventoryImage() == null)return Blocks.getTexture(blockID);
        return super.getInventoryImage();  // falls das InvBild nicht das gleiche wie die Block Textur ist
    }
}
