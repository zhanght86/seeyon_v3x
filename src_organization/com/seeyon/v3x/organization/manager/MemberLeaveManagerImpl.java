/**
 * Id: MemberLeaveHelper.java, v1.0 2011-12-1 wangchw wangchw Exp
 * Copyright (c) 2011 Seeyon, Ltd. All rights reserved
 */
package com.seeyon.v3x.organization.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.domain.FormAppMain;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;

import com.seeyon.v3x.agent.domain.V3xAgent;
import com.seeyon.v3x.agent.domain.V3xAgentDetail;
import com.seeyon.v3x.agent.manager.AgentIntercalateManager;
import com.seeyon.v3x.agent.manager.AgentUtil;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.authenticate.domain.V3xAgentDetailModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.Constants.LoginOfflineOperation;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.inquiry.domain.InquirySurveytypeextend;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.office.admin.domain.MAdminSetting;
import com.seeyon.v3x.office.admin.manager.AdminManager;
import com.seeyon.v3x.office.asset.manager.AssetManager;
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.book.manager.BookManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRelationship;
import com.seeyon.v3x.organization.event.OrganizationEventComposite;
import com.seeyon.v3x.organization.event.OrganizationEventListener;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.space.manager.SpaceManager;

/**
 * @Project/Product: A8（A8）
 * @Description: 人员离职管理
 * @Copyright: Copyright (c) 2011 of Seeyon, Ltd.
 * @author: wangchw
 * @time: 2011-12-1 下午06:01:53
 * @version: v1.0
 */
public class MemberLeaveManagerImpl implements MemberLeaveManager {
	/**
	 * 表单操作接口
	 */
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	/**
	 * 代理
	 */
	private AgentIntercalateManager agentIntercalateManager;
	/**
	 * 应用日志
	 */
	private AppLogManager appLogManager;
	/**
	 * 组织模型
	 */
	private OrgManager orgManager;
	/**
	 * 项目管理
	 */
	private ProjectManager projectManager;
	
	/**
	 * 空间操作接口
	 */
	private SpaceManager spaceManager;
	/**
	 * 公告操作接口
	 */
	private BulTypeManager bulTypeManager;
	/**
	 * 调查操作接口
	 */
	private InquiryManager inquiryManager;
	/**
	 * bbs讨论操作接口
	 */
	private BbsBoardManager bbsBoardManager;
	/**
	 * 新闻操作接口
	 */
	private NewsTypeManager newsTypeManager;
	/**
	 * 综合办公管理操作接口
	 */
	private AdminManager officeAdminManager;
	/**
	 * 车辆管理类
	 */
	private AutoManager autoManager;
	/**
	 * 办公设备管理类
	 */
	private AssetManager assetManager;
	/**
	 * 图书资料管理类
	 */
	private BookManager bookManager;
	/**
	 * 用户消息提醒接口
	 */
	private UserMessageManager userMessageManager;
	
	/**
	 * 组织机构事件
	 */
	private OrganizationEventListener eventListener =  OrganizationEventComposite.getInstance();

	/**
     * 
     * @param agent_to_id
     * @param agent
     * @param leave9UserId
     * @throws NumberFormatException
     * @throws Exception
     */
    public void handleOldProxyInfo9(String agent_to_id, V3xAgent agent,
			String leave9UserId) throws NumberFormatException, Exception {
    	List<V3xAgent> oldList= agentIntercalateManager.queryAvailabilityList1(Long.parseLong(agent_to_id));
		for (Iterator iterator = oldList.iterator(); iterator.hasNext();) {
			V3xAgent v3xAgent = (V3xAgent) iterator.next();
			String optionStr= v3xAgent.getAgentOption();
			String[] options= optionStr.split("&");
			boolean flag= false;
			boolean oldflag= false;
			String optionTempStr="";
			String equalOpionStr="";
			for (int i = 0; i < options.length; i++) {
				if(options[i].equals("7") || options[i].equals("10") || options[i].equals("8")){
					flag= true;
					equalOpionStr += options[i].trim()+"&";
				}else if(!"".equals(options[i].trim())){//还代理了其他事项
					oldflag= true;
					optionTempStr += options[i].trim()+"&";
				}
			}
			if(flag){//代理别人的协同，需要转给新的代理人
				V3xAgent agentProxy = new V3xAgent();
				agentProxy.setIdIfNew();
				agentProxy.setAgentId(Long.parseLong(leave9UserId));
				agentProxy.setAgentToId(v3xAgent.getAgentToId());
				agentProxy.setCreateDate(v3xAgent.getCreateDate());
				agentProxy.setStartDate(v3xAgent.getStartDate());
				agentProxy.setEndDate(v3xAgent.getEndDate());
				agentProxy.setAgentOption(StringUtils.removeEnd(equalOpionStr,"&"));
				agentProxy.setCancelFlag(false);
				//保存代理关系
	    		agentIntercalateManager.save(agentProxy,null);
    			if(oldflag){//还有其它代理事项
    				v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
    				List<V3xAgentDetail> agentDetailsTemp=  v3xAgent.getAgentDetails();
    				List<V3xAgentDetail> agentDetailsTemp1= null;
    				if(null!=agentDetailsTemp && agentDetailsTemp.size()>0){
    					agentDetailsTemp1= new ArrayList<V3xAgentDetail>();
    					for (Iterator iterator2 = agentDetailsTemp.iterator(); iterator2.hasNext();) {
    						V3xAgentDetail v3xAgentDetail = (V3xAgentDetail) iterator2.next();
    						V3xAgentDetail v3xAgentDetailTemp= new V3xAgentDetail();
    						v3xAgentDetailTemp.setAgentId(v3xAgentDetail.getAgentId());
    						v3xAgentDetailTemp.setApp(v3xAgentDetail.getApp());
    						v3xAgentDetailTemp.setEntityId(v3xAgentDetail.getEntityId());
    						v3xAgentDetailTemp.setIdIfNew();
    						agentDetailsTemp1.add(v3xAgentDetailTemp);
    					}
    				}
    				agentIntercalateManager.update(v3xAgent, agentDetailsTemp1);
    			}else{//否则取消代理
    				v3xAgent.setCancelFlag(true);
    				agentIntercalateManager.update(v3xAgent, null);
    			}
			}
		}
	}

