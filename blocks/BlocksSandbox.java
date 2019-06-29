package blocks;

import java.util.ArrayList;
import util.geom.VektorI;
import client.SandboxInSandbox;
/**
 * Damit dieses Package nichts von server.* importieren muss (für die Events), 
 * gibt es hier ein Interface für Sandboxen.
 * Dieses enthält viele Methoden, die für Block-Events sinnvoll sein können, 
 * aber nicht alle Methoden, die server.Sandbox hat. Für eine Beschreibung der
 * Methoden siehe server.Sandbox
 * Dazu gibt es noch die Methode newTask, für eventuelle Tasks (in server.Sandbox 
 * leitet diese nur auf Main.newTask weiter).
 */
public interface BlocksSandbox{
    public VektorI getSize();
    
    /**
     * kann eine Request-Funktion sein, ist hier aber trotzdem
     */
    public void rightclickBlock(Integer playerID, Integer sandboxIndex, VektorI pos);
    public void placeBlock(Block block, VektorI pos, int playerID);
    public void setBlock(Block block, VektorI pos);
    public void swapBlock(Block block, VektorI pos);
    public void breakBlock(VektorI pos, int playerID);
    public void breakBlock(VektorI pos);
    public Block getBlock(VektorI pos);
    
    public Meta getMeta(VektorI pos);
    public void setMeta(VektorI pos, Meta meta);
    public Object getMeta(VektorI pos, String key);
    public void setMeta(VektorI pos, String key, Object o);
    public void removeMeta(VektorI pos);
    
    public void addSandbox(SandboxInSandbox sbNeu);
    public void removeSandbox(int index);
    public ArrayList<SandboxInSandbox> getAllSubsandboxes();
    public int subsandboxIndex(int sandboxIndex);
    public boolean isSubsandbox(int sandboxIndex);
    
    public void newTask(int playerID, String todo, Object... params);
}