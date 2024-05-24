
public class REmake {

    public static Character[] charArray;
    public static int j = 0;
    public static int state = 0; // state
    // public int next1 = 0;
    // public int next2 = 0;
    public static String[] typeStringArray;
    public static int[] next1Array;
    public static int[] next2Array;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java REmake <regexp>");
        }

        String regexp = args[0];
        // insert each char of string into array
        charArray = new Character[regexp.length() + 2];
        // placeholder for start state
        charArray[0] = '0';
        for (int i = 0; i < regexp.length(); i++) {
            charArray[i + 1] = regexp.charAt(i);
            // System.out.println(charArray[i] + " at index: " + i );
        }
        charArray[charArray.length - 1] = '\0';

        for (char a : charArray) {
            System.out.println(a);
        }

        typeStringArray = new String[charArray.length];
        next1Array = new int[charArray.length];
        next2Array = new int[charArray.length];

        parse();
        // System.out.println("wellformed regex!");
     
        System.out.println("state: " + state);
        updateStates(1);

        for (int i = 0; i < typeStringArray.length; i++) {
            System.out.println(i + "," + typeStringArray[i] + "," + next1Array[i] + "," + next2Array[i]);
        }

    }

    public static void parse() {
        set_state(state, "BR", j + 1, j + 1);
        state++;
        j++;

        int result = expression();
        // if exited out of expression then error as char is not an expression
        // check if array at index j is valid(not at the end)
        if (charArray[j] != '\0') {
            System.out.println(68);
            error();
        }
        set_state(state, "end", 0, 0);
    }

    // E -> T
    // E -> TE
    public static int expression() {
        int result = term();
        // System.out.println("j at expression(): " + j);
        if (charArray[j] == '\0') {
            return result;
        }

        // j would be +1 for this(next char in array)
        // isvocab looks ahead if next char is a literal
        if (isVocab(charArray[j]) || (charArray[j].compareTo('(')) == 0) {
            // make note once this reaches mark as visited(end of recursion)
            expression();
            return result;
        }
        return result;
    }

    // T -> F
    // T -> F*
    // T -> F | F
    public static int term() {
        // calls factor first as 1st char cannot be special char
        int result = factor();
        // final state of first literal before * or | (final state of result)
        int finalState = state - 1;
        // System.out.println("j: " + j + " char: " + charArray[j]);
        if ((charArray[j] != null) && charArray[j].compareTo('*') == 0) {
            // n1 is the previous state and n2 is the next state
            set_state(state, "BR", result, state + 1);
            j++;
            state++;
            return state - 1;
        }

        if ((charArray[j] != null) && charArray[j].compareTo('|') == 0) {
            // keep track of state(number of slot to build |), is the branching state
            int br = state;
            state++;
            j++;
            // build the state after | first and save to result
            int result2 = factor(); // from hereeeeeeeeeeeeeeeeeeeeeeee
            // n1 is the previous state before | and and n2 is the state after the |
            set_state(br, "BR", result, result2);
            result = br;
            // check if final state of previous state is same for n1 & n2
            // means non branching state
            if (next1Array[finalState] == next2Array[finalState]) {
                set_state(finalState, typeStringArray[finalState], state, state);
            } else {
                // it is a branching state
                set_state(finalState, typeStringArray[finalState], next1Array[finalState], state);
            }
        }

        // add later
        // for '.' call factor and match any literal
        // for '?'
        // for '\followed by any specialChar'
        return result; // or return result or br
    }

    // F -> v
    // F -> (E)
    public static int factor() {
        int result = 0;
        // System.out.println("char: " + charArray[j] + " j:" + j);
        if (isVocab(charArray[j])) {
            // System.out.println("j: " + j + " char: " + charArray[j]);
            // System.out.println("char: " + charArray[j] + " at index: " + j + " is a
            // vocab");

            set_state(state, charArray[j].toString(), state + 1, state + 1);
            j++;
            result = state;
            state++;
            // System.out.println("j: " +j);
        } else {
            if (charArray[j].compareTo('(') == 0) {
                // implement look ahead to check if closing parenthesis is follwoed by special
                // character
                j++;
                result = expression();

                if (charArray[j].compareTo(')') == 0) {
                    j++;
                } else {
                    // malformed regex
                    System.out.println(174);
                    error();
                }
            } else {
                // malformed regex
                System.out.println(180);
                error();
            }
        }
        return result;

    }

    // check if character is a literal(alphabet or number)
    public static boolean isVocab(Character ch) {
        Character[] specialChars = { '*', '.', '?', '|', '(', ')', '\0' };
        // check if char is any one of these
        for (Character specialChar : specialChars) {
            if (ch.compareTo(specialChar) == 0)
                return false;
        }
        return true;
    }

    // build a state(output) once regex is determined to be wellformed
    public static int set_state(int i, String type, int n1, int n2) {
        // System.out.println(type);
        typeStringArray[i] = type;
        next1Array[i] = n1;
        next2Array[i] = n2;
        return i;
    }

    public static void error() {
        System.err.println("malformed regex! Existing entries:");
        for (int i = 0; i < typeStringArray.length; i++) {
            System.out.println(i + "," + typeStringArray[i] + "," + next1Array[i] + "," + next2Array[i]);
        }
        System.exit(1);
    }

    /*
     * when you have c(aa)|b, on the branch state, if character before is a ")"
     * (bracket counter >0) ,
     * then go to state before closing bracket and put its n1 & n2 to state of BR
     * 
     * when you have BR (* or |), on the branch state, if no ")" before,
     * then go to state -2, on make its n1*n2 to the state of BR
     * 
     * when you have branch state like a(a|b), 1st a has to point to |
     * 
     * recursively call this at the main to update states 
     * save states?
     * should only recursively csll when brackets
     */
    public static int updateStates(int index){
        int savedState = index-1;
        for (int i = index;i < charArray.length; i++){
            //if | and diff is not more than 2
            if(charArray[i].compareTo('|') == 0 && (i - savedState ==2)){
                set_state(savedState, typeStringArray[savedState], i, i);
                savedState = state ;
                state++; 
            }

            //when this is called, state points to character after '('
            if(charArray[i].compareTo('(') == 0){
                int result = updateStates(i);
            }
            else if(charArray[i].compareTo(')') == 0){
                return i;
            } 

            if(isVocab(charArray[i])){
                state++;
            }

            
        }

    //     int openingBracketCount = 0;
    //     int closingBracketCount = 0;
    //     state = state--; //as we are starting from 1 off the end state
    //     Stack <Integer> savedStates = new Stack<>();

    //     //assign i to index before null state(end state)
    //     for(int i = charArray.length -2; i >= 0;i--){
    //         System.out.println("i: " + i);
    //         //check if closing bracket
    //         if(charArray[i].compareTo(')') == 0 ){
    //             closingBracketCount++;
    //             //save the state before this
    //             savedStates.add(state);
    //             continue;
    //         }else if(charArray[i].compareTo('(') == 0 ){
    //             openingBracketCount++;
    //             int state1 = savedStates.pop();
    //             //update state as state point to char before '('
    //             System.out.println("state: " + state);

    //             //check if non branching state then set state
    //             if (next1Array[state-1] == next2Array[state-1]) {
    //                 set_state(state-1, typeStringArray[state-1], state1, state1);
    //             }              
    //             continue;
    //         }

    //         if(charArray[i].compareTo('|')==0){
    //             savedStates.add(state -1);
    //             state--;
    //             continue;
    //         }
    //         if(isVocab(charArray[i])){
    //             state--;//state would be at 1 now
    //             System.out.println(charArray[i] + " state: " +state);
    //             continue;
    //         }
          
    //     }
    //     System.out.println("states still in stack: ");
    //     savedStates.forEach(System.out::println);
    // }
}}