import java.util.ArrayDeque;
import java.util.Deque;

public class ShuntingYardModified {

    //Возвращает истину, если символ является переменной
    static private boolean isOperand(char ch) {
        return (ch == 'x' || ch == 'y');
    }

    //Возвращает истину, если символ - функция одного аргумента
    static private boolean isSimpleOperator(char ch) {
        return (ch == '~');
    }

    //Возвращает истину, если символ - функция двух аргументов
    static private boolean isComplexOperator(char ch) {
        return (ch == '-');
    }

    //Возвращает истину, если символ - функция одного аргумента или функция двух аргументов
    static private boolean isOperator(char ch) {
        return (ch == '~' || ch == '-');
    }

    //Возвращает истину, если символ - лево-ассоциативный
    static private boolean isLeftAssociated(Character ch) {
        return (ch == '-');
    }

    //Возвращает приоритет операций
    static private int getPriority(char ch) {
        return (ch == '~' ? 2 : ch == '-' ? 1 : 0);
    }

    /*
        Кончный автомт — математическая абстракция, модель дискретного устройства,
    имеющего один вход, один выход и в каждый момент времени находящегося в одном
    состоянии из множества возможных.

    Алгоритм сортировочной станции:

    Пока не все токены обработаны:

	    Прочитать токен.
	    Если токен — число, то добавить его в очередь вывода.
	    Если токен — функция, то поместить его в стек.
	    Если токен — разделитель аргументов функции (например запятая):

		    Пока токен на вершине стека не открывающая скобка:
		    	Переложить оператор из стека в выходную очередь.
		    	Если стек закончился до того, как был встречен токен открывающая
		    	скобка, то в выражении пропущен разделитель аргументов функции (запятая),
		    	либо пропущена открывающая скобка.

	    Если токен — оператор op1, то:

	    	Пока присутствует на вершине стека токен оператор op2, чей приоритет выше
	    	или равен приоритету op1, и при равенстве приоритетов op1 является левоассоциативным:
	    		Переложить op2 из стека в выходную очередь;
	    	Положить op1 в стек.

	    Если токен — открывающая скобка, то положить его в стек.
	    Если токен — закрывающая скобка:

	    	Пока токен на вершине стека не открывающая скобка
	    		Переложить оператор из стека в выходную очередь.
	    		Если стек закончился до того, как был встречен токен открывающая скобка,
	    		то в выражении пропущена скобка.
	       	Выкинуть открывающую скобку из стека, но не добавлять в очередь вывода.
	    	Если токен на вершине стека — функция, переложить её в выходную очередь.

    Если больше не осталось токенов на входе:

	    Пока есть токены операторы в стеке:

	    	Если токен оператор на вершине стека — открывающая скобка, то в выражении пропущена скобка.
	       	Переложить оператор из стека в выходную очередь.

    Конец.

     */

