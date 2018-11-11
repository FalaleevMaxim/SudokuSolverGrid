package latin_square.functional.sequence.action;

import latin_square.sequence.Sequence;

import java.util.ArrayList;
import java.util.List;

public class SequenceCollector implements SequenceAction {
    private final List<Sequence> collected = new ArrayList<>();

    public List<Sequence> getCollected() {
        return collected;
    }

    @Override
    public void action(Sequence sequence) {
            collected.add(sequence);
    }
}
