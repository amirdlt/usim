package com.usim.ulib.ai;

import com.usim.ulib.ai.uni2.Runner;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Runner.run(null, "tmp/levels/level8.txt");
//        Runner.run(PathFinderAlgorithm.IDS, "tmp/input/test3.txt");
    }
}
