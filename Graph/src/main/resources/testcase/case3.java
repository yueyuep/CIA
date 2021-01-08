package testcase;

/**
 * Create by yueyue on 2020/12/23
 */
public class case3 {

    /*FieldDeclators*/
    private int a = 0;
    private int b = a * 10;

    public static void main(String[] args) {
        Caller caller = new Caller();
        caller.setA(10);
        /*add called Method */

        /*
         * 此处增加函数调用，但是并没有往Called中传参数，因此对于Called中函数即使是关联的，也不会由于
         * Caller函数的关联而产生任何缺陷(除非是Called中本身是带有缺陷的)
         *
         * */
        // todo change without passing on params

        // todo change with passing on params
        /*传递的参数在另一个函数中所占用的比例*/

    }


    public void setA(int a) {
        this.a = a;
    }

    public int getA() {
        return a;
    }

}
