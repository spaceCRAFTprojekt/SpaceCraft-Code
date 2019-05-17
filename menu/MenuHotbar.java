package menu;

import util.geom.*;
import items.*;
/**
 * Write a description of class MenuHotbar here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class MenuHotbar extends MenuInv
{
    public MenuHotbar(PlayerInv inv){
        super(null, inv);  // ja das mit dem null statt dem Menu ist nicht schön, aber es geht

    }

    @Override public VektorI getInvSize(){
        return new VektorI(inv.getSizeX(),1);
    }
}