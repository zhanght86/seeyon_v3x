package com.seeyon.v3x.edoc.manager;

import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.engine.exception.BPMException;
import net.joinwork.bpm.engine.wapi.ProcessDefManager;
import net.joinwork.bpm.engine.wapi.WAPIFactory;
import net.joinwork.bpm.engine.wapi.WorkItem;
import net.joinwork.bpm.util.Utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.dao.ColTrackMemberDao;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColTrackMember;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.MessageData;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.SeeyonPolicy;
import com.seeyon.v3x.collaboration.domain.WorkflowData;
import com.seeyon.v3x.collaboration.event.CollaborationCancelEvent;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.Partition;
import com.seeyon.v3x.common.filemanager.manager.AttachmentEditHelper;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.filemanager.manager.PartitionManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.permission.manager.PermissionManager;
import com.seeyon.v3x.common.processlog.ProcessLogAction;
import com.seeyon.v3x.common.processlog.domain.ProcessLog;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.util.ActionType;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.dao.EdocBodyDao;
import com.seeyon.v3x.edoc.dao.EdocOpinionDao;
import com.seeyon.v3x.edoc.dao.EdocSummaryDao;
import com.seeyon.v3x.edoc.domain.EdocBody;
import com.seeyon.v3x.edoc.domain.EdocForm;
import com.seeyon.v3x.edoc.domain.EdocFormExtendInfo;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.util.EdocSuperviseHelper;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.edoc.util.MetaUtil;
import com.seeyon.v3x.edoc.webmodel.EdocOpinionDisplayConfig;
import com.seeyon.v3x.edoc.webmodel.EdocOpinionModel;
import com.seeyon.v3x.edoc.webmodel.EdocSearchModel;
import com.seeyon.v3x.edoc.webmodel.EdocSummaryModel;
import com.seeyon.v3x.edoc.webmodel.MoreSignSelectPerson;
import com.seeyon.v3x.edoc.workflow.event.WorkflowEventListener;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.exchange.domain.EdocSendRecord;
import com.seeyon.v3x.exchange.manager.SendEdocManager;
import com.seeyon.v3x.exchange.util.Constants;
import com.seeyon.v3x.index.share.datamodel.AuthorizationInfo;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.indexInterface.IndexUtil;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.signet.dao.V3xHtmDocumentSignatureDao;
import com.seeyon.v3x.system.signet.domain.V3xHtmDocumentSignature;
import com.seeyon.v3x.system.signet.manager.SignetManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.worktimeset.manager.WorkTimeManager;


public class EdocManagerImpl implements EdocManager,IndexEnable {
	
	private final static Log log = LogFactory.getLog(EdocManagerImpl.class);
    private ProcessLogManager processLogManager ;
    private AppLogManager appLogManager ;
	
	private EdocSummaryDao edocSummaryDao;
	private EdocOpinionDao edocOpinionDao;
	private ColTrackMemberDao colTrackMemberDao;

	private V3xHtmDocumentSignatureDao htmlSignDao;
	private AffairManager affairManager;
	private UserMessageManager userMessageManager = null;
	private SearchManager searchManager = null;
    private AttachmentManager attachmentManager;
    private FileManager fileManager;
    private EdocBodyDao edocBodyDao;
    private SendEdocManager sendEdocManager;
    private DocHierarchyManager docHierarchyManager;
    private MetadataManager metadataManager;
    private OrgManager orgManager;
    private EdocStatManager edocStatManager;
    private EdocSuperviseManager edocSuperviseManager;
    private EdocFormManager edocFormManager;
	private PermissionManager permissionManager;
    private PartitionManager partitionManager;
    private TempleteManager templeteManager;
    private WorkTimeManager workTimeManager;

	private EdocElementManager edocElementManager;
    private EdocMarkManager  edocMarkManager;
    private EdocMarkHistoryManager edocMarkHistoryManager;
    private SignetManager signetManager;
    private ColSuperviseManager colSuperviseManager;
    private ColManager colManager;
    
    public static final String PAGE_TYPE_DRAFT = "draft";
    public static final String PAGE_TYPE_SENT = "sent";
    public static final String PAGE_TYPE_PENDING = "pending";
    public static final String PAGE_TYPE_FINISH = "finish";

	public void setColTrackMemberDao(ColTrackMemberDao colTrackMemberDao) {
		this.colTrackMemberDao = colTrackMemberDao;
	}

