package server;
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
import client.*;
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
    public Meta[][]meta;
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
        meta = new Meta[size.x][size.y];
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
     * Request-Funktion
     */
    public void rightclickBlock(Player p, Boolean success, boolean onPlanet, int sandboxIndex, VektorI pos){
        try{
            if (map[pos.x][pos.y] == null){
                placeBlock(Blocks.get(104), pos, p);
            }else{
                ((SBlock)map[pos.x][pos.y]).onRightclick(this, pos, p);
                System.out.println("Block at "+pos.toString()+" rightclicked by "+p.getName()+"!");
            }
        }catch(Exception e){ //block außerhalb der Map oder kein Special Block => kein rightclick möglich
        }
        success=new Boolean(true); //muss sich immer verändern, sonst wartet der Request ewig
    }

    /**
     * Linksklick auf einen Block in der Welt
     *  wenn an der Stelle ein Block => baut den Block ab
     *  
     *  @param:
     *  * VektorI pos: Position des Blocks
     *  * Player p: Spieler der linksklickt
     * Request-Funktion
     */
    public void leftclickBlock(Player p, Boolean success, boolean onPlanet, int sandboxIndex,  VektorI pos){
        try{
            if (map[pos.x][pos.y] == null){
                return;  // evtl. an Player weitergeben
            }else{
                breakBlock(pos, p);
                System.out.println("Block at "+pos.toString()+" leftclicked by "+p.getName()+"!");
            }
        }catch(Exception e){ //block außerhalb der Map 
        }
        success=new Boolean(true);
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
        }catch(Exception e){breakBlock(pos);}
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
        return pos.add(ClientSettings.PLAYERC_FIELD_OF_VIEW.toDouble().multiply(-0.5) ).add(new VektorD(0.5,0.5));
    }
    
    /**
     * Request-Funktion
     */
    public void getMapIDs(Player p, int[][] ret, boolean onPlanet, int sandboxIndex, VektorI upperLeftCorner, VektorI bottomRightCorner){
        ret=new int[upperLeftCorner.x-bottomRightCorner.x][upperLeftCorner.y-bottomRightCorner.y];
        for (int i=upperLeftCorner.x;i<bottomRightCorner.x;i++){
            for (int j=upperLeftCorner.y;j<bottomRightCorner.y;j++){
                ret[i][j]=map[i-upperLeftCorner.x][j-upperLeftCorner.y].getID();
            }
        }
    }
}