import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ColorModel;
import java.awt.Color;
import geom.*;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.io.ObjectOutputStream;
import java.io.IOException;
/**
 * Eine virtuelle Umgebung aus Blöcken
 * 
 * @Content:
 *  1. Methoden zum Erstellen der Sandbox
 *  2. Methoden für Blöcke (setBlock(),...)
 *  3. Methoden für Subsandboxes und Raketenstart
 *  4. Methoden für Ansicht und Grafikausgabe
 */
public abstract class Sandbox implements Serializable
{
    public transient Block[][]map;
    public transient Meta[][]meta;
    // Sandboxen können Sandboxen enthalten (Kompositum). z.B.: Schiff auf Planet
    protected transient ArrayList<Sandbox> subsandboxes = new ArrayList<Sandbox>(); //Namensänderung, war früher "sandboxes"
    protected transient Timer spaceTimer; //nur eine Referenz

    /***********************************************************************************************************************************************************
    /*********1. Methoden zum Erstellen der Sandbox*************************************************************************************************************
    /***********************************************************************************************************************************************************

    /**
     * erstellt eine neue Sandbox
     * @param: Vektor size: gibt die größe der Sandbox an (Bereich in dem Blöcke sein können)
     */
    public Sandbox(VektorI size, Timer spaceTimer){
        map = new Block[size.x][size.y];
        this.spaceTimer=spaceTimer;
        this.spaceTimerSetup();
    }

    public Sandbox(Block[][] map, ArrayList<Sandbox> subsandboxes, Timer spaceTimer){
        this.map=map;
        this.subsandboxes=subsandboxes;
        this.spaceTimer=spaceTimer;
        this.spaceTimerSetup();
    }

    public void setSpaceTimer(Timer t){
        this.spaceTimer=t;
        this.spaceTimerSetup();
    }

    protected abstract void spaceTimerSetup();
    //Nur hier können neue TimerTasks hinzugefügt werden.

    /**
     * gibt die Größe der Sandbox zurück
     */
    public VektorI getSize(){
        return new VektorI(map.length, map[0].length);
    }

    /**
     * Ersetzt die Map mit einer anderen
     */
    public void setMap(Block[][]map){
        if(map!= null)this.map = map;
    }

    /**
     * Fügt eine Sandbox hinzu
     */
    public void addSandbox(Sandbox sbNeu){
        if(sbNeu!=null)subsandboxes.add(sbNeu);
    }

    /**
     * Löscht eine Sandbox
     */
    public void removeSandbox(Sandbox sbR){
        if(sbR!=null)subsandboxes.remove(sbR);
    }

    public ArrayList<Sandbox> getSubsandboxes(){
        return subsandboxes;
    }

    /***********************************************************************************************************************************************************
    /*********2. Methoden für Blöcke (setBlock(),...)***********************************************************************************************************
    /***********************************************************************************************************************************************************

    /**
     * Rechtsklick auf einen Block in der Welt:
     *  wenn an der Stelle kein Block => plaziert Block
     *  wenn an der Stelle ein Block => führt (wenn möglich) das onRightclick Event im Block aus
     *  
     *  @param:
     *  * VektorI pos: Position des Blocks
     *  * Player p: Spieler der rechtsklickt
     */
    public void rightclickBlock(VektorI pos, Player p){
        try{
            if (map[pos.x][pos.y] == null){
                placeBlock(Blocks.get(3), pos, p);
            }else{
                ((SBlock)map[pos.x][pos.y]).onRightclick(this, pos, p);
                System.out.println("Block at "+pos.toString()+" rightclicked by "+p.getName()+"!");
            }
        }catch(Exception e){ //block außerhalb der Map oder kein Special Block => kein rightclick möglich
        }
    }

    /**
     * Linksklick auf einen Block in der Welt
     *  wenn an der Stelle ein Block => baut den Block ab
     *  
     *  @param:
     *  * VektorI pos: Position des Blocks
     *  * Player p: Spieler der linksklickt
     */
    public void leftclickBlock(VektorI pos, Player p){
        try{
            if (map[pos.x][pos.y] == null){
                return;  // evtl. an Player weitergeben
            }else{
                breakBlock(pos);
                System.out.println("Block at "+pos.toString()+" leftclicked by "+p.getName()+"!");
            }
        }catch(Exception e){ //block außerhalb der Map 
        }
    }

