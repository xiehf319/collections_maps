package com.xiehf.ziglar.collections;

import java.util.List;

/**
 * Copyright (C), 2014-2015, 深圳云集智造系统技术有限公司
 *
 * @author xiehaifan  on 2017/11/6
 */
public interface BaseSet<T> {


    void add(T parent, T child);

    void add(T parent, List<T> children);

    int size();

    boolean contain();

    boolean isEmpty();

}
