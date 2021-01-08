/**
 * Create by yueyue on 2020/12/19
 */
public class Main {
    /*test case1 */
    private static int flag = 1;
    private boolean tp = true;

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            int ans = 0;
            for (int j = 0; j < 20; j++) {
                if (i * j > 5 & tp) {
                    System.out.printf("ans:" + ans + i * j);
                } else {
                    System.out.printf("ans:" + ans + (i + j));
                }

            }
        }
    }
}
