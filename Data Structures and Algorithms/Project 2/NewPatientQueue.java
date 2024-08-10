import java.util.ArrayList;

public class NewPatientQueue {
    private Patient[] array;
    private PHashtable table;
    int size = 0;

    /*TO BE COMPLETED IN PART 1*/
    //constructor: set variables
    //capacity = initial capacity of array
    public NewPatientQueue(int capacity) {
        this.table = new PHashtable(capacity);
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
        table.put(p);
        return current;
    }

    private void fixHeap(int p)
    {
        if (p > (size/2) && p <= size) {
            return;
        }
        if ( ((2*p + 1) < size) && ((2*p + 2) < size)){
            if (array[p].compareTo(array[2*p + 1]) >= 0 && array[p].compareTo(array[2*p + 2]) >= 0 ) {
                return;
            }
            if (array[2*p + 1].compareTo(array[2*p + 2]) > 0) {
                swap(p,2*p + 1);
                fixHeap(2*p + 1);
            } else {
                swap(p,2*p +2);
                fixHeap(2*p + 2);
            }
        }
        if ((2*p +1) < size && ((2*p + 2) >= size)) {
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
            deleted.setPosInQueue(-1);
            return deleted;
        }
        Patient deleted = array[0];
        size--;
        swap(0,size);
        array[size] = null;
        fixHeap(0);
        table.remove(deleted.name());
        deleted.setPosInQueue(-1);
        return deleted;
        /*String maxName = "";
        for (int i = 0; i < table.getArray().length; i++) {
            if (table.getArray()[i] != null) {
                for (int x = 0; x < table.getArray()[i].size(); x++) {
                    if (table.getArray()[i].get(x).posInQueue() == 0) {
                        maxName = table.getArray()[i].get(x).name();
                    }
                }
            }
        }
        return remove(maxName);*/
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
    public PHashtable getTable() {
        return table;
    }
/*TO BE COMPLETED IN PART 2*/

    //remove and return the Patient with
    //name s from the queue
    //return null if the Patient isn't in the queue
    public Patient remove(String s) {
        if (array == null) {
            return null;
        }
        Patient p = table.get(s);
        if (p == null) {
            return null;
        }
        if (p.posInQueue() == size - 1) {
            size--;
            table.remove(s);
            p.setPosInQueue(-1);
            array[size] = null;
            return p;
        }
        for (int x = 0; x < size; x++) {
            if (array[x].name().equals(s)) {
                if (size == 1) {
                    p = array[0];
                    p.setPosInQueue(-1);
                    table.remove(s);
                    size--;
                    return  p;
                }else {
                    size--;
                    swap(x,size);
                    array[size]= null;
                    while (x != 0 && array[x].compareTo(array[(x-1)/2]) > 0){
                        swap(x,(x-1)/2);
                        x = (x-1)/2;
                    }
                    fixHeap(x);
                    break;
                }
            }
        }
        table.remove(s);
        p.setPosInQueue(-1);
        return p;

    }
    
    //update the emergency level of the Patient
    //with name s to urgency
    public void update(String s, int urgency) {
        for (int x = 0; x < size; x++) {
            if (array[x].name().equals(s)) {
                array[x].setUrgency(urgency);
                while (x != 0 && array[x].compareTo(array[(x-1)/2]) > 0){
                    swap(x,(x-1)/2);
                    x = (x-1)/2;
                }
                fixHeap(x);
            }
        }
        if (table.get(s) == null) {
            return;
        }
        table.get(s).setUrgency(urgency);
    }
}
input: s[] - array that contains the salary for every employee i
       p[] - array that contains the performance level of every employee i
Let n ← length of s
Let A be an array list with both pay and performance  // quick sort A by salary
Let count be an integer array

For int i = 0 to n do
    count2 = 0
    For int j = i + 1 to n do
        if A.get(j).pay > A.get(i).pay and A.get(j).performance > A.get(i).performance then
            count2 +1
        EndIf
    EndFor
    count[i] = count2
EndFor
return count


    