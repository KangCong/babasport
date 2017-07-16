package com.itheima.core.service.message;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.itheima.core.service.search.SearchService;
/**
 * 自定义的消息处理类
 * @author Larry
 *
 */
public class CustomMessageListener implements MessageListener {
	
	@Autowired
	private SearchService searchService;
	
	@Override
	public void onMessage(Message message) {
		ActiveMQTextMessage atm = (ActiveMQTextMessage)message;
		try {
			String id = atm.getText();
			searchService.addProductToSolr(Long.parseLong(id));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
