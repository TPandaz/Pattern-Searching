public class REmake {
    public static Character[] charArray;
    public static int j = 0;
    public static int state = 0; // state
    // public int next1 = 0;
    // public int next2 = 0;
    public static String[] typeStringArray;
    public static int[] next1Array;
    public static int[] next2Array;
    public static int arrayLengthCounter = 0;

    // used for update states
    public static int savedState = 0;
    public static int state1 = 1;
    // public static int closingBracketCount = 0;
    // public static int openingBracketCount = 0;
    public static int expressionCount = 0;
    public static int stateFromRecursion = 0;
    public static int stateAfterOpeningBracket = 0;
    public static int stateBeforeClosingBracket = 0;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java REmake <regexp>");
        }

        String regexp = args[0];
        // insert each char of string into array
        charArray = new Character[regexp.length() + 2];
        // placeholder for start state
        charArray[0] = '0';
        arrayLengthCounter++;
        for (int i = 0; i < regexp.length(); i++) {
            // if character is not a '(' or ')', increment arraylengthcounter
            if (!(regexp.charAt(i) == '(' || regexp.charAt(i) == ')')) {
                arrayLengthCounter++;
            }
            charArray[i + 1] = regexp.charAt(i);
            // System.out.println(charArray[i] + " at index: " + i );
        }
        charArray[charArray.length - 1] = '\0';
        arrayLengthCounter++;

        for (char a : charArray) {
            System.out.println(a);
        }

        typeStringArray = new String[arrayLengthCounter];
        next1Array = new int[arrayLengthCounter];
        next2Array = new int[arrayLengthCounter];

        parse();
        // System.out.println("wellformed regex!");

        updateStates(1, savedState);

        // print output
        for (int i = 0; i < typeStringArray.length; i++) {
            System.out.println(i + "," + typeStringArray[i] + "," + next1Array[i] + "," + next2Array[i]);
        }
    }

    public static void parse() {
        set_state(state, "BR", j + 1, j + 1);
        state++;
        j++;

        int br = expression();
        // set_state(0, "BR", br, br);
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
            int br = expression();

            return br;
        }
        return result;
    }

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
            int result2 = factor();
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
        // finlastate of whatever was before "("
        int finalState = j - 1;
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
                // set_state?????
                // set_state(finalState, typeStringArray[finalState], result, result);

                if (charArray[j].compareTo(')') == 0) {
                    j++;
                    set_state(finalState, typeStringArray[finalState], result, result);
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
        Character[] specialChars = { '*', '.', '?', '|', '(', ')', '\\' };
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

    public static int updateStates(int index, int savedState1) {
        System.out.println("state1: " + state1 + " savedState: " + savedState1);
        // saved state starts at 0
        // state1 should be whatever parsed into index
        for (int i = index; i < charArray.length; i++) {

            // saved state should always be behind state by 2
            if ((state1 - savedState1 >= 3) && (expressionCount == 0 || charArray[i] == '*')) {
                savedState1++;
            }
            //if came from bracket and not an |
            if (expressionCount > 0 && (charArray[i] != '|')) {
                set_state(stateBeforeClosingBracket, typeStringArray[stateBeforeClosingBracket], state1, state1);
            }

            if (charArray[i].compareTo('|') == 0) {
                // get savedstate from before to point to branch state
                if (next1Array[savedState1] == next2Array[savedState1]) {
                    set_state(savedState1, typeStringArray[savedState1], state1, state1);
                } else {
                    // it is a branching state
                    set_state(savedState1, typeStringArray[savedState1], next1Array[savedState1], state1);
                }
                // check if came from bracket(additional processing) if you just cam from a
                // bracket
                if (expressionCount > 0) {
                    // get branch to point to stateafterbracket
                    set_state(state1, typeStringArray[state1], stateAfterOpeningBracket, state1 + 1);
                }
                // look ahead and see if | followed by expression, get branch to point to
                // state+1
                if (charArray[i + 1] == '(') {
                    set_state(state1, typeStringArray[state1], state1-1, state1+1);
                }

                savedState1 = state1;
                state1++;
                expressionCount--;
            }

            if(charArray[i] == '*'){
                System.out.println("ss: " + savedState1 + " expressionCount: " + expressionCount);

                //if came from expression
                if (expressionCount > 0){
                    //set state before expression to point to *
                    if (next1Array[savedState1] == next2Array[savedState1]) {
                        set_state(savedState1, typeStringArray[savedState1], state1, state1);
                    } else {
                        // it is a branching state
                        set_state(savedState1, typeStringArray[savedState1], next1Array[savedState1], state1);
                    }
                    // get branch to point to stateafterbracket
                    set_state(state1, typeStringArray[state1], stateAfterOpeningBracket, state1 + 1);
                }
                else if (expressionCount == 0){
                    set_state(savedState1, typeStringArray[savedState1], state1, state1);
                }
            }

            // when this is called, state points to character after '('
            if (charArray[i].compareTo('(') == 0) {
                i++;
                stateAfterOpeningBracket = state1;
                int result = updateStates(i, savedState1);

                i = result;
                state1 = stateFromRecursion;
                expressionCount++;
                System.out.println("index: " + i);

            } else if (charArray[i].compareTo(')') == 0) {
                System.out.println("state2: " + state1);
                stateFromRecursion = state1;
                stateBeforeClosingBracket = state1 - 1;

                return i;
            }

            if (isVocab(charArray[i])) {
                state1++;
            }

        }

        return index;
    }



}
