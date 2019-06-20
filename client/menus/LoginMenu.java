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
 * Login-Menü
 */
public class LoginMenu extends Menu{
    private JButton login;
    public Player player;
    private JTextField name;
    private JPasswordField pw;
    private JButton back;
    public LoginMenu(){
        super("Login", new VektorI(380, 180));
        new MenuLabel(this, "Benutzername", new VektorI(10,20), new VektorI(80,20));
        name = new MenuTextField(this, "", new VektorI(100,20), new VektorI(250,20));
        new MenuLabel(this, "Passwort", new VektorI(10,50), new VektorI(80,20));
        pw = new MenuPasswordField(this, "", new VektorI(100,50), new VektorI(250,20));

        login = new MenuButton(this, "Login", new VektorI(10,80) , new VektorI(120,40), MenuSettings.MENU_BIG_FONT){
            public void onClick(){
                closeMenu();
                try{
                    Socket s=new Socket(ClientSettings.SERVER_ADDRESS,ClientSettings.SERVER_PORT);
                    //Ein extra Socket nur für diesen Request, da alles andere vermutlich noch
                    //sinnloser wäre. Er wird nach 10 Sekunden durch den Timeout geschlossen.
                    ObjectOutputStream getPlayerOut=new ObjectOutputStream(s.getOutputStream());
                    synchronized(getPlayerOut){
                        getPlayerOut.writeBoolean(true); //Request-Client
                        getPlayerOut.writeInt(-1); //eigentlich playerID, hier unwichtig
                        getPlayerOut.flush();
                    }
                    ObjectInputStream getPlayerIn=new ObjectInputStream(s.getInputStream());
                    Player pOnServer=(Player) (new Request(-1,getPlayerOut,getPlayerIn,"Main.getPlayer",Integer.class,name.getText()).ret); //Kopie des Players am Server
                    Player player;
                    String password=new String(pw.getPassword());
                    if (pOnServer!=null){
                        player=new Player(pOnServer.getID(),pOnServer.getName(),true, false);
                        player.synchronizeWithPlayerFromServer(pOnServer);
                    }
                    else{
                        player=Player.newPlayer(name.getText(),password);
                    }
                    if (player!=null){
                        Boolean success=player.login(password);
                        if (!success){
                            new StartMenu(); //kein erfolgreiches Einloggen (Passwort falsch oder schon online)
                        }
                    }
                    else{
                        new StartMenu(); //kein erfolgreiches Erstellen des Spielers
                    }
                    s.close();
                }
                catch(Exception e){
                    System.out.println("Exception when creating socket: "+e);
                }
            }};
        back = new MenuButton(this, "Zurück", new VektorI(230,80) , new VektorI(120,40), MenuSettings.MENU_BIG_FONT){
            public void onClick(){
                closeMenu();
                new StartMenu();
            }};

    }
} 