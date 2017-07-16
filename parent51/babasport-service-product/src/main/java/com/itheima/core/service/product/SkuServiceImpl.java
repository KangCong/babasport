package com.itheima.core.service.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.core.dao.product.ColorDao;
import com.itheima.core.dao.product.SkuDao;
import com.itheima.core.pojo.product.Color;
import com.itheima.core.pojo.product.Sku;
import com.itheima.core.pojo.product.SkuQuery;
import com.itheima.core.pojo.product.SkuQuery.Criteria;

/**
 * 库存管理
 * 
 * @author Larry
 *
 */
@Service("skuService")
@Transactional
public class SkuServiceImpl implements SkuService {

	@Autowired
	private SkuDao skuDao;
	@Autowired
	private ColorDao colorDao;
	
	// 添加库存
	public void insertSku(Sku sku) {
		skuDao.insertSelective(sku);
	}

	// 查询商品库存信息
	public List<Sku> selectSkuByProductId(Long productId) {
		SkuQuery query = new SkuQuery();
		query.createCriteria().andProductIdEqualTo(productId);
		List<Sku> skus = skuDao.selectByExample(query);
		
		for (Sku sku : skus) {
			Color color = colorDao.selectByPrimaryKey(sku.getColorId());
			sku.setColor(color);
		}
		
		return skus;
	}
	
	//修改商品库存信息
	public void updateSkuById(Sku sku){
		skuDao.updateByPrimaryKeySelective(sku);
	}
}
