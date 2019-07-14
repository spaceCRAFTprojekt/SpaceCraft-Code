package blocks;

import util.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.Serializable;
import java.io.ObjectStreamException;

/*

 */
public class Blocks_Door extends SBlock{
    public static final long serialVersionUID=0L;
    public Blocks_Door(int id){
        super(id, "door", "blocks_door_open_bottom", true);  // der Block selbst müsste eig door_open_bottom heißen
        items.Items.get(id).setInventoryImage("items_door");
    }

    @Override
    public void onRightclick(BlocksSandbox sb, int sandboxIndex, VektorI pos, int playerID){
        toggleDoor(sb, pos);
    }

    /**
     * EVENT: wird aufgerufen, wenn ein Block gesetzt wird (nicht zwingend von einem Spieler)
     * @param:
     * Sandbox sb: Sandbox, in der der Block ist
     * int sandboxIndex: Index der Sandbox, für eventuelle Tasks
     * VektorI pos: Position des Blocks in dieser Sandbox
     */
    @ Override public void onConstruct(BlocksSandbox sb, int sandboxIndex, VektorI pos){
        sb.setBlock(door_open_top, pos.add(new VektorI(0, -1)));
    }

    /**
     * EVENT: wird aufgerufen, wenn ein Block entfernt wird (nicht zwingend von einem Spieler)
     * @param:
     * Sandbox sb: Sandbox, in der der Block ist
     * int sandboxIndex: Index der Sandbox, für eventuelle Tasks
     * VektorI pos: Position des Blocks in dieser Sandbox
     */
    @Override public void onDestruct(BlocksSandbox sb, int sandboxIndex, VektorI pos){
        sb.setBlock(null, pos.add(new VektorI(0, -1)));
    }

    // Die anderen Türblöcke und die Methode zum auf-/zumachen sind static

    public static void toggleDoor(BlocksSandbox sb, VektorI pos){
        VektorI pos2 = pos.add(new VektorI(0,-1)); // der zweite Block der Tür
        if(sb.getBlock(pos).getName() == "door"){ // => open
            sb.swapBlock(door_close_bottom, pos);
            sb.swapBlock(door_close_top, pos2);
        }else if(sb.getBlock(pos).getName() == "door_close_bottom"){ // =>  close
            sb.swapBlock(Blocks.get(120), pos);
            sb.swapBlock(door_open_top, pos2);
        }
    }

    static SBlock door_close_bottom = new SBlock(122, "door_close_bottom", "blocks_door_close_bottom", false){
            @Override public void onRightclick(BlocksSandbox sb, int sandboxIndex, VektorI pos, int playerID){
                toggleDoor(sb, pos);
            }

            @Override public void setProperties(){
                placement_prediction = false;
                walkable = false;
                drop = 120;
            }

            /**
             * EVENT: wird aufgerufen, wenn ein Block entfernt wird (nicht zwingend von einem Spieler)
             * @param:
             * Sandbox sb: Sandbox, in der der Block ist
             * int sandboxIndex: Index der Sandbox, für eventuelle Tasks
             * VektorI pos: Position des Blocks in dieser Sandbox
             */
            @Override public void onDestruct(BlocksSandbox sb, int sandboxIndex, VektorI pos){
                sb.setBlock(null, pos.add(new VektorI(0, -1)));
            }
        };

    static SBlock door_close_top = new SBlock(123, "door_close_top", "blocks_door_close_top", false){
            @Override public void onRightclick(BlocksSandbox sb, int sandboxIndex, VektorI pos, int playerID){
                toggleDoor(sb, pos.add(new VektorI(0,1)));  // der Block eins drunter ist die "Basis" der Tür
            }

            @Override public void setProperties(){
                placement_prediction = false;
                walkable = false;
                drop = 120;
            }

            /**
             * EVENT: wird aufgerufen, wenn ein Block entfernt wird (nicht zwingend von einem Spieler)
             * @param:
             * Sandbox sb: Sandbox, in der der Block ist
             * int sandboxIndex: Index der Sandbox, für eventuelle Tasks
             * VektorI pos: Position des Blocks in dieser Sandbox
             */
            @Override public void onDestruct(BlocksSandbox sb, int sandboxIndex, VektorI pos){
                sb.setBlock(null, pos.add(new VektorI(0, 1)));
            }
        };

    static SBlock door_open_top = new SBlock(121, "door_open_top", "blocks_door_open_top", false){
            @Override public void onRightclick(BlocksSandbox sb, int sandboxIndex, VektorI pos, int playerID){
                toggleDoor(sb, pos.add(new VektorI(0,1)));  // der Block eins drunter ist die "Basis" der Tür
            }

            @Override public void setProperties(){
                placement_prediction = false;
                drop = 120;
            }

            /**
             * EVENT: wird aufgerufen, wenn ein Block entfernt wird (nicht zwingend von einem Spieler)
             * @param:
             * Sandbox sb: Sandbox, in der der Block ist
             * int sandboxIndex: Index der Sandbox, für eventuelle Tasks
             * VektorI pos: Position des Blocks in dieser Sandbox
             */
            @Override public void onDestruct(BlocksSandbox sb, int sandboxIndex, VektorI pos){
                sb.setBlock(null, pos.add(new VektorI(0, 1)));
            }
        };
}