package client.menus;
import menu.*;
import util.geom.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import client.Player;
public class WorkspaceMenu{
    //Ja, man kann geschachtelte Klassen so hernehmen. Aber es fühlt sich gewalttätig an. -LG
    public static class Open extends PlayerMenu{
        public Open(Player p){
            super(p,"Arbeitsweltraum erstellen", new VektorI(300,120));
            new MenuLabel(this, "Wollen Sie einen Arbeitsweltraum erstellen?", new VektorI(10,10), new VektorI(300,20));
            new MenuButton(this, "Ja", new VektorI(170,40), new VektorI(100, 30)){
                public void onClick(){
                    p.getPlayerS().openWorkspace();
                    closeMenu();
                }
            };
            new MenuButton(this,"Nein",new VektorI(10,40),new VektorI(100,30)){
                public void onClick(){
                    closeMenu();
                }
            };
        }
    }
    public static class Close extends PlayerMenu{
        public Close(Player p){
            super(p,"Arbeitsweltraum schließen",new VektorI(300,170));
            new MenuLabel(this, "Wollen Sie den Arbeitsweltraum schließen", new VektorI(10,10), new VektorI(300,15));
            new MenuLabel(this, "und die geplanten Manöver anwenden?", new VektorI(10,25), new VektorI(300,15));
            new MenuButton(this, "Ja", new VektorI(10,85), new VektorI(260, 30)){
                public void onClick(){
                    p.getPlayerS().closeWorkspace(true);
                    closeMenu();
                }
            };
            new MenuButton(this, "Verwerfen", new VektorI(10,45), new VektorI(100, 30)){
                public void onClick(){
                    p.getPlayerS().closeWorkspace(false);
                    closeMenu();
                }
            };
            new MenuButton(this,"Nein",new VektorI(170,45),new VektorI(100,30)){
                public void onClick(){
                    closeMenu();
                }
            };
        }
    }
} 