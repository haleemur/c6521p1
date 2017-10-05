import java.util.Arrays;
import java.util.BitSet;

public class BooleanArray implements BitArray{
    private static int defaultCapacity = 16;
    private boolean[] bools;
    private int capacity;
    private int length;

    BooleanArray() {
        this(defaultCapacity);
    }


    BooleanArray(int capacity) {
        bools = new boolean[capacity];
        this.capacity = capacity;
        length = 0;
    }

    BooleanArray(boolean[] boolArray) {
        if (boolArray == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        capacity = boolArray.length;
        length = boolArray.length;
        bools = new boolean[boolArray.length];
        bools = boolArray;
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
        return bools;
    }

    @Override
    public BitSet bitSet() {
        return null;
    }

    @Override
    public void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity > capacity) {
            capacity = (int)(1.5*requiredCapacity);
            bools = Arrays.copyOf(bools, capacity);
        }
    }

    @Override
    public void add(boolean bool) {
        ensureCapacity(length + 1);
        bools[length++] = bool;
    }

    @Override
    public void setTrue(int idx) {
        ensureCapacity(idx + 1);
        bools[idx] = true;
        length = (length > idx)? length: idx;
    }

    @Override
    public int sum() {
        int n = 0;
        for (boolean b: bools) {
            n += (b? 1: 0);
        }
        return n;
    }

    @Override
    public BitArray and(BitArray other) {
        if (other.length() != length) {
            throw new IllegalArgumentException("Other Array must be of equal size");
        }
        boolean[] newBools = new boolean[capacity];
        for (int i=0;i<capacity;i++) {
            newBools[i] = bools[i] & other.array()[i];
        }
        return new BooleanArray(newBools);
    }
}
