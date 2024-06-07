/**
 * A double ended Queue 
 * Usetlises properties of a doubly linked list to link the different value's to its neighbours
 * @author Dolf ten Have
 * SID: 1617266, 1617175
 * Date: 06/06/2024
 */
public class Deque{
    private Node head; //The head of the queue
    private Node tail; //The tail of the queue
    private Node temp; //A temporary node that will be added to the queue
    private int length; //The length of the queue

    /**
     * Creates a new empty Deque
     */
    public Deque(){
        head = null;
        tail = null;
        length = 0;
    }

    /**
     * A signle item in the Deque
     */
    private class Node{
        Node front; //The node to the front, if there is one
        Node back; //The node to the back, if there is one
        int value; //The value of the node

        /**
         * Creates a new node
         * @param data The data of the node
         */
        Node(int value){
            front = null;
            back = null;
            this.value = value;
        }

        /**
         * Sets the node to the front to the node passed in
         * @param node The node that will become the front
         */
        private void setFront(Node node){
            front = node;
        }

        /**
         * Gets the node to the back of this node
         * @return the node to the back or null if there is none
         */
        private Node getBack(){
            return back;
        }

        /**
         * Sets the node to the back of this one
         * @param node The node that will become back node
         */
        private void setBack(Node node){
            back = node;
        }

        /**
         * Gets the value of the node
         * @return An int with the value of the node
         */
        private int getValue(){
            return value;
        }
    }
    
    /**
     * Checks if queue is empty
     * @return true if the queue is empty
     */
    public boolean isEmpty(){
        if(length == 0){
            return true;
        }
        return false;
    }

    /**
     * Removes the item at the front of the queue
     */
    public int pop() throws NullPointerException{
        int i = head.getValue();
        head = head.getBack();
        if(head == null)
            tail = null;
        length--;
        return i;
    }

    /**
     * Adds an item to the front of the queue
     * @param state an int that will be added at the front of the queue
     */
    public void push(int state){
        temp = new Node(state);
        temp.setBack(head);
        if(head != null)
            head.setFront(temp);
        head = temp;
        if(tail == null)
            tail = head;
        length++;
    }

    /**
     * Adds an item at the end of the queue
     * @param state an int that will be added to the end of the queue
     */
    public void put(int state){
        temp = new Node(state);
        temp.setFront(tail);
        if(tail != null)
            tail.setBack(temp);
        tail = temp;
        if(head == null)
            head = tail;
        length++;
    }

    public void dumpstates(){
        System.err.print(" | h=");
        if(head == null){
            System.err.print("null");
        }else{
            System.err.print(head.getValue());
        }
        System.err.print( ", t=");
        if(tail == null){
            System.err.print("null");
        }else{
            System.err.print(tail.getValue());
        }
        System.err.print(" |");
    }
}