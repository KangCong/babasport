package com.itheima.common.web;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json工具类
 * 对象转成Json串
 * Json串转成对象
 * @author Larry
 *
 */
public class JsonUtils {
	//定义Jackson对象
	private static final ObjectMapper mapper = new ObjectMapper();
	static{
		//转换不包含null值
		mapper.setSerializationInclusion(Include.NON_NULL);
	}
	/**
	 * 将对象转换成Json格式字符串
	 * @param obj
	 * @return
	 */
	public static String objectToJson(Object obj){
		String result = null;
		try {
			result = mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 将Json格式字符串转换成对象
	 * @param json
	 * @param classType
	 * @return
	 */
	public static <T> T jsonToObject(String json,Class<T> classType){
		T result = null;
		try {
			result = mapper.readValue(json, classType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
