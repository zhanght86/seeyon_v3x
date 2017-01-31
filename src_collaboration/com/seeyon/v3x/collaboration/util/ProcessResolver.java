package com.seeyon.v3x.collaboration.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMActor;
import net.joinwork.bpm.definition.BPMAndRouter;
import net.joinwork.bpm.definition.BPMEnd;
import net.joinwork.bpm.definition.BPMHumenActivity;
import net.joinwork.bpm.definition.BPMParticipant;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMTransition;
import net.joinwork.bpm.definition.BPMAbstractNode.NodeType;

import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;

/**
 * @author HaoHouCheng
 * @date 2012-09-04
 *
 */
public class ProcessResolver {
	
	private final static Log log = LogFactory.getLog(ProcessResolver.class);

	private static ProcessResolver instance = null;
	
	private ProcessResolver(){}
	
	public static synchronized ProcessResolver getInstance(){
		if(instance == null)
			instance = new ProcessResolver();
		return instance;
	}
	
	public UniqueList<BPMHumenActivity> getAllActivity(FlowData flowData,User user,Long summaryId,String excutedConditionPrevActivityId,Long senderId,boolean isStart,OrgManager orgManager) throws NumberFormatException, BusinessException{
		BPMProcess myprocess = flowData.toBPMProcess();
//    	log.info(flowData.getXml());
        List<BPMTransition> links = myprocess.getLinks();
        
        String from = "start";
        String to = "end";
        
        UniqueList<BPMHumenActivity> nodes = new UniqueList<BPMHumenActivity>();
        List<BPMAndRouter> splitList = new ArrayList<BPMAndRouter>();
        nodes = getNodesByRecursionLinks(from,to,links,nodes,splitList,excutedConditionPrevActivityId,summaryId,flowData.getCondition(),isStart,myprocess);
		return nodes;
	}
	
	public List<Map<BPMHumenActivity,List<V3xOrgMember>>> getAllActivityWithMembersInProcess(FlowData flowData,User user,Long summaryId,String excutedConditionPrevActivityId,Long senderId,boolean isStart,OrgManager orgManager) throws NumberFormatException, BusinessException{
		BPMProcess myprocess = flowData.toBPMProcess();
//    	log.info(flowData.getXml());
        List<BPMTransition> links = myprocess.getLinks();
        
        String from = "start";
        String to = "end";
        
        UniqueList<BPMHumenActivity> nodes = new UniqueList<BPMHumenActivity>();
        List<BPMAndRouter> splitList = new ArrayList<BPMAndRouter>();
        nodes = getNodesByRecursionLinks(from,to,links,nodes,splitList,excutedConditionPrevActivityId,summaryId,flowData.getCondition(),isStart,myprocess);
        List<Map<BPMHumenActivity,List<V3xOrgMember>>> activityIdAndMembers = new ArrayList<Map<BPMHumenActivity,List<V3xOrgMember>>>();
        int nodeNum = 0;
        
        for(BPMHumenActivity activity: nodes){
    		String activityId = activity.getId();
        	List<BPMActor> actors = activity.getActorList();
        	if(actors != null && actors.size() > 0){
        		BPMParticipant party = actors.get(0).getParty();
            	String partyType = party.getType().id;
            	String partyId = party.getId();
            	String accountId = party.getAccountId();
            	
            	String[] currentActor = new String[]{partyType,partyId,accountId,activityId};
            	Map<BPMHumenActivity,List<V3xOrgMember>> prevActivityIdAndMembersMap = null;
            	if(nodeNum > 0)
            		prevActivityIdAndMembersMap = activityIdAndMembers.get(nodeNum - 1);
            	
            	Map<BPMHumenActivity,List<V3xOrgMember>> activityIdAndMembersMap = getActivityIdAndMembersMap(flowData,currentActor,user,activity,summaryId,prevActivityIdAndMembersMap,senderId,orgManager);
            	activityIdAndMembers.add(activityIdAndMembersMap);
            	
            	nodeNum++;
        	}
        	
        }
        
        return activityIdAndMembers;
	}
	
