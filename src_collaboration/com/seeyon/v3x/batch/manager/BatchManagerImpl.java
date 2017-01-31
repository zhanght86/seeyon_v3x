/**
 * 
 */
package com.seeyon.v3x.batch.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMHumenActivity;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.listener.CollaborationFormBindEventListener;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.cap.info.domain.InfoOpinionCAP;
import com.seeyon.cap.info.domain.InfoSummaryCAP;
import com.seeyon.cap.info.manager.InfoManagerCAP;
import com.seeyon.cap.info.manager.InfoSummaryManagerCAP;
import com.seeyon.cap.info.util.InfoHelperCAP;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.batch.BatchData;
import com.seeyon.v3x.batch.BatchResult;
import com.seeyon.v3x.batch.BatchState;
import com.seeyon.v3x.batch.exception.BatchException;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.event.CollaborationFormVouchEvent;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.collaboration.manager.impl.BranchArgs;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.manager.impl.ColLock;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.collaboration.webmodel.NewflowModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.permission.domain.Permission;
import com.seeyon.v3x.common.permission.manager.PermissionManager;
import com.seeyon.v3x.common.permission.util.NodePolicy;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.flowperm.domain.FlowPerm;
import com.seeyon.v3x.flowperm.manager.FlowPermManager;
import com.seeyon.v3x.mobile.manager.MobileFormBean;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

/**
 * @author dongyj
 *
 */
public class BatchManagerImpl  extends BaseHibernateDao<ColSummary>  implements BatchManager {
	
	private static final Log log = LogFactory.getLog(BatchManagerImpl.class);

	private ColManager colManager;
	
	private EdocManager edocManager;
	
	private AffairManager affairManager;
	
	private TempleteManager templeteManager;
	
	private OrgManager orgManager;
	
	private PermissionManager permissionManager;
	
	private FlowPermManager flowPermManager;
	
	private NewflowManager newflowManager;
	
	public void setInfoSummaryManagerCAP(InfoSummaryManagerCAP infoSummaryManagerCAP) {
		this.infoSummaryManagerCAP = infoSummaryManagerCAP;
	}
	private InfoManagerCAP infoManagerCAP;
	
	private InfoHelperCAP infoHelperCAP; 
	
	public void setInfoHelperCAP(InfoHelperCAP infoHelperCAP) {
		this.infoHelperCAP = infoHelperCAP;
	}
	private InfoSummaryManagerCAP infoSummaryManagerCAP;
	
	public void setInfoManagerCAP(InfoManagerCAP infoManagerCAP) {
		this.infoManagerCAP = infoManagerCAP;
	}
	private List<String> supportCategory = new ArrayList<String>();
	
	public void setCollaborationFormBindEventListener(
			CollaborationFormBindEventListener collaborationFormBindEventListener) {
		this.collaborationFormBindEventListener = collaborationFormBindEventListener;
	}
	private CollaborationFormBindEventListener collaborationFormBindEventListener;
	
