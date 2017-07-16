package com.itheima.core.service.product;

import java.util.List;

import com.itheima.core.pojo.product.Sku;

public interface SkuService {
	// 添加库存
	public void insertSku(Sku sku);

	// 查询商品库存信息
	public List<Sku> selectSkuByProductId(Long productId);

	// 修改商品库存信息
	public void updateSkuById(Sku sku);
}
