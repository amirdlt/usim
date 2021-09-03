package com.usim.ulib.net;

import java.io.*;
import java.lang.reflect.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

//@SuppressWarnings("ALL")
public class InsomniaJURL implements Serializable {

    private ArrayList<String> options;
    private long startTime;
    private int responseCode;
    private byte[] rawResponse;
    private HttpURLConnection connection;
    private ArrayList<URL> locations;
    private HashMap<String, String> requestHeaders;
    private HashMap<String, String> responseHeaders;
    private boolean isFollowRedirect;
    private boolean isResponseHeadersVisible;
    private HashMap<String, String> formData;
    private HashMap<String, String> formDataUrlEncoded;
    private HashMap<String, String> jsonData;
    private MemoryIO majorOutput;
    private String urlAddress;
    private String redirectUrlAddress;
    private String method;
    private String formDataUrlEncode;
    private InputOutput output;
    private HashMap<String, Object> allData;
    private URL url;
    private final static int RETRY_MAX = 20;
    private int numOfRetryLeft;
    private double connectionTimeoutLimit;
    private boolean isThereProblem;
    private boolean isSaveNeeded;
    private ArrayList<Integer> loadList;
    private boolean showCookies;
    private long exeTime;
    private double resultSize;
    private String absPathOfFileToUpload;
    private ArrayList<HashMap<String, Object>> savedData;

    static {
        System.setProperty("sun.com.usim.ulib.net.http.allowRestrictedHeaders", "true");
    }

    public InsomniaJURL() {
        resetFields();
        requestHeaders.put("User-Agent", "Insomnia - 9726028");
    }

    public final void resetFields() {
        startTime = System.currentTimeMillis();
        responseCode = 0;
        connectionTimeoutLimit = 0;
        requestHeaders = new HashMap<>();
        responseHeaders = new HashMap<>();
        locations = new ArrayList<>();
        rawResponse = null;
        isFollowRedirect = false;
        isResponseHeadersVisible = false;
        formData = new HashMap<>();
        majorOutput = new MemoryIO();
        urlAddress = null;
        redirectUrlAddress = null;
        method = null;
        output = majorOutput;
        options = new ArrayList<>();
        allData = new HashMap<>();
        isThereProblem = false;
        numOfRetryLeft = RETRY_MAX;
        connectionTimeoutLimit = 100;
        isSaveNeeded = false;
        showCookies = false;
        absPathOfFileToUpload = null;
        formDataUrlEncoded = new HashMap<>();
        formDataUrlEncode = null;
        loadList = new ArrayList<>();
        jsonData = new HashMap<>();
        savedData = new ArrayList<>();
    }

    public static String[] toArray(ArrayList<String> optionList) {
        Object[] options = optionList.toArray();
        String[] result = new String[options.length];
        int i = 0;
        for (Object opt : options)
            result[i++] = (String) opt;
        return result;
    }

