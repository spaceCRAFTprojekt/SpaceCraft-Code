package client;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import util.geom.*;
import items.*;
import util.ImageTools;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.io.ObjectStreamException;
/**
 * ein Spieler in der Craft Ansicht
 */
public class PlayerC implements Serializable
{
    //alle Variablen, die synchronisiert werden, müssen public sein
    private transient Timer timer;

    private int blockBreite = 32;  // Breite eines Blocks in Pixeln
    private Player player;
    public VektorD pos;
    public boolean onPlanet; //sonst: auf einem Schiff
    public int sandboxIndex; //entweder im ShipCs-Array oder im PlanetCs-Array der Index der Sandbox, in der sich der PlayerC gerade befindet
    private transient BufferedImage texture;
    private VektorD hitbox = new VektorD(1,2);
    
    private Inv inv;
    public PlayerC(Player player, boolean onPlanet, int sandboxIndex, VektorD pos, Frame frame)
    {
        this.player = player;
        setSandbox(onPlanet, sandboxIndex, pos);
        makeTexture();
        //muss man hier auch schon synchronisieren?  ka ~ unknown
        
        // Inventar:
        inv = new Inv(ClientSettings.INV_SIZE);
        inv.addStack(new Stack(new CraftItem(1, "", BlocksC.images.get(1)),99));
        inv.setStack(new VektorI(3,3),new Stack(new CraftItem(1, "", BlocksC.images.get(1)),90));
        inv.setStack(new VektorI(7,3),new Stack(new CraftItem(1, "", BlocksC.images.get(1)),34));
        inv.addStack(new Stack(new CraftItem(2, "", BlocksC.images.get(2)),34));
        inv.addStack(new Stack(new CraftItem(0, "", BlocksC.images.get(0)),34));
    }

    private void makeTexture(){
        texture = ImageTools.get('C',"player_texture");
    }

    public void timerSetup(){ //wird von Player.login aufgerufen
        this.timer=new Timer();
        timer.schedule(new TimerTask(){
                public void run(){
                    if (player.onClient() && player.isOnline()){
                        repaint();
                    }
                }
            },0,ClientSettings.PLAYERC_TIMER_PERIOD);
        timer.schedule(new TimerTask(){
                public void run(){
                    if (player.onClient() && player.isOnline()){
                        synchronizeWithServer();
                    }
                }
            },ClientSettings.SYNCHRONIZE_REQUEST_PERIOD,ClientSettings.SYNCHRONIZE_REQUEST_PERIOD);
    }

    Object readResolve() throws ObjectStreamException{
        if (player.isOnline() && player.onClient()){
            this.makeTexture();
            this.timerSetup();
        }
        return this;
    }

