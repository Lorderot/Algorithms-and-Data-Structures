import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class test {
    public static void main(String[] args) {
        boolean[] visited = new boolean[2];
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File("test.txt")));) {
            writer.print(80 + " ");
            writer.println(80);
            for (int i = 0; i < 80; i++) {
                for (int j = 0; j < 80; j++) {
                    if ((i + j) % 2 == 0) {
                        writer.print('.');
                    } else {
                        writer.print('x');
                    }
                }
                writer.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
