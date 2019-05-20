package client;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import util.geom.*;
import items.*;
import client.menus.*;
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
import blocks.*;
/**
 * ein Spieler in der Craft Ansicht
 */
public class PlayerC implements Serializable
{
    public static final long serialVersionUID=0L;
    //alle Variablen, die synchronisiert werden, müssen public sein
    private transient Timer timer;

    private int blockBreite = 32;  // Breite eines Blocks in Pixeln
    private Player player;
    public VektorD pos;
    public boolean onPlanet; //sonst: auf einem Schiff
    public int sandboxIndex; //entweder im ShipCs-Array oder im PlanetCs-Array der Index der Sandbox, in der sich der PlayerC gerade befindet
    private transient BufferedImage texture;
    private VektorD hitbox = new VektorD(1,2);
    public int[][] mapIDCache;
    public VektorI mapIDCachePos; //Position der oberen rechten Ecke des mapIDCaches (relativ zur oberen rechten Ecke der gesamten Map)
    public transient Object[] playerTextureCache = null;  // Es sind Objekte der Klasse OtherPlayerTexture. Ich kann die sch***e nicht in in ein OtherPlayerTexture[] casten!!!
    
    public transient OverlayPanelC opC;
    public PlayerTexture playerTexture;
    
    private PlayerInv inv;
    
    
    public PlayerC(Player player, boolean onPlanet, int sandboxIndex, VektorD pos)
    {
        this.player = player;
        setSandbox(onPlanet, sandboxIndex, pos);
        makeTexture();
        //muss man hier auch schon synchronisieren?  ka ~ unknown
        
        // Inventar:
        inv = new PlayerInv();
        mapIDCache=null;
        mapIDCachePos=null;
        playerTextureCache = null;
        //PlayerTexture
        playerTexture = new PlayerTexture(0);
    }

    private void makeTexture(){
        texture = ImageTools.get('C',"player_texture");
    }

