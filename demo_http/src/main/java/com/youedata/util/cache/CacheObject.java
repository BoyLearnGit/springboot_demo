package com.youedata.util.cache;


public class CacheObject {
	private String key;
	private Object value;
	private long liveEndTime;
	private long defaultLiveTime = 60000;//缓存有效时间单位为秒，默认60s
	public CacheObject(String key, Object object) {
		new CacheObject(key, object,defaultLiveTime );
	}
	public CacheObject(String key, Object object,long liveTime) {
		if(liveTime<1){
			liveTime = defaultLiveTime;
		}
		this.key = key;
		this.value = object;
		this.liveEndTime = System.currentTimeMillis()+liveTime;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	public long getLiveEndTime() {
		return liveEndTime;
	}
	public void setLiveEndTime(long liveEndTime) {
		this.liveEndTime = liveEndTime;
	}
	
}
