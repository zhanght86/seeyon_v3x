package com.seeyon.v3x.common.fileupload;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Author: $
 * $Rev: $
 * $Date:: $
 *
 * Copyright (C) 2012 Seeyon, Inc. All rights reserved.
 *
 * This software is the proprietary information of Seeyon, Inc.
 * Use is subject to license terms.
 */

/**
 * 文件工具类。封装文件编码判断及其他文件操作方法。
 * @author wangwenyou
 *
 */
public class FileUtil {
    private static Log logger = LogFactory.getLog(FileUtil.class);

    /**
     * 为下载文件生成文件名，解决乱码问题。调用方式如response.setHeader("Content-disposition", "attachment;" + FileUtil.getDownloadFileName(request,fileName));
     * @param request HttpRequest
     * @param filename 文件名
     * @return 编码后的文件名，包含filename=。
     */
    public static String getDownloadFileName(HttpServletRequest request ,String filename) {  
        String new_filename = null;  
        if(filename==null) {
        	return null;
        }
        try {  
        	new_filename = java.net.URLEncoder.encode(filename, "UTF-8").replace("+", "%20");  
        	if(filename.endsWith("xls")||filename.endsWith("xlsx")){
        		if (new_filename.length() > 130) {
        			new_filename = new String(filename.getBytes("GBK"), "ISO8859-1");
        		}
        	}
        } catch (UnsupportedEncodingException e1) {  
            logger.warn("当前系统不支持UTF-8编码转换：",e1);
        }  

        String userAgent = request.getHeader("User-Agent");  
        boolean isIe7 = (userAgent!=null) && userAgent.toLowerCase().contains("msie 7");
        if (isIe7 && new_filename.length() > 136) {
        	String ext = FilenameUtils.getExtension(new_filename);
        	String base = FilenameUtils.removeExtension(new_filename);
        	new_filename = base.substring(0,136)+"."+ext;
        }        
        String rtn = "filename=\"" + new_filename + "\"";  
        // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的  
        if (userAgent != null) {  
            userAgent = userAgent.toLowerCase();  
            // IE浏览器，只能采用URLEncoder编码  
            if (userAgent.indexOf("msie") != -1) {  
                if(filename.indexOf(".")<0){
                	// 无扩展名
                	new_filename = new_filename.replace("%20", " ");
                }            	
                rtn = "filename=\"" + new_filename + "\"";  
            }  
            // Opera浏览器只能采用filename*  
            else if (userAgent.indexOf("opera") != -1) {  
                rtn = "filename*=UTF-8''" + new_filename;  
            }  
            // Safari浏览器，只能采用ISO编码的中文输出  
            else if (userAgent.indexOf("safari") != -1) {  
                try {  
                    rtn = "filename=\""  
                            + new String(filename.getBytes("UTF-8"),  
                                    "ISO8859-1") + "\"";  
                } catch (UnsupportedEncodingException e) {  
                    logger.warn("当前系统不支持UTF-8编码转换：",e);
                }  
            }  
            // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出  
            else if (userAgent.indexOf("applewebkit") != -1) {  
                try {  
                    new_filename = MimeUtility  
                            .encodeText(filename, "UTF8", "B");  
                } catch (UnsupportedEncodingException e) {  
                    logger.warn("当前系统不支持UTF-8编码转换：",e);
                }  
                rtn = "filename=\"" + new_filename + "\"";  
            }  
            // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出  
            else if (userAgent.indexOf("mozilla") != -1) {  
                rtn = "filename*=UTF-8''" + new_filename;  
            }  
        }  
        return rtn;  
    }  
}
