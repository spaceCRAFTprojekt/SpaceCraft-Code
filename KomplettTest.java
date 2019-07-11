import java.io.PrintStream;
import java.io.FileNotFoundException;
import client.menus.StartMenu;
public class KomplettTest{
    public static void main(String[] args){
        try{
            System.setOut(new PrintStream("debug.txt"));
            System.setErr(new PrintStream("debug.txt"));
        }
        catch(FileNotFoundException e){}
        
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                System.out.println("[Komplett-Test]: Shutdown-Hook läuft");
                try{
                    System.out.flush();
                    System.err.flush();
                    System.out.close();
                    System.err.close();
                }
                catch(Exception e){}
            }
        });
        new StartMenu();
    }
}
