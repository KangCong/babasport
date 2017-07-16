package com.itheima.core.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.itheima.common.Constants;
import com.itheima.core.pojo.product.Brand;
import com.itheima.core.service.UploadService;
import com.itheima.core.service.product.BrandService;

import cn.itcast.common.page.Pagination;

/**
 * 品牌模块
 * 
 * @author Larry
 *
 */
@Controller
public class BrandController {
	
	
	@Autowired
	private BrandService brandService;
	@Autowired
	private UploadService uploadService;
	/**
	 * 商品列表
	 * @return
	 */
	public String list(String name,Integer isDisplay,Model model){
		List<Brand> brands = brandService.selectBrandListByQuery(name, isDisplay);
		
		model.addAttribute("brands", brands);
		model.addAttribute("name", name);
		model.addAttribute("isDisplay", isDisplay);
		
		return "brand/list";
	}
	
	/**
	 * 商品列表分页
	 * @return
	 */
	@RequestMapping(value="/brand/list.do")
	public String listPage(Integer pageNo,String bname,Integer isDisplay,Model model){
		
		Pagination pagination = brandService.selectPaginationListByQuery(bname, isDisplay, pageNo);
		
		model.addAttribute("pagination", pagination);
		model.addAttribute("bname", bname);
		model.addAttribute("isDisplay", isDisplay);
		
		return "brand/list";
	}
	
	/**
	 * 跳转到修改页面
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/brand/toEdit.do")
	public String toEdit(Long id,Integer pageNo,String bname,Integer isDisplay,Model model){
		
		Brand brand = brandService.selectBrandById(id);
		
		model.addAttribute("brand", brand);
		model.addAttribute("pageNo",pageNo);
		model.addAttribute("bname",bname);
		model.addAttribute("isDisplay",isDisplay);
		
		return "brand/edit";
	}
	
	/**
	 * 异步上传图片
	 * @throws Exception 
	 * @throws IllegalStateException 
	 */
	@RequestMapping(value="/brand/uploadPic.do")
	public void uploadPic(MultipartFile pic,HttpServletRequest request,HttpServletResponse response) throws IllegalStateException, Exception{
		//获取文件的原始名
		String filename = pic.getOriginalFilename();
		//获得文件的扩展名
		String ext = FilenameUtils.getExtension(filename);
		//重新生成文件名
		String name = UUID.randomUUID().toString()+"."+ext;
		
		//将图片上传到FastDFS图片服务器
		// /group1/M00/00/01/wKjIgFWOYc6APpjAAAD-qk29i78248.jpg
		String path = uploadService.uploadPic(pic.getBytes(), name, pic.getSize());
		//http://192.168.200.128/group1/M00/00/01/wKjIgFWOYc6APpjAAAD-qk29i78248.jpg
		String url = Constants.IMG_URL + path;
		
		
//		String url = "/upload/" + name;
//		//获得上传文件夹路径
//		String path = request.getSession().getServletContext().getRealPath("/")+url;
//		//保存图片
//		pic.transferTo(new File(path));
		
		JSONObject jo = new JSONObject();
		jo.put("url", url);
		
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(jo.toString());
		
//		System.out.println(url);
//		System.out.println(path);
	}
	/**
	 * 修改品牌信息
	 * @param pageNo
	 * @param bname
	 * @param isDisplay
	 * @param brand
	 * @return
	 */
	@RequestMapping(value="/brand/edit.do")
	public String edit(Integer pageNo,String bname,Integer isDisplay,Brand brand){
		
		brandService.editBrandById(brand);
		//return "redirect:/brand/list.do?pageNo="+pageNo+"&bname="+bname+"&isDisplay="+isDisplay;
		return "forward:/brand/list.do";
	}
	
	@RequestMapping(value="/brand/deletes.do")
	public String deletes(Integer pageNo,String bname,Integer isDisplay,Long[] ids){
		brandService.deleteMultiBrandById(ids);
		return "forward:/brand/list.do";
	}
}
