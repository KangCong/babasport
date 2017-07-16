package com.itheima.core.service.search;

public interface SessionProvider {
	// 保存用户名到redis中 key--令牌
	public void setAttributeForUsername(String key, String value);

	// 从redis中获取用户名
	public String getAttributeForUsername(String key);
}
