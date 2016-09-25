import java.io.*;

/**
 * @author Mykola Holovatsky
 */
public class MediansInTree {
    public final int NumberOfNodes;
    private int[][] parents;
    private int[] firstEdgeFromNode;
    private int[] nextEdge;
    private int[] firstAppearanceInOrder;
    private int[] order;
    private SegmentTree segmentTree;
    private int[] heightOfNodes;

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in))) {
            StreamTokenizer parser = new StreamTokenizer(reader);
            parser.nextToken();
            int numberOfNodesInTree = (int) parser.nval;
            int[] treeEdges = new int[numberOfNodesInTree - 1];
            for (int i = 0; i < numberOfNodesInTree - 1; i++) {
                parser.nextToken();
                treeEdges[i] = (int) parser.nval;
            }
            parser.nextToken();
            int numberOfTests = (int) parser.nval;
            int[][] tests = new int[2][numberOfTests];
            for (int i = 0; i < numberOfTests; i++) {
                for (int j = 0; j < 2; j++) {
                    parser.nextToken();
                    tests[j][i] = (int) parser.nval;
                }
            }
            MediansInTree tree =  new MediansInTree(treeEdges);
            TestMedianQuery testing = new TestMedianQuery(tree, numberOfTests, tests);
            long[] results = testing.calculateTheSumOfMedians();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
            for (int i = 0; i < results.length; i++) {
                out.print(results[i]);
                out.println();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MediansInTree(int[] treeEdges) {
        NumberOfNodes = treeEdges.length + 1;
        this.firstAppearanceInOrder = new int[NumberOfNodes];
        this.heightOfNodes = new int[NumberOfNodes];

        /*pre-processing*/
        firstEdgeFromNode = new int[NumberOfNodes];
        nextEdge = new int[NumberOfNodes];
        int[] lastEdgeFromNode = new int[NumberOfNodes];
        /*The first edge leads to the first Node (root is zero Node) and so on.*/
        for (int i = 0; i < treeEdges.length; i++) {
            int  node = treeEdges[i] - 1;
            if (firstEdgeFromNode[node] == 0) {
                firstEdgeFromNode[node] = i + 1;
            } else {
                nextEdge[lastEdgeFromNode[node]] = i + 1;
            }
            lastEdgeFromNode[node] = i + 1;
        }

        int[] path = new int[NumberOfNodes * 2 - 1];
        order = new int[NumberOfNodes * 2 - 1];
        depthSearch(order, 0, 0, 0);
        for (int i = 0; i < order.length; i++) {
            int currentNode = order[i];
            if (firstAppearanceInOrder[currentNode] == 0) {
                firstAppearanceInOrder[currentNode] = i;
            }
        }

        for (int i = 0; i < order.length; i++) {
            path[i] = heightOfNodes[order[i]];
        }

        segmentTree = new SegmentTree(path);

        int depth = (int) (Math.log(NumberOfNodes) / Math.log(2));
        parents = new int[depth][NumberOfNodes];
        for (int j = 1; j < NumberOfNodes; j++) {
            parents[0][j] = treeEdges[j - 1] - 1;
        }
        for (int i = 1; i < depth; i++) {
            for (int j = 1; j < NumberOfNodes; j++) {
                parents[i][j] = parents[i - 1][parents[i - 1][j]];
            }
        }
    }

    public int findMedian(int node1, int node2) {
        int leftEdge = firstAppearanceInOrder[node1 - 1];
        int rightEdge = firstAppearanceInOrder[node2 - 1];
        if (leftEdge > rightEdge) {
            int temporary = leftEdge;
            leftEdge = rightEdge;
            rightEdge = temporary;
        }
        int lca = leastCommonAncestor(leftEdge, rightEdge);
        int leftPath = heightOfNodes[node1 - 1] - heightOfNodes[lca];
        int rightPath = heightOfNodes[node2 - 1] - heightOfNodes[lca];
        int median;
        int stepToMedian;
        if (leftPath >= rightPath) {
            median = node1 - 1;
            stepToMedian = (leftPath  + rightPath) / 2;
        } else {
            median = node2 - 1;
            stepToMedian = leftPath + rightPath - (leftPath  + rightPath) / 2;
        }

        while (stepToMedian != 0) {
            int jump = (int) (Math.log(stepToMedian) / Math.log(2));
            stepToMedian -= 1 << jump;
            median = parents[jump][median];
        }

        return median + 1;
    }

   private int leastCommonAncestor(int left, int right) {
       return order[segmentTree.rangeMinQueryIndex(left, right)];
    }

    private int depthSearch(int[] trace, int node, int step, int height) {
        this.heightOfNodes[node] = height;
        trace[step++] = node;
        int j = firstEdgeFromNode[node];
        while (j != 0) {
            step = depthSearch(trace, j, step, height + 1);
            trace[step++] = node;
            j = nextEdge[j];
        }
        return step;
    }
}

class TestMedianQuery {
    private MediansInTree tree;
    private int numberOfTests;
    private int[][] tests;



    public TestMedianQuery(MediansInTree tree, int numberOfTests, int[][] tests) {
        this.tree = tree;
        this.numberOfTests = numberOfTests;
        this.tests = tests;
    }

    public long[] calculateTheSumOfMedians() {
        long[] result = new long[numberOfTests];
        for (int i = 0; i < numberOfTests; i++) {
            int median = tests[0][i];
            for (int j = 0; j < tests[1][i]; j++) {
                median = tree.findMedian(median, 1 + j % tree.NumberOfNodes);
                result[i] += median;
            }
        }
        return result;
    }
}

class SegmentTree {
    private int[] segmentTree;
    private int[] indexesOfMin;

    public SegmentTree(int[] array) {
        segmentTree = new int[4 * array.length];
        indexesOfMin = new int[4 * array.length];
        buildSegmentTree(array, 1, 0, array.length - 1);
    }

    private void buildSegmentTree(int[] array, int position, int leftEdge, int rightEdge) {
        if (leftEdge == rightEdge) {
            segmentTree[position] = array[leftEdge];
            indexesOfMin[position] = leftEdge;
        } else {
            int median = (leftEdge + rightEdge) / 2;
            buildSegmentTree(array, position * 2, leftEdge, median);
            buildSegmentTree(array, position * 2 + 1, median + 1, rightEdge);
            int leftChild = segmentTree[position * 2];
            int rightChild = segmentTree[position * 2 + 1];
            if (leftChild < rightChild) {
                segmentTree[position] = leftChild;
                indexesOfMin[position] = indexesOfMin[position * 2];
            } else {
                segmentTree[position] = rightChild;
                indexesOfMin[position] = indexesOfMin[position * 2 + 1];
            }
        }
    }

    public int rangeMinQueryValue(int queryLeftEdge, int queryRightEdge) {
        return segmentTree[rangeMinQueryPosition(1, 0,
                segmentTree.length / 4 - 1, queryLeftEdge, queryRightEdge)];
    }

    public int rangeMinQueryIndex(int queryLeftEdge, int queryRightEdge) {
        return indexesOfMin[rangeMinQueryPosition(1, 0,
                segmentTree.length / 4 - 1, queryLeftEdge, queryRightEdge)];
    }

    private int rangeMinQueryPosition(int position, int segmentLeftEdge,
                                      int segmentRightEdge, int queryLeftEdge,
                                      int queryRightEdge) {
        if (queryLeftEdge == segmentLeftEdge
                && queryRightEdge == segmentRightEdge) {return position;
        }
        int median = (segmentLeftEdge + segmentRightEdge) / 2;
        if (median < queryRightEdge) {
            if (median < queryLeftEdge) {
                return rangeMinQueryPosition(position * 2 + 1, median + 1,
                        segmentRightEdge, queryLeftEdge,
                        queryRightEdge);
            }
            int leftQuery = rangeMinQueryPosition(position * 2, segmentLeftEdge,
                    median, queryLeftEdge, median);
            int rightQuery = rangeMinQueryPosition(position * 2 + 1,
                    median + 1, segmentRightEdge, median + 1, queryRightEdge);
            if (segmentTree[leftQuery] < segmentTree[rightQuery]) {
                return leftQuery;
            }
            return rightQuery;
        }
        return  rangeMinQueryPosition(position * 2, segmentLeftEdge, median,
            queryLeftEdge, queryRightEdge);

    }
}