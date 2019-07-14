package client.menus;
import menu.*;
import util.geom.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import client.Player;
import client.Manoeuvre;
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
    public static class SelectManoeuvres extends PlayerMenu{
        public SelectManoeuvres(Player p){
            super(p,"Manöver wählen",new VektorI(p.getPlayerS().getWorkspace().getNumControllables()*120+16,200));
            int index=0; //Da ja nur die kontrollierbaren Schiffe angezeigt werden, ist das nicht i
            for (int i=0;i<p.getPlayerS().getWorkspace().masses.size();i++){
                if (p.getPlayerS().getWorkspace().masses.get(i).isControllable(getPlayer().getID())){
                    String[] strs=new String[p.getPlayerS().getWorkspace().masses.get(i).getManoeuvres().size()+1];
                    strs[0]="Neues Manöver";
                    for (int j=0;j<strs.length-1;j++){
                        strs[j+1]=Integer.toString(j);
                    }
                    MenuList list=new MenuList(this,strs,new VektorI(index*120,30),new VektorI(120,200),MenuSettings.MENU_FONT_SIZE);
                    final int massIndex=i; //aus irgendeinem Grund kann man vom MenuButton aus nur finale Variablen referenzieren
                    //recht hässlich
                    new MenuButton(this,"Editieren",new VektorI(index*120,0),new VektorI(120,30)){
                        public void onClick(){
                            int k;
                            closeMenu();
                            if (list.getSelectedIndex()==-1 || list.getSelectedIndex()==0)
                                k=-1; //neues Manöver
                            else{
                                k=Integer.parseInt((String) list.getSelectedValue());
                            }
                            ManoeuvreInfo mi=new ManoeuvreInfo(p,massIndex,k);
                            if (k!=-1){
                                Manoeuvre m=p.getPlayerS().getWorkspace().masses.get(massIndex).getManoeuvres().get(k);
                                mi.accField.setText(Double.toString(-m.dMass));
                                mi.angleField.setText(Double.toString(Math.atan2(m.dir.y,m.dir.x)*180/Math.PI));
                                ((JTextField) mi.table[1][1]).setText(Long.toString(m.t0));
                                ((JTextField) mi.table[1][2]).setText(Long.toString(m.t1));
                                mi.angleToggle.setSelected(m.rel);
                                mi.update();
                            }
                            p.openMenu(mi);
                        }
                    };
                    index++;
                }
            }
        }
    }
} 