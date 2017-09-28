import java.io.*;

/**
 * The main file launches the program
 * the program accepts 1 argument that must be
 * a directory path
 */
public class Main {


    private static void print(String msg) {
        System.err.println(msg);
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            print("Program only accepts 1 argument: Usage java Main <directory_path>");
            System.exit(1);
        }

        File f = new File(args[0]);
        if (!f.isDirectory()) {
            print("'" + args[0] + "' is not a valid directory");
            System.exit(1);
        } else {
            for (File p : f.listFiles()) {
                Thread t = new Thread(new Reader(p));
                t.start();
            }
        }


    }
}
