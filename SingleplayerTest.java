import server.Main;
import client.Player;
public class SingleplayerTest{
    public Main main;
    public Player player;
    public Player player2;
    public SingleplayerTest() throws Exception{
        main=Main.newMain("main1",true,false);
        main.start();
        player=Player.newPlayer("Singleplayer","pw1");player.login("pw1");
        /*
        for (int i=0;i<10;i++){
            Player p=Player.newPlayer("p"+i,"pw"+i);p.login("pw"+i);
        }//*/
        //player2=Player.newPlayer("unknown","pw2"); player2.login("pw2");
    }
    
    public void exit(){
        main.exit();
    }
    public static void main(String[] args){try{new SingleplayerTest();}catch(Exception e){}}
}