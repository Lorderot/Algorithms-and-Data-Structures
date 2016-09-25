import java.io.*;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Mykola Holovatsky
 */
public class SumOfDigits {
    private int[] lowerBound;
    private int[] upperBound;
    private long minimum;
    private long result;
    private long[][] amountForDifferentSum;
    private int sum = 0;

    public static void main(String[] args) {
        try {
            SumOfDigits sum = new SumOfDigits();
            long[] result = sum.solution();
            PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
            for (int i = 0; i < result.length; i++) {
                out.write(Long.toString(result[i]) + '\n');
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public SumOfDigits() throws IOException{
        BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in));
        String line = input.readLine();
        String[] numbers = line.split("\\s");
        lowerBound = new int[numbers[0].length()];
        upperBound = new int[numbers[1].length()];

        for (int i = 0; i < numbers[0].length(); i++) {
            lowerBound[i] = numbers[0].charAt(i) - '0';
        }

        for (int i = 0; i < numbers[1].length(); i++) {
            upperBound[i] = numbers[1].charAt(i) - '0';
        }

        sum = Integer.valueOf(numbers[2]);
        int firstSize = (upperBound.length - 1 > 1) ? upperBound.length - 1 : 1;
        amountForDifferentSum = new long[firstSize][];
        int secondSize = (sum < 9) ? 9 : sum;
        for (int i = 0; i < firstSize; i++) {
            amountForDifferentSum[i] = new long[secondSize + 1];
        }
    }

    public long[] solution() {
        minimum  = findMinimum();
        fillTheArrays();
        if (lowerBound.length < upperBound.length) {
            for (int i = 1; i < upperBound[0]; i++) {
                this.result += amountForDifferentSum[upperBound.length - 2][sum - i];
            }
            this.result += findAmountOfNumbers(upperBound.length - 1, sum, 4);
            this.result += findAmountOfNumbers(upperBound.length - 1, sum
                    - upperBound[0], 2);
        } else {
            this.result = findAmountOfNumbers(upperBound.length, sum, 3);
        }


        return new long[]{this.result, minimum};
    }

    private long findAmountOfNumbers(int n, int sum, int identifier) {
        long result = 0;
        if (n > 1) {
            if (identifier == 1) {
                for (int i = lowerBound[lowerBound.length - n] + 1; i < 10; i++) {
                    if (sum >= i) {
                        result += amountForDifferentSum[n - 2][sum - i];
                    } else {
                        break;
                    }
                }
                result += findAmountOfNumbers(n - 1, sum
                        - lowerBound[lowerBound.length - n], 1);
            } else {
                if (identifier == 2) {
                    for (int i = 0; i < upperBound[upperBound.length - n]; i++) {
                        if (sum >= i) {
                            result += amountForDifferentSum[n - 2][sum - i];
                        } else {
                            break;
                        }
                    }
                    result += findAmountOfNumbers(n - 1, sum
                            - upperBound[upperBound.length - n], 2);
                } else {
                    if (identifier == 3) {
                        for (int i = lowerBound[lowerBound.length - n] + 1;
                             i < upperBound[upperBound.length - n]; i++) {
                            if (sum >= i) {
                                result += amountForDifferentSum[n - 2][sum - i];
                            } else {
                                break;
                            }
                        }

                        if (lowerBound[lowerBound.length - n]
                                == upperBound[upperBound.length - n]) {
                            result += findAmountOfNumbers(n - 1, sum
                                    - lowerBound[lowerBound.length - n], 3);
                        } else {
                            result += findAmountOfNumbers(n - 1, sum
                                    - lowerBound[lowerBound.length - n], 1);
                            result += findAmountOfNumbers(n - 1, sum
                                    - upperBound[upperBound.length - n], 2);
                        }
                    } else {
                        if (identifier == 4) {
                            if (n - 1 == lowerBound.length) {
                                    result += findAmountOfNumbers(n - 1, sum, 1);
                            } else {
                                result += findAmountOfNumbers(n - 1, sum, 4);
                            }
                            for (int i = 1; i < 10; i++) {
                                if (sum >= i) {
                                    result += amountForDifferentSum[n - 2][sum - i];
                                } else {
                                    break;
                                }
                            }

                        } /*else {
                            for (int i = 0; i < 10; i++) {
                                if (sum >= i) {
                                    result += amountForDifferentSum[n - 2][sum - i];
                                } else {
                                    break;
                                }
                            }
                        }*/
                    }
                }
            }
        } else {
            if (sum >= 0 && sum <= 9) {
                if (identifier == 0) {
                    return 1;
                }

                if (sum >= lowerBound[lowerBound.length - n]) {
                    if (identifier == 1) {
                        return 1;
                    } else {
                        if (sum <= upperBound[upperBound.length - n]) {
                            return 1;
                        }
                    }
                }

                if (sum <= upperBound[upperBound.length - n]) {
                    if (identifier == 2) {
                        return 1;
                    }
                }
            }
        }
        return result;
    }

    private void fillTheArrays() {
        for (int j = 0 ; j <= 9; j++) {
            amountForDifferentSum[0][j] = 1;
        }

        for (int i = 1; i < upperBound.length - 1; i++) {
            for (int j = 0; j <= sum; j++) {
                for (int k = 0; k < 10; k++) {
                    if (j - k >= 0) {
                        amountForDifferentSum[i][j] += amountForDifferentSum[i - 1][j - k];
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private long findMinimum() {
        int sum = 0;
        long minimum = 0;
        for (int i = 0; i < lowerBound.length; i++) {
            sum += lowerBound[i];
        }
        if (this.sum <= lowerBound.length * 9) {
            int temporary = this.sum - sum;
            if (temporary > 0 ) {
                int k = 1;
                while (temporary > 0) {
                    if (temporary > 9 - lowerBound[lowerBound.length - k]) {
                        minimum += 9 * Math.pow(10, k - 1);
                        temporary -= 9 - lowerBound[lowerBound.length - k];
                        k++;
                    } else {
                        minimum += (lowerBound[lowerBound.length - k]
                                + temporary) * Math.pow(10, k - 1);
                        temporary = 0;
                        k++;
                    }
                }
                for (int i = k; i <= lowerBound.length; i++) {
                    minimum += lowerBound[lowerBound.length - i]
                            * Math.pow(10, i - 1);
                }
            } else {
                if (temporary == 0) {
                    return makeTheNumber(lowerBound);
                }
                temporary = 0;
                int k = 0;
                while (temporary + lowerBound[k] < this.sum) {
                    temporary += lowerBound[k];
                    minimum *= 10;
                    minimum += lowerBound[k++];
                }
                minimum += 1;
                temporary++;
                minimum *= Math.pow(10, lowerBound.length - k);
                minimum += this.sum - temporary;
            }
        } else {
            int temporary = this.sum;
            int k = 0;
            while (temporary - 9 > 0) {
                minimum += 9 * (long)Math.pow(10, k);
                k++;
                temporary -= 9;
            }
            minimum += temporary * (long)Math.pow(10, k);
        }
        return minimum;
    }
    private long makeTheNumber(int [] number) {
        long result = 0;
        for (int i = number.length - 1; i >= 0; i--) {
            if (number[i] > 0) {
                result += number[i] * Math.pow(10, number.length - 1 - i);
            } else {
                result *= 10;
            }

        }
        return result;
    }
}