	/**
     * 
     * @param leave9UserId
     * @param agent_to_id
     * @param distance
     * @param user
     * @return
     * @throws Exception
     */
    public V3xAgent handleCurrentAgentInfo9(String leave9UserId,String agent_to_id, Timestamp startTime,Timestamp endTime,User user) throws Exception {
    	V3xAgent agent = new V3xAgent();
		agent.setIdIfNew();
		agent.setAgentId(Long.parseLong(leave9UserId));
		agent.setAgentToId(Long.parseLong(agent_to_id));
		agent.setCreateDate(new Timestamp(System.currentTimeMillis()));
		agent.setStartDate(startTime);
		agent.setEndDate(endTime);
		agent.setAgentOption(ApplicationCategoryEnum.bulletin.getKey() + "&" + ApplicationCategoryEnum.inquiry.getKey() + "&" + ApplicationCategoryEnum.news.getKey()+"&");
		agent.setCancelFlag(false);
		agent.setAgentRemind(true);
		agent.setAgentToRemind(true);
		//保存代理关系
		agentIntercalateManager.save(agent,null);
		addAgentCache(agent, null);
		//给代理人发送消息提醒
		sendAgentSettingMessageForMemberLeave(agent,new String[]{"7","10","8"});
		String agentOptionName = AgentUtil.getAgentOptionName(agent);
		//保存应用日志
		appLogManager.insertLog(user, AppLogAction.Agent_New_LeaveMember,user.getName(),orgManager.getMemberById(Long.parseLong(leave9UserId)).getName(),agentOptionName);
		return agent;
	}

    /**
     * 发送消息
     * @param agent
     */
	private void sendAgentSettingMessageForMemberLeave(V3xAgent agent,String[] agentOptions) throws Exception {
		String appNames = "";
		String resourceName = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
		for(int i=0; i<agentOptions.length; i++){
			if(i == (agentOptions.length-1)){
				appNames += ResourceBundleUtil.getString(resourceName, "application." + agentOptions[i] + ".label");
			}else{
				appNames += ResourceBundleUtil.getString(resourceName, "application." + agentOptions[i] + ".label") + "、";
			}
		}
		V3xOrgMember agentToMember = null;
		agentToMember = this.orgManager.getMemberById(agent.getAgentToId());
		Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
		receivers.add(new MessageReceiver(agent.getId(), agent.getAgentId()));
		this.userMessageManager.sendSystemMessage(new MessageContent("agent.setting.msg.remind.from.memberleave", agentToMember.getName(), appNames),
				ApplicationCategoryEnum.agent, agent.getAgentToId(), receivers, 1);
	}

	/**
     * 
     * @param agent_to_id
     * @param agent
     * @param leave4UserId
     * @throws Exception 
     * @throws NumberFormatException 
     */
    public void handleOldProxyInfo4(String agent_to_id, V3xAgent agent,
			String leave4UserId) throws NumberFormatException, Exception {
    	List<V3xAgent> oldList= agentIntercalateManager.queryAvailabilityList1(Long.parseLong(agent_to_id));
		for (Iterator iterator = oldList.iterator(); iterator.hasNext();) {
			V3xAgent v3xAgent = (V3xAgent) iterator.next();
			String optionStr= v3xAgent.getAgentOption();
			String[] options= optionStr.split("&");
			boolean flag= false;
			boolean oldflag= false;
			String optionTempStr="";
			for (int i = 0; i < options.length; i++) {
				if(options[i].equals("4")){
					flag= true;
				}else if(!"".equals(options[i].trim())){//还代理了其他事项
					oldflag= true;
					optionTempStr += options[i].trim()+"&";
				}
			}
			if(flag){//代理别人的协同，需要转给新的代理人
				V3xAgent agentProxy = new V3xAgent();
				agentProxy.setIdIfNew();
				agentProxy.setAgentId(Long.parseLong(leave4UserId));
				agentProxy.setAgentToId(v3xAgent.getAgentToId());
				agentProxy.setCreateDate(v3xAgent.getCreateDate());
				agentProxy.setStartDate(v3xAgent.getStartDate());
				agentProxy.setEndDate(v3xAgent.getEndDate());
				agentProxy.setAgentOption(ApplicationCategoryEnum.edoc.key()+"&");
				agentProxy.setCancelFlag(false);
				//保存代理关系
	    		agentIntercalateManager.save(agentProxy,null);
    			if(oldflag){//还有其它代理事项
    				v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
    				List<V3xAgentDetail> agentDetailsTemp= v3xAgent.getAgentDetails();
    				List<V3xAgentDetail> agentDetailsTemp1= null;
    				if(null!=agentDetailsTemp && agentDetailsTemp.size()>0){
    					agentDetailsTemp1= new ArrayList<V3xAgentDetail>();
    					for (Iterator iterator2 = agentDetailsTemp.iterator(); iterator2.hasNext();) {
    						V3xAgentDetail v3xAgentDetail = (V3xAgentDetail) iterator2.next();
    						V3xAgentDetail v3xAgentDetailTemp= new V3xAgentDetail();
    						v3xAgentDetailTemp.setAgentId(v3xAgentDetail.getAgentId());
    						v3xAgentDetailTemp.setApp(v3xAgentDetail.getApp());
    						v3xAgentDetailTemp.setEntityId(v3xAgentDetail.getEntityId());
    						v3xAgentDetailTemp.setIdIfNew();
    						agentDetailsTemp1.add(v3xAgentDetailTemp);
    					}
    				}
    				agentIntercalateManager.update(v3xAgent, agentDetailsTemp1);
    			}else{//否则取消代理
    				v3xAgent.setCancelFlag(true);
    				agentIntercalateManager.update(v3xAgent, null);
    			}
			}
		}
	}

	/**
     * 
     * @param leave4UserId
     * @param agent_to_id
     * @param distance
     * @param user
     * @return
     * @throws Exception
     */
    public V3xAgent handleCurrentAgentInfo4(String leave4UserId,
			String agent_to_id,  Timestamp startTime,Timestamp endTime, User user) throws Exception {
    	V3xAgent agent = new V3xAgent();
		agent.setIdIfNew();
		agent.setAgentId(Long.parseLong(leave4UserId));
		agent.setAgentToId(Long.parseLong(agent_to_id));
		agent.setCreateDate(new Timestamp(System.currentTimeMillis()));
		agent.setStartDate(startTime);
		agent.setEndDate(endTime);
		agent.setAgentOption(ApplicationCategoryEnum.edoc.key()+"&");
		agent.setCancelFlag(false);
		agent.setAgentRemind(true);
		agent.setAgentToRemind(true);
		//保存代理关系
		agentIntercalateManager.save(agent,null);
		addAgentCache(agent, null);
		//给代理人发送消息提醒
//		agentIntercalateManager.sendAgentSettingMessage(agent);
		sendAgentSettingMessageForMemberLeave(agent, new String[]{"4"});
		String agentOptionName = AgentUtil.getAgentOptionName(agent);
		//保存应用日志
		appLogManager.insertLog(user, AppLogAction.Agent_New_LeaveMember,user.getName(),orgManager.getMemberById(Long.parseLong(leave4UserId)).getName(),agentOptionName);
		return agent;
	}

