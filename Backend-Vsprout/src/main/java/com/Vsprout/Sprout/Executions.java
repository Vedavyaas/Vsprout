package com.Vsprout.Sprout;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.StringTokenizer;

@Component
public class Executions {

    private ArrayList<ArrayList<String>> command;

    public void setCommand(String line) {
        command = new ArrayList<>();
        ArrayList<String> prompt = new ArrayList<>();

        line = line.trim();

        if (line.startsWith("shout")) {
            prompt.add("shout");
            prompt.add(line.substring(5).trim());
        } else if (line.startsWith("judge")) {
            prompt.add("judge");
            prompt.add(line.substring(5).trim());
        } else if (line.startsWith("loop")) {
            prompt.add("loop");
            prompt.add(line.substring(4).trim());
        } else {
            prompt.add("unknown");
            prompt.add(line.trim());
        }

        command.add(prompt);
    }

    public String main() {
        for (ArrayList<String> cmd : command) {
            String keyword = cmd.get(0);
            String value = cmd.get(1);

            switch (keyword) {
                case "shout":
                    return concat(value);
                case "judge":
                    return judge(value);
                case "loop":
                    return loop(value);
                default:
                    return "Error : Unknown command";
            }
        }
        return "Error : No command found";
    }

    private String judge(String value) {
        StringBuilder output = new StringBuilder();

        if (!value.startsWith("{") || !value.endsWith("}")) {
            return "Error : Invalid Syntax.";
        }

        value = value.substring(1, value.length() - 1).trim();
        String[] parts = value.split(";", 2);
        if (parts.length < 2) {
            return "Error : Missing condition or statement.";
        }

        String condition = parts[0].trim();
        String body = parts[1].trim();

        boolean result;
        try {
            result = evaluateCondition(condition);
        } catch (Exception e) {
            return "Error : Invalid condition.";
        }

        if (result) {
            String[] statements = body.split(";");
            for (String line : statements) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Executions inner = new Executions();
                    inner.setCommand(line);
                    String res = inner.main();
                    output.append(res).append("\n");
                }
            }
        }

        return output.toString().trim();
    }

    private String loop(String value) {
        StringBuilder output = new StringBuilder();

        if (!value.startsWith("{") || !value.endsWith("}")) {
            return "Error : Invalid Syntax.";
        }

        value = value.substring(1, value.length() - 1).trim();

        String[] parts = value.split(";", 2);
        if (parts.length < 2) {
            return "Error : Missing loop count or expression.";
        }

        String countStr = parts[0].trim();
        String body = parts[1].trim();

        int count;
        try {
            count = Integer.parseInt(countStr.split("\\s+")[0]);
        } catch (NumberFormatException e) {
            return "Error : Invalid loop count.";
        }

        for (int i = 0; i < count; i++) {
            String[] lines = body.split(";");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Executions inner = new Executions();
                    inner.setCommand(line);
                    String res = inner.main();
                    output.append(res).append("\n");
                }
            }
        }

        return output.toString().trim();
    }

    private String check(String alert, String expression) {
        alert = alert.trim();

        if (alert.startsWith("\"") && alert.endsWith("\"")) {
            return "\"" + alert.substring(1, alert.length() - 1) + "\"";
        } else if ((alert.startsWith("\"") && !alert.endsWith("\"")) || (!alert.startsWith("\"") && alert.endsWith("\""))) {
            return "Error : Must be enclosed in \" \".";
        } else {
            try {
                double result = evaluateExpression(expression);
                return String.valueOf(result);
            } catch (Exception e) {
                return "Error : Invalid expression";
            }
        }
    }

    private String concat(String alert) {
        alert = alert.trim();
        try {
            double result = evaluateExpression(alert);
            return Double.toString(result);
        } catch (Exception e) {
            StringBuilder result = new StringBuilder();
            String[] parts = alert.split("\\+");

            for (String part : parts) {
                String checked = check(part.trim(), part.trim());
                if (checked.startsWith("Error")) return checked;

                if (checked.startsWith("\"") && checked.endsWith("\"")) {
                    result.append(checked, 1, checked.length() - 1);
                } else {
                    result.append(checked);
                }
            }
            return result.toString();
        }
    }

    private double evaluateExpression(String expression) {
        List<String> postfix = toPostfix(expression);
        return evaluatePostfix(postfix);
    }

    private List<String> toPostfix(String expression) {
        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        StringTokenizer tokenizer = new StringTokenizer(expression, "+-*/^() ", true);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty()) continue;

            if (token.matches("\\d+(\\.\\d+)?")) {
                output.add(token);
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (!operators.isEmpty() && operators.peek().equals("(")) {
                    operators.pop();
                }
            }
        }

        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }

        return output;
    }

    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (token.matches("\\d+(\\.\\d+)?")) {
                stack.push(Double.parseDouble(token));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+" -> stack.push(a + b);
                    case "-" -> stack.push(a - b);
                    case "*" -> stack.push(a * b);
                    case "/" -> stack.push(a / b);
                    case "^" -> stack.push(Math.pow(a, b));
                }
            }
        }

        return stack.pop();
    }

    private boolean isOperator(String token) {
        return "+-*/^".contains(token);
    }

    private int precedence(String op) {
        return switch (op) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> 0;
        };
    }

    private boolean evaluateCondition(String condition) {
        condition = condition.replaceAll("\\s+", "");

        String[] ops = {">=", "<=", "==", "!=", ">", "<"};

        for (String op : ops) {
            if (condition.contains(op)) {
                String[] sides = condition.split(Pattern.quote(op));
                if (sides.length != 2) throw new IllegalArgumentException();

                double left = evaluateExpression(sides[0]);
                double right = evaluateExpression(sides[1]);

                return switch (op) {
                    case ">" -> left > right;
                    case "<" -> left < right;
                    case ">=" -> left >= right;
                    case "<=" -> left <= right;
                    case "==" -> left == right;
                    case "!=" -> left != right;
                    default -> false;
                };
            }
        }

        throw new IllegalArgumentException();
    }
}
