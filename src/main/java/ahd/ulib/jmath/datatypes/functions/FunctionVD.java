package ahd.ulib.jmath.datatypes.functions;

import org.jetbrains.annotations.NotNull;

import java.util.Vector;

@SuppressWarnings("unused")
public interface FunctionVD extends Function<Double, Vector<Double>> {
    double valueAt(double... params);

    @Override
    default Double valueAt(@NotNull Vector<Double> params) {
        double[] args = new double[params.size()];
        int counter = 0;
        for (var param : params)
            args[counter++] = param;
        return valueAt(args);
    }

    default UnaryFunction f2D(int xIndex, double... allParams) {
        return new UnaryFunction(x -> {
            allParams[xIndex] = x;
            return valueAt(allParams);
        });
    }

    default UnaryFunction f2D(double... allParams) {
        return f2D(0, allParams);
    }

    default BinaryFunction f3D(int xIndex, int yIndex, double... allParams) {
        return new BinaryFunction((x, y) -> {
            allParams[xIndex] = x;
            allParams[yIndex] = y;
            return valueAt(allParams);
        });
    }

    default BinaryFunction f3D(double... allParams) {
        return f3D(0, 0, allParams);
    }

    default FunctionVD fVD(int[] vIndexes, double... allParams) {
        return ps -> {
            int counter = 0;
            for (var p : ps)
                allParams[vIndexes[counter++]] = p;
            return valueAt(allParams);
        };
    }

    default Double atOrigin(int numOfDimensions) {
        double[] args = new double[numOfDimensions];
        for (int i = 0; i < numOfDimensions; i++)
            args[i] = 0;
        return valueAt(args);
    }
}
