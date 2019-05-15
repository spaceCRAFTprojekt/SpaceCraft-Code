package items;

import util.geom.*;

/**
 * Ein SpielerInventar
 */
public class PlayerInv extends Inv
{
    private VektorI hotkeyPos;
    public PlayerInv(){
        super(InvSettings.INV_SIZE);
        
    }

}
