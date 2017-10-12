import java.io.*;


public class FrequentItemset implements Runnable {
    private File file;
    private static char[] elements = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
//    static char[] elements = {'a', 'b', 'c', 'd', 'e'};
    private String outName;
    FrequentItemset(File file) {
        this.file = file;
        File outdir = new File(this.file.getParent() + "/output/");
        outdir.mkdir();
        outName = this.file.getParent() + "/output/" + this.file.getName().replace("input", "output");
    }

    private void printRecord(char[] record) {
        for (char c: record) System.out.print(c + " ");
        System.out.println();
    }
    @Override
    public void run()  {
        try {
            DataReader buff = ReaderFactory.getMemoryMapReader(file);
            int limit = Integer.parseInt(new String(buff.readLine()));
            int position = buff.position();
            FPTree tree = new FPTree(elements, limit, new File(outName));
            char[] record;
            while ((record = buff.readRecord(',')) != null ) {
                tree.readRecordPass1(record);
            }
            tree.init();
            buff.seek(position);
            while ((record = buff.readRecord(',')) != null) {
                tree.buildTree(record);
            }
            buff.close();
            tree.frequentPairs();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
