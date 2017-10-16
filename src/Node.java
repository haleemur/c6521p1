import java.util.*;
import java.util.stream.Collectors;

public class Node {
    public Integer getSymbol() {
        return symbol;
    }

    public int getCount() {
        return count;
    }

    private Integer symbol;
    private int count;
    private Node parent = null;
    private Node next;
    private Map<Integer, Node> children;

    public void next(Node next) {
        this.next = next;
    }

    public Node next() {
        return next;
    }

    public Node parent() {
        return parent;
    }

    Node() { children = new HashMap<>();}

    Node(Integer symbol, Node parent, Map<Integer, Node> nextNodes) {
        this(symbol, parent, nextNodes, 1);

    }
    Node(Integer symbol, Node parent, Map<Integer, Node> nextNodes, int count) {
        this.symbol = symbol;
        this.count = count;
        this.parent = parent;
        this.next = nextNodes.get(symbol);
        nextNodes.put(symbol, this);
        children = new HashMap<>();

    }


    public void increment (int n) {
        count += n;
    }
    public void decrement (int n) {
        count -= n;
    }

    public Node addChild(Integer symbol, Map<Integer, Node> nextNodes) {
        if (children.get(symbol) == null) {
            children.put(symbol, new Node(symbol, this, nextNodes));
        } else {
            children.get(symbol).increment(1);
        }

        return children.get(symbol);
    }

    public Node addChild(Integer symbol, Map<Integer, Node> nextNodes, int count) {
        if (children.get(symbol) == null) {
            children.put(symbol, new Node(symbol, this, nextNodes, count));
        } else {
            children.get(symbol).increment(count);
        }

        return children.get(symbol);
    }

    public void addChildren(int[] symbols, int length, Map<Integer, Node> nextNodes) {
        int i=0;
        Node child;
        child = addChild(symbols[i++], nextNodes);
        while(i < length) {
            child  = child.addChild(symbols[i++], nextNodes);
        }
    }

    public void addChildren(List<Integer> symbols, Map<Integer, Node> nextNodes, int count) {
        int i=0;
        Node child;
        child = addChild(symbols.get(i++), nextNodes, count);
        while(i < symbols.size()) {
            child  = child.addChild(symbols.get(i++), nextNodes, count);
        }
    }

    public Collection<Node> getChildren() {
        return children.values();
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
