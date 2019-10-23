package lesson2;

import kotlin.NotImplementedError;
import kotlin.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("unused")
public class JavaAlgorithms {
    /**
     * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
     * Простая
     *
     * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
     * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
     *
     * 201
     * 196
     * 190
     * 198
     * 187
     * 194
     * 193
     * 185
     *
     * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
     * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
     * Вернуть пару из двух моментов.
     * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
     * Например, для приведённого выше файла результат должен быть Pair(3, 4)
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    static public Pair<Integer, Integer> optimizeBuyAndSell(String inputName) {
        try {
            ArrayList<String> fileContent = (ArrayList<String>) Files.readAllLines(Paths.get(inputName));

            int n = fileContent.size();

            int[] prices = new int[n];
            for (int i = 0; i < n; i++) {
                String line = fileContent.get(i);
                if (!line.matches("\\d+")) {
                    throw new IllegalArgumentException();
                }
                prices[i] = Integer.parseInt(line);
            }

            int globalMax = 0;
            int globalMaxEnd = 0;
            int globalMaxStart = 0;
            int currentStart = 0;
            int[] maxSumByThisTime = new int[n];
            maxSumByThisTime[0] = 0;

            for (int i = 1; i < n; i++) {
                int takeThisDay = maxSumByThisTime[i - 1] + prices[i] - prices[i - 1];

                if (takeThisDay >= 0) {
                    maxSumByThisTime[i] = takeThisDay;
                    if (globalMax < takeThisDay) {
                        globalMax = takeThisDay;
                        globalMaxEnd = i;
                        globalMaxStart = currentStart;
                    }
                } else {
                    currentStart = i;
                }
            }

            return new Pair<>(globalMaxStart + 1, globalMaxEnd + 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Сложность алгоритма O(n)

    // Оценка ресурсоёмкости: O(n), т.к. требуется создать массив, размер которого равен количеству входных данных

    /**
     * Задача Иосифа Флафия.
     * Простая
     *
     * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
     *
     * 1 2 3
     * 8   4
     * 7 6 5
     *
     * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
     * Человек, на котором остановился счёт, выбывает.
     *
     * 1 2 3
     * 8   4
     * 7 6 х
     *
     * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
     * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
     *
     * 1 х 3
     * 8   4
     * 7 6 Х
     *
     * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
     *
     * 1 Х 3
     * х   4
     * 7 6 Х
     *
     * 1 Х 3
     * Х   4
     * х 6 Х
     *
     * х Х 3
     * Х   4
     * Х 6 Х
     *
     * Х Х 3
     * Х   х
     * Х 6 Х
     *
     * Х Х 3
     * Х   Х
     * Х х Х
     *
     * Общий комментарий: решение из Википедии для этой задачи принимается,
     * но приветствуется попытка решить её самостоятельно.
     */
    static public int josephTask(int menNumber, int choiceInterval) {

        int n = 1;
        int res = 1;

        while (n < menNumber) {
            res = (res + choiceInterval - 1) % ++n + 1;
        }

        return res;
    }

    // Сложность алгоритма O(n)
    // Решение взято с Википедии, т.к. сам я смог придумать только алгоритм, работающий за O(n*(n-1)/2)=O(n^2), и он
    // не проходил тесты по времени. Очень бы хотелось узнать, как следовало аналитически выводить формулу
    // joseph(n, choiceInterval) = (joseph(n - 1, choiceInterval) + choiceInterval - 1) % n + 1;
    // решая самостоятельно, я не смог этого сделать, несмотря на то, что расписал таблицу, а на Википедии эта
    // закономерность обозначена как "отчётливо видная в таблице".

    // Оценка ресурсоёмкости: O(1)

    /**
     * Наибольшая общая подстрока.
     * Средняя
     *
     * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
     * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
     * Если общих подстрок нет, вернуть пустую строку.
     * При сравнении подстрок, регистр символов *имеет* значение.
     * Если имеется несколько самых длинных общих подстрок одной длины,
     * вернуть ту из них, которая встречается раньше в строке first.
     */
    static public String longestCommonSubstring(String first, String second) {

        int[][] matrix = new int[first.length()][second.length()];
        int maxLength = 0;
        int maxEndX = 0;
        int maxEndY = 0;

        for (int i = 0; i < first.length(); i++) {
            for (int j = 0; j < second.length(); j++) {
                if (first.charAt(i) == second.charAt((j))) {
                    matrix[i][j] = (i == 0 || j == 0) ? 1 : (matrix[i - 1][j - 1] + 1);
                    if (matrix[i][j] > maxLength) {
                        maxLength = matrix[i][j];
                        maxEndX = i;
                        maxEndY = j;
                    }
                }
            }
        }

        StringBuilder result = new StringBuilder();
        while (maxEndX >= 0 && maxEndY >= 0 && matrix[maxEndX][maxEndY] != 0) {
            result.append(first.charAt(maxEndX--));
            maxEndY--;
        }

        return result.reverse().toString();
    }

