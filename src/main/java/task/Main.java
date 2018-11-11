package task;

import latin_square.solution.LatinSquareFinder;
import latin_square.solution.points.PointsReader;

import java.io.IOException;
import java.math.BigInteger;

public class Main {
    /**
     * @param args N start end numbersFile [all/one/sudoku]
     */
    public static void main(String[] args) throws IOException {
        int n = Integer.parseInt(args[0]);

        //System.out.println(Square.maxIndex(n));

        BigInteger start = new BigInteger(args[1]);
        BigInteger end = new BigInteger(args[2]);
        String presetNumbersFile = args[3];
        String mode = args[4];


        LatinSquareFinder finder = new LatinSquareFinder(n, start, end);
        finder.setPresetNumbers(PointsReader.readPoints(presetNumbersFile, n));
        switch (mode){
            case "all":
                finder.setFindAllOption(true);
                break;
            case "one":
                break;
            case "sudoku":
                finder.setSudokuOption(true);
                break;
            default:
                throw new IllegalArgumentException("Unknown mode");
        }
        finder.setOutputOption(true);
        finder.start();
    }
}
