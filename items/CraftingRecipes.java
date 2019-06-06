package items;


import java.util.ArrayList;
/**
 * Hier werden alle Crafting "Rezepte" gespeichert.
 */
public abstract class CraftingRecipes
{
    private static ArrayList<CraftingRecipe> recipes=new ArrayList<CraftingRecipe>();

    public static void registerCraftingRecipe(CraftingRecipe cp){
        synchronized(recipes){
            recipes.add(cp);
        }
    }
    
    public static Stack getOutput(CraftingRecipe cpTest){
        for(int i = 0; i < recipes.size(); i++){
            CraftingRecipe cp = recipes.get(i);
            if(cp.equals(cpTest))return new Stack(Items.get(cp.out), cpTest.count * cp.count);
        }
        return null;
    }
    
    public static int getCountPerItem(CraftingRecipe cpTest){
        for(int i = 0; i < recipes.size(); i++){
            CraftingRecipe cp = recipes.get(i);
            if(cp.equals(cpTest))return cp.count;
        }
        return -1;
    }
}