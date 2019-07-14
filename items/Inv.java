package items;
import util.geom.*;
import java.io.Serializable;

/**
 * Ein Inventar
 * Wichtig: Für alle Änderungen von Slots immer die Methode setStack() verwenden!!!
 */
public class Inv implements Serializable
{
    public static final long serialVersionUID=0L;
    Stack[][]stacks;
    public Inv(VektorI size){
        stacks = new Stack[size.x][size.y];
    }
    
    /**
     * fügt ein Stack an den nächsten freien Slot, wenn kein Platz, dann returns leftover
     */
    public Stack addStack(Stack s){
        Stack leftover = s;
        for(int y = 0; y < stacks[0].length; y++){  // erst y, damit die Items von links nach rechts befÃ¼llt werden
            for(int x = 0; x < stacks.length; x++){
                if(stacks[x][y] == null) {
                    stacks[x][y] = leftover;
                    update();
                    return new Stack(s.item, 0);
                }
                else leftover = stacks[x][y].add(leftover);
                if(leftover == null || leftover.getCount() == 0){
                    update();
                    return null;
                }
            }
        }
        update();
        return leftover;
    }
    
    public Stack getStack(VektorI v){
        return stacks[v.x][v.y];
    }
    
    /**
     * Diese Methode immer verwenden, wenn etwas verÃ¤ndert werden soll!!!
     */
    public void setStack(VektorI v,Stack s){
        stacks[v.x][v.y] = s;
        update();
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
        setStack(v, null);
    }
    
    public int getSizeX() {
        return stacks.length;
    }
    public int getSizeY(){
        try{ return stacks[0].length; }catch(Exception e){return 0; } // Warum sollte das Inventar 0 Blï¿½cke groï¿½ sein?! Sicher ist sicher
    }
    public VektorI getSize(){
        try{ return new VektorI(stacks.length, stacks[0].length); }catch(Exception e){return null; } // Warum sollte das Inventar 0 Blï¿½cke groï¿½ sein?! Sicher ist sicher }catch(Exception e){return 0; } // Warum sollte das Inventar 0 Blï¿½cke groï¿½ sein?! Sicher ist sicher
    }
        
    public boolean inBounds(VektorI v){
        try{ Stack s = stacks[v.x][v.y]; }catch(Exception e){return false;}return true;  // Damit kï¿½nnte ich einen Schï¿½nheitswettbewerb gewinnen xD ~unknown
    } 

    public boolean isEmpty(){
        for(int i = 0; i < stacks[0].length*stacks.length; i++){
            Stack s = stacks[i%stacks[0].length][i/stacks.length];
            if(s == null || s.count <= 0)return false;
        }
        return true;
    }
    

    /**
     * kann überschrieben werden, damit man bei jeder Änderung das Inv neu lädt
     */
    public void update(){}

}
