package com.itheima.core.service;

public interface UploadService {
	// 上传图片到FastDFS
	public String uploadPic(byte[] pic,String name,long size) throws Exception;
}
