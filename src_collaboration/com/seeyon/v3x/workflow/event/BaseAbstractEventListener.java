package com.seeyon.v3x.workflow.event;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.engine.event.AbstractEventListener;
import net.joinwork.bpm.engine.exception.BPMException;
import net.joinwork.bpm.engine.execute.ActionRunner;
import net.joinwork.bpm.engine.execute.BPMCase;
import net.joinwork.bpm.engine.wapi.ProcessDefManager;
import net.joinwork.bpm.engine.wapi.WAPIFactory;
import net.joinwork.bpm.engine.wapi.WorkItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.affair.webmodel.AffairData;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.manager.impl.ColMessageHelper;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.edoc.domain.EdocBody;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocMessageHelper;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.worktimeset.exception.WorkTimeSetExecption;
import com.seeyon.v3x.worktimeset.manager.WorkTimeManager;

public abstract class BaseAbstractEventListener extends AbstractEventListener {
	private Log log= LogFactory.getLog(BaseAbstractEventListener.class);
    //回退
    public static final Integer WITHDRAW = 1;
    //取回
    public static final Integer TAKE_BACK = 2;
    //知会
    public static final Integer ADD_INFORM = 3;
    //会签
    public static final Integer COL_ASSIGN = 4;
    //加签
    public static final Integer INSERT = 5;
    //减签
    public static final Integer DELETE = 6;
    //分配任务
    public static final Integer ASSIGN = 7;
    
    //终止
    public static final Integer STETSTOP = 8;
    
    //正常处理
    public static final Integer COMMONDISPOSAL = 9;
    //撤销
    public static final Integer CANCEL = 10;
    //暂存待办
    public static final Integer ZCDB = 11;
    //默认删除操作: 督办替换节点，自动流程复合节点的单人执行
    public static final Integer AUTODELETE = 12;
    //自动跳过
    public static final Integer AUTOSKIP = 13;
    
    /**
     * 取得bean
     * @param beanName
     * @return
     */
    //protected abstract Object getBean(String beanName);
    
    /**
     * 获取应用类型
     * @return
     */
    protected abstract int getApp();
    
    protected abstract Log getLog();
    
