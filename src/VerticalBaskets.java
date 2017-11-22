/**
 * INCOMPLETE
 */

import java.util.*;
import java.util.stream.Collectors;

public class VerticalBaskets {
    private int next = 0;
    private int supportLevel;
    private HashMap<String, BitArray> data;

    private ArrayList<AbstractMap.SimpleImmutableEntry<String, Integer>> header;
    private HashMap<String, Integer> itemsetSupport;

    VerticalBaskets(char[] keys, int initialCapacity) {
        itemsetSupport = new HashMap<>();
        header = new ArrayList<>();
        data = new HashMap<>();
        for (char key: keys) {
            data.put(String.valueOf(key), new BitSetArray(initialCapacity));
        }
    }

    public void setMinSupportLevel(int minSupportLevel) {
        supportLevel = minSupportLevel;
    }

    public void setNext(char[] record) {
        for (char el: record) {
            data.get(String.valueOf(el)).setTrue(next);
        }
        next++;
    }

    public void makeHeader() {
        int support;
        for (Map.Entry<String, BitArray> entry : data.entrySet()) {
            support = entry.getValue().sum();
            if (support >= supportLevel) {
                 header.add(new AbstractMap.SimpleImmutableEntry(entry.getKey(), support));
            }
        }
        header.sort(Comparator.comparing(AbstractMap.SimpleImmutableEntry::getValue));
        Collections.reverse(header);
    }

    public static void printItemSetToString(char[] bytes) {
        for (char b: bytes) System.out.print(b);
    }

    public static void printItemSetToString(Character[] bytes) {
        for (Character b: bytes) System.out.print(b);
    }

    public void printHeader() {
        System.out.println("method: VerticalBaskets.printHeader");
        for (Map.Entry<String, Integer> el : header) {
            System.out.println(el.getKey() + ": " + el.getValue());
        }
    }

    public void makeItemSupport() {
        String bytes;
        StringBuilder builder;
        BitArray bitArray;
        int i, j, sum;

        ArrayList<String> singleElements = header.stream()
                .map(AbstractMap.SimpleImmutableEntry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));

        HashMap<Integer, ArrayList<String>> nSets = new HashMap<>();
        for (i = 0; i < singleElements.size(); i++) {
            nSets.put(i+1, new ArrayList<>());
        }

        for (AbstractMap.SimpleImmutableEntry<String, Integer> el: header) {
            sum = el.getValue();
            bytes = String.valueOf(el.getKey());
            nSets.get(1).add(bytes);
            itemsetSupport.put(bytes, sum);
        }
        for (int k=1; k < singleElements.size(); k++) {
            for (String comb: nSets.get(k)) {
                for (j=singleElements.indexOf(String.valueOf(comb.charAt(k-1))); j < singleElements.size()-1; j++) {
                    bitArray = data.get(comb).and(data.get(singleElements.get(j+1)));
                    sum = bitArray.sum();
                    if (sum > supportLevel) {
                        builder = new StringBuilder(comb);
                        builder.insert(k, singleElements.get(j+1));
                        bytes = builder.toString();
                        nSets.get(k+1).add(bytes);
                        data.put(bytes, bitArray);
                        itemsetSupport.put(bytes, sum);
                    }
                }
            }
        }
    }
}
