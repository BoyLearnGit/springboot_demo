package com.example.demo;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author: lyl
 * @date: 2018/8/16 11:25.
 */
public class Test {

    static final BlockingQueue<Runnable> queue=new SynchronousQueue<Runnable>();

    public static void main(String[] args) {
        int i=3;
        System.out.println(i<<2);
        Map a=new HashMap();
        java.util.concurrent.ConcurrentHashMap m=new ConcurrentHashMap();
        ThreadPoolExecutor executor=new ThreadPoolExecutor(10,600,30,TimeUnit.SECONDS,queue,new Handler());
        executor.execute(new Thread());
    }

    static class Handler implements RejectedExecutionHandler{

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            r.run();
        }
    }

    static class Factory implements ThreadFactory{

        @Override
        public Thread newThread(Runnable r) {
            return null;
        }
    }
}
