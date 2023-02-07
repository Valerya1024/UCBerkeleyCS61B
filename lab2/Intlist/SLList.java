public class SLList {
    private static class IntNode {
        public int item;
        public IntNode next;
        public IntNode(int i, IntNode n) {
            item = i;
            next = n;
        }
    }

    private IntNode first;
    private int size;

    public SLList() {
        first = new IntNode(0, null);;
        size = 0;
    }

    public void addFirst(int x) {
        size += 1;
        first.next = new IntNode(x, first.next);
    }

    public int getFirst() {
        return first.next.item;
    }

    public void addLast(int x) {
        size += 1;
        IntNode p = first;
        while (p.next != null) {
            p = p.next;
        }
        p.next = new IntNode(x, null);
    }
}

