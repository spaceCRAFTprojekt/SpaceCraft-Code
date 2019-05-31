package menu;

import javax.swing.SwingUtilities;
import javax.swing.JComponent;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import items.*;
import util.geom.*;
import client.Player;


/**
 * Ein Menu in dem ein Inventar ist
 */
public abstract class InvMenu extends PlayerMenu implements MouseMotionListener, MouseListener{
    private transient ItemImage draggedItemImage = null;  // Das ItemImage (in dem das MenuInv, das Inv und die Position gespeichert sind)
    private transient Stack draggedStack = null; // wenn der Stack vom ItemImage abweicht
    public InvMenu(Player p, String title, VektorI size){
        super(p,title,size);
        this.setEnabled(true);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
    }
    
    
    public ItemImage getMenuInv(VektorI pos){
        Component c = SwingUtilities.getDeepestComponentAt(this, pos.x, pos.y);
        if(c instanceof ItemImage){
            return (ItemImage)c;
        }
        return null;
    }
    
    // Mouse Events:

    public void mouseDragged(MouseEvent e){
        //System.out.println("Mouse " + e.getClickCount() + " times released at "+ e.getPoint());
    }

    public void mouseMoved(MouseEvent e){
        if(draggedStack != null)return;
        
    }

    public void mousePressed(MouseEvent e) {
        //System.out.println("Mouse " + e.getClickCount() + " times pressed at "+ e.getPoint());
        ItemImage ii = getMenuInv(new VektorI(e));
        switch(e.getButton()){
            case MouseEvent.BUTTON1:  // linksklick
            if(draggedStack == null || draggedStack.getCount() == 0)dragStack(ii);
            else if(ii != null)dropStack(ii, false);
            break;
            case MouseEvent.BUTTON3:  // rechtsklick
            if(ii != null)dropStack(ii, true); //einzelne Items
            break;
        }
    }

    public void mouseReleased(MouseEvent e){
        if(e.getButton() == MouseEvent.BUTTON3) return; // bei rechtsklick relase nichts tun
        System.out.println("released");
        if(draggedStack == null || draggedStack.getCount() == 0 ){draggedStack = null; draggedItemImage = null; return;} // reset
        ItemImage ii = getMenuInv(new VektorI(e));
        if(ii != null) dropStack(ii, false);
    }
    
    public void dragStack(ItemImage ii){
        // nichts machen, wenn kein Item zu draggen, oder wenn der zu draggende Slot leer ist
        if(ii == null)return;
        Inv invOfStack = ii.getMenuInv().getInv();
        Stack stackAtPos = invOfStack.getStack(ii.getPos());
        if (ii == null || stackAtPos == null || draggedItemImage != null)return; 
        draggedItemImage = ii;
        draggedStack = new Stack(stackAtPos);
        invOfStack.removeStack(ii.getPos());
        ii.showNull();  // ein leerer Stack wird angezeigt
        System.out.println("Item at pos "+draggedItemImage.getPos()+ " dragged.");
    }
    
    public void dropStack(ItemImage dropItemImage, boolean singleItem){
        System.out.println("Item to pos "+dropItemImage.getPos()+ " dropped.");
        if (dropItemImage == null || draggedItemImage == null)return;  // bei ersten halbieren???
        MenuInv dragMenuInv = draggedItemImage.getMenuInv();
        Stack dropStack = dropItemImage.getStack();
        MenuInv dropMenuInv = dropItemImage.getMenuInv();
        if(draggedStack == null || draggedStack.getCount() <= 0) {draggedStack = null; draggedItemImage = null; return;}

        if (dropStack == null) {
            dropStack = new Stack(null , 0);
            dropMenuInv.getInv().setStack(dropItemImage.getPos(), dropStack);
        }
        
        Stack leftover;
        if(singleItem){ 
            draggedStack.take(1);
            leftover = draggedStack;
            leftover.add(dropStack.add(new Stack(draggedStack.getItem(), 1)));
        }
        else leftover = dropStack.add(draggedStack);
        if(leftover == null || leftover.getCount() <= 0){
            draggedItemImage = null; draggedStack = null;
        } 
        
        dropMenuInv.updateSlots();

    }

    public void mouseEntered(MouseEvent e) {}  // "Nichts ist besser als Nicht" ~ DSDS Teilnehmer
    public void mouseExited(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}
    
}
