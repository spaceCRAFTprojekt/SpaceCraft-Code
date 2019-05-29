package server;
import util.geom.VektorD;
import java.io.Serializable;
/**
 * Enthält eine Sandbox und die Position der Sandbox (linke obere Ecke) relativ zu einer anderen Sandbox
 */
public class SandboxInSandbox implements Serializable
{
    public static final long serialVersionUID=0L;
    public Sandbox sandbox;
    /**
     * Position der Sandbox (linke obere Ecke) relativ zu einer anderen Sandbox
     */
    public VektorD offset;
    public VektorD vel; //Geschwindigkeit, nur zum Simulieren durch den Client

    public SandboxInSandbox(Sandbox sandbox, VektorD offset, VektorD vel)
    {
        this.sandbox = sandbox;
        this.offset = offset;
        this.vel=vel;
    }
}
