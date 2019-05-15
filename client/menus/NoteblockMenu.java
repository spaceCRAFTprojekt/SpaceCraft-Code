package client.menus;

import client.*;
import menu.*;
import util.geom.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
public class NoteblockMenu extends PlayerMenu{
    MenuTextArea mta;
    MenuButton mb;
    String text;
    VektorI pos; //des NoteBlocks
    public NoteblockMenu(Player p, VektorI pos, String text){
        super(p,"Note-Block", new VektorI(300,340));
        this.text=text;
        this.pos=pos;
        new MenuLabel(this, "Notes:", new VektorI(10,10), new VektorI(100,30), -1);
        mta = new MenuTextArea(this,text,new VektorI(10,40),new VektorI(260,210));
        boolean onPlanet=p.getPlayerC().isOnPlanet();
        int sandboxIndex=p.getPlayerC().getSandboxIndex();
        mb = new MenuButton(this, "Save", new VektorI(170,260), new VektorI(100, 30)){
            public void onClick(){
                Object[] menuParams={onPlanet,sandboxIndex,pos,mta.getText()};
                Boolean success=(Boolean) (new Request(getPlayer().getID(),"Main.returnFromMenu",Boolean.class,"NoteblockMenu",menuParams).ret);
                closeMenu();
            }
        };
    }
}