package client;

import util.geom.*;

/**
 * OverlayPanel für Space
 */
public class OverlayPanelS extends OverlayPanel
{
    public OverlayPanelS(Frame frame, Player p, VektorI screenSize){
        super(frame,p,screenSize);
        //frame.getLayeredPane().add(this, new Integer(200)); // = new Integer(200) => Über dem PaintPanel new Integer(0) 
        // see: https://docs.oracle.com/javase/8/docs/api/javax/swing/JLayeredPane.html#FRAME_CONTENT_LAYER
    }
}
