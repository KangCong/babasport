package com.itheima.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.common.Constants;
import com.itheima.core.dao.order.DetailDao;
import com.itheima.core.dao.order.OrderDao;
import com.itheima.core.dao.product.ColorDao;
import com.itheima.core.dao.product.ProductDao;
import com.itheima.core.dao.product.SkuDao;
import com.itheima.core.dao.user.BuyerDao;
import com.itheima.core.pojo.order.Detail;
import com.itheima.core.pojo.order.Order;
import com.itheima.core.pojo.product.Sku;
import com.itheima.core.pojo.user.Buyer;
import com.itheima.core.pojo.user.BuyerCart;
import com.itheima.core.pojo.user.BuyerItem;
import com.itheima.core.service.search.BuyerService;

import redis.clients.jedis.Jedis;

@Service("buyerService")
@Transactional
public class BuyerServiceImpl implements BuyerService {
	
	@Autowired
	private BuyerDao buyerDao;
	@Autowired
	private Jedis jedis;
	
	//通过用户名查询用户
	public Buyer selectBuyerByUsername(String username){
		Buyer buyer = null;
		//从redis中获取用户. 注册新用户的时候，用户的id是redis生成的。 将用户的id和用户名保存到redis中，用于登录的时候获得用户id
		String id = jedis.get(username);
		if(null != id){
			buyer = buyerDao.selectByPrimaryKey(Long.parseLong(id));
		}
		return buyer;
	}
	
	@Autowired
	private SkuDao skuDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ColorDao colorDao;
	
	//通过库存id查询库存对象
	public Sku selectSkuById(Long id){
		Sku sku = skuDao.selectByPrimaryKey(id);
		sku.setProduct(productDao.selectByPrimaryKey(sku.getProductId()));
		sku.setColor(colorDao.selectByPrimaryKey(sku.getColorId()));
		
		return sku;
	}
	
	//将购物车添加到redis中
	public void addBuyerCartToRedis(BuyerCart buyerCart,String username){
		List<BuyerItem> items = buyerCart.getItems();
		if(items.size() > 0){
			for (BuyerItem item : items) {
				jedis.hincrBy(Constants.BUYER_CART + ":" + username,
						String.valueOf(item.getSku().getId()), item.getAmount());
			}
		}
	}
	
	//将购物车从redis中取出来
	public BuyerCart selectBuyerCartFromRedis(String username){
		BuyerCart buyerCart = null;
		if(null != username){
			Map<String, String> hgetAll = jedis.hgetAll(Constants.BUYER_CART + ":" + username);
			if(null != hgetAll && hgetAll.size() > 0){
				buyerCart = new BuyerCart();
				Set<Entry<String, String>> entrySet = hgetAll.entrySet();
				for (Entry<String, String> entry : entrySet) {
					buyerCart.addItem(Long.parseLong(entry.getKey()), Integer.parseInt(entry.getValue()));
				}
			}
		}
		return buyerCart;
	}
	
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private DetailDao detailDao;
	
	//保存订单
	public Map<String,String> insertOrder(Order order,String username){
		Map<String,String> map = new HashMap<>();
		//订单表号由redis生成
		Long oid = jedis.incr("oid");
		order.setId(oid);
		map.put("oid", String.valueOf(oid));
		//用户的购物车
		BuyerCart buyerCart = selectBuyerCartFromRedis(username);
		List<BuyerItem> items = buyerCart.getItems();
		//将购物车装满
		for (BuyerItem item : items) {
			item.setSku(selectSkuById(item.getSku().getId()));
		}
		//运费，由购物车提供
		order.setDeliverFee(buyerCart.getFee());
		//总计 由购物车提供
		order.setTotalPrice(buyerCart.getTotalPrice());
		map.put("total", String.valueOf(order.getTotalPrice()));
		//订单金额（商品金额） 由购物车提供
		order.setOrderPrice(buyerCart.getProductPrice());
//		支付方式    由页面传递
//		支付要求    0现金 1 POS
//		留言       
//		送货方式   略  
//		电话确认   略
//		支付状态   :0到付1待付款,2已付款,3待退款,4退款成功,5退款失败
		if(order.getPaymentWay() == 0){
			//支付方式为到付，修改支付状态为到付
			order.setIsPaiy(0);
		}else{
			order.setIsPaiy(1);
		}
//		订单状态 0:提交订单 1:仓库配货 2:商品出库 3:等待收货 4:完成 5待退货 6已退货
		order.setOrderState(0);
//		时间：     由程序提供
		order.setCreateDate(new Date());
//		用户ID   由程序提供
		String buyerId = jedis.get(username);
		order.setBuyerId(Long.parseLong(buyerId));
		
		//保存订单
		orderDao.insertSelective(order);
		
		//保存订单项请
		for (BuyerItem item : items) {
			Detail detail = new Detail();
			//1.id
			//2.订单id
			detail.setOrderId(order.getId());
			//商品编号
			detail.setProductId(item.getSku().getProductId());
			//商品名称
			detail.setProductName(item.getSku().getProduct().getName());
			//颜色名称
			detail.setColor(item.getSku().getColor().getName());
			//尺码
			detail.setSize(item.getSku().getSize());
			//价格
			detail.setPrice(item.getSku().getPrice());
			//数量
			detail.setAmount(item.getAmount());
			//图片
			
			detailDao.insertSelective(detail);
		}
		
		//清空redis中的购物车
		jedis.del(Constants.BUYER_CART + ":" + username);
		
		return map;
	}
}
