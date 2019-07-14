package server;
import client.Player;
import client.PlayerS;
import client.PlayerC;
import client.Request;
import client.OtherPlayerTexture;
import client.PlayerTexture;
import client.Task;
import client.menus.StartMenu;
import util.geom.VektorI;
import java.util.ArrayList;
import java.util.Timer;
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
import util.geom.VektorD;
import items.*;

import blocks.*;
/**
 * Der Server, der große Teile des Spielinhalts erledigt. Für Formalia ist der ServerCreator zuständig.
 * History:
 * 0.0.2 AK * erstellt
 * 0.0.3 AK * Spawnplanet in Player verlegt
 * 0.0.5 LG * Serialisierung
 */
public class Main implements Serializable
{
    public static final long serialVersionUID=0L;
    /**
     * Name dieses Servers und der Datei, in der er gespeichert wird
     */
    public String name;
    /**
     * true: Ausloggen führt zu exit()
     */
    public boolean singleplayer;
    /**
     * Liste aller Spieler (Kopien), wird mit dem Client synchronisiert.
     */
    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<String> passwords = new ArrayList<String>(); //ziemlich unsicher!
    private transient ArrayList<String> chat=new ArrayList<String>();
    /**
     * Der Weltraum, der Massen enthält, die Sandboxen enthalten
     */
    private Space space;
    /**
     * Dieser kümmert sich um die tatsächliche Server-Client-Verbindung.
     */
    private transient ServerCreator sc;

    /**
     * "Lasset die Spiele beginnen" ~ Kim Jong Un
     * @param:
     * boolean useOldData: true = "alten" Spielstand laden
     *                     false = neues Spiel beginnen  !überschreibt "alten" Spielstand!
     * Der Server läuft jetzt noch nicht, sondern muss erst noch mit start() gestartet werden!
     */
    public static Main newMain(String name, boolean singleplayer, boolean useOldData){
        String folder=Settings.GAMESAVE_FOLDER;
        if (useOldData && new File(folder+File.separator+name+".ser").exists()){
            try{
                return Serializer.deserialize(name);
            }
            catch(Exception e){
                return null;
            }
        }
        new File(folder+File.separator+name+".ser").delete();
        Main m = new Main(name,singleplayer);
        System.out.println("[Server]: Neuer Server: "+m);
        return m;
    }

    /**
     * Konstruktor
     * erstellt ein neues Spiel und keinen neuen Spieler
     * Privat, da newMain verwendet werden sollte.
     */
    private Main(String name, boolean singleplayer)
    {
        this.name=name;
        this.singleplayer=singleplayer;
        space = new Space(this,1); //keine Beschleunigung im Space
        Serializer.serialize(this); //ein erstes Speichern
    }

    /**
     * "Ich warte" ~ DB Kunde
     * auf die Beschreibung @LG LG
     * Setup-Funktion, aufgerufen nach der Deserialisierung.
     * "Transiente" Variablen werden nicht serialisiert, müssen also hier neu erstellt werden.
     * LG
     */
    public Object readResolve() throws ObjectStreamException{
        this.chat=new ArrayList<String>();
        return this;
    }
    
    /**
     * Startet den Server
     */
    public void start() throws Exception{
        if (sc==null){ //hat noch nicht gestartet
            serverCreatorSetup();
            space.timerSetup();
            StartMenu.currentMain=this;
            Runtime.getRuntime().addShutdownHook(new Thread(){
                //automatisches Herunterfahren des Servers, wenn die Virtual Machine geschlossen wird => etwas sicherer
                //Herunterfahren ohne Speichern!
                public void run(){
                    try{
                        System.out.println("[Server]: Shutdown-Hook läuft");
                    }
                    catch(Exception e){} //schon geschlossen?
                    Main.this.exit(false);
                }
            });
            System.out.println("\n=================[Server]: Start\n=================\n");
        }
    }
    
