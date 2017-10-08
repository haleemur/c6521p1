package javaapplication1;
import java.io.*;
import java.util.*;
/**
 *
 * @author d_masyut
 */
public class PCY {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        long startTime = System.currentTimeMillis();
        String filename = null;
        String myDirectory ="C:\\Temp\\JavaApplication1\\src\\Sample"; //set directory with Sample text files
        File dir = new File(myDirectory);
        File[] directoryListing = dir.listFiles();
        
        if (directoryListing !=null){
            for (File p:directoryListing){
                filename = p.getName();                         //get name of the file - it will be used to name the output file with the solution
                filename = filename.substring(0, filename.length() - 4);  
                
        int[] frequent_items = new int[10];                     //table that keeps track number each singleton appears in the buckets
        int support_level, mod;                                 //support_level -minium support level:this value is read from the file; mod - result of modulo division for each pair
        HashMap <Integer,Integer> buckets = new HashMap<>(10);  // create HashMap which will be used in the PCY algorithm
        for (int x=0;x<=9;x++){                                 // initialize HashMap values with zero
            buckets.put(x,0);
        }
     
        Scanner scanner = new Scanner(p);
        String support = scanner.next();
        support_level = Integer.parseInt(support);
        
        //1st pass of the algorithm
        while (scanner.hasNext()) {                             
            String number = scanner.next();                     
            String[] numbers = number.split(",");
            int[] intValues = new int[numbers.length-1];
            int indx=0;
            for (int x=1;x<numbers.length;x++){                 // reads bucket by bucket from the file
                intValues[indx++]=Integer.parseInt(numbers[x]); //places each read bucket into array
                } 
            for (int i=0;i<intValues.length;i++){
                frequent_items[intValues[i]]++;                 //count occurence of frequent singletons
                for (int j=i+1;j<intValues.length;j++){         //generates pairs of items 
                    mod = (intValues[i]+intValues[j]) % 10;           
                    buckets.put(mod,buckets.get(mod)+1);
                    
                }
            }
        }
        scanner.close();

       PrintWriter solution = new PrintWriter(filename+"_Solution.txt","UTF-8");
        for (int i=0;i<10;i++){
            for (int j=i+1;j<10;j++){
                if (frequent_items[i]>=support_level && frequent_items[j]>=support_level && buckets.get((i+j) % 10)>=support_level){
                    solution.println("{"+Integer.toString(i)+","+Integer.toString(j)+"}");
                }
            }
        }
    solution.close();
    }
        }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    System.out.println("Total execution time:"+elapsedTime);
    }  
}