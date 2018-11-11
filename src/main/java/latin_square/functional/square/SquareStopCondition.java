package latin_square.functional.square;

import latin_square.sequence.Sequence;

public interface SquareStopCondition {
    boolean stopCondition(Sequence sequence);
}