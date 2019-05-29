package client;
import java.io.Serializable;
import util.geom.VektorD;
public class SubsandboxTransferData implements Serializable{
    int index; //Index der Sandbox in der Space.masses-Liste
    VektorD offset; //bezogen auf die Sandbox, in der sich der Spieler gerade befindet
    VektorD vel; //Geschwindigkeit
    public SubsandboxTransferData(int index, VektorD offset, VektorD vel){
        this.index=index;
        this.offset=offset;
        this.vel=vel;
    }
}