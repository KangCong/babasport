package com.itheima.core.message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.itheima.common.web.RequestUtils;
import com.itheima.core.service.search.SessionProvider;
/**
 * 验证用户是否登录的拦截器
 * @author Larry
 *
 */
public class LoginInterceptor implements HandlerInterceptor{

	@Autowired
	private SessionProvider sessionProvider;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//获取登录的用户名
		String username = sessionProvider.getAttributeForUsername(RequestUtils.getCSESSIONID(request, response));
		//如果用户没登录
		if(null == username){
			//跳转到登录界面
			response.sendRedirect("http://localhost:8082/login.aspx?ReturnUrl=http://localhost:8881");
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

}
