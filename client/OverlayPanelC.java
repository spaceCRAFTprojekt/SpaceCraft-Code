package client;

import java.awt.Graphics;

import util.geom.*;
import menu.*;
/**
 * OverlayPanel für Craft
 * z.B. für Chat, Hotbar
 */
public class OverlayPanelC extends OverlayPanel
{
    MenuInv hotbar;

    InfoPopup ip;
    public OverlayPanelC(Frame frame, Player p, VektorI screenSize){
        super(frame,p,screenSize);
    }
}