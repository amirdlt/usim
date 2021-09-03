package com.usim.ulib.notmine.tmp14;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class Exam {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Exam");

        frame.setBounds(200, 200, 300, 500);
        frame.setResizable(false);

        frame.setLayout(new BorderLayout());

        final Image[] img = new Image[1];
        frame.add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
            add(new JButton("Image") {{
                addActionListener(e -> {
                    JFileChooser chooser = new JFileChooser();
                    chooser.showDialog(frame, "Choose");
                    try {
                        img[0] = ImageIO.read(chooser.getSelectedFile());
                        frame.add(new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                g.drawImage(img[0], 0, 0, null);
                            }
                        }, BorderLayout.CENTER);
                        frame.revalidate();
                        frame.repaint();
                    } catch (IOException ignore) {
                    }
                });
            }});
        }}, BorderLayout.SOUTH);
        frame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Graphics g2d = frame.getGraphics();
                g2d.setColor(Color.RED);
                g2d.fillOval(e.getX(), e.getY(), 5, 5);
            }
        });
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}
