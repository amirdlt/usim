package com.usim.ulib.notmine.zhd2;

import java.util.*;

public class ExamQ2 {
    public static void main(String[] args) {
        Map<String, Long> requestMap = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        int num = Integer.parseInt(scanner.nextLine().trim());
        List<String> res = new ArrayList<>();
        VotingSystem vs = new VotingSystem();
        while (num-- > 0) {
            String[] info = scanner.nextLine().split("\\s");
            Long pre = requestMap.get(info[1]);
            long now = Long.parseLong(info[0]);
            requestMap.put(info[1], now);
            if (pre != null && now - pre < 10 || now > VotingSystem.duration) {
                res.add("403");
                continue;
            }
            String query = info[2];
            String id = query.substring(query.indexOf('=') + 1, query.indexOf('&'));
            if (query.startsWith("vote")) {
                res.add(vs.vote(id,
                        Integer.parseInt(query.substring(query.lastIndexOf('=') + 1))) ? "200" : "403");
            } else {
                String[] qq = query.substring(query.indexOf('&') + 1).split("&");
                int q = vs
                        .tally(id, qq[0].substring(qq[0].indexOf('=') + 1), Integer.parseInt(qq[1].substring(qq[1].indexOf('=') + 1)));
                res.add(q < 0 ? "403" : "200 " + q);
            }
        }
        res.forEach(System.out::println);
    }
}

class VotingSystem {
    public static final long duration = 30 * 60 * 1000;
    private static final Map<String, String> specialAccounts = new HashMap<>() {{put("4984760", "#abc123M"); put("7589175", "46812sQ$");}};

    private final Map<String, List<Integer>> votes;

    public VotingSystem() {
        votes = new HashMap<>();
    }

    public boolean vote(String id, int candidateKey) {
        if (candidateKey < 1 || candidateKey > 10)
            return false;
        List<Integer> v = votes.getOrDefault(id, new ArrayList<>());
        if (v.size() > 4 || v.stream().filter(i -> i == candidateKey).count() >= 1)
            return false;
        v.add(candidateKey);
        votes.put(id, v);
        return true;
    }

    public int tally(String id, String password, int candidateKey) {
        if (candidateKey < 1 || candidateKey > 10)
            return -1;
        String pass = specialAccounts.get(id);
        if (pass == null || !pass.equals(password))
            return -1;
        int res = 0;
        for (List<Integer> v : votes.values())
            res += v.stream().filter(i -> i == candidateKey).count();
        return res;
    }
}
