package lesson3;

import kotlin.NotImplementedError;
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

    // TODO: delete printInfo before sending!!!
    void printInfo() {
        printInfo(root);
        System.out.println();
    }

    private void printInfo(Node<T> node) {
        System.out.println("Node: " + node.value + ", left: " + (node.left == null ? "null" : node.left.value) + ", right: " + (node.right == null ? "null" : node.right.value));
        if (node.left != null) printInfo(node.left);
        if (node.right != null) printInfo(node.right);
    }

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
        boolean deleted = false;
        while (true) {
            Pair<Node<T>, Node<T>> closestWithParent = findWithParent(t);
            Node<T> closest = closestWithParent != null ? closestWithParent.getFirst() : null;
            Node<T> p = closestWithParent != null ? closestWithParent.getSecond() : null;
            // null (if tree is empty)
            // Node that would be a parent of a Node that would the t value if it existed (if the tree doesn't contain it)
            // Node that holds the t value (if the tree contains it)

            if (closestWithParent == null || closest == null || t.compareTo(closest.value) != 0) { // check for situations 1 and 2
                return deleted;
            }

            deleted = true;
            size--;

            // case 1: closest has no right child
            if (closest.right == null) {
                if (p == null) {
                    root = closest.left;
                } else {
                    int comp = p.value.compareTo(closest.value); // 1 if parent > child -> left, -1 if parent < child -> right

                    if (comp > 0) {
                        p.left = closest.left;
                    } else {
                        p.right = closest.left;
                    }
                }
                // __________ END OF CASE 1 _________
            } else {
                // case 2: closest has a right child
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
                    int comp = p.value.compareTo(closest.value); // 1 if parent > child -> left, -1 if parent < child -> right

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
        }
    }

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

    public class BinaryTreeIterator implements Iterator<T> {

        Node<T> current;
        boolean initial;

        private BinaryTreeIterator() {
            if (root != null) {
                initial = true;
                current = root.left == null ? root : root.left;
                while (current.left != null) {
                    current = current.left;
                }
            }
        }

        /**
         * Проверка наличия следующего элемента
         * Средняя
         */
        @Override
        public boolean hasNext() {
            if (root != null) {
                Node<T> mostRight = root.right == null ? root : root.right;
                while (mostRight.right != null) {
                    mostRight = mostRight.right;
                }
                return current != null && (current.value != mostRight.value || current.right != null);
            }
            return false;
        }

        /**
         * Поиск следующего элемента
         * Средняя
         */
        private Node<T> theMostLeft(@NotNull Node<T> start) {
            Node<T> ans = start.right;
            while (ans.left != null) {
                ans = ans.left;
            }

            return ans;
        }

        @Override
        public T next() {
            if (hasNext()) {

                if (initial) {
                    initial = false;
                    return current.value;
                }

                Pair<Node<T>, Node<T>> currentAndParent = findWithParent(current.value);
                assert currentAndParent != null;
                Node<T> cPar = currentAndParent.getSecond();

                if (current.right != null) {
                    Node<T> m = theMostLeft(current);
                    current = m;
                    return m.value;
                }

                if (cPar != null) {

                    int comp = current.value.compareTo(cPar.value); // -1 if parent is bigger -> left; 1 -> right

                    while (comp > 0) {
                        Pair<Node<T>, Node<T>> temp = findWithParent(cPar.value);
                        assert temp != null;
                        Node<T> parPar = temp.getSecond();
                        comp = cPar.value.compareTo(parPar.value);
                        cPar = parPar;
                    }

                    current = cPar;
                    return cPar.value;
                }

            }
            throw new NoSuchElementException();
        }

        /**
         * Удаление слэледующего емента
         * Сложная
         */
        @Override
        public void remove() {
            //TODO
        }
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
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO
        throw new NotImplementedError();
    }

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
