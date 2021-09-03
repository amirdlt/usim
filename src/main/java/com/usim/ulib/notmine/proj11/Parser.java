package com.usim.ulib.notmine.proj11;

import java.util.*;

import static java.lang.Math.*;

public final class Parser {
    private final Map<String, Number> variables;
    private static final List<Character> supportedOperations = List.of('+', '-', '/', '*');
    private static final List<String> supportedUnaryFunctions = List.of("sin", "cos", "tan", "cot", "log", "print");

    public Parser() {
        variables = new HashMap<>();
    }

    private void variableParser(String variables) {
        var lines = Arrays.stream(variables.trim().split("\n")).filter(s -> !s.isBlank() && !s.startsWith("//")).map(String::trim).toArray(String[]::new);
        for (var line : lines) {
            try {
                int endIndex = line.contains("=") ? line.indexOf("=") : line.length();
                String value = line.substring(line.indexOf('=') + 1).trim();
                Object pre;
                if (line.startsWith("int")) {
                    var name = line.substring(3, endIndex).trim();
                    if (name.isBlank() || !Character.isAlphabetic(name.charAt(0)))
                        throw new RuntimeException("Bad variable name format: " + name);
                    pre = this.variables.put(name,
                            line.contains("=") ? Integer.parseInt(value) : 0);
                } else if (line.startsWith("float")) {
                    var name = line.substring(5, endIndex).trim();
                    if (name.isBlank() || !Character.isAlphabetic(name.charAt(0)))
                        throw new RuntimeException("Bad variable name format: " + name);
                    pre = this.variables.put(name,
                            line.contains("=") ? Float.parseFloat(value) : 0f);
                } else {
                    throw new RuntimeException("Cannot parse: " + line);
                }
                if (pre != null)
                    System.err.println("Redefining variable: " + line);
                if (!line.contains("="))
                    System.err.println("Not initial value found. It will be automatically 0: " + line);
            } catch (Exception e) {
                throw new RuntimeException("Cannot parse: " + line);
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private String unbox(String exp, char boxOpen, char boxClose) {
        while (exp.startsWith(String.valueOf(boxOpen)))
            exp = exp.substring(1);
        while (exp.endsWith(String.valueOf(boxClose)))
            exp = exp.substring(0, exp.length() - 1);
        return exp;
    }

    private Runnable commandParser(String[] lines, int from, int to) {
        return () -> {
            for (int i = from; i < to; i++) {
                var line = lines[i];
                try {
                    if (line.startsWith("print")) {
                        System.out.println(evaluate(line.substring(5).trim()));
                    } else if (line.contains("=")) {
                        if (variables.put(line.substring(0, line.indexOf('=')).trim(),
                                evaluate(line.substring(line.indexOf('=') + 1).trim())) == null)
                            throw new RuntimeException("Use of undefined variable: " + line);
                    } else if (line.startsWith("for ")) {
                        int repeat = evaluate(line.substring(3).trim()).intValue();
                        var _from = ++i;
                        int depth = 0;
                        while (!lines[i].equals("end for") || depth-- > 0)
                            depth += lines[i++].startsWith("for ") ? 1 : 0;
                        var _for = commandParser(lines, _from, i);
                        while (repeat-- > 0)
                            _for.run();
                    } else if (!line.equals("end for")) {
                        throw new RuntimeException("Parsing error: " + line);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Cannot parse: " + line, e);
                }
            }
        };
    }

    private Number evaluate(String expression) {
        expression = unbox(expression.replace(" ", ""), '(', ')');
        var tokens = expression.toCharArray();
        var values = new Stack<Number>();
        var operations = new Stack<Character>();
        var len = tokens.length;
        for (int i = 0; i < len; i++) {
            var c = tokens[i];
            if (c == '.' || Character.isDigit(c)) {
                var sb = new StringBuilder();
                sb.append(c);
                while (i + 1 < len && (Character.isDigit(c = tokens[i + 1]) || c == '.')) {
                    ++i;
                    sb.append(c);
                }
                try {
                    values.push(Integer.parseInt(sb.toString()));
                } catch (NumberFormatException e) {
                    values.push(Float.parseFloat(sb.toString()));
                }
            } else if (c == '(') {
                operations.push(c);
            } else if (c == ')') {
                while (operations.peek() != '(')
                    values.push(apply(operations.pop(), values.pop(), values.pop()));
                operations.pop();
            } else if (Character.isAlphabetic(c)) {
                var sb = new StringBuilder();
                sb.append(c);
                while (i + 1 < len && (Character.isAlphabetic(tokens[i + 1]) || Character.isDigit(tokens[i + 1])))
                    sb.append(tokens[++i]);
                if (supportedUnaryFunctions.contains(sb.toString()) && tokens[i + 1] == '(') {
                    var start = i + 2;
                    while (i + 1 < len && tokens[i + 1] != ')')
                        i++;
                    var inner = evaluate(expression.substring(start, ++i));
                    values.push(switch (sb.toString()) {
                        case "sin" -> sin(inner.doubleValue());
                        case "cos" -> cos(inner.doubleValue());
                        case "tan" -> tan(inner.doubleValue());
                        case "cot" -> 1 / tan(inner.doubleValue());
                        case "log" -> log(inner.doubleValue());
                        case "print" -> String.valueOf(inner).length();
                        default -> throw new RuntimeException("ASSERT:: NEVER-RICH-THIS-LINE");
                    });
                    if (sb.toString().equals("print"))
                        System.out.println(inner);
                    continue;
                }
                var val = variables.get(sb.toString());
                if (val == null) {
                    System.err.println("use of undefined variable: " + sb);
                    val = 0;
                    variables.put(sb.toString(), val);
                }
                values.push(val);
            } else if (supportedOperations.contains(c)) {
                while (!operations.empty() && hasPrecedence(c, operations.peek()))
                    values.push(apply(operations.pop(), values.pop(), values.pop()));
                operations.push(c);
            }
        }
        while (!operations.empty())
            values.push(apply(operations.pop(), values.pop(), values.pop()));
        return values.pop();
    }

    private static Number apply(char op, Number b, Number a) {
        var aa = a.doubleValue();
        var bb = b.doubleValue();
        var res = switch (op) {
            case '+' -> aa + bb;
            case '-' -> aa - bb;
            case '*' -> aa * bb;
            case '/' -> aa / bb;
            default -> 0;
        };
        return a instanceof Integer && b instanceof Integer ? (int) res : (float) res;
    }

    private static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    private Runnable commandParser(String commands) {
        var lines = Arrays.stream(commands.trim().split("\n")).
                filter(s -> !s.isBlank() && !s.startsWith("//")).map(String::trim).toArray(String[]::new);
        return commandParser(lines, 0, lines.length);
    }

    public Runnable parse(String input) {
        var pp = input.trim().split("%%");
        if (pp.length > 1) {
            variableParser(pp[0]);
            return commandParser(pp[1]);
        } else {
            return commandParser(pp[0]);
        }
    }

    public static void main(String[] args) {
        var p = new Parser();
        p.parse("""
                int a
                int b = 3
                float c = 4
                float a = 5
                float x
                %%
                for 2
                for 3
                a = a + 1 + z
                print a
                end for
                a = a * (2 + 3)
                print a
                end for
                a = a + 3
                print a
                print 124
                print(print(1243) + 12)
                print(a + print(a))
                print sin(1.57) + cos(a)
                print cos(50)
                for 5
                print x
                print sin(x)
                x = x + 0.1
                end for
                """).run();
    }
}
