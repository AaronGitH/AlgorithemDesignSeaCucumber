
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * @author Sarah de Voss <satv@itu.dk>
 * @author Rene Anda Nielsen <rann@itu.dk>
 * @author Aaron Gornott <agor@itu.dk>
 */
public class SeaCucumber {
    
    static byte[][] scores;    
    static List<String> sequenceNames = new ArrayList();
    static List<List<Byte>> sequences = new ArrayList();
    static char[] letter = {'A', 'R', 'N', 'D', 'C', 'Q', 'E', 'G', 'H', 'I', 'L', 'K', 'M', 'F', 'P', 'S', 'T', 'W', 'Y', 'V', 'B', 'Z', 'X', '-'};
    
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
                sequenceNames.add(fields[0].substring(1,fields[0].length()));
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
        
        
        Map<Integer, SortedSet<Integer>> alignedPairs = new HashMap();
        for(int i=0; i < sequences.size(); i++){
            alignedPairs.put(i, new TreeSet());
            alignedPairs.get(i).add(i);  // no self Alignment
        }
        
        for(int i=0; i<sequences.size(); i++){            
            for(int j=0; j<sequences.size(); j++){                
                if(alignedPairs.get(i).contains(j)) continue;
                alignedPairs.get(i).add(j);
                alignedPairs.get(j).add(i);
                AlignmentResult a1 = sequenceAlignment(sequences.get(i),sequences.get(j));
                AlignmentResult a2 = sequenceAlignment(sequences.get(j),sequences.get(i));
                if(a1.score >= a2.score)
                    System.out.print("\n"+sequenceNames.get(i)+"--"+sequenceNames.get(j)+": "+a1.alignment);
                else
                    System.out.print("\n"+sequenceNames.get(j)+"--"+sequenceNames.get(i)+": "+a2.alignment);
            }
        }
        System.out.println();
    }
    
    static AlignmentResult sequenceAlignment(List<Byte> m, List<Byte> n){
        int delta = -4; // hardcore hard-code        
        int M[][] = new int[m.size()+1][n.size()+1];
        
        Tuple[][] backtrackingPointer = new Tuple[m.size()+1][n.size()+1];
        
        for(int i=0; i<m.size()+1; i++){
            M[i][0] = i * delta;
            backtrackingPointer[i][0] = new Tuple(i - 1, 0);
        }
        for(int j=0; j<n.size()+1; j++){
            M[0][j] = j * delta;
            backtrackingPointer[0][j] = new Tuple(0, j - 1);
        }
        backtrackingPointer[0][0] = new Tuple(0, 0);
        
        for(int i=1; i<m.size()+1; i++){
            for(int j=1; j<n.size()+1; j++){
//                M[i][j] = Math.max(Math.max(
//                    // α[x_i, y_j] + M [i – 1, j – 1].
//                    scores[m.get(i-1)][n.get(j-1)] + M[i - 1][j - 1],
//                    // δ + M [i – 1, j],
//                    delta + M[i - 1][j]),
//                    // δ + M [i, j – 1]),
//                    delta + M[i][j - 1]);

                int opt1 = scores[m.get(i-1)][n.get(j-1)] + M[i - 1][j - 1];
                int opt2 = delta + M[i - 1][j];
                int opt3 = delta + M[i][j - 1];
                int bestOpt = Integer.MIN_VALUE;
                
                if(opt1 > bestOpt){
                   bestOpt = opt1;
                   backtrackingPointer[i][j] = new Tuple(i-1, j-1);
                }
                if(opt2 > bestOpt){
                   bestOpt = opt2;
                   backtrackingPointer[i][j] = new Tuple(i-1, j);
                }
                if(opt3 > bestOpt){
                    bestOpt = opt3;
                   backtrackingPointer[i][j] = new Tuple(i , j-1);
                }
                M[i][j] = bestOpt;
            }
        }
        // show internal stuff
//        for(int i=0; i<m.size()+1; i++){
//            System.out.print("\n");
//            for(int j=0; j<n.size()+1; j++){
//                System.out.printf(" (x:"+i+" y:"+j+"=> %3d",M[i][j]);
//                System.out.print(")");
//            }
//        }
//        System.out.print("\n");
//        for(int i=0; i<m.size()+1; i++){
//            System.out.print("\n");
//            for(int j=0; j<n.size()+1; j++){
//                System.out.print(" (x:"+i+" y:"+j+"=> [x:"+backtrackingPointer[i][j].i+",y:"+backtrackingPointer[i][j].j+"])");
//            }
//        }

        // Backtracking
        List<Byte> closestSequenceM = new ArrayList();
        List<Byte> closestSequenceN = new ArrayList();
        
        Tuple lastPos = new Tuple(m.size(), n.size());
        
        while(lastPos.i > 0 || lastPos.j > 0){
            Tuple currPos = backtrackingPointer[lastPos.i][lastPos.j];
            
            if(currPos.i == lastPos.i && currPos.j < lastPos.j){
                closestSequenceM.add(mapScores('*'));
                closestSequenceN.add(n.get(lastPos.j-1));
            }
            if(currPos.i < lastPos.i && currPos.j == lastPos.j){
                closestSequenceM.add(m.get(lastPos.i-1));
                closestSequenceN.add(mapScores('*'));
            }
            if(currPos.i < lastPos.i && currPos.j < lastPos.j){
                closestSequenceM.add(m.get(lastPos.i-1));
                closestSequenceN.add(n.get(lastPos.j-1));
            }
            lastPos = currPos;
        }
        
        String alignment = M[m.size()][n.size()]+"\n";
        for(int i = closestSequenceM.size()-1; i >= 0; i--){
            alignment += letter[closestSequenceM.get(i)];
        }
        alignment += "\n";
        for(int i = closestSequenceM.size()-1; i >= 0; i--){
            alignment += letter[closestSequenceN.get(i)];
        }
        return new AlignmentResult(M[m.size()][n.size()],alignment);
    }
    
    static class AlignmentResult{ 
        public final int score;
        public final String alignment;
        public AlignmentResult(int score, String alignment){
            this.score = score;
            this.alignment = alignment;
        }
    }
    
    static class Tuple{ 
        public final int i, j;
        public Tuple(int i, int j){
            this.i = i;
            this.j = j; 
        } 
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