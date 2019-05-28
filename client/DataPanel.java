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
import client.PlayerC;
import javax.swing.JLabel;

public class DataPanel extends JPanel{
    private JLabel data;
    private PlayerC pC;
    public DataPanel(VektorI screenSize, PlayerC pC, OverlayPanel op){
        this.setLayout(null);
        this.setBounds(0,100,screenSize.x,20);
        this.setEnabled(false);
        setBackground(new Color(0,0,0,0));
        this.pC = pC;

        data = new JLabel();
        data.setBounds(0, 0,screenSize.x, 20);  // Position und Größe
        data.setBackground(new Color(0,0,0,0));
        data.setForeground(new Color(0,0,0));
        data.setEnabled(true);
        data.setFont(MenuSettings.MENU_FONT);
        update();
        data.setVisible(true);
        this.add(data);
        
        op.add(this);    
        this.setVisible(true);
    }
    
    public void toggleVisibility(){
        if (isVisible()) setVisible(false);
        else setVisible(true);
    }
        
    public void update(){
        data.setText("posx: " + pC.pos.x  + "  posy: " + pC.pos.y);
    }
}
