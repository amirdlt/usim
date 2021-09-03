
import java.util.Scanner;

public class BST {
    private Node root;

    public BST() {
        root = null;
    }

    private static Node insert(Node root, int num, Node parent) {
        if (root == null)
            return new Node(num, parent) ;

        if (num < root.value) {
            root.left = insert(root.left, num, root);
        } else if (num > root.value) {
            root.right = insert(root.right, num, root);
        }

        return root;
    }

    public void insert(int num) {
        if (root == null) {
            root = new Node(num, null);
            return;
        }
        insert(root, num, null);
    }

    private static String find(Node root, int key) {
        if (root == null || root.value == key)
            return "";
        return (key < root.value ? "left " : "right ") + find(key < root.value ? root.left : root.right, key);
    }

    public String find(int key) {
        String res = find(root, key).trim();
        return res.isEmpty() ? "not found" : res;
    }

    private static Node parent(Node root, int key) {
        if (root == null)
            return null;
        if (root.value == key)
            return root.parent;
        if (key < root.value)
            return parent(root.left, key);
        else
            return parent(root.right, key);
    }

    public String parent(int k) {
        Node res = parent(root, k);
        return res == null ? "not found" : String.valueOf(res.value);
    }

    private static Node left(Node root, int k) {
        if (root == null)
            return null;
        if (root.value == k)
            return root.left;
        if (k < root.value)
            return left(root.left, k);
        else
            return left(root.right, k);
    }

    private static Node right(Node root, int k) {
        if (root == null)
            return null;
        if (root.value == k)
            return root.right;
        if (k < root.value)
            return right(root.left, k);
        else
            return right(root.right, k);
    }

    public String left(int k) {
        Node res = left(root, k);
        return res == null ? "not found" : String.valueOf(res.value);
    }

    public String right(int k) {
        Node res = right(root, k);
        return res == null ? "not found" : String.valueOf(res.value);
    }

    public int height() {
        return Math.max(0, height(root) - 1);
    }

    private static int height(Node node) {
        if (node == null)
            return 0;
        return Math.max(height(node.left) + 1, height(node.right) + 1);
    }

    private static Node node(Node root, int k) {
        if (root == null)
            return null;
        if (root.value == k)
            return root;
        if (k < root.value)
            return node(root.left, k);
        return node(root.right, k);
    }

    private static int depth(Node root, int k) {
        Node node = node(root, k);
        if (node == null)
            return -1;
        int d = 0;
        while ((node = node.parent) != null)
            d++;
        return d;
    }

    public String depth(int k) {
        int res = depth(root, k);
        return res == -1 ? "not found" : String.valueOf(res);
    }

    public static void main(String[] args) {
        BST bst = new BST();
        Scanner scanner = new Scanner(System.in);
        String command;
        while (!(command = scanner.nextLine().trim().toLowerCase()).equals("end")) {
            if (command.startsWith("height")) {
                System.out.println(bst.height());
            } else if (command.startsWith("depth")) {
                System.out.println(bst.depth(Integer.parseInt(command.split(" ")[1])));
            } else if (command.startsWith("left")) {
                System.out.println(bst.left(Integer.parseInt(command.split(" ")[1])));
            } else if (command.startsWith("right")) {
                System.out.println(bst.right(Integer.parseInt(command.split(" ")[1])));
            } else if (command.startsWith("parent")) {
                System.out.println(bst.parent(Integer.parseInt(command.split(" ")[1])));
            } else if (command.startsWith("find")) {
                System.out.println(bst.find(Integer.parseInt(command.split(" ")[1])));
            } else {
                bst.insert(Integer.parseInt(command));
            }
        }
    }

    public static class Node {
        private final int value;
        private Node left;
        private Node right;
        private final Node parent;

        public Node(int value, Node parent) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = parent;
        }
    }
}
