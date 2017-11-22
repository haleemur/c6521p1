import java.util.Arrays;
import java.util.BitSet;

public class BitSetArray implements BitArray {
    private static int defaultCapacity = 16;
    private BitSet bools;
    private int capacity;
    private int length;


    BitSetArray() {
        this(defaultCapacity);
    }

    BitSetArray(int capacity) {
        bools = new BitSet(capacity);
        this.capacity = capacity;
        length = 0;
    }

    BitSetArray(BitSet bitSet) {
        if (bitSet == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        capacity = bitSet.size();
        bools = new BitSet(capacity);
        bools.or(bitSet);
        length = bitSet.length();
    }


    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public boolean[] array() {
        byte[] bytes =bools.toByteArray() ;
        boolean[] result = new boolean[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i] > 0;
        }
        return result;
    }

    @Override
    public BitSet bitSet() {
        return bools;
    }

    @Override
    public void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity > capacity) {
            capacity = (int)(1.5*requiredCapacity);
            BitSet tmp = new BitSet(capacity);
            tmp.or(bools);
            bools = tmp;
        }
    }

    @Override
    public void add(boolean bool) {
        ensureCapacity(length+1);
        bools.set(length++);
    }

    @Override
    public void setTrue(int idx) {
        ensureCapacity(idx+1);
        bools.set(idx);
        length = idx;
    }

    @Override
    public int sum() {
        return bools.cardinality();
    }

    @Override
    public BitArray and(BitArray other) {
        BitSet cloned = bools;
        cloned.and(other.bitSet());
        return new BitSetArray(cloned);
    }
}
