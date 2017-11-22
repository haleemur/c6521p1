import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FrequentItemsetExtractor implements Runnable {
    private Integer ns;
    private FPTree tree;
    private boolean all_nsets;
    private final BufferedWriter bw;

    FrequentItemsetExtractor(BufferedWriter writer, Integer ns, FPTree tree, boolean all_nsets) throws IOException {
        this.ns = ns;
        this.tree = tree;
        this.all_nsets = all_nsets;
        bw = writer;
    }

    private FPTree buildConditionalPrefixTree(Integer ns, FPTree oldTree) {
        ArrayList<Integer> rkBuilder;
        Node parent, node, root;
        node = oldTree.getLastNodes(ns);
        root = oldTree.getRoot();
        int newHeaderSize = oldTree.getHeader().indexOf(ns);
        FPTree tree = new FPTree(oldTree.getHeader().subList(0,newHeaderSize), oldTree.getSupport(), oldTree.getSuffix());
        tree.init(oldTree.getHeader().subList(0,newHeaderSize));

        tree.addSuffix(ns);
        do {
            parent = node.parent();
            if (parent == oldTree.getRoot()) continue;
            rkBuilder = new ArrayList<>();
            rkBuilder.add(parent.getSymbol());
            while ((parent = parent.parent()) != root) {
                rkBuilder.add(0, parent.getSymbol());
            }
            tree.buildTree(rkBuilder, node.getCount());
        } while ((node = node.next()) != null);
        tree.removeLowSupport();
        return tree;
    }

    private FPTree conditionalPrefix(Integer ns, FPTree subTree) throws IOException {

            StringBuilder builder;
            FPTree prefixTree = buildConditionalPrefixTree(ns, subTree);
            for (Integer prefix : prefixTree.getNodeKeys()) {
                builder = new StringBuilder("{");
                builder.append(prefix);
                for (Integer k : prefixTree.getSuffix()) {
                    builder.append(",");
                    builder.append(k);
                }
                builder.append("}");
                synchronized (bw) {
                    bw.write(builder.toString());
                    bw.newLine();
                }
            }
            return prefixTree;
    }

    private void conditionalPrefix(Integer ns, FPTree subTree, boolean recursive) throws IOException{
        FPTree prefixTree = conditionalPrefix(ns, subTree);
        if (recursive && prefixTree.size() > 1) {
            Integer c;
            for (int i = prefixTree.getHeader().size()-1; i > 0; i--) {
                c = prefixTree.getHeaderElement(i);
                conditionalPrefix(c, prefixTree);
            }
        }
    }

    @Override
    public void run() {
        try {
            conditionalPrefix(ns, tree, all_nsets);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
