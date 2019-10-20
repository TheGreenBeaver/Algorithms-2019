package lesson2;

import kotlin.NotImplementedError;
import kotlin.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

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

            int[] diffs = new int[n];
            diffs[0] = 0;
            for (int i = 1; i < n; i++) {
                diffs[i] = prices[i] - prices[i - 1];
            }

            boolean[] added = new boolean[n];
            int globalMax = 0;
            int globalMaxEnd = 0;
            int[] maxSumByThisTime = new int[n];
            maxSumByThisTime[0] = 0;

            for (int i = 1; i < prices.length; i++) {
                int takeThisDay = maxSumByThisTime[i - 1] + diffs[i];

                if (takeThisDay >= 0) {
                    maxSumByThisTime[i] = takeThisDay;
                    added[i] = true;
                    if (globalMax < takeThisDay) {
                        globalMax = takeThisDay;
                        globalMaxEnd = i;
                    }
                }
            }

            int index = globalMaxEnd;
            while (added[index]) {
                index--;
            }

            return new Pair<>(index + 1, globalMaxEnd + 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Сложность алгоритма O(n)

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

        ArrayList<String> letters = new ArrayList<>();
        while (maxEndX >= 0 && maxEndY >= 0 && matrix[maxEndX][maxEndY] != 0) {
            letters.add(String.valueOf(first.charAt(maxEndX--)));
            maxEndY--;
        }

        StringBuilder result = new StringBuilder();
        for (int i = letters.size() - 1; i >= 0; i--) {
            result.append(letters.get(i));
        }

        return result.toString();
    }

    // Сложность алогоритма O(first.length() * second.length())

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
    static public Set<String> baldaSearcher(String inputName, Set<String> words) {
        throw new NotImplementedError();
    }
}