	/**
     * 将流程中的节点排序
     * @param from
     * @param to
     * @param links
     * @param nodes
     * @param splitList
     * @param isConditionAsEnd
     * @return
     */
    private UniqueList<BPMHumenActivity> getNodesByRecursionLinks(String from,String to,List<BPMTransition> links,UniqueList<BPMHumenActivity> nodes,List<BPMAndRouter> splitList,String excutedConditionPrevActivityId,Long summaryId,Map<String, String> condition,boolean isStart, BPMProcess process){
    	String tempFrom = "";
    	
    	for(BPMTransition tran : links){
    		
        	if(tran.getFrom().getId().equals(from)){

        		Object o = tran.getTo();
        		
        		if(o instanceof BPMAndRouter){
        			BPMAndRouter router = (BPMAndRouter)o;
        			String name = router.getName();
        			tempFrom = router.getId();
        			if(name.equalsIgnoreCase("split") || name.equalsIgnoreCase("join"))
        				splitList.add(router);
        			
        		}else if(o instanceof BPMEnd){
        			tempFrom = "end";
        			break;
        		}else if(o instanceof BPMHumenActivity){
        			BPMHumenActivity activity = (BPMHumenActivity)o;
        			//若分支条件已经选择过，未选中的分支中的节点为(isDelete="true")，需要过滤掉未选中的分支。
        			if(!Boolean.parseBoolean(activity.getSeeyonPolicy().getIsDelete())){
        				nodes.add(activity);
        				tempFrom = activity.getId();
        			}
        			
        		}
        	}
        }
    	
    	from = tempFrom;
    	
    	if(splitList != null && splitList.size() > 0){
    		
    		List<String> sFrom = new ArrayList<String>();
    		sFrom.add(from);
    		String sTo = to;
    		List<String> joinLinkIds = new ArrayList<String>();
    		
    		joinLinkIds = getNodesByRecursionLinksInSplit(sFrom,sTo,joinLinkIds,links,nodes,splitList,excutedConditionPrevActivityId,summaryId,condition,isStart,process);
    		
    		if(joinLinkIds.size() == 1){
    			from = joinLinkIds.get(0);
    		}
    		
    	}
    	
    	if(!from.equals(to)){
    		getNodesByRecursionLinks(from,to,links,nodes,new ArrayList<BPMAndRouter>(),excutedConditionPrevActivityId,summaryId,condition,isStart,process);
		}
        
    	return nodes;
    }
    
