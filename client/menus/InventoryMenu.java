package client.menus;

import client.*;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;


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
    private transient MenuInv mi_main;
    private transient MenuInv mi_crafting;
    private transient MenuInv mi_output;
    private transient MenuButton button_player_texture;
    private static int BORDER = 10;
    private static VektorI offset = new VektorI(20,20);
    private GroupLayout layout;
    public InventoryMenu(Player p, Inv inv)
    {
        super(p, "Inventory", new VektorI(InvSettings.SLOT_SIZE*MenuSettings.INV_SIZE.x + offset.x*2 + 15,
                                          InvSettings.SLOT_SIZE*(MenuSettings.INV_SIZE.y+5) + 30+ offset.y*2));  // +70: Platz für Buttons
        setFont(MenuSettings.MENU_FONT);
        this.layout = new GroupLayout(this.getLayeredPane());
        this.getLayeredPane().setLayout(layout);
        this.layout.setAutoCreateGaps(true);
        this.layout.setAutoCreateContainerGaps(true);
        mi_crafting = new MenuInv(this, new Inv(new VektorI(3,3)));
        mi_crafting.setLocation(offset.x+InvSettings.SLOT_SIZE*2,offset.y);
        
        mi_output = new MenuInv(this, new Inv(new VektorI(1,1)));
        mi_output.setLocation(offset.x+InvSettings.SLOT_SIZE*7, InvSettings.SLOT_SIZE+offset.y);
        mi_main = new MenuInv(this,inv);
        mi_main.setLocation(offset.x, (int)(InvSettings.SLOT_SIZE*3.5)+offset.y);
        //mi_main.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
        
        button_player_texture = new MenuButton(this, "Texture", new VektorI(offset.x, offset.y+8*InvSettings.SLOT_SIZE), new VektorI(100, 40)){
            public void onClick(){
                new TextureSelectMenu(p);
                dispose();
            }
        };
        
 
            
         
        
        
    }
}