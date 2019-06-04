package client;
import util.geom.VektorD;
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
    public VektorD vel; //Geschwindigkeit, nur zum Simulieren durch den Client, noch nicht implementiert

    public SandboxInSandbox(int index, VektorD offset, VektorD vel)
    {
        this.index = index;
        this.offset = offset;
        this.vel=vel;
    }
}
