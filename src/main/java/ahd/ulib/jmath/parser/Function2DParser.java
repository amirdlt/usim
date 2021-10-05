package ahd.ulib.jmath.parser;

import ahd.ulib.jmath.datatypes.functions.Function2D;
import ahd.ulib.jmath.datatypes.functions.UnaryFunction;
import ahd.ulib.jmath.functions.unaries.real.*;
import ahd.ulib.jmath.functions.unaries.real.ConstantFunction2D;
import ahd.ulib.jmath.functions.unaries.real.IdentityFunction;
import ahd.ulib.jmath.functions.utils.InverseFinder;
import ahd.ulib.jmath.operators.Derivative;
import ahd.ulib.jmath.operators.FourierSeries;
import ahd.ulib.jmath.operators.Integral;
import ahd.ulib.jmath.operators.LaplaceTransform;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

import static ahd.ulib.jmath.functions.utils.FunctionUtil.*;

@SuppressWarnings("unused")
@Deprecated
public class Function2DParser implements Parser<UnaryFunction> {
    private final JComponent textField;

    public Function2DParser(JComponent textField) {
        this.textField = textField;
    }

    private Function2DParser() {
        this(null);
    }

    @Override
    public UnaryFunction parse(String expression) {
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

    @SuppressWarnings({"EnhancedSwitchMigration"})
    private UnaryFunction doOrderOfOperations(TokenString tokens) {
        int location;
        var res = ConstantFunction2D.NaN();

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
                                        res = IdentityFunction.f();
                                    } else {
                                        location = scanFromRight(tokens, TokenType.NUMBER);
                                        if (location != -1) {
                                            res = ConstantFunction2D.f(Double.parseDouble(tokens.tokenAt(location).data));
                                        } else {
                                            location = scanFromRight(tokens, TokenType.CONSTANTS);
                                            if (location != -1) {
                                                switch (tokens.tokenAt(location).type) {
                                                    case PI:
                                                        res = ConstantFunction2D.f(Math.PI); break;
                                                    case E:
                                                        res = ConstantFunction2D.f(Math.E); break;
                                                    case POSITIVE_INFINITY:
                                                        res = ConstantFunction2D.f(Double.POSITIVE_INFINITY); break;
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
    private UnaryFunction parseFunctionParams(TokenString paramString, TokenType type) {
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
            return null;
        } else if (params.size() == 1) {
            Function2D param1 = doOrderOfOperations(params.get(0));
            switch (type) {
                case ABSOLUTE_VALUE:
                    return AbsoluteValue.f().setInnerFunction(param1);
                case CEILING:
                    return Ceil.f().setInnerFunction(param1);
                case FLOOR:
                    return Floor.f().setInnerFunction(param1);
                case SINE:
                    return Sine.f().setInnerFunction(param1);
                case COSINE:
                    return Cosine.f().setInnerFunction(param1);
                case TANGENT:
                    return Tangent.f().setInnerFunction(param1);
                case COTANGENT:
                    return Cotangent.f().setInnerFunction(param1);
                case SECANT:
                    return Secant.f().setInnerFunction(param1);
                case CO_SECANT:
                    return CoSecant.f().setInnerFunction(param1);
                case SQUARE_ROOT:
                    return SquareRoot.f().setInnerFunction(param1);
                case LOG:
                    return Logarithm.f();
                case LOG10:
                    return Logarithm.f(10);
                case LOG2:
                    return Logarithm.f(2);
            }
        } else if (params.size() == 2) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            switch (type) {
                case LAPLACE:
                    return LaplaceTransform.laplaceOf(p1, p2.valueAt(0));
                case DERIVATIVE:
                    return Derivative.derivative(p1, p2.valueAt(0));
            }
        } else if (params.size() == 3) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            var p3 = doOrderOfOperations(params.get(2));

            switch (type) {
                case DERIVATIVE:
                    return Derivative.derivative(p1, (int) p2.valueAt(0), p3.valueAt(0));
            }
        } else if (params.size() == 4) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            var p3 = doOrderOfOperations(params.get(2));
            var p4 = doOrderOfOperations(params.get(3));

            switch (type) {
                case INTEGRAL:
                    return Integral.byDefinition(p1, p2, p3, p4.valueAt(0));
                case INVERSE:
                    return InverseFinder.byReSampling(p1, p2.valueAt(0), p3.valueAt(0), p4.valueAt(0));
            }
        } else if (params.size() == 5) {
            var p1 = doOrderOfOperations(params.get(0));
            var p2 = doOrderOfOperations(params.get(1));
            var p3 = doOrderOfOperations(params.get(2));
            var p4 = doOrderOfOperations(params.get(3));
            var p5 = doOrderOfOperations(params.get(4));

            switch (type) {
                case FOURIER_SERIES:
                    return FourierSeries.sN((int) p1.valueAt(0), p2, p3.valueAt(0), p4.valueAt(0), p5.valueAt(0));
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
    public UnaryFunction parse() {
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

            boolean isVariable = (cc == 'x' || cc == 'y' ||cc == 'z') &&
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

    public static UnaryFunction parser(String func) {
        return new Function2DParser().parse(func);
    }
}
