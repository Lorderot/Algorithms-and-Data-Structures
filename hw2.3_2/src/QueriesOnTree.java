import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
*
* @author Mykola Holovatsky
*
* */

public class QueriesOnTree {
    private int numberOfRequests;
    private int lastChainLabel;
    private int numberOfNodes;
    private int[] depth;
    private int[] indexesOfNodesInChain;
    private int[] upFromChainToNext;
    private int[] chainLabelOfNode;
    private int[] childWithMaxSize;
    private int[] nodeValues;
    private int[] firstEdgeFromNode;
    private int[] nextEdge;
    private int[] toWhatNodeLeadsEdge;
    private Map<Integer, Container>[] hashMapOfChain;
    private int[][] requests;

    public QueriesOnTree(int[] nodeValues, int[][] requests, int[][] edges) {
        this.requests = requests;
        numberOfRequests = requests[0].length;
        numberOfNodes = nodeValues.length - 1;
        int numberOfEdges = numberOfNodes - 1;
        this.nodeValues = nodeValues;
        depth = new int[numberOfNodes + 1];
        upFromChainToNext = new int[numberOfNodes];
        indexesOfNodesInChain = new int[numberOfNodes + 1];
        chainLabelOfNode = new int[numberOfNodes + 1];
        childWithMaxSize = new int[numberOfNodes + 1];
        firstEdgeFromNode = new int[numberOfNodes + 1];
        nextEdge = new int[2 * numberOfEdges + 1];
        toWhatNodeLeadsEdge =new int[2 * numberOfEdges + 1];
        depth[0] = -1;
        initializeTree(edges);
        countSizes(edges[0][0], 0, 0);
        heavyLightDecomposition(edges[0][0], 0, 0, 0);
        hashMapOfChain = new HashMap[lastChainLabel + 1];
        initiateHashMap(edges[0][0], 0);
        initiateContainers(edges[0][0], 0);
    }

