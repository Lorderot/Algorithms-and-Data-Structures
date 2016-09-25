import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;

/**
 * @author Mykola Holovatsky
 */
public class InterestingNumbers {
    /*multiplication of two prime numbers*/
    private final long MULTIPLICATION;

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            int multiplication = Integer.parseInt(reader.readLine());
            reader.close();

            InterestingNumbers solution = new InterestingNumbers(multiplication);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
            long[] numbers = solution.findNumbers();
            for (int i = 0; i < numbers.length; i++) {
                out.write(Long.toString(numbers[i]) + " ");
            }
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InterestingNumbers(int multiplication) {
        this.MULTIPLICATION = multiplication;
    }

    public long[] findNumbers() {
        long max = findMaxNumber();
        if (max == -1) {
            return new long[]{0,1};
        }
        long[] result = new long[4];
        result[0] = 0;
        result[1] = 1;
        int count = 2;
        long min = MULTIPLICATION / max;

        for (long i = max; i < MULTIPLICATION; i += max) {
            if ((i - 1) % min == 0) {
                result[count++] = i;
            }
            if ((i + 1) % min == 0) {
                result[count++] = i + 1;
            }
        }
        return result;
    }

    private long findMaxNumber() {
        for (long i = 2; i < Math.sqrt(MULTIPLICATION); i++) {
            if (MULTIPLICATION % i == 0) {
                return (i > MULTIPLICATION / i) ? i : MULTIPLICATION / i;
            }
        }
        return -1;
    }
}
