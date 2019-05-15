package client.menus;

import client.*;
import javax.swing.*;
import util.geom.VektorI;
import menu.*;
/**
 * wird angezeigt beim Start des Spiels:
 * Möglichkeit der Kartenauswahl und Starten des Spiels.
 * VG von MH 06.05.2019
 */
public class StartMenu extends Menu{
    private JList worldlist;
    private JLabel label1;
    private JButton playbutton;
    private JButton newworldbutton;
    private JButton serverbutton;
    private JCheckBox survivalbox;
    private JCheckBox hostbox;
    public StartMenu(){
        super("Hauptmenü", new VektorI(440, 460));
        label1 = new MenuLabel(this, "Welten:", new VektorI(10,10) ,new VektorI(90,30), 20);
        worldlist = new MenuList(this, new String[]{"Hallo", "Hallo2"}, new VektorI(10,50) ,new VektorI(250,350), 15);
        playbutton = new MenuButton(this, "Spielen", new VektorI(280,360) , new VektorI(120,40), 20){
            public void onClick(){
                closeMenu();
                new LoginMenu();

            }};
        newworldbutton = new MenuButton(this, "Neue Welt", new VektorI(280,310) , new VektorI(120,40), 15){
            public void onClick(){
                closeMenu();
            }};
        serverbutton = new MenuButton(this, "Server suchen", new VektorI(280,260) , new VektorI(120,40), 12){
            public void onClick(){
                closeMenu();
            }};
        survivalbox = new MenuCheckBox(this, "Survival", new VektorI(280,50) , new VektorI(120,40), 15);
        hostbox = new MenuCheckBox(this, "Hosten", new VektorI(280,80) , new VektorI(120,40), 15);
        repaint();
    }
}
