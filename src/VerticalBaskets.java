/**
 * INCOMPLETE
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class VerticalBaskets {
    private int next = 0;
    private int supportLevel;
    private HashMap<Byte, BitArray> data;
    private HashMap<Set<Byte>, Integer> itemsetSupport;

    VerticalBaskets(byte[] keys, int initialCapacity) {
        data = new HashMap<>();
        for (byte k: keys) {
            data.put(k, new BitSetArray(initialCapacity));
        }
        Object[] elements = data.keySet().toArray();
        Arrays.sort(elements);
    }

    public void setMinSupportLevel(int minSupportLevel) {
        supportLevel = minSupportLevel;
    }

    private void setNext(byte[] record) {
        for (byte el: record) {
            data.get(el).setTrue(next);
        }
        next++;
    }

    private void calculateSupportLevel() {
        Object[] keys = data.keySet().toArray();
        Arrays.sort(keys);
        int support;
        for (int i=0; i<keys.length; i++) {
            if ((support = data.get(keys[i]).sum()) > supportLevel) {

            }
        }

    }
}
