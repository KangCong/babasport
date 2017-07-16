package com.itheima.core.service.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itheima.core.dao.product.ColorDao;
import com.itheima.core.pojo.product.Color;
import com.itheima.core.pojo.product.ColorQuery;

@Service("colorService")
public class ColorServiceImpl implements ColorService {

	@Autowired
	private ColorDao colorDao;
	
	//查询所有颜色
	public List<Color> selectColorsById(){
		ColorQuery colorQuery = new ColorQuery();
		colorQuery.createCriteria().andParentIdNotEqualTo(0L);
		return colorDao.selectByExample(colorQuery);
	}
}
