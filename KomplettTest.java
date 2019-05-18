import server.Main;
import client.Player;
import client.menus.StartMenu;
public class KomplettTest{
    public Main main;
    public Player player;
    public StartMenu startmenu;
    public KomplettTest(){
        main=Main.newMain(false);
        startmenu = new StartMenu();
    }

    public void exit(){
        main.exit();
    }
} 