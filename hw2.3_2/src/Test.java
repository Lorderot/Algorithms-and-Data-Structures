import java.io.*;

public class Test {
    public static void main(String[] args) {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(new File("test1.txt"))));
            int N = 100000;
            int mod = 10;
            out.println(N);
            int value = 1;
            for (int i = 1; i <= N; i++) {
                if (i % mod == 0) {
                    value++;
                }
                out.print(value + " ");
            }
            out.println();
            int j = 0;
            for (int i = 0; i < N - 1; i++) {
                if (i % mod == 0) {
                    j++;
                }
                out.println(j + " " + (i + 2));
            }
            out.println(N);
            value = 1;
            for (int i = 1; i <= N; i++) {
                if (i % mod == 0) {
                    value++;
                }
                if (i != 1) {
                    j = i - 1;
                } else {
                    j = 1;
                }
                out.println(1 + " " + j + " " + value);
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
