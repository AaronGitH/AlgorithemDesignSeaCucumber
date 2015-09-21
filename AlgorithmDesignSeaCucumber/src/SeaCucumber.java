
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * @author Sarah de Voss <satv@itu.dk>
 * @author Rene Anda Nielsen <rann@itu.dk>
 * @author Aaron Gornott <agor@itu.dk>
 */
public class SeaCucumber {
    
    static byte[][] scores;    
    static List<String> sequenceNames = new ArrayList();
    static List<List<Byte>> sequences = new ArrayList();
    static char[] letter = {'A', 'R', 'N', 'D', 'C', 'Q', 'E', 'G', 'H', 'I', 'L', 'K', 'M', 'F', 'P', 'S', 'T', 'W', 'Y', 'V', 'B', 'Z', 'X', '*'};
    
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("Toy_FASTAs-in.txt");
        Scanner sc = new Scanner(file);        
        
        String sequenceIn = "";
        while(sc.hasNextLine()) {
            String[] fields = sc.nextLine().trim().split(" ");
            if(fields[0].charAt(0) == '>'){
                if(!sequenceNames.isEmpty()){ // first entry
                    List<Byte> seq = new ArrayList();
                    for(int i=0; i<sequenceIn.length(); i++){
                        seq.add( mapScores(sequenceIn.charAt(i)) );
                    }
                    sequences.add(seq);
                    sequenceIn = "";
                }
                sequenceNames.add(fields[0]);
            }else{
                sequenceIn +=  fields[0];
            }
        }
        List<Byte> seq = new ArrayList();
        for(int i=0; i<sequenceIn.length(); i++){
            seq.add( mapScores(sequenceIn.charAt(i)) );
        }   
        sequences.add(seq);
        scores = getScores();
        
        
        
        //SEQUENCE-ALIGNMENT (m, n, x1, …, xm, y1, …, yn, δ, α)
        // _____________________________________________________
        //
        //FOR i = 0 TO m
        //M [i, 0] <- i δ.
        //FOR j = 0 TO n
        //M [0, j] <- j δ.
        //FOR i = 1 TO m
        //FOR j = 1 TO n
        //M [i, j] <- min { α[xi, yj] + M [i – 1, j – 1],
        //δ + M [i – 1, j],
        //δ + M [i, j – 1]).
        //RETURN M [m, n].
        
        
        for(int i=0; i<sequences.size(); i++){
            System.out.print("\n"+sequenceNames.get(i)+": "+"\n");
            for(byte val: sequences.get(i)){
                System.out.print( letter[val] );
            }
        }
    }
    
    static byte mapScores(char s){        
        for(byte i = 0; i < letter.length - 1; i++){
            if( s == letter[i]){
                return i;
            }
        }        
        return (byte)(letter.length - 1); // '*'
    }
    
    static byte[][] getScores(){
        byte[][] scoreTable = 
        {{4, -1, -2, -2, 0, -1, -1, 0, -2, -1, -1, -1, -1, -2, -1, 1, 0, -3, -2, 0, -2, -1, 0, -4}, 
        {-1, 5, 0, -2, -3, 1, 0, -2, 0, -3, -2, 2, -1, -3, -2, -1, -1, -3, -2, -3, -1, 0, -1, -4}, 
        {-2, 0, 6, 1, -3, 0, 0, 0, 1, -3, -3, 0, -2, -3, -2, 1, 0, -4, -2, -3, 3, 0, -1, -4}, 
        {-2, -2, 1, 6, -3, 0, 2, -1, -1, -3, -4, -1, -3, -3, -1, 0, -1, -4, -3, -3, 4, 1, -1, -4}, 
        {0, -3, -3, -3, 9, -3, -4, -3, -3, -1, -1, -3, -1, -2, -3, -1, -1, -2, -2, -1, -3, -3, -2, -4}, 
        {-1, 1, 0, 0, -3, 5, 2, -2, 0, -3, -2, 1, 0, -3, -1, 0, -1, -2, -1, -2, 0, 3, -1, -4}, 
        {-1, 0, 0, 2, -4, 2, 5, -2, 0, -3, -3, 1, -2, -3, -1, 0, -1, -3, -2, -2, 1, 4, -1, -4}, 
        {0, -2, 0, -1, -3, -2, -2, 6, -2, -4, -4, -2, -3, -3, -2, 0, -2, -2, -3, -3, -1, -2, -1, -4}, 
        {-2, 0, 1, -1, -3, 0, 0, -2, 8, -3, -3, -1, -2, -1, -2, -1, -2, -2, 2, -3, 0, 0, -1, -4}, 
        {-1, -3, -3, -3, -1, -3, -3, -4, -3, 4, 2, -3, 1, 0, -3, -2, -1, -3, -1, 3, -3, -3, -1, -4}, 
        {-1, -2, -3, -4, -1, -2, -3, -4, -3, 2, 4, -2, 2, 0, -3, -2, -1, -2, -1, 1, -4, -3, -1, -4}, 
        {-1, 2, 0, -1, -3, 1, 1, -2, -1, -3, -2, 5, -1, -3, -1, 0, -1, -3, -2, -2, 0, 1, -1, -4}, 
        {-1, -1, -2, -3, -1, 0, -2, -3, -2, 1, 2, -1, 5, 0, -2, -1, -1, -1, -1, 1, -3, -1, -1, -4}, 
        {-2, -3, -3, -3, -2, -3, -3, -3, -1, 0, 0, -3, 0, 6, -4, -2, -2, 1, 3, -1, -3, -3, -1, -4}, 
        {-1, -2, -2, -1, -3, -1, -1, -2, -2, -3, -3, -1, -2, -4, 7, -1, -1, -4, -3, -2, -2, -1, -2, -4}, 
        {1, -1, 1, 0, -1, 0, 0, 0, -1, -2, -2, 0, -1, -2, -1, 4, 1, -3, -2, -2, 0, 0, 0, -4}, 
        {0, -1, 0, -1, -1, -1, -1, -2, -2, -1, -1, -1, -1, -2, -1, 1, 5, -2, -2, 0, -1, -1, 0, -4}, 
        {-3, -3, -4, -4, -2, -2, -3, -2, -2, -3, -2, -3, -1, 1, -4, -3, -2, 11, 2, -3, -4, -3, -2, -4}, 
        {-2, -2, -2, -3, -2, -1, -2, -3, 2, -1, -1, -2, -1, 3, -3, -2, -2, 2, 7, -1, -3, -2, -1, -4}, 
        {0, -3, -3, -3, -1, -2, -2, -3, -3, 3, 1, -2, 1, -1, -2, -2, 0, -3, -1, 4, -3, -2, -1, -4}, 
        {-2, -1, 3, 4, -3, 0, 1, -1, 0, -3, -4, 0, -3, -3, -2, 0, -1, -4, -3, -3, 4, 1, -1, -4}, 
        {-1, 0, 0, 1, -3, 3, 4, -2, 0, -3, -3, 1, -1, -3, -1, 0, -1, -3, -2, -2, 1, 4, -1, -4}, 
        {0, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, 0, 0, -2, -1, -1, -1, -1, -1, -4}, 
        {-4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, 1}};
        return scoreTable;
    }
    
}
