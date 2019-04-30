import java.awt.Graphics;
import java.awt.Color;
import geom.*;
import java.io.Serializable;
/**
 * ein Spieler in der Space Ansicht
 */
public class PlayerS implements Serializable
{
    private Player player;
    private VektorD pos;
    private double scale=0.05; //eine Einheit im Space => scale Pixel auf dem Frame

    public PlayerS(Player player, VektorD pos)
    {
        this.player = player;
        this.pos=pos;
    }

    /**
     * Grafik ausgeben
     */
    public void paint(Graphics g, VektorI screenSize){
        g.setColor(Color.BLACK);
        g.fillRect(0,0,screenSize.x,screenSize.y);
        Space sp=player.getSpace();
        for (int i=0;i<sp.masses.size();i++){
            if (sp.masses.get(i)!=null){
                for (int j=1;j<sp.masses.get(i).o.pos.size();j++){
                    VektorL posDiff1=sp.masses.get(i).o.pos.get(j-1).subtract(pos).toLong();
                    posDiff1=posDiff1.multiply(scale).toLong();
                    VektorL posDiff2=sp.masses.get(i).o.pos.get(j).subtract(pos).toLong();
                    posDiff2=posDiff2.multiply(scale).toLong();
                    g.setColor(Color.WHITE);
                    g.drawLine((int) (screenSize.x/2+posDiff1.x),(int) (screenSize.y/2-posDiff1.y),(int) (screenSize.x/2+posDiff2.x),(int) (screenSize.y/2-posDiff2.y));
                }
                VektorD posDiff=sp.masses.get(i).getPos().subtract(pos);
                posDiff=posDiff.multiply(scale);
                int r=2;
                if (sp.masses.get(i) instanceof PlanetS){
                    r=((PlanetS) sp.masses.get(i)).getRadius();
                }
                r=(int)(r*scale);
                g.setColor(Color.WHITE);
                g.fillArc((int) (screenSize.x/2+posDiff.x-r),(int) (screenSize.y/2-posDiff.y-r),2*r,2*r,0,360);
            }
        }
    }
}