	/**
     * 
     * @param agent_to_id
     * @param agent
     * @param leave2UserId
     * @throws NumberFormatException
     * @throws Exception
     */
    public void handleOldProxyInfo2(String agent_to_id, V3xAgent agent,
			String leave2UserId) throws NumberFormatException, Exception {
    	List<V3xAgent> oldList= agentIntercalateManager.queryAvailabilityList1(Long.parseLong(agent_to_id));
		for (Iterator iterator = oldList.iterator(); iterator.hasNext();) {
			V3xAgent v3xAgent = (V3xAgent) iterator.next();
			String optionStr= v3xAgent.getAgentOption();
			String[] options= optionStr.split("&");
			boolean flag= false;
			boolean oldflag= false;
			String optionTempStr="";
			for (int i = 0; i < options.length; i++) {
				if(options[i].equals("1")){
					flag= true;
				}else if(!"".equals(options[i].trim())){//还代理了其他事项
					oldflag= true;
					optionTempStr += options[i].trim()+"&";
				}
			}
			V3xAgentDetail detailTemp = null;
			List<V3xAgentDetail> detailsTemp = new ArrayList<V3xAgentDetail>();
			if(flag){//代理别人的协同，需要转给新的代理人
				boolean mflag= false;
				List<V3xAgentDetail> v3xdetails= v3xAgent.getAgentDetails();
				List<V3xAgentDetail> v3xdetailsTemp= new ArrayList<V3xAgentDetail>();
				if(v3xdetails!= null && v3xdetails.size()>0){//有明细
					V3xAgent agentProxy = new V3xAgent();
					agentProxy.setIdIfNew();
					agentProxy.setAgentId(Long.parseLong(leave2UserId));
					agentProxy.setAgentToId(v3xAgent.getAgentToId());
					agentProxy.setCreateDate(v3xAgent.getCreateDate());
					agentProxy.setStartDate(v3xAgent.getStartDate());
					agentProxy.setEndDate(v3xAgent.getEndDate());
					agentProxy.setAgentOption(ApplicationCategoryEnum.collaboration.key()+"");//模板协同也是协同
					agentProxy.setCancelFlag(false);
					for (Iterator iterator2 = v3xdetails.iterator(); iterator2.hasNext();) {
						V3xAgentDetail v3xAgentDetail = (V3xAgentDetail) iterator2.next();
						if(v3xAgentDetail.getEntityId()==1){//代理了自由协同
							detailTemp = new V3xAgentDetail();
							detailTemp.setIdIfNew();
							detailTemp.setAgentId(agentProxy.getId());
							detailTemp.setApp(ApplicationCategoryEnum.collaboration.getKey());
							detailTemp.setEntityId(Long.parseLong("1"));//代理了自由协同
							detailsTemp.add(detailTemp);
							mflag= true;
						}else{
							detailTemp = new V3xAgentDetail();
							detailTemp.setIdIfNew();
							detailTemp.setAgentId(v3xAgent.getId());
							detailTemp.setApp(ApplicationCategoryEnum.collaboration.getKey());
							detailTemp.setEntityId(v3xAgentDetail.getEntityId());
							v3xdetailsTemp.add(detailTemp);
						}
					}
					if(mflag){//代理了自由协同
						//保存代理关系
			    		agentIntercalateManager.save(agentProxy,detailsTemp);
			    		
			    		//修改之前的代理关系
			    		if(v3xdetailsTemp.size()==0){
			    			if(oldflag){//还有其它代理事项
			    				v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
			    				agentIntercalateManager.update(v3xAgent, null);
			    			}else{//否则取消代理
			    				v3xAgent.setCancelFlag(true);
			    				agentIntercalateManager.update(v3xAgent, null);
			    			}
			    		}else{//还有其他代理明细，主要是自由协同
			    			v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
		    				agentIntercalateManager.update(v3xAgent, v3xdetailsTemp);
			    		}
					}
				}else{//代理了所有协同，这里只代理自由协同部分，模板流程留给指定的人代理
					V3xAgent agentProxy = new V3xAgent();
					agentProxy.setIdIfNew();
					agentProxy.setAgentId(Long.parseLong(leave2UserId));
					agentProxy.setAgentToId(v3xAgent.getAgentToId());
					agentProxy.setCreateDate(v3xAgent.getCreateDate());
					agentProxy.setStartDate(v3xAgent.getStartDate());
					agentProxy.setEndDate(v3xAgent.getEndDate());
					agentProxy.setAgentOption(ApplicationCategoryEnum.collaboration.key()+"");//模板协同也是协同
					agentProxy.setCancelFlag(false);
					
					detailTemp = new V3xAgentDetail();
					detailTemp.setIdIfNew();
					detailTemp.setAgentId(agentProxy.getId());
					detailTemp.setApp(ApplicationCategoryEnum.collaboration.getKey());
					detailTemp.setEntityId(Long.parseLong("1"));//代理了自由协同
					detailsTemp.add(detailTemp);
					
					//保存代理关系
		    		agentIntercalateManager.save(agentProxy,detailsTemp);
		    		
		    		//将模板协同保留下来
		    		V3xAgentDetail detailTemp1 = new V3xAgentDetail();
		    		List<V3xAgentDetail> detailsTemp1 = new ArrayList<V3xAgentDetail>();
		    		detailTemp1.setIdIfNew();
		    		detailTemp1.setAgentId(v3xAgent.getId());
		    		detailTemp1.setApp(ApplicationCategoryEnum.collaboration.getKey());
		    		detailTemp1.setEntityId(Long.parseLong("2"));//代理了自由协同
		    		detailsTemp1.add(detailTemp1);
		    		agentIntercalateManager.update(v3xAgent, detailsTemp1);	
				}
			}
		}
	}

	/**
     * 
     * @param leave2UserId
     * @param agent_to_id
     * @param distance
     * @param user
     * @return
     * @throws Exception
     */
    public V3xAgent handleCurrentAgentInfo2(String leave2UserId,
			String agent_to_id, Timestamp startTime,Timestamp endTime,  User user) throws Exception {
    	V3xAgent agent = new V3xAgent();
		agent.setIdIfNew();
		agent.setAgentId(Long.parseLong(leave2UserId));
		agent.setAgentToId(Long.parseLong(agent_to_id));
		agent.setCreateDate(new Timestamp(System.currentTimeMillis()));
		agent.setStartDate(startTime);
		agent.setEndDate(endTime);
		agent.setAgentOption(ApplicationCategoryEnum.collaboration.key()+"");//模板协同也是协同
		agent.setCancelFlag(false);
		agent.setAgentRemind(true);
		agent.setAgentToRemind(true);
		
		List<V3xAgentDetail> details = new ArrayList<V3xAgentDetail>();
		List<V3xAgentDetailModel> models = new ArrayList<V3xAgentDetailModel>();
		V3xAgentDetail detail = new V3xAgentDetail();
		detail.setIdIfNew();
		detail.setAgentId(agent.getId());
		detail.setApp(ApplicationCategoryEnum.collaboration.getKey());
		detail.setEntityId(Long.parseLong("1"));//自由协同为1
		details.add(detail);
		V3xAgentDetailModel model = new V3xAgentDetailModel();
		model.setId(detail.getId());
		model.setAgentId(detail.getAgentId());
		model.setApp(detail.getApp());
		model.setEntityId(detail.getEntityId());
		models.add(model);
		//保存代理关系
		agentIntercalateManager.save(agent,details);
		addAgentCache(agent, models);
		//给代理人发送消息提醒
//		agentIntercalateManager.sendAgentSettingMessage(agent);
		agent.setAgentDetails(details);
		sendAgentSettingMessageForMemberLeave(agent, new String[]{"1.1"});
		String agentOptionName = AgentUtil.getAgentOptionName(agent);
		//保存应用日志
		appLogManager.insertLog(user, AppLogAction.Agent_New_LeaveMember,user.getName(),orgManager.getMemberById(Long.parseLong(leave2UserId)).getName(),agentOptionName);
		return agent;
	}

