package com.youedata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: lyl
 * @date: 2018/12/4 13:49.
 */
public class MyThread implements Runnable{

    private Logger logger= LoggerFactory.getLogger(MyThread.class);

    private Test test;

    public MyThread(Test test) {
        this.test = test;
    }

    @Override
    public void run() {
        long start=System.currentTimeMillis();
        //任务处理
        logger.info("start thread");
        for(int i=0;i<5;i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //普通的自减方法int
            test.increase();
            //普通的自减方法integer
//            test.increase4();
            //同步锁方法
//            test.increase3();
            //ACS乐观锁方法
//            test.increase2();
            //ReentrantLock方法
//            test.increase1();
            logger.info("普通int"+test.inc+"普通integer"+test.inc2+"乐观锁atomic"+test.inc1);
        }
        logger.info("end thread");
//        System.out.println("耗费时间："+(System.currentTimeMillis()-start));
    }
}
