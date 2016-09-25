import java.io.*;
import java.util.Arrays;
import java.util.Collections;

public class TreeRequests {
    private final int primaryNumber = (int) Math.pow(10, 9) + 7;
    private final int base = 3;
    private int numberOfNodes;
    private int numberOfRequests;
    private int[] firstEdgeFromNode;
    private int[] nextEdge;
    private int[] toWhatNodeLeadsEdge;
    private Request[] requests;
    private SegmentTree subTrees;
    private int[] endOfSubTreeInTrace;
    private int[] nodeIndexInTrace;
    private int[] powersOfBaseModPrimaryNumber;

    public TreeRequests(int[][] treeConnections, int root,
                        int[][] requests) {
        numberOfRequests = requests[0].length;
        numberOfNodes = treeConnections[0].length + 1;
        int numberOfEdges = treeConnections[0].length;
        nextEdge = new int[2 * numberOfEdges + 1];
        toWhatNodeLeadsEdge = new int[2 * numberOfEdges + 1];
        int[] lastEdgeInNode = new int[numberOfNodes + 1];
        firstEdgeFromNode = new int[numberOfNodes + 1];

        for (int i = 0; i < numberOfEdges; i++) {
            for (int j = 0; j < 2; j++) {
                int node = treeConnections[j][i];
                int edge = 2 * i + j + 1;
                if (firstEdgeFromNode[node] == 0) {
                    firstEdgeFromNode[node] = edge;
                } else {
                    nextEdge[lastEdgeInNode[node]] = edge;
                }
                toWhatNodeLeadsEdge[edge] = treeConnections[(j + 1) % 2][i];
                lastEdgeInNode[node] = edge;
            }
        }

        this.requests = new Request[numberOfRequests];
        for (int i = 0; i < requests[0].length; i++) {
            this.requests[i] = new Request(requests[0][i], requests[1][i], i);
        }

        int[] treeTrace = new int[numberOfNodes];
        subTrees = new SegmentTree(treeTrace);
        nodeIndexInTrace = new int[numberOfNodes + 1];
        endOfSubTreeInTrace = new int[numberOfNodes + 1];
        deepSearch(0, root, 0);
    }

    public static void main(String[] args) {
        StreamTokenizer parser = new StreamTokenizer(
                new BufferedReader(new InputStreamReader(System.in)));
        try {
            parser.nextToken();
            int numberOfNodes = (int) parser.nval;
            parser.nextToken();
            int root = (int) parser.nval;
            int[][] treeConnections = new int[2][numberOfNodes - 1];
            for (int i = 0; i < numberOfNodes - 1; i++) {
                parser.nextToken();
                treeConnections[0][i] = (int) parser.nval;
                parser.nextToken();
                treeConnections[1][i] = (int) parser.nval;
            }
            parser.nextToken();
            int numberOfRequests = (int) parser.nval;
            int[][] requests = new int[2][numberOfRequests];
            for (int i = 0; i < numberOfRequests; i++) {
                parser.nextToken();
                requests[0][i] = (int) parser.nval;
                parser.nextToken();
                requests[1][i] = (int) parser.nval;
            }
            TreeRequests treeRequests = new TreeRequests(treeConnections,
                    root, requests);
            int response = treeRequests.processTheRequests();
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(System.out));
            out.print(response);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int processTheRequests() {
        Arrays.sort(requests, Collections.reverseOrder());
        generatePowersOfBaseModPrimaryNumber();
        int previousLowerBound = numberOfNodes;
        int result = 0;
        for (int i = 0; i < numberOfRequests; i++) {
            Request currentRequest = requests[i];
            int lowerBound = currentRequest.getLowerBound();
            int rootOfSubTree = currentRequest.getNode();
            for (int j = lowerBound + 1; j <= previousLowerBound; j++) {
                subTrees.upgrade(nodeIndexInTrace[j], 1);
            }
            previousLowerBound = lowerBound;
            int requestResponse = subTrees.rangeSumQuery(
                    nodeIndexInTrace[rootOfSubTree],
                    endOfSubTreeInTrace[rootOfSubTree]);
            int numberOfRequest = currentRequest.getRequestID();
            result += (int)
                    (((long) powersOfBaseModPrimaryNumber[numberOfRequest]
                    * requestResponse) % primaryNumber);
            result %= primaryNumber;
        }
        return result;
    }

    private void generatePowersOfBaseModPrimaryNumber() {
        powersOfBaseModPrimaryNumber = new int[numberOfRequests];
        powersOfBaseModPrimaryNumber[0] = 1;
        for (int i = 1; i < numberOfRequests; i++) {
            powersOfBaseModPrimaryNumber[i] = (int) ((long) base
                    * powersOfBaseModPrimaryNumber[i - 1]
                    % primaryNumber);
        }
    }

    private int deepSearch(int step, int node, int previousNode) {
        nodeIndexInTrace[node] = step++;
        int edge = firstEdgeFromNode[node];
        int nextNode = toWhatNodeLeadsEdge[edge];
        while (nextNode != 0) {
            if (nextNode == previousNode) {
                edge = nextEdge[edge];
                nextNode = toWhatNodeLeadsEdge[edge];
                continue;
            }
            step = deepSearch(step, nextNode, node);
            edge = nextEdge[edge];
            nextNode = toWhatNodeLeadsEdge[edge];
        }
        endOfSubTreeInTrace[node] = step - 1;
        return step;
    }
}

class SegmentTree {
    private int[] segmentTree;

