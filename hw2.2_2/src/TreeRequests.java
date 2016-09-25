import java.io.*;
import java.util.Random;

public class TreeRequests {
    public static final int numberOfParametersInRequest = 3;
    private int[] sequence;
    private int[][] requests;
    private RandomTree subSequenceOfEvenIndexes;
    private RandomTree subSequenceOfOddIndexes;

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in))) {
            StreamTokenizer parser = new StreamTokenizer(reader);
            parser.nextToken();
            int length = (int) parser.nval;
            int[] sequence = new int[length];
            for (int i = 0; i < length; i++) {
                parser.nextToken();
                sequence[i] = (int) parser.nval;
            }
            parser.nextToken();
            int numberOfRequests = (int) parser.nval;
            int[][] requests = new int[numberOfParametersInRequest]
                    [numberOfRequests];
            for (int i = 0; i < numberOfRequests; i++) {
                for (int j = 0; j < numberOfParametersInRequest; j++) {
                    parser.nextToken();
                    requests[j][i] = (int) parser.nval;
                }
            }
            TreeRequests treeRequests = new TreeRequests(sequence, requests);
            long[] response = treeRequests.getRespond();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
            for (int i = 0; i < response.length; i++) {
                out.write(Long.toString(response[i]));
                out.write('\n');
            }
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public TreeRequests(int[] sequence, int[][] requests) {
        this.sequence = sequence;
        this.requests = requests;
        for (int i = 0; i < sequence.length; i++) {
            if (i % 2 == 0) {
                subSequenceOfEvenIndexes = RandomTree.
                        merge(subSequenceOfEvenIndexes,
                                new RandomTree(sequence[i]));
            } else {
                subSequenceOfOddIndexes = RandomTree.
                        merge(subSequenceOfOddIndexes,
                                new RandomTree(sequence[i]));
            }
        }
    }

    public void set(int index, int value) {
        if (index % 2 == 0) {
            subSequenceOfEvenIndexes.setValue(index / 2 + 1, value);
        } else {
            subSequenceOfOddIndexes.setValue(index / 2 + 1, value);
        }
    }

    public long sum(int leftEdge, int rightEdge) {
        int left2 = leftEdge / 2 + 1;
        int left1 = left2;

        int segmentLength = rightEdge - leftEdge;
        int lengthForEvenIndexes = segmentLength / 2;
        int lengthForOddIndexes = lengthForEvenIndexes;
        if (leftEdge % 2 != 0) {
            left1++;
            if (segmentLength % 2 != 0) {
                lengthForOddIndexes++;
            }
        } else {
            if (segmentLength % 2 != 0) {
                lengthForEvenIndexes++;
            }
        }
        long sum = 0;
        if (lengthForEvenIndexes != 0) {
            sum += subSequenceOfEvenIndexes.getSum(left1,
                    left1 + lengthForEvenIndexes - 1);
        }

        if (lengthForOddIndexes != 0) {
            sum += subSequenceOfOddIndexes.getSum(left2,
                    left2 + lengthForOddIndexes - 1);
        }
        return sum;
    }

    public void swap(int leftEdge, int rightEdge) {
        int segmentLength = rightEdge - leftEdge;
        if (segmentLength <= 0 || segmentLength % 2 != 0) {
            throw new IllegalArgumentException();
        }
        int left2 = leftEdge / 2 + 1;
        int left1 = left2;
        if (leftEdge % 2 != 0) {
            left1++;
        }
        int length = segmentLength / 2;
        subSequenceOfEvenIndexes.split(left1 - 1);
        RandomTree even_LeftTree = RandomTree.resultLeftTree;
        RandomTree.resultRightTree.split(length);
        RandomTree even_CutTree = RandomTree.resultLeftTree;
        RandomTree even_RightTree = RandomTree.resultRightTree;
        subSequenceOfOddIndexes.split(left2 - 1);
        RandomTree odd_LeftTree = RandomTree.resultLeftTree;
        RandomTree.resultRightTree.split(length);
        RandomTree odd_CutTree = RandomTree.resultLeftTree;
        RandomTree odd_RightTree = RandomTree.resultRightTree;
        subSequenceOfEvenIndexes = RandomTree.merge(
                RandomTree.merge(even_LeftTree, odd_CutTree), even_RightTree);
        subSequenceOfOddIndexes = RandomTree.merge(
                RandomTree.merge(odd_LeftTree, even_CutTree), odd_RightTree);
    }

    public long[] getRespond() {
        int numberOfRequests = requests[0].length;
        int count = 0;
        for (int i = 0; i < numberOfRequests; i++) {
            if (requests[0][i] == 2) {
                count++;
            }
        }
        long[] response = new long[count];
        count = 0;
        for (int i = 0; i < numberOfRequests; i++) {
            switch (requests[0][i]) {
                case 1: {
                    set(requests[1][i], requests[2][i]);
                    break;
                }
                case 2: {
                    response[count++] = sum(requests[1][i], requests[2][i]);
                    break;
                }
                case 3: {
                    swap(requests[1][i], requests[2][i]);
                    break;
                }
            }
        }
        return response;
    }
}

