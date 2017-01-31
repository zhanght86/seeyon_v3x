/**
 * 
 */
package com.seeyon.v3x.collaboration.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMAbstractNode.NodeType;
import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMActor;
import net.joinwork.bpm.definition.BPMHumenActivity;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMTransition;
import net.joinwork.bpm.engine.exception.BPMException;
import net.joinwork.bpm.engine.execute.BPMCase;
import net.joinwork.bpm.engine.wapi.WAPIFactory;
import net.joinwork.bpm.task.WorkItemManagerImpl;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.workflow.engine.org.WorkFlowOrgManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.workflow.event.WorkflowEventListener.PersonInfo;

/**
 * @Project/Product:A8
 * @Description:分支匹配算法
 * @Copyright: Copyright (c) 2011 Seeyon,All Rights Reserved.
 * @author: wangchw
 * @time:2011-09-20 下午02:07:46
 * @version:1.0
 */
public class BranchArgs {
	
	/**
	 * preNext()
	 * 遍历当前处理节点的后续节点和分支条件信息
	 * @param currentNode 当前处理节点
	 * @param context 上下文信息
	 * @param isFromTemplate 是否为模板流程
	 * @return 匹配结果集合
	 * @throws NumberFormatException
	 * @throws BusinessException
	 */
	public static Map preNext(
			BPMAbstractNode currentNode,Map context,
			boolean isFromTemplate) throws NumberFormatException, BusinessException{
		Map followUpMap= new HashMap();
		followUpMap.put("isPop", "false");
		//默认都为只读
		followUpMap.put("isAllReadOnly", new Boolean(true));
		//找出当前处理节点（包括发起节点）的后续所有直接人工活动节点:
		//后续所有直接知会节点allInformNodes
		//后续所有直接非知会节点allNonInformNodes
		//后续所有直接分支条件列表allBranchConditions
		//后续所有直接人工节点中不可用人员列表all
		context.put("CurrentActivity", currentNode);//上节点信息获取需要该参数
		getFollowUpDirectInfo(currentNode,followUpMap,context,isFromTemplate);
		Map allInformNodes=null;
		if(null!=followUpMap.get("allInformNodes")){
			allInformNodes= (Map)followUpMap.get("allInformNodes");
		}
		Map allNonInformNodes= null;
		if(null!=followUpMap.get("allNonInformNodes")){
			allNonInformNodes= (Map)followUpMap.get("allNonInformNodes");
		}
		Map allBranchConditions= null;
		if(null!= followUpMap.get("allBranchConditions")){
			allBranchConditions= (Map)followUpMap.get("allBranchConditions");
		}
		if(allInformNodes!=null || allNonInformNodes!=null ){
			if(allInformNodes!=null && allInformNodes.size() > 0){//有知会节点
				if(!isFromTemplate){//不是模板流程，就没有条件分支匹配的问题
					//是否所有非知会节点和知会节点的执行人都只匹配到一个人
					Boolean isAllReadOnly= (Boolean)followUpMap.get("isAllReadOnly");
					if( !isAllReadOnly ){//否，有节点下没有具体执行人
						//弹出流程选择页面
						followUpMap.put("isPop", "true");
					}
				}else{//是模板流程
					//弹出流程选择页面
					followUpMap.put("isPop", "true");
				}
			}else{//只有非知会节点
				if( (allBranchConditions==null || allBranchConditions.size() == 0) ){//没有分支条件
					//是否所有非知会节点的执行人都只匹配到一个人
					Boolean isAllReadOnly= (Boolean)followUpMap.get("isAllReadOnly");
					if( !isAllReadOnly ){//否
						//弹出流程选择页面
						followUpMap.put("isPop", "true");
					}
				}else if( (allBranchConditions!=null && allBranchConditions.size() >0 ) ){//有分支条件
					//弹出流程选择页面
					followUpMap.put("isPop", "true");
				}
			}
		}
		return followUpMap;
	}
	
