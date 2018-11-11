package latin_square.solution;

import latin_square.functional.sequence.SequenceCallbacks;
import latin_square.functional.sequence.SequenceElementConstraint;
import latin_square.functional.sequence.SequenceStopCondition;
import latin_square.functional.sequence.action.SequenceAction;
import latin_square.sequence.Sequence;
import latin_square.solution.points.PointsReader;
import latin_square.solution.points.Pos;
import latin_square.square.Square;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static latin_square.sequence.Sequence.Loop;

public class LatinSquareFinder implements SequenceElementConstraint, SequenceStopCondition, SequenceAction {
    private int n;
    //Начальный индекс квадрата
    private BigInteger startIndex;
    //Строки начального квадрата
    private List<Sequence> startSequences = new ArrayList<>();
    //Индекс последнего квадрата
    private BigInteger endIndex;
    //строки последнего квадрата
    private List<Sequence> endSequences = new ArrayList<>();
    //Строка размера n с индексом 0. Поскольку строки неизменяемые, может переиспользоваться
    private Sequence nullSequence;
    //Строка размера n с максимальным индексом
    private Sequence maxSequence;
    //строки квадрата (изначальные и заменённые)
    private List<Sequence> sequences;
    //Номер текущей обрабатываемой строки
    private int currStr = 0;
    //Функции этого объекта в виде объекта SequenceCallbacks
    private SequenceCallbacks callbacks;
    //Найденные квадраты.
    private List<List<Sequence>> squares = new ArrayList<>();
    public List<List<Sequence>> getSquares(){
        return squares;
    }

    private boolean findAll = false;
    public void setFindAllOption(boolean findAll){
        this.findAll = findAll;
    }

    private boolean output = false;
    public void setOutputOption(boolean output){
        this.output = output;
    }

    private boolean sudoku = false;
    public void setSudokuOption(boolean isSudoku){
        if(isSudoku && n!=9) throw new IllegalStateException("Sudoku option only available for n=9");
        this.sudoku = isSudoku;
        this.findAll=false;
    }

    private boolean stop = false;

    private Map<Pos, Integer> presetNumbers;


    public LatinSquareFinder(int n, BigInteger startSquare, BigInteger lastSquare) {
        this.n=n;
        this.startIndex = startSquare;
        startSequences = Square.fromIndex(startIndex, n);
        sequences = new ArrayList<>(startSequences);
        if(lastSquare.compareTo(Square.maxIndex(n))>0)
            lastSquare = Square.maxIndex(n);
        this.endIndex = lastSquare;
        endSequences = new ArrayList<>(Square.fromIndex(endIndex, n));
        callbacks = new SequenceCallbacks(this, this, this);
        nullSequence = new Sequence(n);
        maxSequence = new Sequence(Sequence.maxIndex(n), n);
    }

    public void setPresetNumbers(Map<Pos, Integer> presetNumbers) {
        this.presetNumbers = presetNumbers;
    }

