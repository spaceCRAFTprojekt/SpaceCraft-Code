package blocks;

 

import client.*;
import server.*;
import util.geom.*;
import items.*;  // eig alles xD

/**
 * Die Erklärung der Methoden befindet sich in der Klasse SBlock
 */
public class Blocks_Chest extends SBlock
{
    public static VektorI size = new VektorI(10,4);
    public Blocks_Chest(int id){
        // id, name, bild, hat ein item
        super(id, "Chest", "blocks_chest", true);
        this.breakment_prediction = false;
    }

    @Override
    public void onRightclick(Sandbox sb, VektorI pos, int playerID){
        Meta meta = sb.getMeta(pos);
        Object[] menuParams={pos,meta.get("inv_main")};
        Main.main.newTask(playerID,"Player.showMenu","ChestMenu",menuParams);
    }
    
    /**
     * Erstellt ein neues Metaobjekt mit dem Inventar in diesem Metaobjekt
     */
    @Override public void onConstruct(Sandbox sb, VektorI pos){
        Meta meta = new Meta();
        meta.put("inv_main", new Inv(size));
        sb.setMeta(pos, meta);
    }
}
