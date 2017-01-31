package com.seeyon.v3x.plugin.deeSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.Document;
import com.seeyon.v3x.dee.Document.Element;
import com.seeyon.v3x.dee.Parameters;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.main.section.bridge.GenericIframeSection;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.HtmlTemplete;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionDefine;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionProps;
import com.seeyon.v3x.plugin.deeSection.domain.DeeSectionSecurity;
import com.seeyon.v3x.plugin.deeSection.manager.DeeSectionManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

public class DeeSection extends GenericIframeSection {

	private static final Log log = LogFactory.getLog(DeeSection.class);
	
	private DeeSectionManager deeSectionManager;

	private OrgManager orgManager ;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setDeeSectionManager(DeeSectionManager deeSectionManager) {
		this.deeSectionManager = deeSectionManager;
	}
	
	public String getId() {
		return "deeSection";
	}
	
	@Override
	protected Integer getTotal(Map<String, String> preference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected String getName(Map<String, String> preference) {
		try{
			Long deeSectionDefineId = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
			
			DeeSectionDefine d = deeSectionManager.findDeeSectionById(deeSectionDefineId);
			
			if(d != null) {
				//判断权限
				User user = CurrentUser.get();
				if(user.isAdmin()){
					return d.getDeeSectionName();
				}
				List<DeeSectionSecurity> sectionSecurities = this.deeSectionManager.getSectionSecurity(deeSectionDefineId);
				List<Long> userDomain = orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_ACCOUNT,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_MEMBER);
				for(DeeSectionSecurity security : sectionSecurities){
					if(userDomain.contains(security.getEntityId())){
						return d.getDeeSectionName();
					}
				}
			}
			return null;
		}
		catch (Exception e) {
			log.warn("获取DeeSection栏目名称错误；" + e.getMessage());
		}
		
		return "deeSection";
	}
	
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		Long deeSectionDefineId = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
		Long entityId = Long.parseLong(preference.get(PropertyName.entityId.name()));
		String ordinal = preference.get(PropertyName.ordinal.name()).toString();
		
		List<DeeSectionProps> props = deeSectionManager.getSectionProps(deeSectionDefineId);
		
		Map<String,String> defaultShowProps = new LinkedHashMap<String,String>();
		if(CollectionUtils.isNotEmpty(props)){
			for(DeeSectionProps p : props){
				if(p.getIsShow()==0){
					defaultShowProps.put(p.getPropName(), p.getPropValue());
				}
			}
		}
		
		Map<String,String> showProps = new LinkedHashMap<String,String>();
		
		String rowList = preference.get("rowList");
		String size = preference.get("count");
		if(Strings.isBlank(size)){
			size = "7";
		}
		if(Strings.isNotBlank(rowList)&&rowList.equals("showField")){
			String showFields = preference.get("showField_value");
			if(Strings.isNotBlank(showFields)){
				String[] fields = showFields.split(",");
				Set<String> keys = defaultShowProps.keySet();
				for(String key : keys){
					for(int i=0; i<fields.length; i++){
						if(fields[i].equals(key)){
							showProps.put(key, defaultShowProps.get(key));
						}
					}
				}
			}
		}
		
		Set<String> keys = null;
		if(showProps!=null&&!showProps.isEmpty()){
			keys = showProps.keySet();
		}else{
			keys = defaultShowProps.keySet();
		}
		
		DeeSectionDefine deeSectionDefine = deeSectionManager.findDeeSectionById(deeSectionDefineId);
		
