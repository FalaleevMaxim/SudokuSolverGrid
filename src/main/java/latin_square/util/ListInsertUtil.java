package latin_square.util;

import java.util.ArrayList;
import java.util.List;

public class ListInsertUtil {
    public static int insert(List<Integer> list, int number){
        int i=0;
        while (i<list.size() && list.get(i)<number) i++;
        list.add(i, number);
        return i;
    }

    public static List<Integer> pureInsert(List<Integer> list, int number){
        ArrayList<Integer> copy = new ArrayList<>(list);
        insert(copy, number);
        return copy;
    }
}
