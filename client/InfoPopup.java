package client;
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
 * Kleines Infofeld in der SpaceAnsicht
 */
public class InfoPopup extends JPanel
{
    private JLabel pause;
    private JButton exit;

    public InfoPopup(){
        this.setSize(100,100);
        this.setLayout(null);
        this.setBounds(300,300,100,100);
        this.setEnabled(false);
        /*
        addWindowListener(new WindowAdapter() {
                public void WindowClosing(WindowEvent e) {

                }
            });
            */
        setBackground(new Color(192,192,192));

        // erstellt ein neues Label
        pause = new JLabel();
        pause.setBounds(60,19,30,30);  // Position und Größe
        pause.setBackground(new Color(214,217,223));
        pause.setForeground(new Color(0,0,0));
        pause.setEnabled(false);
        pause.setFont(new Font("SansSerif",0,30));
        pause.setText("Pause");
        pause.setVisible(true);
        add(pause);  // und fügt es zur Pane hinzu
        
        // noch ein Button...
        exit = new JButton();
        exit.setBounds(30,170,150,35);  // Position und Größe
        exit.setBackground(Color.GRAY);
        exit.setForeground(new Color(0,0,0));
        exit.setEnabled(false);
        exit.setFont(new Font("sansserif",0,12));
        exit.setText("Spiel beenden");
        exit.setVisible(true);
        add(exit);
        exit.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {

                }
            });
            
        this.setVisible(true);
    }
}
