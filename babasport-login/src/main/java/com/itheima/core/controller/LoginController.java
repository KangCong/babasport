package com.itheima.core.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itheima.common.web.RequestUtils;
import com.itheima.core.pojo.user.Buyer;
import com.itheima.core.service.search.BuyerService;
import com.itheima.core.service.search.SessionProvider;

/**
 * 用户登录
 * 
 * @author Larry
 *
 */
@Controller
public class LoginController {
	
	@Autowired
	private BuyerService buyerService;
	@Autowired
	private SessionProvider sessionProvider;
	
	/**
	 * 跳转到登录页面
	 * @return
	 */
	@RequestMapping(value = "/login.aspx", method = RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	/**
	 * 用户登录
	 * @param username
	 * @param password
	 * @param ReturnUrl
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/login.aspx", method = RequestMethod.POST)
	public String login(String username,String password,String ReturnUrl,Model model,
			HttpServletRequest request,HttpServletResponse response) {
		//用户名不能为空
		if(null != username){
			//密码不能为空
			if(null != password){
				Buyer buyer = buyerService.selectBuyerByUsername(username);
				//用户名不正确
				if(null != buyer){
					//密码不正确
					if(buyer.getPassword().equals(encodePassword(password))){
						//如果密码正确，将用户信息保存到session中
						sessionProvider.setAttributeForUsername(
								RequestUtils.getCSESSIONID(request, response), username);
						//跳转到登录前的页面
						return "redirect:" + ReturnUrl;
					}else{
						model.addAttribute("error", "密码不正确");
					}
				}else{
					model.addAttribute("error", "用户名不正确");
				}
			}else{
				model.addAttribute("error", "密码不能为空");
			}
		}else{
			model.addAttribute("error", "用户名不能为空");
		}
		
		return "login";
	}
	
	/**
	 * 密码加密 MD5 + 十六进制
	 * @param password
	 * @return
	 */
	public String encodePassword(String password){
		String algorithm = "MD5";
		//为密码加盐    itheima+password+51
		String p = "itheima" + password + "51";
		char[] encodeHex = null;
		try {
			MessageDigest instance = MessageDigest.getInstance(algorithm);
			//MD5加密
			byte[] digest = instance.digest(p.getBytes());
			//十六进制加密
			encodeHex = Hex.encodeHex(digest);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new String(encodeHex);
	}
	public static void main(String[] args) {
		LoginController l = new LoginController();
		String p = l.encodePassword("123456");
		System.out.println(p);
	}
	//判断用户是否登录
	@RequestMapping(value = "/isLogin.aspx")
	public @ResponseBody
	MappingJacksonValue isLogin(String callback,HttpServletRequest request,HttpServletResponse response,Model model){
		Integer result = 0;
		//判断是否登录
		String username = sessionProvider.getAttributeForUsername(RequestUtils.getCSESSIONID(request, response));
		if(null != username){
			result = 1; 
		}
		MappingJacksonValue mjv = new MappingJacksonValue(result);
		mjv.setJsonpFunction(callback);
		model.addAttribute("user", username);
		return mjv;
	}
	
}