    /**
     * Schließt den Server.
     * Führt nicht zu System.exit, sondern ist die Methode, die von System.exit aufgerufen werden sollte
     * (siehe den Shutdown-Hook in start()).
     * @param: boolean saveData: ob gespeichert wird oder nicht (sollte nur
     * false sein, wenn die Virtual Machine beendet wird, siehe den ShutdownHook in start())
     */
    public void exit(boolean saveData){
        if (sc!=null){ //hat schon gestartet
            System.out.println("\n================\n[Server]: Exit\n=================\n");
            for (int i=0;i<players.size();i++){
                if (players.get(i).isOnline()){
                    players.get(i).logout(); //Server-Kopie des Players
                    newTask(i,"Player.logoutTask"); //Player im Client
                    sc.taskOutputStreams.remove(i);
                }
            }
            if (saveData){
                Serializer.serialize(this);
            }
            StartMenu.currentMain=null;
            for (int i=0;i<sc.threads.size();i++){
                sc.threads.get(i).shouldStop=true;
            }
            try{
                sc.server.close();
            }
            catch(IOException e){}
            space.timer.cancel();
            sc=null;
        }
    }

    /**
     * Der ServerCreator organisiert den Server. Diese Funktion ist wichtig. ~Schnux Sonst wÃ¤r sie wahrscheinlich nicht da ~unknown
     */
    public void serverCreatorSetup() throws Exception{
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
    
    public int getPlayerNumber(){
        return players.size();
    }

    public void exitIfNoPlayers(){
        for(int i = 0; i<players.size();i++){
            if(players.get(i).isOnline())return; // wenn ein Spieler online ist abbrechen
        }
        exit(true); // sonst Spiel beenden
    }
    
    public ServerCreator getServerCreator(){
        return sc;
    }
    
    /**
     * Diese Funktion sollte verwendet werden, um neue Tasks zu erstellen.
     */
    public void newTask(int playerID, String todo, Object... params){
        Task task=new Task(todo, params);
        sc.sendTask(playerID,task);
    }

    //Ab hier Request-Funktionen
    /**
     * Request-Funktion
     */
    public void exit(Integer playerID){
        if (players.get(playerID).isAdmin())
            exit(true);
        else
            noAdminMsg(playerID);
    }
    
    /**
     * Request-Funktion
     */
    public void exitIfNoPlayers(Integer playerID){
        if (players.get(playerID).isAdmin())
            exitIfNoPlayers();
        else
            noAdminMsg(playerID);
    }
    
    /**
     * Request-Funktion
     */
    public Boolean login(Integer playerID, String password){
        if (passwords.get(playerID).equals(password) && !players.get(playerID).isOnline()){
            players.get(playerID).setOnline(true); //wirkt auf die Kopie in der Liste, der Player im Client setzt sich selbst online
            return new Boolean(true);
        }
        return new Boolean(false);
    }
    
    public PlayerInv getPlayerInv(Integer playerID){
        return players.get(playerID).getPlayerC().getInv(); // Kopie des Spielers am Server
    }

    /**
     * Request-Funktion
     */
    public void logout(Integer playerID, PlayerInv inv){
        Player player = players.get(playerID);
        player.setOnline(false); //siehe login(Integer playerID)
        player.getPlayerC().setInv(inv);
        try{
            sc.taskOutputStreams.get(playerID).close();
        }
        catch(IOException e){}
        sc.taskOutputStreams.remove(playerID);
        for (int i=0;i<sc.threads.size();i++){
            if (sc.threads.get(i).id==playerID.intValue())
                sc.threads.remove(i);
        }
        if (singleplayer)
            exit(true);
    }
    
    /**
     * Request-Funktion
     * sollte aufgerufen werden, wenn der Spieler ein Menü schließt
     */
    public Boolean returnFromMenu(Integer playerID, String menuName, Object[] menuParams){
        try{
            if (menuName.equals("NoteblockMenu")){  // @KÃ¤pt'n ernsthaft? Kann man das nicht in die entsprechende Klasse auslagern???
                Sandbox sb = getSandbox((Integer)menuParams[0]);
                Meta mt=sb.getMeta((VektorI) menuParams[1]);
                if (mt!=null){
                    mt.put("text",menuParams[2]);
                    return new Boolean(true);
                }
                return new Boolean(false);
            }else if(menuName.equals("ChestMenu")){
                Sandbox sb = getSandbox((Integer)menuParams[0]);
                Meta mt=sb.getMeta((VektorI) menuParams[1]);
                Inv inv_main = (Inv)menuParams[2];
                if(inv_main != null && mt != null){
                    mt.put("inv_main", inv_main);
                    return new Boolean(true);
                }
            }
        }catch(Exception e){System.out.println("[Server]: Exception in server.Main.returnFromMenu(): "+ e);}
        return new Boolean(false);
    }
    
    public Sandbox getSandbox(Integer sandboxIndex){ //Ich glaube, dass diese Funktion meistens vergessen wird und die hässliche Schreibweise verwendet wird.
        return ((Mass) space.masses.get(sandboxIndex)).getSandbox();
    }
    
    /**
     * Der Status des Players im Client hat sich verändert, also macht er einen Request, damit der Status der Kopie des Players im Server genauso ist.
     */
    public void synchronizePlayerVariable(Integer playerID, String varname, Class cl, Object value) throws NoSuchFieldException, IllegalAccessException{
        try{
            Player p=players.get(playerID);
            //schlechte Sicherheitsüberprüfungen
            if (!p.isAdmin())
                if (varname=="currentMassIndex" && p.getPlayerS().reachedMassIDs.indexOf((int) value)==-1)
                    noAdminMsg(playerID);
                else if (varname=="isAdmin")
                    noAdminMsg(playerID);
            Class pc=Player.class;
            Field f=pc.getDeclaredField(varname);
            f.set(p,value);
        }
        catch(IndexOutOfBoundsException e){} //Warum das? Ich habe es selbst geschrieben und wieder vergessen. -LG
    }
    
    /**
     * Request-Funktion
     * Siehe synchronizePlayerVariable
     */
    public void synchronizePlayerSVariable(Integer playerID, String varname, Class cl, Object value) throws NoSuchFieldException, IllegalAccessException{
        try{
            PlayerS p=players.get(playerID).getPlayerS();
            if (!players.get(playerID).isAdmin())
                if (varname=="reachedMassIDs")
                    noAdminMsg(playerID);
            Class pc=PlayerS.class;
            Field f=pc.getDeclaredField(varname);
            f.set(p,value);
        }
        catch(IndexOutOfBoundsException e){}
    }
    
    /**
     * Request-Funktion
     * Siehe synchronizePlayerVariable
     */
    public void synchronizePlayerCVariable(Integer playerID, String varname, Class cl, Object value) throws NoSuchFieldException, IllegalAccessException{
        try{
            PlayerC p=players.get(playerID).getPlayerC();
            if (!players.get(playerID).isAdmin())
                if (varname=="pos" && p.pos.subtract((VektorD) value).getLength()>20)
                    noAdminMsg(playerID);
            Class pc=PlayerC.class;
            Field f=pc.getDeclaredField(varname);
            f.set(p,value);
        }
        catch(IndexOutOfBoundsException e){}
    }

    /**
     * neuer Spieler
     * Request-Funktion!
     * playerID wird bei Requests standardmäßig übergeben, ist hier aber ohne Belang (-1).
     * Return-Wert: Kein Erfolg: -1, sonst die ID
     * Erstellt nur die Kopie des Players am Server. Um einen Player mit Client zu erstellen, wird static client.Player.newPlayer(String name) verwendet.
     */
    public Integer newPlayer(Integer playerID, String name, String password)
    {
        if (getPlayer(name) != null)return new Integer(-1);
        if (name.length()==0) return new Integer(-1); //Das Password kann auch einfach nichts sein, wenn das erwünscht ist.
        int id=players.size();
        Player p;
        if (id==0) //der erste Spieler ist automatisch Administrator
            p=new Player(id, name, false, true);
        else
            p=new Player(id, name, false, false);
        players.add(p);
        passwords.add(password);
        return id;
    }

    /**
     * Request-Funktion
     * Gibt die Kopie des Players hier vom Server zurÃ¼ck. Zur Synchronisierung (siehe Player.synchronizeWithServer)
     */
    public Player retrievePlayer(Integer playerID){
        return players.get(playerID);
    }
    
    /**
     * Request-Funktion
     */
    public void writeIntoChat(Integer playerID, String message){
        String msg = players.get(playerID).getName()+": "+message;
        chat.add(msg);
        for (int i=0;i<players.size();i++){
            if (players.get(i).isOnline()){
                newTask(i, "Player.addChatMsg", msg);
            }
        }
    }
    /**
     * Request-Funktion
     */
    public void serverChatMsg(Integer playerID, String message){
        String msg = message;
        chat.add(msg);
        for (int i=0;i<players.size();i++){
            if (players.get(i).isOnline()){
                newTask(i, "Player.addChatMsg", msg);
            }
        }
    }
    
    /**
     * Request-Funktion
     */
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
    
    /**
     * Request-Funktion mit üblicherweise playerID=-1 (ist egal)
     */
    public Player getPlayer(Integer playerID, String name){
        for(int i = 0; i<players.size(); i++){
            //aus irgendeinem Grund geht == nicht mit Requests
            if(players.get(i).getName().equals(name)) return players.get(i);
        }
        return null;
    }

    /**
     * Warum kann ich ein scheiß Object[] nicht in ein noch blöderes OtherPlayerTexture[] casten?!?!?!
     * Daher wird Ihnen hier ein scheiß Object[] zurückgegeben :(  
     */
    public Object[] getOtherPlayerTextures(Integer playerID, VektorI upperLeftCorner, VektorI bottomRightCorner){
        if(players.size() < 2)return null; // wenn es nur einen Spieler gibt (Singleplayer), dann null.
        ArrayList<OtherPlayerTexture> ret = new ArrayList<OtherPlayerTexture>();
        int massID = players.get(playerID).getCurrentMassIndex();
        for(int i = 0; i<players.size(); i++){
            if(playerID != i && players.get(i).isOnline()){ // der Spieler selbst soll natürlich nicht im Array zurückgegeben werden
                if (players.get(i).getCurrentMassIndex() == massID){
                    PlayerC pC = players.get(i).getPlayerC();
                    VektorI pos = pC.pos.toInt();
                    if(pos.x >= upperLeftCorner.x && pos.y >= upperLeftCorner.y && pos.x <= bottomRightCorner.x && pos.y <= bottomRightCorner.y){
                        PlayerTexture t = pC.getPlayerTexture();
                        ret.add(new OtherPlayerTexture(i, t.mode, t.textureID, pC.pos, players.get(i).getName()));
                    }
                }
                else{
                    int subsandboxIndex=getSandbox(massID).subsandboxIndex(players.get(i).getCurrentMassIndex());
                    if (subsandboxIndex!=-1){
                        //Spieler in Subsandboxen dieser Sandbox
                        PlayerC pC = players.get(i).getPlayerC();
                        VektorD offset=getSandbox(massID).subsandboxes.get(subsandboxIndex).offset;
                        VektorI pos = pC.pos.add(offset).toInt();
                        if(pos.x >= upperLeftCorner.x && pos.y >= upperLeftCorner.y && pos.x <= bottomRightCorner.x && pos.y <= bottomRightCorner.y){
                            PlayerTexture t = pC.getPlayerTexture();
                            ret.add(new OtherPlayerTexture(i, t.mode, t.textureID, pC.pos.add(offset), players.get(i).getName()));
                        }
                    }
                }
            }
        }
        return (ret.toArray());
    }
    
    public String[] getOnlinePlayerNames(Integer playerID){
        ArrayList<String> names=new ArrayList<String>();
        for (int i=0;i<players.size();i++){
            if (players.get(i).isOnline())
                names.add(players.get(i).getName());
        }
        String[] ret=new String[names.size()];
        ret=names.toArray(ret);
        return ret;
    }
    
    /**
     * etwas bequemer
     */
    public void noAdminMsg(int playerID){
        newTask(playerID,"Player.addChatMsg","Du bist kein Administrator.");
    }
    
    @Override
    public String toString(){
        return "Main: name = "+name+", singleplayer = "+singleplayer+", Spieleranzahl = "+players.size();
    }
}
// Hallo ~unknown