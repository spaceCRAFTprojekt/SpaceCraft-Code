import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import client.menus.StartMenu;
public class KomplettTest{
    public static void main(String[] args){
        try{
            System.setOut(new PrintStream("debug.txt"));
            System.setErr(new PrintStream("debug.txt"));
        }
        catch(FileNotFoundException e){}
        new StartMenu();
    }
}
