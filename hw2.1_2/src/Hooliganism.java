import java.io.*;
import java.util.Arrays;

public class Hooliganism {
    private int primaryNumber = (int) Math.pow(10, 9) + 7;
    private int numberOfThrows;
    private int numberOfThrowsLimit = 2 * (int) Math.pow(10, 6);
    private int[] leftEdgeOfCitizenRoute;
    private int[] rightEdgeOfCitizenRoute;
    private int[] numberOfFrustratedCitizenAfterCertainThrow;
    private SparseTable timeLampIsBroken;

    public Hooliganism(int numberOfLampsInCity, int[] coordinatesOfThrows,
                       int[] leftEdgeOfCitizenRoute, int[] rightEdgeOfCitizenRoute) {
        this.leftEdgeOfCitizenRoute = leftEdgeOfCitizenRoute;
        this.rightEdgeOfCitizenRoute = rightEdgeOfCitizenRoute;
        this.numberOfThrows = coordinatesOfThrows.length;
        numberOfFrustratedCitizenAfterCertainThrow = new int[numberOfThrows];
        int[] timeLampIsBroken = new int[numberOfLampsInCity];
        Arrays.fill(timeLampIsBroken, numberOfThrowsLimit);
        for (int i = 0; i < coordinatesOfThrows.length; i++) {
            int brokenLamp = coordinatesOfThrows[i];
            if (timeLampIsBroken[brokenLamp] == numberOfThrowsLimit) {
                timeLampIsBroken[brokenLamp] = i;
            }
        }
        this.timeLampIsBroken = new SparseTable(timeLampIsBroken);
    }

    public static void main(String[] args) {

        HooliganismDataProducer producer = new HooliganismDataProducer();
        producer.makeRequestForData();
        Hooliganism solver = new Hooliganism(producer.getNumberOfLampsInCity(),
                producer.getCoordinatesOfThrows(),
                producer.getLeftEdgeOfCitizenRoute(),
                producer.getRightEdgeOfCitizenRoute());
        long answer = solver.findTheSumOfFrustratedCitizens();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
        out.print(answer);
        out.close();
    }

    public long findTheSumOfFrustratedCitizens() {
        findDistributionOfFrustratedCitizens();
        long powerOfThreeMod = 1;
        long sum = 0;
        for (int i = 0; i < numberOfThrows; i++) {
            sum += (numberOfFrustratedCitizenAfterCertainThrow[i]
                    * powerOfThreeMod) % primaryNumber;
            powerOfThreeMod *= 3;
            powerOfThreeMod %= primaryNumber;
        }
        return sum % primaryNumber;
    }

    private void findDistributionOfFrustratedCitizens() {
        int numberOfCitizens = leftEdgeOfCitizenRoute.length;
        for (int i = 0; i < numberOfCitizens; i++) {
            int theTimeCitizenBecomeFrustrated
                    = timeLampIsBroken.rangeMaxQuery(leftEdgeOfCitizenRoute[i],
                    rightEdgeOfCitizenRoute[i]);
            if (theTimeCitizenBecomeFrustrated < numberOfThrowsLimit) {
                numberOfFrustratedCitizenAfterCertainThrow[
                        theTimeCitizenBecomeFrustrated]++;
            }
        }
        for (int i = 1; i < numberOfThrows; i++) {
            numberOfFrustratedCitizenAfterCertainThrow[i] = (int)(((long)
                    numberOfFrustratedCitizenAfterCertainThrow[i - 1]
                     + numberOfFrustratedCitizenAfterCertainThrow[i])
                    % primaryNumber);
        }
    }
}

class SparseTable {
    private int[][] sparseTable;
    public SparseTable(int[] array) {
        int length = (int) (Math.log(array.length) / Math.log(2)) + 1;
        sparseTable = new int[length][array.length];
        for (int i = 0; i < array.length; i++) {
            sparseTable[0][i] = array[i];
        }
        for (int j = 1; j < length; j++) {
            int power = 1 << (j - 1);
            for (int i = 0; i < array.length - power; i++) {
                sparseTable[j][i] = max(sparseTable[j - 1][i],
                        sparseTable[j - 1][i + power]);
            }
        }
    }