    public final void handleOptions() throws Exception {
        String dummy;
        for (int i = 0, lenOfOptions = options.size(); i < lenOfOptions; i++) {
            String option = options.get(i);
            if (option.startsWith("http://") || option.startsWith("https://")) {
                urlAddress = option;
                continue;
            }

            switch (option) {
                case "list":
                    urlAddress = "http://dummy.com";
                    showList();
                    break;
                case "fire":
                    urlAddress = "http://dummy.com";
                    while (i < lenOfOptions-1)
                        loadList.add(Integer.parseInt(options.get(++i)));
                case "--help": case "-h":
                    showHelp();
                    break;
                case "--cookies":
                    showCookies = true;
                    break;
                case "--data": case "-d":
                    for (String[] keyValue : split(dummy = options.get(++i), "&", "="))
                        formData.put(keyValue[0], keyValue[1]);
                    allData.put("multi-part-string", dummy);
                    break;
                case "--method": case "-M":
                    method = i<lenOfOptions-1 ? options.get(++i).toUpperCase() : "GET";
                    if (method.startsWith("-")) {
                        i--;
                        method = "GET";
                    }
                    if (!method.equalsIgnoreCase("GET") &&
                            !method.equalsIgnoreCase("PATCH") &&
                            !method.equalsIgnoreCase("DELETE") &&
                            !method.equalsIgnoreCase("POST") &&
                            !method.equalsIgnoreCase("PUT") &&
                            !method.equalsIgnoreCase("OPTIONS") &&
                            !method.equalsIgnoreCase("HEAD"))
                        throw new Exception("Not Acceptable Method");
                    break;
                case "--save": case "-S":
                    isSaveNeeded = true;
                    break;
                case "--form-urlencoded": case "-e": case "--form":
                    split(formDataUrlEncode = options.get(++i), "&", "=", formDataUrlEncoded);
                    break;
                case "-i":
                    isResponseHeadersVisible = true;
                    break;
                case "--output": case "-O": // --output  FILE   Write to FILE instead of stdout
                    String s;
                    String address = ".\\data\\saved files\\";
                    output = new FileIO(address + (i < lenOfOptions - 1 &&
                            !(s = options.get(i + 1)).startsWith("-") && !s.startsWith("\"")
                            ? s : "output_" + new Date().toString().replace(':', '-') + ".html"));
                    output.setAppend(false);
                    if (i<lenOfOptions-1 && !(s = options.get(i+1)).startsWith("-") && !s.startsWith("\""))
                        i++;
                    break;
                case "-f": case "--follow-redirect": // Enable follow redirect
                    isFollowRedirect = true;
                    break;
                case "--json": case "-j":
                    try {
                        split(options.get(++i), ",", ":", jsonData);
                    } catch (IndexOutOfBoundsException e) {
                    }
                    break;
                case "--upload":
                    absPathOfFileToUpload = options.get(++i);
                    break;
                case "--headers": case "-H":
                    split(dummy = options.get(++i), ";", ":", requestHeaders);
                    allData.put("headers-string", dummy);
                    break;
                case "--user-agent":
                    requestHeaders.put("User-Agent", options.get(++i));
                    break;
                case "--connect-timeout":
                    try {
                        connectionTimeoutLimit = Double.parseDouble(options.get(++i));
                    } catch (Exception e) {
                        connectionTimeoutLimit = 0;
                    }
                    break;
                default:
                    if (option.startsWith("-")) {
                        throw new Exception("Illegal Option: " + option + " Is Sent To The JURL");
                    }
            }
        }

        if (urlAddress == null) {
            boolean isURLFound = false;
            for (String option : options) {
                if (!option.startsWith("-") && !option.startsWith("\"") && option.contains(".")) {
                    urlAddress = "http://" + option;
                    isURLFound = true;
                    break;
                }
            }
            if (!isURLFound)
                throw new Exception("No URL Address Specified.");
        }
        urlAddress =  urlAddress.replace(" ", "");

        URL url = new URL(urlAddress);
        if (url.getQuery() != null)

        if (!formData.isEmpty() || absPathOfFileToUpload != null)
            method = method.equals("PATCH") || method.equals("PUT") ? method :"POST";

        if (method == null)
            method = "GET";

        if (isSaveNeeded)
            saveRequest();

        if (!loadList.isEmpty()) {
            for (int n : loadList)
                run((String[]) savedData.get(--n).get("options-array"));
            System.exit(0);
        }


        numOfRetryLeft = RETRY_MAX;
    }

    private void showList() {
        int c = 0;
        for (HashMap<String, Object> options : savedData)
            System.out.println(++c + ". " + "URL: " + options.get("url-address") + " | " +
                    "method: " + options.get("method") + " | " + "request headers: " + options.get("headers-string") +
                    " | multipart/form data: " + options.get("multi-part-string") + " | form data/urlencoded: " +
                    formDataUrlEncode);
        System.exit(0);
    }

    private void showHelp() {
        System.out.println("Help: \n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"--save or -S:                ||  Save the request.                                                    ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"--method or -M:              ||  Specify the method.                                                  ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"--headers or -H:             ||  Add headers to the request.                                          ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"--upload:                    ||  Upload a binary file.                                                ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"--follow-redirect or -f:     ||  Activate the follow redirect.                                        ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"-i:                          ||  Only response headers will be shown.                                 ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"--json or -j:                ||  Send a json data array.                                              ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"--form-urlencoded or --form: ||  Use to send the url encoded data.                                    ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"--output or -O:              ||  Save the raw response to a specific file path.                       ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"--data or -d:                ||  Use to send the multipart form data.                                 ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"list:                        ||  This will show the saved requests properties.                        ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n"
            +"fire:                        ||  This will send selected numbers of saved requests as shown by list.  ||\n"
            +"-----------------------------||-----------------------------------------------------------------------||\n");
        System.exit(0);
    }

    private void saveRequest() {
        savedData.add(getData());
        isSaveNeeded = false;
    }

