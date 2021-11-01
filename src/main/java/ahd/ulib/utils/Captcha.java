package ahd.ulib.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Captcha {
    private static final CaptchaGeneratorProperties DEFAULT_CAPTCHA_GENERATOR_PROPERTIES = new CaptchaGeneratorProperties();

    public static Map<String, BufferedImage> create(int num, CaptchaGeneratorProperties properties) {
        var res = new HashMap<String, BufferedImage>(num);
        while (num-- > 0) {
            var text = randomText(properties.maxCharCount, properties);
            res.put(text, create(text, properties));
        }
        return Collections.unmodifiableMap(res);
    }

    public static Map<String, BufferedImage> create(int num) {
        return create(num, DEFAULT_CAPTCHA_GENERATOR_PROPERTIES);
    }

    public static @NotNull BufferedImage create(String text, CaptchaGeneratorProperties properties) {
        var res = new BufferedImage(properties.width, properties.height, BufferedImage.TYPE_INT_RGB);
        var g2d = res.createGraphics();
        if (Math.random() < 0.5) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, properties.maxX, properties.maxY);
            for (int i = 0; i < properties.minBoxCount; i++) {
                g2d.setColor(properties.backgroundColors[(int) (Math.random() * properties.backgroundColors.length)]);
                g2d.fillRect((int) (Math.random() * properties.maxX), (int) (Math.random() * properties.maxY),
                        (int) (Math.random() * properties.maxX), (int) (Math.random() * properties.maxY));
            }

            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, (properties.maxX) - 1, properties.maxY - 1);

//            g2d.setColor(Color.BLACK);
//            g2d.drawRect(0, 0, (properties.maxX) - 1, properties.maxY - 1);

            var affineTransform = new AffineTransform();
            for (int i = 0; i < properties.maxCharCount; i++) {
                double angle = 0;
                if (Math.random() * 2 > 1) {
                    angle = Math.random() * properties.maxSkew;
                } else {
                    angle = Math.random() * -properties.maxSkew;
                }
                affineTransform.rotate(angle, (properties.charWidth * i) + (properties.charWidth / 2.0), properties.maxY / 2.0);
                g2d.setTransform(affineTransform);

                setRandomFont(g2d);
                setRandomFGColor(g2d);

//                var c = g2d.getColor();
//                g2d.setColor(new Color((int) ((Math.random() * 0.02 + 0.8) * 256), c.getRed(), c.getGreen(), c.getBlue()));
//
//
//                g2d.drawString(text.substring(i, i + 1), (i * properties.charWidth) + 3, 28 + (int) (Math.random() * 6));
//                affineTransform.rotate(-angle, (properties.charWidth * i) + (properties.charWidth / 2.0), properties.maxY / 2.0);
            }
