package com.xiehf.ziglar.maps;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by xiehaifan on 2017/9/19.
 */
public class RBTree<T> implements BinarySortTree<T>, Serializable {

    // 比较器
    private Comparator<? super T> comparator;

    // 根节点
    private transient RBNode<T> root = null;

    private static final boolean BLACK = true;

    private static final boolean RED = false;

    // 节点总数
    private transient int size = 0;

    public RBTree(Comparator<? super T> comparator) {
        this();
        this.comparator = comparator;
    }

    public RBTree() {

    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T geMax() {
        RBNode<T> t = root;
        T max = null;
        while (null != t) {
            max = t.value;
            t = t.right;
        }
        return max;
    }

    @Override
    public T geMin() {
        RBNode<T> t = root;
        T min = null;
        while (null != t) {
            min = t.value;
            t = t.left;
        }
        return min;
    }

    /**
     * 左旋
     *
     * @param node 旋转轴
     */
    private void leftRotate(RBNode<T> node) {
        if (node == null) {
            return;
        }
        // 右儿子
        RBNode rightNode = rightOf(node);

        // 轴的右儿子变为为 原来的右分支的左儿子， 左儿子不变
        node.right = leftOf(rightNode);

        if (rightNode.left != null) {
            rightNode.left.parent = node;
        }

        // 右儿子的父亲变为轴的父亲
        rightNode.parent = parentOf(node);
        if (parentOf(node) == null) {
            root = rightNode;
        } else if (leftOf(parentOf(node)) == node) {
            node.parent.left = rightNode;
        } else {
            node.parent.right = rightNode;
        }
        rightNode.left = node;
        node.parent = rightNode;
    }

    /**
     * 右旋
     *
     * @param node 旋转轴
     */
    private void rightRotate(RBNode<T> node) {
        if (null == node) {
            return;
        }

        // 左儿子
        RBNode<T> leftNode = leftOf(node);

        node.left = rightOf(leftNode);

        if (rightOf(leftNode) != null) {
            rightOf(leftNode).parent = node;
        }
        leftNode.parent = node.parent;

        if (parentOf(node) == null) {
            root = leftNode;
        } else if (rightOf(parentOf(node)) == node) {
            node.parent.right = leftNode;
        } else {
            node.parent.left = leftNode;
        }
        leftNode.right = node;
        node.parent = leftNode;
    }


    /**
     * 新增一个节点
     *
     * @param value
     * @return
     */
    @Override
    public boolean add(T value) {
        if (null == value) {
            throw new NullPointerException("树不支持新增null节点");
        }

        // 1.按二叉树新增元素到叶子节点位置
        // 1.2.如果本来是空的,直接将新节点作为根
        RBNode<T> t = root;
        if (null == t) {
            root = new RBNode<T>(value, null);
            size++;
            root.color = true;
            return true;
        }
        int result = 0;
        RBNode<T> parent;

        Comparator<? super T> c = comparator;

        // 如果传入了比较器，就是用该比较器进行比较处理
        if (c != null) {
            do {
                // 父节点设置为一个非空的节点
                parent = t;
                result = c.compare(value, t.value);
                if (result < 0) {  // 如果比父节点小,则走左分支，递归
                    t = t.left;
                } else if (result > 0) {  // 如果比父节点大,则走右分支，递归
                    t = t.right;
                } else {  // 如果相等，则不需要加入值 (TreeMap是直接覆盖value)
                    return false;
                }
            } while (null != t);
        } else {

            // 如果没有传入比较器，就是用默认的hashcode进行比较
            Comparable<? super T> cc = (Comparable<? super T>) value;
            do {

                // 父节点设置为一个非空的节点
                parent = t;
                result = cc.compareTo(t.value);
                if (result < 0) { // 如果比父节点小,则走左分支，递归
                    t = t.left;
                } else if (result > 0) { // 如果比父节点大,则走右分支，递归
                    t = t.right;
                } else { // 如果相等，则不需要加入值 (TreeMap是直接覆盖value)
                    return false;
                }
            } while (t != null);
        }

        // 创建新节点
        RBNode<T> newNode = new RBNode<>(value, parent);

        // 上面程序计算的比较结果作为确定新节点插入位置;
        if (result < 0) { // 如果比父节点小,则新增到左分支
            parent.left = newNode;
        } else { // 如果比父节点大,则新增到右分支
            parent.right = newNode;
        }

        // 修复树
        fixAfterInsert(newNode);

        // 树节点数量加1
        size++;
        return false;
    }

    /**
     * 新增树之后，会影响到红黑树的平衡，需要进行树结果调整
     *
     * @param node 需要调整的节点起点
     */
    private void fixAfterInsert(RBNode<T> node) {
        node.color = false;

        // 新增节点之后，只需要处理父节点为红色的情况，因为新加节点是红色，如果父节点是红色就破坏了红黑树特性
        // 父节点是黑色的不需要处理
        while (node != null && node != root && isRed(parentOf(node))) {

            // 如果父元素是祖父元素的左子节点
            if (parentOf(node) == leftOf(grandparentOf(node))) {
                RBNode<T> uncle = rightOf(grandparentOf(node));
                if (isRed(uncle)) {

                    // 如果叔元素是红色，则将父元素和叔元素置为黑色，祖父置为红色，当前节点变为祖父元素
                    setColor(uncle, BLACK);
                    setColor(parentOf(node), BLACK);
                    setColor(grandparentOf(node), RED);
                    node = grandparentOf(node);
                } else {

                    // 如果叔叔是黑色的
                    // 节点是父节点的右分支，则需要左旋转一下,父元素变换成新增元素位置，其他没有变化
                    if (node == rightOf(parentOf(node))) {
                        node = parentOf(node);
                        leftRotate(node);
                    }
                    setColor(parentOf(node), BLACK);
                    setColor(grandparentOf(node), RED);
                    rightRotate(grandparentOf(node));
                }
            } else {
                // 如果父元素是祖父元素的左子节点
                RBNode<T> uncle = leftOf(grandparentOf(node));
                if (isRed(uncle)) {
                    // 如果叔元素是红色，则将父元素和叔元素置为黑色，祖父置为红色，当前节点变为祖父元素
                    setColor(uncle, BLACK);
                    setColor(parentOf(node), BLACK);
                    setColor(grandparentOf(node), RED);
                    node = grandparentOf(node);
                } else {

                    // 如果叔叔是黑色的
                    // 节点是父节点的左分支，则需要右旋转一下,父元素变换成新增元素位置，其他没有变化
                    if (node == leftOf(parentOf(node))) {
                        node = parentOf(node);
                        rightRotate(node);
                    }
                    setColor(parentOf(node), BLACK);
                    setColor(grandparentOf(node), RED);
                    leftRotate(grandparentOf(node));
                }
            }
        }
        setColor(root, BLACK);
    }

    private boolean isBlack(RBNode<T> node) {
        return node == null || node.color == BLACK;
    }

    private boolean isRed(RBNode<T> node) {
        return node != null && node.color == RED;
    }

    private RBNode<T> rightOf(RBNode<T> node) {
        return node == null ? null : node.right;
    }

    private RBNode<T> leftOf(RBNode<T> node) {
        return node == null ? null : node.left;
    }

    private RBNode<T> parentOf(RBNode<T> node) {
        return node == null ? null : node.parent;
    }

    private RBNode<T> grandparentOf(RBNode<T> node) {
        return parentOf(parentOf(node));
    }

    /**
     * 删除一个值
     *
     * @param value
     * @return
     */
    @Override
    public boolean remove(T value) {

        // 1.查找元素
        RBNode<T> node = getNode(value);

        // 如果找不到，说明不存在
        if (null == node) {
            return false;
        }

        deleteNode(node);
        return true;
    }

    /**
     * 删除一个节点
     *
     * @param node
     */
    private void deleteNode(RBNode<T> node) {


        // 如果该节点有2个非叶子的儿子节点，则找到左儿子树的最大值或者右儿子树最小值，将最大值替换到需要删除的节点上
        if (leftOf(node) != null && rightOf(node) != null) {
            RBNode<T> m = successor(node);
            node.value = m.value;
            node = m;
        }

        // 从此处起转化为删除最多只有一个儿子的问题
        RBNode<T> child = leftOf(node) != null ? leftOf(node) : rightOf(node);
        if (null != child) {

            // 有一个儿子,把儿子取代父亲的位置, 有儿子必然是红色的儿子
            child.parent = node.parent;
            if (parentOf(node) == null) {
                root = child; //删除点是根的情况
            } else if (node == parentOf(node).left) {
                parentOf(node).left = child; //删除点是父节点的左儿子
            } else {
                parentOf(node).right = child; //删除点是父节点的右儿子
            }

            node.left = null;
            node.right = null;
            node.parent = null;

            // TreeMap中这里进行了一次修复方法调用，那个while循环不会进入，
            // 所以可以直接调用最后一句颜色渲染即可
            setColor(child, BLACK);

        } else if (null == parentOf(node)) {

            // 既没儿子也没父节点，则说明这是树的唯一元素，直接删除即可
            root = null;
        } else {


            // 没儿子有父节点
            if (isBlack(node)) {
                // 没有儿子,又是黑色的
                // 如果删除的是一个黑色节点，则破坏了红黑树特性，需要修复
                fixAfterDelete(node);
            }
            if (null != parentOf(node)) {
                // 如果删除的是一个红节点，直接删除即可
                if (leftOf(parentOf(node)) == node) {
                    parentOf(node).left = null;
                } else if (rightOf(parentOf(node)) == node) {
                    parentOf(node).right = null;
                }
                node.parent = null;
            }
        }
        size--;
    }

    private RBNode<T> successor(RBNode<T> node) {
        if (node.left != null) {
            RBNode<T> maxLeft = node.left;
            while (maxLeft.right != null) {
                maxLeft = maxLeft.right;
            }
            return maxLeft;
        } else {
            RBNode<T> minRight = node.right;
            while (minRight.left != null) {
                minRight = minRight.left;
            }
            return minRight;
        }
    }


    private static <T> boolean colorOf(RBNode<T> node) {
        return node == null ? BLACK : node.color;
    }


    private static <T> void setColor(RBNode<T> node, boolean color) {
        if (node == null) {
            return;
        }
        node.color = color;
    }

    /**
     * 删除后修复
     *
     * @param node 修复起点
     */
    private void fixAfterDelete(RBNode<T> node) {
        while (node != root && colorOf(node) == BLACK) {

            /*
             * 根据红黑树特性，如果该节点是黑色的节点，那一定有非叶子的兄弟节点
             * 因为不管父节点是黑色还是红色，为了保证路径上黑色点一样，只要有一个儿子存在，另一个儿子一定存在
             * 分两种情况，是父节点的左儿子 / 父节点的右儿子
             */

            if (node == leftOf(parentOf(node))) {

                // 是父节点的左儿子
                //获取兄弟节点
                RBNode<T> brother = rightOf(parentOf(node));

                /*
                 * situation-1:
                 *  兄弟的2个儿子全是黑色的，所以不管兄弟是红色还是黑色
                 *  （这里父节点必然是黑色）
                 *      如果：兄弟是红色的，则需要先在父节点左旋，使得兄弟变为黑色的，兄弟位置发生了变化
                 *      兄弟的2个儿子是黑色的也仍然城里，
                 *
                 *
                 *  下面的场景: 兄弟是红色的则需要处理
                 *  >>Brother = Brother(R)
                 *
                 *            Parent(B)                         Brother(B)
                 *    Delete(B)      Brother(R)       ==>  Parent(R)       Right(B)
                 *               Left(B)    Right(B)    Delete(B)  Left(B)
                 *
                 *
                 *  >>Brother = Left(B)
                 */
                if (colorOf(brother) == RED) {
                    setColor(brother, BLACK);
                    setColor(parentOf(node), RED);
                    leftRotate(parentOf(node));
                    brother = rightOf(parentOf(node)); //
                }


                // 如果兄弟节点2个儿子都是黑色的，
                // 则将兄弟转换为红色
                // Brother ==> Brother(R)
                if (colorOf(rightOf(brother)) == BLACK
                        && colorOf(leftOf(brother)) == BLACK) {
                    setColor(brother, RED);
                    node = parentOf(node); // 新的修复起点变为原来的父节点

                    // end situation-1
                } else {

                    /**
                     * situation-2: 上面兄弟2个儿子都是黑色的的逆命题是：兄弟节点的儿子至少有一个是红色
                     *
                     * 上面的转化之后兄弟节点必然是黑色的
                     *
                     *
                     * 如果父节点是黑色 叶子到父节点黑点个数为3
                     * 如果父节点是红色 叶子到父节点黑点个数为2
                     * 且最少是2个
                     *
                     * 大小关系可以列出来  D(代表这个节点以下缺少的一个黑色节点) < P(父节点) < BL(兄弟左儿子) < B(兄弟) < BR(兄弟右儿子)
                     * 则删除 D 后，只需要将BL当做新的parent即可
                     *
                     * 1.
                     * 1.1. 如果BR为黑色，则BL必然存在且是红色，先把BL放到上述的关系链直线中，通过右旋即可达到
                     * 1.2. 如果BR是红色，则不需要关心左儿子是否存在，因为已经保证至少有3个节点在一条直线了
                     * 2.
                     * 2.1  保证从父节点开始右侧一条直线上有3个节点之后，通过左旋，则分配了一个节点到左子树，
                     *      这样左子树删除了一个节点后，就补上了一个，平衡了
                     * 2.2  很简单的处理：通过将旋转到父节点的节点着色为父节点原来颜色，保证父节点不会对上面的节点造成影响，
                     *      然后将父节点强制转为黑色，则补上了由于左子树删除黑节点导致黑点数量减少的问题；
                     *      右侧原来3个点连线黑色点的个数为 1-2个，数量右原来的父节点颜色决定
                     *      只需要将新的父节点的有儿子转为黑色，那就必然满足了右子树黑点的数量不变
                     *
                     *
                     * 经过旋转与着色，删除节点后，最少的情况也会保留3个节点，仍然可以满足最多3个黑点个数
                     * 所以修复OK后，直接将节点指向根，强制结束修复结束
                     *
                     */
                    if (colorOf(rightOf(brother)) == BLACK) {
                        setColor(leftOf(brother), BLACK);
                        setColor(brother, RED);
                        rightRotate(brother);
                    }
                    setColor(brother, colorOf(parentOf(node)));
                    setColor(parentOf(node), BLACK);
                    setColor(rightOf(node), BLACK);
                    leftRotate(parentOf(node));

                    node = root;
                }

            } else {

                // 是父节点的右儿子
                // 获取兄弟节点
                RBNode<T> brother = leftOf(parentOf(node));

                // 兄弟是红色的，则父节点必然是黑色的，直接父节点右旋
                if (colorOf(brother) == RED) {
                    setColor(brother, BLACK);
                    setColor(parentOf(node), RED);
                    rightRotate(parentOf(node));

                    brother = leftOf(parentOf(node)); // 兄弟节点变为原来的兄弟节点的右儿子
                }

                if (colorOf(rightOf(brother)) == BLACK
                        && colorOf(leftOf(brother)) == BLACK) {
                    setColor(brother, RED);
                    node = parentOf(node); // 新的修复起点变为原来的父节点
                } else {


                    // 原理与 上面左子树删除黑点类似，只是方向相反

                    if (colorOf(leftOf(brother)) == BLACK) {
                        setColor(rightOf(brother), BLACK);
                        setColor(brother, RED);
                        leftRotate(brother);
                        brother = leftOf(parentOf(node));
                    }
                    setColor(brother, colorOf(parentOf(node)));
                    setColor(parentOf(node), BLACK);
                    setColor(leftOf(node), BLACK);
                    rightRotate(parentOf(node));
                    node = root;
                }
            }
        }
        setColor(node, BLACK);
    }

    /**
     * 获取一个节点左儿子的最大值节点
     *
     * @param node
     * @return
     */
    private RBNode<T> getMaxLeftNode(RBNode<T> node) {
        RBNode<T> maxNode = node.left;
        while (null != rightOf(maxNode)) {
            maxNode = rightOf(maxNode);
        }
        return maxNode;
    }


    /**
     * 获取一个节点右儿子的最小值节点
     *
     * @param node
     * @return
     */
    private RBNode<T> getMinRightNode(RBNode<T> node) {
        RBNode<T> maxNode = node.right;
        while (null != leftOf(maxNode)) {
            maxNode = leftOf(maxNode);
        }
        return maxNode;
    }


    /**
     * 是否包含值
     *
     * @param value
     * @return
     */
    @Override
    public boolean contain(T value) {
        return getNode(value) != null;
    }

    /**
     * 查找值
     *
     * @param value
     * @return
     */
    private RBNode<T> getNode(T value) {
        if (null == value) {
            throw new NullPointerException("删除的节点不能为null");
        }

        // 如果有比较器 则使用给定的比较器
        if (null != comparator) {
            Comparator<? super T> c = comparator;
            RBNode<T> parent = root;
            while (null != parent) {
                int result = c.compare(value, parent.value);
                if (result > 0) {
                    parent = rightOf(parent);
                } else if (result < 0) {
                    parent = leftOf(parent);
                } else {
                    return parent;
                }
            }
            return null;
        }
        Comparable<? super T> c = (Comparable<? super T>) value;
        RBNode<T> parent = root;
        while (null != parent) {
            int result = c.compareTo(parent.value);
            if (result > 0) {
                parent = rightOf(parent);
            } else if (result < 0) {
                parent = leftOf(parent);
            } else {
                return parent;
            }
        }
        return null;
    }


    static final class RBNode<T> implements Serializable {

        // 父节点
        RBNode<T> parent;

        // 节点值
        private T value;

        // 左子节点
        RBNode<T> left;

        // 右子节点
        RBNode<T> right;

        // 节点颜色 默认为红色
        Boolean color = RED;

        public RBNode() {
        }

        RBNode(T value, RBNode<T> parent) {
            this.value = value;
            this.parent = parent;
        }

        void transColor() {
            this.color = !color;
        }

        @Override
        public int hashCode() {
            return value == null ? 0 : value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RBNode)) {
                return false;
            }
            RBNode<T> o = (RBNode<T>) obj;
            return value == null ? o.value == null : value.equals(o.value);
        }
    }
}
