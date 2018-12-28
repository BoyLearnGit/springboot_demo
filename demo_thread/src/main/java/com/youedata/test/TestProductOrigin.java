package com.youedata.test;

/**
 * 消费者、生产者
 * @author zcc
 * @date 2018年1月1日
 */
public class TestProductOrigin {
	
	/**
	 * 测试生产者消费者
	 * @param args
	 */
	public static void main(String[] args) {
		SyncStackOrigin SyncStackOrigin = new SyncStackOrigin();
		ProducerOrigin ProducerOrigin = new ProducerOrigin(SyncStackOrigin);
		ConsumerOrigin ConsumerOrigin = new ConsumerOrigin(SyncStackOrigin);
		Thread tp = new Thread(ProducerOrigin);
		tp.start();
		Thread tc = new Thread(ConsumerOrigin);
		tc.start();
		
	}
}
	
	class WotouOrigin {
		
		private int id;

		public WotouOrigin() {
			
		}
		
		/**
		 * @param id
		 */
		public WotouOrigin(int id) {
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
			return "WotouOrigin [id=" + id + "]";
		}
		
	}
	
	//篮子
	class SyncStackOrigin {
		int index = 0;
		WotouOrigin[] WotouOriginArr = new WotouOrigin[6];
		
		public synchronized void push(WotouOrigin WotouOrigin) {
			while(index == WotouOriginArr.length) {		////if(index == 0)--此处不应该用if，如果下面发生异常就是通知消费者
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.notify();		//叫醒一个正在我这等待的线程
			WotouOriginArr[index] = WotouOrigin;
			System.out.println("生产==" + WotouOrigin);
			index++;
		}
		
		public synchronized WotouOrigin pop() {
			while(index == 0) {					//if(index == 0)
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.notify();		//叫醒一个正在我这等待的线程
			index--;
			return WotouOriginArr[index];
		}
	}
	
	//生产者
	class ProducerOrigin implements Runnable {
		SyncStackOrigin SyncStackOrigin = null;
		
		public ProducerOrigin(SyncStackOrigin SyncStackOrigin) {
			super();
			this.SyncStackOrigin = SyncStackOrigin;
		}

		@Override
		public void run() {
			for(int i=0; i<20; i++) {
				WotouOrigin wt = new WotouOrigin(i);
				SyncStackOrigin.push(wt);
//				System.out.println("生产==" + wt);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	//消费者
	class ConsumerOrigin implements Runnable {

		SyncStackOrigin SyncStackOrigin = null;
		
		public ConsumerOrigin(SyncStackOrigin SyncStackOrigin) {
			super();
			this.SyncStackOrigin = SyncStackOrigin;
		}

		@Override
		public void run() {
			for(int i=0; i<20; i++) {
				WotouOrigin WotouOrigin = SyncStackOrigin.pop();
				System.out.println("消费-" + WotouOrigin);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}

