package com.youedata.thread;

/**
 * @author: lyl
 * @date: 2018/9/17 14:22.
 */
public class MyTask implements Runnable{
    private int tag;

    public MyTask(int tag) {
        this.tag = tag;
    }

    @Override
    public void run() {
        System.out.println("开始我的任务"+tag);
    }
}
