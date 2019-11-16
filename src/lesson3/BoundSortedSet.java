package lesson3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BoundSortedSet<T extends Comparable<T>> implements SortedSet<T> {

    private final BinaryTree<T> delegate;
    private final T lowerBound;
    private final T upperBound;

    BoundSortedSet(@NotNull BinaryTree<T> delegate, @Nullable T lowerBound, @Nullable T upperBound) {
        this.delegate = delegate;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T first() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T last() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        if (delegate.size() == 0) {
            return 0;
        }
        T last = delegate.last();
        T first = delegate.first();
        if (lowerBound != null && lowerBound.compareTo(last) > 0 ||
                upperBound !=null && upperBound.compareTo(first) < 0) {
            return 0;
        }
        ArrayList<T> allDelegateElements = delegate.fill();
        int ans = 1;
        int comp;
        int index = 1;

        if (lowerBound != null && lowerBound.compareTo(first) > 0) {
            index = Util.binarySearch(lowerBound, allDelegateElements);
            comp = allDelegateElements.get(index).compareTo(lowerBound);
            if (comp <= 0) {
                index++;
            }
            if (comp != 0) {
                ans = 0;
            }
        }

        while (index < allDelegateElements.size() &&
                (upperBound == null || allDelegateElements.get(index).compareTo(upperBound) < 0)) {
            if (allDelegateElements.get(index).compareTo(allDelegateElements.get(index++ - 1)) != 0) {
                ans++;
            }
        }

        return ans;
    }
    // Оценка сложности: O(delegate.size)

    // Оценка памяти: O(log(delegate.size))

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        return delegate.contains(t)
                && (lowerBound == null || t.compareTo(lowerBound) >= 0)
                && (upperBound == null || t.compareTo(upperBound) < 0);
    }
    // Оценка сложности: O(log(delegate.size))

    // Оценка памяти: O(log(delegate.size))

    @NotNull
    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        if ((lowerBound == null || t.compareTo(lowerBound) >= 0)
                && (upperBound == null || t.compareTo(upperBound) <= 0)) {
            return delegate.add(t);
        }
        throw new IllegalArgumentException();
    }
    // Оценка сложности: O(log(delegate.size))

    // Оценка памяти: O(log(delegate.size))

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