    public final void handleConnectionSettingDueToMethod() throws ProtocolException {
        if (method.equals("PATCH")) {
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestMethod("POST");
        } else {
            connection.setRequestMethod(method);
        }

        if ((absPathOfFileToUpload != null || !formData.isEmpty()
                || !formDataUrlEncoded.isEmpty() || !jsonData.isEmpty())
                && !method.equals("PATCH") && !method.equals("POST") && !method.equals("PUT"))
                    method = "POST";

        if (absPathOfFileToUpload != null)
            uploadBinary();
        if (!formData.isEmpty())
            formData();
        if (!formDataUrlEncoded.isEmpty())
            formDataUrlEncoded();
        if (!jsonData.isEmpty())
            jsonData();

    }

    private void jsonData() {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, String> kv : jsonData.entrySet())
            sb.append("\"").append(kv.getKey()).append("\"").append(":").append("\"").append(kv.getValue()).append("\",");
        byte[] postData = (sb.substring(0, sb.toString().length()-1) + "}").getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        try {
            connection.setRequestMethod(!method.equals("PATCH") ? method : "POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            allData.put("request-properties", connection.getRequestProperties());
            connection.setDoOutput(true);
            connection.setUseCaches(false);
        } catch (Exception e) {
        }
        try(DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.write(postData);
        } catch (Exception e) {
        }
    }

