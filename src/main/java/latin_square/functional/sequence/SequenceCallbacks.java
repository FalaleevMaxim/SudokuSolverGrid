package latin_square.functional.sequence;

import latin_square.functional.sequence.action.SequenceAction;

public class SequenceCallbacks {
    public final SequenceElementConstraint elementConstraint;
    public final SequenceAction action;
    public final SequenceStopCondition stopCondition;

    public static final SequenceElementConstraint noConstraint = (number, prev, n) -> true;
    public static final SequenceStopCondition noStopCondition = sequence -> false;

    public SequenceCallbacks(SequenceElementConstraint elementConstraint, SequenceAction action, SequenceStopCondition stopCondition) {
        this.elementConstraint = elementConstraint==null?noConstraint:elementConstraint;
        this.action = action;
        this.stopCondition = stopCondition==null?noStopCondition:stopCondition;
    }
}