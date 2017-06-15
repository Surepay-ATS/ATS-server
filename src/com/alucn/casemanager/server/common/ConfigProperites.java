package com.alucn.casemanager.server.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.listener.MainListener;

/**
 * system config
 * @author wanghaiqi
 *
 */
public class ConfigProperites {
	public static Logger logger = Logger.getLogger(ConfigProperites.class);
	private static ConfigProperites singletonConfigProperites;
	//Attribute configuration for non dynamic refresh
	Properties singletonProperties ;
	
	/**
	 * 	singleton
	 */
	private ConfigProperites(){
		singletonProperties = new Properties();
		InputStream in = null;
		try {
			ConfigProperites.class.getClassLoader();
			
			if(null==MainListener.configFilesPath){
				//Configuration files in the project root directory
				in = getClass().getResourceAsStream(File.separator+"dynamic-refresh-unable.properties");
//				File conf = new File("D:\\conf.properties");
//				in = new FileInputStream(conf);
			}else{
				//Jar package external profile directory, a configuration parameter of the entry class
				String confPath =MainListener.configFilesPath +File.separator+  "dynamic-refresh-unable.properties";
				logger.info("[path of conf.properties：]"+confPath);
				File conf = new File(confPath);
				in = new FileInputStream(conf);
			}

		
			if (null != in){
				singletonProperties.load(in);
			}else {
					throw new Exception("fail to load properties properties");
				}
			
		} catch (Exception e) {
			singletonProperties = null;
			logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
		} finally{
			
				try {
					if(in != null){
						in.close();
						in = null;
					}
				} catch (IOException e) {
					logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
				}
				
			}
	}
		
	/**
	 *singleton
	 * @return
	 */
	public static ConfigProperites getInstance(){
		return singletonConfigProperites = singletonConfigProperites == null ? new ConfigProperites():singletonConfigProperites;
	}
	/**
	 * @param key
	 * @return
	 */
	public  String  getSingletonProperitesVal(String key){
		return singletonProperties.getProperty(key);
	}
}
