package com.xiehf.ziglar.test;

import com.xiehf.ziglar.maps.RBTree;

import java.util.Random;
import java.util.Scanner;

/**
 * Created by xiehaifan on 2017/9/22.
 */
public class TestTree {

    public static void main(String[] args) {


        RBTree rb = new RBTree();
        Scanner scanner = new Scanner(System.in);
        int t = 0;
        while (t < 200){
            int i = new Random().nextInt(500);
            rb.add(i);
            t++;
        }
        System.out.println(rb);
        while(t>0){
            int i = new Random().nextInt(500);
            rb.remove(i);
            t--;
        }
    }
}
