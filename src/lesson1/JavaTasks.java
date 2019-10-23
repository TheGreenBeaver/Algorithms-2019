package lesson1;

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
    private static int parsedTime(String line, int start, int end, int lowerLimit, int upperLimit) {
        int ans = Integer.parseInt(line.substring(start, end));
        if (ans > upperLimit || ans < lowerLimit) {
            throw new IllegalArgumentException();
        }
        return ans;
    }

    private static int rawSeconds(String line) {
        if (!line.matches("(\\d{2}:){2}\\d{2} [AP]M")) {
            throw new IllegalArgumentException();
        }

        int hours = parsedTime(line, 0, 2, 1, 12);
        int minutes = parsedTime(line, 3, 5, 0, 59);
        int seconds = parsedTime(line, 6, 8, 0, 59);

        int ans = SEC_IN_MIN * minutes + seconds;
        int h = hours % 12 * SEC_IN_HR;
        ans += (line.endsWith("AM")) ? h : (SEC_IN_HR * 12 + h);

        return ans;
    }

     public static void sortTimes(String inputName, String outputName) {
        try {
            ArrayList<String> fileContent = (ArrayList<String>) Files.readAllLines(Paths.get(inputName));
            Integer[] timeInRawSeconds = new Integer[fileContent.size()];
            int hours;
            int minutes;
            int seconds;

            for (int i = 0; i < fileContent.size(); i++) {
                String line = fileContent.get(i);
                timeInRawSeconds[i] = rawSeconds(line);
            }

            Util.genericMergeSort(timeInRawSeconds);

            ArrayList<String> result = new ArrayList<>();

            for (int time : timeInRawSeconds) {
                hours = time / SEC_IN_HR;
                minutes = time % SEC_IN_HR / SEC_IN_MIN;
                seconds = time % SEC_IN_HR % SEC_IN_MIN;
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
    // Оценка сложности: O(n*log(n)), т.к. самая длительная операция - mergeSort, имеющая именно такую сложность.

    // Оценка ресурсоёмкости O(n), т.к. необходимо создать массив, размер которого зависит от количества входных данных.

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
            String[] initialSort = new String[fileContent.size()];
            fileContent.toArray(initialSort);
            Util.genericMergeSort(initialSort);

            HashMap<String, ArrayList<String>> inhabitantsByHouses = new HashMap<>();
            for (String line: initialSort) {
                if (!line.matches(
                        "([А-ЯЁA-Z][а-яёa-z]+-?([А-ЯЁA-Z][а-яёa-z]+)? ){2}-" +
                                " ([А-ЯЁA-Z][а-яёa-z]+-?([А-ЯЁA-Z][а-яёa-z]+)? )\\d+")) {
                    throw new IllegalArgumentException();
                }

                // RegEx написано так, чтобы удовлевтворять всем случаям, встречающимся в тестовых файлах,
                // однако такие входные данные, как ШпалернаяАвраам или РалфЭддингтон (притом в "Ралф" использована
                // латинская "a") я лично не считаю верными. Так что я думаю, что правильнее было бы записать RegEx,
                // например, так: ([А-ЯЁ][а-яё]+(-[А-ЯЁ][а-яё]+)? ){2}- ([А-ЯЁ][а-яё]+(-[А-ЯЁ][а-яё]+)? )\\d+

                String[] splitLine = line.split(" - ");
                String nameAndSurname = splitLine[0];
                String address = splitLine[1];

//                inhabitantsByHouses
//                        .getOrDefault(
//                                address,
//                                inhabitantsByHouses.put(address, new ArrayList<>(Collections.emptyList()))
//                        ).add(nameAndSurname);
                // Полагаю, Вы хотели увидеть именно такое применение getOrDefault в этой задаче. Оно вполне логично,
                // но есть всего одна небольшая проблема: оно по неизвестным науке причинам не работает; getOrDefault от
                // таких аргументов всегда возвращает пустой список и не находит нужный ключ в таблице, несмотря на то,
                // что, когда я предварительно проверял его наличие с помощью containsKey, получал на выходе true.

//                ArrayList<String> a = inhabitantsByHouses
//                        .getOrDefault(
//                                address,
//                                new ArrayList<>(Collections.emptyList())
//                        );
//                a.add(nameAndSurname);
//                inhabitantsByHouses.put(address, a);
                // Вот так, напротив, работает. Однако каждый раз вызывать put вне зависимости от того, был ли уже
                // нужный ключ в Map или нет, кажется мне как минимум нецелесообразным; к тому же, если я правильно
                // понимаю, put работает за O(Map.size()), что не есть хорошо. Посему я решил оставить код в исходном
                // виде.

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

            Util.genericMergeSort(toSortByAddress);

            ArrayList<String> result = new ArrayList<>();

            for (HouseWithInhabitants hwi: toSortByAddress) {
                StringBuilder builder = new StringBuilder();
                builder.append(hwi.getAddress().toString()).append(" - ");
                ArrayList<String> inhS = hwi.getInhabitants();
                for (int i = 0; i < inhS.size(); i++) {
                    builder.append(inhS.get(i));
                    if (i != inhS.size() - 1) {
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
    // Сложность алгоритма O(l*a*log(a)+k*n*log(n)), где l - максимальное количество букв в адресе, k - максимальное
    // количество букв в имени, a - количество уникальных адресов. В случае, когда в каждом доме живёт лишь по одному
    // человеку, имена не будут нуждаться в сортировке, и формула превратится в O(l*n*log(n)); в среднем случае n>>a, и
    // первое слагаемое оказывается поглощено, так что формула превращается в O(k*n*log(n)).

    // Оценка ресурсоёмкости: O(n), т.к. требуется создать Map, в которой количество ключей равно количеству уникальных
    // адресов, а суммарный размер всех списков-value равен общему количеству входных данных.

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
        try {
            ArrayList<String> fileContent = (ArrayList<String>) Files.readAllLines(Paths.get(inputName));

            int[] amountsOfPossibleTemps = new int[7731];

            double temp;
            int relative;

            for (String line: fileContent) {
                if (!line.matches("-?\\d+\\.\\d")) {
                    throw new IllegalArgumentException();
                }
                temp = Double.parseDouble(line);
                if (temp > 500 || temp < -273) {
                    throw new IllegalArgumentException();
                }

                relative = (int)(temp * 10) + 2730;
                amountsOfPossibleTemps[relative]++;
            }

            String[] result = new String[fileContent.size()];
            int startIndex = 0;

            int realNum;
            boolean negative;
            int integerPart;
            int mantissa;
            int amount;

            for (int num = 0; num < 7731; num++) {
                amount = amountsOfPossibleTemps[num];

                if (amount > 0) {
                    realNum = num - 2730;
                    negative = realNum < 0;
                    integerPart = Math.abs(realNum / 10);
                    mantissa = Math.abs(realNum % 10);

                    StringBuilder toAdd = new StringBuilder();
                    if (negative) {
                        toAdd.append("-");
                    }
                    toAdd.append(integerPart).append(".").append(mantissa);

                    Arrays.fill(result, startIndex, startIndex + amount, toAdd.toString());
                    startIndex += amount;
                }
            }

            Files.write(Paths.get(outputName), Arrays.asList(result), Charset.defaultCharset());

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    // Сложность алгоритма O(n).
    // Оценка ресурсоёмкости: O(1), т.к. при любых входных данных создаётся один и тот же массив с 7731 элементом

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
            int newAmount;
            int num;

            ArrayList<Integer> nums = new ArrayList<>();

            for (String line: fileContent) {
                if (!line.matches("\\d+")) {
                    throw new IllegalArgumentException();
                }

                num = Integer.parseInt(line);
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
    // Библиотечная операция containsKey работает за O(log(n)), весь остальной код умещается в одном цикле, так что
    // сложность алгоритма O(n*log(n)).

    // Оценка ресурсоёмкости: O(n), т.к. требуется создать Map, размер которого равен количеству уникальных элементов
    // во входном массиве и, следовательно, прямо пропорционален его полному размеру

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
        T[] filledSecond = Arrays.copyOfRange(second, first.length, second.length);
        int firstIndex = 0;
        int filledIndex = 0;

        for (int i = 0; i < second.length; i++) {
            if (firstIndex == first.length ||
                    filledIndex < filledSecond.length && filledSecond[filledIndex].compareTo(first[firstIndex]) < 0) {
                second[i] = filledSecond[filledIndex++];
            } else {
                second[i] = first[firstIndex++];
            }
        }
    }

    // Сложность алгоритма O(n).

    // Оценка ресурсоёмкости: O(n), т.к. создаётся массив, содержащий все non-null элементы второго из входных
    // массивов. Можно было бы не создавать этот массив, а просто добавлять все элементы первого массива вместо null
    // элементов второго: в таком случае мы бы получили сортировку на месте и ресурсоёмкость O(1), но потеряли бы в
    // в скорости, будучи вынужденными проводить полную mergeSort за O(n*log(n)).
}
