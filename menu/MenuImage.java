package menu;

import util.geom.VektorI;
import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
/**
 * Vereinfachung fÃ¼r ein Image
 * Es kann sein, dass es sehr langsam ist, da das Bild mit den built-in Methoden vergrößert/verkleinert wird.
 * Wenn viele Bilder benötigt werden besser Pixel für Pixel zeichnen
 */
public class MenuImage extends JComponent
{
    private Menu m;
    private BufferedImage img;
    private VektorI size;
    /**
     * Constructor for objects of class MenuLabel
     */
    public MenuImage(Menu m, BufferedImage img, VektorI pos,  VektorI size)
    {
        super();
        this.m = m;
        this.img = img;
        this.size = size;
        this.setBounds(pos.x, pos.y, size.x, size.y);
        setEnabled(true);
        setVisible(true);
        m.contentPane.add(this); // und fÃ¼gt es zur Pane hinzu
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g){
        g.drawImage(img, 0,0,size.x, size.y, null);
    }
}

