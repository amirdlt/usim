package com.usim.ulib.notmine.proj5;

public class Main {
    public static void main(String[] args) throws Exception {
        DnsClient client = new DnsClient(new String[] {"@8.8.8.8", "google.com", "-A"});
        client.makeRequest();
    }
}


