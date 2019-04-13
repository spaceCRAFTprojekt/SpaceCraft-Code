import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import geom.VektorI;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
/**
 * Vergleichbar mit JFrame
 * Sozusagen ein Frame für alles was in Craft abläuft.
 */
public class CPanel extends JPanel
{
    PlayerC playerC;
    JButton testButton;
    /**
     * Konstruktor für Objekte der Klasse CPane
     */
    public CPanel(PlayerC playerC, VektorI screenSize)
    {
        super();
        setPreferredSize(new Dimension(screenSize.x, screenSize.y));
        setBackground(new Color(204, 204, 255));
        setOpaque(false);
        JButton testButton = new JButton("test");
        testButton.setBounds(71,110,90,35);
        testButton.setEnabled(true);        
        testButton.setVisible(true);
        add(testButton);
        testButton.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    System.out.println("Hi");
                }
            });
        setVisible(true);

    }

    public void paint(Graphics g){
        paintComponents(g);
    }
}
