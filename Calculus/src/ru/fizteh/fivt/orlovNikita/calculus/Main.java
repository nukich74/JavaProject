package ru.fizteh.fivt.orlovNikita.calculus;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    static void isGood(String array) throws Exception {
        int balance = 0;
        for (int i = 0; i < array.length(); i++) {
            if (array.charAt(i) == '(') {
                balance++;
            } else {
                if (array.charAt(i) == ')') {
                    balance--;
                }
            }

        }
        if (!array.matches("[0-9+/\\-%\\*\\s\\(\\)]*")) {
            throw new RuntimeException("There are unknown signs");
        }

        Pattern pattern = Pattern.compile("[0-9]\\s[0-9]");
        Matcher matcher = pattern.matcher(array);
        if (matcher.find()) {
            throw new RuntimeException("Bad number annotation at position " + String.valueOf(matcher.start()));
        }

        if (balance != 0) {
            throw new RuntimeException("No enough brackets");
        }
    }

    static Stack<String> toBackAnnotation(String[] formula) throws Exception {

        Stack<String> stack = new Stack<String>();
        Stack<String> outputAnnotation = new Stack<String>();

        for (String aFormula : formula) {
            if (aFormula.equals("")) {
                continue;
            }
            if (aFormula.equals(")")) {
                while (String.valueOf(stack.peek()).charAt(0) != '(') {
                    outputAnnotation.push(stack.pop());
                }
                stack.pop();
            } else if (aFormula.equals("(")) {
                stack.push("(");
            } else if (aFormula.matches("(\\-[0-9]*)|([0-9]*)") && !aFormula.equals("-")) {
                outputAnnotation.push(aFormula);
            } else if ((aFormula.equals("+")) || (aFormula.equals("-"))
                    || (aFormula.equals("/")) || (aFormula.equals("*"))) {
                if (stack.size() == 0) {
                    stack.push(aFormula);
                } else if (operationPriority(aFormula) > operationPriority(stack.peek())) {
                    stack.push(aFormula);
                } else {
                    while ((stack.size() != 0) &&
                            (operationPriority(stack.peek()) >= operationPriority(aFormula))) {
                        outputAnnotation.push(stack.pop());
                    }
                    stack.push(aFormula);
                }
            } else {
                throw new RuntimeException("Unknown sign\n");
            }
        }

        while (!stack.empty()) {
            outputAnnotation.push(stack.pop());
        }

        return outputAnnotation;
    }

    public static double calcAnnotation(Stack<String> stack) throws Exception {

        Stack<Double> helpStack = new Stack<Double>();
        for (String aStack : stack) {
            if (!aStack.equals("-") && aStack.matches("(\\-?([0-9]*))|[0-9]*]")) {
                helpStack.push(Double.valueOf(aStack));
            } else {
                double a = helpStack.pop(), b = helpStack.pop();

                if ("+".equals(aStack)) {
                    helpStack.push(a + b);
                } else if ("-".equals(aStack)) {
                    helpStack.push(b - a);
                } else if ("/".equals(aStack)) {
                    helpStack.push(b / a);
                } else if ("*".equals(aStack)) {
                    helpStack.push(a * b);
                } else {
                    throw new RuntimeException("Builded wrong annotation\n");
                }
            }
        }
        return helpStack.pop();
    }


    public static void main(String[] args) {
        try {
            //#TO TEST using file: Scanner in = new Scanner(new File("input.txt"));
            String s = "";
            for (String arg : args) {
                s += arg;
            }
            //#TO TEST using file: String s = in.nextLine();
            isGood(s);
            String[] array = parseString(s);


            double result = calcAnnotation(toBackAnnotation(array));
            System.out.println(result);
        } catch (Exception e) {
            System.out.print(e.getMessage());
            System.exit(1);
        }
    }

    private static String[] parseString(String s) {
        String tmp = s.replaceAll(" ", "");
        return tmp.split("(?<=[\\(\\)])|" +
                "(?=[\\(\\)])|" +
                "((?=[+\\*/\\-])(?<=[0-9]*))|" +
                "((?<=[+\\*/])(?=[0-9]))|" +
                "((?<=[+\\-/\\*])(?=\\-[0-9]*))|" +
                "((?<=[0-9]*\\-)(?=[0-9]*))"
        );
    }

    public static int operationPriority(String c) {
        if (c.equals("+")) {
            return 2;
        } else if (c.equals("-")) {
            return 2;
        } else if (c.equals("/")) {
            return 3;
        } else if (c.equals("*")) {
            return 3;
        } else if (c.equals("(")) {
            return 1;
        } else {
            throw new RuntimeException("Error while building back annotation\n");
        }
    }
}