package javaapplication;

import java.util.*;

public class TTTree {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        TwoThreeTree tree = new TwoThreeTree();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] split = line.split(" ");
            switch (split[0].toUpperCase()) {
                case "I":
                    boolean inserted = tree.insert(Integer.parseInt(split[1]));
                    System.out.println((inserted ? "Inserted" : "Didn't insert") + " " + split[1]);
                    break;
                case "S":
                    boolean found = tree.search(Integer.parseInt(split[1]));
                    System.out.println((found ? "Found" : "Didn't find") + " " + split[1]);
                    break;
                case "D":
                    boolean deleted = tree.remove(Integer.parseInt(split[1]));
                    System.out.println((deleted ? "Deleted" : "Didn't delete") + " " + split[1]);
                    break;
                case "K":
                    tree.keyOrderList();
                    break;
                case "B":
                    tree.bfsList();
                    break;
                case "H":
                    System.out.println(tree.height());
                    break;
                case "M":
                    System.out.println(tree.numberOfNodes());
                    break;
                case "E":
                    return;
            }
            // if (!tree.search(6)) System.out.println("gone");
        }
    }

    public static class TwoThreeTree {

        private TreeNode root;

        public TwoThreeTree() {
            root = new TreeNode();
        }

        /**
         * Returns false if the key already exists.
         */
        public boolean insert(int key) {
            try {
                TreeNode node = (TreeNode) root.insert(key);
                if (node != root) {
                    root = new TreeNode(node, root);
                }
            } catch (DuplicateKeyException ex) {
                return false;
            }
            return true;
        }

        /**
         * Returns true if the key exists.
         */
        public boolean search(int key) {
            return root.search(key);
        }

        /**
         * Returns true if the key is found and deleted.
         */
        public boolean remove(int key) {
            try {
                root.remove(key, null, 0);
                return true;
            } catch (MissingKeyException ex) {
                return false;
            }
        }

        /**
         * Prints all the keys stored in order of increasing values.
         */
        public void keyOrderList() {
            root.printKeys();
        }

        /**
         * Prints all the keys in breadth first order.
         */
        public void bfsList() {
            LinkedList<Node> q = new LinkedList<>();
            q.add(root);
            while (!q.isEmpty()) {
                LinkedList<Node> nextLevel = new LinkedList<Node>();
                for (Node tn : q) {
                    tn.print();
                    System.out.print(" ");
                    if (tn instanceof TreeNode) {
                        for (Node t : ((TreeNode) tn).children) {
                            if (t != null) {
                                nextLevel.add(t);
                            }
                        }
                    }
                }
                System.out.println();
                q = nextLevel;
            }
        }

        /**
         * Returns the number of keys currently stored.
         */
        public int numberOfNodes() {
            return root.numberOfNodes();
        }

        /**
         * Returns the number of links on any path from the root to a leaf.
         */
        public int height() {
            return root.height();
        }

    }

    public static abstract class Node {

        abstract boolean search(int key);

        abstract int getMin();

        abstract List<Integer> getKeys();

        abstract void print();

        abstract int numberOfNodes();

    }

    public static class TreeNode extends Node {

        int[] keys;
        Node[] children;

        /**
         * Number of children
         */
        int degree;

        TreeNode() {
            keys = new int[3];
            children = new Node[3];
            degree = 0;
        }

        /**
         * Constructor for a TreeNode with 2 children
         */
        TreeNode(int a, int b) {
            keys = new int[3];
            children = new Node[3];
            keys[0] = a;
            keys[1] = b;
            children[0] = new LeafNode(a);
            children[1] = new LeafNode(b);
            degree = 2;
        }

        /**
         * Constructor for a TreeNode with 2 treenode children
         */
        TreeNode(Node a, Node b) {
            keys = new int[3];
            children = new Node[3];

            // Ensure correct order
            if (b.getMin() < a.getMin()) {
                Node tmp = a;
                a = b;
                b = tmp;
            }

            keys[0] = a.getMin();
            keys[1] = b.getMin();
            children[0] = a;
            children[1] = b;
            degree = 2;
        }

        int getMin() {
            return keys[0];
        }

        void print() {
            if (degree <= 1) {
                System.out.print("(-,-)");
            } else if (degree == 2) {
                System.out.print("(" + keys[1] + ",-)");
            } else {
                System.out.print("(" + keys[1] + "," + keys[2] + ")");
            }
        }

        void printKeys() {
            StringBuilder b = new StringBuilder();
            for (int key : getKeys()) {
                b.append(key).append(" ");
            }
            System.out.println(b.toString().trim());
        }

        List<Integer> getKeys() {
            List<Integer> keys = new ArrayList<>();
            for (Node child : children) {
                if (child != null) {
                    keys.addAll(child.getKeys());
                }
            }
            return keys;
        }

        void debug() {
            System.out.print("=== NODE ");
            print();
            System.out.println(" ===");
            System.out.println("Degree " + degree);
            System.out.println(this);
            System.out.println("Children:");
            if (children[0] != null) {
                System.out.println("0: " + children[0].getMin());
            }
            if (children[1] != null) {
                System.out.println("1: " + children[1].getMin());
            }
            if (children[2] != null) {
                System.out.println("2: " + children[2].getMin());
            }
            System.out.print("=== NODE ");
            print();
            System.out.println(" ===");
        }

        private int childIndexForKey(int key) {
            if (degree == 0) {
                return 1;
            }
            if (degree >= 1 && key < keys[1]) {
                return 0;
            } else if (degree >= 1 && key >= keys[1]) {
                if (degree == 3) {
                    if (key < keys[2]) {
                        return 1;
                    } else {
                        return 2;
                    }
                } else {
                    return 1;
                }
            }
            return -1;
        }

        boolean search(int key) {
            if (key < keys[1] && children[0] != null) {
                return children[0].search(key);
            } else if (key < keys[2] || children[0] == null || degree <= 2) {
                return children[1].search(key);
            } else {
                return children[2].search(key);
            }
        }

        /**
         * Inserts an element.
         *
         * @return the min key, k_0
         */
        Node insert(int key) {
            int index = childIndexForKey(key);

            if (index == -1) {
                throw new IllegalStateException("invalid insertion on node");
            }

            if (!isLastLevel()) {
                Node node = ((TreeNode) children[index]).insert(key);
                if (children[index] != node) {
                    return handleInsertSplit(node, index);
                }
                return this;
            }

            // We're on the last level.
            if (!isFull()) {
                return insertLast(key);
            }

            // This node is full. We should split.
            return handleDegree3Insert(new LeafNode(key));
        }

        /**
         * Handle insertion splits
         */
        Node handleInsertSplit(Node born, int index) {
            Node left = children[index];
            Node right = born;
            if (left.getMin() > right.getMin()) {
                left = right;
                right = children[index];
            }

            if (degree == 3) {
                return handleDegree3Insert(born);
            }

            // Insert in middle
            if (index == 0) {
                keys[2] = keys[1];
                children[2] = children[1];
                keys[1] = born.getMin();
                children[1] = born;

                // Insert at end
            } else if (index == 1) {
                keys[2] = born.getMin();
                children[2] = born;

                // Child at end -- should not happen
            } else if (index == 2) {
                throw new IllegalStateException("child is too far right");
            }

            degree++;
            return this;
        }

        /**
         * Handles the insertion of a new node at degree 3
         */
        Node handleDegree3Insert(Node toInsert) {

            TreeNode born;
            int key = toInsert.getMin();

            // Bear a new tree node
            if (key < keys[0]) {

                // [-inf, 0)
                keys[1] = keys[0];
                keys[0] = key;
                born = new TreeNode(children[1], children[2]);
                children[1] = children[0];
                children[0] = toInsert;

            } else if (key < keys[1]) {
                // [0, 1)
                born = new TreeNode(children[1], children[2]);
                keys[1] = key;
                children[1] = toInsert;

            } else if (key < keys[2]) {
                // [1, 2)
                born = new TreeNode(toInsert, children[2]);

            } else if (key > keys[2]) {
                // [2, inf)
                born = new TreeNode(children[2], toInsert);

            } else {
                throw new DuplicateKeyException(key);
            }

            // remove abandoned child
            children[2] = null;

            // reset degree to 2
            degree = 2;

            return born;

        }

        /**
         * Insert on the last level
         */
        Node insertLast(int key) {
            // Empty node
            if (degree == 0) {
                // Set center child
                keys[1] = keys[0] = key;
                children[1] = new LeafNode(key);

            } else if (degree == 1) {
                // CASE: there's one leaf in the treenode
                // key less than single key
                if (key < keys[1]) {
                    if (children[0] == null) {
                        children[0] = new LeafNode(key);
                    } else {
                        children[1] = children[0];
                        children[0] = new LeafNode(key);
                    }
                    keys[0] = key;

                    // Key greater than the key
                } else if (key > keys[1]) {

                    // Replace the key with the given
                    children[0] = children[1];
                    children[1] = new LeafNode(key);
                    keys[0] = keys[1];
                    keys[1] = key;

                    // Duplicate value
                } else {
                    throw new DuplicateKeyException(key);
                }

            } else if (degree == 2) {
                // CASE: there's two leaves in the treenode at 0 and 1.
                // This means only one key is set!
                // This key is also the value of the center child.

                // Key is lower than the min.
                if (key < keys[1]) {
                    keys[2] = keys[1];
                    children[2] = children[1];
                    if (key < keys[0]) {
                        keys[1] = keys[0];
                        children[1] = children[0];
                        keys[0] = key;
                        children[0] = new LeafNode(key);
                    } else {
                        keys[1] = key;
                        children[1] = new LeafNode(key);
                    }

                    // Key is greater than center
                    // A child is born.
                } else if (key > keys[1]) {
                    keys[2] = key;
                    children[2] = new LeafNode(key);

                } else {
                    throw new DuplicateKeyException(key);
                }

                // The full node
                // we should split
            } else if (degree == 3) {
                return handleDegree3Insert(new LeafNode(key));
            }

            degree++; // Increase degree as new key
            return this;
        }

        int remove(int key, TreeNode parent, int childIndex) {

            if (!isLastLevel()) {
                if (key < keys[1] && children[0] != null) {
                    keys[0] = ((TreeNode) children[0]).remove(key, this, 0);
                } else if (key < keys[2] || children[0] == null || degree <= 2) {
                    keys[1] = ((TreeNode) children[1]).remove(key, this, 1);
                } else {
                    keys[2] = ((TreeNode) children[2]).remove(key, this, 2);
                }
                return getMin();
            }

            // Last level with a ref to parent
            // let's do this guys
            // CASE 1
            // Just delete
            // If node is full
            if (degree == 3) {
                if (key == keys[0]) {
                    keys[0] = keys[1];
                    children[0] = children[1];
                    keys[1] = keys[2];
                    children[1] = children[2];
                } else if (key == keys[1]) {
                    keys[1] = keys[2];
                    children[1] = children[2];
                } else if (key == keys[2]) {
                } else {
                    throw new MissingKeyException(key);
                }
                children[2] = null;
                degree--;
                return getMin();
            }

            // It is implied that degree is now 2.
            // There cannot be a degree 1 node in the tree.
            // CASE 2
            // Borrow
            int removeIndex = keys[0] == key ? 0
                    : keys[1] == key ? 1
                            : -1;

            if (removeIndex == -1) {
                throw new IllegalStateException("cannot remove");
            }

            // Borrow from left sibling
            if (childIndex > 0) {
                if (borrow(parent, childIndex, childIndex - 1, removeIndex, true)) {
                    return getMin();
                }
            }

            // Borrow from right sibling
            if (childIndex < 2) {
                if (borrow(parent, childIndex, childIndex + 1, removeIndex, false)) {
                    return getMin();
                }
            }

            // CASE 3
            // Donate
            // Donate to left sibling
            if (childIndex > 0) {
                if (donate(parent, childIndex, childIndex - 1, removeIndex, true)) {
                    return getMin();
                }
            }

            // Donate to right sibling
            if (childIndex < 2) {
                if (donate(parent, childIndex, childIndex + 1, removeIndex, false)) {
                    return getMin();
                }
            }

            // CASE 4
            // coalesce
            return coalesce(parent);

        }

        /**
         * Borrows a node from a direct sibling.
         */
        private boolean borrow(TreeNode parent, int childIndex, int fromIndex, int removeIndex, boolean largest) {
            if (removeIndex == -1) {
                return false;
            }
            TreeNode from = (TreeNode) parent.children[fromIndex];
            if (from.children.length != 3) {
                return false;
            }

            // Absorb from left
            if (largest) {
                if (removeIndex == 1) {
                    this.keys[1] = this.keys[0];
                    this.children[1] = this.children[0];
                }
                this.keys[0] = from.keys[2];
                this.children[0] = from.children[2];

                // Absorb from right
            } else {
                if (removeIndex == 0) {
                    this.keys[0] = this.keys[1];
                    this.children[0] = this.children[1];
                }
                this.keys[1] = from.keys[0];
                this.children[1] = from.children[0];
                from.keys[0] = from.keys[1];
                from.children[0] = from.children[1];
                from.keys[1] = from.keys[2];
                from.children[1] = from.children[2];

            }

            // Remove references
            from.children[2] = null;
            from.degree--;

            parent.keys[0] = children[0].getMin();
            parent.keys[childIndex] = getMin();
            parent.keys[fromIndex] = from.getMin();

            return true;
        }

        /**
         * Donates a node to a direct sibling.
         */
        private boolean donate(TreeNode parent, int childIndex, int toIndex, int removeIndex, boolean left) {
            // Check if we can donate
            if (parent.children.length != 3 || children.length != 2) {
                return false;
            }

            TreeNode recipient = (TreeNode) parent.children[toIndex];
            if (recipient.isFull()) {
                return false;
            }

            int donationKey = keys[removeIndex == 0 ? 1 : 0];
            Node donation = children[removeIndex == 0 ? 1 : 0];

            // Donate to left
            if (left) {

                recipient.keys[2] = donationKey;
                recipient.children[2] = donation;

                // Donate to right
            } else {

                recipient.keys[2] = recipient.keys[1];
                recipient.keys[1] = recipient.keys[0];
                recipient.keys[0] = donationKey;
                recipient.children[2] = recipient.children[1];
                recipient.children[1] = recipient.children[0];
                recipient.children[0] = donation;

            }

            // it's bigger now
            recipient.degree++;

            parent.children[childIndex] = null;
            if (childIndex == 0) {
                parent.children[0] = parent.children[1];
                parent.children[1] = parent.children[2];
            } else if (childIndex == 1) {
                parent.children[1] = parent.children[2];
            }
            parent.children[2] = null;

            parent.keys[0] = parent.children[0].getMin();
            parent.keys[1] = parent.children[1].getMin();

            return true;
        }

        private int coalesce(TreeNode node) {
            TreeNode a = (TreeNode) node.children[0];
            TreeNode b = (TreeNode) node.children[1];
            int pt = 0;
            for (Node n : a.children) {
                if (n != null) {
                    node.children[pt++] = n;
                }
            }
            for (Node n : b.children) {
                if (n != null) {
                    node.children[pt++] = n;
                }
            }
            node.keys[0] = node.children[0].getMin();
            node.keys[1] = node.children[1].getMin();
            node.keys[2] = node.children[2].getMin();
            return node.keys[0];
        }

        boolean isLastLevel() {
            return children[0] == null || children[0] instanceof LeafNode;
        }

        boolean isFull() {
            return degree == 3;
        }

        int height() {
            return isLastLevel() ? 1 : ((TreeNode) children[0]).height() + 1;
        }

        int numberOfNodes() {
            int sum = 0;
            for (Node n : children) {
                if (n != null) {
                    sum += n.numberOfNodes();
                }
            }
            return sum;
        }

    }

    public static class LeafNode extends Node {

        private int key;

        public LeafNode(int key) {
            this.key = key;
        }

        boolean search(int key) {
            return this.key == key;
        }

        int getMin() {
            return key;
        }

        List<Integer> getKeys() {
            return Arrays.asList(key);
        }

        void print() {
            System.out.print(key);
        }

        int numberOfNodes() {
            return 1;
        }

    }

    static class MissingKeyException extends IllegalArgumentException {

        public MissingKeyException(int key) {
            super("Missing key on key " + key);
        }

    }

    static class DuplicateKeyException extends IllegalArgumentException {

        public DuplicateKeyException(int key) {
            super("Duplicate insert on key " + key);
        }

    }

}
