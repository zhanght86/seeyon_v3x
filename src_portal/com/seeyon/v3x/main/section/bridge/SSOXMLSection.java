/**
 * 
 */
package com.seeyon.v3x.main.section.bridge;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.BaseSection;
import com.seeyon.v3x.main.section.definition.SectionDefinitionManager;
import com.seeyon.v3x.main.section.definition.domain.SectionDefinition;
import com.seeyon.v3x.main.section.sso.SSOManager;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * 
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 *
 */
public class SSOXMLSection extends BaseSection {
	private static final Log log = LogFactory.getLog(SSOXMLSection.class);
	
	private SectionDefinitionManager sectionDefinitionManager;
	
	private SSOManager ssoManager;
	
	public void setSsoManager(SSOManager ssoManager) {
		this.ssoManager = ssoManager;
	}
	
	public void setSectionDefinitionManager(
			SectionDefinitionManager sectionDefinitionManager) {
		this.sectionDefinitionManager = sectionDefinitionManager;
	}
	
	public String getIcon() {
		return null;
	}

	public String getId() {
		return "ssoXMLSection";
	}

	protected String getName(Map<String, String> preference) {
		try{
			Long sectionDefinitionid = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
			
			SectionDefinition d = this.sectionDefinitionManager.getSectionDefinition(sectionDefinitionid);
			if(d!=null){
				return d.getName();
			}else{
				return null;
			}
		}
		catch (Exception e) {
			log.warn("获取SSOXMLSection栏目名称错误；" + e.getMessage());
		}
		
		return "SSOXMLSection";
	}

	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	protected BaseSectionTemplete projection(Map<String, String> preference) {
		Long memberId = CurrentUser.get().getId();
		
		Long sectionDefinitionid = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
		
		Map<String, String> props = this.sectionDefinitionManager.getSectionProps(sectionDefinitionid);
		
		String linkSystemId = props.get("ssoXMLLinkSystemId");
		String sessionTimeout1 = props.get("ssoXMLSessionTimeout");
		String url = props.get("ssoXMLURL");
		String showContentUrl = props.get("ssoXMLShowContentUrl");
		
		int sessionTimeout = 30;
		if(Strings.isNotBlank(sessionTimeout1)){
			sessionTimeout = Integer.parseInt(sessionTimeout1);
		}
		
		XMLTemplete t = new XMLTemplete();
		
		String xml = null;
		String cookies = "";
		if(Strings.isNotBlank(linkSystemId)){
			try {
				xml = this.ssoManager.useSSO(memberId, Long.parseLong(linkSystemId), sessionTimeout, url, null);
				Map<String, String> cs = this.ssoManager.getCookies(memberId, Long.parseLong(linkSystemId));
				
				int i = 0;
				for (Iterator<String> iter = cs.keySet().iterator(); iter.hasNext();) {
					String n = iter.next();
					if(i++ != 0){
						cookies += ";";
					}
					cookies += n + "=" + java.net.URLEncoder.encode(cs.get(n), "UTF-8");
				}
			}
			catch (Exception e1) {
				log.error("", e1);
			}
			
			t.addBottomButton("set_link_system", "/linkManager.do?method=userLinkMain&linkSystemId=" + linkSystemId);
		}
		
		t.setCookies(cookies);
		t.setXml(xml);
		t.setShowContentUrl(showContentUrl);
		
		return t;
	}
	
	public class XMLTemplete extends BaseSectionTemplete implements
			Serializable {

		private static final long serialVersionUID = -7811388727728141591L;

		private String xml;

		private String cookies;
		
		private String showContentUrl;

		public String getCookies() {
			return cookies;
		}

		public void setCookies(String cookies) {
			this.cookies = cookies;
		}

		public String getXml() {
			return xml;
		}

		public void setXml(String xml) {
			this.xml = xml;
		}
		
		public String getShowContentUrl() {
			return showContentUrl;
		}

		public void setShowContentUrl(String showContentUrl) {
			this.showContentUrl = showContentUrl;
		}

		public String getResolveFunction() {
			return "XMLTemplete";
		}
	}
	@Override
	public boolean isAllowUserUsed(String singleBoardId) {
		if(Strings.isNotBlank(singleBoardId)){
			SectionDefinition d = this.sectionDefinitionManager.getSectionDefinition(Long.valueOf(singleBoardId));
			if(d!=null){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

}
