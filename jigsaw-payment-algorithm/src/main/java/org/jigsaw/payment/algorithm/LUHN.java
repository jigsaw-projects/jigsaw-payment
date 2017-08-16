package org.jigsaw.payment.algorithm;

/**
 * Luhn algorithm,luhn 算法,模10算法，一种简单的校验和算法。 <a href='https://en.wikipedia.org/wiki/Luhn_algorithm'>luhn 算法,模10算法</a>
 * 
 */
public class LUHN {

    /**
     * 根据校验和算法 生成校验码，
     * @param code
     * @return 校验码
     */
    public static char gen(String code){
        if(code == null || code.trim().length() == 0
                || !code.matches("\\d+")) {
            return 'N';
        }
        char[] chs = code.trim().toCharArray();
        int luhmSum = 0;
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if(j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
    }
}
