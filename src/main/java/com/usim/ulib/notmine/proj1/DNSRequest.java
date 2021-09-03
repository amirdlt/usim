package com.usim.ulib.notmine.proj1;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Random;

public class DNSRequest {
    private final String domain;
    private final QueryType type;

    private DNSRequest(String domain, QueryType type){
        this.domain = domain;
        this.type = type;
    }

    private byte[] getRequest(){
        int qNameLength = getDomainLength();
        ByteBuffer request = ByteBuffer.allocate(12 + 5 + qNameLength);
        request.put(createRequestHeader());
        request.put(createQuestionHeader(qNameLength));
        return request.array();
    }

    private byte[] createRequestHeader() {
        var header = ByteBuffer.allocate(12);
        var randomID = new byte[2];
        new Random().nextBytes(randomID);
        header.put(randomID);
        header.put((byte)0x01);
        header.put((byte)0x00);
        header.put((byte)0x00);
        header.put((byte)0x01);
        return header.array();
    }

    private int getDomainLength() {
        int byteLength = 0;
        var items = domain.split("\\.");
        for (var item : items)
            byteLength += item.length() + 1;
        return byteLength;
    }

    private byte[] createQuestionHeader(int qNameLength) {
        var res = ByteBuffer.allocate(qNameLength+5);
        String[] items = domain.split("\\.");
        for (var item : items) {
            res.put((byte) item.length());
            for (int j = 0; j < item.length(); j++)
                res.put((byte) ((int) item.charAt(j)));
        }
        res.put((byte) 0x00);
        res.put(BigInteger.valueOf(Integer.parseInt(("000" + switch (type) {
            case A -> '1';
            case NS -> '2';
            default -> 'F';
        }).toUpperCase(), 16)).toByteArray());
        res.put((byte) 0x00);
        res.put((byte) 0x0001);
        return res.array();
    }

    public static byte[] createRequest(String domain, QueryType type) {
        return new DNSRequest(domain, type).getRequest();
    }

    enum QueryType {
        A, NS, MX, CNAME, OTHER
    }
}
