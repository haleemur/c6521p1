import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void addChildren(List<Integer> symbols, Map<Integer, Node> nextNodes) {

        Node child = addChild(symbols.get(0), nextNodes);

        if (symbols.size() > 1) {
            child.addChildren(symbols.subList(1, symbols.size()), nextNodes);
        }
    }

    public void addChildren(List<Integer> symbols, Map<Integer, Node> nextNodes, int count) {

        Node child = addChild(symbols.get(0), nextNodes, count);

        if (symbols.size() > 1) {
            child.addChildren(symbols.subList(1, symbols.size()), nextNodes, count);
        }
    }

    public ArrayList<Node> getChildren() {
        return children.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toCollection(ArrayList::new));
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
