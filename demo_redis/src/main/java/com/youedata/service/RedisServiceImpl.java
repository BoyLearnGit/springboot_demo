package com.youedata.service;



import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;


@Service
public class RedisServiceImpl extends IRedisService<CodeInfo> {
    private String REDIS_KEY ;
    @Override
    public String getRedisKey() {
    	return this.REDIS_KEY;
    }
    public void setRedisKey(String key){
        this.REDIS_KEY=key;
    }
    public String  okksetRedisKey(String value){
    	this.REDIS_KEY = value;
    	return REDIS_KEY;
    }
    
	@Override
	public void put(String key, CodeInfo doamin, long expire) {
        hashOperations.put(getRedisKey(), key , doamin);
        if (expire != -1) {
            redisTemplate.expire(getRedisKey(), expire, TimeUnit.SECONDS);
        }
		
	}
}
