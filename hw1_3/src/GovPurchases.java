import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GovPurchases{
    private ArmoredCarrier[] suggestions;
    private int[] numberOfSuggestions;
    //Number of armored carrier
    private int N;
    //Number of necessary purchase
    private int K;


    public static void main(String[] args) {
        GovPurchases solution = new GovPurchases("input.txt");
        solution.findSolution("output.txt");
    }

    public GovPurchases(String pathToFile) {
        try (Scanner input = new Scanner(new File(pathToFile))) {
            N = input.nextInt();
            K = input.nextInt();
            numberOfSuggestions = new int[N];
            suggestions = new ArmoredCarrier[N];

            for (int i = 0; i < N; i++) {
                numberOfSuggestions[i] = i+1;
            }

            for (int i = 0; i < N; i++) {
                suggestions[i] = new ArmoredCarrier(input.nextInt(),input.nextInt());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //binary search
    public void findSolution(String pathToOutputFile) {
        // -10^5<= quality <= 10^5;
        // 1<= K, price <= 10^5
        double leftEfficiency = -Math.pow(10,10) / N;
        double rightEfficiency = Math.pow(10,10) / N;
        double middleEfficiency;
        double accuracy = Math.pow(10,-10);

        while(rightEfficiency - leftEfficiency > accuracy) {
            middleEfficiency = (leftEfficiency + rightEfficiency) / 2;

            if (findTheBestPurchases(middleEfficiency)) {
                leftEfficiency = middleEfficiency;
            }
            else {
                rightEfficiency = middleEfficiency;
            }
        }

        try (FileWriter output = new FileWriter(new File(pathToOutputFile))) {
            for (int i = N-1; i > N-1-K; i--) {
                output.write(String.valueOf(numberOfSuggestions[i]));
                output.write(" ");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private boolean findTheBestPurchases(double efficiency) {
        sort(efficiency,0,N-1);
        double summaryEfficiency = 0;

        for (int i = N-1; i > N-1-K; i--) {
            summaryEfficiency += effectiveness(i, efficiency);
        }

        if (summaryEfficiency >= 0) {
            return true;
        }
        else {
            return false;
        }
    }

    //for simplifying code.
    private double effectiveness(int index, double efficiency) {
        return suggestions[index].getQuality()
                - efficiency * suggestions[index].getPrice();
    }

    private double effectiveness(ArmoredCarrier carrier, double efficiency) {
        return carrier.getQuality()
                - efficiency * carrier.getPrice();
    }
    //Sort suggestions by effectiveness = (quality - efficiency*price)
    // in increase order
    private void sort(double efficiency, int left, int right) {
        int leftEdge = left;
        int rightEdge = right;
        ArmoredCarrier middle = suggestions[(left + right) / 2];

        while (leftEdge <= rightEdge) {

            while (effectiveness(leftEdge,efficiency) < effectiveness(middle,efficiency)) {
                leftEdge++;
            }

            while (effectiveness(rightEdge,efficiency) > effectiveness(middle,efficiency)) {
                rightEdge--;
            }

            if (leftEdge <= rightEdge) {
                ArmoredCarrier swap = suggestions[leftEdge];
                suggestions[leftEdge] = suggestions[rightEdge];
                suggestions[rightEdge] = swap;
                int exchange = numberOfSuggestions[leftEdge];
                numberOfSuggestions[leftEdge] = numberOfSuggestions[rightEdge];
                numberOfSuggestions[rightEdge] = exchange;
                leftEdge++;
                rightEdge--;
            }
        }

        if (leftEdge < right) {
            sort(efficiency, leftEdge, right);
        }

        if (rightEdge > left) {
            sort(efficiency, left, rightEdge);
        }
        return;
    }
}

class ArmoredCarrier {
    private int price;
    private int quality;

    public ArmoredCarrier(int price, int quality) {
        this.price = price;
        this.quality = quality;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }
}