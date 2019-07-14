package items;

import util.geom.*;
import menu.MenuSettings;
import menu.Hotbar;
/**
 * Ein SpielerInventar (Also das inventar mit der entsprechenden Grˆﬂe)
 */
public class PlayerInv extends Inv
{
    public transient Hotbar hotbar;
    public PlayerInv(){
        super(MenuSettings.INV_SIZE);
        
    }
    
    @Override public void update(){
        if (hotbar != null)hotbar.updateSlots();
    }
    
    public void setHotbar(Hotbar hotbar){
        this.hotbar = hotbar;
    }
    
    /**
     * gibt den gerade ausgew√§hlten Stack zur√ºck
     */
    public Stack getHotStack(){
        if(hotbar == null)return null;
        return hotbar.getHotStack();
    }
}
    