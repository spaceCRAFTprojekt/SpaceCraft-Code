package server;
import client.Player;
import client.PlayerS;
import client.PlayerC;
import client.Request;
import client.OtherPlayerTexture;
import client.PlayerTexture;
import client.Task;
import util.geom.VektorI;
import java.util.ArrayList;
import java.util.Timer;
import java.util.HashMap;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;

import blocks.*;
/**
 * Der Server.
 * Enthält die main-Methode
 * History:
 * 0.0.2 AK * erstellt
 * 0.0.3 AK * Spawnplanet in Player verlegt
 * 0.0.5 LG * Serialisierung
 */
public class Main implements Serializable
{
    public static Main main; //nur ein Main pro Kopie des Spiels. Mit dieser Referenz können alle Objekte auf den Server zugreifen.
    
    public static final long serialVersionUID=0L;
    static String spacefilename="space"; //sollten die in Settings sein? Lg // die sind ja immer gleich; solange der Path in den Settings ist AK;
    static String playersfilename="players";
    static String shipCfilename="shipC";
    static String planetCfilename="planetC";
    static String blocksfilename="blocks";
    static String fileEnding=".ser";
    
    private transient ArrayList<Player> players = new ArrayList<Player>();
    // Kopie der Player, muss synchronisiert werden!
    // normalerweise nur ein Spieler
    private transient ArrayList<String> chat=new ArrayList<String>();
    private transient Space space;
    private transient ServerCreator sc;

    /**
     * "Lasset die Spiele beginnen" ~ Kim Jong Un
     * @param:
     * boolean useOldData: true = "alten" Spielstand laden
     *                     false = neues Spiel beginnen  !überschreibt "alten" Spielstand!
     */
    public static Main newMain(boolean useOldData){
        String folder=Settings.GAMESAVE_FOLDER;
        if (useOldData && new File(folder+File.separator+spacefilename+fileEnding).exists() &&
                          new File(folder+File.separator+playersfilename+fileEnding).exists() &&
                          new File(folder+File.separator+planetCfilename+"0"+fileEnding).exists() &&
                          new File(folder+File.separator+"main.ser").exists()){ //mindestens einer
            try{
                return Serializer.deserialize();
            }
            catch(Exception e){}
        }
        try{
        for(File file: new File(folder).listFiles()) //aus https://stackoverflow.com/questions/13195797/delete-all-files-in-directory-but-not-directory-one-liner-solution (18.4.2019)
            file.delete();
        }catch(Exception e){}
        Main m = new Main();    
        main=m;
        return m;
    }
    
    public static void main(String[]Args){
        newMain(false);
    }
    
    /**
     * Konstruktor
     * erstellt ein neues Spiel und keinen neuen Spieler
     */
    private Main()
    {
        System.out.println("\n==================\nSpaceCraft startet\n==================\n");
        serverCreatorSetup();
        space = new Space(100); //10-fache Beschleunigung im Space ~LG; drum steht 100 da :) ~AK
    }
    
    /**
     * "Ich warte" ~ DB Kunde
     * auf die Beschreibung @LG
     * Instruktionen zur Serialisierung, siehe Readme.
     * LG
     * ist alles für das Speichern des aktuellen Spielstands
     */
    private Object writeReplace() throws ObjectStreamException{
        String folder=Settings.GAMESAVE_FOLDER;
        
        ArrayList<ShipC> shipCs=ShipC.shipCs; //Schiffe
        for (int i=0;i<shipCs.size();i++){
            try{
                FileOutputStream sbo=new FileOutputStream(folder+File.separator+shipCfilename+i+fileEnding);
                ObjectOutputStream sboO=new ObjectOutputStream(sbo);
                sboO.writeObject(shipCs.get(i).map);
                sboO.writeObject(shipCs.get(i).meta);
                sboO.writeObject(shipCs.get(i).getSubsandboxes());
                //sboO.writeObject(shipCs.get(i).getShipS());
            }
            catch(Exception e){
                System.out.println(e+": "+e.getMessage());
            }
        }
        
        ArrayList<PlanetC> planetCs=PlanetC.planetCs; //Planeten
        for (int i=0;i<planetCs.size();i++){
            try{
                FileOutputStream sbo=new FileOutputStream(folder+File.separator+planetCfilename+i+fileEnding);
                ObjectOutputStream sboO=new ObjectOutputStream(sbo);
                sboO.writeObject(planetCs.get(i).map);
                sboO.writeObject(planetCs.get(i).meta);
                sboO.writeObject(planetCs.get(i).getSubsandboxes());
                //sboO.writeObject(planetCs.get(i).getPlanetS());
            }
            catch(Exception e){
                System.out.println(e+": "+e.getMessage());
            }
        }
        
        try{ //Space
            FileOutputStream spo=new FileOutputStream(folder+File.separator+spacefilename+fileEnding);
            ObjectOutputStream spoO=new ObjectOutputStream(spo);
            spoO.writeObject(space);
        }
        catch(Exception e){
            System.out.println(e+": "+e.getMessage());
        }
        
        try{ //Player
            FileOutputStream plo=new FileOutputStream(folder+File.separator+playersfilename+fileEnding);
            ObjectOutputStream ploO=new ObjectOutputStream(plo);
            ploO.writeObject(players);
        }
        catch(Exception e){
            System.out.println(e+": "+e.getMessage());
        }
        return this;
    }
    