	/**
     * 
     * @param leave1UserId
     * @param agent_to_id
     * @param distance
     * @param user
     * @return
     * @throws Exception 
     */
    public V3xAgent handleCurrentAgentInfo1(String leave1UserId,String agent_to_id,Timestamp startTime,Timestamp endTime,User user) throws Exception {
    	V3xAgent agent = new V3xAgent();
		agent.setIdIfNew();
		agent.setAgentId(Long.parseLong(leave1UserId));
		agent.setAgentToId(Long.parseLong(agent_to_id));
		agent.setCreateDate(new Timestamp(System.currentTimeMillis()));
		agent.setStartDate(startTime);
		agent.setEndDate(endTime);
		agent.setAgentOption(ApplicationCategoryEnum.collaboration.key()+"");//模板协同也是协同
		agent.setCancelFlag(false);
		agent.setAgentRemind(true);
		agent.setAgentToRemind(true);
		
		List<V3xAgentDetail> details = new ArrayList<V3xAgentDetail>();
		List<V3xAgentDetailModel> models = new ArrayList<V3xAgentDetailModel>();
		
		V3xAgentDetail detail = new V3xAgentDetail();
		detail.setIdIfNew();
		detail.setAgentId(agent.getId());
		detail.setApp(ApplicationCategoryEnum.collaboration.getKey());
		detail.setEntityId(Long.parseLong("2"));//模板协同为2
		details.add(detail);
		
		V3xAgentDetailModel model = new V3xAgentDetailModel();
		model.setId(detail.getId());
		model.setAgentId(detail.getAgentId());
		model.setApp(detail.getApp());
		model.setEntityId(detail.getEntityId());
		models.add(model);
		//保存代理关系
		agentIntercalateManager.save(agent,details);
		addAgentCache(agent, models);
		//给代理人发送消息提醒
//		agentIntercalateManager.sendAgentSettingMessage(agent);
		agent.setAgentDetails(details);
		sendAgentSettingMessageForMemberLeave(agent,new String[]{"1.2"});
		String agentOptionName = AgentUtil.getAgentOptionName(agent);
		//保存应用日志
		appLogManager.insertLog(user, AppLogAction.Agent_New_LeaveMember,user.getName(),orgManager.getMemberById(Long.parseLong(leave1UserId)).getName(),agentOptionName);
		
		return agent;
	}

	/**
     * 继续代理模板
     * @param agent_to_id
     * @param agent
     * @param leave1UserId
     * @throws NumberFormatException
     * @throws Exception
     */
    public void handleOldProxyInfo1(String agent_to_id,V3xAgent agent,String leave1UserId) throws NumberFormatException, Exception {
		List<V3xAgent> oldList= agentIntercalateManager.queryAvailabilityList1(Long.parseLong(agent_to_id));
		for (Iterator iterator = oldList.iterator(); iterator.hasNext();) {
			V3xAgent v3xAgent = (V3xAgent) iterator.next();
			String optionStr= v3xAgent.getAgentOption();
			String[] options= optionStr.split("&");
			boolean flag= false;
			boolean oldflag= false;
			String optionTempStr="";
			for (int i = 0; i < options.length; i++) {
				if(options[i].equals("1")){
					flag= true;
				}else if(!"".equals(options[i].trim())){//还代理了其他事项
					oldflag= true;
					optionTempStr += options[i].trim()+"&";
				}
			}
			V3xAgentDetail detailTemp = null;
			List<V3xAgentDetail> detailsTemp = new ArrayList<V3xAgentDetail>();
			if(flag){//代理别人的协同，需要转给新的代理人
				boolean mflag= false;
				List<V3xAgentDetail> v3xdetails= v3xAgent.getAgentDetails();
				List<V3xAgentDetail> v3xdetailsTemp= v3xAgent.getAgentDetails();
				if(v3xdetails!= null && v3xdetails.size()>0){//有明细
					V3xAgent agentProxy = new V3xAgent();
					agentProxy.setIdIfNew();
					agentProxy.setAgentId(Long.parseLong(leave1UserId));
					agentProxy.setAgentToId(v3xAgent.getAgentToId());
					agentProxy.setCreateDate(v3xAgent.getCreateDate());
					agentProxy.setStartDate(v3xAgent.getStartDate());
					agentProxy.setEndDate(v3xAgent.getEndDate());
					agentProxy.setAgentOption(ApplicationCategoryEnum.collaboration.key()+"");//模板协同也是协同
					agentProxy.setCancelFlag(false);
					for (Iterator iterator2 = v3xdetails.iterator(); iterator2.hasNext();) {
						V3xAgentDetail v3xAgentDetail = (V3xAgentDetail) iterator2.next();
						if(v3xAgentDetail.getEntityId()==2){//代理了全部模板
							detailTemp = new V3xAgentDetail();
							detailTemp.setIdIfNew();
							detailTemp.setAgentId(agentProxy.getId());
							detailTemp.setApp(ApplicationCategoryEnum.collaboration.getKey());
							detailTemp.setEntityId(Long.parseLong("2"));//代理了全部模板
							detailsTemp.add(detailTemp);
							mflag= true;
						}else if(v3xAgentDetail.getEntityId()!=1){//代理了具体模板，有模板编号
							detailTemp = new V3xAgentDetail();
							detailTemp.setIdIfNew();
							detailTemp.setAgentId(agentProxy.getId());
							detailTemp.setApp(ApplicationCategoryEnum.collaboration.getKey());
							detailTemp.setEntityId(v3xAgentDetail.getEntityId());
							detailsTemp.add(detailTemp);
							mflag= true;
						}else{//自由协同
							detailTemp = new V3xAgentDetail();
							detailTemp.setIdIfNew();
							detailTemp.setAgentId(v3xAgent.getId());
							detailTemp.setApp(ApplicationCategoryEnum.collaboration.getKey());
							detailTemp.setEntityId(v3xAgentDetail.getEntityId());
							v3xdetailsTemp.add(detailTemp);
						}
					}
					if(mflag){//代理了模板流程
						//保存代理关系
			    		agentIntercalateManager.save(agentProxy,detailsTemp);
			    		
			    		//修改之前的代理关系
			    		if(v3xdetailsTemp.size()==0){
			    			if(oldflag){//还有其它代理事项
			    				v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
			    				agentIntercalateManager.update(v3xAgent, null);
			    			}else{//否则取消代理
			    				v3xAgent.setCancelFlag(true);
			    				agentIntercalateManager.update(v3xAgent, null);
			    			}
			    		}else{//还有其他代理明细，主要是自由协同
			    			v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
		    				agentIntercalateManager.update(v3xAgent, v3xdetailsTemp);
			    		}
					}
				}else{//全部协同，则只代理模板部分，自由协同部分留给指定的人代理
					V3xAgent agentProxy = new V3xAgent();
					agentProxy.setIdIfNew();
					agentProxy.setAgentId(Long.parseLong(leave1UserId));
					agentProxy.setAgentToId(v3xAgent.getAgentToId());
					agentProxy.setCreateDate(v3xAgent.getCreateDate());
					agentProxy.setStartDate(v3xAgent.getStartDate());
					agentProxy.setEndDate(v3xAgent.getEndDate());
					agentProxy.setAgentOption(ApplicationCategoryEnum.collaboration.key()+"");//模板协同也是协同
					agentProxy.setCancelFlag(false);
					
					detailTemp = new V3xAgentDetail();
					detailTemp.setIdIfNew();
					detailTemp.setAgentId(agentProxy.getId());
					detailTemp.setApp(ApplicationCategoryEnum.collaboration.getKey());
					detailTemp.setEntityId(Long.parseLong("2"));//代理了全部模板
					detailsTemp.add(detailTemp);
					
					//保存代理关系
		    		agentIntercalateManager.save(agentProxy,detailsTemp);
		    		
		    		//把以前的改成只代理自由协同部分
		    		V3xAgentDetail detailTemp1 = new V3xAgentDetail();
		    		List<V3xAgentDetail> detailsTemp1 = new ArrayList<V3xAgentDetail>();
		    		detailTemp1.setIdIfNew();
		    		detailTemp1.setAgentId(v3xAgent.getId());
		    		detailTemp1.setApp(ApplicationCategoryEnum.collaboration.getKey());
		    		detailTemp1.setEntityId(Long.parseLong("1"));//代理了全部模板
					detailsTemp1.add(detailTemp1);
		    		agentIntercalateManager.update(v3xAgent, detailsTemp1);
				}
				
			}
		}
	}

