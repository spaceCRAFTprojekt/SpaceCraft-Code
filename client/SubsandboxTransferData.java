package client;
import java.io.Serializable;
import util.geom.VektorD;
public class SubsandboxTransferData implements Serializable{
    int index; //Index der Sandbox in der Space.masses-Liste
    VektorD offset; //bezogen auf die Sandbox, in der sich der Spieler gerade befindet
    public SubsandboxTransferData(int index, VektorD offset){
        this.index=index;
        this.offset=offset;
    }
}