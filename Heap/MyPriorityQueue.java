package javaapplication2;

public class MyPriorityQueue<E extends Comparable<E>> {

    private Heap<E> heap = new Heap<E>();

    public void poll(E newObject) {
        heap.add(newObject);
    }

    public E dequeue() {
        return heap.remove();
    }

    public int getSize() {
        return heap.getSize();
    }
}
