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
import com.seeyon.v3x.main.section.definition.SectionDefinitionManager;
import com.seeyon.v3x.main.section.definition.domain.SectionDefinition;
import com.seeyon.v3x.main.section.definition.domain.SectionSecurity;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 *
 */
public class IframeSection extends GenericIframeSection {
	private static final Log log = LogFactory.getLog(IframeSection.class);
	
	private SectionDefinitionManager sectionDefinitionManager;
	
	private OrgManager orgManager ;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setSectionDefinitionManager(
			SectionDefinitionManager sectionDefinitionManager) {
		this.sectionDefinitionManager = sectionDefinitionManager;
	}
	
	public String getId() {
		return "iframeSection";
	}
	
	protected String getName(Map<String, String> preference) {
		try{
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
			log.warn("获取IframeSection栏目名称错误；" + e.getMessage());
		}
		
		return "IframeSection";
	}
	
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		Long sectionDefinitionid = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
		
		Map<String, String> props = this.sectionDefinitionManager.getSectionProps(sectionDefinitionid);
		
		String url = props.get("iframeURL");
		String height = props.get("iframePageHeight");
		
		super.setUrl(url);
		super.setFrameborder("1");
		super.setScrolling("auto");
		if(Strings.isNotBlank(height)){
			super.setHeight(height);
		}
		
		BaseSectionTemplete h =  super.projection(preference);

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
					log.error("IframeSection,ID:"+singleBoardId+",权限匹配错误：",e);
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
