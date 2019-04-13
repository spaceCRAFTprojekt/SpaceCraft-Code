import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
public class Serializer{
    public static void serialize(Main main){
        try{
            FileOutputStream fos=new FileOutputStream(Main.folder+File.separator+"main.ser");
            //nur eine (fast) leere Datei wird dorthin geschrieben, lässt sich das nicht vermeiden?
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(main);
        }
        catch(Exception e){
            
        }
    }
    
    public static Main deserialize() throws IOException,ClassNotFoundException{
        FileInputStream fis=new FileInputStream(Main.folder+File.separator+"main.ser");
        ObjectInputStream ois=new ObjectInputStream(fis);
        Main main = (Main) ois.readObject();
        return main;
    }
}