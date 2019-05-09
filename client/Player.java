package client;
import java.util.ArrayList;
import java.util.HashMap;
import geom.*;
import menu.*;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
/**
 * Ein Spieler
 * Kann entweder in der Craft oder in der Space Ansicht sein
 */
public class Player implements Serializable
{
    //alle Variablen, die synchronisiert werden müssen, müssen public sein
    private String name;
    private int id; //zum Senden von Daten, um ihn eindeutig zu identifizieren, Index in der server.Main.players-ArrayList
    private transient TaskResolver tr;
    private transient boolean online = false;  // aktuell ob der Frame des Spielers gerade offen ist
    //Warum ist das transient? Ich fände es sehr sinnvoll, das zu serialisieren. -LG
    private boolean onClient;
    public boolean inCraft = true;
    private transient Frame frame;
    private PlayerS playerS;
    private PlayerC playerC;
    private transient Menu openedMenu = null;  // wenn ein Menu (z.B.: Escape Menu; ChestInterface gerade offen ist)
    public int currentMassIndex;
    //transiente Variablen werden nicht synchronisiert
    
    /**
     * Erstellt neuen Spieler in einem Weltraum
     * 
     * @param:
     * int id: Index in der Playerliste in Main
     * String name: Name des Players
     * boolean onClient: ob der Player sich im Client befindet (ob also er synchronisiert wird oder nicht)
     * Sollte nicht verwendet werden (stattdessen static newPlayer(String name)), außer man weiß, was man tut.
     */
    public Player(int id, String name, boolean onClient)
    {
        this.id=id;
        this.name = name;
        this.onClient=onClient;
        this.currentMassIndex=0;
        //der Spawnpunkt muss nochmal überdacht werden
        if (onClient){
            taskResolverSetup();
        }
        this.playerS=new PlayerS(this,new VektorD(0,0),currentMassIndex);
        this.playerC=new PlayerC(this,true,currentMassIndex,new VektorD(50,50),frame);
        //muss man hier auch schon synchronisieren?
    }
    
    public static Player newPlayer(String name){
        int id=((Integer) new Request(-1,"Main.newPlayer",Integer.class,name).ret).intValue(); //Kopie des Players am Server
        if (id!=-1)
            return new Player(id,name,true); //Player hier am Client
        return null;
    }
    
    private void makeFrame(){ //Frame-Vorbereitung (Buttons, Listener etc.) nur hier
        this.frame = new Frame(name,new VektorI(928,608),this);
        Listener l=new Listener(this);
        this.frame.addKeyListener(l);
        this.frame.addMouseListener(l);
        this.frame.addMouseMotionListener(l);
        this.frame.addWindowListener(l);
        this.frame.addMouseWheelListener(l);
    }
    
    public void disposeFrame(){
        if (frame == null)return;
        this.frame.dispose();
        frame = null;
    }
    
    public Object readResolve() throws ObjectStreamException{
        if (online)
            this.makeFrame();
        this.taskResolverSetup();
        return this;
    }
    
    public void taskResolverSetup(){
        this.tr=new TaskResolver(this);
        Task.tasks=new ArrayList<Task>(); //nicht gut, wenn alle die gleiche ArrayList verwenden
    }

    /**
     * gibt den Namen des Spielers zurück
     */
    public String getName(){
        return name;
    }
    
    public int getID(){
        return id;
    }
    
    public boolean onClient(){
        return onClient;
    }
    
    public int getCurrentMassIndex(){
        return currentMassIndex;
    }
    
    public void setCurrentMassIndex(int cmi){
        currentMassIndex=cmi;
        if (onClient)
            new Request(id,"Main.synchronizePlayerVariable",null,"currentMassIndex",Integer.class, cmi);
    }
    
    public void login(){
        if(online)return;
        Boolean success=(Boolean) (new Request(id,"Main.login",Boolean.class).ret);
        if (success){
            synchronizeWithServer();
            this.online = true;
            makeFrame();
        }
        else{
            System.out.println("No success when trying to log in");
        }
        retrieveBlockImages();
    }
    
    public void logout(){
        if(!online)return;
        Boolean success=(Boolean) (new Request(id,"Main.logout",Boolean.class).ret);
        if (success){
            closeMenu();
            this.online = false;
            disposeFrame();
            //Boolean exited=(Boolean) (new Request(id,"Main.exitIfNoPlayers",Boolean.class).ret);
        }
        else{
            System.out.println("No success when trying to log out");
        }
    }
    
    /**
     * Wenn Main exited, dann werden alle Player rausgeschmissen (Es werden keine Requests mehr gestellt im Vergleich zu logout()).
     */
    public void logoutTask(){
        if (!online)return;
        closeMenu();
        this.online=false;
        disposeFrame();
    }
    
    public boolean isOnline(){
        return online;
    }
    
    public void setOnline(boolean b){ //wird nur von der Kopie des Players im Server verwendet, der Player im Client macht das in login() und logout()
        this.online=b;
    }
    
    /**
     * Wechselt die Ansicht zur Space Ansicht
     */
    public void toSpace()
    {
        if (!inCraft)return; // wenn der Spieler schon in der Space Ansicht ist, dann wird nichts getan
        inCraft = false;
        if (onClient)
            new Request(id,"Main.synchronizePlayerVariable",null,"inCraft",Boolean.class, inCraft);
        repaint();
    }
    
