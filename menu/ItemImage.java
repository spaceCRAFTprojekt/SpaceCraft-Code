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
 * Zeichnet ein Item mit Rahmen für das Inventory
 */
public class ItemImage extends JComponent
{
    private BufferedImage img;
    private int size;
    private transient MenuInv mi;
    private transient VektorI pos;
    private int count;

    /**
     * Constructor for objects of class MenuLabel
     *  @param:
     *  img: das Bild (Skalierung egal)
     *  int size: die größe eines Inventar Slots in Pixeln
     */
    public ItemImage(MenuInv mi, Stack stack, VektorI pos, int size)
    {
        super();
        this.size = size;
        this.mi = mi;
        this.pos = pos;
        this.setSize(size, size);

        int halfBorder = MenuInv.border/2;
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(halfBorder,halfBorder,halfBorder,halfBorder),
                BorderFactory.createLoweredBevelBorder()));
        setEnabled(false); // damit die Mouse Events beim MenuInv bleiben
        setVisible(true);
        this.setBackground(Color.GRAY);
        this.setOpaque(true);
        count = 0;
        update(stack);
        repaint();
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
        g.setColor(Color.GRAY);
        int halfBorder = MenuInv.border/2 + 2;
        int width = size-MenuInv.border-4;
        g.fillRect(halfBorder,halfBorder,  width,width); 
        
        try{g.drawImage(img, halfBorder,halfBorder,  width,width, null);}catch(Exception e){}
        if(count > 0){
            g.setColor(Color.WHITE);
            g.setFont(new Font("sansserif",0,size/3));
            g.drawString(count+"", 3, size - 5);
        }
    }

    public VektorI getPos(){
        return pos;
    }

    public MenuInv getMenuInv(){
        return mi;
    }

    public Stack getStack(){
        return mi.getInv().getStack(pos);
    }

    public boolean equals(ItemImage ii){
        return (this.mi.equals(ii.getMenuInv()) && this.pos.equals(ii.getPos()));
    }

}