//
//            for (int i = 0; i < properties.maxLineCount; i ++) {
//                g2d.setXORMode(Color.RED);
//                setRandomBGColor(g2d);
//                g2d.setStroke(new BasicStroke(4));
//                int y1 = (int) (Math.random() * properties.maxY);
//                g2d.drawLine(0, y1,
//                        properties.maxX, y1);
//            }
            affineTransform = new AffineTransform();
            for (int i = 0; i < properties.maxCharCount; i++) {
                double angle;
                if (Math.random() * 2 > 1) {
                    angle = Math.random() * properties.minSkew;
                } else {
                    angle = Math.random() * -properties.minSkew;
                }
                affineTransform.rotate(angle, (properties.charWidth * i) + (properties.charWidth / 2.0), properties.maxY / 2.0);
                g2d.setTransform(affineTransform);
                setRandomFont(g2d);
                setRandomFGColor(g2d);
                g2d.drawString(text.substring(i, i + 1),
                        (i * properties.charWidth) + 3, 28 + (int) (Math.random() * 6));

                affineTransform.rotate(-angle, (properties.charWidth * i) + (properties.charWidth / 2.0), properties.maxY / 2.0);
            }
            for (int i = 0; i < properties.maxLineCount; i ++) {
                g2d.setXORMode(Color.RED);
                setRandomBGColor(g2d);
                g2d.setStroke(new BasicStroke(4));
                //affineTransform.rotate(0);
                int y1 = (int) (Math.random() * properties.maxY);
                //int y2 = (int) (Math.random() * MAX_Y);
                g2d.drawLine(0, y1,
                        properties.maxX, y1);
            }
        } else {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, properties.maxX, properties.maxY);
            for (int i = 0; i < properties.minBoxCount; i++) {
                paindBoxes(g2d);
            }

            Font font = new Font("dialog", Font.BOLD, 33);
            g2d.setFont(font);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, (properties.maxX) - 1, properties.maxY - 1);

            AffineTransform affineTransform = new AffineTransform();
            for (int i = 0; i < properties.maxCharCount; i++) {
                double angle = 0;
                if (Math.random() * 2 > 1) {
                    angle = Math.random() * properties.maxSkew;
                } else {
                    angle = Math.random() * -properties.maxSkew;
                }
                affineTransform.rotate(angle, (properties.charWidth * i) + (properties.charWidth / 2.0), properties.maxY / 2.0);
                // g2d.setTransform(affineTransform);
                // setRandomFont(g2d);
                // setRandomFGColor(g2d);
                g2d.drawString(text.substring(i, i + 1),
                        (i * properties.charWidth) + 3, 28 + (int) (Math.random() * 6));

                affineTransform.rotate(-angle, (properties.charWidth * i) + (properties.charWidth / 2.0), properties.maxY / 2.0);
            }

        /*g2d.setXORMode(Color.RED);
        g2d.setStroke(new BasicStroke(10));
        g2d.drawLine(0,0,MAX_X, MAX_Y);
        g2d.setXORMode(Color.YELLOW);
        g2d.drawLine(0,MAX_Y,MAX_X, 0);*/

            g2d.setXORMode(properties.backgroundColors[(int) (Math.random() * properties.backgroundColors.length)]);
            g2d.setStroke(new BasicStroke(10));
            g2d.drawLine(0, properties.maxY / 2, properties.maxX, properties.maxY / 2);
            g2d.setXORMode(properties.backgroundColors[(int) (Math.random() * properties.backgroundColors.length)]);
            g2d.drawLine(0, (properties.maxY / 2) - 10, properties.maxX, (properties.maxY / 2) - 10);
            g2d.setXORMode(properties.backgroundColors[(int) (Math.random() * properties.backgroundColors.length)]);
            g2d.drawLine(0, (properties.maxY / 2) + 10, properties.maxX, (properties.maxY / 2) + 10);

        /*for (int i = 0; i < DRAW_LINES; i ++) {
            g2d.setXORMode(Color.RED);
            // setRandomBGColor(g2d);
            g2d.setStroke(new BasicStroke(4));
            //affineTransform.rotate(0);
            int y1 = (int) (Math.random() * MAX_Y);
            //int y2 = (int) (Math.random() * MAX_Y);
             g2d.drawLine(0, y1,
                    MAX_X, y1);
        }*/

        }
        g2d.dispose();
        return res;
    }

    private static void paindBoxes(Graphics2D g2d) {
        setRandomBGColor(g2d);
        g2d.fillRect(getRandomX(), getRandomY(),
                getRandomX(), getRandomY());
    }

    private static int getRandomX() {
        return (int) (Math.random() * DEFAULT_CAPTCHA_GENERATOR_PROPERTIES.maxX);
    }

    private static int getRandomY() {
        return (int) (Math.random() * DEFAULT_CAPTCHA_GENERATOR_PROPERTIES.maxY);
    }

    private static void setRandomFont(Graphics2D g2d) {
        Font font = new Font("dialog", Font.BOLD, 33);
        g2d.setFont(font);
    }

    private static void setRandomFGColor(Graphics2D g2d) {
        int colorId = (int) (Math.random() * DEFAULT_CAPTCHA_GENERATOR_PROPERTIES.foregroundColors.length);
        g2d.setColor(DEFAULT_CAPTCHA_GENERATOR_PROPERTIES.foregroundColors[colorId]);
    }

    private static void setRandomBGColor(Graphics2D g2d) {
        int colorId = (int) (Math.random() * DEFAULT_CAPTCHA_GENERATOR_PROPERTIES.backgroundColors.length);
        g2d.setColor(DEFAULT_CAPTCHA_GENERATOR_PROPERTIES.backgroundColors[colorId]);
    }

    @Contract("_, _ -> new")
    private static @NotNull String randomText(int len, @NotNull Captcha.CaptchaGeneratorProperties properties) {
        var chars = CaptchaGeneratorProperties.defaultCharSet.toCharArray();
        var randomText = new char[len];
        for (int i = 0; i < len; i++)
            randomText[i] = chars[(int) (chars.length * Math.random())];
        return String.valueOf(randomText);
    }

    public static CaptchaGeneratorProperties defaultCaptchaProperties() {
        return DEFAULT_CAPTCHA_GENERATOR_PROPERTIES;
    }

    public static class CaptchaGeneratorProperties implements Cloneable {
        public static final Color[] defaultBackgroundColors = new Color[] { Color.RED, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE,
                Color.PINK, Color.YELLOW };
        public static final Color[] defaultForegroundColors = new Color[] { Color.BLACK, Color.BLUE, Color.DARK_GRAY };
        public static String defaultCharSet;

        static {
            var sb = new StringBuilder();
            int index = 0;
            while (index < 26)
                sb.append((char) ('A' + index++));
            defaultCharSet = sb.toString();
        }

        public int width = 150;
        public int height = 40;
        public int minCharCount = 5;
        public int maxCharCount = 5;
        public int minLineCount = 0;
        public int maxLineCount = 2;
        public Color[] backgroundColors = defaultBackgroundColors;
        public Color[] foregroundColors = defaultForegroundColors;
        public int charWidth = 32;
        public double minSkew = 0.5;
        public double maxSkew = 0.5;
        public String charSet;
        public boolean simple = true;
        public int minBoxCount = 2;
        public int maxBoxCount = 2;

        private int maxX = charWidth * maxCharCount - 10;
        private int maxY = height;

        public CaptchaGeneratorProperties width(int width) {
            this.width = width;
            return this;
        }

        public CaptchaGeneratorProperties height(int height) {
            this.height = height;
            maxY = height;
            return this;
        }

        public CaptchaGeneratorProperties minLineCount(int minLineCount) {
            this.minLineCount = minLineCount;
            return this;
        }

        public CaptchaGeneratorProperties maxLineCount(int maxLineCount) {
            this.maxLineCount = maxLineCount;
            return this;
        }

        public CaptchaGeneratorProperties backgroundColors(Color... backgroundColors) {
            this.backgroundColors = backgroundColors;
            return this;
        }

        public CaptchaGeneratorProperties minCharCount(int minCharCount) {
            this.minCharCount = minCharCount;
            return this;
        }

        public CaptchaGeneratorProperties maxCharCount(int maxCharCount) {
            this.maxCharCount = maxCharCount;
            maxX = charWidth * maxCharCount;
            return this;
        }

        public CaptchaGeneratorProperties foregroundColors(Color... foregroundColors) {
            this.foregroundColors = foregroundColors;
            return this;
        }

        public CaptchaGeneratorProperties charWidth(int charWidth) {
            this.charWidth = charWidth;
            maxX = charWidth * maxCharCount;
            return this;
        }

        public CaptchaGeneratorProperties minSkew(double minSkew) {
            this.minSkew = minSkew;
            return this;
        }

        public CaptchaGeneratorProperties maxSkew(double maxSkew) {
            this.maxSkew = maxSkew;
            return this;
        }

        public CaptchaGeneratorProperties skew(double skew) {
            return minSkew(skew).maxSkew(skew);
        }

        public CaptchaGeneratorProperties size(int width, int height) {
            return width(width).height(height);
        }

        public CaptchaGeneratorProperties simple(boolean simple) {
            this.simple = simple;
            return this;
        }

        public CaptchaGeneratorProperties minBoxCount(int minBoxCount) {
            this.minCharCount = minBoxCount;
            return this;
        }

        public CaptchaGeneratorProperties maxBoxCount(int maxBoxCount) {
            this.maxSkew = maxBoxCount;
            return this;
        }

        public CaptchaGeneratorProperties boxCount(int boxCount) {
            return minBoxCount(boxCount).maxBoxCount(boxCount);
        }

        public int maxX() {
            return maxX;
        }

        public int maxY() {
            return maxY;
        }

        @Override
        public CaptchaGeneratorProperties clone() {
            try {
                CaptchaGeneratorProperties clone = (CaptchaGeneratorProperties) super.clone();
                clone.foregroundColors = Arrays.copyOf(foregroundColors, foregroundColors.length);
                clone.backgroundColors = Arrays.copyOf(backgroundColors, backgroundColors.length);
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    @TestOnly
    public static void main(String[] args) throws IOException {
        var set = create(4990).entrySet();
        for (var entry : set)
            Utils.saveRenderedImage(entry.getValue(), ".\\out\\portal\\dataset\\" + entry.getKey().toLowerCase(), "jpeg");
//        Utils.saveRenderedImage(create("abcde", defaultCaptchaProperties()), ".\\out\\portal\\dataset\\abcde", "jpg");
//        ImageIO.write(create("abcde", defaultCaptchaProperties()), "jpg", new File("a.jpeg"));
    }
}
