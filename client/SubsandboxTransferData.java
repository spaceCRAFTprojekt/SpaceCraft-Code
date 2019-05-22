package client;
import java.io.Serializable;
import util.geom.VektorD;
public class SubsandboxTransferData implements Serializable{
    boolean isPlanet;
    int index; //Index der Sandbox im dazugehörigen statischen Array auf dem Server (planetCs oder shipCs)
    VektorD offset; //bezogen auf die Sandbox, in der sich der Spieler gerade befindet
    public SubsandboxTransferData(boolean ip, int i, VektorD o){
        isPlanet=ip;
        index=i;
        offset=o;
    }
}