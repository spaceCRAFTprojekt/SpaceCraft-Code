package client;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;
import util.geom.*;
import client.menus.*;
import java.io.Serializable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Font;
import menu.MenuSettings;
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
    private transient ClientSpace workspace; //Irgendwie mag ich diesen Namen. -LG //null=Darstellung des "echten" Space, nicht-null: Bearbeitungsmodus
    //auch der workspace verwendet zur Zeichnung und für die Events die Variablen posToMass, scale und focussedMassIndex (macht es einfacher)

    
    public transient OverlayPanelS opS;
    
    public PlayerS(Player player, VektorD pos, int focussedMassIndex)
    {
        this.player = player;
        this.posToMass=pos;
        this.focussedMassIndex=focussedMassIndex;
        this.workspace=null;
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
            case Shortcuts.space_focus_next:
                int maxNum;
                if (workspace!=null)
                    maxNum=workspace.getMassNumber()-1;
                else
                    maxNum=((Integer) new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getMassNumber",Integer.class).ret).intValue()-1;
                focussedMassIndex=focussedMassIndex<maxNum ? focussedMassIndex+1 : 0; //immer 1 größer, bei maxNum wieder bei 0 anfangen
                if (player.onClient())
                    new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerSVariable",null,"focussedMassIndex",Integer.class,focussedMassIndex);
                break;
            case Shortcuts.space_switch_workspace:
                if (workspace==null)
                    new WorkspaceMenu.Open(this.player);
                else
                    new WorkspaceMenu.Close(this.player);
                break;
            case Shortcuts.workspace_new_manoeuvre:
                if (workspace!=null){
                    player.openMenu(new WorkspaceMenu.SelectManoeuvres(player));
                }
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
                        new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerSVariable",null,"posToMass",VektorD.class,this.posToMass);
                    }
                    //System.out.println("Stelle 1");
                }
                lastDragPosition = new VektorI(e.getX(), e.getY());
                // @Linus: Ich weiß du sollst dir nicht zu viele Pausen nehmen, damit Spacecraft noch fertig wird, aber in diesem CASE wäre 
                // eine kleine BREAK in der du feststellst, dass du eine BREAK vergessen hast nützlich. Mit anderen Worten: Da gehört ein break hin:
                // ~ unknown
                break;  // :)
            case 'p': VektorI pos = new VektorI(e.getX(), e.getY());
                // das hätte ich auch clientside berechnet:
                // und das komische ist, dass die Methode irgendwas verändert!!!
                int focussedMassIndexNew;
                if (workspace==null)
                    focussedMassIndexNew=((Integer) new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getFocussedMassIndex",Integer.class,pos,getPosToNull(),player.getScreenSize(),scale).ret).intValue();
                else
                    focussedMassIndexNew=workspace.getFocussedMassIndex(pos,getPosToNull(),player.getScreenSize(),scale);
                // das hab ich eingefügt (Alex)
                if (focussedMassIndexNew != -1)focussedMassIndex = focussedMassIndexNew;
                System.out.println("new focussedMass:" + focussedMassIndex);
                if (player.onClient())
                    new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Main.synchronizePlayerSVariable",null,"focussedMassIndex",Integer.class,focussedMassIndex);
                // und das ist auch eine schöne idee: den focussedMassIndex am Server brechnen, zum Client schicken und dann den Client nochmal
                // zum Server schicken lassen, um es mit dem Server zu synchronisieren xD
                //System.out.println("Stelle 2");
                break;
            case 'r': lastDragPosition = null;
                //System.out.println("Stelle 3");
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
        VektorD focussedMassPos;
        if (workspace==null){
            focussedMassPos=(VektorD) (new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getMassPos",VektorD.class,focussedMassIndex).ret);
        }
        else{
            focussedMassPos=workspace.getMassPos(-1,focussedMassIndex);
        }
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
            
            g2.setColor(Color.WHITE);
            ArrayList<ClientMass> masses;
            if (workspace==null){
                masses=(ArrayList<ClientMass>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllMassesInaccurate",ArrayList.class).ret);
            }
            else{
                masses=workspace.getAllMassesInaccurate(-1);
                g2.drawString("Arbeitsweltraum",player.getFrame().getScreenSize().x-140,player.getFrame().getScreenSize().y-60);
            }
            long inGameTime=((Long) new Request(player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getInGameTime",Long.class).ret).longValue();
            //immer der Wert im richtigen Space, nicht im Workspace (in dem vergeht keine Zeit)
            g2.drawString("Zeit: "+inGameTime,player.getFrame().getScreenSize().x-140,player.getFrame().getScreenSize().y-75);
            
            for (int i=0;i<masses.size();i++){
                if (masses.get(i)!=null){
                    g2.setColor(Color.WHITE);
                    Orbit orbit=masses.get(i).o;
                    long time=(long) orbit.t0;
                    long tEnd=(long) (orbit.pos.size()*orbit.dtime); //eigentlich nur eine Zeitdifferenz, nicht die tatsächliche End-Zeit
                    while (orbit.getPos(time+tEnd)==null){ //hässliche Lösung für komische NullPointerExceptions
                        tEnd=(long) (tEnd-orbit.dtime);
                    }
                    //Malen der Bahn des Planeten
                    for (long t=(long) (orbit.dtime);t<tEnd;t=t+(long) (orbit.dtime)){
                        //das sind längst nicht alle berechneten Positionen, nur alle gesendeten
                        VektorD posToNull1=posToNull; //Position relativ zur fokussierten Masse zu diesem Zeitpunkt
                        VektorD posToNull2=posToNull;
                        if (focussedMassIndex!=-1){
                            posToNull1=posToMass.add(masses.get(focussedMassIndex).o.getPos(time+t-(long) orbit.dtime));
                            posToNull2=posToMass.add(masses.get(focussedMassIndex).o.getPos(time+t));
                        }
                        VektorD posDiff1=orbit.getPos(time+t-(long) orbit.dtime).subtract(posToNull1);
                        posDiff1=posDiff1.multiply(scale);
                        VektorD posDiff2=orbit.getPos(time+t).subtract(posToNull2);
                        posDiff2=posDiff2.multiply(scale);
                        g2.drawLine((int) (screenSize.x/2+posDiff1.x),(int) (screenSize.y/2-posDiff1.y),(int) (screenSize.x/2+posDiff2.x),(int) (screenSize.y/2-posDiff2.y));
                    }
                    
                    //Malen des Planeten
                    VektorD posDiff=masses.get(i).getPos().subtract(posToNull);
                    posDiff=posDiff.multiply(scale);
                    int r=masses.get(i).getRadius();
                    r=Math.max((int)(r*scale),2);
                    if (i== player.getCurrentMassIndex())g2.setColor(Color.RED);
                    else if(i == focussedMassIndex)g2.setColor(Color.CYAN);
                    else g2.setColor(Color.WHITE);
                    //-y aufgrund des invertierten Koordinatensystems
                    g2.fillArc((int) (screenSize.x/2+posDiff.x-r),(int) (screenSize.y/2-posDiff.y-r),2*r,2*r,0,360);
                }
            }
            
            //Malen eines Pfeils beim Startpunkt der nächsten (geplanten) Manöver, der in die Richtung des Manövers zeigt, wenn sich der Spieler im workspace befindet
            //und Malen der durch das Manöver beeinflussten Bahn in Grün
            if (workspace!=null){
                for (int j=0;j<workspace.masses.size();j++){ //j ist die äußere Laufvariable, eher ungewöhnlich
                    for (int i=0;i<workspace.masses.get(j).getManoeuvres().size();i++){
                        Manoeuvre manoeuvre=workspace.masses.get(j).getManoeuvres().get(i);
                        g2.setColor(Color.GREEN);
                        long t0=Math.max(manoeuvre.t0,workspace.inGameTime); //Wenn man bereits über den Startpunkt des Manövers hinaus ist, sollte immer noch etwas angezeigt werden
        
                        if (t0<manoeuvre.t1){ //Pfeil
                            try{
                                VektorD posToNull1=posToNull; //Position relativ zur fokussierten Masse zu diesem Zeitpunkt
                                if (focussedMassIndex!=-1){
                                    posToNull1=posToMass.add(masses.get(focussedMassIndex).o.getPos(t0));
                                }
                                VektorD posDiff1=masses.get(j).o.getPos(t0).subtract(posToNull1);
                                posDiff1=posDiff1.multiply(scale);
                                VektorD dir=manoeuvre.getForce(workspace.masses.get(j).o.getVel(t0)).multiply(1000*scale);
                                VektorI start=new VektorI((int) (screenSize.x/2+posDiff1.x),(int) (screenSize.y/2-posDiff1.y));
                                VektorI end=new VektorI((int) (screenSize.x/2+posDiff1.x+dir.x),(int) (screenSize.y/2-posDiff1.y-dir.y));
                                g2.drawLine(start.x,start.y,end.x,end.y);
                                //Pfeilspitze
                                double theta=Math.atan2(dir.y,dir.x);
                                double angle=Math.PI/6;
                                double theta1=Math.PI+theta-angle;
                                double theta2=Math.PI+theta+angle;
                                int l=(int) (400*scale);
                                g2.drawLine(end.x,end.y,(int) (end.x+l*Math.cos(theta1)),(int) (end.y-l*Math.sin(theta1)));
                                g2.drawLine(end.x,end.y,(int) (end.x+l*Math.cos(theta2)),(int) (end.y-l*Math.sin(theta2)));
                            }
                            catch(NullPointerException e){}
                        }
                        //ähnlicb zu oben, wieder ein Teil der Bahn des Planeten
                        for (long t=t0+(long) masses.get(j).o.dtime;t<manoeuvre.t1;t=t+(long) masses.get(j).o.dtime){
                            try{
                                VektorD posToNull2=posToNull;
                                VektorD posToNull3=posToNull;
                                if (focussedMassIndex!=-1){
                                    posToNull2=posToMass.add(masses.get(focussedMassIndex).o.getPos(t-(long) masses.get(j).o.dtime));
                                    posToNull3=posToMass.add(masses.get(focussedMassIndex).o.getPos(t));
                                }
                                VektorD posDiff2=masses.get(j).o.getPos(t-(long) masses.get(j).o.dtime).subtract(posToNull2);
                                posDiff2=posDiff2.multiply(scale);
                                VektorD posDiff3=masses.get(j).o.getPos(t).subtract(posToNull3);
                                posDiff3=posDiff3.multiply(scale);
                                g2.drawLine((int) (screenSize.x/2+posDiff2.x),(int) (screenSize.y/2-posDiff2.y),(int) (screenSize.x/2+posDiff3.x),(int) (screenSize.y/2-posDiff3.y));
                            }
                            catch(NullPointerException e){}
                        }
                    }
                }
            }
            
            g.drawImage(img, 0,0, Color.BLACK, null);
            //p.repaint();
            //player.getFrame().getOverlayPanelS().add(new InfoPopup());
        }
    }
    
    public void openWorkspace(){
        ArrayList<ClientMass> masses=(ArrayList<ClientMass>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllMassesInaccurate",ArrayList.class).ret);
        Long inGameTime=(Long) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getInGameTime",Long.class).ret);

        ArrayList<AbstractMass> clientMasses=new ArrayList<AbstractMass>(masses.size());
        for (int i=0;i<masses.size();i++){
            ClientMass cm=new ClientMass(masses.get(i).m,masses.get(i).isControllable(player.getID()),masses.get(i).getPos(),masses.get(i).getVel(),masses.get(i).getRadius(),masses.get(i).getManoeuvres());
            clientMasses.add(cm);
        }
        workspace=new ClientSpace(clientMasses,inGameTime.longValue(),1);
    }

    public void closeWorkspace(boolean applyChanges){
        if (workspace!=null){
            workspace.timer.cancel();
            if (!applyChanges){
                workspace=null;
                if (player.getMenu() instanceof ManoeuvreInfo || player.getMenu() instanceof WorkspaceMenu.SelectManoeuvres){
                    player.closeMenu();
                }
                return;
            }
            for (int i=0;i<workspace.masses.size();i++){
                AbstractMass m=workspace.masses.get(i);
                if (m.getManoeuvres().size()!=0){
                    //das könnte Probleme geben, wenn neue Massen hinzugefügt oder alte entfernt werden
                    new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.setManoeuvres",null,i,m.getManoeuvres());
                }
            }
            workspace=null;
            if (player.getMenu() instanceof ManoeuvreInfo || player.getMenu() instanceof WorkspaceMenu.SelectManoeuvres){
                player.closeMenu();
            }
        }
    }
    
    public ClientSpace getWorkspace(){
        return workspace;
    }
}