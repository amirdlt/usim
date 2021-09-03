package com.usim.ulib.notmine.tmp12;

public class ExamQ3 {
//    private static final java.util.List<Character> supportedOperations = List.of('+', '-', '/', '*');
//
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Q3");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setBounds(100, 100, 400, 400);
//
//        frame.setLayout(new BorderLayout());
//
//        JPanel buttons = new JPanel(new GridLayout(4, 4, 15, 15)) {{
//            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//        }};
//        frame.add(buttons);
//
//        JTextField field = new JTextField();
//        frame.add(field, BorderLayout.NORTH);
//
//        for (int i = 9; i > 0; i--) {
//            int finalI = i;
//            buttons.add(new JButton(String.valueOf(i)) {{
//                addActionListener(e -> field.setText(field.getText() + finalI));
//            }});
//
//            if (i == 7) {
//                buttons.add(new JButton("=") {{
//                    addActionListener(e -> field.setText(String.valueOf(evaluate(field.getText()))));
//                }});
//            }
//
//            if (i == 4) {
//                buttons.add(new JButton("-") {{
//                    addActionListener(e -> field.setText(field.getText() + getText()));
//                }});
//            }
//
//            if (i == 1) {
//                buttons.add(new JButton("+") {{
//                    addActionListener(e -> field.setText(field.getText() + getText()));
//                }});
//            }
//        }
//
//        buttons.add(new JButton("/") {{
//            addActionListener(e -> field.setText(field.getText() + getText()));
//        }});
//
//        buttons.add(new JButton("x") {{
//            addActionListener(e -> field.setText(field.getText() + "*"));
//        }});
//
//        buttons.add(new JButton("C") {{
//            addActionListener(e -> field.setText(""));
//        }});
//
//        buttons.add(new JButton("CE") {{
//            addActionListener(e -> {
//                if (field.getText().isEmpty())
//                    return;
//                field.setText(field.getText().substring(0, field.getText().length() - 1));
//            });
//        }});
//
//        field.setEditable(false);
//
//        frame.setVisible(true);
//    }
//
//    private static Number evaluate(String expression) {
//        expression = expression.replace(" ", "");
//        var tokens = expression.toCharArray();
//        var values = new Stack<Number>();
//        var operations = new Stack<Character>();
//        var len = tokens.length;
//        for (int i = 0; i < len; i++) {
//            var c = tokens[i];
//            if (c == '.' || Character.isDigit(c)) {
//                var sb = new StringBuilder();
//                sb.append(c);
//                while (i + 1 < len && (Character.isDigit(c = tokens[i + 1]) || c == '.')) {
//                    ++i;
//                    sb.append(c);
//                }
//                try {
//                    values.push(Integer.parseInt(sb.toString()));
//                } catch (NumberFormatException e) {
//                    values.push(Float.parseFloat(sb.toString()));
//                }
//            } else if (c == '(') {
//                operations.push(c);
//            } else if (c == ')') {
//                while (operations.peek() != '(')
//                    values.push(apply(operations.pop(), values.pop(), values.pop()));
//                operations.pop();
//            } else if (supportedOperations.contains(c)) {
//                while (!operations.empty() && hasPrecedence(c, operations.peek()))
//                    values.push(apply(operations.pop(), values.pop(), values.pop()));
//                operations.push(c);
//            }
//        }
//        while (!operations.empty())
//            values.push(apply(operations.pop(), values.pop(), values.pop()));
//        return values.pop();
//    }
//
//    private static Number apply(char op, Number b, Number a) {
//        var aa = a.doubleValue();
//        var bb = b.doubleValue();
//        var res = switch (op) {
//            case '+' -> aa + bb;
//            case '-' -> aa - bb;
//            case '*' -> aa * bb;
//            case '/' -> aa / bb;
//            default -> 0;
//        };
//        return a instanceof Integer && b instanceof Integer ? (int) res : (float) res;
//    }
//
//    private static boolean hasPrecedence(char op1, char op2) {
//        if (op2 == '(' || op2 == ')')
//            return false;
//        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
//    }
}