	public void setPartitionManager(PartitionManager partitionManager) {
		this.partitionManager = partitionManager;
	}
    public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}
    public void setEdocFormManager(EdocFormManager edocFormManager)
	{
		this.edocFormManager=edocFormManager;
	}
    public void setEdocStatManager(EdocStatManager edocStatManager)
    {
    	this.edocStatManager=edocStatManager;
    }
    
    public void setHtmlSignDao(V3xHtmDocumentSignatureDao htmlSignDao)
    {
    	this.htmlSignDao=htmlSignDao;
    }
    
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    
    public void setMetadataManager(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }
    
    public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager)
    {
    	this.docHierarchyManager=docHierarchyManager;
    }
    
    public void setSendEdocManager(SendEdocManager sendEdocManager)
    {
    	this.sendEdocManager=sendEdocManager;
    }
    
    public void setEdocBodyDao(EdocBodyDao edocBodyDao)
    {
    	this.edocBodyDao=edocBodyDao;
    }
    
    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }
	
    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }
	
	public void setUserMessageManager(UserMessageManager userMessageManager) {
        this.userMessageManager = userMessageManager;
    }
	
	public void setEdocOpinionDao(EdocOpinionDao edocOpinionDao)
	{
		this.edocOpinionDao=edocOpinionDao;
	}
	
	public void setEdocSummaryDao(EdocSummaryDao edocSummaryDao)
	{
		this.edocSummaryDao=edocSummaryDao;
	}
	
	public void setAffairManager(AffairManager affairManager)
	{
		this.affairManager=affairManager;
	}
	
	public void setSignetManager(SignetManager signetManager) {
		this.signetManager = signetManager;
	}
	
	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}
	
	public void setWorkTimeManager(WorkTimeManager workTimeManager) {
		this.workTimeManager = workTimeManager;
	}
	
	public boolean writeDocOperateLog(Long docResId,Long summaryId,String subject,String operType)
	{
		//记录操作日志
		String operStr="";
		if("save".equals(operType)){operStr=ActionType.LOG_DOC_SAVE;}
		else if("print".equals(operType)){operStr=ActionType.LOG_DOC_PRINT;}
        //operationlogManager.insertOplog(docResId, summaryId,ApplicationCategoryEnum.doc,operStr, operStr+".desc",CurrentUser.get().getName(), subject);
		return true;
	}
	public Hashtable getEdocOpinion(Long edocFormId,LinkedHashMap hsOpinion) throws EdocException{
		User user = CurrentUser.get();
		return getEdocOpinion(edocFormId,user.getLoginAccount(),hsOpinion);
	}

	public Hashtable getEdocOpinion(Long edocFormId,Long aclAccountId,LinkedHashMap hsOpinion) throws EdocException
	{


		Hashtable locOpin=new Hashtable();
		Hashtable <String,String>locHs=edocFormManager.getOpinionLocation(edocFormId,aclAccountId);
		//Enumeration en = hsOpinion.keys();
		Iterator en=hsOpinion.keySet().iterator();
		String key="";
		String value="";
		String local="";
		String newValue="";
		String tempValue="";
		//发起人意见是否绑定
		boolean setSendLoca=false;
		while(en.hasNext())
		{
			tempValue="";
			key=en.next().toString();
			if("senderOpinionList".equals(key) ||"senderOpinionAttStr".equals(key)){continue;}
			value=(String)hsOpinion.get(key);
			if(value==null || "".equals(value)){continue;}
			/*
			local=locHs.get(key);
			if(local!=null && !"".equals(local)){key=local;}
			newValue=(String)locOpin.get(key);
			if(newValue!=null && !"".equals(newValue))
			{
				tempValue=newValue;
				tempValue+="<br>";
			}
			tempValue+=value;
			locOpin.put(key,tempValue);
			*/
			String opinionKey=key.split("_")[0];
			// tempLoacl格式 公文元素名称为value_公文元素的排序方式
			String tempLocal = locHs.get(opinionKey);
			local=locHs.get(opinionKey);
			// 公文元素名称
			if(local!=null)	local = tempLocal.split("_")[0];
			// 公文元素绑定的排序方式
			//String sortType = tempLocal.split("_")[1];

			//没有设置意见放置位置的，统一放到其它意见，不在按节点权限放置到前台匹配;发起人附言单独处理,如果没有设置绑定就不放入公文单
			if(local==null || "".equals(local))
			{
				if(opinionKey.equals("niwen") || opinionKey.equals("dengji")){continue;}
				local="otherOpinion";
			}
			else
			{
				//发起人附言已经绑定到公文单，不再显示到公文单下面，从对象中删除
				if(opinionKey.equals("niwen") || opinionKey.equals("dengji"))
				{
					setSendLoca=true;					
				}
			}
			newValue=(String)locOpin.get(local);
			if(newValue!=null && !"".equals(newValue))
			{
				tempValue=newValue;
				tempValue+="<br><br>";
			}
			tempValue += value;
			locOpin.put(local, tempValue);
		}		
		if(hsOpinion.get("senderOpinionList")!=null && setSendLoca==false)
		{
			locOpin.put("senderOpinionList",hsOpinion.get("senderOpinionList"));
		}		
		return locOpin;
	}
	public LinkedHashMap getEdocOpinion(Long summaryId,Long curUser,Long sender) throws EdocException{
		EdocSummary summary=edocSummaryDao.get(summaryId);
		return getEdocOpinion(summary,summary.getOrgAccountId(),curUser,sender);
	}
	public LinkedHashMap getEdocOpinion(EdocSummary summary,Long aclAccountId,Long curUser,Long sender) throws EdocException
	{
		//EdocSummary summary=edocSummaryDao.get(summaryId);
		LinkedHashMap hs=new LinkedHashMap();

		List<EdocOpinion> senderOpinions=new ArrayList<EdocOpinion>();
		StringBuffer sb=new StringBuffer();
		// try{
		// opinions=edocOpinionDao.findLastEdocOpinionBySummaryId(summaryId,timeSort);
		// }catch(Exception e)
		// {
		// log.error("EdocManager.getEdocOpinion()得到处理意见错误", e);
		// throw new EdocException(e);
		// }
		String tempValue="";
		String attitude="";
		String doUserName="";
		String content="";
		String key="";
		Object value=null;
		List <Attachment> tempAtts=attachmentManager.getByReference(summary.getId());
		Hashtable <Long,List<Attachment>> attHas=com.seeyon.v3x.common.filemanager.manager.Util.sortBySubreference(tempAtts);
		boolean isHidden=false;
		ResourceBundle r = null;
		V3xOrgMember member = null;
		Map senderAttMap=new HashMap();

		boolean showDate = false;
		boolean showDateTime = false;
		boolean showDept = false;
		boolean showLastOptionOnly = false;
		// 取得公文单的意见元素的绑定关系，key是FlowPermName，value是公文元素名称为value_公文元素的排序方式
		Hashtable<String, String> locHs = edocFormManager
				.getOpinionLocation(summary.getFormId(),aclAccountId);
		// 公文单显示格式
		String optionFormatSet = "0,0,0";
		EdocForm form = edocFormManager.getEdocForm(summary.getFormId());
		Set<EdocFormExtendInfo> infos = form.getEdocFormExtendInfo();
		for(EdocFormExtendInfo info : infos ){
			if(info.getAccountId().equals( summary.getOrgAccountId())){
				optionFormatSet = info.getOptionFormatSet();
			}
		} 
//		String optionFormatSet = edocFormManager.getEdocForm(
//				summary.getFormId()).getOptionFormatSet();
		if (!Strings.isBlank(optionFormatSet)) {
			String[] optionFormatSets = optionFormatSet.split(",");
			if ("1".equals(optionFormatSets[0])) {
				showLastOptionOnly = true;
			}
			if ("1".equals(optionFormatSets[1])) {
				showDept = true;
			}
			if ("1".equals(optionFormatSets[2])) {
				showDate = true;
			} else {
				showDateTime = true;
			}
		}


		List<Object[]> tempResult = new ArrayList<Object[]>();   //查询出来的意见
		List<String> 	boundFlowPerm =new ArrayList<String>();   //绑定的节点权限
		//Map<意见元素名称，List<绑定的节点权限>>因为一个意见元素可以绑定多个节点权限
		Map<String,List<String>> map = new HashMap<String,List<String>>();  
		Map<String,String> sortMap = new HashMap<String,String>();
		//绑定部分的意见
		for (Iterator keyName = locHs.keySet().iterator(); keyName.hasNext();) {
			String flowPermName = (String) keyName.next();
			if(!boundFlowPerm.contains(flowPermName))boundFlowPerm.add(flowPermName);
			// tempLoacl格式 公文元素名称为value_公文元素的排序方式
			String tempLocal = locHs.get(flowPermName);
			String elementOpinion = tempLocal.split("_")[0];//公文元素名,例如公文单上的shenpi这个公文元素
			//取到指定公文元素绑定的节点权限列表
			List<String> flowPermsOfSpecialElement = map.get(elementOpinion);
			if(flowPermsOfSpecialElement == null){
					flowPermsOfSpecialElement = new ArrayList<String>();
			}
			flowPermsOfSpecialElement.add(flowPermName);
			map.put(elementOpinion,flowPermsOfSpecialElement);
			
			// 公文元素绑定的排序方式
			String sortType = tempLocal.split("_")[1];
			sortMap.put(elementOpinion,sortType);
		}
		
		Set<String> bound = map.keySet(); //绑定的公文元素
		for(String s:bound){
			tempResult.addAll( edocOpinionDao.findLastSortOpinionBySummaryIdAndPolicy(summary.getId(),
					map.get(s), sortMap.get(s), showLastOptionOnly,true));
		}
		//查询非绑定意见
		tempResult.addAll( edocOpinionDao.findLastSortOpinionBySummaryIdAndPolicy(summary.getId(),
				boundFlowPerm, "0", false,false));
		
		// 根据公文单的edoc_id,policy查询出指定节点的公文元素
		// TODO 同一个人的意见，在勾选了只显示一条的时候，只显示一条

			for (int i = 0; i < tempResult.size(); i++) {
				Object[] object = (Object[]) tempResult.get(i);
				EdocOpinion opinion = (EdocOpinion) object[0];
				String deptName = (String) object[1];

				attitude=null;
				// 公文单不显示暂存待办意见
				if (opinion.getOpinionType() == EdocOpinion.OpinionType.provisionalOpinoin
						.ordinal()) {
					continue;
				}
				if (opinion.getAttribute() > 0) {
					if(ColOpinion.OpinionType.backOpinion.ordinal() ==opinion.getOpinionType().intValue()){
						attitude="stepBack.label";
					}else if(EdocOpinion.OpinionType.repealOpinion.ordinal() == opinion.getOpinionType().intValue()){
						attitude = "col.state.5.cancel";
					}else{
						attitude = metadataManager.getMetadataItemLabel(
								MetadataNameEnum.collaboration_attitude, Integer
										.toString(opinion.getAttribute()));
					}
				}
				if (attitude != null && !"".equals(attitude)) {
					r = ResourceBundle
							.getBundle(
									"com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource",
									CurrentUser.get().getLocale());
					attitude = ResourceBundleUtil.getString(r, attitude);
				} else if (attitude != null
						&& Integer.valueOf(attitude).intValue() == com.seeyon.v3x.edoc.util.Constants.EDOC_ATTITUDE_NULL) {
					attitude = null;
				}
				if (opinion.getOpinionType() == EdocOpinion.OpinionType.senderOpinion
						.ordinal()) {
					opinion.setOpinionAttachments(attHas.get(opinion.getId()));
					senderOpinions.add(opinion);
					attitude = null;
					// continue;
				}
				sb.delete(0, sb.length());
				key = opinion.getPolicy();
				if (key == null) {
					key = summary.getEdocType() == EdocEnum.edocType.recEdoc
							.ordinal() ? "dengji" : "niwen";
				}
				key += "_" + opinion.getId();
				value = hs.get(key);
				if (value != null) {
					sb.append(value.toString());
				}
				if (sb.length() > 0) {
					sb.append("<br>");
				}

				try {
					member = orgManager
							.getMemberById(opinion.getCreateUserId());
					doUserName = member.getName();
					if (member.getIsAdmin()) {
						// 如果是管理员终止，不显示管理员名字及时间
						doUserName = "";
						// doUserName =
						// "<span class='link-blue'>"+Functions.showMemberNameOnly(opinion.getCreateUserId())+"</span>";
					} else {
						doUserName = "<span class='link-blue' onclick='javascript:showV3XMemberCard(\""
								+ opinion.getCreateUserId()
								+ "\")'>"
								+ doUserName + "</span>";
					}

					if (!Strings.isBlank(opinion.getProxyName())) {
						doUserName += ResourceBundleUtil
								.getString(
										"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
										"edoc.opinion.proxy", opinion
												.getProxyName());
					}
				} catch (Exception e) {
					throw new EdocException(e);
				}
				isHidden = (opinion.getIsHidden() == true
						&& opinion.getCreateUserId() != curUser && !curUser
						.equals(sender));
				if (isHidden == false) {
					content = opinion.getContent();
				} else {
					content = ResourceBundleUtil.getString(
							"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
							"edoc.opinion.hide.label");
				}
				if (attitude != null) {
					sb.append("【").append(attitude).append("】");
				}
				// 意见排序 ：【态度】 意见 部门 姓名 时间
				sb.append(" ").append(Strings.toHTML(content));
				if (showDept) {
					sb.append(" ").append(deptName);
				}
				// 如果是管理员终止，不显示管理员名字及时间
				if (!member.getIsAdmin()) {
					sb.append(" ").append(doUserName);
					if (showDateTime) {
						sb.append(" ").append(
								Datetimes.formatDatetimeWithoutSecond(opinion
										.getCreateTime()));
					} else if (showDate) {
						sb.append(" ").append(
								Datetimes.formatDate(opinion.getCreateTime()));
					}
				}
				if (isHidden == false)
				{
					// 增加附件
					tempAtts = attHas.get(opinion.getId());
					if (tempAtts != null)
					{
						sb.append("<br>");
						StringBuffer attSb = new StringBuffer();
						for (Attachment att : tempAtts) {
							// 不管文件名有多长，显示整体的文件名。yangzd
							String s = com.seeyon.v3x.common.filemanager.manager.Util
									.AttachmentToHtmlWithShowAllFileName(att,
											true, false);
							sb.append(s);
							attSb.append(s);
						}
						senderAttMap.put(opinion.getId(), attSb);
						// sb.append("<br>");
					}
				}
				hs.put(key, sb.toString());
			}
			

		hs.put("senderOpinionAttStr",senderAttMap );
		hs.put("senderOpinionList", senderOpinions);
		return hs;
	}
	
	

	public void addInform(Long summaryId, Long affairId, FlowData flowData, String userId)
			throws EdocException {
		Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
        
		EdocSummary summary = edocSummaryDao.get(summaryId);
        if (summary == null) {
            return;
        }
        Affair affair = affairManager.getById(affairId);
        if (affair == null) {
            return;
        }
        Long _workitemId = affair.getSubObjectId();
        Long caseId = summary.getCaseId();

        WorkflowEventListener.setOperationType(WorkflowEventListener.ADD_INFORM);
        List<String> informMem = null ;
        try{
        	informMem = EdocHelper.addInform(caseId, _workitemId, flowData, summary.getProcessId(), userId);
        }catch(Exception e)
        {
        	throw new EdocException(e);
        }
        //知会消息提醒
        List<MessageData> messageDataList = null;
        if(messageDataMap.get(summaryId) == null){
        	 messageDataList = new ArrayList<MessageData>();
        }else{
        	messageDataList = messageDataMap.get(summaryId);
        	messageDataMap.remove(summaryId);
        }
        MessageData messageData = new MessageData();
        messageData.setOperationType("addInform");
        messageData.setHandlerId(Long.parseLong(userId));
        messageData.setEdocSummary(summary);
        messageData.setAffair(affair);
        List<Party> partyList = flowData.getPeople();
        List<String> partyNames = new ArrayList<String>();
        for(Party party : partyList){
        	if(party != null){
        		partyNames.add(party.getName());
        	}
        }
        messageData.setPartyNames(partyNames);

        //Boolean ok = EdocMessageHelper.addInformMessage(userMessageManager, affairManager, orgManager, flowData, summary, affair);
        //流程日志--知会
        String str = this.messageToString(informMem) ;
        messageData.addProcessLogParam(str) ;
        messageDataList.add(messageData);
        messageDataMap.put(summaryId, messageDataList);
	}
	
	
	public void addPassRead(Long summaryId, Long affairId, FlowData flowData) throws EdocException {
		Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
		User user = CurrentUser.get();
		EdocSummary summary = edocSummaryDao.get(summaryId);
		if (summary == null) {
			return;
		}
		Affair affair = affairManager.getById(affairId);
		if (affair == null) {
			return;
		}
		Long _workitemId = affair.getSubObjectId();
		long workitemId = _workitemId.longValue();

		String processId = summary.getProcessId();
		Long caseId = summary.getCaseId();

		WorkflowEventListener.setOperationType(WorkflowEventListener.ADD_INFORM);
		List<String> addPassRead = null ;
		try{
			addPassRead = EdocHelper.addPassRead(caseId, workitemId, flowData, processId, user.getId()+"");
		}catch(Exception e)
		{
			throw new EdocException(e);
		}
		
		//传阅消息提醒
        List<MessageData> messageDataList = null;
        if(messageDataMap.get(summaryId) == null){
        	 messageDataList = new ArrayList<MessageData>();
        }else{
        	messageDataList = messageDataMap.get(summaryId);
        	messageDataMap.remove(summaryId);
        }
        MessageData messageData = new MessageData();
        messageData.setOperationType("addPassRead");
        messageData.setHandlerId(user.getId());
        messageData.setEdocSummary(summary);
        messageData.setAffair(affair);
        List<Party> partyList = flowData.getPeople();
        List<String> partyNames = new ArrayList<String>();
        for(Party party : partyList){
        	if(party != null){
        		partyNames.add(party.getName());
        	}
        }
        String str = this.messageToString(addPassRead) ;
        messageData.addProcessLogParam(str) ;
        messageData.setPartyNames(partyNames);
        messageDataList.add(messageData);
        messageDataMap.put(summaryId, messageDataList);
	}
	
	public int cancelSummary(long userId, long summaryId, int from, String repealComment) throws EdocException {
        return cancelSummary(userId, summaryId, from, true, repealComment, null);
    }

	public int cancelSummary(long userId, long summaryId, String repealComment) throws EdocException {
		return cancelSummary(userId, summaryId, StateEnum.col_cancel.key(), repealComment);
	}
	
	public int cancelSummary(long userId, long summaryId, String repealComment, EdocOpinion edocOpinion) throws EdocException{
		return cancelSummary(userId, summaryId, StateEnum.col_cancel.key(), true, repealComment, edocOpinion);
	}
		
	public int cancelSummary(long userId, long summaryId, int from, boolean sendMessage, String repealComment, EdocOpinion edocOpinion) throws EdocException {
    	int result = 0;
        User user = CurrentUser.get();
        EdocSummary summary = edocSummaryDao.get(summaryId);
        
        WorkflowEventListener.setOperationType(WorkflowEventListener.CANCEL);
        Long caseId = summary.getCaseId();
        if (caseId == null) 
        	return 1;
        
        //将summary的状态改为待发,撤销已生成事项
        List<Affair> affairs = affairManager.getALLAvailabilityAffairList(summaryId, false);
        
        //撤销流程
        try {
			result = EdocHelper.cancelCase(caseId);
		} catch (ColException e1) {
			log.error("", e1);
		}
        if (result == 1) {
            return result;
        }
        
		if(affairs != null){
	    	for(int i=0;i<affairs.size();i++){
	    		Affair affair = (Affair) affairs.get(i);
	        	if(affair.getState()==StateEnum.col_sent.key()){
	        		affair.setState(StateEnum.col_waitSend.key());
			        affair.setSubState(SubStateEnum.col_waitSend_cancel.key());
			        affair.setIsDelete(false);
			        affairManager.updateAffair(affair);
	        	}	        	
	        	
            	if (affair.getDeadlineDate() != null && affair.getDeadlineDate() != 0) {
            		QuartzHolder.deleteQuartzJob("Remind" + affair.getId());
            	}
            	
            	if (affair.getDeadlineDate() != null && affair.getDeadlineDate() != 0) {
            		QuartzHolder.deleteQuartzJob("DeadLine" + affair.getId());
            	}
	    	}
	    	
	    	this.affairManager.cancelWorkflow(summaryId);
		}
        
        summary.setCaseId(null);
        edocSummaryDao.update(summary);
        
		if (edocOpinion != null) {
			edocOpinion.setEdocSummary(summary);
			edocOpinion
					.setCreateTime(new Timestamp(System.currentTimeMillis()));
			edocOpinion.setOpinionType(EdocOpinion.OpinionType.repealOpinion
					.ordinal());
//			affair.setIsTrack(edocOpinion.affairIsTrack);
			edocOpinionDao.save(edocOpinion);
		}else{
			log.info(summary.getSubject()+"撤销时,当前传入EdocOpinion对象为空");
		}
       
        
        try{
             edocStatManager.deleteEdocStat(summaryId);
             this.processLogManager.deleteLog(Long.valueOf(summary.getProcessId())) ;
             this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), -1L, ProcessLogAction.cancelColl) ;
             this.appLogManager.insertLog(user, AppLogAction.Edoc_Cacel, user.getName() ,summary.getSubject()) ;   
        }catch(Exception e){
        	log.error("删除公文统计记录异常",e);
        }
        
        ApplicationCategoryEnum appEnum=EdocUtil.getAppCategoryByEdocType(summary.getEdocType());
       
        String key = "edoc.cancel";
        //对发起人以外的所有执行人发消息通知
        try{
            String userName = "";
            if (user != null) {
                userName = user.getName();
            }
            List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
            List<MessageReceiver> receivers1 = new ArrayList<MessageReceiver>();
            for(Affair affair1 : affairs){
            	Long agentMemberId = null;
            	if(affair1.getIsDelete())
            		continue;
            	if(affair1.getMemberId()==userId){continue;}
            	if(affair1.getState() == StateEnum.col_waitSend.key()){
            		receivers.add(new MessageReceiver(affair1.getId(), affair1.getMemberId(),"message.link.edoc.done",affair1.getId().toString()));
            	}else{
            		agentMemberId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.key(),affair1.getMemberId());
            		if(agentMemberId != null){
            			receivers.add(new MessageReceiver(affair1.getId(), affair1.getMemberId()));
            			receivers1.add(new MessageReceiver(affair1.getId(), agentMemberId));
            		}else
            		    receivers.add(new MessageReceiver(affair1.getId(), affair1.getMemberId()));
            	}
            }
            if(repealComment == null){
            	repealComment = "";
            }
            userMessageManager.sendSystemMessage(new MessageContent(key, affairs.get(0).getSubject(), userName, affairs.get(0).getApp(), repealComment).setImportantLevel(summary.getImportantLevel()), appEnum, user.getId(), receivers);
            List<MessageReceiver> superviseReceivers = EdocSuperviseHelper.getRecieverBySummaryId(summaryId);
            if(null!=superviseReceivers && superviseReceivers.size()>0){
            	//过滤自己，不给自己发消息。
            	List<MessageReceiver> excludeSuperviseReceivers=new ArrayList<MessageReceiver>();
            	for(MessageReceiver messageReceiver :superviseReceivers){
            		if(messageReceiver.getReceiverId()==userId) continue;
            		excludeSuperviseReceivers.add(messageReceiver);
            	}
            	userMessageManager.sendSystemMessage(new MessageContent(key, affairs.get(0).getSubject(), userName, affairs.get(0).getApp(), repealComment).setImportantLevel(summary.getImportantLevel()), appEnum, user.getId(), excludeSuperviseReceivers);
            }
            if(receivers1 != null && receivers1.size() != 0){
				 userMessageManager.sendSystemMessage(new MessageContent(key, affairs.get(0).getSubject(), userName, affairs.get(0).getApp(), repealComment).add("col.agent").setImportantLevel(summary.getImportantLevel()), appEnum, user.getId(), receivers1);		            
			}
            //文号回滚操作:发起人撤销流程后，已经调用的文号（如果是最大号）可以恢复，下次发文时可继续调用
            if(summary.getEdocType()==0){//发文
            	edocMarkManager.edocMarkCategoryRollBack(summary);
            }
            //删除文档中心已归档的公文。
            deleteDocByResources(summary,user);
             
            /**
             * 撤销时删除归档路径
             * 原则：
             * 如果不是模板可以置空归档archiveId
             * 如果是模板（模板没有设置预归档路径）可以置空归档archiveId
             */
            Long archiveId = EdocHelper.getTempletePrePigholePath(summary.getTempleteId(),templeteManager);
            if(summary.getTempleteId() == null || (summary.getTempleteId() != null && archiveId == null)){
            	summary.setArchiveId(null);
            }
            
        }catch (MessageException e) {            
            log.error("send message failed", e);
            throw new EdocException(e);
        }
        log.info("summary is cancelled:" + summaryId);
        return 0;
    }

	public void claimWorkItem(int workItemId) throws EdocException {
		// TODO Auto-generated method stub

	}
    private String messageToString(List<String> list){
		StringBuffer str = new StringBuffer() ;
		if(list != null && list.size() != 0) {
			for(int i = 0 ; i < list.size() ; i++) {
				if( i == 0) {
					str.append(list.get(i)) ;
				}else {
					str.append("," +list.get(i)) ;
				}
			}
		}
		return str.toString() ;
    }
	public void colAssign(Long summaryId, Long affairId, FlowData flowData, String userId)
			throws EdocException {
		Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
	    
		EdocSummary summary = edocSummaryDao.get(summaryId);
        if (summary == null) {
            return;
        }
        Affair affair = affairManager.getById(affairId);
        if (affair == null) {
            return;
        }
        Long _workitemId = affair.getSubObjectId();
        Long caseId = summary.getCaseId();

        WorkflowEventListener.setOperationType(WorkflowEventListener.COL_ASSIGN);
       
        List<String> colAssign = null ;
        try{
        	colAssign = EdocHelper.colAssign(caseId, _workitemId, flowData, summary.getProcessId(), userId);
        }catch(Exception e)
        {
        	throw new EdocException(e);
        }
        //会签消息提醒
        List<MessageData> messageDataList = null;
        if(messageDataMap.get(summaryId) == null){
        	 messageDataList = new ArrayList<MessageData>();
        }else{
        	messageDataList = messageDataMap.get(summaryId);
        	messageDataMap.remove(summaryId);
        }
        MessageData messageData = new MessageData();
        messageData.setOperationType("colAssign");
        messageData.setHandlerId(Long.parseLong(userId));
        messageData.setEdocSummary(summary);
        messageData.setAffair(affair);
        List<Party> partyList = flowData.getPeople();
        List<String> partyNames = new ArrayList<String>();
        
        for(Party party : partyList){
        	if(party != null){
        		partyNames.add(party.getName());
        	}
        }
        String str = this.messageToString(colAssign) ;
        messageData.addProcessLogParam(str) ;
        messageData.setPartyNames(partyNames);
        messageDataList.add(messageData);
        messageDataMap.put(summaryId, messageDataList);
        //Boolean ok = EdocMessageHelper.colAssignMessage(userMessageManager, affairManager, orgManager, flowData, summary, affair);
        //工作流记录

	}

	public void deleteAffair(String pageType, long affairId)
			throws EdocException {
        User user = CurrentUser.get();
        Affair affair = affairManager.getById(affairId);
        if (affair == null)
            return;

        //如果是保存待发，删除个人事项的同时删除整个协同�?
        if (pageType.equals(PAGE_TYPE_DRAFT)) {
            Long summaryId = affair.getObjectId();
            EdocSummary summary = edocSummaryDao.get(summaryId);
            if (summary != null) {
            	edocOpinionDao.deleteOpinionBySummaryId(summaryId);
    			//删除处理意见，手写批注			
    	    	htmlSignDao.delete(new String[]{"summaryId"},new Object[]{summaryId});
            	edocSummaryDao.delete(summary);
            	
            	EdocHelper.deleteQuartzJobOfSummary(summary);
            	if(summary.getEdocType()==0){//发文
            		appLogManager.insertLog(user, AppLogAction.Edoc_WaitSend_Send_Del, user.getName() ,summary.getSubject()) ; 
            	}else if(summary.getEdocType()==1){//收文
            		appLogManager.insertLog(user, AppLogAction.Edoc_WaitSend_Receive_Del, user.getName() ,summary.getSubject()) ;
            	}
            }
        }

        //如果是待办，删除个人事项的同时finishWorkitem
        if (pageType.equals(PAGE_TYPE_PENDING)) {
            Long workitemId = affair.getSubObjectId();
            EdocSummary summary = null;
            EdocOpinion nullColOpinion = new EdocOpinion();
            nullColOpinion.setIdIfNew();
            nullColOpinion.setCreateTime(new Timestamp(System.currentTimeMillis()));
            nullColOpinion.setOpinionType(EdocOpinion.OpinionType.signOpinion.ordinal());
            nullColOpinion.setCreateUserId(user.getId());
            WorkflowEventListener.setOperationType(WorkflowEventListener.COMMONDISPOSAL);
			finishWorkItem(workitemId.intValue(), summary, nullColOpinion,
					null, null, null, null, null, null);
        }

        affairManager.deleteAffair(affair.getId());

	}
	public void pigeonholeAffair(String pageType,Affair affair, Long summaryId,Long archiveId) throws EdocException{
		pigeonholeAffair( pageType, affair,  summaryId, archiveId,true);
	}

	public void pigeonholeAffair(String pageType,Affair affair, Long summaryId,Long archiveId,boolean needcheckFinish) throws EdocException{
		User user = CurrentUser.get();        
		// 下面这个查询可能使用了hibernate的一级缓存，直接从缓存里面取的，所以在lisenter里面需要设置completetime，否则最后一个节点在这里不是结束状态
		EdocSummary summary = this.getEdocSummaryById(summaryId, false); 
		if(archiveId!=null)summary.setArchiveId(archiveId);
		//所有节点都可以归档了。不需要判断发文流程是否结束了。
//		if(needcheckFinish){
//	        if(affair.getApp()==ApplicationCategoryEnum.edocSend.getKey() && summary!=null && summary.getFinished()==false)
//	        {        	
//	        	throw new EdocException(EdocException.errNumEnum.workflow_not_finish.ordinal(),summary.getSubject());
//	        }
//		}
        if (summary != null && summary.getHasArchive()==false) 
        {//公文只归档一次，未归档才进行归档
        	Map<String, Object> colums = new HashMap<String, Object>();
        	colums.put("hasArchive", true);
        	this.edocSummaryDao.update(summaryId, colums);
        	
        	try{
        		edocStatManager.setArchive(summary.getId());
        	}
        	catch(Exception e)
        	{        		
        	}
        	
        	//公文督办的操作,删除该公文的督办项 -- start --
        	//edocSuperviseManager.pigeonhole(summary);
        	// -- end --
        
        	try{
        		boolean hasAtt=(summary.isHasAttachments());
        		Long folderId=docHierarchyManager.pigeonholeEdoc(summary, hasAtt); 
        		
        		//发文封发时候的归档直接删除已发已办事项,其他情况的归档都不删除已发已办事项
        		if(("fengfa".equals(affair.getNodePolicy()) 
        				&& affair.getApp() == ApplicationCategoryEnum.edocSend.getKey())
        				||summary.getFinished()){
        			
        		     setArchiveIdToAffairsAndSendMessages(summary,affair,true);
        		     
        			 try{
    				    String params = summary.getSubject() ;
    				    Long activityId = affair.getActivityId();
    				    if(activityId==null){
    				    	BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(affair);//当前节点
    				    	if(bPMActivity != null)
    				    		activityId = Long.valueOf(bPMActivity.getId());
    				    }
    				    if(activityId != null){
    				    	this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), activityId.longValue(), ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.pigeonhole.getKey()),params);
    				    }else {
    				    	this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), -1l, 					 ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.pigeonhole.getKey()),params);
    				    }
    				    this.appLogManager.insertLog(user, AppLogAction.Edoc_PingHole, user.getName() ,summary.getSubject()) ;       		    
                    } catch (Exception e) {
                    	log.error("发送消息错误",e);
                    }
        		}
        		
        	}catch(Exception e)
        	{        	
        		log.error("公文归档错误",e);
        	}
        }           
	}
	public void setArchiveIdToAffairsAndSendMessages(EdocSummary summary,Affair affair,boolean needSendMessage){
		 
		Map<String,Object> parameter=new HashMap<String,Object>();
         parameter.put("archiveId",summary.getArchiveId());
         affairManager.updateAllAvailabilityAffair(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()), 
        		 summary.getId(), parameter);
         
         //删除已发已办事项的时候，删除公文在全文检索的记录。
         try{
        	 UpdateIndexManager updateIndexManager = (UpdateIndexManager)ApplicationContextHolder.getBean("updateIndexManager");
        	 updateIndexManager.getIndexManager().deleteFromIndex(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()), summary.getId());
         }catch(Exception e){
        	 log.error("公文归档删除已发已办事项的时候，删除全文检索异常",e);
         }
         
 		//发送系统消息告诉流程总的节点,"收文|发文|签报《****》流程结束，已经被归档到’公文档案\审计部’下，从已发、已办中删除"
        if(needSendMessage){
            DocResource doc = docHierarchyManager.getDocResourceById(summary.getArchiveId());
            String pigeonholePath=docHierarchyManager.getPhysicalPathDetail(doc.getLogicalPath(), java.io.File.separator, false, 0);
            EdocMessageHelper.processFinishedAutoPigeonhole(affairManager,userMessageManager 
        			,summary,affair,orgManager,
        			pigeonholePath,processLogManager,appLogManager);
        }
	}
	public void pigeonholeAffair(String pageType,long affairId, Long summaryId,Long archiveId) throws EdocException{
		Affair affair = affairManager.getById(affairId);
        if (affair == null){return;}
        pigeonholeAffair(pageType,affair, summaryId,archiveId);
	}
	public void pigeonholeAffair(String pageType,long affairId, Long summaryId) throws EdocException
	{
        pigeonholeAffair(pageType,affairId, summaryId,null);
	}
	public void pigeonholeAffair(String pageType, Affair affair, Long summaryId) throws EdocException
	{
		pigeonholeAffair(pageType,affair,summaryId,null);
	}
	
	private List<Affair> findSendAndWaitDoneAffair(ApplicationCategoryEnum appEnum,Long summaryId)
	{
		List <Affair>ls=new ArrayList();
		ls=affairManager.findAvailabilityByObject(appEnum,summaryId);
		if(ls!=null)
		{
			Affair temp=null;
			int len=ls.size();
			for(;len>0;len--)
			{
				temp=ls.get(len-1);
				if(temp.getState()==StateEnum.col_pending.getKey())
				{
					ls.remove(len-1);
				}
			}
		}		
		return ls;
	}

	public FlowData deletePeople(long summaryId, long affairId,
			List<Party> parties, String userId) throws EdocException {
		Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
    	EdocSummary summary = edocSummaryDao.get(summaryId);
        if (summary == null) {
            return null;
        }
        Affair affair = affairManager.getById(affairId);
        if (affair == null) {
            return null;
        }
        Long caseId = summary.getCaseId();
        Long _workitemId = affair.getSubObjectId();
        FlowData flowData = null;
        try{
        WorkflowEventListener.setOperationType(WorkflowEventListener.DELETE);
        flowData=EdocHelper.deletePeople(caseId, _workitemId, parties, summary.getProcessId(), userId);
        }catch(Exception e)
        {
        	throw new EdocException(e);
        }
        //减签消息提醒
        List<MessageData> messageDataList = null;
        if(messageDataMap.get(summaryId) == null){
        	 messageDataList = new ArrayList<MessageData>();
        }else{
        	messageDataList = messageDataMap.get(summaryId);
        	messageDataMap.remove(summaryId);
        }
        MessageData messageData = new MessageData();
        messageData.setOperationType("deletePeople");
        messageData.setHandlerId(Long.parseLong(userId));
        messageData.setEdocSummary(summary);
        messageData.setAffair(affair);
        List<String> partyNames = new ArrayList<String>();
        List<String[]> parame = new ArrayList<String[]>() ;
        StringBuffer processLogActionParam = new StringBuffer() ;     
        if(parties != null){
        	for(int i = 0 ; i < parties.size() ;i++){
        		Party party = parties.get(i) ;
            	if(party != null){
            		partyNames.add(party.getName());
            		if(i == parties.size() -1){
                		processLogActionParam.append(party.getName()).append("(").
                		append(party.getSeeyonPolicy().getName()).append(")") ;	            			
            		}else{
                		processLogActionParam.append(party.getName()).append("(").
                		append(party.getSeeyonPolicy().getName()).append(")").append(",") ;	           			
            		}	
            	}        		
        	}
        }
        /**
        for(Party party : parties){
        	if(party != null){
        		partyNames.add(party.getName());
        		processLogActionParam.append(party.getName()).append("(").
        		append(party.getSeeyonPolicy().getName()).append(")").append(",") ;		
        	}
        }**/
        String[] str = new String[1] ;
        str[0] = processLogActionParam.toString() ;
        parame.add(str) ;
        messageData.setProcessLogParam(parame) ;
        messageData.setPartyNames(partyNames);
        messageDataList.add(messageData);
        messageDataMap.put(summaryId, messageDataList);
 
        return flowData;
	}

	public String finishWorkItem(EdocSummary summary,long affairId, EdocOpinion signOpinion,
 Map<String, String[]> manualMap,
			Map<String, String> condition, String processId, String userId,
			String edocMangerID) throws EdocException {

	        Affair affair=affairManager.getById(affairId);
	        Long workItemId = affair.getSubObjectId();
	        if(affair.getState() != StateEnum.col_pending.key()){
				String msg=EdocHelper.getErrorMsgByAffair(affair);
	        	throw new EdocException(msg);	        
	        }
	        WorkflowEventListener.setEdocSummary(summary);
			WorkflowEventListener.setOperationType(WorkflowEventListener.COMMONDISPOSAL);
			//将affairId传递到onProcessFinished方法
			WorkflowEventListener.setFinishAffairId(affairId);
		return finishWorkItem(workItemId, summary, signOpinion, manualMap,
				condition, affairId, processId, userId, edocMangerID);
	}
	
	public String finishWorkItem(EdocSummary summary,long affairId, EdocOpinion signOpinion,
		   Map<String, String[]> manualMap,
		   Map<String, String> condition, String title,
		   String supervisorMemberId, String supervisorNames,
		   String superviseDate, String processId, String userId,
		   String edocMangerID) throws EdocException {
		  	
			//督办开始--
        	this.edocSupervise(title, supervisorMemberId, supervisorNames, superviseDate, summary);
        	//督办结束--  
        	
        	return finishWorkItem(summary, affairId, signOpinion, manualMap, condition, processId, userId, edocMangerID);
	}
	
	/**
	 * 公文交换规则
	 *单位交换：
	 *在封发节点选择拟文人拟文时所登录单位的单位公文收发员，如果没有设置单位收发员，给出提示框。
	 *部门交换：
	 *拟文人拟文时所登录的单位下，如果该拟文人主岗、兼职、副岗所在的部门数目大于一个弹出选择界面，
	 *选择交换到的部门，如果交换到的部门下没有设置部门公文收发员，给出提示框。如部门等于一个，直
	 *接交换到该部门，如果该部门下没有设置部门公文收发员，给出提示。
	 * @param workItemId
	 * @param summary
	 * @param signOpinion
	 * @param manualMap
	 * @param condition
	 * @param affairId
	 * @param processId
	 * @param userId
	 * @param edocMangerID
	 * @return
	 * @throws EdocException
	 */
	private String finishWorkItem(long workItemId, EdocSummary summary,
			EdocOpinion signOpinion, Map<String, String[]> manualMap,
			Map<String, String> condition, Long affairId, String processId,
			String userId, String edocMangerID) throws EdocException {
    	Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
    	String ret="";
    	Affair affair = affairManager.getBySubObject(Long.parseLong(workItemId+""));
        User user = CurrentUser.get();
        BPMProcess process = null ; 
        //持久化修改后的流程
    	if(processId != null && userId != null){
    		try{
    			process = ColHelper.saveModifyingProcess(processId, userId);
	    		if(process != null){
	    			ColHelper.updateRunningProcess(process);
	    			//如果该流程实例存在待添加的节点，将其激活
	    			ColHelper.saveAcitivityModify(process, userId);
	    			//更新完流程之后,发送消息提醒
	    			List<MessageData> messageDataList = messageDataMap.get(summary.getId());
	    			if(messageDataList != null){
		    			for(MessageData messageData : messageDataList){
		    				if(Long.parseLong(userId) == messageData.handlerId){
			    				String operationType = messageData.getOperationType();
			    				List<String> partyNames = messageData.getPartyNames();
			    				EdocSummary edocSummary = messageData.getEdocSummary();
			    				Affair _affair = messageData.getAffair();
			    				if("insertPeople".equals(operationType)){		
			    					EdocMessageHelper.insertPeopleMessage(affairManager, userMessageManager, orgManager, partyNames, edocSummary, _affair);			    					
			    				    BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点
			    				    List<String[]> processLogParam =  messageData.getProcessLogParam() ;
			    				    for(String[] param : processLogParam) {
			    				    	this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.insertPeople, param);
			    				    }
			    				    
			    				}else if("deletePeople".equals(operationType)){
			    					EdocMessageHelper.deletePeopleMessage(affairManager, orgManager, userMessageManager, partyNames, edocSummary, _affair);
			    					BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点
			    					List<String[]> processLogParam = messageData.getProcessLogParam();
			    					for(String[] param : processLogParam){
			    						processLogManager.insertLog(user, Long.parseLong(process.getId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.deletePeople, param);
			    					}
			    				}else if("colAssign".equals(operationType)){
			    					EdocMessageHelper.colAssignMessage(userMessageManager, affairManager, orgManager, partyNames, edocSummary, _affair);
			    				    List<String[]> processLogParam =  messageData.getProcessLogParam() ;
			    				    BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点			    				   
			    				    for(String[] param : processLogParam) {
			    				    	this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.colAssign, param);
			    				    }
			    				}else if("addInform".equals(operationType)){
			    					EdocMessageHelper.addInformMessage(userMessageManager, affairManager, orgManager, partyNames, edocSummary, _affair);
			    				    List<String[]> processLogParam =  messageData.getProcessLogParam() ;
			    				    BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点
			    				    for(String[] param : processLogParam) {
			    				    	this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.inform, param);
			    				    } 					
			    				}else if("addPassRead".equals(operationType)){
			    					EdocMessageHelper.addPassReadMessage(userMessageManager, affairManager, orgManager, partyNames, edocSummary, _affair);
			    				    List<String[]> processLogParam =  messageData.getProcessLogParam() ;
			    				    BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点
			    				    for(String[] param : processLogParam) {
			    				    	this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.passRound, param);
			    				    }
			    				}
			    				else if("addMoreSign".equals(operationType)){
			    					EdocMessageHelper.addMoreSignMessage(affairManager, userMessageManager, orgManager, partyNames, edocSummary, _affair);		
			    					BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点
			    					List<String[]> processLogParam =  messageData.getProcessLogParam() ;
			    					for(String[] param : processLogParam) {
			    						this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.addMoreSign, param);
			    					 }			    				    
			    				}			    				
		    				}
		    			}
		    			messageDataMap.remove(messageDataList);
	    			}
	    		}
    		}catch(Exception e){
    			log.error("", e);
    		}
    	}
    	String policy=getPolicyByAffair(affair); 
    	BPMActivity bPMActivity =  null;
        try{
        	bPMActivity = EdocHelper.getBPMActivityByAffair(affair);//当前节点
        	if(null!=bPMActivity){
	        	if(NumberUtils.isNumber(bPMActivity.getId())){
	        		signOpinion.setNodeId(Long.parseLong(bPMActivity.getId()));
	        	}
	        	String params = EdocHelper.checkNextNodeMembers(bPMActivity,condition) ; 
	        	//封发节点，日志操作描述记录公文时交换到部门收发员还是单位收发员。
	        	if("fengfa".equals(policy)){
		        	params = getParams(signOpinion, params);
		       	    this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.seal, params);	      	
	        	}else{
	           	    this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.commit, params);	      	
	        	}
        	}
        }catch(Exception e){
        	log.error("", e) ;
        }

    	
        if(manualMap!=null && !manualMap.isEmpty()){
            EdocHelper.setActivityManualSelect(workItemId,manualMap);                        
        }
        
        if(condition != null && !condition.isEmpty()){
	    	try{
	    	EdocHelper.setActivityIsDelete(workItemId, condition);
	    	}catch(Exception e)
	    	{
	    		throw new EdocException(e);
	    	}
	    }
        boolean upd=false;
        Map<String,Object> namedParameter = new HashMap<String,Object>();
        try{
        	//EdocHelper.finishWorkitem(workItemId);
        	//String policy=getPolicyByAffair(affair);        	
	        if("qianfa".equals(policy))
	        {	        	
	        	String issuerName = user.getName();
        		try{
        			issuerName = orgManager.getMemberById(affair.getMemberId()).getName();
        		}catch(Exception e){
        			log.error("查找人员错误", e);
        		}
        		String issuserStr=summary.getIssuer();
	        	if(Strings.isNotBlank(issuserStr)){
	        		if(issuserStr.indexOf(issuerName)!=-1){
	        			issuerName=issuserStr;
	        		}else{
	        			String separator = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.separator.label");
	        			issuerName+=separator+summary.getIssuer();
	        		}
	        	}
	        	summary.setIssuer(issuerName);
        		edocStatManager.updateElement(summary);
        		namedParameter.put("issuer", issuerName);
        		upd=true;
	        	//如果有多人签发，则取最后一个签发节点审批的时间为签发时间
	        	summary.setSigningDate(new Date(System.currentTimeMillis()));
	        	namedParameter.put("signingDate", new Date(System.currentTimeMillis()));
	        	upd=true;	
	        }
	        if("fengfa".equals(policy)||signOpinion.exchangeType>=0)
	        {
	        	//	封发的时候进行相关的问号操作，移动到历史表中。tdbug28578 以封发节点完成提交，作为流程结束标志。
	       	    edocMarkHistoryManager.afterSend(summary);
	       		summary.setFinished(true);
	       		Timestamp now=new Timestamp(System.currentTimeMillis());
	       	    if(summary.getPackTime() == null){
	       	    	summary.setPackTime(now);
	       	    	namedParameter.put("packTime", now);
	       	    }
	        	namedParameter.put("completeTime", now);	        	
	        	namedParameter.put("state", Constant.flowState.finish.ordinal());
	        	upd=true;
	        	
	            if(summary.getHasArchive() && summary.getEdocType() == EdocEnum.edocType.sendEdoc.ordinal())
	            	setArchiveIdToAffairsAndSendMessages(summary,affair,true);
	        }	        
        }catch(Exception e)
        {
        	throw new EdocException(e);
        }
        if (summary != null) {
            signOpinion.setEdocSummary(summary);
            signOpinion.setCreateTime(new Timestamp(System.currentTimeMillis()));
            signOpinion.setOpinionType(EdocOpinion.OpinionType.signOpinion.ordinal());
            if(Strings.isBlank(signOpinion.getPolicy()))
            		signOpinion.setPolicy(affair.getNodePolicy());
            affair.setIsTrack(signOpinion.affairIsTrack);
            
            
            if(affair.getMemberId() != user.getId()){
            	List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(user.getId());
            	if(agentModelList != null && !agentModelList.isEmpty()){
            		signOpinion.setCreateUserId(affair.getMemberId());
                	signOpinion.setProxyName(user.getName());
            	}else{
            		signOpinion.setCreateUserId(user.getId());
            	}
                //非本人处理的，写入处理人ID
                affair.setTransactorId(user.getId());
            }else{
            	signOpinion.setCreateUserId(user.getId());
            }
            
            //signOpinion.setCreateUserId(user.getId());
            edocOpinionDao.save(signOpinion);

            if (signOpinion.isDeleteImmediate) {
                affairManager.deleteAffair(affair.getId());
                //updateEdocIndex(summary.getId());            
            }
            if (signOpinion.affairIsTrack) {
                affairManager.updateAffair(affair);
            }

//            if(signOpinion.getIsHidden())
            
//          处理页面是否包含交换设置
	        if(signOpinion.exchangeType>=0)
	        {
	        	Long unitId=-1L;
	        	if(signOpinion.exchangeType==Constants.C_iExchangeType_Dept){
	        		//部门交换的时候，由于edocManagerID为空，使用edocManagerID来传递选择的要交换的部门ID值
	        		if(Strings.isBlank(edocMangerID))  
	        			unitId= affair.getSender().getOrgDepartmentId();
	        		else
	        			unitId=Long.valueOf(edocMangerID);
	        	}else if(signOpinion.exchangeType==Constants.C_iExchangeType_Org){
	        		unitId=summary.getOrgAccountId();
	        	}
	        	try{
	        		 //保存PDF正文
	    	        addPdfBodyToCurrentSummary(summary);
					sendEdocManager.create(summary, unitId,
							signOpinion.exchangeType, edocMangerID);
	        		edocSuperviseManager.updateBySummaryId(summary.getId());//更新督办结束状态
	        		//更新公文统计表为封发
	        		edocStatManager.setSeal(summary.getId());
	        	}catch(Exception e)
	        	{
	        		log.error("生成公文统计表错误",e);
	        		throw new EdocException(e);
	        	}
	        	summary.setFinished(true);upd=true;
	        	namedParameter.put("completeTime", new Timestamp(System.currentTimeMillis()));
	        }
	        
	        if(upd){
	        	/*edocSummaryDao.update(summary);
	        	edocSummaryDao.forceCommit();*/
	        	edocSummaryDao.update(summary.getId(), namedParameter);
	        }
	       
	        DateSharedWithWorkflowEngineThreadLocal.setFinishWorkitemOpinionId(signOpinion.getId(), signOpinion.getIsHidden(), signOpinion.getContent(), signOpinion.getAttribute(), signOpinion.isHasAtt());
	        if(null!=bPMActivity){
		        try {
		        	Map<String, Object> data = new HashMap<String, Object>();
		        	data.put("CurrentActivity", bPMActivity);
		    		data.put("currentWorkitemId", new Long(workItemId));//当前处理者
		        	EdocHelper.finishWorkitemWithContext(workItemId, data); //这里面有session.flush，故必须放在最后
		        }catch(Exception e) {
		        	log.error(e);
		        	throw new EdocException(e);
		        }
	        }else{//兼容处理
	        	//将workitem修改为已办状态
            	ColHelper.finishItem(workItemId);
            	//将affair更改为已办状态
            	affair.setState(StateEnum.col_done.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
            	Timestamp now = new Timestamp(System.currentTimeMillis());
                affair.setCompleteTime(now);
                affair.setUpdateDate(now);
                //设置运行时长，超时时长等
                //setTime2Affair(affair);
                affairManager.updateAffair(affair);
                //发送完成事项消息提醒
                EdocMessageHelper.workitemFinishedMessage(affairManager, orgManager, this, userMessageManager, affair, summary.getId());
	        }
	        
	        //归档处理
	        if(signOpinion.isPipeonhole)
	        {
	        	pigeonholeAffair("Pending",affairId, summary.getId());  //这里面有session.flush，故必须放在最后
	        	//updateEdocIndex(summary.getId());
	        	//ret="pigeonhole";
	        	//更新统计表归档
	        	try{edocStatManager.setArchive(summary.getId());}catch(Exception e)
	        	{
	        		log.error("更新归档信息错误，summaryId＝"+summary.getId(),e);
	        		throw new EdocException(e);
	        	}
	        }	
	        if(summary.getHasArchive() 
	        		&& summary.getEdocType() == EdocEnum.edocType.sendEdoc.ordinal()
	        		&& summary.getFinished()){//发文归档并且流程结束的时候删除后续节点的已办事项
	        	
	        		affair=affairManager.getById(affairId);
		        	if(affair.getArchiveId()==null)
		        	{
		        		affair.setArchiveId(summary.getArchiveId());
			        	affairManager.updateAffair(affair);
		        	}	        	
		        	ret="pigeonhole";
	        }
	     }
       return ret;
    }
	
//	public void updateEdocIndex(Long summaryId){
//		if(summaryId != null ){
//			try{
//				updateIndexManager.update(summaryId, ApplicationCategoryEnum.edoc.getKey());
//			}catch(Exception e){
//				log.error("公文更新全文检索信息异常",e);
//			}
//		}
//	}
    private void addPdfBodyToCurrentSummary(EdocSummary summary){
    	Set<EdocBody> bodies=summary.getEdocBodies();
    	for(EdocBody body:bodies){
    		if(body.getContentNo()==EdocBody.EDOC_BODY_PDF_ONE ||
    				body.getContentNo()==EdocBody.EDOC_BODY_PDF_TWO){
    			edocBodyDao.save(body);
    		}
    	}
    }
	private String getParams(EdocOpinion signOpinion, String params) {
		if(signOpinion.exchangeType>=0){
			if(signOpinion.exchangeType==Constants.C_iExchangeType_Dept)
			{
				params=ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources", "sys.role.rolename.department_exchange");
			}
			else if(signOpinion.exchangeType==Constants.C_iExchangeType_Org)
			{
				params=ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources", "sys.role.rolename.account_exchange"); 
			}
		}
		return params;
	}
    
    /**
     * 重载了督办的方法,添加3个督办的参数
     * @param workItemId
     * @param summary
     * @param signOpinion
     * @param manualMap
     * @param affairId
     * @param remindMode 督办类型
     * @param supervisorMemberId 督办人员id
     * @param superviseDate 督办时间
     * @return
     * @throws EdocException
     */
