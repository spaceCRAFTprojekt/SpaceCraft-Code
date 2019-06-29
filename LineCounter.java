import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 * Sehr dumm. -LG
 */
public class LineCounter{
    public LineCounter(){
        count();
    }
    public static int count(){
        try{
            int ret=getLines(".");
            System.out.println("Lines: "+ret);
            return ret;
        }catch(Exception e){e.printStackTrace();}
        return 0;
    }
    
    /**
     * rekursiv 
     */
    public static int getLines(String pathname){
        if (!new File(pathname).isDirectory() && pathname.substring(pathname.length()>=5 ? pathname.length()-5: 0).equals(".java")){
            int num=0;
            try{
                FileInputStream fi=new FileInputStream(pathname);
                int re=fi.read();
                while(re!=-1){
                    if ((char) re=='\n'){
                        num=num+1;
                    }
                    re=fi.read();
                }
                num=num+1; //erste bzw. letzte Zeile
                //System.out.println(pathname+" "+num);
                fi.close();
            }
            catch(IOException e){}
            return num;
        }
        else if (!new File(pathname).isDirectory()){
            return 0;
        }
        int lines=0;
        for(File file: new File(pathname).listFiles()){
            lines=lines+getLines(file.getPath());
        }
        return lines;
    }
}