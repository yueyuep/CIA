package CVDP;

/**
 * Create by yueyue on 2021/1/13
 */
public class test {

    public void bubbleSort() throws Throwable {
        /*bubbleSort*/
        int[] arr = {23, 12, 48, 56, 45};
        int temp = 1;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                    System.out.println("change");
                }
            }
        }
    }
}