		List<Map<String,String>> data = new ArrayList<Map<String,String>>();
		DEEClient client = new DEEClient();
		try {
			Parameters param = new Parameters();
			param.add("Paging_pageSize", Integer.valueOf(size));
			param.add("Paging_pageNumber", Integer.valueOf(1));
			param.add("whereString", " where 1=1");
			Document document = client.execute(String.valueOf(deeSectionDefine.getFlowId()),param);
			Element root =  document.getRootElement();
			List<Element> list = root.getChildren();
			if(CollectionUtils.isNotEmpty(list)){
				for(Element t : list){
					
					List<Element> rows = t.getChildren();
					if(CollectionUtils.isNotEmpty(rows)){
						for(Element row : rows){
							if(props!=null&&!props.isEmpty()){
								Map<String,String> rowMap = new LinkedHashMap<String,String>();
								
								for(String key : keys){
									Element e = row.getChild(key);
									if(e!=null){
										if(e.getValue()!=null){
											rowMap.put(key, e.getValue().toString());
										}else{
											rowMap.put(key, "");
										}
									}else{
										rowMap.put(key, "");
									}
								}
								data.add(rowMap);
							}
						}
					}
				}
			}
		} catch (TransformException e) {
			log.error("DEE栏目执行引擎查询出错："+e);
		}
		
		
		HtmlTemplete ht = new HtmlTemplete();
		StringBuilder html = new StringBuilder();
		html.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""+SystemEnvironment.getA8ContextPath()
+"/common/skin/default/skin.css\" />");
		html.append("<div class=\"mxt-grid-header\">");
		html.append("<table class=\"sort ellipsis\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
		if(keys!=null){
			html.append("<THEAD class=\"mxt-grid-thead\" >");
			html.append("<tr class=\"sort\" height=\"25px\">");
			for(String key : keys){
				html.append("<td align=\"center\" type=\"String\" height=\"25px\">"+defaultShowProps.get(key)+"</td>");
			}
			html.append("</tr>");
			html.append("</THEAD>");
			
			html.append("<TBODY>");
			if(CollectionUtils.isNotEmpty(data)){
				for(Map<String,String> row : data){
					html.append("<tr class=\"sort erow\" height=\"25px\">");
					for(String key : keys){
						String value = row.get(key);
						if(!value.equals("")){
							html.append("<td align=\"center\" class=\"sort\" height=\"25px\">"+value+"</td>");
						}else{
							html.append("<td align=\"center\" class=\"sort\" height=\"25px\">&nbsp;</td>");
						}
					}
					html.append("</tr>");
				}
			}
			html.append("</TBODY>");
			
			
		}
		
		
		html.append("</table>");
		html.append("</div>");
        
        String height = "208";
		if (Strings.isNotBlank(size)) {
			height = String.valueOf((Integer.parseInt(size)+1)*26);
		}
		ht.setHeight(height);
		ht.setHtml(html.toString());
		ht.setModel(HtmlTemplete.ModelType.inner);
		ht.setShowBottomButton(true);
		ht.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "javascript:openDeeSectionMore('/deeSectionController.do?method=showSectionData&sectionDefineId="+deeSectionDefineId+"&entityId="+entityId+"&ordinal="+ordinal+"');");
		
		return ht;
	}
	@Override
	public boolean isAllowUsed() {
		if(!SystemEnvironment.hasPlugin("dee")||!DeeSectionFunction.isOpenPortalSection()){
			return false;
		}else{
			return true;
		}
	}
	@Override
	public boolean isAllowUserUsed(String singleBoardId) {
		if(!SystemEnvironment.hasPlugin("dee")||!DeeSectionFunction.isOpenPortalSection()){
			return false;
		}
		if(Strings.isBlank(singleBoardId)){
			return false;
		}
		
		DeeSectionDefine d = deeSectionManager.findDeeSectionById(Long.valueOf(singleBoardId));
		if(d != null) {
			//判断权限
			User user = CurrentUser.get();
			if(user.isAdmin()){
				return true;
			}
			List<DeeSectionSecurity> sectionSecurities = this.deeSectionManager.getSectionSecurity(Long.valueOf(singleBoardId));
			List<Long> userDomain;
			try {
				userDomain = orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_ACCOUNT,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_MEMBER);
				for(DeeSectionSecurity security : sectionSecurities){
					if(userDomain.contains(security.getEntityId())){
						return true;
					}
				}
				return false;
			} catch (BusinessException e) {
				log.error("获取DeeSection栏目错误:",e);
				return false;
			}
		}else{
			return false;
		}
	}
}