    /**
     * Wechselt die Ansicht zur Space Ansicht
    */
    public void toCraft()
    {
        if (inCraft)return; // wenn der Spieler schon in der Craft Ansicht ist, dann wird nichts getan
        inCraft = true;
        if (onClient)
            new Request(id,"Main.synchronizePlayerVariable",null,"inCraft", Boolean.class, inCraft);
        repaint();
    }
    
    /**
     * Wechselt die Ansicht zur anderen Ansicht
     */
    public void changePlayer()
    {
        if (inCraft) toSpace(); else toCraft();
    }
    
    /**
     * soll aufgerufen werden, wenn ein Fenster geöffnet wird
     */
    public void openMenu(Menu menu)
    {
        this.openedMenu = menu;
    }
    
    /**
     * soll vom Fenster aus aufgerufen werden, wenn ein Fenster geschlossen wird
     */
    public void removeMenu()
    {
        this.openedMenu = null;
    }
    
    /**
     * schließt das gerade geöffnete Menu
     */
    public void closeMenu()
    {
        if (openedMenu == null)return;
        openedMenu.closeMenu();
        openedMenu = null;
    }
    
    /**
     * gibt false zurück, wenn gerade ein Menü offen ist
     */
    public boolean isActive()
    {
        return openedMenu == null;
    }
    
    /**
     * Task-Funktion
     */
    public void showMenu(String menuName, Object[] menuParams){
        if (menuName.equals("NoteblockMenu")){
            new NoteblockMenu(this,(VektorI) menuParams[0],(String) menuParams[1]);
        }
    }
    
    /**
     * schließt das gesamte Spiel
     */
    public void exit(){
        logout();
        Boolean exited=(Boolean) (new Request(id,"Main.exit",Boolean.class).ret);
    }
    
    /**
     * Tastatur Event
     * @param:
     *  char type: 'p': pressed
     *             'r': released
     *             't': typed (nur Unicode Buchstaben)
     */
    public void keyEvent(KeyEvent e, char type) {
        if (!isActive())return;  // wenn ein Menü offen ist, dann passiert nichts
        switch (e.getKeyCode()){
            case Shortcuts.open_escape_menu: 
                if (type != 'p') break;
                new EscapeMenu(this);
                break;
            case Shortcuts.change_space_craft:
                if (type != 'p') break;
                changePlayer();
                repaint();
                break;
            default:
                if (inCraft)playerC.keyEvent(e,type);
                else playerS.keyEvent(e,type);
        }
    }
    
    /**
     * Mausrad"Event"
     * @param:
     * irgend ein EventObjekt; Keine Ahnung was das kann
     */
    public void mouseWheelMoved(MouseWheelEvent e){
        if (!isActive())return;  // wenn ein Menü offen ist, dann passiert nichts
        if(!inCraft)playerS.mouseWheelMoved(e);
    }
    
    /**
     * Maus Event
     * @param:
     *  char type: 'p': pressed
     *             'r': released
     *             'c': clicked
     * entered und exited wurde nicht implementiert, weil es dafür bisher keine Verwendung gab
     */
    public void mouseEvent(MouseEvent e, char type) {
        closeMenu();
        if (inCraft)playerC.mouseEvent(e,type);
        else playerS.mouseEvent(e,type);
    }
    
    public void windowEvent(WindowEvent e, char type){
        if (type=='c'){ //Schließen des Fensters
            logout();
        }
    }
    
    /**
     * gibt den Vektor der größe des Bildschirms zurück
     */
    public VektorI getScreenSize(){
        return frame.getScreenSize();
    }
    
    /**
     * ...
     */
    public Frame getFrame(){
        return frame;
    }
    
    public PlayerC getPlayerC(){
        return playerC;
    }
    public PlayerS getPlayerS(){
        return playerS;
    }
    
    /**
     * Grafik ausgeben
     */
    public void paint(Graphics g, VektorI screenSize){
        if (g!=null){
            if (inCraft && playerC != null)playerC.paint(g, screenSize);
            else if (playerS != null) playerS.paint(g, screenSize);
        }
    }
    
    /**
     * (auch) Task-Funktion
     */
    public void repaint(){
        if(frame!=null)frame.repaint();
    }
    
    public void retrieveBlockImages(){
        BlocksC.images=(HashMap<Integer,BufferedImage>) (new Request(id,"Main.retrieveBlockImages",HashMap.class).ret);
    }
    
    public void synchronizeWithServer(){
        Player pOnServer=((Player) new Request(id,"Main.retrievePlayer",Player.class).ret);
        inCraft=pOnServer.inCraft;
        currentMassIndex=pOnServer.currentMassIndex;
        playerS.posToMass=pOnServer.getPlayerS().posToMass;
        playerS.scale=pOnServer.getPlayerS().scale;
        playerS.focussedMassIndex=pOnServer.getPlayerS().focussedMassIndex;
        playerC.pos=pOnServer.getPlayerC().pos;
        playerC.onPlanet=pOnServer.getPlayerC().onPlanet;
        playerC.sandboxIndex=pOnServer.getPlayerC().sandboxIndex;
    }
}