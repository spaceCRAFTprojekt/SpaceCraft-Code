package client.menus;

import menu.*;
import util.geom.*;
import client.*;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import util.ImageTools;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/**
 * Write a description of class TextureSelectMenu here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class TextureSelectMenu extends PlayerMenu
{
    public static VektorI size = new VektorI(7,2);
    public static int textureSize = 64;
    public TextureSelectMenu(Player p){
        super(p, "Select Texture", new VektorD(size.x*textureSize*1.1+20,size.y*textureSize*2.1+20).toInt());
        this.setLayout(null);
        //this.setLayout(new GridLayout(size.y, size.x,(int)(32*0.1),(int)(32*0.1)));  // ich hab mich schon mal aufgeregt, das beim Gridlayout x und y vertauscht ist
        for(int i = 0; i<size.x*size.y; i++){
            if(PlayerTexture.textures.size()<=i)break;
            JButton b = new JButton(new ImageIcon(ImageTools.resize(PlayerTexture.getTexture(i),new VektorI(textureSize, textureSize*2))));
            b.setBounds((int)(textureSize*1.1*(double)(i%size.x)),(int)(textureSize*2.1*(i/size.x)),textureSize, textureSize*2);
            b.setName(i+"");  // Kreativ: Man speichert die id im namen xD. Diese Kreativität wurde ihnen programmiert von unknown
            b.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    p.getPlayerC().setPlayerTexture( Integer.parseInt(b.getName()) );
                    p.getPlayerC().synchronizePlayerTexture();
                    closeMenu();
                }
            });
            b.setEnabled(true);
            add(b);
            b.setVisible(true);
        }

        validate();
        repaint();
    }
}