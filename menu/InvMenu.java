package menu;

import javax.swing.SwingUtilities;
import javax.swing.JComponent;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import items.*;
import util.geom.*;
import client.Player;
import client.ClientSettings;


/**
 * Ein Menu in dem ein Inventar ist
 */
public abstract class InvMenu extends PlayerMenu implements MouseMotionListener, MouseListener{
    private transient ItemImage draggedItemImage = null;  // Das ItemImage (in dem das MenuInv, das Inv und die Position gespeichert sind, von dem gedragged wird 
    private transient DraggedItemImage dii = null;  // das Bild von Item, dass gedraggt wird   // ich schäme mich für diese Namen, aber ich hatte keine Lust wieder alles zu ändern  ~unknown
    private transient Stack draggedStack = null; // wenn der Stack vom ItemImage abweicht
    public InvMenu(Player p, String title, VektorI size){
        super(p,title,size);
        this.setEnabled(true);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
    }
    
    /**
     * wird aufgerufen, wenn ein Stack gedroppt wird
     * @return: boolean: Ob der Spieler den Stack droppen darf
     * @param: boolean singleItem: Ob nur ein Item (rechte Maustaste) vom Stack gedropped werden soll
     */
    public boolean onDrop(MenuInv miFrom, VektorI vFrom, MenuInv miTo, VektorI vTo, Stack stack, boolean singleItem){
        return true;
    }
   
    /**
     * wird aufgerufen, nachdem ein Stack gedroppt wurde
     * @param Stack actDroppedStack: der Stack, der tatsächlich verschoben wurde
     */
    public void afterDrop(MenuInv miFrom, VektorI vFrom, MenuInv miTo, VektorI vTo, Stack actDroppedStack){}
    
    /**
     * wird aufgerufen, wenn ein Stack gedragged wird
     * @return: boolean: Ob der Spieler den Stack draggen darf
     */
    public boolean onDrag(MenuInv miFrom, VektorI vFrom, Stack stack){
        return true;
    }
    
    
    /**
     * gibt das ItemImage (und damit den darin gespeicherten Stack) an der Position des Auszeigers zurück
     */
    public ItemImage getMenuInv(VektorI pos){
        Point pointOnContentPane = SwingUtilities.convertPoint(this, pos.x, pos.y, contentPane);  
        Component c = SwingUtilities.getDeepestComponentAt(this.getContentPane(),(int)pointOnContentPane.getX(), (int)pointOnContentPane.getY());
        // Ok lol
        // Mit der deepestComponentAt Methode kann man das unterste Component auf einem Container finden, in diesem Fall das ItemImage
        // Das Problem ist aber, dass LayerdPanes in Java "tiefer" sind als der Inhalt der content Pane und daher das DraggedItemImage (dii)
        // als tiefstes Component gefunden werden kann. Da aber die content pane ein anderes Koosy hat muss mit der Methode convertPoint der
        // Punkt erst in das richtige Koosy übersetzt werden. Das sind so Sachen die keinen Spaß machen
        if(c instanceof ItemImage){
            return (ItemImage)c;
        }
        return null;
    }
    
    // Mouse Events:

    public void mouseDragged(MouseEvent e){
        mouseMoved(e);  // so geht es auch
    }

    public void mouseMoved(MouseEvent e){
        //System.out.println("InvMenu.mouseMoved "+e);
        if(draggedStack == null || dii == null)return; 
        dii.update(new VektorI(SwingUtilities.convertMouseEvent(this, e, this.getLayeredPane())));  // manchmal liebe ich Objektorientierung
        // updated die Position des Bild des gedraggten Items
        // da die scheiß LayeredPanes ein anderes Koosy haben muss das Offset mit der convertMouseEvent Methode angepasst werden
        // und JA es was nervig die Methode zu finden :(
    }

    public void mousePressed(MouseEvent e) {
        //System.out.println("Mouse " + e.getClickCount() + " times pressed at "+ e.getPoint());
        ItemImage ii = getMenuInv(new VektorI(e));
        switch(e.getButton()){
            case MouseEvent.BUTTON1:  // linksklick
            if(draggedStack == null || draggedStack.getCount() == 0)dragStack(ii, new VektorI(SwingUtilities.convertMouseEvent(this, e, this.getLayeredPane())));
            else if(ii != null)dropStack(ii, false);
            break;
            case MouseEvent.BUTTON3:  // rechtsklick
            if(ii != null)dropStack(ii, true); //einzelne Items
            break;
        }
    }

