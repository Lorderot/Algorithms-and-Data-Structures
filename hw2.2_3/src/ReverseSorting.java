import java.io.*;
import java.util.Random;

public class ReverseSorting {
    private int[] sequence;
    private RandomTree[] nodes;
    private RandomTree tree = RandomTree.DUMMY_NODE;
    public static int countZeros = 0;
    public static int countOnes = 0;

    public ReverseSorting(int[] sequence) {
        this.sequence = sequence;
        nodes = new RandomTree[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
            RandomTree node = new RandomTree(sequence[i], RandomTree.DUMMY_NODE,
                    RandomTree.DUMMY_NODE, RandomTree.DUMMY_NODE);
            tree = RandomTree.merge(tree, node);
            nodes[sequence[i] - 1] = node;
        }
        tree.dfs();
        System.out.println(tree.depth);
        System.out.println();
        tree.split(20);
        RandomTree firstLeft = RandomTree.resultLeftTree;
        RandomTree firstRight = RandomTree.resultRightTree;
        firstRight.split(30);
        RandomTree secondRight = RandomTree.resultRightTree;
        RandomTree secondLeft = RandomTree.resultLeftTree;
        secondLeft.split(1);
        RandomTree thirdRight = RandomTree.resultRightTree;
        RandomTree thirdLeft = RandomTree.resultLeftTree;
        secondLeft = RandomTree.merge(thirdLeft, thirdRight);
        firstRight = RandomTree.merge(secondLeft,secondRight);
        tree = RandomTree.merge(firstLeft,firstRight);
        System.out.println();
        tree.dfs();
        System.out.println(tree.depth);
    }

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

            ReverseSorting reverseSorting = new ReverseSorting(sequence);
            int[] answer = reverseSorting.answer();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
            for (int i = 0; i < answer.length; i++) {
                out.write(Long.toString(answer[i]) + " ");
            }
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public int[] answer() {
        int[] answer = new int[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
            int index = RandomTree.findPosition(nodes[i]);
            answer[i] = index;
            tree = tree.reverse(i + 1, index);
        }
        return answer;
    }
}

class RandomTree {
    public static final RandomTree DUMMY_NODE = new RandomTree(0, 0);
    private boolean reversed = false;
    private int value;
    private int size;
    private int priority;
    private RandomTree parent;
    public static RandomTree resultLeftTree;
    public static RandomTree resultRightTree;
    public int depth;
    private static Random generator = new Random(5);

    private RandomTree left;
    private RandomTree right;

    public RandomTree(int value, RandomTree parent) {
        this.value = value;
        this.priority = generator.nextInt(1);
        this.parent = parent;
        this.size = 1;
    }

    public RandomTree(int value, RandomTree parent,
                      RandomTree left, RandomTree right) {
        this(value, parent);
        this.left = left;
        this.right = right;
        update();
    }

    public static RandomTree buildTree(int[] sequence) {
        RandomTree root = DUMMY_NODE;
        for (int i = 0; i < sequence.length; i++) {
            root = merge(root, new RandomTree(sequence[i], DUMMY_NODE));
        }
        return root;
    }

    public static RandomTree merge(RandomTree leftTree, RandomTree rightTree) {
        if (leftTree == DUMMY_NODE) {
            return rightTree;
        }
        if (rightTree == DUMMY_NODE) {
            return leftTree;
        }
        leftTree.reverseIfItIsNecessary();
        rightTree.reverseIfItIsNecessary();

        boolean flag = false;
        if (leftTree.getPriority() == rightTree.getPriority()) {
            if (Math.abs(leftTree.left.size - leftTree.right.size)
                    > Math.abs(rightTree.left.size - rightTree.right.size)) {
                flag = true;
            }
        }
        if (leftTree.getPriority() > rightTree.getPriority() || flag) {
            RandomTree rightChild = merge(leftTree.right, rightTree);
            leftTree.setRight(rightChild);
            rightChild.setParent(leftTree);
            leftTree.update();
            return leftTree;
        } else {
            RandomTree leftChild = merge(leftTree, rightTree.left);
            rightTree.setLeft(leftChild);
            leftChild.setParent(rightTree);
            rightTree.update();
            return rightTree;
        }
    }

