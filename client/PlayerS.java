package client;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;
import util.geom.*;
import java.io.Serializable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Font;
import javax.swing.*;  // test
/**
 * ein Spieler in der Space Ansicht
 * test
 */
public class PlayerS implements Serializable
{
    public static final long serialVersionUID=0L;
    //alle Variablen, die synchronisiert werden müssen, müssen public sein
    private Player player;
    public VektorD posToMass;
    public double scale=0.05; //eine Einheit im Space => scale Pixel auf dem Frame
    public int focussedMassIndex;
    private transient VektorI lastDragPosition = null;
    
    public transient OverlayPanelS opS;
    
    public PlayerS(Player player, VektorD pos, int focussedMassIndex)
    {
        this.player = player;
        this.posToMass=pos;
        this.focussedMassIndex=focussedMassIndex;
        //muss man hier auch schon synchronisieren?
    }
    
    public void makeFrame(Frame frame){

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
                if (player.onClient())
                    new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerSVariable",null,"focussedMassIndex",Integer.class,focussedMassIndex);
                // ist das notwendig? Muss der Server den fokusierten Planeten kennen?
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
                    if (player.isOnline() && player.onClient()){
                        //focussedMassIndex=((Integer) new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getFocussedMassIndex",Integer.class,pos,getPosToNull(),player.getScreenSize(),scale).ret).intValue();
                        //new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerSVariable",null,"posToMass",VektorD.class,this.posToMass);
                    }
                    lastDragPosition = new VektorI(e.getX(), e.getY());
                    System.out.println("Stelle 1");
                }
                // @Linus: Ich weiß du sollst dir nicht zu viele Pausen nehmen, damit Spacecraft noch fertig wird, aber in diesem CASE wäre 
                // eine kleine BREAK in der du feststellst, dass du eine BREAK vergessen hast nützlich. Mit anderen Worten: Da gehört ein break hin:
                // ~ unknown
                break;  // :)
            case 'p': lastDragPosition = new VektorI(e.getX(), e.getY());
                VektorD pos;
                if (focussedMassIndex==-1){ //stimmt das so?
                    pos=posToMass;
                }
                else{
                    pos=posToMass.add(getFocussedMassPos());
                }
                // das hätte ich auch clientside berechnet:
                // und das komische ist, dass die Methode irgendwas verändert!!!
                int focussedMassIndexNew=((Integer) new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getFocussedMassIndex",Integer.class,pos,getPosToNull(),player.getScreenSize(),scale).ret).intValue();
                // das hab ich eingefügt (Alex)
                if (focussedMassIndexNew != -1)focussedMassIndex = focussedMassIndexNew;
                System.out.println("new focussedMass:" + focussedMassIndex);
                if (player.onClient())
                    new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerSVariable",null,"focussedMassIndex",Integer.class,focussedMassIndex);
                // und das ist auch eine schöne idee: den focussedMassIndex am Server brechnen, zum Client schicken und dann den Client nochmal
                // zum Server schicken lassen, um es mit dem Server zu synchronisieren xD
                System.out.println("Stelle 2");
                break;
            case 'r': lastDragPosition = null;
                System.out.println("Stelle 3");
                break;
        }
    }   
    
    public void mouseWheelMoved(MouseWheelEvent e){
        int amountOfClicks = e.getWheelRotation();
        scale = scale * Math.pow(2,amountOfClicks);
        if (scale == 0)scale = 1;
        if (player.onClient() && player.isOnline())
            new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerSVariable",null,"scale",Double.class,scale);
    }
    
    public VektorD getFocussedMassPos(){
        if (focussedMassIndex==-1 || !player.isOnline() || !player.onClient()){
            return null;
        }
        VektorD focussedMassPos=(VektorD) (new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getMassPos",VektorD.class,focussedMassIndex).ret);
        return focussedMassPos;
    }
    
    public VektorD getPosToNull(){
        if(focussedMassIndex==-1){
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
        if (player.onClient() && player.isOnline()){
            BufferedImage img = new BufferedImage(screenSize.x, screenSize.y, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();   
            
            VektorD posToNull = getPosToNull();
            
            g2.setColor(Color.BLACK);
            g2.fillRect(0,0,screenSize.x,screenSize.y); // lol
            
            ArrayList<VektorD> poss=(ArrayList<VektorD>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllPos",ArrayList.class).ret);
            ArrayList<ArrayList<VektorD>> orbits=(ArrayList<ArrayList<VektorD>>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllOrbits",ArrayList.class).ret);
            ArrayList<Integer> radii=(ArrayList<Integer>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllRadii",ArrayList.class).ret);
            
            for (int i=0;i<poss.size();i++){
                if (poss.get(i)!=null){
                    for (int j=1;j<orbits.get(i).size();j=j+1){  // ernsthaft?
                        //das sind längst nicht alle berechneten Positionen, nur alle gesendeten
                        VektorD posToNull1=posToNull; //Position relativ zur fokussierten Masse zu diesem Zeitpunkt
                        VektorD posToNull2=posToNull;
                        if (focussedMassIndex!=-1){
                            posToNull1=posToMass.add(orbits.get(focussedMassIndex).get(j-1));
                            posToNull2=posToMass.add(orbits.get(focussedMassIndex).get(j));
                        }
                        VektorD posDiff1=orbits.get(i).get(j-1).subtract(posToNull1);
                        posDiff1=posDiff1.multiply(scale);
                        VektorD posDiff2=orbits.get(i).get(j).subtract(posToNull2);
                        posDiff2=posDiff2.multiply(scale);
                        g2.setColor(Color.WHITE);
                        g2.drawLine((int) (screenSize.x/2+posDiff1.x),(int) (screenSize.y/2-posDiff1.y),(int) (screenSize.x/2+posDiff2.x),(int) (screenSize.y/2-posDiff2.y));
                    }
                    VektorD posDiff=poss.get(i).subtract(posToNull);
                    posDiff=posDiff.multiply(scale);
                    int r=radii.get(i);
                    r=(int)(r*scale);
                    if (i== player.getCurrentMassIndex())g2.setColor(Color.RED);
                    else if(i == focussedMassIndex)g2.setColor(Color.CYAN);
                    else g2.setColor(Color.WHITE);
                    g2.fillArc((int) (screenSize.x/2+posDiff.x-r),(int) (screenSize.y/2-posDiff.y-r),2*r,2*r,0,360);
                }
            }
            
            String[] chat=(String[]) new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.getChatContent",String[].class,5).ret;
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(Font.SERIF,Font.PLAIN,12));
            for (int i=0;i<chat.length;i++){
                g2.drawString(chat[i],20,i*16+8);
            }
            
            g.drawImage(img, 0,0, Color.BLACK, null);
            //p.repaint();
            //player.getFrame().getOverlayPanelS().add(new InfoPopup());
        }
    }
}