package com.itheima.core.service.search;

import java.util.Map;

import com.itheima.core.pojo.order.Order;
import com.itheima.core.pojo.product.Sku;
import com.itheima.core.pojo.user.Buyer;
import com.itheima.core.pojo.user.BuyerCart;

public interface BuyerService {
	// 通过用户名查询用户
	public Buyer selectBuyerByUsername(String username);

	// 通过库存id查询库存对象
	public Sku selectSkuById(Long id);

	// 将购物车添加到redis中
	public void addBuyerCartToRedis(BuyerCart buyerCart, String username);

	// 将购物车从redis中取出来
	public BuyerCart selectBuyerCartFromRedis(String username);

	// 保存订单
	public Map<String, String> insertOrder(Order order, String username);
}
