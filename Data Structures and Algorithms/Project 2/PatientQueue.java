public class PatientQueue {
    private Patient[] array;
    int size = 0;
    
    //constructor: set variables
    //capacity = initial capacity of array 
    public PatientQueue(int capacity) {
        this.array = new Patient[capacity];
        this.size = 0;
    }
    public void swap (int a, int b) {
        Patient temp = array[a];
        array[a] = array[b];
        array[b] = temp;
        array[a].setPosInQueue(a);
        array[b].setPosInQueue(b);
    }
    //insert Patient p into queue
    //return the final index at which the patient is stored
    //return -1 if the patient could not be inserted
    public int insert(Patient p) {
        int current = size;
        if (this.array.length == size) {
            return -1;
        }
        this.array[current] = p;
        this.array[current].setPosInQueue(current);

        while (current != 0 && array[current].compareTo(array[(current-1)/2]) > 0){
            swap(current,(current-1)/2);
            current = (current-1)/2;
        }
        size++;
        return current;
    }

    private void fixHeap(int p)
    {
        if (p >= ((size)/2) && p <= size) {
            return;
        }
        if ( ((2*p + 1) < size) && ((2*p + 2) < size)){
            if (array[p].compareTo(array[2*p + 1]) >= 0 && array[p].compareTo(array[2*p + 2]) >= 0 ) {
                return;
            }
            if (array[2*p + 1].compareTo(array[2*p + 2]) >= 0) {
                swap(p,2*p + 1);
                fixHeap(2*p + 1);
            } else {
                swap(p,2*p +2);
                fixHeap(2*p + 2);
            }
        }else if ((2*p +1) < size) {
            if (array[p].compareTo(array[2*p + 1]) >= 0 ) {
                return;
            }
            if (array[2 * p + 1].compareTo(array[p]) > 0) {
                swap(p, 2 * p + 1);
                fixHeap(2 * p + 1);
            }
        }
    }

    //remove and return the patient with the highest urgency level
    //if there are multiple patients with the same urgency level,
    //return the one who arrived first
    public Patient delMax() {
        if (isEmpty()) {
           return  null;
        } else if (size == 1) {
            Patient deleted = array[0];
            array[0] = null;
            size--;
            return deleted;
        }

        Patient deleted = array[0];
        size--;
        swap(0,size);
        array[size]= null;
        fixHeap(0);
        return deleted;
    }

    //return but do not remove the first patient in the queue
    public Patient getMax() {
        return array[0];
    }

    //return the number of patients currently in the queue
    public int size() {
        return size;
    }

    //return true if the queue is empty; false else
    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    //used for testing underlying data structure
    public Patient[] getArray() {
	    return array;
    }
}
    