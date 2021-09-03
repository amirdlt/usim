package com.usim.ulib.jmath.datatypes.matrix;

public class MatUtils {
    public static void set(double[][] mat1, double[][] mat2) {

    }

    public static double[][] mul(double[][] mat1, double[][] mat2) {
        var res = new double[3][3];

        for (int i = 0; i < 3; i ++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++)
                    res[i][j] += mat1[i][k] * mat2[k][j];

        return res;
    }

    public static void mulAndSet(double[][] mat1, double[][] mat2) {

    }
}
