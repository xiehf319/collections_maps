package com.xiehf.ziglar.maps;

/**
 * 二叉排序树：
 * 若任意节点的左子树不空，则左子树上所有结点的值均小于它的根结点的值；
 * 若任意节点的右子树不空，则右子树上所有结点的值均大于它的根结点的值；
 * 任意节点的左、右子树也分别为二叉查找树。
 * 没有键值相等的节点（no duplicate nodes）
 */
public interface BinarySortTree<T> {

    /**
     * 新增节点
     * @param t 需要新增的值
     * @return true:新增成功， false:已存在 新增失败
     */
    boolean add(T t);

    /**
     * 新增节点
     * @param t 需要删除的值
     * @return true:删除成功， false:不存在 新增失败
     */
    boolean remove(T t);

    /**
     * 是否包含节点
     * @param t
     * @return
     */
    boolean contain(T t);

    /**
     * 树的节点数量
     * @return
     */
    int size();

    /**
     * 获取最大值
     * @return
     */
    T geMax();


    /**
     * 获取最小值
     * @return
     */
    T geMin();
}