    public void timerSetup(){ //wird von Player.login aufgerufen
        if (player.onClient()){
            this.timer=new Timer();
            timer.schedule(new TimerTask(){
                    public void run(){
                        repaint();
                    }
                },0,ClientSettings.PLAYERC_TIMER_PERIOD);
            timer.schedule(new TimerTask(){
                    public void run(){
                        synchronizeWithServer();
                    }
                },0,ClientSettings.SYNCHRONIZE_REQUEST_PERIOD);
            timer.schedule(new TimerTask(){
                    public void run(){
                        mapIDCache=(int[][]) (new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.getMapIDs",int[][].class,onPlanet,sandboxIndex,pos.toInt().subtract(ClientSettings.PLAYERC_FIELD_OF_VIEW),pos.toInt().add(ClientSettings.PLAYERC_FIELD_OF_VIEW)).ret);
                        mapIDCachePos=pos.toInt().subtract(ClientSettings.PLAYERC_FIELD_OF_VIEW);
                        playerTextureCache = (Object[])(new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getOtherPlayerTextures",Object[].class).ret);  // hier sehen Sie wie man ein Object in ein Object[] casten kann - Argh!
                    }
                },0,1000);
        }
    }
    
    public void makeFrame(Frame frame){
        this.opC = frame.getOverlayPanelC();
        this.playerTexture.makeFrame(opC,player.getScreenSize(), getBlockWidth());
    }

    Object readResolve() throws ObjectStreamException{
        if (player.onClient()){
            this.makeTexture();
            if (player.isOnline())
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
                case Shortcuts.move_left: pos.x=pos.x - 1;  playerTexture.setMode(PlayerTexture.LEFT);
                break;
                case Shortcuts.move_right: pos.x=pos.x + 1;  playerTexture.setMode(PlayerTexture.RIGHT);
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
            if (!player.isOnline() || !player.onClient())return;
            VektorI clickPos = new VektorI(e);  // Position in Pixeln am Bildschirm
            VektorI sPos=getPosToPlayer(clickPos,blockBreite);  //Position in der Sandbox
            VektorI cPos=getPosToCache(sPos);  // Position im mapIDCache
            if (e.getButton() == e.BUTTON1){   // linksclick => abbauen
                //System.out.println("Tried to break block at "+sPos.toString());
                if(mapIDCache[cPos.x][cPos.y] == -1)return;  // wenn da kein block ist => nichts machen
                if(Blocks.get(mapIDCache[cPos.x][cPos.y]).breakment_prediction){
                    mapIDCache[cPos.x][cPos.y] = -1;  
                }
                // wenn der Block wahrscheinlich zerstört werden kann wird er im cache entfernt. An den Server wird eine Anfrage gestellt, ob das geht, und 
                // für den Fall, dass es nicht geht, wird der Block bei der nächsten synchronisierung wieder hergestellt
                new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.breakBlock",Boolean.class,onPlanet,sandboxIndex,sPos);
                
            }else if (e.getButton() == e.BUTTON3){  // rechtsklick => platzieren oder rechtsklick
                if(mapIDCache[cPos.x][cPos.y] == -1){
                    //plazieren
                    //System.out.println("Tried to place block at "+sPos.toString());
                    int blockID = getHotbarBlockID();
                    if(blockID == -1 || Blocks.get(blockID) == null) return;
                    if(Blocks.get(blockID).placement_prediction){
                        mapIDCache[cPos.x][cPos.y] = blockID;  
                        // wenn der Block wahrscheinlich plaziert werden kann wird er im cache gesetzt. An den Server wird eine Anfrage gestellt, ob das geht, und 
                        // für den Fall, dass es nicht geht, wird der Block bei der nächsten synchronisierung wieder entfernt
                    }
                    new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.placeBlock",Boolean.class,onPlanet,sandboxIndex,sPos, blockID);      
                }else{
                    //leftclick
                    //System.out.println("Tried to leftclick block at "+sPos.toString());
                    Block block = Blocks.get(mapIDCache[cPos.x][cPos.y]);
                    if(block instanceof SBlock){
                        new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.rightclickBlock",Boolean.class,onPlanet,sandboxIndex,sPos);      
                    }
                }
            }
        }
    }
    
    // und die Methoden, die für diese Events gebraucht werden
    public void openInventory(){
        //Just for testing purpose ~unknown //Pourquoi parles-tu en anglais? ~LG // よく私は知らない、よくあなたが知っている ~unknown
        if (inv == null)return;
        /*
        inv.addStack(new Stack(new CraftItem(1, "", BlocksC.images.get(1)),99));
        inv.setStack(new VektorI(3,3),new Stack(new CraftItem(1, "", BlocksC.images.get(1)),90));
        inv.setStack(new VektorI(7,3),new Stack(new CraftItem(1, "", BlocksC.images.get(1)),34));
        inv.addStack(new Stack(new CraftItem(2, "", BlocksC.images.get(2)),34));
        inv.addStack(new Stack(new CraftItem(0, "", BlocksC.images.get(0)),34));
        */
        new InventoryMenu(player, this.inv);
    }
    
    public int getHotbarBlockID(){
        return 300;
    }
    
    public int getBlockWidth(){
        return blockBreite;  // Naming on point !!
    }

    public void setPlayerTexture(int id){
        playerTexture.setTexture(id);
    }
    
    public PlayerTexture getPlayerTexture(){
        return playerTexture;
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
        // das -0.5 ist eine hässliche Lösung von issue #26. Ich hab kleine Ahnung warum es geht aber es geht...
        return pos.add(ClientSettings.PLAYERC_FIELD_OF_VIEW.toDouble().multiply(-0.5) ).add(new VektorD(0.5,-0.5));
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
     * Gibt die Position eines Blocks in cache Array an
     * 
     * @param: 
     * sPos: Position im allgemeinen Map Array (lässt sich mit getPosToPlayer() berechnen)
     */
    public VektorI getPosToCache(VektorI sPos){
        return sPos.subtract(mapIDCachePos);
    }
        
    public void updateOtherPlayerTextures(){
        for(int i = 0; i < playerTextureCache.length; i++){
            
        }
    }
    
    /**
     * Grafik ausgeben
     */
    public void paint(Graphics g, VektorI screenSize){
        //Request r = new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getOtherPlayerTextures",Object[].class);
        //playerTextureCache = (Object[])(new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getOtherPlayerTextures",Object[].class).ret);
        if (player.isOnline() && player.onClient()){
            if (mapIDCache!=null && mapIDCachePos!=null){
                VektorI upperLeftCorner = getUpperLeftCorner(pos).toInt();  // obere linke Ecke der Spieleransicht relativ zur oberen linken Ecke der sb
                VektorI bottomRightCorner = upperLeftCorner.add(ClientSettings.PLAYERC_FIELD_OF_VIEW);  // untere rechte Ecke der Spieleransicht relativ zur oberen linken Ecke der sb
                //System.out.println("UpperLeftCorner: "+ upperLeftCorner.toString()+ " BottomRightCorner: " + bottomRightCorner.toString());
                ColorModel cm=ColorModel.getRGBdefault();
                BufferedImage image=new BufferedImage(cm,cm.createCompatibleWritableRaster(ClientSettings.PLAYERC_FIELD_OF_VIEW.x*blockBreite,ClientSettings.PLAYERC_FIELD_OF_VIEW.y*blockBreite),false,new Hashtable<String,Object>());
                //alle hier erstellten BufferedImages haben den TYPE_INT_ARGB
                int[] oldImageData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    
                Hashtable<Integer,BufferedImage> blockImages=new Hashtable<Integer,BufferedImage>(); //Skalierung
                for (int x = 0; x<=ClientSettings.PLAYERC_FIELD_OF_VIEW.x*2; x++){
                    for (int y = 0; y<=ClientSettings.PLAYERC_FIELD_OF_VIEW.y*2; y++){
                        try{
                            BufferedImage img=Blocks.getTexture(mapIDCache[x][y]);
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
                                blockImages.put(mapIDCache[x][y],img2);
                            }
                            else{
                                Hashtable<String,Object> properties=new Hashtable<String,Object>();
                                blockImages.put(mapIDCache[x][y],new BufferedImage(cm,cm.createCompatibleWritableRaster(blockBreite,blockBreite),false,properties));
                            }
                        }
                        catch(ArrayIndexOutOfBoundsException e){}
                    }
                }
        
                for (int x = (int) pos.x-ClientSettings.PLAYERC_FIELD_OF_VIEW.x/2; x<=(int) pos.x+ClientSettings.PLAYERC_FIELD_OF_VIEW.x/2; x++){
                    for (int y = (int) pos.y-ClientSettings.PLAYERC_FIELD_OF_VIEW.y/2; y<=(int) pos.y+ClientSettings.PLAYERC_FIELD_OF_VIEW.y/2; y++){
                        try{
                            int id = mapIDCache[x-mapIDCachePos.x][y-mapIDCachePos.y];
                            if(id != -1){ //Luft
                                BufferedImage img=blockImages.get(id);
                                if (img!=null){
                                    int[] imgData=((DataBufferInt) img.getRaster().getDataBuffer()).getData();
                                    for (int i=0;i<blockBreite;i++){
                                        int index = ((y-(int) pos.y+ClientSettings.PLAYERC_FIELD_OF_VIEW.y/2)*blockBreite + i)*ClientSettings.PLAYERC_FIELD_OF_VIEW.x*blockBreite + (x-(int) pos.x+ClientSettings.PLAYERC_FIELD_OF_VIEW.x/2)*blockBreite;
                                        //((y-mapIDCachePos.y)*blockBreite + i)*ClientSettings.PLAYERC_FIELD_OF_VIEW.x*blockBreite + (x-mapIDCachePos.x)*blockBreite;
                                        System.arraycopy(imgData,i*blockBreite,oldImageData,Math.min(index,oldImageData.length-blockBreite-1),blockBreite);
                                    }
                                }
                            }
                        }
                        catch(ArrayIndexOutOfBoundsException e){}
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
                Color background = new Color(180,230,255,255);// hier kann der Hintergrund verändert werden
                g.drawImage(image,0,0,background,null);
            }
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