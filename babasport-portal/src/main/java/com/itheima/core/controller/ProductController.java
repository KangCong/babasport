package com.itheima.core.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itheima.core.pojo.product.Brand;
import com.itheima.core.pojo.product.Color;
import com.itheima.core.pojo.product.Product;
import com.itheima.core.pojo.product.Sku;
import com.itheima.core.service.CmsService;
import com.itheima.core.service.search.SearchService;

import cn.itcast.common.page.Pagination;

/**
 * 前台
 * 搜索
 * 购物车
 * 
 * @author Larry
 *
 */
@Controller
public class ProductController {

	/**
	 * 首页
	 * @return
	 */
	@RequestMapping(value="/")
	public String index(){
		return "index";
	}
	
	@Autowired
	private SearchService searchService;
	
	/**
	 * 搜索商品
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/Search")
	public String Search(String keyword,String price,String brandId,Integer pageNo,Model model) throws Exception{
		//查询品牌信息
		List<Brand> brands = searchService.selectBrandListFromRedis();
		//关键词查询商品
		Pagination pagination = searchService.searchProductByKeyword(keyword,price,brandId,pageNo);
		
		//已经点过的过滤条件回显
		Map<String,String> map = new HashMap<String,String>();
		//品牌
		if(null != brandId){
			for (Brand brand : brands) {
				if(brand.getId().toString().equals(brandId)){
					map.put("品牌:", brand.getName());
					break;
				}
			}
		}
		//价格区间
		if(null != price){
			if(price.contains("-")){
				map.put("价格:", price);
			}else{
				map.put("价格:", price + "以上");
			}
		}
		
		model.addAttribute("map", map);
		model.addAttribute("price", price);
		model.addAttribute("brandId", brandId);
		model.addAttribute("brands", brands);
		model.addAttribute("pagination", pagination);
		model.addAttribute("keyword", keyword);
		
		return "search";
	}
	
	@Autowired
	private CmsService cmsService;
	
	/**
	 * 商品详情页
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/product/detail")
	public String showProductById(Long id,Model model){
		//商品对象
		Product product = cmsService.selectProductById(id);
		model.addAttribute("product", product);
		
		//sku对象
		List<Sku> skus = cmsService.selectSkuByProductId(id);
		model.addAttribute("skus", skus);
		
		//去掉重复的颜色
		Set<Color> colors = new HashSet<Color>();
		for (Sku sku : skus) {
			colors.add(sku.getColor());
		}
		model.addAttribute("colors", colors);
		
		
		return "product";
	}
}
