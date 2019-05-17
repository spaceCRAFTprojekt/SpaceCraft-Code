package items;

import util.geom.*;
import menu.MenuSettings;

/**
 * Ein SpielerInventar
 */
public class PlayerInv extends Inv
{
    private VektorI hotkeyPos;
    public PlayerInv(){
        super(MenuSettings.INV_SIZE);

    }

}