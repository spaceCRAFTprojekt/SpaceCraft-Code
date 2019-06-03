import server.Main;
import client.Player;
import client.menus.StartMenu;
public class SingleplayerTest{
    public Main main;
    public Player player;
    public Player player2;
    public SingleplayerTest(){
        main=Main.newMain(false);
        player=Player.newPlayer("Singleplayer","pw1");player.login("pw1");
        //player2=Player.newPlayer("unknown"); player2.login();
    }
    
    public void exit(){
        main.exit();
    }
    public static void main(String[] args){new SingleplayerTest();}
}