class RandomTree {
    private long sum;
    private int value;
    private int size;
    private int priority;
    public static RandomTree resultLeftTree;
    public static RandomTree resultRightTree;
    private static Random generator = new Random(System.nanoTime());

    private RandomTree left;
    private RandomTree right;

    public RandomTree(int value) {
        this.value = value;
        this.priority = generator.nextInt(Integer.MAX_VALUE);
        this.size = 1;
        this.sum = value;
    }

    public RandomTree(int value, RandomTree left, RandomTree right) {
        this(value);
        this.left = left;
        this.right = right;
        update();
    }

    public static RandomTree buildTree(int[] sequence) {
        RandomTree root = null;
        for (int i = 0; i < sequence.length; i++) {
            root = merge(root, new RandomTree(sequence[i]));
        }
        return root;
    }

    public static RandomTree merge(RandomTree leftTree, RandomTree rightTree) {
        if (leftTree == null) {
            return rightTree;
        }
        if (rightTree == null) {
            return leftTree;
        }

        if (leftTree.getPriority() > rightTree.getPriority()) {
            RandomTree rightChild = merge(leftTree.right, rightTree);
            leftTree.setRight(rightChild);
            leftTree.update();
            return leftTree;
        } else {
            RandomTree leftChild = merge(leftTree, rightTree.left);
            rightTree.setLeft(leftChild);
            rightTree.update();
            return rightTree;
        }
    }

    public void split(int index) {
        int currentIndex = size;
        RandomTree rightTree;
        RandomTree leftTree;
        if (right != null) {
            currentIndex -= right.size;
        }
        if (currentIndex <= index) {
            RandomTree leftTreeRightChild = null;
            if (right == null) {
                rightTree = null;
            } else {
                right.split(index - currentIndex);
                leftTreeRightChild = RandomTree.resultLeftTree;
                rightTree = RandomTree.resultRightTree;
            }
            right = leftTreeRightChild;
            update();
            resultLeftTree = this;
            resultRightTree = rightTree;
        } else {
            RandomTree rightTreeLeftChild = null;
            if (left == null) {
                leftTree = null;
            } else {
                left.split(index);
                rightTreeLeftChild = RandomTree.resultRightTree;
                leftTree = RandomTree.resultLeftTree;
            }
            left = rightTreeLeftChild;
            update();
            RandomTree.resultLeftTree = leftTree;
            RandomTree.resultRightTree = this;
        }
    }

    public void update() {
        this.size = 1;
        this.sum = value;
        if (left != null) {
            this.size += left.size;
            this.sum += left.sum;
        }
        if (right != null) {
            this.size += right.size;
            this.sum += right.sum;
        }
    }

    public void setValue(int index, int value) {
        if (index > size) {
            throw new IllegalArgumentException();
        }
        int currentIndex = size;
        if (right != null) {
            currentIndex -= right.size;
        }
        if (index == currentIndex) {
            this.value = value;
        }
        if (index > currentIndex) {
            right.setValue(index - currentIndex, value);
        }
        if (index < currentIndex) {
            left.setValue(index, value);
        }
        update();
    }

    public long getSum(int leftEdge, int rightEdge) {
        split(leftEdge - 1);
        RandomTree leftTree = RandomTree.resultLeftTree;
        RandomTree.resultRightTree.split(rightEdge - leftEdge + 1);
        long sum = RandomTree.resultLeftTree.getSum();
        leftTree = RandomTree.merge(leftTree,RandomTree.resultLeftTree);
        RandomTree.merge(leftTree, RandomTree.resultRightTree);
        return sum;
    }

    public long getSum() {
        return sum;
    }

    public RandomTree getLeft() {
        return left;
    }

    public RandomTree getRight() {
        return right;
    }

    public int getSize() {
        return size;
    }

    public int getPriority() {
        return priority;
    }

    public void setLeft(RandomTree left) {
        this.left = left;
    }

    public void setRight(RandomTree right) {
        this.right = right;
    }

    private RandomTree(int value, int priority, RandomTree left, RandomTree right) {
        this(value, left, right);
        this.priority = priority;
    }
}



















