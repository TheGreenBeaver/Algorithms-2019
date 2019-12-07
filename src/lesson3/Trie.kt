package lesson3

import org.jetbrains.annotations.NotNull
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.NoSuchElementException

class Trie : AbstractMutableSet<String>(), MutableSet<String> {
    override var size: Int = 0
        private set

    class Node {
        val children: MutableMap<Char, Node> = linkedMapOf()
    }

    private var root = Node()

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     * Сложная
     */
    class TrieIterator constructor(root: Node, private val trie: Trie) : MutableIterator<String> { // TODO: refresh!!!
        private val words = ArrayList<String>()
        private var current = 0

        private val stringBuilder = StringBuilder()

        private fun fill(start: MutableMap.MutableEntry<Char, Node>) {
            if (start.key == 0.toChar()) {
                words.add(stringBuilder.toString())
                return
            }
            stringBuilder.append(start.key)
            for (child in start.value.children) {
                fill(child)
            }
            stringBuilder.delete(stringBuilder.length - 1, stringBuilder.length)
        }

        init {
            words.clear()
            if (root.children.isNotEmpty()) {
                for (rootChild in root.children) {
                    fill(rootChild)
                }
            }
        }

        override fun hasNext(): Boolean {
            return current < words.size
        }

        override fun next(): String {
            if (hasNext()) {
                return words[current++]
            }
            throw NoSuchElementException()
        }

        override fun remove() {
            if (current != 0) {
                trie.remove(words[current - 1])
                words.removeAt(current - 1)
            }
            throw IllegalStateException()
        }
    }

    @NotNull
    override fun iterator(): MutableIterator<String> {
        return TrieIterator(root, this)
    }
}