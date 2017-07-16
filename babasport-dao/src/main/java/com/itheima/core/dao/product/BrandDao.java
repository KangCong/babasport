package com.itheima.core.dao.product;

import java.util.List;

import com.itheima.core.pojo.product.Brand;
import com.itheima.core.pojo.product.BrandQuery;

public interface BrandDao {
	//查询品牌列表
	public List<Brand> selectBrandListByQuery(BrandQuery brandQuery);
	
	//查询总数量
	public Integer totalCount(BrandQuery brandQuery);
	
	//查询品牌
	public Brand selectBrandById(Long id);
	
	//更新品牌信息
	public void editBrandById(Brand brand);
	
	//批量删除
	public void deleteMultiBrandById(Long[] ids);
}
