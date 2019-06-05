package client;

 
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.border.Border;
import javax.swing.*;
import menu.MenuSettings;
import client.Player;
import util.geom.VektorI;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

/**
 * Beschreiben Sie hier die Klasse ChatPanel.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class ChatPanel extends JPanel
{
    private JLabel pause;
    private JList chatlist;
    private String[] list = new String[5];
    private JTextField chatwrite; 
    public ChatPanel(VektorI screenSize, OverlayPanelA opA){
        this.setLayout(null);
        this.setBounds(0,0,screenSize.x,100);
        this.setEnabled(false);
        setBackground(new Color(0,0,0,0)); 
        
        //JList
        chatlist = new JList();
        chatlist.setBounds(0,0,screenSize.x,100);  // Position und Größe
        chatlist.setBackground(new Color(0,0,0,0));
        chatlist.setEnabled(false);
        chatlist.setFont(MenuSettings.MENU_FONT);
        chatlist.setForeground(new Color(0,0,0));
        chatlist.setVisible(true);
        add(chatlist);  // und fügt es zur Pane hinzu
        opA.add(this);
        this.setVisible(true);
    }
    
    public void update(){
        chatlist.setListData(list);
    }
    
    public void add(String msg){
        for (int i=0; i<list.length; i++){
            if (i+1 < list.length){
                list[i] = list[i+1];
            }
            else{
                list[i] = msg;
            }
        }
        update();
    }
}
