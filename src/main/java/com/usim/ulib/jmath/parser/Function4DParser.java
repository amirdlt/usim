package com.usim.ulib.jmath.parser;

import com.usim.ulib.jmath.datatypes.functions.Function4D;
import com.usim.ulib.jmath.datatypes.functions.TernaryFunction;
import com.usim.ulib.jmath.functions.utils.InverseFinder;
import com.usim.ulib.jmath.operators.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;
import static com.usim.ulib.jmath.functions.utils.FunctionUtil.*;

@SuppressWarnings("unused")
public class Function4DParser implements Parser<Function4D> {
    private static final Function4DParser parser = new Function4DParser();

    private final JComponent textField;

    public Function4DParser(JComponent textField) {
        this.textField = textField;
    }

    private Function4DParser() {
        this(null);
    }

    @Override
    public Function4D parse(String expression) {
        TokenString tokens = tokenize(expression);
        if (tokens != null) {
            checkParentheses(tokens);
            substituteUnaryMinus(tokens);
            var res = doOrderOfOperations(tokens);
            if (res == null) {
                System.out.println("Parsing of the function \"" + expression + "\" failed.");
                return null;
            }
            return res;
        }
        System.out.println("Parsing of the function \"" + expression + "\" failed.");
        return null;
    }

    private Function4D doOrderOfOperations(TokenString tokens) {
        int location;
        Function4D res = null;

        location = scanFromRight(tokens, TokenType.PLUS);
        if (location != -1) {
            TokenString left = tokens.split(0, location);
            TokenString right = tokens.split(location + 1, tokens.getLength());
            res = sum(doOrderOfOperations(left), doOrderOfOperations(right));
        } else {
            location = scanFromRight(tokens, TokenType.MINUS);
            if (location != -1) {
                TokenString left = tokens.split(0, location);
                TokenString right = tokens.split(location + 1, tokens.getLength());
                res = sub(doOrderOfOperations(left), doOrderOfOperations(right));
            } else {
                location = scanFromRight(tokens, TokenType.DIVIDED_BY);
                if (location != -1) {
                    TokenString left = tokens.split(0, location);
                    TokenString right = tokens.split(location + 1, tokens.getLength());
                    res = fraction(doOrderOfOperations(left), doOrderOfOperations(right));
                } else {
                    location = scanFromRight(tokens, TokenType.TIMES);
                    if (location != -1) {
                        TokenString left = tokens.split(0, location);
                        TokenString right = tokens.split(location + 1, tokens.getLength());
                        res = multiply(doOrderOfOperations(left), doOrderOfOperations(right));
                    } else {
                        location = scanFromRight(tokens, TokenType.MODULO);
                        if (location != -1) {
                            TokenString left = tokens.split(0, location);
                            TokenString right = tokens.split(location + 1, tokens.getLength());
                            res = modulo(doOrderOfOperations(left), doOrderOfOperations(right));
                        } else {
                            location = scanFromRight(tokens, TokenType.RAISED_TO);
                            if (location != -1) {
                                TokenString left = tokens.split(0, location);
                                TokenString right = tokens.split(location + 1, tokens.getLength());
                                res = power(doOrderOfOperations(left), doOrderOfOperations(right));
                            } else {
                                location = scanFromRight(tokens, TokenType.FUNCTIONS);
                                if (location != -1) {
                                    int endParams = getFunctionParamsEnd(tokens, location + 2);
                                    if (endParams != -1) {
                                        TokenString paramString = tokens.split(location + 2, endParams);
                                        res = parseFunctionParams(paramString, tokens.tokenAt(location).type);
                                    }
                                } else if (tokens.getLength() >= 2 &&
                                        tokens.tokenAt(tokens.getLength() - 1).type == TokenType.CLOSE_PARENTHESES
                                        && tokens.tokenAt(0).type == TokenType.OPEN_PARENTHESES) {
                                    TokenString inParentheses = tokens.split(1, tokens.getLength() - 1);
                                    res = doOrderOfOperations(inParentheses);
                                } else {
                                    location = scanFromRight(tokens, TokenType.VARIABLES);
                                    if (location != -1) {
                                        res = switch (tokens.tokenAt(location).type) {
                                            case X, XX -> (x, y, z) -> x;
                                            case Y, YY -> (x, y, z) -> y;
                                            case Z, ZZ -> (x, y, z) -> z;
                                            default -> res;
                                        };
                                    } else {
                                        location = scanFromRight(tokens, TokenType.NUMBER);
                                        if (location != -1) {
                                            int finalLocation = location;
                                            res = (x, y, z) -> Double.parseDouble(tokens.tokenAt(finalLocation).data);
                                        } else {
                                            location = scanFromRight(tokens, TokenType.CONSTANTS);
                                            if (location != -1) {
                                                res = switch (tokens.tokenAt(location).type) {
                                                    case PI -> (x, y, z) -> Math.PI;
                                                    case E -> (x, y, z) -> Math.E;
                                                    case POSITIVE_INFINITY -> (x, y, z) -> Double.POSITIVE_INFINITY;
                                                    default -> res;
                                                };
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    @SuppressWarnings({"SwitchStatementWithTooFewBranches"})
    private Function4D parseFunctionParams(TokenString paramString, TokenType type) {
        List<TokenString> params = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < paramString.getLength(); i++) {
            Token t = paramString.tokenAt(i);
            if (t.type == TokenType.COMMA) {
                params.add(paramString.split(start, i));
                start = i + 1;
            }
        }
        params.add(paramString.split(start, paramString.getLength()));

        if (params.size() == 0) {
            switch (type) {
                case RANDOM: return (x, y, z) -> random();
            }
        } else if (params.size() == 1) {
            var p = doOrderOfOperations(params.get(0));
            switch (type) {
                case ABSOLUTE_VALUE: return (x, y, z) -> abs(p.valueAt(x, y, z));
                case CEILING: return (x, y, z) -> ceil(p.valueAt(x, y, z));
                case FLOOR: return (x, y, z) -> floor(p.valueAt(x, y, z));
                case SINE: return (x, y, z) -> sin(p.valueAt(x, y, z));
                case COSINE: return (x, y, z) -> cos(p.valueAt(x, y, z));
                case TANGENT: return (x, y, z) -> tan(p.valueAt(x, y, z));
                case COTANGENT: return (x, y, z) -> 1 / tan(p.valueAt(x, y, z));
                case SECANT: return (x, y, z) -> 1 / cos(p.valueAt(x, y, z));
                case CO_SECANT: return (x, y, z) -> 1 / sin(p.valueAt(x, y, z));
                case SQUARE_ROOT: return (x, y, z) -> sqrt(p.valueAt(x, y, z));
                case LOG: return (x, y, z) -> log(p.valueAt(x, y, z));
                case LOG10: return (x, y, z) -> log10(p.valueAt(x, y, z));
                case LOG2: return (x, y, z) -> log(p.valueAt(x, y, z)) / log(2);
                case RANDOM: return (x, y, z) -> random() * p.atOrigin();
            }
        } else if (params.size() == 2) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            switch (type) {
                case LAPLACE: return (x, y, z) -> LaplaceTransform.laplaceOf(p1.f2D(y, z), p2.atOrigin()).valueAt(x);
                case DERIVATIVE: return (x, y, z) -> Derivative.derivative(p1.f2D(y, z), p2.atOrigin()).valueAt(x);
                case RANDOM: return (x, y, z) -> random() * (p2.atOrigin() - p1.atOrigin()) + p1.atOrigin();
                case ARC: return new TernaryFunction(p1, p2).setBounds(-PI, PI).setDelta(0.01, 0.01);
            }
        } else if (params.size() == 3) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            var p3 = doOrderOfOperations(params.get(2));

            switch (type) {
                case DERIVATIVE: return (x, y, z) -> Derivative.derivative(p1.f2D(y, z), p2.atOrigin().intValue(), p3.atOrigin().intValue()).valueAt(x);
                case RANDOM: return (x, y, z) -> p1.atOrigin() + (int) (((p2.atOrigin() - p1.atOrigin()) / p3.atOrigin()) * random()) * p3.atOrigin();
                case ARC3: return new TernaryFunction(p1, p2, p3);
            }
        } else if (params.size() == 4) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            var p3 = doOrderOfOperations(params.get(2));
            var p4 = doOrderOfOperations(params.get(3));

            switch (type) {
                case INTEGRAL: return (x, y, z) -> Integral.byDefinition(p1.f2D(y, z), p2.f2D(y, z), p3.f2D(y, z), p4.atOrigin()).valueAt(x);
                case INVERSE: return (x, y, z) -> InverseFinder.byReSampling(p1.f2D(y, z), p2.atOrigin(), p3.atOrigin(), p4.atOrigin()).valueAt(x);
                case TAYLOR_SERIES: return (x, y, z) -> TaylorSeries.taylorSeries(p1.atOrigin().intValue(), p2.f2D(y, z), p3.atOrigin(), p4.atOrigin()).valueAt(x);
                case ARC: return new TernaryFunction(p1, p2).setBounds(p3.atOrigin(), p4.atOrigin()).setDelta(0.01, 0.01);
            }
        } else if (params.size() == 5) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            var p3 = doOrderOfOperations(params.get(2));
            var p4 = doOrderOfOperations(params.get(3));
            var p5 = doOrderOfOperations(params.get(4));

            switch (type) {
                case FOURIER_SERIES: return (x, y, z) -> FourierSeries.sN(p1.atOrigin().intValue(), p2.f2D(y, z), p3.atOrigin(), p4.atOrigin(), p5.atOrigin()).valueAt(x);
                case ARC: return new TernaryFunction(p1, p2).setBounds(p3.atOrigin(), p4.atOrigin()).setDelta(p5.atOrigin(), p5.atOrigin());
            }
        }
        return null;
    }

    private int getFunctionParamsEnd(TokenString tokens, int location) {
        int openParentheses = 0;
        for (int i = location; i < tokens.getLength(); i++) {
            Token t = tokens.tokenAt(i);
            if (t.type == TokenType.OPEN_PARENTHESES) {
                openParentheses++;
            } else if (t.type == TokenType.CLOSE_PARENTHESES) {
                if (openParentheses == 0)
                    return i;
                openParentheses--;
            }
        }
        return -1;
    }

    private int scanFromRight(TokenString tokens, TokenType type) {
        int openParentheses = 0;
        for (int i = tokens.getLength() - 1; i >= 0; i--) {
            Token t = tokens.tokenAt(i);
            if (t.type == TokenType.CLOSE_PARENTHESES) {
                openParentheses++;
            } else if (t.type == TokenType.OPEN_PARENTHESES) {
                openParentheses--;
            } else if (t.type == type && openParentheses == 0) {
                return i;
            }
        }
        return -1;
    }

    private int scanFromRight(TokenString tokens, TokenType[] types) {
        int openParentheses = 0;
        for (int i = tokens.getLength() - 1; i >= 0; i--) {
            Token t = tokens.tokenAt(i);
            if (t.type == TokenType.CLOSE_PARENTHESES) {
                openParentheses++;
            } else if (t.type == TokenType.OPEN_PARENTHESES) {
                openParentheses--;
            } else {
                if (openParentheses == 0) {
                    for (var type : types) {
                        if (t.type == type) {
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public Function4D parse() {
        if (!(textField instanceof JTextComponent) && !(textField instanceof JLabel))
            return null;
        return parse(textField instanceof JLabel ? ((JLabel) textField).getText() : ((JTextComponent) textField).getText());
    }

    private TokenString tokenize(String expr) {
        expr = expr.replace(" ", "");
        TokenString tkString = new TokenString();

        String name = "";
        StringBuilder number = new StringBuilder();
        int numDecimals = 0;
        boolean fName = false;
        for (int i = 0; i < expr.length(); i++) {
            var cc = expr.charAt(i);
            var pc = i > 0 ? expr.charAt(i - 1) : '\"';
            var nc = i < expr.length() - 1 ? expr.charAt(i + 1) : '\"';
            boolean special = false;
            if (!fName)
                fName = i > 0 && Character.isDigit(cc) && Character.isAlphabetic(pc);

            boolean isVariable = (cc == 'x' || cc == 'y' ||cc == 'z' || cc == 'X' || cc == 'Y' ||cc == 'Z') &&
                    !Character.isLetterOrDigit(pc) && !Character.isLetterOrDigit(nc);

            if ((Character.isAlphabetic(cc) || fName) && cc != '(') {
                if (isVariable) {
                    tkString.addToken(new Token(getTokenTypeByName(cc + "", TokenType.VARIABLES)));
                } else {
                    name += cc;
                }
                special = true;
            } else if (name.length() > 0) {
                TokenType type = getTokenTypeByName(name, TokenType.FUNCTIONS) == null ? getTokenTypeByName(name, TokenType.CONSTANTS) :
                        getTokenTypeByName(name, TokenType.FUNCTIONS);
                if (type == null) {
                    System.out.println("The function name " + name + " is not valid!");
                    return null;
                }
                fName = false;
                tkString.addToken(new Token(type));
                name = "";
            }

            if ((Character.isDigit(cc) && !fName) || cc == '.') {
                if (cc == '.') {
                    if (numDecimals == 0)
                        number.append(cc);
                    numDecimals++;
                } else {
                    number.append(cc);
                }
                special = true;
            } else if (number.length() > 0) {
                tkString.addToken(new Token(TokenType.NUMBER, number.toString()));
                number = new StringBuilder();
                numDecimals = 0;
            }

            if (!special) {
                if (cc == '(') tkString.addToken(new Token(TokenType.OPEN_PARENTHESES));
                else if (cc == ')') tkString.addToken(new Token(TokenType.CLOSE_PARENTHESES));
                else if (cc == ',') tkString.addToken(new Token(TokenType.COMMA));
                else if (cc == '+') tkString.addToken(new Token(TokenType.PLUS));
                else if (cc == '-') tkString.addToken(new Token(TokenType.MINUS));
                else if (cc == '*') tkString.addToken(new Token(TokenType.TIMES));
                else if (cc == '/') tkString.addToken(new Token(TokenType.DIVIDED_BY));
                else if (cc == '^') tkString.addToken(new Token(TokenType.RAISED_TO));
                else if (cc == '%') tkString.addToken(new Token(TokenType.MODULO));
                else {
                    System.out.println("The character '" + cc + "' is not allowed!");
                    return null;
                }
            }
        }

        if (name.length() > 0) {
            TokenType type = getTokenTypeByName(name, TokenType.FUNCTIONS) == null ? getTokenTypeByName(name, TokenType.CONSTANTS) :
                    getTokenTypeByName(name, TokenType.FUNCTIONS);

            if (type == null) {
                System.out.println("The function name '" + name + "' is not valid!");
                return null;
            }
            tkString.addToken(new Token(type));
        }

        if (number.length() > 0)
            tkString.addToken(new Token(TokenType.NUMBER, number.toString()));

        return tkString;
    }

    private TokenType getTokenTypeByName(String name, TokenType[] tokenTypes) {
        for (var v : tokenTypes) {
            if (v.name.equals(name))
                return v;
        }
        return null;
    }

    private void checkParentheses(TokenString tokens) {
        int openParentheses = 0;
        for (int i = 0; i < tokens.getLength(); i++) {
            Token t = tokens.tokenAt(i);
            if (t.type == TokenType.OPEN_PARENTHESES) {
                openParentheses++;
            } else if (t.type == TokenType.CLOSE_PARENTHESES) {
                openParentheses--;
            }
            if (openParentheses < 0)
                System.out.println("You closed too many parentheses!");
        }
        if (openParentheses > 0)
            System.out.println("You did not close enough parentheses!");
    }

    private void substituteUnaryMinus(TokenString tokens) {
        Token prev = null;
        for (int i = 0; i < tokens.getLength(); i++) {
            Token t = tokens.tokenAt(i);
            if (t.type == TokenType.MINUS) {
                if (prev == null || !(prev.type == TokenType.NUMBER || prev.type == TokenType.X || prev.type == TokenType.CLOSE_PARENTHESES)) {
                    // Ex: -x becomes (0-1)*x
                    tokens.remove(i);
                    tokens.insert(i, new Token(TokenType.TIMES));
                    tokens.insert(i, new Token(TokenType.CLOSE_PARENTHESES));
                    tokens.insert(i, new Token(TokenType.NUMBER, "1"));
                    tokens.insert(i, new Token(TokenType.MINUS));
                    tokens.insert(i, new Token(TokenType.NUMBER, "0"));
                    tokens.insert(i, new Token(TokenType.OPEN_PARENTHESES));
                    i += 6;
                }
            }
            prev = t;
        }
    }

    public static Function4D parser(String func) {
        return parser.parse(func);
    }
}
