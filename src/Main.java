/**
 * The main file launches the program
 * the program accepts 1 argument that must be
 * a directory path
 */
import java.io.*;


public class Main {


    private static void print(String msg) {
        System.err.println(msg);
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            print("Program only accepts 1 argument: Usage java Main <directory_path>");
            System.exit(1);
        }

        File dir = new File(args[0]);
        if (!dir.isDirectory()) {
            print("'" + args[0] + "' is not a valid directory");
            System.exit(1);
        } else {
            for (File file : dir.listFiles()) {
                if (!file.isDirectory()) {
                    Thread t = new Thread(new BasketReader(file));
                    t.start();
                }
            }
        }
    }
}
