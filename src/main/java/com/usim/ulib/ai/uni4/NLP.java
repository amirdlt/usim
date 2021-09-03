package com.usim.ulib.ai.uni4;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class NLP {

    private Database database;

    public NLP(String positivePath, String negativePath, boolean unigram) {
        try {
            database = new Database(positivePath, negativePath, unigram);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public boolean isPositive(String line) {
        line = line.toLowerCase();
        line = "<s> " + line + " </s>";
        var words = Arrays.stream(line.split(" ")).filter(s -> !s.isBlank()).toArray(String[]::new);
        double positivePossibility = 1;
        double negativePossibility = 1;
        for (int i = 0; i < words.length - 1; i++) {
            var word1 = words[i].equals("<s>") ? words[i] : words[i].replaceAll("[^a-zA-Z]", "");
            var word2 = words[i + 1].equals("</s>") ? words[i + 1] : words[i + 1].replaceAll("[^a-zA-Z]", "");
            positivePossibility *= database.possibility(word1, word2, true);
            negativePossibility *= database.possibility(word1, word2, false);
        }
        double portion = positivePossibility / negativePossibility;
        System.out.println("$$ Portion: " + (portion < 1 ? 1 / portion : portion));
        return positivePossibility >= negativePossibility;
    }

    @Override
    public String toString() {
        return "NLP{" + "database=" + database + '}';
    }

    public static void run() {
        var nlp = new NLP("tmp/ai4/rt-polarity.pos", "tmp/ai4/rt-polarity.neg", false);
        System.err.println(nlp);
        var scn = new Scanner(System.in);
        String line;
        while (!(line = scn.nextLine().trim().toLowerCase()).equals("!q")) {
            if (line.startsWith("%"))
                try {
                    var tokens = Arrays.stream(line.substring(1).split("\\s")).filter(s -> !s.isBlank()).toArray(String[]::new);
                    switch (tokens[0]) {
                        case "get" -> System.err.println(tokens[1] + ": " + switch (tokens[1]) {
                            case "unigram" -> nlp.database.isUnigram();
                            case "epsilon" -> nlp.database.getEpsilon();
                            case "lambda1" -> nlp.database.getLambda1();
                            case "lambda2" -> nlp.database.getLambda2();
                            case "lambda3" -> nlp.database.getLambda3();
                            default -> "Not found property";
                        });
                        case "set" -> {
                            switch (tokens[1]) {
                                case "unigram" -> nlp.database.setUnigram(Boolean.parseBoolean(tokens[2]));
                                case "epsilon" -> nlp.database.setEpsilon(Double.parseDouble(tokens[2]));
                                case "lambda1" -> nlp.database.setLambda1(Double.parseDouble(tokens[2]));
                                case "lambda2" -> nlp.database.setLambda2(Double.parseDouble(tokens[2]));
                                case "lambda3" -> nlp.database.setLambda3(Double.parseDouble(tokens[2]));
                                case "lambda" -> {
                                    nlp.database.setLambda1(Double.parseDouble(tokens[2]));
                                    nlp.database.setLambda2(Double.parseDouble(tokens[3]));
                                    nlp.database.setLambda3(Double.parseDouble(tokens[4]));
                                }
                            }
                            System.err.println(tokens[1] + " --> " + tokens[2]);
                        }
                        case "nlp" -> System.err.println(nlp);
                        default -> throw new RuntimeException();
                    }
                } catch (Exception e) {
                    System.err.println("Could not parse: " + line);
                }
            else
                System.out.println((nlp.isPositive(line) ? "not " : "") + "filter this\n");
        }
    }
}
