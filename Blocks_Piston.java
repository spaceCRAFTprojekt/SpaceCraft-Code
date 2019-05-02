import geom.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.io.ObjectStreamException;
public class Blocks_Piston extends SBlock
{
    private transient BufferedImage imgOn;
    private transient BufferedImage imgOff;
    private boolean state;
    private Sandbox sb;
    private VektorI pos;
    public Blocks_Piston(){
        super("Piston", "blocks_piston_off");
        this.imgOff = getImage();
        //this.dir = dir;
        //this.sb = sb;
        //this.pos = pos;
        this.imgOn = ImageTools.get('C', "blocks_piston_on");
        state = false;
    }
    
    
    public void onRightclick(){
        /*
        state = !state;
        VektorI pos2 = pos.add(new VektorI(dir).multiply(-1));
        if(state == true){
            this.setImage(imgOn);
            sb.setBlock(pos2, new Blocks_PistonFront(dir));
        }else{
            this.setImage(imgOff);
            if(sb.getBlock(pos2).getName() == "Piston Front"){
                sb.setBlock(pos2, null);
            }
        }
        */
    }
    
    //@Override
    public boolean onBreak(Player p){
      /*  if(state == false) return true;
        VektorI pos2 = pos.add(new VektorI(dir).multiply(-1));
        if(sb.getBlock(pos2).getName() == "Piston Front"){
            sb.setBlock(pos2, null);
        }*/
        return true;
    }
    
}
