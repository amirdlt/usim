package com.usim.ulib.ai.uni4;

import java.io.*;
import java.util.*;

public class Database {
    private double lambda1 = 0.05;
    private double lambda2 = 0.15;
    private double lambda3 = 0.8;
    private double epsilon = 0.05;

    private final HashMap<String, Integer> positive;
    private final HashMap<String, Integer> negative;

    private final HashMap<String, Integer> positivePairs;
    private final HashMap<String, Integer> negativePairs;

    private final HashMap<String, Double> positivePossibilities;
    private final HashMap<String, Double> negativePossibilities;

    private final int positiveCount;
    private final int negativeCount;
    private boolean unigram;

    public Database(String positivePath, String negativePath, boolean unigram) throws FileNotFoundException {
        this.unigram = unigram;
        if (unigram) lambda2 += lambda3;
        var pp = fillMap(positivePath);
        var nn = fillMap(negativePath);
        positive = pp.get(0);
        positivePairs = pp.get(1);
        negative = nn.get(0);
        negativePairs = nn.get(1);
        positivePossibilities = new HashMap<>();
        negativePossibilities = new HashMap<>();
        positiveCount = positive.values().stream().mapToInt(e -> e).sum();
        negativeCount = negative.values().stream().mapToInt(e -> e).sum();
    }

    private static List<HashMap<String, Integer>> fillMap(String path) throws FileNotFoundException {
        var res = new ArrayList<HashMap<String, Integer>>();
        var map = new HashMap<String, Integer>();
        var mapPairs = new HashMap<String, Integer>();
        try {
            var reader = new Scanner(new FileReader(path));
            while (reader.hasNextLine()) {
                var list = processLine(reader.nextLine());
                list.forEach(e -> map.put(e, map.getOrDefault(e, 0) + 1));
                var start = "<s> " + list.get(0);
                var end = list.get(list.size() - 1) + " </s>";
                mapPairs.put(start, mapPairs.getOrDefault(start, 0) + 1);
                mapPairs.put(end, mapPairs.getOrDefault(end, 0) + 1);
                for (int i = 0; i < list.size() - 1; i++) {
                    var pair = list.get(i) + " " + list.get(i + 1);
                    mapPairs.put(pair, mapPairs.getOrDefault(pair, 0) + 1);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        }

        res.add(deleteMaximumElements(deleteMinimumElements(map)));
        res.add(mapPairs);
        return res;
    }

    private static HashMap<String, Integer> deleteMinimumElements(HashMap<String, Integer> map) {
        var result = new HashMap<String, Integer>();
        map.forEach((k, v) -> {
            if (v >= 2)
                result.put(k, v);
        });
        return result;
    }

    private static HashMap<String, Integer> deleteMaximumElements(HashMap<String, Integer> map) {
        var entryList = new LinkedList<>(map.entrySet());
        entryList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        HashMap<String, Integer> result = new HashMap<>();
        for (int i = 10; i < entryList.size(); i++) {
            var entry = entryList.get(i);
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private static List<String> processLine(String line) {
        line = line.toLowerCase();
        var words = line.split(" ");
        var result = new ArrayList<String>();
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", "");
            if (word.isBlank())
                continue;
            result.add(word);
        }
        return result;
    }

    private double possibilityPositive(String word) {
        if (word.equals("</s>"))
            return 0;
        var res = positivePossibilities.get(word);
        if (res == null) {
            res = positive.getOrDefault(word, 0) / (double) positiveCount;
            positivePossibilities.put(word, res);
        }
        return res;
    }

    private double possibilityPositive(String word1, String word2) {
        if (unigram)
            return 0;
        var pair = word1 + " " + word2;
        var res = positivePossibilities.get(pair);
        if (res == null) {
            res = positivePairs.getOrDefault(pair, 0) / positive.getOrDefault(word1, Integer.MAX_VALUE).doubleValue();
            positivePossibilities.put(pair, res);
        }
        return res;
    }

    private double possibilityNegative(String word) {
        if (word.equals("</s>"))
            return 0;
        var res = negativePossibilities.get(word);
        if (res == null) {
            res = negative.getOrDefault(word, 0) / (double) negativeCount;
            negativePossibilities.put(word, res);
        }
        return res;
    }

    private double possibilityNegative(String word1, String word2) {
        if (unigram)
            return 0;
        var pair = word1 + " " + word2;
        var res = negativePossibilities.get(pair);
        if (res == null) {
            res = negativePairs.getOrDefault(pair, 0) / negative.getOrDefault(word1, Integer.MAX_VALUE).doubleValue();
            negativePossibilities.put(pair, res);
        }
        return res;
    }

    public double possibility(String word1, String word2, boolean positive) {
        if (positive)
            return lambda3 * possibilityPositive(word1, word2) +
                    lambda2 * possibilityPositive(word2) +
                    lambda1 * epsilon;

        return lambda3 * possibilityNegative(word1, word2) +
                lambda2 * possibilityNegative(word2) +
                lambda1 * epsilon;
    }

    public boolean isUnigram() {
        return unigram;
    }

    public void setUnigram(boolean unigram) {
        if (unigram == this.unigram)
            return;
        positivePossibilities.clear();
        negativePossibilities.clear();
        this.unigram = unigram;
    }

    public double getLambda1() {
        return lambda1;
    }

    public void setLambda1(double lambda1) {
        if (lambda1 == this.lambda1)
            return;
        positivePossibilities.clear();
        negativePossibilities.clear();
        this.lambda1 = lambda1;
    }

    public double getLambda2() {
        return lambda2;
    }

    public void setLambda2(double lambda2) {
        if (lambda2 == this.lambda2)
            return;
        positivePossibilities.clear();
        negativePossibilities.clear();
        this.lambda2 = lambda2;
    }

    public double getLambda3() {
        return lambda3;
    }

    public void setLambda3(double lambda3) {
        if (lambda3 == this.lambda3)
            return;
        positivePossibilities.clear();
        negativePossibilities.clear();
        this.lambda3 = lambda3;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        if (epsilon == this.epsilon)
            return;
        positivePossibilities.clear();
        negativePossibilities.clear();
        this.epsilon = epsilon;
    }

    @Override
    public String toString() {
        return "Database{" + "lambda1=" + lambda1 + ", lambda2=" + lambda2 + ", lambda3=" + lambda3 + ", epsilon=" + epsilon
                + ", unigram=" + unigram + '}';
    }

    public static void main(String[] args) throws FileNotFoundException {
        var db = new Database("tmp/ai4/rt-polarity.pos", "tmp/ai4/rt-polarity.neg", false);
        System.out.println(db.positive.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList());
        System.out.println(db.negative.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList());
        
    }
}
