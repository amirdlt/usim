package com.usim.ulib.notmine.zhd;

import java.io.Serializable;
import java.util.HashSet;

public class User implements Serializable {
    private String userName;
    private String password;
    private HashSet<Store> administrators;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        administrators=new HashSet<>();
    }
}
