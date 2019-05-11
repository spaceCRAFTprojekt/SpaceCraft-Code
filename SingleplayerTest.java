import server.Main;
import client.Player;
public class SingleplayerTest{
    public Main main;
    public Player player;
    public SingleplayerTest(){
        main=Main.newMain(false);
        player=Player.newPlayer("Singleplayer");
        player.login();
    }
    
    public void exit(){
        main.exit();
    }
}