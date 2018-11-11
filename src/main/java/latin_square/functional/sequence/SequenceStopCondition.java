package latin_square.functional.sequence;

import latin_square.sequence.Sequence;


public interface SequenceStopCondition {
    /**
     * Условие остановки при получении очередной последовательности
     * @param sequence полученная последовательность
     * @return true если больше перебирать не нужно.
     */
    boolean stopCondition(Sequence sequence);
}