import java.io.IOException;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * @author Mykola Holovatsky
 */
public class CorridorSolution {
    /*accurately to third number*/
    private final int precisionNumber = 7;
    private final double accuracy = Math.pow(10, -precisionNumber - 1);
    /*Number of columns*/
    private final int n;
    /*the limit of right wall*/
    private final int wall;
    private Column[] columns;
    /*the matrix of "distances". If columns cross each other, distance = 1;
      It means that exists way from one column to another, i.e. you can't
      carry table of zero diameter between them*/
    private double[][] distances;
    /*Indicator of visiting columns. For Deep recursive search. If we were
      in jth column, haveBeenIn[j] == 1 */
    private int[] haveBeenIn;

    public static void main(String[] args) {
        try {
            CorridorSolution solution = new CorridorSolution("input.txt");
            double sol = solution.findMaxTableDiameter();
            System.out.printf("%.20f", sol);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CorridorSolution(String pathToInputFile) throws IOException {
        /*read data from file*/
        try (Scanner scan = new Scanner(new File(pathToInputFile))) {
            n = scan.nextInt();
            wall = scan.nextInt();
            distances = new double[n + 2][n + 2];
            columns = new Column[n];
            haveBeenIn = new int[n + 1];
            for (int i = 0; i < n; i++) {
                columns[i] = new
                        Column(scan.nextInt(), scan.nextInt(), scan.nextInt());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new FileNotFoundException();
        }
    }

    public double findMaxTableDiameter() {
        /*binary search of diameter*/
        double left = 0;
        double right = wall;
        double medium = right;
        while (right - left > accuracy) {
            if (checkIfTableCanBeCarried(medium)) {
                right = medium;
            } else {
                left = medium;
            }
            medium = (right + left) / 2;
        }

        double maxTableDiameter;
        if (!checkIfTableCanBeCarried(right)) {
            maxTableDiameter = right;
        } else {
            maxTableDiameter = left;
        }

        maxTableDiameter = trimToCertainPrecision(maxTableDiameter,
                precisionNumber);
        return maxTableDiameter;
    }

    public int getNumberOfColumns() {
        return n;
    }

    public int getRightWall() {
        return wall;
    }

    /*Check if table with certain diameter can be carried through corridor*/
    private boolean checkIfTableCanBeCarried(double diameter) {
        for (int i = 0; i < n + 1; i++) {
            haveBeenIn[i] = 0;
        }
        /* matrix indicates existing way from one column
            to another (and to walls)*/
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                long x = columns[i].getX() - columns[j].getX();
                long y = columns[i].getY() - columns[j].getY();
                long r1 = columns[i].getR();
                long r2 = columns[j].getR();
                if (Math.hypot(x, y) <= r1 + r2 + diameter) {
                    distances[i + 1][j + 1] = 1;
                    distances[j + 1][i + 1] = 1;
                } else {
                    distances[i + 1][j + 1] = 0;
                    distances[j + 1][i + 1] = 0;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (columns[i].getX() - columns[i].getR() - diameter < 0) {
                distances[0][i + 1] = 1;
                distances[i + 1][0] = 1;
            } else {
                distances[0][i + 1] = 0;
                distances[i + 1][0] = 0;
            }
            if (columns[i].getX() + columns[i].getR() + diameter > wall) {
                distances[n + 1][i + 1] = 1;
                distances[i + 1][n + 1] = 1;
            } else {
                distances[n + 1][i + 1] = 0;
                distances[i + 1][n + 1] = 0;
            }
        }
        return checkIfPassIsBlocked(0);
    }

    /*Check if exist way from one wall to another. Existing way means
    that the way for table is blocked. */
    private boolean checkIfPassIsBlocked(int i) {
        haveBeenIn[i] = 1;
        if (distances[i][n + 1] == 1) {
            return true;
        }
        for (int j = 1; j < n + 1; j++) {
            if (distances[i][j] == 1) {
                distances[i][j] = 0;
                distances[j][i] = 0;
                if (haveBeenIn[j] != 1) {
                    if (checkIfPassIsBlocked(j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private double trimToCertainPrecision (double number,
                                           int precisionNumber) {
        long precision = 1;
        double trimmedNumber = number;
        for (int i = 0; i < precisionNumber; i++) {
            precision *= 10;
        }
        trimmedNumber = trimmedNumber * precision;
        trimmedNumber = (long) trimmedNumber;
        trimmedNumber = trimmedNumber / precision;
        return trimmedNumber;
    }
}

class Column {
    private final int x;
    private final int y;
    private final int r;

    Column(int x, int y, int r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getR() {
        return r;
    }
}








