package client;

import util.geom.*;
import java.io.Serializable;
/**
 * nur für den Transport der Daten zum Client
 */
public class OtherPlayerTexture implements Serializable
{
    public int playerID;
    public int mode;
    public int textureID;
    public VektorD pos;
    
    public OtherPlayerTexture(int playerID, int mode, int textureID, VektorD pos)
    {
        this.playerID = playerID;
        this.pos = pos;
        this.textureID = textureID;
        this.mode = mode;
    }
}
