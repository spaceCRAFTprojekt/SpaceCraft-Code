package client.menus;
import javax.swing.*;
import util.geom.VektorI;
import menu.*;
import client.Player;
import client.ClientSettings;
import client.Request;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
/**
 * wird angezeigt beim Start des Spiels:
 * Möglichkeit der Kartenauswahl und Starten des Spiels.
 * VG von MH 06.05.2019
 */
public class LoginMenu extends Menu{
    private JButton login;
    public Player player;
    private JTextField name;
    private JPasswordField pw;
    private JButton back;
    public LoginMenu(){
        super("Login", new VektorI(290, 180));
        login = new MenuButton(this, "Login", new VektorI(10,80) , new VektorI(120,40), 20){
            public void onClick(){
                closeMenu();
                try{
                    Socket s=new Socket(ClientSettings.SERVER_ADDRESS,ClientSettings.SERVER_PORT);
                    ObjectOutputStream getPlayerOut=new ObjectOutputStream(s.getOutputStream());
                    synchronized(getPlayerOut){
                        getPlayerOut.writeBoolean(true); //Request-Client
                        getPlayerOut.flush();
                    }
                    ObjectInputStream getPlayerIn=new ObjectInputStream(s.getInputStream());
                    Player pOnServer=(Player) (new Request(-1,getPlayerOut,getPlayerIn,"Main.getPlayer",Integer.class,name.getText()).ret); //Kopie des Players am Server
                    Player player;
                    if (pOnServer!=null){
                        player=new Player(pOnServer.getID(),pOnServer.getName(),true);
                        player.synchronizeWithPlayerFromServer(pOnServer);
                    }
                    else{
                        player=Player.newPlayer(name.getText());
                    }
                    if (player!=null){
                        player.login();
                    }
                    s.close();
                }
                catch(Exception e){
                    System.out.println("Exception when creating socket: "+e);
                }
            }};
        back = new MenuButton(this, "Zurück", new VektorI(140,80) , new VektorI(120,40), 20){
            public void onClick(){
                closeMenu();
                new StartMenu();
            }};
        name = new MenuTextField(this, "Benutzername", new VektorI(10,20), new VektorI(250,20));
        pw = new MenuPasswordField(this, "Passwort", new VektorI(10,50), new VektorI(250,20));
    }
} 