package com.itheima.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CenterController {
	// 后台首页
	@RequestMapping(value = "/control/index.do")
	public String index() {

		return "index";
	}

	// 头部
	@RequestMapping(value = "/control/top.do")
	public String top() {
		return "top";
	}

	// 底部
	@RequestMapping(value = "/control/main.do")
	public String main() {
		return "main";
	}

	// 底部 -- 左侧
	@RequestMapping(value = "/control/left.do")
	public String left() {
		return "left";
	}

	// 底部 -- 右侧
	@RequestMapping(value = "/control/right.do")
	public String right() {
		return "right";
	}

	// 商品模块
	@RequestMapping(value = "/control/frame/product_main.do")
	public String product_main() {
		return "frame/product_main";
	}

	// 商品模块 -- 左边
	@RequestMapping(value = "/control/frame/product_left.do")
	public String product_left() {
		return "frame/product_left";
	}

	// 广告模块
	@RequestMapping(value = "/control/frame/ad_main.do")
	public String ad_main() {
		return "frame/ad_main";
	}

	// 广告模块 -- 左边
	@RequestMapping(value = "/control/frame/ad_left.do")
	public String ad_left() {
		return "frame/ad_left";
	}
}
