package ahd.usim.ulib.jmath.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TokenString implements Serializable {
    private final List<Token> tokens;

    public TokenString() {
        tokens = new ArrayList<>();
    }

    private TokenString(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public Token tokenAt(int i) {
        return tokens.get(i);
    }

    public int getLength() {
        return tokens.size();
    }

    public TokenString split(int start, int end) {
        start = Math.max(0, start);
        end = Math.min(tokens.size(), end);

        List<Token> subList = new ArrayList<>();
        for (int i = start; i < end; i++)
            subList.add(tokens.get(i));

        return new TokenString(subList);
    }

    public void insert(int i, Token token) {
        tokens.add(i, token);
    }

    public void remove(int i) {
        tokens.remove(i);
    }

    @Override
    public String toString() {
        StringBuilder line = new StringBuilder();
        for (var t : tokens) {
            line.append(t.toString());
            if (t.data.length() > 0)
                line.append("<").append(t.data).append(">");
            line.append(" ");
        }
        return line.toString();
    }
}
