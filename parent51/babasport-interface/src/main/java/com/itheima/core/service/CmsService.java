package com.itheima.core.service;

import java.util.List;

import com.itheima.core.pojo.product.Product;
import com.itheima.core.pojo.product.Sku;

public interface CmsService {
	// 通过id查询商品信息
	public Product selectProductById(Long id);

	// 通过id查询商品详情sku，只查询有货的
	public List<Sku> selectSkuByProductId(Long id);
}
