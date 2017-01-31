package com.seeyon.v3x.agent.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.agent.domain.V3xAgent;
import com.seeyon.v3x.agent.domain.V3xAgentDetail;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;

public class AgentUtil {
	/**
	 * 得到代理的内容
	 * @param agent
	 * @return
	 */
	public static String getAgentOptionName(V3xAgent agent){
		String agentOptionName = "";
		String[] agentOptions = agent.getAgentOption().split("&");
		List<V3xAgentDetail> v3xdetails= agent.getAgentDetails();
		String seeyonResourceName = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
		String agentResourceName = "com.seeyon.v3x.agent.resources.i18n.AgentResources";
		boolean hasAudit = false;
		String temp = null;
		for(int i=0; i<agentOptions.length; i++){
			if("".equals(agentOptions[i])) continue;
			if(agentOptions[i].equals("1") && v3xdetails!= null && v3xdetails.size()>0){//协同细分下:模板和自由协同
				boolean isTemplate= false;
				boolean isFree= false;
				for (V3xAgentDetail v3xAgentDetail : v3xdetails) {
					if(v3xAgentDetail.getEntityId()==2){//代理了全部模板
						if(!isTemplate){
							isTemplate= true;
						}
					}else if(v3xAgentDetail.getEntityId()!=1){//代理了具体模板，有模板编号
						if(!isTemplate){
							isTemplate= true;
						}
					}else{//自由协同
						isFree= true;
					}
				}
				if( isTemplate && isFree){
					temp = ResourceBundleUtil.getString(seeyonResourceName, "application.1.label");//協同:包含自由協同和表單模板協同
				}else if( isTemplate ){
					temp = ResourceBundleUtil.getString(agentResourceName, "templatecoll.label"+(SystemEnvironment.hasPlugin("form")?"":".noForm"));//表單模板協同
					
				}else if(isFree){
					temp = ResourceBundleUtil.getString(seeyonResourceName, "application.1.1.label");//自由協同
				}
			}else if(String.valueOf(ApplicationCategoryEnum.bulletin.getKey()).equals(agentOptions[i]) 
					|| String.valueOf(ApplicationCategoryEnum.inquiry.getKey()).equals(agentOptions[i]) 
					|| String.valueOf(ApplicationCategoryEnum.news.getKey()).equals(agentOptions[i])){
				if(hasAudit)
					continue;
				else{
					temp = ResourceBundleUtil.getString(agentResourceName,"audit.label");
					hasAudit = true;
				}
			}else{
				temp = ResourceBundleUtil.getString(seeyonResourceName, "application." + agentOptions[i] + ".label");
			}
			
			if(i == (agentOptions.length-1)){
				agentOptionName += temp;
			}else{
				agentOptionName += temp + "、";	
			}
			
		}
		return agentOptionName.endsWith("、")?agentOptionName.substring(0, agentOptionName.length()-1):agentOptionName;
	}
	
	
	public static Object[] getUserAgentToMap(Long memberId){
		Object[] result = new Object[3];
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(memberId);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(memberId);
    	List<AgentModel> agentModelList = null;
		boolean agentToFlag = false;
		boolean isPloxy = false;
		if(_agentModelList != null && !_agentModelList.isEmpty()){
			isPloxy = true;
			agentModelList = _agentModelList;
		}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
			isPloxy = true;
			agentModelList = _agentModelToList;
			agentToFlag = true;
		}
		Map<Integer, AgentModel> agentModelMap = new HashMap<Integer, AgentModel>();
		List<AgentModel> list = new ArrayList<AgentModel>();
		List<AgentModel> collAgent = new ArrayList<AgentModel>();
		List<AgentModel> edocAgent = new ArrayList<AgentModel>();
		List<AgentModel> meetingAgent = new ArrayList<AgentModel>();
		List<AgentModel> bulletinAgent = new ArrayList<AgentModel>();
		List<AgentModel> inquiryAgent = new ArrayList<AgentModel>();
		List<AgentModel> newsAgent = new ArrayList<AgentModel>();
		List<AgentModel> infoAgent = new ArrayList<AgentModel>();
		if(isPloxy){
			Date now = new Date();
	    	for(AgentModel agentModel : agentModelList){
	    		//过滤：只有处于期限内的代理才保留
	    		if(now.before(agentModel.getStartDate()) || now.after(agentModel.getEndDate()))
	    			continue;
	    		list.add(agentModel);
	    		String agentOptionStr = agentModel.getAgentOption();
	    		String[] agentOptions = agentOptionStr.split("&");
	    		for(String agentOption : agentOptions){
	    			int _agentOption = Integer.parseInt(agentOption);
	    			if(_agentOption == ApplicationCategoryEnum.collaboration.key()){
	    				collAgent.add(agentModel);
	    				if(agentModelMap.get(ApplicationCategoryEnum.collaboration.key()) != null ){
	    					if(agentModel.getStartDate().before(agentModelMap.get(ApplicationCategoryEnum.collaboration.key()).getStartDate()))
	    						agentModelMap.put(ApplicationCategoryEnum.collaboration.key(), agentModel);
	    				}else{
	    					agentModelMap.put(ApplicationCategoryEnum.collaboration.key(), agentModel);
	    				}
	    			}else if(_agentOption == ApplicationCategoryEnum.edoc.key()){
	    				edocAgent.add(agentModel);
	    				
	    				if(agentModelMap.get(ApplicationCategoryEnum.edoc.key()) != null ){
	    					if(agentModel.getStartDate().before(agentModelMap.get(ApplicationCategoryEnum.edoc.key()).getStartDate()))
	    						agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
	    				}else{
	    					agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
	    				}
	    			}else if(_agentOption == ApplicationCategoryEnum.meeting.key()){
	    				meetingAgent.add(agentModel);
	    				if(agentModelMap.get(ApplicationCategoryEnum.meeting.key()) != null){
	    					if(agentModel.getStartDate().before(agentModelMap.get(ApplicationCategoryEnum.meeting.key()).getStartDate()))
	    						agentModelMap.put(ApplicationCategoryEnum.meeting.key(), agentModel);
	    				}else{
	    					agentModelMap.put(ApplicationCategoryEnum.meeting.key(), agentModel);
	    				}
	    			}else if(_agentOption == ApplicationCategoryEnum.bulletin.getKey()){
	    				bulletinAgent.add(agentModel);
	    				if(agentModelMap.get(ApplicationCategoryEnum.bulletin.key()) != null){
	    					if(agentModel.getStartDate().before(agentModelMap.get(ApplicationCategoryEnum.bulletin.key()).getStartDate()))
	    						agentModelMap.put(ApplicationCategoryEnum.bulletin.key(), agentModel);
	    				}else{
	    					agentModelMap.put(ApplicationCategoryEnum.bulletin.key(), agentModel);
	    				}
	    			}else if(_agentOption == ApplicationCategoryEnum.inquiry.getKey()){
	    				inquiryAgent.add(agentModel);
	    				if(agentModelMap.get(ApplicationCategoryEnum.inquiry.key()) != null){
	    					if(agentModel.getStartDate().before(agentModelMap.get(ApplicationCategoryEnum.inquiry.key()).getStartDate()))
	    						agentModelMap.put(ApplicationCategoryEnum.inquiry.key(), agentModel);
	    				}else{
	    					agentModelMap.put(ApplicationCategoryEnum.inquiry.key(), agentModel);
	    				}
	    			}else if(_agentOption == ApplicationCategoryEnum.news.getKey()){
	    				newsAgent.add(agentModel);
	    				if(agentModelMap.get(ApplicationCategoryEnum.news.key()) != null){
	    					if(agentModel.getStartDate().before(agentModelMap.get(ApplicationCategoryEnum.news.key()).getStartDate()))
	    						agentModelMap.put(ApplicationCategoryEnum.news.key(), agentModel);
	    				}else{
	    					agentModelMap.put(ApplicationCategoryEnum.news.key(), agentModel);
	    				}
					} else if (_agentOption == ApplicationCategoryEnum.info.getKey()) {
						infoAgent.add(agentModel);
						if (agentModelMap.get(ApplicationCategoryEnum.info.key()) != null) {
							if (agentModel.getStartDate().before(agentModelMap.get(ApplicationCategoryEnum.info.key()).getStartDate()))
								agentModelMap.put(ApplicationCategoryEnum.info.key(), agentModel);
						} else {
							agentModelMap.put(ApplicationCategoryEnum.info.key(), agentModel);
						}
					}
	    		}
	    	}
		}
		Map<Integer,List<AgentModel>> ma = new HashMap<Integer,List<AgentModel>>();
		if(!collAgent.isEmpty()){
			ma.put(ApplicationCategoryEnum.collaboration.key(), collAgent);
		}
		if(!edocAgent.isEmpty()){
			ma.put(ApplicationCategoryEnum.edoc.key(), edocAgent);
		}
		if(!meetingAgent.isEmpty()){
			ma.put(ApplicationCategoryEnum.meeting.key(), meetingAgent);
		}
		if(!newsAgent.isEmpty()){
			ma.put(ApplicationCategoryEnum.news.key(), newsAgent);
		}
		if(!inquiryAgent.isEmpty()){
			ma.put(ApplicationCategoryEnum.inquiry.key(), inquiryAgent);
		}
		if(!bulletinAgent.isEmpty()){
			ma.put(ApplicationCategoryEnum.bulletin.key(), bulletinAgent);
		}
		if (!infoAgent.isEmpty()) {
			ma.put(ApplicationCategoryEnum.info.key(), infoAgent);
		}
		result[0] = agentToFlag;
		result[1] = ma;
		result[2] = list;
		return result;
	}
	
	/**
	 * 判断是否代理自由流程，不包括选择全部协同的情况
	 * @param agent
	 * @return
	 */
	public static boolean hasFreeColl(V3xAgent agent){
		List<V3xAgentDetail> details = agent.getAgentDetails(); 
		if(details != null && !details.isEmpty()){
			for(V3xAgentDetail detail:details){
				if(detail.getEntityId()==1){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断是否代理全部模板流程，不包括选择全部协同的情况
	 * @param agent
	 * @return
	 */
	public static boolean hasAllTemplate(V3xAgent agent){
		List<V3xAgentDetail> details = agent.getAgentDetails(); 
		if(details != null && !details.isEmpty()){
			for(V3xAgentDetail detail:details){
				if(detail.getEntityId()==2){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断是否代理全部协同（包括全部自由流程、模板），注意只有填充代理明细列表后才能使用此方法判断
	 * @param agent
	 * @return
	 */
	public static boolean isAllColl(V3xAgent agent){
		return agent.getAgentDetails()==null;
	}
	
	/**
	 * 判断是否代理了协同模板，因为在调用前就判断是否是协同所以不用判断
	 * @param agent
	 * @return
	 */
	public static boolean hasTemplate(V3xAgent agent){
		List<V3xAgentDetail> details = agent.getAgentDetails(); 
		if(details != null && !details.isEmpty()){
			for(V3xAgentDetail detail:details){
				if(detail.getEntityId()!=1){
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	/**
	 * 判断同一时间段内，不同的代理人是否代理了相同的表单/协同模板
	 * @param agent
	 * @param templateIds
	 * @return
	 */
	//同一时间段内，不同的代理人是允许代理不同的表单/协同模板
	public static boolean hasTemplate(V3xAgent agent, String templateIds) {
		List<V3xAgentDetail> details = agent.getAgentDetails();
		if (StringUtils.isNotBlank(templateIds)) {
			List<Long> templates = FormBizConfigUtils.parseStr2Ids(templateIds);
			if (details != null && !details.isEmpty()) {
				for (V3xAgentDetail detail : details) {
					if (detail.getEntityId() == 2) {
						return true;
					}
					if (detail.getEntityId() != 1 && templates.contains(2L)) {
						return true;
					}
					if (templates.contains(detail.getEntityId())) {
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}
	
	/**
	 * 取得在代理期内代理我某个应用的人员id
	 * @param userId
	 * @param app
	 * @return
	 */
	public static Long getAgentByApp(Long userId,int app){
		return MemberAgentBean.getInstance().getAgentMemberId(app,userId);
	}
}