    /**
     * Spieler platziert einen Block, aber nur wenn das onPlace Event true zurückgibt
     * 
     * @param:
     *  * Block block: Block der plaziert werden soll
     *  * VektorI pos: Position des Blocks
     *  * Player p: Spieler der den Block plaziert
     */
    public void placeBlock(Block block, VektorI pos, Player p){
        try{
            if(!((SBlock)block).onPlace(this, pos, p))return;  // ruft onPlace auf, wenn es ein Special Block ist. Wenn es nicht erfolgreich plaziert wurde => Abbruch
        }catch(Exception e){} // => kein SpecialBlock => kann immer plaziert werden
        setBlock(block, pos);
        System.out.println("Block at "+pos.toString()+" placed by "+p.getName()+"!");
    }

    /**
     * Ein Block wird ausnahmelos gesetzt. Die Metadaten werden aber überschrieben und das onConstruct Event aufgerufen
     * 
     * @param:
     *  * Block block: Block der gesetzt werden soll
     *  * VektorI pos: Position des Blocks
     */
    public void setBlock(Block block, VektorI pos){
        swapBlock(block, pos);
        removeMeta(pos);
        try{
            ((SBlock)block).onConstruct(this, pos);  // ruft onConstruct auf, wenn es ein Special Block ist. 
        }catch(Exception e){} // => kein SpecialBlock
    }

    /**
     * Ein Block wird ausnahmelos gesetzt. 
     * !!! Die Metadaten bleiben aber erhalten !!!
     * das onConstruct Event wird NICHT aubgerufen  
     * 
     * @param:
     *  * Block block: Block der gesetzt werden soll
     *  * VektorI pos: Position des Blocks
     */
    public void swapBlock(Block block, VektorI pos){
        map[pos.x][pos.y]= block; 
    }

    /**
     * Spieler baut einen Block in die Welt ab, wenn das onBreak() Event true zurückgibt und löscht die Metadaten
     * @param:
     *  * VektorI pos: Position des Blocks
     *  * Player p: Spieler der den Block abbaut
     */
    public void breakBlock(VektorI pos, Player p){
        if (map[pos.x][pos.y] == null) return;
        try{
            if (((SBlock)map[pos.x][pos.y]).onBreak(this, pos, p)){
                breakBlock(pos);
                System.out.println("Block at "+pos.toString()+" breaked by "+p.getName()+"!");
            }
        }catch(Exception e){}
    }

    /**
     * Entfernt einen Block ausnahmelos in der Welt. Entfernt die Metadaten und ruft das onDestruct() Event auf.
     * 
     * @param:
     *  * VektorI pos: Position des Blocks
     */
    public void breakBlock(VektorI pos){
        map[pos.x][pos.y] = null;
        try{
            ((SBlock)map[pos.x][pos.y]).onDestruct(this, pos);
        }catch(Exception e){}
        removeMeta(pos);
    }

    /**
     * Gibt das Block-Object zurück
     */
    public Block getBlock(VektorI pos){
        try{
            return map[pos.x][pos.y];
        }catch(Exception e){ return null; }  // Außerhalb des Map-Arrays
    }

    /**
     * gibt das Metadaten Object zurück
     */
    public Meta getMeta(VektorI pos){
        try{
            return this.meta[pos.x][pos.y];
        }catch(Exception e){ return null; }  // Außerhalb des Map-Arrays
    }

    /**
     * setzt das Metadaten Object
     */
    public void setMeta(VektorI pos, Meta meta){
        try{
            this.meta[pos.x][pos.y] = meta;
        }catch(Exception e){ return; }  // Außerhalb des Map-Arrays
    }

    /**
     * entfernt das Metadaten Object
     */
    public void removeMeta(VektorI pos){
        try{
            this.meta[pos.x][pos.y] = null;
        }catch(Exception e){ return; }  // Außerhalb des Map-Arrays
    }

    /***********************************************************************************************************************************************************
    /*********3. Methoden für Subsandboxes und Raketenstart*****************************************************************************************************
    /***********************************************************************************************************************************************************

    /***********************************************************************************************************************************************************
    /*********4. Methoden für Ansicht und Grafikausgabe*********************************************************************************************************
    /***********************************************************************************************************************************************************

    /**
     * Gibt die obere rechte Ecke (int Blöcken) der Spieleransicht an
     * @param: pos: Position des Spielers relativ zur oberen rechten Ecke der Sandbox
     * 
     * @Benny:
     * Das hat Linus programmiert. Die Bilder aller Blöcke werden zuerst zusammengeführt in ein großes Bild und dann nur dieses Bild "gezeichnet". 
     * Das ist deutlich schneller als jedes Bild einzeln zu zeichen. Bitte setz dich mit Linus (König der Kommentare) in Verbindung um das zu verstehen
     * und zu verbessern. Man kann z.B. zur Zeit nur ganze Koordianten darstellen...
     */
    public VektorD getUpperLeftCorner(VektorD pos){
        return pos.add( Settings.PLAYERC_FIELD_OF_VIEW.toDouble().multiply(-0.5) ).add(new VektorD(0.5,0.5));
    }

