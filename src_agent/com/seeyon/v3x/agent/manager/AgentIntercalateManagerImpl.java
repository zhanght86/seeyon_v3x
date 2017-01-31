/**
 * 
 */
package com.seeyon.v3x.agent.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.agent.dao.AgentIntercalateDAO;
import com.seeyon.v3x.agent.domain.V3xAgent;
import com.seeyon.v3x.agent.domain.V3xAgentDetail;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.authenticate.domain.V3xAgentDetailModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;

/**
 * @author jincm
 * @version 1.0 2008-7-30
 */
public class AgentIntercalateManagerImpl extends BaseHibernateDao<V3xAgent> 
	implements AgentIntercalateManager {
	private static final Log log = LogFactory.getLog(AgentIntercalateManagerImpl.class);
	
	private AgentIntercalateDAO agentIntercalateDAO;
	private UserMessageManager userMessageManager;
	private OrgManager orgManager;

	public void setAgentIntercalateDAO(AgentIntercalateDAO agentIntercalateDAO) {
		this.agentIntercalateDAO = agentIntercalateDAO;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	@SuppressWarnings("unchecked")
	public void init(){
		try {
			List agentList = agentIntercalateDAO.findAllAvailability();
			List<Long> ids = new ArrayList();
			for(int i=0; i<agentList.size(); i++){
				V3xAgent agent = (V3xAgent)agentList.get(i);
				ids.add(agent.getId());
			}
			Map<Long,List<V3xAgentDetailModel>> m = this.getDetailModelByAgents(ids);
				
			for (int i = 0; i < agentList.size(); i++) {
				V3xAgent agent = (V3xAgent) agentList.get(i);
				AgentModel agentModel = new AgentModel();
				agentModel.setAgentId(agent.getAgentId());
				agentModel.setAgentToId(agent.getAgentToId());
				agentModel.setId(agent.getId());
				agentModel.setAgentOption(agent.getAgentOption());
				agentModel.setStartDate(agent.getStartDate());
				agentModel.setEndDate(agent.getEndDate());
				if (m != null)
					agentModel.setAgentDetail(m.get(agent.getId()));
				MemberAgentBean.getInstance().put(agent.getAgentId(), agentModel, null);
				MemberAgentBean.getInstance().put(agent.getAgentToId(), null, agentModel);
			}
			
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	public List<V3xAgent> queryAvailabilityList(Long agentToId,String... type) throws Exception{
		List<V3xAgent> availabilityAgentList = agentIntercalateDAO.findAvailabilityByAgentToId(agentToId,type);
		return availabilityAgentList;
	}
	
	public List<V3xAgent> queryHistoryList(Long agentToId) throws Exception{
		List<V3xAgent> historyAgentList = new ArrayList<V3xAgent>();
		historyAgentList = agentIntercalateDAO.findHistoryByAgentToId(agentToId);
		return historyAgentList;
	}
	
	public List<V3xAgent> queryAvailabilityList1(Long agentId,String... type) throws Exception{
		List<V3xAgent> availabilityAgentList = agentIntercalateDAO.findAvailabilityByAgentId(agentId,type);
		return availabilityAgentList;
	}
	
	public List<V3xAgent> queryHistoryList1(Long agentId) throws Exception{
		List<V3xAgent> historyAgentList = new ArrayList<V3xAgent>();
		historyAgentList = agentIntercalateDAO.findHistoryByAgentId(agentId);
		return historyAgentList;
	}
	
	public V3xAgent getById(Long id) throws Exception {
		V3xAgent agent = agentIntercalateDAO.getById(id);
		agent= agentIntercalateDAO.findAgentWithDetailById(agent);
		return agent;
	}
	
	public List<V3xAgent> queryAllValidity() throws Exception{
		return agentIntercalateDAO.findAllAvailability();
	}
	//public String[] compareDate(String _beginDate, String _endDate, String startDate){
	//至于当天比较 代理时间不能早于当天时间
	public String[] compareDate(String _beginDate, String _endDate, String startDate){
		Date beginDate = Datetimes.parseDatetimeWithoutSecond(_beginDate);
		Date endDate = Datetimes.parseDatetimeWithoutSecond(_endDate);
		Date now = null;
		//if(!"".equals(startDate)){
		//	now = Datetimes.parseDatetimeWithoutSecond(startDate);
		//}else{
			now = new Date(System.currentTimeMillis());
			String nowStr = Datetimes.formatDate(now);
			now = Datetimes.parseDatetimeWithoutSecond(nowStr);
		//}
		int compareValue1 = beginDate.compareTo(now);
		int compareValue2 = endDate.compareTo(beginDate);
		String resourceName = "com.seeyon.v3x.agent.resources.i18n.AgentResources";
		String result = "";
		if(compareValue1 == -1){
			result = ResourceBundleUtil.getString(resourceName, "agent.start.time.alert", "");
			return new String[]{"1", result};
		}else if(compareValue2 == -1){
			result = ResourceBundleUtil.getString(resourceName, "agent.end.time.alert", "");
			return new String[]{"2", result};
		}else{
			return null;
		}
	}
	
	public String checkDataValidity(String agentId, String _beginDate, String _endDate,
			String[] selectedValues, String currentAgentId, String templateIds){
		User user = CurrentUser.get();
		Date beginDate = Datetimes.parseDatetimeWithoutSecond(_beginDate);
		Date endDate = Datetimes.parseDatetimeWithoutSecond(_endDate);
		
	    long startDate=beginDate.getTime();
        long endDateL=endDate.getTime();
        
        String resourceName = "com.seeyon.v3x.agent.resources.i18n.AgentResources";
		try {
			List<V3xAgent> _agentList = queryAvailabilityList1(user.getId());
			if(_agentList != null && !_agentList.isEmpty()){
			    //最小,最大日期
			    long minDate=0;
			    long maxDate=0;
			  
				for(V3xAgent agent : _agentList){      
	                minDate=agent.getStartDate().getTime();
	                maxDate=agent.getEndDate().getTime();
	                
	                boolean flag=true;
	                if(endDateL<=minDate||startDate>=maxDate){
	                    flag=false;
	                }
	               //return "您已经被"+member.getName()+"委托为代理人，不能继续操作。";
	                if(flag){
	                    V3xOrgMember member = orgManager.getMemberById(agent.getAgentToId());
	                    return ResourceBundleUtil.getString(resourceName, "agent.is.agent.alert",Datetimes.formatDatetimeWithoutSecond(agent.getStartDate()),Datetimes.formatDatetimeWithoutSecond(agent.getEndDate()),member.getName());
	                }
				}
			}				
            //查询当前设置的代理人，在设置的时间段内，是否委托他人代理
            List<V3xAgent> list = agentIntercalateDAO.findAvailabilityByAgentToId(Long.parseLong(agentId));
            
			if(list != null && !list.isEmpty()){
			    long minDate=0;
                long maxDate=0;
				for(V3xAgent agent : list){
				    minDate=agent.getStartDate().getTime();
                    maxDate=agent.getEndDate().getTime();
                    
                    boolean flag=true;
                    if(endDateL<=minDate||startDate>=maxDate){
                        flag=false;
                    }
                   //return {0} 在【{1} 至 {2}】委托 {3} 为代理，不能被委托。
                    if(flag){
                        String agentToName = orgManager.getMemberById(agent.getAgentToId()).getName();
                        String agentName = orgManager.getMemberById(agent.getAgentId()).getName();
                        return ResourceBundleUtil.getString(resourceName, "agent.is.agentTo.alert",agentToName,Datetimes.formatDatetimeWithoutSecond(agent.getStartDate()),Datetimes.formatDatetimeWithoutSecond(agent.getEndDate()),agentName);
                    }
				}
			}
            
			//当前登录者设置的代理人被其他人设为了代理--- 一个人可以为多个人的代理人 modify by dongyj
			/* List<V3xAgent> list2 = agentIntercalateDAO.findAvailabilityByAgentId(Long.parseLong(agentId));
             * if(list2 != null && !list2.isEmpty()){
                for (V3xAgent agent : list2) {
                    //开始时间大于当前agent的结束时间 或者 结束时间小于当前agent的开始时间
                    if(beginDate.getTime() > agent.getEndDate().getTime() || endDate.getTime() < agent.getStartDate().getTime()){
                        continue;
                    }
                    //有冲突
                    else{
                        String agentOption = agent.getAgentOption();
                        boolean isDup = false;
                        for (String str : selectedValues) {
                            String appKeyStr = null;
                            switch(Integer.parseInt(str)){
                                case 0 : appKeyStr = String.valueOf(ApplicationCategoryEnum.collaboration.key());
                                    break;
                                case 1 : appKeyStr = String.valueOf(ApplicationCategoryEnum.form.key());
                                    break;
                                case 2 : appKeyStr = String.valueOf(ApplicationCategoryEnum.edoc.key());
                                    break;
                                case 3 : appKeyStr = String.valueOf(ApplicationCategoryEnum.meeting.key());
                                    break;
                            }
                            //当代理id不相同时在进行比较
                            if(agentOption.indexOf(appKeyStr) != -1 && !(agent.getId()+"").equals(currentAgentId)){
                                isDup = true;
                                break;
                            }
                        }
                        if(isDup){ 
                            String appNames = "";
                            String resourceName_Common = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
                            String[] agentOptions = agentOption.split("&");
                            for(int i =0; i<agentOptions.length; i++){
                                appNames += ResourceBundleUtil.getString(resourceName_Common, "application." + agentOptions[i] + ".label");
                                if(i < agentOptions.length-1){
                                    appNames += "、";
                                }
                            }
                            String agentTime = Datetimes.format(agent.getStartDate(),Datetimes.dateStyleWithoutYear) + "~" + Datetimes.format(agent.getEndDate(),Datetimes.dateStyleWithoutYear);
                            String resourceName = "com.seeyon.v3x.agent.resources.i18n.AgentResources";
                            V3xOrgMember member = this.orgManager.getMemberById(Long.parseLong(agentId));
                            return ResourceBundleUtil.getString(resourceName, "agent.is.agentToByOther.alert", member.getName(), agentTime, appNames);
                        }
                    }
                }
            }*/
            
		} catch (NumberFormatException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
		
		List<Integer> appTypeList = new ArrayList<Integer>();
		boolean fromAgentTo = true;
		List<V3xAgent> agentToList = null;
		try {
		    //当前登录者已设置过相同应用给其他代理人
			agentToList = agentIntercalateDAO.findAvailabilityByAgentToId(user.getId());
		} catch (NumberFormatException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
		
		for(int i=0; i<selectedValues.length; i++){
			int selectedvalue = Integer.parseInt(selectedValues[i]);
			int appType = 0;
			switch(selectedvalue){
				case 0 : appType = ApplicationCategoryEnum.collaboration.key();
				break;
				case 1 : appType = ApplicationCategoryEnum.form.key();
				break;
				case 2 : appType = ApplicationCategoryEnum.edoc.key();
				break;
				case 3 : appType = ApplicationCategoryEnum.meeting.key();
				break;
				case 4 : appType = ApplicationCategoryEnum.bulletin.key();
			}
			if(fromAgentTo){
				for(V3xAgent agent : agentToList){
					if((agent.getId()+"").equals(currentAgentId)) continue;
					if(agent.getEndDate() != null){
						String agentOption = agent.getAgentOption();
						String[] agentOptions = agentOption.split("&");
						boolean samenessFlag = false; 
						for(int m=0; m<agentOptions.length; m++){
							if((appType == Integer.parseInt(agentOptions[m]) && appType !=1 && appType !=2) 
								|| (appType == 2 && ((AgentUtil.hasTemplate(agent) || AgentUtil.isAllColl(agent)) && Integer.parseInt(agentOptions[m])==2 ))
								|| (appType == 2 && ((AgentUtil.hasTemplate(agent, templateIds) || AgentUtil.isAllColl(agent)) && Integer.parseInt(agentOptions[m])==1 ))
								|| (appType == 1 && ((AgentUtil.hasFreeColl(agent) || AgentUtil.isAllColl(agent)) && Integer.parseInt(agentOptions[m])==1 ))){
								samenessFlag = true;
								break;
							}
						}
						if(samenessFlag){
							boolean isMatch = (endDate.compareTo(agent.getStartDate())>=0 && endDate.compareTo(agent.getEndDate())<=0) 
							|| (beginDate.compareTo(agent.getStartDate())>=0 && beginDate.compareTo(agent.getEndDate())<=0) 
							|| (beginDate.compareTo(agent.getStartDate())<=0 && endDate.compareTo(agent.getEndDate())>=0);
							if(isMatch){
								if(!appTypeList.contains(appType)){
									appTypeList.add(appType);
								}
							}
						}
					}
				}
			}
			if(fromAgentTo && appTypeList != null && !appTypeList.isEmpty()){
				continue;
			}
			
            List<V3xAgent> agentList = null;
            try {
                agentList = agentIntercalateDAO.findAvailabilityByAgentId(user.getId());
            } catch (NumberFormatException e) {
                log.error("", e);
            } catch (Exception e) {
                log.error("", e);
            }
			for(V3xAgent agent : agentList){
				if((agent.getId()+"").equals(currentAgentId)) continue;
				if(agent.getEndDate() != null){
					String agentOption = agent.getAgentOption();
					String[] agentOptions = agentOption.split("&");
					boolean samenessFlag = false; 
					for(int m=0; m<agentOptions.length; m++){
						if((appType == Integer.parseInt(agentOptions[m]) && appType !=1 && appType !=2) || (appType == 2 && (AgentUtil.hasAllTemplate(agent) || AgentUtil.isAllColl(agent))) || (appType == 1 && (AgentUtil.hasFreeColl(agent) || AgentUtil.isAllColl(agent)))){
							samenessFlag = true;
							break;
						}
					}
					if(samenessFlag){
						boolean isMatch = (endDate.compareTo(agent.getStartDate())>=0 && endDate.compareTo(agent.getEndDate())<=0) 
						|| (beginDate.compareTo(agent.getStartDate())>=0 && beginDate.compareTo(agent.getEndDate())<=0) 
						|| (beginDate.compareTo(agent.getStartDate())<=0 && endDate.compareTo(agent.getEndDate())>=0);
						if(isMatch){
							if(!appTypeList.contains(appType)){
								appTypeList.add(appType);
							}
							fromAgentTo = false;
						}
					}
				}
			}
		}
		if(appTypeList == null || appTypeList.isEmpty())
			return null;
		
		String appNames = "";
		String temp = null;
		resourceName = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
		String agentResourceName = "com.seeyon.v3x.agent.resources.i18n.AgentResources";
		for(int i=0; i<appTypeList.size(); i++){
			if(appTypeList.get(i)==ApplicationCategoryEnum.collaboration.key())
				temp = ResourceBundleUtil.getString(agentResourceName,"freecoll.label");
			else if(appTypeList.get(i)==ApplicationCategoryEnum.form.key())
				temp = ResourceBundleUtil.getString(agentResourceName,"templatecoll.label");
			else if(appTypeList.get(i) == ApplicationCategoryEnum.bulletin.key())
				temp = ResourceBundleUtil.getString(agentResourceName,"audit.label");
			else
				temp = ResourceBundleUtil.getString(resourceName, "application." + appTypeList.get(i) + ".label");
			if(i == (appTypeList.size()-1)){
				appNames += temp;
			}else{
				appNames += temp + "、";
			}
		}
		resourceName = "com.seeyon.v3x.agent.resources.i18n.AgentResources";
		String agentName = "";
		try {
			if(!fromAgentTo){
				V3xOrgMember member = this.orgManager.getMemberById(Long.parseLong(agentId));
				agentName = member.getName();
			}
		} catch (NumberFormatException e) {
			log.error("", e);
		} catch (BusinessException e) {
			log.error("", e);
		}
		if(fromAgentTo){
			return ResourceBundleUtil.getString(agentResourceName, "agentTo.data.invalidation.alert", appNames);
		}else{
			return ResourceBundleUtil.getString(agentResourceName, "agent.data.invalidation.alert", agentName, appNames);
		}
	}
	
	public void sendAgentSettingMessage(V3xAgent agent) throws Exception{
		String appNames = "";
		
		String[] agentOptions = agent.getAgentOption().split("&");
		List<V3xAgentDetail> v3xdetails= agent.getAgentDetails();
		for(int i=0; i<agentOptions.length; i++){
			String temp= getAgentOptionName(agentOptions[i],v3xdetails);
			if(i == (agentOptions.length-1)){
				appNames += temp;
			}else{
				if(!"".equals(temp)){
					appNames += temp + "、";
				}			
			}
		}
		appNames= appNames.endsWith("、")?appNames.substring(0, appNames.length()-1):appNames;
		V3xOrgMember agentToMember = null;
		agentToMember = this.orgManager.getMemberById(agent.getAgentToId());
		Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
		receivers.add(new MessageReceiver(agent.getId(), agent.getAgentId()));
		this.userMessageManager.sendSystemMessage(new MessageContent("agent.setting.msg.remind", agentToMember.getName(), appNames),
				ApplicationCategoryEnum.agent, agent.getAgentToId(), receivers, 1);
	}
	
	/**
	 * 获得代理事项信息
	 * @param agentOption
	 * @param v3xdetails
	 * @return
	 */
	private String getAgentOptionName(String agentOption,List<V3xAgentDetail> v3xdetails) {
		if("".equals(agentOption)) return "";
		String temp="";
		String resourceName = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
		if(agentOption.equals("1") && v3xdetails!= null && v3xdetails.size()>0){//协同细分下:模板和自由协同
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
				temp = ResourceBundleUtil.getString(resourceName, "application.1.label");//協同:包含自由協同和表單模板協同
			}else if( isTemplate ){
				temp = ResourceBundleUtil.getString(resourceName, "application.1.2.label");//表單模板協同
			}else if(isFree){
				temp = ResourceBundleUtil.getString(resourceName, "application.1.1.label");//自由協同
			}
		}else{
			temp = ResourceBundleUtil.getString(resourceName, "application." + agentOption + ".label");
		}
		return temp;
	}

	public void sendDelAgentSettingMessage(V3xAgent agent) throws Exception{
		String appNames = "";
		String[] agentOptions = agent.getAgentOption().split("&");
		List<V3xAgentDetail> v3xdetails= agent.getAgentDetails();
		for(int i=0; i<agentOptions.length; i++){
			String temp= getAgentOptionName(agentOptions[i],v3xdetails);
			if(i == (agentOptions.length-1)){
				appNames += temp;
			}else{
				if(!"".equals(temp)){
					appNames += temp + "、";
				}			
			}
		}
		appNames= appNames.endsWith("、")?appNames.substring(0, appNames.length()-1):appNames;
		V3xOrgMember agentToMember = null;
		agentToMember = this.orgManager.getMemberById(agent.getAgentToId());
		Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
		receivers.add(new MessageReceiver(agent.getId(), agent.getAgentId()));
		this.userMessageManager.sendSystemMessage(new MessageContent("agent.setting.del.msg.remind", agentToMember.getName(), appNames),
				ApplicationCategoryEnum.agent, agent.getAgentToId(), receivers, 1);
	}
	
	public void sendCancelAgentSettingMessage(V3xAgent agent) throws Exception{
		String appNames = "";
		String[] agentOptions = agent.getAgentOption().split("&");
		List<V3xAgentDetail> v3xdetails= agent.getAgentDetails();
		for(int i=0; i<agentOptions.length; i++){
			String temp= getAgentOptionName(agentOptions[i],v3xdetails);
			if(i == (agentOptions.length-1)){
				appNames += temp;
			}else{
				if(!"".equals(temp)){
					appNames += temp + "、";
				}			
			}
		}
        V3xOrgMember agentToMember = null;
        agentToMember = this.orgManager.getMemberById(agent.getAgentToId());
        Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
		receivers.add(new MessageReceiver(agent.getId(), agent.getAgentId()));
		this.userMessageManager.sendSystemMessage(new MessageContent("agent.setting.cancel.msg.remind", agentToMember.getName(), appNames),
				ApplicationCategoryEnum.agent, agent.getAgentToId(), receivers, 1);
	}
	/**
	 * 查询设置人在麽短时间是否设置了某类代理yangzd
	 * @param agentToId
	 * @return
	 * @throws Exception
	 */
	public List<V3xAgent> getIsagentByToId(Long agentToId,Date fromDate,Date endDate,String agentOption) throws Exception
	{
		return agentIntercalateDAO.getIsagentByToId(agentToId, fromDate, endDate, agentOption);
	}
	
	public void cancelUserAgent(Long userId,User opeator){
		List<AgentModel> agentModels = MemberAgentBean.getInstance().getAgentModelList(userId);
		List<AgentModel> agentModelTos = MemberAgentBean.getInstance().getAgentModelToList(userId);
		Set<Long> agentIds = new HashSet<Long>();
		removeUserAgent(userId, agentModels, agentModelTos, agentIds);
		try {
//			List<V3xAgent> agentList = agentIntercalateDAO.findAllAvailability();
//			if(agentList!=null){
//				for (V3xAgent agent : agentList) {
//					if(userId.equals(agent.getAgentId()) || userId.equals(agent.getAgentToId())){
//						agentIds.add(agent.getId());
//					}
//				}
//			}
//			if(CollectionUtils.isNotEmpty(agentIds)){
//				OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
				//去掉我设置的代理，和别人设置让我代理的。
//				MemberAgentBean.getInstance().remove(userId,userId);
//				List<Long> agentIdsSet = new ArrayList<Long>(agentIds);
//				List<V3xAgent> agents = agentIntercalateDAO.listAgents(agentIdsSet);
				List<V3xAgent> agents = new ArrayList<V3xAgent>();
				List<V3xAgent> agents1= agentIntercalateDAO.findAvailabilityByAgentId(userId);
				List<V3xAgent> agents2= agentIntercalateDAO.findAvailabilityByAgentToId(userId);
				if(null!=agents1 && agents1.size()>0){
					agents.addAll(agents1);
				}
				if(null!=agents2 && agents2.size()>0){
					agents.addAll(agents2);
				}
				List<Object> agentObject = new ArrayList<Object>();
				for (V3xAgent agent : agents) {
					agent.setCancelFlag(true);
					agent.setCancelDate(new Timestamp(System.currentTimeMillis()));
					String agentOptionName = AgentUtil.getAgentOptionName(agent);
					if(userId.longValue()== agent.getAgentToId().longValue()){//给代理人发送消息提醒
						sendCancelAgentSettingMessage(agent);
					}
					try {
						appLogManager.insertLog(opeator, AppLogAction.Agent_Delete,opeator.getName(),orgManager.getMemberById(agent.getAgentId()).getName(),agentOptionName);
					} catch (BusinessException e) {
						log.error("取消代理，记录日志", e);
					}
					agentObject.add(agent);
				}
				if(CollectionUtils.isNotEmpty(agentObject)){
					super.updateAll(agentObject);
				}
//			}
		} catch (Exception e) {
			log.error("取消代理异常Exception:"+e.getLocalizedMessage(), e);
		}
	}
	//从内存中去掉
	private void removeUserAgent(Long userId, List<AgentModel> agentModels, List<AgentModel> agentModelTos, Set<Long> agentIds) {
		//别人设置我为代理人。
		if(CollectionUtils.isNotEmpty(agentModels)){
			List<Long> ids= new ArrayList<Long>(agentModels.size());
			for (AgentModel model : agentModels) {
				agentIds.add(model.getId());
				//removeCacheUserAgent(model,userId);
				ids.add(model.getId());
			}
			MemberAgentBean.getInstance().remove(userId, ids, true, true);
			MemberAgentBean.getInstance().notifyUpdateAgentModel(userId);
		}
		
		//我设置别人为我的代理。
		if(CollectionUtils.isNotEmpty(agentModelTos)){
			List<Long> ids= new ArrayList<Long>(agentModelTos.size());
			for (AgentModel model : agentModelTos) {
				agentIds.add(model.getId());
				//removeCacheUserAgent(model,userId);
			}
			MemberAgentBean.getInstance().remove(userId, ids, false, true);
			MemberAgentBean.getInstance().notifyUpdateAgentModelTo(userId);
		}
	}
	//修改 by wusb at 2010-11-30
	private void removeCacheUserAgent(AgentModel model,Long userId){
		List<AgentModel> agentModel = MemberAgentBean.getInstance().getAgentModelList(model.getAgentId());
		if(agentModel!=null){
			List<AgentModel> removeModel = new ArrayList<AgentModel>();
			for (AgentModel mod : agentModel) {
				if(mod.getAgentId().equals(userId) || mod.getAgentToId().equals(userId)){
					removeModel.add(mod);
				}
			}
			if(CollectionUtils.isNotEmpty(removeModel)){
				agentModel.removeAll(removeModel);
				MemberAgentBean.getInstance().notifyUpdateAgentModel(model.getAgentId());
			}
		}
		
		List<AgentModel> agenttoModel = MemberAgentBean.getInstance().getAgentModelToList(model.getAgentToId());
		if(agenttoModel!=null){
			List<AgentModel> removeModel = new ArrayList<AgentModel>();
			for (AgentModel mod : agenttoModel) {
				if(mod.getAgentId().equals(userId) || mod.getAgentToId().equals(userId)){
					removeModel.add(mod);
				}
			}
			if(CollectionUtils.isNotEmpty(removeModel)){
				agenttoModel.removeAll(removeModel);
				MemberAgentBean.getInstance().notifyUpdateAgentModelTo(model.getAgentToId());
			}
		}
	}
	
	private AppLogManager appLogManager;

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
	/**
	 * 保存代理及代理明细
	 * @param agent
	 * @param detail
	 */
	public void save(V3xAgent agent,List<V3xAgentDetail> detail){
		this.save(agent);
		if(detail != null){
			this.savePatchAll(detail);
		}
	}
	
	/**
	 * 更新代理及明细
	 * @param agent
	 * @param details
	 */
	public void update(V3xAgent agent,List<V3xAgentDetail> details){
		this.update(agent);
		Object[][] p = {{"agentId",agent.getId()}};
		this.delete(V3xAgentDetail.class, p);
		if(details != null){
			this.savePatchAll(details);
		}
	}
	
	/**
	 * 通过agentId，取得detail列表
	 * @param agentId
	 * @return
	 */
	public List<V3xAgentDetailModel> getDetailModelByAgentId(Long agentId){
		List<Object[]> list = super.find("select d.entityId,t.subject from Templete t,V3xAgentDetail d where t.id=d.entityId and d.agentId=?", -1, -1, null, agentId);
		List<V3xAgentDetailModel> models = null;
		if(list != null && list.size()>0){
			models = new ArrayList<V3xAgentDetailModel>();
			for(Object[] detail:list){
				V3xAgentDetailModel model = new V3xAgentDetailModel();
				model.setEntityId((Long)detail[0]);
				model.setEntityName((String)detail[1]);
				models.add(model);
			}
		}
		//TODO 无法使用左连接，只能再查询一次
		List<Long> entityIds = super.find("select d.entityId from V3xAgentDetail d where (d.entityId=1 or d.entityId=2) and  d.agentId=?", -1, -1, null, agentId);
		if(entityIds != null && entityIds.size()>0){
			if(models == null)
				models = new ArrayList<V3xAgentDetailModel>();
			for(Long detail:entityIds){
				V3xAgentDetailModel model = new V3xAgentDetailModel();
				model.setEntityId(detail);
				models.add(model);
			}
		}
		return models;
	}
	
	public Map<Long,List<V3xAgentDetailModel>> getDetailModelByAgents(List<Long> agentIds){
		if(agentIds == null || agentIds.size()==0)
			return null;
		Map<String,Object> p = new HashMap<String,Object>();
		p.put("agentIds", agentIds);
		List<V3xAgentDetail> list = super.find("from V3xAgentDetail d where d.agentId in (:agentIds)", -1, -1, p);
		if(list != null){
			Map<Long,List<V3xAgentDetailModel>> result = new HashMap<Long,List<V3xAgentDetailModel>>();
			List<V3xAgentDetailModel> models = null;
			V3xAgentDetailModel model = null;
			for(V3xAgentDetail d:list){
				models = result.get(d.getAgentId());
				if(models==null){
					models = new ArrayList<V3xAgentDetailModel>();
					result.put(d.getAgentId(), models);
				}
				model = new V3xAgentDetailModel();
				model.setId(d.getId());
				model.setAgentId(d.getAgentId());
				model.setApp(d.getApp());
				model.setEntityId(d.getEntityId());
				models.add(model);
			}
			return result;
		}
		return null;
	}
	
	public List<V3xAgent> getAgentByMemberId(long memberId) {
		Map<String,Object> param = new HashMap<String,Object>();
		String hql = "from V3xAgent a where a.agentId =:agentId and a.cancelFlag=:cancelFlag and a.agentRemind=:agentRemind";
		param.put("agentId", memberId);
		param.put("cancelFlag", false);
		param.put("agentRemind", true);
		List<V3xAgent> agentList = super.find(hql, -1, -1, param);
		return agentList;
	}
	
	public List<V3xAgent> getToAgentByMemberId(long memberId) {
		Map<String,Object> param = new HashMap<String,Object>();
		String hql = "from V3xAgent a where a.agentToId =:agentToId and a.cancelFlag=:cancelFlag and a.agentToRemind=:agentToRemind";
		param.put("agentToId", memberId);
		param.put("cancelFlag", false);
		param.put("agentToRemind", true);
		List<V3xAgent> agentList = super.find(hql, -1, -1, param);
		return agentList;
	}
	
	public void updateIsAgentRemind(Long id, boolean isRemind, Long currentUserId) {
		Map<String,Object> param = new HashMap<String,Object>();
		String hql = "";
		V3xAgent agent = super.get(id);
		// 当前用户是代理人
		if (agent.getAgentId().toString().equals(currentUserId.toString())) {
			hql = "update V3xAgent set agentRemind=:isRemind where id=:id";
		}
		// 当前用户是被代理人
		else if(agent.getAgentToId().toString().equals(currentUserId.toString())) {
			hql = "update V3xAgent set agentToRemind=:isRemind where id=:id";
		}
		param.put("isRemind", isRemind);
		param.put("id", id);
		super.bulkUpdate(hql, param);
		
	}
}
