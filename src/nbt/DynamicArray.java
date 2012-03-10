package nbt;

import java.util.ArrayList;
import java.util.Iterator;

public class DynamicArray<T> implements Iterable<T> {

    private final ArrayList<T> content;

    public DynamicArray(final int initialSize) {
        content = new ArrayList<T>(initialSize);
    }

    public T get(final int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException(
                    "index may not be smaller than 0: " + i);
        }
        if (i >= content.size()) {
            return null;
        }
        return content.get(i);
    }

    public void set(final int i, final T t) {
        if (i < 0) {
            throw new IndexOutOfBoundsException(
                    "index may not be smaller than 0: " + i);
        }
        ensureSize(i);
        content.set(i, t);
    }

    private void ensureSize(final int i) {
        content.ensureCapacity(i + 1);
        while (i >= content.size()) {
            content.add(null);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private final Iterator<T> it = content.iterator();

            private T next;

            private boolean first = true;

            private void fetchIfFirst() {
                if (first) {
                    fetchNext();
                    first = false;
                }
            }

            private void fetchNext() {
                while (it.hasNext()) {
                    final T t = it.next();
                    if (t != null) {
                        next = t;
                        return;
                    }
                }
                next = null;
            }

            @Override
            public boolean hasNext() {
                fetchIfFirst();
                return next != null;
            }

            @Override
            public T next() {
                fetchIfFirst();
                final T res = next;
                fetchNext();
                return res;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

}
