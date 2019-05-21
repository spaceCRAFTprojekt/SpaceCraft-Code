package client.menus;
import javax.swing.*;
import util.geom.VektorI;
import menu.*;
import client.ClientSettings;
/**
 * wird angezeigt beim Start des Spiels:
 * Möglichkeit der Kartenauswahl und Starten des Spiels.
 * VG von MH 06.05.2019
 */
public class StartMenu extends Menu{
    private JList worldlist;
    private JLabel label1;
    private JButton playbutton;
    public StartMenu(){
        super("Hauptmenü", new VektorI(440, 460));
        label1 = new MenuLabel(this, "Hier könnte ihre Werbung stehen", new VektorI(10,10) ,new VektorI(250,30));
        worldlist = new MenuList(this, new String[]{"localhost:30000"}, new VektorI(10,50) ,new VektorI(250,350), 15);
        playbutton = new MenuButton(this, "Spielen", new VektorI(280,360) , new VektorI(120,40), MenuSettings.MENU_BIG_FONT){
            public void onClick(){
                String str=(String) worldlist.getSelectedValue();
                if (str!=null){
                    String[] spl=str.split(":");
                    ClientSettings.SERVER_ADDRESS=spl[0];
                    ClientSettings.SERVER_PORT=Integer.parseInt(spl[1]);
                    closeMenu();
                    new LoginMenu();
                }
            }};
        repaint();
    }
}