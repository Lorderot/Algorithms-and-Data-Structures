import java.io.*;
import java.util.Arrays;

/**
 * @author Mykola Holovatsky
 */
public class SubString {
    private long[] hashedSubString;
    private int N;
    /*base for hashing*/
    private final int base = 29;
    /*massive of powers*/
    private long[] basePowers;
    private char[] word;
    public static void main(String ...args) {
        try {
            SubString solution = new SubString();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
            out.println(solution.findMaxSubString());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SubString() throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File("input.txt"))));
        N = Integer.parseInt(reader.readLine());
        String word = reader.readLine();
        this.word = word.toCharArray();
        basePowers = new long[N];
        basePowers[0] = 1;
        for (int i = 1; i < N; i++) {
            basePowers[i] = basePowers[i - 1] * base;
        }
    }

    public int findMaxSubString() {
        int left = 0;
        int right = N;
        int medium;
        while (right - left > 1) {
            medium = (right + left) / 2;
            if (hasWordAtLeastTwoSimilarSubString(medium)) {
                left = medium;
            } else {
                right = medium;
            }
        }
        return left;
    }

    /*return true if word contains two different
    substrings with defined length*/
    private boolean hasWordAtLeastTwoSimilarSubString(int length) {
        hashedSubString = new long[N - length + 1];
        int wordIterator = 0;
        for (int j = 0; j < length; j++) {
            hashedSubString[0] += basePowers[length - j - 1]
                    * (word[wordIterator++] - 'a' + 1);
        }

        for (int i = 1; i <= N - length; i++) {
            hashedSubString[i] += ((hashedSubString[i - 1] - basePowers[length - 1]
                    * (word[wordIterator - length] - 'a' + 1)) * base
                    + (word[wordIterator++] - 'a' + 1));
        }

        Arrays.sort(hashedSubString);
        boolean check = false;
        for (int i = 1; i <= N - length; i++) {
            if (hashedSubString[i] == hashedSubString[i - 1]) {
                check = true;
                break;
            }
        }
        return check;
    }
}