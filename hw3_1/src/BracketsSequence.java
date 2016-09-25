import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;

/**
 * @author Mykola Holovatsky
 */
public class BracketsSequence {
    private int n;
    private int tail = 5;
    private final int limit = 100000;
    private char[] sequenceOfBrackets;
    /*array of numbers(restorations) for certain subStrings
    * amountOfSubString[n / 2 - 1][0] - appropriate number
    * of whole String*/
    private int amountOfSubStrings[][];

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            int numberOfBrackets = Integer.parseInt(reader.readLine());
            String sequenceOfBrackets = reader.readLine();
            BracketsSequence bracketsSequence =
                    new BracketsSequence(numberOfBrackets, sequenceOfBrackets);
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(System.out));
            String numberOfCorrectSequences =
                    bracketsSequence.calculateNumberOfCorrectSequences();
            out.write(numberOfCorrectSequences);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BracketsSequence(int numberOfBrackets, String sequenceOfBrackets) {
        n = numberOfBrackets;
        this.sequenceOfBrackets = sequenceOfBrackets.toCharArray();
        amountOfSubStrings = new int[n / 2][];
        for (int i = 0; i < n / 2; i++) {
            amountOfSubStrings[i] = new int[n - 2 * i - 1];
        }
    }

    /*calculates the numbers for elements in next raw by means of previous*/
    public String calculateNumberOfCorrectSequences() {
        for (int k = 1; k < n; k++) {
            amountOfSubStrings[0][k - 1] = checkBrackets(k - 1, k);
        }
        for (int i = 1; i < n / 2; i++) {

            for (int k = 0; k < n - 2 * i - 1; k++) {

                amountOfSubStrings[i][k] = trim(amountOfSubStrings[i][k] +
                        (checkBrackets(k, k + 2 * i + 1)
                                * amountOfSubStrings[i - 1][k + 1]));

                for (int j = 0; j < i; j++) {
                    int amountBetweenBrackets;
                    if (j > 0) {
                        amountBetweenBrackets =
                                amountOfSubStrings[j - 1][k + 1];
                    } else {
                        amountBetweenBrackets = 1;
                    }
                    amountOfSubStrings[i][k] = trim(amountOfSubStrings[i][k]
                            + (long)checkBrackets(k, k + 2 * j + 1)
                            * amountBetweenBrackets
                            * amountOfSubStrings[i - j - 1][k + 2 * (j + 1)]);
                }
            }
        }
        String digits = createTheCorrectTail(amountOfSubStrings[n / 2 - 1][0]);
        return digits;
    }

    private String createTheCorrectTail(int cutNumber) {
        String number = Integer.toString(cutNumber);
        return (cutNumber >= limit) ?
                (number.substring(number.length() - tail)) : number;
    }


    private int checkBrackets(int leftBracket, int rightBracket) {
        char openedBracket = sequenceOfBrackets[leftBracket];
        char closedBracket = sequenceOfBrackets[rightBracket];
        if (openedBracket == '?' && (closedBracket == ')'
                || closedBracket == ']' || closedBracket == '}')) {
            return 1;
        }
        if (closedBracket == '?' && (openedBracket
                == '(' || openedBracket == '['
                || openedBracket == '{')) {
            return 1;
        }
        if (closedBracket == '?' && openedBracket == '?') {
            return 3;
        }
        if ((openedBracket == '(' && closedBracket == ')')
                || (openedBracket == '['
                && closedBracket == ']')
                || (openedBracket == '{'
                && closedBracket == '}')) {
            return 1;
        }
        return 0;
    }
    /*the number in 6-th position identifies if the true number
     was out the limit.*/
    private int trim(long number) {
        return (int)((number > limit) ? (number % limit + limit) : (number));
    }
}