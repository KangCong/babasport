package com.itheima.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.core.dao.TestTbDao;
import com.itheima.core.dao.product.ProductDao;
import com.itheima.core.pojo.TestTb;
import com.itheima.core.pojo.product.Product;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application-context.xml"})
public class TestTbServiceImpl  {
	
	@Autowired
	private ProductDao productDao;
	

	@Test
	public void selectByPrimaryKey() {
		
		Product product = productDao.selectByPrimaryKey(1L);
		System.out.println(product);
	}

}