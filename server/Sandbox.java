package server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import util.geom.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ColorModel;
import java.awt.Color;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.io.ObjectOutputStream;
import java.io.IOException;

import client.ClientSettings;
import client.SandboxInSandbox;
import blocks.*;
/**
 * Eine virtuelle Umgebung aus BlÃ¶cken
 * 
 * @Content:
 *  1. Methoden zum Erstellen der Sandbox
 *  2. Methoden für Blöcke (setBlock(),...)
 *  3. Methoden für Subsandboxes und Raketenstart
 *  4. Methoden für Ansicht und Grafikausgabe
 */
public abstract class Sandbox implements Serializable
{
    public static final long serialVersionUID=0L;
    protected Main main;
    public Block[][]map;
    public Meta[][]meta;
    /**
     * Sandboxen können Sandboxen enthalten, z.B.: Schiff auf Planet
     */
    protected ArrayList<SandboxInSandbox> subsandboxes = new ArrayList<SandboxInSandbox>();
    protected transient Timer spaceTimer; //nur eine Referenz

    /***********************************************************************************************************************************************************
    /*********1. Methoden zum Erstellen der Sandbox*************************************************************************************************************
    /***********************************************************************************************************************************************************/

    /**
     * erstellt eine neue Sandbox
     * @param: Vektor size: gibt die Größe der Sandbox an (Bereich in dem Blöcke sein können)
     */
    public Sandbox(Main main, VektorI size, Timer spaceTimer){
        map = new Block[size.x][size.y];
        meta = new Meta[size.x][size.y];
        this.spaceTimer=spaceTimer;
        this.spaceTimerSetup();
        this.main=main;
    }

    public Sandbox(Main main, Block[][] map, Meta[][] meta, ArrayList<SandboxInSandbox> subsandboxes, Timer spaceTimer){
        this.map=map;
        this.meta=meta;
        this.subsandboxes=subsandboxes;
        this.spaceTimer=spaceTimer;
        this.spaceTimerSetup();
        this.main=main;
    }

    public void setSpaceTimer(Timer t){
        this.spaceTimer=t;
        this.spaceTimerSetup();
    }

    protected abstract void spaceTimerSetup();
    //Nur hier kÃ¶nnen neue TimerTasks hinzugefÃ¼gt werden.

    /**
     * gibt die Größe der Sandbox zurÃ¼ck
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
     * Gibt die Masse der Sandbox zurück
     */
    public abstract Mass getMass();
    
    /***********************************************************************************************************************************************************
    /*********2. Methoden für Blöcke (setBlock(),...)***********************************************************************************************************
    /***********************************************************************************************************************************************************/

    /**
     * Rechtsklick auf einen Block in der Welt:
     *  wenn an der Stelle kein Block => macht nichts!!!
     *  wenn an der Stelle ein Block => fÃ¼hrt (wenn mÃ¶glich) das onRightclick Event im Block aus
     *  
     *  @param:
     *  * VektorI pos: Position des Blocks
     *  * Integer playerID
     * Request-Funktion
     */
    public void rightclickBlock(Integer playerID, Integer sandboxIndex, VektorI pos){
        try{
            if (map[pos.x][pos.y] == null){
                //placeBlock(Blocks.blocks.get(104), pos, playerID);
            }else{
                ((SBlock)map[pos.x][pos.y]).onRightclick(this, main.getSpace().masses.indexOf(getMass()), pos, playerID);
                System.out.println("Block at "+pos.toString()+" rightclicked by Player "+playerID+"!");
            }
        }catch(Exception e){ //block auÃŸerhalb der Map oder kein Special Block => kein rightclick mÃ¶glich
        }
    }

    /**
     * Spieler platziert einen Block, aber nur wenn das onPlace Event true zurÃ¼ckgibt
     * 
     * @param:
     *  * Block block: Block der plaziert werden soll
     *  * VektorI pos: Position des Blocks
     *  * int playerID
     */
    public void placeBlock(Block block, VektorI pos, int playerID){
        try{
            if(!((SBlock)block).onPlace(this, main.getSpace().masses.indexOf(getMass()), pos, playerID))return;  // ruft onPlace auf, wenn es ein Special Block ist. Wenn es nicht erfolgreich plaziert wurde => Abbruch
        }catch(Exception e){} // => kein SpecialBlock => kann immer plaziert werden
        if(!block.placement_prediction)return;
        setBlock(block, pos);
        System.out.println("Block at "+pos.toString()+" placed by Player "+playerID+"!");
    }
    
