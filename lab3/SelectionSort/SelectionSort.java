public class SelectionSort {
    public static void sort(String[] input, int a){
        if (a == input.length-1) {
            return;
        }
        int min_index = findSmallest(input, a);
        swap(input, a, min_index);
        sort(input, a+1);
    }

    public static int findSmallest(String[] input, int a) {
        String min = input[a];
        int index = a;
        for (int i = a+1; i < input.length; i++) {
            if (min.compareTo(input[i]) > 0) {
                min = input[i];
                index = i;
            }
        }
        return index;
    }

    public static void swap(String[] input, int a, int b) {
        String x = input[a];
        input[a] = input[b];
        input[b] = x;
    }

}
