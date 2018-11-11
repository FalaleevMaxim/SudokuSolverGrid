package latin_square.square;

import latin_square.sequence.Sequence;
import latin_square.util.FactorialUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Square {
    public static BigInteger index(List<Sequence> rows){
        final int n = rows.size();
        if(n<2) throw new IllegalArgumentException("Square size must be 2 or bigger");
        BigInteger fact = FactorialUtil.factorial(n);
        BigInteger index = BigInteger.ZERO;
        BigInteger mul = BigInteger.ONE;
        for (int i=n-1;i>=0;i--){
            BigInteger seq = rows.get(i).getIndex();
            index = index.add(mul.multiply(seq));
            if(i>0) mul = mul.multiply(fact);
        }
        return index;
    }

    public static List<Sequence> fromIndex(BigInteger index, int n){
        if(n<2) throw new IllegalArgumentException("Square size must be 2 or bigger");
        BigInteger fact = FactorialUtil.factorial(n);
        BigInteger[] factPows = new BigInteger[n];
        BigInteger pow = BigInteger.ONE;
        for(int i=0;i<n;i++){
            factPows[i] = pow;
            pow = pow.multiply(fact);
        }
        Sequence[] rows = new Sequence[n];
        for (int i=0;i<n;i++){
            BigInteger[] div = index.divideAndRemainder(factPows[n-i-1]);
            rows[i] = new Sequence(div[0], n);
            index = div[1];
        }
        return Arrays.asList(rows);
    }

    public static void all(int n){
        all(new ArrayList<>(n), n);
    }

    private static void all(List<BigInteger> prev, int n){
        final int level = n - prev.size();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(Sequence.maxIndex(n))<=0; i=i.add(BigInteger.ONE)){
            prev.add(i);
            if(level==1){
                System.out.println(prev);
            }else{
                all(prev, n);
            }
            prev.remove(prev.size()-1);
        }
    }

    public static BigInteger maxIndex(int n){
        return FactorialUtil.factorial(n).pow(n).add(BigInteger.ONE.negate());
    }

/*    public static void main(String[] args) {
        int n=3;
        BigInteger max = FactorialUtil.factorial(n).pow(n);
        for(BigInteger i = BigInteger.ZERO; i.compareTo(max)<0;i = i.add(BigInteger.ONE)){
            List<Sequence> sequences = fromIndex(i, n);
            System.out.println(sequences.stream().map(Sequence::getIndex).collect(Collectors.toList()).toString() + index(sequences));
        }
        all(n);
    }*/
}
