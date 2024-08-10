import java.util.ArrayList;

public class Graph {


    /**
     * Node class
     **/
    public static class Node {
        int vertex;
        double weight;
        Node(int vertex, double weight) {
            this.vertex = vertex;
            this.weight = weight;
        }
    }

    private ArrayList<ArrayList<Node>> adj;
    private int size;   //represents the number of vertices in the graph
    private int edges;  //represents the number of edges in the graph
    boolean isDirected; //true when the graph is directed otherwise false
    String[][] end;
    String a;
    int[] visitedSimple;
    //TODO add other instance variables that might be needed

    /***
     * initialize an undirected graph with n vertices and no edges
     * @param vertices
     */
    public void ugraph(int vertices) {
        //TODO implement ugraph
        adj = new ArrayList<ArrayList<Node>>();
        for (int i = 0; i < vertices; i++) {
            adj.add(new ArrayList<Node>());
        }
        size = vertices;
        edges = 0;
        isDirected = false;
    }

    /***
     * initialize a directed graph with n vertices and no edges
     * @param vertices
     */
    public void dgraph(int vertices) {
        //TODO implement dgraph
        adj = new ArrayList<ArrayList<Node>>();
        for (int i = 0; i < vertices; i++) {
            adj.add(new ArrayList<Node>());
        }
        size = vertices;
        edges = 0;
        isDirected = true;
    }

    /***
     * add an edge to a graph that goes from vertex u to vertex v and has weight w;
     * if u or v are not vertices in the graph return false otherwise return true
     *
     * Make sure that you add the edge correctly for both directed and undirected graph
     *
     * @param fromV
     * @param toV
     * @param weight
     */
    public boolean addEdge(int fromV, int toV, double weight) {
        if (fromV < 0 || toV < 0 || fromV >= adj.size() || toV >= adj.size() ) {
            return false;
        }
        adj.get(fromV).add(new Node(toV,weight));
        if (isDirected) {
            edges++;
            return true;
        }
        adj.get(toV).add(new Node(fromV,weight));
        ArrayList<Node> b = adj.get(toV);
        edges++;
        return true;
    }

    /***
     * print the number of vertices in the graph
     */
    public int vertexSize() {
        //TODO implement vertexSize
        return size;
    }

    /***
     * print the number of edges in the graph
     */
    public int edgeSize() {
        //TODO implement edgeSize
        return edges;
    }

    /***
     * print the weight of the edge from vertex u to vertex v
     * if u or v are not vertices in the graph return false otherwise return -1.0
     * @param u
     * @param v
     */
    public double weight(int u, int v) {
        //TODO implement weight
        if (u < 0 || v < 0 || u >= adj.size() || v >= adj.size() ) {
            return -1.0;
        }
        ArrayList<Node> a = adj.get(u);
        if (a == null) {
            return -1.0;
        }
        for (Node n: a) {
            if (n.vertex == v) {
                return n.weight;
            }
        }
        return -1.0;
    }

    /***
     * print a list of vertices that are adjacent to vertex v
     * return a String containing the list of adjacent vertices in ascending order
     * return "none" if there are no adjacent vertices
     * @param v
     */
    public String adjList(int v) {
        if(v < 0 || v >= size) {
            return "none";
        }
        if(adj.get(v).size() == 0) {
            return "none";
        }
        ArrayList<Node> a = adj.get(v);
        ArrayList<Integer> b = new ArrayList<>();
        for (Node n: a) {
            b.add(n.vertex);
        }
        boolean repeat = true;
        while (repeat) {
            repeat = false;
            for (int i =0; i < b.size()-1; i++) {
                if (b.get(i) > b.get(i+1)) {
                    int temp = b.get(i);
                    b.set(i,b.get(i+1));
                    b.set(i+1, temp);
                    repeat = true;
                }
            }
        }
        String vertices = "";
        vertices = "" + b.get(0);
        for (int x = 1; x < b.size(); x++) {
            vertices += " " + b.get(x);
        }
        return vertices;


    }

    /***
     * return the adjacency matrix representation of the graph
     * return a string double dimentional array containing the matrix representation
     * Example:
     * X 1.0 X 1.0 X
     * 1.0 X 1.0 1.0 X
     * X 1.0 X X 1.0
     * 1.0 1.0 X X 1.0
     * X X 1.0 1.0 X
     * X represents the vertex combinations where there are no edges
     */
    public String[][] matrix() {
        //TODO implement matrix
        String[][] matrix = new String[vertexSize()][vertexSize()];
        for (int x = 0; x < matrix.length; x++) {
            String vertices = adjList(x);
            if (!vertices.equals("none")) {
                String[] v = vertices.split(" ");
                int count = 0;
                for (int y = 0; y < matrix.length; y++) {
                    if (count >  y) {
                        matrix[x][y] = "X";
                    } else if (count < v.length && Integer.parseInt(v[count]) == y) {
                        matrix[x][y] = "" + weight(x,y);
                        count++;
                    } else {
                        matrix[x][y] = "X";
                    }
                }
            } else {
                for (int y = 0; y < matrix.length; y++) {
                    matrix[x][y] = "X";
                }
            }
        }
        return matrix;
    }

