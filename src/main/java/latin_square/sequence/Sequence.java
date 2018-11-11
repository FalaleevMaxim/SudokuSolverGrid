package latin_square.sequence;

import latin_square.functional.sequence.SequenceCallbacks;
import latin_square.functional.sequence.SequenceElementConstraint;
import latin_square.functional.sequence.action.SequenceCollector;
import latin_square.util.FactorialUtil;
import latin_square.util.ListInsertUtil;

import java.math.BigInteger;
import java.util.*;

public class Sequence {
    protected BigInteger index;
    protected List<Integer> numbers;
    protected Sequence next;
    protected final int n;

    public Sequence(int n) {
        this(BigInteger.ZERO, n);
    }

    public Sequence(BigInteger index, int n) {
        this.n=n;
        this.index = index;
    }

    public Sequence(List<Integer> numbers) {
        this.numbers = Collections.unmodifiableList(new ArrayList<>(numbers));
        this.n = numbers.size();
    }

    protected Sequence(BigInteger index, List<Integer> numbers) {
        this.index = index;
        this.numbers = Collections.unmodifiableList(new ArrayList<>(numbers));
        this.n = numbers.size();
    }

    //region Properties
    public BigInteger getIndex() {
        if(index==null) index = index(numbers);
        return index;
    }

    public List<Integer> getNumbers() {
        if(numbers==null)
            this.numbers = Collections.unmodifiableList(new ArrayList<>(fromIndex(index, n)));
        return numbers;
    }

    public int size(){
        return n;
    }

    public boolean hasNext(){
        return index.compareTo(maxIndex(size()))<0;
    }

    public Sequence next(){
        if(next==null && hasNext())
            next = new Sequence(index.add(BigInteger.ONE), size());
        return next;
    }
    //endregion

