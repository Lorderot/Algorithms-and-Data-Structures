import java.io.*;
import java.util.EmptyStackException;
/*
* Author: Mykola Holovatsky
* */
public class TheBiggestSubSequence {
    private int[] sequence;
    private int compactness;
    private int lengthOfSubSequence;

    public TheBiggestSubSequence(int[] sequence, int compactness,
                                 int lengthOfSubSequence) {
        this.sequence = sequence;
        this.compactness = compactness;
        this.lengthOfSubSequence = lengthOfSubSequence;
    }

    public static void main(String[] args) {
        StreamTokenizer parser = new StreamTokenizer(new BufferedReader(
                new InputStreamReader(System.in)));
        try {
            parser.nextToken();
            int amountOfNumbers = (int) parser.nval;
            parser.nextToken();
            int compactness = (int) parser.nval;
            parser.nextToken();
            int lengthOfSubSequence = (int) parser.nval;
            int[] sequence = new int[amountOfNumbers];
            for (int i = 0; i < amountOfNumbers; i++) {
                parser.nextToken();
                sequence[i] = (int) parser.nval;
            }
            TheBiggestSubSequence finder = new TheBiggestSubSequence(sequence,
                    compactness, lengthOfSubSequence);
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(System.out));
            out.print(finder.findSumOfTheBiggestSubSequence());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long findSumOfTheBiggestSubSequence() {
        long[] maxSumOfElements = new long[sequence.length];
        long[] previousStep = new long[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
            maxSumOfElements[i] = sequence[i];
        }
        Queue queue = new Queue(compactness);
        for (int j = 2; j <= lengthOfSubSequence; j++) {
            long[] swap = maxSumOfElements;
            maxSumOfElements = previousStep;
            previousStep = swap;
            queue.clear();
            queue.put(previousStep[j - 2]);
            for (int i = j - 1; i < sequence.length; i++) {
                maxSumOfElements[i] =  queue.maxElementInQueue() + sequence[i];
                if (queue.isFull()) {
                    queue.poll();
                }
                queue.put(previousStep[i]);
            }
        }
        int i = lengthOfSubSequence;
        long max = maxSumOfElements[i - 1];
        for (; i < maxSumOfElements.length; i++) {
            if (max < maxSumOfElements[i]) {
                max = maxSumOfElements[i];
            }
        }
        return max;
    }
}

class Queue {
    private int size;
    private Stack stackIn;
    private Stack stackOut;
    private int currentSize;

    public Queue(int size) {
        this.size = size;
        stackIn = new Stack(size);
        stackOut = new Stack(size);
    }

    public boolean put(long element) {
        if (isFull()) {
            return false;
        }
        stackIn.put(element);
        currentSize++;
        return true;
    }

    public long poll() throws EmptyStackException {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        if (stackOut.isEmpty()) {
            while (!stackIn.isEmpty()) {
                stackOut.put(stackIn.poll());
            }
        }
        currentSize--;
        return stackOut.poll();
    }

    public long maxElementInQueue() throws EmptyStackException {
        boolean stackInEmptiness = stackIn.isEmpty();
        boolean stackOutEmptiness = stackOut.isEmpty();
        if (stackInEmptiness && stackOutEmptiness) {
            throw new EmptyStackException();
        }
        if (stackInEmptiness) {
            return stackOut.getMaximumInStack();
        }
        if (stackOutEmptiness) {
            return stackIn.getMaximumInStack();
        }
        long maxInStackIn = stackIn.getMaximumInStack();
        long maxInStackOut = stackOut.getMaximumInStack();
        return (maxInStackIn > maxInStackOut) ? maxInStackIn : maxInStackOut;
    }

    public boolean isFull() {
        return currentSize == size;
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    public void clear() {
        stackOut.clear();
        stackIn.clear();
        currentSize = 0;
    }

    public int size() {
        return currentSize;
    }
}

class Stack {
    private int size;
    private int currentSize = 0;
    private long[] container;
    private long[] maximum;

    public Stack(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException();
        }
        this.size = size;
        container = new long[size];
        maximum = new long[size];
    }

    public boolean put(long element) {
        if (isFull()) {
            return false;
        }
        container[currentSize] = element;
        if (isEmpty()) {
            maximum[currentSize] = element;
            currentSize++;
            return true;
        }
        long currentMaximum = maximum[currentSize - 1];
        if (currentMaximum < element) {
            maximum[currentSize] = element;
        } else {
            maximum[currentSize] = currentMaximum;
        }
        currentSize++;
        return true;
    }

    public long poll() throws EmptyStackException {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return container[--currentSize];
    }

    public long getMaximumInStack() throws EmptyStackException {
        if (currentSize == 0) {
            throw new EmptyStackException();
        }
        return maximum[currentSize - 1];
    }
    public void clear() {
        currentSize = 0;
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    public boolean isFull() {
        return currentSize == size;
    }

}