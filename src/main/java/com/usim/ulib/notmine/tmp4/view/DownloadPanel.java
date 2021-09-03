package com.usim.ulib.notmine.tmp4.view;

import com.usim.ulib.notmine.tmp4.model.DownloadState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static com.usim.ulib.notmine.tmp4.model.DownloadState.NOT_STARTED;

public abstract class DownloadPanel extends JPanel {
    protected JTable table;
    protected DefaultTableModel tableModel;

    public DownloadPanel() {
        super(new BorderLayout());
        init();
    }

    private void init() {
        var address = new JTextField();
        address.addActionListener(e -> addDownloadUrl(address.getText()));
        address.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        address.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        var addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            if (address.getText().isBlank())
                return;
            addDownloadUrl(address.getText());
            address.setText("");
        });
        addButton.setPreferredSize(new Dimension(100, 30));

        var paste = new JButton("Paste From Clipboard");
        paste.addActionListener(e -> {
            try {
                address.setText((String) Toolkit.getDefaultToolkit()
                        .getSystemClipboard().getData(DataFlavor.stringFlavor));
            } catch (UnsupportedFlavorException | IOException exception) {
                exception.printStackTrace();
            }
        });

        var north = new JPanel(new BorderLayout());
        north.add(address, BorderLayout.CENTER);

        var wrapper = new JPanel(new GridLayout(1, 2));
        wrapper.add(addButton);
        wrapper.add(paste);
        north.add(wrapper, BorderLayout.EAST);
        north.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                var c = super.prepareRenderer(renderer, row, column);
                c.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                return c;
            }
        };
        table.setModel(new DefaultTableModel(new Object[0][0], new Object[] {
                "No.", "ID", "Name", "Speed (KB)", "Remaining Time", "Progress",
                "Remaining Size (KB)", "Downloaded Size (KB)", "Total Size (KB)", "State" }) {
            private final Class<?>[] classes = new Class[] { Integer.class, Long.class, String.class, Integer.class,
                    Integer.class, JProgressBar.class, Long.class, Long.class, Long.class, DownloadState.class };
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return classes[columnIndex];
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableModel = (DefaultTableModel) table.getModel();
        table.setRowHeight(40);
        table.setDefaultRenderer(JProgressBar.class, new TableCellRenderer() {
            private final JProgressBar progressBar = new JProgressBar(0, 100);
            {progressBar.setStringPainted(true);}
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                progressBar.setValue((int) ((double) value * 100));
                return progressBar;
            }
        });
        table.setGridColor(Color.WHITE);
        var colModel = table.getColumnModel();
        colModel.getColumn(0).setMaxWidth(50);
        colModel.getColumn(1).setMaxWidth(50);
        colModel.getColumn(2).setWidth(200);
        colModel.getColumn(3).setMaxWidth(90);
        colModel.getColumn(4).setWidth(120);
        colModel.getColumn(5).setMinWidth(250);
        colModel.getColumn(6).setWidth(85);
        colModel.getColumn(7).setWidth(85);
        colModel.getColumn(8).setWidth(85);
        colModel.getColumn(9).setWidth(120);


        var removeButton = new JButton("Remove From List");
        removeButton.addActionListener(e -> {
            if (stateOfSelectedRow() == DownloadState.COMPLETED || stateOfSelectedRow() == DownloadState.ERROR) {
                stopUpdater(idOfSelectedRow());
                removeSelectedRow();
            }
        });

        var removeWithFileButton = new JButton("Delete");
        removeWithFileButton.addActionListener(e -> {
            removeWithFile(idOfSelectedRow());
            removeSelectedRow();
        });

        var start = new JButton("Start");
        start.addActionListener(e -> start(idOfSelectedRow()));

        var stop = new JButton("Stop");
        stop.addActionListener(e -> pause(idOfSelectedRow()));

        var startAll = new JButton("Start All");
        startAll.addActionListener(e -> startAll());

        var pauseAll = new JButton("Pause All");
        pauseAll.addActionListener(e -> pauseAll());

        var south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(start);
        south.add(stop);
        south.add(startAll);
        south.add(pauseAll);
        south.add(removeButton);
        south.add(removeWithFileButton);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    private DownloadState stateOfSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0)
            return null;
        return (DownloadState) tableModel.getValueAt(row, 9);
    }

    private void removeSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        tableModel.removeRow(row);
    }

    private long idOfSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0)
            return -1;
        return (long) tableModel.getValueAt(row, 1);
    }

    protected void updateTable(long id, double speed, double remainingTime, long size, double progress, DownloadState state) {
        for (int i = 0; i < tableModel.getRowCount(); i++)
            if ((long) (tableModel.getValueAt(i, 1)) == id) {
                if (speed >= 0)
                    tableModel.setValueAt((int) speed, i, 3);
                if (remainingTime >= 0)
                    tableModel.setValueAt((int) remainingTime, i, 4);
                if (size >= 0) {
                    tableModel.setValueAt((long) table.getValueAt(i, 8) - size, i, 6);
                    tableModel.setValueAt(size, i, 7);
                    tableModel.setValueAt(progress, i, 5);
                }
                if (state != null)
                    tableModel.setValueAt(state, i, 9);
                return;
            }
    }

    protected abstract void addDownloadUrl(String address);

    protected abstract void removeWithFile(long id);

    protected abstract void start(long id);

    protected abstract void pause(long id);

    protected abstract void startAll();

    protected abstract void pauseAll();

    protected abstract void stopUpdater(long id);

    /*
     No.", "ID", "Name", "Speed", "Remaining Time", "Progress",
                "Remaining Size (KB)", "Downloaded Size (KB)", "Total Size (KB)", "State"
    */
    protected void addRow(long id, String name, long totalSize) {
        tableModel.addRow(new Object[] { tableModel.getRowCount() + 1, id, name, 0, 0, 0d,
                totalSize, 0L, totalSize, NOT_STARTED });
    }
}
