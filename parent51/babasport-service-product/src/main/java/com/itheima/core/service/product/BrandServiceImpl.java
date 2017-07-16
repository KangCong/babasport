package com.itheima.core.service.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.core.dao.product.BrandDao;
import com.itheima.core.pojo.product.Brand;
import com.itheima.core.pojo.product.BrandQuery;

import cn.itcast.common.page.Pagination;
import redis.clients.jedis.Jedis;

@Service("brandService")
@Transactional
public class BrandServiceImpl implements BrandService {

	@Autowired
	private BrandDao brandDao;

	/**
	 * 查询品牌列表
	 */
	public List<Brand> selectBrandListByQuery(String name, Integer isDisplay) {
		BrandQuery brandQuery = new BrandQuery();
		if (null != name) {
			brandQuery.setName(name);
		}
		if (null != isDisplay) {
			brandQuery.setIsDisplay(isDisplay);
		} else {
			brandQuery.setIsDisplay(1);
		}
		return brandDao.selectBrandListByQuery(brandQuery);
	}

	/**
	 * 分页查询品牌列表
	 */
	public Pagination selectPaginationListByQuery(String name, Integer isDisplay, Integer pageNo) {
		BrandQuery brandQuery = new BrandQuery();
		StringBuilder params = new StringBuilder();
		// 设置当前页
		brandQuery.setPageNo(Pagination.cpn(pageNo));
		// 设置页面大小
		brandQuery.setPageSize(2);

		if (null != name) {
			brandQuery.setName(name);
			params.append("bname=").append(name);
		}
		if (null != isDisplay) {
			brandQuery.setIsDisplay(isDisplay);
			params.append("&idDisplay=").append(isDisplay);
		} else {
			brandQuery.setIsDisplay(1);
			params.append("&idDisplay=").append(1);
		}

		// 创建分页对象
		Pagination pagination = new Pagination();
		// 设置当前页
		pagination.setPageNo(brandQuery.getPageNo());
		// 设置页面大小
		pagination.setPageSize(brandQuery.getPageSize());
		// 设置总记录数
		pagination.setTotalCount(brandDao.totalCount(brandQuery));
		// 设置每页数据
		pagination.setList(brandDao.selectBrandListByQuery(brandQuery));

		String url = "/brand/list.do";
		pagination.pageView(url, params.toString());

		return pagination;
	}

	/**
	 * 根据id查询品牌信息
	 */
	public Brand selectBrandById(Long id) {

		return brandDao.selectBrandById(id);

	}

	@Autowired
	private Jedis jedis;
	
	/**
	 * 更新品牌信息
	 */
	public void editBrandById(Brand brand) {
		brandDao.editBrandById(brand);
		//将更新后的品牌信息同步到redis中. 放在更新品牌信息下边,用来让redis操作也支持mysql的事务. 因为spring的aop方式实现事务
		jedis.hset("brand", String.valueOf(brand.getId()), brand.getName());
	}

	/**
	 * 批量删除
	 */
	public void deleteMultiBrandById(Long[] ids){
		brandDao.deleteMultiBrandById(ids);
		//从redis中删除品牌
		for (Long id : ids) {
			jedis.hdel("brand", String.valueOf(id));
		}
	}
}
