package server;
import geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.Serializable;
import java.io.ObjectStreamException;
public class Blocks_Piston extends DirBlock
{
    public Blocks_Piston(int id){
        super(id, "Piston", "blocks_piston_off");
    }

    @Override
    public void onRightclick(Sandbox sb, VektorI pos, int playerID){
        switchOn(sb, pos);
    }
    
    public void switchOn(Sandbox sb, VektorI pos){
        ArrayList<VektorI> connectedBlocks = getConnectedBlocks(sb, pos);
        if (connectedBlocks == null)return;
        VektorI dir = new VektorI(getDir(sb, pos)).multiply(-1);
        for(int i = connectedBlocks.size()-1; i >= 0; i--){
            VektorI pos2 = connectedBlocks.get(i);
            VektorI pos3 = pos2.add(dir);
            if(sb.getBlock(pos3) == null)sb.setBlock(sb.getBlock(pos2), pos3);
            sb.breakBlock(pos2);
        }
        VektorI pos2 = getPos2(sb, pos);
        sb.swapBlock(pistonOn, pos);
        sb.swapBlock(pistonFront, pos2);
    }
    
    public static VektorI getPos2(Sandbox sb, VektorI pos){
        return pos.add(new VektorI(getDir(sb, pos)).multiply(-1));
    }
    
    public static ArrayList<VektorI> getConnectedBlocks(Sandbox sb, VektorI pos){
        try{
            Meta meta = sb.getMeta(pos);
            VektorI dir = new VektorI((Integer)(meta.get("dir"))).multiply(-1);
            VektorI posNew = pos.add(dir);
            if(sb.getBlock(posNew) == null)return new ArrayList<VektorI>();
            ArrayList<VektorI> blocks = getConnectedBlocks(sb, posNew, dir, Settings.CRAFT_PISTON_PUSH_LIMIT+1);
            if(blocks.size() > Settings.CRAFT_PISTON_PUSH_LIMIT)return null;
            else return blocks;
        }catch(Exception e){return null;}
    }
    
    public static ArrayList<VektorI> getConnectedBlocks(Sandbox sb, VektorI posOld, VektorI dir, int max){
        ArrayList<VektorI> blocks = new ArrayList<VektorI>();
        blocks.add(posOld);
        VektorI posNew = posOld.add(dir);
        if(sb.getBlock(posNew) == null)return blocks;
        blocks.addAll(getConnectedBlocks(sb, posNew, dir, max-1));
        return blocks;
    }
    
    DirBlock pistonOn = new DirBlock(301, "piston_on", "blocks_piston_on"){
            @Override
            public boolean onBreak(Sandbox sb, VektorI pos, int playerID){
                VektorI pos2 = getPos2(sb, pos);
                if(sb.getBlock(pos2).getName() == "piston_front")sb.breakBlock(pos2);
                return true;
            }

            @Override
            public void onRightclick(Sandbox sb, VektorI pos, int playerID){
                VektorI pos2 = getPos2(sb, pos);
                if(sb.getBlock(pos2).getName() == "piston_front")sb.breakBlock(pos2);
                sb.swapBlock(Blocks.get(300), pos);  // Piston_off wieder setzten
            }
            
            @Override
            public boolean onPlace(Sandbox sb, VektorI pos, int playerID){return false;}
        };
        
    DirBlock pistonFront = new DirBlock(302, "piston_front", "blocks_piston_front"){
            @Override
            public boolean onBreak(Sandbox sb, VektorI pos, int playerID){
                return false;
            }

            @Override
            public boolean onPlace(Sandbox sb, VektorI pos, int playerID){return false;}
        };
}
