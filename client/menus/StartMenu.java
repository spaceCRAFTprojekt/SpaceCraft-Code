package client.menus;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import util.geom.VektorI;
import menu.*;
import client.ClientSettings;
/**
 * wird angezeigt beim Start des Spiels:
 * Möglichkeit der Kartenauswahl und Starten des Spiels.
 * VG von MH 06.05.2019
 * Jetzt auch mit Serverkontrolle:
 * VG von LG 28.06.2019
 */
public class StartMenu extends Menu{
    private MenuList localserverlist;
    private MenuButton newserverbutton;
    private MenuButton deleteserverbutton;
    private MenuTextField serverPort;
    private MenuButton playlocalbutton;
    /**
     * Das Main, das gerade läuft => nur ein Server gleichzeitig pro Kopie des Spiels
     */
    public static Object currentMain=null;
    
    private MenuList onlineWorldlist;
    private MenuButton playonlinebutton;
    private MenuTextField address;
    private MenuTextField port;
    public StartMenu(){
        super("SpaceCraft", new VektorI(880, 460));
        setFont(MenuSettings.MENU_FONT);
        new MenuLabel(this, "Hier könnte ihre Werbung stehen.", new VektorI(10,10), new VektorI(250,30));
        
        new MenuLabel(this, "<html><b>Lokale Welten: </b></html>", new VektorI(10,30), new VektorI(250,30));
        try{
            Class mainC=Class.forName("server.Main");
            Class serializerC=Class.forName("server.Serializer");
            Class settingsC=Class.forName("server.Settings");
            //um import server.Main etc. zu vermeiden. Vermutlich war das eher eine dumme Idee. -LG
            
            localserverlist=new MenuList(this,new String[]{},new VektorI(10,60),new VektorI(250,340),15);
            updateLocalWorldlist();
            serverPort=new MenuTextField(this,"Port",new VektorI(280,330),new VektorI(130,20));
            
            playlocalbutton=new MenuButton(this, "Lokal spielen", new VektorI(280,360), new VektorI(130,40), MenuSettings.MENU_FONT){
                public void onClick(){
                    String str=(String) localserverlist.getSelectedValue();
                    try{
                        StartMenu.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                        ClientSettings.SERVER_ADDRESS="localhost";
                        if (currentMain!=null && mainC.getField("name").get(currentMain).equals(str)){
                            //Server bereits aktiv => gehostet
                            mainC.getMethod("start").invoke(currentMain); //Wenn der Server schon läuft, dann passiert in start() nichts. 
                            //Wenn nicht, dann wird er hier noch gestartet.
                        }
                        else{
                            try{
                                Object main=serializerC.getMethod("deserialize",String.class).invoke(null,str);
                                try{
                                    settingsC.getField("SERVER_PORT").set(null,Integer.parseInt(serverPort.getText()));
                                }
                                catch(NumberFormatException e){}
                                if (currentMain!=null){ //irgendein Fehler
                                    System.out.println("[StartMenu]: Aus irgendeinem Grund läuft bereits ein Server, der hiermit geschlossen wird.");
                                    mainC.getMethod("exit").invoke(currentMain,true);
                                }
                                mainC.getMethod("start").invoke(main);
                                try{
                                    ClientSettings.SERVER_PORT=Integer.parseInt(serverPort.getText());
                                }
                                catch(NumberFormatException e){}
                            }
                            catch(Exception exc){
                                StartMenu.this.setCursor(Cursor.getDefaultCursor());
                                System.out.println("[StartMenu]: Die Welt "+str+" konnte nicht erfolgreich deserialisiert werden.");
                                return;
                            }
                        }
                        StartMenu.this.setCursor(Cursor.getDefaultCursor());
                        StartMenu.this.closeMenu();
                        if ((boolean) mainC.getField("singleplayer").get(currentMain)){
                            LoginMenu lm=new LoginMenu();
                            lm.name.setText("Singleplayer");
                            lm.pw.setText(" ");
                            lm.login.onClick();
                        }
                        else
                            new LoginMenu();
                    }
                    catch(Exception e){e.printStackTrace();}
                }
            };
            
            newserverbutton=new MenuButton(this, "Neue Welt", new VektorI(280,60), new VektorI(130,40)){
                public void onClick(){
                    new NewServerMenu(StartMenu.this);
                }
            };
            
            deleteserverbutton=new MenuButton(this, "Welt löschen", new VektorI(280,110),new VektorI(130,40)){
                public void onClick(){
                    String str=(String) localserverlist.getSelectedValue();
                    if (str!=null)
                        new DeleteServerMenu(StartMenu.this,str);
                }
            };
        }
        catch(Exception e){
            new MenuLabel(this, "<html>[Um auch selbst Server erstellen zu können, benötigst du <br/>erst ein Upgrade auf eine Version mit dem Server-Package(TM)]</html>", new VektorI(10,50), new VektorI(420,60));
        }
        
        new MenuLabel(this, "<html><b>Globale Server: </b></html>", new VektorI(450,30), new VektorI(250,30));
        onlineWorldlist=new MenuList(this, new String[]{}, new VektorI(450,60) ,new VektorI(250,340), 15);
        updateOnlineWorldlist();
        playonlinebutton = new MenuButton(this, "Online spielen", new VektorI(720,360) , new VektorI(130,40), MenuSettings.MENU_FONT){
            public void onClick(){
                String str=(String) onlineWorldlist.getSelectedValue();
                if (str!=null){
                    try{
                        String[] spl=str.split("[ :]");
                        ClientSettings.SERVER_ADDRESS=spl[1];
                        ClientSettings.SERVER_PORT=Integer.parseInt(spl[2]);
                        closeMenu();
                        new LoginMenu();
                    }
                    catch(Exception e){}
                }
                else{
                    ClientSettings.SERVER_ADDRESS=address.getText();
                    try{ClientSettings.SERVER_PORT=Integer.parseInt(port.getText());} catch(NumberFormatException e){System.out.println("[StartMenu]: Invalid Input: Port muss eine Zahl sein!");}
                    closeMenu();
                    new LoginMenu();
                }
            }};
        new MenuLabel(this,"<html>Verbindung mit <br/>einem unbekannten <br/>Server:</html>", new VektorI(720,240), new VektorI(130,60));
        address =new MenuTextField(this, "Adresse", new VektorI(720,300), new VektorI(130,20));
        port = new MenuTextField(this, "Port", new VektorI(720,330), new VektorI(130,20));
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                try{
                    //Kein Unterbrechen eines Serialisierungs-Vorgangs
                    Class serializerC=Class.forName("server.Serializer");
                    while((boolean) serializerC.getField("currentlyWorking").get(null)){
                        Thread.sleep(1);
                    }
                }
                catch(Exception exc){exc.printStackTrace();}
                System.out.println("[StartMenu]: windowClosing");
                System.exit(0);
            }
        });
        repaint();
    }
    
    /**
     * Update der Online-Weltliste aus der gegebenen Datei heraus
     * Natürlich sollte eigentlich irgendwann ein Server existieren, der eine Liste aller Welten hat.
     */
    public void updateOnlineWorldlist(){
        try{
            //aus https://stackoverflow.com/questions/14169661/read-complete-file-without-using-loop-in-java
            List<String> lines = Files.readAllLines(Paths.get(ClientSettings.SERVERLIST_FILENAME+".txt"), StandardCharsets.UTF_8);
            lines.set(0,lines.get(0).substring(1)); //irgendein Zeichen, das den Start der Datei anzeigt?
            ArrayList<String> serverList=new ArrayList<String>(); //mit Adresse und Port
            for (int i=0;i<lines.size();i++){
                int endIndex=lines.get(i).indexOf("//");
                endIndex=endIndex==-1 ? lines.get(i).length() : endIndex;
                if (endIndex!=-1 && lines.get(i).substring(0,endIndex).length()!=0){
                    serverList.add(lines.get(i).substring(0,endIndex));
                }
            }
            String[] servernames=new String[serverList.size()];
            servernames=serverList.toArray(servernames);
            onlineWorldlist.setListData(servernames);
        }
        catch(IOException e){}
    }
    
    /**
     * Update der lokalen Weltliste aus den Speicherdateien heraus
     */
    public void updateLocalWorldlist(){
        try{
            Class settingsC=Class.forName("server.Settings");
            String folder=(String) settingsC.getField("GAMESAVE_FOLDER").get(null);
            ArrayList<String> names=new ArrayList<String>();
            for (File f: new File(folder).listFiles()){
                names.add(f.getName().substring(0,f.getName().length()-4));
            }
            String[] namesArr=new String[names.size()];
            namesArr=names.toArray(namesArr);
            localserverlist.setListData(namesArr);
        }
        catch(Exception e){}
    }
    
    public class NewServerMenu extends Menu{
        private MenuTextField name;
        private MenuCheckBox hostbox;
        public NewServerMenu(StartMenu startmenu){
            super("Neue Welt erstellen",new VektorI(380,170));
            try{
                Class mainC=Class.forName("server.Main");
                Class settingsC=Class.forName("server.Settings");
                String folder=(String) settingsC.getField("GAMESAVE_FOLDER").get(null);
                new MenuLabel(this, "Name", new VektorI(10,20), new VektorI(80,20));
                name = new MenuTextField(this, "", new VektorI(100,20), new VektorI(250,20));
                hostbox=new MenuCheckBox(this, "Server hosten",new VektorI(10,50),new VektorI(140,20),12);
                new MenuButton(this, "Erstellen", new VektorI(10,80) , new VektorI(120,40), MenuSettings.MENU_BIG_FONT){
                    public void onClick(){
                        String n=name.getText();
                        if (n.length()>0){
                            if (!new File(folder).exists()){
                                new File(folder).mkdirs();
                            }
                            for (File f: new File(folder).listFiles()){
                                if (f.getName().equals(n+".ser")){
                                    System.out.println("Ein Server des Names "+n+" existiert bereits.");
                                    closeMenu();
                                    return;
                                }
                            }
                            try{
                                NewServerMenu.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                Object main=mainC.getMethod("newMain",String.class,boolean.class,boolean.class).invoke(null,n,!hostbox.isSelected(),false);
                                if (hostbox.isSelected()){
                                    mainC.getMethod("start").invoke(main);
                                }
                                NewServerMenu.this.setCursor(Cursor.getDefaultCursor());
                                System.out.println("[StartMenu]: Der Server "+n+" wurde erstellt.");
                            }
                            catch(Exception e){e.printStackTrace();}
                            startmenu.updateLocalWorldlist();
                            closeMenu();
                        }
                    }
                };
                new MenuButton(this, "Schließen", new VektorI(230,80) , new VektorI(120,40), MenuSettings.MENU_BIG_FONT){
                    public void onClick(){
                        closeMenu();
                    }
                };
                repaint();
            }
            catch(Exception e){
                closeMenu();
            }
        }
    }
    
    public class DeleteServerMenu extends Menu{
        public DeleteServerMenu(StartMenu s, String servername){
            super("Welt löschen", new VektorI(300,120));
            try{
                Class mainC=Class.forName("server.Main");
                Class settingsC=Class.forName("server.Settings");
                new MenuLabel(this, "Wollen Sie die Welt wirklich löschen?", new VektorI(10,10), new VektorI(300,20));
                new MenuButton(this, "Ja", new VektorI(170,40), new VektorI(100, 30)){
                    public void onClick(){
                        try{
                            if (mainC.getField("name").get(StartMenu.currentMain).equals(servername)){
                                mainC.getMethod("exit").invoke(StartMenu.currentMain,false);
                            }
                        }
                        catch(Exception e){}
                        try{
                            String folder=(String) settingsC.getField("GAMESAVE_FOLDER").get(null);
                            File f=new File(folder+File.separator+servername+".ser");
                            f.delete();
                            System.out.println("[StartMenu]: Der Server "+servername+" wurde gelöscht.");
                        }
                        catch(Exception e){e.printStackTrace();}
                        s.updateLocalWorldlist();
                        closeMenu();
                    }
                };
                new MenuButton(this,"Nein",new VektorI(10,40),new VektorI(100,30)){
                    public void onClick(){
                        closeMenu();
                    }
                };
                repaint();
            }
            catch(Exception e){
                closeMenu();
            }
        }
    }
}