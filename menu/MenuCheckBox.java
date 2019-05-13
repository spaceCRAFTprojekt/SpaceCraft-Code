package menu;
import util.geom.VektorI;
import javax.swing.JCheckBox;
import java.awt.Color;
import java.awt.Font;
/**
 * Vereinfachung für eine CheckBox
 */
public class MenuCheckBox extends JCheckBox{
    private Menu m;
    public MenuCheckBox(Menu m, String text, VektorI pos,VektorI size, int fontSize){
        // Erstellt ein neues Label
        super();
        this.m = m;
	setBounds(pos.x, pos.y,size.x, size.y);
	setBackground(new Color(192,192,192));
	setForeground(new Color(0,0,0));
	setEnabled(true);
	if (fontSize == -1)fontSize = MenuSettings.MENU_FONT_SIZE;
	setFont(new Font("sansserif",0,fontSize));
	setText(text);
	setVisible(true);
	m.contentPane.add(this); // und fügt es zur Pane hinzu
    }
}
