package com.seeyon.v3x.common.fileupload.util;

import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.filemanager.V3XFile;

public class FileUploadUtil {
	private static final String HEAD = "attach_";
	
	/**
	 * 手机端下载中文附件出现乱码，用于将文件转化成英文和字母的格式。
	 * @param file
	 * @return
	 */
	public static String escapeFileName(V3XFile file){
		String fileName = file.getFilename();
		String suffix = null;
		if(fileName.lastIndexOf(".")>0){
			suffix = fileName.substring(fileName.lastIndexOf("."),fileName.length());
		}
		return HEAD+file.getId()+suffix;
	}
	
	public static String getOfficeHeader(String type){
		if(Constants.EDITOR_TYPE_OFFICE_WORD.equals(type)){
			return "application/msword";
		}
		if(Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(type)){
			return "application/vnd.ms-excel";
		}
		if(Constants.EDITOR_TYPE_WPS_WORD.equals(type)){
			return "application/kswps";
		}
		if(Constants.EDITOR_TYPE_WPS_EXCEL.equals(type)){
			return "application/octet-stream";
		}
		return "application/x-msdownload";
	}
	
	public static String getOfficeSuffix(String type){
		if(Constants.EDITOR_TYPE_OFFICE_WORD.equals(type)){
			return "doc";
		}
		if(Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(type)){
			return "xls";
		}
		if(Constants.EDITOR_TYPE_WPS_WORD.equals(type)){
			return "wps";
		}
		if(Constants.EDITOR_TYPE_WPS_EXCEL.equals(type)){
			return "et";
		}
		return "html";
	}
	
	public static String getOfficeName(Long file,String type){
		return file.toString()+"."+getOfficeSuffix(type);
	}
}
