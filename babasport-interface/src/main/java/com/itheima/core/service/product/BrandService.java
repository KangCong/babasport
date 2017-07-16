package com.itheima.core.service.product;

import java.util.List;

import com.itheima.core.pojo.product.Brand;
import com.itheima.core.pojo.product.BrandQuery;

import cn.itcast.common.page.Pagination;

public interface BrandService {
	// 查询品牌列表
	public List<Brand> selectBrandListByQuery(String name, Integer isDisplay);

	// 查询品牌列表带分页
	public Pagination selectPaginationListByQuery(String name, Integer isDisplay, Integer pageNo);

	// 查询品牌
	public Brand selectBrandById(Long id);

	// 更新品牌信息
	public void editBrandById(Brand brand);

	// 批量删除
	public void deleteMultiBrandById(Long[] ids);
}
