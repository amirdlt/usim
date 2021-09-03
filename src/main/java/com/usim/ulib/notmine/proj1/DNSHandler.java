package com.usim.ulib.notmine.proj1;

import java.io.PrintStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class DNSHandler {
    private final HashMap<String, String> argsMap;
    private final HashMap<String, List<String>> dnsResponse;

    public DNSHandler() {
        argsMap = new HashMap<>();
        dnsResponse = new HashMap<>();
    }

    public void setArgs(String... args) {
        argsMap.clear();
        if (args == null || args.length == 0) {
            System.err.println("ERROR:: Domain name is needed");
            System.err.println("for help use --help");
            System.exit(-1);
        }
        if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
            printHelp(System.err);
            System.exit(0);
        }
        argsMap.put("domain", checkURL(args[0]));
        try {
            for (int i = 1; i < args.length; i++)
                switch (args[i]) {
                    case "-t", "--timeout" -> argsMap.put("timeout", unQuot(args[++i]));
                    case "--max-retries" -> argsMap.put("max-retries", unQuot(args[++i]));
                    case "-p", "--port" -> argsMap.put("port", unQuot(args[++i]));
                    case "--query-type" -> argsMap.put("query-type", unQuot(args[++i].toUpperCase()));
                    case "--server-ip", "-s" -> argsMap.put("server-ip", unQuot(args[++i]));
                    default -> System.err.println("not expected argument: " + args[i]);
                }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("ERROR:: Argument missed");
        }
        argsMap.putIfAbsent("timeout", String.valueOf(4000));
        argsMap.putIfAbsent("max-retries", String.valueOf(5));
        argsMap.putIfAbsent("port", String.valueOf(53));
        argsMap.putIfAbsent("server-ip", "8.8.8.8");
        argsMap.putIfAbsent("query-type", "A");
    }

    public void printHelp(PrintStream ps) {
        ps.println("""
                
                """);
    }

    private static String checkURL(String url) {
        url = unQuot(url);
        try {
            new URL(url).toURI();
            URLDecoder.decode(url, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("ERROR:: Not appropriate url address", e);
        }
        return url;
    }

    private static String unQuot(String str) {
        var res = str.trim();
        while (ICheck.multipleOrCheck(res::startsWith, "\"", "'", "[", "(", "{") && !res.isEmpty())
            res = res.substring(1);
        while (ICheck.multipleOrCheck(res::endsWith, "\"", "'", "]", ")", "}") && !res.isEmpty())
            res = res.substring(0, res.length() - 1);
        return res;
    }

    public void handleRequest(int tryNumber) {
        if (tryNumber >= Integer.parseInt(argsMap.get("max-retries")))
            throw new RuntimeException("ERROR:: Exceeded from max tries: " + --tryNumber);
        try {
            var socket = new DatagramSocket();
            socket.setSoTimeout(Integer.parseInt(argsMap.get("timeout")));
            var address = InetAddress.getByName(argsMap.get("server-ip"));

            var request = new DnsRequest0(argsMap.get("domain"), QueryType.valueOf(argsMap.get("query-type"))).getRequest();
            var response = new byte[1024];

            var requestPacket = new DatagramPacket(request, request.length, address, Integer.parseInt(argsMap.get("port")));
            var responsePacket = new DatagramPacket(response, response.length);

            var startTime = System.currentTimeMillis();
            socket.send(requestPacket);
            socket.receive(responsePacket);
            System.err.println("Response received after " +
                    (System.currentTimeMillis() - startTime) / 1000.0f + " seconds " +
                    "(with " + tryNumber + " retries)");
            socket.close();
            argsMap.put("request-size", String.valueOf(request.length));
            parseResponse(responsePacket.getData());
        } catch (SocketException e) {
            System.err.println("ERROR:: Could not create socket");
        } catch (UnknownHostException e ) {
            System.err.println("ERROR:: Unknown host");
        } catch (SocketTimeoutException e) {
            System.err.println("ERROR:: Socket Timeout Reattempting request...");
            handleRequest(++tryNumber);
        } catch (Exception e) {
            throw new RuntimeException("ERROR:: ", e);
        }
    }

    private void parseResponse(byte[] response) {
        dnsResponse.clear();

        new DNSResponse(response, Integer.parseInt(argsMap.get("request-size")), DNSRequest.QueryType.valueOf(argsMap.get("query-type"))).outputResponse();
    }

    @FunctionalInterface
    private interface ICheck<T> {
        boolean check(T t);
        @SafeVarargs
        static <T, V extends T> boolean multipleOrCheck(ICheck<T> t, V... values) {
            for (var v : values)
                if (t.check(v))
                    return true;
            return false;
        }
    }

    public static void main(String[] args) {
        var h = new DNSHandler();
        h.setArgs("http://www.google.com", "--query-type", "A");
        h.handleRequest(0);
    }
}
