import geom.*;
import java.io.Serializable;
public interface OrbitI extends Serializable{
    public VektorL getPos(int t);
    public VektorL getVel(int t);
}