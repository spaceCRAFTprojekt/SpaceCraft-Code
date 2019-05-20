package blocks;
import util.geom.*;
import menu.*;
import server.*;
import client.Task;
/**
 * Test der Metadaten
 */
public class Blocks_Note extends SBlock
{
    public static final long serialVersionUID=0L;
    public Blocks_Note(int id)
    {
        super(id, "noteblock", "blocks_note");
    }
    
    @Override
    public void onConstruct(Sandbox sb, VektorI pos){
        Meta meta = new Meta();
        meta.put("text", "Hallo auf einer Metaebene!");
        sb.setMeta(pos,meta);
    }
    
    @Override
    public void onRightclick(Sandbox sb, VektorI pos, int playerID){
        Meta meta = sb.getMeta(pos);
        String text = (String)meta.get("text");
        Object[] menuParams={pos,text};
        Main.main.newTask(playerID,"Player.showMenu","NoteblockMenu",menuParams);
    }
}
