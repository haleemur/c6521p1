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

public class Reader implements Runnable {
    File path;
    Reader(File p) {
        path = p;
    }

    @Override
    public void run() {
        System.out.println("Processing File `" + path.toString() + "`");
    }
}
