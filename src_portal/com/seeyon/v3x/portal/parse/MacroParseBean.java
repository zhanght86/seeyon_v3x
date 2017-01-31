package com.seeyon.v3x.portal.parse;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author dongyj
 *
 */
@Deprecated
public class MacroParseBean {
	
	private String layoutType;
	
	private String decoration;
	//路径
	private String contextPath;
	
	private String language;
	
	private String currentDeptId;
	
	private String banner = "space_banner.gif";
	
	private String isManage = null;
	public MacroParseBean(HttpServletRequest request){
		layoutType = request.getAttribute("layoutType").toString();
		decoration = request.getAttribute("decoration").toString();
		if(request.getAttribute("banner") != null){
			banner = request.getAttribute("banner").toString();
		}
		contextPath = request.getContextPath();
		language = Functions.getLanguage(request);
		currentDeptId= request.getParameter("depId");
		isManage = request.getParameter("isManage");
		
		isManage = isManage == null ? "false" : isManage;
	}
	
	public String parse(String str){
		if(Strings.isNotBlank(str)){
			str = str.replaceAll("\\$layoutType", layoutType);
			str = str.replaceAll("\\$decoration", decoration);
			str = str.replaceAll("\\$contextPath", contextPath);
			str = str.replaceAll("\\$language", language);
			str = str.replaceAll("\\$isManage", isManage);
			if(Strings.isNotBlank(currentDeptId)){
				str = str.replaceAll("\\$currentDeptId", currentDeptId);
			}else{
				str = str.replaceAll("\\$currentDeptId", "");
			}
			if(Strings.isNotBlank(banner)){
				str = str.replaceAll("\\$banner", banner);
			}
		}
		return str;
	}
}