    public static void main(String[] args) {
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in))) {
            StreamTokenizer parser = new StreamTokenizer(reader);
            parser.nextToken();
            int numberOfNodes = (int)parser.nval;
            int[] nodeValues = new int[numberOfNodes + 1];
            for (int i = 1; i < numberOfNodes + 1; i++) {
                parser.nextToken();
                nodeValues[i] = (int)parser.nval;
            }
            int[][] edges = new int[2][numberOfNodes - 1];
            for (int i = 0; i < numberOfNodes - 1; i++) {
                parser.nextToken();
                edges[0][i] = (int)parser.nval;
                parser.nextToken();
                edges[1][i] = (int)parser.nval;
            }
            parser.nextToken();
            int numberOfRequests = (int)parser.nval;
            int[][] requests = new int[3][numberOfRequests];
            for (int i = 0; i < numberOfRequests; i++) {
                for (int j = 0; j < 3; j++) {
                    parser.nextToken();
                    requests[j][i] = (int)parser.nval;
                }
            }
            QueriesOnTree queriesOnTree = new QueriesOnTree(nodeValues, requests, edges);
            int[] respond = queriesOnTree.getRespond();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
            for (int element : respond) {
                out.println(element);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getRespond() {
        int[] respond = new int[numberOfRequests];
        for (int i = 0; i < numberOfRequests; i++) {
            if (isNodeOnShortestPath(requests[0][i],
                    requests[1][i], requests[2][i])) {
                respond[i] = 1;
            } else {
                respond[i] = 0;
            }
        }
        return respond;
    }

    private boolean isNodeOnShortestPath(int fromNode, int toNode, int value) {
        int lca = leastCommonAncestor(fromNode, toNode);
        return isNodeOnPathToAncestor(fromNode, lca, value)
                || isNodeOnPathToAncestor(toNode, lca, value);
    }

    private boolean isNodeOnPathToAncestor(int node, int ancestor, int value) {
        int nodeChainLabel = chainLabelOfNode[node];
        int ancestorChainLabel = chainLabelOfNode[ancestor];
        while(nodeChainLabel != ancestorChainLabel) {
            if (isNodeInChainOnRange(0, indexesOfNodesInChain[node],
                    value, nodeChainLabel)) {
                return true;
            }
            node = upFromChainToNext[nodeChainLabel];
            nodeChainLabel = chainLabelOfNode[node];
        }
        return isNodeInChainOnRange(indexesOfNodesInChain[ancestor],
                indexesOfNodesInChain[node], value, ancestorChainLabel);
    }

    private boolean isNodeInChainOnRange(int firstIndex, int secondIndex,
                                         int value, int chain) {
        Container container = hashMapOfChain[chain].get(value);
        return container != null
                && container.hasAnyValueOfRange(firstIndex, secondIndex);
    }

    private int leastCommonAncestor(int firstNode, int secondNode) {
        int firstNodeChain = chainLabelOfNode[firstNode];
        int secondNodeChain = chainLabelOfNode[secondNode];
        while (firstNodeChain !=  secondNodeChain) {
            int nextNode1 = upFromChainToNext[firstNodeChain];
            int nextNode2 = upFromChainToNext[secondNodeChain];
            if (depth[nextNode1] > depth[nextNode2]) {
                firstNode = nextNode1;
                firstNodeChain = chainLabelOfNode[firstNode];
            } else {
                secondNode = nextNode2;
                secondNodeChain = chainLabelOfNode[secondNode];
            }
        }
        if (depth[firstNode] < depth[secondNode]) {
            return firstNode;
        } else {
            return secondNode;
        }
    }

    private int countSizes(int node, int previousNode, int height) {
        depth[node] = height;
        int edge = firstEdgeFromNode[node];
        int nextNode = toWhatNodeLeadsEdge[edge];
        int size = 1;
        int childMaxSize = 0;
        while (nextNode != 0) {
            if (nextNode == previousNode) {
                edge = nextEdge[edge];
                nextNode = toWhatNodeLeadsEdge[edge];
                continue;
            }
            int childSize = countSizes(nextNode, node, height + 1);
            if (childSize > childMaxSize) {
                childMaxSize = childSize;
                childWithMaxSize[node] = nextNode;
            }
            size += childSize;
            edge = nextEdge[edge];
            nextNode = toWhatNodeLeadsEdge[edge];
        }
        return size;
    }

    private void heavyLightDecomposition(int node, int previousNode, int chainLabel, int index) {
        chainLabelOfNode[node] = chainLabel;
        indexesOfNodesInChain[node] = index;
        int edge = firstEdgeFromNode[node];
        int nextNode = toWhatNodeLeadsEdge[edge];
        while (nextNode != 0) {
            if (nextNode == previousNode) {
                edge = nextEdge[edge];
                nextNode = toWhatNodeLeadsEdge[edge];
                continue;
            }
            if (nextNode == childWithMaxSize[node]) {
                heavyLightDecomposition(nextNode, node, chainLabel, index + 1);
            } else {
                lastChainLabel++;
                upFromChainToNext[lastChainLabel] = node;
                heavyLightDecomposition(nextNode, node, lastChainLabel, 0);
            }
            edge = nextEdge[edge];
            nextNode = toWhatNodeLeadsEdge[edge];
        }
    }

    private void initiateHashMap(int node, int previousNode) {
        int chainLabel = chainLabelOfNode[node];
        if (hashMapOfChain[chainLabel] == null) {
            hashMapOfChain[chainLabel] = new HashMap<>();;
        }
        Map<Integer, Container> hashMap = hashMapOfChain[chainLabel];
        int value = nodeValues[node];
        Container indexesOfNodesInChainWithCertainValue = hashMap.get(value);
        if (indexesOfNodesInChainWithCertainValue == null) {
            Container container = new Container(1);
            hashMap.put(value, container);
        } else {
            indexesOfNodesInChainWithCertainValue.incrementInitialSize();
        }
        int edge = firstEdgeFromNode[node];
        int nextNode = toWhatNodeLeadsEdge[edge];
        while (nextNode != 0) {
            if (nextNode == previousNode) {
                edge = nextEdge[edge];
                nextNode = toWhatNodeLeadsEdge[edge];
                continue;
            }
            initiateHashMap(nextNode, node);
            edge = nextEdge[edge];
            nextNode = toWhatNodeLeadsEdge[edge];
        }
    }

    private void initiateContainers(int node, int previousNode) {
        Container container = hashMapOfChain[chainLabelOfNode[node]]
                .get(nodeValues[node]);
        if (!container.isInitialized()) {
            container.initializeArray();
        }
        container.addValue(indexesOfNodesInChain[node]);
        int edge = firstEdgeFromNode[node];
        int nextNode = toWhatNodeLeadsEdge[edge];
        while (nextNode != 0) {
            if (nextNode == previousNode) {
                edge = nextEdge[edge];
                nextNode = toWhatNodeLeadsEdge[edge];
                continue;
            }
            initiateContainers(nextNode, node);
            edge = nextEdge[edge];
            nextNode = toWhatNodeLeadsEdge[edge];
        }
    }

    private void initializeTree(int[][] edges) {
        int[] lastEdgeInNode = new int[numberOfNodes + 1];
        int numberOfEdges = numberOfNodes - 1;
        for (int i = 0; i < numberOfEdges; i++) {
            for (int j = 0; j < 2; j++) {
                int node = edges[j][i];
                int edge = 2 * i + j + 1;
                if (firstEdgeFromNode[node] == 0) {
                    firstEdgeFromNode[node] = edge;
                } else {
                    nextEdge[lastEdgeInNode[node]] = edge;
                }
                toWhatNodeLeadsEdge[edge] = edges[j ^ 1][i];
                lastEdgeInNode[node] = edge;
            }
        }
    }
}

class Container {
    private int initialSize;
    private int[] values;

    public Container(int initialSize) {
        this.initialSize = initialSize;
    }

    public void initializeArray() {
        values = new int[initialSize];
        initialSize = 0;
    }

    public void incrementInitialSize() {
        if (values == null) {
            initialSize++;
        }
    }

    public boolean isInitialized() {
        return values != null;
    }

    public void addValue(int element) {
        if (initialSize >= values.length) {
            int newLength = 2 << ((int) Math.log(values.length) + 1);
            Arrays.copyOf(values, newLength);
        }
        values[initialSize++] = element;
    }

    public boolean hasAnyValueOfRange(int value1, int value2) {
        int index1 = Arrays.binarySearch(values, value1);
        int index2 = Arrays.binarySearch(values, value2);
        return index1 >= 0 || index2 >= 0 || index1 != index2;
    }
}