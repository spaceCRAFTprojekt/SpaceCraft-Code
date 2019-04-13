import java.awt.Graphics;
import geom.*;
import java.io.Serializable;
/**
 * ein Spieler in der Space Ansicht
 */
public class PlayerS implements Serializable
{
    private Player player;

    public PlayerS(Player player)
    {
        this.player = player;
    }

    
    /**
     * Grafik ausgeben
     */
    public void paint(Graphics g, VektorI screenSize){
        
    }
}