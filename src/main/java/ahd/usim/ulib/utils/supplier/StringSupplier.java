package ahd.usim.ulib.utils.supplier;

import ahd.usim.ulib.jmath.datatypes.functions.NoArgFunction;
import ahd.usim.ulib.visualization.canvas.CoordinatedScreen;

import java.awt.*;

@FunctionalInterface
public interface StringSupplier extends NoArgFunction<String> {
    int defaultFontSize = 18;
    int defaultFontStyle = Font.BOLD;
    String defaultFont = Font.SANS_SERIF;
    Color defaultColor = Color.GREEN;

    String getText();

    default Color getColor() {
        return defaultColor;
    }

    default Font getFont() {
        return new Font(defaultFont, defaultFontStyle, (int) (defaultFontSize() * (cs() == null ? 1 : cs().scaleX() / 100)));
    }

    default Point getPosOnScreen() {
        return null;
    }

    default CoordinatedScreen cs() {
        return null;
    }

    default int defaultFontSize() {
        return defaultFontSize;
    }

    @Override
    default String value() {
        return getText();
    }
}
