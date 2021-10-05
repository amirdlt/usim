package ahd.ulib.visualization.canvas;

import ahd.ulib.jmath.datatypes.functions.Arc2D;
import ahd.ulib.jmath.datatypes.functions.Function;
import ahd.ulib.jmath.datatypes.functions.Function2D;
import ahd.ulib.jmath.datatypes.functions.Function3D;
import ahd.ulib.jmath.datatypes.tuples.Point2D;
import ahd.ulib.jmath.functions.utils.FunctionAnalyser;
import ahd.ulib.jmath.functions.utils.RootsFinder;
import ahd.ulib.jmath.functions.utils.Sampling;
import ahd.ulib.jmath.parser.Function4DParser;
import ahd.ulib.utils.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class Graph2DCanvas extends CoordinatedCanvas {

    // point set properties
    public static final int POINT_RADIUS = 7; // double
    public static final int FILLED_OVAL = 8; // boolean

    // common properties
    public static final int NAME = 1; // String
    public static final int COLOR = 0; // Color
    public static final int IS_VISIBLE = 5; // boolean
    public static final int THICKNESS = 10; // float the stroke of the g2d

    // function properties
    public static final int RADIUS_FUNC = 16; // Function2D only in advanced plotting
    public static final int SHOW_ROOTS = 19; // boolean
    public static final int SHOW_STATIONARY_POINTS = 20; // boolean
    public static final int NUM_OF_THREADS = 21; // int
    public static final int ACCURACY_RATE = 11; // double determine the delta of sampling

    // arcs and functions
    public static final int LOW_BOUND = 22; // double
    public static final int UP_BOUND = 23; // double
    public static final int ALL_POINTS = 3; // List of Point2D
    public static final int TYPICAL_PLOT = 17; // boolean
    public static final int DOT_PLOT = 9; // boolean
    public static final int ADVANCED_PLOT = 14; // boolean
    public static final int COLOR_FUNC = 15; // Function2D only in advanced plotting mode
    public static final int BEFORE_PARSE = 25; // Arc or Function before being parsed By Function4DParser
    public static final int OBJECT_ID = 26; // A Random double number between 0 and 1 as an ID

    // arcs only
    public static final int DELTA = 24;

    // DEFAULTS
    public static final HashMap<Integer, Object> DEFAULT_PROPERTIES;

    static {
        DEFAULT_PROPERTIES = new HashMap<>() {{
            put(COLOR, Color.BLUE);
            put(IS_VISIBLE, true);
            put(COLOR_FUNC, (Function2D) x -> Math.random());
            put(RADIUS_FUNC, (Function2D) x -> x / 2);
            put(ACCURACY_RATE, 2D);
            put(FILLED_OVAL, false);
            put(SHOW_ROOTS, false);
            put(SHOW_STATIONARY_POINTS, false);
            put(THICKNESS, 1.5f);
            put(NUM_OF_THREADS, 10);
            put(LOW_BOUND, Double.NEGATIVE_INFINITY);
            put(UP_BOUND, Double.POSITIVE_INFINITY);
            put(TYPICAL_PLOT, true);
            put(DOT_PLOT, false);
            put(ADVANCED_PLOT, false);
            put(POINT_RADIUS, 0.1);
            put(BEFORE_PARSE, "Un Available");
        }};
    }

    // collections to display
    private final HashMap<Function2D, HashMap<Integer, Object>> functions;
    private final HashMap<Set<Point2D>, HashMap<Integer, Object>> pointSets;
    private final HashMap<Arc2D, HashMap<Integer, Object>> arcs;
    protected final HashMap<String, Function<?, ?>> stringBaseMap;

    public Graph2DCanvas(boolean allowChangeLayout) {
        super(allowChangeLayout, true, true);
        functions = new HashMap<>();
        pointSets = new HashMap<>();
        arcs = new HashMap<>();
        stringBaseMap = new HashMap<>();
        addRender(this::typicalPlotting, this::showPointSets, this::advancedPlotting, this::showRoots, this::showStationaryPoints);
        setShowGrid(true);
    }

    public Graph2DCanvas() {
        this(false);
    }

    // Major Function
    public void addArcToDraw(Arc2D arc, double l, double u, double delta, Color... color) {
        HashMap<Integer, Object> properties = new HashMap<>();

        properties.put(COLOR, color == null || color.length != 1 ? (isDark ? LIGHT : DARK) : color[0]);
        properties.put(IS_VISIBLE, true);
        properties.put(COLOR_FUNC, DEFAULT_PROPERTIES.get(COLOR_FUNC));
        properties.put(RADIUS_FUNC, (Function2D) x -> x);
        properties.put(DELTA, delta);
        properties.put(FILLED_OVAL, false);
        properties.put(SHOW_ROOTS, false);
        properties.put(SHOW_STATIONARY_POINTS, false);
        properties.put(THICKNESS, 1.5f);
        properties.put(NUM_OF_THREADS, 10);
        properties.put(LOW_BOUND, l);
        properties.put(UP_BOUND, u);
        properties.put(BEFORE_PARSE, "Un Available");
        properties.put(OBJECT_ID, Math.random());

        properties.put(TYPICAL_PLOT, true);
        properties.put(DOT_PLOT, false);
        properties.put(ADVANCED_PLOT, false);

        this.arcs.put(arc, properties);
        repaint();
    }

    public void addFunctionToDraw(Color[] colors, Function2D... functions) {
        int counter = 0;
        for (var f : functions)
            addFunctionToDraw(f,
                    colors == null || colors.length - counter == 0 ? (isDark ? LIGHT : DARK) : colors[counter++]);
        repaint();
    }

    // Major Function
    public void addFunctionToDraw(Function2D f, Color... color) {
        HashMap<Integer, Object> properties = new HashMap<>();

        properties.put(COLOR, color == null || color.length != 1 ? (isDark ? LIGHT : DARK) : color[0]);
        properties.put(IS_VISIBLE, true);
        properties.put(COLOR_FUNC, (Function2D) x -> 1 / f.valueAt(x));
        properties.put(RADIUS_FUNC, (Function2D) x -> x);
        properties.put(ACCURACY_RATE, 2D);
        properties.put(FILLED_OVAL, false);
        properties.put(SHOW_ROOTS, false);
        properties.put(SHOW_STATIONARY_POINTS, false);
        properties.put(THICKNESS, 1.5f);
        properties.put(NUM_OF_THREADS, 10);
        properties.put(LOW_BOUND, Double.NEGATIVE_INFINITY);
        properties.put(UP_BOUND, Double.POSITIVE_INFINITY);
        properties.put(BEFORE_PARSE, "Un Available");
        properties.put(OBJECT_ID, Math.random());

        properties.put(TYPICAL_PLOT, true);
        properties.put(DOT_PLOT, false);
        properties.put(ADVANCED_PLOT, false);

        this.functions.put(f, properties);
        repaint();
    }

    public void addFunctionToDraw(String f) {
        if (f == null)
            return;
        Function2D func;
        try {
            func = Function4DParser.parser(f).f2D(0, 0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Graph2DCanvas.this, "error in parsing: " + e.getMessage());
            return;
        }
        if (func == null) {
            JOptionPane.showMessageDialog(Graph2DCanvas.this, "error in parsing");
            return;
        }
        stringBaseMap.put(f, func);
        addFunctionToDraw(func);
    }

    public void addFunctionToDraw(Function2D f, String beforeParse) {
        for (var kv : functions.entrySet())
            if (kv.getValue().get(BEFORE_PARSE).equals(beforeParse))
                return;
        addFunctionToDraw(f);
        functions.get(f).put(BEFORE_PARSE, beforeParse.replace(" ", ""));
    }

    public void setFunctionProperty(String functionBeforeParse, int property, Object newValue) {
        for (var kv : functions.entrySet())
            if (kv.getValue().get(BEFORE_PARSE).equals(functionBeforeParse))
                kv.getValue().replace(property, newValue);
        repaint();
    }

    public void removeFunctionByString(String f) {
        for (var kv : functions.entrySet())
            if (kv.getValue().get(BEFORE_PARSE).equals(f)) {
                functions.remove(kv.getKey());
                repaint();
                return;
            }
    }

    public void addFunctionToDraw(Function3D f, double l, double u, double delta, Function2D color) {
        for (double y = l; y < u; y += delta)
            //noinspection SuspiciousNameCombination
            addFunctionToDraw(f.f2D(y), new Color((int) (color.valueAt(y) * Integer.MAX_VALUE)));
        addFunctionToDraw(f.f2D(u), new Color((int) (color.valueAt(u) * Integer.MAX_VALUE)));
        repaint();
    }

    public void addFunctionToDraw(Function3D f, double lowRange, double upRange, double delta, Color... color) {
        var target = f.f().makeZFixByInverse(0, lowRange, upRange, delta);
        addFunctionToDraw(target, color);
    }

    public HashMap<Function2D, HashMap<Integer, Object>> getFunctions() {
        return functions;
    }

    public HashMap<Set<Point2D>, HashMap<Integer, Object>> getPointSets() {
        return pointSets;
    }

    public void removeAllFunctions() {
        functions.clear();
        repaint();
    }

    public void removeAllArcs() {
        arcs.clear();
        repaint();
    }

    @SafeVarargs
    public final void addPointSetToDraw(Color[] colors, Set<Point2D>... sets) {
        int counter = 0;
        for (var s : sets) {
            HashMap<Integer, Object> properties = new HashMap<>();
            properties.put(COLOR,
                    colors == null || colors.length - counter == 0 ? (isDark ? LIGHT.darker() : DARK.darker()) : colors[counter++]);
            properties.put(IS_VISIBLE, true);
            properties.put(POINT_RADIUS, 3.0);
            properties.put(FILLED_OVAL, true);
            this.pointSets.put(s, properties);
        }
        repaint();
    }

    public void typicalPlotting(Graphics2D g2d) {
        for (var f : functions.keySet()) {
            var p = functions.get(f);
            if (!(boolean) p.get(IS_VISIBLE) || !(boolean) p.get(TYPICAL_PLOT))
                continue;

            g2d.setStroke(new BasicStroke((float) p.get(THICKNESS), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
            var sample = Sampling.multiThreadSampling(f, Math.max(coordinateX(0),
                    (double) p.get(LOW_BOUND)), Math.min(coordinateX(getWidth()), (double) p.get(UP_BOUND)),
                    1 / (getXScale() * (double) p.get(ACCURACY_RATE)), (int) p.get(NUM_OF_THREADS));
            p.put(ALL_POINTS, sample);
            typicalPlotter(sample, (Color) p.get(COLOR), this, g2d);
        }

        for (var arc : arcs.keySet()) {
            var p = arcs.get(arc);
            if (!(boolean) p.get(IS_VISIBLE) || !(boolean) p.get(TYPICAL_PLOT))
                continue;

            g2d.setStroke(new BasicStroke((float) p.get(THICKNESS), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

            var sample = Sampling.multiThreadSampling(arc, (double) p.get(LOW_BOUND), (double) p.get(UP_BOUND),
                    (double) p.get(DELTA), (int) p.get(NUM_OF_THREADS));
            p.put(ALL_POINTS, sample);
            typicalPlotter(sample, (Color) p.get(COLOR), this, g2d);
        }
    }

    public static void typicalPlotter(List<Point2D> sample, Color c,
            CoordinatedScreen cs, Graphics2D g2d) {
        var vps = new ArrayList<>(sample);
        List<Point2D> nanPoints = new ArrayList<>();

        for (int i = 1; i < sample.size(); i++)
            if (!Double.isFinite(sample.get(i - 1).y))
                try {
                    while (!Double.isFinite(sample.get(i).y))
                        nanPoints.add(sample.get(i++));
                } catch (Exception e) {
                    break;
                }

        vps.removeAll(nanPoints);
        vps.add(new Point2D(0, Double.NaN));

        g2d.setColor(c);
        for (int i = 1, pre = 0; i < vps.size(); i++)
            if (!Double.isFinite(vps.get(i).y)) {
                var ps = vps.subList(pre + 1, i);

                int[] xa = new int[ps.size()];
                int[] ya = new int[ps.size()];

                int counter = 0;
                for (var p : ps) {
                    xa[counter] = cs.screenX(p.x);
                    ya[counter++] = cs.screenY(p.y);
                }

                g2d.drawPolyline(xa, ya, xa.length);
                pre = i;
            }
    }

    private void showRoots(Graphics2D g2d) {
        for (var f : functions.keySet()) {
            var p = functions.get(f);
            if (!(boolean) p.get(SHOW_ROOTS))
                continue;
            g2d.setColor((Color) p.get(COLOR));
            @SuppressWarnings("unchecked")
            var roots = RootsFinder.bySampling((List<Point2D>) p.get(ALL_POINTS));
            for (var r : roots)
                g2d.fillOval(screenX(r) - 4, screenY(0) - 4, 4 * 2, 4 * 2);
        }

        for (var a : arcs.keySet()) {
            var p = arcs.get(a);
            if (!(boolean) p.get(SHOW_ROOTS))
                continue;
            g2d.setColor((Color) p.get(COLOR));
            @SuppressWarnings("unchecked")
            var roots = RootsFinder.bySampling((List<Point2D>) p.get(ALL_POINTS));
            for (var r : roots)
                g2d.fillOval(screenX(r) - 4, screenY(0) - 4, 4 * 2, 4 * 2);
        }
    }

    public static void flat2DSurfacePlotter(List<Point2D> vertexes, Color bound, Color inner,
            CoordinatedScreen cs, Graphics2D g2d) {
        var vps = new ArrayList<>(vertexes);
        vps.removeIf(e -> !Double.isFinite(e.x) || !Double.isFinite(e.y));
        int[] xa = new int[vps.size()];
        int[] ya = new int[vps.size()];
        int counter = 0;
        for (var v : vps) {
            xa[counter] = cs.screenX(v.x);
            ya[counter++] = cs.screenY(v.y);
        }
        g2d.setColor(inner);
        g2d.fillPolygon(xa, ya, xa.length);
        if (inner.equals(bound))
            return;
        g2d.setColor(bound);
        g2d.drawPolygon(xa, ya, xa.length);
    }

    private void showStationaryPoints(Graphics2D g2d) {
        for (var f : functions.keySet()) {
            var p = functions.get(f);
            if (!(boolean) p.get(SHOW_STATIONARY_POINTS))
                continue;
            showPoints(FunctionAnalyser.stationaryPoints(f, coordinateX(0), coordinateX(getWidth()),
                    1 / getXScale()), (Color) p.get(COLOR), g2d);
        }
    }

    private void showPointSets(Graphics2D g2d) {
        for (var s : pointSets.entrySet()) {
            g2d.setColor((Color) s.getValue().get(COLOR));
            var r = (int) (/*getXScale() * */(double) s.getValue().get(POINT_RADIUS));
            var fs = (boolean) s.getValue().get(FILLED_OVAL);
            for (var p : s.getKey())
                if (fs) {
                    g2d.fillOval(screenX(p.x) - r, screenY(p.y) - r, 2*r, 2*r);
                } else {
                    g2d.drawOval(screenX(p.x) - r, screenY(p.y) - r, 2*r, 2*r);
                }
        }
    }

    private void showPoints(List<Point2D> points, Color c, Graphics2D g2d) {
        g2d.setColor(c);
        for (var p : points)
            g2d.fillOval(screenX(p.x) - 4, screenY(p.y) - 4, 4 * 2, 4 * 2);
    }

//    public void dotPlotting() {
//        for (var f : functions.keySet()) {
//            var p = functions.get(f);
//            if (!(boolean) p.get(DOT_PLOT) || !(boolean) p.get(IS_VISIBLE))
//                continue;
//
//            var sample = (java.util.List<Point2D>) p.get(ALL_POINTS);
//            sample = Sampling.multiThreadSampling(f, Math.max(coordinateX(0),
//                    (double) p.get(LOW_BOUND)), Math.min(coordinateX(getWidth()), (double) p.get(UP_BOUND)),
//                    1 / (getXScale() * (double) p.get(ACCURACY_RATE)), (int) p.get(NUM_OF_THREADS));
//            p.put(ALL_POINTS, sample);
//            dotPlotter(sample, (Color) p.get(COLOR), this::screenX, this::screenY, canvas);
//        }
//
//        for (var arc : arcs.keySet()) {
//            var p = arcs.get(arc);
//            if (!(boolean) p.get(IS_VISIBLE) || !(boolean) p.get(DOT_PLOT))
//                continue;
//
//            g2d.setStroke(new BasicStroke((float) p.get(THICKNESS), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
//            var sample = Sampling.multiThreadSampling(arc, (double) p.get(LOW_BOUND), (double) p.get(UP_BOUND),
//                    (double) p.get(DELTA), (int) p.get(NUM_OF_THREADS));
//            p.put(ALL_POINTS, sample);
//            dotPlotter(sample, (Color) p.get(COLOR), this::screenX, this::screenY, canvas);
//        }
//    }

    public void advancedPlotting(Graphics2D g2d) {
        for (var f : functions.keySet()) {
            var p = functions.get(f);
            if (!(boolean) p.get(ADVANCED_PLOT) || !(boolean) p.get(IS_VISIBLE))
                continue;

            g2d.setStroke(new BasicStroke((float) p.get(THICKNESS), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
            //noinspection unchecked
            var sample = (List<Point2D>) p.get(ALL_POINTS);
            sample = Sampling.multiThreadSampling(f, Math.max(coordinateX(0),
                    (double) p.get(LOW_BOUND)), Math.min(coordinateX(getWidth()), (double) p.get(UP_BOUND)),
                    1 / (getXScale() * (double) p.get(ACCURACY_RATE)), (int) p.get(NUM_OF_THREADS));
            p.put(ALL_POINTS, sample);
            advancedPlotter(sample, (Function2D) p.get(COLOR_FUNC),
                    (Function2D) p.get(RADIUS_FUNC), (boolean) p.get(FILLED_OVAL), this::screenX, this::screenY, g2d);
        }

        for (var arc : arcs.keySet()) {
            var p = arcs.get(arc);
            if (!(boolean) p.get(IS_VISIBLE) || !(boolean) p.get(ADVANCED_PLOT))
                continue;

            g2d.setStroke(new BasicStroke((float) p.get(THICKNESS), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
            var sample = Sampling.multiThreadSampling(arc, (double) p.get(LOW_BOUND), (double) p.get(UP_BOUND),
                    (double) p.get(DELTA), (int) p.get(NUM_OF_THREADS));
            p.put(ALL_POINTS, sample);
            advancedPlotter(sample, (Function2D) p.get(COLOR_FUNC),
                    (Function2D) p.get(RADIUS_FUNC), (boolean) p.get(FILLED_OVAL), this::screenX, this::screenY, g2d);        }
    }

    public static void simplePlotter2D(List<Point2D> sample, CoordinatedScreen canvas, Graphics2D g2d) {
        var xa = new int[sample.size()];
        var ya = new int[sample.size()];

        int counter = 0;
        for (var p : sample) {
            xa[counter] = canvas.screenX(p.x);
            ya[counter++] = canvas.screenY(p.y);
        }

        g2d.drawPolyline(xa, ya, xa.length);
    }

    public static void advancedPlotter(List<Point2D> sample, Function2D color, Function2D radius, boolean fillOval,
            Function<Integer, Double> screenX, Function<Integer, Double> screenY, Graphics2D g2d) {
        var enSample = new ArrayList<>(sample);
        enSample.removeIf(e -> !Double.isFinite(e.y));
        for (var p : enSample) {
            var r = (int) Math.max(Math.abs(radius.valueAt(p.x)), 1);
            g2d.setColor(new Color((int) (Integer.MAX_VALUE * color.valueAt(p.x))));
            if (fillOval) {
                //noinspection SuspiciousNameCombination
                g2d.fillOval(screenX.valueAt(p.x) - r, screenY.valueAt(p.y) - r, 2 * r, 2 * r);
            } else {
                //noinspection SuspiciousNameCombination
                g2d.drawOval(screenX.valueAt(p.x) - r, screenY.valueAt(p.y) - r, 2 * r, 2 * r);
            }
        }
    }

    @Override
    protected JPanel getSettingPanel() {
        var settingPanel = new JPanel();
        settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.Y_AXIS));

        var sp = new JPanel();
        sp.setLayout(new BoxLayout(sp, BoxLayout.Y_AXIS));
        /////////
        var addFunc = new JButton("Add function2D");

        var wrapper = new JPanel(new GridLayout());
        wrapper.add(addFunc);
        sp.add(wrapper);
        sp.add(getFunction2dList());

        addFunc.addActionListener(e -> {
            addFunctionToDraw(JOptionPane.showInputDialog(Graph2DCanvas.this, "Please Enter The Function2D: ", "Function2D", JOptionPane.INFORMATION_MESSAGE));
            sp.remove(1);
            var list = getFunction2dList();
            list.setPreferredSize(new Dimension(250, 350));
            sp.add(list);
            sp.revalidate();
            sp.repaint();
        });
        ////////
        sp.setBorder(BorderFactory.createTitledBorder("Graph2D Canvas"));
        settingPanel.add(sp);
        settingPanel.add(super.getSettingPanel());

        return settingPanel;
    }

    private JPanel getFunction2dList() {
        var list  = new JTable(new DefaultTableModel(new Object[][]{}, new String[] {"No.", "Function2D Expression"})) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        list.setRowHeight(30);
        list.getColumnModel().getColumn(0).setMaxWidth(40);
        var model = (DefaultTableModel) list.getModel();
        int counter = 0;
        for (var kv : stringBaseMap.entrySet())
            if (kv.getValue() instanceof Function2D)
                model.addRow(new Object[] {++counter, kv.getKey()});
        var listPanel = new JPanel(new GridLayout(0, 1));
        listPanel.add(new JScrollPane(list));

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (listPanel.getComponentCount() == 2)
                    listPanel.remove(1);
                //noinspection SuspiciousMethodCalls
                var pp = getFunction2DPropertiesPanel(functions.get(stringBaseMap.get(model.getValueAt(list.getSelectedRow(), 1).toString())));
                var back = new JButton("Back");
                back.addActionListener(ev -> listPanel.remove(1));
                var delete = new JButton("Delete");
                delete.addActionListener(ev -> {
                    var selected = list.getSelectedRow();
                    if (selected == -1) {
                        JOptionPane.showMessageDialog(Graph2DCanvas.this, "you should select a function first", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    var s = model.getValueAt(selected, 1).toString();
                    //noinspection SuspiciousMethodCalls
                    functions.remove(stringBaseMap.get(s));
                    stringBaseMap.remove(s);
                    listPanel.remove(1);
                    model.removeRow(selected);
                    repaint();
                    revalidate();
                });
                pp.add(delete);
                listPanel.add(pp);
                listPanel.repaint();
                listPanel.revalidate();
            }
        });
        listPanel.setPreferredSize(new Dimension(250, 0));
        return listPanel;
    }

    private JPanel getFunction2DPropertiesPanel(HashMap<Integer, Object> p) {
        var panel = new JPanel(new GridLayout(0, 2));
        var color = new JButton("Color");
        var numOfThreadLabel = new JLabel("NumOfThreads: " + p.get(NUM_OF_THREADS));
        var numOfThread = new JSlider(1, 80, (int) p.get(NUM_OF_THREADS));
        var precision = new JSlider(1, 3000, (int) (((double) p.get(ACCURACY_RATE)) * 1000));
        var precisionLabel = new JLabel("Precision: " + Utils.round(1 / (getXScale() * (double) p.get(ACCURACY_RATE)), 4));
        var thicknessLabel = new JLabel("Thickness: " + p.get(THICKNESS));
        var thickness = new JSlider(1, 1000, (int) (((float) p.get(THICKNESS)) * 10));
        var up = new JButton("UpBound");
        var low = new JButton("LowBound");
        var visible = new JCheckBox("Visible", (boolean) p.get(IS_VISIBLE));
        var root = new JCheckBox("ShowRoots", (boolean) p.get(SHOW_ROOTS));
        var stationary = new JCheckBox("ShowStationaryPoints", (boolean) p.get(SHOW_STATIONARY_POINTS));

        panel.add(color);
        panel.add(new JLabel());
        panel.add(numOfThreadLabel);
        panel.add(numOfThread);
        panel.add(precisionLabel);
        panel.add(precision);
        panel.add(thicknessLabel);
        panel.add(thickness);
        panel.add(up);
        panel.add(low);
        panel.add(visible);
        panel.add(root);
        panel.add(stationary);

        color.addActionListener(e -> {
            p.put(COLOR, JColorChooser.showDialog(Graph2DCanvas.this, "ChooseFunctionColor", (Color) p.get(COLOR)));
            repaint();
            revalidate();
        });
        numOfThread.addChangeListener(e -> {
            p.put(NUM_OF_THREADS, numOfThread.getValue());
            numOfThreadLabel.setText("NumOfThreads: " + numOfThread.getValue());
            repaint();
            revalidate();
        });
        precision.addChangeListener(e -> {
            p.put(ACCURACY_RATE, precision.getValue() / 1000d);
            precisionLabel.setText("Precision: " + Utils.round(1 / (getXScale() * (double) p.get(ACCURACY_RATE)), 4));
            repaint();
            revalidate();
        });
        thickness.addChangeListener(e -> {
            p.put(THICKNESS, thickness.getValue() / 10f);
            thicknessLabel.setText("Thickness: " + thickness.getValue() / 10f);
            repaint();
            revalidate();
        });
        visible.addActionListener(e -> {
            p.put(IS_VISIBLE, visible.isSelected());
            repaint();
            revalidate();
        });
        root.addActionListener(e -> {
            p.put(SHOW_ROOTS, root.isSelected());
            repaint();
            revalidate();
        });
        stationary.addActionListener(e -> {
            p.put(SHOW_STATIONARY_POINTS, stationary.isSelected());
            repaint();
            revalidate();
        });
        up.addActionListener(e -> {
            p.put(UP_BOUND, Double.parseDouble(JOptionPane.showInputDialog(Graph2DCanvas.this, "Enter Up Bound: (any exception won't change anything)", p.get(UP_BOUND))));
            repaint();
            revalidate();
        });
        low.addActionListener(e -> {
            p.put(LOW_BOUND, Double.parseDouble(JOptionPane.showInputDialog(Graph2DCanvas.this, "Enter Low Bound: (any exception won't change anything)", p.get(LOW_BOUND))));
            repaint();
            revalidate();
        });

        return panel;
    }

    public static Point[] getPointsOf2DArc(Arc2D arc, double l, double u, double delta, CoordinatedScreen cs) {
        return arc.sample(l, u, delta, 2).stream().map(cs::screen).toArray(Point[]::new);
    }
}
