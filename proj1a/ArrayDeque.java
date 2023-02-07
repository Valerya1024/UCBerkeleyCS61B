import java.lang.reflect.Array;

public class ArrayDeque {
    private int[] arr;
    public ArrayDeque(){
        arr = new int[0];
    }

    public void addFirst(int x) {
        int size = arr.length;
        int[] newArr = new int[1+size];
        newArr[0] = x;
        System.arraycopy(arr,0,newArr,1,size);
        arr = newArr;
    }

    public void addLast(int x) {
        int size = arr.length;
        int[] newArr = new int[1+size];
        newArr[size] = x;
        System.arraycopy(arr,0,newArr,0,size);
        arr = newArr;
    }

    public void printDeque(){
        System.out.print(arr[0]);
        int size = arr.length;
        for (int i = 1; i < size; i++){
            System.out.print(" "+arr[i]);
        }
    }

    public boolean isEmpty() {
        return arr.length == 0;
    }

    public int size(){
        return arr.length;
    }

    public int removeFirst(){
        int size = arr.length;
        int[] newArr = new int[size-1];
        int x = arr[0];
        System.arraycopy(arr,1,newArr,0,size-1);
        arr = newArr;
        return x;
    }

    public int removeLast(){
        int size = arr.length;
        int[] newArr = new int[size-1];
        int x = arr[size-1];
        System.arraycopy(arr,0,newArr,0,size-1);
        arr = newArr;
        return x;
    }

    public int get(int index) {
        int x = 0;
        if (index < arr.length | index > 0) {
            x = arr[index];
        } else {
            System.out.println("invalid i");
        }
        return x;
    }

}
