package testcase;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by yueyue on 2020/12/19
 */
public class case1 {

    /*Field */
    private static int val1 = 0;
    private static boolean val2 = val1 * 10;
    private List<String> val3 = new ArrayList<>();

    /*method*/
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            String tp = String.valueOf(3);
            for (int j = 0; j < 20; j++) {
                if (i * j > 5) {
                    System.out.printf("ans:" + ans + i * j);
                } else {
                    System.out.printf("ans:" + ans + (i + j));
                }

            }
        }
    }
}
