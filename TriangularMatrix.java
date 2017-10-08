package javaapplication1;

import java.io.*;
import java.util.*;
/**
 *
 * @author d_masyut
 */
public class TriangularMatrix {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        long startTime = System.currentTimeMillis();
        String filename = null;
        int support_level = 0, size=10; // size is a dimension of triangular matrix
        
        String myDirectory ="C:\\Temp\\JavaApplication1\\src\\Sample"; //set directory with Sample text files
            
        File dir = new File(myDirectory);
        File[] directoryListing = dir.listFiles();
        if (directoryListing !=null){
            for (File p:directoryListing){
        int[][] matrix = new int[size][size];
        filename = p.getName();
        filename = filename.substring(0, filename.length() - 4);        
        Scanner scanner = new Scanner(p);
          
        String support = scanner.next();
        support_level = Integer.parseInt(support);
        while (scanner.hasNext()) {
            String number = scanner.next();
            String[] numbers = number.split(",");
            int[] intValues = new int[numbers.length-1];
            int indx=0;
            for (int x=1;x<numbers.length;x++){
                intValues[indx++]=Integer.parseInt(numbers[x]);
            } 
            
            for (int i=0;i<intValues.length;i++){
                for (int j=i+1;j<intValues.length;j++){
                    if (intValues[i]>intValues[j]) {//condition that i<j
                        matrix[intValues[j]][intValues[i]]++; // increment count within triangular
                                            } else{
                    matrix[intValues[i]][intValues[j]]++;}
                }
            }
        }
        scanner.close();
       PrintWriter solution = new PrintWriter(filename+"_Solution.txt","UTF-8");
    for (int i=0;i<size;i++){
        for (int j=i;j<size;j++){
            if (matrix[i][j]>support_level){
                solution.print("{"+Integer.toString(i)+","+Integer.toString(j)+"}");
                solution.println("  Support: "+Integer.toString(matrix[i][j]));
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