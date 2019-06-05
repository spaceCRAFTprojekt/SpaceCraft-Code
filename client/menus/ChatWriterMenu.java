package client.menus;
import menu.*;
import util.geom.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import client.Player;
public class ChatWriterMenu extends PlayerMenu{
    public MenuTextField mtf;
    public MenuButton mb;
    public ChatWriterMenu(Player p){
        super(p,"Chat-Nachricht: ", new VektorI(300,160));
        new MenuLabel(this, "Chat-Nachricht: ", new VektorI(10,10), new VektorI(100,30));
        mtf = new MenuTextField(this,"",new VektorI(10,40),new VektorI(260,20));
        mb = new MenuButton(this, "Senden", new VektorI(170,80), new VektorI(100, 30)){
            public void onClick(){
                p.writeIntoChat(mtf.getText());
                closeMenu();
            }
        };
        KeyAdapter kl=new KeyAdapter(){
            public void keyTyped(KeyEvent e){
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    mb.onClick();
                }
            }
        };
        addKeyListener(kl);
        mtf.addActionListener(new ActionListener(){ //automatisch bei Enter aufgerufen
            public void actionPerformed(ActionEvent e) {
                mb.onClick();
            }
        });
    }
}