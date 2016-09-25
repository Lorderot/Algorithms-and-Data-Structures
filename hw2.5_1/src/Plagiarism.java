import java.io.*;
import java.util.LinkedList;

public class Plagiarism {
    private final int INFINITE = 1 << 30;
    private int source = 1;
    private int sink = 2;
    private int amountOfNodes;
    private int numberOfEdges;
    private boolean isSinkIsolatedVertex = true;
    private int[] first;
    private int[] next;
    private int[] toWhatNodeLeadsEdge;
    private int[] fromWhatNodeLeadsEdge;
    private int[] capacity;
    private int[] last;

    public static void main(String[] args) {
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File("test.txt"))));
                PrintWriter writer = new PrintWriter(System.out)) {
            StreamTokenizer parser = new StreamTokenizer(reader);
            parser.nextToken();
            int numberOfRows = (int) parser.nval;
            parser.nextToken();
            int numberOfColumns = (int) parser.nval;
            String[] sits = new String[numberOfRows];
            reader.readLine();
            for (int i = 0; i < numberOfRows; i++) {
                String line = reader.readLine();
                if (line.length() != numberOfColumns) {
                    throw new IllegalArgumentException();
                }
                sits[i] = line;
            }
            Plagiarism plagiarism = new Plagiarism(sits);
            writer.println(plagiarism.findMaxAmountOfStudents());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Plagiarism(String[] sits) {
        int numberOfRows = sits.length;
        int numberOfColumns = sits[0].length();
        amountOfNodes = 2;
        numberOfEdges = 0;
        int[][] numberOfNodes = new int[numberOfRows][numberOfColumns];
        preCalculation(sits, numberOfNodes);
        first = new int[amountOfNodes + 1];
        next = new int[numberOfEdges + 1];
        last = new int[numberOfEdges + 1];
        toWhatNodeLeadsEdge = new int[numberOfEdges + 1];
        fromWhatNodeLeadsEdge = new int[numberOfEdges + 1];
        capacity = new int[numberOfEdges + 1];
        buildGraph(sits, numberOfNodes);
    }

    public int findMaxAmountOfStudents() {
        if (isSinkIsolatedVertex || first[source] == 0) {
            return amountOfNodes - 2;
        }
        int[] parent = new int[amountOfNodes + 1];
        int maxFlow = 0;
        while (breadthFirstSearch(parent)) {
            int capacityOfBottleneck = INFINITE;
            for (int node = sink; node != source;
                 node = fromWhatNodeLeadsEdge[parent[node]]) {
                int edge =  parent[node];
                capacityOfBottleneck = Math.min(capacityOfBottleneck,
                        capacity[edge]);
            }
            for (int node = sink; node != source;
                 node = fromWhatNodeLeadsEdge[parent[node]]) {
                int edge =  parent[node];
                capacity[edge] -= capacityOfBottleneck;
            }
            maxFlow += capacityOfBottleneck;
        }
        return amountOfNodes - 2 - maxFlow;
    }

    private void preCalculation(String[] sits, int[][] numberOfNodes) {
        int numberOfRows = sits.length;
        int numberOfColumns = sits[0].length();
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (sits[i].charAt(j) == '.') {
                    amountOfNodes++;
                    numberOfNodes[i][j] = amountOfNodes;
                    numberOfEdges++;
                }
                if (j < numberOfColumns - 1) {
                    if (i >= 1
                            && sits[i - 1].charAt(j + 1) == '.') {
                        numberOfEdges += 2;
                    }
                    if (i < numberOfRows - 1
                            && sits[i + 1]. charAt(j + 1) == '.') {
                        numberOfEdges += 2;
                    }
                    if (sits[i].charAt(j + 1) == '.') {
                        numberOfEdges += 2;
                    }
                }
            }
        }
    }

    private void buildGraph(String[] sits, int[][] numberOfNodes) {
        int numberOfRows = sits.length;
        int numberOfColumns = sits[0].length();
        int edge = 1;
        int node = 3;
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (sits[i].charAt(j) == '.') {
                    if (j % 2 == 0) {
                        addEdge(source, node, edge);
                        capacity[edge] = 1;
                        edge++;
                    } else {
                        addEdge(node, sink, edge);
                        isSinkIsolatedVertex = false;
                        capacity[edge] = 1;
                        edge++;
                    }
                    if (j < numberOfColumns - 1) {
                        if (i >= 1
                                && sits[i - 1].charAt(j + 1) == '.') {
                            addEdge(node, numberOfNodes[i - 1][j + 1], edge);
                            capacity[edge] = INFINITE;
                            edge++;
                            addEdge(numberOfNodes[i - 1][j + 1], node, edge);
                            capacity[edge] = INFINITE;
                            edge++;
                        }
                        if (i < numberOfRows - 1
                                && sits[i + 1]. charAt(j + 1) == '.') {
                            addEdge(node, numberOfNodes[i + 1][j + 1], edge);
                            capacity[edge] = INFINITE;
                            edge++;
                            addEdge(numberOfNodes[i + 1][j + 1], node, edge);
                            capacity[edge] = INFINITE;
                            edge++;
                        }
                        if (sits[i].charAt(j + 1) == '.') {
                            addEdge(node, numberOfNodes[i][j + 1], edge);
                            capacity[edge] = INFINITE;
                            edge++;
                            addEdge(numberOfNodes[i][j + 1], node, edge);
                            capacity[edge] = INFINITE;
                            edge++;
                        }
                    }
                    node++;
                }
            }
        }
    }

    private void addEdge(int from, int to, int edge) {
        int lastEdge = last[from];
        if (lastEdge == 0) {
            first[from] = edge;
        }
        last[from] = edge;
        next[lastEdge] = edge;
        fromWhatNodeLeadsEdge[edge] = from;
        toWhatNodeLeadsEdge[edge] = to;
    }

    private boolean breadthFirstSearch(int[] parent) {
        boolean[] visited = new boolean[amountOfNodes + 1];
        LinkedList<Integer> queue = new LinkedList<>();
        queue.add(source);
        visited[source] = true;
        while (!queue.isEmpty() && !visited[sink]) {
            int node = queue.poll();
            visited[node] = true;
            int edge = first[node];
            while (edge != 0 && !visited[sink]) {
                int nextNode = toWhatNodeLeadsEdge[edge];
                if (!visited[nextNode] && capacity[edge] > 0) {
                    visited[nextNode] = true;
                    queue.add(nextNode);
                    parent[nextNode] = edge;
                }
                edge = next[edge];
            }
        }
        return visited[sink];
    }
}
