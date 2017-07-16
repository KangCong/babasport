package com.itheima.core.service;

import com.itheima.core.pojo.TestTb;
import com.itheima.core.pojo.product.Product;

public interface TestTbService {
	public void insertTestTb(TestTb testTb);
	
	Product selectByPrimaryKey(Long id);
}
