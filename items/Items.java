package items;
import java.util.HashMap;

/**
 * Hier werden alle Items gespeichert
 */
public abstract class Items
{
    public static HashMap<Integer,Item> items=new HashMap<Integer,Item>();
    static{
        // Blockitems ( id 0 - 10000 ist reserviert für Block Items)
        //Craftitems:
    }
    
    public static void registerItem(Item item){
        items.put(item.id, item);
    }
}
