package com.seeyon.v3x.portal.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.portal.decorations.PortalDecoration;
import com.seeyon.v3x.portal.decorations.PortalDecorationManager;
import com.seeyon.v3x.space.SpaceException;
import com.seeyon.v3x.space.Constants.SpaceType;
import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.domain.SpacePage;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.EnumUtil;
import com.seeyon.v3x.util.PropertiesUtil;
import com.seeyon.v3x.util.Strings;

public class PortalConstants  {
	private static final Log log = LogFactory.getLog(PortalConstants.class);
	/**
	 * 首页显示状态
	 * @author dongyj
	 *
	 */
	public static enum EditModel{
		show,//查看，对应的是编辑页面的查看，但是不能编辑
		edit,//编辑页面 可以进行编辑
		view,//展现页面
		;
	}
	
	/**
	 * 是否是编辑状态
	 */
	public static final String EDIT_MODEL = "_EDIT";
	
	public static final String	SPACE_FIX	  = "_Space_fix";
	
	/**
	 * 页面titile 以后单独提供给外部使用。
	 */
	public static final String TITLE      ="_TITLE";
	
	public static final String PAGEPATH   ="pagePath";
	
	public static final String FRAGMENTS   ="fragments";
	
	public static final String PAGE   =		"page";
	
	public static final String DECORATION ="decoration";
	
	private static final String properties_file_path = "com/seeyon/v3x/portal/util/Constants.properties";
	private static Properties props = null;
	static {
		props = PropertiesUtil.getFromClasspath(properties_file_path);
	}
	private static final String DEFAULT_VALUE = "";
	/**
	 * 判断是否是编辑模式
	 * @param request
	 * @return
	 */
	public static boolean isEdit(HttpServletRequest request){
		EditModel model = getEditModel(request);
		return !EditModel.view.name().equals(model.name());
	}
	
	public static EditModel getEditModel(HttpServletRequest request){
		EditModel model = (EditModel)request.getAttribute(EDIT_MODEL);
		if(model == null){
			return EditModel.view;
		}
		return model;
	}
	
	public static String getString(HttpServletRequest request,String key){
		Object obj = request.getAttribute(key);
		if(obj != null){
			return obj.toString();
		}
		return "";
	}
	
	public static String getString(String key) {
		if (key == null) {
			return DEFAULT_VALUE;
		}

		return props.getProperty(key);
	}
	
