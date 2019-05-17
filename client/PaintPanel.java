package client;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import java.awt.Graphics;

import util.geom.*;

/**
 * Auf dieses Panel ist für die Grafikausgabe aus Space und Craft und ist "ganz unten" (sozusagen der Hintergrund, auf dem OverlayPanels sind)
 */
public class PaintPanel extends JPanel{
    private Frame frame;
    private Player p;
        public PaintPanel(Frame frame, Player p, VektorI screenSize){
        super(null);  // kein Layoutmanager, da nur paint
        setBounds(0,0,screenSize.x, screenSize.y);  // soll den ganzen Frame bedecken
        this.setVisible(true);
        this.setEnabled(true);
        this.p = p;
        this.frame = frame;
        frame.getLayeredPane().add(this, new Integer(0));  // Grafik soll ganz unten sein
    }


    public void paint(Graphics g){
        if (p!= null && g!= null)
            p.paint(g, new VektorI(this.getSize()));
    }
}