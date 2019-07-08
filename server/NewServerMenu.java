package server;
import java.io.File;
import util.geom.VektorI;
import client.menus.StartMenu;
import menu.*;
public class NewServerMenu extends Menu{
    private MenuTextField name;
    private MenuCheckBox hostbox;
    public NewServerMenu(StartMenu startmenu){
        super("Neuen Server erstellen",new VektorI(380,170));
        new MenuLabel(this, "Name", new VektorI(10,20), new VektorI(80,20));
        name = new MenuTextField(this, "", new VektorI(100,20), new VektorI(250,20));
        hostbox=new MenuCheckBox(this, "Server hosten",new VektorI(10,50),new VektorI(140,20),12);
        new MenuButton(this, "Erstellen", new VektorI(10,80) , new VektorI(120,40), MenuSettings.MENU_BIG_FONT){
            public void onClick(){
                String n=name.getText();
                if (n.length()>0){
                    if (!new File(Settings.GAMESAVE_FOLDER).exists()){
                        new File(Settings.GAMESAVE_FOLDER).mkdirs();
                    }
                    for (File f: new File(Settings.GAMESAVE_FOLDER).listFiles()){
                        if (f.getName().equals(n+".ser")){
                            System.out.println("Ein Server des Names "+n+" existiert bereits.");
                            closeMenu();
                            return;
                        }
                    }
                    try{
                        Main.newMain(n,!hostbox.isSelected(),false);
                    }
                    catch(Exception e){e.printStackTrace();}
                    startmenu.updateLocalWorldlist();
                    closeMenu();
                }
            }
        };
        new MenuButton(this, "Schlieﬂen", new VektorI(230,80) , new VektorI(120,40), MenuSettings.MENU_BIG_FONT){
            public void onClick(){
                closeMenu();
            }
        };
        repaint();
    }
}