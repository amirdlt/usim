package com.usim.ulib.algo.tmp;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Q2 {
    private final static Scanner scanner;

    static {
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        scanner.nextLine();
        long[] tt = Arrays.stream(scanner.nextLine().split(" ")).mapToLong(Long::parseLong).toArray();
        long[] ww = Arrays.stream(scanner.nextLine().split(" ")).mapToLong(Long::parseLong).toArray();
        List<Job> jobs = new ArrayList<>();
        for (int i = 0; i < tt.length; i++)
            jobs.add(new Job(tt[i], ww[i]));
        Collections.sort(jobs);
        long time = 0;
        long cost = 0;
        for (Job job : jobs)
            cost += (time += job.time) * job.weight;
        System.out.println(cost);
    }

    private static class Job implements Comparable<Job> {
        private final long weight;
        private final long time;
        private final double portion;

        private Job(long time, long weight) {
            this.weight = weight;
            this.time = time;
            portion = (double) weight / time;
        }

        @Override
        public int compareTo(@NotNull Job o) {
            return portion == o.portion ? Long.compare(o.weight, weight) : Double.compare(o.portion, portion);
        }
    }
}
