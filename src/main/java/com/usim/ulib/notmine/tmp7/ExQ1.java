package com.usim.ulib.notmine.tmp7;

import javax.swing.*;
import java.awt.*;

public class ExQ1 {
    public static void main(String[] args) {
        var f = new JFrame("Exam");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(100, 100, 400, 350);
        f.setLayout(new GridBagLayout());
        f.setResizable(false);

        f.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        var c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        f.add(new JLabel("Temp") {{
            setBackground(Color.YELLOW);
            setOpaque(true);
            setPreferredSize(new Dimension(200, 125));
            setMinimumSize(new Dimension(200, 125));
            setMaximumSize(new Dimension(200, 125));
        }}, c);

        for (int i = 0; i < 3; i++) {
            c.fill = GridBagConstraints.HORIZONTAL | GridBagConstraints.VERTICAL;
            c.gridx = 1;
            c.gridy = i;
            f.add(new JLabel("Temp") {{
                setBackground(new Color((int) (255 * Math.random()), (int) (255 * Math.random()), (int) (255 * Math.random())));
                setOpaque(true);
                setPreferredSize(new Dimension(200, 40));
                setMinimumSize(new Dimension(200, 40));
                setMaximumSize(new Dimension(200, 40));
            }}, c);
        }

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 4;
        f.add(new JLabel("Temp") {{setBackground(Color.CYAN); setOpaque(true);}}, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        f.add(new JLabel("Temp") {{setBackground(Color.CYAN); setOpaque(true);
            setPreferredSize(new Dimension(200, 40));
            setMinimumSize(new Dimension(200, 40));
            setMaximumSize(new Dimension(200, 40));
        }}, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 5;
        f.add(new JLabel("Temp") {{setBackground(Color.GREEN); setOpaque(true);
            setPreferredSize(new Dimension(200, 40));
            setMinimumSize(new Dimension(200, 40));
            setMaximumSize(new Dimension(200, 40));
        }}, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 6;
        f.add(new JLabel("Temp") {{setBackground(Color.GREEN.darker().darker()); setOpaque(true);
            setPreferredSize(new Dimension(200, 40));
            setMinimumSize(new Dimension(200, 40));
            setMaximumSize(new Dimension(200, 40));
        }}, c);


        //////////
        SwingUtilities.invokeLater(() -> f.setVisible(true));
    }
}