    /**
     * Setze Spieler in einer andere Sandbox
     */
    public void setSandbox(boolean onPlanet, int sandboxIndex, VektorD pos){
        this.onPlanet=onPlanet;
        this.sandboxIndex = sandboxIndex;
        this.pos = pos;
        if (player.isOnline() && player.onClient()){
            new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerCVariable",null,"onPlanet",Boolean.class,onPlanet);
            new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerCVariable",null,"sandboxIndex",Integer.class,sandboxIndex);
            new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerCVariable",null,"pos",VektorD.class,pos);
        }
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
            switch(e.getKeyCode()){
                case Shortcuts.move_up: pos.y=pos.y - 1;
                break;
                case Shortcuts.move_down: pos.y=pos.y + 1;
                break;
                case Shortcuts.move_left: pos.x=pos.x - 1;
                break;
                case Shortcuts.move_right: pos.x=pos.x + 1;
                break;
                case Shortcuts.open_inventory: openInventory();
                break;
            }
            if (player.isOnline() && player.onClient())
                new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerCVariable",null,"pos",VektorD.class,pos);
            //System.out.println(pos.toString());
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
            VektorI sPos=getPosToPlayer(clickPos,blockBreite);
            if (e.getButton() == e.BUTTON1){   // rechtsklick => abbauen
                //System.out.println("Tried to break block at "+sPos.toString());
                if (player.isOnline() && player.onClient()){
                    Boolean success=(Boolean) (new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.leftclickBlock",Boolean.class,onPlanet,sandboxIndex,sPos).ret);
                }
            }else if (e.getButton() == e.BUTTON3){  // rechtsklick => platzieren
                //System.out.println("Tried to place block at "+sPos.toString());
                if (player.isOnline() && player.onClient()){
                    Boolean success=(Boolean) (new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.rightclickBlock",Boolean.class,onPlanet,sandboxIndex,sPos).ret);
                }
            }
        }
    }
    
    // und die Methoden, die für diese Events gebraucht werden
    public void openInventory(){
        new InventoryMenu(player, this.inv);
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
     * Gibt die Position eines Blocks an
     * 
     * @param: 
     * bPos: Position des Blocks relativ zur oberen rechten Ecke der Spieleransicht in Pixeln
     * blockBreite: Breite eines Blocks in Pixeln
     */
    public VektorI getPosToPlayer(VektorI bPos, int blockBreite){
        //System.out.println(bPos.toString()+" "+bPos.toDouble().divide(blockBreite).toString());
        return (getUpperLeftCorner(pos).add(bPos.toDouble().divide(blockBreite))).toIntFloor();
    }

    /**
     * Grafik ausgeben
     */
    public void paint(Graphics g, VektorI screenSize){
        if (player.isOnline() && player.onClient()){
            VektorI upperLeftCorner = getUpperLeftCorner(pos).toInt();  // obere linke Ecke der Spieleransicht relativ zur oberen linken Ecke der sb
            VektorI bottomRightCorner = upperLeftCorner.add(ClientSettings.PLAYERC_FIELD_OF_VIEW);  // untere rechte Ecke der Spieleransicht relativ zur oberen linken Ecke der sb
            //System.out.println("UpperLeftCorner: "+ upperLeftCorner.toString()+ " BottomRightCorner: " + bottomRightCorner.toString());
            int[][] mapIDs=(int[][]) (new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.getMapIDs",int[][].class,onPlanet,sandboxIndex,upperLeftCorner,bottomRightCorner).ret);
            ColorModel cm=ColorModel.getRGBdefault();
            BufferedImage image=new BufferedImage(cm,cm.createCompatibleWritableRaster(ClientSettings.PLAYERC_FIELD_OF_VIEW.x*blockBreite,ClientSettings.PLAYERC_FIELD_OF_VIEW.y*blockBreite),false,new Hashtable<String,Object>());
            //alle hier erstellten BufferedImages haben den TYPE_INT_ARGB
            int[] oldImageData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    
            Hashtable<Integer,BufferedImage> blockImages=new Hashtable<Integer,BufferedImage>(); //Skalierung
            for (int x = 0; x<=ClientSettings.PLAYERC_FIELD_OF_VIEW.x; x++){
                for (int y = 0; y<=ClientSettings.PLAYERC_FIELD_OF_VIEW.y; y++){
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
    
            for (int x = 0; x<ClientSettings.PLAYERC_FIELD_OF_VIEW.x; x++){
                for (int y = 0; y<ClientSettings.PLAYERC_FIELD_OF_VIEW.y; y++){
                    int id = mapIDs[x][y];
                    if(id != -1){ //Luft
                        BufferedImage img=blockImages.get(id);
                        int[] imgData=((DataBufferInt) img.getRaster().getDataBuffer()).getData();
                        for (int i=0;i<blockBreite;i++){
                            int index = (y*blockBreite + i)*ClientSettings.PLAYERC_FIELD_OF_VIEW.x*blockBreite + x*blockBreite;
                            System.arraycopy(imgData,i*blockBreite,oldImageData,Math.min(index,oldImageData.length-blockBreite-1),blockBreite);
                        }
                    }
                }
            }
            
            Graphics2D g2=image.createGraphics();
            String[] chat=(String[]) new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getChatContent",String[].class,5).ret;
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(Font.SERIF,Font.PLAIN,12));
            for (int i=0;i<chat.length;i++){
                g2.drawString(chat[i],20,i*16+8);
            }
            
            g.setColor(new Color(0,0,0,1));
            g.drawImage(image,0,0,new Color(0,0,0,255),null);
            
            g.drawImage(texture, (screenSize.x-20)/2, (screenSize.y-32)/2,40, 64, null);
        }
    }

    public void repaint(){
        player.repaint();
    }
    
    public void synchronizeWithServer(){
        player.synchronizeWithServer();
    }
    
    public boolean isOnPlanet(){
        return onPlanet;
    }
    
    public int getSandboxIndex(){
        return sandboxIndex;
    }
}