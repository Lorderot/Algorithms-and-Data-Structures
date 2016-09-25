import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;

public class RectangularTriangle {
    /* number of points; */
    private int N;
    /* number of rectangular triangles */
    private int T;
    /* sequence of given points */
    private Point[] points;
    /* other N-1 points relatively system of coordinates placed in certain point */
    private Point[] relativePoints;

    public static void main(String args[]) {
        RectangularTriangle solution = new RectangularTriangle("input.txt");
        solution.solve();
        return;
    }

    public RectangularTriangle(String pathToInputFile) {
        try(Scanner input = new Scanner(new File(pathToInputFile))){
            N = input.nextInt();
            points = new Point[N];
            relativePoints = new Point[N-1];
            for (int i = 0; i < N; i++) {
                points[i] = new Point(input.nextInt(), input.nextInt());
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void solve() {
        /* choose point and imagine that we place system of coordinates in it */
        for (int i = 0; i < N; i++) {
            int count = 0;
            /* calculate new coordinates in system */
            for (int j = 0; j < N; j++) {
                if (i != j) {
                    relativePoints[count] = new Point(points[j].getX()
                            - points[i].getX(), points[j].getY()
                            - points[i].getY());
                    count++;
                }
            }
            relativePoints = mergeSort(relativePoints);
            /* Assume that points are vectors with appropriate coordinates.
             * To find normal vectors check scalar product of vectors
             * However all points are sorted by angle, cycle will be interrupted
             * if appropriate vector is found. Then just count these vectors*/
            for (int j = 0; j <= (N-1) - 2; j++) {
                int left = j + 1;
                int right = N - 2;
                int identificator = 0;
                int medium = 0;
                int check = normalVectors(relativePoints[j], relativePoints[left], points[i]);
                if (check == -1) {
                    identificator = 2;
                } else {
                    if (check == 1) {
                        identificator = 1;
                    } else {
                        medium = left;
                    }
                }
                if (identificator == 1) {
                    check = normalVectors(relativePoints[j], relativePoints[right], points[i]);
                    if (check == 1) {
                        identificator = 2;
                    } else {
                        if (check == -1) {
                            identificator = 1;
                        } else {
                            medium = right;
                        }
                    }
                }
                if (identificator == 1) {
                    do {
                        medium = (right + left) / 2;
                        if (medium == left) {
                            identificator =  normalVectors(relativePoints[j], relativePoints[right], points[i]);
                            medium = right;
                            left = right;
                        } else {
                            identificator = normalVectors(relativePoints[j], relativePoints[medium], points[i]);
                            if (identificator != 0) {
                                if (identificator < 0) {
                                    right = medium;
                                } else {
                                    left = medium;
                                }
                            }
                        }
                    } while (identificator != 0 && right > left);
                }

                if (identificator == 0) {
                    for (int k = medium + 1; k < N - 1; k++) {
                        identificator = normalVectors(relativePoints[j], relativePoints[k], points[k]);
                        if (identificator == 0) {
                            T++;
                        } else {
                            break;
                        }
                    }

                    for (int k = medium; k > j; k--) {
                        identificator = normalVectors(relativePoints[j], relativePoints[k], points[k]);
                        if (identificator == 0) {
                            T++;
                        } else {
                            break;
                        }
                    }
                }

            }
        }
        System.out.println(T);
        return;
    }

    private Point[] mergeSort(Point[] points) {
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
        firstHalf = mergeSort(firstHalf);
        secondHalf = mergeSort(secondHalf);
        return Merge(firstHalf, secondHalf);
    }

    private Point[] Merge(Point[] firstHalf, Point[] secondHalf) {
        int length = firstHalf.length + secondHalf.length;
        Point[] points = new Point[length];
        int i = 0, first = 0, second = 0;
        for (; first < firstHalf.length && second < secondHalf.length; i++) {
            if (compareAngles(firstHalf[first], secondHalf[second])) {
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

    private boolean compareAngles(Point x, Point y) {
        if (x.getX() * x.getY() == 0 || y.getX() * y.getY() == 0) {
            if (x.getY() * y.getY() == 0) {
                if (x.getY() == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (x.getX() * x.getY()> 0) {
                    return true;
                } else {
                    if (y.getX() * y.getY() > 0) {
                        return false;
                    } else {
                        if (x.getX() == 0) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }

        }
        /*1,3 or 2,4*/
        if ((double)x.getX() / x.getY() * (double)y.getX() / y.getY() > 0) {
            if ((double)x.getY() / x.getX() < (double) y.getY() / y.getX()) {
                return true;
            } else {
                return false;
            }
        } else {
            if (x.getX() * x.getY() > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /*check angle between vectors. projection x on y*/
    private int normalVectors(Point x, Point y, Point i) {
        if (x.getX() * y.getX() + x.getY() * y.getY() == 0) {
            return 0;
        }
        if (y.getY() * x.getY() >= 0) {
            if (x.getY() == 0) {
                if (y.getY() * y.getX() >= 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
            if (x.getX() * y.getX() + x.getY() * y.getY() > 0) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (x.getX() * y.getX() + x.getY() * y.getY() < 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}

class Point {
    private long x;
    private long y;

    public Point(long x, long y) {
        this.x = x;
        this.y = y;
    }
    public String toString() {
        return ("x = " + x + " y = " + y);

    }
    public long getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}