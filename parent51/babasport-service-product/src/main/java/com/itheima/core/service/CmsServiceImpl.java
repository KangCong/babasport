package com.itheima.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itheima.core.dao.product.ColorDao;
import com.itheima.core.dao.product.ProductDao;
import com.itheima.core.dao.product.SkuDao;
import com.itheima.core.pojo.product.Product;
import com.itheima.core.pojo.product.Sku;
import com.itheima.core.pojo.product.SkuQuery;

/**
 * 商品详情页
 * @author Larry
 *
 */
@Service("cmsService")
public class CmsServiceImpl implements CmsService {
	
	@Autowired
	private ProductDao productDao;
	@Autowired
	private SkuDao skuDao;
	@Autowired
	private ColorDao colorDao;
	
	//通过id查询商品信息
	public Product selectProductById(Long id){
		return productDao.selectByPrimaryKey(id);
	}
	
	//通过id查询商品详情sku，只查询有货的
	public List<Sku> selectSkuByProductId(Long id){
		
		SkuQuery skuQuery = new SkuQuery();
		skuQuery.createCriteria().andProductIdEqualTo(id).andStockGreaterThan(0);
		
		List<Sku> skus = skuDao.selectByExample(skuQuery);
		for (Sku sku : skus) {
			//通过sku对象的颜色id查询对应颜色
			sku.setColor(colorDao.selectByPrimaryKey(sku.getColorId()));
		}
		return skus;
	}
}
