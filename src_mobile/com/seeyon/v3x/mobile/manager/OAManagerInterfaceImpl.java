package com.seeyon.v3x.mobile.manager;

import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_TEAM;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMActor;
import net.joinwork.bpm.definition.BPMParticipant;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.definition.BPMStatus;
import net.joinwork.bpm.definition.BPMTransition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputValueAll;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.form.inf.ISeeyonForm.TFieldInputType;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.cap.meeting.domain.MtMeetingCAP;
import com.seeyon.cap.meeting.domain.MtReplyCAP;
import com.seeyon.cap.meeting.manager.MtMeetingManagerCAP;
import com.seeyon.cap.meeting.manager.MtReplyManagerCAP;
import com.seeyon.v3x.addressbook.domain.AddressBookMember;
import com.seeyon.v3x.addressbook.domain.AddressBookTeam;
import com.seeyon.v3x.addressbook.manager.AddressBookManager;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.agent.manager.AgentAppUtil;
import com.seeyon.v3x.bulletin.domain.BulBody;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.util.hql.BulletinHqlUtils;
import com.seeyon.v3x.bulletin.util.hql.SearchInfo;
import com.seeyon.v3x.calendar.constants.CalEventComparator;
import com.seeyon.v3x.calendar.domain.CalContent;
import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.manager.CalContentManager;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColComment;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.ColOpinion.OpinionType;
import com.seeyon.v3x.collaboration.event.CollaborationStartEvent;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.manager.impl.ColLock;
import com.seeyon.v3x.collaboration.manager.impl.FormLockManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.office.HtmlHandWriteManager;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.parser.StrExtractor;
import com.seeyon.v3x.common.parser.UnknowBodyTypeException;
import com.seeyon.v3x.common.permission.domain.Permission;
import com.seeyon.v3x.common.permission.manager.PermissionManager;
import com.seeyon.v3x.common.permission.util.NodePolicy;
import com.seeyon.v3x.common.processlog.ProcessLogAction;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.MessageState;
import com.seeyon.v3x.common.usermessage.MessageUtil;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.usermessage.dao.UserMessageDAO;
import com.seeyon.v3x.common.usermessage.domain.UserHistoryMessage;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.edoc.domain.EdocBody;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocFormElement;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.manager.EdocElementManager;
import com.seeyon.v3x.edoc.manager.EdocFormManager;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.edoc.webmodel.EdocSummaryModel;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.flowperm.domain.FlowPerm;
import com.seeyon.v3x.flowperm.manager.FlowPermManager;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.datamodel.SearchResult;
import com.seeyon.v3x.index.share.datamodel.SearchResultWapper;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexInitConfig;
import com.seeyon.v3x.indexInterface.domain.UrlLinkDAO;
import com.seeyon.v3x.indexInterface.util.IndexSearchHelper;
import com.seeyon.v3x.mobile.MobileException;
import com.seeyon.v3x.mobile.utils.MobileConstants;
import com.seeyon.v3x.mobile.webmodel.AffairsListObject;
import com.seeyon.v3x.mobile.webmodel.Bulletion;
import com.seeyon.v3x.mobile.webmodel.Calendar;
import com.seeyon.v3x.mobile.webmodel.Collaboration;
import com.seeyon.v3x.mobile.webmodel.Edoc;
import com.seeyon.v3x.mobile.webmodel.EdocItem;
import com.seeyon.v3x.mobile.webmodel.MeetingDetial;
import com.seeyon.v3x.mobile.webmodel.MobileBookEntity;
import com.seeyon.v3x.mobile.webmodel.MobileForm;
import com.seeyon.v3x.mobile.webmodel.MobileHistoryMessage;
import com.seeyon.v3x.mobile.webmodel.MobileOrgEntity;
import com.seeyon.v3x.mobile.webmodel.MobileReply;
import com.seeyon.v3x.mobile.webmodel.News;
import com.seeyon.v3x.mobile.webmodel.Nodes;
import com.seeyon.v3x.mobile.webmodel.ProcessModeSelector;
import com.seeyon.v3x.news.domain.NewsBody;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.organization.domain.MemberHelper;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.system.signet.domain.V3xHtmDocumentSignature;
import com.seeyon.v3x.util.IdentifierUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.workflow.event.WorkflowEventListener;
import com.seeyon.v3x.workflow.event.WorkflowEventListener.NodeAddition;
/**
 * 
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-6-1
 */ 