    /**
     * Und das gleiche für einen Request
     */
    public void placeBlock(Integer playerID, Integer sandboxIndex, VektorI pos, Integer BlockID){
        Block block = Blocks.get(BlockID);
        if(block != null)placeBlock(block, pos, playerID);
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
            ((SBlock)block).onConstruct(this, main.getSpace().masses.indexOf(getMass()), pos);  // ruft onConstruct auf, wenn es ein Special Block ist. 
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
     *  * int playerID
     */
    public void breakBlock(VektorI pos, int playerID){
        if (map[pos.x][pos.y] == null) return;
        try{
            if (((SBlock)map[pos.x][pos.y]).onBreak(this, main.getSpace().masses.indexOf(getMass()), pos, playerID)){
                breakBlock(pos);
                System.out.println("Block at "+pos.toString()+" breaked by Player "+playerID+"!");
            }
        }catch(Exception e){
            if(!getBlock(pos).breakment_prediction)return;
            breakBlock(pos);
        }
    }

    /**
     * Das gleiche für ein Request
     */
    public void breakBlock(Integer playerID, Integer sandboxIndex, VektorI pos){
        breakBlock(pos, playerID);
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
            ((SBlock)map[pos.x][pos.y]).onDestruct(this, main.getSpace().masses.indexOf(getMass()), pos);
        }catch(Exception e){}
        removeMeta(pos);
    }

    /**
     * Gibt das Block-Object zurück
     */
    public Block getBlock(VektorI pos){
        try{
            return map[pos.x][pos.y];
        }catch(Exception e){ return null; }  // AuÃŸerhalb des Map-Arrays
    }

    /**
     * gibt das Metadaten Object zurück
     */
    public Meta getMeta(VektorI pos){
        try{
            return this.meta[pos.x][pos.y];
        }catch(Exception e){ return null; }  // AuÃŸerhalb des Map-Arrays
    }

    /**
     * setzt das Metadaten Object
     */
    public void setMeta(VektorI pos, Meta meta){
        try{
            this.meta[pos.x][pos.y] = meta;
        }catch(Exception e){ return; }  // AuÃŸerhalb des Map-Arrays
    }

    /**
     * entfernt das Metadaten Object
     */
    public void removeMeta(VektorI pos){
        try{
            this.meta[pos.x][pos.y] = null;
        }catch(Exception e){ return; }  // AuÃŸerhalb des Map-Arrays
    }

    /***********************************************************************************************************************************************************
    /*********3. Methoden fÃ¼r Subsandboxes und Raketenstart*****************************************************************************************************
    /***********************************************************************************************************************************************************/
    
    /**
     * Fügt eine Sandbox hinzu
     */
    public void addSandbox(Sandbox sbNeu, VektorD offsetPos){
        if(sbNeu!=null){
            int index=main.getSpace().masses.indexOf(sbNeu.getMass());
            subsandboxes.add(new SandboxInSandbox(index,offsetPos,new VektorD(0,0),sbNeu.getSize()));
        }
    }
    
    public void addSandbox(SandboxInSandbox sbNeu){
        subsandboxes.add(sbNeu);
    }

    /**
     * Entfernt eine Sandbox
     */
    public void removeSandbox(Sandbox sbR){
        if(sbR!=null)subsandboxes.remove(sbR);
    }

    public ArrayList<SandboxInSandbox> getAllSubsandboxes(){ //kein Request
        return subsandboxes;
    }
    
    public boolean isSubsandbox(int sandboxIndex){
        for (int i=0;i<subsandboxes.size();i++){
            if (subsandboxes.get(i).index==sandboxIndex){
                return true;
            }
        }
        return false;
    }
    
