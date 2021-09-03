package com.usim.ulib.jmath.parser;

import java.io.Serializable;

public class Token implements Serializable {
    public final TokenType type;
    public final String data;
    public final String name;

    public Token(TokenType type, String data, String name) {
        this.type = type;
        this.data = data;
        this.name = name;
    }

    public Token(TokenType type, String data) {
        this(type, data, "");
    }

    public Token(TokenType type) {
        this(type, "");
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
