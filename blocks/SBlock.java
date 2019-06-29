package blocks;

import util.geom.*;
/**
 * für alle Blöcke, die mehr als eine dekorative Funktion haben
 * z.B.:
 *  Kisten
 *  Pistons
 *  Cables
 */
public abstract class SBlock extends Block
{
    /**
     * ...
     */
    public SBlock(int id, String name, String imageString, boolean hasItem)
    {
        super(id, name, imageString, hasItem);
    }
    
    public SBlock(int id, String name, boolean hasItem)
    {
        super(id, name, hasItem);
    }
    
    /**
     * EVENT: wird aufgerufen, wenn ein Block gerechtsklickt wird
     * @param:
     * BlocksSandbox sb: Sandbox, in der der Block ist
     * int sandboxIndex: Index der Sandbox, für eventuelle Tasks
     * VektorI pos: Position des Blocks in dieser Sandbox
     * int playerID: ID des Spielers
     */
    public void onRightclick(BlocksSandbox sb, int sandboxIndex, VektorI pos, int playerID){};
    
    /**
     * EVENT: wird aufgerufen, wenn ein Block von einem Spieler gesetzt wird
     *        gibt true zurück, wenn der Block gesetzt werden soll
     *        gibt false zurück, wenn der Block nicht gesetzt werden soll
     * @param:
     * BlocksSandbox sb: Sandbox, in der der Block ist
     * int sandboxIndex: Index der Sandbox, für eventuelle Tasks
     * VektorI pos: Position des Blocks in dieser Sandbox
     * int playerID: ID des Spielers
     */
    public boolean onPlace(BlocksSandbox sb, int sandboxIndex, VektorI pos, int playerID){
        return placement_prediction;
    }
    
    /**
     * EVENT: wird aufgerufen, wenn ein Block gesetzt wird (nicht zwingend von einem Spieler)
     * @param:
     * BlocksSandbox sb: Sandbox, in der der Block ist
     * int sandboxIndex: Index der Sandbox, für eventuelle Tasks
     * VektorI pos: Position des Blocks in dieser Sandbox
     */
    public void onConstruct(BlocksSandbox sb, int sandboxIndex, VektorI pos){}
    
    /**
     * EVENT: wird aufgerufen, wenn ein Block von einem Spieler abgebaut wird
     *        gibt true zurück, wenn der Block gesetzt werden soll
     *        gibt false zurück, wenn der Block nicht gesetzt werden soll
     * @param:
     * BlocksSandbox sb: Sandbox, in der der Block ist
     * int sandboxIndex: Index der Sandbox, für eventuelle Tasks
     * VektorI pos: Position des Blocks in dieser Sandbox
     * int playerID: ID des Spielers
     */
    public boolean onBreak(BlocksSandbox sb, int sandboxIndex, VektorI pos, int playerID){
        return breakment_prediction;
    }
    
    /**
     * EVENT: wird aufgerufen, wenn ein Block entfernt wird (nicht zwingend von einem Spieler)
     * @param:
     * BlocksSandbox sb: Sandbox, in der der Block ist
     * int sandboxIndex: Index der Sandbox, für eventuelle Tasks
     * VektorI pos: Position des Blocks in dieser Sandbox
     */
    public void onDestruct(BlocksSandbox sb, int sandboxIndex, VektorI pos){}
    
    /** 
     * !!!!!NOCH NICHT IMPLEMENTIERT!!!!
     * EVENT: wird bei jedem Craft-Tick aufgerufen
     * @param:
     * BlocksSandbox sb: Sandbox, in der der Block ist
     * int sandboxIndex: Index der Sandbox, für eventuelle Tasks
     * VektorI pos: Position des Blocks in dieser Sandbox
     */
    public void onTimer(BlocksSandbox sb, int sandboxIndex, VektorI pos){
    }
}