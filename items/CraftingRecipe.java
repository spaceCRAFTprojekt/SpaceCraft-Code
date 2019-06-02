package items;

import util.geom.*;

/**
 * Hier wird ein Crafting Rezept gespeichert
 */
public class CraftingRecipe
{
    public int[][]recipe = new int[3][3];
    public int out;
    public int count;
    public CraftingRecipe(int i00, int i10, int i20, int i01, int i11, int i21, int i02, int i12, int i22, int out, int count){
        recipe[0][0] = i00;
        recipe[1][0] = i10;
        recipe[2][0] = i20;
        recipe[0][1] = i01;
        recipe[1][1] = i11;
        recipe[2][1] = i21;
        recipe[0][2] = i02;
        recipe[1][2] = i12; 
        recipe[2][2] = i22; 
        this.out = out;
        this.count = count;
    }
    
    public CraftingRecipe(Inv inv){
        count = Integer.MAX_VALUE;
        for(int i = 0; i < 9; i++){
            try{
                Stack s = inv.getStack(new VektorI(i%3, i/3));
                recipe[i%3][i/3] = s.getItem().id;
                if(count > s.getCount())count = s.getCount();
            }catch(Exception e){recipe[i%3][i/3] = -1;}
        }
    }
    
    public boolean equals(CraftingRecipe cp){
        for(int i = 0; i < 9; i++){
            if(recipe[i%3][i/3] != cp.recipe[i%3][i/3])return false;  // wenn ein Slot nicht übereinstimmt => nicht gleich => false
        }
        return true;
    }
}
