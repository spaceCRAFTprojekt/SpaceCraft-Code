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

public class EscapeMenu extends JFrame {
    private JLabel pause;
    private JButton restart;
    private Player p;
    //Constructor 
    public EscapeMenu(Player p){
        this.p = p;
        this.setTitle("Pause");
        this.setSize(230,165);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // warum geht das nicht??
        addWindowListener(new WindowAdapter() {
                public void WindowClosing(WindowEvent e) {
                    close();
                }
            });

        //pane with null layout
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(230,165));
        contentPane.setBackground(new Color(192,192,192));

        pause = new JLabel();
        pause.setBounds(73,19,90,35);
        pause.setBackground(new Color(214,217,223));
        pause.setForeground(new Color(0,0,0));
        pause.setEnabled(true);
        pause.setFont(new Font("SansSerif",0,30));
        pause.setText("Pause");
        pause.setVisible(true);

        restart = new JButton();
        restart.setBounds(71,110,90,35);
        restart.setBackground(new Color(214,217,223));
        restart.setForeground(new Color(0,0,0));
        restart.setEnabled(true);
        restart.setFont(new Font("sansserif",0,12));
        restart.setText("Fly on");
        restart.setVisible(true);
        //Set methods for mouse events
        //Call defined methods
        restart.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    close();
                }
            });

        //adding components to contentPane panel
        contentPane.add(pause);
        contentPane.add(restart);

        //adding panel to JFrame and setting of window position and close operation
        this.add(contentPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }
    
    public void close(){
        p.activate();
        dispose();
    }
}