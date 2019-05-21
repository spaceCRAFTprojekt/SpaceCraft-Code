package client.menus;

import client.*;

import javax.swing.BorderFactory;

import items.*;
import menu.*;
import util.geom.*;

/**
 * Frame für das Inventar eines Spielers
 * 
 * Abkürzung InvMenu leider schon belegt
 */
public class InventoryMenu extends InvMenu  // wer macht da immer PlayerMenu draus??
{
    private transient MenuInv mi;
    private transient MenuButton button_player_texture;
    private static int BORDER = 10;
    public InventoryMenu(Player p, Inv inv)
    {
        super(p, "Inventory", new VektorI(InvSettings.SLOT_SIZE*MenuSettings.INV_SIZE.x + 2*BORDER,
                                          InvSettings.SLOT_SIZE*MenuSettings.INV_SIZE.y + 2*BORDER + 70));  // +70: Platz für Buttons
        mi = new MenuInv(this, inv);
        mi.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
        button_player_texture = new MenuButton(this, "Texture", new VektorI(10, getHeight()-90), new VektorI(100, 40)){
            public void onClick(){
                new TextureSelectMenu(p);
                dispose();
            }
        };
    }
}