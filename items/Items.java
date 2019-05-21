package items;
import java.util.HashMap;

/**
 * Hier werden alle Items gespeichert
 */
public abstract class Items
{
    private static HashMap<Integer,Item> items;
    static{
        if(items == null)items = new HashMap<Integer,Item>();
        // Blockitems ( id 0 - 10000 ist reserviert für Block Items)
        //Craftitems:
    }
    
    public static void registerItem(Item item){
        System.out.println("hallo "+item.id);
        if(items == null)items = new HashMap<Integer,Item>();
        items.put(item.id, item);
    }
    
    public static Item get(int id){
        if(items == null)items = new HashMap<Integer,Item>();
        return items.get(id);
    }
}
