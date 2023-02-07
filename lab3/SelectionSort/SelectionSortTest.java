import static org.junit.Assert.*;

import org.junit.Test;

public class SelectionSortTest {
    public static void TestFindMin(){
        String[] input = {"beware" , "of", "falling", "rocks"};
        int expect = 2;
        int result = SelectionSort.findSmallest(input, 1);
        org.junit.Assert.assertEquals(expect, result);
    }
    public static void TestSwap(){
        String[] input = {"beware" , "of", "falling", "rocks"};
        String[] expect = {"beware" , "falling", "of", "rocks"};
        SelectionSort.swap(input, 1, 2);
        org.junit.Assert.assertArrayEquals(expect, input);
    }
    public static void TestSort(){
        String[] input = {"beware" , "of", "falling", "rocks"};
        String[] expect = {"beware" , "falling", "of", "rocks"};
        SelectionSort.sort(input, 0);
        org.junit.Assert.assertArrayEquals(expect, input);
    }
    public static void main(String[] args) {
        TestSort();
        TestSwap();
        TestFindMin();
    }
}
