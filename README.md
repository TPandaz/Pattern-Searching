# PatternSearch

## REmake
This program follows Tony's code on the parser and compiler. It evaluates the regular expression following these rules: E -> T , E-> TE, T->F, T-> F*, T-> F?, T -> F|E. To update states for the regexp, stacks are used to save the current state that can be popped and updated further along in the regexp. A look back method is implemented when a '|', '?' or '*' is the current state, popping of the saved states in the stack to update them.

Usage: java Remake > make.txt

*Note: The '|' has the lowest precedence so it splits everything before and after it into 2 possible states. 
I have also initialised the start state as a branch state pointing to the first state in the regexp. 
A 'WC' was used to match any literal when a '.' is encountered *


## REfind

__**Usage:**__

`java REfind "<path-to-search-text>"`

*Note: REfind relies on a state array recieved from the standard input. The program will not run without it*

This class uses a state array to find a pattern described by the state array inside of the search text. A deque is used to keep track of the current machine state. The states are stored over 3 different arrays. The index of it's items linking to the items of the other arrays. c is the character array. A single character denotes a match must be made to it. Multiple characters indicate a special rule such a wild or branch. n1 and n2 hold the positions of the next two possible states. If a match is found on the current line then the state machine returns and the line is output to the standard output. No output indicates no match was found.

## Deque
This is a restricted output double ended queue to which you can push integers to either front or end. However items can only be popped off the front of the queue. For the purpose of this assignment the integers represent states in the FSM.
