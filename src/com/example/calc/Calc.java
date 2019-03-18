package com.example.calc;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import java.util.StringTokenizer;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class Calc {
    public JFrame window = new JFrame("Calculator");
    public JTextField input = new JTextField();

    public Calc() {    // устанавливаем параметры окна
        window.setSize(290, 405);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(null);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        input_area();       // подготавливаем окно ввода и обработчик клавиатуры
        buttonPad();     // подготовка кнопок
        window.setVisible(true);
    }

    private void input_area() {
        input.setBounds(16, 10, 248, 36);
        input.setBackground(Color.white);
        input.setHorizontalAlignment(JTextField.RIGHT);
        window.add(input);

    }


    private void buttonPad() {
        int num = 0;
        String arr[] = {"1", "2", "3", "С", "4", "5", "6", "*", "7", "8", "9", "-", "0", ".", "+", "/", "(", ")", "="};
        JButton[] jbutton_array = new JButton[arr.length];  // создаем массив кнопок

        for (int e = 0; e < 5; e++) {
            for (int r = 0; r < 4; r++) {
                jbutton_array[num] = new JButton();
                jbutton_array[num].setText(arr[num]);
                jbutton_array[num].setMargin(new Insets(0, 0, 0, 0));
                if (num < arr.length - 1) {
                    jbutton_array[num].setBounds(16 + r * 62, 55 + e * 62, 60, 60);
                } else {
                    jbutton_array[num].setBounds(16 + r * 62, 55 + e * 62, 122, 60);
                }
                jbutton_array[num].setFocusable(false);
                window.add(jbutton_array[num]);

                // Слушаем события кнопок
                ActionListener num_button = new GoNumListener();
                jbutton_array[num].addActionListener(num_button);

                if (num < arr.length - 1) {
                    num++;
                } else {
                    break;
                }
            }
        }
    }

       public class GoNumListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = ((JButton) e.getSource()).getText();  // Обрабатываем нажатия кнопок

            if (name != "=" || name != "С") {
                input.setText(input.getText() + name);
            }

            if (name == "=") {  // Запускаем подсчет результата
                String result = result(input.getText());
                input.setText(input.getText() + result);
            }

            if (name == "С") {  // Очистка ввода
                input.setText("");
            }
            window.repaint();   // перерисовываем окно
        }
    }

    // Eval опасен. Но так клево с ним получается. :)
    /*private void result() {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        try {
            input.setText("" + engine.eval(input.getText()));
        } catch (ScriptException e1) {
            input.setText("Некорректный ввод");
        }
    }*/

    private String result(String formula) {
       formula = formula.replaceAll("[^0-9,.\\-+*()\\/]+", ""); //чистим ввод от барахла
       formula = formula.replaceAll(",", "."); //запятые не нужны
        double result = 0;
        try {
            result = evaluate(formula);
        } catch (Exception e) {
            e.printStackTrace();
            return "Divison by zero!";
        }

        return String.valueOf(result);
    }


    private static double evaluate(String expression)throws Exception {
        boolean negative = false;
        Stack<Double> operands = new Stack<Double>(); // стек операнд
        Stack<String> operators = new Stack<String>(); // стек операторов
        StringTokenizer stringTokenizer = new StringTokenizer(expression, "()*/+-", true); // разбили строку на токены

        while (stringTokenizer.hasMoreTokens()) {   //идем по токенам
            String s = stringTokenizer.nextToken();
            try {Double digit = Double.parseDouble(s); //пробуем его на принадлежность к числам
                 operands.push(digit);//если получится - суем в числа
                 continue;
            } catch (Exception e) {} //ловим исключение. Если оно выпало - значит мы суем что-то из операторов}

            //TODO обработка минусов в начале строки, и после скобок

            if(operators.isEmpty()){
                operators.push(s);
                continue;}

            if(s.equals("(")){

                if (operators.peek().equals("-")){
                negative = true;
                operators.pop();
                operators.push("+");}

            operators.push(s);
            continue;} //открывающую - всегда в стек

            if(s.equals(")")){
                while (!operators.peek().equals("(")){
                    performOperation(operands,operators.pop());
            }
                operators.pop();
                if (negative){
                    Double temp = operands.pop()*(-1);
                    operands.push(temp);}
                continue;
            }

            if(precedence(operators.peek())<precedence(s)){
                operators.push(s);
                continue;} //выше по приоритету - в стек

            if(precedence(operators.peek())>=precedence(s)){
                performOperation(operands, operators.pop()); //ниже или равен по приоритету - вычисляем, засовываем тот оператор, на котором остановились
                operators.push(s);
                continue;}
        }

        while (!operators.isEmpty()){
            performOperation(operands, operators.pop());
        }
        return operands.peek();
    }

    private static int precedence(String operator){// считаем приоритет оператора
        switch (operator) {
            case "*":
            case "/":
                return 2;
            case "+":
            case "-":
                return 1;
            default:
                return 0;
        }
    }

    private static void performOperation(Stack<Double> operands, String operator) throws Exception {
        double operand2 = operands.pop();
        double operand1 = operands.pop();
        double result = 0;
        switch (operator){
            case "+":
                result = operand1 + operand2;
               // System.out.println(operand1 +"+" + operand2);
                break;
            case "-":
                result = operand1 - operand2;
               // System.out.println(operand1+"-"+operand2);
                break;
            case "*":
                result = operand1 * operand2;
               // System.out.println(operand1+"*"+operand2);
                break;
            case "/":
                if (operand2!=0){
                result = operand1 / operand2;
                //System.out.println(operand1+"/"+operand2);
                }
                else {throw new ArithmeticException("Division by zero!");
                }

                break;
        }
        System.out.println(result);
        operands.push(result);
    }

    public static void main(String[] args) {
        new Calc();
    }
}