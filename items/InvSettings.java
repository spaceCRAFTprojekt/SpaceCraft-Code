package items;

import util.geom.VektorI;

public abstract class InvSettings
{
    public static int SLOT_SIZE = 50; // in Pixeln
    public static int SLOT_BORDER = SLOT_SIZE / 12;  // in Pixeln
    public static VektorI INV_SIZE=new VektorI(10,4);  // größe des Spielerinventars  (in Slots)
    
}
