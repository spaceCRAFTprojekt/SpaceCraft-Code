package client;

import util.geom.*;
import util.ImageTools;
import items.*;
import client.menus.*;
import menu.*;
import blocks.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;

import java.io.Serializable;
import java.io.ObjectStreamException;
// ich hab das mal geordnet ~ Müllmann

/**
 * der Craft-Teil eines Spielers
 * alle Variablen, die synchronisiert werden, müssen public sein
 * 
 * @History:
 * v0.5.2_AK
 */
public class PlayerC implements Serializable
{
    public static final long serialVersionUID=0L;
    public transient Timer timer; //public, damit er in logout() beendet werden kann
    
    /**
     * Breite eines Blocks in Pixeln
     */
    private int blockBreite = 32; //Hiermit präsentiere ich Ihnen die einzige deutsche Variablenbezeichnung im ganzen Spiel (geschätzt).
    private Player player;
    /**
     * Position
     */
    public VektorD pos;
    @Deprecated private VektorD hitbox = new VektorD(1,2);
    /**
     * Ein Teil der Welt, in der sich der Spieler befindet, wird gecacht und nur jede Sekunde aktualisiert. Das macht das ganze hoffentlich schneller.
     */
    public transient int[][] mapIDCache;
    /**
     * Position der oberen rechten Ecke des mapIDCaches (relativ zur oberen rechten Ecke der gesamten Map)
     */
    public transient VektorI mapIDCachePos;
    /**
     * Daten zu allen Subsandboxes der Sandbox, in der sich der Spieler gerade befindet (Index, Position, Geschwindigkeit)
     */
    public transient SandboxInSandbox[] subData;
    /**
     * MapIDCaches für jede Subsandbox
     */
    public transient int[][][] subMapIDCache;
    /**
     * MapIDCache-Positionen für jede Subsandbox
     */
    public transient VektorI[] subMapIDCachePos;

    public transient OverlayPanelC opC;
    public PlayerTexture playerTexture;
    public transient OtherPlayerTexturesPanel otherPlayerTexturesPanel;
    public transient DataPanel dataP;
    private PlayerInv inv;
    public transient Hotbar hotbar;