    /**
     * "Ich warte" ~ DB Kunde
     * auf die Beschreibung @LG LG
     * Setup-Funktion, aufgerufen nach der Deserialisierung. 
     * LG
     * Oder mit anderen Worten: Liest den aktuellen Spielstand aus den gamesaves und erstellt damit alle nötigen Objekte
     * AK
     */
    public Object readResolve() throws ObjectStreamException{
        serverCreatorSetup();
        String folder=Settings.GAMESAVE_FOLDER;
        if (!new File(folder).isDirectory()){
            System.out.println("Folder "+folder+" does not exist.");
            return null;
        }
        
        for (int i=0;i<Integer.MAX_VALUE;i++){  //Schiffe
            try{
                if (new File(folder+File.separator+shipCfilename+i+fileEnding).exists()){
                    FileInputStream sbi=new FileInputStream(folder+File.separator+shipCfilename+i+fileEnding);
                    ObjectInputStream sbiO=new ObjectInputStream(sbi);
                    Block[][] map=(Block[][]) sbiO.readObject();
                    Meta[][] meta=(Meta[][]) sbiO.readObject();
                    ArrayList<Sandbox> subsandboxes=(ArrayList<Sandbox>) sbiO.readObject();
                    ShipS shipS=null; //siehe unten warum
                    Timer spaceTimer=null;
                    new ShipC(map,subsandboxes,shipS,spaceTimer).meta = meta; //fügt sich automatisch in die ArrayList ein
                }
                else{
                    break;
                }
            }
            catch(Exception e){
                System.out.println("Main: 2: "+e+": "+e.getMessage());
            }
        }
        
        for (int i=0;i<Integer.MAX_VALUE;i++){  //Planeten
            try{
                if (new File(folder+File.separator+planetCfilename+i+fileEnding).exists()){
                    FileInputStream sbi=new FileInputStream(folder+File.separator+planetCfilename+i+fileEnding);
                    ObjectInputStream sbiO=new ObjectInputStream(sbi);
                    Block[][] map=(Block[][]) sbiO.readObject();
                    Meta[][] meta=(Meta[][]) sbiO.readObject();
                    ArrayList<Sandbox> subsandboxes=(ArrayList<Sandbox>) sbiO.readObject();
                    PlanetS planetS=null; //Der PlanetS wird erst später (mit Space) hinzugefügt, um ein Problem mit einer zirkulären Referenz zu vermeiden.
                    Timer spaceTimer=null; //dito // ????
                    new PlanetC(map,subsandboxes,planetS,spaceTimer).meta = meta; //fügt sich automatisch in die ArrayList ein
                }
                else{
                    break;
                }
            }
            catch(Exception e){
                System.out.println("Main: 3: "+e+": "+e.getMessage());
            }
        }
        
        try{  // Space
            FileInputStream spi=new FileInputStream(folder+File.separator+spacefilename+fileEnding);
            ObjectInputStream spiO=new ObjectInputStream(spi);
            space=(Space) spiO.readObject();
        }
        catch(Exception e){
            System.out.println("Main: 4: "+e+": "+e.getMessage());
        }
        
        try{  //Players
            FileInputStream pli=new FileInputStream(folder+File.separator+playersfilename+fileEnding);
            ObjectInputStream pliO=new ObjectInputStream(pli);
            players=(ArrayList<Player>) pliO.readObject();
        }
        catch(Exception e){
            System.out.println("Main: 5: "+e+": "+e.getMessage());
        }
        this.chat=new ArrayList<String>();
        main=this;
        return this;
    }
    
