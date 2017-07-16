package com.itheima.core.service.product;

import java.util.List;

import com.itheima.core.pojo.product.Color;
import com.itheima.core.pojo.product.ColorQuery;

public interface ColorService {
	// 查询所有颜色
	public List<Color> selectColorsById();
}
