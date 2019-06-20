package blocks;
import util.geom.VektorI;
import server.Sandbox;
/**
 * Das Platzieren dieses Blocks erzeugt die Schiffs-Subsandbox.
 * Dieser Block benötigt ein Metadatum "ownerID"
 * und ein Metadatum "shipIndex". shipIndex ist der Index des Schiffs, das durch diesen Controller
 * kontrolliert wird, in der space.masses-Liste. Gibt es noch kein solches Schiff (muss erst durch
 * einen Button erstellt werden), ist es -1.
 */
public class Blocks_Rocket_Controller extends SBlock{
    public Blocks_Rocket_Controller(int id){
        super(id,"rocketController","blocks_rocket_controller",true);
        placement_prediction=false;
    }
    
    @Override
    public boolean onPlace(Sandbox sb, int sandboxIndex, VektorI pos, int playerID){
        sb.swapBlock(this,pos);
        sb.setMeta(pos,"ownerID",playerID);
        sb.setMeta(pos,"shipIndex",-1);
        System.out.println("Block at "+pos+" placed by Player "+playerID+"!");
        return false;
    }
    
    @Override
    public void onRightclick(Sandbox sb, int sandboxIndex, VektorI pos, int playerID){
        int ownerID=(int) sb.getMeta(pos,"ownerID");
        int shipIndex=(int) sb.getMeta(pos,"shipIndex");
        Object[] menuParams={sandboxIndex,pos,shipIndex};
        if (ownerID==playerID)
            sb.getMain().newTask(playerID,"Player.showMenu","RocketControllerMenu",menuParams);
    }
}