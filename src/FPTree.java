import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FPTree {
    private Map<Integer, Integer> elements;
    private List<Integer> header;
    private int support;
    private Node root;
    private Map<Integer, Node> lastNodes;
    private Deque<Integer> suffix;
    private File oput;
    public int size() {
        return header.size();
    }

    public void addSuffix(int s) {
        suffix.push(s);
    }

    FPTree(List<Integer> symbols, int support, Deque<Integer> suffix) {
        // build the initial elements;
        elements = new HashMap<>();
        for (int i: symbols) {
            elements.put(i, 0);
        }
        this.support = support;
        this.suffix = new ArrayDeque<>(suffix);
    }


    FPTree(int[] symbols, int support, File oput) {
        // build the initial elements;
        elements = new HashMap<>();
        for (int i: symbols) {
            elements.put(i, 0);
        }
        this.support = support;
        suffix = new ArrayDeque<>();
        this.oput = oput;
    }

    public void readRecordPass1(int[] transaction) {
        Arrays.sort(transaction);
        int[] noDuplicates = new int[transaction.length];
        int position = 0;
        noDuplicates[position] = transaction[0];
        for (int e: transaction) {
            if (e != noDuplicates[position]) {
                noDuplicates[++position] = e;
            }
        }
        for (int i =0; i<=position; i++) {
            elements.put(noDuplicates[i], elements.get(noDuplicates[i]) + 1);
        }
    }

    public void init() {
        header = new ArrayList<>(elements.size());
        lastNodes = new HashMap<>();
        for (Entry<Integer, Integer> elem: elements.entrySet()) {
            if (elem.getValue() >= support) {
                header.add(elem.getKey());
                lastNodes.put(elem.getKey(), null);
            }
        }
        root = new Node();
    }

    public void init(List<Integer> head) {
        header = new ArrayList<>(head);
        lastNodes = new HashMap<>();
        for (int entry: header) {
            lastNodes.put(entry, null);
        }
        root = new Node();
    }

    public void buildTree(int[] transaction) {
        int[] noDuplicates = new int[header.size()];
        int noDuplicateLength = 0;
        for (int h: header) {
            for (int t: transaction) {
                if (t == h) {
                    noDuplicates[noDuplicateLength++] = h;
                    break;
                }
            }
        }
        if (noDuplicateLength > 0) {
            root.addChildren(noDuplicates, noDuplicateLength, lastNodes);
        }
    }

    public void buildTree(List<Integer> path, int count) {
        root.addChildren(path, lastNodes, count);
    }

    public void removeLowSupport() {
        int e, sum;
        Node node;
        for (int i = header.size() - 1; i >= 0; i--) {
            e = header.get(i);
            sum = getSumCounts(e);
            node = lastNodes.get(e);
            if (sum < support) {
                Node parent = node.parent();
                for (Node child: node.getChildren()) {
                    child.setParent(parent);
                }
                lastNodes.remove(e);
                header.remove(i);
            }
        }
    }

    public int getSumCounts(int e) {
        Node node = lastNodes.get(e);
        if (node == null)
            return 0;

        int sum = node.getCount();
        while ((node = node.next()) != null) {
            sum += node.getCount();
        }
        return sum;
    }

    public void frequentSets(boolean all_nsets) throws IOException {
        int e;
        int sum;
        ExecutorService executor = Executors.newWorkStealingPool();
//        FrequentItemsetExtractor f;
        BufferedWriter bw;
        if (!oput.exists()) {
            oput.createNewFile();
        }
        bw = new BufferedWriter(new FileWriter(oput.getAbsoluteFile(), true));
        for (int i = header.size() - 1; i > 0; i--) {
            e = header.get(i);
            sum = getSumCounts(e);
            if (sum < support) continue;
            executor.submit(new FrequentItemsetExtractor(bw, e, this, false));
//            f = new FrequentItemsetExtractor(bw, e, this, all_nsets);
//            f.run();
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
    }

    public Node getLastNodes(Integer c) {
        return lastNodes.get(c);
    }

    public int getHeaderElement(int i) {
        return header.get(i);
    }

    public Set<Integer> getNodeKeys() {
        return lastNodes.keySet();
    }

    public Deque<Integer> getSuffix() {
        return suffix;
    }

    public List<Integer> getHeader() {
        return header;
    }

    public int getSupport() {
        return support;
    }

    public Node getRoot() {
        return root;
    }

}
