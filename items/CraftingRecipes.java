package items;


import java.util.ArrayList;
/**
 * Hier werden alle Crafting "Rezepte" gespeichert.
 */
public abstract class CraftingRecipes
{
    private static ArrayList<CraftingRecipe> recipes=new ArrayList<CraftingRecipe>();
    
    static{
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(13,13,13,13, -1, 13,13,13,13, 100, 2));       // Chest
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(10,-1,-1,-1,-1,-1,-1,-1,-1,13,4));    // Wooden Planks
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(-1,-1,-1,-1,10,-1,-1,-1,-1,13,4));    // Wooden Planks mitte
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(2,-1,-1,-1,-1,-1,-1,-1,-1,110,2));    // Sand
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(-1,-1,-1,-1,110,-1,-1,-1,-1,111,1));    // Glass mitte
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(110,-1,-1,-1,-1,-1,-1,-1,-1,111,1));    // Glass
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(-1,-1,-1,-1,2,-1,-1,-1,-1,110,2));    // Sand mitte
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(13,-1,-1,-1,-1,-1,-1,-1,-1,10001,4)); // Stick
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(13,-1,-1,-1,13,-1,-1,-1,13,10004,16)); // Paper
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(-1,-1,-1,-1,13,-1,-1,-1,-1,10001,4)); // Stick mitte
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(11,11,11,11,11,11,-1,10001,-1,12,2)); // Sapling
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(13,13,-1, 13, 13, -1, 13, 13, -1, 120, 1));  // Tür
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(13,13,13,10001, -1, 10001, 10001, -1, 10001, 142, 1));  // Table
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(13,13,13, 2, 10001, 2, 2, 10001, 2, 300, 1));  // Piston
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(13,13,13, 13, 10004, 13,13,13,13, 104, 1));  // Noteblock
        CraftingRecipes.registerCraftingRecipe(new CraftingRecipe(2,13,-1, 13, 2, 13,-1,13,2, 141, 4));  // roof_tile
    }

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