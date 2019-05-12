package client;

import javax.swing.BorderFactory;

import items.*;
import menu.*;
import util.geom.*;

/**
 * Frame für das Inventar eines Spielers
 * 
 * Abkürzung InvMenu leider schon belegt
 */
public class InventoryMenu extends Menu
{
    private transient MenuInv mi;
    private static int BORDER = 10;
    public InventoryMenu(Player p, Inv inv)
    {
        super(p, "Inventory", new VektorI(InvSettings.SLOT_SIZE*ClientSettings.INV_SIZE.x + 2*BORDER,
                                          InvSettings.SLOT_SIZE*ClientSettings.INV_SIZE.y + 2*BORDER + 30));
        mi = new MenuInv(this, inv);
        mi.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
    }
}
