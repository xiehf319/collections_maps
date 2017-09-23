package com.xiehf.ziglar.test;

import com.xiehf.ziglar.maps.RBTree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by xiehaifan on 2017/9/22.
 */
public class TestTree {

    public static void main(String[] args) {
        TestTree tt = new TestTree();

        InnerClass in = new TestTree.InnerClass(22);

        System.out.println("调用前: " + in.value);

        tt.reset(in);

        System.out.println("调用后: " + in.value);

        int a = 2;
        System.out.println("重置前:" + a);
        tt.resetInt(a);
        System.out.println("重置后:" + a);


        Map<Integer, String> map = new HashMap<>();
        System.out.println(map.size());
        tt.setMap(map);
        System.out.println(map.size());
    }


    void reset(InnerClass innerClass) {
        innerClass.value= 33;
        System.out.println("调用中:" + innerClass.value);
    }

    void resetInt(int a){
        a = 3;
    }

    void setMap(Map<Integer, String> map ){
        map.put(1, "22");
    }

    static class InnerClass{

        int value;

        public InnerClass() {
        }

        public InnerClass(int value) {
            this.value = value;
        }
    }
}