    @Override
    public boolean elementConstraint(int number, List<Integer> prev, int n) {
        if(stop) return false;
        prev.add(number);
        prev.remove(prev.size()-1);

        //Проверка, что в столбце не было повторений
        int j = prev.size();//номер столбца
        for(int i = 0; i< currStr; i++){
            //Если в какой-то строке в том же столбце встречается та же цифра, условие не пройдено.
            if(sequences.get(i).getNumbers().get(j)==number) {
                return false;
            }
        }
        //Проверка, что число соврадает с предустановленным, если оно есть.
        if(presetNumbers==null) return  true;
        Integer presetNum = presetNumbers.get(new Pos(currStr, j));
        if(presetNum != null && !presetNum.equals(number)) return false;

        //Если установлена опция судоку, проверяется наличие того же числа в квадрате 3*3
        if(sudoku){
            int jmin = j-j%3;
            int imin = currStr-currStr%3;
            for(int str=imin; str<currStr; str++){
                List<Integer> strNum = sequences.get(str).getNumbers();
                for(int col=jmin; col<jmin+3; col++){
                    if(strNum.get(col).equals(number)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean stopCondition(Sequence sequence) {
        if(stop) return true;
        //Если требуется найти все квадраты, перебор не останавливается
        if(findAll) return false;
        //Перебор заканчивается, когда найден квадрат
        return !squares.isEmpty();
    }

    @Override
    public void action(Sequence sequence) {
        //Если это последняя строка, квадрат добавляется в список и выводится.
        if(currStr == sequence.size() - 1){
            sequences.set(currStr, sequence);
            List<Sequence> square = new ArrayList<>(sequences);
            if(Square.index(square).compareTo(endIndex)>0){
                stop=true;
                return;
            }
            squares.add(square);
            if(!findAll) stop = true;
            //square = Square.fromIndex(Square.index(square), n);
            if(output) {
                if(sudoku) printSudoku(square);
                else printSquare(square);
            }
            return;
        }
        //Если строка поменялась, все следующие можно сбросить в 0.
        if(!sequences.get(currStr).getIndex().equals(sequence.getIndex())){
            for (int i = currStr +1; i<sequences.size(); i++) {
                sequences.set(i, nullSequence);
            }
        }
        //Записывается обновлённая строка
        sequences.set(currStr, sequence);
        //Указатель переходит на следующую строку
        currStr++;
        start();
    }

    private static void printSquare(List<Sequence> square) {
        for (Sequence seq : square) {
            System.out.println(seq);
        }
        System.out.println();
    }

    private static void printSudoku(List<Sequence> sudoku) {
        String rowsep = Stream.generate(() -> "-")
                .limit(sudoku.size()*2+4)
                .reduce((s, s2) -> s+s2)
                .get();
        for (int i = 0; i < sudoku.size(); i++) {
            if(i%3==0) System.out.println(rowsep);
            Sequence seq = sudoku.get(i);
            for (int j = 0; j < seq.getNumbers().size(); j++) {
                System.out.print((j%3==0?"|":"")+seq.getNumbers().get(j)+" ");
            }
            System.out.println("|");
        }
        System.out.println(rowsep);
    }

    public void start(){
        //Создаётся цикл для следующей строки
        Loop loop = new Loop(sequences.get(currStr));
        //Расчёт максимального индекса для строки
        //для этого проверяется, являются ли все предыдущие строки последними из возможных
        boolean allLast = true;
        for(int i=0;i<currStr;i++){
            if(!sequences.get(i).getIndex().equals(endSequences.get(i).getIndex())) {
                allLast = false;
                break;
            }
        }
        BigInteger endSequence;
        if(allLast){
            endSequence = endSequences.get(currStr).getIndex();
        }else{
            endSequence = Sequence.maxIndex(n);
        }
        //Запускается цикл
        BigInteger counter = loop.allBefore(endSequence, callbacks);
        if(counter==null) stop = true;
        //При выходе из цикла происходит возврат к предыдущей строке
        currStr--;
    }

    public boolean found(){
        return !squares.isEmpty();
    }

    public int getN() {
        return n;
    }

    public static void main(String[] args) throws IOException {
        int n=9;

        Map<Pos, Integer> presetNumbers = PointsReader.readPoints("numbers.txt", n);

        int taskCount = 800;

        BigInteger taskSize = Square.maxIndex(n).divide(BigInteger.valueOf(taskCount));
        BigInteger next = BigInteger.ZERO;
        BigInteger i;

        /*for(i = BigInteger.ZERO; next.compareTo(Square.maxIndex(n))<=0; ){
            i=next;
            next = i.add(taskSize);
            BigInteger end = next.add(BigInteger.ONE.negate()); //i+taskSize-1;

            LatinSquareFinder squareFinder = new LatinSquareFinder(n, i, end);
            squareFinder.setPresetNumbers(presetNumbers);
            //squareFinder.setFindAllOption(true);
            squareFinder.setSudokuOption(true);
            squareFinder.setOutputOption(true);
            squareFinder.start();
        }
        if(next.compareTo(Square.maxIndex(n))>0){
            LatinSquareFinder squareFinder = new LatinSquareFinder(n, i, Square.maxIndex(n));
            squareFinder.setPresetNumbers(presetNumbers);
            //squareFinder.setFindAllOption(true);
            squareFinder.setSudokuOption(true);
            squareFinder.setOutputOption(true);
            squareFinder.start();
        }*/

        LatinSquareFinder squareFinder = new LatinSquareFinder(n, BigInteger.ZERO, Square.maxIndex(n));
        squareFinder.setPresetNumbers(presetNumbers);
        //squareFinder.setFindAllOption(true);
        squareFinder.setSudokuOption(true);
        squareFinder.setOutputOption(true);
        squareFinder.start();
    }
}
