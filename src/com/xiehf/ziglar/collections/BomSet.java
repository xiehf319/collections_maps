package com.xiehf.ziglar.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * 树结果 不能重复的 结构
 *
 * @author xiehaifan  on 2017/11/3
 */
public class BomSet<T> implements BaseSet<T> {


    private transient Node<T> root;

    private Comparator<? super T> comparator;

    private BomSet() {
        this.comparator = null;
    }

    public BomSet(T obj) {
        this();
        if (obj == null) {
            throw new IllegalArgumentException("根不能为空");
        }
    }

    private int compare(T t1, T t2) {
        return comparator == null ? ((Comparable<? super T>) t1).compareTo(t2) : comparator.compare(t1, t2);
    }

    public BomSet(T obj, Comparator<? super T> comparator) {
        this();
        if (obj == null) {
            throw new IllegalArgumentException("根不能为空");
        }
        this.comparator = comparator;
    }

    public BomSet(T obj, boolean flag, Comparator<? super T> comparator) {
        this();
        if (obj == null) {
            throw new IllegalArgumentException("根不能为空");
        }
        root = new Node<>(obj, null, flag);
        this.comparator = comparator;
    }

    @Override
    public void add(T parent, T child) {
        add(parent, child, false);
    }

    public void add(T parent, T child, boolean flag) {
        if (child == null) {
            throw new IllegalArgumentException("child can not be null!");
        }
        Node<T> node = getNode(parent);
        if (node == null) {
            throw new IllegalArgumentException("parent is not exist in set");
        }
        Node<T> c = new Node<>(child, node, flag);
        node.addChild(c);
        if (flag) {
            tag(node);
        }
    }

    private void tag(Node<T> node) {
        if (node == null) {
            return;
        }
        if (node.flag) {
            return;
        } else {
            node.flag = true;
        }
        tag(node.parent);
    }

    private Node<T> getNode(T n) {
        return getTarget(root, n);
    }

    public void clean() {
        clear(root);
    }

    private boolean clear(Node<T> node) {
        if (node == null) {
            return false;
        }
        if (!node.flag) {
            if (node.parent == null) {
                root = null;
                return false;
            }
            node.parent = null;
            return true;
        } else {
            if (node.children != null && node.children.size() > 0) {
                Iterator<Node<T>> iterator = node.children.iterator();
                while (iterator.hasNext()) {
                    Node<T> next = iterator.next();
                    if (next != null) {
                        if (clear(next)) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        return false;
    }

    private Node<T> getTarget(Node<T> node, T n) {
        if (node == null) {
            return null;
        }
        if (compare(node.obj, n) == 0) {
            return node;
        }
        if (node.children == null || node.children.size() == 0) {
            return null;
        }
        for (Node<T> tNode : node.children) {
            Node<T> target = getTarget(tNode, n);
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    public List<T> getElements() {
        List<T> collection = new ArrayList<>();
        if (root == null) {
            return collection;
        }
        getNodes(collection, root);
        return collection;
    }

    private void getNodes(List<T> collection, Node<T> node) {
        if (node != null) {
            collection.add(node.obj);
            if (node.children != null && !node.children.isEmpty()) {
                for (Node<T> child : node.children) {
                    getNodes(collection, child);
                }
            }
        }
    }

    @Override
    public void add(T parent, List<T> children) {
        if (children == null || children.isEmpty()) {
            throw new IllegalArgumentException("children can not be null!");
        }
        Node<T> node = getNode(parent);
        if (node == null) {
            throw new IllegalArgumentException("parent is not exist in set");
        }
        for (T child : children) {
            if (child == null) {
                throw new IllegalArgumentException("child can not be null!");
            }
        }
        for (T child : children) {
            Node<T> c = new Node<>(child, node);
            node.addChild(c);
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean contain() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }


    static class Node<T> implements Serializable {

        Node<T> parent;

        transient List<Node<T>> children;

        T obj;

        boolean flag;

        Node(T obj, Node<T> parent, boolean flag) {
            this();
            this.obj = obj;
            this.parent = parent;
            this.children = null;
            this.flag = flag;
        }


        Node(T obj, Node<T> parent) {
            this();
            this.obj = obj;
            this.parent = parent;
            this.children = null;
            this.flag = true;
        }

        Node() {
        }

        void addChild(Node<T> child) {
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(child);
            child.parent = this;
        }
    }
}
