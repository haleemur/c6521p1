import java.util.BitSet;

interface BitArray {
    int capacity();
    int length();
    boolean[] array();
    BitSet bitSet();
    void ensureCapacity(int requiredCapacity);
    void add(boolean bool);
    void setTrue(int idx);
    int sum();
    BitArray and(BitArray other);
}
