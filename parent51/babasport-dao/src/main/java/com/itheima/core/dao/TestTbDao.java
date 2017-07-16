package com.itheima.core.dao;

import com.itheima.core.pojo.TestTb;
import com.itheima.core.pojo.product.Product;

public interface TestTbDao {
	public void insertTestTb(TestTb testTb);
	
	public Product selectByPrimaryKey(Long id);
}
