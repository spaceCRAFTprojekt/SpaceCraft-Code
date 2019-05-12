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
    private transient JLabel label;

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
        if(stack  == null || stack.getCount() <= 0){
            
        }else{
            this.img = stack.getInventoryImage();

            label = new JLabel(""+stack.getCount());
            label.setBounds(3, (2*size)/3 - 3, size,size/3);
            label.setFont(new Font("sansserif",0,size/3));
            label.setForeground(Color.WHITE);
            label.setVisible(true); 
            label.setOpaque(false);
            this.add(label);
            
            repaint();
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g){
        g.setColor(Color.GRAY);
        int halfBorder = MenuInv.border/2 + 2;
        int width = size-MenuInv.border-4;
        g.fillRect(halfBorder,halfBorder,  width,width); 
        try{g.drawImage(img, halfBorder,halfBorder,  width,width, null);}catch(Exception e){}
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

