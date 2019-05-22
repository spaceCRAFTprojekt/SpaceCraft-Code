package client;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.JPanel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Component;
import java.awt.Image;
import java.awt.Dimension;
import util.geom.*;
import java.util.ArrayList;
import util.ImageTools;
import java.awt.Graphics;
/**
 * Grafikdarstellung für einen Spieler
 */
public class Frame extends JFrame{
    private Player p;
    private PaintPanel pp;  // auf dieses Panel wird gepaintet
    private OverlayPanelC opC;  
    private OverlayPanelS opS; 
    private OverlayPanelA opA; 
    /**
     * Neuer Frame eines Spielers
     */
    public Frame(String name, VektorI screenSize, Player p)
    {
        //hier am besten keine weiteren Setup-Maßnahmen, es sei denn, sie sind absolut immer gleich
        super("SpaceCraft: "+name);
        this.p = p;
        setLayout(null);
        setSize(screenSize.x,screenSize.y);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void WindowClosing(WindowEvent e) {
                    p.logout();
                }
            });
        Image logo = ImageTools.get('a',"logo_s");
        if (logo!=null)setIconImage(logo);
        this.setFocusTraversalKeysEnabled(false);
        
        pp = new PaintPanel(this, p, screenSize);

        opC = new OverlayPanelC(this,p,screenSize);
        opS = new OverlayPanelS(this,p,screenSize);
        opA = new OverlayPanelA(this,p,screenSize);
    }

    /**
     * Nur zum testen
     */
    public Frame()
    {
        this("Singleplayer",new VektorI(960,640), null);
    }
    
    public VektorI getScreenSize(){
        return new VektorI(this.getSize());
    }

    public Dimension getScreenSizeD(){
        return this.getSize();
    }
    
    public PaintPanel getPaintPanel(){
        return pp;
    }

    public OverlayPanelS getOverlayPanelS(){
        return opS;
    }

    public OverlayPanelC getOverlayPanelC(){
        return opC;
    }
    
    public OverlayPanelA getOverlayPanelA(){
        return opA;
    }

        // Grafik:
    public void paint(Graphics g){
        if(pp!= null){
            pp.repaint();
            //opC.repaint();
            g.setColor(java.awt.Color.BLACK);
            g.drawLine((int) (getSize().getWidth()/2),0,(int) (getSize().getWidth()/2),(int) (getSize().getHeight()));
            g.drawLine(0,(int) (getSize().getHeight()/2),(int) (getSize().getWidth()),(int) (getSize().getHeight()/2));
        }
        //if (p!= null && g!= null)
        //    p.paint(g, getScreenSize());
    }
}
