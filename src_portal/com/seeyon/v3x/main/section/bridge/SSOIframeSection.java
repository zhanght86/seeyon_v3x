/**
 * 
 */
package com.seeyon.v3x.main.section.bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.link.domain.LinkOption;
import com.seeyon.v3x.link.domain.LinkOptionValue;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.manager.OuterlinkManager;
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
public class SSOIframeSection extends GenericIframeSection {
	private static final Log log = LogFactory.getLog(SSOIframeSection.class);
	
	private OuterlinkManager outerlinkManager;
	
	private SectionDefinitionManager sectionDefinitionManager;
	
	private OrgManager orgManager ;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setOuterlinkManager(OuterlinkManager outerlinkManager) {
		this.outerlinkManager = outerlinkManager;
	}
	
	public String getId() {
		return "ssoIframeSection";
	}
	
	public void setSectionDefinitionManager(
			SectionDefinitionManager sectionDefinitionManager) {
		this.sectionDefinitionManager = sectionDefinitionManager;
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
			log.warn("获取SSOIframeSection栏目名称错误；" + e.getMessage());
		}
		
		return "SSOIframeSection";
	}
	
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		User user = CurrentUser.get();
		
		Long sectionDefinitionid = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
		Map<String, String> props = this.sectionDefinitionManager.getSectionProps(sectionDefinitionid);
		
		String ssoIframeLinkSystemId = props.get("ssoIframeLinkSystemId");
		String height = props.get("ssoIframePageHeight");
		LinkSystem ls = outerlinkManager.getLinkSystemById(Long.valueOf(ssoIframeLinkSystemId));
		String ssoIframeURL = props.get("ssoIframeURL");
		if(!ls.isNeedContentCheck()){
		    super.setUrl(magerURL(user.getId(), ssoIframeLinkSystemId, ssoIframeURL));
		} else {
		    String url = "/seeyon/linkManager.do?method=linkConnectForSectionDefinition&linkSystemId=" + ssoIframeLinkSystemId + "&sectionDefinitionId=" + sectionDefinitionid;
		    super.setUrl(url);
		}
		super.setFrameborder("1");
		super.setScrolling("auto");
		if(Strings.isNotBlank(height)){
			super.setHeight(height);
		}
		
       BaseSectionTemplete h =  super.projection(preference);
		
		if(Strings.isNotBlank(ssoIframeLinkSystemId)){
			h.addBottomButton("set_link_system", "/linkManager.do?method=userLinkMain&linkSystemId=" + ssoIframeLinkSystemId);
		}
		return h;
	}
	
	private String magerURL(long memberId, String linkSystemId){
		if(Strings.isBlank(linkSystemId)){
			return "";
		}
		
		Map<String, String> optionVos = new HashMap<String, String>();
		
		LinkSystem ls = outerlinkManager.getLinkSystemById(Long.parseLong(linkSystemId));
		if(ls == null){
			return "";
		}
		
		Set<LinkOption> options = ls.getLinkOption();
		if(options != null && options.size() > 0){
			List<Long> idlist = new ArrayList<Long>();
			Map<Long, LinkOption> map = new HashMap<Long, LinkOption>();
			for(LinkOption lo : options){
				idlist.add(lo.getId());
				map.put(lo.getId(), lo);
			}
			List<LinkOptionValue> values = outerlinkManager.findOptionValueById(idlist, memberId);	
			
			if(values != null && !values.isEmpty()){
				for(LinkOptionValue value : values){
					optionVos.put(map.get(value.getLinkOptionId()).getParamSign(), Functions.decodeStr(value.getValue()));
				}
			}
		}
		
		String baseSource = ls.getUrl();
		
        StringBuffer source = new StringBuffer(baseSource);
        if (baseSource.indexOf("?") == -1) {
			source.append("?");
		}
		else {
			source.append("&");
		}
        
        for (Iterator<String> iter = optionVos.keySet().iterator(); iter.hasNext();) {
        	String n = iter.next();
        	source.append(n + "=" + java.net.URLEncoder.encode(optionVos.get(n)) + "&");
		}
        
        return source.toString();
	}
	
	private String magerURL(long memberId, String linkSystemId, String ssoIframeURL){
        if(Strings.isBlank(linkSystemId)){
            return "";
        }
        
        Map<String, String> optionVos = new HashMap<String, String>();
        
        LinkSystem ls = outerlinkManager.getLinkSystemById(Long.parseLong(linkSystemId));
        if(ls == null){
            return "";
        }
        
        Set<LinkOption> options = ls.getLinkOption();
        if(options != null && options.size() > 0){
            List<Long> idlist = new ArrayList<Long>();
            Map<Long, LinkOption> map = new HashMap<Long, LinkOption>();
            for(LinkOption lo : options){
                idlist.add(lo.getId());
                map.put(lo.getId(), lo);
            }
            List<LinkOptionValue> values = outerlinkManager.findOptionValueById(idlist, memberId);  
            
            if(values != null && !values.isEmpty()){
                for(LinkOptionValue value : values){
                    optionVos.put(map.get(value.getLinkOptionId()).getParamSign(), Functions.decodeStr(value.getValue()));
                }
            }
        }
        if(ssoIframeURL == null || ssoIframeURL.trim().length() == 0){
            ssoIframeURL = ls.getUrl();
        }
        String baseSource = ssoIframeURL;
        
        StringBuffer source = new StringBuffer(baseSource);
        if (baseSource.indexOf("?") == -1) {
            if(optionVos.size() > 0){
                source.append("?");
            }
        }
        else {
            source.append("&");
        }
        
        for (Iterator<String> iter = optionVos.keySet().iterator(); iter.hasNext();) {
            String n = iter.next();
            source.append(n + "=" + java.net.URLEncoder.encode(optionVos.get(n)) + "&");
        }
        
        return source.toString();
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
					log.error("SSOIframeSection,ID:"+singleBoardId+",权限匹配错误：",e);
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
