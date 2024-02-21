package src.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class SimpleQueue<T> implements Queue<T> {

    private final ArrayList<T> q = new ArrayList<>();

    @Override
    public int size() {
        return q.size();
    }

    @Override
    public boolean isEmpty() {
        return q.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return q.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return q.iterator();
    }

    @Override
    public Object[] toArray() {
        return q.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return q.toArray(t1s);
    }

    @Override
    public boolean add(T t) {
        return q.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return q.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return q.addAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return q.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return q.retainAll(collection);
    }

    @Override
    public void clear() {
        q.clear();
    }

    @Override
    public boolean offer(T t) {
        return this.add(t);
    }

    @Override
    public T remove() {
        return q.remove(0);
    }

    @Override
    public T poll() {
        return q.remove(0);
    }

    @Override
    public T element() {
        return q.get(0);
    }

    @Override
    public T peek() {
        return q.get(0);
    }
}
