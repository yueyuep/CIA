package case1;
public class case3 {
    private static int val1 = 0;
    private int val2 = val1 + 2;
    private int val3 = val2 + val1 * 4;
    private String str = String.valueOf(val3 + val1);

    public int fun1(int a) {
        return val2 + a;
    }

    public void fun2() {
        if (val3 <= 0) {
            val3 = 0;
        } else {
            val3 = 20;
        }
    }

}
