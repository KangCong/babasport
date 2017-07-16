package com.itheima.core.service.product;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.core.dao.product.ProductDao;
import com.itheima.core.dao.product.SkuDao;
import com.itheima.core.pojo.product.Color;
import com.itheima.core.pojo.product.Product;
import com.itheima.core.pojo.product.ProductQuery;
import com.itheima.core.pojo.product.ProductQuery.Criteria;
import com.itheima.core.pojo.product.Sku;
import com.itheima.core.service.CmsService;
import com.itheima.core.service.StaticPageService;

import cn.itcast.common.page.Pagination;
import redis.clients.jedis.Jedis;

/**
 * 商品管理
 * 
 * @author Larry
 *
 */
@Service("productService")
@Transactional
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductDao productDao;
	@Autowired
	private SkuDao skuDao;

	/**
	 * 查询商品列表带分页
	 */
	public Pagination selectByExample(String name, Long brandId, Boolean isShow, Integer pageNo) {
		// 创建商品查询对象
		ProductQuery productQuery = new ProductQuery();
		StringBuilder params = new StringBuilder();
		// 设置模糊查询商品名称
		Criteria criteria = productQuery.createCriteria();
		if (null != name) {
			criteria.andNameLike("%" + name + "%");
			params.append("name=").append(name);
		}
		if (null != brandId) {
			criteria.andBrandIdEqualTo(brandId);
			params.append("&brandId=").append(brandId);
		}
		if (null != isShow) {
			criteria.andIsShowEqualTo(isShow);
			params.append("&isShow=").append(isShow);
		}
		productQuery.setOrderByClause("id desc");
		// 设置当前页
		productQuery.setPageNo(Pagination.cpn(pageNo));
		// 设置每页显示几条数据
		productQuery.setPageSize(3);
		// 创建分页对象
		Pagination pagination = new Pagination(productQuery.getPageNo(), productQuery.getPageSize(),
				productDao.countByExample(productQuery), productDao.selectByExample(productQuery));

		String url = "/product/list.do";
		pagination.pageView(url, params.toString());

		return pagination;
	}

	@Autowired
	private Jedis jedis;

	/**
	 * 添加商品
	 * 
	 * @param product
	 */
	public void insertProduct(Product product) {
		// id,使用redis生成唯一id
		Long id = jedis.incr("pno");
		product.setId(id);
		jedis.close();
		// 下架
		product.setIsShow(false);
		// 删除
		product.setIsDel(false);
		// 添加时间
		product.setCreateTime(new Date());
		// 添加商品,返回商品id
		productDao.insertSelective(product);

		// 更新库存表sku
		// 商品的尺码,颜色笛卡尔积
		for (String size : product.getSizes().split(",")) {
			for (String color : product.getColors().split(",")) {
				Sku sku = new Sku();
				// 设置商品id
				sku.setProductId(product.getId());
				// 颜色id
				sku.setColorId(Long.parseLong(color));
				// 尺码
				sku.setSize(size);
				// 价格,默认0
				sku.setPrice(0f);
				// 运费,默认10
				sku.setDeliveFee(10f);
				// 库存,默认0
				sku.setStock(0);
				// 购买限制,默认200
				sku.setUpperLimit(200);
				// 创建时间
				sku.setCreateTime(new Date());

				// 保存库存
				skuDao.insertSelective(sku);
			}
		}
	}

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private CmsService cmsService;
	@Autowired
	private StaticPageService staticPageService;
	/**
	 * 上架商品
	 * 
	 * @param ids
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public void isShowProduct(Long[] ids) throws Exception {
		Product product = new Product();
		// 更改商品状态为已上架状态
		product.setIsShow(true);
		for (final Long id : ids) {
			product.setId(id);
			// 修改数据库
			productDao.updateByPrimaryKeySelective(product);

			// MQ 发送消息
			jmsTemplate.send("productId", new MessageCreator() {

				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(String.valueOf(id));
				}
			});
			
			// 静态化
			// 商品对象
			Map<String,Object> root = new HashMap<String,Object>();
			Product p = cmsService.selectProductById(id);
			root.put("product", p);
			// sku对象
			List<Sku> skus = cmsService.selectSkuByProductId(id);
			root.put("skus", skus);
			// 去掉重复的颜色
			Set<Color> colors = new HashSet<Color>();
			for (Sku sku : skus) {
				colors.add(sku.getColor());
			}
			root.put("colors", colors);
			
			staticPageService.index(root, id);
		}

	}
}
