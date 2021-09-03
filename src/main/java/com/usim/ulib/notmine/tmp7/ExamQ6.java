package com.usim.ulib.notmine.tmp7;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ExamQ6 {
    public static void main(String[] args) throws IOException {
        var f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(100, 100, 200, 350);
        f.setLayout(new GridBagLayout());
        f.setResizable(false);
        var c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.VERTICAL;
        var red = new JLabel() {{
            setOpaque(true);
            setBackground(Color.RED);
            setMinimumSize(new Dimension(100, 100));
            setMaximumSize(new Dimension(100, 100));
            setPreferredSize(new Dimension(100, 100));
        }};
        f.add(red, c);

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.VERTICAL;
        var yellow = new JLabel() {{
            setOpaque(true);
            setBackground(Color.YELLOW);
            setMinimumSize(new Dimension(100, 100));
            setMaximumSize(new Dimension(100, 100));
            setPreferredSize(new Dimension(100, 100));
        }};
        f.add(yellow, c);

        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.VERTICAL;
        var green = new JLabel() {{
            setOpaque(true);
            setBackground(Color.GREEN);
            setMinimumSize(new Dimension(100, 100));
            setMaximumSize(new Dimension(100, 100));
            setPreferredSize(new Dimension(100, 100));
        }};
        f.add(green, c);

        new Thread(() -> {
            while (true)
                try {
                    green.setBackground(Color.GREEN);
                    yellow.setBackground(Color.GRAY);
                    red.setBackground(Color.GRAY);
                    f.repaint();
                    f.revalidate();
                    Thread.sleep(10_000);
                    green.setBackground(Color.GRAY);
                    red.setBackground(Color.RED);
                    f.repaint();
                    f.revalidate();
                    Thread.sleep(5_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }).start();

        SwingUtilities.invokeLater(() -> f.setVisible(true));
    }
}
