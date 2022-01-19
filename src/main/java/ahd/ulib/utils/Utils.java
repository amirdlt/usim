package ahd.ulib.utils;

import ahd.ulib.jmath.datatypes.functions.ColorFunction;
import ahd.ulib.jmath.datatypes.functions.IntMapper2D;
import ahd.ulib.jmath.datatypes.functions.Mapper2D;
import ahd.ulib.jmath.datatypes.tuples.Point3D;
import ahd.ulib.swingutils.MainFrame;
import ahd.ulib.utils.annotation.NotFinal;
import ahd.ulib.utils.predicate.IntBinaryPredicate;
import ahd.ulib.visualization.canvas.Canvas;
import ahd.ulib.visualization.canvas.CoordinatedCanvas;
import ahd.ulib.visualization.canvas.Graph3DCanvas;
import ahd.ulib.visualization.canvas.Render;
import com.sun.management.OperatingSystemMXBean;
import org.jetbrains.annotations.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleSupplier;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import static ahd.ulib.utils.Utils.TextFileInfo.*;

@SuppressWarnings({ "unused", "SpellCheckingInspection", "CommentedOutCode"})
@NotFinal
public final class Utils {

    public static final String nirCmdPath;
    public static final String ffmpegCmdPath;
    public static final String ffprobeCmdPath;
    public static final Robot robot;
    public static final ScheduledExecutorService unsafeExecutor;

    public static final String defaultResourceRootPath;

    public static final int NANO = 1000000000;
    public static final int MILLIS = 1000000;
    public static final int MEGABYTE = 1024 * 1024;

    private static final MemoryMXBean memMXBean;
    private static final MemoryUsage memHeapUsage;
    private static final MemoryUsage memNonHeapUsage;
    private static final OperatingSystemMXBean osMXBean;

    static {
        memMXBean = ManagementFactory.getMemoryMXBean();
        memHeapUsage = memMXBean.getHeapMemoryUsage();
        memNonHeapUsage =memMXBean.getNonHeapMemoryUsage();
        osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        nirCmdPath = ".\\bin\\nircmdc.exe";
        ffmpegCmdPath = ".\\bin\\ffmpeg.exe";
        ffprobeCmdPath = ".\\bin\\ffprobe.exe";

        defaultResourceRootPath = ".\\src\\main\\resources\\";

        Robot rbt;
        try {
            rbt = new Robot();
        } catch (AWTException e) {
            rbt = null;
            e.printStackTrace();
        }
        robot = rbt;
        unsafeExecutor = Executors.newScheduledThreadPool(3);
    }

    ///////////////////
    public static @NotNull BufferedImage getCanvasImage(@NotNull Canvas canvas) {
        var res = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        var g2d = res.createGraphics();
        canvas.paintComponents(g2d);
        g2d.dispose();
        return res;
    }

