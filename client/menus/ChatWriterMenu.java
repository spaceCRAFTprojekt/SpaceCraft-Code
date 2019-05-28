package client.menus;
import menu.*;
import util.geom.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import client.Player;
public class ChatWriterMenu extends PlayerMenu{
    MenuTextArea mta;
    MenuButton mb;
    public ChatWriterMenu(Player p){
        super(p,"Chat-Nachricht: ", new VektorI(300,340));
        new MenuLabel(this, "Chat-Nachricht: ", new VektorI(10,10), new VektorI(100,30));
        mta = new MenuTextArea(this,"",new VektorI(10,40),new VektorI(260,210));
        mb = new MenuButton(this, "Senden", new VektorI(170,260), new VektorI(100, 30)){
            public void onClick(){
                p.writeIntoChat(mta.getText().replace('\n',' '));
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
    }
}