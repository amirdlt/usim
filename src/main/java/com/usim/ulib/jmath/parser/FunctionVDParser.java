package com.usim.ulib.jmath.parser;

import com.usim.ulib.jmath.datatypes.functions.FunctionVD;
import com.usim.ulib.jmath.functions.utils.InverseFinder;
import com.usim.ulib.jmath.operators.Derivative;
import com.usim.ulib.jmath.operators.FourierSeries;
import com.usim.ulib.jmath.operators.Integral;
import com.usim.ulib.jmath.operators.LaplaceTransform;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.*;

@SuppressWarnings("unused")
@Deprecated
public class FunctionVDParser implements Parser<FunctionVD> {
    private final JComponent textField;
    private int extraVariablesIdCounter;
    private final Set<String> extraVariableNames;

    public FunctionVDParser(JComponent textField) {
        this.textField = textField;
        extraVariablesIdCounter = 4;
        extraVariableNames = new HashSet<>();
    }

    private FunctionVDParser() {
        this(null);
    }

    @Override
    public FunctionVD parse(String expression) {
        TokenString tokens = tokenize(expression);
        if (tokens != null) {
            checkParentheses(tokens);
            substituteUnaryMinus(tokens);
            var res = doOrderOfOperations(tokens);
            if (res == null) {
                System.out.println("Parsing of the function \"" + expression + "\" failed.");
                extraVariablesIdCounter = 4;
                extraVariableNames.clear();
                return ps -> Double.NaN;
            }
            extraVariableNames.clear();
            extraVariablesIdCounter = 4;
            return res;
        }
        System.out.println("Parsing of the function \"" + expression + "\" failed.");
        extraVariableNames.clear();
        extraVariablesIdCounter = 4;
        return ps -> Double.NaN;
    }

