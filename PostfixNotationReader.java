import java.util.*;

public class PostfixNotationReader {

    //Возвращает количество переменных в постфиксной нотации
    static int countVariables(Deque<String> originalQueue) {
        String elem;
        Deque<String> queue = new ArrayDeque<>(originalQueue);
        HashSet<String> set = new HashSet<>(); //Создаётся множество уникальных значений. Повторения игнорируются при попытке их добавления.
        //Пока в копии исходной очереди есть значения:
        //Если значение равно x или y добавить в множество
        //Удалить элемент очереди
        while ((elem = queue.peek()) != null) {
            if (elem.equals("x") || elem.equals("y"))
                set.add(elem);
            queue.remove();
        }
        return (set.size()); //Возврат длины множества
    }

    //Отрицание Лукасевича
    static int	lukasevich_negation(int num, int k) {
        return (k - 1 - num);
    }

    //Отрицание по модулю
    static int diff(int x, int y, int k)
    {
        return (x >= y ? x - y : x - y + k);
    }

    //Вывод информации о работе
    static void printInfo() {
        int group = 4217;
        int number_in_group = 10;

        System.out.println("Выполнил: Никонов Максим");
        System.out.println("Номер группы: " + group);
        System.out.println("Номер в группе: " + number_in_group);
        System.out.println("Вариант функции одного аргумента: " + ((group + number_in_group - 1) % 6 + 1));
        System.out.println("Вариант функции двух аргументов: " + ((group + number_in_group - 1) % 7 + 1));
        System.out.println("Вариант стандартной формы представления: " + ((group + number_in_group - 1) % 3 + 1));

        System.out.println("Отрицание Лукасевича: ~x");
        System.out.println("Разность по модулю -");

    }

