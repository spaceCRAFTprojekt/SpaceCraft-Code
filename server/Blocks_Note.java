package server;
import geom.*;
import client.*;
import menu.*;
/**
 * Test der Metadaten
 */
public class Blocks_Note extends SBlock
{
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
    public void onRightclick(Sandbox sb, VektorI pos, Player p){
        Meta meta = sb.getMeta(pos);
        String text = (String)meta.get("text");
        int id=p.getID();
        Object[] menuParams={pos,text};
        new Task(id,"Player.showMenu","NoteblockMenu",menuParams);
    }
}
