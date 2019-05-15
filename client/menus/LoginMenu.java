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
public class LoginMenu extends Menu{
    private JButton playbutton;
    public LoginMenu(){
        super("Login", new VektorI(200, 100));
        playbutton = new MenuButton(this, "Spielen", new VektorI(280,360) , new VektorI(120,40), 20){
            public void onClick(){
                closeMenu();
            }};
    }
}