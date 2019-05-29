package client.menus;

import util.geom.*;
import javax.swing.*;
import menu.*;
import client.*;
import java.awt.Color;

/**
 * Zum einstellen eines Manoeuvres
 */
public class ManoeuvreInfo extends Menu
{
    private Manoeuvre m;
    private ClientMass cm;
    private GroupLayout layout;
    
    private JLabel rocketInfo;
    private JSeparator separator0;
    private JLabel accLabel;
    private JTextField accField;
    private JLabel angleLabel;
    private JTextField angleField;
    private JToggleButton angleToggle;
    private JSeparator separator1;
    private JPanel tablePanel;
    private JComponent[][]table;
    private JSeparator separator2;
    private JLabel fuelCost;
    

    public ManoeuvreInfo(Manoeuvre m, ClientMass cm){
        super("Manoeuvre Info", new VektorI(210,280));   // id muss noch gemacht werden
        this.cm = cm;

        setFont(MenuSettings.MENU_FONT);
        layout = new GroupLayout(this.getLayeredPane());
        this.getLayeredPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        rocketInfo = new JLabel("Rocket: - ;  mass: - ");
        rocketInfo.setSize(300,12);
        
        separator0 = new JSeparator(JSeparator.HORIZONTAL);
        separator0.setForeground(Color.gray);
        
        accLabel = new JLabel("Thrust");
        accLabel.setVisible(true); 
        angleLabel = new JLabel("Angle: ");
        accField = new JTextField();
        angleField = new JTextField();
        angleToggle = new JToggleButton("rel");
        
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
        table[1][3] = new JTextField();
        table[2][1] = new JTextField();
        table[2][2] = new JTextField();
        table[2][3] = new JTextField();
        
        separator2 = new JSeparator(JSeparator.HORIZONTAL);
        separator2.setForeground(Color.gray);
        
        fuelCost = new JLabel("Fuel Cost: - ");
        
        
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
                .addComponent(separator2)
                .addComponent(fuelCost)
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
            .addComponent(separator2)
            .addComponent(fuelCost)
        );
    }
    
    public void update(){
        rocketInfo.setText("Rocket: " + "" + ";  mass:" + cm.getMass() );
        fuelCost.setText("Fuelcost: " + "");
    }

}