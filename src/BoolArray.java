import java.util.Arrays;

public class BoolArray {
    private boolean[] bools;
    private int capacity;
    private int length;
    private int defaultCapacity = 16;
    BoolArray() {
        this(10);
    }


    BoolArray(int capacity) {
        bools = new boolean[capacity];
        this.capacity = capacity;
        length = 0;
    }

    BoolArray(boolean[] boolArray) {
        if (boolArray == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        capacity = boolArray.length;
        length = boolArray.length;
        bools = new boolean[boolArray.length];
        bools = boolArray;
    }

    public int capacity() {
        return capacity;
    }

    public int length() {
        return length;
    }

    public boolean[] array() {
        return bools;
    }

    public void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity > capacity) {
            bools = Arrays.copyOf(bools, 2*requiredCapacity);
        }
    }

    public void add(boolean bool) {
        ensureCapacity(length + 1);
        bools[length++] = bool;
    }

    public void setTrue(int idx) {
        ensureCapacity(idx + 1);
        bools[idx] = true;
    }

    public int sum() {
        int n = 0;
        for (boolean b: bools) {
            n += (b? 1: 0);
        }
        return n;
    }

    public BoolArray and(BoolArray other) {
        if (other.length() != length) {
            throw new IllegalArgumentException("Other Array must be of equal size");
        }
        boolean[] newBools = new boolean[capacity];
        for (int i=0;i<capacity;i++) {
            newBools[i] = bools[i] & other.array()[i];
        }
        return new BoolArray(newBools);
    }
}
