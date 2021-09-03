package com.usim.ulib.ai.uni2;

import com.usim.ulib.swingutils.MainFrame;
import com.usim.ulib.utils.Utils;
import com.usim.ulib.visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Runner {
    private static void run0(FitnessModel fitnessModel, String level) {
        var f = new MainFrame();

        var gp = new Graph3DCanvas();
        gp.setBackground(new Color(106, 133, 250));

        var v = new Visualization(gp, level);
        var algo = v.getAlgorithm();
        gp.addRender(v);

        var population = new JTextArea();
        int counter = 0;
        for (var p : algo.getPopulation())
            population.append(++counter + ") " + p + '\n');
        population.setEditable(false);
        population.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

        var sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(population), gp);
        f.add(sp);

        new Thread(() -> {
            int generationCounter = 0;
            int back = Math.min(15, Math.max(3, level.length() / 10));
            double[] variances = new double[back];
            Arrays.fill(variances, Double.MAX_VALUE);
            int index = 0;
            variances[index++ % back] = variance(algo.getPopulation());
            while (generationCounter < 2000 && isDiverge(variances, 0.01)) {
                System.err.println(Arrays.toString(variances));
                algo.nextGeneration0();
                algo.nextGeneration();
                population.setText("");
                int counter_ = 0;
                for (var p : algo.getPopulation())
                    population.append(++counter_ + ") " + p + '\n');
                Utils.sleep(1000);
                variances[index++ % back] = variance(algo.getPopulation());
                f.setTitle("Generation Count: " + ++generationCounter);
            }
            System.out.println(algo.getSolutions());
            System.err.println(Arrays.toString(variances));
        }).start();

        SwingUtilities.invokeLater(f);
    }

    private static boolean isDiverge(double[] variances, double compare) {
        for (var v : variances)
            if (v > compare)
                return true;
        return false;
    }

    private static double variance(List<GeneticAlgorithm.Gene> population) {
        var fitness = population.subList(0, population.size() / 2).stream().map(gene -> gene.fitness(false)).toList();
        var mean = fitness.stream().mapToInt(e -> e).average().orElse(0);
        return Math.sqrt(fitness.stream().mapToDouble(f -> (mean - f) * (mean - f)).average().orElse(0));
    }

    public static void run(FitnessModel fitnessModel, String filePath) throws IOException {
        run0(fitnessModel, Utils.getFileAsString(filePath));
    }
}
