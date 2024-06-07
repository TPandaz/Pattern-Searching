import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author Dolf ten Have
 * SID: 1617266, 
 * Date:
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
        resetV();
        resetPT();

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
        int s = deque.pop();
        //The scan is reached. There are no more possible states
        if(s == -1)
            return;
        //If the pointer has reached the end of the line
        if(pt == currentLine.length())
            return;
        //marks this state as
        visit(s);

        //A match has been found
        if(n1[s] == 0){
            match = true;
            return;
        }
        //If the the next two possible states do no match there is a branch
        //I have decided to use this that comparing The state character to "BR" because comparing int's is faster that comparing strings.
        //And ultimatly this will lead to the same result.
        if(n1[s] != n2[s]){
            deque.put(n1[s]);
            deque.put(n2[s]);
            deque.putScan();
            branch();
        }else{
            if(c[s].compareTo(currentLine.substring(pt, pt+1)) == 0){
                deque.push(n1[s]);
                pt++;
            }
        }     
    }

    /**
     * Branches the state machine to one of the branches
     */
    private static void branch(){
        //Pops the current state and the scan off
        deque.pop();
        int s1 = deque.pop();
        int s2 = deque.pop();
        //If the first state has been visited, try state 2 first.
        if(v[s1]){
            deque.push(s2);
            if(match)
                return;
            deque.push(s1);
        }else{ //Otherwise, try state 1 first
            deque.push(s1);
            if(match)
                return;
            deque.push(s2);
        }   
    }

    /**
     * Initialises components for a new line
     */
    private static void newLine(){
        mark = 0; //Sets mark to 0
        deque = new Deque();
        //Initialises the deque the state 0 values
        deque.putScan();
        deque.push(0);
        deque.put(n1[0]);
        deque.put(n2[0]);
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