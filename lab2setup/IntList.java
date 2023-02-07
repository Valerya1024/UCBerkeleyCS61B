public class IntList {
    public int first;
    public IntList rest;

    public static void main(String[] args){
        IntList L = new IntList(5, new IntList(10,new IntList(19,null)));
        System.out.println(L.get(2));

        IntList Q = dincrList(L,2);
        System.out.println(L);
        System.out.println(Q);

    }

    public IntList(int f, IntList r) {
        first = f;
        rest = r;
    }

    /**
     * Return the size of this IntList.
     */
    public int size() {
        if (rest == null) {
            return 1;
        }
        return 1 + this.rest.size();

    }

    public int get(int i){
        if (i == 0){
            return first;
        }
        return rest.get(i-1);
    }

    public static IntList incrList(IntList L, int x){
        if (L.rest == null){
            return new IntList(L.first + x, null);
        }
        return new IntList(L.first + x, incrList(L.rest, x));
    }

    public static IntList dincrList(IntList L, int x){
        if (L.rest == null){
            L.first = L.first + x;
            IntList Q = L;
            return Q;
        }
        L.first = L.first + x;
        L.rest = dincrList(L.rest, x);
        IntList Q = L;
        return Q;
    }
}
