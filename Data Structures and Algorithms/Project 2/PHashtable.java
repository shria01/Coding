import java.util.ArrayList;
public class PHashtable {
    private ArrayList<Patient>[] table;
    int size = 0;
    
    //set the table size to the first 
    //prime number p >= capacity
    public PHashtable (int capacity) {
        int size = capacity;
        if (!isPrime(capacity)) {
            size = getNextPrime(capacity);
        }
        this.table = new ArrayList[size];
        this.size = 0;
    }
    public int hashing (String name) {
        int h = name.hashCode()%this.table.length;
        if (h < 0) {
            return h + table.length;
        } else {
            return h;
        }
    }

    //return the Patient with the given name 
    //or null if the Patient is not in the table
    public Patient get(String name) {
        int index = hashing(name);
        if (table[index] == null) {
            return null;
        }
        for (int i = 0; i < table[index].size(); i++) {
            if (name.equals(table[index].get(i).name())) {
                return table[index].get(i);
            }
        }
        return null;
    }

    //put Patient p into the table
    public void put(Patient p) {
        int index = hashing(p.name());
        if (table[index] == null) {
            table[index] = new ArrayList<>();
            table[index].add(p);
            size++;
            return;
        }
        for (int i = 0; i < table[index].size(); i++) {
            if (table[index].get(i) != null) {
                if (p.name().equals(table[index].get(i).name())) {
                    return;
                }
            }
        }
        table[index].add(p);
        size++;
        return;
    }

    //remove and return the Patient with the given name
    //from the table
    //return null if Patient doesn't exist
    public Patient remove(String name) {
        int index = hashing(name);
        if (table[index] == null) {
            return null;
        }
        for (int i = 0; i < table[index].size(); i++) {
            if (name.equals(table[index].get(i).name())) {
                Patient removed = table[index].get(i);
                table[index].remove(i);
                size--;
                removed.setPosInQueue(-1);
                return removed;
            }
        }
        return null;
	//TO BE COMPLETED
    }	    

    //return the number of Patients in the table
    public int size() {
        return size;
	//TO BE COMPLETED
    }

    //returns the underlying structure for testing
    public ArrayList<Patient>[] getArray() {
	return table;
    }
    
    //get the next prime number p >= num
    private int getNextPrime(int num) {
    if (num == 2 || num == 3)
        return num;
    int rem = num % 6;
    switch (rem) {
        case 0:
        case 4:
            num++;
            break;
        case 2:
            num += 3;
            break;
        case 3:
            num += 2;
            break;
    }
    while (!isPrime(num)) {
        if (num % 6 == 5) {
            num += 2;
        } else {
            num += 4;
           }
        }
        return num;
    }


    //determines if a number > 3 is prime
    private boolean isPrime(int num) {
        if(num % 2 == 0){
            return false;
        }
        
	int x = 3;
	for(int i = x; i < num; i+=2){
	    if(num % i == 0){
		    return false;
        }
    }
	return true;
    }
}
      