public class OAManagerInterfaceImpl extends BaseHibernateDao implements
		OAManagerInterface {

	private static final Log log = LogFactory
			.getLog(OAManagerInterfaceImpl.class);

	public static enum SEARCH_CONDITIION {
		title, subject
	};
	TempleteManager templeteManager;
	private UserMessageDAO userMsgDao;
	private OrgManager orgManager;

	private AffairManager affairManager;

	private ColManager colManager;

	private AttachmentManager attachmentManager;
	
	private ProcessLogManager processLogManager;
	
	private FileManager fileManager;

	private PeopleRelateManager peoplerelateManager;

	private MtMeetingManagerCAP mtMeetingManagerCAP;

	private MtReplyManagerCAP mtReplyManagerCAP;

	private OnLineManager onLineManager;

	private IndexManager indexManager;

	private BulDataManager bulDataManager;

	private NewsDataManager newsDataManager;

	private CalEventManager calEventManager;

	private CalContentManager calContentManager;

	private PermissionManager permissionManager;

	private UrlLinkDAO urlLinkDAO;
	
	private PeopleRelateManager peopleRelateManager;
	
	private EdocFormManager edocFormManager;
	
	private Map<String,Boolean> containSubForm = new HashMap<String,Boolean>();//是否包含子表
	
	private EdocSummaryManager edocSummaryManager;
	private EdocElementManager edocElementManager;
	private MetadataManager metadataManager;
	private EdocManager edocManager;
	private Map<String,Boolean> containMark = new HashMap<String,Boolean>();//是否包含签章
	private OperationlogManager operationlogManager;
	private FlowPermManager flowPermManager;
	private HtmlHandWriteManager htmlHandWriteManager;
	
	private AddressBookManager addressBookManager;
	
    private UserMessageManager userMessageManager;
	
	private Map<Long,Map<String,Boolean>> cancelPurview = new HashMap<Long,Map<String,Boolean>>();//表单是否正在本某人处理

	
	public FlowPermManager getFlowPermManager() {
		return flowPermManager;
	}

	public void setFlowPermManager(FlowPermManager flowPermManager) {
		this.flowPermManager = flowPermManager;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public EdocFormManager getEdocFormManager() {
		return edocFormManager;
	}

	public void setEdocFormManager(EdocFormManager edocFormManager) {
		this.edocFormManager = edocFormManager;
	}

	public void setPeopleRelateManager(PeopleRelateManager peopleRelateManager) {
		this.peopleRelateManager = peopleRelateManager;
	}

	public Map<Long, Map<String,Boolean>> getCancelPurview() {
		return cancelPurview;
	}

	public void setCancelPurview(Map<Long, Map<String,Boolean>> cancelPurview) {
		this.cancelPurview = cancelPurview;
	}

	public Map<String, Boolean> getContainMark() {
		Map<String, Boolean> result = containMarkThread.get();
		containMarkThread.remove();
		return result;
	}

	public void setContainMark(Map<String, Boolean> containMark) {
		containMarkThread.set(containMark);
	}
	private static ThreadLocal<Map<String, Boolean>> containMarkThread = new ThreadLocal<Map<String, Boolean>> ();
	private static ThreadLocal<Map<String, Boolean>> subtainFormThread = new ThreadLocal<Map<String, Boolean>> ();
	
	public Map<String, Boolean> getContainSubForm() {
		Map<String, Boolean> result = subtainFormThread.get();
		subtainFormThread.remove();
		return result;
	}

	public void setContainSubForm(Map<String, Boolean> containSubForm) {
		subtainFormThread.set(containSubForm);
	}

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public void setMtMeetingManagerCAP(MtMeetingManagerCAP mtMeetingManagerCAP) {
		this.mtMeetingManagerCAP = mtMeetingManagerCAP;
	}

	public void setMtReplyManagerCAP(MtReplyManagerCAP mtReplyManagerCAP) {
		this.mtReplyManagerCAP = mtReplyManagerCAP;
	}

	public void setOnLineManager(OnLineManager onLineManager) {
		this.onLineManager = onLineManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public void setPeoplerelateManager(PeopleRelateManager peoplerelateManager) {
		this.peoplerelateManager = peoplerelateManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public void setCalEventManager(CalEventManager calEventManager) {
		this.calEventManager = calEventManager;
	}

	public void setCalContentManager(CalContentManager calContentManager) {
		this.calContentManager = calContentManager;
	}

	public void setUrlLinkDAO(UrlLinkDAO urlLinkDAO) {
		this.urlLinkDAO = urlLinkDAO;
	}

	public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
        this.userMessageManager = userMessageManager;
    }
	
	public AddressBookManager getAddressBookManager() {
		return addressBookManager;
	}

	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}

	public HtmlHandWriteManager getHtmlHandWriteManager() {
		return htmlHandWriteManager;
	}

	public void setHtmlHandWriteManager(HtmlHandWriteManager htmlHandWriteManager) {
		this.htmlHandWriteManager = htmlHandWriteManager;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Integer> getCollaborationNumWithType(Long uid) {
		User user = CurrentUser.get();
		Long memberId = user.getId();
		uid = memberId;
		List<Long> proxyIdList = MemberAgentBean.getInstance().getAgentToMemberId(ApplicationCategoryEnum.collaboration.key(), user.getId());
		Long proxyId = null;
		boolean hasProxy = proxyIdList != null && !proxyIdList.isEmpty(); //我是否设置了代理
    	java.util.Date agentStamp = null;
    	// 我有被代理人,要去取他的数据,条件有: affair.createDate在他设置代理时间点之后
    	if (hasProxy) {
    		try {
    			//FIXME 可以代理多个人的事项，时间点存在问题
    			proxyId = proxyIdList.get(0);
    			agentStamp = orgManager.getMemberById(proxyId).getAgentTime();// 从agentToMember里面取设置代理时间点
    		}
    		catch (Exception e1) {
    			log.warn("", e1);
    		}
    	}
    	
    	boolean isProxy = proxyIdList != null && !proxyIdList.isEmpty() && agentStamp != null; //我有被代理人

		Map<String, Integer> map = new HashMap<String, Integer>();

		//待发
		DetachedCriteria criteria = DetachedCriteria.forClass(Affair.class)
			.setProjection(Projections.count("id"))
			.add(Expression.eq("memberId", uid))
			.add(Expression.eq("state", StateEnum.col_waitSend.key()))
			.add(Expression.eq("app", ApplicationCategoryEnum.collaboration.key())) //协同
			.add(Expression.ne("isDelete", true)) // 没有删除
		;

		Integer result = (Integer)super.executeUniqueCriteria(criteria);
		map.put(String.valueOf(StateEnum.col_waitSend.key()), result == null ? 0 : result);
		
		//已发
		criteria = DetachedCriteria.forClass(Affair.class)
			.setProjection(Projections.count("id"))
			.add(Expression.eq("memberId", uid))
			.add(Expression.eq("state", StateEnum.col_sent.key()))
			.add(Expression.eq("app", ApplicationCategoryEnum.collaboration.key())) //协同
			.add(Expression.ne("isDelete", true)) // 没有删除
			.add(Expression.isNull("archiveId")) // 没有归档
		;

		result = (Integer)super.executeUniqueCriteria(criteria);
		map.put(String.valueOf(StateEnum.col_sent.key()), result == null ? 0 : result);
		
		//待办
		if(hasProxy){
			map.put(String.valueOf(StateEnum.col_pending.key()), 0);
		}
		else{
	    	criteria = DetachedCriteria.forClass(Affair.class)
	    		.setProjection(Projections.count("id"));
	    	
	    	if(isProxy){
	    		proxyIdList.add(memberId);
	    		criteria.add(Expression.in("memberId", proxyIdList)); //取我和被代理人的
	    	}
	    	else{
	    		criteria.add(Expression.eq("memberId", memberId));
	    	}
	    	
	    	criteria.add(Expression.eq("state", StateEnum.col_pending.key()))
				.add(Expression.eq("app", ApplicationCategoryEnum.collaboration.key())) //协同
		        .add(Expression.eq("isFinish", false))
		        .add(Expression.ne("isDelete", true))
		        .add(Expression.isNull("archiveId"))
			;
	    	
			result = (Integer)super.executeUniqueCriteria(criteria);
			map.put(String.valueOf(StateEnum.col_pending.key()), result == null ? 0 : result);
		}
		
    	//已办
		//if(hasProxy){
		//	map.put(String.valueOf(StateEnum.col_done.key()), 0);
		//}
		//else{
    	criteria = DetachedCriteria.forClass(Affair.class)
    		.setProjection(Projections.count("id"))
	    	.add(Expression.eq("state", StateEnum.col_done.key()))
			.add(Expression.eq("app", ApplicationCategoryEnum.collaboration.key())) //协同
	        //.add(Expression.eq("isFinish", false))
	        .add(Expression.ne("isDelete", true))
	        .add(Expression.isNull("archiveId"))
		;
		if(isProxy){//
			criteria.add(
					Expression.or(
						Expression.eq("memberId", memberId), 
						Expression.and(
							Expression.eq("memberId", proxyId),
							Expression.gt("completeTime", agentStamp)
						)
					)
			); //取我和被代理人的
		}
		else{
			criteria.add(Expression.eq("memberId", memberId));
		}
		result = (Integer)super.executeUniqueCriteria(criteria);
		map.put(String.valueOf(StateEnum.col_done.key()), result == null ? 0 : result);
		//}

		return map;
	}

	public V3xOrgAccount getAccount(Long uid) {
		try {
			V3xOrgMember member = orgManager.getMemberById(uid);

			return orgManager.getAccountById(member.getOrgAccountId());
		} catch (BusinessException e) {
			log.error("", e);
		}

		return null;
	}

	public Collaboration CollaborationDetial(Long cddid, Long uids) throws ColException{
		
		User user = CurrentUser.get();
		
		// cddid就是affairId
		String sign = null;
		
		try {
			Affair affair = affairManager.getById(cddid);
            if(affair == null){
                String msg=ColHelper.getErrorMsgByAffair(affair);
                throw new ColException(msg);
            }
            else{
            	ColHelper.updateAffairStateWhenClick(affair, affairManager);            	
            }
			ColSummary summary = this.colManager.getColAllById(affair
					.getObjectId());
			String newSubject = ColHelper.mergeSubjectWithForwardMembers(
					summary, orgManager, null);

			ColBody body = summary.getFirstBody();
			String content = null;
			String contentP = null;
			if(Constants.EDITOR_TYPE_FORM.equals(body.getBodyType())){
				content = "";
				contentP = body.getContent();
			}
			else{
				try{
					content = StrExtractor.getText(body.getBodyType(), body.getContent(), body.getCreateDate());
				}
				catch (UnknowBodyTypeException e) {
					sign = "UnknowBodyType";
				}
			}
			Set<ColComment> allComments = summary.getComments();
			Set<ColOpinion> allOpinions = summary.getOpinions();

			List<ColOpinion> senderOpinion = new ArrayList<ColOpinion>();
			List<ColOpinion> opinions = new ArrayList<ColOpinion>();

			// 原协同的处理人意�?
			java.util.Map<Integer, List<ColOpinion>> originalSignOpinion = new java.util.HashMap<Integer, List<ColOpinion>>();
			// 原协同的发起人附言
			java.util.Map<Integer, List<ColOpinion>> originalSendOpinion = new java.util.HashMap<Integer, List<ColOpinion>>();

			List<Integer> originalSendOpinionKey = new java.util.ArrayList<Integer>();

			Map<Long, List<ColComment>> commentsMap = new HashMap<Long, List<ColComment>>();

			ColHelper.modulateCommentOpinion(allOpinions, allComments,
					originalSendOpinionKey, originalSendOpinion,
					originalSignOpinion, opinions, senderOpinion, commentsMap,
					orgManager, false);

			java.util.Collections.sort(originalSendOpinionKey);

			Collaboration collaboration = new Collaboration();
			collaboration.setId(cddid);
			collaboration.setSummaryId(affair.getObjectId());
			collaboration.setTitle(newSubject);
			collaboration.setCreaterOr(summary.getStartMemberId());
			collaboration.setContent(content);
			collaboration.setComments(commentsMap);
			collaboration.setOpinions(opinions);
			collaboration.setCreatetime(summary.getCreateDate());
			collaboration.setState(affair.getState());
			collaboration.setProcessId(summary.getProcessId());
			collaboration.setOriginalSendOpinion(originalSendOpinion);
			collaboration.setOriginalSendOpinionKey(originalSendOpinionKey);
			collaboration.setOriginalSignOpinion(originalSignOpinion);
			collaboration.setSenderOpinion(senderOpinion);
			collaboration.setContentType(body.getBodyType());
			collaboration.setBody(body);
			collaboration.setFormURL("summaryId=" + summary.getId() + "&affairId=" + cddid + "&content=" + contentP);
			collaboration.setPower(false);
			collaboration.setSign(sign);
			collaboration.setTempleteId(summary.getTempleteId());
			collaboration.setHasAttachments(summary.isHasAttachments());

			String nodePermissionPolicy = colManager.getPolicyByAffair(affair);
			
			Long flowPermAccountId = user.getLoginAccount();
			try {
				V3xOrgMember sender = orgManager.getMemberById(summary.getStartMemberId());
				flowPermAccountId = ColHelper.getFlowPermAccountId(sender.getOrgAccountId(), summary, templeteManager);
			} catch (Exception e) {
				logger.error("",e);
			}
			
			Map<String, List<String>> actions = permissionManager.getActionMap(MetadataNameEnum.col_flow_perm_policy.name(), nodePermissionPolicy, flowPermAccountId);
			
			List<String> baseActions = actions.get("basic");
			List<String> advancedActions = actions.get("advanced");
			List<String> commonActions = actions.get("common");

			collaboration.setAllowOpinion((baseActions != null && baseActions.contains("Opinion")) || (advancedActions != null && advancedActions.contains("Opinion")) || (commonActions != null && commonActions.contains("Opinion")));

			Permission permission = permissionManager.getPermission(MetadataNameEnum.col_flow_perm_policy.name(), nodePermissionPolicy, flowPermAccountId);
			if(null==permission.getNodePolicy().getAttitude()){
				collaboration.setAllowAttitude(true);
			}else{
				collaboration.setAllowAttitude(permission.getNodePolicy().getAttitude() != 3);
			}
			collaboration.setNodePermissionPolicy(nodePermissionPolicy);
			collaboration.setPermission(permission);
			//设置附件信息
			List<Attachment> all = this.attachmentManager.getByReference(affair.getObjectId());
			if(CollectionUtils.isNotEmpty(all)){
				Map<Long,List<Attachment> > summaryAttahment = new HashMap<Long,List<Attachment>>();
				for (Attachment attachment : all) {
					List<Attachment> atts = summaryAttahment.get(attachment.getSubReference());
					if(atts == null){
						atts = new ArrayList<Attachment>();
					}
					atts.add(attachment);
					summaryAttahment.put(attachment.getSubReference(), atts);
					if(attachment.getFileUrl() != null){
						List<Attachment> urlIds = summaryAttahment.get(attachment.getFileUrl());
						if(urlIds == null){
							urlIds = new ArrayList<Attachment>();
						}
						urlIds.add(attachment);
						summaryAttahment.put(attachment.getFileUrl(), atts);
					}
				}
				collaboration.setColAttachment(summaryAttahment);
			}
			return collaboration;
		}
		catch (ColException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("", e);
		}

		return null;
	}
	
	public Object[] getColAttachment(long summaryId){
		List<Attachment> attOfNew = new ArrayList<Attachment>();
		List<Attachment> attOfNew_1 = new ArrayList<Attachment>();
		List<Attachment> attOfDoc = new ArrayList<Attachment>();//关联协同
		List<Attachment> all = this.attachmentManager.getByReference(summaryId);
		for (Attachment attachment : all) {
			if(attachment.getType() != MobileConstants.ATTACHMENT_TYPE.DOCUMENT.ordinal() && attachment.getType() != MobileConstants.ATTACHMENT_TYPE.IMAGE.ordinal()){
				if(summaryId == attachment.getSubReference().longValue()){
					attOfNew.add(attachment);
				}
				attOfNew_1.add(attachment);
			}else if(attachment.getMimeType().equals(ApplicationCategoryEnum.collaboration.name())){
				attOfDoc.add(attachment);
			} 
		}
		return new Object[]{attOfNew, attOfNew_1,attOfDoc};
	}
	
	public List<Attachment> getAttachment(long objectId){
		return this.attachmentManager.getByReference(objectId, objectId);
	}

	public Map<String, Object> getNodes(Long summaryId,Long caseId,String processId,boolean isProcess) throws MobileException {
		Map<String, Object> result = new HashMap<String, Object>();
		// cid为affairId
//		Long summaryId = this.affairManager.getObjectIdByAffairId(cid);
		if (summaryId == Constants.GLOBAL_NULL_ID) {
			return null;
		}
		String caseProcessXML = null;
		String caseWorkItemLogXML = null;
			if (caseId != null) {
				String modifyUserName = null;
				try {
					
					modifyUserName = colManager.checkModifyingProcess(processId, summaryId);
				} catch (ColException e1) {
					log.error("", e1);
				}
				if(modifyUserName != null && !"".equals(modifyUserName) && isProcess){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("isLock", true);
					map.put("locker", modifyUserName);
					return map;
				}
				try {
					caseProcessXML = colManager.getCaseProcessXML(caseId);
				} catch (ColException e) {
					log.error("移动接口实现中的得到节点的方法报错!", e);
					throw new MobileException(e);
				}
				try {
					caseWorkItemLogXML = colManager.getCaseWorkItemLogXML(caseId);
				} catch (ColException e) {
					log.error("移动接口实现中的得到节点的方法报错!", e);
					throw new MobileException(e);
				}
			} else if (StringUtils.isNotBlank(processId)) {
				try {
					caseProcessXML = colManager.getProcessXML(processId);
				} catch (ColException e) {
					log.error("移动接口实现中的得到节点的方法报错!", e);
					throw new MobileException(e);
				}
			}

		Nodes n = new Nodes();

		BPMProcess bPMProcess = getWorkflowInfo(n, caseProcessXML);

		result.put("nodes", n);
		if (caseWorkItemLogXML != null) {
			Map<String, List<Object[]>> caseWorkItemLog = getItemLog(caseWorkItemLogXML,bPMProcess);
			result.put("caseWorkItemLog", caseWorkItemLog);
		}

		return result;
	}

	private static BPMProcess getWorkflowInfo(Nodes n, String caseProcessXML) {
		if (StringUtils.isBlank(caseProcessXML)) {
			return null;
		}

		BPMProcess process = BPMProcess.fromXML(caseProcessXML);
		if (process == null) {
			return null;
		}
		boolean isShowShortName = "true".equalsIgnoreCase(process.getIsShowShortName());

		BPMStatus start = process.getStart();
		n.setNid(start.getId());
		n.setNodename(start.getName());
		List<BPMActor> actorList = start.getActorList();
		BPMActor actor = actorList.get(0);
		Long actorId = Long.parseLong(actor.getParty().getId());
		n.setUid(actorId);
		n.setRoleName(actor.getNodeInfo()[0]);
		addNodeToParty(start, n, isShowShortName);
		
		return process;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, List<Object[]>> getItemLog(String caseItemLogXML,BPMProcess process) {
		Map<String, List<Object[]>> result = new HashMap<String, List<Object[]>>();
		Document doc = null;
		String processId = null;
		try {
			doc = DocumentHelper.parseText(caseItemLogXML);
			List<Element> nodes = doc.selectNodes("//caseItemLog/WL");
			for (Element node : nodes) {
				String nodeId = node.valueOf("@N");
				Long memberId = new Long(node.valueOf("@P"));
				String memberName = node.valueOf("@PN");
				
				List<Object[]> members = result.get(nodeId);
				if (members == null) {
					members = new ArrayList<Object[]>();
				}
				List<Element> nodeList = node.selectNodes("L");
				
				BPMActivity activity = process.getActivityById(nodeId);
				BPMSeeyonPolicy policy = activity.getSeeyonPolicy();
				String excuteModel = policy.getProcessMode();
				
				if("competition".equals(excuteModel)&&(processId==null||(processId!=null&&!processId.equals(nodeId)))){
					inner:
					for(Element ele : nodeList){
						Integer action = Integer.parseInt(ele.valueOf("@A"));
						if(action==5){
							processId = nodeId;
							members.clear();
							members.add(new Object[] { memberId, memberName });
							break inner;
						}
						else{
							if(action==0){
								members.add(new Object[] { memberId, memberName });
							}
						}
					}
				}else{
					if(!"competition".equals(excuteModel)){
						members.add(new Object[] { memberId, memberName });
					}
				}
				
				result.put(nodeId, members);
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private static void addNodeToParty(BPMAbstractNode o, final Nodes n, final boolean isShowShortName) {
		String nodeType = o.getNodeType().name().trim().toLowerCase();
		Nodes n1 = null;
		if (!java.util.regex.Pattern.matches("start|split|join|end", nodeType)) {
			n1 = new Nodes();
			n1.setNid(o.getId());

			BPMActor actor = (BPMActor) o.getActorList().get(0);
			BPMParticipant party = actor.getParty();

			String[] nodeInfo = actor.getNodeInfo();
			
			String name = nodeInfo[0];
			String id = party.getId();
			String type = party.getType().id;

			if (type == null || "user".equalsIgnoreCase(type)) {
				type = V3xOrgEntity.ORGENT_TYPE_MEMBER;
			}

			if (V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(type)
					&& Strings.isDigits(id)) {
			}

			String seeyonPolicy = o.getSeeyonPolicy().getId();
			if(isShowShortName 
					&& Strings.isNotBlank(nodeInfo[1]) 
					&& !V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(type)){
				name = "(" + nodeInfo[1] + ")" + name;
			}
			n1.setNodename(Strings.isDigits(id)?o.getName():name);
			n1.setPermission(seeyonPolicy);
			n1.setParent(n);
			n1.setType(type);
			n1.setRoleName(name);
			n1.setUid(Strings.isDigits(id)?new Long(id):0L);
			n1.setIsDelete(o.getSeeyonPolicy().getIsDelete());
			n.addChild(n1);
		}

		List<BPMTransition> ts = o.getDownTransitions();
		if (ts == null) {
			return;
		}

		for (BPMTransition t : ts) {
			if (n1 != null) {
				addNodeToParty(t.getTo(), n1, isShowShortName);
			} else {
				addNodeToParty(t.getTo(), n, isShowShortName);
			}
		}
	}

	/**
	 * 
	 * @param pagecounter
	 *            每页多少条
	 * @param pagenumber
	 *            第几页
	 */
	private static void paginationHandle(int pagecounter, int pagenumber) {
		if (pagecounter < 1) {
			pagecounter = MobileConstants.PAGE_COUNTER;
		}

		if (pagenumber < 1) {
			pagenumber = 1;
		}

		int firstResult = (pagenumber - 1) * pagecounter;

		Pagination.setNeedCount(true); // 需要分页
		Pagination.setFirstResult(firstResult);
		Pagination.setMaxResults(pagecounter);
	}

	@SuppressWarnings("unchecked")
	public int getCollaborationDoneList(Long uid, int pagecounter,
			int pagenumber, List<Affair> currentlist, String titleKeyword) {
		try {
			paginationHandle(pagecounter, pagenumber);

			List<Affair> affairList = colManager.queryList4Mobile(uid, StateEnum.col_done, SEARCH_CONDITIION.title.name(), titleKeyword, null);

			currentlist.addAll(affairList);
			//Collections.sort(currentlist);
		}
		catch (ColException e) {
			log.error("", e);
		}

		return Pagination.getRowCount();
	}

	@SuppressWarnings("unchecked")
	public int getCollaborationPendingList(Long uid, int pagecounter,
			int pagenumber, List<AffairsListObject> currentlist, String titleKeyword) {
		try {
			paginationHandle(pagecounter, pagenumber);

			List<Affair> models = colManager.queryList4Mobile(uid, StateEnum.col_pending, SEARCH_CONDITIION.title.name(), titleKeyword, null);

			for (Affair objects : models) {
				currentlist.add(collaborationToObject(objects,StateEnum.col_pending.key()));
			}
		} catch (ColException e) {
			log.error("", e);
		}

		return Pagination.getRowCount();
	}

	@SuppressWarnings("unchecked")
	public int getCollaborationWaitSendList(Long uid, int pagecounter,
			int pagenumber, List<AffairsListObject> currentlist, String titleKeyword) {
		try {
			paginationHandle(pagecounter, pagenumber);

			List<Affair> models = colManager.queryList4Mobile(uid, StateEnum.col_waitSend, SEARCH_CONDITIION.title.name(), titleKeyword, null);

			for (Affair objects : models) {
				currentlist.add(collaborationToObject(objects,StateEnum.col_waitSend.key()));
			}
		} catch (ColException e) {
			log.error("", e);
		}

		return Pagination.getRowCount();
	}

	@SuppressWarnings("unchecked")
	public int getCollaborationSentList(Long uid, int pagecounter,
			int pagenumber, List<Affair> currentlist,
			String titleKeyword) {
		try {
			paginationHandle(pagecounter, pagenumber);

			List<Affair>  affairList = colManager.queryList4Mobile(uid, StateEnum.col_sent, SEARCH_CONDITIION.title.name(), titleKeyword, null,ApplicationCategoryEnum.collaboration.key(),ApplicationCategoryEnum.edoc.key(),ApplicationCategoryEnum.exSend.key(), ApplicationCategoryEnum.exSign.key(),ApplicationCategoryEnum.edocSend.key(), ApplicationCategoryEnum.edocRec.key(),ApplicationCategoryEnum.edocSign.key(),ApplicationCategoryEnum.edocRegister.key());
			currentlist.addAll(affairList);
			/*for (Affair objects : models) {
				currentlist.add(collaborationToObject(objects,StateEnum.col_sent.key()));
			}*/
		} 
		catch (ColException e) {
			log.error("", e);
		}

		return Pagination.getRowCount();
	}
	
	@SuppressWarnings("unchecked")
	private AffairsListObject collaborationToObject(Affair objects,int state){
		String forwardMember = objects.getForwardMember();
		Integer resentTime = objects.getResentTime();

		String subject = ColHelper.mergeSubjectWithForwardMembers(objects.getSubject(), forwardMember, resentTime, orgManager, null);

		AffairsListObject object = new AffairsListObject();
		
		object.setTitle(subject);
		object.setId(objects.getId());
		object.setSendTime(objects.getCreateDate());
		object.setSenderId(objects.getSenderId());
		object.setState(state);
		if(state==StateEnum.col_done.getKey()){
			object.setDealTiem(objects.getCompleteTime());
		}
		object.setType(objects.getApp());
		object.setBodyType(objects.getBodyType());
		object.setHasAttach(objects.isHasAttachments());
		
		return object;
	}
	

	public V3xOrgDepartment getDepartment(Long ddid) {
		try {
			return this.orgManager.getDepartmentById(ddid);
		} catch (BusinessException e) {
			log.error("", e);
		}

		return null;
	}

	public List<V3xOrgDepartment> getDepartmentSubordinate(Long dsid) {
		try {
			return this.orgManager.getChildDepartments(dsid, true, true);
		} catch (BusinessException e) {
			log.error("", e);
		}

		return null;
	}

	public int getTrackAffairObjectList(Long uid, int pagecounter,
			int pagenumber, List<Affair> pengingaffairlist,
			String titleKeyword) {
		paginationHandle(pagecounter, pagenumber);

		User user = CurrentUser.get();
		Long memberId = user.getId();
		Long proxyId = null; //不取代理人的跟踪

		List<Affair> affairs = this.affairManager.queryTrackList4Mobile(memberId, proxyId, SEARCH_CONDITIION.title.name(), titleKeyword, null);
		pengingaffairlist.addAll(affairs);

		return Pagination.getRowCount();
	}

	public Map<String, Integer> getHomePageInfo(Long uid,List<String> needCount) {
		User user = CurrentUser.get();
		Long memberId = user.getId();
		Map<String, Integer> map = new HashMap<String, Integer>();
		Long proxyIdCol = null; //代理不跟踪
		for(String menuId : needCount){
			if("6".equals(menuId)){
				int myTrack = this.affairManager.countTrack4Mobile(memberId, proxyIdCol, null, null, null);
				map.put("myTrack", myTrack);
			}else if("9".equals(menuId)){
				map.put("online", getOnLineNum());
			}else if("13".equals(menuId)){
				int myMeeting = this.affairManager.count(ApplicationCategoryEnum.meeting, memberId, StateEnum.col_pending, null);
				map.put("myMeeting", myMeeting);
			}else if("1".equals(menuId)){
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
				}else{
					isPloxy = false;
				}
				Map<Integer, AgentModel> agentModelMap = new HashMap<Integer, AgentModel>();
				if(isPloxy){
			    	for(AgentModel agentModel : agentModelList){
			    		String agentOptionStr = agentModel.getAgentOption();
			    		String[] agentOptions = agentOptionStr.split("&");
			    		for(String agentOption : agentOptions){
			    			int _agentOption = Integer.parseInt(agentOption);
			    			if(_agentOption == ApplicationCategoryEnum.collaboration.key()){
			    				if(agentModelMap.get(ApplicationCategoryEnum.collaboration.key()) != null ){
			    					if(agentModel.getStartDate().before(agentModelMap.get(ApplicationCategoryEnum.collaboration.key()).getStartDate()))
			    						agentModelMap.put(ApplicationCategoryEnum.collaboration.key(), agentModel);
			    				}else{
			    					agentModelMap.put(ApplicationCategoryEnum.collaboration.key(), agentModel);
			    				}
			    			}else if(_agentOption == ApplicationCategoryEnum.edoc.key()){
			    				if(agentModelMap.get(ApplicationCategoryEnum.edoc.key()) != null ){
			    					if(agentModel.getStartDate().before(agentModelMap.get(ApplicationCategoryEnum.collaboration.key()).getStartDate()))
			    						agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
			    				}else{
			    					agentModelMap.put(ApplicationCategoryEnum.edoc.key(), agentModel);
			    				}
			    			}
			    		}
			    	}
				}
				List<ApplicationCategoryEnum> categorys = new ArrayList<ApplicationCategoryEnum>();
				categorys.add(ApplicationCategoryEnum.collaboration);
				categorys.add(ApplicationCategoryEnum.edoc);
				int paddCount = affairManager.countPending(memberId, agentModelMap, null, null, null, agentToFlag,categorys);
				map.put("padding", paddCount);
			}
		}
		return map;
	}

	public MeetingDetial getMeetingDetial(Long mid, Long uid) {
		MtMeetingCAP m = this.mtMeetingManagerCAP.getByMtId(mid);
		String content = "";
		String sign = null;
		if(m!=null){
			MeetingDetial d = new MeetingDetial();
			try {
				content = StrExtractor.getText(m.getDataFormat(), m.getContent(), m.getCreateDate());
				if(!Constants.EDITOR_TYPE_HTML.equals(m.getDataFormat())){
					d.setBulContent(m.getContent());
				}
			}
			catch (UnknowBodyTypeException e) {
				sign = "UnknowBodyType";
			}

			d.setTitle(m.getTitle());
			d.setMasterId(m.getEmceeId());
			d.setRecordId(m.getRecorderId());
			d.setBeginDate(m.getBeginDate());
			d.setEndDate(m.getEndDate());
			d.setCreateDate(m.getCreateDate());
			d.setCreator(this.getMemberById(m.getCreateUser()).getId());
			d.setContent(content);
			d.setLocation(m.getAddress());
			d.setContentType(m.getDataFormat());
			d.setSign(sign);
			// d.setLocation(m);

			// 1 参加，2 不参加，3 未回执, 4 待定
			List<Object[]> attenders = new ArrayList<Object[]>();
			int attendNumber = 0;
			int unAttendNumber = 0;
			int noReplyNumber = 0;
			int pendingNumber = 0;

			List<MtReplyCAP> replies = this.mtReplyManagerCAP.findByPropertyNoInit(MtReplyCAP.PROP_MEETING_ID, mid);
			
			List<MobileReply> list = getMobileReplyList(m.getConferees());
			if(replies!=null){
				for(MtReplyCAP mr : replies){
					for(MobileReply r : list){
						if(mr.getUserId().equals(r.getUid())){
							r.setAttend(mr.getFeedbackFlag());
							boolean proxy = Strings.isNotBlank(mr.getExt1())?mr.getExt1().equals("1"):false;
							r.setProxy(proxy);
							r.setProxyName(mr.getExt2());
							break;
						}
					}
				}
			}
			for (MobileReply reply : list) {
				int attend = 3;
				switch (reply.getAttend()) {
				case com.seeyon.cap.meeting.util.Constants.FEEDBACKFLAG_ATTEND: // 参加
					attend = 1;
					attendNumber++;
					break;

				case com.seeyon.cap.meeting.util.Constants.FEEDBACKFLAG_UNATTEND:
					attend = 2;
					unAttendNumber++;
					break;
				case com.seeyon.cap.meeting.util.Constants.FEEDBACKFLAG_NOREPLY:
					attend = 3;
					noReplyNumber++;
					break;

				case com.seeyon.cap.meeting.util.Constants.FEEDBACKFLAG_PENDING:
					attend = 4;
					pendingNumber++;
					break;
				}

				attenders.add(new Object[] { reply.getUid(), attend ,reply.isProxy(),reply.getProxyName()});
			}

			d.setAttenders(attenders);

			Map<String, Integer> condition = new HashMap<String, Integer>();
			condition.put("1", attendNumber);
			condition.put("2", unAttendNumber);
			condition.put("3", noReplyNumber);
			condition.put("4", pendingNumber);

			d.setCondition(condition);

			return d;
		}else{
			return null;
		}
		
	}
	
	/**
	 * 查询符合条件的会议回执列表
	 * @param meetingId
	 * @param userId
	 * @return
	 */
	public List<MtReplyCAP> findByMeetingIdAndUserId(Long meetingId,Long userId){
		return this.mtReplyManagerCAP.findByMeetingIdAndUserId(meetingId, userId);
	}
	
	private List<MobileReply> getMobileReplyList(String conferees){
		List<MobileReply> newList = new ArrayList<MobileReply>();
		if(conferees!=null){
			String[] strs = conferees.split(",");
			for(String str : strs){
				String[] s = str.split("[|]");
				if(s!=null){
					if("Member".equals(s[0])){
						MobileReply mr = new MobileReply();
						mr.setUid(Long.parseLong(s[1]));
						mr.setAttend(com.seeyon.cap.meeting.util.Constants.FEEDBACKFLAG_NOREPLY);
						newList.add(mr);
					}else{
						if("Department".equals(s[0])){
							try {
								List<V3xOrgMember> l = orgManager.getMembersByDepartment(Long.parseLong(s[1]), false);
								if(l!=null){
									for(V3xOrgMember o : l){
										MobileReply mr = new MobileReply();
										mr.setUid(o.getId());
										mr.setAttend(com.seeyon.cap.meeting.util.Constants.FEEDBACKFLAG_NOREPLY);
										newList.add(mr);
									}
								}
							} catch (Exception e) {
								logger.error("", e);
							} 
						}else{
							if("Team".equals(s[0])){
								try {
									Set<V3xOrgMember> t = orgManager.getMembersByType("Team", Long.parseLong(s[1]));
									for(V3xOrgMember o : t){
										MobileReply mr = new MobileReply();
										mr.setUid(o.getId());
										mr.setAttend(com.seeyon.cap.meeting.util.Constants.FEEDBACKFLAG_NOREPLY);
										newList.add(mr);
									}
								} catch (Exception e) {
									logger.error("", e);
								} 
							}
						}
					}
				}
			}
		}
		return newList;
	}

	public int getMeetingObjectList(Long uid, int pagecounter, int pagenumber,
			List<AffairsListObject> currentlist, String titleKeyword) {
		//paginationHandle(pagecounter, pagenumber);
		User user = CurrentUser.get();
		Long memberId = user.getId();
		try {
			List<Affair> meetings = this.affairManager.getAffairs(
					ApplicationCategoryEnum.meeting, memberId,
					StateEnum.col_pending, null, titleKeyword, "createDate",true);
			
			List<Affair> meetingsSent =this.affairManager.getAffairs(
					ApplicationCategoryEnum.meeting, memberId,
					StateEnum.col_sent, null, titleKeyword, "createDate",true);
			meetings.addAll(meetingsSent);
			/*Boolean agentToFlag = false;
			List<AgentModel> agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(memberId);
			List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(memberId);
			
			List<AgentModel> modelList = null;
			List<AgentModel> meetingAgent = new ArrayList<AgentModel>();
	        if(agentModelToList != null && !agentModelToList.isEmpty()){
				modelList = agentModelToList;
				agentToFlag = true;
			}else{
				modelList = agentModelList;
			}
	        //空指针防护
	        if(modelList != null && !modelList.isEmpty()){
	        	 for(AgentModel agentModel : modelList){
	 	    		String agentOptionStr = agentModel.getAgentOption();
	 	    		String[] agentOptions = agentOptionStr.split("&");
	 	    		for(String agentOption : agentOptions){
	 	    			int _agentOption = Integer.parseInt(agentOption);
	 	    			if(_agentOption == ApplicationCategoryEnum.meeting.key()){
	 	    				meetingAgent.add(agentModel);
	 	    			}
	 	    		}
	 	    	}
	        }
	       
		    List<Affair> meetingAffairList = affairManager.queryAgentPendingList(memberId, meetingAgent, ApplicationCategoryEnum.meeting.key(), agentToFlag,Boolean.FALSE, false);
		    List<AffairsListObject> agentAffair = getAffairListMeetingMobile(meetingAffairList,true,meetingAgent,ApplicationCategoryEnum.meeting.key());
		    currentlist.addAll(agentAffair);
		    */
		    List<AffairsListObject> selfAffair = getAffairListMeetingMobile(meetings,false,null,ApplicationCategoryEnum.meeting.key());
		    currentlist.addAll(selfAffair);
		} catch (Exception e) {
			log.error("", e);
		}

		return currentlist!=null?currentlist.size():0;
	}
	
	private List<AffairsListObject> getAffairListMeetingMobile(List<Affair> list,boolean proxy,List<AgentModel> agentModels,int type){
		List<AffairsListObject> affairObjets = new ArrayList<AffairsListObject>();
		if(list!=null){
			User user = CurrentUser.get();
			for(Affair a : list){
				AffairsListObject affairObjet = new AffairsListObject();
				if(a.getMemberId().longValue() != user.getId()){
					affairObjet.setPorxyId(a.getMemberId());
					affairObjet.setProxy(true);
				}
				affairObjet.setTitle(a.getSubject());
				/*if(proxy){
					for(AgentModel am : agentModels){
						if(am.getAgentToId().equals(a.getMemberId())){
							break;
						}
					}
				}
				if (!com.seeyon.v3x.common.taglibs.functions.Functions
						.isMyAccount(com.seeyon.v3x.common.taglibs.functions.Functions
								.getMember(a.getSenderId())
								.getOrgAccountId())) {
					affairObjet
							.setTitle("("
									+ com.seeyon.v3x.common.taglibs.functions.Functions
											.getAccountShortName(com.seeyon.v3x.common.taglibs.functions.Functions
													.getMember(a.getSenderId())
													.getOrgAccountId()) + ")"
									+ a.getSubject());
				} else {
					affairObjet.setTitle(a.getSubject());
				}*/
				affairObjet.setId(a.getObjectId());
				affairObjet.setType(type);
				affairObjet.setSenderId(a.getSenderId());
				affairObjet.setSendTime(a.getCreateDate());
				affairObjet.setHasAttach(a.isHasAttachments());
				affairObjets.add(affairObjet);
			}
		}
		return affairObjets;
	}

	public List<V3xOrgMember> getMemberByDepartment(Long did) {
		V3xOrgAccount account = null;
		try {
			account = orgManager.getAccountById(did);
			if(account!=null){
				return null;
			}else{
				List<V3xOrgMember> innerList = orgManager.getMembersByDepartment(did, true);
				//List<V3xOrgMember> outerList = orgManager.getExtMembersByDepartment(did,true);
				/*if(innerList!=null && outerList!=null){
					innerList.addAll(outerList);
				}*/
				return innerList;
			}
			
		} catch (BusinessException e1) {
			log.error("", e1);
		}
		

		return null;
	}

	public List<V3xOrgMember> getMemberByTeam(Long tid) {
		try {
			List<Long> members = this.orgManager.getTeamById(tid).getAllMembers();
			List<V3xOrgMember> mems = new ArrayList<V3xOrgMember>();
			for (Long id : members) {
				V3xOrgMember m = this.orgManager.getMemberById(id);
				mems.add(m);
			}

			return mems;
		} catch (BusinessException e) {
			log.error("", e);
		}

		return null;
	}

	public int getOnLineNum() {
		return this.onLineManager.getOnlineNumber();
	}

    public List<OnlineUser> getOnLineUsers() {
	    return this.onLineManager.getOnlineList();
	}

	public List<Affair> getPendingAffairObjectList(Long memberId, String titleKeyword) {
		AgentAppUtil agentHelper = new AgentAppUtil(memberId);
		List<Integer> cateList = new ArrayList<Integer>();
		List<ApplicationCategoryEnum> categorys = new ArrayList<ApplicationCategoryEnum>();
		categorys.add(ApplicationCategoryEnum.collaboration);
		cateList.add(ApplicationCategoryEnum.collaboration.getKey());
		cateList.add(ApplicationCategoryEnum.edoc.getKey());
		categorys.add(ApplicationCategoryEnum.edoc);
		Pagination.setMaxResults(MobileConstants.PAGE_COUNTER);
		List<Affair> affairs = affairManager.queryPendingList(memberId, agentHelper.getAppAgentMap(cateList), "title", titleKeyword, "", agentHelper.isAgentToFlag(),true,categorys);
		return affairs;
	}

	public Map<RelationType, List<Long>> getRelativeMember(Long uid) {
		if (uid == null) {
			uid = CurrentUser.get().getId();
		}

		Map<RelationType, List<Long>> relates = null;
		try {
			relates = this.peoplerelateManager.getAllRelateMembersId(uid);
		} catch (Exception e) {
			log.error("", e);
			return null;
		}

		return relates;
	}

	public List<V3xOrgTeam> getTeamList(Long uid) {
		if (uid == null) {
			uid = CurrentUser.get().getId();
		}

		try {
			List<V3xOrgTeam> teams = new ArrayList<V3xOrgTeam>();
			List<V3xOrgEntity> entities = this.orgManager.getUserDomain(uid,
					ORGENT_TYPE_TEAM);
			for (V3xOrgEntity entity : entities) {
				if (entity instanceof V3xOrgTeam) {
					teams.add((V3xOrgTeam) entity);
				}
			}

			return teams;
		} catch (BusinessException e) {
			log.error("", e);
		}
		return null;
	}

	public void processCollaboration(int actiontype, Long id, String opinion,
			int attitude, Map<String, String[]> flowchart,
			Map<String, String> conditionNodes,boolean track) throws MobileException {
		// id就是affairId
		ColOpinion colOpinion = new ColOpinion();
		colOpinion.setContent(opinion);
		colOpinion.setAttitude(attitude);
		colOpinion.affairIsTrack = track;
		
		switch (actiontype) {
		case 1:
			try {
					this.doSign(id, colOpinion, flowchart, conditionNodes);
				} catch (ColException e) {
					log.error("移动接口实现中的处理协同的方法报错!", e);
					throw new MobileException(e);
				}
			break; // 提交
		case 2:
			try {
					this.doZcdb(id, colOpinion);
				} catch (ColException e) {
					log.error("移动接口实现中的处理协同的方法报错!", e);
					throw new MobileException(e);
				}
			break; // 暂存待办
		}

	}

	/**
	 * 协同处理
	 * 
	 * @param affairId
	 * @param signOpinion
	 * @throws ColException
	 */
	private void doSign(Long affairId, ColOpinion signOpinion,
			Map<String, String[]> flowchart, Map<String, String> conditionNodes)
			throws ColException ,MobileException{
		User user = CurrentUser.get();
		signOpinion.setOpinionType(ColOpinion.OpinionType.signOpinion);
        signOpinion.isPipeonhole = false;
		
        if (conditionNodes != null) {
			for (Iterator iter = conditionNodes.keySet().iterator(); iter
					.hasNext();) {
				String node = (String) iter.next();
				if ("true".equals(conditionNodes.get(node))) {
					flowchart.remove(node);
				}
			}
		}

		colManager.finishWorkItem(affairId, signOpinion, flowchart,
				conditionNodes, null, user);
		
		Affair affair = affairManager.getById(affairId);
		ColSummary summary = null;
		try {
			summary = colManager.getColAllById(affair.getObjectId());
		} catch (ColException e1) {
			throw new MobileException(e1.getMessage());
		}
        Map<Long,Map<String,Boolean>> cancelPurviewNew = this.getCancelPurview();
		 
		 if(cancelPurviewNew!=null){
			 Map<String,Boolean> mapNew = cancelPurviewNew.get(user.getId());
			 if(mapNew!=null){
				 mapNew.put(affairId.toString(), false);
				 this.setCancelPurview(cancelPurviewNew);
			 }
		 }
		
		
	}

	/**
	 * 协同暂存待办
	 * 
	 * @param affairId
	 * @throws ColException
	 */
	private void doZcdb(Long affairId, ColOpinion opinion) throws ColException ,MobileException{
		User user = CurrentUser.get();
		String processId = null;
		long summaryId = 0L;
		String userId = user.getId() + "";
		
		Affair affair = this.affairManager.getById(affairId);
		
		colManager.zcdb(summaryId, affair, opinion, processId, userId);
		ColSummary summary = null;
		try {
			summary = colManager.getColAllById(affair.getObjectId());
		} catch (ColException e1) {
			throw new MobileException(e1.getMessage());
		}
		if(summary != null)
			processLogManager.insertLog(user, Long.parseLong(summary.getProcessId()), affair.getActivityId(), ProcessLogAction.zcdb);
		
	}

    
    /**
     * 根据会议的短信回复信息处理会议回执
     */
	public void processMeeting(Long mid, Long uid, int process, String opinion,boolean proxy)
			throws MobileException {
		try {
			MtMeetingCAP m = this.mtMeetingManagerCAP.getByMtId(mid);
			List<MtReplyCAP> list = this.mtReplyManagerCAP.findByMeetingIdAndUserId(mid,uid);
			MtReplyCAP reply = null;
			if(CollectionUtils.isNotEmpty(list)){
				for(int i = 0; i < list.size(); i ++){
					reply = list.get(i);
					if(proxy){
						reply.setExt1("1");// 标记由代理人带办的
						reply.setExt2(CurrentUser.get().getName());// 存储代理人的姓名
					}
					this.mtReplyManagerCAP.reply(reply.getMeetingId(),uid,opinion,process);
					if(process != com.seeyon.cap.meeting.util.Constants.FEEDBACKFLAG_ATTEND){
						this.mtMeetingManagerCAP.deleteCalEvent(m.getId(), uid);
					}
				}
			}else{
				reply = new MtReplyCAP();
				if(proxy){
					reply.setExt1("1");// 标记由代理人带办的
					reply.setExt2(CurrentUser.get().getName());// 存储代理人的姓名
				}
				reply.setFeedbackFlag(process);
				reply.setMeetingId(mid);
				reply.setUserId(uid);
				reply.setFeedback(opinion);
				reply = this.mtReplyManagerCAP.save(reply);
				if(process == com.seeyon.cap.meeting.util.Constants.FEEDBACKFLAG_ATTEND){
					this.mtMeetingManagerCAP.createCalEvent(m, uid);
				}
			}
			
			// 发送消息
			List<Long> listId = new ArrayList<Long>();
			listId.add(m.getCreateUser());
			Collection<MessageReceiver> receivers = MessageReceiver.get(m.getId(), listId, "message.link.mt.reply", m.getId().toString(), reply.getId().toString());
			try {
				String feedback = MessageUtil.getComment4Message(reply.getFeedback());
				int contentType = Strings.isBlank(feedback) ? -1 : 1;
				int proxyType = proxy ? 1 : 0;
				V3xOrgMember member = orgManager.getMemberById(uid);
				String userName = "";
				if (member != null) {
					userName = member.getName();
				}
				userMessageManager.sendSystemMessage(MessageContent.get("mt.reply", m.getTitle(), userName, reply.getFeedbackFlag(), contentType, feedback, proxyType, userName),
						ApplicationCategoryEnum.meeting, uid, receivers);
			} catch (MessageException e) {
				log.error("发送消息失败：", e);
			}
			
		} catch (BusinessException e) {
			log.error("移动接口实现中的处理会议方法报错!", e);
			throw new MobileException(e);
		}

	}

	public void saveToPendingAffair(Long affairId, String title,
			String content, List<String[]> memberlist, int type, Long senderid,HttpServletRequest request)
			throws MobileException {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		ColSummary colSummary = new ColSummary();

		if (affairId != null) {
			Affair affair = affairManager.getById(affairId);

			colSummary.setId(affair.getObjectId());
		}

		colSummary.setIdIfNew();
		colSummary.setSubject(title);
		colSummary.setStartMemberId(senderid);
		colSummary.setCanForward(true);
		colSummary.setCanArchive(true);
		colSummary.setCanDueReminder(true);
		colSummary.setCanEditAttachment(true);
		colSummary.setCanModify(true);
		colSummary.setCanTrack(true);
		colSummary.setCanEdit(true);
		colSummary.setCreateDate(now);
		colSummary.setStartDate(now);

		ColOpinion senderOninion = new ColOpinion();
		senderOninion.setContent(null);
		senderOninion.setIdIfNew();
		senderOninion.affairIsTrack = true;

		ColBody body = new ColBody();
		body.setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		body.setContent(content);
		body.setCreateDate(now);
		body.setIdIfNew();

		FlowData flowData = FlowData.flowdataFromTypeIds(senderid,type, getEntityType(memberlist),getEntityMemberIds(memberlist),orgManager);

		try {
			colManager.saveDraft(flowData, colSummary, body, senderOninion, colSummary.isNew());
		} catch (ColException e) {
			log.error("移动接口实现中的保存为待发事项方法报错!", e);
			throw new MobileException(e);
		}
		
		if(attachmentManager.hasAttachments(colSummary.getId(), colSummary.getId())){
			
			try {
				attachmentManager.update(ApplicationCategoryEnum.collaboration, colSummary.getId(), colSummary.getId(), request);
			} catch (Exception e) {
				log.error("移动上传附件错误！", e);
			}
		}else{
			try {
				attachmentManager.create(ApplicationCategoryEnum.collaboration, colSummary.getId(), colSummary.getId(), request);
			} catch (Exception e1) {
				log.error("移动上传附件错误！", e1);
			}
		}
		
	}
	
	private List<String> getEntityType(List<String[]> list){
		List<String> arrayList = new ArrayList<String>();
		for(String[] array : list){
			arrayList.add(array[1]);
		}
		return arrayList;
	}
	
	private List<Long> getEntityMemberIds(List<String[]> list){
		List<Long> arrayList = new ArrayList<Long>();
		for(String[] arrayStr : list){
			arrayList.add(Long.parseLong(arrayStr[0]));
		}
		return arrayList;
	}

	public void sendCollaborationNow(String title, String content,
			List<String[]> memberlist, int type, Long senderid, Long cid,
			String processId, Map<String, String[]> flowchart,
			List<String> conditionNodes, String allNodes,HttpServletRequest request) throws MobileException {
		// cidd是affairId
		Timestamp now = new Timestamp(System.currentTimeMillis());
		User user = CurrentUser.get();
		FlowData flowData = null;
		if (memberlist != null && !memberlist.isEmpty()) { // 新建的流程
			flowData = FlowData.flowdataFromTypeIds(senderid,type,getEntityType(memberlist),getEntityMemberIds(memberlist),
					orgManager);
		}
		
		Long summaryId = null;
		if(cid != null){
			summaryId = this.affairManager.getObjectIdByAffairId(cid);
		}
		
		if (cid == null || (cid != null && flowData != null)) { // 新建
			ColSummary colSummary = new ColSummary();
			colSummary.setId(summaryId);
			
			boolean isNew = colSummary.isNew();
			
			colSummary.setIdIfNew();
			summaryId = colSummary.getId();
			
			colSummary.setCreateDate(now);
			colSummary.setStartDate(now);
			colSummary.setSubject(title);
			colSummary.setStartMemberId(senderid);
			colSummary.setCanForward(true);
			colSummary.setCanArchive(true);
			colSummary.setCanDueReminder(true);
			colSummary.setCanEditAttachment(true);
			colSummary.setCanModify(true);
			colSummary.setCanTrack(true);
			colSummary.setCanEdit(true);
			if(request.getParameterValues(com.seeyon.v3x.common.filemanager.Constants.FILEUPLOAD_INPUT_NAME_fileUrl) != null){
				colSummary.setHasAttachments(true);
			}
			ColOpinion senderOninion = new ColOpinion();
			senderOninion.setContent(null);
			senderOninion.setIdIfNew();
			senderOninion.affairIsTrack = true;

			ColBody body = new ColBody();
			body.setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
			body.setContent(content);
			body.setCreateDate(now);
			body.setIdIfNew();

			megerFlowData(flowData, flowchart, conditionNodes, allNodes);

			Map<String, Object> options = new HashMap<String, Object>();
			try {
				colManager.runCase(flowData, colSummary, body, senderOninion,
						Constant.SendType.normal, options, isNew, CurrentUser.get().getId(),null);
			} catch (ColException e) {
				log.error("移动接口实现中的立即发送的方法报错!", e);
				throw new MobileException(e);
			}
			//发送 流程日志
			processLogManager.insertLog(user, Long.parseLong(colSummary.getProcessId()), -1l, ProcessLogAction.sendColl);
	        try {
				//协同发起事件通知
				CollaborationStartEvent event = new CollaborationStartEvent(this);
				event.setSummaryId(colSummary.getId());
				event.setFrom("mobile");
				event.setAffairId(affairManager.getCollaborationSenderAffair(
						colSummary.getId()).getId());
				EventDispatcher.fireEvent(event);
			} catch (Throwable e) {
				log.error(e.getMessage(),e);
			}
	        
		}else if (cid != null && flowData == null
				&& Strings.isNotBlank(processId)) { // 来自待发 没有新流程，但有老流程
			try {
				flowData = ColHelper.getRunningProcessPeople(processId);
			} catch (ColException e) {
				log.error("移动接口实现中的立即发送的方法报错!", e);
				throw new MobileException(e);
			}
			megerFlowData(flowData, flowchart, conditionNodes, allNodes);
			
			Map<String, Object> summ = new HashMap<String, Object>();
			ColSummary colSummary = null;
			try {
				colSummary = colManager.getColSummaryById(summaryId, false);
			}
			catch (Exception e1) {
			}

			Map<String, Object> bo = new HashMap<String, Object>();
			bo.put("content", content);

			Map<String, Object> aff = new HashMap<String, Object>();

			aff.put("subject", title);
			aff.put("app", ApplicationCategoryEnum.collaboration.key());
			aff.put("state", StateEnum.col_sent.key());
			aff.put("subState", SubStateEnum.col_normal.key());
			aff.put("createDate", now);

			summ.put("createDate", now);
			summ.put("subject", title);
			summ.put("processId", processId);
			summ.put("startDate", now);
			
			colSummary.setSubject(title);
			colSummary.setCreateDate(now);
			colSummary.setStartDate(now);
			colSummary.setOrgAccountId(CurrentUser.get().getLoginAccount());
			colSummary.setOrgDepartmentId(CurrentUser.get().getDepartmentId());
			colSummary.setState(Constant.flowState.run.ordinal());

			try {
				processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, processId, false);
			} catch (ColException e) {
				log.error("移动接口实现中的立即发送的方法报错!", e);
				throw new MobileException(e);
			}
	        // 通知全文检索不入库
	        DateSharedWithWorkflowEngineThreadLocal.setNoIndex();
	        DateSharedWithWorkflowEngineThreadLocal.setColSummary(colSummary);
	        
			try {
				long caseId = ColHelper.runCase(processId);
				summ.put("caseId", caseId);
				colSummary.setCaseId(caseId);
			} catch (ColException e) {
				log.error("移动接口实现中的立即发送的方法报错!", e);
				throw new MobileException(e);
			}
			
			//全文检索统一入库
			if(IndexInitConfig.hasLuncenePlugIn()){
				try {
					indexManager.index(((IndexEnable)colManager).getIndexInfo(summaryId));
				}
				catch (Exception e) {
					throw new MobileException(e);
				}
			}

			this.colManager.update(ColBody.class, bo, new Object[][]{{"summaryId", summaryId}});
			this.colManager.update(summaryId, summ);
			this.affairManager.update(cid, aff);
			
			if(colSummary != null){
		        //协同发起事件通知
		        CollaborationStartEvent event = new CollaborationStartEvent(this);
		        event.setSummaryId(colSummary.getId());
		        event.setFrom("mobile");
		        EventDispatcher.fireEvent(event);
			}
		}
		
		boolean ishasAtt = attachmentManager.hasAttachments(summaryId, summaryId);
		if(!ishasAtt){
			try {
				attachmentManager.create(ApplicationCategoryEnum.collaboration, summaryId, summaryId, request);
			} catch (Exception e1) {
				log.error("移动上传附件错误！", e1);
			}
		}else{
			try {
				attachmentManager.update(ApplicationCategoryEnum.collaboration, summaryId, summaryId, request);
			} catch (Exception e) {
				log.error("移动上传附件错误！", e);
			}
		}
		
	}

	/**
	 * 
	 * 
	 * @param flowData
	 * @param flowchart
	 * @param conditionNodes
     * 
	 * @param allNodes
	 */
	private static void megerFlowData(FlowData flowData,
			Map<String, String[]> flowchart, List<String> conditionNodes,
			String allNodes) {
		Map<String, String> condition = new HashMap<String, String>();
		if (allNodes != null) {
			String[] nodes = StringUtils.split(allNodes, ":");
			if (nodes != null) {
				if (conditionNodes == null) {
					conditionNodes = new ArrayList<String>();
				}

				for (String node : nodes) {
					String result = conditionNodes.contains(node) ? "false"
							: "true"; // 传递给后台的isDelete属性，所以要取反
					condition.put(node, result);

					if ("false".equals(result))
						flowchart.remove(node);
				}
				flowData.setCondition(condition);
			}
		}

		flowData.setAddition(flowchart);
	}

	public V3xOrgDepartment getDepartmentByPath(String path,Long accountId) {
		try {
			if(accountId == null)
				return this.orgManager.getDepartmentByPath(path);
			else{
				List<V3xOrgDepartment> depts = orgManager.getAllDepartments(accountId);
				for(V3xOrgDepartment dept :depts){
					if(dept.getPath().equals(path)){
						return dept;
					}
				}
			}
		} catch (BusinessException e) {
			log.error("", e);
		}

		return null;
	}

	public List<V3xOrgDepartment> getDepartmentByAccount(Long accountId,
			boolean isFirstLayer) {
		try {
			return this.orgManager.getChildDepartments(accountId, isFirstLayer);
		} catch (BusinessException e) {
			log.error("", e);
		}

		return null;
	}

	public V3xOrgMember getMemberById(Long id) {
		return (V3xOrgMember) this.getOrgEntity(
				V3xOrgEntity.ORGENT_TYPE_MEMBER, id);
	}

	public V3xOrgEntity getOrgEntity(String type, Long id) {
		try {
			return this.orgManager.getGlobalEntity(type, id);
		} catch (BusinessException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int searchResult(Long userId, String keyword, int pagecounter,
			int pagenumber, List<SearchResult> searchResult) {
		User user = CurrentUser.get();
        if (userId == null) {
			userId = user.getId();
		}

		try {
//            String authorKey = "(owner:'" + userId + "')OR(owner:'ALL')"+ "OR(department:'"+user.getDepartmentId()+"')"+"OR(account:'"+user.getLoginAccount()+"')";
			String authorKey = IndexSearchHelper.getAuthorKey();
			paginationHandle(pagecounter, pagenumber);
			Map map = new HashMap();
			map.put("pageSize",new Integer(pagecounter < 1?MobileConstants.PAGE_COUNTER:pagecounter).toString());
			String appEnum[] = {ApplicationCategoryEnum.collaboration.name(),ApplicationCategoryEnum.news.name(),ApplicationCategoryEnum.bulletin.name(),ApplicationCategoryEnum.meeting.name(),ApplicationCategoryEnum.calendar.name()};
			map.put(IndexInfo.PARAMETER_KEYWORD, IndexSearchHelper.replaceSearchKey(keyword));
			SearchResultWapper searchResultWapper = this.indexManager.search(
					authorKey, map, appEnum,
					Pagination.getFirstResult());
			SearchResult[] results = searchResultWapper.getSearchResults();

			int maxResults = Pagination.getMaxResults();

			for (int i = 0; i < results.length && i < maxResults; i++) {
				SearchResult result = results[i];

				int appKey = Integer.parseInt(results[i].getAppType());
				String affairId = "";
				if(appKey == ApplicationCategoryEnum.collaboration.key()||appKey==ApplicationCategoryEnum.form.key()){
					affairId = urlLinkDAO.findAffairID(
							ApplicationCategoryEnum.collaboration.key(), result
									.getId().toString(), userId.toString());
				}else{
					affairId = result.getId().toString();
				}
				results[i].setLinkId(affairId);
				searchResult.add(result);
			}

			return searchResultWapper.getResultCount();
		} catch (Exception e) {
			log.error("", e);
		}

		return 0;
	}

	public void transmitCollaboration(String opinion, List<Long> memberlist,
			Long senderid) {
		// TODO Auto-generated method stub
	}

	public StringBuffer getAttachmentContent(Long fileURL, Date createDate) {
		try {
			V3XFile v3xFile = this.fileManager.getV3XFile(fileURL);
			if (v3xFile != null) {
				String s = StrExtractor.getV3XFileContent(v3xFile);
				if(s == null){
					s = "";
				}
				return new StringBuffer(s);
			}
		} catch (BusinessException e) {
			log.error("", e);
		}

		return null;
	}

	public int BulletinList(Long uid, int pagecounter, int pagenumber,
			List<AffairsListObject> currentlist, String titleKeyword) {
		paginationHandle(pagecounter, pagenumber);

		User user = CurrentUser.get();

		List<BulData> list = null;
		try {
			SearchInfo searchInfo = BulletinHqlUtils.getSearchInfo(SEARCH_CONDITIION.title.name(), titleKeyword, null);
			list = bulDataManager.findMyBulDatas(user, searchInfo, false);
		}
		catch (Exception e) {
			log.error("", e);
		}

		if (list != null) {
			for (BulData data : list) {
				AffairsListObject a = new AffairsListObject();
				a.setId(data.getId());
				if (!Functions.isMyAccount(data.getAccountId())) {
					a.setTitle("(" + Functions.getAccountShortName(data.getAccountId()) + ")" + data.getTitle());
				}
				else {
					a.setTitle(data.getTitle());
				}
				a.setType(ApplicationCategoryEnum.bulletin);
				a.setSenderId(data.getCreateUser());
				a.setSendTime(data.getCreateDate());
				if(data.getAttachmentsFlag() != null)
					a.setHasAttach(data.getAttachmentsFlag());
				currentlist.add(a);
			}
		}

		return Pagination.getRowCount();
	}

	public BulData getBulletionDetial(Long bid, Long uid) {
		BulData bul = bulDataManager.getById(bid, uid);
		return bul;
	}
	
	public Bulletion getBulletionDetial(BulData bul){
		if(bul == null){
			return null;
		}
		String sign = null;
		String content = null;
		BulBody bulBody = bulDataManager.getBody(bul.getId());
		try {
			if(bulBody != null){
				content = StrExtractor.getText(bul.getDataFormat(), bulBody.getContent(), bul.getCreateDate());
			}
		}
		catch (UnknowBodyTypeException e) {
			sign = "UnknowBodyType";
		}
		
		Bulletion b = new Bulletion();
		b.setTitle(bul.getTitle());
		b.setSendTime(bul.getCreateDate());
		b.setContent(content);
		b.setSenderId(bul.getCreateUser());
		b.setType(bul.getType().getTypeName());
		b.setContentType(bul.getDataFormat());
		b.setAttachmentList(this.attachmentManager.getByReference(bul.getId(), bul.getId()));
		b.setSign(sign);
		b.setDeleteFlag(bul.isDeletedFlag());
		b.setBulState(bul.getState());
		b.setBulBody(bulBody);
		return b;
	}

	public Calendar getCalendarDetial(Long cid, Long uid) {
		String sign = null;
//		This instruction assigns a value to a local variable, but the value is not read or used in any subsequent instruction. Often, this indicates an error, because the value computed is never used.
//		uid = CurrentUser.get().getId();
		CalEvent event = this.calEventManager.getEventById(cid);
		CalContent body=new CalContent();
		List<CalContent> eventContent=null;
		if(event!=null){
			eventContent=this.calContentManager.getContentByEventId(event.getId());
			for (int i=0;i<eventContent.size();i++){
				body= eventContent.get(i);	
			}
			String content = null;
			try {
				content = StrExtractor.getText(body.getContentType(), body.getContent(), body.getCreateDate());
			}
			catch (UnknowBodyTypeException e) {
				sign = "UnknowBodyType";
			}

			List<Attachment> attachments = this.attachmentManager.getByReference(cid, cid);

			Calendar c = new Calendar();
			c.setTitle(event.getSubject());
			c.setBeginTime(event.getBeginDate());
			c.setEndTime(event.getEndDate());
			c.setContent(content);
			c.setAttachments(attachments);
			c.setContentType(body.getContentType());
			c.setCalEvent(event);
			c.setSign(sign);
			return c;
		}else{
			return null;
		}
	}
	private static CalEventComparator calEventComparator = new CalEventComparator();
	
	public int CalendarList(Long uid, int pagecounter, int pagenumber,
			List<AffairsListObject> currentlist, String titleKeyword) {
		paginationHandle(pagecounter, pagenumber);
		uid = CurrentUser.get().getId();

		List<CalEvent> list = null;
		list = this.calEventManager.getEventListByUserId(uid,true,true);
		List<CalEvent> newList = calEventManager.getOtherEventListByUserIdForMain(CurrentUser.get(),this.peopleRelateManager,null,null);
		if(newList!=null){
			if(list == null){
				list = newList;
			}else{
				for(CalEvent c : newList){
					if(list!=null && !list.contains(c)){
						list.add(c);
					}
				}
			}
		}
		if (list == null) {
			return 0;
		}
		boolean isSearch = Strings.isNotBlank(titleKeyword);
		CharSequence title = null;
		if(isSearch){
			title = new StringBuffer(titleKeyword);
		}
		Collections.sort(list,calEventComparator);
		for (CalEvent data : list) {
			if(isSearch && !data.getSubject().contains(title)){
				continue;
			}
			AffairsListObject a = new AffairsListObject();
			a.setId(data.getId());
			a.setSenderId(data.getCreateUserId());
			a.setSendTime(data.getBeginDate());
			a.setTitle(data.getSubject());
			a.setType(ApplicationCategoryEnum.calendar);
			a.setCalEvent(data);
			if(data.getAttachmentsFlag() != null)
				a.setHasAttach((Boolean)data.getAttachmentsFlag());
			currentlist.add(a);
		}
		return Pagination.getRowCount();
	}

	public News getNewsDetial(Long nid, Long uid) {
		String sign = null;
		uid = CurrentUser.get().getId();
		NewsData news = newsDataManager.getById(nid, uid);
		if(news == null){//如果没有，提示删除信息
			return null;
		}
		String content = null;
		News n = new News();
		try {
			NewsBody newsBody = newsDataManager.getBody(nid);
			if(newsBody != null){
				content = StrExtractor.getText(news.getDataFormat(), newsBody.getContent(), news.getCreateDate());
				if(!Constants.EDITOR_TYPE_HTML.equals(news.getDataFormat())){
					n.setBodyId(newsBody.getContent());
				}
			}
		}
		catch (UnknowBodyTypeException e) {
			sign = "UnknowBodyType";
		}
		n.setTitle(news.getTitle());
		n.setSendTime(news.getCreateDate());
		n.setContent(content);
		n.setSenderId(news.getCreateUser());
		n.setNewsType(news.getType().getTypeName());
		n.setContentType(news.getDataFormat());
		n.setSign(sign);
		n.setDeletedFlag(news.isDeletedFlag());
		n.setState(news.getState());
		n.setAttachments(this.attachmentManager.getByReference(nid, nid));

		return n;
	}

	public int NewsList(Long uid, int pagecounter, int pagenumber,
			List<AffairsListObject> currentlist, String titleKeyword) {
		paginationHandle(pagecounter, pagenumber);
		uid = CurrentUser.get().getId();

		List<NewsData> list = null;
		
		try {
			list = newsDataManager.findByReadUser4Mobile(uid, SEARCH_CONDITIION.title.name(), titleKeyword);
		}
		catch (BusinessException e) {
			log.error("", e);
		}

		if (list != null) {
			for (NewsData data : list) {
				AffairsListObject a = new AffairsListObject();
				a.setId(data.getId());
				if (!Functions.isMyAccount(data.getAccountId())) {
					a.setTitle("(" + Functions.getAccountShortName(data.getAccountId()) + ")" + data.getTitle());
				}
				else {
					a.setTitle(data.getTitle());
				}
				a.setType(ApplicationCategoryEnum.news);
				a.setSenderId(data.getCreateUser());
				a.setSendTime(data.getCreateDate());
				if(data.getAttachmentsFlag() != null)
					a.setHasAttach(data.getAttachmentsFlag());
				currentlist.add(a);
			}
		}

		return Pagination.getRowCount();
	}
	
	public int searchMember(Long departmentId, String keyword, int pagecounter, int pagenumber,
			List<MobileOrgEntity> list) {

		paginationHandle(pagecounter, pagenumber);

		List<MobileOrgEntity> temp = new ArrayList<MobileOrgEntity>();
		List<MobileOrgEntity> list1 = getMobileOrgEntity(departmentId);
		CharSequence c = new StringBuffer(keyword);
		Set<V3xOrgMember> member = new HashSet<V3xOrgMember>();
		try {
			for(MobileOrgEntity entity : list1){
				switch(entity.getType()){
				case 0:	member.addAll(orgManager.getMembersByType("Department", entity.getId()));
				break;
				case 1:member.addAll(orgManager.getMembersByType("Member", entity.getId()));
				break;
				case 2:member.addAll(orgManager.getMembersByType("Account", entity.getId()));
				break;
				}
			}
		} catch (BusinessException e) {
			log.error(e.getMessage(),e);
		}
		for (V3xOrgMember member2 : member) {
			if(member2.getName().contains(c)){
				MobileOrgEntity entity = new MobileOrgEntity();
				entity.setId(member2.getId());
				entity.setName(member2.getName());
				entity.setType(1);
				temp.add(entity);
			}
		}
		if(temp == null || temp.isEmpty()){
			return 0;
		}
		
		int size = temp.size();

		//temp = subList(temp, (pagenumber - 1) * pagecounter, pagenumber * pagecounter);
		
		list.addAll(temp);
		Pagination.setRowCount(size);
		
		return size;
		
	}
	
	private static <T> List<T> subList(List<T> list, int fromIndex, int toIndex){
		if(list.size() < fromIndex){
			return null;
		}
		
		if(toIndex > list.size()){
			toIndex = list.size();
		}
		
		return list.subList(fromIndex, toIndex);
	}

	public void setPAGE_COUNTER(int page_counter) {
		MobileConstants.PAGE_COUNTER = page_counter;
	}

	public  void setHtmlSuffix(String htmlSuffix) {
		MobileConstants.htmlSuffix = htmlSuffix;
	}

	public void setMOBILE(String mobile) {
		MobileConstants.MOBILE = mobile;
	}

	public void setCAT(int cat) {
		MobileConstants.CAT = cat;
	}

	public Long getCurrentId() {
		User currentUser = CurrentUser.get();
		return currentUser.getId();
	}
	
	public boolean isSeen(Long uid, Long currentUserId) {
		if (currentUserId == null) {
			currentUserId = CurrentUser.get().getId();
		}
		try {
			V3xOrgMember currentMember = this.getMemberById(currentUserId); // 当前登录者
			V3xOrgMember member = this.getMemberById(uid); // 被检测的人

			if (currentMember == null || member == null) {
				return false;
			}else{
				if((currentMember.getOrgLevelId()==-1)&&(currentMember.getOrgLevelId() == member.getOrgLevelId())){
					return true;
				}else{
					if(currentMember.getOrgLevelId()!=-1){
						
						if(member.getOrgLevelId()!=-1){
							V3xOrgLevel currentMemberLevel = orgManager.getLevelById(currentMember.getOrgLevelId());
							int currentMemberLevelSortId = currentMemberLevel!=null ? currentMemberLevel.getLevelId() : 0;
							
							V3xOrgLevel memberLevel = this.orgManager.getLevelById(member.getOrgLevelId());
							int memberLevelSortId = memberLevel!=null ? memberLevel.getLevelId() : 0;

							int currentAccountLevelScope = this.orgManager.getAccountById(
									currentMember.getOrgAccountId()).getLevelScope();

							if ((currentMember.getOrgDepartmentId().equals(member
									.getOrgDepartmentId()))
									|| currentAccountLevelScope < 0) {
								return true;
							}

							if (currentMemberLevelSortId - memberLevelSortId <= currentAccountLevelScope) {
								return true;
							}
						}else{
							return true;//内部的所有人员都可以看到外部人员
						}
						
					}else{
						return false;//外部人员不能看到所有的内部人员
					}
				}
			}
			

			// 副岗在这个部门的有权限
			if (MemberHelper.isSndPostContainDept(currentMember, member.getOrgDepartmentId())) {
				return true;
			}
		} catch (Exception e) {
			log.warn("", e);
		}

		return false;
	}

	// cid为affairId
	public Map<String, Object> getProcessModeSelectorList(Long cid, Long uid)throws MobileException {
		Map<String, Object> result = new HashMap<String, Object>();
		List<ProcessModeSelector> processModeSelector = new ArrayList<ProcessModeSelector>();
		
		Affair affair = this.affairManager.getById(cid);
		String caseProcessXML;
		FlowData floData = null;
		try {
			floData = getFlowData(cid);
			caseProcessXML = floData!=null?floData.getXml():"";
		} catch (Exception e) {
			log.error("在移动接口实现中的得到的流程节点, 用在协同发起方法报错!", e);
			throw new MobileException(e);
		}
		BPMActivity activity;
		try {
			activity = ColHelper.getBPMActivityByAffair(affair);
		} catch (ColException e) {
			log.error("在移动接口实现中的得到的流程节点, 用在协同发起方法报错!", e);
			throw new MobileException(e);
		}
		if(activity == null){
			return result;
		}
		String currentNodeId = activity.getId();
		ColSummary summary = null;
		EdocSummary edocSummary = null;
		try {
			summary = colManager.getSimpleColSummaryById(affair.getObjectId());
		}catch (Exception e1) {
			throw new MobileException(e1);
		}
		if(summary==null){
			try {
				edocSummary = getEdocManager().getColAllById(affair.getObjectId());
			} catch (EdocException e1) {
				logger.error("", e1);
			}
		}
		long caseId = summary!=null?summary.getCaseId():edocSummary!=null?edocSummary.getCaseId():-1;
		boolean isFromTemplete = false;
		if(summary != null){
			isFromTemplete = summary.getTempleteId()!= null ;
		}else if(edocSummary != null){
			isFromTemplete = edocSummary.getTempleteId()!= null ;
		}
		WorkflowEventListener.ProcessModeSelector selector = new WorkflowEventListener.ProcessModeSelector();
		try {
			selector = ColHelper.preRunCase(caseProcessXML, currentNodeId, isFromTemplete, caseId);
		} catch (ColException e) {
			log.error("在移动接口实现中的得到的流程节点, 用在协同发起方法报错!", e);
			throw new MobileException(e);
		}
		List<NodeAddition> invalidateActivity = selector.getInvalidateActivity();
		if(invalidateActivity != null && !invalidateActivity.isEmpty()){ //存在不可用的节点，不让发
			//done by wangchw at 2011-11-7
			result.put("invalidateActivityMap", selector.invalidateActivityMap);
		}
		result.put("nodeTypes", selector.nodeTypes);
		if(null!=selector.nodeTypes){
			if(selector.nodeTypes.size()>0){//是否含有知会节点
				result.put("hasInformNode", "true");
			}else{
				result.put("hasInformNode", "false");
			}
		}
		
		List<WorkflowEventListener.NodeAddition> nodes = selector.getNodeAdditions();
		for (WorkflowEventListener.NodeAddition n : nodes) {
			ProcessModeSelector p = new ProcessModeSelector();
			p.setId(n.getNodeId());
			p.setName(n.getNodeName());
			p.setType(n.getProcessMode());
		
			List<WorkflowEventListener.PersonInfo> ps = n.getPeople();
			for (WorkflowEventListener.PersonInfo info : ps) {
				p.addMemberId(new Long(info.getId()), info.getName());
			}
		
			processModeSelector.add(p);
		}
		HashMap<String, String> hash = new HashMap<String, String>();
		BPMSeeyonPolicy seeyonPolicy = activity.getSeeyonPolicy();
		try {
			boolean isExecuteFinished= ColHelper.isExecuteFinished(floData.toBPMProcess(), affair) && !"inform".equals(seeyonPolicy.getId()) && !"zhihui".equals(seeyonPolicy.getId());
			if(isExecuteFinished){
				result.put("isExecuteFinished", "true");
			}else{
				result.put("isExecuteFinished", "false");
			}
			if(isExecuteFinished){
				ColHelper.findDirectHumenChildrenCondition(activity, hash);
			}
		} catch (ColException e1) {
			ColHelper.findDirectHumenChildrenCondition(ColHelper.getStartNode(caseProcessXML), hash);
		}
		if (hash.size() > 0) {
			Set<Map.Entry<String, String>> entry = hash.entrySet();
			List<String> keys = new ArrayList<String>();
			List<String> nodeNames = new ArrayList<String>();
			List<String> conditions = new ArrayList<String>();
			String[] temp = null;
			User user = CurrentUser.get();
			V3xOrgPost post = null;
			V3xOrgLevel level = null;
			try{
				post = this.orgManager.getBMPostByPostId(user.getPostId());
				level = this.orgManager.getLevelById(user.getLevelId());
			}catch(Exception e){
				log.error("", e);
			}
			StringBuffer sb = new StringBuffer();
			Map<String,Integer> conditionTypes = new HashMap<String,Integer>();
			for (Map.Entry<String, String> item : entry) {
				if (item.getValue() != null
						&& item.getValue().indexOf("↗") != -1) {
					sb.append(item.getKey() + "↗");
					keys.add(item.getKey());
					temp = StringUtils.split(item.getValue(), "↗");
					if (temp != null) {
						nodeNames.add(temp[0]);
						temp[1] = temp[1].replaceAll("Department",
								String.valueOf(user.getDepartmentId()))
								.replaceAll("Post",
										String.valueOf(user.getPostId()))
								.replaceAll("Level",
										String.valueOf(user.getLevelId()))
								.replaceAll("standardpost", post==null?"-1":post.getId().toString())
								.replaceAll("grouplevel", level==null||level.getGroupLevelId()==null?"-1":level.getGroupLevelId().toString())
								.replaceAll("handCondition", "false");
						conditions.add(temp[1]);
					}
				}
				if(item.getValue() != null && item.getValue().indexOf("handCondition") != -1){
					conditionTypes.put(item.getKey(),2);
				}else{
					conditionTypes.put(item.getKey(),0);
				}
			}
			result.put("allNodes", sb.toString()); // 所有的分支拼接成一个字符串,用:分隔
			result.put("keys", keys);
			result.put("names", nodeNames);
			result.put("conditions", conditions);
			result.put("conditionTypes", conditionTypes);
		}
		result.put("processModeSelector", processModeSelector);
		return result;
	}
    /**
     * 判断下一节点是否确认<br>
     * 如果需要选择节点处理人(false)，则移动端暂不允许处理
     * @throws MobileException 
     */
    public boolean isNextNodeUnsure(Long cid) throws ColException, MobileException {
        String caseProcessXML = null;
		try {
			caseProcessXML = getFlowData(cid).getXml();
		} catch (Exception e1) {
			logger.error("", e1);
		}
        String currentNodeId = null;
        ColSummary summary = null;
        try {
            Affair affair = this.affairManager.getById(cid);
            BPMActivity activity = ColHelper.getBPMActivityByAffair(affair);
            if(activity == null){
                return false;
            }
            currentNodeId = activity.getId();
            summary = colManager.getSimpleColSummaryById(affair.getObjectId());
        } catch (ColException e) {
            log.error("在移动接口实现中的得到的流程节点, 用在协同发起方法报错!", e);
            throw new MobileException(e);
        }

		long caseId = summary.getCaseId();
        
        WorkflowEventListener.ProcessModeSelector selector = ColHelper.preRunCase(caseProcessXML, currentNodeId, false, caseId);
        
        return selector.getMode()==2;
    }
    
	private FlowData getFlowData(Long cid) throws Exception {
		// cid为affairId
		Long summaryId = this.affairManager.getObjectIdByAffairId(cid);
		if (summaryId == Constants.GLOBAL_NULL_ID) {
			return null;
		}

		ColSummary summary = colManager.getColSummaryById(summaryId, false);
		
		String caseProcessXML = null;
		if (summary != null) {
			if (summary.getCaseId() != null) {
				long caseId = summary.getCaseId();
				caseProcessXML = colManager.getCaseProcessXML(caseId);
			} else if (StringUtils.isNotBlank(summary.getProcessId())) {
				String processId = summary.getProcessId();
				caseProcessXML = colManager.getProcessXML(processId);
			}
		}else {
			EdocSummary edocSummary = getEdocManager().getColAllById(summaryId);
			if(edocSummary!=null){
				if (edocSummary.getCaseId() != null) {
					long caseId = edocSummary.getCaseId();
					caseProcessXML = colManager.getCaseProcessXML(caseId);
				} else if (StringUtils.isNotBlank(edocSummary.getProcessId())) {
					String processId = edocSummary.getProcessId();
					caseProcessXML = colManager.getProcessXML(processId);
				}
			}
			
		}

		FlowData flowData = new FlowData();
		flowData.setDesc_by(FlowData.DESC_BY_XML);
		flowData.setXml(caseProcessXML);

		return flowData;
	}
	
	@SuppressWarnings("unchecked")
    public Map<ColOpinion,List<Attachment>> getOpinionAndAttachments(Long cid,Long uid)throws MobileException{
		Map<ColOpinion,List<Attachment>> map = new HashMap<ColOpinion,List<Attachment>>();
//		if (uid == null) {
//			uid = CurrentUser.get().getId();
//		}
		try {
			Affair affair = affairManager.getById(cid);
            if(affair == null){
                String msg=ColHelper.getErrorMsgByAffair(affair);
                throw new MobileException(msg);
            }
            else{
            	ColHelper.updateAffairStateWhenClick(affair, affairManager);            	
            }
			ColSummary summary = this.colManager.getColAllById(affair
					.getObjectId());
			Set<ColOpinion> allOpinions = summary.getOpinions();
			Set<ColOpinion> senderColOpinion = getSenderColOpinion(allOpinions);
			Object[] list = getColAttachment(summary.getId());
			List<Attachment> attList = (List<Attachment>)list[1];
			attList.addAll((List<Attachment>)list[2]);
			for(ColOpinion col : senderColOpinion){
				if(col!=null){
					List<Attachment> attachments = validateOpinionAndAttachment(cid,col,attList);
					if(attachments!=null){
						map.put(col, attachments);
					}
				}
			}
			return map;
		}catch (Exception e) {
			log.error("", e);
			throw new MobileException(e);
		}
	}
	
	private Set<ColOpinion> getSenderColOpinion(Set<ColOpinion> allOpinions){
		Set<ColOpinion> set = new HashSet<ColOpinion>();
		if(allOpinions!=null){
			for(ColOpinion col : allOpinions){
				if(col!=null&&col.getOpinionType()==OpinionType.senderOpinion.ordinal()){
					set.add(col);
				}
			}
		}
		return set;
	}
	private List<Attachment> validateOpinionAndAttachment(Long cid,ColOpinion col,List<Attachment> list){
		List<Attachment> listAttachment = new ArrayList<Attachment>();
		if(col!=null&&list!=null&&list.size()!=0){
			for(Attachment a : list){
				if(a!=null){
					if(a.getSubReference().equals(col.getId())){
						listAttachment.add(a);
					}
				}
			}
		}
		if(listAttachment.size()!=0){
			return listAttachment;
		}else{
			return null;
		}
	}
	
	private String[] getFormPolicy(Long cid)throws MobileException{
		 String[] formPolicy = null;
		 Affair affair = affairManager.getById(cid);
		 ColSummary summary = null;
		try {
			summary = this.colManager.getColAllById(affair.getObjectId());
		} catch (ColException e) {
			log.error("得到表单列表错误！", e);
			throw new MobileException(e);
		}
		 ColBody body = summary.getFirstBody();
		 if("FORM".equals(body.getBodyType())){
			 if(affair.getSubObjectId()==null){
				 BPMProcess xml = null;
				 if(summary.getCaseId()!=null)
					try {
						xml = ColHelper.getRunningProcessByCaseId(summary.getCaseId());
					} catch (ColException e) {
						log.error("得到表单列表错误！", e);
						throw new MobileException(e);
					}
				else if(summary.getProcessId()!=null)
					try {
						xml = ColHelper.getProcess(summary.getProcessId());
					} catch (ColException e) {
						log.error("得到表单列表错误！", e);
						throw new MobileException(e);
					}
	     		formPolicy = FormHelper.getFormPolicy(xml);
			 }else{
				 try {
					formPolicy = ColHelper.getFormPolicyByAffair(affair);
				} catch (ColException e) {
					log.error("得到表单列表错误！", e);
					throw new MobileException(e);
				}
			 }
		 }
		return formPolicy;
	}
	
	public MobileFormBean getFormAll(Long affairId,Long summaryId,User user) throws MobileException{
		ColSummary summary = null;
		try {
			summary = this.colManager.getColAllById(summaryId);
		} catch (ColException e) {
			log.error("移动取得summary！", e);
			throw new MobileException(e);
		}
		String[] formPolicy = getFormPolicy(affairId);
		String masterId = summary.getFirstBody().getContent();
		MobileFormBean formBean = null;
		try {
			Map<String,Object> formMap = FormHelper.getFormRunForMobile(user.getId(), user.getName(), user.getLoginName(), 
					 formPolicy[0], formPolicy[1], formPolicy[2], masterId, 
					summary.getId().toString(), affairId.toString(), formPolicy[3], false);
			formBean = new MobileFormBean();
			formBean.handle(formMap,true);
		} catch (MobileException e) {
			throw e; 
		}
		return formBean;
	}
	
	public Map<String, TIP_InputValueAll> getFormList(Long cid, boolean readOnly)throws MobileException {
		Affair affair = affairManager.getById(cid);
		User user = CurrentUser.get();
		MobileFormBean formBean = getFormAll(affair.getId(), affair.getObjectId(), user);
		return formBean.getFromApp();
	}

	public Map<String, Long> isHasOffset(Long cid, List<MobileForm> list) throws MobileException {
		
		// TODO Auto-generated method stub
		return null;
	}

	private void validateMark(String cid,Map<String,Object> map){
		if(map!=null){
			
			for (Iterator<Object> iter = map.values().iterator(); iter.hasNext();){
				TIP_InputValueAll tip = (TIP_InputValueAll) iter.next();
				if(tip!=null){
					if(tip.getType().equals(TFieldInputType.fitHandwrite)){
						if(tip.getAccess().equals("edit")){
							if(containMark!=null){
								containMark.put(cid, true);
							}else{
								containMark = new HashMap<String,Boolean>();
								containMark.put(cid, true);
							}
							this.setContainMark(containMark);
							break;
						}
					}else{
						continue;
					}
				}
			}
		}
	}
	public void processForm(Map<String,TIP_InputValueAll> objectMap, Long cid,Integer pass,String vouchPass) throws MobileException, SeeyonFormException {
		 Affair affair = affairManager.getById(cid);
	
		 ColSummary summary = null;
			try {
				summary = this.colManager.getColAllById(affair.getObjectId());
			} catch (ColException e) {
				log.error("得到表单列表错误！", e);
				throw new MobileException(e);
			}
			ColBody body = summary.getFirstBody();
			String masterId = body.getContent();
			String methodsign = pass==2?"auditPass":pass==1?"finishWorkItem":"";
			Map<String,Object> map =  new HashMap<String,Object>();
			Set<String> set = objectMap!=null? objectMap.keySet():null;
			if(set!=null){
				for(String str : set){
					map.put(str, objectMap.get(str));
				}
			}
			String operationType = null;
			//如果是关联表单的或子流程的不更新表单状态
            if(summary.getParentformSummaryId() !=null || (summary.getNewflowType()!=null && summary.getNewflowType() == Constant.NewflowType.child.ordinal())){
                operationType = "update";
                pass = null;
            }
            if(summary.getIsVouch() == null || summary.getIsVouch().equals(Constant.ColSummaryVouch.vouchDefault.getKey())){
	             Map<String, Object> columns = new HashMap<String, Object>();
	   	         int isVouch = Constant.ColSummaryVouch.vouchDefault.getKey();
	   	         if(vouchPass!= null){
	   	         	if(vouchPass.equals("1")){
	   	         		methodsign ="vouchPass";
	   				    isVouch = Constant.ColSummaryVouch.vouchPass.getKey();
	   	         	}else if(vouchPass.equals("2")){
	   	         		methodsign ="vouchBack";
	   	         		isVouch = Constant.ColSummaryVouch.vouchBack.getKey();
	   	         	}
	   	         }
	   	         columns.put("isVouch", isVouch);
	   	         colManager.update(summary.getId(), columns);
            }
           
		FormHelper.saveOrUpdateFormDataForMobile(CurrentUser.get().getId(), CurrentUser.get().getName(),
				CurrentUser.get().getLoginName(), String.valueOf(affair.getFormAppId()), String.valueOf(affair.getFormId()), String.valueOf(affair.getFormOperationId()), masterId, map, "submit", pass != null ? pass.toString():null, operationType, methodsign,vouchPass);
	}

	public String getFormName(Long cid) throws MobileException {
		String[] formPolicy = getFormPolicy(cid);
		
		 SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime
			.getInstance().getAppManager().findById(Long.parseLong(formPolicy[0]));
		 
		return fapp!=null?fapp.getAppName():null;
	}

	public Map<String,Object> getBranchLong(Map<String, TIP_InputValueAll> map,Long cid,String isForm) throws MobileException {
		 Affair affair = affairManager.getById(cid);
		 
		 HashMap<String,TIP_InputValueAll> tipMap = new HashMap<String,TIP_InputValueAll>();
		if(map!=null){
			for(String key : map.keySet()){
				tipMap.put(key, (TIP_InputValueAll)map.get(key));
			}
		}

		Map<String,Object> mapObj  = null;
		try {
			FlowData floData = null;
			try {
				floData = getFlowData(cid);
			} catch (Exception e) {
				log.error("在移动接口实现中的得到的流程节点, 用在协同发起方法报错!", e);
				throw new MobileException(e);
			}
			
			BPMActivity activity;
			try {
				activity = ColHelper.getBPMActivityByAffair(affair);
			} catch (ColException e) {
				log.error("在移动接口实现中的得到的流程节点, 用在协同发起方法报错!", e);
				throw new MobileException(e);
			}
			BPMSeeyonPolicy seeyonPolicy = activity.getSeeyonPolicy();
			Long formAppId=-1l;
			Long masterId=-1l;
			if (Strings.isNotBlank(isForm)) {
				String formAppIdStr = seeyonPolicy.getFormApp() ;
				ColSummary colSummary= colManager.getColAllById(affair.getObjectId());
				String masterIdStr= colSummary.getFirstBody().getContent();
			}
			try {
				if(ColHelper.isExecuteFinished(floData.toBPMProcess(), affair) && !"inform".equals(seeyonPolicy.getId()) && !"zhihui".equals(seeyonPolicy.getId())){
					mapObj  = ColHelper.getCondition(affair, tipMap, affair.getSenderId(), MobileConstants.getCurrentId(), orgManager,formAppId,masterId);
				}
			} catch (ColException e1) {
				mapObj  = ColHelper.getCondition(affair, tipMap, affair.getSenderId(), MobileConstants.getCurrentId(), orgManager,formAppId,masterId);
			}
		} catch (Exception e) {
			log.error("表单处理错误");
			throw new MobileException(e);
		}
		return mapObj;
	}

	public Long getModifyMember(String summeryId, String affairId)throws MobileException{
		User user = CurrentUser.get();
		
		Long lockObject = FormLockManager.add(Long.parseLong(summeryId),Long.parseLong(affairId),user.getId(), user.getLoginName(),user.getLoginTimestamp()==null?0:user.getLoginTimestamp().getTime()).getOwner();
		
		return lockObject;
	}

	public String getSummeryIdByAffairId(String cid) throws MobileException{
		 Affair affair = affairManager.getById(Long.parseLong(cid));
		 ColSummary summary = null;
			try {
				summary = this.colManager.getColAllById(affair.getObjectId());
			} catch (ColException e) {
				log.error("得到表单列表错误！", e);
				throw new MobileException(e);
			}
		return summary!=null?summary.getId().toString():null;
	}

	public void removeModifyMember(String summaryId) throws MobileException {
		if(Strings.isNotBlank(summaryId)){
			FormLockManager.remove(Long.parseLong(summaryId));
		}
	}

	public List<V3xOrgEntity> getOrgEntityByName(String className, String property, String value, Long accountId) throws MobileException {
		
		try {
			return orgManager.getEntity(className, property, value, accountId);
		
		} catch (BusinessException e) {
			log.error("得到V3xOrgEntity实体对象错误", e);
			throw new MobileException(e);
		}
	}

	public int getPendingEdocList(Long uid, int pageCounter, int pageNumber, List<AffairsListObject> pendingEdocList, String keyWorld) {
		
		try {
			//paginationHandle(pageCounter, pageNumber);
			
			List<Affair> affairLs = affairManager.queryPendingListOfEdoc(uid, null, SEARCH_CONDITIION.title.name(),keyWorld , null, false);
			for(Affair affair:affairLs){
				AffairsListObject obj = affairToAffairsListObject(affair);
				if(obj!=null){
					pendingEdocList.add(obj);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}

		if(pageNumber==1){
			
			return pendingEdocList!=null?pendingEdocList.size():-1;
		}else{
			return -1;
		}
	}
	private AffairsListObject affairToAffairsListObject(Affair objects){
		if(objects!=null && ( objects.getApp()!=ApplicationCategoryEnum.exSend.key() && objects.getApp()!=ApplicationCategoryEnum.exSign.key() && objects.getApp()!=ApplicationCategoryEnum.edocRegister.key())){
			AffairsListObject alo=new AffairsListObject();
			alo.setId(objects.getId());
			alo.setSummaryId(objects.getObjectId());
			alo.setTitle(objects.getSubject());
			alo.setType(objects.getApp());	
			alo.setSenderId(objects.getSenderId());
			alo.setSendTime(objects.getCreateDate());
			alo.setHasAttach(objects.isHasAttachments());
			return alo;
		}else{
			return null;
		}
	}
	
	public Edoc getPendingEdocObj(Affair aff) {
		EdocSummary summary=edocSummaryManager.findById(aff.getObjectId());
		Edoc e = this.EdocSummaryToEdoc(summary,aff.getId());
		e.setState(aff!=null?aff.getState():-1);
		e.setHasArchive(summary!=null?summary.getHasArchive():false);
		e.setAppType(aff.getApp());
		return e;
	}
	/**
	 * 得到公文的紧急程度
	 * @param urgnetValue
	 * @return
	 */
	private String getUrgentLevelEdoc(String urgnetValue){
		Map<String,String> urgentLevelMap = metadataManager.getMetadataItemLabelMap(MetadataNameEnum.edoc_urgent_level);
		if(urgentLevelMap!=null){
			String s = urgentLevelMap.get(urgnetValue);
			if(isVarchar(s)){
				return urgnetValue;
			}else{
				return s;
			}
		}else{
			return null;
		}
	}
	
	/**
	 * 得到公文的密级
	 * @param secretValue
	 * @return
	 */
	private String getSecretLevelEdoc(String secretValue){
		Map<String,String> urgentLevelMap = metadataManager.getMetadataItemLabelMap(MetadataNameEnum.edoc_secret_level);
		if(urgentLevelMap!=null){
			String s = urgentLevelMap.get(secretValue);
			if(isVarchar(s)){
				return secretValue;
			}else{
				return s;
			}
			
		}else{
			return null;
		}
	}
	/**
	 * 判断一个字符串是否是由字母组成的
	 * @param str
	 * @return
	 */
	private boolean isVarchar(String str){
		if(str!=null){
			String pattern = "[A-Za-z.]*";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(str);
			return m.matches();
		}else{
			return false;
		}
	}
	private Edoc EdocSummaryToEdoc(EdocSummary s,Long affairId){
		Edoc e=new Edoc();
		e.setTitle(s.getSubject());
		
		e.setEdocType(s.getEdocType());
		e.setCreateDate(s.getCreateTime());
		e.setCreateUserId(s.getStartUserId());
		String docMark = s.getDocMark();
		if(docMark!=null){
			e.setEdocNumber(docMark);
		}
		String serialNo = s.getSerialNo();
		if(serialNo!=null){
			e.setInNumber(serialNo);
		}
		String secretLevel = s.getSecretLevel();
		
		if(secretLevel!=null){
			String se = getSecretLevelEdoc(secretLevel);
			e.setSecretType(se!=null?se:"");
		}
		String urgentLevel = s.getUrgentLevel();
		if(urgentLevel!=null){
			String u = getUrgentLevelEdoc(urgentLevel);
			e.setEmergentType(u!=null?u:"");
		}
		e.setEdocId(s.getId());
		e.setAffairid(affairId);
		List<Attachment> atts = getEdocAtts(s.getId());
		e.setHasAtts((atts!=null && atts.size()!=0)?true:false);
		e.setAttsNum(atts!=null?atts.size():0);
		String nodePermissionPolicy="shenpi";
		try {
			nodePermissionPolicy = getEdocManager().getPolicyByAffair(affairManager.getById(affairId));
		} catch (EdocException e1) {
			log.error("", e1);
		}
		MetadataNameEnum edocTypeEnum=EdocUtil.getEdocMetadataNameEnum(s.getEdocType());
		V3xOrgMember sender = null;
		try {
			sender = orgManager.getMemberById(s.getStartUserId());
		}
		catch (BusinessException e1) {
		}
		Long flowPermAccountId = EdocHelper.getFlowPermAccountId(s, sender.getOrgAccountId(), templeteManager);
		Map<String, List<String>> actionMap = permissionManager.getActionMap(edocTypeEnum.name(), nodePermissionPolicy, flowPermAccountId);
		
		List<String> baseActions = actionMap.get("basic");
        List<String> advancedActions = actionMap.get("advanced");
        List<String> commonActions = actionMap.get("common");
        
        List<String> actions = new ArrayList<String>();
        if(baseActions != null){
        	actions.addAll(baseActions);
        }
        if(advancedActions != null){
        	actions.addAll(advancedActions);
        }
        if(commonActions != null){
        	actions.addAll(commonActions);
        }
        
        Permission permission = permissionManager.getPermission(edocTypeEnum.name(), nodePermissionPolicy, CurrentUser.get().getAccountId());
    	if(permission!=null){
    		NodePolicy policy = permission.getNodePolicy();
    		if(policy!=null){
    			e.setCanAttitude(policy.getAttitude()!= 3);
    			e.setOpinionPolicy(policy.getOpinionPolicy());
    		}
    	}
    	
    	Set<EdocBody> sb=s.getEdocBodies();
		if(sb != null && !sb.isEmpty()){
			EdocBody eb=sb.iterator().next();
			e.setEdocBody(eb);
		}
        for(String str : actions){
        	if("EdocExchangeType".equals(str)){//若有交换类型，则不能对移动公文进行处理
        			e.setCanProcess(false);
        	}
        	if("Opinion".equals(str)){//可以填写意见
        		e.setCanOpinion(true);
        	}
        	if("Attitude".equals(str)){//可以填写态度
        		e.setCanAttitude(true);
        	}
        	if("ContinueSubmit".equals(str)){
        		e.setCanSubmit(true);//可以提交
        	}
        	if("Comment".equals(str)){
        		e.setCanComment(true);//可以暂存待办
        	}
        }
		return e;
	}
	public String getPendingEdocObjContent(Long uid, Long edocId) {
		String content;
		EdocSummary summary=edocSummaryManager.findById(edocId);
		if(null!=summary.getEdocBodies())
		{
			Set<EdocBody> sb=summary.getEdocBodies();
			EdocBody eb=sb.iterator().next();
			try {
				content = StrExtractor.getText(eb.getContentType(), eb.getContent(), eb.getCreateTime());
				return content;
			} catch (UnknowBodyTypeException e) {
				
				log.error("", e);
				return null;
			}
		}
		else
		{
			return null;
		}
	}


	public Map<String,Object> getPendingEodcItemList(Long uid,Long edocId) {
		EdocSummary summary=edocSummaryManager.findById(edocId!=null?edocId:0L);
		
		Map<Long,Map<String,String>> mapSystemMetadata = new HashMap<Long,Map<String,String>>();
		Map<Long,Map<String,String>> mapUserMetadata = new HashMap<Long,Map<String,String>>();
		
		Map<String,Map> mapMetadata = new HashMap<String,Map>();
		
		EdocFormManager edocFormManager = (EdocFormManager)ApplicationContextHolder.getBean("edocFormManager");
		List<EdocFormElement> efeLs=edocFormManager.getEdocFormElementByFormId(summary.getFormId());
		List<EdocElement> edocElements = new ArrayList<EdocElement>();
		for(EdocFormElement efe:efeLs){
			String elementId = "";
			if(efe.getElementId()<10&&efe.getElementId()>0){
				elementId = "00"+String.valueOf(efe.getElementId());
			}else{
				if(efe.getElementId()<100 && efe.getElementId()>=10){
					elementId = "0"+String.valueOf(efe.getElementId());
				}else{
					elementId = elementId+efe.getElementId();
				}
			}
			EdocElement e=edocElementManager.getEdocElement(elementId);
			
			if(e!=null && e.getType()!=EdocElement.C_iElementType_LogoImg&&e.getType()!=EdocElement.C_iElementType_Comment){
				edocElements.add(e);
				if(e.getMetadataId()!=null){
					//系统枚举
					Metadata metadataSystem = metadataManager.getMetadata(e.getMetadataId());
					if(metadataSystem!=null){
						List<MetadataItem> items = metadataSystem.getItems();
						Map<String,String> itemMap = new HashMap<String,String>();
						if(items!=null){
							for(MetadataItem item : items){
								itemMap.put(item.getValue(), item.getLabel());
							}
						}
						mapSystemMetadata.put(e.getId(), itemMap);
					}
					//自定义枚举
					Metadata metadataUser = metadataManager.getUserMetadata(e.getMetadataId());
					if(metadataUser!=null){
						List<MetadataItem> items = metadataUser.getItems();
						Map<String,String> itemMap = new HashMap<String,String>();
						if(items!=null){
							for(MetadataItem item : items){
								itemMap.put(item.getValue(), item.getLabel());
							}
						}
						mapUserMetadata.put(e.getId(), itemMap);
					}
				}
			}
		}
		
		mapMetadata.put("system", mapSystemMetadata);
		mapMetadata.put("user", mapUserMetadata);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", edocElements);
		map.put("map", mapMetadata);
		return map;
	}
	/**
	  *  将该节点的处理意见写入对应的公文绑定意见
	  * @param actiontype 处理态度（1 提交，2 暂存待办）
	  * @param affairId 事项的id
	  * @param opinion 处理意见
	  * @param attitude （1已阅  2 同意 3 不同意）
	  * @param flowchart 流程选择的人员（key – nodeId value-人员）
	  * @param conditionNodes 分支流程选择的项
	  */

	public void processEdoc(int actiontype, Long affairId, String opinion, int attitude, Map<String, String[]> flowchart, Map<String, String> conditionNodes,boolean track) 
	{
		//提交的
		User user = CurrentUser.get();
		Affair affair=affairManager.getById(affairId);
		long summaryId = affair.getObjectId();
		EdocSummary summary=edocSummaryManager.findById(summaryId);
		String processId = summary.getProcessId();
		EdocOpinion signOpinion = new EdocOpinion();
		try {
			BPMProcess process = ColHelper.getCaseProcess(summary.getProcessId());
			BPMActivity activity = ColHelper.getBPMActivityByAffair(process, affair);
			if(activity != null){ //发起者不需要
				String currentNodeId = activity.getId();
				signOpinion.setNodeId(Long.parseLong(currentNodeId));
			}
			signOpinion.setPolicy(activity.getSeeyonPolicy().getId());
		} catch (ColException e1) {
			log.error(e1.getMessage(),e1);
		}
        signOpinion.setCreateUserId(affair.getMemberId());
        signOpinion.setAttribute(Integer.valueOf(attitude).intValue());
        signOpinion.setIdIfNew();
        signOpinion.setContent(opinion);
        signOpinion.setIsHidden(false);
        signOpinion.setCreateTime(new Timestamp(System.currentTimeMillis()));
        signOpinion.setEdocSummary(summary);
        signOpinion.setAffairId(affairId);
        signOpinion.affairIsTrack = track;
        
		if(actiontype==1)
		{
	        boolean isRelieveLock = true;
		        signOpinion.setOpinionType(OpinionType.signOpinion.ordinal());
		        signOpinion.isPipeonhole=false;
		        //设置代理人信息
		        if(user.getId()!= affair.getMemberId())
		        {
		        	signOpinion.setProxyName(user.getName());
		        }
		        try {
		        	getEdocManager().finishWorkItem(summary, affairId, signOpinion,
						flowchart, conditionNodes, processId, String
								.valueOf(user.getId()), null);
					//operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.edoc, 
					//		EactionType.LOG_EDOC_RED_FORM, EactionType.LOG_EDOC_RED_FORM_DESCRIPTION, user.getName(), summary.getSubject());
				} catch (EdocException e) {
					log.error("",e);
				}
				finally
				{
					if(isRelieveLock)
		        		ColLock.getInstance().removeLock(summaryId);
				}
		}
		else
		{
	        boolean isRelieveLock = true;
	        try{
		        signOpinion.setOpinionType(OpinionType.provisionalOpinoin.ordinal());
		        signOpinion.isPipeonhole=true;
		        getEdocManager().zcdb(summary, affair, signOpinion, processId, user.getId()+"");
	        }catch(EdocException e){
	        	log.error("", e);
	        }finally{
	        	if(isRelieveLock)
	        		ColLock.getInstance().removeLock(summaryId);
	        }
		}
	}

	public EdocSummaryManager getEdocSummaryManager() {
		return edocSummaryManager;
	}

	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}

	public EdocElementManager getEdocElementManager() {
		return edocElementManager;
	}

	public void setEdocElementManager(EdocElementManager edocElementManager) {
		this.edocElementManager = edocElementManager;
	}

	public EdocManager getEdocManager() {
		if(edocManager==null){
			edocManager = (EdocManager) ApplicationContextHolder.getBean("edocManager");
		}
		return edocManager;
	}

/*	public void setEdocManager(EdocManager edocManager) {
		this.edocManager = edocManager;
	}*/

	public Map<String, String> flowChartParam(Long affairId) {
		Map<String, String> map = new HashMap<String,String>();
		Long summaryId = this.affairManager.getObjectIdByAffairId(affairId);
		ColSummary summary = null;
		EdocSummary edocSummary = null;
		Long caseId = null;
		String processId = null;
		try {//协同
			summary = colManager.getColSummaryById(summaryId, false);
		} catch (ColException e) {
			log.error("",e);
		}
		if(summary!=null){
			caseId = summary.getCaseId();
			processId = summary.getProcessId();
		}else{
			edocSummary = edocSummaryManager.findById(summaryId);
			if(edocSummary!=null){
				caseId = edocSummary.getCaseId();
				processId = edocSummary.getProcessId();
			}
		}
		map.put("summaryId", String.valueOf(summaryId));
		map.put("caseId", String.valueOf(caseId));
		map.put("processId", processId);
		return map;
	}
	public OperationlogManager getOperationlogManager() {
		return operationlogManager;
	}

	public List<Attachment> getEdocAtts(Long summaryId) {
		if(summaryId!=null){
			List<Attachment> newList = new ArrayList<Attachment>();
			List<Attachment> list = attachmentManager.getByReference(summaryId, summaryId);
			if(list!=null){
				for(Attachment att : list){
					if(att.getType()!=MobileConstants.ATTACHMENT_TYPE.IMAGE.ordinal())
					if(att.getType()!=MobileConstants.ATTACHMENT_TYPE.DOCUMENT.ordinal() || ApplicationCategoryEnum.edoc.name().equals(att.getMimeType())){
						newList.add(att);
					}
				}
			}
			return newList;
		}
		return null;
	}


	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}

	public int getDoneOfEdocs(Long memberId, ApplicationCategoryEnum app, int state) {
		
		List<EdocSummaryModel> queryList0 = null;
		List<EdocSummaryModel> queryList1 = null;
		List<EdocSummaryModel> queryList2 = null;
		try {
			queryList0 = this.edocManager.queryFinishedList(0);
			queryList1 = this.edocManager.queryFinishedList(1);
			 queryList2 = this.edocManager.queryFinishedList(2);
		} catch (EdocException e) {
			log.error("", e);
		}
		
		List<EdocSummaryModel> queryList = new ArrayList<EdocSummaryModel>();
		queryList.addAll(queryList0);
		queryList.addAll(queryList1);
		queryList.addAll(queryList2);
		
		return queryList!=null?queryList.size():0;
	}

	public int getDoneEdocList(Long uid, int pageCounter, int pageNumber, List<AffairsListObject> doneEdocList, String keyWorld) {
		
		try {
			
			List<EdocSummaryModel> queryList0  = null;
			List<EdocSummaryModel> queryList1  = null;
			List<EdocSummaryModel> queryList2  = null;
			if(Strings.isNotBlank(keyWorld)){
				queryList0 = this.edocManager.queryByCondition(0,Strings.isNotBlank(keyWorld)?SEARCH_CONDITIION.subject.name():"", keyWorld, "",  StateEnum.col_done.key());
				queryList1 = this.edocManager.queryByCondition(1,Strings.isNotBlank(keyWorld)?SEARCH_CONDITIION.subject.name():"", keyWorld, "",  StateEnum.col_done.key());
				queryList2 = this.edocManager.queryByCondition(2,Strings.isNotBlank(keyWorld)?SEARCH_CONDITIION.subject.name():"", keyWorld, "",  StateEnum.col_done.key());
			}else{
				queryList0 = edocManager.queryFinishedList(0);
				queryList1 = edocManager.queryFinishedList(1);
				queryList2 = edocManager.queryFinishedList(2);
			}
		
			doneEdocList.addAll(setDoneEdocList(queryList0));
			doneEdocList.addAll(setDoneEdocList(queryList1));
			doneEdocList.addAll(setDoneEdocList(queryList2));
			
		} catch (Exception e) {
			log.error("", e);
		}

		return doneEdocList.size();
	}
	
	private List<AffairsListObject> setDoneEdocList(List<EdocSummaryModel> queryList){
		List<AffairsListObject> s = new ArrayList<AffairsListObject>();
		if(queryList!=null){
			for(EdocSummaryModel edoc : queryList){
				Affair affair = affairManager.getById(edoc.getAffairId());
				AffairsListObject obj = affairToAffairsListObject(affair);
				if(obj!=null){
					obj.setDealTiem(affair.getCompleteTime());
					s.add(obj);
				}
			}
		}
		return s;
	}

	public List<EdocItem> getEdocOpinion(Long edocId,Long affariId) {
		List<EdocItem> l = new ArrayList<EdocItem>();
		
		EdocSummary summary=edocSummaryManager.findById(edocId);
		Affair affair = affairManager.getById(affariId);
		Set <EdocOpinion> st=summary.getEdocOpinions();
		
		List<EdocFormElement> edocElements = edocFormManager.getEdocFormElementByFormId(summary.getFormId());
		List <Attachment> tempAtts=attachmentManager.getByReference(edocId);
		Hashtable <Long,List<Attachment>> attHas=com.seeyon.v3x.common.filemanager.manager.Util.sortBySubreference(tempAtts);
		List<EdocElement> ees = new ArrayList<EdocElement>();
		boolean hasNiWen = false;
		for(EdocFormElement e : edocElements){
			EdocElement ee = edocElementManager.getEdocElementsById(e.getElementId());
			if(ee.getType()==EdocElement.C_iElementType_Comment){
				ees.add(ee);
				if("niwen".equals(ee.getFieldName())){
					hasNiWen = true;
				}
			}
		}
		
		if(null!=st){
			Iterator<EdocOpinion> it=st.iterator();
			while(it.hasNext()){
				EdocOpinion eo=it.next();
				if(eo!=null && eo.getOpinionType()!=OpinionType.provisionalOpinoin.ordinal()){
					EdocItem ei=new EdocItem();
					ei.setOpinion(true);
					ei.setEdocType(affair.getApp());
					if("niwen".equals(eo.getPolicy())||eo.getPolicy()==null){
						ei.setSenderOpinion(hasNiWen);
					}
					ei.setKey(eo.getPolicy());
					setPolicy(summary,eo.getPolicy(),ei);
					ei.setValue(eo.getContent());
					ei.setAttitude(eo.getAttribute());
					ei.setProcessDate(eo.getCreateTime());
					ei.setProcesser(eo.getCreateUserId());
					ei.setOpinionType(eo.getOpinionType());
					ei.setOpinionAttachments(attHas.get(eo.getId()));
					isCanSee(summary.getId(),eo.getPolicy(),ei,eo.getCreateUserId());
					l.add(ei);
				}
			}
		}
		return l;
	}
	
	private void isCanSee(Long edocId,String policy,EdocItem ei,Long mid){
		boolean isSet = false;
		List <V3xHtmDocumentSignature> list = htmlHandWriteManager.getHandWrites(edocId);
		if(list!=null&&list.size()!=0){
			for(V3xHtmDocumentSignature v : list){
				String name = this.getMemberById(mid).getName();
				if(policy!=null&&("hw"+policy).equals(v.getFieldName())&&v.getUserName().equals(name)){
					ei.setDisplay(false);
					isSet = true;
				}
			}
			if(!isSet){
				ei.setDisplay(true);
			}
		}else{
			ei.setDisplay(true);
		}
	}

	/**
	 * 设置节点权限是否是系统的 (移动公文)
	 * @param summary
	 * @param policy
	 * @param ei
	 */
	private void setPolicy(EdocSummary summary,String policy,EdocItem ei){
		int type = summary.getEdocType();
		String configCategory = type==0?MetadataNameEnum.edoc_send_permission_policy.toString():type==1?MetadataNameEnum.edoc_rec_permission_policy.toString():type==3?MetadataNameEnum.edoc_qianbao_permission_policy.toString():"";
		try {
			FlowPerm flow = flowPermManager.getFlowPerm(configCategory,policy,CurrentUser.get().getAccountId());
			if(flow!=null && flow.getType()==1){
				ei.setSystem(false);
			}else{
				if(flow!=null && flow.getType()==0){
					ei.setSystem(true);
				}
			}
		} catch (Exception e1) {
			log.error("",e1);
		}
	}

	public List<MobileOrgEntity> getMobileOrgEntity(Long id) {
		List<MobileOrgEntity> list = new ArrayList<MobileOrgEntity>();
		if(id!=null){
			if(id != V3xOrgEntity.VIRTUAL_ACCOUNT_ID){
				List<V3xOrgDepartment> departments = this.getDepartmentSubordinate(id);
				List<V3xOrgMember> members = this.getMemberByDepartment(id);
				list.addAll(getEntitysByDepartment(departments));
				list.addAll(getEntitysByMember(members));
			}else{
				try {
					List<V3xOrgAccount> accountsList = orgManager.accessableAccounts(MobileConstants.getCurrentId());
					list.addAll(getAllAccount(accountsList));
				} catch (BusinessException e) {
					log.error("移动取得所有单位报错", e);
				}
			}
		}
		return list;
	}
	private List<MobileOrgEntity> getAllAccount(List<V3xOrgAccount> list){
		List<MobileOrgEntity> entitys = new ArrayList<MobileOrgEntity>();
		if(list!=null){
			for(V3xOrgAccount d : list){
				MobileOrgEntity e = new MobileOrgEntity();
				e.setId(d.getId());
				e.setName(d.getName());
				e.setType(2);
				entitys.add(e);
			}
		}
		return entitys;
	}
	private List<MobileOrgEntity> getEntitysByDepartment(List<V3xOrgDepartment> list){
		List<MobileOrgEntity> entitys = new ArrayList<MobileOrgEntity>();
		if(list!=null){
			for(V3xOrgDepartment d : list){
				MobileOrgEntity e = new MobileOrgEntity();
				e.setId(d.getId());
				e.setName(d.getName());
				e.setType(0);
				entitys.add(e);
			}
		}
		return entitys;
	}
	
	private List<MobileOrgEntity> getEntitysByMember(List<V3xOrgMember> list){
		List<MobileOrgEntity> entitys = new ArrayList<MobileOrgEntity>();
		if(list!=null){
			for(V3xOrgMember d : list){
				MobileOrgEntity e = new MobileOrgEntity();
				e.setId(d.getId());
				e.setName(d.getName());
				e.setType(1);
				e.setTelNum(d.getTelNumber());
				entitys.add(e);
			}
		}
		return entitys;
	}

	public EdocSummary getEodcSummaryById(Long id) {
		if(id!=null){
			EdocSummary summary=edocSummaryManager.findById(id);
			return summary;
		}
		return null;
	}

	public Map<String,Object> getEdocPolicyName(Long affairId,Long summaryId) {
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(affairId!=null && summaryId!=null){
			Affair affair = affairManager.getById(affairId);
			EdocSummary summary = edocSummaryManager.findById(summaryId);
			try {
				String nodePermissionPolicy = edocManager.getPolicyByAffair(affair);
				
				MetadataNameEnum edocTypeEnum=EdocUtil.getEdocMetadataNameEnum(summary.getEdocType());
				MetadataItem tempMitem=null;
				Long accountId=orgManager.getMemberById(summary.getStartUserId()).getOrgAccountId();
				FlowPerm fpm=flowPermManager.getFlowPerm(edocTypeEnum.name(), nodePermissionPolicy, accountId);
				
				if(fpm.getType()==com.seeyon.v3x.flowperm.util.Constants.F_type_system){
    				tempMitem=metadataManager.getMetadataItem(edocTypeEnum, nodePermissionPolicy);
    			}
				map.put("nodePermissionPolicy", nodePermissionPolicy);
				map.put("tempMitem", tempMitem);
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return map;
	}
	
	public Map<String,Object> getCollPolicyName(Long affairId,Long summaryId)throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(affairId!=null && summaryId!=null){
			Affair affair = affairManager.getById(affairId);
			ColSummary summary = this.colManager.getColAllById(summaryId);
			try {
				//modify by wangchw 2011-11-7
				//String nodePermissionPolicy = edocManager.getPolicyByAffair(affair);
				String nodePermissionPolicy = this.colManager.getPolicyByAffair(affair);
				MetadataNameEnum edocTypeEnum=MetadataNameEnum.col_flow_perm_policy;
				MetadataItem tempMitem=null;
				Long accountId=orgManager.getMemberById(summary.getStartMemberId()).getOrgAccountId();
				FlowPerm fpm=flowPermManager.getFlowPerm(edocTypeEnum.name(), nodePermissionPolicy, accountId);
				
				if(fpm.getType()==com.seeyon.v3x.flowperm.util.Constants.F_type_system){
    				tempMitem=metadataManager.getMetadataItem(edocTypeEnum, nodePermissionPolicy);
    			}
				map.put("nodePermissionPolicy", nodePermissionPolicy);
				map.put("tempMitem", tempMitem);
			} catch (Exception e) {
				log.error("", e);
			}
		}
		return map;
	}

	public String getLevelNameByMember(V3xOrgMember member) {
		if(member!=null){
			try {
				V3xOrgLevel level = orgManager.getLevelById(member.getOrgLevelId());
				
				return level!=null?level.getName():"";
			} catch (BusinessException e) {
				log.error("", e);
			}
		}
		return "";
	}

	public String getPostNameByMember(V3xOrgMember member) {
		if(member!=null){
			try {
				V3xOrgPost post = orgManager.getPostById(member.getOrgPostId());
				
				return post!=null?post.getName():"";
			} catch (BusinessException e) {
				log.error("", e);
			}
		}
		return "";
	}

	public int getMobileMessageList(List<MobileHistoryMessage> mobileMessageList,Long memberId, String content,String type,int pageNum,int pageCounter) {
		paginationHandle(pageCounter, pageNum);
		
		List<UserHistoryMessage>  messageList = null;
		 if ("0".equals(type)){// 系统消息
				try {
					messageList = userMessageManager.getAllSystemMessages(memberId, Strings.isNotBlank(content)?"messageContent":null, Strings.isNotBlank(content)?content:null, null);
				} catch (MessageException e) {
					logger.error("", e);
				}
			}
			else { // 个人信息
				try {
					messageList = userMessageManager.getAllPersonMessages(memberId, Strings.isNotBlank(content)?"messageContent":null, Strings.isNotBlank(content)?content:null, null);
				} catch (MessageException e) {
					logger.error("", e);
				}
			}
		 
			if(messageList!=null){
				for(UserHistoryMessage m : messageList){
					MobileHistoryMessage mm = new MobileHistoryMessage();
					mm.setContent(m.getMessageContent());
					mm.setReceiverId(m.getReceiverId());
					mm.setSenderId(m.getSenderId());
					mm.setSendTime(m.getCreationDate());
					mm.setType(m.getMessageCategory());
					mobileMessageList.add(mm);
				}
			}
			
			
		return Pagination.getRowCount();
	}
	
	public void setMessageReadedState(Long memberId,int msgTypte){
		userMessageManager.setMessageReadedSate(memberId, msgTypte);
	}

	public boolean isExchangeEdoc() {
		try {
			V3xOrgRole accountRole = orgManager.getRoleByName(EdocRoleHelper.acountExchangeRoleName);
			Long accountRoleId = accountRole!=null?accountRole.getId():null;
			boolean accountExchange = accountRoleId!=null?orgManager.isInDomain(CurrentUser.get().getAccountId(), accountRoleId, CurrentUser.get().getId()):false;
			
			V3xOrgRole departmentRole = orgManager.getRoleByName(EdocRoleHelper.departmentExchangeRoleName);
			Long departmentRoleId = departmentRole!=null?departmentRole.getId():null;
			boolean departmentExchange = departmentRoleId!=null?orgManager.isInDomain(CurrentUser.get().getDepartmentId(), departmentRoleId, CurrentUser.get().getId()):false;
			
			return accountExchange||departmentExchange;
			
		} catch (BusinessException e) {
			logger.error("", e);
		}
		
		return false;
	}

	public List<MobileBookEntity> getAllOutTeam(Long createrId) {
		List<MobileBookEntity> entitys = new ArrayList<MobileBookEntity>();
		List<AddressBookTeam> listTeam = addressBookManager.getTeamsByCreatorId(createrId);
		List<AddressBookMember>  listMember = addressBookManager.getMembersByTeamId(new Long(-1));
		if(listTeam!=null){
			for(AddressBookTeam m : listTeam){
				MobileBookEntity b = new MobileBookEntity();
				b.setName(m.getName());
				b.setId(m.getId());
				b.setType(0);
				entitys.add(b);
			}
		}
		if(listMember!=null){
			for(AddressBookMember m : listMember){
				MobileBookEntity b = new MobileBookEntity();
				b.setName(m.getName());
				b.setId(m.getId());
				b.setType(1);
				entitys.add(b);
			}
		}
		
		return entitys;
	}
	
	public MobileBookEntity getMobileBookMember(Long id){
		if(id!=null){
			AddressBookMember m = addressBookManager.getMember(id);
			if(m!=null){
				MobileBookEntity mm = new MobileBookEntity();
				mm.setAccountName(m.getCompanyName());
				mm.setEmail(m.getEmail());
				mm.setId(m.getId());
				mm.setMobileNum(m.getMobilePhone());
				mm.setName(m.getName());
				mm.setFamilyNum(m.getFamilyPhone());
				mm.setOfficeNum(m.getCompanyPhone());
				mm.setPost(m.getCompanyPost());
				mm.setDepartmentName(m.getCompanyDept());
				mm.setFamilyAddress(m.getAddress());
				mm.setFaxNumber(m.getFax());
				mm.setPostCode(m.getPostcode());
				
				
				return mm;
			}
		}
		return null;
	}

	public String getZCDBOpinion(Long summaryId) {
		List<EdocOpinion> list = new ArrayList<EdocOpinion>();
		if(summaryId!=null){
			EdocSummary summary=edocSummaryManager.findById(summaryId);
			if(summary!=null){
				Set <EdocOpinion> st=summary.getEdocOpinions();
				if(st!=null){
					Iterator<EdocOpinion> it=st.iterator();
					while(it.hasNext()){
						EdocOpinion eo=it.next();
						if(eo!=null && eo.getOpinionType()==OpinionType.provisionalOpinoin.ordinal()&& eo.getCreateUserId()==CurrentUser.get().getId()){
							list.add(eo);
						}
					}
				}
			}
			int size = list!=null?list.size():0;
			if(list!=null && size!=0){
				EdocOpinion index0 = list.get(0);
				for(int i=0;i<size;i++){
					EdocOpinion indexi = list.get(i);
					if(i>0 && index0!=null && indexi!=null && index0.getCreateTime().before(indexi.getCreateTime())){
						index0 = indexi;
					}
				}
				
				return index0.getContent();
			}
		}
		return null;
	}

	public List<MobileBookEntity> showTeamMembers(Long teamId) {
		List<MobileBookEntity> list = new ArrayList<MobileBookEntity>();
		if(teamId!=null){
			List<AddressBookMember> members = addressBookManager.getMembersByTeamId(teamId);
			if(members!=null){
				for(AddressBookMember a : members){
					MobileBookEntity mm = new MobileBookEntity();
					mm.setName(a.getName());
					mm.setId(a.getId());
					mm.setType(1);
					list.add(mm);
				}
			}
			return list;
		}
		return null;
	}

	public MobileBookEntity showTeamName(Long teamId) {
		if(teamId!=null){
			AddressBookTeam team = addressBookManager.getTeam(teamId);
			
			MobileBookEntity e = new MobileBookEntity();
			e.setName(team.getName());
			e.setId(team.getId());
			e.setType(0);
			return e;
		}
		return null;
	}

	public void removeAttachmentById(Long attId) {

		if(attId!=null){
			attachmentManager.deleteById(attId);
		}
	}
	
	public Map<String,Object> getEdocBratch(Long edocId,Long affairId){
		
		Map<String,Object> mapObj = new HashMap<String,Object>();
		
		Affair affair = affairManager.getById(affairId);
		EdocSummary summary=edocSummaryManager.findById(edocId!=null?edocId:0L);
		EdocFormManager edocFormManager = (EdocFormManager)ApplicationContextHolder.getBean("edocFormManager");
		List<EdocFormElement> efeLs=edocFormManager.getEdocFormElementByFormId(summary.getFormId());
		Map<String,String> edocMap = new HashMap<String,String>();
		
		if(efeLs!=null){
			for(EdocFormElement e : efeLs){
				
				String elementId = "";
				if(e.getElementId()<10&&e.getElementId()>0){
					elementId = "00"+String.valueOf(e.getElementId());
				}else{
					if(e.getElementId()<100 && e.getElementId()>=10){
						elementId = "0"+String.valueOf(e.getElementId());
					}else{
						elementId = elementId+e.getElementId();
					}
				}
				EdocElement element =edocElementManager.getEdocElement(elementId);
				
				if(element!=null && element.getType()>=0 && element.getType()<=5){
					
					String fieldName = element.getFieldName();
					
					Method method;
					try {
						method = summary.getClass().getMethod("get"+(getFormString(fieldName)));
						if(method!=null){
							Object o = method.invoke(summary, null);
							String value=o!=null?o.toString():"";
							edocMap.put(fieldName, value);
						}
					} catch (Exception e1) {
						logger.error("移动公文发射机制取值问题",e1);
						edocMap.put(fieldName, "");
					}
					
				}
			}
		}
		try {
			mapObj = ColHelper.getCondition(affair, edocMap, affair.getSenderId(), MobileConstants.getCurrentId(), orgManager,-1l,-1l);
		} catch (ColException e) {
			logger.error("", e);
		}
		return mapObj;
	}
	
	private String getFormString(String s){
		String str = "";
		if(s!=null){
			String[] ss = s.split("_");
			for(String sm : ss){
				if(sm.equals("keyword")){
					sm = "keywords";
				}else if(sm.equals("createdate")){
					sm = "createTime";
				}else if(sm.equals("packdate")){
					sm = "packTime";
				}else{
					String pattern = "string[0-9]*";
					Pattern p = Pattern.compile(pattern);
					Matcher m = p.matcher(sm);
					if(m.matches()){
						sm =  "varchar"+sm.substring(6);
					}
				}
				str = str+sm.substring(0,1).toUpperCase()+sm.substring(1);
			}
		}
		return str;
	}

	
	public Long getCollSummaryIdByAffairId(Long affairId) {
		
		return affairManager.getObjectIdByAffairId(affairId);
	}
	
	public void setPageCount(Integer pageCount){
		MobileConstants.PAGE_COUNT = pageCount;
	}
	
	public List<List<MobileHistoryMessage>> findUnReadMessage(Long userId){
		List<Object[]> ls = new ArrayList<Object[]>();
		Integer count= MessageState.getInstance().getState(userId);
		if(count >0){
			try {
				ls = userMsgDao.getUnresolvedMessages(userId);
			} catch (MessageException e) {
				log.error("查询最新消息-移动应用",e);
			}
		}
		MessageState.getInstance().setNoMessageState(userId);
		List<List<MobileHistoryMessage>> result = new ArrayList<List<MobileHistoryMessage>>();
		List<MobileHistoryMessage> onlineMessage = new ArrayList<MobileHistoryMessage>();
		List<MobileHistoryMessage> sysMessage = new ArrayList<MobileHistoryMessage>();
		splitMessage(ls, onlineMessage, sysMessage);
		result.add(onlineMessage);
		result.add(sysMessage);
		return result;
	}
	//将最新消息拆分为在线交流消息和系统消息
	private void splitMessage(List<Object[]> allMessage,List<MobileHistoryMessage> onlineMessage,List<MobileHistoryMessage> sysMessage){
		if(allMessage != null){
			String content = null;
			for (Object[] message : allMessage) {
				MobileHistoryMessage msg = new MobileHistoryMessage();
				Long id = (Long)message[0];
				content = message[3].toString();
				msg.setSenderId((Long)message[1]);
				msg.setSendTime((Date)message[5]);
				Integer messageType = (Integer) message[2];
				msg.setType(messageType);
				msg.setId(id);
				String identifier = message[6].toString();
				msg.setContent(content);
				if(messageType == com.seeyon.v3x.common.usermessage.Constants.UserMessage_TYPE.PERSON.ordinal()){
					boolean isHasAttachments = IdentifierUtil.lookupInner(identifier, com.seeyon.v3x.common.usermessage.Constants.INENTIFIER_INDEX.HAS_ATTACHMENTS.ordinal(), '1');
					if(isHasAttachments){
						List<Attachment> attachment = this.attachmentManager.getByReference(id, id);
						msg.addAttachments(attachment);
						//将消息中的附件内容给去掉。
						content = content.substring(0, content.lastIndexOf(":"));
						content = content.substring(0, content.lastIndexOf(" "));
						msg.setContent(content);
					}
					onlineMessage.add(msg);
				}else{
					sysMessage.add(msg);
				}
			}
		}
	}

	public void setUserMsgDao(UserMessageDAO userMsgDao) {
		this.userMsgDao = userMsgDao;
	}
	
	public void setSupMaxSizeFile(String length){
		if(Strings.isNotBlank(length)){
			MobileConstants.supMaxSizeFile = Long.parseLong(length)*Strings.ONE_KB;
		}
	}

	public void setProcessLogManager(ProcessLogManager processLogManager) {
		this.processLogManager = processLogManager;
	}

	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}
	
	@Override
	public Map<String, Object> getProcessModeSelectorList(Long cid,
			Long currentId, Map<String, String[]> fieldValueMap) throws MobileException {
		Map<String, Object> result = new HashMap<String, Object>();
		List<ProcessModeSelector> processModeSelector = new ArrayList<ProcessModeSelector>();
		
		Affair affair = this.affairManager.getById(cid);
		String caseProcessXML;
		FlowData floData = null;
		try {
			floData = getFlowData(cid);
			caseProcessXML = floData!=null?floData.getXml():"";
		} catch (Exception e) {
			log.error("在移动接口实现中的得到的流程节点, 用在协同发起方法报错!", e);
			throw new MobileException(e);
		}
		BPMActivity activity;
		try {
			activity = ColHelper.getBPMActivityByAffair(affair);
		} catch (ColException e) {
			log.error("在移动接口实现中的得到的流程节点, 用在协同发起方法报错!", e);
			throw new MobileException(e);
		}
		if(activity == null){
			return result;
		}
		String currentNodeId = activity.getId();
		ColSummary summary = null;
		EdocSummary edocSummary = null;
		try {
			summary = colManager.getSimpleColSummaryById(affair.getObjectId());
		}catch (Exception e1) {
			throw new MobileException(e1);
		}
		if(summary==null){
			try {
				edocSummary = getEdocManager().getColAllById(affair.getObjectId());
			} catch (EdocException e1) {
				logger.error("", e1);
			}
		}
		long caseId = summary!=null?summary.getCaseId():edocSummary!=null?edocSummary.getCaseId():-1;
		boolean isFromTemplete = false;
		if(summary != null){
			isFromTemplete = summary.getTempleteId()!= null ;
		}else if(edocSummary != null){
			isFromTemplete = edocSummary.getTempleteId()!= null ;
		}
		WorkflowEventListener.ProcessModeSelector selector = new WorkflowEventListener.ProcessModeSelector();
		try {
			BPMProcess process = BPMProcess.fromXML(caseProcessXML);
			selector = ColHelper.preRunCase(process, currentNodeId, isFromTemplete, caseId,fieldValueMap);
		} catch (ColException e) {
			log.error("在移动接口实现中的得到的流程节点, 用在协同发起方法报错!", e);
			throw new MobileException(e);
		}
		List<NodeAddition> invalidateActivity = selector.getInvalidateActivity();
		if(invalidateActivity != null && !invalidateActivity.isEmpty()){ //存在不可用的节点，不让发
			//done by wangchw at 2011-11-7
			result.put("invalidateActivityMap", selector.invalidateActivityMap);
		}
		result.put("nodeTypes", selector.nodeTypes);
		if(null!=selector.nodeTypes){
			if(selector.nodeTypes.size()>0){//是否含有知会节点
				result.put("hasInformNode", "true");
			}else{
				result.put("hasInformNode", "false");
			}
		}
		
		List<WorkflowEventListener.NodeAddition> nodes = selector.getNodeAdditions();
		for (WorkflowEventListener.NodeAddition n : nodes) {
			ProcessModeSelector p = new ProcessModeSelector();
			p.setId(n.getNodeId());
			p.setName(n.getNodeName());
			p.setType(n.getProcessMode());
		
			List<WorkflowEventListener.PersonInfo> ps = n.getPeople();
			for (WorkflowEventListener.PersonInfo info : ps) {
				p.addMemberId(new Long(info.getId()), info.getName());
			}
		
			processModeSelector.add(p);
		}
		HashMap<String, String> hash = new HashMap<String, String>();
		BPMSeeyonPolicy seeyonPolicy = activity.getSeeyonPolicy();
		try {
			boolean isExecuteFinished= ColHelper.isExecuteFinished(floData.toBPMProcess(), affair) && !"inform".equals(seeyonPolicy.getId())&& !"zhihui".equals(seeyonPolicy.getId());
			if(isExecuteFinished){
				result.put("isExecuteFinished", "true");
			}else{
				result.put("isExecuteFinished", "false");
			}
			if(isExecuteFinished){
				ColHelper.findDirectHumenChildrenCondition(activity, hash);
			}
		} catch (ColException e1) {
			ColHelper.findDirectHumenChildrenCondition(ColHelper.getStartNode(caseProcessXML), hash);
		}
		if (hash.size() > 0) {
			Set<Map.Entry<String, String>> entry = hash.entrySet();
			List<String> keys = new ArrayList<String>();
			List<String> nodeNames = new ArrayList<String>();
			List<String> conditions = new ArrayList<String>();
			String[] temp = null;
			User user = CurrentUser.get();
			V3xOrgPost post = null;
			V3xOrgLevel level = null;
			try{
				post = this.orgManager.getBMPostByPostId(user.getPostId());
				level = this.orgManager.getLevelById(user.getLevelId());
			}catch(Exception e){
				log.error("", e);
			}
			StringBuffer sb = new StringBuffer();
			Map<String,Integer> conditionTypes = new HashMap<String,Integer>();
			for (Map.Entry<String, String> item : entry) {
				if (item.getValue() != null
						&& item.getValue().indexOf("↗") != -1) {
					sb.append(item.getKey() + "↗");
					keys.add(item.getKey());
					temp = StringUtils.split(item.getValue(), "↗");
					if (temp != null) {
						nodeNames.add(temp[0]);
						temp[1] = temp[1].replaceAll("Department",
								String.valueOf(user.getDepartmentId()))
								.replaceAll("Post",
										String.valueOf(user.getPostId()))
								.replaceAll("Level",
										String.valueOf(user.getLevelId()))
								.replaceAll("standardpost", post==null?"-1":post.getId().toString())
								.replaceAll("grouplevel", level==null||level.getGroupLevelId()==null?"-1":level.getGroupLevelId().toString())
								.replaceAll("handCondition", "false");
						conditions.add(temp[1]);
					}
				}
				if(item.getValue() != null && item.getValue().indexOf("handCondition") != -1){
					conditionTypes.put(item.getKey(),2);
				}else{
					conditionTypes.put(item.getKey(),0);
				}
			}
			result.put("allNodes", sb.toString()); // 所有的分支拼接成一个字符串,用:分隔
			result.put("keys", keys);
			result.put("names", nodeNames);
			result.put("conditions", conditions);
			result.put("conditionTypes", conditionTypes);
		}
		result.put("processModeSelector", processModeSelector);
		return result;
	}
}