    /***
     * return the adjacency matrix representation of the transitive closure of the graph
     * Example:
     * 1 1 1 1
     * 1 1 1 1
     * 1 1 1 1
     * X X X 1
     * This will only be tested on directed graphs where edge weights are all one so the
     * matrix should use "1" to signify reachability or "X" otherwise
     */
    public void dfs(int s, int v) {
        end[s][v] = "1";
        for (Node i : adj.get(v)) {
            if (end[s][i.vertex].equals("X")) {
                dfs(s, i.vertex);
            }
        }
    }
    public String[][] tclosure() {
        end = matrix();
        for (int x = 0; x < end.length; x++) {
            for (int y = 0; y < end.length; y++) {
                if (end[x][y].equals("1.0")) {
                    end[x][y] = "1";
                }
            }
        }
        int n = end.length;
        for (int i = 0; i < size; i++) {
            dfs(i, i);
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    end[i][j] = (!end[i][j].equals("X")) || ((!end[i][k].equals("X")) && (!end[k][j].equals("X")))?"1":"X";
                }
            }
        }
        return end;
    }
    /***
     * print the shortest path from vertex u to vertex v
     * use any of the algorithms taught in the class
     * You need to return the shortest path in the following format
     * "4 6 2 0, 5.0"
     * Here 4 6 2 0 are vertices and 5.0 is the maximum edge weight
     * return "path does not exist" if there is no path"
     * You can assume that only one shortest path will exist for each pair of vertices in a given graph.
     * In this section, no negative weight edges will be used in the test cases,
     * @param s
     * @param v
     */
    public String spath (int s, int v) {
        //TODO implement spath
        if (s < 0 || v < 0 || s >= size || v >= size) {
            return "path does not exist";
        }
        double[] dist = new double[size];
        int[] prev = new int[size];
        ArrayList<Integer> pq = new ArrayList();
        for (int x = 0; x < size; x++) {
            dist[x] = 0;
            if (x != s) {
                dist[x] = Integer.MAX_VALUE;
            }
            prev[x] = -1;
            pq.add(x, (int) dist[x]);
        }
        dist[s] = 0;
        int visited = 0;
        while (visited != size){
            int min = Integer.MAX_VALUE;
            int minIndex = 0;
            for (int y = 0; y < pq.size(); y++) {
                if (min > pq.get(y) && pq.get(y) != -1) {
                    min = pq.get(y);
                    minIndex = y;
                }
            }
            pq.set(minIndex, -1);
            visited++;
            int u = minIndex;
            ArrayList<Node> a = adj.get(u);
            for (Node w : a) {
                double d = Math.min(dist[w.vertex], dist[u] + weight(u,w.vertex));
                if (d < dist[w.vertex]) {
                    dist[w.vertex] =  d;
                    prev[w.vertex] = u;
                    pq.set(w.vertex, (int) d);
                    /*if (pq.get(w.vertex) != -1) {
                        pq.set(w.vertex, (int) d);
                    }*/
                }
            }
        }
        double weight = dist[v];
        if (weight == Integer.MAX_VALUE) {
            return "path does not exist";
        }
        int current = v;
        String output = "";
        while (current != -1) {
            output = current + " " + output;
            current = prev[current];
        }
        output = output.substring(0,output.length()-1);
        output += ", " + weight;
        return output;

    }

    /***
     * return any topological sorting of the graph
     * You need to return a string in the following format
     * "9 6 7 4 3 5 2 1 8 0"
     * Where each number is a vertex
     */
    public String tsort(){
        //TODO implement tsort
        ArrayList<ArrayList<Node>> h = adj;
        int n = h.size();
        ArrayList<Integer> tSort = new ArrayList();
        int remove = -1;
        while (n > 0) {
            remove = 0;
            for (int x = 0; x < h.size(); x++) {
                if (h.get(x)!= null && h.get(x).isEmpty()) {
                    remove = x;
                    break;
                }
            }
            for (int y = 0; y < h.size(); y++) {
                if (h.get(y) != null) {
                    for (int z = 0; z < h.get(y).size(); z++) {
                        if (remove == h.get(y).get(z).vertex) {
                            h.get(y).remove(z);
                            break;
                        }
                    }
                }
            }
            tSort.add(0,remove);
            h.set(remove, null);
            n--;
        }
        String output = "";
        for (int a = 0; a < tSort.size(); a++) {
            if (a == 0) {
                output = "" + tSort.get(a);
            } else {
                output += " " + tSort.get(a);
            }
        }
        return output;
    }

    /***
     * return true if the graph is strongly connected
     * otherwise false
     */
    public boolean sconn() {
        //TODO implement sconn
        boolean[] hasInput = new boolean[size];
        for (int x = 0; x < adj.size(); x++) {
            if (adj.get(x).isEmpty()) {
                return false;
            }
        }
        for (int x = 0; x < adj.size(); x++) {
            for (int y = 0; y < adj.get(x).size(); y++) {
                hasInput[adj.get(x).get(y).vertex] = true;
            }
        }
        for (int x = 0; x < hasInput.length; x++) {
            if (hasInput[x] == false) {
                return false;
            }
        }
        return true;
    }

    /***
     * return the connected components of the graph
     * Each connected component is stored in a string separated by spaces
     * Return an array of strings in a sorted ordered
     */
    public void dfs2(int v, boolean[] visited)
    {
        visited[v] = true;
        if (a == null) {
            a = "" + v;
        } else {
            a += " " + v;
        }
        for (Node x : adj.get(v)) {
            if (!visited[x.vertex]) {
                dfs2(x.vertex, visited);
            }
        }
    }
    public String[] components() {
        boolean[] visited = new boolean[size];
        ArrayList<String> o = new ArrayList<>();
        for (int v = 0; v < size; ++v) {
            if (!visited[v]) {
                a = null;
                dfs2(v, visited);
                if (a != null) {
                    o.add(a);
                }
            }
        }

        String[] out = new String[o.size()];
        for (int i = 0; i < o.size(); i++) {
            String sort = o.get(i);
            String[] sort1 = sort.split(" ");
            boolean repeat = true;
            while (repeat) {
                repeat = false;
                for (int y =0; y < sort1.length-1; y++) {
                    if (Integer.parseInt(sort1[y]) > Integer.parseInt(sort1[y+1])) {
                        String temp = sort1[y];
                        sort1[y] = sort1[y+1];
                        sort1[y+1] = temp;
                        repeat = true;
                    }
                }
            }

            for (int c = 0; c < sort1.length; c++ ) {
                if (c == 0) {
                    sort = "" + sort1[c];
                } else {
                    sort += " " + sort1[c];
                }
            }

            out[i] = sort;
        }
        return out;
    }

    /***
     * return true if the graph is a simple graph
     * otherwise false
     * @return
     */
    public boolean dfs3(int u)
    {
        visitedSimple[u] = -1;
        for (Node x : adj.get(u)) {
            if (visitedSimple[x.vertex] == -1) {
                return false;
            }
            else if (visitedSimple[x.vertex] != 1 && !dfs3(x.vertex)) {
                return false;
            }
        }
        visitedSimple[u] = 1;
        return true;
    }
    public boolean dfs4(int u)
    {
        visitedSimple[u] = 1;
        for (Node x : adj.get(u)) {
            if (visitedSimple[x.vertex] != 1 && !dfs3(x.vertex)) {
                return true;
            } else if (x.vertex != u) {
                return true;
            }
        }
        return false;
    }
    public boolean simple() {
        //TODO implement simple
        String[][] mat = matrix();
        for (int y = 0; y < adj.size(); y++) {
            String edges = "";
            for (int z = 0; z < adj.get(y).size(); z++) {
                if (edges.contains("" + adj.get(y).get(z).vertex)) {
                    System.out.println("parallel");
                    return false;
                } else {
                    edges += " " + adj.get(y).get(z).vertex;
                }
            }
        }
        /*for (int y = 0; y < adj.size(); y++) {
            for (int z = 0; z < adj.get(y).size(); z++) {
                if (y == adj.get(y).get(z).vertex) {
                    System.out.println("self loop");
                    return false;
                }
            }
        }*/

        if (isDirected) {
            visitedSimple = new int[size];
            for (int v = 0; v < size; v++) {
                if (visitedSimple[v] != 1) {
                    if (!dfs3(v)) {
                        System.out.println("dir cycle");
                        return false;
                    }
                }
            }
            return true;
        } else {
            visitedSimple = new int[size];
            for (int v = 0; v < size; v++) {
                if (visitedSimple[v] != 1) {
                    if (dfs4(v)) {
                        System.out.println("undir cycle");
                        return false;
                    }
                }
            }
            return true;
        }
    }
}

