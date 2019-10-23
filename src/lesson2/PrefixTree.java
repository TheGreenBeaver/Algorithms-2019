package lesson2;

class PrefixTree {

    private TrieNode root = new TrieNode();

    TrieNode getRoot() {
        return root;
    }

    void insert(String word) {
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            current = current.getChildren().computeIfAbsent(word.charAt(i), c -> new TrieNode());
        }

        current.setEndOfWord(word);
    }

    TrieNode nextNode(TrieNode current, char letter) {
        return current.getChildren().getOrDefault(letter, null);
    }

    void delete(String word) {
        delete(root, word, 0);
    }

    private boolean delete(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (current.isEndOfWord() == null) {
                return false;
            }

            current.setEndOfWord(null);
            return current.getChildren().isEmpty();
        }

        char letter = word.charAt(index);
        TrieNode node = current.getChildren().get(letter);
        if (node == null) {
            return false;
        }

        boolean shouldDeleteCurrentNode = delete(node, word, index + 1) && node.isEndOfWord() == null;

        if (shouldDeleteCurrentNode) {
            current.getChildren().remove(letter);
            return current.getChildren().isEmpty();
        }
        return false;
    }
}