    protected boolean onWorkitemAssigned(Map eventData) throws BPMException {
    	List<WorkItem> workitems = (List<WorkItem>)(((Map)eventData.get(ActionRunner.SYSDATA_EXTEND_DATA)).get("workitems"));
    	try {
    		//控制是否发送协同发起消息
			Boolean _isSendMessage = (Boolean)eventData.get("isSendMessage");
			Boolean isSendMessage = true;
			if(_isSendMessage != null && !_isSendMessage) isSendMessage = false;
			
			BPMCase theCase = (BPMCase) eventData.get(ActionRunner.SYSDATA_CASE);
	        long caseId = theCase.getId();
	        
	        String processId = theCase.getProcessId();
	        BPMProcess _process = (BPMProcess) eventData.get("real_process");
	        if(_process == null){
	        	try{
	        		ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
	        		_process = (BPMProcess)pdm.getProcessInReady("admin", processId);
	        		if(_process == null){
	        			try{
	        				_process = ColHelper.getRunningProcessByProcessId(processId);
	        			}catch(ColException e){
	        				throw new ColException("获取runningProcess异常[processId ="+ processId+ "]", e);
	        			}
	        		}
	        	}catch(BPMException e){
	        		getLog().error("获取流程定义管理接口对象失败", e);
	        		throw new BPMException("获取流程定义管理接口对象失败", e);
	        	}
	        }
		        
	        BPMActivity activity = (BPMActivity)eventData.get("real_activity");
	        BPMSeeyonPolicy seeyonPolicy = activity.getSeeyonPolicy();
	        
	        Long deadline = null;
	        Long remindTime = null;
	        String dealTermType= null;
	        String dealTermUserId= null;
	        if(!"".equals(seeyonPolicy.getdealTerm()) && seeyonPolicy.getdealTerm() != null 
	                && !"null".equals(seeyonPolicy.getdealTerm()) 
	                && !"undefined".equals(seeyonPolicy.getdealTerm()) ){
	        	deadline = Long.parseLong(seeyonPolicy.getdealTerm());
	        	if( null != seeyonPolicy.getDealTermType() && !"".equals(seeyonPolicy.getDealTermType().trim()) ){
	        		dealTermType= seeyonPolicy.getDealTermType().trim();
	        	}else{
	        		dealTermType= "0";
	        	}
	        	if( null != seeyonPolicy.getDealTermUserId() && !"".equals(seeyonPolicy.getDealTermUserId().trim())){
	        		dealTermUserId= seeyonPolicy.getDealTermUserId();
	        	}else{
	        		dealTermUserId= "-1";
	        	}
	        }
	        if(!"".equals(seeyonPolicy.getRemindTime()) && seeyonPolicy.getRemindTime() != null
	        		&& !("undefined").equals(seeyonPolicy.getRemindTime()) 
	        		&& !("null").equals(seeyonPolicy.getRemindTime())){
	        	remindTime = Long.parseLong(seeyonPolicy.getRemindTime());
	        }   
	        Timestamp now = new Timestamp(System.currentTimeMillis());
	        
	        Long senderId = null;
	        Long objectId = null;
	        java.sql.Timestamp createDate = null;
	        ColBody colBody = null;
	        EdocBody edocBody = null;
	        Boolean isHasAttachments = null;
	        ColSummary colSummary = null;
	        EdocSummary edocSummary = null;
	        boolean isEdoc = this.isEdoc();
	        boolean isColl = this.isColl();
	        Long summaryAccountId = null;
	        
	        if(isColl){
	        	colSummary = (ColSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
	        	if(colSummary == null){
		            try {
		            	ColManager colManager = (ColManager)getBean("colManager");
		            	colSummary = colManager.getSummaryByCaseId(caseId);
		                DateSharedWithWorkflowEngineThreadLocal.setColSummary(colSummary);
		            } catch (Exception e) {
		                throw new BPMException("获取流程对应的协同对象失败", e);
		            }
		        }
	        	senderId = colSummary.getStartMemberId();
	        	objectId = colSummary.getId();
	        	isHasAttachments = colSummary.isHasAttachments();
	        	createDate = colSummary.getCreateDate();
	        	colBody = colSummary.getFirstBody();
	        	summaryAccountId = colSummary.getOrgAccountId();
	        }
	        else if(isEdoc){
	        	//这块有些问题，虽然不会出错，以后应该分开
	        	edocSummary = (EdocSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
	        	if(edocSummary == null){
		            try {
		            	EdocManager edocManager = (EdocManager)getBean("edocManager");
		            	edocSummary = edocManager.getSummaryByCaseId(caseId);
		                DateSharedWithWorkflowEngineThreadLocal.setColSummary(edocSummary);
		            } catch (Exception e) {
		            	throw new BPMException("获取流程对应的公文对象失败", e);
		            }
		        }
	        	senderId = edocSummary.getStartUserId();
	        	objectId = edocSummary.getId();
	        	createDate = edocSummary.getCreateTime();       
	        	edocBody=edocSummary.getFirstBody();
	        	isHasAttachments = edocSummary.isHasAttachments();
	        	summaryAccountId = edocSummary.getOrgAccountId();
	        }
	        
	        //生成affair
	        List<Affair> affairs = new ArrayList<Affair>(workitems.size());
	        Integer subApp;
	        for (WorkItem workitem : workitems) {
	        	Long memberId = Long.parseLong(workitem.getPerformer());
    			
	            Affair affair = new Affair();
	            affair.setIdIfNew();
	            
	            affair.setIsTrack(false);
	            affair.setIsDelete(false);
		    	affair.setSubObjectId(new Long(workitem.getId()));
		    	affair.setMemberId(memberId);
		    	affair.setState(StateEnum.col_pending.key());
		    	affair.setSubState(SubStateEnum.col_pending_unRead.key());
				affair.setSenderId(senderId);
				affair.setSubject(isColl?colSummary.getSubject():edocSummary.getSubject());
				affair.setObjectId(objectId);
				affair.setDeadlineDate(deadline);
				try{
					affair.setDealTermType(Integer.parseInt(dealTermType));
				}catch(Throwable e){
//					log.warn(e.getMessage());
					affair.setDealTermType(0);
				}
				try{
					affair.setDealTermUserId(Long.parseLong(dealTermUserId));
				}catch(Throwable e){
//					log.warn(e.getMessage());
					affair.setDealTermUserId(-1l);
				}
				affair.setRemindDate(remindTime);
				affair.setReceiveTime(now);
				affair.setApp(isColl?ApplicationCategoryEnum.collaboration.key():EdocUtil.getAppCategoryByEdocType(edocSummary.getEdocType()).key());
				//branches_a8_v350_r_gov GOV-4020 唐桂林 在待分发列表中只勾选一个公文流程选择阅文批处理，结果把2个流程都不见了。已分发里面也没有记录了 start
				if(isEdoc && (Boolean)SysFlag.is_gov_only.getFlag()) {
					subApp = -1;
		            if(ApplicationCategoryEnum.edocRec.getKey() == affair.getApp() && null != edocSummary.getProcessType()){
		            	if(2 == edocSummary.getProcessType().intValue()){
		            		subApp = ApplicationSubCategoryEnum.edocRecRead.getKey();
		            	}else if(1== edocSummary.getProcessType().intValue()){
		            		subApp = ApplicationSubCategoryEnum.edocRecHandle.getKey();
		            	}
		            }
		            affair.setSubApp(subApp);
				}
				//branches_a8_v350_r_gov GOV-4020 唐桂林 在待分发列表中只勾选一个公文流程选择阅文批处理，结果把2个流程都不见了。已分发里面也没有记录了 end
				affair.setCreateDate(createDate);
				affair.setIsSendMessage(isSendMessage);
				affair.setTempleteId(isColl?colSummary.getTempleteId():edocSummary.getTempleteId());
				
				if(isColl){
					affair.setImportantLevel(colSummary.getImportantLevel());
					affair.setResentTime(colSummary.getResentTime());
					affair.setForwardMember(colSummary.getForwardMember());
					affair.setFormReadonly("1".equals(seeyonPolicy.getFR()));
				}else if(isEdoc && edocSummary.getUrgentLevel()!=null&&!"".equals(edocSummary.getUrgentLevel())){
//					公文紧急程度
					affair.setImportantLevel(Integer.parseInt(edocSummary.getUrgentLevel()));
				}
				
				if(Strings.isNotBlank(seeyonPolicy.getAddedFromId())){
					affair.setFromId(Long.parseLong(seeyonPolicy.getAddedFromId()));
				}
				
				affair.setBodyType(isColl?colBody.getBodyType():edocBody.getContentType());
				affair.setHasAttachments(isHasAttachments);
				
				affair.setNodePolicy(seeyonPolicy.getId());
				affair.setActivityId(Long.parseLong(workitem.getActivityId()));
				affair.setFormAppId(seeyonPolicy.getFormApp());
				affair.setFormId(seeyonPolicy.getForm());
				affair.setFormOperationId(seeyonPolicy.getOperationName());
				
				affair.serialExtProperties();
//				回退导致新生成的事项
				if(!isSendMessage){
					DateSharedWithWorkflowEngineThreadLocal.addToAffairMap(memberId, affair.getId());
				}
				affairs.add(affair);
	        }
	        
	        AffairData affairData = new AffairData();
	        
	        affairData.setAffairList(affairs);
	        affairData.setIsSendMessage(affairs.get(0).getIsSendMessage());
	        affairData.setSender(affairs.get(0).getSenderId());
	        affairData.setBodyType(isColl?colBody.getBodyType():edocBody.getContentType());
	        affairData.setBodyContent(isColl?colBody.getContent():edocBody.getContent());
	        affairData.setBodyCreateDate(isColl?colBody.getCreateDate():edocBody.getCreateTime());
	        affairData.setSummaryAccountId(summaryAccountId);
	        
	        try {
    			ListMapTask.run1(affairData);
			}
    		catch (Exception e) {
				getLog().error("", e);
			}
    	}catch (Exception e) {
			getLog().error(BPMException.EXCEPTION_CODE_DATA_FORMAT_ERROR, e);
			throw new BPMException(BPMException.EXCEPTION_CODE_DATA_FORMAT_ERROR, e);
		}
    	return false;
    }
    
    protected boolean onWorkitemCanceled(Map eventData) throws BPMException {
    	int operationType = DateSharedWithWorkflowEngineThreadLocal.getOperationType();
    	WorkItem workitem = (WorkItem) eventData.get(ActionRunner.SYSDATA_WORKITEM);
    	BPMActivity currentActivity = (BPMActivity) eventData.get("real_activity");
    	if(currentActivity == null){
    		currentActivity = ColHelper.getActivityByWorkitem(workitem);
    	}
    	
    	if(currentActivity == null){
    		DateSharedWithWorkflowEngineThreadLocal.setOperationType(AUTODELETE);
        	operationType = 12;
    	}
    	else{
	        BPMSeeyonPolicy policy = currentActivity.getSeeyonPolicy();
	        String processMode = policy.getProcessMode();
	        if((operationType == 9 || operationType == 11) && !"competition".equals(processMode)){
	        	DateSharedWithWorkflowEngineThreadLocal.setOperationType(AUTODELETE);
	        	operationType = 12;
	        }
	    	if(operationType== AUTOSKIP && "competition".equals(processMode)){
	    		operationType= 9;
	    	}
    	}
    	
    	Timestamp now = new Timestamp(System.currentTimeMillis());
    	AffairManager affairManager = (AffairManager)getBean("affairManager"); 
    		
    	if(operationType == TAKE_BACK || operationType == WITHDRAW){
    		List<WorkItem> workItems = (List<WorkItem>)eventData.get(ActionRunner.SYSDATA_EXTEND_DATA);
    		if(workItems != null){
    			Affair affair = affairManager.getBySubObject(Long.parseLong(workitem.getId()+""));
    			if (affair == null)
    	            return false;
    			
    			int MaxCommitNumber = 300;
    			int length = workItems.size();
    			List<Long> workitemIds = new ArrayList<Long>();
    			int i = 0;
    			int state = operationType == TAKE_BACK ? StateEnum.col_takeBack.key() : StateEnum.col_stepBack.key();
    			List<Affair>  affairs = affairManager.getALLAvailabilityAffairList(affair.getObjectId(),false);
    			Map<Long,Affair> m = new HashMap<Long,Affair>();
    			for(Affair af : affairs){
    				if(af.getSubObjectId()!=null){
    					m.put(af.getSubObjectId(), af);
    				}
    			}
    			for (WorkItem workItem0 : workItems) {
    				
    				if(m.keySet().contains(workItem0.getId())) {
    					Affair af = m.get(workItem0.getId());
    					DateSharedWithWorkflowEngineThreadLocal.addToAllStepBackAffectAffairMap(af.getMemberId(), af.getId());	
    				}
        			
    				workitemIds.add((long)workItem0.getId());
    				i++;
    				
    				if(i % MaxCommitNumber == 0 || i == length){
    					Map<String, Object> nameParameters = new HashMap<String, Object>();
    					nameParameters.put("subObjectId", workitemIds);
    					affairManager.bulkUpdate("update " + Affair.class.getName() + " set state=?,subState=?,updateDate=? where objectId=? and (subObjectId in (:subObjectId))", 
    							nameParameters, state, SubStateEnum.col_normal.key(), now, affair.getObjectId());
    					
    					workitemIds = new ArrayList<Long>();
    				}
    			}
    			return false;
    		}
    	}
    	
    	Affair affair = eventData2ExistingAffair(eventData);
    	if (affair == null)
            return false;
    	boolean executeSingleUpdate = true;
    	boolean isEdoc = this.isEdoc();
    	boolean isColl = this.isColl();
    	OrgManager orgManager = (OrgManager)getBean("OrgManager");
    	UserMessageManager userMessageManager = (UserMessageManager)getBean("UserMessageManager");

    	//竞争执行
        if(operationType == 9 || operationType == 11){
        	List<Affair> affairs = affairManager.findByObjectIdAndActivityId(affair.getApp(), affair.getObjectId(), affair.getActivityId());
        	if(!affairs.isEmpty()){
	        	affairManager.bulkUpdate("UPDATE " + Affair.class.getName() + " SET state=?,subState=?,updateDate=? WHERE app=? AND objectId=? AND activityId=? AND subObjectId<>?", 
	        			null, StateEnum.col_competeOver.key(), SubStateEnum.col_normal.key(), new Date(), affair.getApp(), affair.getObjectId(), affair.getActivityId(), workitem.getId());
	        	
	        	if(isColl){
	        		ColMessageHelper.competitionCancel(affairManager, orgManager, userMessageManager, workitem, affairs);
	        	}
	        	else if(isEdoc){
//	        		给在竞争执行中被取消的affair发送消息提醒
	            	EdocMessageHelper.competitionCancel(affairManager, orgManager, userMessageManager, workitem, affairs);
	        	}
        	}
        	
        	return true;
        }
        else if(operationType == 8){
        	throw new UnsupportedOperationException(affair.getId() + ", " + affair.getSubject());
        }
        else if(operationType == 12){
//        	删除被替换的所有affair事项
        	List<Affair> affairs = new ArrayList<Affair>();
        	if(eventData.get(ActionRunner.SYSDATA_EXTEND_DATA) != null){
        		if(eventData.get(ActionRunner.SYSDATA_EXTEND_DATA) instanceof List){
        			affairs = this.superviseCancel((List)eventData.get(ActionRunner.SYSDATA_EXTEND_DATA),now);
        			executeSingleUpdate = false;
        		}
        	}
        	
//        	给在督办中被删除的affair发送消息提醒
        	if(affairs.isEmpty()){
        		affairs.add(affair);
        	}
        	if(isColl){
        		ColMessageHelper.superviseDelete(affairManager, orgManager, userMessageManager, workitem, affairs);
        	}else if(isEdoc){
        		EdocMessageHelper.superviseDelete(affairManager, orgManager, userMessageManager, workitem, affairs);
        	}
        }
        
        if(executeSingleUpdate){
        	affair.setUpdateDate(now);
        	affairManager.updateAffair(affair);
        }
        return false;
    }
    
    protected boolean onWorkitemStoped( Map eventData)  throws BPMException{
    	WorkItem workitem = (WorkItem) eventData.get(ActionRunner.SYSDATA_WORKITEM);
    	Affair affair = DateSharedWithWorkflowEngineThreadLocal.getTheStopAffair();
    	AffairManager affairManager = (AffairManager)getBean("affairManager"); 
    	OrgManager orgManager = (OrgManager)getBean("OrgManager");
    	UserMessageManager userMessageManager = (UserMessageManager)getBean("UserMessageManager");
    	if(affair == null){
        	affair = affairManager.getBySubObject((long)workitem.getId());
        }
    	Timestamp now = new Timestamp(System.currentTimeMillis());
    	Map<String, Object> columns = new HashMap<String, Object>();
    	columns.put("state", StateEnum.col_done.key());
    	columns.put("subState", SubStateEnum.col_done_stepStop.key());
    	columns.put("completeTime", now);
    	columns.put("updateDate", now);
    	
//    	终止时不给待发送事项发消息
    	List<Affair> trackingAndPendingAffairs = affairManager.getTrackingAndPendingAffairBySummaryId(affair.getObjectId(),affair.getApp());
//    	根据app判断，避免终止时更新待发送事项的状态
    	affairManager.update(columns, new Object[][]{{"objectId", affair.getObjectId()}, {"state", StateEnum.col_pending.key()},{"app",affair.getApp()}});
    	log.info("流程终止，发送消息。。。"+this.isColl()+" " + affair.getId());
    	if(this.isColl()){
    		ColMessageHelper.terminateCancel(affairManager, orgManager, userMessageManager, workitem, affair, trackingAndPendingAffairs);
    	}else{
    		EdocMessageHelper.terminateCancel(affairManager, orgManager, userMessageManager, workitem, affair, trackingAndPendingAffairs);
    	}
    	return true;
    }
    
    //通过workitemId得到affair
    protected Affair eventData2ExistingAffair(Map eventData) throws BPMException{
        WorkItem workitem = (WorkItem) eventData.get(ActionRunner.SYSDATA_WORKITEM);
        int operationType = DateSharedWithWorkflowEngineThreadLocal.getOperationType();
        AffairManager affairManager = (AffairManager)getBean("affairManager"); 
        Affair affair = affairManager.getBySubObject(Long.parseLong(workitem.getId()+""));
        if(affair == null){
        	getLog().warn("不能通过workitem取到affair，workitem id："+workitem.getId());
        }
        switch(operationType){
        	case 1 :  
        		affair.setState(StateEnum.col_stepBack.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
            	break;
        	case 2 :
        		affair.setState(StateEnum.col_takeBack.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
            	break;
        	case 10 :
        		affair.setState(StateEnum.col_cancel.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
            	break;
        	case 9 :
        		affair.setState(StateEnum.col_done.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
            	break;
        	case 13:
        		affair.setState(StateEnum.col_done.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
        		break;
        	case 8 :
        		affair.setState(StateEnum.col_done.key());
            	affair.setSubState(SubStateEnum.col_done_stepStop.key());
            	affair.setCompleteTime(new Timestamp(System.currentTimeMillis()));
            	break;
        }
        return affair;
    }
    
    protected List<Affair> superviseCancel(List workitems,Timestamp now) throws BPMException{
		List<Affair> affair4Message = new ArrayList<Affair>();
		if(workitems == null || workitems.size()==0)
			return affair4Message;
		List<Long> ids = new ArrayList<Long>();
		Map<String,Object> nameParameters = new HashMap<String,Object>();
		AffairManager affairManager = (AffairManager)getBean("affairManager"); 
		for(int i=0;i<workitems.size();i++){
			ids.add((long)((WorkItem)workitems.get(i)).getId());
			//防止in超长，300个一更新，事务上会有问题
			if((i+1) % 300 == 0 || i == workitems.size()-1){
				nameParameters.put("subObjectId", ids);
				affairManager.bulkUpdate("update " + Affair.class.getName() + " set state=?,subState=?,updateDate=?,is_delete=1 where subObjectId in (:subObjectId)", nameParameters, StateEnum.col_cancel.key(),SubStateEnum.col_normal.key(),now);
				List<Affair> affairs = affairManager.getByConditions(nameParameters);
				affair4Message.addAll(affairs);
				ids.clear();
			}
		}
		return affair4Message;
	}
    
    private Object getBean(String beanName) throws BPMException{
    	return ApplicationContextHolder.getBean(beanName);
    	/*try{
    		return getClass().getDeclaredField(beanName).get(this);
    	}catch(Exception ne){
    		getLog().error("获取bean失败：",ne);
    		throw new BPMException(BPMException.EXCEPTION_CODE_DATA_FORMAT_ERROR, ne);
    	}*/
    }
    
    private boolean isEdoc(){
    	return getApp() == ApplicationCategoryEnum.edoc.getKey() || getApp() == ApplicationCategoryEnum.edocRec.getKey() 
    	|| getApp() == ApplicationCategoryEnum.edocSend.getKey() || getApp() == ApplicationCategoryEnum.edocSign.getKey();
    }
    
    private boolean isColl(){
    	return getApp() == ApplicationCategoryEnum.collaboration.getKey();
    }
    /**
     * 设置Affair的运行时长，超时时长，按工作时间设置的运行时长，按工作时间设置的超时时长。
     * @param affair
     */
    public void setTime2Affair(Affair affair){
    	WorkTimeManager workTimeManager = null;
		try {
			workTimeManager = (WorkTimeManager)getBean("workTimeManager");
		} catch (BPMException e) {
			getLog().error("获取时间设置对象",e);
		}
    	//工作日计算运行时间和超期时间。
    	long runWorkTime = 0L;
    	long orgAccountId = 0L;
    	if(isColl()){
    		ColSummary summary = (ColSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
    		orgAccountId = summary.getOrgAccountId();
    	}else if(isEdoc()){
    		EdocSummary summary = (EdocSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
    		orgAccountId = summary.getOrgAccountId();
    	}
    	try {
			runWorkTime = workTimeManager.getDealWithTimeValue(affair.getReceiveTime(),new Date(),orgAccountId);
			runWorkTime = runWorkTime/(60*1000);
		} catch (WorkTimeSetExecption e1) {
			getLog().error("",e1);
		}
		Long workDeadline = workTimeManager.convert2WorkTime(affair.getDeadlineDate(), orgAccountId);
		//超期工作时间
		Long overWorkTime = 0L;
		//设置了处理期限才进行计算,没有设置处理期限的话,默认为0;
		if(workDeadline!=null &&  workDeadline!=0){
			long ow = runWorkTime - workDeadline;
			overWorkTime =  ow >0 ? ow: null ;
		}
    	
    	//自然日计算运行时间和超期时间
    	Long runTime = (System.currentTimeMillis() - affair.getReceiveTime().getTime())/(60*1000);
    	Long overTime = 0L;
    	if( affair.getDeadlineDate()!= null && affair.getDeadlineDate()!= 0){
    		Long o = runTime - affair.getDeadlineDate();
    		overTime = o >0 ? o : null;
    	}
    	affair.setOverTime(overTime);
    	affair.setOverWorkTime(overWorkTime);
    	affair.setRunTime(runTime);
    	affair.setRunWorkTime(runWorkTime);
    }
    /**
     * 设置Summary的运行时长，超时时长，按工作时间设置的运行时长，按工作时间设置的超时时长。
     * @param affair
     */
    public void setTime2Summary(){
    	WorkTimeManager workTimeManager = null;
		try {
			workTimeManager = (WorkTimeManager)getBean("workTimeManager");
		} catch (BPMException e) {
			getLog().error("获取时间设置对象",e);
		}
    	//工作日计算运行时间和超期时间。
    	long runWorkTime = 0L;
    	long orgAccountId = 0L;
    	Date startDate = null;
    	Long deadLine = 0L;
    	if(isColl()){
    		ColSummary summary = (ColSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
    		orgAccountId = summary.getOrgAccountId();
    		startDate = summary.getCreateDate();
    		deadLine = summary.getDeadline();
    	}else if(isEdoc()){
    		EdocSummary summary = (EdocSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
    		orgAccountId = summary.getOrgAccountId();
    		startDate = summary.getCreateTime();
    		deadLine = summary.getDeadline();
    	}
    	try {
			runWorkTime = workTimeManager.getDealWithTimeValue(startDate,new Date(),orgAccountId);
			runWorkTime = runWorkTime/(60*1000);
		} catch (WorkTimeSetExecption e1) {
			getLog().error("",e1);
		}
		Long workDeadline = workTimeManager.convert2WorkTime(deadLine, orgAccountId);
		//超期工作时间
		Long overWorkTime = 0L;
		//设置了处理期限才进行计算,没有设置处理期限的话,默认为0;
		if(workDeadline!=null&&workDeadline!=0){
			long ow = runWorkTime - workDeadline;
			overWorkTime =  ow >0 ? ow: null ;
		}
    	
    	//自然日计算运行时间和超期时间
    	Long runTime = (System.currentTimeMillis() - startDate.getTime())/(60*1000);
    	Long overTime = 0L;
    	if( deadLine!= null &&  deadLine!=0){
    		Long o = runTime - deadLine;
    		overTime = o >0 ? o : null;
    	}
    	if(isColl()){
    		ColSummary summary = (ColSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
    		summary.setOverTime(overTime);
    		summary.setOverWorkTime(overWorkTime);
    		summary.setRunTime(runTime);
    		summary.setRunWorkTime(runWorkTime);
    	}else if(isEdoc()){
    		EdocSummary summary = (EdocSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
    		summary.setOverTime(overTime);
    		summary.setOverWorkTime(overWorkTime);
    		summary.setRunTime(runTime);
    		summary.setRunWorkTime(runWorkTime);
    	}
    }
}
