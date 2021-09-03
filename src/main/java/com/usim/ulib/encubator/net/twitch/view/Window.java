package com.usim.ulib.encubator.net.twitch.view;

import com.usim.ulib.swingutils.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Window extends MainFrame {

    private JPanel mainPanel;
    private JTable notSelectedFiles;
    private JTable selectedFiles;

    public Window() {
        super("Twitch Automatic Clips Downloader");
        init();
    }

    private void init() {
        mainPanel = new JPanel(new BorderLayout()) {{
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            add(
                new JPanel(new GridLayout(2, 1)) {{
                    add(new JScrollPane(notSelectedFiles = makeSimpleTable(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            ((DefaultTableModel) selectedFiles.getModel()).addRow(new Object[] {});
                            ((DefaultTableModel) notSelectedFiles.getModel()).removeRow(notSelectedFiles.getSelectedRow());
                            revalidate();
                            repaint();
                        }
                    })));
                    add(new JScrollPane(selectedFiles = makeSimpleTable(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            ((DefaultTableModel) notSelectedFiles.getModel()).addRow(new Object[] {});
                            ((DefaultTableModel) selectedFiles.getModel()).removeRow(selectedFiles.getSelectedRow());
                            revalidate();
                            repaint();
                        }
                    })));
                }}
            , BorderLayout.CENTER);
            add(new JPanel(new FlowLayout(FlowLayout.RIGHT)) {{
                add(new JButton("Stop") {{

                }});
                add(new JButton("Start") {{

                }});
                add(new JLabel() {{

                }});
            }}, BorderLayout.SOUTH);
        }};
        addState("main", mainPanel);
        setState("main");
    }

    private static JTable makeSimpleTable(MouseListener ml) {
        return new JTable(new Object[][] {}, new Object[] { "No.", "Duration", "Size" }) {
            private static final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);

            {
                setRowHeight(25);
                addMouseListener(ml);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                var c = super.prepareRenderer(renderer, row, column);
                c.setFont(font);
                return c;
            }
        };
    }
}
