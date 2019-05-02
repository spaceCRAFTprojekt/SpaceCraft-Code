import geom.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.io.ObjectStreamException;
public class Blocks_PistonFront extends Block
{
    public Blocks_PistonFront(){
        super("Piston Front", "blocks_pistonFront");
        this.dir = dir;
    }
    
    @Override
    public boolean onPlace(Player p, VektorI pos){
        return false;
    }
    
    public boolean onConstruct(dir i){
        this.dir = dir;
    }
    
    @Override
    public boolean onBreak(Player p){
        return false;  // kann nicht abgebaut werden!!
    }
}
