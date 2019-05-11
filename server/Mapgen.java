package server;
import geom.VektorI;
public class Mapgen
{
    static Block[][] generateMap(String type, VektorI size, int radius){
        Block[][]map = new Block[size.x][size.y];
        switch(type){
            case "moonlike": return map;
            default:
                // Nur ein Quadrat aus Stein zum testen :)
                for (int x = (size.x / 2)-radius; x<=(size.x / 2)+radius; x++){
                    for (int y = (size.y / 2)-radius; y<=(size.y / 2)+radius; y++){
                        if((size.x / 2)-radius == x || (size.x / 2)+radius == x ||
                           (size.y / 2)-radius == y || (size.y / 2)+radius == y){
                               map[x][y] = Blocks.blocks.get(2);
                        }else if ((size.x / 2)-radius >= x-4 || (size.x / 2)+radius <= x+4 ||
                                  (size.y / 2)-radius >= y-4 || (size.y / 2)+radius <= y+4){
                               map[x][y] = Blocks.blocks.get(1);
                        }else{
                               map[x][y] = Blocks.blocks.get(0);
                        }
                    }
                }
                return map;
                
        }
    }
}
