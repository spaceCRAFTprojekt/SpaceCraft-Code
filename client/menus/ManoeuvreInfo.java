package client.menus;

import util.geom.*;
import javax.swing.*;
import menu.*;
import client.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

/**
 * Zum einstellen eines Manoeuvres
 */
public class ManoeuvreInfo extends PlayerMenu
{
    public int massIndex;
    public int manoeuvreIndex; //das manoeuvreIndex-te Manöver der Masse mit dem MassIndex wird editiert (oder hinzugefügt). -1 bedeutet einfach hinzufügen
    private GroupLayout layout;

    private JLabel rocketInfo;
    private JSeparator separator0;
    private JLabel accLabel;
    public JTextField accField; //Diese Namen entsprechen nicht alle dem, was damit eingestellt wird. -LG
    private JLabel angleLabel;
    public JTextField angleField;
    public JToggleButton angleToggle;
    private JSeparator separator1;
    private JPanel tablePanel;
    public JComponent[][]table;
    //private JSeparator separator2;
    //private JLabel fuelCost;


    public ManoeuvreInfo(Player p, int massIndex, int manoeuvreIndex){
        super(p, "Manoeuvre Info", new VektorI(230,260));   // id muss noch gemacht werden
        this.massIndex=massIndex;
        this.manoeuvreIndex=manoeuvreIndex;

        setFont(MenuSettings.MENU_FONT);
        layout = new GroupLayout(this.getLayeredPane());
        this.getLayeredPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        rocketInfo = new JLabel("Rocket: - ;  mass: - ");
        rocketInfo.setSize(300,12);

        separator0 = new JSeparator(JSeparator.HORIZONTAL);
        separator0.setForeground(Color.gray);

        accLabel = new JLabel("dMass: ");
        accLabel.setVisible(true); 
        angleLabel = new JLabel("Angle: ");
        accField = new JTextField();
        angleField = new JTextField();
        angleToggle = new JToggleButton("rel");
        angleToggle.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode()==KeyEvent.VK_ENTER)
                    angleToggle.doClick();
            }
        });

        separator1 = new JSeparator(JSeparator.HORIZONTAL);
        separator1.setForeground(Color.gray);

        table = new JComponent[3][4];
        table[1][0] = new JLabel("Time");
        table[2][0] = new JLabel("Location");
        table[0][1] = new JLabel("Start");
        table[0][2] = new JLabel("End");
        table[0][3] = new JLabel("Duration");
        table[1][1] = new JTextField();
        table[1][2] = new JTextField();
        table[1][3] = new JLabel("");
        table[2][1] = new JLabel("");
        table[2][2] = new JLabel("");
        table[2][3] = new JLabel("");

        //separator2 = new JSeparator(JSeparator.HORIZONTAL);
        //separator2.setForeground(Color.gray);

        //fuelCost = new JLabel("Fuel Cost: - ");
        

        layout.linkSize(SwingConstants.VERTICAL, angleField, accField);
        //layout.linkSize(SwingConstants.HORIZONTAL, table[1][1], table[2][1]);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(rocketInfo)
                .addComponent(separator0)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(accLabel)
                        .addComponent(angleLabel) )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER) 
                        .addComponent(accField)
                        .addComponent(angleField) )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER) 
                        .addComponent(angleToggle) ) )
                .addComponent(separator1)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(table[0][1])
                        .addComponent(table[0][2])
                        .addComponent(table[0][3]))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER) 
                        .addComponent(table[1][0])
                        .addComponent(table[1][1],50, 50, Short.MAX_VALUE)
                        .addComponent(table[1][2])
                        .addComponent(table[1][3]) )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER) 
                        .addComponent(table[2][0])
                        .addComponent(table[2][1],50, 50, Short.MAX_VALUE)
                        .addComponent(table[2][2])
                        .addComponent(table[2][3]) )
                    )
                //.addComponent(separator2)
                //.addComponent(fuelCost)
            ) );


        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(rocketInfo)
            .addComponent(separator0)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(accLabel)
                .addComponent(accField) ) 
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(angleLabel)
                .addComponent(angleField)
                .addComponent(angleToggle) )
            .addComponent(separator1)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(table[1][0])
                .addComponent(table[2][0]) )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(table[0][1])
                .addComponent(table[1][1])
                .addComponent(table[2][1]) )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(table[0][2])
                .addComponent(table[1][2])
                .addComponent(table[2][2]) )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(table[0][3])
                .addComponent(table[1][3])
                .addComponent(table[2][3]) )
            //.addComponent(separator2)
            //.addComponent(fuelCost)
        );
    }

    public void update(){
        try{
            ClientSpace workspace=getPlayer().getPlayerS().getWorkspace();
            AbstractMass rocket=workspace.masses.get(massIndex);
            int rocketIndex=0; //Index der Raketen, die dieser Spieler kontrollieren kann, nicht in der Space.masses-Liste
            for (int i=0;i<massIndex;i++){
                if (workspace.masses.get(i).isControllable(getPlayer().getID()))
                    rocketIndex++;
            }
            rocketIndex++; //die erste Rakete hat den Index 1, nicht den Index 0
            rocketInfo.setText("Rocket: " + rocketIndex + ";  mass: " + Math.round(rocket.getMass()));
            
            double angle=Double.parseDouble(angleField.getText())*Math.PI/180; //Eingabe in Grad ist vermutlich schöner
            VektorD dir=new VektorD(Math.cos(angle),Math.sin(angle));
            double dMass=-Double.parseDouble(accField.getText()); //Eingabe: positiver Wert => negative Massenänderung (wirkt vielleicht logischer?)
            long t0=Long.parseLong(((JTextField) table[1][1]).getText());
            long t1=Long.parseLong(((JTextField) table[1][2]).getText());
            boolean isRel=angleToggle.isSelected();
            Manoeuvre mano=new Manoeuvre(dir,isRel,dMass,((ClientMass) workspace.masses.get(massIndex)).getOutvel(),t0,t1);
            
            //einfache Checks, ob das überhaupt geht, ähnlich zu server.ShipS.applyManoeuvres
            //irgendwas hier ist falsch, aber es ist besser als nichts
            boolean applicable=true;
            double dMassGes=0; //gesamte verlorene Masse, sollte natürlich nicht größer als die Restmasse des Schiffs sein
            for (int i=0;i<rocket.getManoeuvres().size();i++){
                dMassGes=dMassGes+rocket.getManoeuvres().get(i).dMass;
            }
            dMassGes=dMassGes+dMass;
            if (rocket.getMass()+dMassGes<rocket.getRestMass()){ //Massenausstoß zu groß
                applicable=false;
            }
            if (t0<workspace.inGameTime){ //Manöver schon verstrichen
                applicable=false;
            }
            if (t1<=t0){ //falsche Zeitangaben
                applicable=false;
            }
            if (applicable){
                if (manoeuvreIndex>=0 && manoeuvreIndex<rocket.getManoeuvres().size()){ //altes Manöver editieren
                    rocket.getManoeuvres().set(manoeuvreIndex,mano);
                }
                else if (manoeuvreIndex==-1){ //neues Manöver hinzufügen
                    rocket.getManoeuvres().add(mano);
                    manoeuvreIndex=rocket.getManoeuvres().size()-1; 
                    //sollte jetzt natürlich das neue Manöver editieren, nicht bei jedem Update ein neues Manöver hinzufügen
                }
                else{ //neues Manöver an der gegebenen Stelle hinzufügen
                    rocket.getManoeuvres().add(manoeuvreIndex,mano);
                }
                ((JLabel) table[1][3]).setText(Long.toString(t1-t0));
                workspace.calcOrbits(ClientSettings.SPACE_CALC_TIME);
                t0=t0>=workspace.inGameTime ? t0 : workspace.inGameTime; //eher unschön, führt zu Veränderungen der Werte im Lauf der Zeit
                VektorI pos0=workspace.masses.get(massIndex).getOrbit().getPos(t0).toInt();
                VektorI pos1=workspace.masses.get(massIndex).getOrbit().getPos(t1).toInt();
                ((JLabel) table[2][1]).setText("("+pos0.x+", "+pos0.y+")");
                ((JLabel) table[2][2]).setText("("+pos1.x+", "+pos1.y+")");
                ((JLabel) table[2][3]).setText(Long.toString(Math.round(workspace.masses.get(massIndex).getOrbit().getTravelledDistance(t0,t1))));
            }
            else{
                ((JLabel) table[1][3]).setText("<html><font color='red'>Bad Input</font></html>");
                ((JLabel) table[2][1]).setText("<html><font color='red'>Bad Input</font></html>");
                ((JLabel) table[2][2]).setText("<html><font color='red'>Bad Input</font></html>");
                ((JLabel) table[2][3]).setText("<html><font color='red'>Bad Input</font></html>");
            }
        }
        catch(Exception e){}
    }
} 