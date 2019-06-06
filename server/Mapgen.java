package server;
import util.geom.VektorI;
import blocks.*;
public class Mapgen
{
    static Block[][] generateMap(String type, VektorI size, int radius){
        Block[][]map = new Block[size.x][size.y];
        switch(type){
            case "moonlike": return map;
            default:
            // Nur ein Quadrat aus Stein mit Gras (keine Drogen) und Erde zum testen :)
            for (int x = (size.x / 2)-radius; x<=(size.x / 2)+radius; x++){
                for (int y = (size.y / 2)-radius; y<=(size.y / 2)+radius; y++){
                    if((size.x / 2)-radius == x || (size.x / 2)+radius == x ||
                    (size.y / 2)-radius == y || (size.y / 2)+radius == y){
                        map[x][y] = Blocks.blocks.get(0);
                    }else if ((size.x / 2)-radius >= x-4 || (size.x / 2)+radius <= x+4 ||
                    (size.y / 2)-radius >= y-4 || (size.y / 2)+radius <= y+4){
                        map[x][y] = Blocks.blocks.get(1);
                    }else{
                        //Erzgeneration
                        int rand = (int)Math.round(Math.random()*100);
                        if (rand > 5 || rand < 2){
                            map[x][y] = Blocks.blocks.get(2); 
                        }
                        else if (rand > 1){
                            map[x][y] = Blocks.blocks.get(rand);
                        }
                    }
                }
            }
            return map;
        }
    }
    
    static Block[][] getDummyShipMap(VektorI size){
        Block[][] map=new Block[size.x][size.y];
        for (int x = 0; x<size.x; x++){
            for (int y = 0; y<size.y; y++){
                map[x][y] = Blocks.blocks.get(4); //ein Schiff aus Gold
            }
        }
        return map;
    }
}