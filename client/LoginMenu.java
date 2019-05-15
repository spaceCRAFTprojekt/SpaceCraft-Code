package client;
import javax.swing.*;
import util.geom.VektorI;
import menu.*;
import client.Player;
/**
 * wird angezeigt beim Start des Spiels:
 * Möglichkeit der Kartenauswahl und Starten des Spiels.
 * VG von MH 06.05.2019
 */
public class LoginMenu extends Menu{
    public StartMenu startmenu;
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
                player = Player.newPlayer(name.getText());
                player.login();
            }};
        back = new MenuButton(this, "Zurück", new VektorI(140,80) , new VektorI(120,40), 20){
            public void onClick(){
                closeMenu();
                startmenu = new StartMenu();
            }};
        name = new MenuTextField(this, "Benutzername", new VektorI(10,20), new VektorI(250,20));
        pw = new MenuPasswordField(this, "Passwort", new VektorI(10,50), new VektorI(250,20));
    }
}