    public PlayerC(Player player, VektorD pos)
    {
        this.player = player;
        setSandbox(player.currentMassIndex, pos);
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
    
    /**
     * Diese Methode wird von Player.login aufgerufen.
     * Aus irgendeinem Grund hat nämlich der PlayerC einen Timer, aber der Player selbst nicht.
     */
    public void timerSetup(){
        if (player.onClient()){
            this.timer=new Timer("PlayerC-"+player.getID()+"-Timer");
            timer.schedule(new TimerTask(){
                    public void run(){
                        repaint();
                    }
                },0,ClientSettings.PLAYERC_TIMER_PERIOD);
            timer.schedule(new TimerTask(){
                    public void run(){
                        player.synchronizeWithServer(); //Variablen des Spielers
                        if (player.isOnline()){ //holt sich einen neuen MapIDCache
                            mapIDCache=(int[][]) (new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.getMapIDs",int[][].class,player.currentMassIndex,pos.toInt().subtract(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2)),pos.toInt().add(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2))).ret);
                            mapIDCachePos=pos.toInt().subtract(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2));
                            //Object[] ret = (Object[])(new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getOtherPlayerTextures",Object[].class,pos.toInt().subtract(ClientSettings.PLAYERC_FIELD_OF_VIEW),pos.toInt().add(ClientSettings.PLAYERC_FIELD_OF_VIEW)).ret);
                            //It was so nice, unknown did it twice. Zweimal der gleiche Request erscheint mir irgendwie sinnlos. -LG
                            otherPlayerTexturesPanel.repaint((Object[])(new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getOtherPlayerTextures",Object[].class,pos.toInt().subtract(ClientSettings.PLAYERC_FIELD_OF_VIEW),pos.toInt().add(ClientSettings.PLAYERC_FIELD_OF_VIEW)).ret));  // hier sehen Sie wie man ein Object in ein Object[] casten kann - Argh!
                            subData=(SandboxInSandbox[])(new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.getAllSubsandboxes",SandboxInSandbox[].class,player.currentMassIndex).ret);
                            subMapIDCache=new int[subData.length][ClientSettings.PLAYERC_MAPIDCACHE_SIZE.x][ClientSettings.PLAYERC_MAPIDCACHE_SIZE.y];
                            subMapIDCachePos=new VektorI[subData.length];
                            for (int i=0;i<subData.length;i++){
                                VektorD posRel=pos.subtract(subData[i].offset);
                                subMapIDCache[i]=(int[][]) (new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.getMapIDs",int[][].class,subData[i].index,posRel.toInt().subtract(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2)),posRel.toInt().add(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2))).ret);
                                subMapIDCachePos[i]=posRel.toInt().subtract(ClientSettings.PLAYERC_MAPIDCACHE_SIZE.divide(2));
                            }
                        }
                    }
                },0,ClientSettings.SYNCHRONIZE_REQUEST_PERIOD);
            timer.schedule(new TimerTask(){
                    public void run(){
                        if (player.getMenu() instanceof ManoeuvreInfo)
                            ((ManoeuvreInfo) player.getMenu()).update();
                    }
                },0,100);
        }
    }

    
    /**
     * Diese Methode wird von Player.makeFrame aufgerufen.
     */
    public void makeFrame(Frame frame){
        this.opC = frame.getOverlayPanelC();
        this.playerTexture.makeFrame(opC,player.getScreenSize(), getBlockWidth());
        this.otherPlayerTexturesPanel = new OtherPlayerTexturesPanel(opC, this, player.getScreenSize());
        // setup des Invs und der Hotbar
        setPlayerInv((PlayerInv)(new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getPlayerInv",PlayerInv.class).ret));
        this.dataP = new DataPanel(player.getScreenSize(), this, opC);
    }

    public void setupHotbar(){
        if(hotbar != null){ // wenn eine alte Hotbar da ist diese entfernen
            hotbar.setVisible(false);
            opC.remove(hotbar);
        }
        this.hotbar = new Hotbar(opC, inv, player.getScreenSize());   // wird automatisch dem Overlaypanel geadded
        inv.setHotbar(hotbar);
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
     * Setze den Spieler in einer andere Sandbox.
     */
    public void setSandbox(int sandboxIndex, VektorD pos){
        this.pos = pos;
        if (player.isOnline() && player.onClient()){
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
            //braucht eigentlich noch einen posInsideOfBounds request o.Ã„.
            switch(e.getKeyCode()){
                case Shortcuts.move_up: pos.y=pos.y - 0.5;
                break;
                case Shortcuts.move_down: pos.y=pos.y + 0.5;
                break;
                case Shortcuts.move_left: pos.x=pos.x - 0.5;  playerTexture.setMode(PlayerTexture.LEFT); synchronizePlayerTexture();
                break;
                case Shortcuts.move_right: pos.x=pos.x + 0.5;  playerTexture.setMode(PlayerTexture.RIGHT); synchronizePlayerTexture();
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
            VektorD sPos=getPosToPlayer(pos,clickPos,blockBreite); //Position in der Sandbox
            int sbIndex=getInteractSandboxIndex(sPos);
            VektorI cPos; // Position im mapIDCache
            if (sbIndex==-1) //Interaktion mit der Hauptsandbox
                cPos=getPosToCache(player.currentMassIndex,sPos);
            else{ //Interaktion mit einer Subsandbox
                cPos=getPosToCache(subData[sbIndex].index,sPos);
            }
            int[][] interactMapCache;
            if (sbIndex==-1)
                interactMapCache=mapIDCache;
            else
                interactMapCache=subMapIDCache[sbIndex];
            if (e.getButton() == e.BUTTON1){   // linksclick => abbauen
                    /** 
                     * ABBAUEN
                     */
                Block block=Blocks.get(interactMapCache[cPos.x][cPos.y]);
                if (block==null) return; // wenn da kein block ist => nichts machen
                if(block.breakment_prediction){
                    interactMapCache[cPos.x][cPos.y] = -1;
                    
                    if(block.drop_prediction && block.item != null){
                        if(block.drop == -1)getInv().addStack(new Stack(block.item, 1));
                        else{
                            Item dropItem = Items.get(block.drop);
                            if(dropItem != null)getInv().addStack(new Stack(dropItem, 1));
                        }
                    }
                }
                // wenn der Block wahrscheinlich zerstÃ¶rt werden kann wird er im cache entfernt. An den Server wird eine Anfrage gestellt, ob das geht, und 
                // fÃ¼r den Fall, dass es nicht geht, wird der Block bei der nÃ¤chsten synchronisierung wieder hergestellt
                if (sbIndex==-1)
                    new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.breakBlock",null,player.currentMassIndex,sPos.toInt());
                else
                    new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.breakBlock",null,subData[sbIndex].index,sPos.subtract(subData[sbIndex].offset).toInt());
            }else if (e.getButton() == e.BUTTON3){  // rechtsklick => platzieren oder rechtsklick
                if(interactMapCache[cPos.x][cPos.y] == -1){
                    /** 
                     * PLATZIEREN
                     */
                  
                    //System.out.println("Tried to place block at "+sPos.toString());
                    Stack hotStack = inv.getHotStack();
                    if(hotStack == null || hotStack.count < 1)return;
                    int blockID;
                    try{ 
                        blockID = ((BlockItem)(hotStack.getItem())).id; 
                    }
                    catch(Exception e1){return;}// => Craftitem
                    if(blockID == -1 || Blocks.get(blockID) == null) return;
                    if(Blocks.get(blockID).placement_prediction){
                        interactMapCache[cPos.x][cPos.y] = blockID;  
                        // wenn der Block wahrscheinlich platziert werden kann wird er im cache gesetzt. An den Server wird eine Anfrage gestellt, ob das geht, und 
                        // fÃ¼r den Fall, dass es nicht geht, wird der Block bei der nÃ¤chsten synchronisierung wieder entfernt
                        hotStack.setCount(hotStack.getCount() -1);
                        hotbar.updateSlots();
                    }
                    if (sbIndex==-1)
                        new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.placeBlock",null,player.getCurrentMassIndex(),sPos.toInt(), blockID);
                    else
                        new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.placeBlock",null,subData[sbIndex].index,sPos.subtract(subData[sbIndex].offset).toInt(), blockID);
                }else{
                    Block block = Blocks.get(interactMapCache[cPos.x][cPos.y]);
                    if(block instanceof SBlock){
                        if (sbIndex==-1)
                            new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.rightclickBlock",null,player.getCurrentMassIndex(),sPos.toInt());
                        else
                            new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Sandbox.rightclickBlock",null,subData[sbIndex].index,sPos.subtract(subData[sbIndex].offset).toInt());
                    }
                }
            }
        }
    }

    /**
     * Mausrad"Event"
     * @param:
     * irgend ein EventObjekt; Keine Ahnung was das kann
     */
    public void mouseWheelMoved(MouseWheelEvent e){
        try{
            hotbar.scrollDelta(e.getWheelRotation());
            //System.out.println(e.getWheelRotation());
        }catch(Exception ichhattekeinelustzuueberpruefenobhotbarnullist){}
    }

    
    // und die Methoden, die für diese Events gebraucht werden
    public void openInventory(){
        //Just for testing purpose ~unknown //Pourquoi parles-tu en anglais? ~LG // ???????????????????? ~unknown
        if (inv == null)return;

        new InventoryMenu(player, this.inv);
    }

    
    public PlayerInv getInv(){ //von LG zum Testen, auch wenn ich eigentlich keine Ahnung vom inv habe
        return inv;
    }

    /**
     * setzt das Inv ohne die Hotbar upzudaten (nur für den Server)
     */
    public void setInv(PlayerInv inv){
        this.inv = inv;
    }
    
    /**
     * setzt das Inv und updated die Hotbar (nur für den Client)
     */
    public void setPlayerInv(PlayerInv inv){
        this.inv = inv;
        setupHotbar();
        hotbar = inv.hotbar;
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
    /***********************************************************************************************************************************************************/

    /**
     * Gibt den Index der ersten Sandbox (im subMapIDCache-Array, also nicht im Space.masses-Array, oder -1 => Hauptsandbox) zurück, mit der der Spieler interagieren kann.
     * Bevorzugt wird immer eine Subsandbox.
     * Die Subsandboxen sollten sich also nicht überschneiden (außer mit der Hauptsandbox), sonst gibt es hier Probleme.
     * @param:
     * sPos: Position im allgemeinen Map Array (lässt sich mit getPosToPlayer() aus einer Klick-Position berechnen)
     */
    public int getInteractSandboxIndex(VektorD sPos){
        for (int i=0;i<subData.length;i++){
            VektorD posRel=sPos.subtract(subData[i].offset);
            if (posRel.x>=0 && posRel.y>=0 && posRel.x<subData[i].size.x && posRel.y<subData[i].size.y)
                return i;
        }
        return -1;
    }

    /***********************************************************************************************************************************************************
    /*********4. Methoden für Ansicht und Grafikausgabe*********************************************************************************************************
    /***********************************************************************************************************************************************************/


     /**
     * Gibt die obere linken Ecke (int Blöcken) der aktuellen Spieleransicht an
     */
    public VektorD getUpperLeftCorner(){
        return getUpperLeftCorner(pos);
    }

    /**
     * Gibt die obere linken Ecke (in Blöcken) der Spieleransicht an
     * @param: pos: Position des Spielers relativ zur oberen linken Ecke der Sandbox
     */
    public VektorD getUpperLeftCorner(VektorD pos){
        // 2.6.2019 AK: .subtract(new VektorD(0.5,0.5)) ist richtig. (Stichwort obere linke ecke des Blocks & Rundung)
        return pos.add(ClientSettings.PLAYERC_FIELD_OF_VIEW.toDouble().multiply(-0.5).subtract(new VektorD(0.5,0.5)) );
    }

    /**
     * gibt die Position eines Blocks (erstmal noch als VektorD, falls noch ein VektorD-Subsandbox-Offset dazuaddiert werden muss)
     * 
     * @param: 
     * posRel: Position des Spielers relativ zu der Sandbox, mit der er interagiert
     * bPos: Position des Klicks
     * blockBreite: Breite eines Blocks in Pixeln
     */
    public VektorD getPosToPlayer(VektorD posRel, VektorI bPos, int blockBreite){
        //System.out.println(bPos.toString()+" "+bPos.toDouble().divide(blockBreite).toString());
        return (getUpperLeftCorner(posRel).add(bPos.toDouble().divide(blockBreite)));
    }

    /**
     * gibt die Position eines Blocks im Cache-Array an
     * 
     * @param: 
     * sandboxIndex: Index der Sandbox, mit der der Spieler interagiert, im Space.masses-Array, im Normalfall player.currentMassIndex
     * sPos: Position im allgemeinen Map Array (lässt sich mit getPosToPlayer() berechnen)
     */
    public VektorI getPosToCache(int sandboxIndex, VektorD sPos){
        if (sandboxIndex==player.currentMassIndex)
            return sPos.subtract(mapIDCachePos.toDouble()).toInt();
        else{
            for (int i=0;i<subData.length;i++){
                if (subData[i].index==sandboxIndex){
                    return sPos.subtract(subMapIDCachePos[i].toDouble()).subtract(subData[i].offset).toInt();
                }
            }
        }
        return null;
    }

    /**
     * Zeichnen
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
                int drawX=(int) ((minX-pos.x+((fieldOfView.x)/2))*blockBreite); //es wird nur ein Teil des Bilds gezeichnet (ein Rechteck von (drawX|drawY) mit Breite width und Höhe height
                int drawY=(int) ((minY-pos.y+((fieldOfView.y)/2))*blockBreite);
                int width=(int) (fieldOfView.x*blockBreite);
                int height=(int) (fieldOfView.y*blockBreite);

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
                //Zeichnen von Subsandboxen, recht Ã¤hnlich zu dem Zeichnen der Sandbox oberhalb
                if (subData!=null && subMapIDCache!=null && subMapIDCachePos!=null){
                    for (int i=0;i<subData.length;i++){
                        SandboxInSandbox sd=subData[i];
                        int[][] smic=subMapIDCache[i];
                        VektorI smicp=subMapIDCachePos[i];
                        if (sd!=null && smic!=null && smicp!=null){
                            VektorD posRel=pos.subtract(sd.offset);

                            int minXSub=(int) Math.floor(posRel.x-fieldOfView.x/2)-1;
                            int maxXSub=(int) Math.ceil(posRel.x+fieldOfView.x/2)+1;
                            int minYSub=(int) Math.floor(posRel.y-fieldOfView.y/2)-1;
                            int maxYSub=(int) Math.ceil(posRel.y+fieldOfView.y/2)+1;
                            ColorModel subCm=ColorModel.getRGBdefault();
                            BufferedImage subImage=new BufferedImage(subCm,subCm.createCompatibleWritableRaster((maxXSub-minXSub)*blockBreite,(maxYSub-minYSub)*blockBreite),false,new Hashtable<String,Object>());
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
                                            subBlockImages.put(smic[x][y],new BufferedImage(cm,cm.createCompatibleWritableRaster(blockBreite,blockBreite),false,properties));
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
                            
                            //Zeichnen dieses Subsandbox-Bilds auf das allgemeine Bild
                            int drawXSub=(int) ((minXSub-posRel.x+((fieldOfView.x)/2))*blockBreite);
                            int drawYSub=(int) ((minYSub-posRel.y+((fieldOfView.y)/2))*blockBreite);
                            int widthSub=(int) (fieldOfView.x*blockBreite);
                            int heightSub=(int) (fieldOfView.y*blockBreite);
                            subImage=subImage.getSubimage(-drawXSub,-drawYSub,widthSub,heightSub);
                            g2.drawImage(subImage,-drawX,-drawY,new Color(255,255,255,0),null); //keine Ahnung warum -drawX, -drawY, aber es geht
                            
                            //roter Rahmen um die Subsandbox, damit man ihre Grenzen sehen kann
                            g2.setColor(Color.RED);
                            g2.drawRect((int) ((sd.offset.x-minX)*blockBreite),(int) ((sd.offset.y-minY)*blockBreite),sd.size.x*blockBreite,sd.size.y*blockBreite);
                            g2.setColor(Color.BLACK);
                        }
                    }
                }

                g.setColor(new Color(0,0,0,1));
                Color background = new Color(180,230,255,255);// hier kann der Hintergrund verÃ¤ndert werden
                //System.out.println(drawX+" "+drawY+" "+width+" "+height+" "+image.getWidth()+" "+image.getHeight());
                image=image.getSubimage(-drawX,-drawY,width,height);
                g.drawImage(image,0,0,background,null);
            }
            //DataPanel wird upgedated v0.3.13_MH
            dataP.update();
        }
    }
    
    public void repaint(){
        player.repaint();
    }

    public void synchronizeWithServer(){
        player.synchronizeWithServer();
    }

}