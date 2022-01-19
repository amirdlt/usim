package ahd.ulib.swingutils;

import ahd.ulib.utils.annotation.Critical;
import ahd.ulib.visualization.canvas.*;
import ahd.ulib.visualization.canvas.Canvas;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ElementBaseContainer {
    Map<String, Object> elements();

    private  <T> T element(@NotNull String tag, T component) {
        if (tag.contains("."))
            throw new RuntimeException("AHD:: Component tag should not contain '.'");
        elements().put(tag, component);
        return component;
    }
    private Object element(@NotNull String tag) {
        if (tag.contains("."))
            throw new RuntimeException("AHD:: Use elementE method instead.");
        return elements().get(tag);
    }
    default <T> T elementWithDummyTagE(T component) {
        return element((component.getClass().getName() + " -- hash=" + component.hashCode()).replace('.', ','), component);
    }
    default void removeE(String tag) {
        var e = element(tag);
        if (!(e instanceof JComponent))
            return;
        var parent = ((JComponent) e).getParent();
        if (parent == null)
            return;
        parent.remove((Component) e);
        parent.repaint();
        elements().remove(tag);
    }
    @Critical
    default void removeE(JComponent component) {
        forEachE((k, v) -> {
            if (v == component) {
                var p = component.getParent();
                if (p != null) {
                    p.remove(component);
                    p.repaint();
                    p.revalidate();
                }
            }
        });
    }
    default void repaintAndRevalidateParent(String tag) {
        var parent = elementE(tag).getParent();
        if (parent != null) {
            parent.repaint();
            parent.revalidate();
        }
    }
    default JButton buttonE(String tag) {
        return elementE(tag, JButton.class);
    }
    default JTextField textFieldE(String tag) {
        return elementE(tag, JTextField.class);
    }
    default JSlider sliderE(String tag) {
        return elementE(tag, JSlider.class);
    }
    default JTabbedPane tabbedPaneE(String tag) {
        return (JTabbedPane) elementE(tag);
    }
    default JTextArea textAreaE(String tag) {
        return (JTextArea) elementE(tag);
    }
    default JTextPane textPaneE(String tag) {
        return (JTextPane) elementE(tag);
    }
    default JScrollPane scrollPaneE(String tag) {
        return (JScrollPane) elementE(tag);
    }
    default JPanel panelE(String tag) {
        return (JPanel) elementE(tag);
    }
    default JTable tableE(String tag) {
        return (JTable) elementE(tag);
    }
    default <T> JComboBox<T> comboBoxE(String tag) {
        //noinspection unchecked
        return (JComboBox<T>) elementE(tag);
    }
    default JLabel labelE(String tag) {
        return (JLabel) elementE(tag);
    }
    default JSplitPane splitPaneE(String tag) {
        return (JSplitPane) elementE(tag);
    }
    default JSeparator separatorE(String tag) {
        return (JSeparator) elementE(tag);
    }
    default <T> JList<T> listE(String tag) {
        //noinspection unchecked
        return (JList<T>) elementE(tag);
    }
    default JScrollBar scrollbarE(String tag) {
        return (JScrollBar) elementE(tag);
    }
    default JCheckBox checkBoxE(String tag) {
        return (JCheckBox) elementE(tag);
    }
    default JMenu menuE(String tag) {
        return (JMenu) elementE(tag);
    }
    default JMenuBar menuBarE(String tag) {
        return (JMenuBar) elementE(tag);
    }
    default JMenuItem menuItemE(String tag) {
        return (JMenuItem) elementE(tag);
    }
    default JEditorPane editorPaneE(String tag) {
        return (JEditorPane) elementE(tag);
    }
    default Graph2DCanvas graph2DCanvasE(String tag) {
        return (Graph2DCanvas) elementE(tag);
    }
    default Graph3DCanvas graph3DCanvasE(String tag) {
        return (Graph3DCanvas) elementE(tag);
    }
    default Canvas canvasE(String tag) {
        return (Canvas) elementE(tag);
    }
    default CoordinatedCanvas coordinatedCanvasE(String tag) {
        return (CoordinatedCanvas) elementE(tag);
    }
    default CoordinatedScreen coordinatedScreenE(String tag) {
        return (CoordinatedScreen) elementE(tag);
    }
    default ImageCanvas imageCanvasE(String tag) {
        return (ImageCanvas) elementE(tag);
    }
    @Critical
    default <T> List<T> elementsE(@NotNull Class<T> clazz) {
        var res = new ArrayList<T>();
        elements().values().stream().filter(ElementBaseContainer.class::isInstance).forEach(e -> res.addAll(((ElementBaseContainer) e).elementsE(clazz)));
        //noinspection unchecked
        res.addAll((Collection<? extends T>) elements().values().stream().filter(clazz::isInstance).toList());
        return res;
    }
    default JProgressBar progressbarPaneE(String tag) {
        return (JProgressBar) elementE(tag);
    }
    default JPasswordField passwordFieldE(String tag) {
        return (JPasswordField) elementE(tag);
    }
    default JDesktopPane desktopPaneE(String tag) {
        return (JDesktopPane) elementE(tag);
    }
    default JTree treeE(String tag) {
        return (JTree) elementE(tag);
    }
    default JInternalFrame internalFrameE(String tag) {
        return (JInternalFrame) elementE(tag);
    }
    default boolean checkTypeE(String tag, @NotNull Class<?> expectedType) {
        return expectedType.isInstance(elementE(tag));
    }
    default void updateElementsE() {}
    default void repaintAndRevalidateElementE(@NotNull String address) {
        var ee = address.contains(".") ? parent(address).element(address.substring(address.lastIndexOf('.') + 1)) : element(address);
        if (ee instanceof JComponent c) {
            c.repaint();
            c.revalidate();
        } else if (ee instanceof ElementBaseContainer ebc) {
            ebc.forEachE(e -> {
                e.repaint();
                e.revalidate();
            });
        }
    }
    default void forEachE(Consumer<JComponent> consumer) {
        elements().values().forEach(e -> {
            if (e instanceof JComponent c) {
                consumer.accept(c);
            } else if (e instanceof ElementBaseContainer ebc) {
                ebc.forEachE(consumer);
            }
        });
    }
    default void forEachE(BiConsumer<String, JComponent> consumer) {
        elements().forEach((k, v) -> {
            if (v instanceof JComponent c) {
                consumer.accept(k, c);
            } else if (v instanceof ElementBaseContainer ebc) {
                ebc.forEachE(consumer);
            }
        });
    }
    default <T> T asE(String tag, @NotNull Class<T> clazz) {
        return clazz.cast(elementE(tag));
    }
    private static ElementBaseContainer safety(@NotNull ElementBaseContainer parent, String key) {
        var ebc = (ElementBaseContainer) parent.element(key);
        if (ebc != null)
            return ebc;
        ebc = getInstance();
        return parent.element(key, ebc);
    }
    private ElementBaseContainer parent(@NotNull String address) {
        var path = address.split("\\.");
        final var len = path.length;
        if (len == 0)
            return this;
        if (len == 1)
            return (ElementBaseContainer) element(path[0]);
        int i = 0;
        var node = safety(this, path[0]);
        while (++i < len - 1)
            node = safety(node, path[i]);
        return node;
    }
    default <T extends JComponent> T elementE(@NotNull String address, T component) {
        return address.contains(".") ? parent(address).element(address.substring(address.lastIndexOf('.') + 1), component) : element(address,  component);
    }
    default JComponent elementE(@NotNull String address) {
        return (JComponent) (address.contains(".") ? parent(address).element(address.substring(address.lastIndexOf('.') + 1)) : element(address));
    }
    default <T extends JComponent> T elementE(String address, @NotNull Class<T> resultType) {
        return resultType.cast(elementE(address));
    }
    default ElementBaseContainer asElementBaseE(@NotNull String address) {
        return (ElementBaseContainer) (address.contains(".") ? parent(address).element(address.substring(address.lastIndexOf('.') + 1)) : element(address));
    }
    default void removeAllE(@NotNull String address, Predicate<JComponent> condition) {
        var ee = address.contains(".") ? parent(address).element(address.substring(address.lastIndexOf('.') + 1)) : element(address);
        if (ee instanceof JComponent c && condition.test(c)) {
            c.removeAll();
        } else if (ee instanceof ElementBaseContainer ebc) {
            ebc.forEachE(e -> {
                if (condition.test(e))
                    e.removeAll();
            });
        }
        if (this instanceof JComponent c) {
            c.repaint();
            c.revalidate();
        }
    }
    default void removeAllE(String address) {
        removeAllE(address, JPanel.class::isInstance);
    }
    static <T extends JComponent> T as(JComponent component, @NotNull Class<T> clazz) {
        return clazz.cast(component);
    }
    @Contract(pure = true)
    static @NotNull ElementBaseContainer getInstance() {
        final var map = new HashMap<String, Object>();
        return () -> map;
    }
    static @NotNull ElementBaseContainer getInstance(Map<String, Object> fromMap) {
        return () -> fromMap;
    }
}
