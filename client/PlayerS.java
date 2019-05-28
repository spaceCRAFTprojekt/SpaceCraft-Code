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
    private transient Manoeuvre manoeuvre; //nur im workspace relevant
    
    public transient OverlayPanelS opS;
    
    public PlayerS(Player player, VektorD pos, int focussedMassIndex)
    {
        this.player = player;
        this.posToMass=pos;
        this.focussedMassIndex=focussedMassIndex;
        this.workspace=null;
        this.manoeuvre=null;
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
            case Shortcuts.space_switch_workspace:
                if (workspace==null)
                    new WorkspaceMenu.Open(this.player);
                else
                    new WorkspaceMenu.Close(this.player);
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
            focussedMassPos=workspace.getMassPos(focussedMassIndex);
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
            ArrayList<VektorD> poss;
            ArrayList<Orbit> orbits;
            ArrayList<Integer> radii;
            if (workspace==null){
                poss=(ArrayList<VektorD>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllPos",ArrayList.class).ret);
                orbits=(ArrayList<Orbit>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllOrbits",ArrayList.class).ret);
                radii=(ArrayList<Integer>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllRadii",ArrayList.class).ret);
            }
            else{
                poss=workspace.getAllPos();
                orbits=workspace.getAllOrbits();
                radii=workspace.getAllRadii();
                g2.drawString("Arbeitsweltraum",20,20);
            }
            
            for (int i=0;i<poss.size();i++){
                if (poss.get(i)!=null){
                    g2.setColor(Color.WHITE);
                    long time=(long) orbits.get(i).t0;
                    long tEnd=(long) (orbits.get(i).pos.size()*orbits.get(i).dtime); //eigentlich nur eine Zeitdifferenz, nicht die tatsächliche End-Zeit
                    while (orbits.get(i).getPos(time+tEnd)==null){ //hässliche Lösung für komische NullPointerExceptions
                        tEnd=(long) (tEnd-orbits.get(i).dtime);
                    }
                    //Malen der Bahn des Planeten
                    for (long t=(long) (orbits.get(i).dtime);t<tEnd;t=t+(long) (orbits.get(i).dtime)){
                        //das sind längst nicht alle berechneten Positionen, nur alle gesendeten
                        VektorD posToNull1=posToNull; //Position relativ zur fokussierten Masse zu diesem Zeitpunkt
                        VektorD posToNull2=posToNull;
                        if (focussedMassIndex!=-1){
                            posToNull1=posToMass.add(orbits.get(focussedMassIndex).getPos(time+t-(long) orbits.get(i).dtime));
                            posToNull2=posToMass.add(orbits.get(focussedMassIndex).getPos(time+t));
                        }
                        VektorD posDiff1=orbits.get(i).getPos(time+t-(long) orbits.get(i).dtime).subtract(posToNull1);
                        posDiff1=posDiff1.multiply(scale);
                        VektorD posDiff2=orbits.get(i).getPos(time+t).subtract(posToNull2);
                        posDiff2=posDiff2.multiply(scale);
                        g2.drawLine((int) (screenSize.x/2+posDiff1.x),(int) (screenSize.y/2-posDiff1.y),(int) (screenSize.x/2+posDiff2.x),(int) (screenSize.y/2-posDiff2.y));
                    }
                    
                    //Malen des Planeten
                    VektorD posDiff=poss.get(i).subtract(posToNull);
                    posDiff=posDiff.multiply(scale);
                    int r=radii.get(i);
                    r=Math.max((int)(r*scale),2);
                    if (i== player.getCurrentMassIndex())g2.setColor(Color.RED);
                    else if(i == focussedMassIndex)g2.setColor(Color.CYAN);
                    else g2.setColor(Color.WHITE);
                    //-y aufgrund des invertierten Koordinatensystems
                    g2.fillArc((int) (screenSize.x/2+posDiff.x-r),(int) (screenSize.y/2-posDiff.y-r),2*r,2*r,0,360);
                }
            }
            
            //Malen eines Pfeils beim Startpunkt des nächsten (geplanten) Manövers, der in die Richtung des Manövers zeigt, wenn sich der Spieler im workspace befindet
            //und Malen der durch das Manöver beeinflussten Bahn in Grün
            if (workspace!=null && manoeuvre!=null){
                g2.setColor(Color.GREEN);
                long t0=Math.max(manoeuvre.oc.t0,workspace.inGameTime); //Wenn man bereits über den Startpunkt des Manövers hinaus ist, sollte immer noch etwas angezeigt werden

                if (t0<manoeuvre.oc.t1){ //Pfeil
                    try{
                        VektorD posToNull1=posToNull; //Position relativ zur fokussierten Masse zu diesem Zeitpunkt
                        if (focussedMassIndex!=-1){
                            posToNull1=posToMass.add(orbits.get(focussedMassIndex).getPos(t0));
                        }
                        VektorD posDiff1=orbits.get(manoeuvre.shipIndex).getPos(t0).subtract(posToNull1);
                        posDiff1=posDiff1.multiply(scale);
                        VektorD dir=manoeuvre.oc.F.multiply(1000*scale);
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
                for (long t=t0+(long) orbits.get(manoeuvre.shipIndex).dtime;t<manoeuvre.oc.t1;t=t+(long) orbits.get(manoeuvre.shipIndex).dtime){
                    try{
                        VektorD posToNull2=posToNull;
                        VektorD posToNull3=posToNull;
                        if (focussedMassIndex!=-1){
                            posToNull2=posToMass.add(orbits.get(focussedMassIndex).getPos(t-(long) orbits.get(manoeuvre.shipIndex).dtime));
                            posToNull3=posToMass.add(orbits.get(focussedMassIndex).getPos(t));
                        }
                        VektorD posDiff2=orbits.get(manoeuvre.shipIndex).getPos(t-(long) orbits.get(manoeuvre.shipIndex).dtime).subtract(posToNull2);
                        posDiff2=posDiff2.multiply(scale);
                        VektorD posDiff3=orbits.get(manoeuvre.shipIndex).getPos(t).subtract(posToNull3);
                        posDiff3=posDiff3.multiply(scale);
                        g2.drawLine((int) (screenSize.x/2+posDiff2.x),(int) (screenSize.y/2-posDiff2.y),(int) (screenSize.x/2+posDiff3.x),(int) (screenSize.y/2-posDiff3.y));
                    }
                    catch(NullPointerException e){}
                }
            }
            
            g.drawImage(img, 0,0, Color.BLACK, null);
            //p.repaint();
            //player.getFrame().getOverlayPanelS().add(new InfoPopup());
        }
    }
    
    public void openWorkspace(){
        ArrayList<VektorD> poss=(ArrayList<VektorD>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllPos",ArrayList.class).ret);
        ArrayList<VektorD> vels=(ArrayList<VektorD>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllVels",ArrayList.class).ret);
        ArrayList<Double> masses=(ArrayList<Double>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllMasses",ArrayList.class).ret);
        ArrayList<Integer> radii=(ArrayList<Integer>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllRadii",ArrayList.class).ret);
        ArrayList<ArrayList<OrbitChange>> orbitChanges=(ArrayList<ArrayList<OrbitChange>>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllOrbitChanges",ArrayList.class).ret);
        ArrayList<ArrayList<MassChange>> massChanges=(ArrayList<ArrayList<MassChange>>) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getAllMassChanges",ArrayList.class).ret);
        Long inGameTime=(Long) (new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.getInGameTime",Long.class).ret);

        ArrayList<ClientMass> clientMasses=new ArrayList<ClientMass>(poss.size());
        for (int i=0;i<poss.size();i++){
            ClientMass cm=new ClientMass(masses.get(i),poss.get(i),vels.get(i),radii.get(i),orbitChanges.get(i),massChanges.get(i));
            clientMasses.add(cm);
        }
        manoeuvre=new Manoeuvre(2,new VektorD(0,2),0,400,1000);
        workspace=new ClientSpace(clientMasses,inGameTime.longValue(),1,manoeuvre);
    }

    public void closeWorkspace(boolean applyChanges){
        workspace.timer.cancel();
        if (!applyChanges){
            workspace=null;
            return;
        }
        if (manoeuvre!=null){
            ClientMass m=workspace.masses.get(manoeuvre.shipIndex);
            m.orbitChanges.add(manoeuvre.oc);
            m.massChanges.add(manoeuvre.mc);
        }
        for (int i=0;i<workspace.masses.size();i++){
            ClientMass m=workspace.masses.get(i);
            if (m.orbitChanges.size()!=0){
                //das könnte Probleme geben, wenn neue Massen hinzugefügt oder alte entfernt werden
                new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.setOrbitChanges",null,i,m.orbitChanges);
            }
            if (m.massChanges.size()!=0){
                new Request(this.player.getID(),player.getRequestOut(),player.getRequestIn(),"Space.setMassChanges",null,i,m.massChanges);
            }
        }
        workspace=null;
        manoeuvre=null;
    }
}