    @Override
    public String toString() {
        return getNumbers().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sequence)) return false;

        Sequence sequence = (Sequence) o;

        return getIndex().equals(sequence.getIndex());
    }

    @Override
    public int hashCode() {
        return getIndex().hashCode();
    }

    private static final Map<Integer, BigInteger> maxIndexes = new HashMap<>();

    public static BigInteger maxIndex(int n){
        if(!maxIndexes.containsKey(n))
            maxIndexes.put(n, FactorialUtil.factorial(n).add(BigInteger.ONE.negate()));
        return maxIndexes.get(n);
    }

    //region convert
    /**
     * @param seq допустимая последовательность чисел
     * @return индекс последовательности
     */
    public static BigInteger index(List<Integer> seq){
        validateSeq(seq);
        return index(seq, new ArrayList<>());
    }

    /**
     * Проверяет, что последовательность содержит все числа от 1 до размера последовательности по 1 разу.
     */
    private static void validateSeq(List<Integer> seq){
        boolean[] entry = new boolean[seq.size()];
        for(Integer num : seq){
            int i = num-1;
            if(i<0 || i>=entry.length) throw new IllegalArgumentException("Wrong index " + num + " in sequence of " + seq.size() + "numbers");
            if(entry[i]) throw new IllegalArgumentException("Number " + num + " repeats in sequence");
            entry[i] = true;
        }
    }

    /**
     *
     * @param seq Последовательность, для которой считается номер
     * @param prev Использованные числа последовательности, отсортированные по возрастанию
     * @return индекс части последовательности с учётом использованных чисел
     */
    private static BigInteger index(List<Integer> seq, List<Integer> prev){
        //Если в последовательности нет чисел или 1 число, возвращается 0
        if(seq.size()-prev.size()<=1){
            return BigInteger.ZERO;
        }

        //Очередное число из последовательности
        int seqNum = seq.get(prev.size());

        //номер этого числа с пропуском использованных чисел
        int mul = seqNum-1;
        for (Integer p : prev) {
            if (p < seqNum) {
                mul--;
            } else {
                break;
            }
        }
        //Счётчик равен номеру числа, умноженному на факториал количества неиспользованных чисел
        BigInteger counter = BigInteger.valueOf(mul).multiply(
                FactorialUtil.factorial(seq.size()-prev.size()-1));

        ListInsertUtil.insert(prev, seqNum);
        counter = counter.add(index(seq, prev));

        return counter;
    }

    /**
     * @param index Индекс последовательности
     * @param n длина последовательности
     * @return последовательность по индексу
     */
    public static List<Integer> fromIndex(BigInteger index, int n){
        return fromIndex(index, n, new ArrayList<>());
    }

    /**
     * @param number Индекс части последовательности от n-prev.size() числа
     * @param n длина полной последовательности
     * @param prev Использованные числа последовательности, отсортированные по возрастанию
     * @return часть последовательности
     */
    private static List<Integer> fromIndex(BigInteger number, int n, List<Integer> prev){
        BigInteger[] divideAndRemainder = number.divideAndRemainder(FactorialUtil.factorial(n-prev.size()-1));
        //Число в последовательности без учёта использованных
        int seqNum = divideAndRemainder[0].intValue() + 1;

        //Смещение числа с учётом пропуска использованных чисел
        for (Integer p : prev) {
            if (p <= seqNum) {
                seqNum++;
            } else {
                break;
            }
        }

        if(seqNum>n) throw new IllegalArgumentException("Number is too big. Got index " + seqNum + " for sequence of " + n + "numbers");

        //Если это последнее число в последовательности, оно оборачивается в список и возвращается
        if(n-prev.size()<=1){
            List<Integer> res = new ArrayList<>();
            res.add(seqNum);
            return res;
        }

        //Иначе это число добавляется в использованные и добавляется в начало списка, полученного с рекурсивного вызова.
        ListInsertUtil.insert(prev, seqNum);
        List<Integer> res = fromIndex(divideAndRemainder[1], n, prev);
        res.add(0, seqNum);

        return res;
    }
    //endregion

    //region get sequences

    /**
     *
     * @param n длина последовательности
     * @return все последовательности из n чисел
     */
    public static List<Sequence> all(int n){
        if(n<2)
            throw new IllegalArgumentException("'n' must be 2 or bigger");
        List<Sequence> all = new ArrayList<>();
        all(new ArrayList<>(), n, n, BigInteger.ZERO, all);
        return all;
    }

    private static BigInteger all(List<Integer> prev, int n, int level, BigInteger counter, List<Sequence> sequences){
        for(int i=1;i<=n;i++){
            if(prev.contains(i)){
                continue;
            }
            prev.add(i);
            if(level<=1){
                sequences.add(new Sequence(counter, prev));
                counter = counter.add(BigInteger.ONE);
                prev.remove(prev.size()-1);
                continue;
            }
            counter = all(prev, n, level-1, counter, sequences);
            prev.remove(prev.size()-1);
        }
        return counter;
    }

    /**
     * Все последовательности от начальной до конечной
     * @param start начальная последовательность. Если null, то берётся по индексу 0.
     * @param end конечная последовательность. Если null, берётся по максимальному индексу.
     * @exception IllegalArgumentException если оба параметра null
     * @exception IllegalArgumentException если индекс у start больше чем у end.
     * @return Все последовательности от start до end (включая start и end)
     */
    public static List<Sequence> all(Sequence start, Sequence end){
        if(start==null && end==null) throw new IllegalArgumentException("Start or end sequence must be not null");
        int n;
        if(start==null){
            n = end.size();
            start = new Sequence(BigInteger.ZERO, n);
        } else if(end==null){
            n = start.size();
            end = new Sequence(maxIndex(start.size()), n);
        }
        SequenceCollector collector = new SequenceCollector();
        new Loop(start).allBefore(end.getIndex(), new SequenceCallbacks(null, collector, null));
        return collector.getCollected();
    }
    //endregion

    /*public static void main(String[] args) {
        //List<Sequence> sequences = Sequence.all(5);
        //List<Sequence> sequences = Sequence.all(new Sequence(BigInteger.valueOf(15), 5), new Sequence(BigInteger.valueOf(115), 5));
        //List<Sequence> sequences = Sequence.all(new Sequence(BigInteger.valueOf(15), 5), null);
        *//*List<Sequence> sequences = new LinkedList<>();
        Sequence seq = new SequenceLoop(5);
        do {
            sequences.add(seq);
            seq = seq.next();
        } while (seq!=null);*//*
        //sequences.forEach(sequence -> System.out.println(sequence.toString()+sequence.getIndex()));
        //System.out.println(fromIndex(BigInteger.valueOf(100), 5));

    }*/

    /**
     * Задаёт начальные счётчики циклов и итерируется по числу в последовательности.
     * Объекты одноразовые, т.к. счётчик изменяется
     */
    public static class Loop {
        //Счётчик цикла (минимальный 1)
        private int i;
        //Максимальное число
        private final int n;
        //Уровень цикла (n самый внешний, 1 самый внутренний)
        private final int level;
        //Внешний цикл
        private Loop outer;
        //Вложенный цикл
        private Loop inner;
        //Изначальное значение счётчика (задаётся только в самом внешнем цикле)
        private BigInteger initCounter;


        public Loop(Sequence start){
            this(start.size(), start.getNumbers(), null);
            initCounter = start.getIndex();
        }

        private Loop(int n, List<Integer> numbers, Loop outer) {
            this.n=n;
            int level = numbers.size();
            this.level = level;

            int i = numbers.get(0);
            if(i<1) throw new IllegalArgumentException("Number " + i + " is less than 1");
            if(i>n) throw new IllegalArgumentException("Number " + i + " is bigger than n="+n);
            this.i = i;
            this.outer = outer;
            if(this.level >1) inner = new Loop(n, numbers.subList(1, level), this);
        }

        public BigInteger allBefore(BigInteger last, SequenceCallbacks callbacks){
            if(initCounter.compareTo(last)>0) throw new IllegalStateException("Initial counter is bigger than last one. Initial: " + initCounter + "Last: " + last);
            if(last.compareTo(maxIndex(n))>0) throw new IllegalArgumentException("Last counter is bigger than max index");
            initCounter = initCounter.add(initialCheck(new ArrayList<>(n), callbacks.elementConstraint));
            if(initCounter.compareTo(last)>0) return null;
            return allBefore(initCounter, last, new ArrayList<>(n), callbacks);
        }

        //Проверяет, что изначальные числа соответствуют ограничениям или продвигает на 1 самый внешний не подходящий по ограничениям счётчик.
        private BigInteger initialCheck(List<Integer> prev, SequenceElementConstraint constraint){
            //Если число не соответствует ограничению, оно увеличивается на 1, а все последующие сбрасываются в 1.
            if(!constraint.elementConstraint(i, prev, n)){
                prev.add(i);
                i++;
                if(inner==null){
                    //Если это самый внутренний цикл, то счётчик увеличивается на 1.
                    return BigInteger.ONE;
                }else {
                    //Внутренние циклы сбрасываются, и возвращается число, на которое увеличится счётчик при прохождении внутренних циклов до конца.
                    return inner.initialReset(prev);
                }
            }

            if(inner==null)
                //Если это последнее число, и оно прошло ограничение, то возвращается 0, т.е. начальный индекс не изменяется.
                return BigInteger.ZERO;
            else {
                //Если это число не последнее и прошло ограничение, то оно добавляется в использованные и проверка вызывается для следующего.
                prev.add(i);
                return inner.initialCheck(prev, constraint);
            }
        }

        //Возвращает число, на которое увеличится счётчик, когда все внутренние циклы дойдут до конца.
        private BigInteger initialReset(List<Integer> prev){
            //Количество непройденных чисел до конца цикла
            int rest = 0;
            for(int i1=i+1;i1<n;i1++){
                if(!prev.contains(i1)) rest++;
            }

            //Счётчик увеличивается на факториал уровня вложенного цикла (т.е. сколько последовательностей переберёт внутренний цикл за полный проход),
            // умноженный на количество оставшихся чисел в этом цикле (т.е. сколько раз был бы вызван полный перебор внутреннего цикла).
            BigInteger counter = FactorialUtil.factorial(level-1).multiply(BigInteger.valueOf(rest));
            //Для внутренних циклов (если они есть) рекурсивно вызывается сброс, и к счётчику добавляется, сколько они проходят до сброса.
            if(inner!=null) {
                prev.add(i);
                counter = counter.add(inner.initialReset(prev));
            }
            i=1;
            return counter;
        }

        private BigInteger allBefore(BigInteger counter, BigInteger last, List<Integer> prev, SequenceCallbacks callbacks){
            for(;i<=n;i++){
                //Проверка, что число ещё не использовалось
                if(prev.contains(i))
                    continue;
                //Проверка, что число подходит под ограничение
                if(!callbacks.elementConstraint.elementConstraint(i, prev, n)) {
                    //Если элемент не подошёл по ограничениям, все внутренние циклы пропускаются, но счётчик (индекс) увеличивается, как будто циклы прошли.
                    //В отличие от начальной проверки, тут прибавляется ровно факториал, т.к. внутренние циклы установлены в начало.
                    counter = counter.add(FactorialUtil.factorial(level-1));
                    if(counter.compareTo(last)>0){
                        counter=null;
                        break;
                    }
                    continue;
                }
                //Если число подходит, то оно добавляется в список использованных
                prev.add(i);
                if(level==1){
                    //Если это самый внутренний цикл, создаётся новая последовательность (используется приватный конструктор с 2 параметрами, т.к. и индекс, и числа точно известны)
                    Sequence sequence = new Sequence(counter, prev);
                    //Для созданной последовательности выполняется действие
                    callbacks.action.action(sequence);
                    //Счётчик увеличивается на 1
                    counter = counter.add(BigInteger.ONE);
                    //Если счётчик оказался больше максимальной границы, или сработало условие остановки, перебор останавливается.
                    if(counter.compareTo(last)>=0 || callbacks.stopCondition.stopCondition(sequence)){
                        //Установка счётчика в null сигнализирует, что перебор следует остановить
                        counter=null;
                        break;
                    }
                }else {
                    //Если цикл не самый внутренний, рекурсивно вызывается перебор для внутреннего цикла.
                    counter = inner.allBefore(counter, last, prev, callbacks);
                    //Если счётчик null, это означает конец перебора.
                    if (counter == null) break;
                }
                //Добавленное число удаляется из списка использованных.
                prev.remove(prev.size() - 1);
            }
            i=1;//Счётчик сбрасывается после прохождения цикла
            return counter;
        }

        public Sequence next(){
            List<Integer> numbers = next(new ArrayList<>(n));
            if(numbers==null) return null;
            initCounter = initCounter.add(BigInteger.ONE);
            return new SequenceLoop(initCounter, numbers, this);
        }

        private List<Integer> next(List<Integer> prev){
            if(inner==null){
                //i увеличивается пока не найдётся следующий счётчик, либо до n.
                if(i>n) i=0;
                do i++;
                while (i<=n && prev.contains(i));
                //Если новый счётчик не найден, возвращается null, чтобы внешний цикл увеличился.
                if(i>n) return null;

                List<Integer> numbers = new ArrayList<>(n);
                numbers.add(i);
                return numbers;
            }

            if(i>n){
                i=0;
                do i++;
                while (i<=n && prev.contains(i));
            }

            prev.add(i);
            List<Integer> numbers = inner.next(prev);
            prev.remove(prev.size()-1);
            if(numbers==null){
                do i++;
                while (i<=n && prev.contains(i));
                if(i>n){
                    return null;
                }
                prev.add(i);
                numbers = inner.next(prev);
                prev.remove(prev.size()-1);
            }
            numbers.add(0,i);
            return numbers;
        }
    }
}