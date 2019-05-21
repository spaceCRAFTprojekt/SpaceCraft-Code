package menu;
import java.awt.Font;
import java.awt.Color;
import util.geom.VektorI;

public abstract class MenuSettings{
    public static int MENU_FONT_SIZE=12;
    public static int MENU_BIG_FONT_SIZE = 20;
    public static int MENU_HEAD_FONT_SIZE = 30;
    public static Color BACKGROUND_COLOR = new Color(192,192,192);
    public static VektorI INV_SIZE=new VektorI(10,4);  // größe des Spielerinventars  (in Slots)
    public static Font MENU_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, MENU_FONT_SIZE);
    public static Font MENU_BIG_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, MENU_BIG_FONT_SIZE);
    public static Font MENU_HEAD_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, MENU_HEAD_FONT_SIZE);
}