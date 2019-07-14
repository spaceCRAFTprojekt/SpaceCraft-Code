package menu;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.border.Border;
import javax.swing.*;
import util.geom.VektorI;
import client.Player;
/**
 * Ein Menü mit Referenz auf einen Player
 */
public abstract class PlayerMenu extends Menu{
    private Player p;

    public PlayerMenu(Player p, String title, VektorI size){
        super(title, size);
        this.p = p;
        p.openMenu(this);
        addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode()==KeyEvent.VK_ESCAPE){
                    closeMenu();
                }
            }
        });
    }

    public void closeMenu(){
        p.removeMenu();
        super.closeMenu();
    }

    public Player getPlayer(){
        return p;
    }
}