package client;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import geom.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.Color;
import java.io.Serializable;
import java.io.ObjectStreamException;
/**
 * ein Spieler in der Craft Ansicht
 */
public class PlayerC implements Serializable
{
    private transient Timer timer;

    private int blockBreite = 32;  // Breite eines Blocks in Pixeln
    private Player player;
    private VektorD pos;
    private boolean onPlanet; //sonst: auf einem Schiff
    private int sandboxIndex; //entweder im ShipCs-Array oder im PlanetCs-Array der Index der Sandbox, in der sich der PlayerC gerade befindet
    private transient BufferedImage texture;
    private VektorD hitbox = new VektorD(1,2);
    public PlayerC(Player player, boolean onPlanet, int sandboxIndex, VektorD pos, Frame frame)
    {
        this.player = player;
        setSandbox(onPlanet, sandboxIndex, pos);
        makeTexture();
        timerSetup();
    }

    private void makeTexture(){
        texture = ImageTools.get('C',"player_texture");
    }

    private void timerSetup(){
        this.timer=new Timer();
        timer.schedule(new TimerTask(){
                public void run(){
                    repaint();
                }
            },0,ClientSettings.PLAYERC_TIMER_PERIOD);
    }

    Object readResolve() throws ObjectStreamException{
        this.makeTexture();
        this.timerSetup();
        return this;
    }

    /**
     * Setze Spieler in einer andere Sandbox
     */
    public void setSandbox(boolean onPlanet, int sandboxIndex, VektorD pos){
        this.onPlanet=onPlanet;
        this.sandboxIndex = sandboxIndex;
        this.pos = pos;
    }

    /**
     * Tastatur event
     * @param:
     *  char type: 'p': pressed
     *             'r': released
     *             't': typed (nur Unicode Buchstaben)
     */
    public void keyEvent(KeyEvent e, char type) {
        if (type == 'p'){
            //System.out.println("KeyEvent in PlayerC: "+e.getKeyChar()+type);
            //braucht eigentlich noch einen posInsideOfBounds request o.Ä.
            switch(Character.toLowerCase(e.getKeyChar())){
                case 'w': pos.y=pos.y - 1; // up
                break;
                case 's': pos.y=pos.y + 1; // down
                break;
                case 'a': pos.x=pos.x - 1; // left
                break;
                case 'd': pos.x=pos.x + 1; // right
                break;
            }
            System.out.println(pos.toString());
        }
    }

    /**
     * Maus Event
     * @param:
     *  char type: 'p': pressed
     *             'r': released
     *             'c': clicked
     *             'd': dragged
     * entered und exited wurde nicht implementiert, weil es dafür bisher keine Verwendung gab
     */
    public void mouseEvent(MouseEvent e, char type) {
        if (type == 'c'){
            VektorI clickPos = new VektorI(e);
            VektorI sPos=new VektorI(Integer.MAX_VALUE,Integer.MAX_VALUE); //schlecht gelöst: sollte ein unmöglicher Wert sein
            new Request(player,"Sandbox.getPosToPlayer",sPos,onPlanet,sandboxIndex,clickPos,pos,blockBreite);
            if (sPos.x!=Integer.MAX_VALUE && sPos.y!=Integer.MAX_VALUE){
                if (e.getButton() == e.BUTTON1){   // rechtsklick => abbauen
                    //System.out.println("Tried to break block at "+sPos.toString());
                    Boolean success=new Boolean(false);
                    new Request(player,"Sandbox.leftclickBlock",success,onPlanet,sandboxIndex,sPos);
                }else if (e.getButton() == e.BUTTON3){  // rechtsklick => platzieren
                    //System.out.println("Tried to place block at "+sPos.toString());
                    Boolean success=new Boolean(false);
                    new Request(player,"Sandbox.rightclickBlock",success,onPlanet,sandboxIndex,sPos);
                }
            }
        }
    }
    
