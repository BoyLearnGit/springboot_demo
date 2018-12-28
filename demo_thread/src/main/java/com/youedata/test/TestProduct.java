package com.youedata.test;

/**
 * 消费者、生产者
 * @author zcc
 * @date 2018年1月1日
 */
public class TestProduct {
	
	/**
	 * 测试生产者消费者
	 * @param args
	 */
	public static void main(String[] args) {
		SyncStack syncStack = new SyncStack();
		Producer producer = new Producer(syncStack);
		Consumer consumer = new Consumer(syncStack);
		Thread tp = new Thread(producer);
		tp.start();
		Thread tc = new Thread(consumer);
		tc.start();
		
	}
}
	
	class Wotou {
		
		private int id;

		public Wotou() {
			
		}
		
		/**
		 * @param id
		 */
		public Wotou(int id) {
			super();
			this.id = id;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(int id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "Wotou [id=" + id + "]";
		}
		
	}
	
	//篮子
	class SyncStack {
		int index = 0;
		Wotou[] wotouArr = new Wotou[6];
		
		public synchronized void push(Wotou wotou) {
			while(index!=0&&index==6) {		////if(index == 0)--此处不应该用if，如果下面发生异常就是通知消费者
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.notify();		//叫醒一个正在我这等待的线程
			if(wotou.getId()==19){
				System.out.println(19);
			}
			wotouArr[index] = wotou;
			index++;
		}
		
		public synchronized Wotou pop() {
			while(index==0||wotouArr[index-1]==null) {					//if(index == 0)
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.notify();		//叫醒一个正在我这等待的线程
			Wotou wt=wotouArr[index-1];
			wotouArr[index-1]=null;
			index--;
			return wt;
		}
	}
	
	//生产者
	class Producer implements Runnable {
		SyncStack syncStack = null;
		
		public Producer(SyncStack syncStack) {
			super();
			this.syncStack = syncStack;
		}

		@Override
		public void run() {
			for(int i=0; i<20; i++) {
				Wotou wt = new Wotou(i);
				syncStack.push(wt);
				System.out.println("生产==" + wt);
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
		}
		
		
	}
	
	//消费者
	class Consumer implements Runnable {

		SyncStack syncStack = null;
		
		public Consumer(SyncStack syncStack) {
			super();
			this.syncStack = syncStack;
		}

		@Override
		public void run() {
			for(int i=0; i<20; i++) {
				Wotou wotou = syncStack.pop();
				System.out.println("消费-" + wotou);
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
			
		}
		
	}