    /**
     * 遇到分支时，分支内单独遍历排序
     * @param sfrom
     * @param sto
     * @param joinLinkId
     * @param links
     * @param nodes
     * @param splitList
     * @param excutedConditionPrevActivityId
     * @param continueRecursionFlag
     * @param summaryId
     * @param condition
     * @param isStart
     * @return
     */
    private List<String> getNodesByRecursionLinksInSplit(List<String> from, String to, List<String> joinLinkId, List<BPMTransition> links, UniqueList<BPMHumenActivity> nodes, List<BPMAndRouter> splitList, String excutedConditionPrevActivityId, Long summaryId, Map<String, String> condition, boolean isStart, BPMProcess process){
    	String joinLink = "";
    	
    	List<BPMAndRouter> newSplitList = new ArrayList<BPMAndRouter>();
    	List<String> nextFrom = new UniqueList<String>();
    	//以下的循环内，如果满足了(刚刚选择完毕分支条件，继续往下查询的情况)，将isNextCondition=true.
    	//然后在循环外continueRecursionFlag=true.表示下次遇到分支条件时，在下次的分支条件处为止。如果后面没有分支条件，则一直查询到终点为止。
    	boolean isNextCondition = false;
    	
    	if(splitList != null && splitList.size() > 0){
    		outer:
    		for(BPMAndRouter Router : splitList)
    		{
    			List<BPMTransition> splitDownLinks = Router.getDownTransitions();
    			
    			for(BPMTransition tran : splitDownLinks)
    			{
    				fromOuter:
    				for(String strFrom : from)
    				{
    					if(tran.getFrom().getId().equals(strFrom))
    					{
    						Object fromObj = tran.getFrom();
    						Object toObj = tran.getTo();
    						
    						if(Router.getNodeType() == BPMAbstractNode.NodeType.join){ //join时直接取下一节点
    							
    							if(toObj instanceof BPMEnd){
        		        			nextFrom.add("end");
        	        				joinLink = "end";
        		        		}else if(toObj instanceof BPMActivity){
        		        			nextFrom.add(((BPMActivity)toObj).getId());
        		        		}
    							
    							break outer;
    		    			}
    						
        					boolean isCondition = false;
        					List<BPMHumenActivity> parentHumanActivityList = new ArrayList<BPMHumenActivity>();
        					
        					/**
        					 * 情况1：发起流程时，以第一个条件节点为“终点”，分段计算。
        					 * 情况2：在节点处理过程中(分支条件选择后)，
        					 * 以该条件节点之前的所有节点 和 从该条件节点到下一个条件节点(特殊情况：下一个条件节点就是真正的终点)的所有节点计算。
        					 */
//        	        		if(tran.getConditionType() != 3 || 
//        	        				( tran.getFrom() != null && (tran.getFrom() instanceof BPMAndRouter) 
//        	    	        		  && (((BPMAndRouter)tran.getFrom()).getUpTransitions() != null 
//        	    	        		  && ((BPMAndRouter)tran.getFrom()).getUpTransitions().size() > 0)
//        	    	        		  && ((BPMTransition)((BPMAndRouter)tran.getFrom()).getUpTransitions().get(0)).getConditionType() != 3) ){
//        	        			
//        	        			isCondition = true;
//        	        			
//        	        			parentHumanActivityList = ColHelper.getParentHumens( ((BPMActivity)fromObj) );
//        	        			
//        	        			if(parentHumanActivityList.size() == 0 && Strings.isNotBlank(excutedConditionPrevActivityId) && isStart){ //父节点为空，表明条件节点的前一人工节点为发起节点
//        	        				isNextCondition = true;
//        	        			}else{
//        	        				
//        	        				for(BPMHumenActivity parentHumenActivity : parentHumanActivityList)
//        	        				{
//        	        					if(!Boolean.parseBoolean(parentHumenActivity.getSeeyonPolicy().getIsDelete())){
//        	        						
//        	        						String prevOfConditionActivityId = parentHumenActivity.getId(); //条件节点的父人工节点的id
//            	        					
//            	        					if(!Strings.isBlank(prevOfConditionActivityId)){
//                    	        				
//                    	    	        		//继本次条件节点往下查询直到下一次分支条件的情况已满足，本次操作需要做的事情：排除掉下一次分支条件的前一人工节点；退出递归。
//                    	    	        		if(continueRecursionFlagList.size() == 1 && continueRecursionFlagList.get(0)){
//                    	    	        			
//                	        						//删除掉第一个条件节点的前一人工节点
//                    	    	        			if(nodes != null && nodes.size() > 0){
//                    	    	        				
//                    	    	        				Iterator<BPMHumenActivity> iter = nodes.iterator();
//                    	        						while(iter.hasNext()){
//                    	        							BPMHumenActivity sortedActivity = iter.next();
//                    	        							if(parentHumenActivity != null && sortedActivity.getId().equals(prevOfConditionActivityId)){
//                    	        								ignoredActivityDao.save(new IgnoredActivity(UUIDLong.longUUID(),summaryId,Long.parseLong(sortedActivity.getId())));
//                    	        							//	iter.remove(); 兴业证券 修改分支bug 修改人张华荣
//                    	        							}
//                    	        								
//                    	        						}
//                    	        						
//                    	    	        			}
//                	        						
//                	        						continue;
//                	        						
//                    	    	        		}else{
//                    	    	        			
//                    	    	        			//发起流程时，到第一个条件处截止。并且需要从列表中排除掉第一个条件的前一人工节点
//                    	    	        			if(Strings.isBlank(excutedConditionPrevActivityId))
//                    	        					{
//                    	        						nextFrom.add("end");
//                    	        						joinLink = "end";
//                    	        						//删除掉第一个条件节点的前一人工节点
//                    	        						if(nodes != null && nodes.size() > 0){
//                    	        							
//                    	        							Iterator<BPMHumenActivity> iter = nodes.iterator();
//                        	        						while(iter.hasNext()){
//                        	        							
//                        	        							BPMHumenActivity sortedActivity = iter.next();
//                        	        							if(parentHumenActivity != null && sortedActivity.getId().equals(prevOfConditionActivityId)){
//                        	        								ignoredActivityDao.save(new IgnoredActivity(UUIDLong.longUUID(),summaryId,Long.parseLong(sortedActivity.getId())));
//                        	        						//		iter.remove(); 兴业证券 修改分支bug 修改人张华荣
//                        	        							}
//                        	        							
//                        	        						}
//                    	        						}
//                    	        						
//                    	        						break fromOuter; 
//                    	        					}
//                    	    	        			else
//                    	    	        			{
//                    	    	        				
//                    	    	        				/**
//                        	        					 * 刚刚处理分支条件选择后，此时的计算情况有2种：
//                        	        					 * 1.以条件节点的前一人工节点之前，和条件节点的前一人工节点之后直到下一次条件节点的前一人工节点为止的所有节点。
//                        	        					 *   下一次条件节点的前一人工节点不能包含在返回列表之内。(特殊情况：下一次条件节点的前一人工节点就是本次条件节点，当然更不能包含在返回列表之内。)
//                        	        					 * 2.以条件节点的前一人工节点之前，和条件节点的前一人工节点之后直到终点为止的所有节点。
//                        	        					 */
//                    	        						if(prevOfConditionActivityId.equals(excutedConditionPrevActivityId)) {
//                        	        						isNextCondition = true;
//                        	        					}else
//                        	        						continue;
//                        	        						
//                    	    	        			}
//                    	        					
//                    	    	        		}
//                    	        			}
//        	        						
//        	        					}
//        	        					
//        	        				}
//        	        				
//        	        			}
//        	        			
//        	        		}
        	        		
//        	        		//在不是发起流程时遇到的第一个条件处时，应该让上面的parentHumanActivityList循环完(主要是排除第二次条件分支的前一节点，这个很重要)，再跳转到fromOuter继续循环。否则会漏掉多个分支同时进行的情况。
//        	        		if(continueRecursionFlagList.size() == 1 && continueRecursionFlagList.get(0)){
//        	        			nextFrom.add("end");
//    	        				joinLink = "end";
//	    	        			
//        	        			continue fromOuter;
//        	        		}
        	        		
        	        		List<BPMHumenActivity> childHumenActivityList = new UniqueList<BPMHumenActivity>();
    		        		
    		        		if(fromObj instanceof BPMActivity){ //不能使用toObj，因为这样会取到孩子的孩子。
    		        			
    		        			childHumenActivityList = ColHelper.getChildHumens( (BPMActivity)fromObj );
        		        		
        		        		if(childHumenActivityList != null && childHumenActivityList.size() > 0){
        		        			for(BPMHumenActivity childHumenActivity : childHumenActivityList)
        		        			{
        		        				
//        		        				BPMHumenActivity activity = (BPMHumenActivity)o;
            		        			/* 若分支条件已经选择过，未选中的分支中的节点为(isDelete="true")，需要过滤掉未选中的分支。
            		        			*	或者当发起时就有条件分支，则根据condition过滤。
            		        			*/
            		        			if(isStart && condition != null && !condition.isEmpty() && isCondition){
            		        				boolean isDelete = Boolean.parseBoolean(condition.get(childHumenActivity.getId()));
            		        				
            		        				if(!isDelete){
            		        					nodes.add(childHumenActivity);
            		        					nextFrom.add(childHumenActivity.getId());
            		        				}
            		        			}else{
            		        				if(!Boolean.parseBoolean(childHumenActivity.getSeeyonPolicy().getIsDelete())){
            	    	        				nodes.add(childHumenActivity);
                	        					nextFrom.add(childHumenActivity.getId());
            	    	        			}
            	        				}
            		        			
        		        			}
        		        		}else{ //后面没有人工节点，证明后面就是终点了。
        		        			nextFrom.add("end");
        	        				joinLink = "end";
        		        		}
        		        		
    		        		}
    		        		
        				}
    					
    				}
    				
    			}
    		}
    		
//			if(continueRecursionFlagList.size() == 1 && continueRecursionFlagList.get(0)){
//				continueRecursionFlagList = new ArrayList<Boolean>();
//			}
    	
    	}else{
    		
        	for(BPMTransition tran : links)
        	{
        		
        		for(String strFrom : from)
        		{
        			
        			if(tran.getFrom().getId().equals(strFrom)){
        				
        				String tempFrom = "";
                		Object o = tran.getTo();

                		if(o instanceof BPMAndRouter){
                			BPMAndRouter router = (BPMAndRouter)o;
                			String name = router.getName();
                			tempFrom = router.getId();
                			
                			if(!Boolean.parseBoolean(router.getSeeyonPolicy().getIsDelete())){
                				
                				if(name.equalsIgnoreCase("split")){
                    				newSplitList.add(router);
                    			}else if(name.equalsIgnoreCase("join")){
                    				joinLink = router.getId();
                    			}
                			}
                			
                		}else if(o instanceof BPMEnd){
                			tempFrom = "end";
                			joinLink = "end";
                			nextFrom.add(tempFrom);
                			break;
                		}else if(o instanceof BPMHumenActivity){
                			BPMHumenActivity activity = (BPMHumenActivity)o;
                			//若分支条件已经选择过，未选中的分支中的节点为(isDelete="true")，需要过滤掉未选中的分支。
                			if(!Boolean.parseBoolean(activity.getSeeyonPolicy().getIsDelete())){
                				nodes.add(activity);
                				tempFrom = activity.getId();
                			}
                			
                		}
                		
                		if(!Strings.isBlank(tempFrom))
                			nextFrom.add(tempFrom);
                	}
        			
                }
        			
    		}
        	
    	}
    	
    	
//    	if(isNextCondition)
//    		continueRecursionFlagList.add(true);
    	
    	if(nextFrom.size() > 0 && !nextFrom.contains(joinLink)){
    		getNodesByRecursionLinksInSplit(nextFrom,to,joinLinkId,links,nodes,newSplitList,excutedConditionPrevActivityId,summaryId,condition,isStart,process);
		}else if(nextFrom.size() == 0 && Strings.isBlank(joinLink)){
			joinLink = "end";
			joinLinkId.add(joinLink);
		}else{
			joinLinkId.add(joinLink);
		}

    	return joinLinkId;
    }
    
