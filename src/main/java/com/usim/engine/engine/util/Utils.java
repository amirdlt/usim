package com.usim.engine.engine.util;

import java.io.InputStream;
import java.util.Scanner;

public class Utils {

    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream(fileName)) {
            assert in != null;
            try (Scanner scanner = new Scanner(in, java.nio.charset.StandardCharsets.UTF_8.name())) {
                result = scanner.useDelimiter("\\A").next();
            }
        }
        return result;
    }

}
