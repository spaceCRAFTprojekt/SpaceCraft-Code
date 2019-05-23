package client;

import util.geom.*;
/**
 * Das Overlay Panel für alle (Space und Craft). Zum Beispiel für den Chat
 */
public class OverlayPanelA extends OverlayPanel

{
    public OverlayPanelA(Frame frame, Player p, VektorI screenSize){
        super(frame,p,screenSize);
        setVisible(true);
    }
}
