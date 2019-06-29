package blocks;

import util.geom.*;
/**
 * Mei es is halt a Setzling, der kann wachsen oder halt ned.
 * 
 * Außerdem is da a Methodn drin, die Sie befähigt irgendwo in der Welt an Baum zu pflanzen (Serverseitig)
 */
public class Blocks_Sapling extends SBlock
{
    public static final long serialVersionUID=0L;
    public Blocks_Sapling(int id)
    {
        super(id, "sapling", "blocks_tree1", true);
    }
    
    @Override
    public void onConstruct(BlocksSandbox sb, int sandboxIndex, VektorI pos){
        System.out.println("Plant sapling at "+pos);
        plantTree(sb, pos);
        return;
    }
    
    
    public static void plantTree(BlocksSandbox sb, VektorI pos){
        VektorI up = new VektorI(0,-1);   // Falls Bäume auf der Seite wachsen sollen
        VektorI left = new VektorI(-1,0);
        VektorI right = new VektorI(1,0);
        VektorI pAkt = new VektorI(pos.x, pos.y);
        
        // Die Höhe der Stämme soll zwischen 1 und 3 liegen. Die Höhe 2 soll aber wahrscheinlicher sein.
        // Zufallszahl zwischen 1 (inclusive) und 8+1 = 9 (exclusiv)
        int random = (int)(Math.random() * 8 + 1);
        // Alles was größer als 4 ist ist wieder eine 2, damit die 2 am wahrscheinlichsten ist
        if(random > 3)random = 2;
        
        // Der Stamm:
        for(int i = 0; i<random; i++){
            Block b=sb.getBlock(pAkt);
            if(sb.getBlock(pAkt) != null && i != 0)return; //Abbruch, wenn da schon ein Block ist außer dem ersten Block (da ist der Setzling)
            sb.setBlock(Blocks.get(10), pAkt);
            pAkt = pAkt.add(up);
        }
        
        //Die Krone
        pAkt = pAkt.add(left).add(left);
        for(int i = 0; i<3; i++){
            VektorI pNew = new VektorI(pAkt.x, pAkt.y).add(up);
            if(i == 2)pAkt = pAkt.add(right);
            
            int amount = 5;
            if(i == 2)amount = 3;
            for(int j = 0; j<amount; j++){
                if(sb.getBlock(pAkt) == null){
                    sb.setBlock(Blocks.get(11), pAkt);
                    pAkt = pAkt.add(right);
                }
            }
            pAkt = pNew;
        }
        
    }
}