    /**
     * Der ServerCreator organisiert den Server. Diese Funktion ist wichtig. ~Schnux Sonst wär sie wahrscheinlich nicht da ~unknown
     */
    public void serverCreatorSetup(){
        this.sc=new ServerCreator(this);
    }
    
    /**
     * gibt !!! zu Testzwecken !!! den Bildschirm aller Spieler neu aus
     */
    public void repaint()
    {
        for (int i = 0; i<players.size();i++){
            newTask(i,"Player.repaint");
        }
    }
    
    /**
     * gibt das Space Objekt zurück
     */
    public Space getSpace(){
        return space;
    }
    
    /**
     * gibt das Spieler Objekt mit dem Namen name zurück
     * wenn der Spieler nicht vorhanden ist: null
     */
    public Player getPlayer(String name){
        for(int i = 0; i<players.size(); i++){
            if(players.get(i).getName() == name) return players.get(i);
        }
        return null;
    }
    
    public Player getPlayer(int id){
        if (id>=0 && id<players.size()){
            return players.get(id);
        }
        return null;
    }
    
    public void exitIfNoPlayers(){
        for(int i = 0; i<players.size();i++){
            if(players.get(i).isOnline())return; // wenn ein Spieler online ist abbrechen
        }
        exit(); // sonst Spiel beenden
    }
    
    /**
     * Schließt das Spiel UND speichert den Spielstand!!!
     */
    public void exit(){
        System.out.println("\n===================\nSpaceCraft schließt\n===================\n");
        for (int i=0;i<players.size();i++){
            if (players.get(i).isOnline()){
                players.get(i).logout(); //Server-Kopie des Players
                newTask(i,"Player.logoutTask"); //Player im Client
                sc.taskOutputStreams.remove(i);
            }
        }
        Serializer.serialize(this);
        main=null;
        System.exit(0);
    }
    
    public ServerCreator getServerCreator(){
        return sc;
    }
    
    public void newTask(int playerID, String todo, Object... params){
        Task task=new Task(todo, params);
        sc.sendTask(playerID,task);
    }
    
    //Ab hier Request-Funktionen
    
    public Boolean exit(Integer playerID){
        Boolean exited=new Boolean(true);
        exit();
        return exited;
    }
    
    public Boolean exitIfNoPlayers(Integer playerID){
        Boolean exited=new Boolean(true);
        exitIfNoPlayers();
        return exited;
    }
    
    public Boolean login(Integer playerID){
        players.get(playerID).setOnline(true); //wirkt auf die Kopie in der Liste, der Player im Client setzt sich selbst online
        return new Boolean(true);
    }
    
    public Boolean logout(Integer playerID){
        players.get(playerID).setOnline(false); //siehe login(Integer playerID)
        sc.taskOutputStreams.remove(playerID);
        return new Boolean(true);
    }
    
    public Boolean returnFromMenu(Integer playerID, String menuName, Object[] menuParams){
        if (menuName.equals("NoteblockMenu")){
            Sandbox sb;
            if ((Boolean) menuParams[0]){ //onPlanet
                sb=PlanetC.planetCs.get((Integer) menuParams[1]);
            }
            else{
                sb=ShipC.shipCs.get((Integer) menuParams[1]);
            }
            Meta mt=sb.getMeta((VektorI) menuParams[2]);
            if (mt!=null){
                mt.put("text",menuParams[3]);
                return new Boolean(true);
            }
            return new Boolean(false);
        }
        return new Boolean(false);
    }
    