    /**
     * 改变离职用户的状态信息：将离职用户踢下线和将用户状态信息设置为离职状态
     * @param agent_to_id
     * @throws Exception 
     */
	public void changeUserSate(String agent_to_id,User user) throws Exception {
		V3xOrgMember member= orgManager.getMemberById(Long.parseLong(agent_to_id));
		//为事件调用记录修改前的人员
		V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
		BeanUtils.copyProperties(memberBeforeUpdate, member);
		//踢用户下线
		OnlineRecorder.moveToOffline(member.getLoginName(), LoginOfflineOperation.adminKickoff);
    	//agentIntercalateManager.cancelUserAgent(member.getId(),user);
		//修改AEIGHT-1597人员修改，离职后修改其登录名
		String _tempLoginName = member.getLoginName();
		if(StringUtils.isNotBlank(_tempLoginName)
				&& null!=_tempLoginName
				&& !_tempLoginName.contains("_had_left")) {//已经离职人员可以再次点击离职，避免出现重复增加人名后缀增加
			_tempLoginName += "_had_left";
		}
		member.setLoginName(_tempLoginName);
		//将用户设置为离职状态
    	member.setEnabled(false);
    	member.setState(new Byte("2"));
    	orgManager.updateMember(member);
    	List<V3xAgent> list1= agentIntercalateManager.queryAvailabilityList1(member.getId());
    	for (V3xAgent v3xAgent : list1) {
    		v3xAgent.setCancelFlag(true);
    		v3xAgent.setCancelDate(new Timestamp(System.currentTimeMillis()));
			agentIntercalateManager.update(v3xAgent,null);
			deleteAgentCache(v3xAgent);
		}
    	//更新人员事件
    	if(memberBeforeUpdate.getEnabled()){
    		eventListener.updateMember(memberBeforeUpdate, member);
    	}
    	
	}

	/**
	 * 获得离职人员的角色列表
	 * @param userid
	 * @return
	 * @throws NumberFormatException
	 * @throws BusinessException
	 */
	public List<String[]> getRolesByUserId(String userid) throws NumberFormatException, BusinessException {
		//取得个人角色
		List<String[]> roleNameList = new ArrayList<String[]>();
		List<V3xOrgRelationship> relList = orgManager.getRolesByMember(Long.parseLong(userid));
		for(V3xOrgRelationship rel:relList){
			String[] roleStr = new String[2];
			if(rel.getType().equals(V3xOrgEntity.ORGREL_TYPE_MEMBER_ACCROLE)){
				roleStr[0] = "";
				roleStr[1] = orgManager.getRoleById(rel.getBackupId()).getName();				
			}else if(rel.getType().equals(V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE)){
				roleStr[0] = orgManager.getDepartmentById(rel.getObjectiveId()).getName();
				roleStr[1] = orgManager.getRoleById(rel.getBackupId()).getName();
			}
			roleNameList.add(roleStr);
		}
		return roleNameList;
	}

	/**
	 * 根据用户id获得其所负责的所有项目列表
	 * @param userid
	 * @return
	 */
	public List getProjectManagerListByUserId(String userid) {
		List projectSummaryList = projectManager.getUserManagedProjectsByUserId(userid);
		return projectSummaryList;
	}

	/**
	 * 获得所属人为指定人员的表单模板列表
	 * @param userid
	 * @return
	 * @throws DataDefineException 
	 * @throws BusinessException 
	 */
	public List getFormAppList(String userid) throws BusinessException, DataDefineException {
		FormAppMain fam = new FormAppMain();
		fam.setCategory(null);
		fam.setUserids(userid);
		List applst =  iOperBase.queryAllData(fam);
		applst = iOperBase.assignCategory(applst);
		return applst;
	}

	/**
	 * 根据指定的用户id，获得由其管理的部门空间列表
	 * @param userid
	 * @return
	 */
	public List getManagementSpaceList(String userid) {
		List list= spaceManager.getManagmentSpaceListByUserId(userid);
		return list;
	}

