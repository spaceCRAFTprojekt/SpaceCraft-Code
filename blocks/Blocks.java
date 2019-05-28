package blocks;

 

import java.util.HashMap;
import java.util.Map.Entry;
import java.awt.image.BufferedImage;

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
        new Block(000, "stone", "blocks_stone", true);
        new Block(001, "dirt", "blocks_dirt", true); 
        new Block(002, "grass", "blocks_grass", true); 
        new Blocks_Chest(100);
        new Blocks_Note(104); // id kann noch verändert werden
        new Blocks_Piston(300);
        // piston_on: 301
        // piston_front: 302
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
