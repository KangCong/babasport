package com.itheima.core.service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itheima.core.dao.product.BrandDao;
import com.itheima.core.dao.product.ProductDao;
import com.itheima.core.dao.product.SkuDao;
import com.itheima.core.pojo.product.Brand;
import com.itheima.core.pojo.product.Product;
import com.itheima.core.pojo.product.ProductQuery;
import com.itheima.core.pojo.product.Sku;
import com.itheima.core.pojo.product.SkuQuery;

import cn.itcast.common.page.Pagination;
import redis.clients.jedis.Jedis;

/**
 * 前台搜索
 * 
 * @author Larry
 *
 */
@Service("searchService")
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SolrServer solrServer;

	/**
	 * 根据关键词搜索
	 * 
	 * @param keyword
	 * @return
	 * @throws Exception
	 */
	public Pagination searchProductByKeyword(String keyword, String price, String brandId, Integer pageNo)
			throws Exception {
		// 创建productQuery对象用来计算分页的开始索引
		ProductQuery productQuery = new ProductQuery();
		productQuery.setPageNo(Pagination.cpn(pageNo));
		productQuery.setPageSize(10);

		SolrQuery solrQuery = new SolrQuery();
		// 分页
		solrQuery.setStart(productQuery.getStartRow());
		solrQuery.setRows(productQuery.getPageSize());
		// 高亮
		// 打开高亮开关
		solrQuery.setHighlight(true);
		// 设置高亮字段
		solrQuery.addHighlightField("name_ik");
		// 设置高亮前缀
		solrQuery.setHighlightSimplePre("<span style='color:red'>");
		// 设置高亮后缀
		solrQuery.setHighlightSimplePost("</span>");

		StringBuilder params = new StringBuilder();
		// 关键字
		if (null != keyword) {
			solrQuery.setQuery("name_ik:" + keyword);
			params.append("keyword=").append(keyword);
		}
		// 品牌
		if (null != brandId) {
			solrQuery.addFilterQuery("brandId:" + brandId);
			params.append("&brandId=").append(brandId);
		}
		// 价格
		if (null != price) {
			// 根据-切割
			String[] prices = price.split("-");
			// 价格格式为0-99
			if (prices.length == 2) {
				solrQuery.addFilterQuery("price:[" + prices[0] + " TO " + prices[1] + "]");
			} else {
				solrQuery.addFilterQuery("price:[" + prices[0] + " TO *]");
			}
			params.append("&price=").append(price);
		}

		// 设置按价格升序
		solrQuery.setSort("price", ORDER.asc);
		// 执行查询
		QueryResponse response = solrServer.query(solrQuery);

		// 获取高亮数据 外层map的键是商品id,内层map键是索引库中的键name_ik
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

		// 获取结果
		SolrDocumentList docs = response.getResults();
		// 返回结果总条数
		long numFound = docs.getNumFound();

		List<Product> products = new ArrayList<Product>();
		// 遍历结果集
		if (docs.size() > 0) {
			for (SolrDocument doc : docs) {
				Product product = new Product();
				// 商品id
				String id = (String) doc.get("id");
				product.setId(Long.parseLong(id));
				// 品牌id
				product.setBrandId(Long.parseLong(String.valueOf((Integer) doc.get("brandId"))));

				// 高亮数据
				Map<String, List<String>> map = highlighting.get(String.valueOf(id));
				List<String> list = map.get("name_ik");
				// 商品名称
				product.setName(list.get(0));
				// 价格
				product.setPrice((Float) doc.get("price"));
				// 图片url
				product.setImgUrl((String) doc.get("url"));

				// 添加到集合中
				products.add(product);
			}
		}
		// 创建分页对象
		Pagination pagination = new Pagination(productQuery.getPageNo(), productQuery.getPageSize(), (int) numFound,
				products);

		String url = "/Search";
		pagination.pageView(url, params.toString());

		return pagination;
	}

	@Autowired
	private Jedis jedis;
	@Autowired
	private BrandDao brandDao;

	/**
	 * 从redis中查询品牌列表
	 * 
	 * @return
	 */
	public List<Brand> selectBrandListFromRedis() {
		List<Brand> brands = null;
		// 从redis中查询品牌列表
		Map<String, String> hgetAll = jedis.hgetAll("brand");
		if (null != hgetAll && hgetAll.size() > 0) {
			// redis中有数据,直接取
			brands = new ArrayList<Brand>();
			for (Map.Entry<String, String> entry : hgetAll.entrySet()) {
				Brand brand = new Brand();
				brand.setId(Long.parseLong(entry.getKey()));
				brand.setName(entry.getValue());
				brands.add(brand);
			}
			return brands;
		}
		// redis中没有数据,查询mysql数据库,并将数据存入redis中
		brands = brandDao.selectBrandListByQuery(null);
		Map<String, String> map = new HashMap<String, String>();
		for (Brand brand : brands) {
			// 存入redis中
			map.put(String.valueOf(brand.getId()), brand.getName());
		}
		jedis.hmset("brand", map);
		return brands;
	}
	
	@Autowired
	private ProductDao productDao;
	@Autowired
	private SkuDao skuDao;
	
	/**
	 * 上架商品的时候,将商品信息添加到索引库中,ActiveMQ
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public void addProductToSolr(Long id) throws Exception {
		// 将商品信息保存到solr服务器 商品id,名称,品牌id,图片url,价格
		SolrInputDocument doc = new SolrInputDocument();
		// 商品id
		doc.setField("id", id);
		// 商品名称
		Product p = productDao.selectByPrimaryKey(id);
		doc.setField("name_ik", p.getName());
		// 品牌id
		doc.setField("brandId", p.getBrandId());
		// 图片url
		doc.setField("url", p.getImgUrl());
		// 价格取最小值 需要拼sql语句 select price from bbs_sku where product_id=442 order
		// by price asc limit 0,1
		SkuQuery skuQuery = new SkuQuery();
		skuQuery.createCriteria().andProductIdEqualTo(id);
		skuQuery.setOrderByClause("price asc");
		skuQuery.setPageNo(1);
		skuQuery.setPageSize(1);
		// 只查询price
		skuQuery.setFields("price");
		List<Sku> skus = skuDao.selectByExample(skuQuery);
		// 价格
		doc.setField("price", skus.get(0).getPrice());
		// 时间
		doc.setField("last_modified", new Date());

		solrServer.add(doc);
		solrServer.commit();
		// TODO 静态化
	}
}