    // Сложность алогоритма O(first.length() * second.length())

    // Оценка ресурсоёмкости: O(first.length() * second.length()) из-за матрицы

    /**
     * Число простых чисел в интервале
     * Простая
     *
     * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
     * Если limit <= 1, вернуть результат 0.
     *
     * Справка: простым считается число, которое делится нацело только на 1 и на себя.
     * Единица простым числом не считается.
     */
    static public int calcPrimesNumber(int limit) {
        throw new NotImplementedError();
    }

    /**
     * Балда
     * Сложная
     *
     * В файле с именем inputName задана матрица из букв в следующем формате
     * (отдельные буквы в ряду разделены пробелами):
     *
     * И Т Ы Н
     * К Р А Н
     * А К В А
     *
     * В аргументе words содержится множество слов для поиска, например,
     * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
     *
     * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
     * и вернуть множество найденных слов. В данном случае:
     * ТРАВА, КРАН, АКВА, НАРТЫ
     *
     * И т Ы Н     И т ы Н
     * К р а Н     К р а н
     * А К в а     А К В А
     *
     * Все слова и буквы -- русские или английские, прописные.
     * В файле буквы разделены пробелами, строки -- переносами строк.
     * Остальные символы ни в файле, ни в словах не допускаются.
     */

    private static PrefixTree prefixTree;
    private static String[][] letterMatrix;
    private static int width;
    private static int amountOfLetters;
    private static boolean[][] adjacencyMatrix;
    private static boolean[] visited;
    private static HashSet<String> answer;

    private static void strangeDfs(int letterIndex, TrieNode current) {
        visited[letterIndex] = true;

        for (int i = 0; i < amountOfLetters; i++) {
            if (adjacencyMatrix[letterIndex][i] && !visited[i]) {
                char l = letterMatrix[letterIndex / width][letterIndex % width].charAt(0);

                TrieNode probableNext = prefixTree.nextNode(current, l);

                if (probableNext != null) {
                    if (probableNext.isEndOfWord() != null) {
                        String word = probableNext.isEndOfWord();
                        answer.add(word);
                        prefixTree.delete(word);
                    }
                    strangeDfs(i, probableNext);
                }
            }
        }

        visited[letterIndex] = false;
    }

    static public Set<String> baldaSearcher(String inputName, Set<String> words) {
        try {
            answer = new HashSet<>();
            prefixTree = new PrefixTree();

            // Fill the prefix tree
            for (String word : words) {
                prefixTree.insert(word);
            }

            // Read file
            ArrayList<String> fileContent = (ArrayList<String>) Files.readAllLines(Paths.get(inputName));
            int height = fileContent.size();

            // Convert file content into matrix of letters
            letterMatrix = new String[height][];

            for (int i = 0; i < height; i++) {
                letterMatrix[i] = fileContent.get(i).split(" ");
            }
            width = letterMatrix[0].length;

            // Fill adjacency matrix based on the suggested table
            amountOfLetters = height * width;
            adjacencyMatrix = new boolean[amountOfLetters][amountOfLetters];
            visited = new boolean[amountOfLetters];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int letterNum = i * width + j;

                    if (j != 0) {
                        adjacencyMatrix[letterNum][letterNum - 1] = true;
                        adjacencyMatrix[letterNum - 1][letterNum] = true;
                    }

                    if (j != width - 1) {
                        adjacencyMatrix[letterNum][letterNum + 1] = true;
                        adjacencyMatrix[letterNum + 1][letterNum] = true;
                    }

                    if (i != 0) {
                        adjacencyMatrix[letterNum][letterNum - width] = true;
                        adjacencyMatrix[letterNum - width][letterNum] = true;
                    }

                    if (i != height - 1) {
                        adjacencyMatrix[letterNum][letterNum + width] = true;
                        adjacencyMatrix[letterNum + width][letterNum] = true;
                    }
                }
            }

            for (int i = 0; i < amountOfLetters; i++) {
                int x = i / width;
                int y = i % width;
                if (prefixTree.getRoot().getChildren().containsKey(letterMatrix[x][y].charAt(0))) {
                    strangeDfs(i, prefixTree.getRoot());
                }
            }

            return answer;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Оценка сложности: words.size() * (input.height * input.width) ^ 2

    // Оценка ресурсоёмкости: O(words.size() * longest_word_length)
}
