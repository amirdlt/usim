package com.usim.ulib.jmath.datatypes;

import com.usim.ulib.jmath.datatypes.functions.Function2D;

import java.io.Serializable;

public interface Operator<Z> extends Serializable {
    Z operate(Function2D f);
}
