package latin_square.sequence;

import java.math.BigInteger;
import java.util.List;

public class SequenceLoop extends Sequence {
    private Loop loop;

    public SequenceLoop(int n){
        super(n);
        loop = new Loop(this);
    }

    protected SequenceLoop(BigInteger index, List<Integer> numbers, Loop loop) {
        super(index, numbers);
        this.loop = loop;
    }

    @Override
    public Sequence next() {
        if(loop==null) return next;
        next = loop.next();
        loop = null;
        return next;
    }
}
