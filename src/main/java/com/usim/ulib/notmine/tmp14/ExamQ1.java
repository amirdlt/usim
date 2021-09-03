package com.usim.ulib.notmine.tmp14;

import java.util.Arrays;
import java.util.Scanner;

public class ExamQ1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a text then print ^END at it end: ");
        String line;
        int numOfSentences = 0;
        while (!(line = scanner.nextLine()).equals("^END"))
            numOfSentences += Arrays.stream(line.replace("\n", ".").
                    replace(":", ".").split("\\.")).filter(s -> !s.isBlank()).count();
        System.out.println("Number of sentences in the text is " + numOfSentences);
    }
}
