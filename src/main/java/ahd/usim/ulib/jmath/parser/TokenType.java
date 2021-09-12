package ahd.usim.ulib.jmath.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum TokenType implements Serializable {
    OPEN_PARENTHESES("("),
    CLOSE_PARENTHESES(")"),
    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIVIDED_BY("/"),
    RAISED_TO("^"),
    SINE("sin"),
    COSINE("cos"),
    TANGENT("tan"),
    COTANGENT("cot"),
    SECANT("sec"),
    CO_SECANT("csc"),
    CEILING("ceil"),
    FLOOR("floor"),
    LOG("log"),
    LOG10("log10"),
    LOG2("log2"),
    MODULO("%"),
    RANDOM("random"),
    SQUARE_ROOT("sqrt"),
    INVERSE("inverse"),
    ABSOLUTE_VALUE("abs"),
    DERIVATIVE("derivative"),
    INTEGRAL("integral"),
    FOURIER_SERIES("fourier"),
    TAYLOR_SERIES("taylor"),
    ARC("arc"),
    ARC3("arc3"),
    AREA("area"),
    LAPLACE("laplace"),
    COMMA(","),
    PI("pi"),
    E("e"),
    POSITIVE_INFINITY("inf"),
    X("x", 0),
    Y("y", 1),
    Z("z", 2),
    XX("X", 0),
    YY("Y", 1),
    ZZ("Z", 2),
    W("w", 3),
    EXTRA_VARIABLE("", -2),
    NUMBER("");

    public static final TokenType[] FUNCTIONS = {
            SINE, COSINE, TANGENT, COTANGENT, SECANT, SQUARE_ROOT,
            CEILING, FLOOR, LOG, MODULO, ABSOLUTE_VALUE, CO_SECANT,
            ARC, AREA, ARC3,
            LOG10, LOG2, INVERSE, TAYLOR_SERIES, RANDOM,
            DERIVATIVE, INTEGRAL, FOURIER_SERIES, LAPLACE
    };

    public static final TokenType[] CONSTANTS = {
            PI, E, POSITIVE_INFINITY
    };

    public static final TokenType[] VARIABLES = {
            X, Y, Z, XX, YY, ZZ, W, EXTRA_VARIABLE
    };

    public static boolean isConstant(TokenType type) {
        return CONSTANT_LIST.contains(type);
    }

    private static final List<TokenType> CONSTANT_LIST = new ArrayList<>(Arrays.asList(CONSTANTS));

    public final String name;

    private int id;

    public int getId() {
        return id;
    }

    public TokenType setId(int id) {
        if (this == X || this == Y || this == Z || this == W)
            return this;
        this.id = id > 3 ? id : -2;
        return this;
    }

    TokenType(String name) {
        this(name, -1);
    }

    TokenType(String name, int id) {
        this.name = name;
        this.id = id;
    }
}
