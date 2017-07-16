package com.itheima.core.service.search;

import java.util.List;

import com.itheima.core.pojo.product.Brand;

import cn.itcast.common.page.Pagination;

public interface SearchService {
	//关键字查询商品
	public Pagination searchProductByKeyword(String keyword,String price,String brandId,Integer pageNo) throws Exception;
	//从redis中查询品牌列表
	public List<Brand> selectBrandListFromRedis();
	//将上架商品添加到索引库
	public void addProductToSolr(Long id) throws Exception;
}
