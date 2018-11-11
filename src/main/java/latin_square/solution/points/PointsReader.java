package latin_square.solution.points;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PointsReader {
    public static Map<Pos, Integer> readPoints(String fileName, int n) throws IOException {
        Map<Pos, Integer> points = new HashMap<>();
        BufferedReader reader = new BufferedReader (new FileReader(fileName));
        String line;
        int i=0;
        while ((line = reader.readLine()) != null ){
            if(i>=n) throw new RuntimeException("Wrong square size");
            String[] tokens = line.split(" ");
            if(tokens.length>n) throw new RuntimeException("Wrong square size");
            for(int j=0;j<tokens.length;j++){
                try{
                    int num = Integer.parseInt(tokens[j]);
                    if(num>n || num<1) throw new RuntimeException("Wrong number");
                    points.put(new Pos(i,j), num);
                }catch (NumberFormatException ignored){

                }
            }
            i++;
        }
        reader.close();
        return points;
    }
}
