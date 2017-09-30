import java.io.*;
import java.lang.*;

public class Highlighter
{
    public static String[] colors = new String [100];
    public static int colorLength = 0;
    public static String[] ignoreTag = {"area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"};

    public static void addColor(String color){
        colors[colorLength] = color;
        colorLength++;
    }

    public static void removeColor(){
        colors[colorLength-1] = "";
        colorLength--;
    }

    public static String generateColor(){

        String[] letters = new String[15];
        letters = "0123456789ABCDEF".split("");
        String code ="";
        for(int i=0;i<6;i++)
        {
            double ind = Math.random() * 15;
            code += letters[(int)ind]; 
        }
        return code;

    }

    public static String getVoidName(String sub,int selfclosing,int closetag,int space){
        if(selfclosing==-1 && space==-1){
            return sub.substring(0,closetag);         

        }

        else if(selfclosing==-1){  
            return sub.substring(0,Math.min(closetag,space));        
        }

        else if(space==-1){    
            return sub.substring(0,Math.min(closetag,selfclosing));    
        }
        else{
            return sub.substring(0,Math.min((Math.min(selfclosing,closetag)),space));

        }

    }
    
    public static boolean checkVoidElement(String sub){
        int selfclosing = sub.indexOf('/');
        int closetag = sub.indexOf('>');
        int space = sub.indexOf(' ');
        String compareStr="";
        compareStr=getVoidName(sub,selfclosing,closetag,space);

        for(int z=0; z<ignoreTag.length; z++){
            if(ignoreTag[z].equals(compareStr)){
                return true;
            }
        }
        return false;

    }

    public static void main(String args[]) throws IOException {

        String line;
        int prev=1;//1 for open tag, 0 for close tag
        int flag=0;//1 if there is no opening tag after a closing tag, 0 if otherwise

        try (BufferedReader br = new BufferedReader(new FileReader("abc.html"))) {
            BufferedWriter writer = new BufferedWriter(new FileWriter("abc2.html"));

            while ((line = br.readLine()) != null) {
                char [] ar= line.toCharArray();
                int len = ar.length;
                for(int x=0; x<ar.length; x++){

                    if(prev==0 && ar[x]=='>'){
                        flag=1;
                    }

                    else if(ar[x]=='<'){
                        flag=0;
                        if (ar[x+1]!='/'){

                            prev=1;
                            String tagColor = generateColor();
                            writer.write("\\color["+tagColor+"]");

                            if(checkVoidElement(line.substring(x+1,ar.length))){
                                prev=0;
                            }

                            else{
                                addColor(tagColor);
                            }

                        }
                        else{

                            if(prev==0){
                                writer.write("\\color["+colors[colorLength-1]+"]");
                                removeColor();
                            }
                            else{
                                prev=0;
                                removeColor();
                            }
                        }

                    }

                    else if(flag==1 && ar[x]!=' '){
                        flag=0;
                        prev=1;
                        writer.write("\\color["+colors[colorLength-1]+"]");
                    }
                    writer.write(ar[x]);
                }

                writer.write("\n");
            }
            writer.close(); 
        }

    }
}

