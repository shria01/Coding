public class StepStack {
    private Step[] stack;
    private int size;

    public StepStack() {
        //TODO: Implement constructor
        this.stack = new Step[1];
        this.size = 0;
    }

    public void push(Step step) {
        //TODO: Implement push()
        if(size == stack.length) {
            Step[] newArray = new Step[stack.length*2];
            for(int x = 0; x < size; x++) {
                newArray[x] = stack[x];
            }
            stack = newArray;
        }
        stack[size++] = step;
    }

    public Step peek() throws EmptyStackException {
        //TODO: Implement peek()
        if (size <= 0) {
            throw new EmptyStackException();
        }
        return this.stack[size-1];
    }

    public Step pop() throws EmptyStackException {
        //TODO: Implement pop()
        if (size <= 0) {
            throw new EmptyStackException();
        }
        Step popped = stack[--size];
        stack[size] = null;
        if (size > 0 && size == stack.length/4) {
            Step[] newArray = new Step[stack.length/2];
            for(int x = 0; x < size; x++) {
                newArray[x] = stack[x];
            }
            stack = newArray;
        }
        return popped;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public String toString() {
        String path = "";
        for (int i = 0; i < size; i++) {
            if (stack[i] != null) {
                {
                    path += stack[i];
                }
            }
        }
        return path;
    }
}