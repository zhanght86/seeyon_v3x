package com.seeyon.v3x.hr.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.seeyon.v3x.hr.conf.TempleteConfig;
import com.seeyon.v3x.hr.conf.TempleteProfile;

public class TempleteHelper {
	private transient static final Log LOG = LogFactory
	.getLog(TempleteHelper.class); 
	private TempleteProfile templeteProfile = null;
	
	static class SingletonHolder {  
		static TempleteHelper instance = new TempleteHelper();  
	}  

	public static TempleteHelper getInstance() {  
		return SingletonHolder.instance;  
	}
	
	public void initialized(ServletContext servletContext) {
		if (null == templeteProfile) {
			//templeteProfile = fromXML(servletContext);//暂去掉，Websphere启动报错，初始化表单的templete_config.xml
			//LOG.info("HR表单加载完成！");
		}
	}

	public void destroyed() {
		templeteProfile = null;
	}
	
	private InputStream getXMLFromFile(String filepath,
			ServletContext servletContext) {
		InputStream in = null;
		try {
			in = servletContext.getResourceAsStream(filepath);


//	        StringBuffer buffer = new StringBuffer();
//	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//	        String line = "";
//	        line = reader.readLine();
//	        while(line != null){
//	        	buffer.append(line);
//	        	line = reader.readLine();
//	        }
//	        String xml = buffer.toString().trim();
//LOG.debug("xml========"+xml);


		}
		catch (Exception e) {
			LOG.error("加载配置文件", e);
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) {
					LOG.error("", e);
				}
			}
		}
		return in;
	}
	
	@SuppressWarnings("unused")
	private TempleteProfile fromXML(ServletContext servletContext) {
//		String xml = "";
		TempleteProfile templeteProfile = new TempleteProfile();
		InputStream is = getXMLFromFile(Constants.FORM_TEMPLETE_CONFIG_FILE_PATH+Constants.FORM_TEMPLETE_CONFIG_FILE, servletContext);
		DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder dombuilder=domfac.newDocumentBuilder();
			Document doc=dombuilder.parse(is);
			Element root = doc.getDocumentElement();
			NodeList templetes = root.getElementsByTagName("TempleteConfig");
			if(templetes != null){ 
				List<TempleteConfig> templeteConfigs = new ArrayList<TempleteConfig>();
				for(int i = 0; i<templetes.getLength(); i++){
					Element node = (Element)templetes.item(i);
					TempleteConfig templeteConfig = new TempleteConfig();
					templeteConfig.setKey(node.getElementsByTagName("key").item(0).getFirstChild().getNodeValue());
					templeteConfig.setName(node.getElementsByTagName("name").item(0).getFirstChild().getNodeValue());
					templeteConfig.setFName(node.getElementsByTagName("fName").item(0).getFirstChild().getNodeValue());
					templeteConfigs.add(templeteConfig);
				}
				templeteProfile.setTempleteConfigs(templeteConfigs);
			}
		}catch(Exception ex){
			LOG.error("", ex);
		}
//        XStream xstream = new XStream();
//        xstream.alias("templeteProfile", TempleteProfile.class);
//		xstream.alias("templeteConfig",TempleteConfig.class);
//		TempleteProfile templeteProfile = (TempleteProfile)xstream.fromXML(xml);
		return templeteProfile;
	}
	
	//根据键值的到相应的表单模板
	public String getName(String key){
		String name = "";
		if (null != templeteProfile) {
			List<TempleteConfig> templeteConfigs = templeteProfile.getTempleteConfigs();
			for(TempleteConfig templete : templeteConfigs){
				if(templete.getKey().equals(key)){
					name = templete.getName();
					break;
				}
			}
		}
		return name;
	}
	
	public String getFName(String key){
		String fName = "";
		if (null != templeteProfile) {
			List<TempleteConfig> templeteConfigs = templeteProfile.getTempleteConfigs();
			for(TempleteConfig templete : templeteConfigs){
				if(templete.getKey().equals(key)){
					fName = templete.getFName();
					break;
				}
			}
		}
		return fName;
	}
}
