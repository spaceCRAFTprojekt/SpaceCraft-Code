package items;
import java.util.HashMap;
import java.util.Map.Entry;
/**
 * Hier werden alle Items gespeichert. Diese Klasse sollte nie instanziiert werden.
 */
public abstract class Items
{
    /**
     * id 0-10000 ist reserviert für BlockItems
     */
    private static HashMap<Integer,Item> items=new HashMap<Integer,Item>();
    
    public static void registerItem(Item item){
        synchronized(items){
            items.put(item.id, item);
        }
    }
    
    public static Item get(int id){
        return items.get(id);
    }
    
    public static Item get(String name){
        for (Entry<Integer, Item> entry : items.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
