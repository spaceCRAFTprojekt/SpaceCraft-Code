package menu;
import geom.VektorI;
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
    public MenuLabel(Menu m, String text, VektorI pos,VektorI size, int fontSize)
    {
        // Erstellt ein neues Label
        super();
        this.m = m;
        setBounds(pos.x, pos.y,size.x, size.y);  // Position und Größe
        setBackground(Color.GRAY);
        setForeground(new Color(0,0,0));
        setEnabled(true);
        if (fontSize == -1)fontSize = MenuSettings.MENU_FONT_SIZE;
        setFont(new Font("sansserif",0,fontSize));
        setText(text);
        setVisible(true);
        m.contentPane.add(this); // und fügt es zur Pane hinzu
    }
}

