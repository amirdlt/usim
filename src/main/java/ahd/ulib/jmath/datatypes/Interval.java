package ahd.ulib.jmath.datatypes;

import ahd.ulib.jmath.datatypes.tuples.ComparablePair;

import java.util.*;

@Deprecated
@SuppressWarnings("all")
public final class Interval {
    private List<ComparablePair<Double, Boolean>> downPairs;
    private List<ComparablePair<Double, Boolean>> upPairs;

    private Interval(List<ComparablePair<Double, Boolean>> downPairs, List<ComparablePair<Double, Boolean>> upPairs) {
        this.downPairs = downPairs;
        this.upPairs = upPairs;
    }

    private Interval(Interval intervalToCopy) {
        this.downPairs = new ArrayList<>(intervalToCopy.downPairs);
        this.upPairs = new ArrayList<>(intervalToCopy.upPairs);
    }

    private Interval(Map<ComparablePair<Double, Boolean>, ComparablePair<Double, Boolean>> map) {
        downPairs = new ArrayList<>();
        upPairs = new ArrayList<>();
        for (var entry : map.entrySet()) {
            downPairs.add(entry.getKey());
            upPairs.add(entry.getValue());
        }
    }

    public static Interval create(double down, boolean downInclusive, double up, boolean upInclusive) {
        List<ComparablePair<Double, Boolean>> downs = new ArrayList<>();
        downs.add(new ComparablePair<>(down, downInclusive));
        List<ComparablePair<Double, Boolean>> ups = new ArrayList<>();
        ups.add(new ComparablePair<>(up, upInclusive));
        return new Interval(downs, ups);
    }

    public static Interval create(List<ComparablePair<Double, Boolean>> downPairs, List<ComparablePair<Double, Boolean>> upPairs) {
        return new Interval(downPairs, upPairs);
    }

    public static Interval copy(Interval intervalToCopy) {
        return new Interval(intervalToCopy);
    }

    public static Interval downIncludeUpExclude(double down, double up) {
        return create(down, true, up, false);
    }

    public static Interval upIncludeDownExclude(double down, double up) {
        return create(down, false, up, true);
    }

    public static Interval upIncludeDownInclude(double down, double up) {
        return create(down, true, up, true);
    }

    public static Interval upExcludeDownExclude(double down, double up) {
        return create(down, false, up, false);
    }

    public Interval union(Interval... intervals) {
        var map = getMap();
        var newMap = new LinkedHashMap<ComparablePair<Double, Boolean>, ComparablePair<Double, Boolean>>();
        for (Interval interval : intervals)
            map.putAll(interval.getMap());
        Interval i1, i2;
        ArrayList<Map.Entry<ComparablePair<Double, Boolean>, ComparablePair<Double, Boolean>>> entries = new ArrayList<>(map.entrySet());
        for (int i = 0; i < entries.size(); i++)
            for (int j = i; j < entries.size(); j++)
                if (!intersect(i1 = create(entries.get(i).getKey().getX(),
                        entries.get(i).getKey().getY(), entries.get(i).getValue().getX(),
                        entries.get(i).getValue().getY()), i2 = create(entries.get(j).getKey().getX(),
                        entries.get(j).getKey().getY(), entries.get(j).getValue().getX(), entries.get(j).getValue().getY())).isVoid()) {
                    var u = union(i1, i2);
                    if (!new Interval(newMap).contains(u)) {
                        newMap.put(u.downPairs.get(0), u.upPairs.get(0));
                    }
                } else if (!new Interval(newMap).contains(i1)) {
                    newMap.put(entries.get(i).getKey(), entries.get(i).getValue());
                }

        set(new Interval(newMap));
        return this;
    }

    public HashMap<ComparablePair<Double, Boolean>, ComparablePair<Double, Boolean>> getMap() {
        LinkedHashMap<ComparablePair<Double, Boolean>, ComparablePair<Double, Boolean>> map = new LinkedHashMap<>();
        for (int i = 0; i < downPairs.size(); i++)
          map.put(downPairs.get(i), upPairs.get(i));
        return map;
    }

    public void set(Interval interval) {
        setDownPairs(interval.downPairs);
        setUpPairs(interval.upPairs);
    }

    // Intervals1&2 should be one part.
    private static Interval union(Interval interval1, Interval interval2) {
        var res = copy(interval1);
        if (intersect(interval1, interval2).isVoid()) {
            res.downPairs.addAll(interval2.downPairs);
            res.upPairs.addAll(interval2.upPairs);
        } else {
            res.downPairs.addAll(interval2.downPairs);
            res.upPairs.addAll(interval2.upPairs);
            var down = Collections.min(res.downPairs);
            var up = Collections.max(res.upPairs);

            res.set(create(down.getX(), down.getY(), up.getX(), up.getY()));
        }
        return res;
    }

    private static Interval intersect(Interval interval1, Interval interval2) {
        return new Interval(interval1).intersect(interval2);
    }

    public Interval intersect(Interval... intervals) {
        for (Interval interval : intervals) {
            downPairs.addAll(interval.downPairs);
            upPairs.addAll(interval.upPairs);
        }
        var down = Collections.max(downPairs);
        var up = Collections.min(upPairs);
        if (down.compareTo(up) > 0 || down.equals(up) && !down.getY()) {
            set(create(new ArrayList<>(), new ArrayList<>()));
        } else {
            set(create(down.getX(), down.getY(), up.getX(), up.getY()));
        }
        return this;
    }

    public Interval complement() {

        return this;
    }

    public Interval sub(Interval interval) {

        return this;
    }

    public Interval symmetricSub(Interval... intervals) {

        return this;
    }

    public boolean contains(Interval interval) {
        int res = 0;
        var map = interval.getMap();
        for (var entryOut : map.entrySet())
            for (var entry : getMap().entrySet())
                if (entry.getKey().compareTo(entryOut.getKey()) <= 0 && entry.getValue().compareTo(entryOut.getValue()) >= 0) {
                    res++;
                    break;
                }
        return res == map.size();
    }

    public void setDownPairs(List<ComparablePair<Double, Boolean>> downPairs) {
        this.downPairs = downPairs;
    }

    public void setUpPairs(List<ComparablePair<Double, Boolean>> upPairs) {
        this.upPairs = upPairs;
    }

    public boolean isVoid() {
        return downPairs.isEmpty() || upPairs.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        int counter = 0;
        for (var d : downPairs) {
            s.append(d.getY() ? "[" : "(").append(d.getX()).append(", ").
                    append(upPairs.get(counter).getX()).append(upPairs.get(counter).getY() ? "]" : ")");
            s.append(" U ");
            counter++;
        }
        try {
            return s.toString().substring(0, s.length() - 3);
        } catch (Exception e) {
            return "VOID";
        }
    }
}
