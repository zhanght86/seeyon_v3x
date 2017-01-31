package com.seeyon.v3x.portal;

import java.util.EnumMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.main.section.BaseBannerSection;
import com.seeyon.v3x.main.section.BaseSection;
import com.seeyon.v3x.main.section.SectionRegisterManager;
import com.seeyon.v3x.portal.parse.MacroParse;
import com.seeyon.v3x.portal.parse.MacroParseBean;
import com.seeyon.v3x.portal.util.PortalConstants;
import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Strings;


public class SectionPortletFunction {
	private static PortletEntityPropertyManager portletEntityPropertyManager;
	private static SpaceManager spaceManager;
	private static SectionRegisterManager sectionRegisterManager;
	private static void initManager(){
		if(portletEntityPropertyManager == null){
			portletEntityPropertyManager = (PortletEntityPropertyManager)ApplicationContextHolder.getBean("portletEntityPropertyManager");
			spaceManager = (SpaceManager)ApplicationContextHolder.getBean("spaceManager");
			sectionRegisterManager = (SectionRegisterManager)ApplicationContextHolder.getBean("sectionRegisterManager");
		}
	}
	
	public static String parseFragment(Fragment fragment, String pagePath, int width, Long spaceId, String showState) {
		initManager();
		
		StringBuffer result = new StringBuffer();
		String entityId = String.valueOf(fragment.getId());
		Map<String, String> props = portletEntityPropertyManager.getPropertys(fragment.getId());
		
		String sections = props.get(PortletEntityProperty.PropertyName.sections.name());
		
		EnumMap<PortletEntityProperty.PropertyName, String>  pageParams = spaceManager.getPortletEntityProperty(pagePath);
		if(pageParams == null){
			return "";
		}
//		String pagePath = pageParams[0];
		String spaceTypeStr = pageParams.get(PortletEntityProperty.PropertyName.spaceType);
		String spaceEntityId = pageParams.get(PortletEntityProperty.PropertyName.ownerId);
		
		//modify 频道里面没有东西，就显示空白
		boolean isNotNull = StringUtils.isNotBlank(sections) && !"undefined".equals(sections);
		
		String nodeId = String.valueOf(Math.abs(UUIDLong.longUUID()));
		String sectionPanels = "sectionPanels" + nodeId;
		if (isNotNull) {
			StringBuffer out = new StringBuffer();
			out.append("<div id=\"sectionHeader"+nodeId+"\" class=\"portal-layout-cell_head\"><div class=\"portal-layout-cell_head_l\"></div><div class=\"portal-layout-cell_head_r\"></div></div>");
			out.append("<table width=\"100%\" border=\"0\" class=\"portal-layout-cell-right\" cellspacing=\"0\" cellpadding=\"0\" onmouseover=\"showEditButton('"+nodeId+"')\" onmouseout=\"hiddeEditButton('"+nodeId+"')\">");
			out.append("  <tr>");
			out.append("    <td id=\"title" + nodeId + "\" class='sectionTitleLine "+(isNotNull?"sectionTitleLineBackground":"")+"'>&nbsp;</td>");
			out.append("  </tr>");
			out.append("  <tr>");
			out.append("    <td id=\"" + nodeId + "\" class='sectionBody "+(isNotNull?"sectionBodyBorder":"")+"'>&nbsp;</td>");
			out.append("  </tr>");
			out.append("</table>");
			out.append("<div class=\"portal-layout-cell_footer\"><div class=\"portal-layout-cell_footer_l\"></div><div class=\"portal-layout-cell_footer_r\"></div></div>");
			
			out.append("<script>");
			//在Header中已经有了，不再定义
//			out.append("var " + PortletEntityProperty.PropertyName.spaceType.name() + " = \"" + spaceTypeStr + "\";");
			out.append("var " + PortletEntityProperty.PropertyName.ownerId.name() + " = \"" + spaceEntityId + "\";");
			out.append("var " + sectionPanels + " = [];");
			
			String[] sectionIds = sections.split(",");
			for (int i = 0; i < sectionIds.length; i++) {
				String sectionId = sectionIds[i];
				if(StringUtils.isBlank(sectionId)){
					continue;
				}
				
				BaseSection sectionMgr = sectionRegisterManager.getSection(sectionId);
				//BaseSection sectionMgr = sectionRegisterManager.getSection(sections);
				
				if(sectionMgr == null){
					//log.warn("Spring Bean [" + sectionId + "]没有定义。PortletName = " + super.getPortletName());
					continue;
				}
				
				if (isBanner(sectionMgr)) {
					if ("view".equals(showState)) {
						String htm = showBanner(sectionMgr, entityId, String.valueOf(i), spaceTypeStr, spaceEntityId, spaceId);
						out = null;
						return htm;
					}
				}
				
				try {
					String singleBoardId = props.get("singleBoardId:"+i);
					if(!sectionMgr.isAllowUserUsed(singleBoardId)){
						continue;
					}
				}
				catch (Exception e) {
					//log.error("", e);
					return "";
				}
				
				String label = sectionMgr.doGetName(entityId, String.valueOf(i), spaceTypeStr, spaceEntityId);
				if(label == null){
					continue;
				}
				String icon  = Strings.escapeNULL(sectionMgr.getIcon(), "");
				Integer total = sectionMgr.doGetTotal(entityId, String.valueOf(i), spaceTypeStr, spaceEntityId);
				String TotalUnit = sectionMgr.doGetTotalUnit(entityId, String.valueOf(i), spaceTypeStr, spaceEntityId);
				
				String beanId = sectionRegisterManager.getSectionBeanId(sectionId);
				
				boolean isReadOnly = sectionMgr.isReadOnly(spaceTypeStr, spaceEntityId);
				boolean hasParam = sectionMgr.hasParam();
				//out.append("var sectionPanel_from_server = new SectionPanel(\"" + Math.abs(UUIDLong.longUUID()) + "\", \"" + nodeId + "\", \"" + sections + "\", \"" + beanId + "\", \"" + label + "\", " + total + ", \"" + (TotalUnit == null ? "" : TotalUnit) + "\", \"" + entityId + "\", \"" + 0 + "\", \"" + icon + "\",\""+width+"\",\""+sectionMgr.getDelay()+"\",\""+isReadOnly+"\",\""+hasParam+"\");\n");
				//out.append(sectionPanels + "[" + sectionPanels + ".length] = sectionPanel_from_server;\n");
				out.append(sectionPanels + "[" + sectionPanels + ".length] = new SectionPanel(\"" + Math.abs(UUIDLong.longUUID()) + "\", \"" + nodeId + "\", \"" + sections + "\", \"" + beanId + "\", \"" + Functions.escapeJavascript(label) + "\", " + total + ", \"" + (TotalUnit == null ? "" : TotalUnit) + "\", \"" + entityId + "\", \"" + i + "\", \"" + icon + "\",\""+width+"\",\""+sectionMgr.getDelay()+"\",\""+isReadOnly+"\",\""+hasParam+"\");\n");
			}
			out.append("showSectionPanel(\"" + nodeId + "\", " + sectionPanels + ");");
			out.append("</script>");
			result.append(out.toString());
		}
		
		return result.toString();
	}
	
