package ahd.ulib.jmath.datatypes.graph;

import ahd.ulib.jmath.datatypes.tuples.AbstractPoint;
import ahd.ulib.jmath.datatypes.tuples.Point2D;
import ahd.ulib.jmath.datatypes.tuples.Point3D;
import ahd.ulib.utils.Utils;
import ahd.ulib.visualization.canvas.CoordinatedScreen;
import ahd.ulib.visualization.shapes.shape3d.Line3D;
import ahd.ulib.visualization.shapes.shape3d.Shape3D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Graph<T> extends Shape3D implements Iterable<T> {
    private final List<Node<T>> nodes;
    private final List<Edge<T>> edges;
    private final HashMap<String, Node<T>> nodeMap;
    private CoordinatedScreen cs;

    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        nodeMap = new HashMap<>();
        cs = null;
    }

    public void addNode(T newNode, String id) {
        var node = new Node<>(newNode, id);
        nodes.add(node);
        nodeMap.put(id, node);
        if (newNode instanceof Point3D)
            points.add((Point3D) newNode);
        else if (newNode instanceof Point2D)
            points.add(new Point3D((Point2D) newNode, 0));
    }

    public void addEdge(String id1, String id2) {
        var node1 = getNodeById(id1);
        var node2 = getNodeById(id2);
        node1.addNeighbor(node2);
        node2.addNeighbor(node1);
        var edge = new Edge<>(node1, node2);
        edges.add(edge);
        if (!(node1.getData() instanceof AbstractPoint))
            return;
        Line3D line = null;
        if (node1.getData() instanceof Point3D)
            components.add(line = new Line3D(cs, (Point3D) node1.getData(), (Point3D) node2.getData(), Color.RED));
        else if (node1.getData() instanceof Point2D)
            components.add(line = new Line3D(cs, new Point3D((Point2D) node1.getData(), 0), new Point3D((Point2D) node2.getData(), 0), Color.RED));
        if (line == null)
            throw new RuntimeException("AHD:: This Type of point is not valid here");
        line.setLabel(() -> String.valueOf(Utils.round(edge.getWeight(), 2)));
    }

    protected Node<T> getNodeById(String id) {
        return nodeMap.get(id);
    }

    public T getNodeDataById(String id) {
        return nodeMap.get(id).getData();
    }

    public List<Node<T>> getNodes() {
        return nodes;
    }

    public List<Edge<T>> getEdges() {
        return edges;
    }

    public Set<Edge<T>> getEdgesConnectedTo(Node<T> node) {
        return edges.stream().filter(edge -> edge.isConnectedTo(node)).collect(Collectors.toSet());
    }

    public int numOfNodes() {
        return nodes.size();
    }

    public int numOfEdges() {
        return edges.size();
    }

    public void clear() {
        nodes.clear();
        nodeMap.clear();
        edges.clear();
        cs = null;
    }

    @Override
    public CoordinatedScreen getCs() {
        return cs;
    }

    public void setCs(CoordinatedScreen cs) {
        this.cs = cs;
    }

    @Override
    public Iterator<T> iterator() {
        return nodes.stream().map(Node::getData).iterator();
    }

    @Override
    public void render(@NotNull Graphics2D g2d) {
        if (cs == null || nodes.isEmpty() || !(nodes.get(0).getData() instanceof AbstractPoint))
            return;
        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(Color.RED);
        points.stream().map(cs::screen).forEach(p -> g2d.fillOval(p.x, p.y, 2, 2));
        Collections.sort(components);
        components.forEach(e -> e.render(g2d));
    }
}
