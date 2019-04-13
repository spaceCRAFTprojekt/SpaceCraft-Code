import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
public class Listener implements MouseListener, KeyListener, WindowListener{
    Player p;
    public Listener(Player p){
        this.p=p;
    }
     // Key Events (Beschreibung im Player):
    public void keyTyped(KeyEvent e) {
        p.keyEvent(e,'t');
    }
    public void keyPressed(KeyEvent e) {
        p.keyEvent(e,'p');
    }
    public void keyReleased(KeyEvent e) {
        p.keyEvent(e,'r');
    } 

    // Mouse Events:
    public void mouseClicked(MouseEvent e) {
        p.mouseEvent(e,'c');
        //System.out.println("Mouse " + e.getClickCount() + " times clicked at "+ e.getPoint());
    }

    public void mousePressed(MouseEvent e) {
        p.mouseEvent(e,'p');
        //System.out.println("Mouse " + e.getClickCount() + " times pressed at "+ e.getPoint());
    }

    public void mouseReleased(MouseEvent e) {
        p.mouseEvent(e,'r');
        //System.out.println("Mouse " + e.getClickCount() + " times released at "+ e.getPoint());
    }

    public void mouseEntered(MouseEvent e) {}  // "Nichts ist besser als Nicht" ~ DSDS Teilnehmer
    public void mouseExited(MouseEvent e) {}
    
    public void windowClosing(WindowEvent e){
        p.windowEvent(e,'c');
    }
    
    public void windowActivated(WindowEvent e){}
    public void windowClosed(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowOpened(WindowEvent e){}
}