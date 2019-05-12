package client;
import java.awt.image.BufferedImage;
import java.io.File;
import util.geom.VektorI;
import javax.imageio.ImageIO;
import java.io.IOException;
/**
 * ImageTools
 * get() gibt ein Image zurück
 * v0.0.1 by AK
 */
public abstract class ImageTools
{
    /**
     * @param  char type: 'A': Allgemein
     *                    'S': Space
     *                    'C': Craft
     * @return BufferedImage Object oder wenn nicht vorhanden null
     */
    static BufferedImage get(char type, String name)
    {
        try{
            BufferedImage img=ImageIO.read(new File("textures"+Character.toUpperCase(type)+"/"+name+".png"));
            return img;
        }catch(IOException e){
            return null;
        }
    }
}