	public static String getString(String key, Object... parameters) {
		if (key == null) {
			return DEFAULT_VALUE;
		}

		try {
			String baseMsg = props.getProperty(key);

			if (parameters != null) {
				for(int i = 0;i< parameters.length;i++){
					baseMsg = baseMsg.replace("{"+i+"}", parameters[i].toString());
				}
				return baseMsg;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return key;
	}
	
	/**
	 * Portal数据组装，传入数据为<br/>
	 * <pre>
	 * 键  				值
	 * fragments		Map&ltString,Map&lt;String,Fragment&gt;&gt;
	 * pagePath			String path
	 * decoration		PortalDecoration decoration
	 * banner			String banner
	 * </pre>
	 * @param request
	 * @param page
	 * @param spaceManager
	 * @throws SpaceException
	 */
	public static void initPortalData(HttpServletRequest request,String path,SpaceManager spaceManager,String editKeyId,Long memberId) throws SpaceException{
		String editModel = request.getParameter("edit");
		if(Strings.isNotBlank(editModel)){
			request.setAttribute(EDIT_MODEL, EditModel.valueOf(editModel));
		}
		boolean isEdit = isEdit(request);
		String decorationId = request.getParameter("decorationId");
		if(Strings.isBlank(decorationId)){
			decorationId = request.getParameter("decoration");
		}
		if(!isEdit){
			try {
				SpacePage page = spaceManager.getSpacePage(path);
				Map<String,Map<String,Fragment>> fragments;
				if(Strings.isNotBlank(editKeyId)){
					fragments = spaceManager.getFragments(path,Long.valueOf(editKeyId),memberId);
				}else{
					fragments = spaceManager.getFragments(path);
				}
				String[] layout = spaceManager.getLayoutType(path);
				SpaceFix fix = spaceManager.getSpaceFix(path);
				if(fix!=null){
					request.setAttribute(SPACE_FIX, fix);
					SpaceType spaceType = EnumUtil.getEnumByOrdinal(SpaceType.class, fix.getType());
					request.setAttribute("trueSpaceType", spaceType.name());
				}
				request.setAttribute(FRAGMENTS, fragments);
				request.setAttribute(PAGE, page);
				if(Strings.isBlank(decorationId)){
					decorationId = layout[1];
				}
			} catch (Exception e) {
				log.error("取得空间出错："+e, e);
			}
		}
		PortalDecoration decoration = PortalDecorationManager.getDecoration(decorationId);
		request.setAttribute(PAGEPATH, path);
		request.setAttribute(DECORATION, decoration);
		
		/*
		 * if(!isEdit){
			//TODO 以后会写成栏目，不在需要以下数据
			request.setAttribute("banner", request.getParameter("banner"));
			String slogan = request.getParameter("slogan");
			if(Strings.isNotBlank(slogan)){
				try {
					slogan = new String(slogan.getBytes("ISO-8859-1"),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			request.setAttribute("slogan", slogan);
		}*/
		
	}
	
	/**
     * S_fragmentId                     得到 sections的值, 如"pendingSection,ssoWebcontentSection"
     * P_fragmentId_sectionId           得到该栏目所有可配参数数组
     * N_fragmentId_sectionId_paramName 得到某参数的值
     * 拆解各参数，追加顺序号
     */
    public static Map<String, String> doPortletEntityProperty(HttpServletRequest request, String paramEntity){
    	Map<String, String> properties = new HashMap<String, String>();
    	String sectionsName = request.getParameter("S_" + paramEntity);
    	//解析相应的section, 按顺序拼装参数
    	if(Strings.isNotBlank(sectionsName)){
    		String[] sections = sectionsName.split(",");
    		for (int i = 0; i < sections.length; i++) {
    			String[] paramsNames = request.getParameterValues("P_" + paramEntity + "_" + sections[i] + "_" + i);
    			if(paramsNames != null && paramsNames.length > 0){
    				for (String paramName : paramsNames) {
    					String[] paramValues = request.getParameterValues("N_" + paramEntity + "_" + sections[i] + "_" + i + "_" + paramName);
    					//页签属性
    					if(paramValues != null){
    						for(String paramValue : paramValues){
    							String[] valPro = request.getParameterValues("N_" + paramEntity + "_" + sections[i] + "_" + i + "_" + paramName+"_"+paramValue);
    							if(valPro != null){
    								for(String pro : valPro){
    									String[] proValues = request.getParameterValues("N_" + paramEntity + "_" + sections[i] + "_" + i + "_" + paramName+"_"+paramValue+"_"+pro);
    									String proValue = proValues != null ? StringUtils.join(proValues, ",") : "";
    									String valueName = paramValue+"_"+pro+":"+i;
    									properties.put(valueName, proValue);
    								}
    							}
    						}
    					}
    					String praramExtends = request.getParameter("N_" + paramEntity + "_" + sections[i] + "_" + i + "_" + paramName+"_extends");
    					if(praramExtends != null){
    						properties.put(paramName+"_extends"+":"+i, praramExtends);
    					}
    					
						String paramValue = paramValues != null ? StringUtils.join(paramValues, ",") : "";
						String[] paramValuesH = request.getParameterValues("N_" + paramEntity + "_" + sections[i] + "_" + i + "_" + paramName + "_H");
						String paramValueH = paramValuesH != null ? StringUtils.join(paramValuesH, ",") : "";
						if (!java.util.regex.Pattern.matches(PortletEntityProperty.PropertyName_No_Save_Pattern, paramName)) {
							if ("columnsName".equals(paramName)) {
								paramName += ":" + i;
								if (Strings.isNotBlank(paramValue)) {
									properties.put(paramName, paramValue);
								}else{
									properties.put(paramName, paramValueH);
								}
							} else if("slogan".equals(paramName)){
								paramName += ":" + i;
								if (paramValue == null && Strings.isNotBlank(paramValueH)) {
									properties.put(paramName, paramValueH);
								}else{
									properties.put(paramName, paramValue);
								}
							} else {
								paramName += ":" + i;
								if(Strings.isNotBlank(paramValue)){
									properties.put(paramName, paramValue);
								}else{
									properties.put(paramName, paramValueH);
								}
							}
						}
					}
    			}
			}
    	}
    	properties.put("sections", sectionsName);
    	
    	/*
    	String[] propNames = request.getParameterValues("propsName_" + paramEntity);
    	String[] propValues = request.getParameterValues("propsValue_" + paramEntity);
    	
    	if(propNames != null){
        	for (int i = 0; i < propNames.length; i++) {
        		if(!java.util.regex.Pattern.matches(PortletEntityProperty.PropertyName_No_Save_Pattern, propNames[i])) {
        			properties.put(propNames[i], propValues[i]);
        		}
			}
    	}*/
    	
    	return properties;
    }
    
    /**
     * 取得栏目参数的配置.
     * 
     * @param props
     * @param ordinal
     * @return
     */
    public static Map<String,String> getFragmentProp(Map<String,String> props,String ordinal){
    	Map<String,String> result = new HashMap<String,String>();
    	if(props != null && !props.isEmpty()){
    		Iterator<Map.Entry<String, String>> enities = new HashMap<String, String>(props).entrySet().iterator();
    		while(enities.hasNext()){
    			Map.Entry<String, String> entry = enities.next();
    			String key = entry.getKey();
    			String value = entry.getValue();
    			
    			int m = -1;
    			if((m = key.indexOf(":")) > 0){
    				if(key.endsWith(":" + ordinal)){
    					result.put(key.substring(0, m), value);
    				}
    			}else{
    				result.put(key, value);
    			}
    		}
    	}
		return result;
    }
    
    private static final String resource_section = "com.seeyon.v3x.main.section.resources.i18n.SectionResources";
    
    /**
     * 得到栏目页签的名称<br>
     * 默认定义在{@link com.seeyon.v3x.main.section.resources.i18n.SectionResources}中。<br>
     * 
     * @param panelId 页签id
     * @param props   配置参数集合
     * @return
     */
    public static String getPanelName(String panelId,Map<String,String> props){
    	String name = props != null ?props.get(panelId+"_name"):null;
    	if(Strings.isBlank(name)){
    		//判断type 如果有type 。取默认的type值。如果没有就白瞎了
    		if(props != null){
    			String type = props.get(panelId+"_type");
        		if(Strings.isNotBlank(type)){
        			return  ResourceBundleUtil.getString(resource_section, "section.panel."+type);
        		}
    		}
    		return ResourceBundleUtil.getString(resource_section, "section.panel."+panelId);
    	}
    	return name;
    }
    
    
}
