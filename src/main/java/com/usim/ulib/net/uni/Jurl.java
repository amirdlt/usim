package com.usim.ulib.net.uni;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public final class Jurl {
    private final static List<String> supportedFileFormatForDownload;
    private final static Map<Integer, String> httpCodeMessages;

    static {
        supportedFileFormatForDownload = List.of("rar", "zip", "jpg", "png", "pdf", "jpeg", "mp3",
                "mp4", "mkv", "wav", "webm", "epub", "jar", "exe", "txt", "apk", "file");
        httpCodeMessages = new HashMap<>();
        httpCodeMessages.put(200, "OK");
        httpCodeMessages.put(201, "Created");
        httpCodeMessages.put(202, "Accepted");
        httpCodeMessages.put(203, "Non-Authoritative");
        httpCodeMessages.put(204, "No Content");
        httpCodeMessages.put(205, "Reset Content");
        httpCodeMessages.put(206, "Partial Content");
        httpCodeMessages.put(300, "Multiple Choices");
        httpCodeMessages.put(301, "Moved Permanently");
        httpCodeMessages.put(302, "Found");
        httpCodeMessages.put(303, "See Other");
        httpCodeMessages.put(304, "Not Modified");
        httpCodeMessages.put(305, "Use Proxy");
        httpCodeMessages.put(400, "Bad Request");
        httpCodeMessages.put(401, "Unauthorized");
        httpCodeMessages.put(402, "Payment Required");
        httpCodeMessages.put(403, "Forbidden");
        httpCodeMessages.put(404, "Not Found");
        httpCodeMessages.put(405, "Method Not Allowed");
        httpCodeMessages.put(406, "Not Acceptable");
        httpCodeMessages.put(407, "Proxy Authentication Required");
        httpCodeMessages.put(408, "Request Timeout");
        httpCodeMessages.put(409, "Conflict");
        httpCodeMessages.put(410, "Gone");
        httpCodeMessages.put(411, "Length Required");
        httpCodeMessages.put(412, "Precondition Failed");
        httpCodeMessages.put(413, "Payload Too Large");
        httpCodeMessages.put(414, "URI Too Long");
        httpCodeMessages.put(415, "Unsupported Media Type");
        httpCodeMessages.put(500, "Internal Server Error");
        httpCodeMessages.put(501, "Not Implemented");
        httpCodeMessages.put(502, "Bad Gateway");
        httpCodeMessages.put(503, "Service Unavailable");
        httpCodeMessages.put(504, "Gateway Timeout");
        httpCodeMessages.put(505, "HTTP Version Not Supported");
    }

    private final HttpClient client;
    private Map<String, List<String>> argsMap;
    private HttpResponse<String> response;
    private HttpResponse<InputStream> response0;

    public Jurl(String... args) throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        parseArgs(args);
        response = null;
        response0 = handleHttpRequest0();

        if (firstOf("method").equals("GET")) {
            try {
                findFormatFromResponseHeader();
            } catch (Exception e) {
                if (argsMap.containsKey("file"))
                    System.err.println("AHD:: file format from header is not supported");
            } finally {
                if (argsMap.containsKey("file") && !argsMap.containsKey("file-format"))
                    setArg("file-format", "txt");
            }

            if (argsMap.containsKey("file-format")) {
                var url = firstOf("url").trim();
                while (url.endsWith("/"))
                    url = url.substring(0, url.length() - 1);
                setArg("file-name", url.substring(url.lastIndexOf("/") + 1));
                var response = response0.headers().map();
                setArg("download-length", response.containsKey("content-length") ? response.get("content-length").get(0) : String.valueOf(100));
            }
        }

        if (argsMap.containsKey("file-format"))
            downloadFile();
    }

    private String checkURL(String url) {
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

    private void findFormatFromResponseHeader() {
        try {
            var contentType = response0.headers().map().get("content-type").get(0).toLowerCase();
            if (contentType.contains("octet-stream") || contentType.contains("download")) {
                var url = firstOf("url");
                while (url.endsWith("/"))
                    url = url.substring(0, url.length() - 1);
                setArg("file-format", url.contains(".") ? url.substring(url.lastIndexOf('.') + 1) : "file");
            } else {
                for (var v : supportedFileFormatForDownload)
                    if (contentType.contains(v)) {
                        setArg("file-format", v);
                        System.err.println("file format detected: " + v);
                        break;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            System.err.println("WARNING:: couldn't retrieve file format from header.");
            throw new RuntimeException();
        }
    }

    private HttpRequest createHttpRequest() {
        var res = HttpRequest.
                newBuilder().
                uri(URI.create(firstOf("url")));
        switch (firstOf("method")) {
            case "GET" -> res.GET();
            case "POST" -> {
                if (argsMap.containsKey("file")) {
                    try {
//                        res.POST(HttpRequest.BodyPublishers.ofFile(Path.of(firstOf("file"))));
                        res.POST(HttpRequest.BodyPublishers.ofInputStream(() -> fileStreamToUpload(firstOf("file"))));
                    } catch (Exception e) {
                        System.err.println("WARNING:: Error in finding file");
                    }
                } else if (argsMap.containsKey("json") || argsMap.containsKey("data")) {
                    res.POST(HttpRequest.BodyPublishers.ofString(firstOf("data")));
                } else {
                    res.POST(HttpRequest.BodyPublishers.noBody());
                }
            }
            case "HEAD" -> {
                if (argsMap.containsKey("file")) {
                    try {
//                        res.method("PATCH", HttpRequest.BodyPublishers.ofFile(Path.of(firstOf("file"))));
                        res.method("PATCH", HttpRequest.BodyPublishers.ofInputStream(() -> fileStreamToUpload(firstOf("file"))));
                    } catch (Exception e) {
                        System.err.println("WARNING:: Error in finding file");
                    }
                } else if (argsMap.containsKey("json") || argsMap.containsKey("data")) {
                    res.method("HEAD", HttpRequest.BodyPublishers.ofString("{\"" + firstOf("data").trim() + "\"}"));
                } else {
                    res.method("PATCH", HttpRequest.BodyPublishers.noBody());
                }
            }
            case "PUT" -> {
                if (argsMap.containsKey("file")) {
                    try {
//                        res.PUT(HttpRequest.BodyPublishers.ofFile(Path.of(firstOf("file"))));
                        res.PUT(HttpRequest.BodyPublishers.ofInputStream(() -> fileStreamToUpload(firstOf("file"))));
                    } catch (Exception e) {
                        System.err.println("WARNING:: Error in finding file");
                    }
                } else if (argsMap.containsKey("json") || argsMap.containsKey("data")) {
                    res.PUT(HttpRequest.BodyPublishers.ofString(firstOf("data").trim()));
                } else {
                    res.PUT(HttpRequest.BodyPublishers.noBody());
                }
            }
            case "PATCH" -> {
                if (argsMap.containsKey("file")) {
                    try {
//                        res.method("PATCH", HttpRequest.BodyPublishers.ofFile(Path.of(firstOf("file"))));
                        res.method("PATCH", HttpRequest.BodyPublishers.ofInputStream(() -> fileStreamToUpload(firstOf("file"))));
                    } catch (Exception e) {
                        System.err.println("WARNING:: Error in finding file");
                    }
                } else if (argsMap.containsKey("json") || argsMap.containsKey("data")) {
                    res.method("PATCH", HttpRequest.BodyPublishers.ofString(firstOf("data").trim()));
                } else {
                    res.method("PATCH", HttpRequest.BodyPublishers.noBody());
                }
            }
            case "DELETE" -> res.DELETE();
            default -> throw new RuntimeException("ERROR:: This method is not supported by jurl");
        }
        res.setHeader("user-agent", firstOf("user-agent"));
        for (var header : allValues("headers")) {
            if (header == null || header.isEmpty())
                continue;
            var kv = header.split(":");
            try {
                res.setHeader(kv[0], kv[1]);
            } catch (Exception e) {
                System.err.println("WARNING:: Bad header format");
            }
        }
        res.timeout(Duration.ofMillis(Integer.parseInt(firstOf("timeout"))));
        res.version(HttpClient.Version.valueOf(firstOf("version")));
        if (argsMap.containsKey("content-type"))
            return res.build();
        if (argsMap.containsKey("data"))
            res.setHeader("content-type", "application/x-www-form-urlencoded");
        if (argsMap.containsKey("json"))
            res.setHeader("content-type", "application/json");
        if (argsMap.containsKey("file"))
            res.setHeader("content-type", "application/octet-stream");
        return res.build();
    }

    private InputStream fileStreamToUpload(String path) {
        try {
            return new BufferedInputStream(new FileInputStream(path)) {
                private final long len = new File(path).length() / 1024;
                {System.err.println("Uploading file (" + path + "): ");}
                private final ProgressBar pb = new ProgressBar(
                        " ",
                        len,
                        500,
                        System.err,
                        ProgressBarStyle.ASCII,
                        "KB",
                        1,
                        true,
                        new DecimalFormat(".##"),
                        ChronoUnit.SECONDS,
                        0L,
                        Duration.ZERO);
                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    pb.stepBy(len/1024);
                    return super.read(b, off, len);
                }

                @Override
                public int readNBytes(byte[] b, int off, int len) throws IOException {
                    pb.stepBy(len/1024);
                    return super.readNBytes(b, off, len);
                }
            };
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound", e);
        }
    }

    private void downloadFile() throws IOException, InterruptedException {
        var stream = new BufferedInputStream((response0 == null ? handleHttpRequest0() : response0).body());
        int numRead;
        byte[] data = new byte[1024];
        var path = firstOf(argsMap.containsKey("file") ? "file" : "file-name");
        if (!path.contains(".") && argsMap.containsKey("file-format"))
            path = path + "." + firstOf("file-format");
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        var file = new File(path);
        var writer = new FileOutputStream(file);
        long len = Long.parseLong(firstOf("download-length")) / 1024;
        System.err.println("Downloading file (" + path + "): ");
        try (var pb = new ProgressBar(
                " ",
                len,
                500,
                System.err,
                ProgressBarStyle.ASCII,
                "KB",
                1,
                true,
                new DecimalFormat(".##"),
                ChronoUnit.SECONDS,
                0L,
                Duration.ZERO)) {
            while((numRead = stream.read(data, 0, 1024)) != -1) {
                writer.write(data, 0, numRead);
                pb.stepBy(numRead / 1024);
            }
            pb.stepTo(len);
        } catch (Exception e) {
            System.err.println("There was an error in downloading file.");
        }
        System.err.println("Download Ended " + file);
        writer.close();
    }

    private String firstOf(String key, Runnable actionIfNotExist) {
        if (!argsMap.containsKey(key)) {
            actionIfNotExist.run();
            return "";
        }
        return argsMap.get(key).get(0);
    }

    private String firstOf(String key) {
        return firstOf(key, () -> System.err.println("WARNING:: Couldn't find: " + key + " argument"));
    }

    private List<String> allValues(String key) {
        if (!argsMap.containsKey(key))
            return List.of("");
        return argsMap.get(key);
    }

    private static String headersAsString(Map<String, List<String>> headers) {
        var sb = new StringBuilder();
        for (var kv : headers.entrySet()) {
            sb.append(kv.getKey());
            if (kv.getValue().size() == 1) {
                sb.append(" : ").append(kv.getValue().get(0));
            } else if (!kv.getValue().isEmpty()) {
                sb.append(" : // multiple values\n\t{\n");
                for (int i = 0; i < kv.getValue().size() - 1; i++)
                    sb.append("\t  ").append(kv.getValue().get(i)).append(",\n");
                sb.append("\t  ").append(kv.getValue().get(kv.getValue().size()-1)).append("\n\t}");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String headersAsString() {
//        createHttpRequest();
        return headersAsString(getResponseHeaders0());
    }

    public HttpResponse<String> handleHttpRequest() throws IOException, InterruptedException {
        var req = createHttpRequest();
        if (argsMap.containsKey("file-format"))
            try {
                downloadFile();
                return response = client.send(HttpRequest.
                                newBuilder().
                                uri(URI.create(firstOf("url"))).
                                method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build(),
                        HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        if (req == null)
            return response = null;
        response = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (argsMap.containsKey("redirect") && response.headers().map().containsKey("location")) {
            setArg("url", response.headers().map().get("location").get(0));
            handleHttpRequest();
        }
        return response;
    }

    public HttpResponse<String> setArgs(String[] args) throws IOException, InterruptedException {
        parseArgs(args);
        return handleHttpRequest();
    }

    private Map<String, List<String>> getResponseHeaders() {
        if (response != null)
            return response.headers().map();
        try {
            return client.send(HttpRequest.
                    newBuilder().
                    uri(URI.create(firstOf("url"))).
                    method(firstOf("method"), HttpRequest.BodyPublishers.noBody()).build(),
                    HttpResponse.BodyHandlers.ofString()).headers().map();
        } catch (Exception e) {
            throw new RuntimeException("WARNING:: impossible to get headers", e);
        }
    }

    private Map<String, List<String>> getResponseHeaders0() {
        if (response0 != null)
            return response0.headers().map();
        try {
            return (response0 = client.send(createHttpRequest(), HttpResponse.BodyHandlers.ofInputStream())).headers().map();
        } catch (Exception e) {
            throw new RuntimeException("WARNING:: impossible to get headers", e);
        }
    }

    private void parseArgs(String[] args) {
        if (args.length == 0) {
            System.err.println("WARNING:: Jurl can't do anything without URL address");
            args = new String[] {"--help"};
        }
        if (args[0].equalsIgnoreCase("--help")) {
            System.err.println(getHelp());
            System.exit(0);
        }
        setArg("url", checkURL(unQuot(args[0])));
        boolean multipleTimeDataSet = false;
        for (int i = 1; i < args.length; i++) {
            if (args[i] == null || args[i].trim().isEmpty())
                continue;
            switch (args[i].trim()) {
                case "-M", "--method" -> setArg("method", args[++i].toUpperCase());
                case "-f", "--form-urlencoded" -> {
                    setArg("form-urlencoded");
                    try {
                        if (argsMap.containsKey("data"))
                            multipleTimeDataSet = true;
                        setArg("data", checkFormUrlEncoded(args[++i]));
                    } catch (Exception e) {
                        System.err.println("WARNING:: Bad form-urlencoded format");
                        setArg("data", args[i]);
                    }
                }
                case "--json", "-J" -> {
                    setArg("json");
                    try {
                        if (argsMap.containsKey("data"))
                            multipleTimeDataSet = true;
                        argsMap.put("data", List.of(checkJsonFormat(args[++i])));
                    } catch (Exception e) {
                        argsMap.put("data", List.of(args[i]));
                        System.err.println("WARNING:: Bad json format " + args[i]);
                    }
                }
                case "-D", "--data" -> {
                    if (argsMap.containsKey("data"))
                        multipleTimeDataSet = true;
                    argsMap.put("data", List.of(args[++i]));
                }
                case "-R", "--redirect" -> setArg("redirect");
                case "--headers", "-H" -> addArg("headers", args[++i].toLowerCase().split(","));
                case "-F", "--file" -> setArg("file", args[++i]);
                case "--timeout", "-T" -> setArg("timeout", args[++i]);
                case "--version", "-V" -> setArg("version", args[++i].toUpperCase());
                case "--user-agent" -> setArg("user-agent", args[++i]);
                default -> throw new RuntimeException("ERROR:: unexpected argument: " + args[i]);
            }
        }
        if (!argsMap.containsKey("method"))
            setArg("method", argsMap.containsKey("json") || argsMap.containsKey("data") || argsMap.containsKey("file") ? "POST" : "GET");
        if (!argsMap.containsKey("timeout"))
            setArg("timeout", String.valueOf(Integer.MAX_VALUE));
        if (!argsMap.containsKey("user-agent"))
            setArg("user-agent", "AHD_JURL");
        if (!argsMap.containsKey("version"))
            setArg("version", HttpClient.Version.HTTP_1_1.toString());
        if (multipleTimeDataSet)
            System.err.println("WARNING:: you have entered multiple body, (The last will be consider)");
        checkConflictInArgs();
    }

    private void checkConflictInArgs() {
        var method = firstOf("method");
        var res = method.equals("GET") || method.equals("DELETE");
        res &= argsMap.containsKey("data") || argsMap.containsKey("json");
        if (res)
            System.err.println("WARNING:: Bad combination of method and data or json flags");
        if (!argsMap.containsKey("content-type") && argsMap.containsKey("data") && !argsMap.containsKey("json")) {
            try {
                checkFormUrlEncoded(firstOf("data"));
            } catch (Exception e) {
                System.err.println("WARNING:: Bad format of default form-urlencoded");
            }
        }
        if (!argsMap.containsKey("content-type"))
            return;
        if (firstOf("content-type").equals("application/json") || (argsMap.containsKey("json") && !argsMap.containsKey("file"))) {
            try {
                checkJsonFormat(firstOf("data"));
            } catch (Exception e) {
                System.err.println("WARNING:: Bad format of json " + "{" + "\"" + firstOf("data") + "\"" + "}");
            }
        }
    }

    private String checkFormUrlEncoded(String form) {
        try {
            var map = new HashMap<String, String>();
            Arrays.stream(
                    unQuot(form).split("&")).
                    map(kv -> unQuot(kv).split("=")).
                    forEach(kv -> {
                        if (kv.length != 2) throw new RuntimeException();
                        map.put(unQuot(kv[0]), unQuot(kv[1]));
                    });
            return buildFormDataFromMap(map);
        } catch (Exception e) {
            throw new RuntimeException("WARNING:: Bad form-urlencoded format");
        }
    }

    private void setArg(String key, String... values) {
        if (argsMap == null)
            argsMap = new HashMap<>();
        argsMap.put(key,
                new ArrayList<>(Arrays.stream(values).filter(v -> v != null && !v.isEmpty()).map(Jurl::unQuot).collect(Collectors.toList())));
    }

    private void addArg(String key, String... values) {
        if (argsMap == null)
            argsMap = new HashMap<>();
        var value = argsMap.getOrDefault("key", new ArrayList<>());
        value.addAll(Arrays.stream(values).filter(v -> v != null && !v.isEmpty()).map(Jurl::unQuot).collect(Collectors.toList()));
        argsMap.put(key, value);
        if (key.equals("headers")) {
            var list = value.stream().filter(e -> unQuot(e).startsWith("content-type")).collect(Collectors.toList());
            if (list.isEmpty())
                return;
            var kv = list.get(list.size() - 1).split(":");
            setArg(kv[0], kv[1]);
        }
    }

    private static String getHelp() {
        return """
                Help (flags explain):\s
                |-----------------------------|-----------------------------------------------|
                |--method or -M:              |  Specify the method.                          |
                |-----------------------------|-----------------------------------------------|
                |--headers or -H:             |  Add headers to the request.                  |
                |-----------------------------|-----------------------------------------------|
                |--redirect or -R:            |  Activate the follow redirect.                |
                |-----------------------------|-----------------------------------------------|
                |--json or -J:                |  Send a json data array.                      |
                |-----------------------------|-----------------------------------------------|
                |--data or -D:                |  Use to send the multipart form data.         |
                |-----------------------------|-----------------------------------------------|
                |--form-urlencoded or -f:     |  Use to send form urlencoded data             |
                |-----------------------------|-----------------------------------------------|
                |--version or -V:             |  Set version (HTTP_1_1 or HTTP_2)             |
                |-----------------------------|-----------------------------------------------|
                |--user-agent:                |  Set user agent                               |
                |-----------------------------|-----------------------------------------------|
                |--help:                      |  Show help                                    |
                |-----------------------------|-----------------------------------------------|
                |--timeout:                   |  Set request time out in millis               |
                |-----------------------------|-----------------------------------------------|
                |--file or -F:                |  Set file path for download or upload         |
                |-----------------------------|-----------------------------------------------|""";
    }

    private static String checkJsonFormat(String json) {
        var res = json.trim();
        json = res;
        json = json.substring(1, json.length() - 1).trim();
        var list = new ArrayList<String>();
        Arrays.stream(json.split(",")).
                map(String::trim).
                map(kv -> kv.split(":")).
                forEach(kv -> {for (var s : kv) list.add(s.trim());});
        if (!(res.startsWith("{") && res.endsWith("}") && list.stream().allMatch(s -> s.startsWith("\"") && s.endsWith("\""))))
            throw new RuntimeException("WARNING:: Bad Json Format");
        return res;
    }

    private static String buildFormDataFromMap(Map<String, String> data) {
        var builder = new StringBuilder();
        for (var entry : data.entrySet()) {
            if (builder.length() > 0)
                builder.append("&");
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return builder.toString();
    }

    public HttpResponse<String> getResponse() throws IOException, InterruptedException {
        if (response == null)
            return handleHttpRequest();
        return response;
    }

    private HttpResponse<InputStream> handleHttpRequest0() throws IOException, InterruptedException {
        if (response0 != null)
            return response0;
        response0 = client.send(createHttpRequest(), HttpResponse.BodyHandlers.ofInputStream());
        if (argsMap.containsKey("redirect") && response0.headers().map().containsKey("location")) {
            setArg("url", response0.headers().map().get("location").get(0));
            handleHttpRequest0();
        }
        if (argsMap.containsKey("file-format"))
            try {
                downloadFile();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        return response0;
    }

    public boolean isDownloadable() {
        return argsMap.containsKey("file-format");
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

    public static void main(String[] args) throws IOException, InterruptedException {
        var jurl = new Jurl(new Scanner(System.in).nextLine().split(" "));
        /*
         * https://www.httpbin.org/get
         * https://www.httpbin.org/image/jpeg --method get --file download.jpeg
         * https://www.httpbin.org/image/png --method get --file download.png
         * https://www.httpbin.org/get -H Key:Value,Key2:Value2
         *
         * https://www.httpbin.org/post --file ulib.iml
         * https://www.httpbin.org/post --method post
         * https://www.httpbin.org/post --json {"A":"B"}
         * https://www.httpbin.org/post --data amir=hossein&name=ali
         * https://www.httpbin.org/post --file this3.png
         * https://www.httpbin.org/post --method get --file t.obj
         * https://www.httpbin.org/post -M post -H Content-Type:application/x-www-form-urlencoded --json Name=Amir&Family=Dolatkhah
         * https://www.httpbin.org/post -M post -H "Content-Type:application/json" -D {"Name":"Amir","Family":"Dolatkhah"}
         *
         * https://www.httpbin.org/patch --method patch
         * https://www.httpbin.org/patch --method patch --json {"A":"B"}
         * https://www.httpbin.org/patch --method patch --file ulib.iml
         *
         * https://www.httpbin.org/put --method put --data amir=hossein&name=ali
         * https://www.httpbin.org/put --method put --json {"A":"B"}
         * https://www.httpbin.org/put --method put --file ulib.iml
         *
         * https://httpbin.org/delete -M delete -D Name=Arman&Family=Feizy
         * https://www.httpbin.org/delete --method delete
         *
         * http://dl.subdlrica.xyz/musicvideos/George%20Michael/George%20Michael%20-%20Careless%20Whisper%20(Official%20Video).mp4
         * https://www.un.org/Depts/los/convention_agreements/texts/unclos/unclos_e.pdf
         *
         * https://www.un.org/Depts/los/convention_agreements/texts/unclos/unclos_e.pdf --timeout 5
         *
         * google.com
         * htttp://google.com
         * http://gpp.commm
         * http:/gpp.commm
         * http//google.com
         */
        var response = jurl.handleHttpRequest0();
        System.out.println("-----Status Message-----");
        System.out.println("status:: " + response.statusCode() + " " + httpCodeMessages.getOrDefault(response.statusCode(), "Unknown"));
        System.out.println("-----Response Headers-----");
        System.out.println(headersAsString(response.headers().map()));
        System.out.println("-----Body of Response-----");
        System.out.println(jurl.isDownloadable() ? "File has been downloaded" : new String(response.body().readAllBytes(), StandardCharsets.UTF_8));
    }
}
