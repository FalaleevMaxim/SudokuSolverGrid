package latin_square.functional.sequence;

import java.util.List;

public interface SequenceElementConstraint {
    /**
     * Ограничение на число в последовательности (работает до составления всей последовательности)
     * @param number очередное число в последовательности
     * @param prev предыдущие числа последовательности
     * @param n длина последовательности
     * @return true если число соответствует ограничению
     */
    boolean elementConstraint(int number, List<Integer> prev, int n);
}
