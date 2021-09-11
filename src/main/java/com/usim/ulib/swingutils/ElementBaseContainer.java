package com.usim.ulib.swingutils;

import com.usim.ulib.visualization.canvas.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public interface ElementBaseContainer {
    Map<String, JComponent> elements();

    default JComponent element(String tag, JComponent component) {
        elements().put(tag, component);
        return component;
    }
    default JComponent element(String tag) {
        return elements().get(tag);
    }
    default void removeE(String tag) {
        var e = element(tag);
        e.getParent().remove(e);
        elements().remove(tag);
    }
    default JButton buttonE(String tag) {
        return (JButton) element(tag);
    }
    default JTextField textFieldE(String tag) {
        return (JTextField) element(tag);
    }
    default JSlider sliderE(String tag) {
        return (JSlider) element(tag);
    }
    default JTabbedPane tabbedPaneE(String tag) {
        return (JTabbedPane) element(tag);
    }
    default JTextArea textAreaE(String tag) {
        return (JTextArea) element(tag);
    }
    default JTextPane textPaneE(String tag) {
        return (JTextPane) element(tag);
    }
    default JScrollPane scrollPaneE(String tag) {
        return (JScrollPane) element(tag);
    }
    default JPanel panelE(String tag) {
        return (JPanel) element(tag);
    }
    default JTable tableE(String tag) {
        return (JTable) element(tag);
    }
    default <T> JComboBox<T> comboBoxE(String tag) {
        //noinspection unchecked
        return (JComboBox<T>) element(tag);
    }
    default JLabel labelE(String tag) {
        return (JLabel) element(tag);
    }
    default JSplitPane splitPaneE(String tag) {
        return (JSplitPane) element(tag);
    }
    default JSeparator separatorE(String tag) {
        return (JSeparator) element(tag);
    }
    default <T> JList<T> listE(String tag) {
        //noinspection unchecked
        return (JList<T>) element(tag);
    }
    default JScrollBar scrollbarE(String tag) {
        return (JScrollBar) element(tag);
    }
    default JCheckBox checkBoxE(String tag) {
        return (JCheckBox) element(tag);
    }
    default JMenu menuE(String tag) {
        return (JMenu) element(tag);
    }
    default JMenuBar menuBarE(String tag) {
        return (JMenuBar) element(tag);
    }
    default JEditorPane editorPaneE(String tag) {
        return (JEditorPane) element(tag);
    }
    default Graph2DCanvas graph2DCanvasE(String tag) {
        return (Graph2DCanvas) element(tag);
    }
    default Graph3DCanvas graph3DCanvasE(String tag) {
        return (Graph3DCanvas) element(tag);
    }
    default Canvas canvasE(String tag) {
        return (Canvas) element(tag);
    }
    default CoordinatedCanvas coordinatedCanvasE(String tag) {
        return (CoordinatedCanvas) element(tag);
    }
    default CoordinatedScreen coordinatedScreenE(String tag) {
        return (CoordinatedScreen) element(tag);
    }
    default ImageCanvas imageCanvasE(String tag) {
        return (ImageCanvas) element(tag);
    }
    default <T> List<T> elements(@NotNull Class<T> clazz) {
        //noinspection unchecked
        return (List<T>) elements().values().stream().filter(clazz::isInstance).toList();
    }
    default JProgressBar progressbarPaneE(String tag) {
        return (JProgressBar) element(tag);
    }
    default JPasswordField passwordFieldE(String tag) {
        return (JPasswordField) element(tag);
    }
    default void updateElements() {}
}
