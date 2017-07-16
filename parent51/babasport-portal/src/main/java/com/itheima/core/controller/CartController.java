package com.itheima.core.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itheima.common.Constants;
import com.itheima.common.web.JsonUtils;
import com.itheima.common.web.RequestUtils;
import com.itheima.core.pojo.order.Order;
import com.itheima.core.pojo.user.BuyerCart;
import com.itheima.core.pojo.user.BuyerItem;
import com.itheima.core.service.search.BuyerService;
import com.itheima.core.service.search.SessionProvider;

/**
 * 购物车
 * 
 * @author Larry
 *
 */
@Controller
public class CartController {

	@Autowired
	private SessionProvider sessionProvider;

	/**
	 * 添加商品到购物车
	 * 
	 * @param skuId
	 * @param amount
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/shopping/buyerCart")
	public String buyerCart(Long skuId, Integer amount, HttpServletRequest request, HttpServletResponse response) {
		BuyerCart buyerCart = null;
		// 1.获取Cookie
		Cookie[] cookies = request.getCookies();
		if (null != cookies && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				// 判断cookie中是否有购物车
				if (Constants.BUYER_CART.equals(cookie.getName())) {
					// 有购物车
					// 2.获取Cookie中的购物车
					buyerCart = JsonUtils.jsonToObject(cookie.getValue(), BuyerCart.class);
					break;
				}
			}
		}
		// 3.没有 创建购物车
		if (null == buyerCart) {
			buyerCart = new BuyerCart();
		}
		// 4.追加当前款
		if (null != skuId && null != amount) {
			buyerCart.addItem(skuId, amount);
		}

		// 判断用户是否登录
		String username = sessionProvider.getAttributeForUsername(RequestUtils.getCSESSIONID(request, response));
		if (null != username) {
			// 用户已登录
			// 5.将购物车添加到redis 清空Cookie
			buyerService.addBuyerCartToRedis(buyerCart, username);
			// 清空cookie
			Cookie cookie = new Cookie(Constants.BUYER_CART, null);
			cookie.setPath("/");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		} else {
			// 5.创建Cookie并添加最新购物车，写回浏览器
			Cookie cookie = new Cookie(Constants.BUYER_CART, JsonUtils.objectToJson(buyerCart));
			// 设置路径
			cookie.setPath("/");
			// 设置有效时间 一天
			cookie.setMaxAge(60 * 60 * 24);
			// 将cookie写回浏览器
			response.addCookie(cookie);
		}

		// 6.重定向到购物车页面
		return "redirect:/shopping/toCart";
	}

	@Autowired
	private BuyerService buyerService;

	/**
	 * 跳转到购物车页面
	 * 
	 * @param reqeust
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/shopping/toCart")
	public String toCart(HttpServletRequest request, HttpServletResponse response, Model model) {
		BuyerCart buyerCart = null;
		// 1.获取Cookie
		Cookie[] cookies = request.getCookies();
		if (null != cookies && cookies.length > 0) {
			// 2.获取Cookie中的购物车
			for (Cookie cookie : cookies) {
				if (Constants.BUYER_CART.equals(cookie.getName())) {
					buyerCart = JsonUtils.jsonToObject(cookie.getValue(), BuyerCart.class);
				}
			}
		}

		// 判断用户是否登录
		String username = sessionProvider.getAttributeForUsername(RequestUtils.getCSESSIONID(request, response));
		if(null != username){
			//已登录
			// 3.有 将购物车添加到redis 清空Cookie
			if(null != buyerCart){
				buyerService.addBuyerCartToRedis(buyerCart, username);
				// 清空cookie
				Cookie cookie = new Cookie(Constants.BUYER_CART, null);
				cookie.setPath("/");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
			// 4.从redis中将购物车取出来
			buyerCart = buyerService.selectBuyerCartFromRedis(username);
		}
		

		// 5.有 装购物车装满
		if (null != buyerCart) {
			List<BuyerItem> items = buyerCart.getItems();
			if (items.size() > 0) {
				for (BuyerItem item : items) {
					item.setSku(buyerService.selectSkuById(item.getSku().getId()));
				}
			}
		}
		// 6.带数据
		model.addAttribute("buyerCart", buyerCart);
		// 7.跳视图
		return "cart";
	}
	
	/**
	 * 点击去结算按钮
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/buyer/trueBuy")
	public String trueBuy(HttpServletRequest request,HttpServletResponse response,Model model){
		String username = sessionProvider.getAttributeForUsername(RequestUtils.getCSESSIONID(request, response));
//		2、	判断购物车中是否有商品 1）无商品 刷新购物车页面进行提示 2）有商品  继续判断
		BuyerCart buyerCart = buyerService.selectBuyerCartFromRedis(username);
		List<BuyerItem> items = buyerCart.getItems();
		//判断购物车中是否有商品
		if(items.size() > 0){
			//购物车中有商品
			Boolean flag = false;
//		3、	判断购物车中商品是否有货 1）无货 刷新购物车页面进行无货提示 2）全有货  真过了进入订单提交页面
			for (BuyerItem item : items) {
				item.setSku(buyerService.selectSkuById(item.getSku().getId()));
//		注意：购买数量大于库存数量视为无货
				if(item.getAmount() > item.getSku().getStock()){
					//无货
					item.setIsHave(false);
					flag = true;
				}
			}
			//购物车中有商品无货，跳转回购物车页面，并进行无货提示
			if(flag){
				model.addAttribute("buyerCart", buyerCart);
				return "cart";
			}
		}else{
			//购物车中没有商品，跳转到购物车页面
			return "redirect:/shopping/toCart";
		}
		//进入订单页面
		return "order";
	}

	
	@RequestMapping(value = "/buyer/submitOrder")
	public String submitOrder(Order order,Model model,HttpServletRequest request,HttpServletResponse response){
		//获取用户名
		String username = sessionProvider.getAttributeForUsername(RequestUtils.getCSESSIONID(request, response));
		//保存订单
		//清空redis中购物车
		Map<String, String> map = buyerService.insertOrder(order, username);
		model.addAttribute("oid", map.get("oid"));
		model.addAttribute("total", map.get("total"));
		//回显
		return "success";
	}
}
