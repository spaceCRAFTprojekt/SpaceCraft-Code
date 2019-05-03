import geom.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.io.ObjectStreamException;
public class Blocks_Piston extends DirBlock
{
    public Blocks_Piston(){
        super("Piston", "blocks_piston_off");
    }

    @Override
    public void onRightclick(Sandbox sb, VektorI pos, Player p){
        VektorI pos2 = getPos2(sb, pos);
        if(sb.getBlock(pos2) == null)sb.setBlock(pistonFront, pos2);
        sb.swapBlock(pistonOn, pos);
    }

    public static VektorI getPos2(Sandbox sb, VektorI pos){
        return pos.add(new VektorI(getDir(sb, pos)).multiply(-1));
    }

    DirBlock pistonOn = new DirBlock("piston_on", "blocks_piston_on"){
            @Override
            public boolean onBreak(Sandbox sb, VektorI pos, Player p){
                VektorI pos2 = getPos2(sb, pos);
                if(sb.getBlock(pos2).getName() == "piston_front")sb.breakBlock(pos2);
                return true;
            }

            @Override
            public void onRightclick(Sandbox sb, VektorI pos, Player p){
                VektorI pos2 = getPos2(sb, pos);
                if(sb.getBlock(pos2).getName() == "piston_front")sb.breakBlock(pos2);
                sb.swapBlock(Blocks.get(3), pos);
            }
            
            @Override
            public boolean onPlace(Sandbox sb, VektorI pos, Player p){return false;}
        };
        
    DirBlock pistonFront = new DirBlock("piston_front", "blocks_piston_front"){
            @Override
            public boolean onBreak(Sandbox sb, VektorI pos, Player p){
                return false;
            }

            @Override
            public boolean onPlace(Sandbox sb, VektorI pos, Player p){return false;}
        };
}
