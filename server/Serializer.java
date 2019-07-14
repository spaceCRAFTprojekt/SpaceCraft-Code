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
    /**
     * (De-)Serialisierungsvorg�nge sollten nicht unterbrochen werden, also gibt es diesen boolean,
     * auf den getestet werden sollte.
     * (Es sollten eigentlich nie zwei (De-)Serialisierungsvorg�nge gleichzeitig stattfinden,
     * also reicht ein statisches Feld.)
     */
    public static volatile boolean currentlyWorking=false;
    
    public static void serialize(Main main){
        currentlyWorking=true;
        System.out.println("[Server]: Serialisieren einer Welt. Das kann eine Weile dauern...");
        new File(Settings.GAMESAVE_FOLDER).mkdirs();
        try(FileOutputStream fos=new FileOutputStream(Settings.GAMESAVE_FOLDER+File.separator+main.name+".ser");
                ObjectOutputStream oos=new ObjectOutputStream(fos)){
            oos.writeObject(main);
            System.out.println("[Server]: Serialisierung abgeschlossen");
        }
        catch(Exception e){
            System.out.println("Exception when serializing: "+e);
        }
        currentlyWorking=false;
    }
    
    public static Main deserialize(String name) throws IOException,ClassNotFoundException{
        currentlyWorking=true;
        System.out.println("[Server]: Deserialisieren einer Welt. Das kann eine Weile dauern...");
        Main main;
        try(FileInputStream fis=new FileInputStream(Settings.GAMESAVE_FOLDER+File.separator+name+".ser");
                ObjectInputStream ois=new ObjectInputStream(fis)){
            main = (Main) ois.readObject();
        }
        System.out.println("[Server]: Deserialisierung abgeschlossen");
        currentlyWorking=false;
        return main;
    }
}