    public int rangeMaxQuery(int leftEdge, int rightEdge) {
        if (leftEdge > rightEdge) {
            int swap = leftEdge;
            leftEdge = rightEdge;
            rightEdge = swap;
        }
        int index = (int) (Math.log(rightEdge - leftEdge + 1) / Math.log(2));
        return max(sparseTable[index][leftEdge],
                sparseTable[index][rightEdge - (1 << index) + 1]);
    }
    private int max(int number1, int number2) {
        return (number1 > number2) ? number1 : number2;
    }
}

class HooliganismDataProducer {
    private int numberOfLampsInCity = 0;
    private int numberOfInitialValues = 0;
    private int[] coordinatesOfThrows;
    private int[] leftEdgeOfCitizenRoute;
    private int[] rightEdgeOfCitizenRoute;


    public int[] getCoordinatesOfThrows() {
        return coordinatesOfThrows;
    }

    public int[] getLeftEdgeOfCitizenRoute() {
        return leftEdgeOfCitizenRoute;
    }

    public int[] getRightEdgeOfCitizenRoute() {
        return rightEdgeOfCitizenRoute;
    }

    public int getNumberOfLampsInCity() {
        return numberOfLampsInCity;
    }

    public void makeRequestForData() {
        StreamTokenizer parser = new StreamTokenizer(new BufferedReader(
                new InputStreamReader(System.in)));
        try {
            parser.nextToken();
            numberOfLampsInCity = (int) parser.nval;
            parser.nextToken();
            int numberOfCitizens = (int) parser.nval;
            leftEdgeOfCitizenRoute = new int[numberOfCitizens];
            rightEdgeOfCitizenRoute = new int[numberOfCitizens];
            parser.nextToken();
            int numberOfThrows = (int) parser.nval;
            parser.nextToken();
            coordinatesOfThrows = new int[numberOfThrows];
            numberOfInitialValues = (int) parser.nval;
            parser.nextToken();
            int coordinatesGeneratorParam1 = (int) parser.nval;
            parser.nextToken();
            int coordinatesGeneratorParam2 = (int) parser.nval;
            parser.nextToken();
            int firstLampGeneratorParam1 = (int) parser.nval;
            parser.nextToken();
            int firstLampGeneratorParam2 = (int) parser.nval;
            parser.nextToken();
            int lastLampGeneratorParam1 = (int) parser.nval;
            parser.nextToken();
            int lastLampGeneratorParam2 = (int) parser.nval;
            for (int i = 0; i < numberOfInitialValues; i++) {
                parser.nextToken();
                if (i < coordinatesOfThrows.length) {
                    coordinatesOfThrows[i] = (int) parser.nval
                            % numberOfLampsInCity;
                }
            }
            coordinatesOfThrows = generateTheData(coordinatesOfThrows,
                    coordinatesGeneratorParam1, coordinatesGeneratorParam2);
            for (int i = 0; i < numberOfInitialValues; i++) {
                parser.nextToken();
                if (i < leftEdgeOfCitizenRoute.length) {
                    leftEdgeOfCitizenRoute[i] = (int) parser.nval
                            % numberOfLampsInCity;
                }
            }
            leftEdgeOfCitizenRoute = generateTheData(leftEdgeOfCitizenRoute,
                    firstLampGeneratorParam1, firstLampGeneratorParam2);
            for (int i = 0; i < numberOfInitialValues; i++) {
                parser.nextToken();
                if (i < rightEdgeOfCitizenRoute.length) {
                    rightEdgeOfCitizenRoute[i] = (int) parser.nval
                            % numberOfLampsInCity;
                }
            }
            rightEdgeOfCitizenRoute = generateTheData(rightEdgeOfCitizenRoute,
                    lastLampGeneratorParam1, lastLampGeneratorParam2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] generateTheData(int[] initial, int param1, int param2) {
        for (int i = numberOfInitialValues; i < initial.length; i++) {
            initial[i] = (int)((((long)initial[i - 1] * param1) + param2)
                    % numberOfLampsInCity);
        }
        return initial;
    }
}