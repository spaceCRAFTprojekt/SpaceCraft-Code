package menu;

import util.geom.VektorI;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

import items.*;
/**
 * Zeichnet ein Item ohne Rahmen fÃ¼r das drop and drag [sic] in Inventorys
 * Ich hätte das auch irgendwie mit Vererbung hinbekommen, aber so geht es auch und braucht eine Klasse weniger
 */
public class DraggedItemImage extends JComponent
{
    private BufferedImage img;
    private int count;
    private int size;  // nur ein Wert   // Quadratisch, praktisch, gut. ~ Reifenhersteller

    /**
     * Constructor for objects of class MenuLabel
     *  @param:
     *  img: das Bild (Skalierung egal)
     *  int size: die grÃ¶ÃŸe eines Inventar Slots in Pixeln (größenangabe mit dem Rahmen)
     *  pos: linke obere Ecke !in Pixeln!
     */
    public DraggedItemImage(Stack stack, VektorI pos, JFrame frame)
    {
        super();
        this.size = MenuInv.iconSize - MenuInv.border;  // könnte man in Zukunft variabel machen
        this.setSize(size, size);
        setEnabled(false); // damit die Mouse Events beim MenuInv bleiben
        setVisible(true);
        update(pos);
        this.setOpaque(false);
        count = 0;
        update(stack);
        frame.getLayeredPane().add(this, new Integer(100));
        repaint();
    }

    public void update(VektorI pos){
        this.setLocation(pos.x-size/2, pos.y-size/2);
        this.repaint();
    }
    
    public void update(Stack stack){
        if(stack == null || stack.getCount() <= 0)showNullWithoutRepaint();
        else{
            count = stack.getCount();
            this.img = stack.getInventoryImage();
        }
        repaint();
    }
    
    
    
    /**
     * es wird ein leerer Stack angezeigt. (mit repaint!!!) 
     * public, da es auch in den drop and drag [sic] Methoden verwendet wird
     */
    public void showNull(){
        showNullWithoutRepaint();
        repaint();
    }

    /**
     * es wird ein leerer Stack wird gesetzt, aber nicht gepaintet (-> nur private)
     */
    private void showNullWithoutRepaint(){
        count = 0;
        this.img = null;
    }

    @Override
    public void paintComponent(Graphics g){

        if(count <= 0)return;  
        try{g.drawImage(img, 0,0,  size, size, null);}catch(Exception e){}

        g.setColor(Color.WHITE);
        g.setFont(new Font("sansserif",0,size/3));
        g.drawString(count+"", 3, size - 5);

    }

    public void dispose(JFrame frame){
        frame.getLayeredPane().remove(this);
    }
}