    /**
     * Gibt die Position eines Blocks an
     * 
     * @param: 
     * bPos: Position des Blocks relativ zur oberen rechten Ecke der Spieleransicht in Pixeln
     * pPos: Position des Spielers relativ zur oberen rechten Ecke der Sandbox in Blöcken
     * blockBreite: Breite eines Blocks in Pixeln
     */
    public VektorI getPosToPlayer(VektorI bPos, VektorD pPos, int blockBreite){
        //System.out.println(bPos.toString()+" "+bPos.toDouble().divide(blockBreite).toString());
        return (getUpperLeftCorner(pPos).add(bPos.toDouble().divide(blockBreite))).toIntFloor();
    }

    /**
     * Grafik ausgeben
     * @param: 
     * pos: Position des Spielers relativ zur oberen rechten Ecke der Sandbox
     * blockBreite: Breite eines Blocks in Pixeln
     */
    public void paint(Graphics g, VektorI screenSize, VektorD pos, int blockBreite){
        VektorI upperLeftCorner = getUpperLeftCorner(pos).toInt();  // obere linke Ecke der Spieleransicht relativ zur oberen linken Ecke der sb
        VektorI bottomRightCorner = upperLeftCorner.add(Settings.PLAYERC_FIELD_OF_VIEW);  // untere rechte Ecke der Spieleransicht relativ zur oberen linken Ecke der sb
        //System.out.println("UpperLeftCorner: "+ upperLeftCorner.toString()+ " BottomRightCorner: " + bottomRightCorner.toString());

        ColorModel cm=ColorModel.getRGBdefault();
        BufferedImage image=new BufferedImage(cm,cm.createCompatibleWritableRaster(Settings.PLAYERC_FIELD_OF_VIEW.x*blockBreite,Settings.PLAYERC_FIELD_OF_VIEW.y*blockBreite),false,new Hashtable<String,Object>());
        //alle hier erstellten BufferedImages haben den TYPE_INT_ARGB
        int[] oldImageData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        Hashtable<String,BufferedImage> blockImages=new Hashtable<String,BufferedImage>(); //Skalierung
        for (int x = upperLeftCorner.x; x<=bottomRightCorner.x; x++){
            for (int y = upperLeftCorner.y; y<=bottomRightCorner.y; y++){
                Block block=map[x][y];
                if (block!=null && blockImages.get(block.getName())==null){
                    BufferedImage img=block.getImage();
                    Hashtable<String,Object> properties=new Hashtable<String,Object>();
                    String[] prns=image.getPropertyNames();
                    if (prns!=null){
                        for (int i=0;i<prns.length;i++){
                            properties.put(prns[i],image.getProperty(prns[i]));
                        }
                    }
                    BufferedImage img2=new BufferedImage(cm,cm.createCompatibleWritableRaster(blockBreite,blockBreite),false,properties);
                    Graphics gr=img2.getGraphics();
                    gr.drawImage(img,0,0,blockBreite,blockBreite,null);
                    blockImages.put(block.getName(),img2);
                }
            }
        }

        for (int x = upperLeftCorner.x; x<bottomRightCorner.x; x++){
            for (int y = upperLeftCorner.y; y<bottomRightCorner.y; y++){
                Block block = map[x][y];
                if(block != null){
                    BufferedImage img=blockImages.get(block.getName());
                    int[] imgData=((DataBufferInt) img.getRaster().getDataBuffer()).getData();
                    for (int i=0;i<blockBreite;i++){
                        int index = ((y-upperLeftCorner.y)*blockBreite + i)*Settings.PLAYERC_FIELD_OF_VIEW.x*blockBreite + (x-upperLeftCorner.x)*blockBreite;
                        System.arraycopy(imgData,i*blockBreite,oldImageData,Math.min(index,oldImageData.length-blockBreite-1),blockBreite);
                    }
                }
            }
        }
        g.setColor(new Color(0,0,0,1));
        g.drawImage(image,0,0,new Color(0,0,0,255),null);
    }
}
