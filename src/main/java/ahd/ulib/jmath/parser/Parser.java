package ahd.ulib.jmath.parser;

import java.io.Serializable;

@SuppressWarnings("unused")
public interface Parser<T> extends Serializable {
    T parse(String expression);
    default T parse() {return null;}
}
