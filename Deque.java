/**
 * A double ended Queue used with REfind
 * @author Dolf ten Have
 * SID: 1617266, 
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
     * Adds a scan element to the queue
     */
    public void pushScan(){
        push(-1);
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
         * Gets the Node to the front of this node
         * @return The node to the front or null if there is none
         */
        private Node getFront(){
            return front;
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
        private int getState(){
            return value;
        }
    }

    /**
     * Gets the first item in the queue
     * @return the int value of the first item in the queue
     * @throws if there are no items in the queue
     */
    public int front() throws NullPointerException{
        return head.getState();
    } 

    /**
     * Gets the last item in the queue
     * @return the int value of the last item in the queue
     * @throws NullPointerException If there are no items in the queue
     */
    public int back() throws NullPointerException{
        return tail.getState();
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
    public void pop(){
        if(head == null)
            return;
        head = head.getBack();
        if(head == null)
            tail = null;
        length--;
    }

    /**
     * Adds an item to the front of the queue
     * @param state an int that will be added at the front of the queue
     */
    public void push(int state){
        temp = new Node(state);
        length++;
        temp.setBack(head);
        if(head != null)
            head.setFront(temp);
        head = temp;
        if(tail == null)
            tail = head;
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
}