    /**
     * 将流程中所有的节点解析为member
     * @param flowData
     * @param user
     * @return
     * @throws DocumentException
     * @throws NumberFormatException
     * @throws BusinessException
     */
    private Map<BPMHumenActivity,List<V3xOrgMember>> getActivityIdAndMembersMap(FlowData flowData, String[] currentActor,User user,BPMHumenActivity activity, Long summaryId, Map<BPMHumenActivity,List<V3xOrgMember>> prevActivityIdAndMembersMap,Long senderId,OrgManager orgManager) throws NumberFormatException, BusinessException{
		
    	Map<BPMHumenActivity,List<V3xOrgMember>> activityIdAndMembersMap = new HashMap<BPMHumenActivity,List<V3xOrgMember>>();
		List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
		boolean isSecondNode = false; //是否为第二个节点
		List<BPMTransition> nexts = activity.getDownTransitions();
    	List<BPMHumenActivity> parentHumanActivityList = new ArrayList<BPMHumenActivity>();
    	List<BPMHumenActivity> childHumenActivityList = new UniqueList<BPMHumenActivity>();
    	
    	//已经指定过节点，直接根据Addition解析member
    	if(flowData.getAddition() != null){
			
			String[] ids = ((String[])flowData.getAddition().get(currentActor[3]));
			if(ids != null && ids.length > 0)
				for(int i=0; i<ids.length; i++){
					V3xOrgMember member = orgManager.getMemberById(Long.parseLong(ids[i]));
					members.add(member);
				}
			
		}
    	
    	if(members.size() == 0){
			
			if(currentActor[0].equals(V3xOrgEntity.ORGENT_TYPE_ROLE)){
				/**
				 * 上一节点(recusionMemberList)为空的情况有：
				 * 1.当前节点为发起人
				 * 2.上一节点是手动指定的，如发起者部门负责人(Role)，在部门设置中未指派的情况下
				 */
				if(nexts != null && nexts.size() > 0){
					for(BPMTransition tran : nexts){
						
						Object fromObj = tran.getFrom();
						Object toObj = tran.getTo();
						List<V3xOrgMember> tempMembers = null;
						
						parentHumanActivityList = ColHelper.getParentHumens( ((BPMActivity)fromObj) );
						
						if(parentHumanActivityList == null || parentHumanActivityList.size() == 0 || tran.getFrom().getId().equals("start")){
							isSecondNode = true;
						}
						
						if(!currentActor[1].equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER) && currentActor[1].contains(V3xOrgEntity.ORGENT_META_KEY_SEDNER)){ //发起者 什么什么的角色。
							tempMembers = orgManager.parseMember4RelativeRole(currentActor[1], V3xOrgEntity.ORGENT_TYPE_MEMBER, String.valueOf(senderId), Long.parseLong(currentActor[2]));
							
						}else if(currentActor[1].equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER)){ //发起者
							V3xOrgMember member = orgManager.getMemberById(senderId);
							if(member != null)
								members.add(member);
							
						}else{
							if(isSecondNode){

								tempMembers = orgManager.parseMember4RelativeRole(currentActor[1], V3xOrgEntity.ORGENT_TYPE_MEMBER, String.valueOf(user.getId()), Long.parseLong(currentActor[2]));

							}else{
								
								String preNodeType = ""; //上一节点类型
								String preNodeId = ""; //上一节点的类型id，不是节点activityId
								String parentHumanActivityId = "";
								
								if(parentHumanActivityList != null && parentHumanActivityList.size() > 0)
									for(BPMHumenActivity parentHumanActivity : parentHumanActivityList){
										
										if(!Boolean.parseBoolean(parentHumanActivity.getSeeyonPolicy().getIsDelete())){
											
											parentHumanActivityId = parentHumanActivity.getId();
											String tempPreNodeType = "";
											
											if(parentHumanActivity != null && parentHumanActivity.getActorList() != null && parentHumanActivity.getActorList().size() > 0){
												BPMActor prevActor = (BPMActor)parentHumanActivity.getActorList().get(0);
												BPMParticipant prevNodePary = prevActor.getParty();
												preNodeType = prevNodePary.getType().id;
												
												tempPreNodeType = preNodeType;
												if(!Strings.isBlank(preNodeType) && preNodeType.equalsIgnoreCase("user"))
													tempPreNodeType = V3xOrgEntity.ORGENT_TYPE_MEMBER;
												preNodeId = prevNodePary.getId();
											}
								            	
											tempMembers = orgManager.parseMember4RelativeRole(currentActor[1], tempPreNodeType, preNodeId, Long.parseLong(currentActor[2]));
											
										}
										
									}
								
								if((tempMembers == null || tempMembers.size() == 0) && prevActivityIdAndMembersMap != null){
									
									tempMembers = new ArrayList<V3xOrgMember>();
									List<V3xOrgMember> prevNodeMembers = prevActivityIdAndMembersMap.get(parentHumanActivityId);
									if(prevNodeMembers != null && prevNodeMembers.size() > 0)
										for(V3xOrgMember m : prevNodeMembers){
											List<V3xOrgMember> stempMembers = orgManager.parseMember4RelativeRole(currentActor[1], V3xOrgEntity.ORGENT_TYPE_MEMBER, String.valueOf(m.getId()), Long.parseLong(currentActor[2]));
											if(stempMembers != null && stempMembers.size() > 0)
												tempMembers.addAll(stempMembers);
										}
										
								}
								
							}
								
						}
						
						if(tempMembers != null)
							members.addAll(tempMembers);
					}
				}
			}else{
				members = orgManager.parseMember(currentActor[0], currentActor[1], currentActor[2]);
			}
			
		}
		
//		activityIdAndMembersMap.put(currentActor[3], members);
    	activityIdAndMembersMap.put(activity, members);
			
    	return activityIdAndMembersMap;
    }
	
	
}
