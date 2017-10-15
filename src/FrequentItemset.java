import java.io.*;


public class FrequentItemset implements Runnable {
    private File file;
    private static int[] elements = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
//    static char[] elements = {'a', 'b', 'c', 'd', 'e'};
    private String outName;
    FrequentItemset(File file) {
        this.file = file;
        File outdir = new File(this.file.getParent() + "/output/");
        outdir.mkdir();
        outName = this.file.getParent() + "/output/" + this.file.getName().replace("input", "output");
    }

    private void printRecord(int[] record) {
        for (int c: record) System.out.print(c + " ");
        System.out.println();
    }
    @Override
    public void run()  {
        try {
            MemoryMapReaderInt buff = ReaderFactory.getIntReader(file);
            int limit = Integer.parseInt(String.valueOf(buff.readLine()));
            System.out.println(limit);
            int position = buff.position();
            FPTree tree = new FPTree(elements, limit, new File(outName));
            int[] record;
            while ((record = buff.readRecord('\t', ',')) != null ) {
                tree.readRecordPass1(record);
//                printRecord(record);
            }
            tree.init();
            buff.seek(position);
            while ((record = buff.readRecord('\t', ',')) != null) {
                tree.buildTree(record);
            }
            buff.close();
            tree.frequentPairs();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
