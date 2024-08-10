public class StepQueue {

    private int size;
    private Node head;
    private Node tail;

    public StepQueue() {
        //TODO: Implement constructor()
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    public boolean isEmpty() { return size == 0; }

    public int size() {
        return size;
    }


    public void enqueue(Step step) {
        //TODO: Implement enqueue()
        Node newNode = new Node();
        newNode.step = step;
        newNode.next = null;
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = tail.next;
        }
        size++;
    }

    public Step dequeue() throws EmptyQueueException {
        //TODO: Implement dequeue()
        if(size == 0) {
            throw new EmptyQueueException();
        }
        Node temp = head;
        head = head.next;
        size--;
        if (isEmpty()) {
            head = null;
            tail = null;
        }
        return temp.step;
    }


    /**
     * If the linked list is: Up->Down->Left->Right,
     * toString should return "UDLR"
     */
    @Override
    public String toString() {
        //TODO: Implement toString
        String output = "";
        while(size != 0) {
            try {
                Step a = dequeue();
                output.concat(a.toString());
            } catch (EmptyQueueException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    private static class Node {
        Step step;
        Node next;
    }
}


