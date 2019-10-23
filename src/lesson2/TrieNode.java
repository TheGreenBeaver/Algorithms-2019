package lesson2;

import java.util.HashMap;

class TrieNode {
    private HashMap<Character, TrieNode> children = new HashMap<>();
    private String endOfWord = null;

    HashMap<Character, TrieNode> getChildren() {
        return children;
    }

    void setEndOfWord(String endOfWord) {
        this.endOfWord = endOfWord;
    }

    String isEndOfWord() {
        return endOfWord;
    }
}
