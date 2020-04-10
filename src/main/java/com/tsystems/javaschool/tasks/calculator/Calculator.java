package com.tsystems.javaschool.tasks.calculator;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Calculator {

    /**
     * Evaluate statement represented as string.
     *
     * @param statement mathematical statement containing digits, '.' (dot) as decimal mark,
     *                  parentheses, operations signs '+', '-', '*', '/'<br>
     *                  Example: <code>(1 + 38) * 4.5 - 1 / 2.</code>
     * @return string value containing result of evaluation or null if statement is invalid
     */

    public String evaluate(String statement) {
        // note that this calculator do not recognize the substring "()" as an error, it just ignore it
        if(statement == null) {
            return null;
        }

        final char LEFT_PAREN   = '(';
        final char RIGHT_PAREN  = ')';
        final char PLUS         = '+';
        final char MINUS        = '-';
        final char TIMES        = '*';
        final char DIVISION     = '/';
        final char DOT          = '.';

        List<String> values = new ArrayList<>();
        // we will read 'statements' string as tokens

        StringBuilder tempString = new StringBuilder(); // this temp string will accumulate digits of numbers in input string
        char tempChar;  // this temp char will store operators

        String s = statement.replaceAll(" |\\(\\)", "");
        // just in case - delete all whitespaces and "()": it is, let's be honest, not an error, just misprint

        for (int i = 0; i < s.length(); i++) {
            tempChar = s.charAt(i); // reading symbol-by-symbol

            // if 'tempChar' is operator or parenthesis
            if (tempChar == PLUS || tempChar == MINUS || tempChar == TIMES || tempChar == DIVISION || tempChar == LEFT_PAREN || tempChar == RIGHT_PAREN) {
                // we will add number stored in 'tempString' - if it exists
                if (!tempString.toString().equalsIgnoreCase("")) {
                    values.add(tempString.toString());
                    tempString.setLength(0);
                }
                // and check for "double operator"
                if(tempChar != LEFT_PAREN && tempChar != RIGHT_PAREN) {
                    if (!values.isEmpty()) {
                        if (values.get(values.size() - 1).equalsIgnoreCase(String.valueOf(tempChar))) {
                            return null;
                        }
                    }
                }
                // if checked, add it as token
                values.add(String.valueOf(tempChar));
            } else {    // if 'tempChar' is an another symbol
                // if 'tempChar' is not a digit or '.'
                if ((tempChar < '0' || tempChar > '9') && tempChar != DOT) {
                    return null;
                }
                // if there are two '.' in a row
                if(!tempString.toString().equalsIgnoreCase("") && tempString.charAt(tempString.length() - 1) == tempChar && tempChar == DOT){
                    return null;
                }
                tempString.append(tempChar);
            }
        }

        if(!tempString.toString().equalsIgnoreCase("")) {
            values.add(tempString.toString());
        }

        // "tokenizing" ended. evaluation part is divided by two stages: preparation and evaluation
        try {
            // we need two stacks and one queue. i decided not to implement my own.

            // this stack is needed for storing operations
            // and kicking out parenthesis
            Stack<Character> stack = new Stack<>();

            // this stack is needed for computing operations.
            // it contains result(s) of partial and total evaluations
            // used only in evaluation stage
            Stack<String> stackForComputing = new Stack<>();

            // this queue is used to "reverse Polish notation" of evaluation string
            ConcurrentLinkedQueue<String> polishStack = new ConcurrentLinkedQueue<>();

            // preparing stage!
            for (String value: values) {
                switch (value.charAt(0)) {
                    // if ')' then we should retrieve everything between that parenthesis and last '('
                    case RIGHT_PAREN: {
                        while (!stack.isEmpty() && stack.peek() != LEFT_PAREN) {
                            polishStack.add(Character.toString(stack.pop()));
                        }
                        stack.pop(); // kick out last '('
                        break;
                    }
                    // if '(' - just stack it
                    case LEFT_PAREN: {
                        stack.push(LEFT_PAREN);
                        break;
                    }
                    // if operator of lowest priority ('+' or '-') - unstack everything that is not operator from 'stack' to 'polishStack'
                    case PLUS: {
                        while (!stack.isEmpty() && (stack.peek() == PLUS || stack.peek() == MINUS || stack.peek() == TIMES || stack.peek() == DIVISION))
                            polishStack.add(Character.toString(stack.pop()));
                        stack.push(PLUS);
                        break;
                    }
                    // same
                    case MINUS: {
                        while (!stack.isEmpty() && (stack.peek() == PLUS || stack.peek() == MINUS || stack.peek() == TIMES || stack.peek() == DIVISION))
                            polishStack.add(Character.toString(stack.pop()));
                        stack.push(MINUS);
                        break;
                    }
                    // if operator of highest priority ('*' or '/') - unstack everything that is not operator of same priority from 'stack' to 'polishStack'
                    case TIMES: {
                        if (!stack.isEmpty())
                            while (!stack.isEmpty() && (stack.peek() == DIVISION || stack.peek() == TIMES))
                                polishStack.add(Character.toString(stack.pop()));
                        stack.push(TIMES);
                        break;
                    }
                    case DIVISION: {
                        if (!stack.isEmpty())
                            while (!stack.isEmpty() && (stack.peek() == TIMES || stack.peek() == DIVISION))
                                polishStack.add(Character.toString(stack.pop()));
                        stack.push(DIVISION);
                        break;
                    }
                    // if not operator => it is operand => just push it
                    default: {
                        polishStack.add(value);
                    }
                }
            }
            // if something left in 'stack', -> to 'polishStack'
            while (!stack.isEmpty()) {
                polishStack.add(Character.toString(stack.pop()));
            }

            // evaluation stage!
            double a, b;    // it is not safe, but i haven't decided how not to create new double every iteration
            while (polishStack.size() > 0) {
                String temp = polishStack.poll();
                switch (temp.charAt(0)) {
                    // if operator - do what operator should do
                    case PLUS: {
                        a = Double.parseDouble(stackForComputing.pop());
                        b = Double.parseDouble(stackForComputing.pop());
                        stackForComputing.push(Double.toString(a + b));
                        break;
                    }
                    case MINUS: {
                        a = Double.parseDouble(stackForComputing.pop());
                        b = Double.parseDouble(stackForComputing.pop());
                        stackForComputing.push(Double.toString(b - a));
                        break;
                    }
                    case TIMES: {
                        a = Double.parseDouble(stackForComputing.pop());
                        b = Double.parseDouble(stackForComputing.pop());
                        stackForComputing.push(Double.toString(a * b));
                        break;
                    }
                    case DIVISION: {
                        a = Double.parseDouble(stackForComputing.pop());
                        b = Double.parseDouble(stackForComputing.pop());
                        // divisionByZero evasion
                        if(a == 0){
                            return null;
                        }
                        // not a/b - because REVERSE Polish notation :)
                        stackForComputing.push(Double.toString(b / a));
                        break;
                    }
                    default: {
                        // if not operator, that means there is operand, we need it in 'stackForComputing'
                        stackForComputing.push(temp);
                    }
                }
            }

            double result = Double.parseDouble(stackForComputing.pop());

            result = Math.round(result * 10_000) / 10_000.; // it is kinda rounding

            if (result == (int)result)
                //  it is strange that in case of integer answer we should return int value instead of float
                return String.valueOf((int)result);
            return String.valueOf(result);
        } catch (NumberFormatException nfe){ // if there are too many opening parenthesis, we have this type of exception
            return null;
        } catch (EmptyStackException ese){  // if there are too many closing parenthesis, we have this type of exception
            return null;
        }
    }
}
