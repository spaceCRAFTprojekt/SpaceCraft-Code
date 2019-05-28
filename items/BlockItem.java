package items;

import blocks.*;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Ein item, das mit einem Block verlinkt ist
 */
public class BlockItem extends Item implements Serializable
{
    public static final long serialVersionUID=0L;
    
    public BlockItem(int id){
        super(id);
    } 
    
    public BlockItem(int id, BufferedImage inventoryImage){
        super(id, inventoryImage);
    }
    
    @Override
    public BufferedImage getInventoryImage(){
        if(super.getInventoryImage() == null)return Blocks.getTexture(id);
        return super.getInventoryImage();  // falls das InvBild nicht das gleiche wie die Block Textur ist
    }
    
    public String getName(){
        return Blocks.get(id).getName();
    }
}
