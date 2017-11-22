import java.io.*;


public class FrequentItemset implements Runnable {
    private File file;
    private static int[] elements = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
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
            long start = System.currentTimeMillis();
            MemoryMapReader buff = new MemoryMapReader(file, 2048);
            int limit = Integer.parseInt(String.valueOf(buff.readLine()));
            int position = buff.position();
            FPTree tree = new FPTree(elements, limit, new File(outName));
            int[] record;
            while ((record = buff.readRecord('\t', ',')) != null ) {
                tree.readRecordPass1(record);
            }
            tree.init();
            buff.seek(position);
            while ((record = buff.readRecord('\t', ',')) != null) {
                tree.buildTree(record);
            }
            buff.close();

            tree.frequentSets(false);

//            System.out.println(file.getName());
//            System.out.println("total = " + (System.currentTimeMillis() - start));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
