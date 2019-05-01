import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import geom.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.io.ObjectStreamException;
/**
 * ein Spieler in der Craft Ansicht
 */
public class PlayerC implements Serializable
{
    private transient Timer timer;

    private int blockBreite = 32;  // Breite eines Blocks in Pixeln
    private Player player;
    private Sandbox sandbox;
    private VektorD pos;
    private transient BufferedImage texture;
    private VektorD hitbox = new VektorD(1,2);
    public PlayerC(Player player, Sandbox sandbox, VektorD pos, Frame frame)
    {
        this.player = player;
        setSandbox(sandbox, pos);
        makeTexture();
        this.timer=new Timer();
        timerSetup();
    }

    private void makeTexture(){
        texture = ImageTools.get('C',"player_texture");
    }

    private void timerSetup(){
        timer.schedule(new TimerTask(){
                public void run(){
                    repaint();
                }
            },0,Settings.PLAYERC_TIMER_PERIOD);
    }

    Object readResolve() throws ObjectStreamException{
        this.makeTexture();
        this.timer=new Timer();
        this.timerSetup();
        return this;
    }

    /**
     * Setze Spieler in einer andere Sandbox
     */
    public void setSandbox(Sandbox sandbox, VektorD pos){
        this.sandbox = sandbox;
        this.pos = pos;
    }

    /**
     * Tastatur event
     * @param:
     *  char type: 'p': pressed
     *             'r': released
     *             't': typed (nur Unicode Buchstaben)
     */
    public void keyEvent(KeyEvent e, char type) {
        if (type == 'p'){
            //System.out.println("KeyEvent in PlayerC: "+e.getKeyChar()+type);
            switch(Character.toLowerCase(e.getKeyChar())){
                case 'w': pos.y=pos.y - 1; // up
                break;
                case 's': pos.y=pos.y + 1; // down
                break;
                case 'a': pos.x=pos.x - 1; // left
                break;
                case 'd': pos.x=pos.x + 1; // right
                break;
            }
            System.out.println(pos.toString());
        }
    }

    /**
     * Maus Event
     * @param:
     *  char type: 'p': pressed
     *             'r': released
     *             'c': clicked
     *             'd': dragged
     * entered und exited wurde nicht implementiert, weil es dafür bisher keine Verwendung gab
     */
    public void mouseEvent(MouseEvent e, char type) {
        if (type == 'c'){
            VektorI clickPos = new VektorI(e);
                VektorI sPos = sandbox.getPosToPlayer(clickPos, pos, blockBreite);
            if (e.getButton() == e.BUTTON1){   // rechtsklick => abbauen
                //System.out.println("Tried to break block at "+sPos.toString());
                sandbox.breakBlock(sPos, player);
            }else if (e.getButton() == e.BUTTON3){  // rechtsklick => platzieren
                //System.out.println("Tried to place block at "+sPos.toString());
                sandbox.rightclickBlock(Blocks.blocks.get(2), sPos, player);
            }
        }
    }

    /**
     * Grafik ausgeben
     */
    public void paint(Graphics g, VektorI screenSize){
        sandbox.paint(g, screenSize, pos, blockBreite);
        g.drawImage(texture, (screenSize.x-20)/2, (screenSize.y-32)/2,40, 64, null);
    }

    public void repaint(){
        player.repaint();
    }
}