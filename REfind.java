import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @author Dolf ten Have
 * SID: 1617266, 
 * Date:
 * 
 */
public class REfind{
    private static Deque deque; //The Deque used to see the current an next states of the FSM
    private static String[] c; //The characters of the states
    private static int[] n1; //The 1st next possible state
    private static int[] n2; //The 2nd next possible state
    private static boolean[] v; //The visited states | true if visited
    private static int mark; //The current starting position of the FSM
    private static int pt; //The current position of the FSM
    private static String currentLine; //The current line of the file
    private static boolean match;

    public static void main(String[] args){
        String usage = "java REmake <pattern> | REfind <path to text file>";
        if(args.length < 1){
            System.err.println("Please add a path to the text file after REfind. \n"+usage);
        }
        if(args.length > 1){
            System.err.println("Please add ONLY the path to the text file after REfind. \n"+usage);
        }

        parseSTDOut();
        matchFile(args[0]);
    }

    /**
     * Parses the content of STDIn to create the state arrays
     */
    private static void parseSTDOut(){
        try{
            //Parse the content of STD out to their respective arrays
        }catch(Exception e){
            System.err.println("Something went wrong!\n");
            e.printStackTrace(System.err);
        }
        v = new boolean[c.length];        
    }

    /**
     * Tries to find a match for the pattern in the file
     * @param path The path to the text file
     */
    private static void matchFile(String path){
        try{
            File file = new File(path);    
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()){
                newLine();
                currentLine = sc.nextLine();
                
            }
            sc.close();
        }catch(Exception e){
            System.err.println("Something went wrong!\n");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Will try to find match on the current
     * returns if a match is found of mark >= line length
     */
    private static void matchLine(){
        tryMatch();
        if(match){
            System.out.println(currentLine);
            return;
        }
        //No match found
        resetV();
        resetPT();
        mark++;
        matchLine();
    }

    /**
     * Will try to find a match from mark
     * returns if a match is found or no match is found
     */
    private static void tryMatch(){
        //Will work through the fsm untill a match is found or the state does not match a character
    }

    /**
     * Initialises components for a new line
     */
    private static void newLine(){
        resetV();
        mark = 0; //Sets mark to 0
        resetPT();
        deque = new Deque();
        match = false;
    }

    /**
     * Sets the vistited state at i to true
     * @param i The index of the state
     */
    private static void visit(int i){
        v[i] = true;
    }

    /**
     * Resets the visited array
     */
    private static void resetV(){
        Arrays.fill(v, false); //Sets all values in the visited array to false
    }

    /**
     * Resets the pointer to match mark
     */
    private static void resetPT(){
        pt = mark; //Sets the pointer to mark
    }
}