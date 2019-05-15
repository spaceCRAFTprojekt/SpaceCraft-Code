package client;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import java.awt.Graphics;

import util.geom.*;

/**
 * Auf diesem Panel sind alle JComponents von Space oder Craft (es wird jeweils ein Objekt für Space und Craft erstellt)
 */
public abstract class OverlayPanel extends JPanel
{
    private Frame frame;
    private Player p;
    public OverlayPanel(Frame frame, Player p, VektorI screenSize){
        super(null);  // kein Layoutmanager, da nur paint
        setBounds(0,0,screenSize.x, screenSize.y);  // soll den ganzen Frame bedecken
        this.setVisible(false);
        this.setEnabled(false);
        this.setOpaque(false);
        this.p = p;
        frame.getLayeredPane().add(this, new Integer(1)); //=> Über dem PaintPanel
    }
}