    public SegmentTree(int[] array) {
        segmentTree = new int[4 * array.length];
        buildSegmentTree(array, 1, 0, array.length - 1);
    }

    public void upgrade(int indexOfElement, int value) {
        upgrade(1, 0, segmentTree.length / 4 - 1, indexOfElement, value);
    }

    public int rangeSumQuery(int queryLeftEdge, int queryRightEdge) {
        return rangeSumQuery(1, 0, segmentTree.length / 4 - 1,
                queryLeftEdge, queryRightEdge);
    }

    private void buildSegmentTree(int[] array, int position, int leftEdge, int rightEdge) {
        if (leftEdge == rightEdge) {
            segmentTree[position] = array[leftEdge];
        } else {
            int median = (leftEdge + rightEdge) / 2;
            buildSegmentTree(array, position * 2, leftEdge, median);
            buildSegmentTree(array, position * 2 + 1, median + 1, rightEdge);
            segmentTree[position] = segmentTree[position * 2]
                    + segmentTree[position * 2 + 1];
        }
    }

    private int rangeSumQuery(int position, int segmentLeftEdge,
                              int segmentRightEdge, int queryLeftEdge,
                              int queryRightEdge) {
        if (queryRightEdge < queryLeftEdge) {
            return 0;
        }
        if (queryLeftEdge == segmentLeftEdge
                && queryRightEdge == segmentRightEdge) {
            return segmentTree[position];
        }
        int median = (segmentLeftEdge + segmentRightEdge) / 2;
        int leftQuery_RightQueryEdge =
                (queryRightEdge < median) ? queryRightEdge : median;
        int rightQuery_LeftQueryEdge =
                (queryLeftEdge > median + 1) ? queryLeftEdge : median + 1;
        int leftQuery = rangeSumQuery(position * 2, segmentLeftEdge,
                median, queryLeftEdge, leftQuery_RightQueryEdge);
        int rightQuery = rangeSumQuery(position * 2 + 1,
                median + 1, segmentRightEdge, rightQuery_LeftQueryEdge,
                queryRightEdge);
        return leftQuery + rightQuery;
    }

    private void upgrade(int position, int segmentLeftEdge,
                        int segmentRightEdge, int indexOfElement, int value) {
        if (segmentLeftEdge == segmentRightEdge) {
            segmentTree[position] = value;
            return;
        }
        int median = (segmentRightEdge + segmentLeftEdge) / 2;
        if (indexOfElement <= median) {
            upgrade(position * 2, segmentLeftEdge,
                    median, indexOfElement, value);
        } else {
            upgrade(position * 2 + 1, median + 1,
                    segmentRightEdge, indexOfElement, value);
        }
        segmentTree[position] = segmentTree[position * 2]
                + segmentTree[position * 2 + 1];
    }
}

final class Request implements Comparable{
    private int node;
    private int lowerBound;
    private int requestID;

    public Request(int node, int number, int requestID) {
        this.node = node;
        this.lowerBound = number;
        this.requestID = requestID;
    }

    @Override
    public int compareTo(Object object) throws IllegalArgumentException {
        if (!(object.getClass().equals(Request.class))) {
            throw new IllegalArgumentException();
        }
        Request request = (Request) object;
        return this.lowerBound - request.lowerBound;
    }

    public int getNode() {
        return node;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getRequestID() {
        return requestID;
    }
}