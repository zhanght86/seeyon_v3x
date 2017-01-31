package com.seeyon.v3x.common.taglibs;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.plugin.menu.ThirdpartyAddinMenu;
import com.seeyon.v3x.util.Strings;

public class ShowThirdMenusTag extends BodyTagSupport{
	
	public static final String TAG_NAME = "showThirdMenus";
	
	private static final long serialVersionUID = 6698566395568009571L;
	
	private static final String IMAGE =  "/seeyon/common/images/toolbar/addMem.gif" ; 
	
	private static final String RESOURCE =  "com.seeyon.v3x.doc.resources.i18n.DocResource" ; 
	
	private static final String KEY =  "doc.menu.thirdPMenu.label" ; 
	
	private String rootBarName ;
	
	private String parentBarName ;
	
	private List<ThirdpartyAddinMenu> addinMenus  ;
	/**
	 * 下面三个参数暂时没有使用
	 */
	private String image ;
	
	private String resource ;
	
	private String key ;
	
	public ShowThirdMenusTag() {
		init();
	}

	public void init() {
		rootBarName = null;
		addinMenus = null ;
		image = null ;
		resource = null ;
		key = null ;
	}
	
	public int doEndTag() throws JspException {
		try{
			JspWriter out = pageContext.getOut();
			String lable = null ;
			
			//out.println("<script>");
			if(hasBarName()){
				if(addinMenus != null && !addinMenus.isEmpty()){
					out.println(parentBarName+".add(new WebFXMenuItem('', 'septalLine', '', ''));") ;
					for(ThirdpartyAddinMenu thirdpartyAddinMenu : addinMenus) {	
						lable = ResourceBundleUtil.getString(thirdpartyAddinMenu.getI18NResource(), thirdpartyAddinMenu.getLabel());			    	
						out.println(parentBarName+".add(new WebFXMenuItem('thirdPMenuSend', '"+lable+"', '"+ thirdpartyAddinMenu.getUrl()+"', ''));") ;		    	    	
					}
				}
			}else{
				if(rootBarName != null && addinMenus != null && !addinMenus.isEmpty()){
					out.println("var thirdPMenu = new WebFXMenu;") ;
					for(ThirdpartyAddinMenu thirdpartyAddinMenu : addinMenus) {			    			    	
						lable = ResourceBundleUtil.getString(thirdpartyAddinMenu.getI18NResource(), thirdpartyAddinMenu.getLabel());			    	
						out.println("thirdPMenu.add(new WebFXMenuItem('thirdPMenuSend', '"+lable+"', '"+ thirdpartyAddinMenu.getUrl()+"', ''));") ;		    	    	
					}
					lable = ResourceBundleUtil.getString (RESOURCE,KEY) ;
					out.println("try{");
					out.println(rootBarName+".add(new WebFXMenuButton('thirdPMenu', \""+lable+"\", '', \""+IMAGE+"\", \""+lable+"\", thirdPMenu));") ;
					out.println("}catch(e){alert('加载项菜单的加载出现错误！');}");
				}			
			}
			//out.println("</script>");
		}catch (IOException e) {
			throw new JspTagException(e.toString(), e);
		}
		
		init();
		return super.doEndTag();
	}
	
	public boolean hasBarName(){
		if(Strings.isNotBlank(parentBarName)){
			return true ;
		}
		return false ;
	}
	
	public String getLable(){
		if(Strings.isNotBlank(resource) && Strings.isNotBlank(key)){
			return ResourceBundleUtil.getString (resource,key) ;
		}
		return ResourceBundleUtil.getString (RESOURCE,KEY) ;
	}
	
	public String getImage(){
		if(Strings.isNotBlank(image)){
			return image ;
		}
		return IMAGE;
	}
	
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public void setRootBarName(String rootBarName) {
		this.rootBarName = rootBarName;
	}
	
	public void setParentBarName(String parentBarName) {
		this.parentBarName = parentBarName;
	}
	
	public void release() {
		super.release();
		init();
	}

	public void setAddinMenus(List<ThirdpartyAddinMenu> addinMenus) {
		this.addinMenus = addinMenus;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
}
