package menu;
import util.geom.VektorI;
import javax.swing.JList;
import java.awt.Color;
import java.awt.Font;
/**
 * Vereinfachung für eine Liste
 */
public class MenuList extends JList{
    private Menu m;
    public MenuList(Menu m, String[] text, VektorI pos,VektorI size, int fontSize){
        // Erstellt eine neue Liste
        super(text);
        this.m = m;
        setBounds(pos.x, pos.y,size.x, size.y);  // Position und Größe
        setBackground(Color.WHITE);
	setForeground(new Color(0,0,0));
	setEnabled(true);
	if (fontSize == -1)fontSize = MenuSettings.MENU_FONT_SIZE;
	setFont(new Font("sansserif",0,fontSize));
	setVisible(true);
	m.contentPane.add(this); // und fügt es zur Pane hinzu
    }
}