    public void split(int index) {
        if (size == 0) {
            RandomTree.resultLeftTree = DUMMY_NODE;
            RandomTree.resultRightTree = DUMMY_NODE;
            return;
        }
        reverseIfItIsNecessary();
        int currentIndex = size;
        RandomTree rightTree;
        RandomTree leftTree;
        currentIndex -= right.size;

        if (currentIndex <= index) {
            RandomTree leftTreeRightChild;
            right.split(index - currentIndex);
            leftTreeRightChild = RandomTree.resultLeftTree;
            rightTree = RandomTree.resultRightTree;
            right = leftTreeRightChild;
            right.parent = this;
            update();
            resultLeftTree = this;
            rightTree.parent = DUMMY_NODE;
            resultRightTree = rightTree;
        } else {
            RandomTree rightTreeLeftChild;
            left.split(index);
            rightTreeLeftChild = RandomTree.resultRightTree;
            leftTree = RandomTree.resultLeftTree;
            left = rightTreeLeftChild;
            left.parent = this;
            update();
            RandomTree.resultLeftTree = leftTree;
            leftTree.parent = DUMMY_NODE;
            RandomTree.resultRightTree = this;
        }
    }

    public static int findPosition(RandomTree node) {
        if (node.parent == DUMMY_NODE) {
            node.reverseIfItIsNecessary();
            return 1 + node.left.size;
        }
        int parentIndex = findPosition(node.parent);
        node.reverseIfItIsNecessary();
        int currentIndex = parentIndex;
        if (node.equals(node.parent.right)) {
            currentIndex += 1 + node.left.size;
        } else {
            currentIndex -= 1 + node.right.size;
        }
        return currentIndex;
    }

    public RandomTree reverse(int leftEdge, int rightEdge) {
        if (leftEdge == rightEdge) {
            return this;
        }
        split(leftEdge - 1);
        RandomTree leftTree = RandomTree.resultLeftTree;
        RandomTree.resultRightTree.split(rightEdge - leftEdge + 1);
        RandomTree middleTree = RandomTree.resultLeftTree;
        RandomTree rightTree = RandomTree.resultRightTree;
        middleTree.reversed ^= true;
        return merge(merge(leftTree,middleTree),rightTree);
    }

    public void update() {
        this.size = 1 + left.size + right.size;
    }

    public int getValue(int index) {
        reverseIfItIsNecessary();
        if (index > size) {
            throw new IllegalArgumentException();
        }
        int currentIndex = size - right.size;
        if (index == currentIndex) {
            return value;
        }
        if (index > currentIndex) {
            return right.getValue(index - currentIndex);
        } else {
            return left.getValue(index);
        }
    }

    public void reverseIfItIsNecessary() {
        if (reversed) {
            reversed = false;
            left.reversed ^= true;
            right.reversed ^= true;
            RandomTree swap = left;
            left = right;
            right = swap;
        }
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

    public void setParent(RandomTree parent) {
        this.parent = parent;
    }

    public RandomTree getParent() {
        return parent;
    }

    public void setLeft(RandomTree left) {
        this.left = left;
    }

    public void setRight(RandomTree right) {
        this.right = right;
    }

    private RandomTree(int size, int value) {
        this.size = size;
        this.value = value;
        left = this;
        right = this;
        parent = this;
    }
    public void dfs() {
        int parentValue = -1;
        String description = "";
        if (this.equals(DUMMY_NODE)) {
            return;
        }
        parentValue = parent.value;
        if (parent.left.equals(this)) {
            description += " left";
        } else {
            description += " right";
        }
        System.out.println(value + " " + parentValue +  description + " " + reversed);
        left.dfs();
        right.dfs();
        depth = Math.max(left.depth, right.depth) + 1;
    }
}
