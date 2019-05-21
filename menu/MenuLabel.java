package menu;
import util.geom.VektorI;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
/**
 * Vereinfachung für ein Label
 */
public class MenuLabel extends JLabel
{
    private Menu m;
    /**
     * Constructor for objects of class MenuLabel
     */
    public MenuLabel(Menu m, String text, VektorI pos,VektorI size, Font font){
        // Erstellt ein neues Label
        super();
        this.m = m;
        setBounds(pos.x, pos.y,size.x, size.y);  // Position und Größe
        setBackground(Color.GRAY);
        setForeground(new Color(0,0,0));
        setEnabled(true);
        setFont(font);
        setText(text);
        setVisible(true);
        m.contentPane.add(this); // und fügt es zur Pane hinzu
    }
    public MenuLabel(Menu m, String text, VektorI pos,VektorI size){
        this(m, text, pos, size, MenuSettings.MENU_FONT);
    }
}

