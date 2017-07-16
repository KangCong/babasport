package com.itheima.common.upload;

import org.apache.commons.io.FilenameUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;

/**
 * 使用FastDFS实现上传图片到FastDFS图片服务器
 * @author Larry
 *
 */
public class FastDFSUtils {
	
	public static String uploadPic(byte[] pic,String name,long size) throws Exception{
		
		//全局设置tracker的ip
		ClassPathResource resource = new ClassPathResource("fdfs_client.conf");
		ClientGlobal.init(resource.getClassLoader().getResource("fdfs_client.conf").getPath());
		//连接tracker
		TrackerClient trackerClient = new TrackerClient();
		//获取Storage的地址
		TrackerServer trackerServer = trackerClient.getConnection();
		//连接storage服务器
		StorageServer storageServer = null;
		StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
		//上传图片到storage
		String ext = FilenameUtils.getExtension(name);
		NameValuePair[] meta_list = new NameValuePair[3];
		meta_list[0] = new NameValuePair("filename", name);
		meta_list[1] = new NameValuePair("fileext", ext);
		meta_list[2] = new NameValuePair("filesize", String.valueOf(size));
		
		String path = storageClient1.upload_file1(pic, ext, meta_list);
		
		return path;
	}
}