	/**
	 * 根据用户id，获得由其管理的公告列表
	 * @param userid
	 * @return
	 */
	public List getBulTypeList(String userid) {
		List<BulType> list1= bulTypeManager.getManagerTypeByMember(Long.parseLong(userid), Constants.BulTypeSpaceType.corporation, null);
		List<BulType> list2= bulTypeManager.getManagerTypeByMember(Long.parseLong(userid), Constants.BulTypeSpaceType.group, null);
		List<BulType> list3= bulTypeManager.getManagerTypeByMember(Long.parseLong(userid), Constants.BulTypeSpaceType.department, null);
		list1.addAll(list2);
		list1.addAll(list3);
		return list1;
	}

	/**
	 * 根据userid获得该用户管理的调查板块列表
	 * @param userid
	 * @return
	 */
	public List getInquiryList(String userid) {
		List list= inquiryManager.getInquiryTypeListByUserId(userid,InquirySurveytypeextend.MANAGER_SYSTEM.intValue());
		return list;
	}
	/**
	 * 根据userid获得该用户管理的讨论板块列表
	 * @param userid
	 * @return
	 */
	public List getBbsList(String userid) {
		List list= bbsBoardManager.getBbsTypeByUserId(userid);
		return list;
	}
	/**
	 * 根据userid获得该用户管理的新闻板块列表
	 * @param userid
	 * @return
	 */
	public List getNewsList(String userid) {
		List list = newsTypeManager.getManagerTypeByMember(new Long(userid), null, null);
		return list;
	}
	/**
	 * 根据userid获得该用户审核的公告板块列表
	 * @param userid
	 * @return
	 */
	public List getBulTypeAuditList(String userid) {
		List list1= bulTypeManager.getAuditTypeByMember(new Long(userid),Constants.BulTypeSpaceType.corporation, null);
		List list2= bulTypeManager.getAuditTypeByMember(new Long(userid),Constants.BulTypeSpaceType.group, null);
		List list3= bulTypeManager.getAuditTypeByMember(new Long(userid),Constants.BulTypeSpaceType.department, null);
		list1.addAll(list2);
		list1.addAll(list3);
		return list1;
	}
	
	/**
	 * 根据userid获得该用户审核的调查板块列表
	 * @param userid
	 * @return
	 */
	public List getInquiryAuditList(String userid) {
		List list= inquiryManager.getInquiryTypeListByUserId(userid,InquirySurveytypeextend.MANAGER_CHECK.intValue());
		return list;
	}
	
	/**
	 * 根据userid获得该用户审核的新闻板块列表
	 * @param userid
	 * @return
	 */
	public List getNewsAuditList(String userid) {
		List list = newsTypeManager.getAuditTypeByMember(new Long(userid), null, null);
		return list;
	}

