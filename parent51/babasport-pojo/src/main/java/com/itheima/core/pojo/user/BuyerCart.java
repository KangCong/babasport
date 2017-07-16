package com.itheima.core.pojo.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itheima.core.pojo.product.Sku;

/**
 * 购物车
 * @author Larry
 *
 */
public class BuyerCart implements Serializable{

	private static final long serialVersionUID = 1L;
	//购物车项
	private List<BuyerItem> items = new ArrayList<BuyerItem>();
	public List<BuyerItem> getItems() {
		return items;
	}
	public void setItems(List<BuyerItem> items) {
		this.items = items;
	}
	
	//添加商品到购物车
	public void addItem(Long skuId, Integer amount) {
		BuyerItem item = new BuyerItem();
		Sku sku = new Sku();
		sku.setId(skuId);
		item.setAmount(amount);
		item.setSku(sku);
		if(items.contains(item)){
			//追加数量
			for (BuyerItem buyerItem : items) {
				if(buyerItem.equals(item)){
					buyerItem.setAmount(buyerItem.getAmount() + amount);
				}
			}
		}else{
			//新的商品  添加到购物车
			items.add(item);
		}
	}
	
	
	//商品数量
	@JsonIgnore
	public Integer getProductAmount(){
		Integer result = 0;
		for (BuyerItem item : items) {
			result += item.getAmount();
		}
		return result;
	}
	//商品金额
	@JsonIgnore
	public Float getProductPrice(){
		Float result = 0f;
		for (BuyerItem item : items) {
			result += item.getAmount()*item.getSku().getPrice();
		}
		return result;
	}
	//运费
	@JsonIgnore
	public Float getFee(){
		Float result = 0f;
		if(getProductPrice() < 99){
			result = 6f;
		}
		return result;
	}
	//总计
	@JsonIgnore
	public Float getTotalPrice(){
		return getProductPrice() + getFee();
	}

}
