package com.usim.ulib.notmine.hho;

import java.util.*;
import java.util.stream.DoubleStream;

public class HarrisHawksOptimizations {

    double[] Lower;
    double[] Upper;
    int N;
    int D;
    int Maxiter;
    f_xj ff;
    int iter;

    double[] Rabbit_Location;
    double Rabbit_Energy;
    double[] CNVG;
    double[][] X;
    double[] X1;
    double[] X2;
    double[] fitnessX;
    double[] mean2Dmat;
    double[] X_rand;
    double E1;
    double E0;
    double Escaping_Energy;

    double q;
    double r;
    int rand_Hawk_index;

    public HarrisHawksOptimizations(f_xj iff, int iN, double[] iLower, double[] iUpper, int iMaxiter) {
        Lower = iLower;
        Upper = iUpper;
        ff = iff;
        N = iN;
        D = Lower.length;
        Maxiter = iMaxiter;
        Rabbit_Location = new double[D];
        CNVG = new double[Maxiter];
        fitnessX = new double[N];
        X = new double[N][D];
        mean2Dmat = new double[D];
        X1 = new double[D];
        X2 = new double[D];
        X_rand = new double[D];
        Rabbit_Energy = 1e80;
    }

    static double mean(double[] X) {
        return DoubleStream.of(X).average().orElse(0);
    }

