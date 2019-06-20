package blocks;
import server.Sandbox;
import util.geom.VektorI;
/**
 * An Positionen, an denen ein solcher Block liegt, muss ein Metadatum "full" vorhanden sein.
 * Diese ist ein Prozentsatz, wie viel Treibstoff dieser Tank noch enthält.
 * Diese Variable ist für Space während dem Flug nicht relevant (die Masse des Schiffes wird 
 * einmal berechnet und dann wird mit dieser gearbeitet). Sie wird von Space gesetzt 
 * und ist für Craft (Abbauen etc.) relevant.
 * (noch nicht implementiert)
 */
public class Blocks_Rocket_Fuel extends SBlock{
    public Blocks_Rocket_Fuel(int id){
        super(id,"fuel","blocks_rocket_fuel",true);
    }
    @Override
    public void onConstruct(Sandbox sb, int sandboxIndex, VektorI pos){
        Meta meta = new Meta();
        meta.put("full",1);
        sb.setMeta(pos,meta);
    }
}