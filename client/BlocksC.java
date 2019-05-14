package client;
import java.util.HashMap;
import java.awt.image.BufferedImage;
public abstract class BlocksC{ //Blocks für den Client. Enthält als einzige Information IDs und Bilder
    public static HashMap<Integer,BufferedImage> images=new HashMap<Integer,BufferedImage>();
    static{
        images.put(000,ImageTools.get('C', "blocks_stone"));
        images.put(001,ImageTools.get('C', "blocks_dirt"));
        images.put(002,ImageTools.get('C', "blocks_grass"));
        images.put(104,ImageTools.get('C', "blocks_note"));
        images.put(300,ImageTools.get('C', "blocks_piston_off"));
    }
}