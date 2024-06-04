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
    public static Stack<Integer> savedStatesBranchStack = new Stack<>(); // stores saved states for alternation operator

    public static int bracketCount = 0; // determines if were in a bracket(int to accommodate nested brackets)
    public static int savedStateBeforeBracket;
    public static boolean escapeChar = false;
    public static int branchCount = 0;

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

        System.out.println(arrayLengthCounter);

        savedStatesStack.add(state); // add starting state to savedstates stack
        savedStatesBranchStack.add(state); // add starting state to branch stack
        parse();

        for (int i = arrayLengthCounter - 1; i >= 0; i--) {
            if (stateTypeArray[i] == null) {
                stateTypeArray[i] = "end";
            }
        }
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

        expression();

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
            // if (regexpCharArray[j] == '(') {
            // bracketCount += 1;
            // }
            // make note once this reaches mark as visited(end of recursion)
            int branchReturn = expression();

            return branchReturn;
        }
        return result;
    }

    // T -> F
    // T -> F*
    // T -> F | E
    // T -> F.
    public static int term() {
        int result = factor();
        // state of the first literal before a * or |
        // System.out.println("j: " + j + " char: " + charArray[j]);

        // check that char is not null and if char is a '*'
        if ((regexpCharArray[j] != null) && regexpCharArray[j].compareTo('*') == 0) {
            processRepetition(result);
            j++;
            state++;
            return state - 1;
        }

        if ((regexpCharArray[j] != null) && regexpCharArray[j].compareTo('?') == 0) {
            processOption(result);
            j++;
            state++;
            return state - 1;
        }

        // if char is a '|'
        if ((regexpCharArray[j] != null) && regexpCharArray[j].compareTo('|') == 0) {
            result = processAlternation(result);
        }

        return result; // or return result or br
    }

    // F -> V
    // F -> (E)
    public static int factor() {
        int result = 0;
        // check if \ char
        if (regexpCharArray[j] == '\\') {
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
                savedStatesBranchStack.add(state - 1);
                bracketCount += 1;

                j++;
                result = expression();

                if (regexpCharArray[j].compareTo(')') == 0) {
                    j++;
                    bracketCount -= 1;
                    System.out.println("this executes and bracketcount: " + bracketCount);
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

    // method is called when a '*' char is encountered
    public static void processRepetition(int result) {
        savedStatesStack.add(state - 2); // add state connecting to br to savedstates
        setState(state, "BR", result, state + 1);

        // check if j-2 is a ?, update state -3 to point at current state(only
        // valid for non-bracket)
        if (regexpCharArray[j - 2] == '?') {
            setState(state - 3, stateTypeArray[state - 3], state, state);
        }
        // check if currently not in a bracket and no savedstates in bracketstack
        if ((bracketCount == 0) && savedStatesBracketStack.isEmpty()) {
            for (int i = 0; i < savedStatesStack.size(); i++) {
                int savedState = savedStatesStack.pop();
                // check if state is a br or literal
                if (next1Array[savedState] == next2Array[savedState]) {
                    // get savedstate to point to current state
                    setState(savedState, stateTypeArray[savedState], state, state);
                } else {
                    setState(savedState, stateTypeArray[savedState], next1Array[savedState], state);
                }
            }
        } else {

            System.out.println(savedStatesBracketStack.isEmpty());
            // update the state in bracketstack
            int savedState = savedStatesBracketStack.pop();
            System.out.println("savedstateBracket: " + savedState);

            if (next1Array[savedState] == next2Array[savedState]) {
                setState(savedState, stateTypeArray[savedState], state, state);
            } else {
                setState(savedState, stateTypeArray[savedState], next1Array[savedState], state);
            }
            // check if theres is a ')' followed by a branch preceding
            if (regexpCharArray[j - 1] == ')' && regexpCharArray[j-2] != '\\') {
                for (int j2 = j - 2; j2 >= 0; j2--) {
                    if (regexpCharArray[j2] == '(')
                        break;
                    if (regexpCharArray[j2] == '|') {
                        setState(state, stateTypeArray[state], j2-1, state + 1);
                        setState(j2-2, stateTypeArray[j2-2] , state, state);
                    }

                }
            } else {
                //update current state
                setState(state, stateTypeArray[state], savedState + 1, state + 1);
            }

        }
    }

    // method is called when a '?' is encountered
    public static void processOption(int result) {
        savedStatesStack.add(state - 2); // add state connecting to br to savedstates
        setState(state, "BR", result, state + 1);
        System.out.println("state: " + state + " result: " + result);

        // update state before'?' to point to state after '?'
        if (next1Array[state - 1] == next2Array[state - 1]) {
            setState(state - 1, stateTypeArray[state - 1], state + 1, state + 1);
        } else {
            setState(state - 1, stateTypeArray[state - 1], next1Array[state - 1], state + 1);
        }
        // check if j-2 is also a ?, update state -3 to point at current state(only
        // valid for non-bracket)
        if (regexpCharArray[j - 2] == '?') {
            // this means a bracket precedes
            setState(state - 3, stateTypeArray[state - 3], state, state);
        }

        // check if currently not in a bracket and no savedstates in bracketstack
        if ((bracketCount == 0) && savedStatesBracketStack.isEmpty()) {
            for (int i = 0; i < savedStatesStack.size(); i++) {
                int savedState = savedStatesStack.pop();
                // check if state is a br or literal
                if (next1Array[savedState] == next2Array[savedState]) {
                    // get savedstate to point to current state
                    setState(savedState, stateTypeArray[savedState], state, state);
                } else {
                    setState(savedState, stateTypeArray[savedState], next1Array[savedState], state);
                }
            }
        } else {
            // might(or might not) be in a bracket but savedstateBracket is not empty
            // update the state in bracketstack
            int savedState = savedStatesBracketStack.pop();
            // check if state is a literal or br
            if (next1Array[savedState] == next2Array[savedState]) {
                setState(savedState, stateTypeArray[savedState], state, state);
            } else {
                setState(savedState, stateTypeArray[savedState], next1Array[savedState], state);
            }
            // check if savedstate is a '?', update savedstate-2 to point to current state
            if ((savedState != 0) && (next1Array[savedState - 1] == savedState + 1)) {
                setState(savedState - 1, stateTypeArray[savedState - 1], state, state);
            }

            System.out.println(savedState);
            // update br
            setState(state, stateTypeArray[state], savedState + 1, state + 1);

        }
    }

    // method is called when a '|' char is encountered
    public static int processAlternation(int result) {
        int previousState = state - 1;
        int currentState = state; // keep track of current state
        int savedState; // savedstate in a stack
        // check if not in a bracket
        // if (bracketCount == 0) {
        // savedState = savedStatesBranchStack.pop();

        // } else {
        // savedState = savedStatesBracketStack.pop();
        // }
        savedState = savedStatesBranchStack.pop();

        // assign savedstate to point to current state
        if (next1Array[savedState] == next2Array[savedState]) {
            setState(savedState, stateTypeArray[savedState], currentState, currentState);
        } else {
            setState(savedState, stateTypeArray[savedState], next1Array[savedState], currentState);
            System.out.println("does this execute");
        }
        System.out.println(savedState + " currentstate: " + currentState);

        state++;
        int currentJValue = j;
        j++;

        // build next state after '|' and save to result2
        int result2 = factor();
        System.out.println("result2: " + result2);

        // determine if savedstate is greater or currentstate+1 is greater, as result2
        // calls factor which evalutes everything after, so prevent currentstate from
        // being overwritten by first branch
        if ((!savedStatesBranchStack.isEmpty()) && (currentState + 1) < savedStatesBranchStack.lastElement()) {
            setState(currentState, "BR", savedState + 1, savedStatesBranchStack.pop());
        } else {
            setState(currentState, "BR", savedState + 1, currentState + 1);
            System.out.println("savedstate: " + savedState);
        }
        // set state before '|' to point to end
        setState(previousState, stateTypeArray[previousState], arrayLengthCounter - 1, arrayLengthCounter - 1);

        savedStatesBranchStack.add(currentState);
        savedStatesStack.add(currentState);
        if (bracketCount != 0) {
            savedStatesStack.add(currentState);
        }
        branchCount++;
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
