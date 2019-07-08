package blocks;
import java.util.HashMap;
import java.util.Map.Entry;
import java.awt.image.BufferedImage;
import items.*;

/**
 * hier wird je ein Objekt jedes Blocks gespeichert (z.B.: Erde, Sand, Stein, Wasser)
 * Soll nie initialisiert werden!
 * 
 * v0.0.6 AK * Alles geändert
 * 
 * v0.3.2_AK * mit { @Override public void setProperties(){breakment_prediction = false;}};  können Properties von Blöcken überschrieben werden
 */
public abstract class Blocks
{
    public static final HashMap<Integer,Block> blocks = new HashMap<Integer,Block>();
    
    static{
        //System.out.println("static");
        
        new Block(0, "grass", "blocks_grass", true);
        new Block(1, "dirt", "blocks_dirt", true); 
        new Block(2, "stone", "blocks_stone", true);
        new Block(3, "iron", "blocks_iron_ore", true);
        new Block(4, "gold", "blocks_gold_ore", true);
        new Block(5, "copper", "blocks_copper_ore", true);
        new Block(10, "tree", "blocks_tree", true);  // auf keinen Fall ändern !!!
        new Block(11, "leaves", "blocks_leaves", true);
        new Blocks_Sapling(12);
        new Block(13, "wooden_planks", "blocks_wooden_planks", true);
         
        
        
        new Blocks_Chest(100);
        new Blocks_Note(104); 
        new Block(110, "sand", "blocks_sand", true);
        new Block(111, "glass", "blocks_glass", true);
       
        new Block(141, "roof_tile", "blocks_roof_tile", true);
        new Block(142, "table", "blocks_table", true);
        new Blocks_Door(120);  
        // door_open_top: 121
        // door_close_bottom: 122
        // door_close_top: 123
        new Blocks_Piston(300);
        // piston_on: 301
        // piston_front: 302
        new Blocks_Rocket_Controller(400);  // "rocketController"
        new Blocks_Rocket_Fuel(401);        // "fuel"
    }
    /**
     * gibt den Block mit der id zuück
     */
    public static Block get(int id){
        return blocks.get(id);
    }
    public static BufferedImage getTexture(int id){
        Block block = get(id);
        if(block != null)return block.getImage();
        else return null;
    }
    /**
     * gibt den Block mit dem Namen name zurück.
     * Wenn möglich besser get(int id) verwenden, da dafür weniger Rechenzeit benötigt wird!
     */
    public static Block get(String name){
        for (Entry<Integer, Block> entry : blocks.entrySet()) {
            if (entry.getValue().getName() == name) {
                return entry.getValue();
            }
        }
        return null;
    }
}
