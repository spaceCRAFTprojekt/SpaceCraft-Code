package items;

import java.awt.image.BufferedImage;


/**
 * Write a description of class Stack here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Stack
{
    public static int maxCount = 99;
    
    public Item item;
    public int count;
    
    public Stack(Item item, int count){
        this.item = item;
        this.count = count;
    }
    public Stack add(Stack stack){
        if(!stack.item.equals(item))return stack;
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
    public Item getItem(){
        return item;
    }
}