//	private String finishWorkItem(Long workItemId, EdocSummary summary,
//			EdocOpinion signOpinion, Map<String, String[]> manualMap,
//			Map<String, String> condition, Long affairId, String title,
//			String supervisorMemberId, String supervisorNames,
//			String superviseDate, String processId, String userId,
//			String edocMangerID) throws EdocException {
//			
//			this.finishWorkItem(summary, affairId, signOpinion, manualMap, condition, processId, userId, edocMangerID);
//			//督办开始--
//	        this.edocSupervise(title, supervisorMemberId, supervisorNames, superviseDate, summary);
//	        //督办结束--  
//	}

	
	public String getCaseLogXML(long caseId) throws EdocException {
	    String userId = CurrentUser.get().getId() + "";
	    String xml="";
	    try{
	    	xml=EdocHelper.getCaseLogXML(userId, caseId);
	    }catch(Exception e)
	    {
	    	throw new EdocException(e);
	    }
	    return xml;
	}

	public String getCaseProcessXML(long caseId) throws EdocException {
	    String userId = CurrentUser.get().getId() + "";
	    String xml="";
	    try{
	    	xml=EdocHelper.getCaseProcessXML(userId, caseId);
	    }catch(Exception e)
	    {
	    	throw new EdocException(e);
	    }
	    return xml;         		
	}

	public String getCaseWorkItemLogXML(long caseId) throws EdocException {
	    String userId = CurrentUser.get().getId() + "";
	    String xml="";
	    try{
	    	xml=EdocHelper.getCaseWorkItemLogXML(userId, caseId);
	    }catch(Exception e)
	    {
	    	throw new EdocException(e);
	    }
	    return xml;   
	}

	public EdocSummary getColAllById(long summaryId) throws EdocException {
		EdocSummary summary = getEdocSummaryById(summaryId,false);
		if(summary == null) return null;
    	if(summary.getEdocOpinions()!=null)summary.getEdocOpinions().size();
    	if(summary.getEdocBodies()!=null) summary.getEdocBodies().size();
            summary.getFirstBody();
        return summary;
	}

	public EdocBody getEdocBody(long summaryId) throws EdocException {
		// TODO Auto-generated method stub
		return null;
	}

	public EdocSummary getEdocSummaryById(long summaryId, boolean needBody)
			throws EdocException {
		EdocSummary summary = edocSummaryDao.get(summaryId);
       if(summary!=null){
			try {
	        	
	        	summary.checkSendUnitData();
	        	
	            V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
	            summary.setStartMember(member);
	        }
	        catch (BusinessException e) {
	            log.error("读取公文主体属性时查询发起人错误", e);
	        }
	
	        if (needBody) {
	            summary.getEdocBodies().size();
	//            ColBody body = (ColBody) this.getHibernateTemplate()
	//                    .iterate("from "+ColBody.class.getName()+" as body where body.summaryId = ?", summaryId)
	//                    .next();
	//            summary.getBodies().add(body);
	        }
       }
        return summary;		
	}

	public String getPolicyByAffair(Affair affair) throws EdocException {
		if(Strings.isNotBlank(affair.getNodePolicy())){
			return affair.getNodePolicy();
		}
		
        SeeyonPolicy p = null;
        try{
        	p=EdocHelper.getPolicyByAffair(affair);
        }catch(Exception e)
        {
        	throw new EdocException(e);
        }
        if (p == null)
            return SeeyonPolicy.DEFAULT_POLICY;
        String policy = p.getId();
        return policy;
	}

	public String getPolicyBySummary(EdocSummary summary) throws EdocException {
		summary.setCompleteTime(new Timestamp(System.currentTimeMillis()));
        edocSummaryDao.update(summary);
		return null;
	}

	public String getProcessXML(String processId) throws EdocException {
		String xml = "";
		try{
		xml=EdocHelper.getProcessXML(processId);
		}catch(Exception e)
		{
			throw new EdocException(e);
		}
        xml = EdocHelper.trimXMLProcessor(xml);
        return xml;
	}

	public EdocSummary getSummaryByCaseId(long caseId) throws EdocException {
		EdocSummary summary = edocSummaryDao.getSummaryByCaseId(caseId);
		try {
			V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
			summary.setStartMember(member);
		}
		catch (BusinessException e) {
			log.error(e);
			throw new EdocException(e);
		}
		return summary;
	}

	public Long getSummaryIdByCaseId(long caseId) throws ColException {
		List<Object> list = edocSummaryDao.find("select id from EdocSummary where caseId=?", -1, -1, null, caseId);
		
		if(list != null && !list.isEmpty()){
			return (Long)(list.get(0));
		}
		
		return null;
	}
	
	public EdocSummary getSummaryByWorkItemId(int workItemId)
			throws EdocException {
		// TODO Auto-generated method stub
		return null;
	}

	public void hasten(String processId, String activityId,
			String additional_remark) throws EdocException {
		// TODO Auto-generated method stub

	}

	public void insertPeople(EdocSummary summary, Affair affair, FlowData flowData, BPMProcess process, String userId,String operationType)
		throws EdocException {
		Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
		User user = CurrentUser.get();
		List<String> memAndPolicy = null ;
		try {
			memAndPolicy = EdocHelper.insertPeople(summary.getCaseId(), affair.getSubObjectId(), flowData, process, userId, false);
		} catch (NumberFormatException e) {
			log.error("", e);
		} catch (ColException e) {
			log.error("", e);
		}
		//增加自己不发消息
		if("addMoreSignSelf".equals(operationType)){return;}
		//加签消息提醒
		List<MessageData> messageDataList = null;
		Long summaryId = summary.getId();
		if(messageDataMap.get(summaryId) == null){
			 messageDataList = new ArrayList<MessageData>();
		}else{
			messageDataList = messageDataMap.get(summaryId);
			messageDataMap.remove(summaryId);
		}
		MessageData messageData = new MessageData();
		messageData.setOperationType(operationType);
		messageData.setHandlerId(Long.parseLong(userId));
		messageData.setEdocSummary(summary);
		messageData.setAffair(affair);
		List<Party> partyList = flowData.getPeople();
		List<String> partyNames = new ArrayList<String>();
		for(Party party : partyList){
			if(party != null){
				partyNames.add(party.getName());
			}
		}
		messageData.setPartyNames(partyNames);
        String str = this.messageToString(memAndPolicy)	;
		messageData.addProcessLogParam(str);
		messageDataList.add(messageData);
		messageDataMap.put(summaryId, messageDataList);
	}

	public FlowData preDeletePeople(long summaryId, long affairId, String processId, String userId)
			throws EdocException {
		EdocSummary summary = edocSummaryDao.get(summaryId);
        if (summary == null) {
            return null;
        }
        Affair affair = affairManager.getById(affairId);
        if (affair == null) {
            return null;
        }
        Long caseId = summary.getCaseId();
        Long _workitemId = affair.getSubObjectId();
        long workitemId = _workitemId;
        FlowData flowData = null;
        try{
        flowData=EdocHelper.preDeletePeople(caseId, workitemId, processId, userId);
        }catch(Exception e)
        {
        	throw new EdocException(e);
        }
        return flowData;
	}
	
	private static final String selectSummary = "summary.id," +
			"summary.startUserId," +
			"summary.caseId," +
			"summary.completeTime,"+
			"summary.subject," +
			"summary.secretLevel," +
			"summary.identifier," +
			"summary.docMark," +
			"summary.serialNo," +
			"summary.createTime,"+
			"summary.sendTo," +
			"summary.issuer," +
			"summary.signingDate," +
			"summary.deadline," +
			"summary.startTime," +
			"summary.copies," +
			"summary.createPerson," +
			"summary.sendUnit," +
			"summary.hasArchive," + 
			"summary.processId," +
			"summary.caseId,"+
			"summary.urgentLevel, " +
			"summary.templeteId, "+
			"summary.state, " +
			"summary.copyTo, " +
			"summary.reportTo,"+
			"summary.archiveId,"+
			"summary.edocType, "+
			"summary.docMark2,"+
			"summary.sendTo2 ";
	
	private static final String selectAffair = selectSummary+
		",affair.id," +
		"affair.state," +
		"affair.subState," +
		"affair.isTrack," +
		"affair.hastenTimes," +
		"affair.isOvertopTime," +
		"affair.remindDate," +
		"affair.deadlineDate," +
		"affair.receiveTime," +
		"affair.completeTime," +
		"affair.createDate," +
		"affair.memberId," +
		"affair.bodyType," +
		"affair.transactorId,"+
		"affair.nodePolicy";

	private static void make(Object[] object, EdocSummary summary, Affair affair)
	{
		int n = 0;
		summary.setId((Long)object[n++]);
		summary.setStartUserId((Long)object[n++]);
        summary.setCaseId((Long)object[n++]);
        summary.setCompleteTime((Timestamp)object[n++]);        
        summary.setSubject((String)object[n++]);
        summary.setSecretLevel((String)object[n++]);
        summary.setIdentifier((String)object[n++]);
        summary.setDocMark((String)object[n++]);
        summary.setSerialNo((String)object[n++]);
        summary.setCreateTime((Timestamp)object[n++]);
        summary.setSendTo((String)object[n++]);
        summary.setIssuer((String)object[n++]);
        summary.setSigningDate((Date)object[n++]);
        summary.setDeadline((Long)object[n++]);
        summary.setStartTime((Timestamp)object[n++]);
        summary.setCopies((Integer)object[n++]);
        summary.setCreatePerson((String)object[n++]);
        summary.setSendUnit((String)object[n++]);
        summary.setHasArchive((Boolean)object[n++]);
        summary.setProcessId((String)object[n++]);
        summary.setCaseId((Long)object[n++]);
        summary.setUrgentLevel((String)object[n++]);
        summary.setTempleteId((Long)object[n++]);
        summary.setState((Integer)object[n++]);
        summary.setCopyTo((String)object[n++]);
        summary.setReportTo((String)object[n++]);
        summary.setArchiveId((Long)object[n++]);
        summary.setEdocType((Integer)object[n++]);
        summary.setDocMark2((String)object[n++]);
        summary.setSendTo2((String)object[n++]);
        
        affair.setId((Long)object[n++]);
		affair.setState((Integer)object[n++]);
		affair.setSubState((Integer)object[n++]);
		affair.setIsTrack((Boolean)object[n++]);
		affair.setHastenTimes((Integer)object[n++]);
		affair.setIsOvertopTime((Boolean)object[n++]);
		affair.setRemindDate((Long)object[n++]);
		affair.setDeadlineDate((Long)object[n++]);
		affair.setReceiveTime((Timestamp)object[n++]);
		affair.setCompleteTime((Timestamp)object[n++]);
		affair.setCreateDate((Timestamp)object[n++]);
		affair.setMemberId((Long)object[n++]);
		affair.setBodyType((String)object[n++]);
		affair.setTransactorId((Long)object[n++]);
		affair.setNodePolicy((String)object[n++]);	
		affair.setSubject(summary.getSubject());
	}
	private static void make(Object[] object, EdocSummary summary)
	{
		int n = 0;
		summary.setId((Long)object[n++]);
		summary.setStartUserId((Long)object[n++]);
        summary.setCaseId((Long)object[n++]);
        summary.setCompleteTime((Timestamp)object[n++]);        
        summary.setSubject((String)object[n++]);
        summary.setSecretLevel((String)object[n++]);
        summary.setIdentifier((String)object[n++]);
        summary.setDocMark((String)object[n++]);
        summary.setSerialNo((String)object[n++]);
        summary.setCreateTime((Timestamp)object[n++]);
        summary.setSendTo((String)object[n++]);
        summary.setIssuer((String)object[n++]);
        summary.setSigningDate((Date)object[n++]);
        summary.setDeadline((Long)object[n++]);
        summary.setStartTime((Timestamp)object[n++]);
        summary.setCopies((Integer)object[n++]);
        summary.setCreatePerson((String)object[n++]);
        summary.setSendUnit((String)object[n++]);
        summary.setHasArchive((Boolean)object[n++]);
        summary.setProcessId((String)object[n++]);
        summary.setCaseId((Long)object[n++]);
        summary.setUrgentLevel((String)object[n++]);
        summary.setTempleteId((Long)object[n++]);
        summary.setState((Integer)object[n++]);
        summary.setCopyTo((String)object[n++]);
        summary.setReportTo((String)object[n++]);
        summary.setArchiveId((Long)object[n++]);
        summary.setEdocType((Integer)object[n++]);
        summary.setDocMark2((String)object[n++]);
        summary.setSendTo2((String)object[n++]);
	}
	
	/**
	 * 供公文统计使用，仅查询出秘密级别
	 * @param ids
	 * @return
	 */
	public Hashtable<Long,EdocSummary> queryBySummaryIds(List<Long> ids)
	{
		Hashtable<Long,EdocSummary> hs=new Hashtable<Long,EdocSummary>();
		String hsql="select id,secretLevel,archiveId from EdocSummary as summary where id in (:ids)";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		List<Long>[]  arr = Strings.splitList(ids, 1000);
		List result=new ArrayList();
		for(List<Long> idl : arr){
			parameterMap.put("ids", idl);
			result.addAll(edocSummaryDao.find(hsql,-1,-1,parameterMap));
		}
		for (int i = 0; i < result.size(); i++) {
            Object[] object = (Object[]) result.get(i);            
            EdocSummary summary = new EdocSummary();
            summary.setId(Long.parseLong(object[0].toString()));
            if(object[1]!=null)
            {
            	summary.setSecretLevel(object[1].toString());
            }
            if(object[2] != null){
            	summary.setArchiveId((Long)object[2]);
            }
            if(object[3] != null){
            	summary.setSendUnit(object[3].toString());
            }
            hs.put(summary.getId(),summary);
		}
		
		return hs;		
	}
	public List<EdocSummaryModel> queryByCondition(int edocType,String condition,String field, String field1, int state) {
//		yangzd 特殊字符处理
		if(null!=field)
        {
        	StringBuffer buffer=new StringBuffer();
        	for(int i=0;i<field.length();i++)
        	{
        		
        		if(field.charAt(i)=='\'')
        		{
        			buffer.append("\\'");
        		}
        		else
        		{
        			buffer.append(field.charAt(i));
        		}
        	}
        	field=SQLWildcardUtil.escape(buffer.toString());
        }
		if(null!=field1)
        {
        	StringBuffer buffer=new StringBuffer();
        	for(int i=0;i<field1.length();i++)
        	{
        		
        		if(field1.charAt(i)=='\'')
        		{
        			buffer.append("\\'");
        		}
        		else
        		{
        			buffer.append(field1.charAt(i));
        		}
        	}
        	field1=SQLWildcardUtil.escape(buffer.toString());
        }
//		yangzd 特殊字符处理
		String exp0 = null;     
        String paramName = null;
        String paramValue = null;
        Map<String, Object> parameterMap = new HashMap<String, Object>();
		//List<String> paramNameList = new ArrayList<String>();
        User user = CurrentUser.get();
        long user_id = user.getId();
		
		//获取代理相关信息
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(user_id);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(user_id);
		List<AgentModel> agentModelList = null;
		boolean agentToFlag = false;
		boolean agentFlag = false;
		if(_agentModelList != null && !_agentModelList.isEmpty()){
			agentModelList = _agentModelList;
			agentFlag = true;
		}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
			agentModelList = _agentModelToList;
			agentToFlag = true;
		}
		//Map<Integer, AgentModel> agentModelMap = new HashMap<Integer, AgentModel>();
		List<AgentModel> edocAgent = new ArrayList<AgentModel>();
		if(agentModelList != null && !agentModelList.isEmpty()){
			java.util.Date now = new java.util.Date();
	    	for(AgentModel agentModel : agentModelList){
	    		String agentOptionStr = agentModel.getAgentOption();
	    		String[] agentOptions = agentOptionStr.split("&");
	    		for(String agentOption : agentOptions){
	    			int _agentOption = Integer.parseInt(agentOption);
	    			if(_agentOption == ApplicationCategoryEnum.edoc.key()){
	    				if(agentModel.getStartDate().before(now) && agentModel.getEndDate().after(now))
	    					edocAgent.add(agentModel);
	    				//agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
	    			}
	    		}
	    	}
		}
    	boolean isProxy = false;
		if(edocAgent != null && !edocAgent.isEmpty()){
			isProxy = true;
		}else{
			agentFlag = false;
			agentToFlag = false;
		}
		
		String hql = "select "+selectAffair+" from Affair as affair,EdocSummary as summary"        	
                + " where";  
        if(edocAgent != null && !edocAgent.isEmpty()){
			if (!agentToFlag) {
				hql += "(";
				hql += " (affair.memberId=:user_id) ";
				parameterMap.put("user_id", user_id);
				if (state == StateEnum.col_pending.key() || state == StateEnum.col_done.key()) {
					if(edocAgent != null && !edocAgent.isEmpty()){
						hql += "   or (";
						int i = 0;
						for(AgentModel agent : edocAgent){
							if(i != 0){
								hql +=" or ";
							}
							hql += " (affair.memberId=:edocAgentToId"+i;
							hql += " and affair.receiveTime>=:proxyCreateDate"+i;
							parameterMap.put("edocAgentToId"+i, agent.getAgentToId());
							parameterMap.put("proxyCreateDate"+i, agent.getStartDate());
							hql +=" )";
							i++;
						}
						hql += "   )";
					}
				}
				hql += ")";
			}
			else {
				if (state == StateEnum.col_pending.key()) {
					hql += " affair.memberId=:user_id1";
					parameterMap.put("user_id1", user_id);
					/*hql += " and affair.receiveTime<=:proxyCreateDate";
					//paramNameList.add("proxyCreateDate");
					java.util.Date early = edocAgent.get(0).getStartDate();
					int size = edocAgent.size();
					for(int i = 1 ; i < size ; i ++){
						java.util.Date angetDate = edocAgent.get(i).getStartDate();
						if(angetDate.before(early)){
							early = edocAgent.get(i).getStartDate();
						}
					}
					parameterMap.put("proxyCreateDate", early);*/
				}else{
					hql += " affair.memberId=:user_id";
		        	//paramNameList.add("user_id");
					parameterMap.put("user_id", user_id);
				}
			}		
        }else{
        	hql += " (affair.memberId=:user_id )";
			parameterMap.put("user_id", user_id);
        }
        
        hql+= " and affair.state=:a_state "
                + " and affair.objectId=summary.id"
                + " and affair.isDelete=false ";                
        
        parameterMap.put("a_state", state);
        if(edocType>=0){
			hql+= " and affair.app=:a_app ";
			parameterMap.put("a_app", EdocUtil.getAppCategoryByEdocType(edocType).getKey());
		}else if(edocType == -1){
			hql+= " and (affair.app =:a_app1 or affair.app =:a_app2 or affair.app =:a_app3)";
			parameterMap.put("a_app1", ApplicationCategoryEnum.edocRec.getKey());
			parameterMap.put("a_app2", ApplicationCategoryEnum.edocSend.getKey());
			parameterMap.put("a_app3", ApplicationCategoryEnum.edocSign.getKey());
		}
        
        //跟踪
        if (condition.equals("isTrack")) {
            String expss = " and affair.isTrack = 1";
            hql = hql + expss;
        }

        //关于已经完成的filter
        if (condition.equals("finishfilter")) {
            String expss = " and summary.finishDate is not null";
            hql = hql + expss;
        }
        //关于未完成的filter
        if (condition.equals("notfinishfilter")) {
            String expss = " and summary.finishDate is null";
            hql = hql + expss;
        }

        if (condition.equals("subject")) {
        	paramName = "subject";
			exp0 = " and summary.subject like :" + paramName + " ";
			paramValue = "%" +  field + "%";
			//paramNameList.add(paramName);
			parameterMap.put(paramName, paramValue);
			hql = hql + exp0;			
        } else if (condition.equals("docMark")) {
        	if(Strings.isNotBlank(field)){
            	paramName = "docMark";
                exp0 = " and summary.docMark like :"+paramName+" ";
                paramValue = "%" + field + "%";
    			//paramNameList.add(paramName);
    			parameterMap.put(paramName, paramValue);
                hql = hql + exp0;        		
        	}

        }else if (condition.equals("docInMark")) {  
        	if(Strings.isNotBlank(field1)){
            	paramName = "serialNo";
                exp0 = " and summary.serialNo like :"+paramName+" ";
                paramValue = "%" + field1 + "%";
    			//paramNameList.add(paramName);
    			parameterMap.put(paramName, paramValue);
                hql = hql + exp0;       		
        	}

        }else if (condition.equals("createDate")) {
        	if (StringUtils.isNotBlank(field)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(field);

				paramName = "timestamp1";
				hql = hql + " and affair.createDate >= :" + paramName;

				//paramNameList.add(paramName);
				parameterMap.put(paramName, stamp);
			}

			if (StringUtils.isNotBlank(field1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(field1);
				paramName = "timestamp2";
				hql = hql + " and affair.createDate <= :" + paramName;

				//paramNameList.add(paramName);
				parameterMap.put(paramName, stamp);
			}
        } else if (condition.equals("startMemberName")) {
            hql = "select "+selectAffair+" from Affair as affair,"+V3xOrgMember.class.getName()+" as mem,EdocSummary as summary"
            + " where ";
            
            if(edocAgent != null){
    			if (!agentToFlag) {
    				hql += "(";
    				hql += " (affair.memberId=:user_id) ";
    				parameterMap.put("user_id", user_id);
    				
    				if (state == StateEnum.col_pending.key()) {
						if (edocAgent != null && !edocAgent.isEmpty()) {
							hql += "   or (";
							int i = 0;
							for (AgentModel agent : edocAgent) {
								if (i != 0) {
									hql += " or ";
								}
								hql += " (affair.memberId=:edocAgentToId" + i;
								hql += " and affair.receiveTime>=:proxyCreateDate"+ i;
								parameterMap.put("edocAgentToId" + i, agent.getAgentToId());
								parameterMap.put("proxyCreateDate" + i, agent.getStartDate());
								hql += " )";
								i++;
							}
							hql += " )";
						}
    				}
    				hql += ")";
    			}
    			else {
    				if (state == StateEnum.col_pending.key()) {
    					hql += " affair.memberId=:user_id";
    					hql += " and affair.receiveTime<=:proxyCreateDate";
    					
        				//paramNameList.add("user_id");
        				parameterMap.put("user_id", user_id);
        				java.util.Date early = edocAgent.get(0).getStartDate();
    					int size = edocAgent.size();
    					for(int i = 1 ; i < size ; i ++){
    						java.util.Date angetDate = edocAgent.get(i).getStartDate();
    						if(angetDate.before(early)){
    							early = edocAgent.get(i).getStartDate();
    						}
    					}
    					parameterMap.put("proxyCreateDate", early);
    				}else{
    					hql += " affair.memberId=:user_id";
    					//paramNameList.add("user_id");
        				parameterMap.put("user_id", user_id);
    				}
    			}		
            }else{
            	hql += " affair.memberId=:user_id ";
            	//paramNameList.add("user_id");
				parameterMap.put("user_id", user_id);
            }
            
            hql+= " and affair.state=:a_state "
                    + " and affair.objectId=summary.id"
                    //+ " and body.edocSummary.id=summary.id"                    
                    + " and affair.senderId=mem.id"
                    + " and affair.isDelete=false"
                    + " and mem.name like :startMemberName";
            
            parameterMap.put("a_state", state);
            if(edocType>=0){
            	hql+= " and affair.app=:a_app ";
            	parameterMap.put("a_app", EdocUtil.getAppCategoryByEdocType(edocType).getKey());
            }else if(edocType == -1){
    			hql+= " and (affair.app =:a_app1 or affair.app =:a_app2 or affair.app =:a_app3)";
    			parameterMap.put("a_app1", ApplicationCategoryEnum.edocRec.getKey());
    			parameterMap.put("a_app2", ApplicationCategoryEnum.edocSend.getKey());
    			parameterMap.put("a_app3", ApplicationCategoryEnum.edocSign.getKey());
    		}
            
            paramName = "startMemberName";
            paramValue = "%" + SQLWildcardUtil.escape(field) + "%";
			//paramNameList.add(paramName);
			parameterMap.put(paramName, paramValue);			

        }
        
        if(edocType>=0){
        	hql+=" and summary.edocType=:s_edocType";
        	parameterMap.put("s_edocType", edocType);
        }else if(edocType == -1){
        	hql+=" and (summary.edocType=:edocType1 or summary.edocType =:edocType2 or summary.edocType =:edocType3)";
        	parameterMap.put("edocType1", EdocEnum.edocType.sendEdoc.ordinal());
			parameterMap.put("edocType2", EdocEnum.edocType.recEdoc.ordinal());
			parameterMap.put("edocType3",EdocEnum.edocType.signReport.ordinal());
        }
       
        if (state == StateEnum.col_done.key() || state == StateEnum.col_sent.key()) {
            hql += " and affair.archiveId is null";
        }

        if(state == StateEnum.col_pending.key()){
			hql = hql + " order by affair.receiveTime desc";
		}
        else if(state == StateEnum.col_done.key()){
			hql = hql + " order by affair.completeTime desc";
		}
        
        
        
        
        else{
			hql = hql + " order by affair.createDate desc";
		}
        
		List result = edocSummaryDao.find(hql.toString(), parameterMap);
		
		java.util.Date early = null;
		if(edocAgent != null && !edocAgent.isEmpty())
			early = edocAgent.get(0).getStartDate();
        List<EdocSummaryModel> models = new ArrayList<EdocSummaryModel>(result.size());
        for (int i = 0; i < result.size(); i++) {
            Object[] object = (Object[]) result.get(i);
            Affair affair = new Affair();
            EdocSummary summary = new EdocSummary();
            make(object,summary,affair);

            try {
                V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
                summary.setStartMember(member);
            }
            catch (BusinessException e) {
                log.error("", e);
            }

            //开始组装最后返回的结果
            EdocSummaryModel model = new EdocSummaryModel();
            if (state == StateEnum.col_waitSend.key()) {
                model.setWorkitemId(null);
                model.setCaseId(null);
                model.setStartDate(new Date(summary.getCreateTime().getTime()));
                model.setSummary(summary);
                model.setAffairId(affair.getId());                

            } else if (state == StateEnum.col_sent.key()) {
                model.setWorkitemId(null);
                model.setCaseId(summary.getCaseId() + "");
                model.setStartDate(new Date(summary.getCreateTime().getTime()));
                model.setSummary(summary);
                model.setAffairId(affair.getId()); 
                //设置流程是否超期标志
                java.sql.Timestamp startDate = summary.getStartTime();
				java.sql.Timestamp finishDate = summary.getCompleteTime();
				Date now = new Date(System.currentTimeMillis());
				if(summary.getDeadline() != null && summary.getDeadline() != 0){
					Long deadline = summary.getDeadline()*60000;
					if(finishDate == null){
						if((now.getTime()-startDate.getTime()) > deadline){
							summary.setWorklfowTimeout(true);
						}
					}else{
						Long expendTime = summary.getCompleteTime().getTime() - summary.getStartTime().getTime();
						if((deadline-expendTime) < 0){
							summary.setWorklfowTimeout(true);
						}
					}
				}
            } else if (state == StateEnum.col_done.key()) {
                model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            } else if (state == StateEnum.col_pending.key()) {
                model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            }else{
               model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            }
            int affairState=affair.getState();
            if(affairState == StateEnum.col_waitSend.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.WaitSend.name());}
            else if(affairState == StateEnum.col_sent.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Sent.name());}
            else if(affairState == StateEnum.col_done.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Done.name());}
            else if(affairState == StateEnum.col_pending.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Pending.name());}            

            model.setFinshed(summary.getCompleteTime()!= null);

            model.setBodyType(affair.getBodyType());

            //公文状态
            Integer sub_state = affair.getSubState();
            if (sub_state != null) {
                model.setState(sub_state.intValue());
            }

            //是否跟踪
            Boolean isTrack = affair.getIsTrack();
            if (isTrack != null) {
                model.setIsTrack(isTrack.booleanValue());
            }

            //催办次数
            Integer hastenTimes = affair.getHastenTimes();
            if (hastenTimes != null) {
                model.setHastenTimes(hastenTimes);
            }

            //检查是否有附件
            model.setHasAttachments(affair.isHasAttachments());

            //是否超期
            Boolean overtopTime = affair.getIsOvertopTime();
            if(overtopTime != null){
            	model.setOvertopTime(overtopTime.booleanValue());
            }
            
            //提前提醒
            Long advanceRemind = affair.getRemindDate();
            if(advanceRemind == null){
            	advanceRemind = 0L;
            }
            model.setAdvanceRemindTime(advanceRemind);
            
            //协同处理期限
            Long deadLine = affair.getDeadlineDate();
            if(deadLine == null){
            	deadLine = 0L;
            }
            model.setDeadLine(deadLine);
            V3xOrgMember member = null;
            //是否代理			
			if (state == StateEnum.col_done.key()) {
				model.setAffair(affair);
			    /*if(affair.getTransactorId() != null){
					try {
						if(affair.getMemberId() == user.getId())
                            member = orgManager.getMemberById(affair.getTransactorId());
						else
							member = orgManager.getMemberById(affair.getMemberId());
						    model.setProxyName(member.getName());
						    model.setProxy(true);
					} catch (BusinessException e) {
						log.error("", e);
					}
			    }else{
			    	if(affair.getMemberId() != user.getId()){
			    		try{
			    			member = orgManager.getMemberById(affair.getMemberId());
			    			model.setProxyName(member.getName());
			    		}catch(BusinessException e){
			    			log.error("", e);
			    		}
			    		model.setAgentDeal(true);
			    		model.setProxy(true);
			    	}
			    }*/
			}
			
			if (state == StateEnum.col_pending.key() && agentFlag && affair.getMemberId() != user.getId()) {
				Long proxyMemberId = affair.getMemberId();
				try {
					member = orgManager.getMemberById(proxyMemberId);
				} catch (BusinessException e) {
					log.error("", e);
				}
				model.setProxyName(member.getName());
				model.setProxy(true);
			}else if(state == StateEnum.col_pending.key() && agentToFlag && early != null && early.before(affair.getReceiveTime())){
				model.setProxy(true);
			}
			
			model.setNodePolicy(affair.getNodePolicy());
			
			if(affair.getCompleteTime() != null){
				model.setDealTime(new Date(affair.getCompleteTime().getTime()));
			}
            models.add(model);
        }
        return models;
	}
	//成发集团项目 程炯 2012-8-31 重写queryByCondition
	public List<EdocSummaryModel> queryByCondition(int edocType,String condition,String field, String field1, int state,Integer edocSecretLevel) {
//		yangzd 特殊字符处理
		if(null!=field)
        {
        	StringBuffer buffer=new StringBuffer();
        	for(int i=0;i<field.length();i++)
        	{
        		
        		if(field.charAt(i)=='\'')
        		{
        			buffer.append("\\'");
        		}
        		else
        		{
        			buffer.append(field.charAt(i));
        		}
        	}
        	field=SQLWildcardUtil.escape(buffer.toString());
        }
		if(null!=field1)
        {
        	StringBuffer buffer=new StringBuffer();
        	for(int i=0;i<field1.length();i++)
        	{
        		
        		if(field1.charAt(i)=='\'')
        		{
        			buffer.append("\\'");
        		}
        		else
        		{
        			buffer.append(field1.charAt(i));
        		}
        	}
        	field1=SQLWildcardUtil.escape(buffer.toString());
        }
//		yangzd 特殊字符处理
		String exp0 = null;     
        String paramName = null;
        String paramValue = null;
        Map<String, Object> parameterMap = new HashMap<String, Object>();
		//List<String> paramNameList = new ArrayList<String>();
        User user = CurrentUser.get();
        long user_id = user.getId();
		
		//获取代理相关信息
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(user_id);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(user_id);
		List<AgentModel> agentModelList = null;
		boolean agentToFlag = false;
		boolean agentFlag = false;
		if(_agentModelList != null && !_agentModelList.isEmpty()){
			agentModelList = _agentModelList;
			agentFlag = true;
		}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
			agentModelList = _agentModelToList;
			agentToFlag = true;
		}
		//Map<Integer, AgentModel> agentModelMap = new HashMap<Integer, AgentModel>();
		List<AgentModel> edocAgent = new ArrayList<AgentModel>();
		if(agentModelList != null && !agentModelList.isEmpty()){
			java.util.Date now = new java.util.Date();
	    	for(AgentModel agentModel : agentModelList){
	    		String agentOptionStr = agentModel.getAgentOption();
	    		String[] agentOptions = agentOptionStr.split("&");
	    		for(String agentOption : agentOptions){
	    			int _agentOption = Integer.parseInt(agentOption);
	    			if(_agentOption == ApplicationCategoryEnum.edoc.key()){
	    				if(agentModel.getStartDate().before(now) && agentModel.getEndDate().after(now))
	    					edocAgent.add(agentModel);
	    				//agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
	    			}
	    		}
	    	}
		}
    	boolean isProxy = false;
		if(edocAgent != null && !edocAgent.isEmpty()){
			isProxy = true;
		}else{
			agentFlag = false;
			agentToFlag = false;
		}
		
		String hql = "select "+selectAffair+" from Affair as affair,EdocSummary as summary"        	
                + " where";  
        if(edocAgent != null && !edocAgent.isEmpty()){
			if (!agentToFlag) {
				hql += "(";
				hql += " (affair.memberId=:user_id) ";
				parameterMap.put("user_id", user_id);
				if (state == StateEnum.col_pending.key() || state == StateEnum.col_done.key()) {
					if(edocAgent != null && !edocAgent.isEmpty()){
						hql += "   or (";
						int i = 0;
						for(AgentModel agent : edocAgent){
							if(i != 0){
								hql +=" or ";
							}
							hql += " (affair.memberId=:edocAgentToId"+i;
							hql += " and affair.receiveTime>=:proxyCreateDate"+i;
							parameterMap.put("edocAgentToId"+i, agent.getAgentToId());
							parameterMap.put("proxyCreateDate"+i, agent.getStartDate());
							hql +=" )";
							i++;
						}
						hql += "   )";
					}
				}
				hql += ")";
			}
			else {
				if (state == StateEnum.col_pending.key()) {
					hql += " affair.memberId=:user_id1";
					parameterMap.put("user_id1", user_id);
					/*hql += " and affair.receiveTime<=:proxyCreateDate";
					//paramNameList.add("proxyCreateDate");
					java.util.Date early = edocAgent.get(0).getStartDate();
					int size = edocAgent.size();
					for(int i = 1 ; i < size ; i ++){
						java.util.Date angetDate = edocAgent.get(i).getStartDate();
						if(angetDate.before(early)){
							early = edocAgent.get(i).getStartDate();
						}
					}
					parameterMap.put("proxyCreateDate", early);*/
				}else{
					hql += " affair.memberId=:user_id";
		        	//paramNameList.add("user_id");
					parameterMap.put("user_id", user_id);
				}
			}		
        }else{
        	hql += " (affair.memberId=:user_id )";
			parameterMap.put("user_id", user_id);
        }
        
        hql+= " and affair.state=:a_state "
                + " and affair.objectId=summary.id"
                + " and affair.isDelete=false ";                
        
        parameterMap.put("a_state", state);
        if(edocType>=0){
			hql+= " and affair.app=:a_app ";
			parameterMap.put("a_app", EdocUtil.getAppCategoryByEdocType(edocType).getKey());
		}else if(edocType == -1){
			hql+= " and (affair.app =:a_app1 or affair.app =:a_app2 or affair.app =:a_app3)";
			parameterMap.put("a_app1", ApplicationCategoryEnum.edocRec.getKey());
			parameterMap.put("a_app2", ApplicationCategoryEnum.edocSend.getKey());
			parameterMap.put("a_app3", ApplicationCategoryEnum.edocSign.getKey());
		}
        
        //跟踪
        if (condition.equals("isTrack")) {
            String expss = " and affair.isTrack = 1";
            hql = hql + expss;
        }

        //关于已经完成的filter
        if (condition.equals("finishfilter")) {
            String expss = " and summary.finishDate is not null";
            hql = hql + expss;
        }
        //关于未完成的filter
        if (condition.equals("notfinishfilter")) {
            String expss = " and summary.finishDate is null";
            hql = hql + expss;
        }

        if (condition.equals("subject")) {
        	paramName = "subject";
			exp0 = " and summary.subject like :" + paramName + " ";
			paramValue = "%" +  field + "%";
			//paramNameList.add(paramName);
			parameterMap.put(paramName, paramValue);
			hql = hql + exp0;			
        } else if (condition.equals("docMark")) {
        	if(Strings.isNotBlank(field)){
            	paramName = "docMark";
                exp0 = " and summary.docMark like :"+paramName+" ";
                paramValue = "%" + field + "%";
    			//paramNameList.add(paramName);
    			parameterMap.put(paramName, paramValue);
                hql = hql + exp0;        		
        	}

        }else if (condition.equals("docInMark")) {  
        	if(Strings.isNotBlank(field1)){
            	paramName = "serialNo";
                exp0 = " and summary.serialNo like :"+paramName+" ";
                paramValue = "%" + field1 + "%";
    			//paramNameList.add(paramName);
    			parameterMap.put(paramName, paramValue);
                hql = hql + exp0;       		
        	}

        }else if (condition.equals("createDate")) {
        	if (StringUtils.isNotBlank(field)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(field);

				paramName = "timestamp1";
				hql = hql + " and affair.createDate >= :" + paramName;

				//paramNameList.add(paramName);
				parameterMap.put(paramName, stamp);
			}

			if (StringUtils.isNotBlank(field1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(field1);
				paramName = "timestamp2";
				hql = hql + " and affair.createDate <= :" + paramName;

				//paramNameList.add(paramName);
				parameterMap.put(paramName, stamp);
			}
        } else if (condition.equals("startMemberName")) {
            hql = "select "+selectAffair+" from Affair as affair,"+V3xOrgMember.class.getName()+" as mem,EdocSummary as summary"
            + " where ";
            parameterMap = new HashMap<String, Object>();
            if(edocAgent != null){
    			if (!agentToFlag) {
    				hql += "(";
    				hql += " (affair.memberId=:user_id) ";
    				parameterMap.put("user_id", user_id);
    				
    				if (state == StateEnum.col_pending.key()) {
						if (edocAgent != null && !edocAgent.isEmpty()) {
							hql += "   or (";
							int i = 0;
							for (AgentModel agent : edocAgent) {
								if (i != 0) {
									hql += " or ";
								}
								hql += " (affair.memberId=:edocAgentToId" + i;
								hql += " and affair.receiveTime>=:proxyCreateDate"+ i;
								parameterMap.put("edocAgentToId" + i, agent.getAgentToId());
								parameterMap.put("proxyCreateDate" + i, agent.getStartDate());
								hql += " )";
								i++;
							}
							hql += " )";
						}
    				}
    				hql += ")";
    			}
    			else {
    				if (state == StateEnum.col_pending.key()) {
    					hql += " affair.memberId=:user_id";
    					hql += " and affair.receiveTime<=:proxyCreateDate";
    					
        				//paramNameList.add("user_id");
        				parameterMap.put("user_id", user_id);
        				java.util.Date early = edocAgent.get(0).getStartDate();
    					int size = edocAgent.size();
    					for(int i = 1 ; i < size ; i ++){
    						java.util.Date angetDate = edocAgent.get(i).getStartDate();
    						if(angetDate.before(early)){
    							early = edocAgent.get(i).getStartDate();
    						}
    					}
    					parameterMap.put("proxyCreateDate", early);
    				}else{
    					hql += " affair.memberId=:user_id";
    					//paramNameList.add("user_id");
        				parameterMap.put("user_id", user_id);
    				}
    			}		
            }else{
            	hql += " affair.memberId=:user_id ";
            	//paramNameList.add("user_id");
				parameterMap.put("user_id", user_id);
            }
            
            hql+= " and affair.state=:a_state "
                    + " and affair.objectId=summary.id"
                    //+ " and body.edocSummary.id=summary.id"                    
                    + " and affair.senderId=mem.id"
                    + " and affair.isDelete=false"
                    + " and mem.name like :startMemberName";
            
            parameterMap.put("a_state", state);
            if(edocType>=0){
            	hql+= " and affair.app=:a_app ";
            	parameterMap.put("a_app", EdocUtil.getAppCategoryByEdocType(edocType).getKey());
            }else if(edocType == -1){
    			hql+= " and (affair.app =:a_app1 or affair.app =:a_app2 or affair.app =:a_app3)";
    			parameterMap.put("a_app1", ApplicationCategoryEnum.edocRec.getKey());
    			parameterMap.put("a_app2", ApplicationCategoryEnum.edocSend.getKey());
    			parameterMap.put("a_app3", ApplicationCategoryEnum.edocSign.getKey());
    		}
            
            paramName = "startMemberName";
            paramValue = "%" + SQLWildcardUtil.escape(field) + "%";
			//paramNameList.add(paramName);
			parameterMap.put(paramName, paramValue);			

        }
        
        if(edocType>=0){
        	hql+=" and summary.edocType=:s_edocType";
        	parameterMap.put("s_edocType", edocType);
        }else if(edocType == -1){
        	hql+=" and (summary.edocType=:edocType1 or summary.edocType =:edocType2 or summary.edocType =:edocType3)";
        	parameterMap.put("edocType1", EdocEnum.edocType.sendEdoc.ordinal());
			parameterMap.put("edocType2", EdocEnum.edocType.recEdoc.ordinal());
			parameterMap.put("edocType3",EdocEnum.edocType.signReport.ordinal());
        }
       
        if (state == StateEnum.col_done.key() || state == StateEnum.col_sent.key()) {
            hql += " and affair.archiveId is null";
        }
        
        //成发集团项目 程炯 2012-8-31 根据人员密级筛选公文 begin
        if(edocSecretLevel != null){
        	hql += " and (summary.edocSecretLevel <= :edocSecretLevel or summary.edocSecretLevel is null)";
        	parameterMap.put("edocSecretLevel", edocSecretLevel);
        }
        //end

        if(state == StateEnum.col_pending.key()){
			hql = hql + " order by affair.receiveTime desc";
		}
        else if(state == StateEnum.col_done.key()){
			hql = hql + " order by affair.completeTime desc";
		}
        
        
        
        
        else{
			hql = hql + " order by affair.createDate desc";
		}
        
		List result = edocSummaryDao.find(hql.toString(), parameterMap);
		
		java.util.Date early = null;
		if(edocAgent != null && !edocAgent.isEmpty())
			early = edocAgent.get(0).getStartDate();
        List<EdocSummaryModel> models = new ArrayList<EdocSummaryModel>(result.size());
        for (int i = 0; i < result.size(); i++) {
            Object[] object = (Object[]) result.get(i);
            Affair affair = new Affair();
            EdocSummary summary = new EdocSummary();
            make(object,summary,affair);

            try {
                V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
                summary.setStartMember(member);
            }
            catch (BusinessException e) {
                log.error("", e);
            }

            //开始组装最后返回的结果
            EdocSummaryModel model = new EdocSummaryModel();
            if (state == StateEnum.col_waitSend.key()) {
                model.setWorkitemId(null);
                model.setCaseId(null);
                model.setStartDate(new Date(summary.getCreateTime().getTime()));
                model.setSummary(summary);
                model.setAffairId(affair.getId());                

            } else if (state == StateEnum.col_sent.key()) {
                model.setWorkitemId(null);
                model.setCaseId(summary.getCaseId() + "");
                model.setStartDate(new Date(summary.getCreateTime().getTime()));
                model.setSummary(summary);
                model.setAffairId(affair.getId()); 
                //设置流程是否超期标志
                java.sql.Timestamp startDate = summary.getStartTime();
				java.sql.Timestamp finishDate = summary.getCompleteTime();
				Date now = new Date(System.currentTimeMillis());
				if(summary.getDeadline() != null && summary.getDeadline() != 0){
					Long deadline = summary.getDeadline()*60000;
					if(finishDate == null){
						if((now.getTime()-startDate.getTime()) > deadline){
							summary.setWorklfowTimeout(true);
						}
					}else{
						Long expendTime = summary.getCompleteTime().getTime() - summary.getStartTime().getTime();
						if((deadline-expendTime) < 0){
							summary.setWorklfowTimeout(true);
						}
					}
				}
            } else if (state == StateEnum.col_done.key()) {
                model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            } else if (state == StateEnum.col_pending.key()) {
                model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            }else{
               model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());                
            }
            int affairState=affair.getState();
            if(affairState == StateEnum.col_waitSend.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.WaitSend.name());}
            else if(affairState == StateEnum.col_sent.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Sent.name());}
            else if(affairState == StateEnum.col_done.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Done.name());}
            else if(affairState == StateEnum.col_pending.key()){model.setEdocType(EdocSummaryModel.EDOCTYPE.Pending.name());}            

            model.setFinshed(summary.getCompleteTime()!= null);

            model.setBodyType(affair.getBodyType());

            //公文状态
            Integer sub_state = affair.getSubState();
            if (sub_state != null) {
                model.setState(sub_state.intValue());
            }

            //是否跟踪
            Boolean isTrack = affair.getIsTrack();
            if (isTrack != null) {
                model.setIsTrack(isTrack.booleanValue());
            }

            //催办次数
            Integer hastenTimes = affair.getHastenTimes();
            if (hastenTimes != null) {
                model.setHastenTimes(hastenTimes);
            }

            //检查是否有附件
            model.setHasAttachments(affair.isHasAttachments());

            //是否超期
            Boolean overtopTime = affair.getIsOvertopTime();
            if(overtopTime != null){
            	model.setOvertopTime(overtopTime.booleanValue());
            }
            
            //提前提醒
            Long advanceRemind = affair.getRemindDate();
            if(advanceRemind == null){
            	advanceRemind = 0L;
            }
            model.setAdvanceRemindTime(advanceRemind);
            
            //协同处理期限
            Long deadLine = affair.getDeadlineDate();
            if(deadLine == null){
            	deadLine = 0L;
            }
            model.setDeadLine(deadLine);
            V3xOrgMember member = null;
            //是否代理			
			if (state == StateEnum.col_done.key()) {
				model.setAffair(affair);
			    /*if(affair.getTransactorId() != null){
					try {
						if(affair.getMemberId() == user.getId())
                            member = orgManager.getMemberById(affair.getTransactorId());
						else
							member = orgManager.getMemberById(affair.getMemberId());
						    model.setProxyName(member.getName());
						    model.setProxy(true);
					} catch (BusinessException e) {
						log.error("", e);
					}
			    }else{
			    	if(affair.getMemberId() != user.getId()){
			    		try{
			    			member = orgManager.getMemberById(affair.getMemberId());
			    			model.setProxyName(member.getName());
			    		}catch(BusinessException e){
			    			log.error("", e);
			    		}
			    		model.setAgentDeal(true);
			    		model.setProxy(true);
			    	}
			    }*/
			}
			
			if (state == StateEnum.col_pending.key() && agentFlag && affair.getMemberId() != user.getId()) {
				Long proxyMemberId = affair.getMemberId();
				try {
					member = orgManager.getMemberById(proxyMemberId);
				} catch (BusinessException e) {
					log.error("", e);
				}
				model.setProxyName(member.getName());
				model.setProxy(true);
			}else if(state == StateEnum.col_pending.key() && agentToFlag && early != null && early.before(affair.getReceiveTime())){
				model.setProxy(true);
			}
			
			model.setNodePolicy(affair.getNodePolicy());
			
			if(affair.getCompleteTime() != null){
				model.setDealTime(new Date(affair.getCompleteTime().getTime()));
			}
            models.add(model);
        }
        return models;
	}

	public List<EdocSummaryModel> queryByCondition4Quote(ApplicationCategoryEnum appEnum, String condition,
            String field, String field1) {
        List<EdocSummaryModel> models = new ArrayList<EdocSummaryModel>();
        long user_id = CurrentUser.get().getId();
        
        List<Object> objects = new ArrayList<Object>();
        
        String hql = "select "+selectAffair+" from Affair as affair,EdocSummary as summary"
        		+ " where (affair.objectId=summary.id) and (affair.memberId=?)"
        		//+ " and (affair.state=" + state.key() + ")"
        		+ " and (affair.state in ("+StateEnum.col_done.getKey()+","+StateEnum.col_pending.getKey()+","+StateEnum.col_sent.getKey()+") )"
        		+ " and (affair.app=?)"
                + " and affair.isDelete=false"
        		+ " and affair.archiveId is null";
        objects.add(user_id);
        objects.add(appEnum.key());

        if (condition != null) {
        	if (condition.equals("subject")) {
                hql += " and (summary.subject like ?)";

                objects.add("%" + SQLWildcardUtil.escape(field) + "%");
            }
        	else if (condition.equals("docMark")) {
                hql += " and (summary.docMark like ?)";

                objects.add("%" + SQLWildcardUtil.escape(field) + "%");
            }
        	else if (condition.equals("docInMark")) {
                hql += " and (summary.serialNo like ?)";

                objects.add("%" + SQLWildcardUtil.escape(field) + "%");
            }
        	else if (condition.equals("docInMark")) {
                hql += " and (summary.serialNo like ?)";

                objects.add("%" + SQLWildcardUtil.escape(field) + "%");
            }
        	else if (condition.equals("startMemberName")) {
        		hql = "select "+selectAffair+" from Affair as affair,"+V3xOrgMember.class.getName()+" as mem,EdocSummary as summary"
                        + " where (affair.senderId=mem.id) and (affair.objectId=summary.id) and (affair.memberId=?)"
                        //+ " and (affair.state=" + state.key() + ")"
                        + " and (affair.state in ("+StateEnum.col_done.getKey()+","+StateEnum.col_pending.getKey()+","+StateEnum.col_sent.getKey()+") )"
                        + " and (affair.app=?)"
                        + " and affair.isDelete=false"
                		+ " and affair.archiveId is null"
                		//+ " and (summary.canForward=true)"
                        + " and (mem.name like ?)";

                objects.add("%" + SQLWildcardUtil.escape(field) + "%");
            }            
            else if (condition.equals("createDate")) {
                if (StringUtils.isNotBlank(field)) {
                    hql += " and affair.createDate >= ?";
                    java.util.Date stamp = Datetimes.getTodayFirstTime(field);

                    objects.add(stamp);
                }
                if (StringUtils.isNotBlank(field1)) {
                    hql += " and affair.createDate <= ?";
                    java.util.Date stamp = Datetimes.getTodayLastTime(field1);

                    objects.add(stamp);
                }
            }
        }

        String selectHql = hql + " order by affair.createDate desc";

        List result = edocSummaryDao.find(selectHql, null, objects);

        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
            	Object[] object = (Object[]) result.get(i);
                Affair affair = new Affair();
                EdocSummary summary = new EdocSummary();
                make(object,summary,affair);
                
                try {
                    V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
                    summary.setStartMember(member);
                }
                catch (BusinessException e) {
                    log.error("", e);
                }

                //开始组装最后返回的结果�?
                EdocSummaryModel model = new EdocSummaryModel();

                model.setStartDate(new Date(summary.getCreateTime().getTime()));
                model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());
                model.setBodyType(affair.getBodyType());

                //协同状�?
                Integer sub_state = affair.getState();                
                if (sub_state != null) {
                    model.setState(sub_state.intValue());
                }

                //是否跟踪
                Boolean isTrack = affair.getIsTrack();
                if (isTrack != null) {
                    model.setIsTrack(isTrack.booleanValue());
                }
                models.add(model);
            }
        }

        return models;		
	}

	public List<EdocSummaryModel> queryDraftList(int edocType) throws EdocException {
		List<EdocSummaryModel> summaryModelList = queryByCondition(edocType,"", null, null,  StateEnum.col_waitSend.key());
		return summaryModelList;
	}
	//重写
	public List<EdocSummaryModel> queryDraftList(int edocType,Integer edocSecretLevel) throws EdocException {
		List<EdocSummaryModel> summaryModelList = queryByCondition(edocType,"", null, null,  StateEnum.col_waitSend.key(),edocSecretLevel);
		return summaryModelList;
	}

	public List<EdocSummaryModel> queryFinishedList(int edocType) throws EdocException {
		List<EdocSummaryModel> result = queryByCondition(edocType,"", null, null,  StateEnum.col_done.key());
		return result;
	}
	//重写queryFinishedList
	public List<EdocSummaryModel> queryFinishedList(int edocType,Integer edocSecretLevel) throws EdocException {
		List<EdocSummaryModel> result = queryByCondition(edocType,"", null, null,  StateEnum.col_done.key(),edocSecretLevel);
		return result;
	}

	public List<EdocSummaryModel> querySentList(int edocType) throws EdocException {
		List<EdocSummaryModel> summaryModelList = queryByCondition(edocType,"", null, null, StateEnum.col_sent.key());
		return summaryModelList;
	}
	//重写querySentList
	public List<EdocSummaryModel> querySentList(int edocType,Integer edocSecretLevel) throws EdocException {
		List<EdocSummaryModel> summaryModelList = queryByCondition(edocType,"", null, null, StateEnum.col_sent.key(),edocSecretLevel);
		return summaryModelList;
	}

	public List<EdocSummaryModel> queryTodoList(int edocType) throws EdocException {
		List<EdocSummaryModel> result = queryByCondition(edocType,"", null, null, StateEnum.col_pending.key());
		return result;
	}
	//重写
	public List<EdocSummaryModel> queryTodoList(int edocType,Integer edocSecretLevel) throws EdocException {
		List<EdocSummaryModel> result = queryByCondition(edocType,"", null, null, StateEnum.col_pending.key(),edocSecretLevel);
		return result;
	}

	public List<EdocSummaryModel> queryTrackList(int edocType) throws EdocException {
		List<EdocSummaryModel> summaryModelList = queryByCondition(edocType,"isTrack", null, null,0);
        return summaryModelList;
	}
	/**
	 * 设置是否为联合发文标志
	 *
	 */
	private void _setIsUnit(EdocSummary summary)
	{
		Long edocFormId=summary.getFormId();
		if(edocFormId!=null)
		{
			EdocForm ef=edocFormManager.getEdocForm(edocFormId);
			if(ef!=null)
			{
				summary.setIsunit(ef.getIsunit());
			}
		}
	}
   
	public  Long runCase(FlowData flowData, EdocSummary summary, 
			EdocBody body, EdocOpinion senderOpinion, EdocEnum.SendType sendType,
			Map options,String from,Long agentToId) throws EdocException{
        boolean isResend = (Constant.SendType.resend.ordinal() == sendType.ordinal());
        boolean isForward = (Constant.SendType.forward.ordinal() == sendType.ordinal());
        User user = CurrentUser.get();
        Long affairId = 0L;
        try {        	
            String processId = summary.getProcessId();
            if (processId != null && processId.trim().equals(""))
                processId = null;

            
            //根据选人界面传来的people生成流程模板XML
            processId = EdocHelper.saveOrUpdateProcessByFlowData(flowData, processId, false);
            
           body.setIdIfNew();
           body.setEdocSummary(summary);
           summary.getEdocBodies().add(body);
            
            //查找出所有的edocBody对象
            
            List<EdocBody> allBody=edocBodyDao.getBodyByIdAndNum(summary.getId());
            
            if(allBody!=null){
	            for(EdocBody eb:allBody){
	            	//防止含有两个相同的对象或者相同正文编号（0，1，2）的对象。
	            	if(eb.getContentNo()!=null
	            			&&body.getContentNo()!=null
	            				&&eb.getContentNo().intValue()!=body.getContentNo().intValue())
	              	summary.getEdocBodies().add(eb);
	            }
            }
            
            //生成流程模板对象
            EdocSummary _summary = edocSummaryDao.get(summary.getId());
            if (_summary != null) {
            	edocOpinionDao.deleteOpinionBySummaryId(_summary.getId());
            	edocSummaryDao.delete(_summary);
                affairManager.deleteByObject(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()), summary.getId());
                //删除处理意见，手写批注			
    	    	htmlSignDao.delete(new String[]{"summaryId"},new Object[]{summary.getId()});	  
            }            
            //保存colsummary、body
            summary.setIdIfNew();
            //if (isResend) {
            //    summary.setResentTime(summary.getResentTime() == null ? 1 : summary.getResentTime() + 1);
            //}
      
           

            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            summary.setCreateTime(now);
            
            if (body.getCreateTime() == null) {
                body.setCreateTime(now);
            }
            summary.setProcessId(processId);
            if(summary.getStartTime() ==  null){
            	summary.setStartTime(now);
            }
            summary.setStartUserId(user.getId());

            //附言内容为空，就不记录了
            if (senderOpinion.getContent() != null && !"".equals(senderOpinion.getContent())) {
                senderOpinion.setEdocSummary(summary);
                senderOpinion.setOpinionType(EdocOpinion.OpinionType.senderOpinion.ordinal());
                senderOpinion.setCreateTime(now);
                senderOpinion.setCreateUserId(user.getId());
                summary.getEdocOpinions().add(senderOpinion);
            }

            Affair affair = new Affair();
            affair.setIdIfNew();    
            affairId = affair.getId();
            _setIsUnit(summary);
           
           
            
            WorkflowEventListener.setEdocSummary(summary);

            //运行流程实例
            DateSharedWithWorkflowEngineThreadLocal.setCurrentUserData(new Long[]{user.getId(),agentToId});
            long caseId = EdocHelper.runCase(processId,ApplicationCategoryEnum.edoc);
            summary.setCaseId(caseId);
            
            //先保存公文，在维护公文和附言的关系即存储公文附言 author:zhangg
            edocSummaryDao.save(summary);
            if (senderOpinion.getContent() != null && !"".equals(senderOpinion.getContent())) {
            	edocOpinionDao.save(senderOpinion);
            }
            affair.setApp(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()));
            affair.setSubject(summary.getSubject());
            affair.setCreateDate(now);
            //收文登记的时候可能是代理人登记的。
            if(agentToId != null){
        	   affair.setMemberId(agentToId);
        	   affair.setTransactorId(user.getId());
            }else{
            	 affair.setMemberId(user.getId());
            }
            affair.setObjectId(summary.getId());
            affair.setSubObjectId(null);
            affair.setSenderId(user.getId());
            affair.setState(StateEnum.col_sent.key());
            affair.setIsTrack(senderOpinion.affairIsTrack);            
            Long _deadline = summary.getDeadline();
            if (_deadline != null && _deadline.intValue() > 0) {
                affair.setDeadlineDate(_deadline);
            }
            affair.setBodyType(summary.getFirstBody().getContentType());
			affair.setHasAttachments(summary.isHasAttachments());
			affair.serialExtProperties();
			if(summary.getUrgentLevel()!=null&&!"".equals(summary.getUrgentLevel())){
				affair.setImportantLevel(Integer.parseInt(summary.getUrgentLevel()));
			}
			affair.setTempleteId(summary.getTempleteId());
            affairManager.addAffair(affair);
            
            BPMProcess bPMProcess = EdocHelper.getCaseProcess(processId);      
            String params  = EdocHelper.checSecondNodeMembers(bPMProcess,flowData.getCondition()) ;
            
            if ("transmitSend".equals(from)) {
            	this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), -1l, 
            			ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.fowardIssuing.getKey())) ;
            	 this.appLogManager.insertLog(user, AppLogAction.Edoc_Forward, user.getName() ,summary.getSubject()) ;
            }else if("register".equals(from)) {
            	this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), -1l, ProcessLogAction.register, params) ;
            	this.appLogManager.insertLog(user, AppLogAction.Edoc_RegEdoc, user.getName() ,summary.getSubject()) ;
            }else {
            	this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), -1l, ProcessLogAction.drafting, params) ;
            	this.appLogManager.insertLog(user, AppLogAction.Edoc_Send, user.getName() ,summary.getSubject()) ;
            }

            //添加到公文统计表
            edocStatManager.createState(summary, user);
            
            
            //如果收文和签报设置了预归档目录，则流程发起后就自动归档，V320工作项    
            if(summary.getEdocType() == EdocEnum.edocType.recEdoc.ordinal()
            		||summary.getEdocType() == EdocEnum.edocType.signReport.ordinal()){
            	
            	if(summary.getArchiveId() != null && summary.getTempleteId() != null){  //设置了预先归档的目录
            		pigeonholeAffair("", affair, summary.getId(),summary.getArchiveId());
            	}
            }
            
    		try{
    			MetaUtil.refMeta(summary);
    		}catch(Exception e){
    			log.error("更改枚举项为引用出现异常 error = "+e);
    		}
    		
    		EdocHelper.createQuartzJobOfSummary(summary, workTimeManager);
        }
        catch (Exception e) {
            log.error("", e);
        }
        return affairId;
    }
	
	
	public int runCase(String processId, EdocSummary summary, EdocBody body,
			EdocOpinion senderOpinion) throws EdocException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void saveBody(EdocBody body) throws EdocException {
		// TODO Auto-generated method stub

	}

	public Long saveDraft(FlowData flowData, EdocSummary summary, EdocBody body,EdocOpinion senderOpinion) throws EdocException
	{
	        User user = CurrentUser.get();
	        String processId = summary.getProcessId();
	        ProcessDefManager pdm = null;
	        try {
	            pdm = WAPIFactory.getProcessDefManager("Engine_1");
	        } catch (Exception ex) {
	            throw new EdocException("获取引擎对外接口异常", ex);
	        }
	        if (processId != null && !processId.trim().equals("")){
	            try {
					pdm.deleteProcessInReady("admin", processId);
				} catch (BPMException e) {
					throw new EdocException("删除ReadyProcess异常", e);
				}
	            processId = null;
	        }
	        //生成流程模板对象
	        if (!flowData.isEmpty()) {
	        	try{
		        	processId = EdocHelper.saveOrUpdateProcessByFlowData(flowData, processId,false);
		            EdocHelper.addRunningProcess(processId);
	        	}catch(ColException e1){
	        		log.error("保存待发process异常 [processId =" + processId + "]", e1);
	        		throw new EdocException("Save Process Failed");
	        	}
	        	try {
					pdm.deleteProcessInReady("admin", processId);
				} catch (BPMException e) {
					log.error("删除readyProcess异常 [processId =" + processId + "]", e);
	        		throw new EdocException("delete readyProcess Failed");
				}
	        }
	        if(summary.getId()!=null)
	        {
	        	EdocSummary _summary = edocSummaryDao.get(summary.getId());
	        	if (_summary != null) {
	        		edocOpinionDao.deleteOpinionBySummaryId(_summary.getId());
	        		edocSummaryDao.delete(_summary);	        
	        		affairManager.deleteByObject(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()), summary.getId());
	        	}
	        }
	        summary.setIdIfNew();

	        Timestamp now = new Timestamp(System.currentTimeMillis());

	        //保存colsummary、body
	        summary.setCaseId(null);
	        summary.setCreateTime(now);
	        summary.setProcessId(processId);
	        if(summary.getStartTime() ==  null){
	        	summary.setStartTime(now);
	        }
	        summary.setStartUserId(user.getId());        

	        body.setLastUpdate(now);
	        body.setEdocSummary(summary);
	        summary.getEdocBodies().add(body);  
 
	        //附言内容为空，就不记录了
	        if (StringUtils.isNotBlank(senderOpinion.getContent())) {
	            senderOpinion.setEdocSummary(summary);
	            senderOpinion.setOpinionType(EdocOpinion.OpinionType.senderOpinion.ordinal());
	            senderOpinion.setCreateTime(now);
	            senderOpinion.setCreateUserId(user.getId());	            
	            summary.getEdocOpinions().add(senderOpinion);
	        }
	        
	        Affair affair = new Affair();
	        affair.setIdIfNew();
	        affair.setApp(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()));
	        affair.setSubject(summary.getSubject());
	        affair.setCreateDate(now);
	        affair.setUpdateDate(now);
	        affair.setMemberId(user.getId());
	        affair.setObjectId(summary.getId());
	        affair.setSubObjectId(null);
	        affair.setSenderId(user.getId());
	        affair.setState(StateEnum.col_waitSend.key());
	        affair.setSubState(SubStateEnum.col_waitSend_draft.key());
	        affair.setIsDelete(senderOpinion.isDeleteImmediate);	        
	        affair.setIsTrack(senderOpinion.affairIsTrack);
	        affair.setBodyType(summary.getFirstBody().getContentType());
			affair.setHasAttachments(summary.isHasAttachments());
			affair.serialExtProperties();
			//紧急标志,如果没有，则设置为默认值1（普通）
			if(!(StringUtils.isNotBlank(summary.getUrgentLevel()))){
				summary.setUrgentLevel("1");
			}
			affair.setImportantLevel(Integer.parseInt(summary.getUrgentLevel()));
			_setIsUnit(summary);
			edocSummaryDao.save(summary);
	        if (senderOpinion.getContent() != null && !"".equals(senderOpinion.getContent())) {
            	edocOpinionDao.save(senderOpinion);
            }
	        affairManager.addAffair(affair);
	        return affair.getId();
	}

	public EdocSummary saveForward(Long summaryId, FlowData flowData,
			boolean forwardOriginalNode, boolean foreardOriginalopinion,
			EdocOpinion senderOpinion) throws EdocException {
		return null;
	}

	public void saveOpinion(EdocOpinion opinion,boolean isSendMessage) throws EdocException {
		edocOpinionDao.save(opinion);
		
        if(isSendMessage){
	        User user = CurrentUser.get();
	    	Long summaryId = opinion.getEdocSummary().getId();
	    	EdocSummary summary = opinion.getEdocSummary();
	    	ApplicationCategoryEnum appEnum=EdocUtil.getAppCategoryByEdocType(summary.getEdocType());
	    	List<Affair> affairList = affairManager.findAvailabilityByObject(appEnum, summaryId);
	    	List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
	    	for(Affair affair : affairList){
	    		Long memberId = affair.getMemberId();
	    		Long senderId = affair.getSenderId();
	    		if(memberId.intValue() == senderId.intValue())
	    			continue;
	    		receivers.add(new MessageReceiver(affair.getId(), memberId, "message.link.edoc.done", affair.getId().toString()));
	    	}
	    	try {
				userMessageManager.sendSystemMessage(new MessageContent("edoc.addnote", summary.getSubject(), user.getName(),EdocUtil.getAppCategoryByEdocType(summary.getEdocType()).getKey(), opinion.getContent()).setImportantLevel(summary.getImportantLevel()), appEnum, user.getId(), receivers);
			} catch (MessageException e) {
				log.error("发起人增加附言消息提醒失败", e);
			}
        }
	}
	public void setFinishedFlag(long summaryId, int summaryState) throws EdocException {
		setFinishedFlag(summaryId, summaryState, null, null, null, null);
	}
	 public void setFinishedFlag(long summaryId, 
				int summaryState,
				Long runTime,
				Long runWorkTime,
				Long overTime,
				Long overWorkTime) throws EdocException{
    	
		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put("completeTime", new Timestamp(System.currentTimeMillis()));
		columns.put("state", summaryState);
		columns.put("overTime", overTime == null ?0:overTime);
		columns.put("overWorkTime", overWorkTime ==null ?0:overWorkTime);
		columns.put("runTime", runTime == null ? 0 : runTime);
		columns.put("runWorkTime", runWorkTime == null ? 0: runWorkTime);
		
		edocSummaryDao.update(summaryId, columns);
		//更新公文统计标志
		try{
		edocStatManager.updateFlowState(summaryId,Constant.flowState.finish.ordinal());
		}catch(Exception e)
		{
			log.error("更新公文统计流程状态错误 summaryId="+summaryId,e);
		}
	}
	
    public int stepBackSummary(long userId, long summaryId, int from) throws EdocException{
    	User user = CurrentUser.get();
    	EdocSummary summary =  edocSummaryDao.get(summaryId);
        
        WorkflowEventListener.setOperationType(WorkflowEventListener.WITHDRAW);
        List<Affair> affairs = affairManager.findByObject(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()), summaryId);
        //获取所有待办事项
        List<Affair> colPendingAffairList = new ArrayList<Affair>();
        for(Affair pendingAffair : affairs){
        	if(pendingAffair.getState() == StateEnum.col_pending.key()){
        		colPendingAffairList.add(pendingAffair);
        	}
        }
        
        Long caseId = summary.getCaseId();
        int result = 0;
        if (caseId != null) {
        	try{
            result = EdocHelper.cancelCase(caseId);
        	}catch(Exception e)
        	{
        		throw new EdocException(e);
        	}
        }

        if (result == 1) {
            return result;
        }

        //将summary的状态改为待�发
        summary.setCaseId(null);
        edocSummaryDao.update(summary);

        if(affairs != null){
        	for(int i=0;i<affairs.size();i++){
        		Affair affair = (Affair) affairs.get(i);
	        	if(affair.getState() == StateEnum.col_sent.key()){
	        		affair.setState(StateEnum.col_waitSend.key());
	                affair.setSubState(SubStateEnum.col_waitSend_stepBack.key());
	                affair.setIsDelete(false);
	                affairManager.updateAffair(affair);
	        	}
        	}
        	this.affairManager.cancelWorkflow(summaryId);
        }
        //撤销后，删除公文统计数据
        try{
          edocStatManager.deleteEdocStat(summaryId);
        }catch(Exception e)
        {
        	throw new EdocException(e);
        }
        //TODO 写日�志
        log.info("summary is cancelled:" + summaryId);
        return 0;
    }

	public boolean stepBack(Long summaryId, Long affairId,
			EdocOpinion signOpinion) throws EdocException {
		User user = CurrentUser.get();
        EdocSummary summary =edocSummaryDao.get(summaryId);
        if (summary == null) {
            return false;
        }
        
        Affair affair = affairManager.getById(affairId);
        if (affair == null) {
            return false;
        }

        //设置变量发回退消息。
        List<Affair> allAvailabilityAffairList = affairManager.queryALLAvailabilityAffairList(summary.getId());
        
        Long _workitemId = affair.getSubObjectId();
        BPMActivity bPMActivity = null;
		try {
			//BPMProcess bPMProcess = EdocHelper.getProcess(summary.getProcessId()) ;
			//bPMActivity = EdocHelper.getBPMActivityByAffair(bPMProcess, affair); //当前节点
			bPMActivity = EdocHelper.getBPMActivityByAffair(affair) ;
		}
		catch (Exception e1) {	
		}

		Long caseId = summary.getCaseId();

        WorkflowEventListener.setOperationType(WorkflowEventListener.WITHDRAW);
        int result = -1;
    	try{
    		BPMProcess process = EdocHelper.getRunningProcessByCaseId(caseId);
            WorkItem workitem = EdocHelper.getWorkItemById(_workitemId);
            BPMActivity activity = process.getActivityById(workitem.getActivityId());
            Map resultMap= Utils.isAllHumenNodeValid(activity);
            String result_str= (String)resultMap.get("result");
            if("0".equals(result_str)) {
            	result=EdocHelper.stepBack(caseId, _workitemId, null, null, null);
            }else{
            	result= Integer.parseInt(result_str);
            }
        }catch(Exception e){
        	throw new EdocException(e);
        }
        if(result==0 || result==1){
            signOpinion.setEdocSummary(summary);
            signOpinion.setCreateTime(new Timestamp(System.currentTimeMillis()));
            signOpinion.setOpinionType(EdocOpinion.OpinionType.backOpinion.ordinal());
            affair.setIsTrack(signOpinion.affairIsTrack);
            edocOpinionDao.save(signOpinion);    
        }
        
        if(result == 0){
        	affair.setState(StateEnum.col_stepBack.key());
        	affair.setSubState(SubStateEnum.col_normal.key());
        	affairManager.updateAffair(affair);
        }
        //需要撤消流程
        if (result == 1) {
            stepBackSummary(CurrentUser.get().getId(), summary.getId(), StateEnum.col_stepBack.key());
            //撤销，则在应用日志中记录撤销记录
            appLogManager.insertLog(user, AppLogAction.Coll_Repeal, user.getName(), summary.getSubject());
            try{
            	//发送消息给督办人，更新督办状态，并删除督办日志、删除督办记录、删除催办次数
            	this.colSuperviseManager.updateStatusAndNoticeSupervisorWithoutMes(summaryId, Constant.superviseType.edoc.ordinal(), Constant.superviseState.waitSupervise.ordinal());	            
            }catch(Exception e){
        		log.error("删除待发事项相关督办信息异常：",e);
        	}
            //删除文档中心已归档的公文。
            deleteDocByResources(summary,user);
            
            //删除跟踪消息设置
            //deleteColTrackMembersByObjectId(summaryId);
            
            result = 0;
            
            for(Affair affair0 : allAvailabilityAffairList){
            	DateSharedWithWorkflowEngineThreadLocal.addToAllStepBackAffectAffairMap(affair0.getMemberId(), affair0.getId());
            }
            
            //撤销流程事件
            CollaborationCancelEvent event = new CollaborationCancelEvent(this);
            event.setSummaryId(summary.getId());
            event.setUserId(user.getId());
            EventDispatcher.fireEvent(event);
        }
        
        if (result == 0) {
	           try{
	        	    List<Affair> trackingAffairLists = affairManager.getAvailabilityTrackingAffairBySummaryId(summaryId);
	        	    Affair sentAffair = null ;
	        	    for (Affair _sentAffair : allAvailabilityAffairList) {
	        	    	if(_sentAffair.getState() == StateEnum.col_sent.getKey() 
	            				|| _sentAffair.getState() == StateEnum.col_waitSend.getKey()){
	            			sentAffair = _sentAffair;
	            			break;
	            		}
	        	    }
	        	    if (sentAffair == null) {
	        	    	sentAffair = this.affairManager.getCollaborationSenderAffair(summary.getId());
	        	    }
	        	    
	        	    trackingAffairLists.add(sentAffair);
	        	    
	          	    List<ColTrackMember> trackMembers = getColTrackMembersByObjectIdAndTrackMemberId(summaryId,null);
	                EdocMessageHelper.getTrackAffairExcludePart(trackingAffairLists, trackMembers,affair.getMemberId());
		   	        EdocMessageHelper.stepBackMessage(affairManager, orgManager, userMessageManager, trackingAffairLists, affair, summaryId, signOpinion);
		   	        
		   	        if(EdocHelper.isSecondNode(null,null,bPMActivity)){
		   	        	this.processLogManager.deleteLog(Long.valueOf(summary.getProcessId())) ;
		   	        }
		   	        else if(bPMActivity != null){
			   	        List<BPMAbstractNode> parents = EdocHelper.getParent(bPMActivity) ;
			   	        StringBuffer params = new StringBuffer() ;
			   	        for(BPMAbstractNode node : parents) {
		   	        		params.append(node.getName()).append("(").append(node.getSeeyonPolicy().getName()).append(")").append(" ");  	
			   	        }
				        this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.stepBack, params.toString()) ;        	
		   	        }
	           }catch(Exception e){
	        	   log.error("", e) ;
	           }
	        return true;
        } else {
            return false;
        }
	}
	/**
	 * 删除归档的公文
	 * @param summary
	 * @throws EdocException
	 */
	private void deleteDocByResources(EdocSummary summary ,User user) throws EdocException{
	   try {
	      	List<Long> ids=new ArrayList<Long>();
	      	ids.add(summary.getId());
	      	docHierarchyManager.deleteDocByResources(ids, user);
	      	summary.setHasArchive(false);
	      	edocSummaryDao.saveOrUpdate(summary);
		} catch (Exception e) {
			log.error("撤销公文流程，删除归档文档:"+e);
		}
	}

	public boolean stepStop(Long summaryId, Long affairId, EdocOpinion signOpinion) throws EdocException {
		EdocSummary summary =edocSummaryDao.get(summaryId);
		BPMActivity bPMActivity = null ;
		if (summary == null) {
            return false;
        }
        
		Affair curerntAffair = null;
		User user = CurrentUser.get();
		if(affairId != null){
			curerntAffair = affairManager.getById(affairId);
			if (curerntAffair == null) {
	            return false;
	        }
			if (signOpinion.isDeleteImmediate) {
	        	curerntAffair.setIsDelete(true);
	        }
            if(curerntAffair.getMemberId() != user.getId()){
                //由代理人终止需要写入处理人ID
                curerntAffair.setTransactorId(user.getId()); 
            }
            curerntAffair.setState(StateEnum.col_done.key());
            curerntAffair.setSubState(SubStateEnum.col_done_stepStop.key());
            Timestamp now = new Timestamp(System.currentTimeMillis());
            /*curerntAffair.setState(StateEnum.col_done.key());
            curerntAffair.setSubState(SubStateEnum.col_done_stepStop.key());*/
            curerntAffair.setCompleteTime(now);
            curerntAffair.setUpdateDate(now);
	        affairManager.updateAffair(curerntAffair);
		}
		else if(affairId == null && (user.isAdministrator() || user.isGroupAdmin())){
			//TODO 管理员终止，临时这么处理
			List<Affair> curerntAffairs = affairManager.getPendingAffairListByObject(summaryId);
	        if(curerntAffairs != null && !curerntAffairs.isEmpty()){
	        	try {
					curerntAffair = (Affair)org.apache.commons.beanutils.BeanUtils.cloneBean(curerntAffairs.get(0));
					curerntAffair.setMemberId(user.getId());
				}
				catch (Exception e) {
					log.error("", e);
				}
	        }
		}

        if (curerntAffair == null) {
            return false;
        }
        
        
        /*
        try {
            currentActivity = Long.parseLong(ColHelper.getActvityIdByAffair(curerntAffair));
		}
		catch (Exception e) {
			log.error("", e);
		}
        
        if (signOpinion.isDeleteImmediate) {
        	curerntAffair.setIsDelete(true);
        }
        if(curerntAffair.getMemberId() != user.getId()){
            //由代理人终止需要写入处理人ID
            curerntAffair.setTransactorId(user.getId()); 
        }*/
        
	    try{
	    	bPMActivity = EdocHelper.getBPMActivityByAffair(curerntAffair) ;//当前节点
	    }catch(Exception e) {
	    	log.error("记录流程日志获取当前节点时候出现问题",e) ;
	    }
       
        //将终止流程的当前Affair放入ThreadLocal，便于工作流中发送消息时获取代理信息。
	    DateSharedWithWorkflowEngineThreadLocal.setColSummary(summary);
        DateSharedWithWorkflowEngineThreadLocal.setTheStopAffair(curerntAffair);
        DateSharedWithWorkflowEngineThreadLocal.setFinishWorkitemOpinionId(signOpinion.getId(), signOpinion.getIsHidden(), signOpinion.getContent(), signOpinion.getAttribute(), signOpinion.isHasAtt());
        
        summary.setState(Constant.flowState.terminate.ordinal());	// 值参考Contant.java 枚举值
        signOpinion.setEdocSummary(summary);
        signOpinion.setCreateTime(new Timestamp(System.currentTimeMillis()));
		signOpinion.setOpinionType(EdocOpinion.OpinionType.stopOpinion.ordinal());
		signOpinion.setCreateUserId(user.getId());
		
		//终止时只记录当时用户填写的意见，被终止信息不保留
		/*ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.edoc.resources.i18n.EdocResource",CurrentUser.get().getLocale());
		signOpinion.setContent(user.getName()+ResourceBundleUtil.getString(r, "stopedoc.label"));*/
		
		edocOpinionDao.save(signOpinion);
        
        //affairManager.updateAffair(curerntAffair);
        
        //如果公文已经归档，则终止后不在已办中显示
        if(summary.getHasArchive()) {
        	//因为目前终止操作所有的待办都进入已办，所以将该公文在affair中所有archiveId为null的更新为-1
        	this.affairManager.updateArchiveInfo(summary.getId(), -1L);
        }
        
    	Long _workitemId = curerntAffair.getSubObjectId();
        
        WorkflowEventListener.setOperationType(WorkflowEventListener.STETSTOP);
        DateSharedWithWorkflowEngineThreadLocal.setFinishWorkitemOpinionId(signOpinion.getId(), signOpinion.getIsHidden(), signOpinion.getContent(), signOpinion.getAttribute(), signOpinion.isHasAtt());
        try {
			EdocHelper.stopWorkitem(_workitemId);
		}
        catch (Exception e) {
			log.error("", e);
		}
        
        try{
	        if(user.isAdministrator() || user.isGroupAdmin() || user.isSystemAdmin()){
		        this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), -1L, ProcessLogAction.stepStop, "") ;
	        }
	        else{
	           if(bPMActivity != null) {
	        	   this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.stepStop, "") ;  
	           }	            
	        }
        }
        catch(Exception e){
        	log.warn("", e) ;
        }
        
        return true;
	}

	public boolean takeBack(Long affairId) throws EdocException {
		
		
		User user = CurrentUser.get();
        Affair affair = affairManager.getById(affairId);
        if (affair == null) {
            return false;
        }

        Long summaryId = affair.getObjectId();
        EdocSummary summary =edocSummaryDao.get(summaryId);
        if (summary == null) {
            return false;
        }
        
        int colAffairState = StateEnum.col_pending.key();
        List<Affair> pendingAffairList = affairManager.queryColPendingAffairList(summaryId, colAffairState);

        Long _workitemId = affair.getSubObjectId();

        String processId = summary.getProcessId();
        Long caseId = summary.getCaseId();

        WorkflowEventListener.setOperationType(WorkflowEventListener.TAKE_BACK);
        int result = -1;
        try{
        	result=EdocHelper.takeBack(caseId, _workitemId, null, null, null);
        }catch(Exception e)
        {
        	throw new EdocException(e);
        }
		List<String> baseActions = null;
		if (result == 0) {
			MetadataNameEnum edocTypeEnum = EdocUtil
					.getEdocMetadataNameEnumByApp(affair.getApp());
			String nodePermissionPolicy = getPolicyByAffair(affair);
			baseActions = permissionManager.getActionList(edocTypeEnum.name(),
					nodePermissionPolicy, "basic", summary.getOrgAccountId());
		}
        if (result == 0) {
			// 删除封发时已发文
			if ((!Strings.isBlank(affair.getNodePolicy()) && "fengfa"
					.equals(affair.getNodePolicy()))
					|| ((baseActions != null) && (baseActions
							.contains("EdocExchangeType")))) {
				Long sendEdocId = 0l;
				for (int i = 0; i < pendingAffairList.size(); i++) {
					Affair tempPendAffair = pendingAffairList.get(i);
					if (tempPendAffair.getApp() == ApplicationCategoryEnum.exSend
							.key()) {
						sendEdocId = tempPendAffair.getSubObjectId();
						break;
					}
				}
				try {
					EdocSendRecord edocSendRecord = sendEdocManager
							.getEdocSendRecord(sendEdocId.longValue());
					if (edocSendRecord != null)
						sendEdocManager.delete(edocSendRecord.getId());
				} catch (Exception e) {
					log.error("取回公文时，删除封发后的已发送公文错误。", e);
					throw new EdocException(e);
				}
			}
		}
		if (result == 0) {
        	/*affair.setState(StateEnum.col_takeBack.key());
        	affair.setSubState(SubStateEnum.col_normal.key());*/
        	affair.setState(StateEnum.col_pending.key());
        	affair.setSubState(SubStateEnum.col_pending_unRead.key());
        	affairManager.updateAffair(affair);
			// 更新封发后取回时，本affair的已发事件的isdelete为true
			if ((!Strings.isBlank(affair.getNodePolicy()) && "fengfa"
					.equals(affair.getNodePolicy()))
					|| ((baseActions != null) && (baseActions
							.contains("EdocExchangeType")))) {

				// 批量更新
				Map<String, Object> columns = new Hashtable<String, Object>();
				columns.put("isDelete", true);
				columns.put("state", StateEnum.col_takeBack.key());
				affairManager.update(columns, new Object[][] {
						{ "app", ApplicationCategoryEnum.exSend.getKey() },
						{ "state", StateEnum.col_pending.key() },
						{ "objectId", summaryId } });
				// 更新summary的state为0，使其不显示流程结束图标
				Map<String, Object> summaryColumns = new HashMap<String, Object>();
				summaryColumns.put("state", 0);
				update(summaryId, summaryColumns);
				/*
				 * List<Affair> sentAndPendingAffairList = affairManager
				 * .getSentAndPendingAffairList(summaryId); for (Affair
				 * sentAffair : sentAndPendingAffairList) { if
				 * (sentAffair.getState() == StateEnum.col_pending.key() &&
				 * sentAffair.getApp() == ApplicationCategoryEnum.exSend
				 * .getKey()) { // 待办事项，且是公文交换中的待发送
				 * sentAffair.setIsDelete(true);
				 * sentAffair.setState(StateEnum.col_takeBack.key());//
				 * 设置为已取回，防止发送消息 affairManager.updateAffair(sentAffair); } }
				 */
				// 同时，不给自己发送一般消息，只发送跟踪消息
				int removeIndex = 0;
				boolean isRemove = false;
				for (int i = 0; i < pendingAffairList.size(); i++) {
					Affair tempPendAffair = pendingAffairList.get(i);
					if ((tempPendAffair.getMemberId().longValue() == user
							.getId())
							&& (tempPendAffair.getApp() == ApplicationCategoryEnum.exSend
									.key())) {
						removeIndex = i;
						isRemove = true;
						break;
					}
				}
				if (isRemove) {
					pendingAffairList.remove(removeIndex);
				}
			}
        }
        if (result == 0) {
        	EdocMessageHelper.takeBackMessage(affairManager, orgManager, userMessageManager, pendingAffairList, affair, summaryId);
           
        	try{
        		String paramer = "" ;
	   	        BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(affair) ;//当前节点
	   	        this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.takeBack, paramer);

        	}catch(Exception e){
        		log.error("", e) ;
        	}
        	return true;
        } else {
            return false;
        }
	}

	public void update(Long summaryId, Map<String, Object> columns) {
		edocSummaryDao.update(summaryId, columns);
	}
	public void update(EdocSummary summary) throws Exception
	{
		edocSummaryDao.update(summary);
		//更新公文统计数据
		edocStatManager.updateElement(summary);
		try{
			MetaUtil.refMeta(summary);
		}catch(Exception e){
			log.error("更改枚举项为引用出现异常 error = "+e);
		}
	}

	public void zcdb(EdocSummary edocSummary, Affair affair, EdocOpinion opinion, String processId, String userId) throws EdocException {
		Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
		//持久化修改后的流程
		User user = CurrentUser.get();
    	if(processId != null && userId != null){
    		try{
	    		BPMProcess process = ColHelper.saveModifyingProcess(processId, userId);
	    		if(process != null){
	    			ColHelper.updateRunningProcess(process);
	    			//如果该流程实例存在待添加的节点，将其激活
	    			ColHelper.saveAcitivityModify(process, userId);
	    			//更新完流程之后,发送消息提醒
	    			List<MessageData> messageDataList = messageDataMap.get(edocSummary.getId());
	    			if(messageDataList != null){
		    			for(MessageData messageData : messageDataList){
		    				if(Long.parseLong(userId) == messageData.handlerId){
			    				String operationType = messageData.getOperationType();
			    				List<String> partyNames = messageData.getPartyNames();
			    				Affair _affair = messageData.getAffair();
			    				if("insertPeople".equals(operationType)){
			    					EdocMessageHelper.insertPeopleMessage(affairManager, userMessageManager, orgManager, partyNames, edocSummary, _affair);
			    				    List<String[]> processLogParam =  messageData.getProcessLogParam() ;		    				   
			    				    BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点
			    				    for(String str[] : processLogParam){
			    				    	 this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.insertPeople, str);
			    				    }
			    				   
			    				}else if("deletePeople".equals(operationType)){
			    					EdocMessageHelper.deletePeopleMessage(affairManager, orgManager, userMessageManager, partyNames, edocSummary, _affair);
			    					BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点
			    					List<String[]> processLogParam = messageData.getProcessLogParam();
			    					for(String[] param : processLogParam){
			    						processLogManager.insertLog(user, Long.parseLong(process.getId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.deletePeople, param);
			    					}
			    				}else if("colAssign".equals(operationType)){
			    					EdocMessageHelper.colAssignMessage(userMessageManager, affairManager, orgManager, partyNames, edocSummary, _affair);
			    				    List<String[]> processLogParam =  messageData.getProcessLogParam() ;	    				   
			    				    BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点
			    				    for(String str[] : processLogParam){
			    				    	this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.colAssign,str);
			    				    }  	
			    				}else if("addInform".equals(operationType)){
			    					EdocMessageHelper.addInformMessage(userMessageManager, affairManager, orgManager, partyNames, edocSummary, _affair);
			    				    List<String[]> processLogParam =  messageData.getProcessLogParam() ;		    				   
			    				    BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点
			    				    for(String str[] : processLogParam){
			    				    	this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.inform, str);
			    				    }			    				    				    					
			    				}else if("addPassRead".equals(operationType)){
			    					EdocMessageHelper.addPassReadMessage(userMessageManager, affairManager, orgManager, partyNames, edocSummary, _affair);
			    					List<String[]> processLogParam =  messageData.getProcessLogParam() ;	    				   
			    				    BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点
			    				    for(String str[] : processLogParam){
			    				    	this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.passRound, str);
			    				    }			    				    	
			    				}else if("addMoreSign".equals(operationType)){
			    					BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(_affair);//当前节点
			    					List<String[]> processLogParam =  messageData.getProcessLogParam() ;
			    					for(String str[] : processLogParam){
			    						this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.addMoreSign, str);
			    					}
			    				    
			    				}	
		    				}
		    			}
		    			messageDataMap.remove(messageDataList);
	    			}
	    		}
    		}catch(Exception e){
    			log.error("", e);
    		}
    	}
		
    	try{
	    	long workItemId = affair.getSubObjectId();
	    	WorkflowEventListener.setOperationType(WorkflowEventListener.ZCDB);
	        EdocHelper.zcdbWorkitem(workItemId);
    	}catch(Exception e){
    		log.error("zcdb affair failed", e);
    	}
    	
        long startMemberId = affair.getSenderId();
        opinion.setIdIfNew();

        opinion.setEdocSummary(edocSummary);
        opinion.setCreateTime(new Timestamp(System.currentTimeMillis()));
        opinion.setOpinionType(EdocOpinion.OpinionType.provisionalOpinoin.ordinal());
        
        if(affair.getMemberId() != user.getId()){
        	List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(user.getId());
        	if(agentModelList != null && !agentModelList.isEmpty()){
        		opinion.setCreateUserId(affair.getMemberId());
        		opinion.setProxyName(user.getName());
        	}else{
        		opinion.setCreateUserId(user.getId());
        	}
        }else{
        	opinion.setCreateUserId(user.getId());
        }

        try{
        	edocOpinionDao.save(opinion);
    		String params = "" ;
    		BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(affair);//当前节点
    		//params = EdocHelper.checkNextNodeMembers(bPMActivity) ;
    		this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.zcdb, params);         	
        }catch(Exception e){
        	log.error("", e) ;
        }
        
        Integer sub_state = affair.getSubState();
        if (sub_state == null || sub_state.intValue() == SubStateEnum.col_normal.key()|| sub_state.intValue() == SubStateEnum.col_pending_unRead.key() || sub_state.intValue() == SubStateEnum.col_pending_read.key()) {
            affair.setSubState(SubStateEnum.col_pending_ZCDB.key());
            this.affairManager.updateAffair(affair);
        }
        
        //暂存待办消息提醒
        EdocMessageHelper.zcdbMessage(userMessageManager, orgManager, affairManager, affair);
	}
	
	public void zcdb(Affair affair, EdocOpinion opinion, String title,String supervisorMemberId,String supervisorNames,String superviseDate, EdocSummary summary, String processId, String userId) throws EdocException {
		
		this.zcdb(summary, affair, opinion, processId, userId);
        //督办开始--
        this.edocSupervise(title, supervisorMemberId, supervisorNames, superviseDate, summary);
        //督办结束--  
		
	}
	
	public boolean updateHtmlBody(long bodyId,String content) throws EdocException
	{
		User user = CurrentUser.get();
		EdocBody edocBody=edocBodyDao.get(bodyId);
		if(edocBody!=null)
		{		
			edocBody.setContent(content);
			edocBody.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			edocBodyDao.update(edocBody);			
			EdocMessageHelper.saveBodyMessage(affairManager, userMessageManager, orgManager, edocBody.getEdocSummary());
			EdocSummary summary = edocBody.getEdocSummary();
			if(null!=summary){
			//operationlogManager.insertOplog(summary.getId(), bodyId,ApplicationCategoryEnum.edoc, 
    		//		EactionType.LOG_EDOC_UPDATE_CONTENT, EactionType.LOG_EDOC_UPDATE_CONTENT_DESCRIPTION, user.getName(), summary.getSubject());			
			}
			return true;
		}
		return false;
	}
	
	public void deleteEdocOpinion(Long opinionId) throws EdocException
	{
		edocOpinionDao.delete(opinionId);
	}
	
	/**
     * 待发列表，点击理解发送
     * @param summary
     * @param map：  调用模版时候，角色匹配选择人员数据
     * @throws EdocException
     */
    public void sendImmediate(Long affairId,EdocSummary summary,FlowData flowData) throws EdocException
    {
    	User user = CurrentUser.get();
    	Long summaryId=summary.getId();
    	String processId=summary.getProcessId();    	
		//edocManager.clearSummaryOCA(summaryId, false);
		java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
		summary.setCreateTime(now);

		Map<String, Object> summ = new HashMap<String, Object>();
		summ.put("createTime", now);
		summ.put("startTime", now);
		try
		{
			//FlowData flowData = EdocHelper.getRunningProcessPeople(processId);
			//flowData.setAddition(map);

			EdocHelper.saveOrUpdateProcessByFlowData(flowData, processId,false);

			long caseId = EdocHelper.runCase(processId,ApplicationCategoryEnum.edoc);
			summ.put("caseId", caseId);
			
			edocSummaryDao.update(summary.getId(),summ);
			
			edocStatManager.createState(summary, user);
			
			edocOpinionDao.deleteDealOpinion(summaryId);
			//删除处理意见，手写批注			
	    	htmlSignDao.delete(new String[]{"summaryId"},new Object[]{summaryId});	    	
		}catch(Exception e)
		{
			throw new EdocException(e);
		}

//		Map<String, Object> aff = new HashMap<String, Object>();
		
		Affair updAffair=affairManager.getById(affairId);
		
		updAffair.setApp(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()).ordinal());
		updAffair.setSubObjectId(null);
		updAffair.setState(StateEnum.col_sent.key());
		updAffair.setSubState(SubStateEnum.col_normal.key());
		updAffair.setCreateDate(now);
		affairManager.updateAffair(updAffair);
		
		try{
			BPMProcess process = EdocHelper.getCaseProcess(processId) ;
			String params = EdocHelper.checSecondNodeMembers(process,flowData.getCondition()) ;
        	this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), -1l, ProcessLogAction.drafting, params) ;
        	this.appLogManager.insertLog(user, AppLogAction.Edoc_Send, user.getName() ,summary.getSubject()) ;			
		}catch(Exception e){
			log.error("流程日志写入失败：", e) ;
		}

		EdocHelper.createQuartzJobOfSummary(summary, workTimeManager);
    }

	public void setEdocSuperviseManager(EdocSuperviseManager edocSuperviseManager) {
		this.edocSuperviseManager = edocSuperviseManager;
	}
	public EdocOpinion findBySummaryIdAndAffairId(long summaryId,long affairId)
	{
		return edocOpinionDao.findBySummaryIdAndAffairId(summaryId,affairId);
	}
	private void edocSupervise(String title,String supervisorMemberId,String supervisorNames,String superviseDate, EdocSummary summary){
		edocSuperviseManager.supervise(title, supervisorMemberId, supervisorNames, superviseDate, summary);		
	}
	public String createContentBody(String summaryId,int contentNum,String srcOfficeId,String bodyType)
	{
		String newOfficeId=edocBodyDao.createContentNum(summaryId,contentNum,bodyType);
		if(srcOfficeId!=null&&!"".equals(srcOfficeId))
			signetManager.insertSignet(Long.parseLong(srcOfficeId), Long.parseLong(newOfficeId));
		return newOfficeId;
	}
	public String createContentBody(String summaryId, int contentNum, String srcOfficeId) {
		String newOfficeId=edocBodyDao.createContentNum(summaryId,contentNum);
		signetManager.insertSignet(Long.parseLong(srcOfficeId), Long.parseLong(newOfficeId));
		return newOfficeId;
	}
	public String getModifyingProcessXML(String processId) throws ColException {
    	String userId = CurrentUser.get().getId() + "";
        return ColHelper.getModifyingProcessXML(userId, processId);
    }
	
	public List<ResultModel> iSearch(ConditionModel cModel)
	{
		List<ResultModel> rsms=new ArrayList<ResultModel>();
        String exp0 = null;     
        String paramName = null;
        String paramValue = null;
        Map<String, Object> parameterMap = new HashMap<String, Object>();
		List<String> paramNameList = new ArrayList<String>();
        User user = CurrentUser.get();
        long user_id = user.getId();
        
        String hql = "select affair,summary from Affair as affair,EdocSummary as summary"        	
                + " where";  
        hql += " affair.memberId=" + user_id;
		
        
        hql+= " and affair.objectId=summary.id"
                + " and affair.isDelete!=true";                
        hql+= " and affair.app in ("+ApplicationCategoryEnum.edocSend.getKey() +","+ApplicationCategoryEnum.edocRec.getKey()+","+ApplicationCategoryEnum.edocSign.getKey()+")";                        

        if (cModel.getTitle()!=null) {
        	paramName = "subject";
			exp0 = " and summary.subject like :" + paramName + " ";
			paramValue = "%" + SQLWildcardUtil.escape(cModel.getTitle()) + "%";
			paramNameList.add(paramName);
			parameterMap.put(paramName, paramValue);
			hql = hql + exp0;			
        }
        if (cModel.getKeywords()!=null) {
        	paramName = "keywords";
            exp0 = " and summary.keywords like :"+paramName+" ";
            paramValue = "%" + SQLWildcardUtil.escape(cModel.getKeywords()) + "%";
			paramNameList.add(paramName);
			parameterMap.put(paramName, paramValue);
            hql = hql + exp0;
        }
        if (cModel.getFromUserId()!=null) {  
        	paramName = "createUser";
            exp0 = " and summary.startUserId = :"+paramName+" ";
            exp0 +=" and affair.state="+StateEnum.col_sent.getKey();
			paramNameList.add(paramName);
			parameterMap.put(paramName, cModel.getFromUserId());
            hql = hql + exp0;
        }
        else 
        {//查发给我的
        	//paramName = "createUser";
            //exp0 = " and summary.startUserId = :"+paramName+" ";
            exp0 =" and (affair.state="+StateEnum.col_pending.getKey()+" or affair.state="+StateEnum.col_done.getKey()+")";
			//paramNameList.add(paramName);
			//parameterMap.put(paramName, cModel.getFromUserId());
            hql = hql + exp0;
        }
        
        if (cModel.getBeginDate()!=null) {        	
        	paramName = "timestamp1";
			hql = hql + " and summary.startTime >= :" + paramName;

			paramNameList.add(paramName);
			parameterMap.put(paramName, cModel.getBeginDate());
        }
        if (cModel.getEndDate()!=null) {        	
        	paramName = "timestamp2";
			hql = hql + " and summary.startTime <= :" + paramName;

			paramNameList.add(paramName);
			parameterMap.put(paramName, cModel.getEndDate());
        }
        //归挡标志
        paramName = "isPigeonholed";
        cModel.getPigeonholedFlag();
        hql = hql + " and summary.hasArchive = :" + paramName;
        paramNameList.add(paramName);
		parameterMap.put(paramName, cModel.getPigeonholedFlag());

        hql = hql + " order by summary.startTime desc";

        parameterMap.put(SearchManager.NAME_LIST, paramNameList);
		List result = searchManager.searchByHql(hql, parameterMap);

        List<ResultModel> models = new ArrayList<ResultModel>();
        for (int i = 0; i < result.size(); i++) {
            Object[] object = (Object[]) result.get(i);
            Affair affair = (Affair) object[0];
            EdocSummary summary = (EdocSummary) object[1];            

            //开始组装最后返回的结果
            String fromUserName="";
            try{
            fromUserName=orgManager.getMemberById(summary.getStartUserId()).getName();
            }catch(Exception e)
            {
            	log.error("",e);
            }
            String bodyType = affair.getBodyType();
            boolean hasAttachments = summary.isHasAttachments();
            ResultModel rsm = new ResultModel(summary.getSubject(),fromUserName,summary.getStartTime(),"","",bodyType,hasAttachments);
            
            String edocLocationName=EdocUtil.getEdocLocationName(summary.getEdocType(),affair.getState());
            rsm.setLocationPath(edocLocationName);
            
            if (affair.getState()== StateEnum.col_waitSend.key()) {            	
                rsm.setOpenLink("/edocController.do?method=detail&from=WaitSend&affairId="+affair.getId());
            } else if (affair.getState() == StateEnum.col_sent.key()) {
            	rsm.setOpenLink("/edocController.do?method=detail&from=sended&affairId="+affair.getId());
            } else if (affair.getState() == StateEnum.col_done.key()) {
            	rsm.setOpenLink("/edocController.do?method=detail&from=Done&affairId="+affair.getId());                      
            } else if (affair.getState() == StateEnum.col_pending.key()) {
            	rsm.setOpenLink("/edocController.do?method=detail&from=Pending&affairId="+affair.getId());
            }else{
            	rsm.setOpenLink("/edocController.do?method=detail&from=Done&affairId="+affair.getId());
            }           
            
            rsms.add(rsm);
        }
		return rsms;
	}
	
	public boolean useMetadataValue(Long domainId,Long metadataId,String value)
	{
		boolean ret=true;
		String fieldNames=edocElementManager.getRefMetadataFieldName(domainId, metadataId);
		if("".equals(fieldNames)){ret=false;return ret;}
		ret=edocSummaryDao.isUseMetadataValue(fieldNames, value);
		return ret;
	}
	public EdocElementManager getEdocElementManager() {
		return edocElementManager;
	}
	public void setEdocElementManager(EdocElementManager edocElementManager) {
		this.edocElementManager = edocElementManager;
	}
	
	/**
	 * 获取流程详细信息
	 * @param subject
	 * @param beginDate
	 * @param endDate
	 * @param memberIdList
	 * @param endFlag
	 * @return
	 * @modify by lilong 2012-01-04 增加部门授权支持，是否包含子部门代码修改
	 */
	public List<WorkflowData> queryWorkflowDataByCondition(String subject, String beginDate, String endDate, List<String> objectStrs,
			int flowstate, int appKey, String operationType, String[] operationTypeIds, boolean paginationFlag) {
        List<WorkflowData> models = new ArrayList<WorkflowData>();
        List<Object> objects = new ArrayList<Object>();

        String hql = " from EdocSummary as summary" + " where";
        
        String sqlStr1 = "";
        if(objectStrs != null && objectStrs.size() != 0){
        	/** 此处代码改动较大2012-01-05 by lilong 增加对流程管理的 */
        	hql += " (";
	        for(int i = 0; i < objectStrs.size(); i++){
	        	String objectStr = objectStrs.get(i);
	        	String[] objectArr = objectStr.split("[|]");//按照竖线分割，排除正则表达式的影响用[]转义
	        	
	        	if(i > 0){
	        		sqlStr1 += " or";
	        	}
	        	String propName = null;
	        	if(objectArr[0].equals(V3xOrgEntity.ORGENT_TYPE_MEMBER)){
	        		propName = "startUserId";
	        	}
	        	else if(objectArr[0].equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT)){
	        		/** 不包含子部门 */
	        		if(objectArr.length > 2 
	        				&& objectArr[0].equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT) 
	        				&& 1 == Long.valueOf(objectArr[2])) {
	        			sqlStr1 += " summary.orgDepartmentId" + "= ? ";
	        			objects.add(new Long(objectArr[1]));
	        			continue;
	        		}
	        		/** 包含子部门 */
	        		sqlStr1 += " summary.orgDepartmentId" + "= ? or ";
    				objects.add(new Long(objectArr[1]));
					List<V3xOrgDepartment> v3xOrgDepartmentList;
					try {
						v3xOrgDepartmentList = this.orgManager.getChildDepartments(Long.valueOf(objectArr[1]), false);
						for(V3xOrgDepartment orgDepartment : v3xOrgDepartmentList) {
							sqlStr1 += " summary.orgDepartmentId" + "= ? or";
							objects.add(orgDepartment.getId());
						}
						sqlStr1 = sqlStr1.substring(0, sqlStr1.lastIndexOf("or")-1);//去除尾部多余or字符串
						continue;
					}catch (BusinessException e) {
						log.error("流程管理获取子部门ID异常EdocManagerImpl.queryWorkflowDataByCondition" + e.getLocalizedMessage());
						break;
					}
	        	}
	        	else if(objectArr[0].equals(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)){
	        		propName = "orgAccountId";
	        	}
	        	else{
	        		continue;
	        	}
	        	sqlStr1 += " summary." + propName + "=?";
	        	objects.add(new Long(objectArr[1]));
	        }
	        sqlStr1 += ")";
        }else if(CurrentUser.get().isAdministrator()){
        	sqlStr1 = " summary.orgAccountId=" + CurrentUser.get().getLoginAccount();
        }
        
    	int edocType = EdocUtil.getEdocTypeByAppCategory(appKey);
    	if(!"".equals(sqlStr1)){
			sqlStr1 += " and summary.edocType=" + edocType;
		}else{
			sqlStr1 = " summary.edocType=" + edocType;
		}
        
        if("template".equals(operationType)){
        	 if(!"".equals(operationTypeIds[0])){
				for(int i=0; i<operationTypeIds.length; i++){
		    		Long templeteId = Long.parseLong(operationTypeIds[i]);
		    		if(!"".equals(sqlStr1)){
			        	if(operationTypeIds.length == 1){
		        			sqlStr1 += " and (summary.templeteId=" + templeteId + ")";
			        	}else{
				        	if(i == 0){
			        			sqlStr1 += " and (summary.templeteId=" + templeteId + " or";
				        	}else if(i == (operationTypeIds.length-1)){
				        		sqlStr1 += " summary.templeteId=" + templeteId + ")";
				        	}else{
				        		sqlStr1 += " summary.templeteId=" + templeteId + " or";
				        	}
			        	}
		    		}else{
		    			if(operationTypeIds.length == 1){
			        		sqlStr1 = " (summary.templeteId=" + templeteId + ")";
			        	}else{
				        	if(i == 0){
				        		sqlStr1 = " (summary.templeteId=" + templeteId + " or";
				        	}else if(i == (operationTypeIds.length-1)){
				        		sqlStr1 += " summary.templeteId=" + templeteId + ")";
				        	}else{
				        		sqlStr1 += " summary.templeteId=" + templeteId + " or";
				        	}
			        	}
		    		}
		    	}
        	}
    		else{
    			if(!"".equals(sqlStr1)){
            		sqlStr1 += " and summary.templeteId is not null";
            	}else{
            		sqlStr1 += " summary.templeteId is not null";
            	}
	    	}
        }else if("self".equals(operationType)){
        	if(!"".equals(sqlStr1)){
        		sqlStr1 += " and summary.templeteId is null";
        	}else{
        		sqlStr1 += " summary.templeteId is null";
        	}
        }
        
    	if (!"".equals(subject)) {
    		if(!"".equals(sqlStr1)){
    			sqlStr1 += " and (summary.subject like ?)";
    		}else{
    			sqlStr1 = " (summary.subject like ?)";
    		}
            objects.add("%" + SQLWildcardUtil.escape(subject) + "%");
        }
        if (!"".equals(beginDate)) {
        	if(!"".equals(sqlStr1)){
        		sqlStr1 += " and summary.createTime >= ?";
        	}else{
        		sqlStr1 = " summary.createTime >= ?";
        	}
            java.util.Date stamp = Datetimes.getTodayFirstTime(beginDate);

            objects.add(stamp);
        } 
        if (!"".equals(endDate)){
        	if(!"".equals(sqlStr1)){
        		sqlStr1 += " and summary.createTime <= ?";
        	}else{
        		sqlStr1 = " summary.createTime <= ?";
        	}
            java.util.Date stamp = Datetimes.getTodayLastTime(endDate);

            objects.add(stamp);
        }
        
		if(!"".equals(sqlStr1)){
			sqlStr1 += " and summary.state=?";
		}else{
			sqlStr1 = " summary.state=?";
		}
		
		objects.add(flowstate);
    	
    	if(!"".equals(sqlStr1)){
			sqlStr1 += " and summary.caseId is not null";
		}else{
			sqlStr1 = " summary.caseId is not null";
		}
    	
    	String selectSummary = " summary.id," +
		"summary.subject," +
		"summary.startUserId," +
		"summary.createTime," +
		"summary.startTime," +
		"summary.completeTime," +
		"summary.processId," +
		"summary.caseId," +
		"summary.edocType," +
		"summary.deadline," +
		"summary.advanceRemind,"+
		"summary.templeteId ";
    	

        String selectHql = "select" + selectSummary + hql + sqlStr1 +" order by summary.createTime desc";
        List result = null;
        if(paginationFlag){
        	result =  edocSummaryDao.find(selectHql, null, objects);
        }else{
        	result =  edocSummaryDao.find(selectHql, -1, -1, null, objects);
        }
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                Object[] object = (Object[]) result.get(i);
    			EdocSummary summary = new EdocSummary();
    			int n = 0;
    			summary.setId((Long)object[n++]);
    			summary.setSubject((String)object[n++]);
    			summary.setStartUserId((Long)object[n++]);
    			summary.setCreateTime((Timestamp)object[n++]);
    			summary.setStartTime((Timestamp)object[n++]);
    			summary.setCompleteTime((Timestamp)object[n++]);
    			summary.setProcessId((String)object[n++]);
    			summary.setCaseId((Long)object[n++]);
    			summary.setEdocType((Integer)object[n++]);
    			summary.setDeadline((Long)object[n++]);
    			summary.setAdvanceRemind((Long)object[n++]);
    			Object templeteId = object[n++];
    			if(templeteId != null){
    				summary.setTempleteId((Long)templeteId);
    			}
