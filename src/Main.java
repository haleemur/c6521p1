/**
 * The main file launches the program
 * the program accepts 1 argument that must be
 * a directory path
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class Main {
    static long start;
    static File dir;
    static boolean parallel = false;

    private static void parseArgs(String[] args) {
        if (args.length == 0 || args.length > 2) {
            System.out.println("Program only accepts 1 required  argument & 1 optional argument: ");
            System.out.println("Usage:\n\t java Main <directory_path> [parallel|P]");
            System.exit(1);
        }

        dir = new File(args[0]);
        if (args.length == 2) {
            if (args[1].equals("parallel") || args[1].equals("P")) {
                parallel = true;
            }
        }
        if (!dir.isDirectory()) {
            System.out.println("'" + args[0] + "' is not a valid directory");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        start = System.currentTimeMillis();
        parseArgs(args);
        process();
        System.out.printf("total execution time: %.3f (seconds)", (System.currentTimeMillis()-start) / 1000.0);

    }

    private static void process() {

        List<File> files = new ArrayList<>();
        for(File f: dir.listFiles()) {
            if (f.isFile()) files.add(f);
        }
        ExecutorService executor = parallel ? Executors.newWorkStealingPool(): Executors.newSingleThreadExecutor();
        for (File file: files) {
            executor.submit(new FrequentItemset(file));
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
    }
}
