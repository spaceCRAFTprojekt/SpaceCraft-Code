import geom.*;
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
        new Menu(p, "Note-Block", new VektorI(300,340)){
            MenuTextArea mta;
            MenuButton mb;
            public void initComponents(){
                new MenuLabel(this, "Notes:", new VektorI(10,10), new VektorI(100,30), -1);
                mta = new MenuTextArea(this, text, new VektorI(10,40), new VektorI(260, 200));
                mb = new MenuButton(this, "Save", new VektorI(170,260), new VektorI(100, 30)){
                    public void onClick(){
                        Meta meta = sb.getMeta(pos);
                        meta.put("text", mta.getText());
                        closeMenu();
                    }
                };
            }
        };
    }
}
