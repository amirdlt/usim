package ahd.usim.ulib.jmath.datatypes.graph;

import java.util.Objects;

public class Edge<T> implements Comparable<Edge<? extends T>> {
    private final Node<T> node1;
    private final Node<T> node2;
    private double weight;

    public Edge(Node<T> node1, Node<T> node2, double weight) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }

    public Edge(Node<T> node1, Node<T> node2) {
        this(node1, node2, -1);
    }

    public Node<T> getNode1() {
        return node1;
    }

    public Node<T> getNode2() {
        return node2;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isConnectedTo(Node<T> node) {
        return node1.equals(node) || node2.equals(node);
    }

    public Node<T> getAnotherNode(Node<T> node) {
        return node1.equals(node) ? node2 : node2.equals(node) ? node1 : null;
    }

    @Override
    public int compareTo(Edge<? extends T> o) {
        return Double.compare(weight, o.weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Edge<?> edge = (Edge<?>) o;
        return Objects.equals(node1, edge.node1) && Objects.equals(node2, edge.node2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node1, node2);
    }
}
