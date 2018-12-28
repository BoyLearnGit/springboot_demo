package com.youedata;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: lyl
 * @date: 2018/12/4 13:55.
 */
public class Test {
    public  volatile  int inc = 100;
    public  volatile  Integer inc2 = 100;
    public  AtomicInteger inc1 = new AtomicInteger(100);
    Lock lock = new ReentrantLock();

    public  void increase() {
        inc--;
    }
    public  void increase4() {
        inc2--;
    }
    public  void increase1() {
        lock.lock();
        inc--;
        lock.unlock();
    }
    public  void increase2() {
        lock.lock();
        inc1.decrementAndGet();
        lock.unlock();
    }
    public synchronized void increase3() {
        inc--;
    }


    public static void main(String[] args) {
        long start=System.currentTimeMillis();
        Test test=new Test();
        for(int i=0;i<10;i++){
            MyThread myThread1=new MyThread(test);
            Thread th1=new Thread(myThread1);
            th1.start();
        }


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("最终结果是："+"普通int："+test.inc+"乐观锁atomic："+test.inc1+"普通integer："+test.inc2);
        System.out.println(System.currentTimeMillis()-start);
    }
}
//public class Test {
//    public volatile int inc = 0;
//
//    public void increase() {
//        inc++;
//    }
//
//    public static void main(String[] args) {
//        final Test test = new Test();
//        for(int i=0;i<10;i++){
//            new Thread(){
//                @Override
//                public void run() {
//                    for(int j=0;j<1000;j++) {
//                        test.increase();
////                        System.out.println(test.inc);
//                    }
//                };
//            }.start();
//        }
//
//        while(Thread.activeCount()>1) { //保证前面的线程都执行完
//            Thread.yield();
//        }
//        System.out.println(test.inc);
//    }
//}
