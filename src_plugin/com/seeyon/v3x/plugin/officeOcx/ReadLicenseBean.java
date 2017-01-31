/**
 * 
 */
package com.seeyon.v3x.plugin.officeOcx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2012-4-28
 */
public class ReadLicenseBean {
	
	private static Log log = LogFactory.getLog(ReadLicenseBean.class);
	
	/** office插件的License文件 */
	public void init(){
		InputStream in = null;
		OutputStream licenseToFile = null;
    	try{
    		File licenseFile = new File(SystemEnvironment.getA8BaseFolder() + File.separator + "license" + File.separator + "officeplugin.office");
    		if (licenseFile.exists()) {
    			in = new FileInputStream(licenseFile);
    			String licenseText = "";
    			List<String> licenseTexts = IOUtils.readLines(in);
    			for (String s : licenseTexts) {
    				if(s != null && !s.trim().equals("")){
    					licenseText += s.replaceAll("\"", "\\\"");
    				}
    			}
    			
    			licenseText = "var ___OfficeLicese = \"" + licenseText + "\";";
    			
    			licenseToFile = new FileOutputStream(SystemEnvironment.getA8ApplicationFolder() + File.separator + "common" +  File.separator +  "office" +  File.separator + "license.js");
    			
    			IOUtils.write(licenseText, licenseToFile);
    			
    			log.info("加载Office License文件成功");
    		}
    		else{
    			log.warn("没有找到Office License文件：" + licenseFile);
    		}
    	}
    	catch(Exception e){
    		log.error("", e);
    	}
    	finally{
    		IOUtils.closeQuietly(in);
    		IOUtils.closeQuietly(licenseToFile);
    	}
	}
}
