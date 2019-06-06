package client;
import util.geom.VektorD;
import util.geom.VektorI;
import java.io.Serializable;
/**
 * Enthält den Index einer Sandbox (in der Space.masses-Liste) und die Position der Sandbox (linke obere Ecke) relativ zu einer anderen Sandbox
 */
public class SandboxInSandbox implements Serializable
{
    public static final long serialVersionUID=0L;
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
