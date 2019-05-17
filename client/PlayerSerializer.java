package client;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
public class PlayerSerializer{
    //sollte noch nicht verwendet werden
    static String filename="player";
    static String fileEnding=".ser";
    public static void serialize(Player player){
        new File(ClientSettings.PLAYER_SAVE_FOLDER).mkdirs();
        try{
            FileOutputStream fos=new FileOutputStream(ClientSettings.PLAYER_SAVE_FOLDER+File.separator+filename+fileEnding);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(player);
        }
        catch(Exception e){
            System.out.println("Exception when serializing player (on client): "+e);
        }
    }
    
    public static Player deserialize() throws IOException,ClassNotFoundException{
        FileInputStream fis=new FileInputStream(ClientSettings.PLAYER_SAVE_FOLDER+File.separator+filename+fileEnding);
        ObjectInputStream ois=new ObjectInputStream(fis);
        Player player = (Player) ois.readObject();
        return player;
    }
}