    //Алгоритм сортировочной станции вместе + модель конечного автомата
    //Преобразует стандартный ввод в обратную польскую запись
    public static Deque<String> getPostfixNotation(String str) {

        States state = States.SIMPLE_OR_OPERAND; //Текущее состояние машины
        Deque<String> stack = new ArrayDeque<>(); //Стэк
        Deque<String> queue = new ArrayDeque<>(); //Очередь вывода

        String elem;
        int  len = str.length();

        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i); // Посимвольная итерация строки
            if (ch != ' ') {

                // Если токен оператор op1, то:
                if (isSimpleOperator(ch)) {

                    if (state != States.SIMPLE_OR_OPERAND) {
                        System.out.println("Не ожидалось отрицание или операнд на позиции " + i + ".");
                        return (null);
                    }

                    // Пока на вершине стека присутствует токен оператор op2,
                    // а также оператор op1 лево-ассоциативный и его приоритет меньше или такой же чем у оператора op2,
                    // или оператор op1 право-ассоциативный и его приоритет меньше чем у оператора op2
                    while ((elem = stack.peek()) != null) {
                        if (isOperator(ch) &&
                                ((isLeftAssociated(ch) && (getPriority(ch) <= getPriority(stack.peek().charAt(0))) ||
                                        (!isLeftAssociated(ch) && (getPriority(ch) < getPriority(stack.peek().charAt(0)))))))
                        {
                            // Переложить оператор op2 из стека в очередь вывода.
                            queue.add(elem);
                            stack.pop();
                        } else
                            break;
                    }

                    // положить в стек оператор op1
                    stack.push(String.valueOf(ch));

                    state = States.SIMPLE_OR_OPERAND;

                // Аналогично предыдущему
                } else if (isComplexOperator(ch)) {

                    if (state != States.COMPLEX_OPERATOR) {
                        System.out.println("Не ожидалось следствие на позиции " + i + ".");
                        return (null);
                    }

                    while ((elem = stack.peek()) != null) {
                        if (isOperator(ch) &&
                                ((isLeftAssociated(ch) && (getPriority(ch) <= getPriority(stack.peek().charAt(0))) ||
                                        (!isLeftAssociated(ch) && (getPriority(ch) < getPriority(stack.peek().charAt(0)))))))
                        {
                            queue.add(elem);
                            stack.pop();
                        } else
                            break ;
                    }
                    stack.push(String.valueOf(ch));

                    state = States.SIMPLE_OR_OPERAND;

                // Если токен - левая круглая скобка, то положить его в стек.
                } else if (ch == '(') {

                    if (state == States.COMPLEX_OPERATOR) {
                        System.out.println("Ожидалось следствие на позиции " + i + ".");
                        return (null);
                    }

                    stack.push(String.valueOf(ch));

                    state = States.SIMPLE_OR_OPERAND;

                // Если токен - правая круглая скобка:
                } else if (ch == ')') {

                    if (state == States.SIMPLE_OR_OPERAND) {
                        System.out.println("Ожидалось отрицание или операнд на позиции " + i + ".");
                        return (null);
                    }

                    // До появления на вершине стека токена "левая круглая скобка"
                    // перекладывать операторы из стека в очередь вывода.
                    int flag = 0;
                    while ((elem = stack.peek()) != null) {
                        if (elem.equals("(")) {
                            flag = 1;
                            break ;
                        } else {
                            queue.add(elem);
                            stack.pop();
                        }
                    }
                    // Если стек кончится до нахождения токена левая круглая скобка, то была пропущена скобка.
                    if (flag == 0) {
                        System.out.println("Несовпадение скобок!");
                        return (null);
                    }
                    stack.pop();

                    state = States.COMPLEX_OPERATOR;

                // Если токен является числом (идентификатором), то добавить его в очередь вывода.
                } else if (Character.isDigit(ch)) {

                    if (state != States.SIMPLE_OR_OPERAND) {
                        System.out.println("Не ожидалось отрицание или операнд на позиции " + i + ".");
                        return (null);
                    }

                    String number = "";
                    //Пока символ число, перейти к следующему символу. Из пройденных символов сотавляется число в строковом виде.
                    while (i < len && Character.isDigit(str.charAt(i))) {
                        number += str.charAt(i);
                        i++;
                    }
                    if (i == len - 1)
                        i--;

                    queue.add(number); // Добавление в очередь вывода

                    state = States.COMPLEX_OPERATOR;

                // Если токен является оператором, то добавить его в очередь вывода.
                } else if (isOperand(ch)) {

                    if (state != States.SIMPLE_OR_OPERAND) {
                        System.out.println("Не ожидалось отрицание или операнд на позиции " + i + ".");
                        return (null);
                    }

                    queue.add(String.valueOf(ch)); // Добавление в очередь вывода

                    state = States.COMPLEX_OPERATOR;

                // Если токен не распознан, вывести сообщение об ошибке и завершить работу.
                } else {
                    System.out.println("Неизвестный токен: " + ch + ".");
                    return (null);
                }
            }
        }

        // Когда не осталось токенов на входе:
        // Если в стеке остались токены:
        while((elem = stack.peek()) != null) {
            if (elem.equals("(") || elem.equals(")")) {
                System.out.println("Несовпадение скобок.");
                return (null);
            }
            queue.add(stack.pop());
        }

        //Возврат обратной польской записи
        return (queue);
    }
}
