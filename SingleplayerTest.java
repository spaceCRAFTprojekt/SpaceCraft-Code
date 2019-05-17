import server.Main;
import client.Player;
import client.menus.StartMenu;
public class SingleplayerTest{
    public Main main;
    public Player player;
    public StartMenu startmenu;
    public SingleplayerTest(){
        main=Main.newMain(false);
        //startmenu = new StartMenu();
        player=Player.newPlayer("Singleplayer");
        player.login();
    }
    
    public void exit(){
        main.exit();
    }
}