package com.usim.ulib.ai.genetic;

import org.jetbrains.annotations.NotNull;
import com.usim.ulib.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.random;

public class ImageGenerator {
    private final BufferedImage baseImage;
    private final int width;
    private final int height;
    private int generationCounter;
    private final ImageGene[] population;
    private final int[][] baseBuffer;
    private final int len;

    public ImageGenerator(BufferedImage baseImage, int populationSize) {
        this.baseImage = baseImage;
        var base = Utils.getIntColorArrayOfImage(baseImage);
        width = baseImage.getWidth();
        height = baseImage.getHeight();
        len = width * height;
        this.baseBuffer = new int[len][];
        for (int i = 0; i < len; i++) {
            var c = new Color(base[i]);
            this.baseBuffer[i] = new int[] { c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue() };
        }
        generationCounter = 0;
        population = new ImageGene[populationSize];
        Arrays.setAll(population, i -> new ImageGene());
    }

    public void nextGeneration() {
        Arrays.sort(population);
        int mid = population.length / 2;
        for (int i = 0; i < mid; i++)
            population[population.length - i - 1].remix(population[i], population[i + 1]);
        generationCounter++;
    }

    private class ImageGene implements Comparable<ImageGene> {
        private static int index = 0;
        private final int[] buffer;
        private final BufferedImage image;
        private final int id = index++;

        public ImageGene(int[] buffer) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            this.buffer = Utils.getIntColorArrayOfImage(image);
            System.arraycopy(buffer, 0, this.buffer, 0, width * height);
        }

        public ImageGene(BufferedImage image) {
            this.image = image;
            buffer = Utils.getIntColorArrayOfImage(image);
        }

        public ImageGene() {
            this(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
            Arrays.setAll(buffer, i -> (int) (Integer.MAX_VALUE * random()));
        }

        public int fitness() {
            var res = 0;
            for (int i = 0; i < len; i++) {
                var b = baseBuffer[i];
                var t = new Color(buffer[i]);
                res += abs(b[0] - t.getAlpha()) +
                        abs(b[1] - t.getRed()) +
                        abs(b[2] - t.getGreen()) +
                        abs(b[3] - t.getBlue());
            }
            return res;
        }

        public void save() {
            try {
                ImageIO.write(image, "png",
                        new File("tmp/image/" + generationCounter + "-" + id + "-" + fitness() / len + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void remix(ImageGene gene1, ImageGene gene2) {
            int index = (int) (len * random());
            int counter = 0;
            while (counter <= index)
                buffer[counter] = gene1.buffer[counter++];
            while (counter < len)
                buffer[counter] = gene2.buffer[counter++];
            mutate();
        }

        public void mutate() {
//            if (random() > 0.9)
//                return;
            var seed = 0.17 * len;
            for (int i = 0; i < seed ; i++)
                buffer[(int) (random() * len)] = (int) (Integer.MAX_VALUE * random());
//            mutate();
        }

        @Override
        public int compareTo(@NotNull ImageGene o) {
            return fitness() - o.fitness();
        }
    }

    public double fitness() {
        return Arrays.stream(population).mapToInt(ImageGene::fitness).average().orElse(0);
    }

    public static void main(String[] args) throws IOException {
        var ig = new ImageGenerator(Utils.readImage("tmp/me.JPG"), 10);
        var count = 500;
        while (count-- > 0) {
            ig.nextGeneration();
            System.out.println(ig.fitness() / ig.len);
        }
        Arrays.sort(ig.population);
        ig.population[0].save();
    }
}
