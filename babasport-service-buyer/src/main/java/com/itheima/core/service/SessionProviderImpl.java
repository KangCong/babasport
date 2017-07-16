package com.itheima.core.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.itheima.common.Constants;
import com.itheima.core.service.search.SessionProvider;

import redis.clients.jedis.Jedis;
/**
 * 保存session 到redis
 * @author Larry
 *
 */

public class SessionProviderImpl implements SessionProvider {
	
	@Autowired
	private Jedis jedis;
	//自定义有效时间  单位分钟
	private Integer exp = 30;
	public void setExp(Integer exp) {
		this.exp = exp;
	}

	//保存用户名到redis中  key--令牌
	public void setAttributeForUsername(String key,String value){
		jedis.set(key + ":" + Constants.USER_SESSION, value);
		//设置有效时间
		jedis.expire(key + ":" + Constants.USER_SESSION, 60 * exp);
	}
	
	//从redis中获取用户名
	public String getAttributeForUsername(String key){
		
		String username = jedis.get(key + ":" + Constants.USER_SESSION);
		if(null != username){
			//重新设置有效时间
			jedis.expire(key + ":" + Constants.USER_SESSION, 60 * exp);
		}
		return username;
	}
}
