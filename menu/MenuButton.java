package menu;
import util.geom.VektorI;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/**
 * Vereinfachung für den Button
 * Gibt einen Button mit Listener zurück
 */
public abstract class MenuButton extends JButton
{
    private Menu m;
    /**
     * Constructor for objects of class MenuButton
     */
    public MenuButton(Menu m, String text, VektorI pos, VektorI size)
    {
        // Erstellt einen neuen Button
        super();
        this.m = m;
        setBounds(pos.x, pos.y,size.x,size.y);  // Position und Größe
        setBackground(Color.GRAY);
        setForeground(new Color(0,0,0));
        setEnabled(true);
        setFont(new Font("sansserif",0,MenuSettings.MENU_FONT_SIZE));
        setText(text);
        setVisible(true);
        m.contentPane.add(this); // und fügt ihn zur Pane hinzu
        // und fügt einen "MouseListener" hinzu, der eine Methode aufruft, wenn der Button gedrückt wird
        addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    onClick();
                }
            });
    }

    public abstract void onClick();
}
