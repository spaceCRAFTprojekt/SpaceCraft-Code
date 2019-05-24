package menu;

import util.geom.*;
import items.*;
import client.OverlayPanelC;

import javax.swing.*;
/**
 * Die Auswahlleiste in der Craft Ansicht
 * 
 * Es wird einfach das normale Inv-Component extendet und die Methode getInvSize() überschrieben.
 * Deshalb geht die super-Klasse immer davon aus, dass das inv nur eine Zeile hat. Deshalb wird auch nur 
 * die erste Zeile gezeichnet. 
 * 
 * Ehrlich ist, wer seine Faulheit nicht für Müdigkeit ausgibt.  ~ der Sandmann
 * 
 * Dieses falsch zugeordnete Zitat wurde Ihnen präsentiert von unknown.
 */
public class Hotbar extends MenuInv
{
    public Hotbar(OverlayPanelC opC, PlayerInv inv, VektorI screenSize){
        super(null, inv);
        
        resize(screenSize);
        opC.add(this);
    }
    
    public void resize(VektorI screenSize){
        this.setLocation((screenSize.x - getWidth())/2, screenSize.y - 50 - getHeight());
    }
    
    @Override public VektorI getInvSize(){
        return new VektorI(inv.getSizeX(),1);
    }
    
}