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
public abstract class InvMenu extends Menu implements MouseMotionListener, MouseListener{
    private transient ItemImage draggedItemImage = null;  // Das ItemImage (in dem das MenuInv, das Inv und die Position gespeichert sind)
    private transient Stack draggedStack = null; // wenn der Stack vom ItemImage abweicht
    public InvMenu(Player p, String title, VektorI size){
        super(p,title,size);
        
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
        if(draggedItemImage != null){
            //TO DO
        }
    }

    public void mousePressed(MouseEvent e) {
        //System.out.println("Mouse " + e.getClickCount() + " times pressed at "+ e.getPoint());
        ItemImage ii = getMenuInv(new VektorI(e));
        switch(e.getButton()){
            case MouseEvent.BUTTON1:
            if(draggedItemImage == null)dragStack(ii);
            else if(ii == null)dropStack(ii);
            else if(draggedItemImage.equals(ii))draggedItemImage = null;
            else dropStack(ii);
            break;
            case MouseEvent.BUTTON3:
            dropStack(getMenuInv(new VektorI(e))); //einzelne Items???
            break;
        }
    }

    public void mouseReleased(MouseEvent e){
        if(draggedItemImage == null)return;
        ItemImage ii = getMenuInv(new VektorI(e));
        if(ii == null)dropStack(ii);
        else if(draggedItemImage.equals(ii))return;
        else dropStack(ii);
    }
    
    public void dragStack(ItemImage ii){
        // nichts machen, wenn kein Item zu draggen, oder wenn der zu draggende Slot leer ist
        if (ii == null || ii.getMenuInv().getInv().getStack(ii.getPos())==null || draggedItemImage != null)return; 
        draggedItemImage = ii;
        System.out.println("Item at pos "+draggedItemImage.getPos()+ " dragged.");
    }
    
    public void dropStack(ItemImage dropItemImage){
        if (dropItemImage == null || draggedItemImage == null)return;  // bei ersten halbieren???
        Stack dragStack = draggedItemImage.getStack();
        MenuInv dragMenuInv = draggedItemImage.getMenuInv();
        Stack dropStack = dropItemImage.getStack();
        MenuInv dropMenuInv = dropItemImage.getMenuInv();
        if(dragStack == null)return;
        if(dragStack.getCount() <= 0) return;

        if (dropStack == null){
            dropMenuInv.getInv().setStack(dropItemImage.getPos(), dragStack);
            dragMenuInv.getInv().removeStack(draggedItemImage.getPos());
            draggedItemImage = null;
        }else{
            Stack leftover = dropStack.add(dragStack);
            if(leftover == null || leftover.getCount() <= 0){
                dragMenuInv.getInv().removeStack(draggedItemImage.getPos());
                draggedItemImage = null;
            }else{
                dragMenuInv.getInv().setStack(draggedItemImage.getPos(), leftover);
            }
        } 
        
        dragMenuInv.updateSlots();
        if(!dropMenuInv.equals(dragMenuInv))dropMenuInv.updateSlots();

    }

    public void mouseEntered(MouseEvent e) {}  // "Nichts ist besser als Nicht" ~ DSDS Teilnehmer
    public void mouseExited(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}
    
}