    static double[] mean2D(double[][] XX) {
        int m = XX.length;
        int n = XX[0].length;
        double[] X = new double[m];
        double[] Y = new double[n];

        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++)
                X[i] = XX[i][j];
            Y[j] = mean(X);
        }
        return Y;
    }

    double logGamma(double x) {
        double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
        double ser =
                1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1) + 24.01409822 / (x + 2) - 1.231739516 / (x + 3) + 0.00120858003 / (
                        x + 4) - 0.00000536382 / (x + 5);
        return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
    }

    double gamma(double x) {
        return Math.exp(logGamma(x));
    }

    double[] Levy() {
        double beta = 1.5;
        double sigma = Math.pow(
                (gamma(1 + beta) * Math.sin(Math.PI * beta / 2) / (gamma((1 + beta) / 2) * beta * Math.pow(2, ((beta - 1) / 2)))),
                (1 / beta));
        Random rnd = new Random();
        double[] u = new double[D];
        double[] v = new double[D];
        double[] step = new double[D];
        for (int j = 0; j < D; j++)
            u[j] = rnd.nextGaussian() * sigma;
        for (int j = 0; j < D; j++)
            v[j] = rnd.nextGaussian();
        for (int j = 0; j < D; j++)
            step[j] = u[j] / (Math.pow(Math.abs(v[j]), (1.0 / beta)));
        return step;
    }

    double[][] boundary(double[][] XX) {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < D; j++)
                if ((XX[i][j] < Lower[j]) || (XX[i][j] > Upper[j]))
                    XX[i][j] = Lower[j] + ((Upper[j] - Lower[j]) * Math.random());
        return XX;
    }

    void init() {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < D; j++)
                X[i][j] = (Lower[j] + ((Upper[j] - Lower[j]) * Math.random()));
    }

    double[][] solution() {
        init();
        iter = 0;
        while (iter < Maxiter) {
            X = boundary(X);
            for (int i = 0; i < N; i++) {
                fitnessX[i] = ff.func(X[i]);
                if (fitnessX[i] < Rabbit_Energy) {
                    Rabbit_Energy = fitnessX[i];
                    if (D >= 0)
                        System.arraycopy(X[i], 0, Rabbit_Location, 0, D);
                }
            }

            E1 = 2.0 * (1.0 - ((double) iter / (double) Maxiter));

            for (int i = 0; i < N; i++) {
                E0 = 2.0 * Math.random() - 1.0;
                Escaping_Energy = E1 * E0;

                if (Math.abs(Escaping_Energy) >= 1.0) {
                    q = Math.random();
                    rand_Hawk_index = (int) Math.floor((double) N * Math.random());
                    if (D >= 0)
                        System.arraycopy(X[rand_Hawk_index], 0, X_rand, 0, D);
                    if (q < 0.5) {
                        for (int j = 0; j < D; j++)
                            X[i][j] = X_rand[j] - Math.random() * Math.abs(X_rand[j] - 2.0 * Math.random() * X[i][j]);
                    } else if (q >= 0.5) {
                        mean2Dmat = mean2D(X);
                        for (int j = 0; j < D; j++)
                            X[i][j] = (Rabbit_Location[j] - mean2Dmat[j]) - (Math.random() * (Lower[j] + ((Upper[j] - Lower[j])
                                    * Math.random())));
                    }
                } else if (Math.abs(Escaping_Energy) < 1.0) {
                    r = Math.random();
                    if ((r >= 0.5) && (Math.abs(Escaping_Energy) < 0.5))
                        for (int j = 0; j < D; j++)
                            X[i][j] = Rabbit_Location[j] - Escaping_Energy * Math.abs(Rabbit_Location[j] - X[i][j]);
                    if ((r >= 0.5) && (Math.abs(Escaping_Energy) >= 0.5))
                        for (int j = 0; j < D; j++)
                            X[i][j] = (Rabbit_Location[j] - X[i][j]) - Escaping_Energy * Math.abs(
                                    2.0 * (1.0 - Math.random()) * Rabbit_Location[j] - X[i][j]);
                    if ((r < 0.5) && (Math.abs(Escaping_Energy) >= 0.5)) {
                        for (int j = 0; j < D; j++)
                            X1[j] = Rabbit_Location[j] - Escaping_Energy * Math.abs(
                                    2.0 * (1.0 - Math.random()) * Rabbit_Location[j] - X[i][j]);
                        if (ff.func(X1) < ff.func(X[i])) {
                            if (D >= 0)
                                System.arraycopy(X1, 0, X[i], 0, D);
                        } else {
                            double[] Levyout = Levy();
                            for (int j = 0; j < D; j++)
                                X2[j] = Rabbit_Location[j] - Escaping_Energy * Math.abs(
                                        2.0 * (1.0 - Math.random()) * Rabbit_Location[j] - X[i][j]) + Math.random() * Levyout[j];
                            if (ff.func(X2) < ff.func(X[i]))
                                if (D >= 0)
                                    System.arraycopy(X2, 0, X[i], 0, D);
                        }
                    }
                    if ((r < 0.5) && (Math.abs(Escaping_Energy) < 0.5)) {
                        mean2Dmat = mean2D(X);
                        for (int j = 0; j < D; j++) {
                            X1[j] = Rabbit_Location[j] - Escaping_Energy * Math.abs(
                                    2.0 * (1.0 - Math.random()) * Rabbit_Location[j] - mean2Dmat[j]);
                        }
                        if (ff.func(X1) < ff.func(X[i])) {
                            if (D >= 0)
                                System.arraycopy(X1, 0, X[i], 0, D);
                        } else {
                            double[] Levyout = Levy();
                            mean2Dmat = mean2D(X);
                            for (int j = 0; j < D; j++)
                                X2[j] = Rabbit_Location[j] - Escaping_Energy * Math.abs(
                                        2.0 * (1.0 - Math.random()) * Rabbit_Location[j] - mean2Dmat[j]) + Math.random() * Levyout[j];
                            if (ff.func(X2) < ff.func(X[i]))
                                if (D >= 0)
                                    System.arraycopy(X2, 0, X[i], 0, D);
                        }
                    }
                }
            }
            CNVG[iter] = Rabbit_Energy;
            iter++;
        }
        double[][] out = new double[2][D];
        System.arraycopy(Rabbit_Location, 0, out[0], 0, D);
        out[1][0] = Rabbit_Energy;
        return out;
    }

    void toStringnew() {
        double[][] out = solution();
        System.out.println("Optimized value = " + out[1][0]);
        for (int i = 0; i < D; i++) {
            System.out.println("x[" + i + "] = " + out[0][i]);
        }
    }
}

interface f_xj {
    double func(double[] x);
}
