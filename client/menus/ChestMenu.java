package client.menus;


import client.*;
import menu.*;
import util.geom.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import items.*;

public class ChestMenu extends InvMenu{
    MenuInv invM_player;
    Inv inv_main;
    MenuInv invM_main;
    
    boolean onPlanet;
    int sandboxIndex;
    VektorI pos;
    public ChestMenu(Player p, VektorI pos, Inv inv_main){
        super(p,"Note-Block", new VektorI(550,500));
        
        this.inv_main = inv_main;
        invM_main = new MenuInv(this, inv_main, new VektorI(20,20));
        invM_player = new MenuInv(this, p.getPlayerC().getInv(), new VektorI(20,250));
        
        onPlanet=p.getPlayerC().isOnPlanet();
        sandboxIndex=p.getPlayerC().getSandboxIndex();  //
        this.pos = pos;
        validate();
    }
    
    
    @Override public void closeMenu(){
        Object[] menuParams={onPlanet,sandboxIndex,pos,inv_main};
        Boolean success=(Boolean) (new Request(getPlayer().getID(),getPlayer().getRequestOut(),getPlayer().getRequestIn(),"Main.returnFromMenu",Boolean.class,"ChestMenu",menuParams).ret);
        super.closeMenu();
    }
} 