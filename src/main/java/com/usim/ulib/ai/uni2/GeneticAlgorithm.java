package com.usim.ulib.ai.uni2;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.Math.random;

public class GeneticAlgorithm {
    private final String level;
    private final Point agent;
    private List<Gene> population;
    private final Map<Gene, Integer> solutions;

    public GeneticAlgorithm(String level) {
        this.level = level;
        agent = new Point();
        solutions = new HashMap<>();
        fillPopulationRandomly(300);
    }

    public void nextGeneration() {
        Collections.sort(population);
        int mid = population.size() / 2;
        var totalWeight = 0;
        var weights = new double[mid];
        for (int i = 0; i < mid; i++)
            totalWeight += population.get(i).fitness(false);
        for (int i = 0; i < mid; i++)
            weights[i] = population.get(i).fitness(false) / (double) totalWeight + (i == 0 ? 0 : weights[i - 1]);
        for (int i = population.size() - 1; i >= mid; i--)
            population.set(i, father(weights).remix(father(weights)));
    }

    public void nextGeneration0() {
        Collections.sort(population);
        int mid = population.size() / 2;
        for (int i = 0; i < mid; i++)
            population.set(population.size() - i - 1, population.get(i).remix(population.get(i + 1)));
    }

    private Gene father(double[] weights) {
        var rand = random();
        int counter = -1;
        //noinspection StatementWithEmptyBody
        while (weights[++counter] <= rand);
        return population.get(counter);
    }

    private void fillPopulationRandomly(int size) {
        population = new ArrayList<>(size);
        while (size-- > 0)
            population.add(new Gene());
        solutions.clear();
    }

    public Map<Gene, Integer> getSolutions() {
        population.forEach(e -> e.fitness(true));
        return solutions;
    }

    public List<Gene> getPopulation() {
        Collections.sort(population);
        return population;
    }

    public Point getAgent() {
        return agent;
    }

    public class Gene implements Comparable<Gene> {
        public final int[] data;

        public Gene() {
            data = new int[level.length()];
            Arrays.setAll(data, i -> (int) (3 * random()));
        }

        public Gene(int[] data) {
            this.data = data;
        }

        public Gene remix(Gene gene) {
            var data = new int[level.length()];
            int index = (int) (level.length() * random());
            int counter = 0;
            while (counter <= index)
                data[counter] = this.data[counter++];
            while (counter < level.length())
                data[counter] = gene.data[counter++];
            var g = new Gene(data);
            g.mutate();
            return g;
        }

        public void mutate() {
            if (random() > 0.1)
                return;
            data[(int) (random() * level.length())] = (int) (3 * random());
            mutate();
        }

        public int fitness(boolean addSolutions) {
            int fitness = 0;
            var win = true;
            for (int i = 1; i < data.length; i++) {
                var move = data[i - 1];
                var ch = level.charAt(i);
                if (win)
                    win = !(ch == 'G' && move != 1) && !(ch == 'L' && move != 2);
                switch (ch) {
                    case 'G' -> fitness += move != 1 ? -2 : 2;
                    case 'M' -> fitness += move == 0 ? 3 : 2;
                    case 'L' -> fitness += move != 2 ? -2 : 0;
                    case '_' -> fitness += move == 0 ? 1 : 0;
                }
            }
            if (win) {
                fitness += 5;
                if (addSolutions)
                    solutions.put(this, solutions.getOrDefault(this, 0) + 1);
            }
            if (data[data.length - 1] == 1)
                fitness += 2;
            return Math.max(fitness, 0);
        }

        @Override
        public int compareTo(@NotNull Gene o) {
            return o.fitness(false) - fitness(false);
        }

        @Override
        public String toString() {
            return "f: " + fitness(false) + " " + Arrays.toString(data);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Gene gene = (Gene) o;
            return Arrays.equals(data, gene.data);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }
    }
}
