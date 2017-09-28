/**
 * Created by hal on 2017-09-27.
 *
 * The reader instance takes a file path
 * and reads it.
 * If the file path is invalid or the file
 * is not in the right format, it gets angry
 *
 * very angry.
 */

import java.io.*;
import java.util.*;

public class Reader implements Runnable {
    File path;
    Reader(File p) {
        path = p;
    }

    @Override
    public void run()  {
        ArrayList<Boolean> zeroes = new ArrayList<>();
        ArrayList<Boolean> ones = new ArrayList<>();
        ArrayList<Boolean> twos = new ArrayList<>();
        ArrayList<Boolean> threes = new ArrayList<>();
        ArrayList<Boolean> fours = new ArrayList<>();
        ArrayList<Boolean> fives = new ArrayList<>();
        ArrayList<Boolean> sixes = new ArrayList<>();
        ArrayList<Boolean> sevens = new ArrayList<>();
        ArrayList<Boolean> eights = new ArrayList<>();
        ArrayList<Boolean> nines = new ArrayList<>();



        System.out.println("Processing File `" + path.toString() + "`");
        String basket;

        try {
            Scanner in = new Scanner(path);
            in.useDelimiter("\n");
            String limit = in.next();
            System.out.println("Support Limit: " + limit);

            in.useDelimiter("\t");
            while(in.hasNext()) {
                basket = in.next();
                String[] basket_elems = basket.split(",");
                for (String el : basket_elems) {
                    System.out.print(el);
                    System.out.print(" ");
                }
                System.out.print("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found. Skipping...");
        }
    }
}