    /***********************************************************************************************************************************************************
    /*********3. Methoden für Subsandboxes und Raketenstart*****************************************************************************************************
    /***********************************************************************************************************************************************************

    /***********************************************************************************************************************************************************
    /*********4. Methoden für Ansicht und Grafikausgabe*********************************************************************************************************
    /***********************************************************************************************************************************************************

    /**
     * Gibt die obere rechte Ecke (int Blöcken) der Spieleransicht an
     * @param: pos: Position des Spielers relativ zur oberen rechten Ecke der Sandbox
     */
    public VektorD getUpperLeftCorner(VektorD pos){
        return pos.add(ClientSettings.PLAYERC_FIELD_OF_VIEW.toDouble().multiply(-0.5) ).add(new VektorD(0.5,0.5));
    }

    /**
     * Grafik ausgeben
     */
    public void paint(Graphics g, VektorI screenSize){
        VektorI upperLeftCorner = getUpperLeftCorner(pos).toInt();  // obere linke Ecke der Spieleransicht relativ zur oberen linken Ecke der sb
        VektorI bottomRightCorner = upperLeftCorner.add(ClientSettings.PLAYERC_FIELD_OF_VIEW);  // untere rechte Ecke der Spieleransicht relativ zur oberen linken Ecke der sb
        //System.out.println("UpperLeftCorner: "+ upperLeftCorner.toString()+ " BottomRightCorner: " + bottomRightCorner.toString());
        int[][] mapIDs=new int[0][0];
        new Request(player,"Sandbox.getMapIDs",mapIDs,onPlanet,sandboxIndex,upperLeftCorner,bottomRightCorner);
        ColorModel cm=ColorModel.getRGBdefault();
        BufferedImage image=new BufferedImage(cm,cm.createCompatibleWritableRaster(ClientSettings.PLAYERC_FIELD_OF_VIEW.x*blockBreite,ClientSettings.PLAYERC_FIELD_OF_VIEW.y*blockBreite),false,new Hashtable<String,Object>());
        //alle hier erstellten BufferedImages haben den TYPE_INT_ARGB
        int[] oldImageData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        Hashtable<Integer,BufferedImage> blockImages=new Hashtable<Integer,BufferedImage>(); //Skalierung
        for (int x = 0; x<=screenSize.x; x++){
            for (int y = 0; y<=screenSize.y; y++){
                BufferedImage img=BlocksC.images.get(mapIDs[x][y]);
                if (img!=null){
                    Hashtable<String,Object> properties=new Hashtable<String,Object>();
                    String[] prns=image.getPropertyNames();
                    if (prns!=null){
                        for (int i=0;i<prns.length;i++){
                            properties.put(prns[i],image.getProperty(prns[i]));
                        }
                    }
                    BufferedImage img2=new BufferedImage(cm,cm.createCompatibleWritableRaster(blockBreite,blockBreite),false,properties);
                    Graphics gr=img2.getGraphics();
                    gr.drawImage(img,0,0,blockBreite,blockBreite,null);
                    blockImages.put(mapIDs[x][y],img2);
                }
                else{
                    Hashtable<String,Object> properties=new Hashtable<String,Object>();
                    blockImages.put(mapIDs[x][y],new BufferedImage(cm,cm.createCompatibleWritableRaster(blockBreite,blockBreite),false,properties));
                }
            }
        }

        for (int x = upperLeftCorner.x; x<bottomRightCorner.x; x++){
            for (int y = upperLeftCorner.y; y<bottomRightCorner.y; y++){
                int id = mapIDs[x][y];
                if(id != -1){ //Luft
                    BufferedImage img=blockImages.get(id);
                    int[] imgData=((DataBufferInt) img.getRaster().getDataBuffer()).getData();
                    for (int i=0;i<blockBreite;i++){
                        int index = ((y-upperLeftCorner.y)*blockBreite + i)*ClientSettings.PLAYERC_FIELD_OF_VIEW.x*blockBreite + (x-upperLeftCorner.x)*blockBreite;
                        System.arraycopy(imgData,i*blockBreite,oldImageData,Math.min(index,oldImageData.length-blockBreite-1),blockBreite);
                    }
                }
            }
        }
        g.setColor(new Color(0,0,0,1));
        g.drawImage(image,0,0,new Color(0,0,0,255),null);
        
        g.drawImage(texture, (screenSize.x-20)/2, (screenSize.y-32)/2,40, 64, null);
    }

    public void repaint(){
        player.repaint();
    }
}