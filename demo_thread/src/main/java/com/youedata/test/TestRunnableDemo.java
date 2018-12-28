package com.youedata.test;

/**
 * @author zcc
 * @date 2017年12月1日
 */
public class TestRunnableDemo {

    public static void main(String args[]) {
        RunnableDemo R1 = new RunnableDemo("Thread-1one");
        R1.start();

        RunnableDemo R2 = new RunnableDemo("Thread-2two");
        R2.start();

		/*for(int i=0; i<50; i++) {
			RunnableDemo tmp = new RunnableDemo(i+ "");
			tmp.start();
		}*/
    }
}