	/**
	 * getFollowUpDirectInfo()
	 * 获得当前节点的所有后续直接相关信息(知会、非知会和分支条件)
	 * @param currentNode 当前节点
	 * @param followUpMap 后续节点存储
	 * @param context 上下文信息
	 * @param isFromTemplate 是否来自模板
	 * @throws NumberFormatException
	 * @throws BusinessException
	 */
	private static void getFollowUpDirectInfo(
			BPMAbstractNode currentNode,
			Map followUpMap,
			Map context,
			boolean isFromTemplate) throws NumberFormatException, BusinessException {
		//获得当前节点的down线集合
		List<BPMTransition> downs= currentNode.getDownTransitions();
		if(downs!= null && downs.size()>0 ){
			for (BPMTransition bpmTransition : downs) {
				//获得down线指向的节点
				BPMAbstractNode toNode= bpmTransition.getTo();
				//获得当前节点的节点类型
				NodeType nodeType= toNode.getNodeType();
				//System.out.println("nodeType:="+nodeType);
				if( nodeType.equals(NodeType.split) || nodeType.equals(NodeType.join) ){//split节点或join节点
					//继续查找后续人工活动节点
					getFollowUpDirectInfo(toNode,followUpMap,context,isFromTemplate);
				}else if( nodeType.equals(NodeType.humen) ){//humen节点
					String toNodePolicy = toNode.getSeeyonPolicy().getId();
					BPMHumenActivity hactivity= (BPMHumenActivity)toNode;
					if( "inform".equals(toNodePolicy) || "zhihui".equals(toNodePolicy) ){//知会节点
						Map allInformNodes= null;
						if( followUpMap.get("allInformNodes") != null ){
							allInformNodes= (Map)followUpMap.get("allInformNodes");
							if( allInformNodes.get(toNode.getId()) == null ){
								allInformNodes.put(toNode.getId(), toNode);
							}
						}else{
							allInformNodes= new HashMap();
							allInformNodes.put(toNode.getId(), toNode);
						}
						followUpMap.put("allInformNodes", allInformNodes);
					}else{//非知会节点
						Map allNonInformNodes= null;
						if( followUpMap.get("allNonInformNodes") != null ){
							allNonInformNodes= (Map)followUpMap.get("allNonInformNodes");
							if( allNonInformNodes.get(toNode.getId()) == null ){
								allNonInformNodes.put(toNode.getId(), toNode);
							}
						}else{
							allNonInformNodes= new HashMap();
							allNonInformNodes.put(toNode.getId(), toNode);
						}
						followUpMap.put("allNonInformNodes", allNonInformNodes);
					}
					//是否所有的非知会节点和非知会只有一个可执行人
					processNodeExecuteUsers(hactivity,context,isFromTemplate,followUpMap);
					//判断该人工活动节点的执行人员是否为不可用
					if(!"normal".equals(hactivity.isValid())){ //节点不可用
						Map invalidateActivityMap= null;
						if( followUpMap.get("invalidateActivityMap") != null ){
							invalidateActivityMap= (Map)followUpMap.get("invalidateActivityMap");
							invalidateActivityMap.put(hactivity.getId(), hactivity.getBPMAbstractNodeName());
						}else{
							invalidateActivityMap= new HashMap();
							invalidateActivityMap.put(hactivity.getId(), hactivity.getBPMAbstractNodeName());
						}
						followUpMap.put("invalidateActivityMap", invalidateActivityMap);
					}
				}
				//对down进行分支条件处理
				String currentCondition = bpmTransition.getFormCondition();
				Map allBranchConditions= null;
	        	if(followUpMap.get("allBranchConditions")==null){
	        		allBranchConditions= new HashMap();
	    		}else{
	    			allBranchConditions= (Map)followUpMap.get("allBranchConditions");
	    		}
				if(currentCondition != null && !"".equals(currentCondition.trim()) && !"null".equals(currentCondition.trim())){//有自动条件分支
		        	allBranchConditions.put(bpmTransition.getId(), currentCondition);
		        	followUpMap.put("allBranchConditions", allBranchConditions);
		        }else if(bpmTransition.getConditionType()==2){//有手动选择分支
		        	allBranchConditions.put(bpmTransition.getId(), "handCondition");
		        	followUpMap.put("allBranchConditions", allBranchConditions);
		        }
			}
		}
	}

	/**
	 * processNodeExecuteUsers()
	 * 对节点的执行人进行匹配
	 * @param hactivity 人工活动节点对象
	 * @param context 上下文信息
	 * @param isFromTemplate 是否来自模板
	 * @param followUpMap 后续节点信息存储
	 * @throws BusinessException 
	 * @throws NumberFormatException 
	 */
	private static void processNodeExecuteUsers(
			BPMHumenActivity hactivity,
			Map context,
			boolean isFromTemplate,
			Map followUpMap) throws NumberFormatException, BusinessException {
		List<BPMActor> actors = hactivity.getActorList();
    	BPMActor actor = actors.get(0);
    	if(!"user".equals(actor.getType().id)
    			//该节点直接是人，不用匹配
    			&& !V3xOrgEntity.ORGENT_META_KEY_BlankNode.equals(actor.getParty().getId())
    			//空节点，不用匹配
    			){
    		context.put("activity", hactivity);
    		actor.getParty().setAddition("");
	    	WorkFlowOrgManager workFlowOrgManager = WAPIFactory.getWorkFlowOrgManager("Engine_1");
	        List<com.seeyon.v3x.common.organization.sharemodel.User> users = actor.getUserList(workFlowOrgManager, context);
	        //不需要匹配，有人就行:1. 该节点是后加的，如：加签;2. 不是模板(自由协同);3. 竞争执行 ;4. 全体执行
	        if(users != null && !users.isEmpty() 
	        		&& (hactivity.isAdded() 
//	        		        || !isFromTemplate 
	        		        || hactivity.isCompetitionProcessMode() || hactivity.isAllProcessMode())){
	        	//donothing!
	        }else{
	        	if(users.size() !=1 ){//当前匹配到不止一个人时（没有人或多于一个人时），在前端显示出流程人员选择页面
	        		if(followUpMap.get("isAllReadOnly")!=null){
	        			Boolean isAllReadOnly= (Boolean)followUpMap.get("isAllReadOnly");
	        			if(isAllReadOnly==true){
	        				isAllReadOnly= false;
	        			}
	        			followUpMap.put("isAllReadOnly", isAllReadOnly);
	        		}else{
	    	        	followUpMap.put("isAllReadOnly", new Boolean(false));
	        		}
	        	}
	        }
    	}
	}

