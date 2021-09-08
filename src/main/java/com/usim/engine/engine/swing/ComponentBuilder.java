package com.usim.engine.engine.swing;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public final class ComponentBuilder {

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
                                args.put(textField.getName(), Double.parseDouble(textField.getText()));
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
}
