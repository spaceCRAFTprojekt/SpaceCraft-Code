package items;
import java.util.HashMap;
import java.util.Map.Entry;
import util.ImageTools;
/**
 * Hier werden alle Items gespeichert. Diese Klasse sollte nie instanziiert werden.
 */
public abstract class Items
{
    /**
     * id 0-10000 ist reserviert für BlockItems
     */
    private static HashMap<Integer,Item> items=new HashMap<Integer,Item>();
    static{
        registerItem(new CraftItem(10001,"stick",ImageTools.get('C',"items_stick")));
        registerItem(new CraftItem(10004,"paper",ImageTools.get('C',"items_paper")));
    }
    
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
