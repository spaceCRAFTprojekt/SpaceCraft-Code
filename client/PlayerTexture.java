package client;

import javax.swing.JComponent;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;
import util.ImageTools;
import util.geom.*;
/**
 * Da der Spieler, wenn er in der Grafik gepaintet wird immer flackert, wird der Spieler als eigenes JComponent in 
 * das OverlayPanel gemalt
 * 
 * Die SpielerTexturen müssen im Ordner texturesC sein.
 * Bezeichnung: player_texture_<name>
 */
public class PlayerTexture extends JComponent implements Serializable
{
    public static HashMap<Integer, SimpleEntry> textures = new HashMap<Integer, SimpleEntry>();
    static{
        textures.put(0,new SimpleEntry("default",getPlayerTexture("default")));
        textures.put(1,new SimpleEntry("Schnux",getPlayerTexture("schnux")));
    }

    public static BufferedImage getTexture(int id){
        try{
            return (BufferedImage)textures.get(id).getValue();
        }catch(Exception e){
            System.out.println("Etwas komisches ist in client.PlayerTexture passiert: "+e);
            return null;
        }
    }

    public static int RIGHT = 0;
    public static int LEFT = 1;
    public static int WALKING_RIGHT = 2;
    public static int WALKING_LEFT = 3;
    public static int NUM_MODES = 2; // 2 & 3 aktuell noch nicht verfügbar

    public BufferedImage texture;

    private int mode = RIGHT;

    private int blockWidth;

    /**
     * @param: String name: Name des Texturenpakets für den Spieler, wenn "", dann wird die default Textur verwendet
     */
    public PlayerTexture(OverlayPanelC opC, int id, VektorI screenSize ,int blockWidth){
        this.blockWidth = blockWidth;

        VektorI upperLeftCorner = screenSize.toDouble().divide(2).subtract(  new VektorD(blockWidth*0.5,blockWidth*1.5)  ) .toInt();  
        //ich versteh das mit der 1.5 auch nicht
        this.setBounds(upperLeftCorner.x, upperLeftCorner.y, blockWidth, blockWidth*2);
        this.setVisible(true);
        this.setEnabled(false);
        opC.add(this);
        setTexture(id);  // + repaint()
    }

    public void setTexture(int id){
        texture = getTexture(id);
        if (texture == null)texture = getTexture(0);
        repaint();
    }

    public void setMode(int mode){
        this.mode = mode;
        repaint();
    }

    /**
     * importiert die PlayerTexturen.
     * @param: Name des Texturenpakets für den Spieler im textures Ordner
     * 
     */
    public static BufferedImage getPlayerTexture(String name){
        return ImageTools.get('C', "player_texture_"+name);
    }

    @Override
    public void paint(Graphics g){
        if(mode == RIGHT)g.drawImage(texture, 0, 0, blockWidth, blockWidth*2, null);
        else g.drawImage(texture, blockWidth, 0, -blockWidth, blockWidth*2, null);
    }
}