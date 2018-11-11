package jdf;

import latin_square.square.Square;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;

public class JdfGenerator {
    public static void main(String[] args) throws FileNotFoundException {
        //System.out.println(Square.maxIndex(9).divide(BigInteger.valueOf(2000000)));
boolean[][] arr = new boolean[5][];
        String fileName = "numbers.txt";

        PrintWriter pw = new PrintWriter("LatinSquare.jdf");
        pw.println("init:");
        pw.println("    copyFile:");
        pw.println("    - LatinSquare.jar");
        pw.println("    - " + fileName);
        pw.println("tasks:");
        int n=9;
        int taskCount = 800;
        BigInteger taskSize = Square.maxIndex(9).divide(BigInteger.valueOf(taskCount));
        BigInteger next = BigInteger.ZERO;
        BigInteger i;
        for(i = BigInteger.ZERO; next.compareTo(Square.maxIndex(n))<=0; i=next){
            next = i.add(taskSize);
            BigInteger end = next.add(BigInteger.ONE.negate()); //i+taskSize-1;
            pw.println(" - java -jar LatinSquare.jar " + n + " " + i + " " + end + " " + fileName + " " + "sudoku");
        }
        if(next.compareTo(Square.maxIndex(n))>0){
            pw.println(" - java -jar LatinSquare.jar " + n + " " + i + " " + Square.maxIndex(n) + " " + fileName + " " + "sudoku");
        }
        pw.close();
    }
}
