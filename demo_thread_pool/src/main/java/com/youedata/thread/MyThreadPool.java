package com.youedata.thread;

import java.util.concurrent.*;

/**
 * @author: lyl
 * @date: 2018/9/17 14:08.
 */
public class MyThreadPool {
    public static void main(String[] args) {
//        ArrayBlockingQueue;
//        LinkedBlockingQueue;
//        SynchronousQueue;
// ArrayBlockingQueue和PriorityBlockingQueue使用较少，一般使用LinkedBlockingQueue和Synchronous。线程池的排队策略与BlockingQueue有关。
        BlockingQueue queue=new LinkedBlockingQueue(5);
//        queue.add(new MyTask(1));
//        queue.add(new MyTask(2));
//        queue.add(new MyTask(3));
//        queue.add(new MyTask(4));
//        queue.add(new MyTask(5));
        ThreadPoolExecutor executor=new ThreadPoolExecutor(10,20,300, TimeUnit.SECONDS,queue);
        executor.execute(new MyTask(1));
    }
}
