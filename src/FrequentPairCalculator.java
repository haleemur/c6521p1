import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FrequentPairCalculator implements Runnable {
    private Character ns;
    private FPTree tree;
    private boolean recursive;
    private BufferedWriter bw;
    private String fname;

    FrequentPairCalculator(BufferedWriter writer, Character ns, FPTree tree, boolean recursive, String fname) throws IOException {
        this.ns = ns;
        this.tree = tree;
        this.recursive = recursive;
        bw = writer;
        this.fname = fname;
    }

    private FPTree buildConditionalPrefixTree(Character ns, FPTree oldTree) {
        ArrayList<Character> rkBuilder;
        Node parent, node;
        node = oldTree.getLastNodes(ns);
        FPTree tree = new FPTree(oldTree.getHeader().subList(0,oldTree.getHeader().indexOf(ns)), oldTree.getSupport());
        tree.init(oldTree.getHeader().subList(0,oldTree.getHeader().indexOf(ns)));

        tree.setSuffix(oldTree.getSuffix());
        tree.setSuffix(ns, 0);
        do {
            parent = node.parent();
            if (parent == oldTree.getRoot()) continue;
            rkBuilder = new ArrayList<>();
            rkBuilder.add(parent.getSymbol());
            while ((parent = parent.parent()) != oldTree.getRoot()) {
                rkBuilder.add(0, parent.getSymbol());
            }
            tree.buildTree(rkBuilder, node.getCount());
        } while ((node = node.next()) != null);
        tree.removeLowSupport();
        return tree;
    }

    private FPTree conditionalPrefix(Character ns, FPTree subTree) throws IOException {

            StringBuilder bldr;
            FPTree prefixTree = buildConditionalPrefixTree(ns, subTree);
            for (Character prefix : prefixTree.getNodeKeys()) {
                bldr = new StringBuilder();
                bldr.append("{");
                bldr.append(prefix);
                for (Character k : prefixTree.getSuffix()) {
                    bldr.append(",");
                    bldr.append(k);
                }
                bldr.append("}");
                synchronized(this) {bw.write(bldr.toString() + "\n");}
            }
            return prefixTree;
    }
    private void conditionalPrefix(Character ns, FPTree subTree, boolean recursive) throws IOException{
        FPTree prefixTree = conditionalPrefix(ns, subTree);
        if (recursive && prefixTree.size() > 1) {
            Character c;
            for (int i = prefixTree.getHeader().size()-1; i > 0; i--) {
                c = prefixTree.getHeaderElement(i);
                conditionalPrefix(c, prefixTree);
            }
        }
    }

    @Override
    public void run() {
        try {
            conditionalPrefix(ns, tree, recursive);
            bw.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
