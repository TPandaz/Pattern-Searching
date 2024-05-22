import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

// need a cfg to tell if regexp is wellformed
// term(anything that can be involved in an operation):
// match a literal or a closure, or disjuction with a factor
// top-down left to right recursive descent parser
/**
 * start with an E, followed by a queue of all possbile rewrites
 */

// expression:anything in a bracket
// literal(v: actual chars)
// closure:
// disjunction:
// concatenation:
/**
 * E -> T
 * E -> TE
 * T -> F
 * T -> F*
 * T -> F | F
 * factor has 2 rules, if it is a literal or if it is an expression
 * F -> v
 * F -> (E)
 */

public class REmake {

    public static Character[] charArray;
    public static int j = 0;
    public static int state = 0; // state
    // public int next1 = 0;
    // public int next2 = 0;
    public static String[] charStringArray;
    public static int[] next1Array;
    public static int[] next2Array;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java REmake <regexp>");
        }

        String regexp = args[0];
        // insert each char of string into array
        charArray = new Character[regexp.length() + 1];
        for (int i = 0; i < regexp.length(); i++) {
            charArray[i] = regexp.charAt(i);
            // System.out.println(charArray[i] + " at index: " + i );
        }
        charArray[charArray.length - 1] = '\0';

        charStringArray = new String[charArray.length];
        next1Array = new int[charArray.length];
        next2Array = new int[charArray.length];

        parse();
        System.out.println("wellformed regex!");

    }

    public static void parse() {

        int result = expression();
        // if exited out of expression then error as char is not an expression
        // check if array at index j is valid(not at the end)
        if (charArray[j] != '\0') {
            System.out.println(69);
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
        int f = state -1;
        int t1 = result;
        int t2;
        // System.out.println("j: " + j + " char: " + charArray[j]);
        if ((charArray[j] != null) && charArray[j].compareTo('*') == 0) {
            set_state(state, "BR", result, state + 1);
            j++;
            result = state;
            state++;
        }

        // if ((charArray[j] != null) && charArray[j].compareTo('|') == 0) {
        //     int br = state;
        //     state++;
        //     int result2 = factor();
        //     set_state(br, "BR", result, result2);
        //     result = br;
        //     j++;
        //     if (next1Array[f1] == next2Array[f1]) {
        //         set_state(f1, ch[f1], state, state);
        //     } else {
        //         set_state(f1, ch[f1], next1[f1], state);
        //     }}
        
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
            if (charArray[j + 2].compareTo('*') == 0) {
                set_state(state, charArray[j].toString(), state + 2, state + 2);
            } else {
                set_state(state, charArray[j].toString(), state + 1, state + 1);
            }
            j++;
            result = state;
            state++;
            // System.out.println("j: " +j);
        } else {
            if (charArray[j].compareTo('(') == 0) {
                j++;
                result = expression();

                if (charArray[j].compareTo(')') == 0) {
                    j++;
                } else {
                    // malformed regex
                    System.out.println(149);
                    error();
                }
            } else {
                // malformed regex
                System.out.println(164);
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
        charStringArray[i] = type;
        next1Array[i] = n1;
        next2Array[i] = n2;
        System.out.println(i + "," + type + "," + n1 + "," + n2);
        return i;
    }

    public static void error() {
        System.err.println("malformed regex! Type in a correct regex");
        System.exit(1);
    }
}