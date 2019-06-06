package items;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Ein Stapel Objekte in einem Inventar
 */
public class Stack implements Serializable
{
    public static final long serialVersionUID=0L;
    public static int maxCount = 99;
    
    public Item item;
    public int count;
    
    public Stack(Item item, int count){
        this.item = item;
        this.count = count;
    }
    public Stack(Stack s){
        this(s.getItem(), s.getCount());
    }
    public Stack add(Stack stack){
        if(stack == null)return null;
        if(item == null || count == 0)item = stack.getItem();
        else if(!stack.item.equals(item))return stack;
        int countNew = count + stack.count;
        if (countNew > maxCount){
            count = maxCount;
            return new Stack(stack.item, countNew - maxCount);
        }else{
            count = countNew;
        } return null;
    }
    
    public BufferedImage getInventoryImage(){
        return item.getInventoryImage();
    }
    public int getCount(){
        return count;
    }
    public void setCount(int count){
        this.count = count;
    }
    public Item getItem(){
        return item;
    }
    public boolean take(int i){
        count = count - i;  // wieso hab ich da -1 hin geschreiben 
        if(count < 0) {
            count = 0;
            return false;
        }else return true;
        
    }
}
