package server;
import geom.VektorI;
import java.io.Serializable;
/**
 * Enthält eine Sandbox und die Position der Sandbox (linke obere Ecke) relativ zu einer anderen Sandbox
 */
public class SandboxInSandbox implements Serializable
{
    public Sandbox sandbox;
    /**
     * Position der Sandbox (linke obere Ecke) relativ zu einer anderen Sandbox
     */
    public VektorI offset;

    public SandboxInSandbox(Sandbox sandbox, VektorI offset)
    {
        this.sandbox = sandbox;
        this.offset = offset;
    }
}
