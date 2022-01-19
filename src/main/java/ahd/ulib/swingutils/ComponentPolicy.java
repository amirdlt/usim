package ahd.ulib.swingutils;

import javax.swing.*;

@FunctionalInterface
public interface ComponentPolicy {
    JComponent affectPolicy(JComponent component);
}