	/**
	 * getWorkitemList()
	 * 查找任务工作项
	 * @param nodeId 流程节点标识
	 * @param caseId 流程实例Id
	 * @param status 工作任务状态：0表示待办，1表已办
	 * @return
	 * @throws BPMException 
	 */
	public static List getWorkitemList(String nodeId, long caseId,
			String status) throws BPMException {
		//从工作流中获得任务工作项操作接口
		List list= new ArrayList();
		WorkItemManagerImpl wim = (WorkItemManagerImpl)WAPIFactory.getWorkItemManager("Task_1");
		if("0".equals(status)){//查找待办
			list= wim.getWorkItemList(
					null,
					null,
					null, 
					null, 
					caseId,
					null,
					nodeId, 
					null, 
					null,
					null, 
					null,
					null, 
					0,
					0, 
					0,
					1, 
					false);
		}else if("1".equals(status)){//查找已办
			list= wim.getHistoryWorkitemList(
					null, 
					null, 
					null,
					null, 
					null,
					null, 
					caseId,
					null,
					null, 
					nodeId, 
					null, 
					null, 
					0,
					0,
					1, 
					false);
		}
		return list;
	}
	
	public static boolean hasSelectorOrCondition(BPMProcess bpmProcess,
			BPMAbstractNode currentActivity, Long caseId,
			boolean isFromTemplate,
			Map<String,String[]> fieldDataBaseMap,
			OrgManager orgManager,
			Map followUpMap,long workItemId) throws Exception {
		Map context= new HashMap();
		context.put("process", bpmProcess);
		context.put("currentWorkitemId", workItemId);
        if(fieldDataBaseMap != null){
        	context.put("fieldValueMap", fieldDataBaseMap) ;
        }
        if(!"start".equals(currentActivity.getId())){//发起时，case不存在
			BPMCase theCase = ColHelper.getProcessEngine().getCase(caseId);
			context.put("case", theCase);
        	List<BPMActor> actorList = bpmProcess.getStart().getActorList();
            String sender = actorList.get(0).getParty().getId() + "";
            context.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, sender);
        }else{
        	String sender = CurrentUser.get().getId() + "";
            context.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, sender);
        }
		boolean isHasCondtion= false;//用来标志当前节点之后是否带有分支条件
		boolean isHasNeedSelectPepole= false;//用来标志当前节点后续直接人工活动节点是否需要选人
		if(null== followUpMap){
			followUpMap= new HashMap();
		}
		followUpMap.put("isPop", "false");
		//默认都为只读
		followUpMap.put("isAllReadOnly", new Boolean(true));
		//找出当前处理节点（包括发起节点）的后续所有直接人工活动节点:
		//后续所有直接知会节点allInformNodes
		//后续所有直接非知会节点allNonInformNodes
		//后续所有直接分支条件列表allBranchConditions
		//后续所有直接人工节点中不可用人员列表all
		context.put("CurrentActivity", currentActivity);//上节点信息获取需要该参数
		BranchArgs.getFollowUpDirectInfo(currentActivity,followUpMap,context,true);
		List<String> allSelectInformNodes= new ArrayList<String>();
		Map allInformNodes=null;
		if(null!=followUpMap.get("allInformNodes")){
			allInformNodes= (Map)followUpMap.get("allInformNodes");
		}
		Map allNonInformNodes= null;
		if(null!=followUpMap.get("allNonInformNodes")){
			allNonInformNodes= (Map)followUpMap.get("allNonInformNodes");
		}
		Map allBranchConditions= null;
		if(null!= followUpMap.get("allBranchConditions")){
			allBranchConditions= (Map)followUpMap.get("allBranchConditions");
		}
		Map invalidateActivityMap= null;
		if(null!=followUpMap.get("invalidateActivityMap")){
			invalidateActivityMap= (Map)followUpMap.get("invalidateActivityMap");
		}
		Boolean isAllReadOnly= (Boolean)followUpMap.get("isAllReadOnly");
		
		if( (allBranchConditions!=null && allBranchConditions.size() >0 ) ){//有分支条件
			isHasCondtion= true;
			followUpMap.put("isPop", "true");
		}
		if( !isAllReadOnly ){//需要选择人员
			isHasNeedSelectPepole= true;
			followUpMap.put("isPop", "true");
		}
		//判断是否存在不可用的节点，如果存在则进行如下处理
    	if(invalidateActivityMap != null && !invalidateActivityMap.isEmpty()){//存在不可用的人员（人员离职）
    		isHasNeedSelectPepole= true;
    	}
    	if(!isHasCondtion && !isHasNeedSelectPepole){//后续直接人工活动没有分支条件，也不需要选人
    		if((null==allNonInformNodes && null==allInformNodes)
    				||(null!=allNonInformNodes && allNonInformNodes.size()<=0 
    						&& null!=allInformNodes && allInformNodes.size()<=0)){//当前节点之后为结束节点
    			followUpMap.put("isPop", "false");
    			return false;
    		}else if(null!= allInformNodes && allInformNodes.size()>0){//当前节点之后有知会节点，就得往后遍历
    			//以当前节点所有后续知会节点为起点，继续递归往后遍历
    			List allSelectNodeList= new ArrayList();
    			allSelectNodeList.addAll(allInformNodes.keySet());
    			allSelectInformNodes.addAll(new ArrayList(allInformNodes.keySet()));
    			if(null!= allNonInformNodes){
    				allSelectNodeList.addAll(new ArrayList(allNonInformNodes.keySet()));
    			}
    			boolean result= handleAllInformNodes(allInformNodes,allSelectInformNodes,currentActivity,allSelectNodeList,context,isFromTemplate,fieldDataBaseMap,orgManager,followUpMap);
    			return result;
    		}else{//其他情况
    			followUpMap.put("isPop", "false");
    			return false;
    		}
        }else{
        	return true;
        }
	}
	
	/**
	 * 当前节点currentActivity之后是否有分支和选择执行人员
	 * @param bpmProcess
	 * @param currentActivity
	 * @param caseId
	 * @param context
	 * @param isFromTemplate
	 * @param fieldMap
	 * @param fieldDataBaseMap
	 * @return
	 * @throws Exception
	 */
	public static boolean hasSelectorOrCondition(BPMProcess bpmProcess,
			BPMAbstractNode currentActivity, Long caseId,
			boolean isFromTemplate,
			Map<String,String[]> fieldDataBaseMap,
			OrgManager orgManager,long workitem) throws Exception {
		return hasSelectorOrCondition(bpmProcess, currentActivity, caseId, isFromTemplate, fieldDataBaseMap, orgManager,null,workitem);
	}
	
	/**
	 * 
	 * @param allInformNodes
	 * @param current_node
	 * @param allSelectNodeList
	 * @param context
	 * @param isFromTemplate
	 * @param fieldMap
	 * @param fieldDataBaseMap
	 * @return
	 * @throws Exception
	 */
	private static boolean handleAllInformNodes(Map allInformNodes,List<String> allSelectInformNodes,
			BPMAbstractNode current_node,
			List allSelectNodeList,
			Map context,
			boolean isFromTemplate,
			Map<String,String[]> fieldDataBaseMap,
			OrgManager orgManager,
			Map followUpMap) throws Exception {
		List<String> allNotSelectNodeList= new ArrayList<String>();
		HashMap<String,String> conditonMap= new HashMap<String, String>();
		HashMap<String,String> nodeTypes= new HashMap<String, String>();
		List<BPMHumenActivity> allChildren= new ArrayList<BPMHumenActivity>();
		List<String> allChildrenList= new ArrayList<String>();
		//循环对informNodeList进行处理
		Iterator keyIter= allInformNodes.keySet().iterator();
		List informNodeList= new ArrayList(allInformNodes.keySet());
		for (; keyIter.hasNext();) {
			String currentInformNodeId = (String) keyIter.next();
			BPMActivity currentInformNode = (BPMActivity)allInformNodes.get(currentInformNodeId);
			List<BPMHumenActivity> children =
				ColHelper.findDirectHumenChildrenOfInformNode(
						current_node,
						currentInformNode,
						currentInformNode, 
						conditonMap,
						nodeTypes,
						allNotSelectNodeList,
						allSelectNodeList,
						informNodeList,
						allSelectInformNodes,
						context,
						allChildrenList);
			if(conditonMap.size()>0) {//可能有分支条件
				Map<String,Object> map = ColHelper.splitCondition(conditonMap);
				List<String> conditions = (List<String>)map.get("conditions");
				if(conditions==null || conditions.size()==0){//无分支
				}else{//有分支
					followUpMap.put("isPop", "true");
					return true;
				}
			}
			if(children!=null){
				allChildren.addAll(children);
			}
		}
		Map tempInformNodes= new HashMap();
		for (BPMHumenActivity activity : allChildren) {
			if(ColHelper.isInform(activity)){
				tempInformNodes.put(activity.getId(),activity);//找出后续知会节点
			}
			allSelectNodeList.add(activity.getId());
			if(!"normal".equals(activity.isValid())){ //节点不可用
				Map invalidateActivityMap= null;
				if( followUpMap.get("invalidateActivityMap") != null ){
					invalidateActivityMap= (Map)followUpMap.get("invalidateActivityMap");
					invalidateActivityMap.put(activity.getId(), activity.getBPMAbstractNodeName());
				}else{
					invalidateActivityMap= new HashMap();
					invalidateActivityMap.put(activity.getId(), activity.getBPMAbstractNodeName());
				}
				followUpMap.put("invalidateActivityMap", invalidateActivityMap);
				followUpMap.put("isPop", "false");
				return true;
			}
			List<BPMActor> actors = activity.getActorList();
	    	BPMActor actor = actors.get(0);
	    	if("user".equals(actor.getType().id) ||
	    			V3xOrgEntity.ORGENT_META_KEY_BlankNode.equals(actor.getParty().getId())){
	    		//该节点直接是人或空节点，不用匹配
	    		continue;
	    	}
	    	context.put("activity", activity);
	    	actor.getParty().setAddition("");
	    	WorkFlowOrgManager workFlowOrgManager = WAPIFactory.getWorkFlowOrgManager("Engine_1");
	        List<com.seeyon.v3x.common.organization.sharemodel.User> users = actor.getUserList(workFlowOrgManager, context);
	        if(users != null && !users.isEmpty() 
	        		&& (activity.isAdded() 
//	        				|| !isFromTemplate 
	        				|| activity.isCompetitionProcessMode() 
	        				|| activity.isAllProcessMode())){
	        	/*
		         * 不需要匹配，有人就行:
		         * 1. 该节点是后加的，如：加签
		         * 2. 不是模板(自由协同)
		         * 3. 竞争执行 
		         * 4. 全体执行
		         */
	        	continue;
	        }else{
	        	List<PersonInfo> people = new ArrayList<PersonInfo>(users.size());
	            for (com.seeyon.v3x.common.organization.sharemodel.User user : users) {
	                PersonInfo p = new PersonInfo();
	                p.setId(user.getId());
	                p.setName(user.getName());
	                people.add(p);
	            }
	            if(people.size() > 1){
	            	followUpMap.put("isPop", "true");
	        		return true;
	        	}
	            //对表单控件的处理
	        	if("FormField".equals(actor.getParty().getType().id)){
	        		String id = "" ;
	        		String display = "" ;
	        		if(!"start".equals(current_node.getId()) && fieldDataBaseMap != null 
	        				&& Strings.isBlank(id) && Strings.isBlank(display)){
	            		String str[] =	fieldDataBaseMap.get(actor.getParty().getId()) ;
	            		if(str != null){
	            			id = str[1] ;
	            			display = str[0] ;
	            		}
	        		} 
	        		if(Strings.isNotBlank(id) && Strings.isNotBlank(display)){
	        			continue;
	        		}else{//表单控件无人，需要进行选人
	        			followUpMap.put("isPop", "true");
	        			return true;
	        		}
	        	}else{
	        		if(people.size() < 1){
		            	followUpMap.put("isPop", "true");
		        		return true;
		        	}
	        	}
	        }
		}
		if(tempInformNodes.size()>0){//还有知会节点
		    allSelectInformNodes.addAll(new ArrayList<String>(tempInformNodes.keySet()));
			boolean result= handleAllInformNodes(tempInformNodes,allSelectInformNodes,current_node,allSelectNodeList,context,isFromTemplate,fieldDataBaseMap,orgManager,followUpMap);
			return result;
		}else{
			return false;
		}
	}

	public static boolean hasConditionOrSelectForSkipVerify(BPMActivity currentNode) throws Exception {
		Map followUpMap= new HashMap();
		getFollowUpDirectInfo(followUpMap,currentNode);
		List<String> allSelectInformNodes= new ArrayList<String>();
		Map allInformNodes=null;
		if(null!=followUpMap.get("allInformNodes")){
			allInformNodes= (Map)followUpMap.get("allInformNodes");
		}
		Map allNonInformNodes= null;
		if(null!=followUpMap.get("allNonInformNodes")){
			allNonInformNodes= (Map)followUpMap.get("allNonInformNodes");
		}
		Map allBranchConditions= null;
		if(null!= followUpMap.get("allBranchConditions")){
			allBranchConditions= (Map)followUpMap.get("allBranchConditions");
		}
		Map invalidateActivityMap= null;
		if(null!=followUpMap.get("invalidateActivityMap")){
			invalidateActivityMap= (Map)followUpMap.get("invalidateActivityMap");
		}
		boolean isAllInform= true;//用来标志是否后续直接人工活动都是知会节点
		boolean isHasCondtion= false;//用来标志当前节点之后是否带有分支条件
		boolean isHasNeedSelectPepole= false;//用来标志当前节点后续直接人工活动节点是否需要选人
		if( (allBranchConditions!=null && allBranchConditions.size() >0 ) ){//有分支条件
			isHasCondtion= true;
		}
		//判断是否存在不可用的节点，如果存在则进行如下处理
    	if(invalidateActivityMap != null && !invalidateActivityMap.isEmpty()){//存在不可用的人员（人员离职）
    		isHasNeedSelectPepole= true;
    	}
    	if(!isHasCondtion && !isHasNeedSelectPepole){//后续直接人工活动没有分支条件，也不需要选人
    		if((null==allNonInformNodes && null==allInformNodes)
    				||(null!=allNonInformNodes && allNonInformNodes.size()<=0 
    						&& null!=allInformNodes && allInformNodes.size()<=0)){//当前节点之后为结束节点
    			return false;
    		}else if(null!= allInformNodes && allInformNodes.size()>0){//当前节点之后有知会节点，就得往后遍历
    			//以当前节点所有后续知会节点为起点，继续递归往后遍历
    			List allSelectNodeList= new ArrayList();
    			allSelectNodeList.addAll(allInformNodes.keySet());
    			if(null!= allNonInformNodes){
    				allSelectNodeList.addAll(new ArrayList(allNonInformNodes.keySet()));
    				allSelectInformNodes.addAll(new ArrayList(allNonInformNodes.keySet()));
    			}
    			boolean result= handleAllInformNodes(allInformNodes,allSelectInformNodes,currentNode,allSelectNodeList);
    			return result;
    		}else{//其他情况
    			return false;
    		}
        }else{
        	return true;
        }
	}

	private static boolean handleAllInformNodes(Map allInformNodes,List<String> allSelectInformNodes,
			BPMActivity currentNode, List allSelectNodeList) throws Exception {
		List<String> allNotSelectNodeList= new ArrayList<String>();
		HashMap<String,String> conditonMap= new HashMap<String, String>();
		HashMap<String,String> nodeTypes= new HashMap<String, String>();
		List<BPMHumenActivity> allChildren= new ArrayList<BPMHumenActivity>();
		List<String> allChildrenList= new ArrayList<String>();
		//循环对informNodeList进行处理
		Iterator keyIter= allInformNodes.keySet().iterator();
		List informNodeList= new ArrayList(allInformNodes.keySet());
		Map context= new HashMap();
		for (; keyIter.hasNext();) {
			String currentInformNodeId = (String) keyIter.next();
			BPMActivity currentInformNode = (BPMActivity)allInformNodes.get(currentInformNodeId);
			List<BPMHumenActivity> children =
				ColHelper.findDirectHumenChildrenOfInformNode(
						currentNode,
						currentInformNode,
						currentInformNode, 
						conditonMap,
						nodeTypes,
						allNotSelectNodeList,
						allSelectNodeList,
						informNodeList,
						allSelectInformNodes,
						context,
						allChildrenList);
			if(conditonMap.size()>0) {//可能有分支条件
				Map<String,Object> map = ColHelper.splitCondition(conditonMap);
				List<String> conditions = (List<String>)map.get("conditions");
				if(conditions==null || conditions.size()==0){
				}else{
					return true;
				}
			}
			if(children!=null){
				allChildren.addAll(children);
			}
		}
		Map tempInformNodes= new HashMap();
		for (BPMHumenActivity activity : allChildren) {
			if(ColHelper.isInform(activity)){
				tempInformNodes.put(activity.getId(),activity);//找出后续知会节点
			}
			allSelectNodeList.add(activity.getId());
			if(!"normal".equals(activity.isValid())){ //节点不可用
				return true;
			}
		}
		if(tempInformNodes.size()>0){//还有知会节点
		    allSelectInformNodes.addAll(new ArrayList<String>(tempInformNodes.keySet()));
			boolean result= handleAllInformNodes(tempInformNodes,allSelectInformNodes,currentNode,allSelectNodeList);
			return result;
		}else{
			return false;
		}
	}

	private static void getFollowUpDirectInfo(Map followUpMap,
			BPMAbstractNode currentNode) {
		//获得当前节点的down线集合
		List<BPMTransition> downs= currentNode.getDownTransitions();
		if(downs!= null && downs.size()>0 ){
			for (BPMTransition bpmTransition : downs) {
				//获得down线指向的节点
				BPMAbstractNode toNode= bpmTransition.getTo();
				//获得当前节点的节点类型
				NodeType nodeType= toNode.getNodeType();
				//System.out.println("nodeType:="+nodeType);
				if( nodeType.equals(NodeType.split) || nodeType.equals(NodeType.join) ){//split节点或join节点
					//继续查找后续人工活动节点
					getFollowUpDirectInfo(followUpMap,toNode);
				}else if( nodeType.equals(NodeType.humen) ){//humen节点
					String toNodePolicy = toNode.getSeeyonPolicy().getId();
					BPMHumenActivity hactivity= (BPMHumenActivity)toNode;
					if( "inform".equals(toNodePolicy) || "zhihui".equals(toNodePolicy) ){//知会节点
						Map allInformNodes= null;
						if( followUpMap.get("allInformNodes") != null ){
							allInformNodes= (Map)followUpMap.get("allInformNodes");
							if( allInformNodes.get(toNode.getId()) == null ){
								allInformNodes.put(toNode.getId(), toNode);
							}
						}else{
							allInformNodes= new HashMap();
							allInformNodes.put(toNode.getId(), toNode);
						}
						followUpMap.put("allInformNodes", allInformNodes);
					}else{//非知会节点
						Map allNonInformNodes= null;
						if( followUpMap.get("allNonInformNodes") != null ){
							allNonInformNodes= (Map)followUpMap.get("allNonInformNodes");
							if( allNonInformNodes.get(toNode.getId()) == null ){
								allNonInformNodes.put(toNode.getId(), toNode);
							}
						}else{
							allNonInformNodes= new HashMap();
							allNonInformNodes.put(toNode.getId(), toNode);
						}
						followUpMap.put("allNonInformNodes", allNonInformNodes);
					}
					//判断该人工活动节点的执行人员是否为不可用
					if(!"normal".equals(hactivity.isValid())){ //节点不可用
						Map invalidateActivityMap= null;
						if( followUpMap.get("invalidateActivityMap") != null ){
							invalidateActivityMap= (Map)followUpMap.get("invalidateActivityMap");
							invalidateActivityMap.put(hactivity.getId(), hactivity.getBPMAbstractNodeName());
						}else{
							invalidateActivityMap= new HashMap();
							invalidateActivityMap.put(hactivity.getId(), hactivity.getBPMAbstractNodeName());
						}
						followUpMap.put("invalidateActivityMap", invalidateActivityMap);
					}
				}
				//对down进行分支条件处理
				String currentCondition = bpmTransition.getFormCondition();
				Map allBranchConditions= null;
	        	if(followUpMap.get("allBranchConditions")==null){
	        		allBranchConditions= new HashMap();
	    		}else{
	    			allBranchConditions= (Map)followUpMap.get("allBranchConditions");
	    		}
				if(currentCondition != null && !"".equals(currentCondition.trim()) && !"null".equals(currentCondition.trim())){//有自动条件分支
		        	allBranchConditions.put(bpmTransition.getId(), currentCondition);
		        	followUpMap.put("allBranchConditions", allBranchConditions);
		        }else if(bpmTransition.getConditionType()==2){//有手动选择分支
		        	allBranchConditions.put(bpmTransition.getId(), "handCondition");
		        	followUpMap.put("allBranchConditions", allBranchConditions);
		        }
			}
		}
	}

	/**
	 * 判断指定分支currentLinkId在processXml流程定义文件中，指定分支之前是否有可以自动跳过的节点(穿过知会)
	 * @param currentTransaction
	 * @return
	 */
	public static String[] hasAutoSkipNodeBeforeSetCondition(BPMTransition currentTransaction) {
		String[] result= new String[2];
		if(null!=currentTransaction){
			BPMAbstractNode fromNode= currentTransaction.getFrom();
			NodeType nodeType= fromNode.getNodeType();//获得当前节点的节点类型
			if( nodeType.equals(NodeType.split) ){//split节点
				BPMTransition upOfSplit= (BPMTransition)fromNode.getUpTransitions().get(0);
				return hasAutoSkipNodeBeforeSetCondition(upOfSplit);
			}else if( nodeType.equals(NodeType.join) ){//join节点
				Map<String,Object> resultMap= hasAutoSkipNodeBeforeSetConditionForJoin(fromNode,0);
				result[0]= resultMap.get("resultBoolean").toString();
				result[1]= resultMap.get("autoSkipNodeName").toString();
				return result;
			}else if( nodeType.equals(NodeType.humen) ){//humen节点
				String fromNodePolicy = fromNode.getSeeyonPolicy().getId();
				BPMHumenActivity hactivity= (BPMHumenActivity)fromNode;
				if( "inform".equals(fromNodePolicy) || "zhihui".equals(fromNodePolicy) ){//知会节点
					BPMTransition upOfSplit= (BPMTransition)fromNode.getUpTransitions().get(0);
					return hasAutoSkipNodeBeforeSetCondition(upOfSplit);
				}else{//非知会节点
					String dealType= fromNode.getSeeyonPolicy().getDealTermType();
					String dealTerm= fromNode.getSeeyonPolicy().getdealTerm();//是否选择处理期限
					if( null!=dealType && "2".equals(dealType.trim()) 
							&& null!=dealTerm && !"".equals(dealTerm) && !"0".equals(dealTerm) ){//自动跳过节点
						result[0]= String.valueOf(true);
						result[1]= fromNode.getName();
						return result;
					}else{//遇到非知会节点，但没有设置为自动跳过，则可以设置分支条件。
						result[0]= String.valueOf(false);
						result[1]= fromNode.getName();
						return result;
					}
				}
			}else if( nodeType.equals(NodeType.start) ){//start节点
				result[0]= String.valueOf(false);
				result[1]= fromNode.getName();
				return result;
			}
		}
		return result;
	}

	/**
	 * 针对join节点做特殊处理
	 * @param fromNode
	 * @return
	 */
	private static Map<String,Object> hasAutoSkipNodeBeforeSetConditionForJoin(
			BPMAbstractNode fromNode,int level) {
		Map<String,Object> resultMap= new HashMap<String, Object>();
		List<BPMTransition> upsOfJoins= fromNode.getUpTransitions();
		boolean result= false;
		boolean hasNonInformNode= false;
		for (BPMTransition bpmTransition : upsOfJoins) {
			if(!result){
				BPMAbstractNode preFromNode= bpmTransition.getFrom();
				NodeType nodeType= preFromNode.getNodeType();//获得当前节点的节点类型
				if( nodeType.equals(NodeType.join) ){//join节点
					int newLevel= level+1;
					resultMap= hasAutoSkipNodeBeforeSetConditionForJoin(preFromNode,newLevel);
				}else if( nodeType.equals(NodeType.humen) ){//humen节点
					String fromNodePolicy = preFromNode.getSeeyonPolicy().getId();
					BPMHumenActivity hactivity= (BPMHumenActivity)preFromNode;
					if( "inform".equals(fromNodePolicy) || "zhihui".equals(fromNodePolicy) ){//知会节点
						BPMTransition upOfInfo= (BPMTransition)preFromNode.getUpTransitions().get(0);
						resultMap= hasAutoSkipNodeBeforeSetConditionOfInfo(upOfInfo,level);
					}else{//非知会节点
						String dealType= preFromNode.getSeeyonPolicy().getDealTermType();
						String dealTerm= preFromNode.getSeeyonPolicy().getdealTerm();
						if( null!=dealType && "2".equals(dealType.trim()) 
								&& null!=dealTerm && !"".equals(dealTerm) && !"0".equals(dealTerm) ){//自动跳过节点
							resultMap.put("resultBoolean", true);
							resultMap.put("splitNode", null);
							resultMap.put("autoSkipNodeName", preFromNode.getName());
						}else{
							resultMap.put("resultBoolean", false);
							resultMap.put("splitNode", null);
							resultMap.put("autoSkipNodeName", preFromNode.getName());
						}
						hasNonInformNode= true;
					}
				}
				result= (Boolean)resultMap.get("resultBoolean");
			}
		}
		if(!result && !hasNonInformNode){//都为知会节点，则继续往前查找
			Object splitObj= resultMap.get("splitNode");
			if( null!= splitObj ){
				BPMAbstractNode splitNode= (BPMAbstractNode)splitObj;
				BPMTransition preTransition= (BPMTransition)splitNode.getUpTransitions().get(0);
				if(level==0){//最外层
					String[] returnResult= hasAutoSkipNodeBeforeSetCondition(preTransition);
					resultMap.put("resultBoolean", returnResult[0]);
					resultMap.put("autoSkipNodeName", returnResult[1]);
				}else{//类似知会
					resultMap= hasAutoSkipNodeBeforeSetConditionOfInfo(preTransition,level);
				}
			}
		}
		return resultMap;
	}

	/**
	 * 针对知会节点处理
	 * @param currentTransaction
	 * @return
	 */
	private static Map<String,Object> hasAutoSkipNodeBeforeSetConditionOfInfo(BPMTransition currentTransaction,int level) {
		Map<String,Object> resultMap= new HashMap<String, Object>();
		resultMap.put("resultBoolean", false);
		resultMap.put("splitNode", null);
		resultMap.put("autoSkipNodeName", "");
		if(null!=currentTransaction){
			BPMAbstractNode fromNode= currentTransaction.getFrom();
			NodeType nodeType= fromNode.getNodeType();//获得当前节点的节点类型
			if( nodeType.equals(NodeType.split) ){//split节点
				resultMap.put("resultBoolean", false);
				resultMap.put("splitNode", fromNode);
				resultMap.put("autoSkipNodeName", fromNode.getName());
				return resultMap;
			}else if( nodeType.equals(NodeType.join) ){//join节点
				int newLevel= level+1;
				return hasAutoSkipNodeBeforeSetConditionForJoin(fromNode,newLevel);
			}else if( nodeType.equals(NodeType.humen) ){//humen节点
				String fromNodePolicy = fromNode.getSeeyonPolicy().getId();
				BPMHumenActivity hactivity= (BPMHumenActivity)fromNode;
				if( "inform".equals(fromNodePolicy) || "zhihui".equals(fromNodePolicy) ){//知会节点
					BPMTransition upOfSplit= (BPMTransition)fromNode.getUpTransitions().get(0);
					return hasAutoSkipNodeBeforeSetConditionOfInfo(upOfSplit,level);
				}else{//非知会节点
					String dealType= fromNode.getSeeyonPolicy().getDealTermType();
					String dealTerm= fromNode.getSeeyonPolicy().getdealTerm();
					if( null!=dealType && "2".equals(dealType.trim()) 
							&& null!=dealTerm && !"".equals(dealTerm) && !"0".equals(dealTerm) ){//自动跳过节点
						resultMap.put("resultBoolean", true);
						resultMap.put("splitNode", null);
						resultMap.put("autoSkipNodeName", fromNode.getName());
						return resultMap;
					}else{//遇到非知会节点，但没有设置为自动跳过，则可以设置分支条件。
						resultMap.put("resultBoolean", false);
						resultMap.put("splitNode", null);
						resultMap.put("autoSkipNodeName", fromNode.getName());
						return resultMap;
					}
				}
			}else if( nodeType.equals(NodeType.start) ){//start节点
				resultMap.put("resultBoolean", false);
				resultMap.put("splitNode", null);
				resultMap.put("autoSkipNodeName", fromNode.getName());
				return resultMap;
			}
		}
		return resultMap;
	}
}