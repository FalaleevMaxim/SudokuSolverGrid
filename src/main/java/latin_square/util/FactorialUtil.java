package latin_square.util;

import java.math.BigInteger;
import java.util.HashMap;

public class FactorialUtil
{
    private static HashMap<Integer,BigInteger> cache = new HashMap<>();

    public static BigInteger factorial(int n)
    {
        BigInteger ret;

        if (n == 0) return BigInteger.ONE;
        if (null != (ret = cache.get(n))) return ret;
        ret = BigInteger.valueOf(n).multiply(factorial(n-1));
        cache.put(n, ret);
        return ret;
    }
}