    public static void main(String[] args) {

        printInfo();
        Scanner in = new Scanner(System.in); //Создание сканера для чтения данных с консоли

        //Ввод k
        int k = 0;
        do {
            System.out.println("Введите допустимое k: ");
            try {
                k = in.nextInt();
            } catch (Exception e) {
                in.nextLine();
            }
        } while (!(k >= 3));

        //Пока не будет введена корректная функция, повтор ввода
        Deque<String> queue = null;
        in.nextLine();
        do {
            System.out.println("Введите входную функцию.");
            queue = ShuntingYardModified.getPostfixNotation(in.nextLine()); //queue равно null, если ввод не удался
        } while (queue == null);

        int n = countVariables(queue); //Подсчёт количества переменных
        if (n == 0) {
            System.out.println("Переменных должно быть от 1 до 2!");
            return;
        }
        int matrix_size = (int)(Math.pow(k, n)); //Подсчёт строк в матрице значений переменной

        String elem;
        String firstVar = null; //Первая встреченная переменная
        String secondVar = null; //Вторая встреченная переменная

        Deque<Integer[]> stack = new ArrayDeque<>();

        // Пока на входе остались токены
        // Прочитать следующий токен (elem)
        while ((elem = queue.peek()) != null) {
            Integer[] sub = new Integer[matrix_size];

            // Если токен - число
            if (Character.isDigit(elem.charAt(0))) {

                int num = Integer.parseInt(elem) % k;
                //Создать матрицу на matrix_size элементов
                Integer[] subMatrix = new Integer[matrix_size];

                //И заполнить её числами
                for (int i = 0; i < matrix_size; i++)
                    subMatrix[i] = num;

                stack.push(subMatrix); //Добавить её в стэк
            }

            // Если токен - переменная
            else if (elem.equals("x") || elem.equals("y")) {

                //Если первая переменная не определена, присвоить ей значение elem
                if (firstVar == null)
                    firstVar = elem;
                //Если вторая переменная не определена, присвоить ей значение elem
                if (secondVar == null && !elem.equals(firstVar))
                    secondVar = elem;

                Integer[] subMatrix = new Integer[matrix_size];
                //Если текущий элемент очереди первая переменная добавить в стэк матрицу со значениями,
                //например, 0, 1, 2, 0, 1, 2, 0, 1, 2
                if (elem.equals(firstVar)) {
                    for (int i = 0; i < matrix_size; i++)
                        subMatrix[i] = i % k;
                }
                //Если текущий элемент очереди вторая переменная добавить в стэк матрицу со значениями,
                //например, 0, 0, 0, 1, 1, 1, 2, 2, 2
                else {
                    for (int i = 0; i < matrix_size; i++)
                        subMatrix[i] = i / k;
                }
                stack.push(subMatrix);

            // Если токен - функция
            } else if (elem.equals("~") || elem.equals("-")) {
                int arguments = elem.equals("-") ? 2 : 1;

                //Если функция принимает 1 аргумент, взять из стэка матрицу переменной и применить к ней функцию.
                if (arguments == 1) {
                    Integer[] m1 = stack.pop();
                    for (int i = 0; i < matrix_size; i++)
                        sub[i] = lukasevich_negation(m1[i], k);

                // Если функция принимает 2 аргумента, взять из стэка 2 матрицы переменных и применить
                // к ним функцию.
                } else {
                    Integer[] m1 = stack.pop();
                    Integer[] m2 = stack.pop();

                    for (int i = 0; i < matrix_size; i++)
                        sub[i] = diff(m2[i], m1[i], k);
                }
                stack.push(sub); //Добавить в стэк результат
            }
            queue.pop();
        }
        // Например
        // stack = xy+!
        //  sub = x + y
        //  stack = sub
        // stack = sub!
        //  sub = !sub
        // stack = sub
        // По окончанию токенов в очереди в стэке остаётся только одна конечная матрица


        //Циклический вывод элементов в консоль, если переменных одна или две
        if (n == 1) {
            System.out.println(firstVar + " f");
            for (int i = 0; i < matrix_size; i++) {
                System.out.println((i % k) + " " + stack.peek()[i]);
            }
        } else {
            System.out.println(firstVar + " " + secondVar + " f(x)");
            for (int i = 0; i < matrix_size; i++) {
                System.out.println((i % k) + " " + (i / k) + " " + stack.peek()[i]);
            }
        }

        // Вывод СКНФ, если переменных одна или две
        System.out.println("Аналог СКНФ:");
        String sknf = "";
        if (n == 1) {
            for (int i = 0; i < matrix_size; i++) {
                int f = stack.peek()[i]; // i-я строчка результата
                 //Обработка только не максимальных значений
                if (f != k - 1) {
                    //Упрощение нулевых значений и добавление скобок
                    if (f != 0) {
                        sknf += "(";
                        sknf += f + " v ";
                    }
                    sknf += "J_" + i % k + "(" + firstVar + ")";
                    //Упрощение нулевых значений и добавление скобок
                    if (f != 0)
                        sknf += ")";
                    sknf += " & ";
                }
            }
        } else {
            for (int i = 0; i < matrix_size; i++) {
                int f = stack.peek()[i]; // i-я строчка результата
                //Обработка только не максимальных значений
                if (f != k - 1) {
                    sknf += "(";
                    //Упрощение нулевых значений
                    if (f != 0)
                        sknf += f + " v ";
                    sknf += "J_" + i / k + "(" + secondVar + ") v " + "J_" + (i % k) + "(" + firstVar + ")";
                    sknf += ")";
                    sknf += " & ";
                }
            }
        }
        sknf = sknf.substring(0, sknf.length() - 3);
        System.out.println(sknf);

        //Проверка на принадлежность функции классу Е
        //Вводе значений множества Е
        HashSet<Integer> E = new HashSet<>();
        int l = 0;
        do {
            System.out.println("Введите количество элементов множества: ");
            try {
                l = in.nextInt();
            } catch (Exception e) {
                in.nextLine();
            }
        } while (!(l >= 1)); // Пока размер множества не валидный повторять ввод

        do {
            System.out.println("Введите допустимое значение: ");
            try {
                int tmp = in.nextInt();
                if (tmp < 0) {
                    System.out.println("Число должно быть положительным!");
                    continue;
                }
                E.add(tmp);
                l--;
            } catch (Exception e) {
                in.nextLine();
            }
        } while (l > 0); // Пока колчество переменных не равно размеру множества повторять ввод
        System.out.println("Множество E: " + E);

        boolean flag = true; // Изначально предполагается, что функция принадлежит классу T(E)
        if (n == 1) {
            for (int i = 0; i < matrix_size; i++) {
                int f = stack.peek()[i];
                int var1 = i % k;       //Значения одной переменной
                if (E.contains(var1))   //Обработка строк со значениями, входящими в исходное множество
                    if (!E.contains(f)) //Если результат функции не принадлежит множеству, функция определяется как
                                        //не принадлежащая классу E
                        flag = false;
            }
        } else {
            for (int i = 0; i < matrix_size; i++) {
                int f = stack.peek()[i];
                int var1 = i % k; //Значения первой переменной
                int var2 = i / k; //Значения второй переменной
                if (E.contains(var1) && E.contains(var2)) //Обработка строк со значениями, входящими в исходное множество
                    if (!E.contains(f)) //Если результат функции не принадлежит множеству, функция определяется как
                        flag = false;   //не принадлежащая классу E
            }
        }

        if (!flag)
            System.out.println("Функция не принадлежит классу T(E)");
        else
            System.out.println("Функция принадлежит классу T(E)");

    }
}
