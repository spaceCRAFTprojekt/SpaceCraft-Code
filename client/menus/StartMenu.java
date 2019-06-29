package client.menus;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import java.awt.Dimension;
import util.geom.VektorI;
import menu.*;
import client.ClientSettings;
/**
 * wird angezeigt beim Start des Spiels:
 * Möglichkeit der Kartenauswahl und Starten des Spiels.
 * VG von MH 06.05.2019
 * Jetzt auch mit Serverkontrolle und GroupLayout:
 * VG von LG 28.06.2019
 */
public class StartMenu extends Menu{
    private GroupLayout layout;
    private JList worldlist;
    private JLabel label1;
    private JButton playbutton;
    private JTextField address;
    private JTextField port;
    public StartMenu() throws IOException{
        super("SpaceCraft", new VektorI(440, 460));
        setFont(MenuSettings.MENU_FONT);
        layout = new GroupLayout(this.getLayeredPane());
        this.getLayeredPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        worldlist=new JList();
        updateWorldlist();
        label1 = new JLabel("Hier könnte ihre Werbung stehen");
        playbutton = new JButton("Spielen"){
            public void onClick(){
                String str=(String) worldlist.getSelectedValue();
                if (str!=null){
                    String[] spl=str.split("[ :]");
                    ClientSettings.SERVER_ADDRESS=spl[1];
                    ClientSettings.SERVER_PORT=Integer.parseInt(spl[2]);
                    closeMenu();
                    new LoginMenu();
                }
                else{
                    ClientSettings.SERVER_ADDRESS=address.getText();
                    try{ClientSettings.SERVER_PORT=Integer.parseInt(port.getText());} catch(NumberFormatException e){System.out.println("Invalid Input: Port muss eine Zahl sein!");}
                    closeMenu();
                    new LoginMenu();
                }
            }};
        playbutton.setFont(MenuSettings.MENU_BIG_FONT);
        address =new JTextField("Adresse");
        port = new JTextField("Port");
        JSeparator sep1=new JSeparator(JSeparator.HORIZONTAL);
        JSeparator sep2=new JSeparator(JSeparator.VERTICAL);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup() //Die Server-Gruppe
                .addComponent(label1)
                .addComponent(sep1)
            )
            .addComponent(sep2)
            .addGroup(layout.createParallelGroup() //Die Client-Gruppe
                .addComponent(worldlist,100,150,200)
                .addComponent(address)
                .addComponent(port)
                .addComponent(playbutton)
            )
        );
        
        layout.setVerticalGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup() //Die Server-Gruppe
                .addComponent(label1)
                .addComponent(sep1)
            )
            .addComponent(sep2)
            .addGroup(layout.createSequentialGroup() //Die Client-Gruppe
                .addComponent(worldlist,250,300,350)
                .addComponent(address)
                .addComponent(port)
                .addComponent(playbutton)
            )
        );
        repaint();
    }
    
    /**
     * Update der Weltliste aus der gegebenen Datei heraus
     * Natürlich sollte eigentlich irgendwann ein Server existieren, der eine Liste aller Welten hat.
     */
    public void updateWorldlist() throws IOException{
        List<String> lines = Files.readAllLines(Paths.get(ClientSettings.SERVERLIST_FILENAME+".txt"), StandardCharsets.UTF_8);
        lines.set(0,lines.get(0).substring(1)); //irgendein Zeichen, das den Start der Datei anzeigt?
        ArrayList<String> serverList=new ArrayList<String>(); //mit Adresse und Port
        for (int i=0;i<lines.size();i++){
            int endIndex=lines.get(i).indexOf("//");
            endIndex=endIndex==-1 ? lines.get(i).length()-1 : endIndex;
            if (endIndex!=-1 && lines.get(i).substring(0,endIndex).length()!=0){
                serverList.add(lines.get(i).substring(0,endIndex));
            }
        }
        String[] servernames=new String[serverList.size()];
        servernames=serverList.toArray(servernames);
        worldlist.setListData(servernames);
    }
}