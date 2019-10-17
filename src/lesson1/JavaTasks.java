package lesson1;

import kotlin.NotImplementedError;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@SuppressWarnings("unused")
public class JavaTasks {

    private static final int SEC_IN_MIN = 60;
    private static final int MIN_IN_HR = 60;
    private static final int SEC_IN_HR = SEC_IN_MIN * MIN_IN_HR;
    /**
     * Сортировка времён
     *
     * Простая
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
     * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
     *
     * Пример:
     *
     * 01:15:19 PM
     * 07:26:57 AM
     * 10:00:03 AM
     * 07:56:14 PM
     * 01:15:19 PM
     * 12:40:31 AM
     *
     * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
     * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
     *
     * 12:40:31 AM
     * 07:26:57 AM
     * 10:00:03 AM
     * 01:15:19 PM
     * 01:15:19 PM
     * 07:56:14 PM
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
     public static void sortTimes(String inputName, String outputName) {
        try {
            ArrayList<String> fileContent = (ArrayList<String>) Files.readAllLines(Paths.get(inputName));

            int[] timeInRawSeconds = new int[fileContent.size()];

            for (int i = 0; i < fileContent.size(); i++) {
                String line = fileContent.get(i);
                if (!line.matches("\\d{2}:\\d{2}:\\d{2} [A,P]M")) {
                    throw new IllegalArgumentException();
                }

                int hours = Integer.parseInt(line.substring(0, 2));
                if (hours > 12 || hours < 1) {
                    throw new IllegalArgumentException();
                }
                int minutes = Integer.parseInt(line.substring(3, 5));
                if (minutes > 59 || minutes < 0) {
                    throw new IllegalArgumentException();
                }
                int seconds = Integer.parseInt(line.substring(6, 8));
                if (seconds > 59 || seconds < 0) {
                    throw new IllegalArgumentException();
                }

                if (line.endsWith("AM")) {
                    if (!line.startsWith("12")) {
                        timeInRawSeconds[i] = SEC_IN_HR * hours + SEC_IN_MIN * minutes + seconds;
                    } else {
                        timeInRawSeconds[i] = SEC_IN_MIN * minutes + seconds;
                    }
                } else {
                    if (!line.startsWith("12")) {
                        timeInRawSeconds[i] =  SEC_IN_HR * (12 + hours) + SEC_IN_MIN * minutes + seconds;
                    } else {
                        timeInRawSeconds[i] = SEC_IN_HR * hours + SEC_IN_MIN * minutes + seconds;
                    }
                }
            }

            Util.mergeSortInt(timeInRawSeconds, 0, timeInRawSeconds.length);

            ArrayList<String> result = new ArrayList<>();
            for (int time : timeInRawSeconds) {
                int hours = time / SEC_IN_HR;
                int minutes = time % SEC_IN_HR / SEC_IN_MIN;
                int seconds = time % SEC_IN_HR % SEC_IN_MIN;

                String marker = "AM";

                if (hours == 0) {
                    hours = 12;
                } else {
                    if (hours >= 12) {
                        marker = "PM";
                        if (hours > 12) {
                            hours -= 12;
                        }
                    }
                }

                result.add(String.format("%02d:%02d:%02d %s", hours, minutes, seconds, marker));
            }

            Files.write(Paths.get(outputName), result, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Оценка сложности: O(nlogn), т.к. самая длительная операция - mergeSort, имеющая именно такую сложность

    /**
     * Сортировка адресов
     *
     * Средняя
     *
     * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
     * где они прописаны. Пример:
     *
     * Петров Иван - Железнодорожная 3
     * Сидоров Петр - Садовая 5
     * Иванов Алексей - Железнодорожная 7
     * Сидорова Мария - Садовая 5
     * Иванов Михаил - Железнодорожная 7
     *
     * Людей в городе может быть до миллиона.
     *
     * Вывести записи в выходной файл outputName,
     * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
     * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
     *
     * Железнодорожная 3 - Петров Иван
     * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
     * Садовая 5 - Сидоров Петр, Сидорова Мария
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    static public void sortAddresses(String inputName, String outputName) {
        try {
            ArrayList<String> fileContent = (ArrayList<String>) Files.readAllLines(Paths.get(inputName));

            HashMap<String, ArrayList<String>> inhabitantsByHouses = new HashMap<>();
            for (String line: fileContent) {
                if (!line.matches(
                        "([А-ЯЁA-Z][а-яёa-z]+-?([А-ЯЁA-Z][а-яёa-z]+)? ){2}- ([А-ЯЁA-Z][а-яёa-z]+-?([А-ЯЁA-Z][а-яёa-z]+)? )\\d+")) {
                    throw new IllegalArgumentException();
                }

                String[] splitLine = line.split(" - ");
                String nameAndSurname = splitLine[0];
                String address = splitLine[1];
                if (inhabitantsByHouses.containsKey(address)) {
                    inhabitantsByHouses.get(address).add(nameAndSurname);
                } else {
                    inhabitantsByHouses.put(address, new ArrayList<>(Collections.singletonList(nameAndSurname)));
                }
            }

            HouseWithInhabitants[] toSortByAddress = new HouseWithInhabitants[inhabitantsByHouses.size()];
            int index = 0;
            for (Map.Entry<String, ArrayList<String>> entry: inhabitantsByHouses.entrySet()) {
                String[] addrSplit = entry.getKey().split(" ");
                Address address = new Address(addrSplit[0], Integer.parseInt(addrSplit[1]));
                toSortByAddress[index] = new HouseWithInhabitants(address, entry.getValue());
                index++;
            }

            Util.mergeSortByAddress(toSortByAddress, 0, toSortByAddress.length);

            ArrayList<String> result = new ArrayList<>();

            for (HouseWithInhabitants hwi: toSortByAddress) {
                StringBuilder builder = new StringBuilder();
                builder.append(hwi.address.street).append(" ").append(hwi.address.house).append(" - ");
                String[] inhS = new String[hwi.inhabitants.size()];
                hwi.inhabitants.toArray(inhS);
                Util.mergeSortString(inhS, 0, inhS.length);
                for (int i = 0; i < inhS.length; i++) {
                    builder.append(inhS[i]);
                    if (i != inhS.length - 1) {
                        builder.append(", ");
                    }
                }
                result.add(builder.toString());
            }

            Files.write(Paths.get(outputName), result, Charset.defaultCharset());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Сортировка температур
     *
     * Средняя
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
     * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
     * Например:
     *
     * 24.7
     * -12.6
     * 121.3
     * -98.4
     * 99.5
     * -12.6
     * 11.0
     *
     * Количество строк в файле может достигать ста миллионов.
     * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
     * Повторяющиеся строки сохранить. Например:
     *
     * -98.4
     * -12.6
     * -12.6
     * 11.0
     * 24.7
     * 99.5
     * 121.3
     */
    static public void sortTemperatures(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Сортировка последовательности
     *
     * Средняя
     * (Задача взята с сайта acmp.ru)
     *
     * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
     *
     * 1
     * 2
     * 3
     * 2
     * 3
     * 1
     * 2
     *
     * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
     * а если таких чисел несколько, то найти минимальное из них,
     * и после этого переместить все такие числа в конец заданной последовательности.
     * Порядок расположения остальных чисел должен остаться без изменения.
     *
     * 1
     * 3
     * 3
     * 1
     * 2
     * 2
     * 2
     */
    static public void sortSequence(String inputName, String outputName) {
        try {
            ArrayList<String> fileContent = (ArrayList<String>) Files.readAllLines(Paths.get(inputName));

            HashMap<Integer, Integer> amountsByNums = new HashMap<>();

            int maxAmount = 0;
            int smallestNumWithMaxAmount = 0;
            ArrayList<Integer> nums = new ArrayList<>();

            for (String line: fileContent) {
                if (!line.matches("\\d+")) {
                    throw new IllegalArgumentException();
                }

                int newAmount;
                int num = Integer.parseInt(line);
                nums.add(num);

                if (amountsByNums.containsKey(num)) {
                    newAmount = amountsByNums.get(num) + 1;
                    amountsByNums.put(num, newAmount);
                } else {
                    newAmount = 1;
                    amountsByNums.put(num, 1);
                }

                if (newAmount > maxAmount || newAmount == maxAmount && num < smallestNumWithMaxAmount) {
                    maxAmount = newAmount;
                    smallestNumWithMaxAmount = num;
                }
            }

            ArrayList<String> result = new ArrayList<>();

            for (int n : nums) {
                if (n != smallestNumWithMaxAmount) {
                    result.add(String.valueOf(n));
                }
            }

            for (int i = 0; i < maxAmount; i++) {
                result.add(String.valueOf(smallestNumWithMaxAmount));
            }

            Files.write(Paths.get(outputName), result, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Соединить два отсортированных массива в один
     *
     * Простая
     *
     * Задан отсортированный массив first и второй массив second,
     * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
     * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
     *
     * first = [4 9 15 20 28]
     * second = [null null null null null 1 3 9 13 18 23]
     *
     * Результат: second = [1 3 4 9 9 13 15 20 23 28]
     */
    static <T extends Comparable<T>> void mergeArrays(T[] first, T[] second) {
        throw new NotImplementedError();
    }
}
