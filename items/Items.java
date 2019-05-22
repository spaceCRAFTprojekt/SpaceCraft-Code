package items;
import java.util.HashMap;

/**
 * Hier werden alle Items gespeichert
 */
public abstract class Items
{
    private static HashMap<Integer,Item> items=new HashMap<Integer,Item>();
    // Blockitems ( id 0 - 10000 ist reserviert für Block Items)
    //Craftitems:
    
    public static void registerItem(Item item){
        synchronized(items){
            items.put(item.id, item);
        }
    }
    
    public static Item get(int id){
        return items.get(id);
    }
}