    public void mouseReleased(MouseEvent e){
        if(e.getButton() == MouseEvent.BUTTON3) return; // bei rechtsklick relase nichts tun
        System.out.println("InvMenu.MouseReleased");
        if(draggedStack == null || draggedStack.getCount() == 0 ){removeDragData(); return;} // reset
        ItemImage ii = getMenuInv(new VektorI(e));
        if(ii != null) dropStack(ii, false);
        else resetDrag();
    }
    
    public void dragStack(ItemImage ii, VektorI mousePos){
        // nichts machen, wenn kein Item zu draggen, oder wenn der zu draggende Slot leer ist
        if(ii == null)return;
        Inv invOfStack = ii.getMenuInv().getInv();
        Stack stackAtPos = invOfStack.getStack(ii.getPos());
        if (ii == null || stackAtPos == null || draggedItemImage != null)return; 
        draggedItemImage = ii;
        draggedStack = new Stack(stackAtPos);
        if(!onDrag(ii.getMenuInv(),ii.getPos(),draggedStack)){removeDragData(); return;}
        invOfStack.removeStack(ii.getPos());
        ii.showNull();  // ein leerer Stack wird angezeigt
        dii = new DraggedItemImage(draggedStack, mousePos ,this);
        System.out.println("Item at pos "+draggedItemImage.getPos()+ " dragged.");
    }
    
    /**
     * die Methode ist wichtig, aber ich gehe davon aus, dass die Funktion mit dem Namen beschrieben ist und die genaue
     * Funktionsweise m ganzen nicht relevant ist. ~AK
     */
    public void dropStack(ItemImage dropItemImage, boolean singleItem){
        
        if (dropItemImage == null || draggedItemImage == null)return;  // bei ersten halbieren???
        MenuInv dragMenuInv = draggedItemImage.getMenuInv();
        Stack dropStack = dropItemImage.getStack();
        MenuInv dropMenuInv = dropItemImage.getMenuInv();
        VektorI dragItemPos = draggedItemImage.getPos();
        if(draggedStack == null || draggedStack.getCount() <= 0) {removeDragData(); return;}
        
        if(!onDrop(dragMenuInv,draggedItemImage.getPos(),dropMenuInv,dragItemPos,draggedStack, singleItem)){resetDrag(); return;}
        
        if (dropStack == null) {
            dropStack = new Stack(null , 0);
            dropMenuInv.getInv().setStack(dropItemImage.getPos(), dropStack);
        }
        
        Stack actDroppedStack;
        if(singleItem){
            draggedStack.take(1);
            actDroppedStack = new Stack(draggedStack.getItem(), 1);
            Stack leftover = dropStack.add(actDroppedStack);
            if(leftover != null && leftover.getCount() > 0){
                draggedStack.add(leftover);
                actDroppedStack.take(1);    // => leerer Stack, da nichts verschoben wurde
            }
        }else{ 
            Stack leftover = dropStack.add(draggedStack);
            if(leftover == null || leftover.getCount() <= 0){
                actDroppedStack = draggedStack;
                draggedStack = null;
            }else{
                actDroppedStack = new Stack(draggedStack.getItem(), draggedStack.getCount() -leftover.getCount());
                draggedStack = leftover;
            }
            
        }    
        
        
        if(draggedStack == null || draggedStack.getCount() <= 0)removeDragData();
        else dii.update(draggedStack);
        afterDrop(dragMenuInv, dragItemPos,dropMenuInv,dropItemImage.getPos(), actDroppedStack);
        
        dropMenuInv.updateSlots();
        
    }
    
    /**
     * Setzt das Item wieder an die Stelle an der es vor dem drag war.
     */
    private void resetDrag(){
        try{ 
            draggedItemImage.getMenuInv().getInv().addToStack(draggedItemImage.getPos(), draggedStack);
            draggedItemImage.getMenuInv().updateSlots();
            
        }catch (Exception e){}
        finally{
            removeDragData();
        }
    }
    
    private void removeDragData(){
        draggedStack = null;
        draggedItemImage = null;
        if(dii != null){
            dii.setVisible(false); // da das Remove manchmal nicht geht wird das Item vorher einmal leer repainted
            dii.dispose(this);
            dii = null;
        }
    }

    public void mouseEntered(MouseEvent e) {}  // "Nichts ist besser als Nicht" ~ DSDS Teilnehmer
    public void mouseExited(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}
    
}
