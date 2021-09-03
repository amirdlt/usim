package com.usim.ulib.utils.inputmanagers;

import java.awt.event.KeyEvent;

public class UserInput {
    public final MouseManager mouseManager;
    public final KeyManager keyManager;

    public UserInput() {
        mouseManager = new MouseManager();
        keyManager = new KeyManager() {
            @Override
            public void keyTyped(KeyEvent e) {

            }
        };
    }
}
