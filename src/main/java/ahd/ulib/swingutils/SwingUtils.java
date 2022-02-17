package ahd.ulib.swingutils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.border.Border;
import java.awt.*;

public final class SwingUtils {
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Border getRoundedBorder(int radius) {
        return new Border() {
            private final Insets insets = new Insets(radius + 1, radius + 1, radius + 2, radius);

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return insets;
            }

            @Override
            public boolean isBorderOpaque() {
                return true;
            }
        };
    }
}
