import java.io.Serializable;
/**
 * Write a description of class Orbit here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Orbit implements Serializable
{
    OrbitI orbit;
    public Orbit(OrbitI orbit){
        this.orbit=orbit;
    }
}