	public void setNewflowManager(NewflowManager newflowManager) {
		this.newflowManager = newflowManager;
	}
	public void setSupportCategory(List<String> supportCategory) {
		this.supportCategory = supportCategory;
	}
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}
	public void setEdocManager(EdocManager edocManager) {
		this.edocManager = edocManager;
	}
	
	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}
	
	public List<BatchResult> doBatch(List<BatchData> data) {
		List<BatchResult> result = new ArrayList<BatchResult>();
		if(data == null || data.isEmpty()){
			return result;
		}
		User user = CurrentUser.get();
		for(BatchData d : data){
			BatchResult re = new BatchResult(d.getAffairId(),d.getSummaryId());
			try {
				if(d.getCategory() == ApplicationCategoryEnum.collaboration.getKey()){
					colFinish(d.getAffairId(),d.getSummaryId(),(ColOpinion)d.getOpinion(),user);
				}else if(d.getCategory() == ApplicationCategoryEnum.info.getKey()){//branches_a8_v350sp1_r_gov GOV-4029 魏俊标 首页信息报送批处理 start
					try {
						infoFinish(d.getAffairId(),d.getSummaryId(),(InfoOpinionCAP)d.getOpinion(),user);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{//branches_a8_v350sp1_r_gov GOV-4029 魏俊标 首页信息报送批处理 end
					edocFinish(d.getAffairId(),d.getSummaryId(),(EdocOpinion)d.getOpinion(),user);
				}
				re.setResultCode(BatchState.Normal.getCode());
			} catch (BatchException e) {
				re.setResultCode(e.getErrorCode());
				re.addMessage(e.getMessage());
			}
			super.getSession().flush();//四川科伦药业股份有限公司 _流程中节点已经处理了，但仍显示暂存待办，流程终止无效
			super.getSession().clear();
			DateSharedWithWorkflowEngineThreadLocal.remove();
			result.add(re);
		}
		return result;
	}
	
	private void colFinish(Long affairId,Long summaryId,ColOpinion opinion,User user) throws BatchException{
		ColLock colLock = ColLock.getInstance();
		boolean isRelieveLock = false;
		try {
			try {
				//同步锁
				colLock.checkCanAction(summaryId, user.getId(), user.getName(),  ColLock.COL_ACTION.finishWorkItem);
				isRelieveLock = true;
			} catch (BusinessException e) {
				String message = ColHelper.getErrorMessage(e,ApplicationCategoryEnum.collaboration);
				throw new BatchException(BatchState.Error.getCode(),message);
			}
			Map<String, String[]> map = new HashMap<String, String[]>();
			Map<String,String> condition = new HashMap<String,String>();
			ColSummary summary;
			try {
				summary = colManager.getColSummaryById(summaryId, false);
				//检查流程锁
				checkProcess(summary.getProcessId(),summary.getId());
				
				//原始核定节点通过标记
				Integer oldIsVouch = summary.getIsVouch();
				if(oldIsVouch==null)
					oldIsVouch = new Integer(0);
				if(checkIsVouch(affairId, summary)){
					Map<String, Object> columns = new HashMap<String, Object>();
   			        columns.put("isVouch", Constant.ColSummaryVouch.vouchPass.getKey());
   				    colManager.update(summaryId, columns);
   				    summary.setIsVouch(Constant.ColSummaryVouch.vouchPass.getKey());
				}
		  		// 协同表单核定事件触发
		  		if(summary.getIsVouch() == 1){
			  		// 协同表单核定事件触发
			  		CollaborationFormVouchEvent event = new CollaborationFormVouchEvent(this);
			  		event.setSummaryId(summaryId);
			  		event.setIsVouch(summary.getIsVouch());
			  		event.setOldIsVouch(oldIsVouch);
			  		event.setAffairId(affairId);
			  		EventDispatcher.fireEvent(event);
		  		}
				colManager.finishWorkItem(affairId, opinion, map, condition, summary.getProcessId(), user);
				
				doAuditState4Form(affairId, summary);
			} catch (ColException e) {
				throw new BatchException(BatchState.Error.getCode(),e.getMessage());
			}
		} catch (BatchException e) {
			throw e;
		}finally{
			if(isRelieveLock){
				ColLock.getInstance().removeLock(summaryId);
			}
		}
	}
	//更新表单动态表的审核state
	private void doAuditState4Form(Long affairId, ColSummary summary) throws ColException{
		Affair affair = affairManager.getById(affairId);
		if(affair != null){
			BPMProcess process = ColHelper.getCaseProcess(summary.getProcessId());
	        BPMHumenActivity humenactivity= (BPMHumenActivity)process.getActivityById(affair.getActivityId().toString());
	        BPMSeeyonPolicy policy = humenactivity.getSeeyonPolicy();
	        if(summary.getFormAppId() != null && policy != null){
	        	String nodePermissionPolicy = policy.getId();
	        	if("formaudit".equals(nodePermissionPolicy)){
	        		IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	        		if(iOperBase != null){
	        			try {
		        			//审核通过：2
		        			iOperBase.updateState4Form(summary.getFormAppId(), summary.getFormRecordId(), 2);
	        			} catch (Exception e){
	        				log.error("更新表单动态表单state时出错！", e);
	        			}
	        		}
	        	}
	        }
		}
	}
	private boolean checkIsVouch(Long affairId,ColSummary summary) throws ColException{
		Affair affair = affairManager.getById(affairId);
		if(affair != null){
			String vouch = null;
			if(Strings.isNotBlank(affair.getNodePolicy())){
				vouch = affair.getNodePolicy();
			}else{
				BPMProcess process = null;
				BPMActivity activity = null;
				process = ColHelper.getCaseProcess(summary.getProcessId());
				activity = ColHelper.getBPMActivityByAffair(process, affair);
				String currentNodeId = activity.getId();
				BPMSeeyonPolicy seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();
				vouch = seeyonPolicy.getId();
			}
			if("vouch".equals(vouch)){
				return true;
			}
		}
		return false;
	}
	private void checkProcess(String processId,Long summaryId) throws BatchException{
		try {
			String userName = colManager.checkModifyingProcess(String.valueOf(processId),summaryId);
			if(Strings.isNotBlank(userName)){
				if(userName.indexOf("--NoSuchSummary--") >=0){
					throw new BatchException(BatchState.NoSuchSummary.getCode());
				}
				throw new BatchException(BatchState.ProcessLocked.getCode(),userName);
			}
		} catch (ColException e) {
			throw new BatchException(BatchState.Error.getCode(),e.getMessage());
		}
	}
	
	private void edocFinish(Long affairId,Long summaryId,EdocOpinion opinion,User user) throws BatchException{
		ColLock colLock = ColLock.getInstance();
		boolean isRelieveLock = false;
		try {
			try {
				colLock.checkCanAction(summaryId, user.getId(), user.getName(),  ColLock.COL_ACTION.finishWorkItem);
				isRelieveLock = true;
			} catch (BusinessException e) {
				String message = ColHelper.getErrorMessage(e,ApplicationCategoryEnum.edoc);
				throw new BatchException(BatchState.Error.getCode(),message);
			}
			Map<String, String[]> map = new HashMap<String, String[]>();
			Map<String,String> condition = new HashMap<String,String>();
			EdocSummary summary;
			try {
				summary = edocManager.getEdocSummaryById(summaryId, true);
				checkProcess(summary.getProcessId(),summary.getId());
				
				edocManager.finishWorkItem(summary, affairId,opinion, map, condition, summary.getProcessId(), user.getId()+ "", null);
			} catch (EdocException e) {
				throw new BatchException(BatchState.Error.getCode(),e.getMessage());
			}
		} catch (BatchException e) {
			throw e;
		}finally{
			if(isRelieveLock){
				ColLock.getInstance().removeLock(summaryId);
			}
		}
	}
	
	private void infoFinish(Long affairId,Long summaryId,InfoOpinionCAP opinion,User user) throws Exception{
		ColLock colLock = ColLock.getInstance();
		boolean isRelieveLock = false;
		try {
			try {
				colLock.checkCanAction(summaryId, user.getId(), user.getName(),  ColLock.COL_ACTION.finishWorkItem);
				isRelieveLock = true;
			} catch (BusinessException e) {
				String message = ColHelper.getErrorMessage(e,ApplicationCategoryEnum.info);
				throw new BatchException(BatchState.Error.getCode(),message);
			}
			Map<String, String[]> map = new HashMap<String, String[]>();
			Map<String,String> condition = new HashMap<String,String>();
			InfoSummaryCAP summaryCAP;
			try {
				summaryCAP = infoSummaryManagerCAP.getInfoSummaryById(summaryId, true);
				checkProcess(summaryCAP.getProcessId(),summaryCAP.getId());
				
				infoManagerCAP.finishWorkItem(summaryCAP, affairId, opinion, map, condition, summaryCAP.getProcessId(), user.getId() + "", null);
			} catch (EdocException e) {
				throw new BatchException(BatchState.Error.getCode(),e.getMessage());
			}
		} catch (BatchException e) {
			throw e;
		}finally{
			if(isRelieveLock){
				ColLock.getInstance().removeLock(summaryId);
			}
		}
	}
	
	public BatchResult[] preCheckBatch(Long[] affairId, Long[] summaryId, Integer[] category) {
		List<BatchResult> result = new ArrayList<BatchResult>();
		for(int i = 0 ; i < category.length ;i++){
			BatchResult batch = new BatchResult(affairId[i],summaryId[i]);
			try {
				List<String> parameter = new ArrayList<String>();
				isBatchSupport(category[i]);
				Affair affair = affairManager.getById(affairId[i]);
				if(affair != null){
					BPMProcess process = null;
					BPMActivity activity = null;
					if(category[i] ==1 || category[i] ==2){
						ColSummary summary = colManager.getColSummaryById(summaryId[i],true);
						process = ColHelper.getCaseProcess(summary.getProcessId());
						activity = ColHelper.getBPMActivityByAffair(process, affair);
						//判断节点权限
						parameter.addAll(checkColPolicy(affair,summary,process,activity));
						checkColProcess(summary,affair,process,activity);
					}else if(category[i] ==32){//信息报送//branches_a8_v350sp1_r_gov GOV-4029 魏俊标 首页信息报送批处理 start
						InfoSummaryCAP summary = infoSummaryManagerCAP.getInfoSummaryById(summaryId[i], true);
						process = ColHelper.getCaseProcess(summary.getProcessId());
						activity = ColHelper.getBPMActivityByAffair(process, affair);
						//判断节点权限
						parameter.addAll(checkInfoPolicy(affair,summary,process,activity));
						checkInfoProcess(summary,affair,process,activity);
					}else{//branches_a8_v350sp1_r_gov GOV-4029 魏俊标 首页信息报送批处理 end
						EdocSummary summary = edocManager.getEdocSummaryById(summaryId[i], false);
						process = ColHelper.getCaseProcess(summary.getProcessId());
						activity = ColHelper.getBPMActivityByAffair(process, affair);
						//判断节点权限
						parameter.addAll(checkEdocPolicy(affair,summary,process,activity));
						checkEdocProcess(summary,affair,process,activity);
					}
				}
				batch.setResultCode(BatchState.Normal.getCode());
				batch.addMessage(parameter);
			} catch (BatchException e) {
				batch.setResultCode(e.getErrorCode());
				batch.addMessage(e.getMessage());
			} catch (Exception e) {
				batch.setResultCode(BatchState.Error.getCode());
				batch.addMessage(e.getMessage());
			}
			result.add(batch);
		}
		return result.toArray(new BatchResult[0]);
	}
	
	/**
	 * 判断公文流程（当前处理人下个节点是否需要选人）
	 * @param summary
	 * @param affair
	 * @param process
	 * @param activity
	 * @throws BatchException
	 * @throws ColException
	 */
	private void checkEdocProcess(EdocSummary summary,Affair affair,BPMProcess process,BPMActivity activity) throws BatchException, ColException{
		String currentNodeId = activity.getId();
		BPMSeeyonPolicy seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();
		if(!ColHelper.isExecuteFinished(process, affair) || "inform".equals(seeyonPolicy.getId())){
			return ;
		}
		long workItemId= -1l;
		if(affair.getSubObjectId() != null){
			workItemId = affair.getSubObjectId();
		}
		checkPreSend(process, activity, summary.getTempleteId() != null, summary.getCaseId(), null,summary.getStartUserId(),workItemId);
	}
	/**
	 * 判断信息报送流程（当前处理人下个节点是否需要选人）
	 * @param summary
	 * @param affair
	 * @param process
	 * @param activity
	 * @throws BatchException
	 * @throws ColException
	 */
	private void checkInfoProcess(InfoSummaryCAP summary,Affair affair,BPMProcess process,BPMActivity activity) throws BatchException, ColException{
		String currentNodeId = activity.getId();
		BPMSeeyonPolicy seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();
		if(!ColHelper.isExecuteFinished(process, affair) || "inform".equals(seeyonPolicy.getId())){
			return ;
		}
		long workItemId= -1l;
		if(affair.getSubObjectId() != null){
			workItemId = affair.getSubObjectId();
		}
		checkPreSend(process, activity, summary.getTempleteId() != null, summary.getCaseId(), null,summary.getStartUserId(),workItemId);
	}
	//协同，判断流程是否需要选择人员
	private void checkColProcess(ColSummary summary,Affair affair,BPMProcess process,BPMActivity activity) throws BatchException, ColException{
		ColBody body = summary.getFirstBody();
		//判断新流程
		String currentNodeId = activity.getId();
		
		BPMSeeyonPolicy seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();

		boolean hasNewflow = summary.getTempleteId() != null && seeyonPolicy != null && "1".equals(seeyonPolicy.getNF());
		if(hasNewflow){
			List<NewflowModel> newflowModels = newflowManager.getNewflowModelList(summary.getId(), summary.getTempleteId(), currentNodeId);
			if(newflowModels != null && !newflowModels.isEmpty()){
				throw new BatchException(BatchState.NewFlow.getCode());
			}
		}
		boolean isForm = false;
		if(body != null){
			if("FORM".equals(body.getBodyType())){
				isForm = true;
			}
		}
		
		if(!ColHelper.isExecuteFinished(process, affair) || "inform".equals(seeyonPolicy.getId())){
			return ;
		}
		Map<String,String[]> fieldDataBaseMap = new HashMap<String,String[]>() ;
		String mastrid = null;
		if(isForm){
			if(body != null){
    			mastrid = body.getContent() ;
    		}
			String formApp = seeyonPolicy.getFormApp() ;
			try {
				fieldDataBaseMap  =	FormHelper.getFieldValueMap(formApp, seeyonPolicy.getForm(), seeyonPolicy.getOperationName(), mastrid) ;
			} catch (Exception e) {
				throw new BatchException(BatchState.Error.getCode(),e.getMessage());
			}
		}
		long workItemId= -1l;
		if(affair.getSubObjectId() != null){
			workItemId = affair.getSubObjectId();
		}
		checkPreSend(process, activity, summary.getTempleteId() != null, summary.getCaseId(), fieldDataBaseMap,summary.getStartMemberId(),workItemId);
		// 如果是表单，判断是否有必填项
		try {
			if(isForm)
				checkForm(summary,affair,mastrid);
		} catch (BatchException e) {
			throw e;
		} catch (Exception e) {
			throw new BatchException(BatchState.Error.getCode(),e.getMessage());
		}
		if(isForm){
			collaborationFormBindEventListener.checkBindEventBatch(affair.getFormAppId(), affair.getFormOperationId(), summary.getId());
		}
	}
	
	//判断表单中的必填项是否已经不为空
	private void checkForm(ColSummary summary,Affair affair,String masterId) throws BatchException,Exception{
		User user = CurrentUser.get();
		String[] formPolicy = ColHelper.getFormPolicyByAffair(affair);
		Map<String,Object> formMap = FormHelper.getFormRunForMobile(user.getId(), user.getName(), user.getLoginName(), 
				 formPolicy[0], formPolicy[1], formPolicy[2], masterId, 
				summary.getId().toString(), affair.getId().toString(), formPolicy[3], false);
		MobileFormBean formBean = new MobileFormBean(FormHelper.loadFormPojoById(Long.valueOf(masterId), Long.valueOf(formPolicy[0])));
		formBean.handle(formMap,false);
		if(formBean.isContainMustWrite()){
			throw new BatchException(BatchState.FormNotNull.getCode()); 
		}
	}
	
	/**
	 * 判断是否需要选人或有分支条件
	 * @param process
	 * @param activity
	 * @param isFromTemplate
	 * @param caseId
	 * @param fieldValueMap
	 * @throws BatchException
	 */
	private void checkPreSend(BPMProcess process, BPMActivity activity, boolean isFromTemplate, Long caseId,Map<String,String[]> fieldValueMap,Long startMemberId,long workItemId)throws BatchException{
//		try {
//			WorkflowEventListener.ProcessModeSelector selector = ColHelper.preRunCase(process,activity.getId(), isFromTemplate, caseId,fieldValueMap);
			Map followUpMap= new HashMap();
			try {
				BranchArgs.hasSelectorOrCondition(process, activity, caseId, isFromTemplate, fieldValueMap, orgManager, followUpMap,workItemId);
			} catch (Exception e) {
				log.info(e.getMessage(),e);
				throw new BatchException(BatchState.Error.getCode(),e.getMessage());
			}
			//log.info("checkPreSend:="+followUpMap);
			if(followUpMap!= null){
        		//对不需要弹出页面，但存在不用人员时，在这里进行处理，以便在alert时提示用户
        		Map invalidateActivityMap= (Map)followUpMap.get("invalidateActivityMap");
        		//判断是否存在不可用的节点，如果存在则进行如下处理
            	if(invalidateActivityMap != null && !invalidateActivityMap.isEmpty()){
            		throw  new BatchException(BatchState.InvidateNode.getCode());//存在不可用的节点，不让发
            	}
            	String isNextPop= (String)followUpMap.get("isPop");
                if("true".equals(isNextPop)){
                	throw  new BatchException(BatchState.ProcessNeedPerson.getCode());//流程需要选择人员或有分支条件
                }
			}
//			List<NodeAddition> invalidateActivity = selector.getInvalidateActivity();
//	        if(invalidateActivity != null && !invalidateActivity.isEmpty()){ //存在不可用的节点，不让发
//	        	throw  new BatchException(BatchState.InvidateNode.getCode());
//	        }
//	        List<NodeAddition> nodeAdditions = selector.getNodeAdditions();
//	        if(nodeAdditions != null && !nodeAdditions.isEmpty()){
//	        	throw  new BatchException(BatchState.ProcessNeedPerson.getCode());
//	        }
//	        Map<String,Object> hash = ColHelper.getCondition(activity, null, startMemberId, CurrentUser.get().getId(), orgManager,false,-1l,-1l,null);
//	        if(hash != null){
//	        	List<String> keys = (List<String>)hash.get("keys");
//	        	List<String> nodeNames = (List<String>)hash.get("names");
//	        	if(keys != null && nodeNames != null && !keys.isEmpty() && !nodeNames.isEmpty()){
//	        		throw  new BatchException(BatchState.ProcessNeedPerson.getCode());
//	        	}
//	        }
//		} catch (Exception e) {
//			throw new BatchException(BatchState.Error.getCode(),e.getMessage());
//		}
		
	}
	
	/**
	 * 应用是否支持批处理
	 * @param category
	 * @return
	 */
	private void isBatchSupport(int category) throws BatchException{
		if(!supportCategory.contains(String.valueOf(category))){
			throw new BatchException(BatchState.NotSupport.getCode());
		}
	}
	
	private List<String> checkColPolicy(Affair affair,ColSummary summary,BPMProcess process,BPMActivity activity) throws Exception{
		V3xOrgMember sender = orgManager.getMemberById(affair.getSenderId());
		
		Long flowPermAccountId = ColHelper.getFlowPermAccountId(sender.getOrgAccountId(), summary, templeteManager);
		
		String nodePermissionPolicy = "collaboration";
        nodePermissionPolicy = activity.getSeeyonPolicy().getId();
        
        Permission permission = permissionManager.getPermission(MetadataNameEnum.col_flow_perm_policy.name(), nodePermissionPolicy, flowPermAccountId);
        NodePolicy policy = permission.getNodePolicy();
        
        return checkPolicy(policy);
	}
	
	private List<String> checkEdocPolicy(Affair affair,EdocSummary summary,BPMProcess process,BPMActivity activity) throws Exception{
		V3xOrgMember sender = orgManager.getMemberById(affair.getSenderId());
		
		Long flowPermAccountId = EdocHelper.getFlowPermAccountId(summary,sender.getOrgAccountId(),templeteManager);
		
		String nodePermissionPolicy = "shenpi";
		if(activity != null){
			nodePermissionPolicy = activity.getSeeyonPolicy().getId();
		}
		MetadataNameEnum edocTypeEnum=EdocUtil.getEdocMetadataNameEnum(summary.getEdocType());
		
		FlowPerm fpm = flowPermManager.getFlowPerm(edocTypeEnum.name(), nodePermissionPolicy, flowPermAccountId);
		NodePolicy policy = fpm.getNodePolicy();
		
		return checkPolicy(policy);
	}
	private List<String> checkInfoPolicy(Affair affair,InfoSummaryCAP summary,BPMProcess process,BPMActivity activity) throws Exception{
		V3xOrgMember sender = orgManager.getMemberById(affair.getSenderId());
		Long flowPermAccountId = infoHelperCAP.getFlowPermAccountId(summary,sender.getOrgAccountId());
		String nodePermissionPolicy = "shenhe";
		if(activity != null){
			nodePermissionPolicy = activity.getSeeyonPolicy().getId();
		}
		FlowPerm fpm = flowPermManager.getFlowPerm("info_send_permission_policy", nodePermissionPolicy, flowPermAccountId);
		NodePolicy policy = fpm.getNodePolicy();
		return checkPolicy(policy);
	}
	
	private List<String> checkPolicy(NodePolicy policy) throws BatchException{
		List<String> result = new ArrayList<String>(2);
		if(policy.getBatch() != 1){//不允许批量处理
			throw new BatchException(BatchState.PolicyNotOpe.getCode());
		}
		if(policy.getAttitude() == null){
			result.add("1");
		}else{
			result.add(String.valueOf(policy.getAttitude()));
		}
		String baseAction = policy.getBaseAction();
		if(Strings.isNotBlank(baseAction)){
			if(baseAction.indexOf("Opinion") >=0){
				result.add(String.valueOf(policy.getOpinionPolicy()));
			}else{
				result.add(String.valueOf(2));
			}
		}
		return result;
	}

	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}
	public void setFlowPermManager(FlowPermManager flowPermManager) {
		this.flowPermManager = flowPermManager;
	}
	
}
