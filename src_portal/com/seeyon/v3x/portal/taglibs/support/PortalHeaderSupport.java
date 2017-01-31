/**
 * 
 */
package com.seeyon.v3x.portal.taglibs.support;

import java.io.IOException;
import java.util.EnumMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.portal.decorations.PortalDecoration;
import com.seeyon.v3x.portal.util.PortalConstants;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.Constants.SpaceType;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.EnumUtil;

/**
 * 空间展现页面portal：header标签的实现
 * @author dongyj
 *
 */
public class PortalHeaderSupport extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5711961669119342801L;
	
	protected PortalDecoration decoration;
	
	private static SpaceManager spaceManager;
	private static void initManager(){
		if(spaceManager == null){
			spaceManager = (SpaceManager)ApplicationContextHolder.getBean("spaceManager");
		}
	}
	
	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}
	
	public PortalHeaderSupport(){
		init();
	}
	
	private void init(){
		decoration = null;
	}
	@Override
	public int doEndTag() throws JspException {
		initManager();
		
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		boolean isEdit = PortalConstants.isEdit(request);
		//编辑状态 不输出头
		JspWriter out = pageContext.getOut();
		try {
			String title = PortalConstants.getString(request,PortalConstants.TITLE);
			if(decoration == null){
				decoration = (PortalDecoration)request.getAttribute(PortalConstants.DECORATION);
			}
			String decoId = "";
			if(decoration != null){
				decoId = decoration.getId();
			}
			String contentPath = request.getContextPath();
			String language = Functions.getLanguage(request);
			if(!isEdit){//输出Constants.properties文件中设置的文件头
				out.println(PortalConstants.getString("portal.head",title,decoId,contentPath,language, Functions.resSuffix(),com.seeyon.v3x.skin.Constants.getUserSkinSuffix()));
			}
			if(decoration != null){
				out.println(getStyleStr(request));
			}
			SpaceFix fix = (SpaceFix)request.getAttribute(PortalConstants.SPACE_FIX);
			if(fix != null){
				out.println("<script type='text/javascript'>");
				SpaceType trueSpacetype = EnumUtil.getEnumByOrdinal(SpaceType.class, fix.getType());
				SpaceType type = Constants.parseDefaultSpaceType(trueSpacetype);
				out.println("var spaceType='"+type.name()+"';\n");
				out.println("var trueSpaceType='"+trueSpacetype.name()+"';\n");
				out.println("var spaceId='"+fix.getId()+"'\n");
				out.println("var pagePath='"+fix.getPagePath()+"';\n");
				out.println("var spaceName=\""+Functions.escapeJavascript(Constants.getSpaceName(fix))+"\";\n");
				out.println("var isAllowdefined=\""+fix.isAllowdefined()+"\";\n");
				if(type.equals(SpaceType.department)){
					out.println("var departmentSpaceId='"+fix.getEntityId()+"';\n");
				}
				out.println("</script>");
			}
			else{
				String pagePath =  PortalConstants.getString(request,PortalConstants.PAGEPATH);
				if(!"/seeyon/default.psml".equals(pagePath)){
					EnumMap<PortletEntityProperty.PropertyName, String>  pageParams = spaceManager.getPortletEntityProperty(pagePath);
					if(pageParams != null && !pageParams.isEmpty()){
						String spaceTypeStr = pageParams.get(PortletEntityProperty.PropertyName.spaceType);
						
						out.println("<script type='text/javascript'>");
						out.println("var spaceType='"+spaceTypeStr+"';\n");
						out.println("var trueSpaceType='"+spaceTypeStr+"';\n");
						out.println("var pagePath='"+pagePath+"';\n");
						out.println("var spaceId='"+request.getParameter("space_id")+"'\n");
						out.println("</script>");
					}
				}
			}
		}
		catch (IOException e) {
			throw new JspTagException(e.toString(), e);
		}
		return super.doEndTag();
	}
	

	private String getStyleStr(HttpServletRequest request){
		StringBuffer result = new StringBuffer();
		if(decoration != null){
			String[] styles = decoration.getStyle();
			for(String path : styles){
				String temp = parseStylePath(request,path);
				result.append((PortalConstants.getString("portal.link",temp, Functions.resSuffix())));
			}
		}
		return result.toString();
	}
	
	private String parseStylePath(HttpServletRequest request,String path){
		if(path != null){
			if(path.toLowerCase().startsWith("http://") || path.toLowerCase().startsWith("https://")){
				return path;
			}
			return request.getContextPath()+"/"+path;
		}
		return "";
	}
	

}