	/**
	 * 
	 * @param macroName
	 * @param page
	 * @return
	 * 
	 */
	public static String _(String macroName,PageContext page){
		String value = MacroParse.getMacro(macroName);
		if(Strings.isNotBlank(value)){
			ServletRequest request = page.getRequest();
			MacroParseBean bean = new MacroParseBean((HttpServletRequest)request);
			value = bean.parse(value);
		}
		return value;
	}
	
	
	public static Boolean isEdit(PageContext page){
		HttpServletRequest request = (HttpServletRequest) page.getRequest();
		boolean isEdit = PortalConstants.isEdit(request);
		return isEdit;
	}
	
	public static Boolean isBanner(Fragment fragment){
		if(fragment == null){
			return false;
		}
		initManager();
		Map<String, String> props = portletEntityPropertyManager.getPropertys(fragment.getId());
		String sections = props.get(PortletEntityProperty.PropertyName.sections.name());
		if(sections != null){
			String[] ss = sections.split(",");
			if(ss.length ==1){
				return isBanner(sectionRegisterManager.getSection(ss[0]));
			}
		}
		return false;
	}
	private static Boolean isBanner(Object o){
		if(o == null) return false;
		return o instanceof BaseBannerSection;
	}
	
	public static String showBanner(Object tem,String entityId, String ordinal, String spaceType, String ownerId,Long spaceId){
		if(tem instanceof BaseBannerSection){
			BaseBannerSection banner = (BaseBannerSection)tem	;
			return banner.getHTML(entityId, ordinal,  spaceType, ownerId,spaceId);
		}
		return null;
	}
	public static Boolean isAllowedUserUsed(Fragment fragment){
		if(fragment == null){
			return false;
		}
		initManager();
		Map<String, String> props = portletEntityPropertyManager.getPropertys(fragment.getId());
		String sections = props.get(PortletEntityProperty.PropertyName.sections.name());
		//modify 频道里面没有东西，就显示空白
		boolean isNotNull = StringUtils.isNotBlank(sections) && !"undefined".equals(sections);
		if (isNotNull) {
			String[] sectionIds = sections.split(",");
			for (int i = 0; i < sectionIds.length; i++) {
				String sectionId = sectionIds[i];
				if(StringUtils.isBlank(sectionId)){
					continue;
				}
				BaseSection sectionMgr = sectionRegisterManager.getSection(sectionId);
				if(sectionMgr == null){
					continue;
				}
				String singleBoardId = props.get("singleBoardId:"+i);
				if(sectionMgr.isAllowUserUsed(singleBoardId)){
					return true;
				}else{
					continue;
				}
			}
			return false;
		}else{
			return false;
		}
	}
}