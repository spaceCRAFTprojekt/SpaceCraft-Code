package menu;
import util.geom.VektorI;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.ListModel;
import java.awt.Color;
import java.awt.Font;
/**
 * Vereinfachung f�r eine Liste
 */
public class MenuList extends JList{
    private Menu m;
    public MenuList(Menu m, String[] text, VektorI pos,VektorI size, int fontSize){
        // Erstellt eine neue Liste
        super(text);
        this.m = m;
        setBounds(pos.x, pos.y,size.x, size.y);  // Position und Größe
        setBackground(Color.WHITE);
        setForeground(new Color(0,0,0));
        setEnabled(true);
        if (fontSize == -1)fontSize = MenuSettings.MENU_FONT_SIZE;
        setFont(new Font("sansserif",0,fontSize));
        setVisible(true);
        m.contentPane.add(this); // und fügt es zur Pane hinzu
    }
    
    public void add(String s){
        ListModel<String> model=getModel();
        ArrayList<String> newstrs=new ArrayList<String>();
        for (int i=0;i<model.getSize();i++){
            newstrs.add(model.getElementAt(i));
        }
        newstrs.add(s);
        String[] strs=new String[newstrs.size()];
        strs=newstrs.toArray(strs);
        setListData(strs);
    }
    
    public void set(int i, String s){
        ListModel<String> model=getModel();
        String[] newstrs=new String[model.getSize()];
        for (int j=0;j<model.getSize();j++){
            newstrs[j]=(model.getElementAt(j));
        }
        newstrs[i]=s;
        setListData(newstrs);
    }
    
    public void remove(int i){
        ListModel<String> model=getModel();
        ArrayList<String> newstrs=new ArrayList<String>();
        for (int j=0;j<model.getSize();j++){
            newstrs.add(model.getElementAt(j));
        }
        newstrs.remove(i);
        String[] strs=new String[newstrs.size()];
        strs=newstrs.toArray(strs);
        setListData(strs);
    }
}