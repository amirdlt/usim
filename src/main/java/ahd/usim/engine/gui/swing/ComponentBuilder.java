package ahd.usim.engine.gui.swing;

import ahd.ulib.jmath.parser.Function4DParser;
import ahd.ulib.swingutils.ElementBaseContainer;
import ahd.ulib.utils.consumer.StringConsumer;
import ahd.ulib.utils.supplier.StringSupplier;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;

public final class ComponentBuilder {
    private final static Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);

    private ComponentBuilder() {}

    @FunctionalInterface
    interface Function {
        void func(Map<String, Object> args);
    }

    interface Arg {}
    record NumberBasedArg<T extends Number>(String name, T from, T to) implements Arg {}
    record OptionBasedArg<T>(String name, Map<String, T> options) implements Arg {}

    @Contract("_, _, _, _ -> new")
    static @NotNull JPanel createFunctionCallerPanel(String name, Function function, Runnable inAppropriateArgValueAlert, Arg @NotNull ... args) {
        return new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
            add(new JLabel(name + ": "));
            var _map = new HashMap<String, OptionBasedArg<?>>();
            var map = new HashMap<String, JComponent>(){{
                for (var arg : args)
                    if (arg instanceof NumberBasedArg numberBasedArg) {
                        put(numberBasedArg.name, new JTextField(numberBasedArg.name) {{
                            setName(numberBasedArg.name);
                            setPreferredSize(new Dimension(80, 32));
                            setToolTipText("from: " + numberBasedArg.from + " to: " + numberBasedArg.to);
                        }});
                    } else if (arg instanceof OptionBasedArg optionBasedArg) {
                        put(optionBasedArg.name, new JComboBox<>() {{
                            setName(optionBasedArg.name);
                            addItem(optionBasedArg.name);
                            //noinspection unchecked
                            optionBasedArg.options.keySet().forEach(this::addItem);
                        }});
                        _map.put(optionBasedArg.name, optionBasedArg);
                    } else {
                        throw new RuntimeException("AHD:: Not appropriate Arg type.");
                    }
            }};
            map.values().forEach(this::add);
            add(new JButton("Apply") {{
                addActionListener(e -> {
                    var args = new HashMap<String, Object>();
                    for (var component : map.values())
                        if (component instanceof JTextField textField) {
                            if (textField.getText().equals(textField.getName())) {
                                inAppropriateArgValueAlert.run();
                                return;
                            }
                            try {
                                args.put(textField.getName(), Function4DParser.parser(textField.getText()).atOrigin());
                            } catch (NumberFormatException ex) {
                                inAppropriateArgValueAlert.run();
                                return;
                            }
                        } else if (component instanceof JComboBox comboBox) {
                            if (Objects.equals(comboBox.getSelectedItem(), comboBox.getName())) {
                                inAppropriateArgValueAlert.run();
                                return;
                            }
                            //noinspection SuspiciousMethodCalls
                            args.put(comboBox.getName(), _map.get(comboBox.getName()).options.get(comboBox.getSelectedItem()));
                        }
                    function.func(args);
                    for (var c : map.values())
                        if (c instanceof JTextField textField) {
                            textField.setText(textField.getName());
                        } else if (c instanceof JComboBox comboBox) {
                            comboBox.setSelectedIndex(0);
                        }
                });
            }});
        }};
    }

    @Contract("_, _ -> new")
    private static @NotNull JPanel createEntityPanel(@NotNull ElementBaseContainer frame, int id) {
        return new JPanel() {{
            final var main = this;
            setName("Entity-" + id);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(new JLabel(getName(), JLabel.CENTER) {{
                setFont(titleFont);
            }});
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                setPreferredSize(new Dimension(420, 40));
                add(new JLabel("Surface: "));
                add(frame.element(main.getName() + "-xSurface-textField", new JTextField("x") {{
                    setPreferredSize(new Dimension(200, 32));
                }}));
                add(frame.element(main.getName() + "-ySurface-textField", new JTextField("y") {{
                    setPreferredSize(new Dimension(200, 32));
                }}));
                add(frame.element(main.getName() + "-zSurface-textField", new JTextField("z") {{
                    setPreferredSize(new Dimension(200, 32));
                }}));
                add(frame.element(main.getName() + "-xLowBound-textField", new JTextField("z") {{
                    setPreferredSize(new Dimension(200, 32));
                }}));
                add(frame.element(main.getName() + "-xUpBound-textField", new JTextField("z") {{
                    setPreferredSize(new Dimension(200, 32));
                }}));
                add(frame.element(main.getName() + "-xDelta-textField", new JTextField("z") {{
                    setPreferredSize(new Dimension(200, 32));
                }}));
                add(frame.element(main.getName() + "-yDelta-textField", new JTextField("z") {{
                    setPreferredSize(new Dimension(200, 32));
                }}));
                add(frame.element(main.getName() + "-yUpBound-textField", new JTextField("z") {{
                    setPreferredSize(new Dimension(200, 32));
                }}));
            }});
            add(frame.element(getName() + "-new-panel", new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(new JButton("New") {{
                    addActionListener(e -> {
                        frame.removeE(main.getName() + "-new-panel");
                        main.add(createEntityPanel(frame, id + 1));
                    });
                }});
            }}));
        }};
    }

    static @NotNull JPanel createEntityPanel(ElementBaseContainer frame) {
        return createEntityPanel(frame, 0);
    }

    static @NotNull JPanel createTextEditor(StringConsumer saveAction, StringSupplier refreshAction, StringConsumer applyAction, String initialText) {
        return new JPanel() {{
            setLayout(new BorderLayout());
            final var textArea = new RSyntaxTextArea(initialText) {{
                setCodeFoldingEnabled(true);
                setSyntaxEditingStyle(SYNTAX_STYLE_C);
                setAntiAliasingEnabled(true);
                try {
                    //noinspection SpellCheckingInspection
                    Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml")).apply(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                var font = getFont();
                setFont(new Font(font.getFontName(), font.getStyle(), 14));
            }};
            add(new RTextScrollPane(textArea));
            add(new JPanel(new FlowLayout(FlowLayout.RIGHT)) {{
                add(new JButton("Apply") {{
                    addActionListener(e -> applyAction.consume(textArea.getText()));
                }});
                add(new JButton("Reload") {{
                    addActionListener(e -> textArea.setText(refreshAction.getText()));
                }});
                add(new JButton("Save") {{
                    addActionListener(e -> saveAction.consume(textArea.getText()));
                }});
            }}, BorderLayout.SOUTH);
        }};
    }
}