    public static void saveCanvasAsImage(@NotNull String path, Canvas canvas) {
        try {
            ImageIO.write(getCanvasImage(canvas),
                    path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : "jpg",
                    new File(path.contains(".") ? path : path + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Contract(" -> new")
    public static @NotNull Color randomColor() {
        return new Color((int) (Math.random() * Integer.MAX_VALUE));
    }

    public static double round(double num, int precision) {
        if (!Double.toString(num).contains("."))
            return num;
        String res = num + "0".repeat(20);
        return Double.parseDouble(res.substring(0, res.indexOf('.') + precision + 1));
    }

    public static int[] getIntColorArrayOfImage(@NotNull BufferedImage bi) {
        return bi.getRaster().getDataBuffer() instanceof DataBufferInt b ?
                b.getData() :
                ((DataBufferInt) exactCloneWithARGB(bi).getRaster().getDataBuffer()).getData();
    }

    public static int[] @NotNull [] getIntColorArray2dOfImage(@NotNull BufferedImage bi) {
        return getAs2dArray(getIntColorArrayOfImage(bi), bi.getWidth(), bi.getHeight());
    }

    public static void multiThreadIntArraySetter(int[] src, IntUnaryOperator func, int numOfThreads) {
        if (numOfThreads < 1) {
            var len = src.length;
            for (int i = 0; i < len; i++)
                src[i] = func.applyAsInt(i);
            return;
        }
        var list = new Thread[numOfThreads];
        var partLen = src.length / numOfThreads;
        for (var i = 0; i < numOfThreads; i++) {
            final var counter = i;
            var t = new Thread(() -> {
                var start = counter * partLen;
                var end = counter == numOfThreads - 1 ? src.length : start + partLen;
                for (int j = start; j < end; j++)
                    src[j] = func.applyAsInt(j);
            });
            list[counter] = t;
            t.start();
        }
        for (var t : list)
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public static @NotNull BufferedImage createImage(int width, int height, IntBinaryOperator colorFunc, int numOfThreads) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        multiThreadIntArraySetter(getIntColorArrayOfImage(res), i -> colorFunc.applyAsInt(i / width, i % width), numOfThreads);
        return res;
    }

    public static @NotNull BufferedImage createImage(int width, int height, IntUnaryOperator colorFunc, int numOfThreads) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        multiThreadIntArraySetter(getIntColorArrayOfImage(res), colorFunc, numOfThreads);
        return res;
    }

    public static @NotNull BufferedImage createImage(@NotNull CoordinatedCanvas cc, ColorFunction colorFunc, int numOfThreads) {
        var w = cc.getWidth();
        var h = cc.getHeight();
        var res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        multiThreadIntArraySetter(getIntColorArrayOfImage(res),
                i -> colorFunc.valueAt(cc.coordinateX(i / w), cc.coordinateY(i % w)).getRGB(), numOfThreads);
        return res;
    }

    public static @NotNull BufferedImage createImage(int width, int height, @NotNull Render render) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g2d = res.createGraphics();
        render.render(g2d);
        g2d.dispose();
        return res;
    }

    public static BufferedImage @NotNull [] createImageSequence(int width, int height, Render render, int numOfFrames) {
        var res = new BufferedImage[numOfFrames];
        for (int i = 0; i < numOfFrames; i++) {
            var bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            var g2d = bi.createGraphics();
            render.render(g2d);
            g2d.dispose();
            res[i] = bi;
            render.tick();
        }
        return res;
    }

    @Contract("null -> fail")
    @Deprecated(forRemoval = true)
    public static @NotNull BufferedImage createMergeImageFromImageSequence(BufferedImage[] imageSequence) {
        if (imageSequence == null || imageSequence.length == 0)
            throw new IllegalArgumentException("AHD:: image sequence is null or empty");
        var res = new BufferedImage(imageSequence[0].getWidth(), imageSequence[0].getHeight(), BufferedImage.TYPE_INT_ARGB);
        var g2d = res.createGraphics();
        Arrays.stream(imageSequence).forEach(bi -> g2d.drawImage(bi, 0, 0, null));
        g2d.dispose();
        return res;
    }

    public static @NotNull BufferedImage createImageSingleThread(int width, int height, IntUnaryOperator colorFunc) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var arr = getIntColorArrayOfImage(res);
        for (int i = 0; i < arr.length; i++)
            arr[i] = colorFunc.applyAsInt(i);
        return res;
    }

    public static BufferedImage toBufferedImage(Image img) {
        return toBufferedImage(img, new Dimension(1280, 720));
    }

    public static BufferedImage toBufferedImage(Image img, Dimension dimensionIfNotRendered) {
        if (img instanceof BufferedImage im)
            return im;
        BufferedImage res = new BufferedImage(img.getWidth(null) <= 0 ? dimensionIfNotRendered.width : img.getWidth(null),
                img.getHeight(null) <= 0 ? dimensionIfNotRendered.height : img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        var g2d = res.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return res;
    }

    public static BufferedImage getBufferedImage(String path) {
        return toBufferedImage(getImage(path));
    }

    public static void affectOnImageSingleThread(BufferedImage image, IntUnaryOperator func) {
        var pixels = getIntColorArrayOfImage(image);
        for (int i = 0; i < pixels.length; i++)
            pixels[i] = func.applyAsInt(pixels[i]);
    }

    public static void affectOnImage(BufferedImage image, IntUnaryOperator func, int numOfThreads) {
        if (numOfThreads <= 0) {
            affectOnImageSingleThread(image, func);
            return;
        }
        var pixels = getIntColorArrayOfImage(image);
        multiThreadIntArraySetter(pixels, i -> func.applyAsInt(pixels[i]), numOfThreads);
    }

    public static @NotNull BufferedImage readImage(String path) throws IOException {
        return exactCloneWithARGB(ImageIO.read(new File(path)));
    }

    public static @Nullable BufferedImage readImageFromUrl(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }

    public static @Nullable Icon getIconFromUrl(String url) {
        var bi = readImageFromUrl(url);
        return bi == null ? null : new ImageIcon(bi);
    }

    private static @NotNull BufferedImage exactCloneWithARGB(@NotNull BufferedImage source) {
        var res = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        var g = res.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return res;
    }

    public static @NotNull BufferedImage glitchedCloneOfImage(@NotNull BufferedImage source, IntBinaryOperator colorFunction, DoubleSupplier randFactorFunction) {
        final var w = source.getWidth();
        var res = new BufferedImage(w, source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final var s = getIntColorArrayOfImage(source);
        final var c = getIntColorArrayOfImage(res);
        for (int i = 0; i < s.length; i++)
            if (Math.random() < randFactorFunction.getAsDouble()) {
                c[i] = s[i];
            } else {
                c[i] = colorFunction.applyAsInt(i / w, i % w);
            }
        return res;
    }

    public static @NotNull BufferedImage cloneAffectivelyImage(@NotNull BufferedImage source, IntUnaryOperator effector,
            IntBinaryPredicate changePredicate) {
        final var w = source.getWidth();
        var res = new BufferedImage(w, source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        var c = getIntColorArrayOfImage(res);
        var s = getIntColorArrayOfImage(source);
        for (int i = 0; i < c.length; i++)
            c[i] = changePredicate.test(i / w, i % w) ? effector.applyAsInt(c[i]) : s[i];
        return res;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static @NotNull BufferedImage cloneSpatialAffectedImage(@NotNull BufferedImage source, IntMapper2D spatialMapper,
            IntBinaryPredicate mappingPredicate, IntBinaryOperator colorIfNotInBound,
            IntBinaryOperator colorIfNotMapping) {
        final var w = source.getWidth();
        final var h = source.getHeight();
        var res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        var c = getIntColorArrayOfImage(res);
        var s = getIntColorArrayOfImage(source);
        for (int i = 0; i < s.length; i++) {
            final var x = i / w;
            final var y = i % w;
            var m = spatialMapper.map(x, y);
            if (m.x >= 0 && m.y >= 0 && m.x < h && m.y < w) {
                c[i] = mappingPredicate.check(m.x, m.y) ? s[m.x * w + m.y] : colorIfNotMapping.applyAsInt(x, y);
            } else if (colorIfNotInBound != null) {
                c[i] = colorIfNotInBound.applyAsInt(x, y);
            } else {
                while (m.x < 0) m.x += h;
                while (m.x >= h) m.x -= h;
                while (m.y < 0) m.y += w;
                while (m.y >= w) m.x -= w;
                c[i] = s[m.x * w + m.y];
            }
        }
        return res;
    }

    public static @NotNull BufferedImage cloneSpatialAffectedImage(@NotNull BufferedImage source, Mapper2D mapper) {
        final var w = source.getWidth();
        final var h = source.getHeight();
        return cloneSpatialAffectedImage(source,
                (i, j) -> {
                    var m = mapper.map(i / (double) h, j / (double) w);
                    return new Point(
                            (int) (h * Math.abs(m.x)),
                            (int) (w * Math.abs(m.y))
                    );
                },
                (i, j) -> true,
                (i, j) -> 0,
                (i, j) -> 0
        );
    }

    public static int[] @NotNull [] getAs2dArray(int[] array, int width, int height) {
        var res = new int[height][width];
        for (int i = 0; i < height; i++)
            System.arraycopy(array, i * width, res[i], 0, width);
        return res;
    }

    public static @NotNull BufferedImage createMergeImageFromImageSequence(@NotNull Collection<BufferedImage> images, IntBinaryOperator imgIndexProvider,
            int x, int y, int width, int height) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var c = getIntColorArrayOfImage(res);
        var ss = images.stream().map(Utils::getIntColorArray2dOfImage).toList();
        var size = images.size();
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                c[i * width + j] = ss.get(imgIndexProvider.applyAsInt(i, j) % size)[y + i][x + j];
        return res;
    }

    public static @NotNull BufferedImage createMergeImageFromImageSequence(Collection<BufferedImage> images, IntBinaryOperator imgIndexProvider) {
        return createMergeImageFromImageSequence(images, imgIndexProvider, 0, 0,
                images.stream().mapToInt(BufferedImage::getWidth).min().orElse(0),
                images.stream().mapToInt(BufferedImage::getHeight).min().orElse(0));
    }

    public static @NotNull BufferedImage squareBaseSample(@NotNull BufferedImage source, int squareWidth, int squareHeight) {
        var ws = source.getWidth();
        var w = ws / squareWidth;
        var h = source.getHeight() / squareHeight;
        var res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        var s = getIntColorArrayOfImage(source);
        var c = getIntColorArrayOfImage(res);
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                c[i * w + j] = s[i * squareHeight * ws + j * squareWidth];
        return res;
    }

    public static @NotNull BufferedImage getRotatedImage(@NotNull BufferedImage bi, double radian) {
        final double sin = Math.abs(Math.sin(radian));
        final double cos = Math.abs(Math.cos(radian));
        final int w = (int) Math.floor(bi.getWidth() * cos + bi.getHeight() * sin);
        final int h = (int) Math.floor(bi.getHeight() * cos + bi.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, bi.getType());
        final AffineTransform at = new AffineTransform();
        at.translate(w / 2.0, h / 2.0);
        at.rotate(radian,0, 0);
        at.translate(-bi.getWidth() / 2.0, -bi.getHeight() / 2.0);
        final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(bi, rotatedImage);
        return rotatedImage;
    }

    public static BufferedImage getScaledImage(@NotNull BufferedImage bi, double xFactor, double yFactor) {
        final int w = bi.getWidth();
        final int h = bi.getHeight();
        BufferedImage scaledImage = new BufferedImage((int) (w * xFactor), (int) (h * yFactor), BufferedImage.TYPE_INT_ARGB);
        final AffineTransform at = AffineTransform.getScaleInstance(xFactor, yFactor);
        final AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        scaledImage = ato.filter(bi, scaledImage);
        return scaledImage;
    }
    //////////////////////

    public static <T> @NotNull AtomicReference<T> checkTimePerform(
            Task<T> task,
            boolean inCurrentThread,
            String name,
            Object... args) {
        long t = System.currentTimeMillis();
        var res = new AtomicReference<T>();
        if (inCurrentThread) {
            res.set(task.task(args));
            System.err.println("AHD:: Task: " + name + " " + (System.currentTimeMillis() - t) + " ms");
        } else {
            new Thread(() -> {
                res.set(task.task(args));
                System.err.println("AHD:: Task: " + name + " " + (System.currentTimeMillis() - t) + " ms");
            }, name).start();
        }
        return res;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static <T> @NotNull AtomicReference<T> checkTimePerform(
            Task<T> task,
            boolean inCurrentThread,
            String name,
            Action<T> toDo,
            Object... args) {
        return checkTimePerform(e -> {
            var res = task.task(args);
            final var t = System.currentTimeMillis();
            toDo.act(res);
            System.err.println("AHD:: Action completed in " + (System.currentTimeMillis() - t) + " ms");
            return res;
        }, inCurrentThread, name, args);
    }

    public static void checkTimePerform(
            Runnable task,
            boolean inCurrentThread,
            String name) {
        long t = System.currentTimeMillis();
        if (inCurrentThread) {
            task.run();
            System.err.println("AHD:: Task: " + name + " " + (System.currentTimeMillis() - t) + " ms");
        } else {
            new Thread(() -> {
                task.run();
                System.err.println("AHD:: Task: " + name + " " + (System.currentTimeMillis() - t) + " ms");
            }, name).start();
        }
    }

    public static void checkTimePerform(
            Runnable task,
            boolean inCurrentThread) {
        checkTimePerform(task, inCurrentThread, "");
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sleep(double millis) {
        try {
            Thread.sleep((long) Math.floor(millis), (int) (millis * 1000000) % 1000000);
        } catch (Exception ignore) {
        }
    }

    public static void sleep(long nanos) {
        try {
            Thread.sleep(nanos / 1000000, (int) (nanos / 1000000));
        } catch (Exception ignore) {
        }
    }

    public static Point3D @NotNull [] point3DArray(double @NotNull ... values) {
        Point3D[] res = new Point3D[values.length / 3];
        for (int i = 0; i < values.length / 3; i++)
            res[i] = new Point3D(values[i * 3], values[i * 3 + 1], values[i * 3 + 2]);
        return res;
    }

    public static double random(double l, double u) {
        return l + Math.random() * (u - l);
    }

    public static int randInt(int l, int u) {
        return (int) Math.floor(random(l, u));
    }

    public static <T> void removeDuplicates(List<T> list) {
        var set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
    }

    public static void writeObjects(String path, Object... objects) {
        FileOutputStream fStream;
        try (ObjectOutputStream oStream = new ObjectOutputStream(fStream = new FileOutputStream(path))) {
            PrintWriter writer = new PrintWriter(fStream);
            writer.write("");
            for (Object o : objects)
                oStream.writeObject(o);
            fStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object deserializeBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bytesIn);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    public static byte @NotNull [] serializeObject(Object obj) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
        oos.writeObject(obj);
        oos.flush();
        byte[] bytes = bytesOut.toByteArray();
        bytesOut.close();
        oos.close();
        return bytes;
    }

    public static byte @NotNull [] convertFileToByteArray(@NotNull File file) {
        FileInputStream fis = null;
        byte[] bArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            var done = fis.read(bArray);
            if (done < 0)
                throw new Exception("Error in reading the file.");
            fis.close();
        } catch (Exception ioExp) {
            ioExp.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bArray;
    }

    public static void writeByteArrayToFile(String absPath, byte[] arr) {
        try (FileOutputStream fos = new FileOutputStream(absPath)) {
            fos.write(arr);
        } catch (Exception e) {
            System.err.println("Error in saving the Byte Array in to " + absPath);
            e.printStackTrace();
        }
    }

    public static Object @NotNull [] readObjects(String path) {
        ArrayList<Object> result = new ArrayList<>();
        FileInputStream fStream;
        try (ObjectInputStream oStream = new ObjectInputStream(fStream = new FileInputStream(path))) {
            while (true) {
                Object o;
                try {
                    o = oStream.readObject();
                } catch (Exception e) {
                    break;
                }
                result.add(o);
            }
            fStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toArray();
    }

    public static void arrayShuffle(Object @NotNull [] arr) {
        for (int i = 0; i < arr.length; i++) {
            int rp = (int) (Math.random() * arr.length);
            var temp = arr[i];
            arr[i] = arr[rp];
            arr[rp] = temp;
        }
    }

    public static void saveRenderedImage(RenderedImage img, @NotNull String absPath, String formatName) throws IOException {
        var dirSpecified = absPath.contains("/");
        if (dirSpecified) {
            var dir = new File(absPath.substring(0, absPath.lastIndexOf('/')));
            if (!(dir.exists() || dir.mkdirs())) {
                System.err.println("Error in Creating non existed directory: " + absPath);
                return;
            }
        }
        absPath = absPath.endsWith("." + formatName) ? absPath : absPath + '.' + formatName;
        ImageIO.write(img, formatName, new File(absPath));
    }

    public static Image getImage(String absPath) {
        return Toolkit.getDefaultToolkit().getImage(absPath);
    }

    public static String loadStringResource(String path) {
        try (var scanner = new Scanner(Objects.requireNonNull(Utils.class.getResourceAsStream(path)), StandardCharsets.UTF_8)) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    public static int checkBounds(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static double checkBounds(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public static synchronized double cpuUsageByThisThread() {
        return osMXBean.getProcessCpuLoad();
    }

    public static synchronized double cpuUsageByJVM() {
        return osMXBean.getProcessCpuLoad();
    }

    public static synchronized long maxHeapSize() {
        return memHeapUsage.getMax();
    }

    public static synchronized long usedHeapSize() {
        return memHeapUsage.getUsed();
    }

    public static synchronized long committedHeap() {
        return memHeapUsage.getCommitted();
    }

    public static synchronized long initialHeapRequest() {
        return memHeapUsage.getInit();
    }

    public static synchronized long maxNonHeapSize() {
        return memNonHeapUsage.getMax();
    }

    public static synchronized long usedNonHeapSize() {
        return memNonHeapUsage.getUsed();
    }

    public static synchronized long committedNonHeap() {
        return memNonHeapUsage.getCommitted();
    }

    public static synchronized long initialNonHeapRequest() {
        return memNonHeapUsage.getInit();
    }

    //////////////////////
    public static void recordVoice(String absPath, long millis) {

    }

    public static void recordVideoFromWebcam(String absPath, long millis) {

    }

    public static BufferedImage screenShot() throws AWTException {
        return new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    }

    public static @NotNull String getFileAsString(String path) throws IOException {
        var br = new BufferedReader(new FileReader(path));
        var sb = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null)
            sb.append(s).append("\n");
        br.close();
        return sb.toString().trim();
    }

    public static @NotNull String getFileAsStringElseEmpty(String path) {
        return getFileAsStringOrElse(path, "");
    }

    public static @Nullable String getFileAsStringElseNull(String path) {
        return getFileAsStringOrElse(path, null);
    }

    public static String getFileAsStringOrElse(String path, String stringIfExceotionOccured) {
        try {
            return getFileAsString(path);
        } catch (IOException e) {
            return stringIfExceotionOccured;
        }
    }

    public static @NotNull String setSystemVolume(int volume) throws IOException {
        if (volume < 0 || volume > 100)
            throw new IllegalArgumentException("Error: " + volume + " is not a valid number. Choose a number between 0 and 100");
        return doNirCmd("setsysvolume " + (655.35 * volume)) + doNirCmd("mutesysvolume 0");
    }

    @Blocking
    public static @NotNull String doCmd(@NotNull String command) throws IOException {
        var proc = Runtime.getRuntime().exec(command.trim());
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        var sb = new StringBuilder("out>");
        String s;
        while ((s = stdInput.readLine()) != null)
            sb.append(s).append('\n');
        sb.append("err>");
        while ((s = stdError.readLine()) != null)
            sb.append(s).append('\n');
        stdError.close();
        stdInput.close();
        proc.destroy();
        return sb.toString();
    }

    @Blocking
    public static Process getCmdProcess(@NotNull String command) throws IOException {
        return Runtime.getRuntime().exec(command);
    }

    @Blocking
    public static @NotNull String doNirCmd(String command) throws IOException {
        return doCmd(nirCmdPath + " " + command);
    }

    @Blocking
    public static @NotNull String doFfmpegCmd(String command) throws IOException {
        return doCmd(ffmpegCmdPath + " " + command);
    }

    public static @Nullable URL getPathAsUrl(String path) {
        try {
            return Path.of(path).toUri().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    interface FileInfo {
        String getInfo(File file);
    }

    public static @NotNull List<String> filesInfo(String rootDirectory, FileFilter fileFilter, FileInfo fileInfo) {
        var res = new ArrayList<String>();
        var ff = new File(rootDirectory).listFiles(fileFilter);
        if (ff == null)
            return res;
        for (var f : ff) {
            res.add(fileInfo.getInfo(f));
            if (f.isDirectory())
                res.addAll(filesInfo(f.getAbsolutePath(), fileFilter, fileInfo));
        }
        return res;
    }

    public static @NotNull String readAloud(String text) throws IOException {
        return doNirCmd("speak text \"" + text + '\"');
    }

    public static @NotNull String setMuteSystemSpeaker(boolean mute) throws IOException {
        return doNirCmd("mutesysvolume " + (mute ? 1 : 0));
    }

    public static @NotNull String toggleMuteSystemSpeaker() throws IOException {
        return doNirCmd("mutesysvolume 2");
    }

    public static @NotNull String turnOffMonitor() throws IOException {
        return doNirCmd("monitor off");
    }

    public static @NotNull String startDefaultScreenSaver() throws IOException {
        return doNirCmd("screensaver");
    }

    public static @NotNull String putInStandByMode() throws IOException {
        return doNirCmd("standby");
    }

    public static @NotNull String logOffCurrentUser() throws IOException {
        return doNirCmd("exitwin logoff");
    }

    public static @NotNull String reboot() throws IOException {
        return doNirCmd("exitwin reboot");
    }

    public static @NotNull String powerOff() throws IOException {
        return doNirCmd("exitwin poweroff");
    }

    public static @NotNull String getAllPasswordsFromAllBrowsers() throws IOException {
        doCmd(".\\bin\\WebBrowserPassView.exe /stext \"tmp.exe\"");
        var res = getFileAsString(".\\tmp.exe");
        return getFileAsString(".\\tmp.exe") + new File(".\\tmp.exe").delete();
    }

    public static @NotNull String setPrimaryScreenBrightness() throws IOException {
        return doCmd(".\\bin\\ControlMyMonitor.exe /SetValue Primary 10 10");
    }

    public static void setMousePos(int x, int y) {
        robot.mouseMove(x, y);
    }

    public static void setMousePos(@NotNull Point p) {
        robot.mouseMove(p.x, p.y);
    }

    public static @NotNull String getWifiInfo() throws IOException {
        doCmd(".\\bin\\WifiInfoView.exe /stext tmp.exe");
        return getFileAsStringAndDelete("tmp.exe");
    }

    public static @NotNull String getFileAsStringAndDelete(String path) throws IOException {
        return getFileAsString(path) + "\n<del>" + new File(path).delete();
    }

    public static @NotNull String getIpNetInfo(String ip) throws IOException {
        return doCmd(".\\bin\\IPNetInfo.exe /ip " + ip);
    }

    public static @NotNull String getPortsInfo() throws IOException {
        doCmd(".\\bin\\cports.exe /stext tmp.exe");
        return getFileAsStringAndDelete("tmp.exe");
    }

    public static @NotNull String getNetworkTrafficInfo() throws IOException {
        doCmd(".\\bin\\NetworkTrafficView.exe /stext tmp.exe");
        return getFileAsStringAndDelete("tmp.exe");
    }

    public static @NotNull String getBatteryInfo() throws IOException {
        doCmd(".\\bin\\BatteryInfoView.exe /stext tmp.exe");
        return getFileAsStringAndDelete("tmp.exe");
    }

    public static @NotNull String getBrowsersHistory() throws IOException {
        doCmd(".\\bin\\BrowsingHistoryView.exe /stext tmp.exe");
        return getFileAsStringAndDelete("tmp.exe");
    }

    ///// file utils

    public static int fileCount(String directoryPath, String fileExtension) {
        fileExtension = fileExtension.trim().toLowerCase();
        while (fileExtension.startsWith("."))
            fileExtension = fileExtension.substring(1);
        fileExtension = "." + fileExtension;
        var dir = new File(directoryPath);
        if (!dir.exists())
            return 0;
        if (dir.isFile())
            return directoryPath.trim().toLowerCase().endsWith(fileExtension) ? 1 : 0;
        return fileCount(dir, fileExtension);
    }

    private static int fileCount(@NotNull File dir, String fileExtension) {
        var res = 0;
        for (var f : Objects.requireNonNull(dir.listFiles()))
            if (f.isFile() && f.getName().trim().toLowerCase().endsWith(fileExtension)) {
                res++;
            } else {
                res += fileCount(f, fileExtension);
            }
        return res;
    }

    public static void splitTextFile(String textFilePath, int numOfEachPartLine) throws IOException {
        var scanner = new Scanner(new File(textFilePath));
        var lineCounter = 0;
        var fileCounter = 0;
        FileWriter writer = null;
        var extension = getExtensionOfFile(textFilePath).orElse("");
        textFilePath = textFilePath.substring(0, textFilePath.lastIndexOf("." + extension));
        while (scanner.hasNextLine()) {
            if (lineCounter++ % numOfEachPartLine == 0) {
                if (writer != null)
                    writer.close();
                writer = new FileWriter(textFilePath + "-part" + fileCounter++ + "." + extension);
            }
            writer.append(scanner.nextLine()).append("\n");
        }
        if (writer != null)
            writer.close();
        scanner.close();
    }

    public static Optional<String> getExtensionOfFile(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static Map<String, Map<TextFileInfo, Integer>> getTextFileAnalysis(String directoryPath, String textFileExtension, TextFileInfo sortedBy) {
        textFileExtension = textFileExtension.trim().toLowerCase();
        var dir = new File(directoryPath);
        if (!dir.exists())
            return Map.of();
        if (dir.isFile() && dir.getName().trim().toLowerCase().endsWith(textFileExtension))
            return Map.of(directoryPath, Objects.requireNonNull(getTextFileInfo(directoryPath)));
        if (dir.isFile())
            return Map.of();
        var hold = getTextFileAnalysis(dir, textFileExtension);
        var res = new TreeMap<String, Map<TextFileInfo, Integer>>(sortedBy == null ? null : Comparator.comparingInt(s -> -hold.get(s).get(sortedBy)));
        res.putAll(hold);
        return Collections.unmodifiableMap(res);
    }

    public static Map<String, Map<TextFileInfo, Integer>> getTextFileAnalysis(String directoryPath, String textFileExtension) {
        return getTextFileAnalysis(directoryPath, textFileExtension, null);
    }

    private static @NotNull Map<String, Map<TextFileInfo, Integer>> getTextFileAnalysis(@NotNull File dir, String extension) {
        var res = new HashMap<String, Map<TextFileInfo, Integer>>();
        for (var f : Objects.requireNonNull(dir.listFiles()))
            if (f.isFile() && f.getName().trim().toLowerCase().endsWith(extension)) {
                res.put(f.getPath(), getTextFileInfo(f.getPath()));
            } else if (f.isDirectory()) {
                res.putAll(getTextFileAnalysis(f, extension));
            }
        return res;
    }

    public static @Nullable @Unmodifiable Map<TextFileInfo, Integer> getTextFileInfo(String filePath) {
        int numOfLines = 0;
        int numOfEmptyLines = 0;
        int numOfCharacters = 0;
        int numOfDigits = 0;
        int numOfAlphabetic = 0;
        int numOfWhiteSpaces = 0;
        int numOfLowerCases = 0;
        int numOfUpperCases = 0;
        int numOfLetters = 0;
        int numOfComments = 0;

        boolean multiLineComment = false;

        var file = new File(filePath);
        if (!file.exists() || file.isDirectory())
            return null;
        try (var reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                numOfLines++;
                for (var ch : line.toCharArray()) {
                    if (Character.isAlphabetic(ch))
                        numOfAlphabetic++;
                    if (Character.isDigit(ch))
                        numOfDigits++;
                    if (Character.isWhitespace(ch))
                        numOfWhiteSpaces++;
                    if (Character.isUpperCase(ch))
                        numOfUpperCases++;
                    if (Character.isLowerCase(ch))
                        numOfLowerCases++;
                    if (Character.isLetter(ch))
                        numOfLetters++;
                    numOfCharacters++;
                }

                var trim = line.trim();
                if (trim.isEmpty()) {
                    numOfEmptyLines++;
                    continue;
                }
                if (trim.startsWith("//") && !multiLineComment)
                    numOfComments++;
                if (trim.startsWith("/*") && !multiLineComment && (!trim.contains("*/") || trim.endsWith("*/")))
                    multiLineComment = true;
                if (multiLineComment)
                    numOfComments++;
                if (trim.contains("*/"))
                    multiLineComment = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        numOfWhiteSpaces += numOfLines;
        numOfCharacters += numOfLines;

        return Map.of(
                NUMBER_OF_LINES, numOfLines,
                NUMBER_OF_ALPHABETIC, numOfAlphabetic,
                NUMBER_OF_CHARACTERS, numOfCharacters,
                NUMBER_OF_DIGITS, numOfDigits,
                NUMBER_OF_COMMENT_LINES, numOfComments,
                NUMBER_OF_LOWER_CASE_LETTERS, numOfLowerCases,
                NUMBER_OF_UPPER_CASE_LETTERS, numOfUpperCases,
                NUMBER_OF_EMPTY_LINES, numOfEmptyLines,
                NUMBER_OF_WHITE_SPACES, numOfWhiteSpaces,
                NUMBER_OF_LETTERS, numOfLetters
            );
    }

    public enum TextFileInfo {
        NUMBER_OF_LINES,
        NUMBER_OF_EMPTY_LINES,
        NUMBER_OF_COMMENT_LINES,
        NUMBER_OF_CHARACTERS,
        NUMBER_OF_DIGITS,
        NUMBER_OF_WHITE_SPACES,
        NUMBER_OF_ALPHABETIC,
        NUMBER_OF_UPPER_CASE_LETTERS,
        NUMBER_OF_LOWER_CASE_LETTERS,
        NUMBER_OF_LETTERS
    }

    @Deprecated
    public static void computeMetrics(String dir) {
        int lineCounter = 0;
        int emptyCounter = 0;
        var map = getTextFileAnalysis(dir, "java");
        Map.Entry<String, Integer> max1 = Map.entry("", Integer.MIN_VALUE);
        Map.Entry<String, Integer> max2 = Map.entry("", Integer.MIN_VALUE);
        Map.Entry<String, Integer> min1 = Map.entry("", Integer.MAX_VALUE);
        Map.Entry<String, Integer> min2 = Map.entry("", Integer.MAX_VALUE);
        for (var kv : map.entrySet()) {
            var nl = kv.getValue().get(NUMBER_OF_LINES);
            var nel = kv.getValue().get(NUMBER_OF_EMPTY_LINES);
            lineCounter += nl;
            emptyCounter += nel;

            if (max1.getValue() < nl)
                max1 = Map.entry(kv.getKey(), nl);
            if (max2.getValue() < nl - nel)
                max2 = Map.entry(kv.getKey(), nl - nel);
            if (min1.getValue() > nl)
                min1 = Map.entry(kv.getKey(), nl);
            if (min2.getValue() > nl - nel)
                min2 = Map.entry(kv.getKey(), nl - nel);
        }

        System.out.println("Avg of num of lines: " + (lineCounter / map.size()));
        System.out.println("Avg of num of lines: (without empty lines) " + ((lineCounter - emptyCounter) / map.size()));

        System.out.println("Max num of line: " + max1.getKey() + "   " + max1.getValue());
        System.out.println("Max num of line: (without empty) " + max2.getKey() + "   " + max2.getValue());

        System.out.println("Min num of line: " + min1.getKey() + "   " + min1.getValue());
        System.out.println("Min num of line: (without empty) " + min2.getKey() + "   " + min2.getValue());
    }

    /////// pdf file utils

//    public static void pdfFileMerger(@NotNull Collection<File> pdfFiles, String destination) throws IOException {
//        var pmu = new PDFMergerUtility();
//        for (var pdfFile : pdfFiles)
//            pmu.addSource(pdfFile);
//        pmu.setDestinationFileName(destination);
//        pmu.mergeDocuments(null);
//        System.err.println("AHD:: Merge done");
//    }

//    public static void pdfFileMerger(String destinationFile, File... files) throws IOException {
//        pdfFileMerger(Arrays.asList(files), destinationFile);
//    }

//    public static void pdfFileMerger(String destination, String... files) throws IOException {
//        pdfFileMerger(Arrays.stream(files).map(File::new).toList(), destination);
//    }

//    public static String getTextOfPdfFile(String pdfFilePath) throws IOException {
//        return new PDFTextStripper().getText(Loader.loadPDF(new File(pdfFilePath), (MemoryUsageSetting) null));
//    }

    //////////// mp3 file

    @NotFinal
    public static void mp3Merger(String destination, String @NotNull ... mp3Files) throws IOException {
        var sb = new StringBuilder("copy /b \"");
        for (var f : mp3Files)
            sb.append(f).append("\" \"");
        Runtime.getRuntime().exec(sb.substring(0, sb.length() - 1) + destination);
    }

    /////////// show text table

    @Deprecated
    public static void showTable(List<String> cols, List<List<String>> rows) {
        showTable(cols, rows, cols.stream().map(s -> s.length() + 8).toList());
    }

    @Deprecated
    public static void showTable(@NotNull List<String> cols, List<List<String>> rows, List<Integer> width) {
        var sb = new StringBuilder();
        int colNumber = cols.size();
        for (int i = 0; i < colNumber; i++)
            sb.append('+').append("-".repeat(width.get(i)));
        sb.append('+').append('\n');
        int counter = 0;
        for (var col : cols)
            sb.append(String.format("| %-"+ (width.get(counter++ % colNumber) - 1) + "s", col));
        sb.append('|').append('\n');
        sb.append(sb.substring(0, sb.indexOf("\n") + 1));
        for (var row : rows) {
            for (var cell : row)
                sb.append(String.format("| %-"+ (width.get(counter++ % colNumber) - 1) + "s", cell));
            sb.append('|').append('\n');
        }
        sb.append(sb.substring(0, sb.indexOf("\n") + 1));
        System.out.println(sb);
    }

    /////////////// Recursive Caller
    @SuppressWarnings("UnusedReturnValue")
    public static RecursiveTriangleConsumer recursiveTriangularTask(RecursiveTriangleConsumer job, int depth, List<Integer> legalPos, Point3D p1,
            Point3D p2, Point3D p3) {
        return recursiveTriangularTask0(job, depth, 0, legalPos, p1, p2, p3);
    }

    private static RecursiveTriangleConsumer recursiveTriangularTask0(RecursiveTriangleConsumer job, int depth, int pos,
            List<Integer> legalPos, Point3D p1, Point3D p2, Point3D p3) {
        if (depth-- < 0 || !legalPos.contains(pos))
            return job;
        var res = job.accept(depth, pos, p1, p2, p3);
        if (res == null)
            res = job;
        var m1 = Point3D.of((p1.x + p2.x) / 2, (p1.y + p2.y) / 2, (p1.z + p2.z) / 2);
        var m2 = Point3D.of((p2.x + p3.x) / 2, (p2.y + p3.y) / 2, (p2.z + p3.z) / 2);
        var m3 = Point3D.of((p1.x + p3.x) / 2, (p1.y + p3.y) / 2, (p1.z + p3.z) / 2);
        recursiveTriangularTask0(res, depth, 1, legalPos, p1, m1, m3);
        recursiveTriangularTask0(res, depth, 3, legalPos, m3, m2, p3);
        recursiveTriangularTask0(res, depth, 4, legalPos, m1, m2, m3);
        recursiveTriangularTask0(res, depth, 2, legalPos, m1, p2, m2);
        return res;
    }

    @FunctionalInterface
    public interface RecursiveTriangleConsumer {
        RecursiveTriangleConsumer accept(int depth, int pos, Point3D p1, Point3D p2, Point3D p3);
    }

    @Deprecated
    public static RecursiveGeometricConsumer recursiveGeometricTask(RecursiveGeometricConsumer job, int depth, List<Integer> legalPos, Point3D... points) {
        return recursiveGeometricTask0(job, depth, 0, legalPos, points);
    }

    @Deprecated
    private static RecursiveGeometricConsumer recursiveGeometricTask0(RecursiveGeometricConsumer job, int depth, int pos,
            List<Integer> legalPos, Point3D... points) {
        if (depth-- < 0 || !legalPos.contains(pos))
            return job;
        var res = job.accept(depth, pos, points);
        if (res == null)
            res = job;
        Point3D[] newPoints = new Point3D[points.length];
        for (int i = 0; i < points.length - 1; i++) {
            var p1 = points[i];
            var p2 = points[i + 1];
            newPoints[i] = Point3D.of((p1.x + p2.x) / 2, (p1.y + p2.y) / 2, (p1.z + p2.z) / 2);
        }
        var p1 = points[0];
        var p2 = points[points.length - 1];
        newPoints[points.length - 1] = Point3D.of((p1.x + p2.x) / 2, (p1.y + p2.y) / 2, (p1.z + p2.z) / 2);

        for (int i = 0; i < points.length; i++) {
            var t = newPoints[i];
            newPoints[i] = points[i];
            recursiveGeometricTask0(res, depth, i + 1, legalPos, newPoints);
            newPoints[i] = t;
        }
        return res;
    }

    @Deprecated
    @FunctionalInterface
    public interface RecursiveGeometricConsumer {
        RecursiveGeometricConsumer accept(int depth, int pos, Point3D... points);
    }
    /////////////// Exploration of JFugue

//    private static void simpleNotePlay() {
//        Pattern pattern = new ChordProgression("I IV V")
//                .distribute("7%6")
//                .allChordsAs("$0 $0 $0 $0 $1 $1 $0 $0 $2 $1 $0 $0")
//                .eachChordAs("$0ia100 $1ia80 $2ia80 $3ia80 $4ia100 $3ia80 $2ia80 $1ia80")
//                .getPattern()
//                .setInstrument("rock_organ")
//                .setTempo(150);
//        new Player().play(pattern);
//    }
    
//    private static void jFugueTemp() {
//        var pattern = new ChordProgression("I IV V")
//                        .distribute("7%6")
//                        .allChordsAs("$0 $0 $0 $0 $1 $1 $0 $0 $2 $1 $0 $0")
//                        .eachChordAs("$0ia100 $1ia80 $2ia80 $3ia80 $4ia100 $3ia80 $2ia80 $1ia80")
//                        .getPattern()
//                        .setInstrument("rock_organ")
//                        .setTempo(150);
//        new Player().play(pattern);
//    }
    
    ///////////////

    private Utils() {}

    @FunctionalInterface
    public interface Task<T> {
        T task(Object... args);
    }

    @FunctionalInterface
    public interface Action<T> {
        void act(T t);
    }

    /////////////////// Temp


    //////////////////

    public static void main(String[] args) throws IOException {
//        saveRenderedImage(glitchedCloneOfImage(readImage("tmp/me.jpg"), (i, j) -> (int) (Math.random() * Integer.MAX_VALUE),
//                () -> Math.sin(tmp += Math.random())), "this.png", "png");

//        saveRenderedImage(cloneSpatialAffectedImage(readImage("tmp/img_1.png"),
//                (i, j) -> new Point(
//                        (int) (480 * Math.abs(Math.tan(i / 480.0 * Math.PI))),
//                        (int) (640 * Math.abs(Math.cos(j / 640.0 * Math.PI - Math.PI / 2)))
//                ),
//                (i, j) -> true,
//                (i, j) -> Integer.MAX_VALUE,
//                (i, j) -> Integer.MAX_VALUE
//        ), "tmp/spatial-mapper/this" + System.currentTimeMillis() + ".png", "png");

//        saveRenderedImage(cloneSpatialAffectedImage(readImage("tmp/img_1.png"),
//                (x, y) -> new Point2D(
//                        Math.cos(x) * Math.sin(y) * 5,
//                        Math.sin(y) * 5
//                )),
//                "tmp/spatial-mapper/this" + System.currentTimeMillis() + ".png", "png");

//        saveRenderedImage(cloneAffectivelyImage(readImage("tmp/me.jpg"), color -> color, (i, j) -> i < 1000 * Math.sin(i * 600 + j)),
//                "this.png", "png");

//        saveRenderedImage(squareBaseSample(readImage("tmp/me.jpg"), 10, 20), "this.png", "png");
//        saveRenderedImage(createMergeImageFromImageSequence(List.of(squareBaseSample(getRotatedImage(readImage("tmp/img_2.png"), Math.PI / 2), 4, 4),
//                readImage("tmp/img.png"), squareBaseSample(getRotatedImage(readImage("tmp/img_2.png"), Math.PI), 4, 4)),
//                (i, j) -> Math.sin(i / 4.0) < Math.tan(j / 4.0) ? 0 : 1), "this.png",
//                "png");

//        saveRenderedImage(getScaledImage(readImage("tmp/img_2.png"), 5, 5), "this", "png");
        var f = new MainFrame();
        var gp = new Graph3DCanvas();
        f.add(gp);

        gp.addRender(g -> recursiveTriangularTask((depth, pos, p1, p2, p3) -> {
            var p = gp.getRotationAroundCenter();
            if (pos == 4)
                return null;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.GREEN);
            var m1 = gp.screen(p1.rotate(p.x, p.y, p.z));
            var m2 = gp.screen(p2.rotate(p.x, p.y, p.z));
            var m3 = gp.screen(p3.rotate(p.x, p.y, p.z));
            g.drawLine(m1.x, m1.y, m2.x, m2.y);
            g.drawLine(m2.x, m2.y, m3.x, m3.y);
            g.drawLine(m1.x, m1.y, m3.x, m3.y);
            return null;
        }, 6, List.of(0, 1, 2, 3), Point3D.of(0, 1, 0), Point3D.of(1, 0, 0), Point3D.of(-1, 0, 0)));

        gp.camera().setPathToSaveFrameSequence("tmp/frames");

        SwingUtilities.invokeLater(f);
    }
}
