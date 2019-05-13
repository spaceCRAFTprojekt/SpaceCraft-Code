package items;

import client.BlocksC;

import java.awt.image.BufferedImage;

/**
 * Ein item, das mit einem Block verlinkt ist
 * Achtung : di blockID != id
 */
public class BlockItem extends Item
{
    
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
        if(super.getInventoryImage() == null)return BlocksC.images.get(blockID);
        return super.getInventoryImage();  // falls das InvBild nicht das gleiche wie die Block Textur ist
    }
}
