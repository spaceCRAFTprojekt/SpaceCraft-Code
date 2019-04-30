import java.awt.Graphics;
import java.awt.Color;
import geom.*;
import java.io.Serializable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
/**
 * ein Spieler in der Space Ansicht
 */
public class PlayerS implements Serializable
{
    private Player player;
    private VektorL pos;
    private double scale=1; //eine Einheit im Space => scale Pixel auf dem Frame
    private Mass focussedMass;
    
    public PlayerS(Player player, VektorL pos)
    {
        this.player = player;
        this.pos=pos;
    }
    
    /**
     * Tastatur event
     * @param:
     *  char type: 'p': pressed
     *             'r': released
     *             't': typed (nur Unicode Buchstaben)
     */
    public void keyEvent(KeyEvent e, char type) {
        if (type == 'p'){
            //System.out.println("KeyEvent in PlayerC: "+e.getKeyChar()+type);
            switch(Character.toLowerCase(e.getKeyChar())){
                case 'w': pos.y=pos.y - (Settings.SPACE_MOVE_PER_CLICK/(long)scale); // up
                    break;
                case 's': pos.y=pos.y + (Settings.SPACE_MOVE_PER_CLICK/(long)scale); // down
                    break;
                case 'a': pos.x=pos.x - (Settings.SPACE_MOVE_PER_CLICK/(long)scale); // left
                    break;
                case 'd': pos.x=pos.x + (Settings.SPACE_MOVE_PER_CLICK/(long)scale); // right
                    break;
            }
            System.out.println(pos.toString());
        }
    }

    /**
     * Maus Event
     * @param:
     *  char type: 'p': pressed
     *             'r': released
     *             'c': clicked
     * entered und exited wurde nicht implementiert, weil es dafür bisher keine Verwendung gab
     */
    public void mouseEvent(MouseEvent e, char type) {

    }   
    
    public void mouseWheelMoved(MouseWheelEvent e){
        int amountOfClicks = e.getWheelRotation();
        scale = scale * Math.pow(2,amountOfClicks);
    }
    
    /**
     * Grafik ausgeben
     */
    public void paint(Graphics g, VektorI screenSize){
        g.setColor(Color.BLACK);
        g.fillRect(0,0,screenSize.x,screenSize.y); // nice
        Space sp=player.getSpace();
        for (int i=0;i<sp.masses.size();i++){
            if (sp.masses.get(i)!=null){
                for (int j=1;j<sp.masses.get(i).o.pos.size();j++){  // ernsthaft?
                    VektorL posDiff1=sp.masses.get(i).o.pos.get(j-1).subtract(pos);
                    posDiff1=posDiff1.multiply(scale).toLong();
                    VektorL posDiff2=sp.masses.get(i).o.pos.get(j).subtract(pos);
                    posDiff2=posDiff2.multiply(scale).toLong();
                    g.setColor(Color.WHITE);
                    g.drawLine((int) (screenSize.x/2+posDiff1.x),(int) (screenSize.y/2-posDiff1.y),(int) (screenSize.x/2+posDiff2.x),(int) (screenSize.y/2-posDiff2.y));
                }
                VektorL posDiff=sp.masses.get(i).getPos().subtract(pos);
                posDiff=posDiff.multiply(scale).toLong();
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