//    			开始组装最后返回的结果
                WorkflowData model = new WorkflowData();

                String appTypeName = "";
                String appEnumStr = "";
                if(summary.getEdocType() == EdocEnum.edocType.sendEdoc.ordinal()){
                	appTypeName = Constant.getCommonString("application."+ApplicationCategoryEnum.edocSend.key()+".label");
                	appEnumStr = ApplicationCategoryEnum.edocSend.toString();
                }else if(summary.getEdocType() == EdocEnum.edocType.recEdoc.ordinal()){
                	appTypeName = Constant.getCommonString("application."+ApplicationCategoryEnum.edocRec.key()+".label");
                	appEnumStr = ApplicationCategoryEnum.edocRec.toString();
                }else{
                	appTypeName = Constant.getCommonString("application."+ApplicationCategoryEnum.edocSign.key()+".label");
                	appEnumStr = ApplicationCategoryEnum.edocSign.toString();
                }
                
                String depName = "";
                String accName = "";
                V3xOrgMember member = null;
				try {
					member = orgManager.getMemberById(summary.getStartUserId());
					V3xOrgDepartment dep = orgManager.getDepartmentById(member.getOrgDepartmentId());
					if(dep != null){
						depName = dep.getName();
	                    V3xOrgAccount acc = orgManager.getAccountById(dep.getOrgAccountId());
	                    if(acc != null){
	                        accName = acc.getShortname();
	                        model.setAccountId(acc.getId());
	                    }
					}
				} catch (BusinessException e) {
					log.error("", e);
				}
				
				if(CurrentUser.get().isGroupAdmin()){
					model.setDepName(depName+"("+accName+")");
				}else{
					model.setDepName(depName);
				}
				
				model.setSummaryId(String.valueOf(summary.getId()));
                model.setAppType(appTypeName);
                model.setInitiator(member.getName());
                model.setSendTime(summary.getCreateTime());
                model.setSubject(summary.getSubject());
                model.setProcessId(summary.getProcessId());
                model.setCaseId(summary.getCaseId());
                model.setDeadLine(summary.getDeadline());
                model.setAdvanceRemind(summary.getAdvanceRemind());
                model.setAppEnumStr(appEnumStr);
                
                if(flowstate == 1 || flowstate == 3){
                	model.setEndFlag(0);
                }
                if(summary.getTempleteId() != null){
                	model.setIsFromTemplete(true);
                	model.setTempleteId(summary.getTempleteId());
                }
                models.add(model);
            }
        }
        return models;
    }
	
    /**
     * 通过processId取到summary
     * @param processId
     * @return
     */
    public EdocSummary getSummaryByProcessId(String processId) {
    	DetachedCriteria criteria = DetachedCriteria.forClass(EdocSummary.class);
    	criteria.add(Restrictions.eq("processId", processId));
    	 return (EdocSummary)edocSummaryDao.executeUniqueCriteria(criteria);
    }
    /**
     * 重写公文查询(成发集团项目)
     * @param curUserId  当前用户ID
     * @param em		 查询条件
     * @param needByPage 是否需要分页
     * @return
     */
    public List<EdocSummaryModel> queryByCondition(long curUserId,EdocSearchModel em,boolean needByPage,Integer secretLevel) {
    	List<EdocSummaryModel> models = new ArrayList<EdocSummaryModel>();        
        
        List<Object> objects = new ArrayList<Object>();
        String hql = "select distinct  "+selectSummary+",summary.archiveId,affair.bodyType,affair.id  from EdocSummary as summary , Affair as affair"
        		+ " where (affair.objectId=summary.id) and (affair.memberId=?)"
        		//+ " and (affair.state=" + state.key() + ")"
        		+ " and (affair.state in ("+StateEnum.col_done.getKey()+","+StateEnum.col_pending.getKey()+","+StateEnum.col_sent.getKey()+") )"
        		+ " and (affair.app=?)"
                + " and affair.isDelete=false"
                //310sp2:不根据文件的密级限制查询权限。muj
                // + " and (summary.hasArchive=false or (summary.hasArchive=true and (summary.secretLevel='1' or summary.secretLevel='5' or summary.secretLevel is null)))"//不读取归档密级不是普通的
        		+ " and summary.state!=" + Constant.flowState.deleted.ordinal();//不读取公文归档后删除。		
        		//+ " and affair.archiveId is null";
        objects.add(curUserId);
        objects.add(EdocUtil.getAppCategoryByEdocType(em.getEdocType()).key());
        
        if(secretLevel != null){
        	hql += " and (summary.edocSecretLevel <="+secretLevel+" or summary.edocSecretLevel is null)";//成发集团项目 根据密级筛选公文
        }
        if (!Strings.isBlank(em.getSubject())) {        	
                hql += " and (summary.subject like ?)";
                objects.add("%" + SQLWildcardUtil.escape(em.getSubject()) + "%");
            }
        if (!Strings.isBlank(em.getDocMark())) {
                hql += " and (summary.docMark like ? or summary.docMark2 like ?)";
                objects.add("%" + SQLWildcardUtil.escape(em.getDocMark()) + "%");
                objects.add("%" + SQLWildcardUtil.escape(em.getDocMark()) + "%");
            }
        if (!Strings.isBlank(em.getSerialNo())) {
                hql += " and (summary.serialNo like ?)";
                objects.add("%" + SQLWildcardUtil.escape(em.getSerialNo()) + "%");
            }
        if (!Strings.isBlank(em.getKeywords())) {
            hql += " and (summary.keywords like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getKeywords()) + "%");
        }
        if (!Strings.isBlank(em.getDocType())) {
            hql += " and (summary.docType = ?)";
            objects.add(em.getDocType());
        }
        if (!Strings.isBlank(em.getSendType())) {
            hql += " and (summary.sendType = ?)";
            objects.add(em.getSendType());
        }
        if (!Strings.isBlank(em.getCreatePerson())) {
            hql += " and (summary.createPerson like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getCreatePerson()) + "%");
        }
        if (em.getCreateTimeB()!=null) {
            hql += " and (summary.createTime >= ?)";
            objects.add(Datetimes.getTodayFirstTime(em.getCreateTimeB()));
        }
        if (em.getCreateTimeE()!=null) {
            hql += " and (summary.createTime <= ?)";
            objects.add(Datetimes.getTodayLastTime(em.getCreateTimeE()));
        }
        if (!Strings.isBlank(em.getSendToId())) {
        	//主送
            hql += " and ((summary.sendToId like ? or summary.sendToId2 like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
            //抄送
            hql += " or (summary.copyToId like ? or summary.copyToId2 like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
            //抄报
            hql += " or (summary.reportToId like ? or summary.reportToId2 like ?))";
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
        }
        if (!Strings.isBlank(em.getSendUnit())) {
            hql += " and (summary.sendUnit like ? or summary.sendUnit2 like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getSendUnit()) + "%");
            objects.add("%" + SQLWildcardUtil.escape(em.getSendUnit()) + "%");
        }
        if (!Strings.isBlank(em.getIssuer())) {
            hql += " and (summary.issuer like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getIssuer()) + "%");
        }
        if (em.getSigningDateA()!=null) {
            hql += " and (summary.signingDate >= ?)";
            objects.add(Datetimes.getTodayFirstTime(em.getSigningDateA()));
        }
        if (em.getSigningDateB()!=null) {
            hql += " and (summary.signingDate <= ?)";
            objects.add(Datetimes.getTodayLastTime(em.getSigningDateB()));
        }
        
        String selectHql = hql + " order by summary.createTime desc";

        List result =new ArrayList();

        if(needByPage){ //需要分页
        	 result = edocSummaryDao.find(selectHql,"affair.id",true, null, objects); 
        }else { //不需要分页
        	 result = edocSummaryDao.find(selectHql,-1,-1,null,objects);
        }
        String summaryIds = "";
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
            	Object[] object = (Object[]) result.get(i);
                //Affair affair = new Affair();
                EdocSummary summary = new EdocSummary();
                make(object,summary);
                summary.setEdocType(em.getEdocType());
                
                try {
                    V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
                    summary.setStartMember(member);
                }
                catch (BusinessException e) {
                    log.error("", e);
                }

                //开始组装最后返回的结果
                EdocSummaryModel model = new EdocSummaryModel();

                model.setStartDate(new Date(summary.getCreateTime().getTime()));
                model.setWorkitemId(summary.getId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                //model.setAffairId(affair.getId());
                //summaryIds.add(summary.getId());
                Object archiveId=object[object.length-3];
                if(archiveId != null ){
	            	if("".equals(summaryIds)) summaryIds=String.valueOf(archiveId);
	    			else summaryIds+=","+String.valueOf(archiveId);
	            	model.getSummary().setArchiveId((Long)(archiveId));
                }
            	
                model.setAffairId((Long)object[object.length-1]);
                model.setBodyType((String)object[object.length-2]);

                
               
                /*
                //协同状
                Integer sub_state = affair.getState();                
                if (sub_state != null) {
                    model.setState(sub_state.intValue());
                }

                //是否跟踪
                Boolean isTrack = affair.getIsTrack();
                if (isTrack != null) {
                    model.setIsTrack(isTrack.booleanValue());
                }
                */
                String sendToUnit = "";
                if (!Strings.isBlank(summary.getSendTo())) 
                {
                	sendToUnit = summary.getSendTo();
				}
                if(!Strings.isBlank(sendToUnit)){
	            	if (!Strings.isBlank(summary.getCopyTo())) 
	            	{
	            		sendToUnit = sendToUnit + "、" + summary.getCopyTo();
					}
                }
                else
                {
                	sendToUnit = summary.getCopyTo();
                }
                if(!Strings.isBlank(sendToUnit)){
	            	if (!Strings.isBlank(summary.getReportTo())) 
	            	{
	            		sendToUnit = sendToUnit + "、" + summary.getReportTo();
					}
                }
                else
                {
                	sendToUnit = summary.getReportTo();
                }
                model.setSendToUnit(sendToUnit);
                
                models.add(model);
            }
        }
        //查询DocResouce,获取归档路径。
        List<DocResource> docs=docHierarchyManager.getDocsByIds(summaryIds);
        for(EdocSummaryModel model: models){
        	Long summaryId = model.getSummary().getId();
        	if(summaryId != null && model.getSummary().getHasArchive()){
	        	for(DocResource doc :docs){
	        		if(doc.getId().equals(model.getSummary().getArchiveId())){
	        			
	        			String frName=doc.getFrName();
	        			if (com.seeyon.v3x.doc.util.Constants.needI18n(doc.getFrType()));
	        				frName = com.seeyon.v3x.doc.util.Constants.getDocI18nValue(frName);
	        			
	        			if(doc.getLogicalPath()!=null && doc.getLogicalPath().split("\\.").length>1)
	        				frName=com.seeyon.v3x.edoc.util.Constants.Edoc_PAGE_SHOWPIGEONHOLE_SYMBOL+java.io.File.separator+frName;
	        			
	        			model.setArchiveName(frName);
	        			model.setLogicalPath((String)doc.getLogicalPath());
	        			break;
	        		}
	        	}
        	}
        }
        return models;	
    }
    /**
     * 公文查询
     * @param curUserId  当前用户ID
     * @param em		 查询条件
     * @param needByPage 是否需要分页
     * @return
     */
    public List<EdocSummaryModel> queryByCondition(long curUserId,EdocSearchModel em,boolean needByPage) {
    	List<EdocSummaryModel> models = new ArrayList<EdocSummaryModel>();        
        
        List<Object> objects = new ArrayList<Object>();
        String hql = "select  "+selectSummary+",summary.archiveId,affair.bodyType,affair.id  from EdocSummary as summary , Affair as affair"
        		+ " where (affair.objectId=summary.id) and (affair.memberId=?)"
        		//+ " and (affair.state=" + state.key() + ")"
        		+ " and (affair.state in ("+StateEnum.col_done.getKey()+","+StateEnum.col_pending.getKey()+","+StateEnum.col_sent.getKey()+") )"
        		+ " and (affair.app=?)"
                + " and affair.isDelete=false"
                //310sp2:不根据文件的密级限制查询权限。muj
                // + " and (summary.hasArchive=false or (summary.hasArchive=true and (summary.secretLevel='1' or summary.secretLevel='5' or summary.secretLevel is null)))"//不读取归档密级不是普通的
        		+ " and summary.state!=" + Constant.flowState.deleted.ordinal();//不读取公文归档后删除。		
        		//+ " and affair.archiveId is null";
        objects.add(curUserId);
        objects.add(EdocUtil.getAppCategoryByEdocType(em.getEdocType()).key());

        if (!Strings.isBlank(em.getSubject())) {        	
                hql += " and (summary.subject like ?)";
                objects.add("%" + SQLWildcardUtil.escape(em.getSubject()) + "%");
            }
        if (!Strings.isBlank(em.getDocMark())) {
                hql += " and (summary.docMark like ? or summary.docMark2 like ?)";
                objects.add("%" + SQLWildcardUtil.escape(em.getDocMark()) + "%");
                objects.add("%" + SQLWildcardUtil.escape(em.getDocMark()) + "%");
            }
        if (!Strings.isBlank(em.getSerialNo())) {
                hql += " and (summary.serialNo like ?)";
                objects.add("%" + SQLWildcardUtil.escape(em.getSerialNo()) + "%");
            }
        if (!Strings.isBlank(em.getKeywords())) {
            hql += " and (summary.keywords like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getKeywords()) + "%");
        }
        if (!Strings.isBlank(em.getDocType())) {
            hql += " and (summary.docType = ?)";
            objects.add(em.getDocType());
        }
        if (!Strings.isBlank(em.getSendType())) {
            hql += " and (summary.sendType = ?)";
            objects.add(em.getSendType());
        }
        if (!Strings.isBlank(em.getCreatePerson())) {
            hql += " and (summary.createPerson like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getCreatePerson()) + "%");
        }
        if (em.getCreateTimeB()!=null) {
            hql += " and (summary.createTime >= ?)";
            objects.add(Datetimes.getTodayFirstTime(em.getCreateTimeB()));
        }
        if (em.getCreateTimeE()!=null) {
            hql += " and (summary.createTime <= ?)";
            objects.add(Datetimes.getTodayLastTime(em.getCreateTimeE()));
        }
        if (!Strings.isBlank(em.getSendToId())) {
        	//主送
            hql += " and ((summary.sendToId like ? or summary.sendToId2 like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
            //抄送
            hql += " or (summary.copyToId like ? or summary.copyToId2 like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
            //抄报
            hql += " or (summary.reportToId like ? or summary.reportToId2 like ?))";
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
        }
        if (!Strings.isBlank(em.getSendUnit())) {
            hql += " and (summary.sendUnit like ? or summary.sendUnit2 like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getSendUnit()) + "%");
            objects.add("%" + SQLWildcardUtil.escape(em.getSendUnit()) + "%");
        }
        if (!Strings.isBlank(em.getIssuer())) {
            hql += " and (summary.issuer like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getIssuer()) + "%");
        }
        if (em.getSigningDateA()!=null) {
            hql += " and (summary.signingDate >= ?)";
            objects.add(Datetimes.getTodayFirstTime(em.getSigningDateA()));
        }
        if (em.getSigningDateB()!=null) {
            hql += " and (summary.signingDate <= ?)";
            objects.add(Datetimes.getTodayLastTime(em.getSigningDateB()));
        }
        
        String selectHql = hql + " order by summary.createTime desc";

        List result =new ArrayList();

        if(needByPage){ //需要分页
        	 result = edocSummaryDao.find(selectHql,"affair.id",true, null, objects); 
        }else { //不需要分页
        	 result = edocSummaryDao.find(selectHql,-1,-1,null,objects);
        }
        String summaryIds = "";
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
            	Object[] object = (Object[]) result.get(i);
                //Affair affair = new Affair();
                EdocSummary summary = new EdocSummary();
                make(object,summary);
                summary.setEdocType(em.getEdocType());
                
                try {
                    V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
                    summary.setStartMember(member);
                }
                catch (BusinessException e) {
                    log.error("", e);
                }

                //开始组装最后返回的结果
                EdocSummaryModel model = new EdocSummaryModel();

                model.setStartDate(new Date(summary.getCreateTime().getTime()));
                model.setWorkitemId(summary.getId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                //model.setAffairId(affair.getId());
                //summaryIds.add(summary.getId());
                Object archiveId=object[object.length-3];
                if(archiveId != null ){
	            	if("".equals(summaryIds)) summaryIds=String.valueOf(archiveId);
	    			else summaryIds+=","+String.valueOf(archiveId);
	            	model.getSummary().setArchiveId((Long)(archiveId));
                }
            	
                model.setAffairId((Long)object[object.length-1]);
                model.setBodyType((String)object[object.length-2]);

                
               
                /*
                //协同状
                Integer sub_state = affair.getState();                
                if (sub_state != null) {
                    model.setState(sub_state.intValue());
                }

                //是否跟踪
                Boolean isTrack = affair.getIsTrack();
                if (isTrack != null) {
                    model.setIsTrack(isTrack.booleanValue());
                }
                */
                String sendToUnit = "";
                if (!Strings.isBlank(summary.getSendTo())) 
                {
                	sendToUnit = summary.getSendTo();
				}
                if(!Strings.isBlank(sendToUnit)){
	            	if (!Strings.isBlank(summary.getCopyTo())) 
	            	{
	            		sendToUnit = sendToUnit + "、" + summary.getCopyTo();
					}
                }
                else
                {
                	sendToUnit = summary.getCopyTo();
                }
                if(!Strings.isBlank(sendToUnit)){
	            	if (!Strings.isBlank(summary.getReportTo())) 
	            	{
	            		sendToUnit = sendToUnit + "、" + summary.getReportTo();
					}
                }
                else
                {
                	sendToUnit = summary.getReportTo();
                }
                model.setSendToUnit(sendToUnit);
                
                models.add(model);
            }
        }
        //查询DocResouce,获取归档路径。
        List<DocResource> docs=docHierarchyManager.getDocsByIds(summaryIds);
        for(EdocSummaryModel model: models){
        	Long summaryId = model.getSummary().getId();
        	if(summaryId != null && model.getSummary().getHasArchive()){
	        	for(DocResource doc :docs){
	        		if(doc.getId().equals(model.getSummary().getArchiveId())){
	        			
	        			String frName=doc.getFrName();
	        			if (com.seeyon.v3x.doc.util.Constants.needI18n(doc.getFrType()));
	        				frName = com.seeyon.v3x.doc.util.Constants.getDocI18nValue(frName);
	        			
	        			if(doc.getLogicalPath()!=null && doc.getLogicalPath().split("\\.").length>1)
	        				frName=com.seeyon.v3x.edoc.util.Constants.Edoc_PAGE_SHOWPIGEONHOLE_SYMBOL+java.io.File.separator+frName;
	        			
	        			model.setArchiveName(frName);
	        			model.setLogicalPath((String)doc.getLogicalPath());
	        			break;
	        		}
	        	}
        	}
        }
        return models;	
    }
    public List<EdocSummaryModel> queryByCondition(long curUserId,EdocSearchModel em) {
    	List<EdocSummaryModel> models = new ArrayList<EdocSummaryModel>();        
        
        List<Object> objects = new ArrayList<Object>();
        
        String hql = "select distinct "+selectSummary+",affair.bodyType from EdocSummary as summary, Affair as affair"
        		+ " where (affair.objectId=summary.id) and (affair.memberId=?)"
        		//+ " and (affair.state=" + state.key() + ")"
        		+ " and (affair.state in ("+StateEnum.col_done.getKey()+","+StateEnum.col_pending.getKey()+","+StateEnum.col_sent.getKey()+") )"
        		+ " and (affair.app=?)"
                + " and affair.isDelete=false"
                //310sp2:不根据文件的密级限制查询权限。muj
                // + " and (summary.hasArchive=false or (summary.hasArchive=true and (summary.secretLevel='1' or summary.secretLevel='5' or summary.secretLevel is null)))"//不读取归档密级不是普通的
        		+ " and summary.state!=" + Constant.flowState.deleted.ordinal();//不读取公文归档后删除。		
        		//+ " and affair.archiveId is null";
        objects.add(curUserId);
        objects.add(EdocUtil.getAppCategoryByEdocType(em.getEdocType()).key());

        if (!Strings.isBlank(em.getSubject())) {        	
                hql += " and (summary.subject like ?)";
                objects.add("%" + SQLWildcardUtil.escape(em.getSubject()) + "%");
            }
        if (!Strings.isBlank(em.getDocMark())) {
                hql += " and (summary.docMark like ? or summary.docMark2 like ?)";
                objects.add("%" + SQLWildcardUtil.escape(em.getDocMark()) + "%");
                objects.add("%" + SQLWildcardUtil.escape(em.getDocMark()) + "%");
            }
        if (!Strings.isBlank(em.getSerialNo())) {
                hql += " and (summary.serialNo like ?)";
                objects.add("%" + SQLWildcardUtil.escape(em.getSerialNo()) + "%");
            }
        if (!Strings.isBlank(em.getKeywords())) {
            hql += " and (summary.keywords like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getKeywords()) + "%");
        }
        if (!Strings.isBlank(em.getDocType())) {
            hql += " and (summary.docType = ?)";
            objects.add(em.getDocType());
        }
        if (!Strings.isBlank(em.getSendType())) {
            hql += " and (summary.sendType = ?)";
            objects.add(em.getSendType());
        }
        if (!Strings.isBlank(em.getCreatePerson())) {
            hql += " and (summary.createPerson like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getCreatePerson()) + "%");
        }
        if (em.getCreateTimeB()!=null) {
            hql += " and (summary.createTime >= ?)";
            objects.add(Datetimes.getTodayFirstTime(em.getCreateTimeB()));
        }
        if (em.getCreateTimeE()!=null) {
            hql += " and (summary.createTime <= ?)";
            objects.add(Datetimes.getTodayLastTime(em.getCreateTimeE()));
        }
        if (!Strings.isBlank(em.getSendToId())) {
            hql += " and (summary.sendToId like ? or summary.sendToId2 like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
            objects.add("%" + SQLWildcardUtil.escape(em.getSendToId()) + "%");
        }
        if (!Strings.isBlank(em.getSendUnit())) {
            hql += " and (summary.sendUnit like ? or summary.sendUnit2 like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getSendUnit()) + "%");
            objects.add("%" + SQLWildcardUtil.escape(em.getSendUnit()) + "%");
        }
        if (!Strings.isBlank(em.getIssuer())) {
            hql += " and (summary.issuer like ?)";
            objects.add("%" + SQLWildcardUtil.escape(em.getIssuer()) + "%");
        }
        if (em.getSigningDateA()!=null) {
            hql += " and (summary.signingDate >= ?)";
            objects.add(Datetimes.getTodayFirstTime(em.getSigningDateA()));
        }
        if (em.getSigningDateB()!=null) {
            hql += " and (summary.signingDate <= ?)";
            objects.add(Datetimes.getTodayLastTime(em.getSigningDateB()));
        }
        
        String selectHql = hql + " order by summary.createTime desc";

        List result = edocSummaryDao.find(selectHql,"summary.id",true, null, objects);        
        
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
            	Object[] object = (Object[]) result.get(i);
                //Affair affair = new Affair();
                EdocSummary summary = new EdocSummary();
                make(object,summary);
                summary.setEdocType(em.getEdocType());
                
                try {
                    V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
                    summary.setStartMember(member);
                }
                catch (BusinessException e) {
                    log.error("", e);
                }

                //开始组装最后返回的结果
                EdocSummaryModel model = new EdocSummaryModel();

                model.setStartDate(new Date(summary.getCreateTime().getTime()));
                model.setWorkitemId(summary.getId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                //model.setAffairId(affair.getId());
                model.setBodyType((String)object[object.length-1]);

                /*
                //协同状
                Integer sub_state = affair.getState();                
                if (sub_state != null) {
                    model.setState(sub_state.intValue());
                }

                //是否跟踪
                Boolean isTrack = affair.getIsTrack();
                if (isTrack != null) {
                    model.setIsTrack(isTrack.booleanValue());
                }
                */
                models.add(model);
            }
        }

        return models;	
    }
    /**
     * 人员名称前加角色名称
     * @param roleaName
     * @return
     */
    private List<V3xOrgMember> addRoleName(String roleName,List<V3xOrgMember> ml)
    {
    	List<V3xOrgMember> rml=new ArrayList<V3xOrgMember>();
    	for(V3xOrgMember mem:ml)
    	{
    		V3xOrgMember tm=new V3xOrgMember();
    		tm.setId(mem.getId());
    		tm.setName(roleName+"　"+mem.getName());
    		tm.setOrgAccountId(mem.getOrgAccountId());
    		rml.add(tm);
    	}
    	return rml;
    }
    
    public List<MoreSignSelectPerson> findMoreSignPersons(String typeAndIds)
    {    	
        List <MoreSignSelectPerson>msps=new ArrayList<MoreSignSelectPerson>();
        try{
        List<V3xOrgEntity> ents= orgManager.getEntities(typeAndIds);
        List<MoreSignSelectPerson> ms=new ArrayList<MoreSignSelectPerson>();
        
        V3xOrgRole edocManagerRole=null;
        V3xOrgRole depManagerRole=null; 
        V3xOrgRole depAdminRole=null; 
        
        String edocManagerRoleName=ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources","sys.role.rolename."+EdocRoleHelper.departmentExchangeRoleName);
        String depManagerRoleName=ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources","sys.role.rolename."+V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER);
        String depAdminRoleName=ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources","sys.role.rolename."+V3xOrgEntity.ORGENT_META_KEY_DEPADMIN);
    	
        List<V3xOrgMember> memList=new ArrayList<V3xOrgMember>();
        for(V3xOrgEntity ent:ents)
        {
        	MoreSignSelectPerson msp=new MoreSignSelectPerson();
        	msp.setSelObj(ent);
        	List <V3xOrgMember> selPersons=new ArrayList<V3xOrgMember>();
        	if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(ent.getEntityType()))
        	{        		
        		selPersons.add((V3xOrgMember)ent);
        	}
        	else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(ent.getEntityType()))
        	{
        		edocManagerRole=orgManager.getRoleByName(EdocRoleHelper.departmentExchangeRoleName,ent.getOrgAccountId());                
                memList=orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, ent.getId(),edocManagerRole.getId());
                memList=addRoleName(edocManagerRoleName,memList);
                selPersons.addAll(memList);
                
                depManagerRole=orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER,ent.getOrgAccountId());
                memList=orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, ent.getId(),depManagerRole.getId());
                memList=addRoleName(depManagerRoleName,memList);
                selPersons.addAll(memList);
                
                depAdminRole=orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPADMIN,ent.getOrgAccountId());
                memList=orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, ent.getId(),depAdminRole.getId());
                memList=addRoleName(depAdminRoleName,memList);
                selPersons.addAll(memList);
        	}        	
        	msp.setSelPersons(selPersons);
        	msps.add(msp);
        }
        }catch(Exception e)
        {
        	log.error(e);
        }        
    	return msps;
    	
    }
    public void recoidChangeWords(String affairIds, String summaryIds,String changeType,String userId){
    	if(affairIds == null || "".equals(affairIds) 
    			|| summaryIds==null || "".equals(summaryIds))
    		return;
    	String[] affairId=affairIds.split(",");
    	String[] summaryId=summaryIds.split(",");
    	try{
	    	for(int i=0;i<affairId.length;i++){
	    		recoidChangeWord(affairId[i],summaryId[i],changeType,userId);
	    	}
    	}catch(Exception e){
    		log.error("记录修日志出错:",e) ;
    	}
    }

    public void recoidChangeWord(String affairId ,String summaryId,String changeType,String userId){    	
    	  if(affairId == null ||  affairId.equals("") || 
    			  summaryId == null || "".equals(summaryId)
    			  || changeType == null || changeType.equals("")){
    		  return ;   		  
    	  }
    	  try{
        	  User user = CurrentUser.get();
        	  if(user == null) {
        		  user = new User() ;
        		  user.setId(Long.valueOf(userId)) ;
        	  }
        	  Affair affair = affairManager.getById(Long.valueOf(affairId));
    		  BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(affair);//当前节点
    		  EdocSummary summary = edocSummaryDao.get(Long.valueOf(summaryId));
    		  String[] changeWords = null ;	 
    		  if(changeType.contains(",") ){
    			  changeWords =  changeType.split(",");
    		  }else{
    			  changeWords = new String[1] ;
    			  changeWords[0] = changeType ;
    		  }
    		  if(changeWords != null && changeWords.length > 0) {
    			  for(int i = 0 ; i < changeWords.length ; i++) {
    	    		  if(changeWords[i].equals("contentUpdate")){
    	    			  this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.modifyBody.getKey()));
    	    		  }else if(changeWords[i].equals("taohong")) {
    	    			  this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.Body.getKey()));
    	    		  }else if(changeWords[i].equals("qianzhang")){
    	    			  this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.signed.getKey()));
    	    		  }else if(changeWords[i].equals("taohongwendan")){
    	    			  this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.bodyFromRed.getKey()));
    	    		  }else if(changeWords[i] .equals("depPinghole")){
    	    			  this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.depHigeonhole.getKey()));
    	    		  }else if(changeWords[i] .equals("duban")) {
    	    	          this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.duban.getKey()));    	    			  
    	    		  }else if(changeWords[i] .equals("wendanqianp")){
    	    			  this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), Long.valueOf(bPMActivity.getId()), ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.wendanqianp.getKey()));
    	    		  }  				  
    			  }
    		  } 		     	      		  
    	  }catch(Exception e){
    		  log.error("记录修改正文时候出错:",e) ;
    	  }
    }
    
  //获取正文附件属性。
  	private List<Attachment> createAttachmentFromRequest(Long reference,Long subReference ,HttpServletRequest request)throws Exception{
  		//正文附件
  	  	String ContentAttFILEUPLOAD_INPUT_NAME_fileUrl = "content_attachment_fileUrl";
  	  	String ContentAttFILEUPLOAD_INPUT_NAME_mimeType = "content_attachment_mimeType";
  	  	String ContentAttFILEUPLOAD_INPUT_NAME_size = "content_attachment_size";
  	  	String ContentAttFILEUPLOAD_INPUT_NAME_createDate = "content_attachment_createDate";
  	  	String ContentAttFILEUPLOAD_INPUT_NAME_filename = "content_attachment_filename";
  	  	String ContentAttFILEUPLOAD_INPUT_NAME_type = "content_attachment_type";
  	  	String ContentAttFILEUPLOAD_INPUT_NAME_needClone = "content_attachment_needClone";
  	  	String ContentAttFILEUPLOAD_INPUT_NAME_description = "content_attachment_description";	
  		String[] fileUrl = request.getParameterValues(ContentAttFILEUPLOAD_INPUT_NAME_fileUrl);
  		String[] mimeType = request.getParameterValues(ContentAttFILEUPLOAD_INPUT_NAME_mimeType);
  		String[] size = request.getParameterValues(ContentAttFILEUPLOAD_INPUT_NAME_size);
  		String[] createdate = request.getParameterValues(ContentAttFILEUPLOAD_INPUT_NAME_createDate);
  		String[] filename = request.getParameterValues(ContentAttFILEUPLOAD_INPUT_NAME_filename);
  		String[] type = request.getParameterValues(ContentAttFILEUPLOAD_INPUT_NAME_type);
  		String[] needClone = request.getParameterValues(ContentAttFILEUPLOAD_INPUT_NAME_needClone);
  		String[] description = request.getParameterValues(ContentAttFILEUPLOAD_INPUT_NAME_description);
  		return this.attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.edoc,reference ,subReference, fileUrl, 
				mimeType, size, createdate, filename, type, needClone, description);
  	}
    
    public void updateAttachment(EdocSummary edocSummary,Affair cAffair,User user,HttpServletRequest request)throws Exception{
		//添加新的正文附件
		attachmentManager.create(ApplicationCategoryEnum.edoc,edocSummary.getId(),edocSummary.getId(),request);
		AttachmentEditHelper editHelper = new AttachmentEditHelper(request);
        if(editHelper.hasEditAtt()){//是否修改附件
        	saveUpdateAttInfo(editHelper.attSize(),edocSummary.getId(),editHelper.parseProcessLog(Long.valueOf(edocSummary.getProcessId()), -1l));
        }
	}
    
    public void saveUpdateAttInfo(int attSize,Long summaryId,List<ProcessLog> logs){
    	try {
			EdocSummary edocSummary = getEdocSummaryById(summaryId, false);
			boolean needUpdate = false;
			boolean hasAtt = attSize !=0;
			if(edocSummary.isHasAttachments() && !hasAtt){
				needUpdate = true;
			}else if(!edocSummary.isHasAttachments() && hasAtt){
				needUpdate = true;
			}
			if(needUpdate){
				edocSummary.setHasAttachments(hasAtt);
				
				Map<String,Object> p = new HashMap<String,Object>();
				p.put("identifier", edocSummary.getIdentifier());
				update(edocSummary.getId(),p);
				
				Affair affair = new Affair();
				affair.setHasAttachments(hasAtt);
				Map<String,Object> parameter = new HashMap<String,Object>();
				parameter.put("identifier", affair.getIdentifier());
				
				affairManager.updateAllAvailabilityAffair(EdocUtil.getAppCategoryByEdocType(edocSummary.getEdocType()), 
						edocSummary.getId(), parameter);
			}
			processLogManager.insertLog(logs);
			EdocMessageHelper.updateAttachmentMessage(affairManager, userMessageManager, orgManager, edocSummary);
		} catch (Exception e) {
			log.error("更新附件信息",e);
		}
    	
    }
    public  String getShowArchiveNameByArchiveId(Long archiveId){
    	//预归档
        String archiveName = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", "prep-pigeonhole.label.no");

        if(archiveId!= null){
        	String frName = docHierarchyManager.getNameById(archiveId);
        	DocResource doc  = docHierarchyManager.getDocResourceById(archiveId);
        	if(doc != null){
	        	if(doc.getLogicalPath().split("\\.").length>1){
	        		archiveName = com.seeyon.v3x.edoc.util.Constants.Edoc_PAGE_SHOWPIGEONHOLE_SYMBOL+"\\"+frName;
	        	}else{
	        		archiveName = frName;
	        	}
        	}
        }
        return archiveName;
    }
   /**
    * 得到预归档目录名： 根文件夹名/xxx/yyy/最好一个文件夹名
    * @param archiveId  归档路径ID
    * @return
    */
   public String getFullArchiveNameByArchiveId(Long archiveId){
	 //预归档
       String archiveName = "";
       if(archiveId!= null){
    	   DocResource dr=docHierarchyManager.getDocResourceById(archiveId);
    	   if(dr != null){
    		   archiveName = docHierarchyManager.getPhysicalPathDetail(dr.getLogicalPath(),"\\",false,0);
    	   }
       }
       return archiveName;
   }
   public String checkHasAclNodePolicyOperation(String affairIds,String operationName){
   	String ret="ok";
   	String notArchiveAclAffairIds = "";
   	if(affairIds!=null){
   		String[] affairId=affairIds.split(",");
   		for(int i=0;i<affairId.length;i++){
   			Long _affairId=Long.parseLong(affairId[i]);
   			Affair affair=affairManager.getById(_affairId);
	    		try{
	    			
	    			EdocSummary summary=getEdocSummaryById(affair.getObjectId(), false);
	    			MetadataNameEnum edocTypeEnum=EdocUtil.getEdocMetadataNameEnumByApp(affair.getApp());
	    			String nodePermissionPolicy = getPolicyByAffair(affair);
	    			
	    			V3xOrgMember sender = orgManager.getMemberById(summary.getStartUserId());
	    			Long flowPermAccountId = EdocHelper.getFlowPermAccountId(summary, sender.getOrgAccountId(), templeteManager);
	        		
	    			Map<String, List<String>> actions = permissionManager.getActionMap(edocTypeEnum.name(), nodePermissionPolicy, flowPermAccountId);
	    			
	    			List<String> advancedActions = actions.get("advanced");
	        		if(advancedActions!=null && advancedActions.contains(operationName)) continue;
	        		
	        		List<String> commonActions = actions.get("common");
	        		if(commonActions != null && commonActions.contains(operationName)) continue;
	        		
	        		List<String> basicActions = actions.get("basic");
	        		if(basicActions!=null && basicActions.contains(operationName)) continue;
	        		
	        		if("ok".equals(ret))	ret="《"+summary.getSubject()+"》";
	        		else ret+=",《"+summary.getSubject()+"》";
	        		
	        		if(Strings.isBlank(notArchiveAclAffairIds)){
	        			notArchiveAclAffairIds = String.valueOf(_affairId);
	        		}else{
	        			notArchiveAclAffairIds +=","+ String.valueOf(_affairId);
	        		}
	        	
   			}catch(Exception e){
   				log.error("ajax判断归档权限：",e);
   			}
       	}
   	}
   	if(!"ok".equals(ret)) ret += "&"+notArchiveAclAffairIds;
   	return ret;
   }

	public String checkIsCanBeRepealed(String summaryId4Check) {
		boolean isFinished = false;
		String result = "";
		StringBuffer info = new StringBuffer();
		try {
			EdocSummary summary = this.getEdocSummaryById(Long
					.parseLong(summaryId4Check), false);
			isFinished = summary.getFinished();
			if (isFinished) {
				info.append("《").append(summary.getSubject()).append("》");
				result = ResourceBundleUtil.getString(
						"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
						"edoc.state.end.alert", info.toString());
			} else {
				result = "Y";
			}
		} catch (EdocException e) {
			log.error("ajax判断是否流程结束异常：", e);
		}
		return result;
	}

	public void setProcessLogManager(ProcessLogManager processLogManager) {
		this.processLogManager = processLogManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	public EdocMarkManager getEdocMarkManager() {
		return edocMarkManager;
	}
	public void setEdocMarkManager(EdocMarkManager edocMarkManager) {
		this.edocMarkManager = edocMarkManager;
	}
	public EdocMarkHistoryManager getEdocMarkHistoryManager() {
		return edocMarkHistoryManager;
	}
	public void setEdocMarkHistoryManager(
			EdocMarkHistoryManager edocMarkHistoryManager) {
		this.edocMarkHistoryManager = edocMarkHistoryManager;
	}
	public ColSuperviseManager getColSuperviseManager() {
		return colSuperviseManager;
	}
	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}
	
	public IndexInfo getIndexInfo(long id) throws Exception {
		EdocSummary summary = null;
		try {
			summary = getColAllById(id);
		} catch (EdocException e) {
			log.error("EdocManagerImpl getIndexInfo getColAllById", e);
			throw new ColException("ColManagerImpl getIndexInfo getColAllById", e);
		}
		if (summary == null)
			return null;
		
		IndexInfo info = new IndexInfo();
		
		info.setEntityID(id);
		info.setAppType(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()));
		

		//在此取得权限！！
		AuthorizationInfo ai = new AuthorizationInfo();
		List list = affairManager.getOwnerList(
				ApplicationCategoryEnum.collaboration, id);
		ai.setOwner(list);
		info.setAuthorizationInfo(ai);
	
		V3xOrgMember member = null;
        try {
            member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
        }
        catch (BusinessException e) {
        	log.error("ColManagerImpl getIndexInfo getColAllById", e);
            throw new BusinessException("ColManagerImpl getIndexInfo getEntityById", e);
        }

		info.setStartMemberId(member.getId());
		info.setAuthor(member.getName());
		info.setTitle(summary.getSubject());
		java.util.Date date1 = new java.util.Date(summary.getCreateTime().getTime());
		info.setCreateDate(date1);
		Set opinions = summary.getEdocOpinions();
