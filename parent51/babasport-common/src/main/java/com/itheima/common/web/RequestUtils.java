package com.itheima.common.web;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itheima.common.Constants;

/**
 * 获取或生成令牌
 * @author Larry
 *
 */
public class RequestUtils {
	public static String getCSESSIONID(HttpServletRequest request,HttpServletResponse response){
		//获取令牌
		Cookie[] cookies = request.getCookies();
		if(null != cookies && cookies.length > 0){
			for (Cookie cookie : cookies) {
				if(Constants.CSESSIONID.equals(cookie.getName())){
					//有直接使用
					return cookie.getValue();
				}
			}
		}
		//没有 生成一个
		String csessionid = UUID.randomUUID().toString().replaceAll("-", "");
		Cookie cookie = new Cookie(Constants.CSESSIONID,csessionid);
		//设置关闭浏览器失效
		cookie.setMaxAge(-1);
		//设置路径
		cookie.setPath("/");
		//写回到浏览器
		response.addCookie(cookie);
		//返回
		return csessionid;
	}
}
