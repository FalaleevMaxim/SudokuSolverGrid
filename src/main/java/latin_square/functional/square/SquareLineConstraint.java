package latin_square.functional.square;

import latin_square.sequence.Sequence;

import java.util.List;

public interface SquareLineConstraint {
    boolean lineConstraint(List<Sequence> prev, Sequence sequence);
}
