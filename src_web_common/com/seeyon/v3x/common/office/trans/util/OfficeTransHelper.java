package com.seeyon.v3x.common.office.trans.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.Constants;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.office.trans.manager.OfficeTransManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.util.Datetimes;

/**
 * Office转换工具类。提供控制判断。
 * @author wangwenyou
 *
 */
public class OfficeTransHelper {
	private static final Log log = LogFactory.getLog(OfficeTransHelper.class);
	
	public static final String OfficeTransPathPrefix = "/seeyon/office/cache/";
	
	private final static String[] supportArray = new String[] { "doc", "docx", "xls", "xlsx", "ppt", "pptx", "rtf", "eio" };
	private static Set<String> supports = new HashSet<String>();
	static {
		for (int i = 0; i < supportArray.length; i++) {
			supports.add(supportArray[i]);
		}
	}

	/**
	 * 是否组件支持的Office文件类型。
	 * 
	 * @param extension
	 *            扩展名
	 * @return
	 */
	private static boolean isSupport(String extension) {
		return supports.contains(extension);
	}

	/**
	 * 判断是否允许转换。（非关联文档＋小于10M＋支持的文件类型）
	 * 
	 * @param attachment
	 *            附件对象
	 * @return 允许返回<tt>true</tt>，否则返回<tt>false</tt>。
	 */
	public static boolean allowTrans(Attachment attachment) {
		if (attachment == null)
			return false;
		final Integer type = attachment.getType();
		// 不是关联文档
		if (!checkType(type))
			return false;
		if (!checkSize(attachment))
			return false;
		return isSupport(attachment.getExtension());
	}
	/**
	 * 判断是否允许转换。（非关联文档＋小于10M＋支持的文件类型）
	 * 
	 * @param file
	 *            附件对象
	 * @return 允许返回<tt>true</tt>，否则返回<tt>false</tt>。
	 */
	public static boolean allowTrans(V3XFile file) {
		if (file == null)
			return false;
		if (!checkSize(file))
			return false;
		final String mimeType = file.getMimeType();
		if("msoffice".equals(mimeType)){
			return true;
		}
		if("application/vnd.ms-excel".equals(mimeType)){
			return true;
		}
		if("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(mimeType)){
			return true;
		}
		if("application/vnd.openxmlformats-officedocument.presentationml.presentation".equals(mimeType)){
			return true;
		}
		if("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType)){
		    return true;
		}
		if("application/msword".equals(mimeType)){
			return true;
		}
		final Integer type = file.getType();
		// 不是关联文档
		if (!checkType(type))
			return false;
		return isSupport(FilenameUtils.getExtension(file.getFilename()).toLowerCase());
	}
	
	private static boolean checkSize(Attachment attachment) {
		return attachment.getSize() < getOfficeTransManager().getFileMaxSize();
	}

	private static boolean checkSize(V3XFile attachment) {
		return attachment.getSize() < getOfficeTransManager().getFileMaxSize();
	}

	private static boolean checkType(final int type) {
		return type == Constants.ATTACHMENT_TYPE.FILE.ordinal() || type == Constants.ATTACHMENT_TYPE.FormFILE.ordinal();
	}
	
	/**
	 * 生成Office转换文件的访问链接。
	 * @param fileId 文件Id。
	 * @return 访问链接。/seeyon/office/cache/201104/*
	 */
	public static String buildCacheUrl(V3XFile file, boolean needDownload){
		return buildCacheUrl(file.getCreateDate(), file.getId(), file.getFilename(), needDownload);
	}
	
	/**
	 * 生成Office转换文件的访问链接
	 * 
	 * @param createDate
	 * @param fileId
	 * @param filename
	 * @param needDownload
	 * @return
	 */
	public static String buildCacheUrl(Date createDate, long fileId, String filename, boolean needDownload){
		String d = Datetimes.format(createDate, "yyyyMMdd");
		String d1 = Datetimes.format(createDate, "yyyy-MM-dd");
		try {
			return SystemEnvironment.getA8ContextPath() + "/officeTrans.do?fileCreateDate=" + d 
				+ "&fileCreateDate1=" + d1
				+ "&fileId=" + fileId 
				+ "&needDownload=" + needDownload
				+ "&filename=" + java.net.URLEncoder.encode(filename, "UTF-8");
		}
		catch (Exception e) {
			log.error("创建Office文档转换在线查看URL地址时出现异常[fileId=" + fileId + "]", e);
		}
		
		return null;
	}
	
	private static OfficeTransManager officeTransManager;
	private static OfficeTransManager getOfficeTransManager(){
		if(officeTransManager == null){
			officeTransManager = (OfficeTransManager) ApplicationContextHolder.getBean("officeTransManager");
		}
		
		return officeTransManager;
	}
	
	 /**
	   * 获得系统配置，是否开始永中转换
	   * 
	   * @return <code>true</code>:启用
	   */
	  public static boolean isOfficeTran(){
		  SystemConfig systemConfig = (SystemConfig)ApplicationContextHolder.getBean("systemConfig");
		  String item = systemConfig.get(com.seeyon.v3x.config.IConfigPublicKey.OFFICE_TRANSFORM_ENABLE);
		  return item.equals("enable");
	  }

}
