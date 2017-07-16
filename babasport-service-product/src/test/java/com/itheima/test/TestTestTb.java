package com.itheima.test;


import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.itheima.core.pojo.TestTb;
import com.itheima.core.service.TestTbService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:application-context.xml"})
public class TestTestTb {
	
	@Autowired
	private TestTbService testTbService;
	@Test
	public void testInsertTestTb(){
		
		TestTb testTb = new TestTb();
		testTb.setName("笑笑2");
		testTb.setBirthday(new Date());
		
		testTbService.insertTestTb(testTb);
	}
}
