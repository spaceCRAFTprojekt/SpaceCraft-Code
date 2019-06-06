package client;

 

import util.geom.*;
import java.io.Serializable;
import java.awt.*;
import java.awt.Font;
/**
 * nur für den Transport der Daten zum Client
 * nicht nur die Textur, sondern allgemein die Daten des Spielers!
 */
public class OtherPlayerTexture implements Serializable
{
    public static final long serialVersionUID=0L;
    public int playerID;
    public int mode;
    public int textureID;
    public VektorD pos;
    public String name;
    
    public OtherPlayerTexture(int playerID, int mode, int textureID, VektorD pos, String name)
    {
        this.playerID = playerID;
        this.pos = pos;
        this.textureID = textureID;
        this.mode = mode;
        this.name = name;
    }
    public void paint(Graphics g, VektorI pixPos, int blockWidth){
        Font f = new Font("sansserif", 0, 12);
        g.setFont(f);
        double width = name.length()*6; //f.getStringBounds(name, ((Graphics2D)g).getFontRenderContext()).getX();
        g.drawString(name, (int)(pixPos.x+(blockWidth/2)-(width/2)), pixPos.y-(blockWidth/3));
        PlayerTexture.paint(g, textureID, mode, blockWidth, pixPos);
    }
}
