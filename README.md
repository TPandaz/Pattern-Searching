# PatternSearch

## REmake

## REfind
**__Usage__**
`java REfind "<path-to-search-text>"`
*Note: REfind relies on a state array recieved from the standard input. The program will not run without it*

This call uses a state array to find a pattern outlined in the state array inside of the search text. A deque is used to keep track of the current machine state. The states are stored over 3 differnt arrays. The index of it's items linking to the items of the other arrays. c is the character array. A single character denotes a match must be made to it. Multiple characters indicate a special rule such a wild or branch. n1 and n2 hold the positions of the next two possible states. If a match is found on the current line then the state machine returns and the line is output to the standard output. No output indicates no match was found.

## Deque
This is a restricted output double ended queue to which you can push integers to either front or end. However items can only be popped off the front of the queue. For the purpose of this assignment the integers represent states in the FSM.