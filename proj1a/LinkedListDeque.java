public class LinkedListDeque<type> {
    private class Node {
        public type item;
        public Node next;
        public Node prev;
        public Node(type x, Node n, Node p) {
            item = x;
            next = n;
            prev = p;
        }
    }

    private Node sentinal;
    private int size;

    public LinkedListDeque() {
        sentinal = new Node(null, null, null);
        sentinal.next = sentinal;
        sentinal.prev = sentinal;
        size = 0;
    }

    public void addFirst(type x) {
        size += 1;
        Node first = new Node(x, sentinal.next, sentinal);;
        sentinal.next.prev = first;
        sentinal.next = first;
    }

    public void addLast(type x) {
        size += 1;
        Node last = new Node(x, sentinal, sentinal.prev);;
        sentinal.prev.next = last;
        sentinal.prev = last;
    }


    public boolean isEmpty() {
        return size == 0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        Node p = sentinal.next;
        System.out.print(p.item);
        for (int i = 1; i < size; i++){
            p = p.next;
            System.out.print(" "+p.item);
        }
    }

    public type removeFirst(){
        size -= 1;
        type x = sentinal.next.item;
        sentinal.next = sentinal.next.next;
        sentinal.next.next.prev = sentinal;
        return x;
    }

    public type removeLast(){
        size -= 1;
        type x = sentinal.prev.item;
        sentinal.prev = sentinal.prev.prev;
        sentinal.prev.prev.next = sentinal;
        return x;
    }

    public type get(int index) {
        type x = null;
        if (index < size | index > 0) {
            Node p = sentinal.next;
            for (int i = 0; i <= index; i++){
                x = p.item;
                p = p.next;
            }
        } else {
            System.out.println("invalid i");
        }
        return x;
    }

    private type getr(Node p, int index){
        if (index == 0){
            return p.item;
        }
        return getr(p.next, index-1);
    }

    public type getRecursive(int index){
        return getr(sentinal.next, index);
    }
}
