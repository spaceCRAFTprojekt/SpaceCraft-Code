package client;
import javax.swing.JComponent;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

import java.io.Serializable;
import java.io.IOException;
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
public class PlayerTexture implements Serializable
{
    public static final long serialVersionUID=0L;
    public static HashMap<Integer, SimpleEntry> textures = new HashMap<Integer, SimpleEntry>();
    //statische Variablen müssen nicht auch noch transient sein -LG
    static{
        textures.put(0,new SimpleEntry("default",getPlayerTexture("default")));
        textures.put(1,new SimpleEntry("Schnux",getPlayerTexture("schnux")));
        textures.put(2,new SimpleEntry("K�nguru",getPlayerTexture("k�nguru")));
    }

    public static BufferedImage getTexture(int id){
        try{
            return (BufferedImage)textures.get(id).getValue();
        }catch(Exception e){
            System.out.println("Etwas komisches ist in client.PlayerTexture passiert: "+e);
            return null;
        }
    }

    public static transient int RIGHT = 0;
    public static transient int LEFT = 1;
    public static transient int WALKING_RIGHT = 2;
    public static transient int WALKING_LEFT = 3;
    public static transient int NUM_MODES = 2; // 2 & 3 aktuell noch nicht verfügbar

    // Es wird nur die Textur gespeichert
    public int textureID;

    public int mode = RIGHT;

    private transient int blockWidth;

    private transient JComponent component;

    /**
     * @param: String name: Name des Texturenpakets für den Spieler, wenn "", dann wird die default Textur verwendet
     */
    public PlayerTexture(int id){
        setTexture(id);  // + repaint()
    }

    public void makeFrame(OverlayPanelC opC, VektorI screenSize ,int blockWidth){
        component = new JComponent(){
            @Override
            public void paint(Graphics g){
                if(component == null)return;
                PlayerTexture.paint(g,textureID, mode, blockWidth, new VektorI(0,0));
            }
        };

        resize(screenSize, blockWidth);
        component.setVisible(true);
        component.setEnabled(false);
        opC.add(component);
    }

    public void resize(VektorI screenSize ,int blockWidth){
        if(component == null)return;
        this.blockWidth = blockWidth;
        VektorI upperLeftCorner = screenSize.toDouble().divide(2).subtract(  new VektorD(blockWidth*0.5,blockWidth*1.5)  ) .toInt();  
        //ich versteh das mit der 1.5 auch nicht
        component.setBounds(upperLeftCorner.x, upperLeftCorner.y, blockWidth, blockWidth*2);
    }

    public void setTexture(int id){
        if(textures.get(id)!=null)textureID = id;
        if(component == null)return;
        component.repaint();
    }

    public void setMode(int mode){
        this.mode = mode;
        if(component == null)return;
        component.repaint();
    }

    public static void paint(Graphics g, int textureID, int mode, int blockWidth, VektorI pos){
        try{
            if(mode == RIGHT)g.drawImage(getTexture(textureID), pos.x, pos.y, blockWidth, blockWidth*2, null);
            else g.drawImage(getTexture(textureID), blockWidth+pos.x, pos.y, -blockWidth, blockWidth*2, null);
        }catch(Exception e){}
    }

    /**
     * importiert die PlayerTexturen.
     * @param: Name des Texturenpakets für den Spieler im textures Ordner
     * 
     */
    public static BufferedImage getPlayerTexture(String name){
        return ImageTools.get('C', "player_texture_"+name);
    }

}