package client;
import util.geom.VektorD;
import util.geom.VektorI;
import java.io.Serializable;
/**
 * Diese Objekte können zwischen Server und Client hin und her gesendet werden.
 */
public class SandboxInSandbox implements Serializable
{
    public static final long serialVersionUID=0L;
    /**
     * Index der Sandbox (in der Space.masses-Liste)
     */
    public int index;
    /**
     * Position der Sandbox (linke obere Ecke) relativ zu einer anderen Sandbox
     */
    public VektorD offset;
    /**
     * Geschwindigkeit
     */
    public VektorD vel;
    /**
     * Größe der Subsandbox in Blöcken
     */
    public VektorI size;

    public SandboxInSandbox(int index, VektorD offset, VektorD vel, VektorI size)
    {
        this.index = index;
        this.offset = offset;
        this.vel=vel;
        this.size=size;
    }
}
