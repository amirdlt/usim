package com.usim.ulib.ai.uni;


import com.usim.ulib.utils.annotation.Algorithm;
import org.jetbrains.annotations.NotNull;
import com.usim.ulib.utils.api.SemaphoreBase;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;

@Algorithm(type = "Search")
public class Grid2DPathFinderAlgorithm implements SemaphoreBase<String> {
    private final String[][] cells;
    private final String[][] safeCellsCopy;
    private int[][] info;
    private final int rows;
    private final int cols;
    private final Map<String, Semaphore> semaphoreMap;

    public Grid2DPathFinderAlgorithm(String[][] cells) {
        this.cells = cells;
        if (cells == null || cells.length == 0 || cells[0].length == 0)
            throw new RuntimeException("AHD:: cells must have at least one row and one column");
        rows = cells.length;
        cols = cells[0].length;
        semaphoreMap = new HashMap<>();
        safeCellsCopy = new String[rows][cols];
        resetSafeCopy();
        //////
        addSemaphore("step");
    }

    private void resetSafeCopy() {
        for (int i = 0; i < rows; i++)
            System.arraycopy(cells[i], 0, safeCellsCopy[i], 0, cols);
    }

    protected void resetCells() {
        for (int i = 0; i < rows; i++)
            System.arraycopy(safeCellsCopy[i], 0, cells[i], 0, cols);
    }

