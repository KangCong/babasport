package com.itheima.core.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.itheima.common.Constants;
import com.itheima.core.pojo.product.Brand;
import com.itheima.core.pojo.product.Color;
import com.itheima.core.pojo.product.Product;
import com.itheima.core.pojo.product.Sku;
import com.itheima.core.pojo.product.SkuQuery;
import com.itheima.core.service.UploadService;
import com.itheima.core.service.product.BrandService;
import com.itheima.core.service.product.ColorService;
import com.itheima.core.service.product.ProductService;
import com.itheima.core.service.product.SkuService;

import cn.itcast.common.page.Pagination;

/**
 * 商品管理模块
 * 
 * @author Larry
 *
 */
@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;

	/**
	 * 商品列表
	 * 
	 * @param name
	 * @param brandId
	 * @param isShow
	 * @param pageNo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/product/list.do")
	public String list(String name, Long brandId, Boolean isShow, Integer pageNo, Model model) {

		// 查询品牌列表
		List<Brand> brands = brandService.selectBrandListByQuery(null, 1);

		// 查询分页结果集
		Pagination pagination = productService.selectByExample(name, brandId, isShow, pageNo);

		model.addAttribute("brands", brands);
		model.addAttribute("name", name);
		model.addAttribute("brandId", brandId);
		model.addAttribute("isShow", isShow);
		model.addAttribute("pageNo", pageNo);
		model.addAttribute("pagination", pagination);

		return "product/list";
	}

	@Autowired
	private ColorService colorService;
	
	/**
	 * 跳转到添加商品页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/product/toAdd.do")
	public String toAdd(Model model) {
		// 查询品牌列表
		List<Brand> brands = brandService.selectBrandListByQuery(null, 1);
		model.addAttribute("brands", brands);
		
		//查询颜色列表
		List<Color> colors = colorService.selectColorsById();
		model.addAttribute("colors", colors);
		
		return "product/add";
	}
	
	@Autowired
	private UploadService uploadService;
	/**
	 * 异步上传多张图片
	 * @param pics
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/upload/uploadPics.do")
	public @ResponseBody
	List<String> uploadPics(@RequestParam(required=false) MultipartFile[] pics) throws Exception{
		List<String> urls = new ArrayList<String>();
		//遍历,一张一张的上传
		for (MultipartFile pic : pics) {
			String path = uploadService.uploadPic(pic.getBytes(), pic.getOriginalFilename(), pic.getSize());
			urls.add(Constants.IMG_URL + path);
		}
		
		return urls;
	}
	
	/**
	 * 异步上传多张图片(富文本编辑器)
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/upload/uploadFck.do")
	public void uploadFck(HttpServletRequest request,HttpServletResponse response) throws Exception{
		//无敌版接受文件或图片
		MultipartRequest mr = (MultipartRequest)request;
		//只有文件或图片
		Map<String, MultipartFile> fileMap = mr.getFileMap();
		Set<Entry<String, MultipartFile>> entrySet = fileMap.entrySet();
		//遍历map
		for (Entry<String, MultipartFile> entry : entrySet) {
			MultipartFile pic = entry.getValue();
			String path = uploadService.uploadPic(pic.getBytes(), pic.getOriginalFilename(), pic.getSize());
			String url = Constants.IMG_URL + path;
			
			JSONObject jo = new JSONObject();
			//将保存图片的地址转换成json格式, url 和 error 0 为固定格式
			jo.put("url", url);
			jo.put("error", 0);
			//响应回去
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().write(jo.toString());
		}
	}
	
	/**
	 * 添加商品
	 * @param product
	 * @return
	 */
	@RequestMapping(value="/product/add.do")
	public String add(Product product){
		productService.insertProduct(product);
		return "redirect:/product/list.do";
	}
	
	@Autowired
	private SkuService skuService;
	
	/**
	 * 库存列表
	 * @param productId
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/sku/list.do")
	public String listSku(Long productId,Model model){
		List<Sku> skus = skuService.selectSkuByProductId(productId);
		
		model.addAttribute("skus", skus);
		
		return "sku/list";
	}
	
	/**
	 * 修改库存信息
	 * @param sku
	 * @param response
	 */
	@RequestMapping(value="/sku/save.do")
	public void save(Sku sku,HttpServletResponse response){
		JSONObject jo = new JSONObject();
		try {
			skuService.updateSkuById(sku);
			jo.put("msg", "修改成功!");
		} catch (Exception e) {
			jo.put("msg", "修改失败!");
		}
		try {
			response.setContentType("application/json;charset=utf8");
			response.getWriter().write(jo.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商品上架
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/product/isShow.do")
	public String isShow(Long[] ids){
		try {
			productService.isShowProduct(ids);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "forward:/product/list.do";
	}
}
