package server;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
/**
 * "Die auf die einzelnen Moleküle eines Körpers verteilte Bewegungsenergie geht stets von einem weniger wahrscheinlichen Verteilungszustand in
 * einen wahrscheinlicheren über, nicht aber umgekehrt. Sind z. B. alle Luftmoleküle zu Anfang in einer Ecke eines Zimmers, so verteilen sie sich
 * gleichmäßig in diesem Zimmer: die Entropie nimmt zu. Es ist jedoch praktisch ausgeschlossen, dass umgekehrt die gleichmäßig verteilten Moleküle
 * sich einmal alle in einer Zimmerecke ansammeln." ~ Linus beim Programmieren
 * 
 * Denk mal bitte drüber nach Linus!
 * 
 * Ich (unknown) glaube, dass die Klasse dafür verantwortlich ist, dass man den alten Spielstand laden kann. Wir haben (bis Linus sich Gedanken gemacht hat)
 * nur die Möglichkeit das ganze als BlackBox zu betrachten. Immerhin geht es :-)
 * Diese Klasse speichert den Spielstand in der Datei gamesaves/main.ser.
 */
public class Serializer{
    public static void serialize(Main main){
        new File(Settings.GAMESAVE_FOLDER).mkdirs();
        try{
            FileOutputStream fos=new FileOutputStream(Settings.GAMESAVE_FOLDER+File.separator+main.name+".ser");
            //nur eine (fast) leere Datei wird dorthin geschrieben, lässt sich das nicht vermeiden?
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(main);
            oos.close();
            fos.close();
        }
        catch(Exception e){
            System.out.println("Exception when serializing: "+e);
        }
    }
    
    public static Main deserialize(String name) throws IOException,ClassNotFoundException{
        FileInputStream fis=new FileInputStream(Settings.GAMESAVE_FOLDER+File.separator+name+".ser");
        ObjectInputStream ois=new ObjectInputStream(fis);
        Main main = (Main) ois.readObject();
        ois.close();
        fis.close();
        return main;
    }
}