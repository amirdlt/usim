package ahd.ulib.jmath.datatypes.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node<T> {
    private final String id;
    private T data;
    private final List<Node<? extends T>> neighbors;

    protected Node(T data, String id) {
        neighbors = new ArrayList<>();
        this.data = data;
        this.id = id;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public List<Node<? extends T>> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Node<? extends T> newNeighbor) {
        neighbors.add(newNeighbor);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Node<?> node = (Node<?>) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
