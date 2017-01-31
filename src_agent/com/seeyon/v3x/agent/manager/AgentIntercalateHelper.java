package com.seeyon.v3x.agent.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.agent.domain.V3xAgent;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;

public class AgentIntercalateHelper implements Job{
	static Log log = LogFactory.getLog(AgentIntercalateHelper.class);
	
	public void execute(JobExecutionContext datamap) throws JobExecutionException {
		AgentIntercalateManager agentIntercalateManager = (AgentIntercalateManager)ApplicationContextHolder.getBean("agentIntercalateManager");
		if(agentIntercalateManager==null){
			log.warn("无法获取AgentIntercalateManager，忽略");
			return;
		}
		List<V3xAgent> agentModelList = null;
		try {
			agentModelList = agentIntercalateManager.queryAllValidity();
			if(agentModelList == null) {
				return;
			}
			Date now = new Date();
			
			for(V3xAgent agent : agentModelList){
				Date startDate = new Date(agent.getStartDate().getTime());
				Date endDate = new Date(agent.getEndDate().getTime());
				if((now.compareTo(startDate) != -1 && now.compareTo(endDate) == -1) || now.compareTo(startDate) == -1){
					AgentModel agentModel = new AgentModel();
					agentModel.setAgentId(agent.getAgentId());
					agentModel.setAgentToId(agent.getAgentToId());
					agentModel.setId(agent.getId());
					agentModel.setAgentOption(agent.getAgentOption());
					agentModel.setStartDate(agent.getStartDate());
					agentModel.setEndDate(agent.getEndDate());
					MemberAgentBean.getInstance().put(agent.getAgentId(), agentModel, null);
					MemberAgentBean.getInstance().put(agent.getAgentToId(), null, agentModel);
					//给代理人发送消息提醒(每天都发消息，太烦人了，去掉)
					//agentIntercalateManager.sendAgentSettingMessage(agent);
				}
			}
			
			//删除内存中到期代理设置信息，将代理信息结束标记置上
			List<Map<Long,List<AgentModel>>> agentModelMap = MemberAgentBean.getInstance().getAllAgentModels();
			Map<Long, List<AgentModel>> agentData = agentModelMap.get(0);
			Map<Long, List<AgentModel>> agentToData = agentModelMap.get(1);
			for(Long key : agentData.keySet()){
				List<AgentModel> modelList = agentData.get(key);
				List<AgentModel> preDelModelList = new ArrayList<AgentModel>();
				for(AgentModel agentModel : modelList){
					Date endDate = new Date(agentModel.getEndDate().getTime());
					if(endDate.compareTo(now) == -1){
						preDelModelList.add(agentModel);
					}
				}
				for(AgentModel preDelModel : preDelModelList){
					V3xAgent agent = agentIntercalateManager.getById(preDelModel.getId());
					List<Long> list = new ArrayList<Long>();
					list.add(agent.getId());
					MemberAgentBean.getInstance().remove(preDelModel.getAgentId(), list, true, true);
					MemberAgentBean.getInstance().notifyUpdateAgentModel(key);
					//给代理人发送消息提醒
					agentIntercalateManager.sendDelAgentSettingMessage(agent);
					agent.setCancelFlag(true);
					agent.setCancelDate(new java.sql.Timestamp(System.currentTimeMillis()));
					agentIntercalateManager.update(agent);
				}
			}
			
			for(Long key : agentToData.keySet()){
				List<AgentModel> modelToList = agentToData.get(key);
				List<AgentModel> preDelModelToList = new ArrayList<AgentModel>();
				for(AgentModel agentModel : modelToList){
					Date endDate = new Date(agentModel.getEndDate().getTime());
					if(endDate.compareTo(now) == -1){
						preDelModelToList.add(agentModel);
					}
				}
				for(AgentModel preDelModel : preDelModelToList){
					V3xAgent agent = agentIntercalateManager.getById(preDelModel.getId());
					List<Long> list = new ArrayList<Long>();
					list.add(agent.getId());
					MemberAgentBean.getInstance().remove(preDelModel.getAgentToId(), list, false, true);
					MemberAgentBean.getInstance().notifyUpdateAgentModelTo(key);
					agent.setCancelFlag(true);
					agent.setCancelDate(new java.sql.Timestamp(System.currentTimeMillis()));
					agentIntercalateManager.update(agent);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}
}