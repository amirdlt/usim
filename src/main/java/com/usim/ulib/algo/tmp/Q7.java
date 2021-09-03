package com.usim.ulib.algo.tmp;

import java.util.*;
import java.util.List;

public class Q7 {
    private static final List<Integer> baseDomain;
    private static long count;
    private static final long modular;
    private static int[] colors;
    private static final List<Node> graph = new ArrayList<>();

    static {
        baseDomain = new ArrayList<>();
        modular = 1_000_000_007;
    }

    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        String[] line = scn.nextLine().split(" ");
        char mode = line[0].charAt(0);
        int n = Integer.parseInt(line[1]);
        int c = Integer.parseInt(line[2]);
        int m = Integer.parseInt(line[3]);
        for (int i = 0; i < c; i++)
            baseDomain.add(i);
        for (int i = 0; i < n; i++)
            graph.add(new Node(i));
        int clone = m;
        while (clone-- > 0) {
            Node n1 = graph.get(scn.nextInt() - 1);
            Node n2 = graph.get(scn.nextInt() - 1);
            n1.adj.add(n2);
            n2.adj.add(n1);
        }
        if (mode == 't') {
            count = 1;
            long base = (c - 1) % modular;
            long power = n - 1;
            while (power > 0) {
                if (power % 2 == 1)
                    count = (count * base) % modular;
                power = power >> 1;
                base = (base * base) % modular;
            }
            count *= c;
            System.out.println(count % modular);
            return;
        }
        colors = new int[n];
        Arrays.fill(colors, -1);
        solve(new boolean[n]);
        System.out.println(count % modular);
    }

    public static void solve(boolean[] visited) {
        boolean allColored = true;
        int index = -1;
        for (int i = 0; i < visited.length; i++) {
            if (!visited[i] && index < 0)
                index = i;
            allColored &= visited[i];
        }
        if (allColored) {
            count++;
            return;
        }
        Node node = graph.get(index);
        visited[node.id] = true;
        for (Integer color : node.domain()) {
            colors[node.id] = color;
            solve(visited.clone());
            colors[node.id] = -1;
        }
    }

    private static class Node {
        private final int id;
        private final List<Node> adj;
        private Node(int id) {
            this.id = id;
            adj = new ArrayList<>();
        }
        private List<Integer> domain() {
            List<Integer> res = new ArrayList<>(baseDomain);
            for (Node node : adj) {
                int index = node.id;
                if (colors[index] != -1)
                    res.remove(Integer.valueOf(colors[index]));
            }
            return res;
        }
    }
}
