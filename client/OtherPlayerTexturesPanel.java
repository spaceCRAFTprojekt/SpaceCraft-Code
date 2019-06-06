package client;

 

import javax.swing.JComponent;
import java.awt.Graphics;
import util.geom.*;
/**
 * Bewerten Sie diesen Namen auf einer Skale von 1 - 10 - AK:
 * 4+3i -LG
 */
public class OtherPlayerTexturesPanel extends JComponent
{
    public Object[] textures;
    public VektorI screenSize;

    private PlayerC pC;
    public OtherPlayerTexturesPanel(OverlayPanelC opC, PlayerC pC,  VektorI screenSize){
        this.setVisible(true);
        this.setEnabled(false);
        this.setOpaque(false);
        this.setLocation(0,0);
        this.resize(screenSize);
        this.pC = pC;
        opC.add(this);
    }
    public void resize(VektorI screenSize){
        this.setSize(screenSize.x, screenSize.y);
        this.screenSize = screenSize;

    }
    
    public void repaint(Object[] textures){
        if(textures == null)return;
        this.textures = textures;
        repaint();
        //this.textures = null; // unnötiger Speicherplatz ??
    }
    
    public void paint(Graphics g){
        if (textures == null)return;
        VektorD ulc = pC.getUpperLeftCorner();
        for(int i = 0; i<textures.length; i++){
            OtherPlayerTexture opt = (OtherPlayerTexture)(textures[i]);
            int blockWidth = pC.getBlockWidth();
            VektorI pixPos = opt.pos.subtract(ulc).add(new VektorD(0,-2)).multiply(blockWidth).toInt();
            opt.paint(g, pixPos, blockWidth);
        }
    }
}