    @SuppressWarnings({"EnhancedSwitchMigration"})
    private FunctionVD doOrderOfOperations(TokenString tokens) {
        int location;
        FunctionVD res = null;

        location = scanFromRight(tokens, TokenType.PLUS);
        if (location != -1) {
            TokenString left = tokens.split(0, location);
            TokenString right = tokens.split(location + 1, tokens.getLength());
            res = ps -> doOrderOfOperations(left).valueAt(ps) + doOrderOfOperations(right).valueAt(ps);
        } else {
            location = scanFromRight(tokens, TokenType.MINUS);
            if (location != -1) {
                TokenString left = tokens.split(0, location);
                TokenString right = tokens.split(location + 1, tokens.getLength());
                res = ps -> doOrderOfOperations(left).valueAt(ps) - doOrderOfOperations(right).valueAt(ps);
            } else {
                location = scanFromRight(tokens, TokenType.DIVIDED_BY);
                if (location != -1) {
                    TokenString left = tokens.split(0, location);
                    TokenString right = tokens.split(location + 1, tokens.getLength());
                    res = ps -> doOrderOfOperations(left).valueAt(ps) / doOrderOfOperations(right).valueAt(ps);
                } else {
                    location = scanFromRight(tokens, TokenType.TIMES);
                    if (location != -1) {
                        TokenString left = tokens.split(0, location);
                        TokenString right = tokens.split(location + 1, tokens.getLength());
                        res = ps -> doOrderOfOperations(left).valueAt(ps) * doOrderOfOperations(right).valueAt(ps);
                    } else {
                        location = scanFromRight(tokens, TokenType.MODULO);
                        if (location != -1) {
                            TokenString left = tokens.split(0, location);
                            TokenString right = tokens.split(location + 1, tokens.getLength());
                            res = ps -> doOrderOfOperations(left).valueAt(ps) % doOrderOfOperations(right).valueAt(ps);
                        } else {
                            location = scanFromRight(tokens, TokenType.RAISED_TO);
                            if (location != -1) {
                                TokenString left = tokens.split(0, location);
                                TokenString right = tokens.split(location + 1, tokens.getLength());
                                res = ps -> pow(doOrderOfOperations(left).valueAt(ps), doOrderOfOperations(right).valueAt(ps));
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
                                        switch (tokens.tokenAt(location).type) {
                                            case X: res = ps -> ps[0]; break;
                                            case Y: res = ps -> ps[1]; break;
                                            case Z: res = ps -> ps[2]; break;
                                            case W: res = ps -> ps[3]; break;
                                            case EXTRA_VARIABLE:
                                                int finalLocation1 = location;
                                                res = ps -> ps[Integer.parseInt(tokens.tokenAt(finalLocation1).data)];
                                        }
                                    } else {
                                        location = scanFromRight(tokens, TokenType.NUMBER);
                                        if (location != -1) {
                                            int finalLocation = location;
                                            res = ps -> Double.parseDouble(tokens.tokenAt(finalLocation).data);
                                        } else {
                                            location = scanFromRight(tokens, TokenType.CONSTANTS);
                                            if (location != -1) {
                                                switch (tokens.tokenAt(location).type) {
                                                    case PI:
                                                        res = ps -> Math.PI; break;
                                                    case E:
                                                        res = ps -> Math.E; break;
                                                    case POSITIVE_INFINITY:
                                                        res = ps -> Double.POSITIVE_INFINITY; break;
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
        }
        return res;
    }

    @SuppressWarnings({"SwitchStatementWithTooFewBranches"})
    private FunctionVD parseFunctionParams(TokenString paramString, TokenType type) {
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

        if (params.size() == 1) {
            var p = doOrderOfOperations(params.get(0));
            switch (type) {
                case ABSOLUTE_VALUE:
                    return ps -> abs(p.valueAt(ps));
                case CEILING:
                    return ps -> ceil(p.valueAt(ps));
                case FLOOR:
                    return ps -> floor(p.valueAt(ps));
                case SINE:
                    return ps -> sin(p.valueAt(ps));
                case COSINE:
                    return ps -> cos(p.valueAt(ps));
                case TANGENT:
                    return ps -> tan(p.valueAt(ps));
                case COTANGENT:
                    return ps -> 1 / tan(p.valueAt(ps));
                case SECANT:
                    return ps -> 1 / cos(p.valueAt(ps));
                case CO_SECANT:
                    return ps -> 1 / sin(p.valueAt(ps));
                case SQUARE_ROOT:
                    return ps -> 1 / sqrt(p.valueAt(ps));
                case LOG:
                    return ps -> log(p.valueAt(ps));
                case LOG10:
                    return ps -> log10(p.valueAt(ps));
                case LOG2:
                    return ps -> log(p.valueAt(ps)) / log(2);
            }
        } else if (params.size() == 2) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            switch (type) {
                case LAPLACE:
                    return ps -> LaplaceTransform.laplaceOf(p1.f2D(ps), p2.atOrigin(ps.length)).valueAt(ps[0]);
                case DERIVATIVE:
                    return ps -> Derivative.derivative(p1.f2D(ps), p2.atOrigin(ps.length)).valueAt(ps[0]);
            }
        } else if (params.size() == 3) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            var p3 = doOrderOfOperations(params.get(2));

            switch (type) {
                case DERIVATIVE:
                    return ps -> Derivative.derivative(p1.f2D(ps), p2.atOrigin().intValue(), p3.atOrigin(ps.length).intValue()).valueAt(ps[0]);
            }
        } else if (params.size() == 4) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            var p3 = doOrderOfOperations(params.get(2));
            var p4 = doOrderOfOperations(params.get(3));

            switch (type) {
                case INTEGRAL:
                    return ps -> Integral.byDefinition(p1.f2D(ps), p2.f2D(ps), p3.f2D(ps), p4.atOrigin(ps.length)).valueAt(ps[0]);
                case INVERSE:
                    return ps -> InverseFinder.byReSampling(p1.f2D(ps), p2.atOrigin(ps.length), p3.atOrigin(ps.length), p4.atOrigin(ps.length)).valueAt(ps[0]);
            }
        } else if (params.size() == 5) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            var p3 = doOrderOfOperations(params.get(2));
            var p4 = doOrderOfOperations(params.get(3));
            var p5 = doOrderOfOperations(params.get(4));

            switch (type) {
                case FOURIER_SERIES:
                    return ps -> FourierSeries.sN(p1.atOrigin(ps.length).intValue(), p2.f2D(ps), p3.atOrigin(ps.length), p4.atOrigin(ps.length), p5.atOrigin(ps.length)).valueAt(ps[0]);
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
                if (openParentheses == 0) {
                    return i;
                }
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
    public FunctionVD parse() {
        if (!(textField instanceof JTextComponent) && !(textField instanceof JLabel))
            return null;
        return parse(textField instanceof JLabel ? ((JLabel) textField).getText() : ((JTextComponent) textField).getText());
    }

    private TokenString tokenize(String expr) {
        expr = expr.replace(" ", "");
        TokenString tkString = new TokenString();

        StringBuilder name = new StringBuilder();
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

            boolean isVariable = (cc == 'x' || cc == 'y' || cc == 'z' || cc == 'w') &&
                    !Character.isLetterOrDigit(pc) && Character.isLetterOrDigit(nc);

            if ((Character.isAlphabetic(cc) || fName) && cc != '(') {
                if (isVariable) {
                    tkString.addToken(new Token(getTokenTypeByName(cc + "", TokenType.VARIABLES)));
                } else {
                    name.append(cc);
                }
                special = true;
            } else if (name.length() > 0) {
                TokenType type = getTokenTypeByName(name.toString(), TokenType.FUNCTIONS) == null ? getTokenTypeByName(name.toString(), TokenType.CONSTANTS) :
                        getTokenTypeByName(name.toString(), TokenType.FUNCTIONS);
                if (type == null) {
//                    System.out.println("The function name " + name + " is not valid!");
                    if (nc != '(') {
                        if (!extraVariableNames.contains(name.toString())) {
//                            tkString.addToken(new Token(TokenType.EXTRA_VARIABLE, "" + extraVariablesIdCounter, name.toString()));
                            extraVariableNames.add(name.toString());
                            extraVariablesIdCounter++;
                        }
                    } else {
                        return null;
                    }
                }
                fName = false;
                tkString.addToken(new Token(type));
                name = new StringBuilder();
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
            TokenType type = getTokenTypeByName(name.toString(), TokenType.FUNCTIONS) == null ? getTokenTypeByName(name.toString(), TokenType.CONSTANTS) :
                    getTokenTypeByName(name.toString(), TokenType.FUNCTIONS);

            if (type == null) {
//                System.out.println("The function name '" + name + "' is not valid!");
                if (!extraVariableNames.contains(name.toString())) {
//                    tkString.addToken(new Token(TokenType.EXTRA_VARIABLE, "" + extraVariablesIdCounter, name.toString()));
                    extraVariableNames.add(name.toString());
                    extraVariablesIdCounter++;
                }
//                return null;
            }
            tkString.addToken(new Token(type));
        }

        if (number.length() > 0)
            tkString.addToken(new Token(TokenType.NUMBER, number.toString()));

        return tkString;
    }

    @Contract(pure = true)
    private @Nullable TokenType getTokenTypeByName(String name, TokenType @NotNull [] tokenTypes) {
        for (var v : tokenTypes) {
            if (v.name.equals(name))
                return v;
        }
        return null;
    }

    private void checkParentheses(@NotNull TokenString tokens) {
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

    private void substituteUnaryMinus(@NotNull TokenString tokens) {
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

    public static FunctionVD parser(String func) {
        return new FunctionVDParser().parse(func);
    }

}
