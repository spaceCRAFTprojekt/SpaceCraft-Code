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
 * Die wichtige Funktion ist read/writeObject. Beim (de-)serialisieren von Main passiert eigentlich gar nichts
 * (alle Attribute sind transient), aber es wird Main.readResolve/Main.writeReplace (automatisch) aufgerufen. Diese machen
 * dann die eigentliche Arbeit.
 */
public class Serializer{
    public static void serialize(Main main){
        new File(Settings.GAMESAVE_FOLDER).mkdirs();
        try{
            FileOutputStream fos=new FileOutputStream(Settings.GAMESAVE_FOLDER+File.separator+"main.ser");
            //nur eine (fast) leere Datei wird dorthin geschrieben, lässt sich das nicht vermeiden?
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(main);
        }
        catch(Exception e){
            System.out.println("Exception when serializing: "+e);
        }
    }
    
    public static Main deserialize() throws IOException,ClassNotFoundException{
        FileInputStream fis=new FileInputStream(Settings.GAMESAVE_FOLDER+File.separator+"main.ser");
        ObjectInputStream ois=new ObjectInputStream(fis);
        Main main = (Main) ois.readObject();
        return main;
    }
}