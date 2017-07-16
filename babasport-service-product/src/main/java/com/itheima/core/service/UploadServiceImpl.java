package com.itheima.core.service;

import org.springframework.stereotype.Service;

import com.itheima.common.upload.FastDFSUtils;

/**
 * fastDFS文件上传
 * @author Larry
 *
 */
@Service("uploadService")
public class UploadServiceImpl implements UploadService {
	
	//上传单张图片到FastDFS
	public String uploadPic(byte[] pic,String name,long size) throws Exception{
		
		return FastDFSUtils.uploadPic(pic, name, size);
	}
	
	//上传多张图片到FastDFS
	
}