    public Point findRobot() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (cells[i][j].contains("r"))
                    return new Point(i, j);
        throw new RuntimeException("ERROR:: Robot lost");
    }

    public List<Point> ids(Point start) {
        List<Point> res;
        for (int i = 0; i < rows * cols + 1; i++) {
            info = null;
//            resetCells();
            if ((res = dls(start, new int[rows][cols], i, null, findRobot())) != null) {
                if (res.isEmpty())
                    break;
                Collections.reverse(res);
                acquire("step");
                info = null;
                res.forEach(e -> getInfo()[e.x][e.y] = 16);
                acquire("step");
                for (int ii = 0; ii < rows; ii++)
                    for (int j = 0; j < cols; j++)
                        info[ii][j] = info[ii][j] == 16 ? 16 : 0;
                resetCells();
                return res;
            }
            resetCells();
        }
        resetCells();
        acquire("step");
        info = null;
        return null;
    }

    public List<Point> dls(Point start, int[][] explored, int limit, Point parent, Point robot) {
        explored[start.x][start.y] = 10;
        info = explored;

        if (isGoal(start))
            return new ArrayList<>(List.of(start));

        setCellAttribute(start, 'b');
        setCellAttribute(robot, 'r');

        acquire("step");

        if (limit == 0) {
            if (parent != null) {
                removeCellAttribute(start);
                removeCellAttribute(robot);
            }
            return null;
        }

        List<Point> res;
        var neighbors = neighbors(start);
        if (neighbors.isEmpty()) {
            removeCellAttribute(start);
            removeCellAttribute(robot);
            return List.of();
        }
        neighbors.stream().filter(e -> explored[e.x][e.y] <= 0).forEach(e -> info[e.x][e.y] = -1);
        boolean flag = false;
        for (var neighbor : neighbors)
            if (explored[neighbor.x][neighbor.y] <= 0) {
                info[start.x][start.y] = 1;
                setCellAttribute(start, 'b');
                setCellAttribute(robot, 'r');
                acquire("step");
                removeCellAttribute(start);
                removeCellAttribute(robot);
                explored[neighbor.x][neighbor.y] = 1;
                res = dls(neighbor, clone(explored), limit - 1, start, start);
                info[neighbor.x][neighbor.y] = 1;
                explored[neighbor.x][neighbor.y] = 1;
                if (res == null) {
                    flag = true;
                    continue;
                }
                if (res.isEmpty())
                    continue;
                res.add(start);
//                resetCells();
                removeCellAttribute(start);
                removeCellAttribute(robot);
                return res;
            }

//        resetCells();
        removeCellAttribute(start);
        removeCellAttribute(robot);
        return flag ? null : List.of();
    }

    private int[][] clone(int[][] arr) {
        var res = new int[arr.length][arr[0].length];
        for (int i = 0; i < arr.length; i++)
            System.arraycopy(arr[i], 0, res[i], 0, arr[0].length);
        return res;
    }

    private boolean isGoal(Point p) {
        return cells[p.x][p.y].toLowerCase().contains("p") && !cells[p.x][p.y].toLowerCase().contains("pb");
    }

    private List<Point> neighborsDest(Point butter) {
        //For first call.
        var res = new ArrayList<Point>();
        if(cells[butter.x][butter.y].contains("p")) {
            var clone = new Point(butter);
            clone.translate(0, -1);
            if (isNeighbor(clone))
                res.add(new Point(clone));
            clone.translate(1, 1);
            if (isNeighbor(clone))
                res.add(new Point(clone));
            clone.translate(-1, 1);
            if (isNeighbor(clone))
                res.add(new Point(clone));
            clone.translate(-1, -1);
            if (isNeighbor(clone))
                res.add(new Point(clone));
            return res;
        }


        var oldButter = new Point(butter);
        var oldRobot = findRobot();
        var newRobot = new Point(2 * oldRobot.x - butter.x, 2 * oldRobot.y - butter.y);
        if(!isNeighbor(newRobot))
            return new ArrayList<>();

        removeCellAttribute(oldButter);
        setCellAttribute(oldRobot, 'b');
        setCellAttribute(newRobot, 'r');


        res.add(newRobot);
        var clone = new Point(oldRobot);
        clone.translate(0, -1);
        if (!clone.equals(oldButter) && !clone.equals(newRobot) && validNeighborForDest(clone, newRobot))
            res.add(new Point(clone));
        clone.translate(1, 1);
        if (!clone.equals(oldButter) && !clone.equals(newRobot) && validNeighborForDest(clone, newRobot))
            res.add(new Point(clone));
        clone.translate(-1, 1);
        if (!clone.equals(oldButter) && !clone.equals(newRobot) && validNeighborForDest(clone, newRobot))
            res.add(new Point(clone));
        clone.translate(-1, -1);
        if (!clone.equals(oldButter) && !clone.equals(newRobot) && validNeighborForDest(clone, newRobot))
            res.add(new Point(clone));

        removeCellAttribute(newRobot);
        setCellAttribute(oldRobot, 'r');
        setCellAttribute(oldButter, 'b');

        return res;
    }

    private List<Point> neighbors(Point point) {
        return neighbors(point, 1, true);
    }

    private List<Point> neighbors(Point point, int factor, boolean forButter) {
        var res = new ArrayList<Point>();
        var clone = new Point(point);
        clone.translate(0, -1);
        if (!forButter) {
            if (isNeighbor(clone) && !cells[clone.x][clone.y].contains("p"))
                res.add(new Point(clone));
            clone.translate(1, 1);
            if (isNeighbor(clone) && !cells[clone.x][clone.y].contains("p"))
                res.add(new Point(clone));
            clone.translate(-1, 1);
            if (isNeighbor(clone) && !cells[clone.x][clone.y].contains("p"))
                res.add(new Point(clone));
            clone.translate(-1, -1);
            if (isNeighbor(clone) && !cells[clone.x][clone.y].contains("p"))
                res.add(new Point(clone));
            return res;
        }
        if (validNeighbor(point, clone, factor))
            res.add(new Point(clone));
        clone.translate(1, 1);
        if (validNeighbor(point, clone, factor))
            res.add(new Point(clone));
        clone.translate(-1, 1);
        if (validNeighbor(point, clone, factor))
            res.add(new Point(clone));
        clone.translate(-1, -1);
        if (validNeighbor(point, clone, factor))
            res.add(new Point(clone));
        return res;
    }

    private boolean isNeighbor(Point point) {
        return  point.x > -1 && point.x < rows &&
                point.y > -1 && point.y < cols &&
                !cells[point.x][point.y].equalsIgnoreCase("x") &&
                !cells[point.x][point.y].contains("b") &&
                !cells[point.x][point.y].contains("pb");
    }

    private boolean validNeighbor(Point from, Point to, int factor) {
        var dest = new Point(from);
        dest.translate(factor * (from.x - to.x), factor * (from.y - to.y));
        if (dest.x < 0 ||
                dest.x >= rows ||
                dest.y < 0 ||
                dest.y >= cols ||
                cells[dest.x][dest.y].contains("x") ||
                cells[dest.x][dest.y].contains("b"))
            return false;
        return isNeighbor(to) && bbfs(findRobot(), dest, false) != null;
    }

    private boolean validNeighborForDest(Point neighborOfRobot, Point realRobot) {
        return isNeighbor(neighborOfRobot) && bbfs(neighborOfRobot, realRobot, false) != null;
    }

    private boolean isNeighborForRobot(Point butter, Point butterDest, int factor) {
        return bbfs(findRobot(), new Point(factor * (butter.x - butterDest.x), factor * (butter.y - butterDest.y)), false) != null;
    }

    public List<Point> robotPath(List<Point> butterPath) {
        if (butterPath == null) {
            info = null;
            return null;
        }
        var res = new ArrayList<Point>();
        Point p1 = null;
        var robot = findRobot();
        getInfo()[robot.x][robot.y] = 15;
        for (int i = 0; i < butterPath.size() - 1; i++) {
            p1 = butterPath.get(i);
            var p2 = butterPath.get(i + 1);
            var dest = new Point(2 * p1.x - p2.x, 2 * p1.y - p2.y);
            var path = bbfs(robot, dest, true);
            if (path == null) {
                acquire("step");
                info = null;
                return null;
            }
            res.addAll(path);
            cells[p2.x][p2.y] += "b";
            cells[p1.x][p1.y] = cells[p1.x][p1.y].charAt(0) + "r";
            getInfo()[p1.x][p1.y] = 15;
            cells[dest.x][dest.y] = String.valueOf(cells[dest.x][dest.y].charAt(0));
            robot = p1;
        }
        res.add(p1);
        res.forEach(e -> getInfo()[e.x][e.y] = 15);
        resetSafeCopy();
        acquire("step");
        info = null;
        return res;
    }

    private boolean validAddress(Point p) {
        return p.x >= 0 && p.y >= 0 && p.x < rows && p.y < cols;
    }

    public List<Point> bbfs(Point start, Point dest, boolean showChanges) {
        if (!validAddress(dest) || !validAddress(start) || cells[dest.x][dest.y].contains("x"))
            return null;
        Optional<Point> intersect;
        if (neighbors(start, 0, false).contains(dest)) {
            if (!showChanges)
                return new ArrayList<>(List.of(start, dest));
            var path = new ArrayList<>(List.of(start, dest));
            for (int i = 1; i < path.size(); i++) {
                acquire("step");
                var p = path.get(i - 1);
                cells[p.x][p.y] = String.valueOf(cells[p.x][p.y].charAt(0));
                var pp = path.get(i);
                cells[pp.x][pp.y] += 'r';
                getInfo()[pp.x][pp.y] = 15;
            }
            acquire("step");
            return path;
        }
        if (start.equals(dest)) {
            if (!showChanges)
                return new ArrayList<>(List.of(start));
            acquire("step");
            getInfo()[start.x][start.y] = 15;
            return new ArrayList<>(List.of(start));
        }
        var explored = new int[rows][cols];
        var queueOfStart = new ArrayDeque<>(List.of(new Node(start, null, 0)));
        var queueOfDest = new ArrayDeque<>(List.of(new Node(dest, null, 0)));
        Node nodeStart = null, nodeDest = null;
        var pointNodeMap = new HashMap<Point, Node>();
        boolean find = false;
        explored[start.x][start.y] = 1;
        explored[dest.x][dest.y] = 2;
        while (!queueOfStart.isEmpty() && !queueOfDest.isEmpty()) {
            // expand from start
            nodeStart = queueOfStart.pop();
            start = nodeStart.point;
            var neighborsStart = neighbors(start, 0, false);
            if ((intersect = neighborsStart.stream().filter(e -> explored[e.x][e.y] == 2).findFirst()).isPresent()) {
                nodeDest = pointNodeMap.get(intersect.get());
                find = true;
                break;
            }
            Node finalNodeStart = nodeStart;
            neighborsStart.stream().filter(e -> explored[e.x][e.y] == 0)
                    .forEach(e -> {
                        explored[e.x][e.y] = 1;
                        var node = new Node(e, finalNodeStart, 0);
                        queueOfStart.add(node);
                        pointNodeMap.put(e, node);
                    });
            // expand from dest
            //            acquire("step");
            nodeDest = queueOfDest.pop();
            dest = nodeDest.point;
            var neighborsDest = neighbors(dest, 0, false);
            if ((intersect = neighborsDest.stream().filter(e -> explored[e.x][e.y] == 1).findFirst()).isPresent()) {
                nodeStart = pointNodeMap.get(intersect.get());
                find = true;
                break;
            }
            Node finalNodeDest = nodeDest;
            neighborsDest.stream().filter(e -> explored[e.x][e.y] == 0)
                    .forEach(e -> {
                        explored[e.x][e.y] = 2;
                        var node = new Node(e, finalNodeDest, 0);
                        queueOfDest.add(node);
                        pointNodeMap.put(e, node);
                    });
        }
        if (!find)
            return null;
        var path = new LinkedList<Point>();
        while (nodeStart != null) {
            path.addFirst(nodeStart.point);
            nodeStart = nodeStart.parent;
        }
        while (nodeDest != null) {
            path.add(nodeDest.point);
            nodeDest = nodeDest.parent;
        }
        if (!showChanges)
            return path;
        for (int i = 1; i < path.size(); i++) {
            acquire("step");
            var p = path.get(i - 1);
            cells[p.x][p.y] = String.valueOf(cells[p.x][p.y].charAt(0));
            var pp = path.get(i);
            cells[pp.x][pp.y] += 'r';
            getInfo()[pp.x][pp.y] = 15;
        }
        acquire("step");
        return path;
    }

    ////////////////
    public List<Point> bbfs(Point start) {
        Optional<Point> intersect;
        Point dest;
        var pointNodeMap = new HashMap<Point, Node>();
        var startNode = new Node(start, null, 0);
        pointNodeMap.put(start, startNode);
        var queueOfStart = new ArrayDeque<>(List.of(startNode));
        var mapOfQueueDestinations = new HashMap<Point, ArrayDeque<Node>>();
        getDestinations().forEach(e -> {
            var node = new Node(e, null, 0);
            mapOfQueueDestinations.put(e, new ArrayDeque<>(List.of(node)));
            pointNodeMap.put(e, node);
        });
        Node nodeStart = null, nodeDest = null;
        boolean find = false;
        getInfo()[start.x][start.y] = 1;
        for (var d : mapOfQueueDestinations.keySet()) getInfo()[d.x][d.y] = 2;
        boolean firstRound = true;
        while (!queueOfStart.isEmpty() && !mapOfQueueDestinations.isEmpty()) {
            // expand from start
            acquire("step");
            nodeStart = queueOfStart.pop();
            start = nodeStart.point;
            setCellAttribute(start, 'b');
            if (!firstRound)
                setCellAttribute(nodeStart.parent.point, 'r');
            var neighborsStart = neighbors(start);
            if ((intersect = neighborsStart.stream().filter(e -> getInfo()[e.x][e.y] == 2).findFirst()).isPresent()) {
                nodeDest = pointNodeMap.get(intersect.get());
                find = true;
                break;
            }
            Node finalNodeStart = nodeStart;
            neighborsStart.stream().filter(e -> getInfo()[e.x][e.y] == 0 || getInfo()[e.x][e.y] == -15)
                    .forEach(e -> {
                        getInfo()[e.x][e.y] = 10;
                        var node = new Node(e, finalNodeStart, 0);
                        queueOfStart.add(node);
                        pointNodeMap.put(e, node);
                    });
            // expand from dest
            neighborsStart.stream().filter(e -> getInfo()[e.x][e.y] == 10).forEach(e -> {
                acquire("step");
                getInfo()[e.x][e.y] = 1;
            });
            if (!firstRound) {
                removeCellAttribute(start);
                removeCellAttribute(nodeStart.parent.point);
            }

            var shouldRemove = new ArrayList<>();
            for (var kv : mapOfQueueDestinations.entrySet()) {
                if (kv.getValue().isEmpty()) {
                    shouldRemove.add(kv.getKey());
                    continue;
                }
                acquire("step");
                nodeDest = kv.getValue().pop();
                dest = nodeDest.point;
                List<Point> neighborsDest;
                if (!firstRound) {
                    setCellAttribute(dest, 'r');
                    setCellAttribute(nodeDest.parent.point, 'b');
                    neighborsDest = neighborsDest(nodeDest.parent.point);
                }
                else neighborsDest = neighborsDest(dest);
                if (firstRound)
                    neighborsDest.removeIf(e -> bbfs(findRobot(), e, false) == null);
                Node finalNodeDest = nodeDest;
                if (nodeDest.parent != null && (intersect = neighborsDest.stream().filter(e -> getInfo()[/*finalNodeDest.parent.point*/e.x][/*finalNodeDest.parent.point*/e.y] == 1).findFirst()).isPresent()) {
                    nodeStart = pointNodeMap.get(intersect.get());
                    find = true;
                    break;
                }
                Point finalDest = dest;
                neighborsDest.stream().filter(e -> getInfo()[e.x][e.y] == 0 || getInfo()[e.x][e.y] == -15)
                        .forEach(e -> {
                            getInfo()[e.x][e.y] = 10;
//                            acquire("step");
                            getInfo()[finalDest.x][finalDest.y] = 2;
                            var node = new Node(e, finalNodeDest, 0);
                            kv.getValue().add(node);
                            pointNodeMap.put(e, node);
                        });
                if (!firstRound) {
                    acquire("step");
                    removeCellAttribute(dest);
                    removeCellAttribute(nodeDest.parent.point);
                }
            }
            if(find)
                break;
            //noinspection SuspiciousMethodCalls
            shouldRemove.forEach(mapOfQueueDestinations::remove);
            if (firstRound)
                removeCellAttribute(findRobot());
            firstRound = false;
        }
        if (!find) {
            acquire("step");
            info = null;
            resetCells();
            return null;
        }
        var path = new LinkedList<Point>();
        while (nodeStart != null) {
            path.addFirst(nodeStart.point);
            nodeStart = nodeStart.parent;
        }
        while (nodeDest != null) {
            path.add(nodeDest.point);
            nodeDest = nodeDest.parent;
        }
        path.forEach(e -> getInfo()[e.x][e.y] = 16);
        acquire("step");
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                info[i][j] = info[i][j] == 16 ? 16 : 0;
        resetCells();
        return path;
    }
    ////////////////

    public List<Point> aStar(Point start) {
        var fringe = new PriorityQueue<Node>();
        var dest = nearestManhattanDest(start);
        fringe.add(new Node(start, null, manhattan(start, dest)));
        Node node = null;
        boolean firstRound = true;
        boolean find = false;
        while (!fringe.isEmpty()) {
            node = fringe.poll();
            assert node != null;
            start = node.point;
            if (isGoal(start)) {
                find = true;
                break;
            }
            setCellAttribute(start, 'b');
            if (!firstRound)
                setCellAttribute(node.parent.point, 'r');
            getInfo()[start.x][start.y] = 10;
            acquire("step");
            getInfo()[start.x][start.y] = 1;
            var neighbors = neighbors(start);
            Node finalNode = node;
            neighbors.stream().
                    filter(e -> getInfo()[e.x][e.y] % 15 == 0).
                    forEach(e -> {
                        fringe.add(new Node(e, finalNode, manhattan(e, dest)));
                        getInfo()[e.x][e.y] = -1;
                    });
            if (!firstRound) {
                removeCellAttribute(start);
                removeCellAttribute(node.parent.point);
            } else
                removeCellAttribute(findRobot());
            firstRound = false;
        }
        assert node != null;
        if (!find) {
            resetCells();
            return null;
        }
        var path = new LinkedList<Point>();
        while (node != null) {
            path.addFirst(node.point);
            node = node.parent;
        }
        path.forEach(e -> getInfo()[e.x][e.y] = 16);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                getInfo()[i][j] = getInfo()[i][j] == 16 ? 16 : 0;
        resetCells();
        return path;
    }

    private List<Point> getDestinations() {
        var res = new ArrayList<Point>();
        Point p;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (isGoal(p = new Point(i, j)))
                    res.add(p);
        return res;
    }

    private Point nearestManhattanDest(Point start) {
        Point tempPoint, dest = null;
        int temp;
        int minManhattan = Integer.MAX_VALUE;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (cells[i][j].contains("p") && minManhattan > (temp = manhattan(start, tempPoint = new Point(i, j)))) {
                    minManhattan = temp;
                    dest = tempPoint;
                }
        return dest;
    }

    private static int manhattan(Point start, Point dest) {
        return Math.abs(start.x - dest.x) + Math.abs(start.y - dest.y);
    }

    private void removeCellAttribute(Point point) {
        cells[point.x][point.y] = String.valueOf(cells[point.x][point.y].charAt(0));
    }

    private void setCellAttribute(Point point, char attribute) {
        cells[point.x][point.y] = cells[point.x][point.y].charAt(0) + "" + attribute;
    }

    public String[][] getCells() {
        return cells;
    }

    public int[][] getInfo() {
        if (info == null) {
            info = new int[rows][cols];
            Point r;
            try {
                r = findRobot();
            } catch (Exception e) {
                return info;
            }
            getInfo()[r.x][r.y] = -15;
        }
        return info;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    @Override
    public Map<String, Semaphore> getSemaphoreMap() {
        return semaphoreMap;
    }

    private class Node implements Comparable<Node> {
        private final Point point;
        private final int g, h;
        private final Node parent;
        public Node(Point point, Node parent, int h) {
            this.point = point; this.g = parent == null ? 0 : parent.g + (cells[point.x][point.y].charAt(0) - '0');
            this.h = h; this.parent = parent;
        }
        @Override public int compareTo(@NotNull Node o) {return Integer.compare(g + h, o.g + o.h);}
    }
}
