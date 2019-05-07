package client;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;
import geom.*;
import java.io.Serializable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.swing.*;  // test
/**
 * ein Spieler in der Space Ansicht
 * test
 */
public class PlayerS implements Serializable
{
    private Player player;
    private VektorD posToMass;
    private double scale=0.05; //eine Einheit im Space => scale Pixel auf dem Frame
    private Integer focussedMassIndex; //Integer, damit man ihn notify'n kann
    private transient VektorI lastDragPosition = null;
    
    private JPopupMenu popupmenu;
    
    public PlayerS(Player player, VektorD pos, int focussedMassIndex)
    {
        this.player = player;
        this.posToMass=pos;
        this.focussedMassIndex=new Integer(focussedMassIndex);
        
        popupmenu = new JPopupMenu("Edit");   
         JMenuItem cut = new JMenuItem("Cut");  
         JMenuItem copy = new JMenuItem("Copy");  
         JMenuItem paste = new JMenuItem("Paste");  
         popupmenu.add(cut); popupmenu.add(copy); popupmenu.add(paste);        
          //player.getFrame().add(popupmenu);
        //player.getFrame().setLayout(null);
    }
    
    /**
     * Tastatur event
     * @param:
     *  char type: 'p': pressed
     *             'r': released
     *             't': typed (nur Unicode Buchstaben)
     */
    public void keyEvent(KeyEvent e, char type) {
        switch(e.getKeyCode()){
            case Shortcuts.space_focus_current_mass: 
                focussedMassIndex=player.getCurrentMassIndex();
                popupmenu.show(player.getFrame() , 300,300);  
                popupmenu.setVisible(true);
                break;
        }
    }

    /**
     * Maus Event
     * @param:
     *  char type: 'p': pressed
     *             'r': released
     *             'c': clicked
     *             'd': dragged
     */
    public void mouseEvent(MouseEvent e, char type) {
        switch(type){
            case 'd': 
                if (lastDragPosition != null){
                    VektorI thisDragPosition = new VektorI(e.getX(), e.getY());
                    VektorD diff = lastDragPosition.subtract(thisDragPosition).toDouble().multiply(1/scale);
                    diff.y = -diff.y;   // die Y Achse ist umgedreht
                    this.posToMass = posToMass.add(diff);
                }
                lastDragPosition = new VektorI(e.getX(), e.getY());
            case 'p': lastDragPosition = new VektorI(e.getX(), e.getY());
                VektorD pos;
                if (focussedMassIndex==-1){ //stimmt das so?
                    pos=posToMass;
                }
                else{
                    pos=posToMass.add(getFocussedMassPos());
                }
                focussedMassIndex=new Integer(-1);
                
                Request req=new Request(player,"Space.getFocussedMassIndex",focussedMassIndex,pos,player.getScreenSize(),scale);
                focussedMassIndex=(Integer) req.ret;
                req=null;
                if (focussedMassIndex.intValue()==-2){
                    focussedMassIndex=new Integer(-1);
                }
                break;
            case 'r': lastDragPosition = null;
                break;
        }
    }   
    
    public void mouseWheelMoved(MouseWheelEvent e){
        int amountOfClicks = e.getWheelRotation();
        scale = scale * Math.pow(2,amountOfClicks);
        if (scale == 0)scale = 1;
    }
    
    public VektorD getFocussedMassPos(){
        if (focussedMassIndex==-1){
            return null;
        }
        VektorD focussedMassPos=null;
        Request req=new Request(player,"Space.getMassPos",focussedMassPos,focussedMassIndex);
        req.ret=focussedMassPos;
        req=null;
        if (focussedMassPos.x==Double.NaN || focussedMassPos.y==Double.NaN){
            return null;
        }
        return focussedMassPos;
    }
    
    public VektorD getPosToNull(){
        if(focussedMassIndex.intValue()==-1){
            return posToMass;
        }
        else{
            return posToMass.add(getFocussedMassPos());
        }
    }
    
    /**
     * Grafik ausgeben
     */
    public void paint(Graphics g, VektorI screenSize){
        BufferedImage img = new BufferedImage(screenSize.x, screenSize.y, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();   
        
        VektorD posToNull = getPosToNull();
        
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0,screenSize.x,screenSize.y); // lol
        
        ArrayList<VektorD> poss=new ArrayList<VektorD>();
        Request req1=new Request(this.player,"Space.getAllPos",poss);
        poss=(ArrayList<VektorD>) req1.ret;
        req1=null;
        ArrayList<ArrayList<VektorD>> orbits=new ArrayList<ArrayList<VektorD>>(); //das pos-Array
        Request req2=new Request(this.player,"Space.getAllOrbits",orbits);
        orbits=(ArrayList<ArrayList<VektorD>>) req2.ret;
        req2=null;
        ArrayList<Integer> radii=new ArrayList<Integer>();
        Request req3=new Request(this.player,"Space.getAllRadii",radii);
        radii=(ArrayList<Integer>) req3.ret;
        req3=null;
        
        int accuracy = 100;
        for (int i=0;i<poss.size();i++){
            if (poss.get(i)!=null){
                for (int j=accuracy;j<orbits.get(i).size();j=j+accuracy){  // ernsthaft?
                    VektorD posDiff1=orbits.get(i).get(j-accuracy).subtract(posToNull);
                    posDiff1=posDiff1.multiply(scale);
                    VektorD posDiff2=orbits.get(i).get(j).subtract(posToNull);
                    posDiff2=posDiff2.multiply(scale);
                    g2.setColor(Color.WHITE);
                    g2.drawLine((int) (screenSize.x/2+posDiff1.x),(int) (screenSize.y/2-posDiff1.y),(int) (screenSize.x/2+posDiff2.x),(int) (screenSize.y/2-posDiff2.y));
                }
                VektorD posDiff=poss.get(i).subtract(posToNull);
                posDiff=posDiff.multiply(scale);
                int r=radii.get(i);
                r=(int)(r*scale);
                if (i== player.getCurrentMassIndex())g2.setColor(Color.RED);
                else if(i == focussedMassIndex.intValue())g2.setColor(Color.CYAN);
                else g2.setColor(Color.WHITE);
                g2.fillArc((int) (screenSize.x/2+posDiff.x-r),(int) (screenSize.y/2-posDiff.y-r),2*r,2*r,0,360);
            }
        }
        
        g.drawImage(img, 0,0, Color.BLACK, null);
        //popupmenu.paint(g);
    }
}