	/**
	 * 根据userid获得该用户是否为综合办公的管理员
	 * @param userid
	 * @return
	 * @throws BusinessException 
	 * @throws NumberFormatException 
	 */
	public List getOfficeAdminListByUserId(String userid) throws NumberFormatException, BusinessException {
		List result= new ArrayList();
		//根据userid获得所属的单位id
		V3xOrgMember leaveMember= orgManager.getMemberById(new Long(userid));
		//根据管理员来查询
		List list= officeAdminManager.findAdminSetting(leaveMember.getOrgAccountId(), "2", userid);
		if (list != null) {
			ArrayList mArr = new ArrayList();
			out: for (int i = 0; i < list.size(); i++) {
				MAdminSetting admin = (MAdminSetting) list.get(i);
				for (int j = 0; j < mArr.size(); j++) {
					String[] s_id = (String[]) mArr.get(j);
					if (s_id[0].equals(String.valueOf(admin.getId().getAdmin())) && s_id[1].equals(admin.getAdminModel())) {
						continue out;
					}
				}
				mArr.add(new String[] {String.valueOf(admin.getId().getAdmin()),admin.getAdminModel() });
			}
			boolean flag0= false;
			boolean flag1= false;
			boolean flag2= false;
			boolean flag3= false;
			boolean flag4= false;
			for (int i = 0; i < mArr.size(); i++) {
				String[] s_id = (String[]) mArr.get(i);
				char[] c_model = s_id[1].toCharArray();
				for (int j = 0; j < c_model.length; j++) {
					if (c_model[j] == '1') {
						if (j == 0 && !flag0) {//是车辆管理员
							String str0= ResourceBundleUtil.getString(
									com.seeyon.v3x.office.admin.util.Constants.ADMIN_RESOURCE_NAME,"admin.label.auto", new Object[0]);
							result.add(str0);
							flag0= true;
						}else if(j == 1 && !flag1) {//是设备管理员
							String str1= ResourceBundleUtil.getString(
									com.seeyon.v3x.office.admin.util.Constants.ADMIN_RESOURCE_NAME,"admin.label.asset",new Object[0]);
							result.add(str1);
							flag1= true;
						}else if(j == 2 && !flag2){//是图书资料管理员
							String str2= ResourceBundleUtil.getString(
									com.seeyon.v3x.office.admin.util.Constants.ADMIN_RESOURCE_NAME,"admin.label.book", new Object[0]);
							result.add(str2);
							flag2= true;
						}else if(j == 3 && !flag3){//是办公用品管理员
							String str3= ResourceBundleUtil.getString(
									com.seeyon.v3x.office.admin.util.Constants.ADMIN_RESOURCE_NAME,"admin.label.stock",new Object[0]);
							result.add(str3);
							flag3= true;
						}else if(j == 4 && !flag4){//是会议室管理员
							String str4= ResourceBundleUtil.getString(
									com.seeyon.v3x.office.admin.util.Constants.ADMIN_RESOURCE_NAME,"admin.label.meetingroom",new Object[0]);
							result.add(str4);
							flag4= true;
						}
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 根据userid获得该用户还没有归还的综合办公物品列表
	 * @param userid
	 * @return
	 */
	public List getOfficeDeviceListByUserId(String userid) {
		//车辆待归还列表
		List list1= autoManager.getAutoBackListByUserId(userid);
		//办公设备待归还列表
		List list2= assetManager.getAssetBackListByUserId(userid);
		//图书待归还列表
		List list3= bookManager.getBookBackListByUserId(userid);
		list1.addAll(list2);
		list1.addAll(list3);
		return list1;
	}

	/**
	 * @param agentIntercalateManager the agentIntercalateManager to set
	 */
	public void setAgentIntercalateManager(
			AgentIntercalateManager agentIntercalateManager) {
		this.agentIntercalateManager = agentIntercalateManager;
	}

	/**
	 * @param appLogManager the appLogManager to set
	 */
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	/**
	 * @param orgManager the orgManager to set
	 */
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	/**
	 * @param projectManager the projectManager to set
	 */
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	/**
	 * @param spaceManager the spaceManager to set
	 */
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	/**
	 * @param bulTypeManager the bulTypeManager to set
	 */
	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}

	/**
	 * @param inquiryManager the inquiryManager to set
	 */
	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	/**
	 * @param bbsBoardManager the bbsBoardManager to set
	 */
	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	/**
	 * @param newsTypeManager the newsTypeManager to set
	 */
	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	/**
	 * @param officeAdminManager the officeAdminManager to set
	 */
	public void setOfficeAdminManager(AdminManager officeAdminManager) {
		this.officeAdminManager = officeAdminManager;
	}

	/**
	 * @param autoManager the autoManager to set
	 */
	public void setAutoManager(AutoManager autoManager) {
		this.autoManager = autoManager;
	}

	/**
	 * @param assetManager the assetManager to set
	 */
	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	/**
	 * @param bookManager the bookManager to set
	 */
	public void setBookManager(BookManager bookManager) {
		this.bookManager = bookManager;
	}

	@Override
	public void handleOldProxyedInfo1(String agent_to_id,V3xAgent newV3xAgent) throws NumberFormatException, Exception {
		List<V3xAgent> oldList= agentIntercalateManager.queryAvailabilityList(Long.parseLong(agent_to_id));
		for (V3xAgent v3xAgent :oldList) {
			if(newV3xAgent.getId().longValue()!= v3xAgent.getId().longValue()){
				String optionStr= v3xAgent.getAgentOption();
				String[] options= optionStr.split("&");
				boolean flag= false;
				boolean oldflag= false;
				String optionTempStr="";
				for (int i = 0; i < options.length; i++) {
					if(options[i].equals("1")){
						flag= true;
					}else if(!"".equals(options[i].trim())){//还代理了其他事项
						oldflag= true;
						optionTempStr += options[i].trim()+"&";
					}
				}
				List<V3xAgentDetailModel> models = null;
				if(flag){//指定了别人代理了自己的协同，需要删除这个关系
					boolean mflag= false;
					List<V3xAgentDetail> v3xdetails= v3xAgent.getAgentDetails();
					List<V3xAgentDetail> v3xdetailsTemp= new ArrayList<V3xAgentDetail>();
					if(v3xdetails!= null && v3xdetails.size()>0){//有明细
						V3xAgentDetailModel model = null;
						models = new ArrayList<V3xAgentDetailModel>();
						for (V3xAgentDetail v3xAgentDetail : v3xdetails) {
							if(v3xAgentDetail.getEntityId()==2){//代理了全部模板
								mflag= true;
								continue;
							}else if(v3xAgentDetail.getEntityId()!=1){//代理了具体模板，有模板编号
								mflag= true;
								continue;
							}else{//自由协同
								v3xdetailsTemp.add(v3xAgentDetail);
								model = new V3xAgentDetailModel();
								model.setId(v3xAgentDetail.getId());
								model.setAgentId(v3xAgentDetail.getAgentId());
								model.setApp(v3xAgentDetail.getApp());
								model.setEntityId(v3xAgentDetail.getEntityId());
								models.add(model);
							}
						}
						if(mflag){//代理了模板流程
				    		if(v3xdetailsTemp.size()==0){//修改之前的代理关系
				    			if(oldflag){//还有其它代理事项
				    				v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
				    				agentIntercalateManager.update(v3xAgent, null);
				    				deleteAgentCache(v3xAgent);
				    				addAgentCache(v3xAgent,models);
				    			}else{//否则取消代理
				    				v3xAgent.setCancelFlag(true);
				    				agentIntercalateManager.update(v3xAgent, null);
				    				deleteAgentCache(v3xAgent);
				    			}
				    		}else{//还有其他代理明细，主要是自由协同
				    			v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
			    				agentIntercalateManager.update(v3xAgent, v3xdetailsTemp);
			    				deleteAgentCache(v3xAgent);
			    				addAgentCache(v3xAgent,models);
				    		}
						}
					}else{//把以前的改成只代理自由协同部分
			    		V3xAgentDetail detailTemp1 = new V3xAgentDetail();
			    		List<V3xAgentDetail> detailsTemp1 = new ArrayList<V3xAgentDetail>();
			    		detailTemp1.setIdIfNew();
			    		detailTemp1.setAgentId(v3xAgent.getId());
			    		detailTemp1.setApp(ApplicationCategoryEnum.collaboration.getKey());
			    		detailTemp1.setEntityId(1l);//代理了全部模板
						detailsTemp1.add(detailTemp1);
			    		agentIntercalateManager.update(v3xAgent, detailsTemp1);
			    		models = new ArrayList<V3xAgentDetailModel>();
			    		V3xAgentDetailModel model = new V3xAgentDetailModel();
						model.setId(detailTemp1.getId());
						model.setAgentId(detailTemp1.getAgentId());
						model.setApp(detailTemp1.getApp());
						model.setEntityId(detailTemp1.getEntityId());
						models.add(model);
						deleteAgentCache(v3xAgent);
						addAgentCache(v3xAgent, models);
					}
				}
			}
		}
	}

	/**
	 * 删除代理缓存
	 * @param v3xAgent
	 */
	private void deleteAgentCache(V3xAgent v3xAgent) {
		List<Long> ids;
		List<AgentModel> agentModels = MemberAgentBean.getInstance().getAgentModelList(v3xAgent.getAgentId());
		AgentModel _agentModel = null;
		if(agentModels != null && !agentModels.isEmpty()){
			for(AgentModel agentModel : agentModels){
				if(agentModel.getId().equals(v3xAgent.getId())){
					_agentModel = agentModel;
					break;
				}
			}
			if(_agentModel != null){
				ids = new ArrayList<Long>(1);
				ids.add(_agentModel.getId());
				MemberAgentBean.getInstance().remove(_agentModel.getAgentId(), ids, true, true);
				MemberAgentBean.getInstance().notifyUpdateAgentModel(v3xAgent.getAgentId());
			}
		}
	}

	/**
	 * 刷新代理缓存(变化)
	 * @param agent
	 * @param models
	 * @throws Exception
	 */
	private void addAgentCache(V3xAgent agent,List<V3xAgentDetailModel> models) throws Exception {
		AgentModel agentModel = new AgentModel();
		agentModel.setAgentId(agent.getAgentId());
		agentModel.setAgentToId(agent.getAgentToId());
		agentModel.setId(agent.getId());
		agentModel.setAgentOption(agent.getAgentOption());
		agentModel.setStartDate(agent.getStartDate());
		agentModel.setEndDate(agent.getEndDate());
		agentModel.setAgentDetail(models);
		MemberAgentBean.getInstance().put(agent.getAgentId(), agentModel, null);
		MemberAgentBean.getInstance().put(agent.getAgentToId(), null, agentModel);
		MemberAgentBean.getInstance().notifyUpdateAgentModel(agent.getAgentId());
		MemberAgentBean.getInstance().notifyUpdateAgentModelTo(agent.getAgentToId());
	}

	@Override
	public void handleOldProxyedInfo2(String agent_to_id,V3xAgent newV3xAgent) throws NumberFormatException, Exception{
		List<V3xAgent> oldList= agentIntercalateManager.queryAvailabilityList(Long.parseLong(agent_to_id));
		for ( V3xAgent v3xAgent: oldList ) {
			if(newV3xAgent.getId().longValue()!= v3xAgent.getId().longValue()){
				String optionStr= v3xAgent.getAgentOption();
				String[] options= optionStr.split("&");
				boolean flag= false;
				boolean oldflag= false;
				String optionTempStr="";
				for (int i = 0; i < options.length; i++) {
					if(options[i].equals("1")){
						flag= true;
					}else if(!"".equals(options[i].trim())){//还代理了其他事项
						oldflag= true;
						optionTempStr += options[i].trim()+"&";
					}
				}
				List<V3xAgentDetailModel> models = null;
				if(flag){//代理别人的协同，需要转给新的代理人
					boolean mflag= false;
					List<V3xAgentDetail> v3xdetails= v3xAgent.getAgentDetails();
					List<V3xAgentDetail> v3xdetailsTemp= new ArrayList<V3xAgentDetail>();
					if(v3xdetails!= null && v3xdetails.size()>0){//有明细
						V3xAgentDetailModel model = null;
						models = new ArrayList<V3xAgentDetailModel>();
						for ( V3xAgentDetail v3xAgentDetail: v3xdetails ) {
							if(v3xAgentDetail.getEntityId()==1){//代理了自由协同
								mflag= true;
								continue;
							}else{
								v3xdetailsTemp.add(v3xAgentDetail);
								model = new V3xAgentDetailModel();
								model.setId(v3xAgentDetail.getId());
								model.setAgentId(v3xAgentDetail.getAgentId());
								model.setApp(v3xAgentDetail.getApp());
								model.setEntityId(v3xAgentDetail.getEntityId());
								models.add(model);
							}
						}
						if(mflag){//代理了自由协同
				    		//修改之前的代理关系
				    		if(v3xdetailsTemp.size()==0){
				    			if(oldflag){//还有其它代理事项
				    				v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
				    				agentIntercalateManager.update(v3xAgent, null);
				    				deleteAgentCache(v3xAgent);
				    				addAgentCache(v3xAgent,models);
				    			}else{//否则取消代理
				    				v3xAgent.setCancelFlag(true);
				    				agentIntercalateManager.update(v3xAgent, null);
				    				deleteAgentCache(v3xAgent);
				    			}
				    		}else{//还有其他代理明细，主要是模板协同
				    			v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
			    				agentIntercalateManager.update(v3xAgent, v3xdetailsTemp);
			    				deleteAgentCache(v3xAgent);
			    				addAgentCache(v3xAgent,models);
				    		}
						}
					}else{//保留之前的模板流程
						//将模板协同保留下来
			    		V3xAgentDetail detailTemp1 = new V3xAgentDetail();
			    		List<V3xAgentDetail> detailsTemp1 = new ArrayList<V3xAgentDetail>();
			    		detailTemp1.setIdIfNew();
			    		detailTemp1.setAgentId(v3xAgent.getId());
			    		detailTemp1.setApp(ApplicationCategoryEnum.collaboration.getKey());
			    		detailTemp1.setEntityId(Long.parseLong("2"));//代理了自由协同
			    		detailsTemp1.add(detailTemp1);
			    		agentIntercalateManager.update(v3xAgent, detailsTemp1);
			    		
			    		models = new ArrayList<V3xAgentDetailModel>();
			    		V3xAgentDetailModel model = new V3xAgentDetailModel();
						model.setId(detailTemp1.getId());
						model.setAgentId(detailTemp1.getAgentId());
						model.setApp(detailTemp1.getApp());
						model.setEntityId(detailTemp1.getEntityId());
						models.add(model);
						deleteAgentCache(v3xAgent);
						addAgentCache(v3xAgent, models);
					}
				}
			}
		}
	}

	@Override
	public void handleOldProxyedInfo4(String agent_to_id,V3xAgent newV3xAgent)
			throws NumberFormatException, Exception {
		List<V3xAgent> oldList= agentIntercalateManager.queryAvailabilityList(Long.parseLong(agent_to_id));
		for (V3xAgent v3xAgent: oldList) {
			if(newV3xAgent.getId().longValue()!= v3xAgent.getId().longValue()){
				String optionStr= v3xAgent.getAgentOption();
				String[] options= optionStr.split("&");
				boolean flag= false;
				boolean oldflag= false;
				String optionTempStr="";
				for (int i = 0; i < options.length; i++) {
					if(options[i].equals("4")){
						flag= true;
					}else if(!"".equals(options[i].trim())){//还代理了其他事项
						oldflag= true;
						optionTempStr += options[i].trim()+"&";
					}
				}
				if(flag){//指定了别人代理自己的公文
	    			if(oldflag){//还指定了别人代理自己的其他事项，则保留下来
	    				v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
	    				agentIntercalateManager.update(v3xAgent);
	    				deleteAgentCache(v3xAgent);
	    				addAgentCache(v3xAgent,null);
	    			}else{//否则取消本代理
	    				v3xAgent.setCancelFlag(true);
	    				agentIntercalateManager.update(v3xAgent, null);
	    				deleteAgentCache(v3xAgent);
	    			}
				}
			}
		}
	}

	@Override
	public void handleOldProxyedInfo9(String agent_to_id,V3xAgent newV3xAgent)
			throws NumberFormatException, Exception {
		List<V3xAgent> oldList= agentIntercalateManager.queryAvailabilityList(Long.parseLong(agent_to_id));
		for ( V3xAgent v3xAgent: oldList ) {
			if(newV3xAgent.getId().longValue()!=v3xAgent.getId().longValue()){
				String optionStr= v3xAgent.getAgentOption();
				String[] options= optionStr.split("&");
				boolean flag= false;
				boolean oldflag= false;
				String optionTempStr="";
				String equalOpionStr="";
				for (int i = 0; i < options.length; i++) {
					if(options[i].equals("7") || options[i].equals("10") || options[i].equals("8")){
						flag= true;
						equalOpionStr += options[i].trim()+"&";
					}else if(!"".equals(options[i].trim())){//还代理了其他事项
						oldflag= true;
						optionTempStr += options[i].trim()+"&";
					}
				}
				if(flag){//指定了别人代理自己的公共信息
	    			if(oldflag){//还指定了别人代理自己的其他事项，则保留下来
	    				v3xAgent.setAgentOption(StringUtils.removeEnd(optionTempStr, "&"));
	    				agentIntercalateManager.update(v3xAgent);
	    				deleteAgentCache(v3xAgent);
	    				addAgentCache(v3xAgent, null);
	    			}else{//否则取消代理
	    				v3xAgent.setCancelFlag(true);
	    				agentIntercalateManager.update(v3xAgent, null);
	    				deleteAgentCache(v3xAgent);
	    			}
				}
			}
		}
	}

	/**
	 * @param userMessageManager the userMessageManager to set
	 */
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
}
