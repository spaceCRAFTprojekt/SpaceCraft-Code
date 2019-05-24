package menu;

//It Dont't Mean A Thing It It Ain't Got That SWING
import javax.swing.JPanel;
import javax.swing.BorderFactory;
//Alter Was [soll] Tas
import java.awt.image.BufferedImage;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.dnd.*;
import java.awt.event.*;

//ach MEIN, dein, das sind doch bürgerliche Kategorien
import util.geom.VektorI;
import items.*;
/**
 * Das Menu eines Inventars. Das kann ein Slot oder eine ganze Chest sein
 */
public class MenuInv extends JPanel

{
    public static int iconSize = 50; // Pixel
    public static int border = iconSize/12;  //in Pixel: Abstand zweier Slots
    private transient ItemImage[][]slots;
    public transient Inv inv;
    private transient VektorI draggedItemPos = null;
    /**
     * Ich hatte keinen Block einen Konstruktor zu beschreiben xD
     */
    public MenuInv(Menu m, Inv inv){
        super();
        this.inv = inv;
        VektorI size = getInvSize(); // Achtung nicht ändern, weil getSize() wird in Hotbar überschrieben

        this.setSize(size.x*iconSize, size.y*iconSize);
        this.setLayout(new GridLayout(size.y, size.x));   // Warum Y vor X??? Liebe Java Entwickler, so ein Fehler muss doch auffallen! Arrrrrrrrrrrrrrrrrrgh!!!
        this.setVisible(true);
        this.setEnabled(false); //damit die Events zum InvMenu kommen
        this.setOpaque(false);  // kein Hintergrund

        slots = new ItemImage[size.x][size.y];
        initSlots(size);

        if (m!=null) m.contentPane.add(this); // und fÃ¼gt ihn zur Pane hinzu (in der Hotbar ist m null)
    }
    
    public MenuInv(Menu m, Inv inv, VektorI pos){
        this(m, inv);
        this.setLocation(pos.x, pos.y);
    }

    public void initSlots(VektorI size){
        for(int y = 0; y < size.y; y++){
            for(int x = 0; x < size.x; x++){
                slots[x][y] = new ItemImage(this, inv.getStack(new VektorI(x,y)),new VektorI(x,y), iconSize);
                this.add(slots[x][y]);
            }
        }
    }

    /**
     * Diese Methode bräuchte mehr Liebe (falls mal jemand Zeit hat)
     */
    public void updateSlots(){
        VektorI size = getInvSize();
        for(int y = 0; y < size.y; y++){
            for(int x = 0; x < size.x; x++){
                this.remove(slots[x][y]);
                slots[x][y] = new ItemImage(this, inv.getStack(new VektorI(x,y)),new VektorI(x,y), iconSize);
                this.add(slots[x][y]);
            }
        }
        revalidate();
        repaint();
    }

    /**
     * gibt die Position eines Item bei einem Mouseclick in der Stackliste zurück
     *  @Param VektorI pos: Position des MouseClicks in Pix
     */
    public VektorI getItemAtPos(VektorI pos){
        VektorI v = pos.toDouble().divide(iconSize).toIntFloor();
        if(inv.inBounds(v))return v;
        else return null;
    }

    public Inv getInv(){
        return inv;
    }
	public VektorI getInvSize(){
        return inv.getSize();
    }

}
