import geom.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.io.ObjectStreamException;
public class Blocks_PistonFront extends Block implements Serializable
{
    public Blocks_PistonFront(int dir){
        super("Piston Front", "blocks_pistonFront");
        this.dir = dir;
    }
    
    @Override
    public boolean onBreak(Player p){
        return false;  // kann nicht abgebaut werden!!
    }
}