//		StringBuffer commentStr = null;
		StringBuffer opinionStr = null;

		if (opinions != null && opinions.size() > 0) {
			opinionStr = new StringBuffer();
			Iterator it1 = opinions.iterator();
			ResourceBundle r = null;
			while (it1.hasNext()) {
				EdocOpinion opin = (EdocOpinion) it1.next();
				if (opin.getContent() != null && opin.getIsHidden() == false){
					Long userId = opin.getCreateUserId();
					String userName= "*";
					  try {
						     member = orgManager.getMemberById(opin.getCreateUserId());
				            userName = member.getName();
					  }
			        catch (BusinessException e) {
			        	log.error("edoc getIndexInfo",e);
			        }
			        String attitude = "";//态度
			    	if (opin.getAttribute() > 0) {
						attitude = metadataManager.getMetadataItemLabel(
								MetadataNameEnum.collaboration_attitude, Integer
										.toString(opin.getAttribute()));
					}
					if (Strings.isNotBlank(attitude)) {
						if(CurrentUser.get()!=null){
							r = ResourceBundle
									.getBundle(
											"com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource",
											CurrentUser.get().getLocale());
							attitude = ResourceBundleUtil.getString(r, attitude);
						}
					} else if (Strings.isNotBlank(attitude)
							&& Integer.valueOf(attitude).intValue() == com.seeyon.v3x.edoc.util.Constants.EDOC_ATTITUDE_NULL) {
						attitude = "";
					}
			        //处理态度
					opinionStr.append(userName+" "+ attitude+" "+opin.getContent());
				}
			}
			info.setOpinion(opinionStr.toString());
		}
		EdocBody body = summary.getFirstBody();
		if ("HTML".equals(body.getContentType())) {
			info.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
			info.setContent(Strings.isBlank(body.getContent())?"":body.getContent());
		} else {
			
			if("OfficeWord".equals(body.getContentType()))
				info.setContentType(IndexInfo.CONTENTTYPE_WORD);
			else if("OfficeExcel".equals(body.getContentType()))
				info.setContentType(IndexInfo.CONTENTTYPE_XLS);
			else if("WpsWord".equals(body.getContentType()))
				info.setContentType(IndexInfo.CONTENTTYPE_WPS_Word);
			else if("WpsExcel".equals(body.getContentType()))
				info.setContentType(IndexInfo.CONTENTTYPE_WPS_EXCEL);
			else if("Pdf".equals(body.getContentType()))
				info.setContentType(IndexInfo.CONTENTTYPE_PDF);
			
			Long fileId = Long.parseLong(body.getContent());
			Date date = new Date(body.getCreateTime().getTime());
			info.setContentID(fileId);
			info.setContentCreateDate(date);
			Partition partition = partitionManager.getPartition(date, true);
			info.setContentAreaId(partition.getId().toString());
			String contentPath = this.fileManager.getFolder(body.getCreateTime(), false);
			info.setContentPath(contentPath.substring(contentPath.length()-11)+System.getProperty("file.separator"));
		}
		
		//在此处理附件
		IndexUtil.convertToAccessory(info);
		//}
		info.setHasAttachment(summary.isHasAttachments());
		info.setImportantLevel(summary.getImportantLevel());
		int t =  IndexInfo.FieldIndex_Type.IndexTOKENIZED.ordinal();
		
        StringBuilder  keyword=new StringBuilder();
        keyword.append(summary.getSecretLevel()==null?"":summary.getSecretLevel()+" ");
        keyword.append(String.valueOf(summary.getEdocType())+" ");
        keyword.append(StringUtils.isBlank(summary.getDocMark())?"":summary.getDocMark()+" ");
        keyword.append(StringUtils.isBlank(summary.getSerialNo())?"":summary.getSerialNo()+" ");
        keyword.append(StringUtils.isBlank(summary.getKeywords())?"":summary.getKeywords()+" ");
        keyword.append(StringUtils.isBlank(summary.getSendUnit())?"":summary.getSendUnit()+" ");
        keyword.append(StringUtils.isBlank(summary.getIssuer())?"":summary.getIssuer()+" ");
        keyword.append(StringUtils.isBlank(summary.getSendTo())?"":summary.getSendTo()+" ");
        keyword.append(StringUtils.isBlank(summary.getCopyTo())?"":summary.getCopyTo()+" ");
        keyword.append(StringUtils.isBlank(summary.getReportTo())?"":summary.getReportTo()+" ");
        keyword.append(StringUtils.isBlank(summary.getSendType())?"":summary.getSendType()+" ");
        
		info.addExtendProperties(IndexInfo.SECRETLEVEL,summary.getSecretLevel(),t);
		info.addExtendProperties(IndexInfo.EDOCTYPE,String.valueOf(summary.getEdocType()),t);
		info.addExtendProperties(IndexInfo.SENDTYPE,summary.getSendType(),t);
		info.addExtendProperties(IndexInfo.DOCMARK,summary.getDocMark(),t);
		info.addExtendProperties(IndexInfo.SERIALNO,summary.getSerialNo(),t);
		info.addExtendProperties(IndexInfo.EDOCKEYWORD,summary.getKeywords(),t);
		info.addExtendProperties(IndexInfo.SENDUNIT,summary.getSendUnit(),t);
		info.addExtendProperties(IndexInfo.ISSUER,summary.getIssuer(),t);
		info.addExtendProperties(IndexInfo.SENDTO,summary.getSendTo(),t);
		info.addExtendProperties(IndexInfo.COPYTO,summary.getCopyTo(),t);
		info.addExtendProperties(IndexInfo.REPORTTO,summary.getReportTo(),t);
		if(summary.getSigningDate()!=null)
			info.addExtendProperties("signingDate", String.valueOf(summary.getSigningDate()), t);
		
		//组合自定义元素意见
		StringBuilder edocCustomInfo = new StringBuilder();
		try{
			Method[] m = summary.getClass().getDeclaredMethods();
			for(Method method : m){
				if(method.getName().indexOf("getString")!=-1 ||
						method.getName().indexOf("getText")!=-1||
						method.getName().indexOf("getInteger")!=-1||
						method.getName().indexOf("getDecimal")!=-1||
						method.getName().indexOf("getDate")!=-1||
						method.getName().indexOf("getList")!=-1){
				
					Object o= method.invoke(summary);
					if(o==null){continue;}
					String tem=String.valueOf(o);
					if(StringUtils.isNotBlank(tem))
					{
						edocCustomInfo.append(tem+" ");
					}
				}
			}
			info.addExtendProperties("edocCustomInfo", edocCustomInfo.toString(), t);
			
		}catch(Exception e){
			log.error("edoc getIndexinfo 获取自定义公文元素值反射异常",e);
		}
		//签发日期，SendType,edocCustomInfo ,关联文档名称
		//info.addExtendProperties(IndexInfo.SENDTYPE, value, fieldIndexType)
		info.setKeyword(keyword.append(" "+edocCustomInfo.toString()).toString());
		return info;
	}
	public List<ColTrackMember> getColTrackMembersByObjectIdAndTrackMemberId(Long objectId,Long trackMemberId){
		return colTrackMemberDao.getColTrackMembersByObjectIdAndTrackMemberId(objectId, trackMemberId);
	}

	public void deleteColTrackMembersByObjectId(Long objectId) {
		colTrackMemberDao.deleteColTrackMembersByObjectId(objectId);
	}

	private List<EdocOpinion> getEdocOpinionObjectList(EdocSummary summary,boolean isOnlyShowLastOpinion) {		
		// 取得公文单的意见元素的绑定关系，key是FlowPermName，value是公文元素名称为value_公文元素的排序方式
		long flowPermAccout = EdocHelper.getFlowPermAccountId(summary.getOrgAccountId(), summary, templeteManager);
		Hashtable<String, String> locHs = edocFormManager.getOpinionLocation(summary.getFormId(),flowPermAccout);
		
		List <Attachment> tempAtts=attachmentManager.getByReference(summary.getId());
		Hashtable <Long,List<Attachment>> attHas=com.seeyon.v3x.common.filemanager.manager.Util.sortBySubreference(tempAtts);
		
		
		List<Object[]> tempResult = new ArrayList<Object[]>();   //查询出来的意见
		List<String> 	boundFlowPerm =new ArrayList<String>();   //绑定的节点权限
		//Map<意见元素名称，List<绑定的节点权限>>因为一个意见元素可以绑定多个节点权限
		Map<String,List<String>> map = new HashMap<String,List<String>>();  
		Map<String,String> sortMap = new HashMap<String,String>();
		//绑定部分的意见
		for (Iterator keyName = locHs.keySet().iterator(); keyName.hasNext();) {
			String flowPermName = (String) keyName.next();
			if(!boundFlowPerm.contains(flowPermName))boundFlowPerm.add(flowPermName);
			// tempLoacl格式 公文元素名称为value_公文元素的排序方式
			String tempLocal = locHs.get(flowPermName);
			String elementOpinion = tempLocal.split("_")[0];//公文元素名,例如公文单上的shenpi这个公文元素
			//取到指定公文元素绑定的节点权限列表
			List<String> flowPermsOfSpecialElement = map.get(elementOpinion);
			if(flowPermsOfSpecialElement == null){
					flowPermsOfSpecialElement = new ArrayList<String>();
			}
			flowPermsOfSpecialElement.add(flowPermName);
			map.put(elementOpinion,flowPermsOfSpecialElement);
			
			// 公文元素绑定的排序方式
			String sortType = tempLocal.split("_")[1];
			sortMap.put(elementOpinion,sortType);
		}
		
		Set<String> bound = map.keySet(); //绑定的公文元素
		for(String s:bound){
			tempResult.addAll( edocOpinionDao.findLastSortOpinionBySummaryIdAndPolicy(summary.getId(),
					map.get(s), sortMap.get(s), isOnlyShowLastOpinion,true));
		}
		//查询非绑定意见
		tempResult.addAll(edocOpinionDao.findLastSortOpinionBySummaryIdAndPolicy(summary.getId(),
				boundFlowPerm, "0", false,false));
		
		List<EdocOpinion> edocOpionionList = new ArrayList<EdocOpinion>();
		//查询没有分配的人员的意见和职务级别为null的人员的意见
		tempResult.addAll(edocOpinionDao.findOtherOpinionBySummaryId(summary.getId()));
		for(Object[] object: tempResult){
			EdocOpinion edocOpinion = (EdocOpinion)object[0];
			String deptName = (String)object[1];
			edocOpinion.setDeptName(deptName);
			edocOpionionList.add(edocOpinion);
			
			edocOpinion.setOpinionAttachments(attHas.get(edocOpinion.getId()));
		}
		return edocOpionionList;
}
    public Map<String,EdocOpinionModel> getEdocOpinion(EdocSummary summary){
    	//公文处理意见回显到公文单,排序    
        long flowPermAccout = EdocHelper.getFlowPermAccountId(summary.getOrgAccountId(), summary, templeteManager);
    	EdocOpinionDisplayConfig displayConfig = edocFormManager.getEdocOpinionDisplayConfig(summary.getFormId(),flowPermAccout);
    	
    	return getEdocOpinion(summary,displayConfig.isOnlyShowLastOpinion());
    }
    public Map<String,EdocOpinionModel> getEdocOpinion(EdocSummary summary,boolean isOnlyShowLastOpinion){
    	List<EdocOpinion> list = getEdocOpinionObjectList(summary,isOnlyShowLastOpinion);
    	
    	long flowPermAccout = EdocHelper.getFlowPermAccountId(summary.getOrgAccountId(), summary, templeteManager);
    	Hashtable<String, String> opinionLocation = edocFormManager.getOpinionLocation(summary.getFormId(),flowPermAccout);
    	
    	return getEdocOpinion(list,summary,opinionLocation);
    }	
    public  Map<String, EdocOpinionModel> getEdocOpinion(List<EdocOpinion> edocOpinions,
		EdocSummary summary,Map<String,String> opinionLocation){
		
    	Long summaryId = summary.getId();
		
		Map<String, EdocOpinionModel> map = new HashMap<String, EdocOpinionModel>();
		Map<String,V3xHtmDocumentSignature> signMap = getEdocOpinionSignatureMap(summaryId);
		//拟文或者登记意见是否被绑定到意见框中显示了。
		for(EdocOpinion edocOpinion : edocOpinions){
			//节点权限
			String policy = edocOpinion.getPolicy();
			if (policy == null) {
				policy = summary.getEdocType() == EdocEnum.edocType.recEdoc.ordinal() ? "dengji" : "niwen";
			}
			//公文元素_排序方式
			String location = opinionLocation.get(policy);
			
			//没有设置意见放置位置的，统一放到其它意见，不再按节点权限放置到前台匹配;发起人附言单独处理,如果没有设置绑定就不放入公文单
			if(Strings.isBlank(location)){
				if(policy.equals("niwen") || policy.equals("dengji")
						|| edocOpinion.getOpinionType() == EdocOpinion.OpinionType.senderOpinion.ordinal())	{
					//发起人附言
					location = "senderOpinion";
				}else{
					//其他意见。
					location="otherOpinion";
				}
			}else{
				location = location.split("[_]")[0];
			}
			EdocOpinionModel model = map.get(location);
			if(model == null){
				model = new EdocOpinionModel();
			}
			if(model.getOpinions() == null){
				model.setOpinions(new ArrayList<EdocOpinion>()); 
			}
			model.getOpinions().add(edocOpinion);
			
			V3xHtmDocumentSignature v3xHtmDocumentSignature = signMap.get(location);
			List<V3xHtmDocumentSignature> signList = new ArrayList<V3xHtmDocumentSignature>();
			signList.add(v3xHtmDocumentSignature);
			
			model.setV3xHtmDocumentSignature(signList);
			
			map.put(location, model);
		}
		return map;
	}
	private Map<String ,V3xHtmDocumentSignature> getEdocOpinionSignatureMap(Long summaryId) {
		//查找印章数据
		List <V3xHtmDocumentSignature>ls = htmlSignDao.findBy("summaryId",summaryId);
		
		Map<String ,V3xHtmDocumentSignature> signMap = new HashMap <String ,V3xHtmDocumentSignature>();
		for(V3xHtmDocumentSignature htmlSign : ls){
			String fieldName = htmlSign.getFieldName();
			if(Strings.isNotBlank(fieldName)){
				String[] name =fieldName.split("hw");
				if(name.length>1){
					signMap.put(name[1],htmlSign);
				}
			}
		}
		return signMap;
	}

	@Override
	public EdocBody getEdocBodyByFileid(long fileid) {
		// TODO Auto-generated method stub
		DetachedCriteria criteria = DetachedCriteria.forClass(EdocBody.class);
        criteria.add(Restrictions.like("content", fileid+""));
        return (EdocBody)edocOpinionDao.executeUniqueCriteria(criteria);
	}

	@Override
	public List<EdocSummaryModel> queryPendingByUserAndApp(
			String userid,
			int appType, 
			int stateType,
			boolean isImportant,
			boolean isCommon, 
			String condition,
			String field, 
			String field1) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer("select ");
		hql.append(selectAffair);
		condition = StringUtils.defaultString(condition);
		if(!"startMemberName".equals(condition)){
			hql.append(" from Affair as affair,EdocSummary as summary ");
			hql.append(" where affair.objectId=summary.id ");
		}else{
			hql.append(" from Affair as affair,EdocSummary as summary,").append(V3xOrgMember.class.getName()).append(" as mem");
			hql.append(" where affair.objectId=summary.id and affair.senderId=mem.id ");
		}
		hql.append(" and affair.memberId=").append(userid);
		hql.append(" and affair.state=").append(stateType);
		hql.append(" and ( affair.app=").append(ApplicationCategoryEnum.edocSend.key());
		hql.append(" or affair.app=").append(ApplicationCategoryEnum.edocRec.key());
		hql.append(" or affair.app=").append(ApplicationCategoryEnum.edocSign.key());
		hql.append(" or affair.app=").append(ApplicationCategoryEnum.exSend.key());
		hql.append(" or affair.app=").append(ApplicationCategoryEnum.exSign.key());
		hql.append(" or affair.app=").append(ApplicationCategoryEnum.edocRegister.key());
		hql.append(" )");
		hql.append(" and affair.isDelete=false ");
		if (isImportant) {//重要
			hql.append(" and ((summary.importantLevel=2) or (summary.importantLevel=3))");
		} else if(isCommon){//普通
			hql.append(" and summary.importantLevel=1 ");
		}
		if (condition.equals("subject")) {//标题
			hql.append(" and summary.subject like :subject ");
			parameterMap.put("subject", "%" + SQLWildcardUtil.escape(field) + "%");
		}else if (condition.equals("createDate")) {//发起时间
			if (StringUtils.isNotBlank(field)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(field);
				hql.append(" and affair.createDate >= :timestamp1");
				parameterMap.put("timestamp1", stamp);
			}
			if (StringUtils.isNotBlank(field1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(field1);
				hql.append(" and affair.createDate <= :timestamp2");
				parameterMap.put("timestamp2", stamp);
			}
		}else if(condition.equals("startMemberName")){//发起人
			hql.append(" and mem.name like :startMemberName");
			parameterMap.put("startMemberName", "%" + SQLWildcardUtil.escape(field) + "%");
		}
		hql.append("  order by affair.receiveTime desc ");
		List result = edocSummaryDao.find(hql.toString(), parameterMap);
		List<EdocSummaryModel> models = new ArrayList<EdocSummaryModel>(result.size());
        for (int i = 0; i < result.size(); i++) {
            Object[] object = (Object[]) result.get(i);
            Affair affair = new Affair();
            EdocSummary summary = new EdocSummary();
            make(object,summary,affair);
            try {
                V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
                summary.setStartMember(member);
            }catch (BusinessException e) {
                log.error("", e);
            }
            //开始组装最后返回的结果
            EdocSummaryModel model = new EdocSummaryModel();
            model.setWorkitemId(affair.getObjectId() + "");
            model.setCaseId(summary.getCaseId() + "");
            model.setSummary(summary);
            model.setAffairId(affair.getId());                
            model.setEdocType(EdocSummaryModel.EDOCTYPE.Pending.name());            
            model.setFinshed(summary.getCompleteTime()!= null);
            model.setBodyType(affair.getBodyType());
            //公文状态
            Integer sub_state = affair.getSubState();
            if (sub_state != null) {
                model.setState(sub_state.intValue());
            }
            //是否跟踪
            Boolean isTrack = affair.getIsTrack();
            if (isTrack != null) {
                model.setIsTrack(isTrack.booleanValue());
            }
            //催办次数
            Integer hastenTimes = affair.getHastenTimes();
            if (hastenTimes != null) {
                model.setHastenTimes(hastenTimes);
            }
            //检查是否有附件
            model.setHasAttachments(affair.isHasAttachments());
            //是否超期
            Boolean overtopTime = affair.getIsOvertopTime();
            if(overtopTime != null){
            	model.setOvertopTime(overtopTime.booleanValue());
            }
            //提前提醒
            Long advanceRemind = affair.getRemindDate();
            if(advanceRemind == null){
            	advanceRemind = 0L;
            }
            model.setAdvanceRemindTime(advanceRemind);
            //协同处理期限
            Long deadLine = affair.getDeadlineDate();
            if(deadLine == null){
            	deadLine = 0L;
            }
            model.setDeadLine(deadLine);
			model.setNodePolicy(affair.getNodePolicy());
			if(affair.getCompleteTime() != null){
				model.setDealTime(new Date(affair.getCompleteTime().getTime()));
			}
            models.add(model);
        }
		return models;
	}

	@Override
	public Map<Integer,Integer> queryPendingCountByGroup(String userid, int stateType) {
		Map<Integer,Integer> map= new HashMap<Integer,Integer>();
		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		hql.append(" app,count(affair.id) ");
		hql.append(" from Affair as affair ");
		hql.append(" where affair.memberId=").append(userid);
		hql.append(" and affair.state=").append(stateType);
		hql.append(" and affair.isDelete=false ");
		hql.append(" group by app ");
		List result = edocSummaryDao.find(hql.toString(), null);
		if(null!=result){
			for (Iterator iterator = result.iterator(); iterator.hasNext();) {
				Object[] object = (Object[]) iterator.next();
				Object appType= object[0];
				Object appTypeNum= object[1];
				map.put((Integer)appType, (Integer)appTypeNum);
			}
		}
		return map;
	}
	public String queryMarkList(int edocType, int state) {
		List<EdocSummaryModel> markList = this.queryByCondition(edocType, "", null, null, state);
		if (markList != null && markList.size() > 0) {
			List<String> list = this.getFormatEdocMark(markList);
			return list.get(0) + "::" + list.get(1) ;
		} 
		return "";
	}
	
	/**
     * 按指定格式得到文号，如：
     * [ { value:"010", label:"Beijing北京"}, { value:"020", label:"guangzhou广州" }, { value:"021",label:"shanghai上海"} ];
     * @param list
     * @return
     */
	private List<String> getFormatEdocMark(List<EdocSummaryModel> list) {
		
    	List<String> resultList = new ArrayList<String>();

    	// 公文文号
        String edocMark = "" ;
        if (list != null && list.size() > 0) {
        	for (int i  = 0 ; i < list.size() ; i ++) {
            	EdocSummaryModel model = list.get(i) ;
            	if (Strings.isNotBlank(model.getSummary().getDocMark()))
            		edocMark += "{value:'"+i+"',label:'"+model.getSummary().getDocMark()+"'}";
            	if ((i+1) < list.size() && Strings.isNotBlank(model.getSummary().getDocMark())) 
            		edocMark += ",";
            }
            if (edocMark.length() > 1 && ",".equals(edocMark.substring(edocMark.length()-1, edocMark.length())))
            	resultList.add("["+edocMark.substring(0, edocMark.length()-1)+"]");
            else
            	resultList.add("["+edocMark+"]");
            
            // 内部文号
            String edocInMark = "" ;
            for (int i  = 0 ; i < list.size() ; i ++) {
            	EdocSummaryModel model = list.get(i) ;
            	if (Strings.isNotBlank(model.getSummary().getSerialNo()))
            		edocInMark += "{value:'"+i+"',label:'"+model.getSummary().getSerialNo()+"'}";
            	if ((i+1) < list.size() && Strings.isNotBlank(model.getSummary().getSerialNo())) 
            		edocInMark += ",";
            }
            if (edocInMark.length() >1 && ",".equals(edocInMark.substring(edocInMark.length()-1, edocInMark.length())))
            	resultList.add("["+edocInMark.substring(0, edocInMark.length()-1)+"]");
            else
            	resultList.add("["+edocInMark+"]");
        }
    	return resultList ;
    }
	
	/**
     * 跟踪相关设置
     * @param affairId : affairiD
     * @param isTrack :是否设置了跟踪
     * @param trackMembers ：部门跟踪人员的ID串
     */
    public void setTrack(Long affairId,boolean isTrack,String trackMembers){
    	 List<Long> members = new ArrayList<Long>();
         if(Strings.isNotBlank(trackMembers)){
         	String[] m = trackMembers.split(",");
         	for(String s : m){
         		members .add(Long.valueOf(s));
         	}
         }
         colManager.changeTrack(affairId, isTrack,members);
    }

	/**
	 * @param colManager the colManager to set
	 */
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}
}