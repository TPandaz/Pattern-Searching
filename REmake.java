import java.lang.module.FindException;
import java.util.Stack;

public class REmake {
    public static Character[] regexpCharArray; // holds regexp as characters
    public static int arrayLengthCounter = 0; // counter for length of arrays
    public static String[] stateTypeArray; // holds the state type
    public static int[] next1Array; // holds 1st possible next state
    public static int[] next2Array; // holds 2nd possible next state
    public static int state = 0; // position of state
    public static int j = 0; // counter/index for regexp

    public static Stack<Integer> savedStatesStack = new Stack<>(); // stores savedStates
    public static Stack<Integer> savedStatesBracketStack = new Stack<>(); // stores savedstates before a bracket
    public static int bracketCount = 0; //determines if were in a bracket(int to accommodate nested brackets)
    public static int savedStateBeforeBracket;
    public static boolean escapeChar = false;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java REmake <regexp>");
            System.exit(1);
        }
        String regexp = args[0];
        // insert each char of string into array, assign extra 2 spaces for start and
        // end state
        regexpCharArray = new Character[regexp.length() + 2];
        // insert placeholder for start state
        regexpCharArray[0] = '0';
        arrayLengthCounter++;
        for (int i = 0; i < regexp.length(); i++) {
            // if character is not a '(' or ')', increment arraylengthcounter
            if (!(regexp.charAt(i) == '(' || regexp.charAt(i) == ')')) {
                arrayLengthCounter++;
            }
            regexpCharArray[i + 1] = regexp.charAt(i);
        }
        // insert placeholder for end state
        regexpCharArray[regexpCharArray.length - 1] = '\0';
        arrayLengthCounter++;

        // debug
        for (char a : regexpCharArray) {
            System.out.println(a);
        }
        // init array lengths
        stateTypeArray = new String[arrayLengthCounter];
        next1Array = new int[arrayLengthCounter];
        next2Array = new int[arrayLengthCounter];

        savedStatesStack.add(state); // add starting state to savedstates
        parse();
        // print output
        for (int i = 0; i < stateTypeArray.length; i++) {
            System.out.println(i + "," + stateTypeArray[i] + "," + next1Array[i] + "," + next2Array[i]);
        }
    }

    // sets starting state and calls expression
    public static void parse() {
        setState(state, "BR", j + 1, j + 1);
        state++;
        j++;

        int branchReturn = expression();

        // if returned from expression, either at the last state, or malformed
        if (regexpCharArray[j] != '\0') {
            System.out.println(57);
            error();
        }
        // at last state so call setState
        setState(state, "end", 0, 0);
    }

    // E -> T
    // E -> TE
    public static int expression() {
        int result = term();
        // if current index is at end state, return
        if (regexpCharArray[j] == '\0') {
            return result;
        }
        // j would be +1 for this(next char in array)
        // isvocab looks ahead if next char is a literal
        if (isVocab(regexpCharArray[j]) || (regexpCharArray[j].compareTo('(')) == 0) {
            if (regexpCharArray[j] == '(') {
                bracketCount += 1;
            }
            // make note once this reaches mark as visited(end of recursion)
            int branchReturn = expression();

            return branchReturn;
        }
        return result;
    }

    // T -> F
    // T -> F*
    // T -> F | E
    //T -> F. 
    public static int term() {
        int result = factor();
        // state of the first literal before a * or |
        int finalState = state - 1;
        // System.out.println("j: " + j + " char: " + charArray[j]);

        // check that char is not null and if char is a '*'
        if ((regexpCharArray[j] != null) && regexpCharArray[j].compareTo('*') == 0) {
            savedStatesStack.add(state - 2); // add state connecting to br to savedstates
            setState(state, "BR", result, state + 1);
            System.out.println("state: " + state + " result: " + result);

            // check if currently not in a bracket and no savedstates in bracketstack
            if ((bracketCount==0) && savedStatesBracketStack.isEmpty()) {
                for (int i = 0; i < savedStatesStack.size(); i++) {
                    int savedState = savedStatesStack.pop();
                    // only change if state is a literal
                    if (next1Array[savedState] == next2Array[savedState]) {
                        setState(savedState, stateTypeArray[savedState], state, state);
                    }
                }
            } else {
                // update the state in bracketstack
                int savedState = savedStatesBracketStack.pop();
                // only change if state is a literal
                if (next1Array[savedState] == next2Array[savedState]) {
                    setState(savedState, stateTypeArray[savedState], state, state);
                }
                // update br
                setState(state, stateTypeArray[state], savedState + 1, state + 1);

            }
            
            j++;
            state++;
            return state - 1;
        }

        if ((regexpCharArray[j] != null) && regexpCharArray[j].compareTo('?') == 0) {
            
        }

        int stateInBracket = 0; // will never be 0 as 0 is start state
        // if char is a '|'
        if ((regexpCharArray[j] != null) && regexpCharArray[j].compareTo('|') == 0) {
            // keep track of current state as it is the branching state
            int currentState = state;
            // check if currently not in a bracket and no savedstates in bracketstack
            if ((bracketCount==0) && savedStatesBracketStack.isEmpty()) {
                //if preceding is not a bracket
                if (regexpCharArray[j-1] != ')') {
                    savedStatesStack.add(state - 2); // add state connecting to br to savedstates
                    // update previous states connecting to branch state
                    for (int i = 0; i < savedStatesStack.size(); i++) {
                        int savedState = savedStatesStack.pop();
                        // only change if state is a literal
                        if (next1Array[savedState] == next2Array[savedState]) {
                            setState(savedState, stateTypeArray[savedState], currentState, currentState);
                            System.out.println("savedstate: " + savedState + " currentstate: " + currentState);
                        }
                    }
                    savedStatesStack.add(state - 1);
                }else{
                    //need to connect state before bracket to br
                    setState(savedStateBeforeBracket, stateTypeArray[savedStateBeforeBracket], currentState, currentState);
                }

            } else if(!savedStatesBracketStack.isEmpty()){
                // update the state in bracketstack
                System.out.println("savedstatesBracket: " + savedStatesBracketStack.isEmpty());
                int savedState = savedStatesBracketStack.pop();
                savedStateBeforeBracket = savedState;
                // only change if state is a literal
                if (next1Array[savedState] == next2Array[savedState]) {
                    setState(savedState, stateTypeArray[savedState], state, state);
                    System.out.println("savedstate: " + savedState + " state: " + state + " j: " + j);
                }
                stateInBracket = savedState + 1;

            }

            state++;
            j++;
            // build the state after the '|' first and save to result
            int result2 = factor();
            if (stateInBracket != 0) {
                setState(currentState, "BR", stateInBracket, currentState + 1);
            } else {
                setState(currentState, "BR", result, result2);
            }
            result = currentState;
            // check if final state of previous state is same for n1 & n2
            // as it means it is a non-branching state
            if (next1Array[finalState] == next2Array[finalState]) {
                setState(finalState, stateTypeArray[finalState], state, state);
            } else {
                // it is a branching state
                setState(finalState, stateTypeArray[finalState], next1Array[finalState], state);
            }

        }

        return result; // or return result or br
    }

    // F -> V
    // F -> (E)
    public static int factor() {
        int result = 0;
        //check if \ char
        if(regexpCharArray[j] == '\\'){
            System.out.println("matches");
            escapeChar = true;
            j++;
        }
        // check if char is a vocab(literal) or preceded by an escape char
        if (isVocab(regexpCharArray[j]) || escapeChar) {
            setState(state, regexpCharArray[j].toString(), state + 1, state + 1);
            j++;
            result = state;
            state++;
            escapeChar = false;
        } else {
            if (regexpCharArray[j].compareTo('(') == 0) {
                // look ahead to check if closing parenthesis is followed by special character
                // by calling expression
                // save state before (
                savedStatesBracketStack.add(state - 1);
                bracketCount += 1;

                j++;
                result = expression();

                if (regexpCharArray[j].compareTo(')') == 0) {
                    j++;
                    bracketCount -= 1;
                } else {
                    // malformed regex
                    System.out.println(154);
                    error();
                }
            } else {
                // malformed regex
                System.out.println(159);
                error();
            }
        }
        return result;

    }

    // check if character is a literal(alphabet or number)
    public static boolean isVocab(Character ch) {
        Character[] specialChars = { '*', '?', '|', '(', ')' };
        // check if char is any one of these
        for (Character specialChar : specialChars) {
            if (ch.compareTo(specialChar) == 0)
                return false;
        }
        return true;
    }

    // add components of a state(output) to arrays
    public static void setState(int i, String type, int n1, int n2) {
        stateTypeArray[i] = type;
        next1Array[i] = n1;
        next2Array[i] = n2;
    }

    // outputs error message and existing entries to console
    public static void error() {
        System.err.println("Malformed regex! Existing states:");
        for (int i = 0; i < stateTypeArray.length; i++) {
            System.out.println(i + "," + stateTypeArray[i] + "," + next1Array[i] + "," + next2Array[i]);
        }
        System.exit(1);
    }

}