    public static void bufferOutFormData(HashMap<String, String> body, String boundary,
                                         BufferedOutputStream bufferedOutputStream) throws IOException {
        for (String key : body.keySet()) {
            bufferedOutputStream.write(("--" + boundary + "\r\n").getBytes());
            if (key.contains("file")) {
                bufferedOutputStream.write(("Content-Disposition: form-data; filename=\"" + (new File(body.get(key))).getName() + "\"\r\nContent-Type: Auto\r\n\r\n").getBytes());
                try {
                    BufferedInputStream tempBufferedInputStream = new BufferedInputStream(new FileInputStream(new File(body.get(key))));
                    byte[] filesBytes = tempBufferedInputStream.readAllBytes();
                    bufferedOutputStream.write(filesBytes);
                    bufferedOutputStream.write("\r\n".getBytes());
                } catch (IOException ignored) {
                }
            } else {
                bufferedOutputStream.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
                bufferedOutputStream.write((body.get(key) + "\r\n").getBytes());
            }
        }
        bufferedOutputStream.write(("--" + boundary + "--\r\n").getBytes());
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    public void formDataUrlEncoded() {
        byte[] postData = formDataUrlEncode.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        try {
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(!method.equals("PATCH") ? method : "POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            allData.put("request-properties", connection.getRequestProperties());
            connection.setUseCaches(false);
            connection.setDoOutput(true);
        } catch (Exception e) {
        }
        try(DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.write( postData );
        } catch (Exception e) {
        }
    }

    public void formData() {
        try {
            String boundary = System.currentTimeMillis() + "";
            connection.setRequestMethod(!method.equals("PATCH") ? method : "POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            allData.put("request-properties", connection.getRequestProperties());
            BufferedOutputStream request = new BufferedOutputStream(connection.getOutputStream());
            bufferOutFormData(formData, boundary, request);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            writeOutput(bufferedInputStream.readAllBytes(), true);
        } catch (Exception e) {
        }
    }

    public String execute() throws Exception {
        handleOptions();
        boolean numOfRetryExceeded;
        boolean timeoutExceeded = false;

        do {
            if (redirectUrlAddress != null) {
                url = new URL(url, redirectUrlAddress);
            } else {
                url = new URL(urlAddress);
            }

            if (numOfRetryLeft == RETRY_MAX) {
                if (locations.size() > 51) {
                    redirectUrlAddress = null;
                    throw new Exception("There is too many redirect addresses.");
                }

                locations.add(url);
            }

            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(isFollowRedirect);
            connection.setConnectTimeout((int) (connectionTimeoutLimit * 1000d));

            for (Map.Entry<String, String> keyValue : requestHeaders.entrySet())
                connection.setRequestProperty(keyValue.getKey(), keyValue.getValue());


            handleConnectionSettingDueToMethod();

            redirectUrlAddress = null;
            try {
                responseCode = connection.getResponseCode();
            } catch (Exception e) {
            }

            if (responseCode / 100 == 4) {
                System.out.flush();
                fillResponseHeaders(responseHeaders);
            } else if (responseCode / 100 == 3)
                redirectUrlAddress = connection.getHeaderField("Location");

            if (redirectUrlAddress != null)
                numOfRetryLeft = RETRY_MAX;

            InputStream inputStream = null;
            try {
                inputStream = connection.getInputStream();
            } catch (Exception ignore) {
            }

            byte[] a;
            if (inputStream != null) {
                writeOutput((a = readStream(inputStream, true)) == null ?
                        ("There is a problem with the method or website\n" +
                                "No body returned as response.").getBytes() : a, true);
            } else {
                writeOutput(("There is a problem with the method or website\n" +
                        "No body returned as response.").getBytes(), true);
            }

            fillResponseHeaders(responseHeaders);

            if (redirectUrlAddress == null || !isFollowRedirect) {
                rawResponse = majorOutput.toByteArray();
                exeTime = System.currentTimeMillis() - startTime;
                resultSize = rawResponse.length == 0 ? output.getInputStream().readAllBytes().length * 1d / 1024 :
                        rawResponse.length * 1d / 1024;
                return new String(rawResponse, StandardCharsets.UTF_8);
            }

        } while ((numOfRetryExceeded = --numOfRetryLeft >= 0) &&
                (timeoutExceeded = (exeTime = System.currentTimeMillis() - startTime) < connectionTimeoutLimit) ||
                 responseCode / 100 == 3 && isFollowRedirect);

        throw new Exception((numOfRetryExceeded ? "number of retry Exceeded " : "") + (timeoutExceeded ? "timeout exceeded " : "") +
                (!numOfRetryExceeded && !timeoutExceeded ? "There is a problem in connection" : ""));
    }

    public InsomniaJURL options(String... options) {
        for (String option : options)
            this.options.add(unquote(option));
        return this;
    }

    public static HashMap<String, Object> run(String... args) {

        InsomniaJURL jurl = new InsomniaJURL();
        try {
            String rawData = jurl.options(args).execute();
            if (jurl.isThereProblem)
                return null;
            if (!jurl.isResponseHeadersVisible)
                System.out.println(rawData);
            jurl.printResponseHeaders(false);
            jurl.printExeTimeResSize();
        } catch (Exception ignored) {
        }

        return jurl.getData();
    }

    public void printResponseHeaders(boolean isForced) {
        if (this.isResponseHeadersVisible || isForced) {
            System.out.println("\nThese are the response headers:");
            for (Map.Entry<String, String> keyValue : this.responseHeaders.entrySet())
                System.out.println(keyValue.getKey() + " = " + keyValue.getValue());
        }
    }

    public void printExeTimeResSize() {
        System.out.println("\nThe time spent for executing: " +
                exeTime / 1000 + " seconds and " +  exeTime % 1000 + " milliseconds");
        System.out.println("Data Size: " + String.format("%.2f KB", resultSize));
    }

    public HashMap<String, Object> getData() {
        allData.put("options-array", toArray(options));
        allData.put("raw-response", rawResponse);
        allData.put("request-headers", requestHeaders);
        allData.put("response-headers", responseHeaders);
        allData.put("multipart-form", formData);
        allData.put("form-url-encoded", formDataUrlEncoded);
        allData.put("response-code", responseCode);
        allData.put("response-message", responseHeaders.get("Response Message"));
        allData.put("executing-time", exeTime);
        allData.put("data-size", resultSize);
        allData.put("start-time", startTime);
        allData.put("url-address", urlAddress);
        allData.put("method-real", isSaveNeeded ? null : connection.getRequestMethod());
        allData.put("method", method);
        allData.put("json-data", jsonData);
        allData.put("header-fields", isSaveNeeded ? null : connection.getHeaderFields());
        allData.put("upload-file-path", absPathOfFileToUpload);
        allData.put("query", isSaveNeeded ? null : url.getQuery());
        //////////
        HashMap<String, String> kv = new HashMap<>();
        String temp;
        String[] nameValues = ((temp = responseHeaders.get("Set-Cookie")) != null ? temp : "").split(";");
        for (String nameValue : nameValues) {
            String name;
            String value;
            try {
                name = nameValue.substring(0, nameValue.indexOf("="));
                value = nameValue.substring(nameValue.indexOf("=") + 1);
            } catch (Exception e) {
                continue;
            }
            kv.put(name, value);
            if (showCookies)
                System.out.println("cookie name: " + name + " cookie value: " + value);
        }
        //////////
        allData.put("cookies", kv);
        /////////
        var queryData = new HashMap<String, String>();
        if (!isSaveNeeded)
            split(url.getQuery() != null ? url.getQuery() : "" , "&", "=", queryData);
        ////////
        allData.put("query-data", queryData);
        return allData;
    }

    public void uploadBinary() {
        try {
            File fileToUpload = new File(absPathOfFileToUpload);
            connection.setRequestMethod(!method.equals("PATCH") ? method : "POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            allData.put("request-properties", connection.getRequestProperties());
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(connection.getOutputStream());
            BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(fileToUpload));
            bufferedOutputStream.write(fileInputStream.readAllBytes());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            writeOutput(bufferedInputStream.readAllBytes(), true);
            System.out.println(new String(bufferedInputStream.readAllBytes()));
            System.out.println(connection.getResponseCode());
            System.out.println(connection.getHeaderFields());
        } catch (IOException ignored) {
        }
    }

    public void writeOutput(byte[] bb, boolean append) {
        output.setAppend(append);
        if (output instanceof FileIO) {
            try {
                new FileWriter(((FileIO) output).file).write("");
            } catch (Exception ignore) {
            }
        }
        try (OutputStream outputStream = output.getOutputStream()) {
            outputStream.write(bb);
            outputStream.flush();
        } catch (Exception e) {
        }
    }

    public void fillResponseHeaders(HashMap<String, String> responseHeaders) {
        responseHeaders.clear();
        Object responses = getField(connection, "responses");
        String[] keys;
        String[] values;
        Integer nkeys;
        if (responses != null && (nkeys = (Integer) getField(responses, "nkeys")) != null
                && (keys = (String[]) getField(responses, "keys")) != null
                && (values = (String[]) getField(responses, "values")) != null) {
            for (int i = 0; i < nkeys; i++)
                responseHeaders.put(keys[i], values[i]);
        } else {
            try {
                responseHeaders.put("Response Message", connection.getResponseMessage());
            } catch (Exception ignored) {
            }

            responseHeaders.put("Protocol", connection.getHeaderField(0));
            for (int i = 1; ; i++) {
                String k = connection.getHeaderFieldKey(i);
                String v = connection.getHeaderField(i);
                if (k == null && v == null) break;
                responseHeaders.put(k, v);
            }
        }
    }

    private static class MemberInfo {
        String signature; // null for field
        int numArgs; // -1 for field
        Member member;

        public MemberInfo(String sign, int num, Member member) {
            signature = sign;
            numArgs = num;
            this.member = member;
        }

        public final String toString() {
            return member.toString();
        }
    }

    public interface InputOutput {
        InputStream getInputStream();
        OutputStream getOutputStream();
        void setAppend(boolean append);
    }

    public static class MemoryIO extends ByteArrayOutputStream implements InputOutput {
        private ByteArrayInputStream inputStream;

        public MemoryIO() {
            super(0);
        }

        public InputStream getInputStream() {
            return inputStream = new ByteArrayInputStream(buf, 0, count);
        }

        public OutputStream getOutputStream() {
            return this;
        }

        public void setAppend(boolean append) {
            if (!append) this.reset();
        }

        public void close() {
            try {
                inputStream.close();
            } catch (Exception ignore) {
            }
        }

        public String toString() {
            return "MemoryIO<" + this.hashCode() + ">";
        }
    }

    public static class FileIO implements InputOutput {
        private final File file;
        boolean append;

        public FileIO(File file) {
            append = false;
            this.file = file.getAbsoluteFile();
        }

        public FileIO(String pathOfTheFile) {
            this(new File(pathOfTheFile));
        }

        @Override
        public InputStream getInputStream() {
            if (file.exists() && file.canRead()) {
                try {
                    InputStream inputStream;
                    return inputStream = new FileInputStream(file);
                } catch (Exception ignore) {
                }
            }
            return null;
        }

        @Override
        public OutputStream getOutputStream() {
            try {
                return new FileOutputStream(file, append);
            } catch (Exception ignored) {
            }

            return null;
        }

        @Override
        public void setAppend(boolean append) {
            this.append = append;
            if (append) {
                try (FileWriter fileWriter = new FileWriter(file)) {
                    fileWriter.write("");
                } catch (Exception ignored) {
                }
            }
        }

        @Override
        public String toString() {
            return "FileIO<" + file + ">";
        }

        public File getFile() {
            return file;
        }
    }

    public static String unquote(String s) {
        return s.startsWith("'") && s.endsWith("'")
                || s.startsWith("\"") && s.endsWith("\"")
                || s.startsWith("[") && s.endsWith("]")
                || s.startsWith("{") && s.endsWith("}") ?
                s.substring(1, s.length() - 1) : s;
    }

    private static final Map<Class<?>, Map<String, ArrayList<MemberInfo>>> mapClassMembers = new HashMap<>();
    public static final int PIPE_COUNT_MASK = 0x7FFFFFFF;
    private static final int BUFFER_SIZE = 100000;

    public void mapListAdd(Map<String, ArrayList<MemberInfo>> map, String key, MemberInfo val) {
        ArrayList l;
        if ((l = map.get(key)) == null)
            try {
                map.put(key, l = ArrayList.class.getDeclaredConstructor().newInstance());
            } catch (Exception ignored) {
            }

        assert l != null;
        Collections.addAll(l, val);
    }

    public <T> T[] safeArray(T[] array, Class<T> componentType) {
        return array != null ? array : (T[]) Array.newInstance(componentType, 0);
    }

    public String[][] split(String s, String regex1, String regex2) {
        String[] ss = s.split(regex1);
        String[][] result = new String[ss.length][];
        for (int i = ss.length; --i >= 0; result[i] = ss[i].split(regex2));
        return result;
    }

    public void split(String s, String entryRegex, String kvRegex, Map<String, String> toMap) {
        String[] ss = s.split(entryRegex);
        if (toMap == null)
            toMap = new HashMap<>(ss.length);
        for (String l : ss) {
            String[] sub = l.split(kvRegex);
            toMap.put(sub[0].trim(), sub.length > 1 ? sub[1].trim() : "");
        }
    }

    public byte[] readStream(InputStream is, boolean close) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int count = 0;
        int c;
        while ((c = pipeStream(is, bos)) > 0)
            count += c;
        if (c < 0)
            count += (c & PIPE_COUNT_MASK);
        if (close)
            try {
            is.close();
            } catch (Exception ignored) {
            }
        return c < 0 && count == 0 ? null : bos.toByteArray();
    }

    public int pipeStream(InputStream source, OutputStream destination) {
        byte[] bb = new byte[BUFFER_SIZE];
        int len;
        int count = 0;

        do {
            try {
                len = source.read(bb);
            } catch (Exception e) {
                len = -1;
            }
            if (len > 0) {
                try {
                    destination.write(bb, 0, len);
                } catch (Exception ignored) {
                }
                count += len;
            }
        } while (len == BUFFER_SIZE);

        return count;
    }

    public Object getField(Object object, String fieldName) {
        if (object == null || fieldName == null)
            throw new NullPointerException("inst=" + object + ",field=" + fieldName);
        try {
            for (MemberInfo mi : Objects.requireNonNull(getMembers(fieldName))) {
                if (-1 == mi.numArgs) {
                    ((AccessibleObject) mi.member).setAccessible(true);
                    return ((Field) mi.member).get(object);
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private synchronized List<MemberInfo> getMembers(String name) {
        if (!mapClassMembers.containsKey(connection.getClass())) {
            Map<String, ArrayList<MemberInfo>> map;
            mapClassMembers.put(connection.getClass(), map = new LinkedHashMap<>());
            Class<?> clss = connection.getClass();
            while (clss != null && !Object.class.equals(clss)) {
                for (Constructor<?> c : safeArray(clss.getDeclaredConstructors(), Constructor.class)) {
                    Class<?>[] ptypes = c.getParameterTypes();
                    mapListAdd(map, "<init>", new MemberInfo("", ptypes.length, c));
                }
                for (Method m : safeArray(clss.getDeclaredMethods(), Method.class)) {
                    Class<?>[] ptypes = m.getParameterTypes();
                    mapListAdd(map, m.getName(), new MemberInfo("", ptypes.length, m));
                }
                for (Field f : safeArray(clss.getDeclaredFields(), Field.class)) {
                    mapListAdd(map, f.getName(), new MemberInfo(null, -1, f));
                }
                clss = clss.getSuperclass();
            }
        }
        return mapClassMembers.get(connection.getClass()) != null && mapClassMembers.get(connection.getClass()).get(name) != null ?
                mapClassMembers.get(connection.getClass()).get(name) : null;
    }

    public static void main(String[] args) {
        run("https://httpbin.org/post --json {a:b,c:d}".split(" "));
    }
}
