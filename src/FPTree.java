import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FPTree {
    private Map<Integer, Integer> elements;
    private List<Integer> header;
    private int support;
    private Node root;
    private Map<Integer, Node> lastNodes;
    private List<Integer> suffix;
    private File oput;
    public int size() {
        return header.size();
    }
    public void setSuffix(List<Integer> suf) {
        for (int s: suf) suffix.add(s);
    }

    public void setSuffix(int s, int i) {
        suffix.add(0, s);
    }



    FPTree(List<Integer> symbols, int support) {
        // build the initial elements;
        elements = new HashMap<>();
        for (int i: symbols) {
            elements.put(i, 0);
        }
        this.support = support;
        suffix = new ArrayList<>();
    }

    FPTree(int[] symbols, int support, File oput) {
        // build the initial elements;
        elements = new HashMap<>();
        for (int i: symbols) {
            elements.put(i, 0);
        }
        this.support = support;
        suffix = new ArrayList<>();
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
        ArrayList<Integer> head
                = elements.entrySet().stream()
                .filter(x -> x.getValue() >= support)
                .sorted((x, y) -> y.getValue() - x.getValue())
                .map(Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
        init(head);
    }

    public void init(List<Integer> head) {
        header = new ArrayList<>();
        for (int c: head) {
            header.add(c);
        }
        lastNodes = new HashMap<>();

        for (int entry: header) {
            lastNodes.put(entry, null);
        }
        root = new Node();
    }

    public void buildTree(int[] transaction) {
        ArrayList<Integer> noDuplicates = new ArrayList<>();
        for (int h: header) {
            for (int t: transaction) {
                if (t == h) {
                    noDuplicates.add(h);
                    break;
                }
            }
        }
        root.addChildren(noDuplicates, lastNodes);
    }

    public void buildTree(List<Integer> path, int count) {
        root.addChildren(path, lastNodes, count);
    }

    public void removeLowSupport() {
        int e;
        Node node;
        Integer sum;
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

    public Integer getSumCounts(int e) {
        Node node = lastNodes.get(e);
        if (node == null)
            return null;

        int sum = node.getCount();
        while ((node = node.next()) != null) {
            sum += node.getCount();
        }
        return sum;
    }

    public void frequentTuples() throws IOException {
        int e;
        Integer sum;
        ExecutorService executor = Executors.newWorkStealingPool();
        ArrayList<BufferedWriter> writers = new ArrayList<>();
        BufferedWriter bw;
        for (int i = header.size() - 1; i > 0; i--) {
            e = header.get(i);
            sum = getSumCounts(e);
            if (sum == null) continue;
            if (sum >= support) {
                if (sum >= support) {
                    if (!oput.exists()) {
                        oput.createNewFile();
                    }
                    bw = new BufferedWriter(new FileWriter(oput.getAbsoluteFile(), true));
                    executor.submit(new FrequentPairCalculator(bw, e, this, true, oput.getName()));
                }
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        for (BufferedWriter writer: writers) {
            writer.close();
        }
    }

    public void frequentPairs() throws IOException {
        int e;
        Integer sum;
        ExecutorService executor = Executors.newWorkStealingPool();
        ArrayList<BufferedWriter> writers = new ArrayList<>();
        BufferedWriter bw;
        for (int i = header.size() - 1; i > 0; i--) {
            e = header.get(i);
            sum = getSumCounts(e);
            if (sum == null) continue;
            if (sum >= support) {
                if (!oput.exists()) {
                    oput.createNewFile();
                }
                bw = new BufferedWriter(new FileWriter(oput.getAbsoluteFile(), true));
                executor.submit(new FrequentPairCalculator(bw, e, this, false, oput.getName()));
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        for (BufferedWriter writer: writers) {
            writer.close();
        }
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

    public List<Integer> getSuffix() {
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
