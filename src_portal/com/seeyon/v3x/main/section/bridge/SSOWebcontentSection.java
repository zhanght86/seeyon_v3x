/**
 * 
 */
package com.seeyon.v3x.main.section.bridge;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.BaseSection;
import com.seeyon.v3x.main.section.definition.SectionDefinitionManager;
import com.seeyon.v3x.main.section.definition.domain.SectionDefinition;
import com.seeyon.v3x.main.section.definition.domain.SectionSecurity;
import com.seeyon.v3x.main.section.sso.SSOManager;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.HtmlTemplete;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 *
 */
public class SSOWebcontentSection extends BaseSection {
	private static final Log log = LogFactory.getLog(SSOWebcontentSection.class);
	
	private SectionDefinitionManager sectionDefinitionManager;
	private OrgManager orgManager ;

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

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
		return "ssoWebcontentSection";
	}

	protected String getName(Map<String, String> preference) {
		try {
			Long sectionDefinitionid = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
			
			SectionDefinition d = this.sectionDefinitionManager.getSectionDefinition(sectionDefinitionid);
			if(d != null) {
				//判断权限
				User user = CurrentUser.get();
				if(user.isAdmin()){
					return d.getName();
				}
				List<SectionSecurity> sectionSecurities = this.sectionDefinitionManager.getSectionSecurity(sectionDefinitionid);
				List<Long> userDomain = orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_ACCOUNT,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_MEMBER);
				for(SectionSecurity security : sectionSecurities){
					if(userDomain.contains(security.getEntityId())){
						return d.getName();
					}
				}
			}
			return null;
		}
		catch (Exception e) {
			log.warn("获取SSOWebcontentSection栏目名称错误；" + e.getMessage());
		}
		
		return "SSOWebcontentSection";
	}

	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	protected BaseSectionTemplete projection(Map<String, String> preference) {
		Long memberId = CurrentUser.get().getId();
		
		Long sectionDefinitionid = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
		
		Map<String, String> props = this.sectionDefinitionManager.getSectionProps(sectionDefinitionid);
		
		String linkSystemNameId = props.get("ssoWebcontentLinkSystemId");
		String sessionTimeout1 = props.get("ssoWebcontentSessionTimeout");
		String url = props.get("ssoWebcontentURL");
		String height = props.get("ssoWebcontentPageHeight");
		
		int sessionTimeout = 30;
		if(Strings.isNotBlank(sessionTimeout1)){
			sessionTimeout = Integer.parseInt(sessionTimeout1);
		}
		
		HtmlTemplete h = new HtmlTemplete();
		h.setModel(HtmlTemplete.ModelType.inner);
		
		if(Strings.isNotBlank(height)){
			h.setHeight(height);
		}
		
		if(Strings.isNotBlank(linkSystemNameId)){
			String html = null;
			try {
				html = this.ssoManager.useSSO(memberId, Long.parseLong(linkSystemNameId), sessionTimeout, url, "UTF-8");
			}
			catch (Exception e1) {
				log.error("", e1);
			}
			
			h.setHtml(html);
			
			h.addBottomButton("set_link_system", "/linkManager.do?method=userLinkMain&linkSystemId=" + linkSystemNameId);
		}
		
		return h;
	}
	@Override
	public boolean isAllowUserUsed(String singleBoardId) {
		if(Strings.isNotBlank(singleBoardId)){
			SectionDefinition d = this.sectionDefinitionManager.getSectionDefinition(Long.valueOf(singleBoardId));
			if(d != null) {
				//判断权限
				User user = CurrentUser.get();
				if(user.isAdmin()){
					return true;
				}
				List<SectionSecurity> sectionSecurities = this.sectionDefinitionManager.getSectionSecurity(Long.valueOf(singleBoardId));
				List<Long> userDomain;
				try {
					userDomain = orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_ACCOUNT,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_MEMBER);
					for(SectionSecurity security : sectionSecurities){
						if(userDomain.contains(security.getEntityId())){
							return true;
						}
					}
					return false;
				} catch (BusinessException e) {
					log.error("SSOWebcontentSection,ID:"+singleBoardId+",权限匹配错误：",e);
					return false;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

}
