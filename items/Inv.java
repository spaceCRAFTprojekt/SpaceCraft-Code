package items;
import util.geom.*;
import java.io.Serializable;

/**
 * Write a description of class Inv here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Inv implements Serializable
{
    Stack[][]stacks;
    public Inv(VektorI size){
        stacks = new Stack[size.x][size.y];
    }
    
    /**
     * f�gt ein Stack an den n�chsten freien Slot, wenn kein Platz, dann returns leftover
     */
    public Stack addStack(Stack s){
        Stack leftover = s;
        for(int x = 0; x < stacks.length; x++){
            for(int y = 0; y < stacks[x].length; y++){
                if(stacks[x][y] == null) {
                    stacks[x][y] = leftover;
                    return new Stack(s.item, 0);
                }
                else leftover = stacks[x][y].add(leftover);
            }
        }
        return leftover;
    }
    
    public Stack getStack(VektorI v){
        return stacks[v.x][v.y];
    }
    
    public void setStack(VektorI v,Stack s){
        stacks[v.x][v.y] = s;
    }
    public Stack addToStack(VektorI v, Stack s){
        Stack sTo = getStack(v);
        if(sTo == null){
            setStack(v, s);
            return null;
        }else{
            return sTo.add(s);
        }
    }
    public void removeStack(VektorI v){
        stacks[v.x][v.y] = null;
    }
    
    public int getSizeX() {
        return stacks.length;
    }
    public int getSizeY(){
        try{ return stacks[0].length; }catch(Exception e){return 0; } // Warum sollte das Inventar 0 Bl�cke gro� sein?! Sicher ist sicher
    }
    public VektorI getSize(){
        try{ return new VektorI(stacks.length, stacks[0].length); }catch(Exception e){return null; } // Warum sollte das Inventar 0 Bl�cke gro� sein?! Sicher ist sicher }catch(Exception e){return 0; } // Warum sollte das Inventar 0 Bl�cke gro� sein?! Sicher ist sicher
    }
        
    public boolean inBounds(VektorI v){
        try{ Stack s = stacks[v.x][v.y]; }catch(Exception e){return false;}return true;  // Damit k�nnte ich einen Sch�nheitswettbewerb gewinnen xD ~unknown
    } 
}
