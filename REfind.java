import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author Dolf ten Have
 * SID: 1617266, 
 * Date: 07/06/2024
 * 
 */
public class REfind{
    //The FSM
    private static String[] c; //The characters of the states
    private static int[] n1; //The 1st next possible state
    private static int[] n2; //The 2nd next possible state
    private static boolean[] v; //The visited states | true if visited

    private static String currentLine; //The current line of the file
    //Line pointers
    private static int mark; //The current starting position of the FSM
    private static int pt; //The current position of the FSM

    private static Deque deque; //The Deque used to see the current an next states of the FSM
    private static boolean match; //True is a match has been found
    
    private static String usage = "java REmake \"<pattern>\" | REfind <path to text file>"; //The usage of the class

    public static void main(String[] args){
        if(args.length < 1){
            System.err.println("Please add a path to the text file after REfind. \n"+usage);
        }
        if(args.length > 1){
            System.err.println("Please add ONLY the path to the text file after REfind. \n"+usage);
        }
        parseSTDIn();
        matchFile(args[0]);
    }

    /**
     * Parses the content of STDIn to create the state arrays
     */
    private static void parseSTDIn(){
        Scanner sc = new Scanner(System.in);
        String[] line;
        ArrayList<String[]> in = new ArrayList<String[]>();
        System.err.println("Reading STD in:");

        //Reads the piped input into an array list for parsing into the varios arrays
        try{
            //Parse the content of STD out to their respective arrays
            while(sc.hasNextLine()){
                line = sc.nextLine().split(",");
                if(line.length == 4){
                    in.add(line);
                }
            }
        }catch(Exception e){
            System.err.println("Something went wrong!\n");
            e.printStackTrace(System.err);
        }

        if(in.size() > 0){
            //Initialises all the FSM arrays
            c = new String[in.size()];
            n1 = new int[in.size()];
            n2 = new int[in.size()];
            v = new boolean[in.size()];        
            try{
                //Parses the in list into their respective arrays
                for(int i = 0; i < in.size(); i++){
                    c[i] = in.get(i)[1];
                    n1[i] = Integer.parseInt(in.get(i)[2]);
                    n2[i] = Integer.parseInt(in.get(i)[3]);
                }
            }catch(Exception e){
                System.err.println("Error. There was a problem parsing the input into their arrays.");
                e.printStackTrace();
            }
        }else{
            System.err.println("Error. There was no piped input found to generate the FSM. \n"+usage);
        }

        printArrays();
    }

    /**
     * Tries to find a match for the pattern in the file
     * @param path The path to the text file
     */
    private static void matchFile(String path){
        try{
            File file = new File(path);    
            Scanner sc = new Scanner(file);
            System.out.println("Searching the text:");
            while(sc.hasNextLine()){
                newLine();
                currentLine = sc.nextLine();
                System.err.println("\tMatching line : " + currentLine);
                matchLine();               
            }
            sc.close();
        }catch(Exception e){
            System.err.println("Something went wrong!\n");
            e.printStackTrace(System.err);
        }
    }

    /**
     * A recursive function that Will try to find a pattern match on the current line
     * returns if a match is found or mark >= line length
     */
    private static void matchLine(){
        //The end of the line has been reached
        if(mark >= currentLine.length())
            return;

        System.err.println("\t\tStarting match from: m=" + mark +", c=" + currentLine.substring(mark, mark + 1));

        //Resets visited states, pointer and the Deque for a new pass
        Arrays.fill(v, false); //Sets all values in the visited array to false
        pt = mark; //Sets the pointer to mark
        resetDeque();
        tryMatch();

        //A match has been found
        if(match){
            System.out.println(currentLine);
            return;
        }

        //No match found
        mark++;
        matchLine();
    }

    /**
     * A recursive function that will try to find a match from mark
     * returns if a match is found or no match is found
     */
    private static void tryMatch(){  
        System.err.println("");
        
        //Gets the front element from the deque
        int s = deque.pop();
        System.err.print("\t\t\t s="+s +", pt="+pt);
        deque.dumpstates();

        //The scan is reached. There are no more possible states
        if(s == -1){
            System.err.println("");
            return;
        }

        //marks this state as
        visit(s);
            
        //A match has been found
        if(n1[s] == 0){
            System.err.println(", match found!!");
            match = true;
            return;
        }

        //If there is a branch
        if(c[s].compareTo("BR") == 0){

            //If this branch is the first state then don't branch
            if(s == 0){
                System.err.print(" is 'branch'");
                deque.push(n1[s]);
                tryMatch();
                return;
            }

            System.err.print(" Branching!");
            deque.push(n1[s]);
            deque.push(n2[s]);
            branch();
            return;
            //If the character is a wild card
        }
        
        //If the pointer has reached the end of the line then no match has been found
        if(pt == currentLine.length()){
            System.err.println("");
            return;
        }

        System.err.print(", Matching: "+ c[s]+"="+currentLine.substring(pt, pt + 1));

        //If the element is a wildcard
        if(c[s].compareTo("WC") == 0){
            System.err.print("WC = match!");
            deque.push(n1[s]);
            pt++;
        }else{
            //Otherwise a character match is attempted
            if(c[s].compareTo(currentLine.substring(pt, pt+1)) == 0){
                System.err.print(", match!");
                deque.push(n1[s]);
                pt++;
            }
        }     
        tryMatch();
    }

    /**
     * Branches the state machine
     * Attempts the first branch not visited first followed by the other branch if no match is found
     */
    private static void branch(){
        //Pops the current state and the scan off
        int s1 = deque.pop();
        int s2 = deque.pop();
        //If the first state has been visited, try state 2 first.
        if(v[s1]){
            deque.push(s2);
            tryMatch();
            if(match)
                return;
            deque.push(s1);
        }else{ //Otherwise, try state 1 first
            deque.push(s1);
            tryMatch();
            if(match)
                return;
            deque.push(s2);
        }   
        deque.putScan();
        tryMatch();
    }

    /**
     * Sets the vistited state at i to true
     * @param i The index of the state
     */
    private static void visit(int i){
        v[i] = true;
    }

    /**
     * Initialises components for a new line
     */
    private static void newLine(){
        mark = 0; //Sets mark to 0
        resetDeque();
        match = false;
    }

    /**
     * Resets the deque to initial state 0 and adds a new scan element
     */
    private static void resetDeque(){
        deque = new Deque();
        //Initialises the deque the state 0 values
        deque.putScan();
        deque.push(0);
    }

    /***********************************
     *          DEBUG OUTPUTS          *
     ***********************************/
    private static void printArrays(){
        printSTRArray(c);
        printIntrray(n1);
        printIntrray(n2);
    }

    private static void printSTRArray(String[] arr){
        System.out.print("STR Arr: ");
        for(int i = 0; i < arr.length; i++){
            System.out.print(arr[i]);
            if(i != arr.length - 1)
                System.out.print(", ");
        }
        System.out.println("");
    }
    private static void printIntrray(int[] arr){
        System.out.print("INT Arr: ");
        for(int i = 0; i < arr.length; i++){
            System.out.print(arr[i]);
            if(i != arr.length - 1)
                System.out.print(", ");
        }
        System.out.println("");
    }    
}