    /**
     * Der Status des Players im Client hat sich verändert, also macht er einen Request, damit der Status der Kopie des Players im Server genauso ist.
     */
    public void synchronizePlayerVariable(Integer playerID, String varname, Class cl, Object value) throws NoSuchFieldException, IllegalAccessException{
        try{
            //hier sollte wahrscheinlich eine Überprüfung stattfinden, ob dieser Wert überhaupt gültig ist
            Player p=players.get(playerID);
            Class pc=Player.class;
            Field f=pc.getDeclaredField(varname);
            f.set(p,value);
        }
        catch(IndexOutOfBoundsException e){} //Warum das? Ich habe es selbst geschrieben und wieder vergessen. -LG
    }
    
    public void synchronizePlayerSVariable(Integer playerID, String varname, Class cl, Object value) throws NoSuchFieldException, IllegalAccessException{
        try{
            PlayerS p=players.get(playerID).getPlayerS();
            Class pc=PlayerS.class;
            Field f=pc.getDeclaredField(varname);
            f.set(p,value);
        }
        catch(IndexOutOfBoundsException e){}
    }
    
    public void synchronizePlayerCVariable(Integer playerID, String varname, Class cl, Object value) throws NoSuchFieldException, IllegalAccessException{
        try{
            PlayerC p=players.get(playerID).getPlayerC();
            Class pc=PlayerC.class;
            Field f=pc.getDeclaredField(varname);
            f.set(p,value);
        }
        catch(IndexOutOfBoundsException e){}
    }
    
    /**
     * neuer Spieler (vorerst nur zu Testzwecken)
     * Request-Funktion!
     * playerID wird bei Requests standardmäßig übergeben, ist hier aber ohne Belang (-1).
     * Return-Wert: Kein Erfolg: -1, sonst die ID
     * Erstellt nur die Kopie des Players am Server. Um einen Player mit Client zu erstellen, wird static client.Player.newPlayer(String name) verwendet.
     */
    public Integer newPlayer(Integer playerID, String name)
    {
        if (getPlayer(name) != null)return new Integer(-1);
        int id=players.size();
        Player p=new Player(id, name, false);
        players.add(p);
        return id;
    }
    
    /**
     * Gibt die Kopie des Players hier vom Server zurück. Zur Synchronisierung (siehe Player.synchronizeWithServer)
     */
    public Player retrievePlayer(Integer playerID){
        return players.get(playerID);
    }
    
    public void writeIntoChat(Integer playerID, String message){
        chat.add(players.get(playerID).getName()+": "+message);
    }
    
    public String[] getChatContent(Integer playerID, Integer numLines){
        //die letzten (numLines) Zeilen
        String[] ret=new String[numLines];
        int chatSize=chat.size();
        for (int i=0;i<numLines;i++){
            if (chatSize-numLines+i>=0){
                ret[i]=chat.get(chatSize-numLines+i);
            }
            else{
                ret[i]="";
            }
        }
        return ret;
    }
    
    public Player getPlayer(Integer playerID, String name){ //playerID=-1
        for(int i = 0; i<players.size(); i++){
            //aus irgendeinem Grund geht == nicht mit Requests
            if(players.get(i).getName().equals(name)) return players.get(i);
        }
        return null;
    }
    
    /**
     * Warum kann ich ein scheiß Object[] nicht in ein noch blöderes OtherPlayerTexture[] casten?!?!?!
     * Daher wird Ihnen hier ein scheiß Obejct[] zurückgeben :(  
     */
    public Object[] getOtherPlayerTextures(Integer playerID){
        if(players.size() < 2)return null; // wenn es nur einen Spieler gibt (Singleplayer), dann null.
        ArrayList<OtherPlayerTexture> ret = new ArrayList<OtherPlayerTexture>();
        int massID = players.get(playerID).getCurrentMassIndex();
        for(int i = 0; i<players.size(); i++){
            if(playerID != i && players.get(i).isOnline() && players.get(i).getCurrentMassIndex() == massID){ // der Spieler selbst soll natürlich nicht im Array zurückgegeben werden
                PlayerC pC = players.get(i).getPlayerC();
                PlayerTexture t = pC.getPlayerTexture();
                ret.add(new OtherPlayerTexture(i, t.mode, t.textureID, pC.pos));
            }
        }
        return (ret.toArray());
    }
}
// Hallo ~unknown