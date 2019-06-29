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
import client.AbstractMass;
import blocks.*;
/**
 * Eine virtuelle Umgebung aus Blöcken
 * 
 * @Content:
 *  1. Methoden zum Erstellen der Sandbox
 *  2. Methoden für Blöcke (setBlock(),...)
 *  3. Methoden für Subsandboxes und Raketenstart
 *  4. Methoden für Ansicht und Grafikausgabe
 */
public abstract class Sandbox implements Serializable, BlocksSandbox
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
        this.main=main;
    }

    public Sandbox(Main main, Block[][] map, Meta[][] meta, ArrayList<SandboxInSandbox> subsandboxes, Timer spaceTimer){
        this.map=map;
        this.meta=meta;
        this.subsandboxes=subsandboxes;
        this.spaceTimer=spaceTimer;
        this.main=main;
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
            if(!((SBlock)block).onPlace(this, main.getSpace().masses.indexOf(getMass()), pos, playerID))return;
            // ruft onPlace auf, wenn es ein Special Block ist. Wenn es nicht erfolgreich platziert wurde => Abbruch
        }catch(Exception e){} // => kein SpecialBlock => kann immer platziert werden
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
        try{
            map[pos.x][pos.y]= block; 
        }
        catch(ArrayIndexOutOfBoundsException e){}
    }
    
    /**
     * Spieler baut einen Block in die Welt ab, wenn das onBreak() Event true zurückgibt und löscht die Metadaten
     * @param:
     *  * VektorI pos: Position des Blocks
     *  * int playerID
     */
    public void breakBlock(VektorI pos, int playerID){
        try{
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
        catch(ArrayIndexOutOfBoundsException e){}
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
        try{
            map[pos.x][pos.y] = null;
            try{
                ((SBlock)map[pos.x][pos.y]).onDestruct(this, main.getSpace().masses.indexOf(getMass()), pos);
            }catch(Exception e){}
            removeMeta(pos);
        }
        catch(ArrayIndexOutOfBoundsException e){}
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
     * gibt das Metadatum an dieser Position mit diesem Key zurück
     */
    public Object getMeta(VektorI pos, String key){
        try{
            return this.meta[pos.x][pos.y].get(key);
        }catch(Exception e){ return null; }  // Außerhalb des Map-Arrays
    }
    
    /**
     * setzt das Metadatum an dieser Position mit diesem Key
     */
    public void setMeta(VektorI pos, String key, Object o){
        try{
            if (this.meta[pos.x][pos.y]==null)
                this.meta[pos.x][pos.y]=new Meta();
            this.meta[pos.x][pos.y].put(key,o);
        }catch(Exception e){ return; }  // Außerhalb des Map-Arrays
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
    /*********3. Methoden für Subsandboxes und Raketenstart*****************************************************************************************************
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
     * Request, um diese Position herum mit der in den (Client-)Settings angegebenen Größe ein Schiff mit eigener Subsandbox zu konstruieren
     */
    public void createShip(Integer playerID, Integer sandboxIndex, VektorI pos){
        VektorI shipSize=ClientSettings.SHIP_SIZE; //kürzerer Name
        Block[][] shipMap=new Block[shipSize.x][shipSize.y];
        Meta[][] shipMeta=new Meta[shipSize.x][shipSize.y];
        double mass=0;
        for (int x=pos.x-shipSize.x/2;x<=pos.x+shipSize.x/2;x++){
            for (int y=pos.y-shipSize.y/2;y<=pos.y+shipSize.y/2;y++){
                try{
                    Block b=map[x][y];
                    shipMap[x+shipSize.x/2-pos.x][y+shipSize.y/2-pos.y]=b;
                    map[x][y]=null;
                    shipMeta[x+shipSize.x/2-pos.x][y+shipSize.y/2-pos.y]=meta[x][y];
                    meta[x][y]=null;
                    if (b!=null)
                        mass=mass+b.mass;
                }
                catch(ArrayIndexOutOfBoundsException e){}
            }
        }
        VektorD posInSpace=pos.toDouble();
        posInSpace.x=posInSpace.x-map.length/2;
        posInSpace.y=posInSpace.y-map[0].length/2; //Der Mittelpunkt des Planeten in Craft ist nicht bei (0|0)
        posInSpace.y=-posInSpace.y; //Space verwendet ein "normales" mathematisches Koordinatensystem, Craft das Java-y-invertierte
        posInSpace=posInSpace.add(getMass().getPos());
        ShipS shipS=new ShipS(main,mass,posInSpace,getMass().getVel(),10,10,null);
        ShipC shipC=new ShipC(main,shipMap,shipMeta,new ArrayList<SandboxInSandbox>(),null,null);
        shipS.shipC=shipC;
        shipC.shipS=shipS;
        
        shipS.setSpaceTimer(spaceTimer);
        shipC.setSpaceTimer(spaceTimer);
        shipS.setOwner(playerID);
        shipS.isDrawn=false;
        ArrayList<AbstractMass> masses=main.getSpace().masses;
        synchronized(masses){
            masses.add(shipS);
            main.getSpace().calcOrbits(ClientSettings.SPACE_CALC_TIME);
        }
        addSandbox(shipC,pos.subtract(shipSize.divide(2)).toDouble()); //Der Offset ist die Position der oberen linken Ecke, nicht der Mitte
        main.getPlayer(playerID).getPlayerS().reachedMassIDs.add(masses.size()-1);
        
        if (shipMeta[shipSize.x/2][shipSize.y/2]==null) //an dieser Stelle liegt der rocketController, der dieses Metadatum benötigt
            shipMeta[shipSize.x/2][shipSize.y/2]=new Meta();
        shipMeta[shipSize.x/2][shipSize.y/2].put("shipIndex",masses.size()-1);
    }

    /**
     * Entfernt eine Sandbox
     */
    public void removeSandbox(Sandbox sbR){
        if(sbR!=null)subsandboxes.remove(sbR);
    }
    
    /**
     * Index in der Space.masses-Liste
     */
    public void removeSandbox(int index){
        subsandboxes.remove(subsandboxIndex(index));
    }

    public ArrayList<SandboxInSandbox> getAllSubsandboxes(){ //kein Request
        return subsandboxes;
    }
    
    /**
     * gibt den Index in der subsandboxen-Liste zurück, falls es eine Subsandbox ist, sonst -1
     * Der Parameter ist der Index in der Space.masses-Liste
     */
    public int subsandboxIndex(int sandboxIndex){
        for (int i=0;i<subsandboxes.size();i++){
            if (subsandboxes.get(i).index==sandboxIndex){
                return i;
            }
        }
        return -1;
    }
    
    public boolean isSubsandbox(int sandboxIndex){
        return subsandboxIndex(sandboxIndex)!=-1;
    }
    
    /**
     * Request
     */
    public SandboxInSandbox[] getAllSubsandboxes(Integer playerID, Integer sandboxIndex){
        return subsandboxes.toArray(new SandboxInSandbox[subsandboxes.size()]);
    }
    
    /**
     * Request
     * Das Space-Schiff wird erst richtig gestartet (mit Position und Geschwindigkeit), wenn das Craft-Schiff den Planeten erfolgreich verlässt.
     * (siehe PlanetC.handleShipLeaves)
     */
    public void startShip(Integer playerID, Integer sandboxIndex, Integer shipIndex){
        AbstractMass ship=main.getSpace().masses.get(shipIndex);
        int i=subsandboxIndex(shipIndex.intValue());
        if (!(ship instanceof ShipS) || !ship.isControllable(playerID) || i==-1){
            return;
        }
        VektorD posInCraft=subsandboxes.get(i).offset.add(ClientSettings.SHIP_SIZE.divide(2).toDouble());
        posInCraft.x=posInCraft.x-map.length/2;
        posInCraft.y=posInCraft.y-map[0].length/2; //Jetzt liegt der Mittelpunkt des Planeten bei (0|0)
        VektorD velInCraft;
        if (posInCraft.x>=0 && posInCraft.x>=Math.abs(posInCraft.y)) //erstmal nur senkrecht vom Planeten wegkommen
            velInCraft=new VektorD(1,0);
        else if (posInCraft.y>=0 && posInCraft.y>=Math.abs(posInCraft.x))
            velInCraft=new VektorD(0,1);
        else if (posInCraft.x<0 && Math.abs(posInCraft.x)>=Math.abs(posInCraft.y))
            velInCraft=new VektorD(-1,0);
        else
            velInCraft=new VektorD(0,-1);
        velInCraft=velInCraft.multiply(ship.getOutvel()/10); //physikalisch inkorrekt, aber was solls
        subsandboxes.get(i).vel=velInCraft;
        main.getSpace().calcOrbits(ClientSettings.SPACE_CALC_TIME);
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
    
    public void newTask(int playerID, String todo, Object... params){
        main.newTask(playerID,todo,params);
    }
}