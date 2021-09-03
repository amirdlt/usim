package com.usim.ulib.notmine.proj8;

public class HanoiTower {
    private static final int numOfDisks = 4;
    private static final char[][] shape = new char[numOfDisks + 1][3];
    private static final int[] towerHeights = new int[3];

    public static void main(String[] args) {
        shape[numOfDisks][0] = 'A';
        shape[numOfDisks][1] = 'B';
        shape[numOfDisks][2] = 'C';
        for (int i = 0; i < numOfDisks; i++)
            for (int j = 0; j < 3; j++)
                shape[i][j] = j == 0 ? '*' : ' ';
        towerHeights[0] = numOfDisks;
        showShape();
        doTowers(numOfDisks, 'A', 'B', 'C');
    }

    private static void doTowers(int topN, char from, char inter, char to) {
        if (topN == 1) {
//            System.out.println("Disk 1 from " + from + " to " + to);
            shape[numOfDisks - towerHeights[from - 'A']][from - 'A'] = ' ';
            towerHeights[from - 'A']--;
            towerHeights[to - 'A']++;
            shape[numOfDisks - towerHeights[to - 'A']][to - 'A'] = '*';
            showShape();
        } else {
            doTowers(topN - 1, from, to, inter);
            shape[numOfDisks - towerHeights[from - 'A']][from - 'A'] = ' ';
            towerHeights[from - 'A']--;
            towerHeights[to - 'A']++;
            shape[numOfDisks - towerHeights[to - 'A']][to - 'A'] = '*';
            showShape();
//            System.out.println("Disk " + topN + " from " + from + " to " + to);
            doTowers(topN - 1, inter, from, to);
        }
    }

    private static void showShape() {
        for (char[] line : shape) {
            for (char ch : line)
                System.out.print(ch + "   ");
            System.out.println();
        }
        System.out.println("-------------");
    }
}
