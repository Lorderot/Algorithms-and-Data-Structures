import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.File;

public class BrokenLine {
    /* number of points; */
    private int N;
    /* sequence of given points */
    private Point[] points;
    /*number of points, which make up broken line*/
    private int[] brokenLine;
    private String answer;

    public static void main(String args[]) {
        BrokenLine solution = new BrokenLine();
        solution.solve();
    }

    public BrokenLine() {
        try(Scanner input = new Scanner(new File("input.txt"))){
            N = input.nextInt();
            points = new Point[N];
            brokenLine = new int[N];

            for (int i = 0; i < N; i++) {
                points[i] = new Point(input.nextInt(), input.nextInt(), i + 1);
            }

        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    /*implicitly draw a line through 1-st and last elements in massive.
     * A * x + B* y + C = 0
     *(x - x1) / (x2 - x1) == (y - y1) / (y2 - y1)
     * (x - x1) * (y2 - y1) == (y - y1) * (x2 - x1)
     * (y2 - y1) * x - (x2 - x1) * y + y1 * (x2 - x1) - x1 * (y2 - y1)
     * Alternatives:
     * if (x2 - x1 != 0) {
     *      A = (y2 - y1) / (x2 - x1);
     *      B = -1;
     *      C = -x1 * (y2 - y1) / (x2 - x1) + y1
     * }
     * if (y2 - y1 != 0) {
     *      A = -1;
     *      B = (x2 - x1) / (y2 - y1);
     *      C = -y1 * (x2 - x1) / (y2 - y1) + x1
     *
     * }
     * And match all points one by one in massive, which are on the one side
     *or on this line. Then match with points on the other side of line.
     * (x1, y1) = points[0]
     * (x2, y2) = points[N-1]
    */
    public void solve() {
        points = MergeSort(points);
        double A;
        double B;
        double C;
        if (points[N - 1].getX() - points[0].getX() != 0) {
            B = -1.0;
            A = (double)(points[N - 1].getY() - points[0].getY())
                    / (points[N - 1].getX() - points[0].getX());
            C = (double)-points[0].getX() * ((double)(points[N - 1].getY() - points[0].getY())
                    / (points[N - 1].getX() - points[0].getX()))
                    + points[0].getY();

        } else {
            if (points[N - 1].getY() - points[0].getY() != 0) {
                A = -1.0;
                B = (double)(points[N - 1].getX() - points[0].getX())
                        / (points[N - 1].getY() - points[0].getY());
                C = (double)-points[0].getY() * ((double)(points[N - 1].getX() - points[0].getX())
                        / (points[N - 1].getY() - points[0].getY()))
                        + points[0].getX();
            } else {
                throw new InputMismatchException();
            }
        }
        int positive = 0;
        for (int i = 0; i < N; i++) {
            if (A * points[i].getX() + B * points[i].getY() + C > 0) {
                positive++;
            }
        }
        /*how many points are added to the way*/
        int count = 0;
        int previousResult;
        if (positive == 0) {
            for (int i = 0; i < N; i++) {
                if (A * points[i].getX() + B * points[i].getY() + C == 0) {
                    brokenLine[count++] = points[i].getID();
                }
            }
            previousResult = count;
            for (int i = N - 1; i > 0; i--) {
                if (A * points[i].getX() + B * points[i].getY() + C < 0) {
                    brokenLine[count++] = points[i].getID();
                }
            }
        } else {
            for (int i = 0; i < N; i++) {
                if (A * points[i].getX() + B * points[i].getY() + C <= 0) {
                    brokenLine[count++] = points[i].getID();
                }
            }
            previousResult = count;
            for (int i = N - 1; i > 0; i--) {
                if (A * points[i].getX() + B * points[i].getY() + C > 0) {
                    brokenLine[count++] = points[i].getID();
                }
            }

        }
        /*check if there is such point from another half-plane, that can connect
        * tail with head */
        if (previousResult != count) {
            System.out.println("Yes");
            for(int i = 0; i < N; i++) {
                System.out.print(brokenLine[i] + " ");
            }
        }
        else {
            System.out.println("No");
        }
    }

    /*priority sort points in decreasing order:
    * 1. By X.
    * 2. By Y */
    private Point[] MergeSort(Point[] points) {
        if (points.length == 1) {
            return points;
        }
        Point[] firstHalf = new Point[points.length / 2];
        Point[] secondHalf = new Point[points.length - points.length / 2];
        int i;
        for (i = 0; i < points.length / 2; i++) {
            firstHalf[i] = points[i];
        }
        for (int j = 0; i < points.length; j++, i++) {
            secondHalf[j] = points[i];
        }
        firstHalf = MergeSort(firstHalf);
        secondHalf = MergeSort(secondHalf);
        return Merge(firstHalf, secondHalf);
    }

    private Point[] Merge(Point[] firstHalf, Point[] secondHalf) {
        int length = firstHalf.length + secondHalf.length;
        Point[] points = new Point[length];
        int i = 0, first = 0, second = 0;
        for (; first < firstHalf.length && second < secondHalf.length; i++) {

            if (firstHalf[first].getX() > secondHalf[second].getX()
                    || (firstHalf[first].getX() == secondHalf[second].getX()
                    && firstHalf[first].getY()
                    > secondHalf[second].getY())) {

                points[i] = firstHalf[first];
                first++;

            } else {
                points[i] = secondHalf[second];
                second++;
            }
        }

        for(; first < firstHalf.length; i++, first++) {
            points[i] = firstHalf[first];
        }

        for(; second < secondHalf.length; i++, second++) {
            points[i] = secondHalf[second];
        }
        return points;
    }
}

class Point {
    private int x;
    private int y;
    /* serial number of given points */
    private int identifier;

    public Point(int x, int y, int ID) throws IllegalArgumentException {
        if (ID <= 0) {
            throw new IllegalArgumentException();
        }
        this.x = x;
        this.y = y;
        this.identifier = ID;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getID() {
        return identifier;
    }

    public void setID(int ID) throws IllegalArgumentException {
        if (ID > 0) {
            throw  new IllegalArgumentException();
        }
        this.identifier = ID;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}