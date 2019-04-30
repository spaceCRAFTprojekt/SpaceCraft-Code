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

/**
 * Wird angezeigt, wenn man esc drügggt...  // es ist kurz vor 0 Uhr; ich kann nicht mehr schreiben
 * Gibt die Möglichkeit das Spiel zu beenden oder weiterzuspielen
 * 
 * @info:
 * Dient als Vorlage für Menüs  // Plural ist um die Uhrzeit auch schwer
 */
public class EscapeMenu extends JFrame {
    private JLabel pause;
    private JButton restart;
    private JButton exit;
    private Player p;
    //Constructor 
    public EscapeMenu(Player p){
        this.p = p;
        this.setTitle("Pause");
        this.setSize(225,270);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void WindowClosing(WindowEvent e) {
                    closeMenu();
                }
            });

        //Erstellt einen neue "Pane", auf die Grafikelemente ge"tan" werden können
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(230,165));
        contentPane.setBackground(new Color(192,192,192));

        // erstellt ein neues Label
        pause = new JLabel();
        pause.setBounds(60,19,90,35);  // Position und Größe
        pause.setBackground(new Color(214,217,223));
        pause.setForeground(new Color(0,0,0));
        pause.setEnabled(true);
        pause.setFont(new Font("SansSerif",0,30));
        pause.setText("Pause");
        pause.setVisible(true);
        contentPane.add(pause);  // und fügt es zur Pane hinzu
        
        // Erstellt einen neuen Button
        restart = new JButton();
        restart.setBounds(30,110,150,35);  // Position und Größe
        restart.setBackground(Color.GRAY);
        restart.setForeground(new Color(0,0,0));
        restart.setEnabled(true);
        restart.setFont(new Font("sansserif",0,12));
        restart.setText("Fly on");
        restart.setVisible(true);
        contentPane.add(restart); // und fügt ihn zur Pane hinzu
        // und fügt einen "MouseListener" hinzu, der eine Methode aufruft, wenn der Button gedrückt wird
         restart.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    closeMenu();
                }
            });
        
        // noch ein Button...
        exit = new JButton();
        exit.setBounds(30,170,150,35);  // Position und Größe
        exit.setBackground(Color.GRAY);
        exit.setForeground(new Color(0,0,0));
        exit.setEnabled(true);
        exit.setFont(new Font("sansserif",0,12));
        exit.setText("Spiel beenden");
        exit.setVisible(true);
        contentPane.add(exit);
        exit.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    exit();
                }
            });
            
        // Bringt diese "Platte" = Pane auf das Fenster (JFrame) 
        this.add(contentPane);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    public void closeMenu(){
        p.activate();
        dispose();
    }
    public void exit(){
        p.exit();
        dispose();
    }
}