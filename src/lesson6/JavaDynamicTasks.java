package lesson6;

import kotlin.NotImplementedError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class JavaDynamicTasks {
    /**
     * Наибольшая общая подпоследовательность.
     * Средняя
     * <p>
     * Дано две строки, например "nematode knowledge" и "empty bottle".
     * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
     * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
     * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
     * Если общей подпоследовательности нет, вернуть пустую строку.
     * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
     * При сравнении подстрок, регистр символов *имеет* значение.
     */
    public static String longestCommonSubSequence(String first, String second) {
        int height = second.length();
        int width = first.length();
        StringBuilder ansBuilder = new StringBuilder();
        int[][] matrix = new int[height][width];
        matrix[0][0] = first.charAt(0) == second.charAt(0) ? 1 : 0;
        if (height > 1) {
            for (int i = 1; i < height; i++) {
                matrix[i][0] = first.charAt(0) == second.charAt(i) || matrix[i - 1][0] == 1 ? 1 : 0;
            }
        }
        if (width > 1) {
            for (int i = 1; i < width; i++) {
                matrix[0][i] = first.charAt(i) == second.charAt(0) || matrix[0][i - 1] == 1 ? 1 : 0;
            }
        }
        if (height > 1 && width > 1) {
            for (int i = 1; i < height; i++) {
                for (int j = 1; j < width; j++) {
                    int prevV = matrix[i - 1][j];
                    int prevH = matrix[i][j - 1];
                    int prevD = matrix[i - 1][j - 1];
                    if (first.charAt(j) == second.charAt(i)) {
                        matrix[i][j] = prevD + 1;
                    } else {
                        matrix[i][j] = Math.max(prevH, prevV);
                    }
                }
            }
        }
        int l = matrix[height - 1][width - 1];
        if (l == 0) {
            return "";
        }
        for (int i = height - 1; i >= 0; i--) {
            for (int j = width - 1; j >= 0; j--) {
                if (matrix[i][j] == l
                        && (l == 1
                        && (j == 0 && i == 0
                        || j == 0 && matrix[i - 1][j] == 0
                        || i == 0 && matrix[i][j - 1] == 0)
                        || i > 0 && j > 0
                        && matrix[i][j] != matrix[i - 1][j]
                        && matrix[i][j] != matrix[i][j - 1]
                        && matrix[i][j] == matrix[i - 1][j - 1] + 1)) {
                    l--;
                    ansBuilder.append(second.charAt(i));
                    i--;
                }
                if (l == 0) {
                    break;
                }
            }
        }
        return ansBuilder.reverse().toString();
    }

    // Сложность алгоритма: O(first.length() * second.length())

    // Оценка ресурсоёмкости: O(first.length() * second.length())

    /**
     * Наибольшая возрастающая подпоследовательность
     * Сложная
     * <p>
     * Дан список целых чисел, например, [2 8 5 9 12 6].
     * Найти в нём самую длинную возрастающую подпоследовательность.
     * Элементы подпоследовательности не обязаны идти подряд,
     * но должны быть расположены в исходном списке в том же порядке.
     * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
     * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
     * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
     */
    public static List<Integer> longestIncreasingSubSequence(List<Integer> list) {
        throw new NotImplementedError();
    }

    /**
     * Самый короткий маршрут на прямоугольном поле.
     * Средняя
     * <p>
     * В файле с именем inputName задано прямоугольное поле:
     * <p>
     * 0 2 3 2 4 1
     * 1 5 3 4 6 2
     * 2 6 2 5 1 3
     * 1 4 3 2 6 2
     * 4 2 3 1 5 0
     * <p>
     * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
     * В каждой клетке записано некоторое натуральное число или нуль.
     * Необходимо попасть из верхней левой клетки в правую нижнюю.
     * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
     * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
     * <p>
     * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
     */
    public static int shortestPathOnField(String inputName) throws IOException {
        ArrayList<String> fileContent = (ArrayList<String>) Files.readAllLines(Paths.get(inputName));
        int[][] field = new int[fileContent.size()][];
        for (int i = 0; i < fileContent.size(); i++) {
            String[] s = fileContent.get(i).split(" ");
            field[i] = new int[s.length];
            for (int j = 0; j < s.length; j++) {
                field[i][j] = Integer.parseInt(s[j]);
            }
        }

        int width = field[0].length;
        int height = field.length;

        int[][] paths = new int[height][width];
        for (int i = 1; i < height; i++) {
            paths[i][0] = paths[i - 1][0] + field[i][0];
        }
        for (int i = 1; i < width; i++) {
            paths[0][i] = paths[0][i - 1] + field[0][i];
        }

        for (int i = 1; i < height; i++) {
            for (int j = 1; j < width; j++) {
                paths[i][j] = Math.min(Math.min(paths[i - 1][j], paths[i][j - 1]), paths[i - 1][j - 1]) + field[i][j];
            }
        }

        return paths[height - 1][width - 1];
    }

    // Сложность алгоритма: O(width * height)

    // Оценка ресурсоёмкости: O(width * height)


    // Задачу "Максимальное независимое множество вершин в графе без циклов"
    // смотрите в уроке 5
}
