import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {

        Graph graph = new Graph();
        try {
            File input = new File(args[0]);
            File output = new File(args[1]);
            PrintWriter out = new PrintWriter(output);
            BufferedReader br = new BufferedReader(new FileReader(input));

            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("ugraph")) {
                    String[] arr = line.split(" ");
                    int n = Integer.parseInt(arr[1]);

                    graph.ugraph(n);

                }
                if (line.contains("dgraph")) {
                    String[] arr = line.split(" ");
                    int n = Integer.parseInt(arr[1]);

                    graph.dgraph(n);

                }
                if (line.contains("add")) {
                    String[] arr = line.split(" ");
                    int u = Integer.parseInt(arr[1]);
                    int v = Integer.parseInt(arr[2]);
                    double w = Double.parseDouble(arr[3]);
                    boolean check = graph.addEdge(u, v, w);
                    if (!check) {
                        out.println("vertex does not exist");
                    }
                }
                if (line.equals("v")) {
                    out.println(graph.vertexSize());
                }
                if (line.equals("e")) {
                    out.println(graph.edgeSize());
                }
                if (line.contains("adj")) {
                    String[] arr = line.split(" ");
                    int v = Integer.parseInt(arr[1]);

                    out.println(graph.adjList(v));

                }
                if (line.contains("weight")) {
                    String[] arr = line.split(" ");
                    int u = Integer.parseInt(arr[1]);
                    int v = Integer.parseInt(arr[2]);
                    double weight = graph.weight(u, v);
                    if (weight == -1.0) {
                        out.println("edge does not exist");
                    } else {
                        out.println(weight);
                    }
                }
                if (line.equals("matrix")) {
                    String[][] matrix = graph.matrix();
                    for (int i = 0; i < matrix.length; i++) {
                        for (int j = 0; j < matrix.length; j++) {
                            out.print(matrix[i][j] + " ");
                        }
                        out.println();
                    }
                }
                if (line.equals("tclosure")) {
                    String[][] matrix = graph.tclosure();
                    for (int i = 0; i < matrix.length; i++) {
                        for (int j = 0; j < matrix.length; j++) {
                            out.print(matrix[i][j] + " ");
                        }
                        out.println();
                    }
                }
                if (line.contains("spath")) {
                    String[] arr = line.split(" ");
                    int u = Integer.parseInt(arr[1]);
                    int v = Integer.parseInt(arr[2]);
                    out.println(graph.spath(u, v));
                }
                if (line.equals("tsort")) {
                    out.println(graph.tsort());
                }
//                if (line.equals("mst")) {
//                    out.println(graph.mst());
//                }
                if (line.equals("sconn")) {
                    out.println(graph.sconn());
                }
                if (line.equals("components")) {
                    String[] components = graph.components();
                    for (int i = 0; i < components.length; i++) {
                        out.println(components[i]);
                    }
                }
                if (line.equals("simple")) {
                    out.println(graph.simple());
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}

