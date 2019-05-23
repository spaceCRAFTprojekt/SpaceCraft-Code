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
import menu.MenuSettings;
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
    @Deprecated private VektorD hitbox = new VektorD(1,2);
    public transient int[][] mapIDCache;
    public transient VektorI mapIDCachePos; //Position der oberen rechten Ecke des mapIDCaches (relativ zur oberen rechten Ecke der gesamten Map)
    public transient SubsandboxTransferData[] subData;
    public transient int[][][] subMapIDCache;
    public transient VektorI[] subMapIDCachePos;
    //Verschoben in OtherPlayerTexturePanel: public transient Object[] playerTextureCache = null;  // Es sind Objekte der Klasse OtherPlayerTexture. Ich kann die sch***e nicht in in ein OtherPlayerTexture[] casten!!!
    
    public transient OverlayPanelC opC;
    public PlayerTexture playerTexture;
    public transient OtherPlayerTexturesPanel otherPlayerTexturesPanel;
    
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
        subData=null;
        subMapIDCache=null;
        subMapIDCachePos=null;
        //playerTextureCache = null;
        //PlayerTexture
        playerTexture = new PlayerTexture(0);
    }

    private void makeTexture(){

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
                        if (player.isOnline()){
                            mapIDCache=(int[][]) (new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.getMapIDs",int[][].class,onPlanet,sandboxIndex,pos.toInt().subtract(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2)),pos.toInt().add(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2))).ret);
                            mapIDCachePos=pos.toInt().subtract(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2));
                            Object[] ret = (Object[])(new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getOtherPlayerTextures",Object[].class,pos.toInt().subtract(ClientSettings.PLAYERC_FIELD_OF_VIEW),pos.toInt().add(ClientSettings.PLAYERC_FIELD_OF_VIEW)).ret);
                            otherPlayerTexturesPanel.repaint((Object[])(new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getOtherPlayerTextures",Object[].class,pos.toInt().subtract(ClientSettings.PLAYERC_FIELD_OF_VIEW),pos.toInt().add(ClientSettings.PLAYERC_FIELD_OF_VIEW)).ret));  // hier sehen Sie wie man ein Object in ein Object[] casten kann - Argh!
                            subData=(SubsandboxTransferData[])(new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.getAllSubsandboxTransferData",SubsandboxTransferData[].class,onPlanet,sandboxIndex).ret);
                            subMapIDCache=new int[subData.length][ClientSettings.PLAYERC_MAPIDCACHE_SIZE.x][ClientSettings.PLAYERC_MAPIDCACHE_SIZE.y];
                            subMapIDCachePos=new VektorI[subData.length];
                            for (int i=0;i<subData.length;i++){
                                VektorD posRel=pos.subtract(subData[i].offset);
                                subMapIDCache[i]=(int[][]) (new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.getMapIDs",int[][].class,subData[i].isPlanet,subData[i].index,posRel.toInt().subtract(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2)),posRel.toInt().add(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2))).ret);
                                subMapIDCachePos[i]=posRel.toInt().subtract(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2));
                            }
                        }
                    }
                },0,1000);
        }
    }
    
    public void makeFrame(Frame frame){
        this.opC = frame.getOverlayPanelC();
        this.playerTexture.makeFrame(opC,player.getScreenSize(), getBlockWidth());
        this.otherPlayerTexturesPanel = new OtherPlayerTexturesPanel(opC, this, player.getScreenSize());
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
                case Shortcuts.move_up:if(mapIDCache[getPosToCache(pos).toInt().x][getPosToCache(pos).toInt().y-1]==-1){ pos.y=pos.y - 0.5;}
                break;
                case Shortcuts.move_down:if(mapIDCache[getPosToCache(pos).toInt().x][getPosToCache(pos).toInt().y+1]==-1){ pos.y=pos.y + 0.5;}
                break;
                case Shortcuts.move_left:if(mapIDCache[getPosToCache(pos).toInt().x-1][getPosToCache(pos).toInt().y]==-1){ pos.x=pos.x - 0.5;  playerTexture.setMode(PlayerTexture.LEFT); synchronizePlayerTexture();}
                break;
                case Shortcuts.move_right:if(mapIDCache[getPosToCache(pos).toInt().x+1][getPosToCache(pos).toInt().y]==-1){ pos.x=pos.x + 0.5;  playerTexture.setMode(PlayerTexture.RIGHT); synchronizePlayerTexture();}
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
    
    public PlayerInv getInv(){
        return inv; //von LG zum Testen, auch wenn ich eigentlich keine Ahnung vom inv habe
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
    
    /**
     * geht nicht oder doch?
     */
    public void synchronizePlayerTexture(){
        new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerCVariable",null,"playerTexture",PlayerTexture.class,playerTexture);
    }


    
    /***********************************************************************************************************************************************************
    /*********3. Methoden für Subsandboxes und Raketenstart*****************************************************************************************************
    /***********************************************************************************************************************************************************

    /***********************************************************************************************************************************************************
    /*********4. Methoden für Ansicht und Grafikausgabe*********************************************************************************************************
    /***********************************************************************************************************************************************************

     /**
     * Gibt die obere linken Ecke (int Blöcken) der aktuellen Spieleransicht an
     */
    public VektorD getUpperLeftCorner(){
        return getUpperLeftCorner(pos);
    }
   
    /**
     * Gibt die obere linken Ecke (int Blöcken) der Spieleransicht an
     * @param: pos: Position des Spielers relativ zur oberen linken Ecke der Sandbox
     */
    public VektorD getUpperLeftCorner(VektorD pos){
        // das -0.5 ist eine hässliche Lösung von issue #26. Ich hab kleine Ahnung warum es geht aber es geht...
        return pos.add(ClientSettings.PLAYERC_FIELD_OF_VIEW.toDouble().multiply(-0.5) );
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
        return (getUpperLeftCorner(pos).add(bPos.toDouble().divide(blockBreite))).toInt();
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
    public VektorD getPosToCache(VektorD sPos){
        return sPos.subtract(mapIDCachePos.toDouble());
    }
    /**
     * Grafik ausgeben
     */
    public void paint(Graphics g, VektorI screenSize){
        //Request r = new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getOtherPlayerTextures",Object[].class);
        //playerTextureCache = (Object[])(new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getOtherPlayerTextures",Object[].class).ret);
        if (player.isOnline() && player.onClient()){
            if (mapIDCache!=null && mapIDCachePos!=null){
                VektorD fieldOfView=ClientSettings.PLAYERC_FIELD_OF_VIEW.toDouble();
                int minX=(int) Math.floor(pos.x-fieldOfView.x/2)-1; //keine Ahnung, warum die -1 und +1
                int maxX=(int) Math.ceil(pos.x+fieldOfView.x/2)+1;
                int minY=(int) Math.floor(pos.y-fieldOfView.y/2)-1;
                int maxY=(int) Math.ceil(pos.y+fieldOfView.y/2)+1;
                ColorModel cm=ColorModel.getRGBdefault();
                BufferedImage image=new BufferedImage(cm,cm.createCompatibleWritableRaster((maxX-minX)*blockBreite,(maxY-minY)*blockBreite),false,new Hashtable<String,Object>());
                //alle hier erstellten BufferedImages haben den TYPE_INT_ARGB
                int[] oldImageData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    
                Hashtable<Integer,BufferedImage> blockImages=new Hashtable<Integer,BufferedImage>(); //Skalierung
                for (int x = 0; x<=ClientSettings.PLAYERC_MAPIDCACHE_SIZE.x; x++){
                    for (int y = 0; y<=ClientSettings.PLAYERC_MAPIDCACHE_SIZE.y; y++){
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
                for (int x = minX ; x<=maxX; x++){
                    for (int y = minY; y<=maxY; y++){
                        try{
                            int id = mapIDCache[x-mapIDCachePos.x][y-mapIDCachePos.y];
                            if(id != -1){ //Luft
                                BufferedImage img=blockImages.get(id);
                                if (img!=null){
                                    int[] imgData=((DataBufferInt) img.getRaster().getDataBuffer()).getData();
                                    for (int i=0;i<blockBreite;i++){
                                        int index = ((y-minY)*blockBreite + i)*(maxX-minX)*blockBreite + (x-minX)*blockBreite;
                                        //((y-mapIDCachePos.y)*blockBreite + i)*ClientSettings.PLAYERC_FIELD_OF_VIEW.x*blockBreite + (x-mapIDCachePos.x)*blockBreite;
                                        System.arraycopy(imgData,i*blockBreite,oldImageData,index,blockBreite);
                                    }
                                }
                            }
                        }
                        catch(ArrayIndexOutOfBoundsException e){}
                    }
                }
                
                Graphics2D g2=image.createGraphics();
                //Zeichnen von Subsandboxen, recht ähnlich zu dem Zeichnen der Sandbox oberhalb
                if (subData!=null && subMapIDCache!=null && subMapIDCachePos!=null){
                    for (int i=0;i<subData.length;i++){
                        SubsandboxTransferData sd=subData[i];
                        int[][] smic=subMapIDCache[i];
                        VektorI smicp=subMapIDCachePos[i];
                        if (sd!=null && smic!=null && smicp!=null){
                            VektorD posRel=pos.subtract(sd.offset);
                                        
                            int minXSub=(int) Math.floor(posRel.x-fieldOfView.x/2)-1;
                            int maxXSub=(int) Math.ceil(posRel.x+fieldOfView.x/2)+1;
                            int minYSub=(int) Math.floor(posRel.y-fieldOfView.y/2)-1;
                            int maxYSub=(int) Math.ceil(posRel.y+fieldOfView.y/2)+1;
                            ColorModel subCm=ColorModel.getRGBdefault();
                            BufferedImage subImage=new BufferedImage(subCm,subCm.createCompatibleWritableRaster((maxX-minX)*blockBreite,(maxY-minY)*blockBreite),false,new Hashtable<String,Object>());
                            int[] oldSubImageData = ((DataBufferInt) subImage.getRaster().getDataBuffer()).getData();
                
                            Hashtable<Integer,BufferedImage> subBlockImages=new Hashtable<Integer,BufferedImage>();
                            for (int x = 0; x<=ClientSettings.PLAYERC_MAPIDCACHE_SIZE.x; x++){
                                for (int y = 0; y<=ClientSettings.PLAYERC_MAPIDCACHE_SIZE.y; y++){
                                    try{
                                        BufferedImage img=Blocks.getTexture(smic[x][y]);
                                        if (img!=null){
                                            Hashtable<String,Object> properties=new Hashtable<String,Object>();
                                            String[] prns=image.getPropertyNames();
                                            if (prns!=null){
                                                for (int j=0;j<prns.length;j++){
                                                    properties.put(prns[j],image.getProperty(prns[j]));
                                                }
                                            }
                                            BufferedImage img2=new BufferedImage(cm,cm.createCompatibleWritableRaster(blockBreite,blockBreite),false,properties);
                                            Graphics gr=img2.getGraphics();
                                            gr.drawImage(img,0,0,blockBreite,blockBreite,null);
                                            subBlockImages.put(smic[x][y],img2);
                                        }
                                        else{
                                            Hashtable<String,Object> properties=new Hashtable<String,Object>();
                                            blockImages.put(smic[x][y],new BufferedImage(cm,cm.createCompatibleWritableRaster(blockBreite,blockBreite),false,properties));
                                        }
                                    }
                                    catch(ArrayIndexOutOfBoundsException e){}
                                }
                            }
                            for (int x = minXSub ; x<=maxXSub; x++){
                                for (int y = minYSub; y<=maxYSub; y++){
                                    try{
                                        int id = smic[x-smicp.x][y-smicp.y];
                                        if(id != -1){
                                            BufferedImage img=subBlockImages.get(id);
                                            if (img!=null){
                                                int[] imgData=((DataBufferInt) img.getRaster().getDataBuffer()).getData();
                                                for (int j=0;j<blockBreite;j++){
                                                    int index = ((y-minYSub)*blockBreite + j)*(maxXSub-minXSub)*blockBreite + (x-minXSub)*blockBreite;
                                                    System.arraycopy(imgData,j*blockBreite,oldSubImageData,index,blockBreite);
                                                }
                                            }
                                        }
                                    }
                                    catch(ArrayIndexOutOfBoundsException e){}
                                }
                            }
                            g2.drawImage(subImage,0,0,new Color(255,255,255,0),null); //unfertig
                        }
                    }
                }
                
                //Chat
                String[] chat=(String[]) new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getChatContent",String[].class,5).ret;
                g2.setColor(Color.WHITE);
                g2.setFont(MenuSettings.MENU_FONT);
                for (int i=0;i<chat.length;i++){
                    g2.drawString(chat[i],20,i*16+8);
                }
                
                g.setColor(new Color(0,0,0,1));
                Color background = new Color(180,230,255,255);// hier kann der Hintergrund verändert werden
                int drawX=(int) ((minX-pos.x+((fieldOfView.x)/2))*blockBreite);
                int drawY=(int) ((minY-pos.y+((fieldOfView.y)/2))*blockBreite);
                int width=(int) (fieldOfView.x*blockBreite);
                int height=(int) (fieldOfView.y*blockBreite);
                //System.out.println(drawX+" "+drawY+" "+width+" "+height+" "+image.getWidth()+" "+image.getHeight());
                image=image.getSubimage(-drawX,-drawY,width,height);
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