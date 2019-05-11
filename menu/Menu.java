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
import geom.VektorI;
import client.Player;

/**
 * @info:
 * Dient als Vorlage für Menüs  // Plural ist um die Uhrzeit auch schwer
 * Sollten nur clientseitig konstruiert werden! (Task newMenu o.Ä., wenns vom Server ausgeht)
 * 
 * @rewrite
 * wird auch in Blöcken (z.B. Blocks_Note) intern verwendet!!!
 * 
 * v0.1.8_AK Umschreiben als abstracte Klasse
 */
public abstract class Menu extends JFrame {
    protected Player p;
    JPanel contentPane;
    //Constructor 
    public Menu(Player p, String title, VektorI size){
        this.setTitle(title);
        this.setSize(size.x, size.y);
        this.p=p;
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                closeMenu();
            }
        });
        //Erstellt einen neue "Pane", auf die Grafikelemente ge"tan" werden können
        contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(230,165));
        contentPane.setBackground(new Color(192,192,192));

        // Bringt diese "Platte" = Pane auf das Fenster (JFrame) 
        this.add(contentPane);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        p.openMenu(this);
        
        initComponents();
    }
    
    public void initComponents(){}
    
    public void closeMenu(){
        p.removeMenu();
        dispose();
    }
    
    public Player getPlayer(){
        return p;
    }
}