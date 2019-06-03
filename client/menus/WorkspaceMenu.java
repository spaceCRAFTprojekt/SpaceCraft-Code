package client.menus;
import menu.*;
import util.geom.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import client.Player;
public class WorkspaceMenu{
    //Ja, man kann geschachtelte Klassen so hernehmen. Aber es f√ºhlt sich gewaltt√§tig an. -LG
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
            super(p,"Arbeitsweltraum schlie√üen",new VektorI(300,170));
            new MenuLabel(this, "Wollen Sie den Arbeitsweltraum schlie√üen", new VektorI(10,10), new VektorI(300,15));
            new MenuLabel(this, "und die geplanten Man√∂ver anwenden?", new VektorI(10,25), new VektorI(300,15));
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
    public static class SelectManoeuvres extends PlayerMenu{
        public SelectManoeuvres(Player p){
            super(p,"Manˆver w‰hlen",new VektorI(p.getPlayerS().getWorkspace().getNumControllables()*120+16,200));
            int index=0; //Da ja nur die kontrollierbaren Schiffe angezeigt werden, ist das nicht i
            for (int i=0;i<p.getPlayerS().getWorkspace().masses.size();i++){
                if (p.getPlayerS().getWorkspace().masses.get(i).isControllable(getPlayer().getID())){
                    String[] strs=new String[p.getPlayerS().getWorkspace().masses.get(i).getManoeuvres().size()+1];
                    strs[0]="Neues Manˆver";
                    for (int j=0;j<strs.length-1;j++){
                        strs[j+1]=Integer.toString(j);
                    }
                    MenuList list=new MenuList(this,strs,new VektorI(index*120,30),new VektorI(120,200),MenuSettings.MENU_FONT_SIZE);
                    final int massIndex=i; //aus irgendeinem Grund kann man vom MenuButton aus nur finale Variablen referenzieren
                    //recht h‰sslich
                    new MenuButton(this,"Editieren",new VektorI(index*120,0),new VektorI(120,30)){
                        public void onClick(){
                            if (list.getSelectedIndex()==-1)
                                return;
                            int k;
                            closeMenu();
                            if (list.getSelectedIndex()==0)
                                k=-1; //neues Manˆver
                            else{
                                k=Integer.parseInt((String) list.getSelectedValue());
                            }
                            p.openMenu(new ManoeuvreInfo(p,massIndex,k));
                        }
                    };
                    index++;
                }
            }
        }
    }
} 