    public SandboxInSandbox[] getAllSubsandboxes(Integer playerID, Integer sandboxIndex){ //Request
        return subsandboxes.toArray(new SandboxInSandbox[subsandboxes.size()]);
    }
    
    /**
     * Gibt die Position zurück, an der sich die Subsandbox, wenn sie sich mit vel weiterbewegen würde, zum ersten Mal mit einem Objekt in dieser Sandbox kollidieren würde
     * (wird gebraucht, um sie sich bewegen zu lassen). Wenn die Sandbox nicht kollidiert, wird offset+vel zurückgegeben
     * => der Timer, der die Sandboxen bewegt, der SpaceTimer, kann einfach diesen Wert übernehmen und als neue Position setzen.
     * dtime ist die Zeit, für die das berechnet wird, in Sekunden (im Spiel).
     * Diese Methode kann vermutlich noch stark optimiert werden.
     */
    public VektorD collisionPoint(SandboxInSandbox sub, long dtime){
        for (double t=0;t<dtime;t=t+ClientSettings.SANDBOX_CALC_PERIOD_INGAME){
            VektorD offsetNew=sub.offset.add(sub.vel.multiply(t));
            for (double x=offsetNew.x;x<offsetNew.x+sub.size.x;x++){
                for (double y=offsetNew.y;y<offsetNew.y+sub.size.y;y++){
                    try{
                        if (map[(int) x][(int) y]!=null){
                            return offsetNew.subtract(sub.vel.multiply(ClientSettings.SANDBOX_CALC_PERIOD_INGAME));
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException e){}
                }
            }
            for (int i=0;i<subsandboxes.size();i++){
                if (subsandboxes.get(i).index!=sub.index){ //keine Kollision mit der Subsandbox selbst
                    SandboxInSandbox sub2=subsandboxes.get(i);
                    VektorD sub2OffsetNew=sub2.offset.add(sub2.vel.multiply(t));
                    Sandbox sb=((Mass) main.getSpace().masses.get(subsandboxes.get(i).index)).getSandbox(); //Das ist der Weg, um von einer SandboxInSandbox auf die dazugehörige Sandbox zu kommen, und er ist überhaupt nicht umständlich.
                    for (double x=offsetNew.x-sub2OffsetNew.x;x<offsetNew.x-sub2OffsetNew.x+sub.size.x;x++){ //Positionen relativ zur anderen Subsandbox
                        for (double y=offsetNew.y-sub2OffsetNew.y;y<offsetNew.y-sub2OffsetNew.y+sub.size.y;y++){
                            try{
                                if (sb.map[(int) x][(int) y]!=null){
                                    return offsetNew.subtract(sub.vel.multiply(ClientSettings.SANDBOX_CALC_PERIOD_INGAME));
                                }
                            }
                            catch(ArrayIndexOutOfBoundsException e){}
                        }
                    }
                }
            }
        }
        return sub.offset.add(sub.vel.multiply(dtime-ClientSettings.SANDBOX_CALC_PERIOD_INGAME));
    }

    /***********************************************************************************************************************************************************
    /*********4. Methoden fÃ¼r Ansicht und Grafikausgabe*********************************************************************************************************
    /***********************************************************************************************************************************************************/
    
    /**
     * Request-Funktion => mapIDCache im client.PlayerC
     */
    public int[][] getMapIDs(Integer playerID, Integer sandboxIndex, VektorI upperLeftCorner, VektorI bottomRightCorner){
        int[][] ret=new int[bottomRightCorner.x-upperLeftCorner.x+1][bottomRightCorner.y-upperLeftCorner.y+1];
        for (int x=upperLeftCorner.x;x<=bottomRightCorner.x;x++){
            for (int y=upperLeftCorner.y;y<=bottomRightCorner.y;y++){
                int i=x-upperLeftCorner.x;
                int j=y-upperLeftCorner.y;
                if (x>=0 && y>=0 && x<map.length && y<map[0].length && map[x][y]!=null){
                    ret[i][j]=map[x][y].getID();
                }
                else
                    ret[i][j]=-1; //Luft
            }
        }
        return ret;
    }
    
    public Main getMain(){
        return main;
    }
}