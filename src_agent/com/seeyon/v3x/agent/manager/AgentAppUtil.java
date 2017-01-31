package com.seeyon.v3x.agent.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;

/**
 * 各个应用查询代理信息
 * @author dongyj
 *
 */
public class AgentAppUtil {
	
	private static Log log = LogFactory.getLog(AgentAppUtil.class);
	
	private boolean agentToFlag = false;
	
	private boolean isPloxy = false;
	
	private List<AgentModel> agentList = null;
	
	public AgentAppUtil(long memberId){
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(memberId);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(memberId);
    	if(_agentModelList != null && !_agentModelList.isEmpty()){
			isPloxy = true;
			agentList = validateAgentModel(_agentModelList,memberId);
		}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
			isPloxy = true;
			agentList = validateAgentModel(_agentModelToList,memberId);
			agentToFlag = true;
		}
	}
	public List<AgentModel> validateAgentModel(List<AgentModel> models,long memberId){
		log.info("代理信息过滤： memberId " + memberId +" size "+models.size());
		Date now = new Date();
		List<AgentModel> tempModelList=new ArrayList<AgentModel>();
		for(AgentModel agentModel : models){
			log.info("agentModel " + agentModel.getId());
			//过滤：只有处于期限内的代理才保留
    		if(now.before(agentModel.getStartDate()) || now.after(agentModel.getEndDate())){
    			log.info("过滤掉agentModel " + agentModel.getId());
    			continue;
    		}
    		tempModelList.add(agentModel);
		}
		return tempModelList;
	}
	private void saveAgentMap(int app,Map<Integer,List<AgentModel>> map,AgentModel model){
		List<AgentModel> list = map.get(app);
		if(list == null){
			list = new ArrayList<AgentModel>();
		}
		list.add(model);
		map.put(app, list);
	}
	public Map<Integer,List<AgentModel>> getAppAgentMap(List<Integer> apps){
		Map<Integer,List<AgentModel>> result = new HashMap<Integer,List<AgentModel>>();
		if(agentList != null){
			for(AgentModel model: agentList){
				String agentOptionStr = model.getAgentOption();
	    		String[] agentOptions = agentOptionStr.split("&");
	    		for(String agentOption : agentOptions){
	    			int _agentOption = Integer.parseInt(agentOption);
	    			if(apps.contains(_agentOption)){
	    				saveAgentMap(_agentOption,result,model);
	    			}
	    		}
			}
		}
		return result;
	}
	public boolean isAgentToFlag() {
		return agentToFlag;
	}
	public boolean isPloxy() {
		return isPloxy;
	}
}
