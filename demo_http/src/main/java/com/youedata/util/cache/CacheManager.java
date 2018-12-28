package com.youedata.util.cache;


import com.youedata.util.Const;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 单例的缓存类
 * 
 */
public class CacheManager{
	private  final static int MAX_SIZE = 100000;//默认缓存大小
	private  long ALIVE_TIME = -1;
	// 用来存放cache数据
	static LinkedHashMap<String, CacheObject> cache = new LinkedHashMap<String, CacheObject>(2000, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<String, CacheObject> eldest) {
			return size() > MAX_SIZE;
		}
	};
	private static final CacheManager instance = new CacheManager();

	private CacheManager() {

	}

	public static CacheManager getInstance() {
		long msec = -1;
		try {
			String s = System.getProperty(Const.CACHE_MAX_MSEC);
			if(s!=null){
				msec = Long.parseLong(System.getProperty(Const.CACHE_MAX_MSEC));
			}
		} catch (Exception e) {
			
		}
		if(msec>0){
			instance.setAliveTime(msec);
		}
		return instance;
	}

	public void put(String key, Object object) {
		CacheObject cacheObject;
		if(ALIVE_TIME>0){
			cacheObject = new CacheObject(key, object,ALIVE_TIME);
		}else{
			cacheObject = new CacheObject(key, object);
		}
		cache.put(key, cacheObject);
	}

	public Object get(String key) {
		CacheObject cacheObject = cache.get(key);
		if (cacheObject != null) {
			if(System.currentTimeMillis()<=cacheObject.getLiveEndTime()){
				return cacheObject.getValue();
			}
		}
		return null;
	}

	public void remove(String key) {
		cache.remove(key);
	}

	public void clear() {
		cache.clear();
	}
	public int getCount() {
		return cache.size();
	}

	public void setAliveTime(long msec) {
		this.ALIVE_TIME = msec;
	}
}
