package menu;

import util.geom.*;
import items.*;
import client.OverlayPanelC;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.Color;
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
    private int scrollPos = 0;
    public Hotbar(OverlayPanelC opC, PlayerInv inv, VektorI screenSize){
        super(null, inv);
        
        resize(screenSize);
        opC.add(this);
        updateBorder();
    }
    
    public void resize(VektorI screenSize){
        this.setLocation((screenSize.x - getWidth())/2, screenSize.y - 50 - getHeight());
    }
    
    public void scroll(int posN){ 
        
        int halfBorder = MenuInv.border/2;
        slots[scrollPos][0].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(halfBorder,halfBorder,halfBorder,halfBorder),
            BorderFactory.createLoweredBevelBorder()));
        this.scrollPos = posN;
        scrollPos = ((scrollPos%inv.getSizeX())+inv.getSizeX())%inv.getSizeX();
        updateBorder();
    }
    
    /**
     * scrollt um die angegebene Zahl an Feldern
     */
    public void scrollDelta(int dPos){
        scroll( (((scrollPos + dPos)%inv.getSizeX())+inv.getSizeX())%inv.getSizeX() );
    }
    
    public void updateBorder(){
        int halfBorder = MenuInv.border/2;
        slots[scrollPos][0].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(halfBorder,halfBorder,halfBorder,halfBorder, Color.RED),
            BorderFactory.createBevelBorder(BevelBorder.LOWERED, new Color(255,20,20), new Color(200,0,0))));
    }
    
    @Override public VektorI getInvSize(){
        return new VektorI(inv.getSizeX(),1);
    }
    
    /**
     * gibt den gerade ausgewählten Stack zurück
     */
    public Stack getHotStack(){
        return inv.getStack(new VektorI(scrollPos, 0));
    }
}