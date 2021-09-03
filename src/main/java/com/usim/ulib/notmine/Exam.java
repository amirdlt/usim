package com.usim.ulib.notmine;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Exam {
    public static void main0(String[] args) {
//        Payload.Switch mySwitch = new Payload.Switch();
//        mySwitch.run();
//        new BigInteger().modPow()
        System.out.println(new BigInteger("1001").modPow(new BigInteger("9"), new BigInteger("13")));
        System.out.println(new BigInteger("1001").modPow(new BigInteger("11"), new BigInteger("17")));
    }

    private static double f(int x, double r, int i){
        if(i>=4) return r;
        return f(x, (r+x/r)/2, ++i);
    }
    public static void main(String[] args) {
        System.out.println(f(2, 1, 1)-Math.sqrt(2));
    }
}

class Payload {

    private final byte length;
    private final String context;

    public Payload(byte length, char[] payloadBytes) {
        this.length = length;
        this.context = new String(payloadBytes);
    }

    public String getContext() {
        return context;
    }

    public byte getLength() {
        return length;
    }

    static class Header {

        private byte srcAddress;
        private byte dstAddress;
        private byte payloadLength;
        private byte key;

        public Header(byte srcAddress, byte dstAddress, byte payloadLength, byte key) {
            this.srcAddress = srcAddress;
            this.dstAddress = dstAddress;
            this.payloadLength = payloadLength;
            this.key = key;
        }

        public byte getPayloadLength() {
            return payloadLength;
        }

        public byte getSrcAddress() {
            return srcAddress;
        }

        public byte getKey() {
            return key;
        }

        public byte getDstAddress() {
            return dstAddress;
        }

    }

    static class Packet {

        public final static List<String> filteredWords;

        static {
            filteredWords = Arrays.asList("yellow", "karma", "hooman", "tehran", "gun", "sadness", "pride", "language",
                    "laptop", "stalker");
        }


        private final Header header;
        private final Payload payload;

        public Packet(Header header, Payload payload) {
            this.header = header;
            this.payload = payload;
        }

        public short forward() {
            String tmp = payload.context.toLowerCase();
            for (String s : filteredWords)
                if (tmp.contains(s))
                    return 0;

            switch (header.dstAddress) {
                case 110: case 120: return 1;
                case 60: case 50: return 2;
                case 90: case 100: return 3;
                case 80: case 70: return 4;
                default: return 0;
            }
        }

        public Header getHeader() {
            return header;
        }

        public Payload getPayload() {
            return payload;
        }
    }

    static class Switch {

        private Parser parser;
        private Deparser deparser;
        private Scanner inputScanner;
        private Packet packet;
        private byte[] outputBuffer;

        public Switch() {
            this.inputScanner = new Scanner(System.in);
            this.parser = new Parser(inputScanner);
            this.deparser = new Deparser();
        }

        public void run() {
            while (inputScanner.hasNext()) {
                packet = parser.parse();
                short outputPort = packet.forward();
                outputBuffer = deparser.deparse(packet);
                showOutputPacket(outputBuffer, outputPort);
            }
        }

        private static String toBitString(final byte[] b) {
            final char[] bits = new char[8 * b.length];
            for (int i = 0; i < b.length; i++) {
                final byte byteval = b[i];
                int bytei = i << 3;
                int mask = 0x1;
                for (int j = 7; j >= 0; j--) {
                    final int bitval = byteval & mask;
                    if (bitval == 0) {
                        bits[bytei + j] = '0';
                    } else {
                        bits[bytei + j] = '1';
                    }
                    mask <<= 1;
                }
            }
            return String.valueOf(bits);
        }

        private void showOutputPacket(byte[] outputBuffer, short outputPort) {
            System.out.println(outputPort + ": " + toBitString(outputBuffer));
        }

    }

    static class Deparser {

        public byte[] deparse(Packet packet) {
            byte[] headerBytes = deparseHeader(packet.getHeader());
            byte[] payloadBytes = deparsePayload(packet.getPayload());
            byte[] output = new byte[headerBytes.length + payloadBytes.length];
            System.arraycopy(headerBytes, 0, output, 0, headerBytes.length);
            System.arraycopy(payloadBytes, 0, output, headerBytes.length, payloadBytes.length);
            return output;
        }

        public byte[] deparseHeader(Header header) {
            return new byte[] { header.srcAddress, header.dstAddress, header.payloadLength, header.key };
        }

        public byte[] deparsePayload(Payload payload) {
            byte[] res = new byte[payload.length];
            String context = payload.context;
            for (byte i = 0; i < payload.length; i++)
                res[i] = (byte) context.charAt(i);
            return res;
        }
    }

    static class Parser {

        static final int HEADER_LENGTH = 4;
        private Scanner inputScanner;
        private byte[] headerBuffer;

        public Parser(Scanner scanner) {
            inputScanner = scanner;
            headerBuffer = new byte[HEADER_LENGTH];
        }

        public Packet parse() {
            Header header = parseHeader(this.inputScanner);
            Payload payload = parsePayload(this.inputScanner, header.getPayloadLength(), header.key);
            header.key = 0;
            return new Packet(header, payload);
        }

        public Header parseHeader(Scanner inputScanner) {
            return new Header(Byte.parseByte(inputScanner.nextLine().trim(), 2),
                    Byte.parseByte(inputScanner.nextLine().trim(), 2),
                    Byte.parseByte(inputScanner.nextLine().trim(), 2),
                    Byte.parseByte(inputScanner.nextLine().trim(), 2));
        }

        public Payload parsePayload(Scanner inputScanner, byte payloadLength, byte k) {
            char[] chars = new char[payloadLength];
            for (byte i = 0; i < payloadLength; i++)
                chars[i] = (char) (Byte.parseByte(inputScanner.nextLine().trim(), 2) + k);
            return new Payload(payloadLength, chars);
        }
    }
}
