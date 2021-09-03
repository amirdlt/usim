package com.usim.ulib.notmine;

import java.util.*;

public class HW3Q2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int q = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray()[1];
        BinarySearchTree bst = new BinarySearchTree();
        Arrays.stream(scanner.nextLine().trim().split(" ")).mapToInt(Integer::parseInt).forEach(bst::insert);
        ArrayList<Integer> res = new ArrayList<>();
        while (q-- >= 0) {
            res.add(bst.sumOfLeftBranch());
            if (q >= 0)
                bst.insert(scanner.nextInt());
        }
        res.forEach(System.out::println);
    }
}

class BinarySearchTree {
    private Node root;

    public BinarySearchTree() {
        root = null;
    }

    public void insert(int key)  {
        root = insertRecursive(root, key);

    }

    private Node insertRecursive(Node root, int key) {
        if (root == null)
            return new Node(key);
        if (key < root.key)
            root.left = insertRecursive(root.left, key);
        else if (key > root.key)
            root.right = insertRecursive(root.right, key);
        return root;
    }

    public int sumOfLeftBranch() {
        int res = 0;
        var q = new LinkedList<Node>();
        q.add(root);
        while (!q.isEmpty()) {
            int n = q.size();
            for (int i = 0 ; i < n ; i++){
                var temp = q.peek();
                q.poll();
                if (i == 0)
                    res += temp.key;
                if (temp.left != null)
                    q.add(temp.left);
                if (temp.right != null)
                    q.add(temp.right);
            }
        }
        return res;
    }

    private static class Node {
        int key;
        Node left, right;
        public Node(int data){
            key = data;
            left = right = null;
        }
    }
}
