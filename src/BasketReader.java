/**
 * Created by hal on 2017-09-27.
 *
 * The reader instance takes a file file
 * and reads it.
 * If the file file is invalid or the file
 * is not in the right format, it gets angry
 *
 * very angry.
 */


import java.io.*;
import java.util.*;


public class BasketReader implements Runnable {
    private File file;
    private HashMap<Byte, BitArray> data;

    BasketReader(File file) {
        this.file = file;
        long size = file.length();
        System.out.println(file.getName() + " " + size);
        int initialCapacity = (int)(size / 15);

    }

    private void assign(byte[] record, int index) {
        for (byte el: record) {
            data.get(el).setTrue(index);
        }
    }

    private void printBaskets() {

    }

    private void printRecord(byte[] record) {
        for (byte b: record) {
            System.out.print(((char)b) + " ");
        }
        System.out.println();
    }


    private void parseData() {
        try {
            DataReader buff = ReaderFactory.getMemoryMapReader(file);
            int limit = Integer.parseInt(new String(buff.readLine()));
            byte[] record;
            int i=0;
            while ((record = buff.readRecord(',')) != null) {
                assign(record, i++);
            }
            buff.close();

        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void printCounts() {
        for (Map.Entry<Byte, BitArray> pair : data.entrySet()) {
            System.out.println((char)pair.getKey().byteValue() + ": " + pair.getValue().sum());
        }
    }

    @Override
    public void run()  {
        parseData();
//        printCounts();
        System.out.println(file.getName() + " finished");
    }
}
