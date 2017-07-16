package com.itheima.core.service.product;

import java.util.List;

import com.itheima.core.pojo.product.Product;
import com.itheima.core.pojo.product.ProductQuery;
import com.itheima.core.pojo.product.Sku;

import cn.itcast.common.page.Pagination;

public interface ProductService {
	// 查询商品列表带分页
	public Pagination selectByExample(String name, Long brandId, Boolean isShow, Integer pageNo);

	// 保存商品信息
	public void insertProduct(Product product);

	//商品上架
	public void isShowProduct(Long[] ids) throws Exception;

}
