package lesson3;

import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Attention: comparable supported but comparator is not
public class BinaryTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> {
        final T value;

        Node<T> left = null;

        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;
    private boolean changed = false;

    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        } else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        } else {
            assert closest.right == null;
            closest.right = newNode;
        }
        size++;
        changed = true;
        return true;
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    public int height() {
        return height(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    private int height(Node<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     */
    @Override
    public boolean remove(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Pair<Node<T>, Node<T>> closestWithParent = findWithParent(t);
        Node<T> closest = closestWithParent != null ? closestWithParent.getFirst() : null;
        Node<T> p = closestWithParent != null ? closestWithParent.getSecond() : null;

        if (closestWithParent == null || closest == null || t.compareTo(closest.value) != 0) {
            changed = false;
            return false;
        }

        size--;

        if (closest.right == null) {
            if (p == null) {
                root = closest.left;
            } else {
                int comp = p.value.compareTo(closest.value);

                if (comp > 0) {
                    p.left = closest.left;
                } else {
                    p.right = closest.left;
                }
            }
        } else {
            Node<T> mostLeft = closest.right;
            Node<T> mostLeftP = null;

            while (mostLeft.left != null) {
                mostLeftP = mostLeft;
                mostLeft = mostLeft.left;
            }

            if (p == null) {
                root = new Node<>(mostLeft.value);
                root.left = closest.left;
                if (mostLeftP != null) {
                    mostLeftP.left = mostLeft.right;
                    root.right = closest.right;
                } else {
                    root.right = closest.right.right;
                }
            } else {
                int comp = p.value.compareTo(closest.value);

                if (comp > 0) {
                    p.left = new Node<>(mostLeft.value);
                    p.left.left = closest.left;
                    if (mostLeftP != null) {
                        mostLeftP.left = mostLeft.right;
                        p.left.right = closest.right;
                    } else {
                        p.left.right = closest.right.right;
                    }
                } else {
                    p.right = new Node<>(mostLeft.value);
                    p.right.left = closest.left;
                    if (mostLeftP != null) {
                        mostLeftP.left = mostLeft.right;
                        p.right.right = closest.right;
                    } else {
                        p.right.right = closest.right.right;
                    }
                }
            }
        }

        changed = true;
        return true;
    }
    // Оценка сложности: O(height) (поиск удаляемого элемента и его предка в худшем случае займёт именно столько)

    // Оценка используемой памяти: O(height) (рекурсивный алгоритм поиска)

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    private Pair<Node<T>, Node<T>> findWithParent(T value) {
        if (root == null) {
            return null;
        }
        parent = null;
        return findWithParent(root, value);
    }

    private Node<T> parent;

    private Pair<Node<T>, Node<T>> findWithParent(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return new Pair<>(start, parent);
        }

        if (comparison < 0) {
            if (start.left == null) {
                return new Pair<>(start, parent);
            }
            parent = start;
            return findWithParent(start.left, value);
        }

        if (start.right == null) return new Pair<>(start, parent);
        parent = start;
        return findWithParent(start.right, value);
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        } else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    private ArrayList<T> asList = new ArrayList<>();

    private void fill(@NotNull Node<T> start) {
        if (start.left != null) {
            fill(start.left);
        }
        asList.add(start.value);
        if (start.right != null) {
            fill(start.right);
        }
    }

    ArrayList<T> fill() {
        asList.clear();
        fill(root);
        return asList;
    }

    public class BinaryTreeIterator implements Iterator<T> {

        int current;

        private void refresh() {
            if (changed) {
                T old = asList.get(current != 0 ? current - 1 : current);
                if (root != null) {
                    fill();
                    int newIndex = binarySearch(old);
                    current = (newIndex != 0 && asList.get(newIndex).compareTo(old) > 0) ? newIndex : newIndex + 1;
                }

                changed = false;
            }
        }

        private BinaryTreeIterator() {
            if (root != null) {
                fill();
                current = 0;
                changed = false;
            }
        }
        // Оценка сложности: O(size)

        // Оценка памяти: O(size) (рекурсия, а затем хранение asList)

        /**
         * Проверка наличия следующего элемента
         * Средняя
         */
        @Override
        public boolean hasNext() {
            refresh();
            return root != null && current < asList.size();
        }
        // Оценка сложности: O(1) (O(size), если дерево было изменено после предыдущей итерации)

        // Оценка памяти: O(1) (O(size), если дерево было изменено после предыдущей итерации)

        /**
         * Поиск следующего элемента
         * Средняя
         */

        @Override
        public T next() {
            if (hasNext()) {
                return asList.get(current++);
            }
            throw new NoSuchElementException();
        }
        // Оценка сложности: O(1) (O(size), если дерево было изменено после предыдущей итерации)

        // Оценка памяти: O(1) (O(size), если дерево было изменено после предыдущей итерации)

        /**
         * Удаление следующего емента
         * Сложная
         */

        private int binarySearch(T toSearch) {
            return Util.binarySearch(toSearch, asList);
        }

        @Override
        public void remove() {
            refresh();
            if (current != 0 && current <= asList.size()) {
                T toRemove = asList.get(current - 1);
                int start = binarySearch(toRemove);
                current = start;
                asList.remove(start);
                BinaryTree.this.remove(toRemove);
                changed = false;
            } else {
                throw new IllegalStateException();
            }
        }

        // Оценка сложности: O(size)

        // Оценка памяти: O(log(size)) = O(height) (рекурсия при бинарном поиске)
        // (O(size), если дерево было изменено после предыдущей итерации)
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinaryTreeIterator();
    }

    @Override
    public int size() {
        return size;
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (fromElement.compareTo(toElement) < 0) {
            return new BoundSortedSet<>(this, fromElement, toElement);
        }
        throw new IllegalArgumentException();
    }

    // Оценка сложности: O(1)

    // Оценка памяти: O(1)

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        return new BoundSortedSet<>(this, null, toElement);
    }
    // Оценка сложности: O(1)

    // Оценка памяти: O(1)

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return new BoundSortedSet<>(this, fromElement, null);
    }
    // Оценка сложности: O(1)

    // Оценка памяти: O(1)

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }
}
