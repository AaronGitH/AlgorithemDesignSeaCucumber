
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
    
    public static void main(String[] args) throws FileNotFoundException{
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
                sequenceIn += fields[0];
            }
        }
        List<Byte> seq = new ArrayList();
        for(int i=0; i<sequenceIn.length(); i++){
            seq.add( mapScores(sequenceIn.charAt(i)) );
        }   
        sequences.add(seq);
        scores = getScores();
        
        
        System.out.println("\n\n"+sequenceAlignment()+"\n");
        //output();
    }
    
    // SEQUENCE-ALIGNMENT (m, n, x1, …, xm, y1, …, yn, δ, α)
    static int sequenceAlignment(){

        List<Byte> m = sequences.get(2); 
        List<Byte> n = sequences.get(1); 
        
        int delta = -4; // hardcore hard-code
        
        int M[][] = new int[m.size()+1][n.size()+1]; 
        // FOR i = 0 TO m
        for(int i=0; i<m.size()+1; i++){
            // M [i, 0] <- i δ.
            M[i][0] = i * delta;
        }
        // FOR j = 0 TO n
        for(int j=0; j<n.size()+1; j++){
            // M [0, j] <- j δ.
            M[0][j] = j * delta;
        }
        
        // FOR i = 1 TO m
        for(int i=1; i<m.size()+1; i++){
            // FOR j = 1 TO n
            for(int j=1; j<n.size()+1; j++){
                // M [i, j] <- min { 
                M[i][j] = Math.max(Math.max(
                    // α[x_i, y_j] + M [i – 1, j – 1].
                    scores[m.get(i-1)][n.get(j-1)] + M[i - 1][j - 1],
                    // δ + M [i – 1, j],
                    delta + M[i - 1][j]),
                    // δ + M [i, j – 1]),
                    delta + M[i][j - 1]);
            }
        }
        for(int i=0; i<m.size()+1; i++){
            System.out.print("\n");
            for(int j=0; j<n.size()+1; j++){
                System.out.printf("%3d ",M[i][j]);
            }
        }
        
        // RETURN M [m, n].
        return M[m.size()][n.size()];
    }
    
    static void output(){
        for(int i=0; i<sequences.size(); i++){
            System.out.print("\n"+sequenceNames.get(i)+": "+"\n");
            for(byte val: sequences.get(i)){
                System.out.print( letter[val] );
            }
        }
        System.out.println("\n");
    }
    
    static byte mapScores(char s){        
        for(byte i = 0; i < letter.length - 1; i++)
            if( s == letter[i])
                return i;
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