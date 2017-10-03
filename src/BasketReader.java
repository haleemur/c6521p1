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
import java.util.stream.Collectors;


public class BasketReader implements Runnable {
    private File file;
    private HashMap<Byte, BoolArray> data;
    BasketReader(File file) {
        this.file = file;
        long size = file.length();
        System.out.println(file.getName() + " " + size);
        int initialCapacity = 10;
        data = new HashMap<Byte, BoolArray>();
        data.put((byte)'0', new BoolArray(initialCapacity));
        data.put((byte)'1', new BoolArray(initialCapacity));
        data.put((byte)'2', new BoolArray(initialCapacity));
        data.put((byte)'3', new BoolArray(initialCapacity));
        data.put((byte)'4', new BoolArray(initialCapacity));
        data.put((byte)'5', new BoolArray(initialCapacity));
        data.put((byte)'6', new BoolArray(initialCapacity));
        data.put((byte)'7', new BoolArray(initialCapacity));
        data.put((byte)'8', new BoolArray(initialCapacity));
        data.put((byte)'9', new BoolArray(initialCapacity));
    }

    private void assign(Set<Byte> elems) {
        for (byte i: elems) {
            for (byte j=0; j<data.size(); j++) {
                data.get(j).add(j == i);
            }
        }
    }

    private void assign(byte[] record, int index) {
        for (byte el: record) {
            data.get(el).setTrue(index);
        }
    }
    private Set<Byte> get_byte_set(String basket) {
        return Arrays.stream(basket.substring(basket.indexOf(',') + 1).split(","))
                .map(Byte::parseByte)
                .collect(Collectors.toSet());
    }

    private void parseBufferedReader() {
        try {
            BufferedReader2 buff = new BufferedReader2(new FileReader(file));
            int limit = Integer.parseInt(buff.readLine());
            String line;
            while ((line = buff.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("File Not Found. Skipping...");
        }
    }

    private void parseNIOReader() {
        try {
            NIOReader buff = new NIOReader(file);
            int limit = Integer.parseInt(new String(buff.readLine()));
            byte[] record;
            int i=0;
            while ((record = buff.readRecord(',')) != null) {
                assign(record, i++);
            }
            buff.close();

        } catch(IOException e) {
            System.out.println("File Not Found. Skipping...");
        }
    }

    @Override
    public void run()  {
        parseNIOReader();
//        parseBufferedReader();
    }
}
