import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FPTree {
    private Map<Character, Integer> elements;
    private List<Character> header;
    private int support;
    private Node root;
    private Map<Character, Node> lastNodes;
    private List<Character> suffix;
    private File oput;
    public int size() {
        return header.size();
    }
    public void setSuffix(List<Character> suf) {
        for (char s: suf) suffix.add(s);
    }

    public void setSuffix(char s, int i) {
        suffix.add(0, s);
    }

    FPTree(char[] symbols, int support) {
        // build the initial elements;
        elements = new HashMap<>();
        for (char i: symbols) {
            elements.put(i, 0);
        }
        this.support = support;
        suffix = new ArrayList<>();
    }

    FPTree(List<Character> symbols, int support) {
        // build the initial elements;
        elements = new HashMap<>();
        for (char i: symbols) {
            elements.put(i, 0);
        }
        this.support = support;
        suffix = new ArrayList<>();
    }

    FPTree(char[] symbols, int support, File oput) {
        // build the initial elements;
        elements = new HashMap<>();
        for (char i: symbols) {
            elements.put(i, 0);
        }
        this.support = support;
        suffix = new ArrayList<>();
        this.oput = oput;
    }

    public void readRecordPass1(char[] transaction) {
        Arrays.sort(transaction);
        char[] noDuplicates = new char[transaction.length];
        int position = 0;
        noDuplicates[position] = transaction[0];
        for (char e: transaction) {
            if (e != noDuplicates[position]) {
                noDuplicates[++position] = e;
            }
        }
        char key;
        for (int i =0; i<=position; i++) {
            key = noDuplicates[i];
            elements.put(key, elements.get(key) + 1);
        }
    }

    public void init() {
        ArrayList<Character> head
                = elements.entrySet().stream()
                .filter(x -> x.getValue() >= support)
                .sorted((x, y) -> y.getValue() - x.getValue())
                .map(Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
        init(head);
    }

    public void init(List<Character> head) {
        header = new ArrayList<>();
        for (Character c: head) {
            header.add(c);
        }
        lastNodes = new HashMap<>();

        for (Character entry: header) {
            lastNodes.put(entry, null);
        }
        root = new Node();
    }

    public void buildTree(char[] transaction) {
        boolean contains;
        ArrayList<Character> noDuplicates = new ArrayList<>();
        for (char h: header) {
            contains = false;
            for (char t: transaction) {
                if (t == h) {
                    contains = true;
                    break;
                }
            }
            if (contains) {
                noDuplicates.add(h);
            }
        }
        root.addChildren(noDuplicates, lastNodes);
    }

    public void buildTree(List<Character> path, int count) {
        root.addChildren(path, lastNodes, count);
    }

    public void removeLowSupport() {
        char e;
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

    public Integer getSumCounts(char e) {
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
        char e;
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
        char e;
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

    public Node getLastNodes(Character c) {
        return lastNodes.get(c);
    }

    public Character getHeaderElement(int i) {
        return header.get(i);
    }

    public Set<Character> getNodeKeys() {
        return lastNodes.keySet();
    }

    public List<Character> getSuffix() {
        return suffix;
    }

    public List<Character> getHeader() {
        return header;
    }

    public int getSupport() {
        return support;
    }

    public Node getRoot() {
        return root;
    }

}
