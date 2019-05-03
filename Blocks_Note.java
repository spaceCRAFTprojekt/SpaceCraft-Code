import geom.*;
/**
 * Test der Metadaten
 */
public class Blocks_Note extends SBlock
{
    public Blocks_Note()
    {
        super("noteblock", "blocks_note");
    }

    @Override
    public void onConstruct(Sandbox sb, VektorI pos){
        Meta meta = new Meta();
        meta.put("text", "Hallo auf einer Metaebene!");
    }
    
    @Override
    public void onRightclick(Sandbox sb, VektorI pos, Player p){
        p.deactivate();
        Meta meta = sb.getMeta(pos);
        String text = (String)meta.get("text");
        new Menu(p, "Note-Block", new VektorI(300,300)){
            MenuLabel l;
            public void initComponents(){
                new MenuLabel(this, text, new VektorI(100,100), new VektorI(200,30), -1);
            }
        };
    }
}
