package com.itheima.core.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 商品详情页静态化
 * @author Larry
 *
 */
public class StaticPageServiceImpl implements StaticPageService,ServletContextAware {
	//注入configuration
	private Configuration conf;
	public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
		this.conf = freeMarkerConfigurer.getConfiguration();
	}
	
	//页面静态化
	public void index(Map<String,Object> root,Long id){
		
		//静态化页面保存路径
		String path = "/html/product/"+ id +".html";
		String url = getRealPath(path);
		File f = new File(url);
		File parentFile = f.getParentFile();
		if(!parentFile.exists()){
			parentFile.mkdirs();
		}
		
		Writer out = null;
		try {
			//读取模板
			Template template = conf.getTemplate("product.html");
			out = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
			//处理
			template.process(root, out);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			try {
				if(null != out){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//获取绝对路径
	public String getRealPath(String path){
		return serveltContext.getRealPath(path);
	}
	private ServletContext serveltContext;
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.serveltContext = servletContext;
	}
}
