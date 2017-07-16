package com.itheima.common.converter;

import org.springframework.core.convert.converter.Converter;
/**
 * 去除空格转换器
 * @author Larry
 *
 */
public class TrimConverter implements Converter<String, String>{

	
	public String convert(String source) {
		try {
			if(null != source){
				source = source.trim();
				if("" != source){
					return source;
				}
			}
		} catch (Exception e) {
			
		}
		return null;
	}

}
