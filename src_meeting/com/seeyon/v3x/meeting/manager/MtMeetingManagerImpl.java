package com.seeyon.v3x.meeting.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.seeyon.cap.calendar.domain.CalContentCAP;
import com.seeyon.cap.calendar.domain.CalEventCAP;
import com.seeyon.cap.calendar.manager.CalEventManagerCAP;
import com.seeyon.cap.indexInterface.IndexUtil;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.meeting.domain.MtMeetingCAP;
import com.seeyon.cap.resource.manager.ResourceManagerCAP;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.Partition;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.filemanager.manager.PartitionManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.quartz.QuartzListener;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.index.share.datamodel.Accessory;
import com.seeyon.v3x.index.share.datamodel.AuthorizationInfo;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.dao.MtConfereeDao;
import com.seeyon.v3x.meeting.dao.MtMeetingDao;
import com.seeyon.v3x.meeting.domain.MtConferee;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.domain.MtReply;
import com.seeyon.v3x.meeting.domain.MtSummaryTemplate;
import com.seeyon.v3x.meeting.util.Constants;
import com.seeyon.v3x.meeting.util.MeetingAgent;
import com.seeyon.v3x.meeting.util.MeetingMsgHelper;
import com.seeyon.v3x.meeting.util.MeetingSearchModel;
import com.seeyon.v3x.meeting.util.TimeJob;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;

/**
 * 会议的Manager的实现类
 * @author wolf
 */
public class MtMeetingManagerImpl extends BaseMeetingManager implements MtMeetingManager,IndexEnable {
	private static final Log log = LogFactory.getLog(MtMeetingManagerImpl.class);
	private MtMeetingDao mtMeetingDao;
	private MtConfereeDao mtConfereeDao;
	private AttachmentManager attachmentManager;
	private FileManager fileManager;
	private MtResourcesManager mtResourcesManager;
	private MtSummaryTemplateManager mtSummaryTemplateManager;
	private AffairManager affairManager;
	private OrgManager orgManager;
	private ResourceManagerCAP resourceManagerCAP;
	private PartitionManager partitionManager;
	private CalEventManagerCAP calEventManagerCAP;
	private IndexManager indexManager;
	private MtReplyManager replyManager;
	private static final String MeetingResources = "com.seeyon.v3x.meeting.resources.i18n.MeetingResources";
	
	public void setCalEventManagerCAP(CalEventManagerCAP calEventManagerCAP) {
		this.calEventManagerCAP = calEventManagerCAP;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	public void setResourceManagerCAP(ResourceManagerCAP resourceManagerCAP) {
		this.resourceManagerCAP = resourceManagerCAP;
	}
	public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	public void setMtMeetingDao(MtMeetingDao MtMeetingDao) {
		this.mtMeetingDao = MtMeetingDao;
	}
	public void setMtConfereeDao(MtConfereeDao mtConfereeDao) {
		this.mtConfereeDao = mtConfereeDao;
	}
	public void setReplyManager(MtReplyManager replyManager) {
		this.replyManager = replyManager;
	}
	/**
	 * 删除会议，包含了删除会议正文、会议附件、与会人员的待办事项记录、占用资源时间段清空、删除任务调度及删除会议等行为
	 */
	public void delete(Long id) {
		MtMeeting meeting = this.getById(id);
		if (meeting.contentIsWordOrExcel()) {
			try {
				fileManager.deleteFile(meeting.getId(), meeting.getCreateDate(), true);
			} catch (BusinessException e) {
				log.error("删除会议正文错误", e);
			}
		}
		
		try {
			attachmentManager.deleteByReference(meeting.getId(), meeting.getId());
		} catch (BusinessException e) {
			log.error("删除会议附件错误", e);
		}
		
		resourceManagerCAP.delResourceIppByAppId(id);
		mtResourcesManager.deleteByMeetingId(id);
		affairManager.deleteByObject(ApplicationCategoryEnum.meeting, id);
		mtMeetingDao.getSessionFactory().getCurrentSession().flush();
		this.deleteQuartzTask(meeting);
		mtMeetingDao.deleteObject(meeting);
	}

	public void deletes(List<Long> ids) {
		for (Long id : ids) {
			delete(id);
		}
	}

	/**
	 * 初始化与会人员的中文显示名称
	 */
	private void initPublishScope(MtMeeting data) {
		try {
			List<V3xOrgEntity> confereeList = orgManager.getEntities(data.getConferees());
			StringBuffer names = new StringBuffer();
			if(CollectionUtils.isNotEmpty(confereeList)){
				for(V3xOrgEntity entity : confereeList){
					if(entity != null && entity.isValid()){
						names.append(entity.getName() + ",");
					}
				}
				data.setConfereesNames(names.substring(0, names.lastIndexOf(",")));
			}
		} catch (Exception e) {
			log.error("初始化与会人员错误", e);
		}
	}

	/**
	 * 初始化会议 1、初始化创建用户姓名
	 * @param template
	 */
	private void initTemplate(MtMeeting template) {
//		template.setAttachmentsFlag(attachmentManager.hasAttachments(template.getId(), template.getId()));
		template.setAttachmentsFlag(template.isHasAttachments());
		template.setCreateUserName(this.getMeetingUtils().getMemberNameByUserId(template.getCreateUser()));
		template.setEmceeName(this.getMeetingUtils().getMemberNameByUserId(template.getEmceeId()));
		template.setRecorderName(this.getMeetingUtils().getMemberNameByUserId(template.getRecorderId()));
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		Long createAccountId = null;
		
		try {
			V3xOrgMember createUser = orgManager.getMemberById(template.getCreateUser());
			createAccountId = createUser.getOrgAccountId();
		} catch (BusinessException e) {
			log.error("获取会议创建者异常", e);
		}
		
		if(!accountId.equals(createAccountId)){
			template.setAccountName(this.getMeetingUtils().getAccountNameByCreateUserId(template.getCreateUser()));
		}
		
		this.initPublishScope(template);
	}

	@SuppressWarnings("unchecked")
	public List<MtMeeting> findAll() {
		DetachedCriteria dc = getDetachedCriteraByUser();
		dc.addOrder(Order.desc("beginDate"));
		List<MtMeeting> list = mtMeetingDao.executeCriteria(dc);
		// bug 8931 8939 initList(list);
		return list;
	}
	
	public List<MtMeeting> findByProperty(String property, Object value) {
		List<MtMeeting> list;
		list = findByPropertyNoInit(property, value);
		//bug 8931 8939 去掉initList(list);
		//initList(list);
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<MtMeeting> findByPropertyNoInit(String property, Object value) {
		DetachedCriteria dc = getDetachedCriteraByUser();

		if (value instanceof String) {
			dc.add(Restrictions.like(property, (String) value, MatchMode.ANYWHERE));
		} else {
			dc.add(Restrictions.eq(property, value));
		}
		dc.addOrder(Order.desc("beginDate"));
		return mtMeetingDao.executeCriteria(dc);
	}
	
	/**
	 * 获取用户创建的已归档会议记录，辅助解决此前无分页的问题
	 */
	@SuppressWarnings("unchecked")
	private List<MtMeeting> findPigeonholeMeetings4NeedCount(Long userId) {
		String hql = "from MtMeeting as mt where mt.state=? and mt.createUser=? order by mt.beginDate desc";
		return mtMeetingDao.find(hql, null, Constants.DATA_STATE_PIGEONHOLE, userId);
	}
	
	private int getTotalCount4Pigeonhole(Long userId) {
		String hql = "select count(mt.id) from MtMeeting as mt where mt.state=? and mt.createUser=?";
		return (Integer)mtMeetingDao.findUnique(hql, null,Constants.DATA_STATE_PIGEONHOLE, userId);
	}
	
	/**
	 * 判断当前用户是否仍在与会人员中，如果当前用户为会议创建者、主持人或记录人，或在有效与会范围中，则返回true，否则返回false<br>
	 * 如果当前会议选择了所属项目，且用户为该项目成员，也表明在与会范围中可以看到
	 * @param userId
	 * @param meetingId
	 * @param projectId
	 */
	public boolean isStillInConferees(Long userId, MtMeeting bean) {
		if (bean == null) {
			return false;
		}
		
		if(bean.getState() == Constants.DATA_STATE_SAVE)
			return bean.getCreateUser().equals(userId);
		
		List<Long> agentToIds = MemberAgentBean.getInstance().getAgentToMemberId(ApplicationCategoryEnum.meeting.key(), userId);
		
		String hql = "select count(distinct mt.id) from " + MtMeeting.class.getName() + " as mt, " + MtConferee.class.getName() + " as mc " +
				"where mt.id=mc.meetingId and mt.id=:meetingId and " +
				"(mc.confereeId in (:domainIds) or mt.createUser=:userId or mt.emceeId=:userId or mt.recorderId=:userId";
		Map<String, Object> params = new HashMap<String, Object>();
		// 被代理人为主持人或记录人，代理人也可查看会议
		if(CollectionUtils.isNotEmpty(agentToIds)) {
			hql += " or mt.emceeId in (:agentToIds) or mt.recorderId  in (:agentToIds)";
			params.put("agentToIds", agentToIds);
		}
		hql += ")";
		params.put("meetingId", bean.getId());
		
		List<Long> domainIds = CommonTools.getSumCollection(this.getDomainIds(userId), this.getAgentDomainIds());
		params.put("domainIds", domainIds);
		params.put("userId", userId);
		boolean inConferee = (Integer)this.mtMeetingDao.findUnique(hql, params) > 0;
		
		Long projectId = bean.getProjectId();
		if(projectId != null && projectId != com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID) {
			String hql2 = "select count(pm.id) from ProjectMember as pm where pm.projectSummary.id=? and pm.memberid=?";
			boolean inProject  = (Integer)this.mtMeetingDao.findUnique(hql2, null, new Object[]{projectId, userId}) > 0;
			return inConferee || inProject;
		} else {
			return inConferee;
		}
	}
	
	/**
	 * 获取被代理人的domainId集合，用于代理人查看被代理人有权查看的内容范围匹配
	 * @param agentedIds
	 */
	private Set<Long> getAgentDomainIds() {
		List<AgentModel> ags = Constants.getMeetingAgents();
		if(CollectionUtils.isNotEmpty(ags)) {
			Set<Long> result = new HashSet<Long>();
			for(AgentModel ag : ags) {
				if(ag != null && ag.getAgentToId() != null)
					CommonTools.addAllIgnoreEmpty(result, this.getDomainIds(ag.getAgentToId()));
			}
			return result;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private DetachedCriteria getDetachedCriteraByUser() {
		DetachedCriteria dc = DetachedCriteria.forClass(MtMeeting.class);
		User user = CurrentUser.get();
		DetachedCriteria replyDc = DetachedCriteria.forClass(MtReply.class);
		Criterion argUid = Restrictions.eq("userId", user.getId());
		replyDc.add(argUid);
		List<MtReply> replyList = mtMeetingDao.executeCriteria(replyDc,-1,-1);
		List<Long> mtIdList = new ArrayList<Long>();
		
		for(MtReply reply : replyList ){
			if(!mtIdList.contains(reply.getMeetingId())){
				mtIdList.add(reply.getMeetingId());
			}			
		}
		
		Criterion arg1 = Restrictions.eq("createUser", CurrentUser.get().getId());     //等于创建者的
		Criterion arg2 = Restrictions.eq("emceeId", user.getId());		   //或者是主持人的
		arg2 = Restrictions.or(arg2,Restrictions.eq("recorderId", user.getId()));      //或者是记录人的
		arg2 = Restrictions.and(arg2,Restrictions.ne("state", Constants.DATA_STATE_SAVE));    //状态不等于草稿的
		//yangzd
		arg2 = Restrictions.and(arg2,Restrictions.ne("state", Constants.DATA_STATE_PIGEONHOLE));    //状态不等于已归档的
		if(mtIdList!=null && mtIdList.size()>0){
			arg2 = Restrictions.or(arg2,Restrictions.in("id", mtIdList));              //与会人的会议列表中存在的
		}
		arg1 = Restrictions.or(arg1, arg2);
		arg1 = Restrictions.and(arg1, Restrictions.ge("state",Constants.DATA_STATE_SAVE));   //并且状态>=10的是已发送的  暂存的也显示
		dc.add(arg1);
		return dc;
	}
	
	@SuppressWarnings("unchecked")
	private DetachedCriteria getDetachedCriteraByUserforAgent() {
		DetachedCriteria dc = DetachedCriteria.forClass(MtMeeting.class);
		User user = CurrentUser.get();
		DetachedCriteria replyDc = DetachedCriteria.forClass(MtReply.class);
		Criterion argUid = Restrictions.eq("userId", user.getId());
		Criterion attend = Restrictions.eq("feedbackFlag", Constants.FEEDBACKFLAG_ATTEND);
		Criterion unattd = Restrictions.eq("feedbackFlag", Constants.FEEDBACKFLAG_UNATTEND);
		Criterion agent=Restrictions.or(attend, unattd);
		replyDc.add(argUid).add(agent);
		List<MtReply> replyList = mtMeetingDao.executeCriteria(replyDc,-1,-1);
		List<Long> mtIdList = new ArrayList<Long>();
		
		for(MtReply reply : replyList ){
			mtIdList.add(reply.getMeetingId());
		}
		
		Criterion arg1 = Restrictions.eq("createUser", CurrentUser.get().getId());     //等于创建者的
		Criterion arg2 = Restrictions.eq("emceeId", user.getId());		   //或者是主持人的
		arg2 = Restrictions.or(arg2,Restrictions.eq("recorderId", user.getId()));      //或者是记录人的
		arg2 = Restrictions.and(arg2,Restrictions.ne("state", Constants.DATA_STATE_SAVE));    //状态不等于草稿的
		if(mtIdList!=null && mtIdList.size()>0){
			arg2 = Restrictions.or(arg2,Restrictions.in("id", mtIdList));              //与会人的会议列表中存在的
		}
		arg1 = Restrictions.or(arg1, arg2);
		arg1 = Restrictions.and(arg1, Restrictions.ge("state",Constants.DATA_STATE_SAVE));   //并且状态>=10的是已发送的  暂存的也显示
		dc.add(arg1);
		return dc;
	}
	
	public MtMeeting getById(Long id) {
		MtMeeting template = mtMeetingDao.get(id);
		if(template==null){
			return null;
		}
		this.initTemplate(template);
		return template;
	}

	public MtMeeting getByMtId(Long id) {
		return mtMeetingDao.get(id);
	}

	/**
	 * 保存会议，并为与会者生成个人待办事项，生成任务调度（会前或准时提醒、会议开始时将会议状态更新为已开始、会议结束后将资源清空），保存与会资源等
	 * @param meeting 待保存的会议
	 * @return 保存后的会议
	 */
	public MtMeeting save(MtMeeting meeting) throws BusinessException {
		String oper = meeting.isNew() ? "save" : "update";
		if (meeting.isNew()) {
			meeting.setIdIfNew();
			mtMeetingDao.save(meeting);
			mtMeetingDao.getHibernateTemplate().flush();
	        try{
	        	if(Constants.DATA_STATE_SEND == meeting.getState() && meeting.getEndDate().getTime()>System.currentTimeMillis()){
	        		assignedMeeting(meeting, oper);
	        	}
	        } catch(Exception e){
	        	log.error("将会议写入个人事项时报错", e);
	        }
		} else {
			mtMeetingDao.update(meeting);
			mtMeetingDao.getHibernateTemplate().flush();
	        try{
	        	if(Constants.DATA_STATE_SEND==meeting.getState()) {
	        		if(meeting.beginAndEndTimeGreaterThanNow())
	        			assignedMeeting(meeting, oper);
	        		else
	        			affairManager.deleteByObject(ApplicationCategoryEnum.meeting, meeting.getId());
	        	}
	        }catch(Exception e){
	        	log.error("更新会议的个人事项时报错", e);
	        }
		}
		
		if(Constants.DATA_STATE_SEND == meeting.getState()) {
			//判断会议发起人是否在主持人或者或者记录人或者与会人
			//如果在，则生成日程事件，否则，不生成。
			boolean flag = true;
			String[] confereesNames = meeting.getConfereesNames().split("、");
			
			if(!(meeting.getEmceeName().equals(meeting.getCreateUserName()) || meeting.getRecorderName().equals(meeting.getCreateUserName())))
					flag = false;
			if(!flag){
				for(int i=0; i<confereesNames.length;i++){
					if(confereesNames[i].equals(meeting.getCreateUserName())){
						flag = true;
						break;
					}else{
						flag = false;
					}
				}
			}
			if(flag)
				this.createCalEvent(meeting, meeting.getCreateUser());
		}
		
		if(Constants.DATA_STATE_SEND==meeting.getState()) {
			if(meeting.isRemindFlag() && meeting.getBeforeTime()!=null) {
				if("save".equals(oper) || "true".equals(meeting.getExt1())) {
					this.setQuartzTask(Constants.TASK_TYPE.remindConferees, meeting, oper);
				}
				if("save".equals(oper) || "true".equals(meeting.getExt2())) {
					if(meeting.getBeforeTime()!=-1) {
						this.setQuartzTask(Constants.TASK_TYPE.update2Start, meeting, oper);
					} else { //准时提醒时，在提醒时也将会议状态更新了，专用于更新会议为开始状态的线程无需使用
						this.deleteUpdate2StartTask(meeting.getId());
					}
				}
			} else if("save".equals(oper) || "true".equals(meeting.getExt2())){
				//会议可能由有提前提醒变为无提前提醒，此时需将之前的提醒任务调度取消
				if("update".equals(oper)) { 
					this.deleteRemindTask(meeting.getId());
				}
				this.setQuartzTask(Constants.TASK_TYPE.update2Start, meeting, oper);
			}
			
			if("save".equals(oper) || "true".equals(meeting.getExt3()))
				this.setQuartzTask(Constants.TASK_TYPE.clearResources, meeting, oper);
		}
		
		this.mtResourcesManager.deleteByMeetingId(meeting.getId());
		this.mtResourcesManager.saveMtResources4Meeting(meeting);
		MtMeetingCAP mtMeetingCAP = new MtMeetingCAP();
		BeanUtils.convert(mtMeetingCAP, meeting);
		this.resourceManagerCAP.saveOrUpdateImpropriateResources4Meeting(mtMeetingCAP, oper);
		return meeting;
	}
	
	/**
	 * 用于比较发送会议时，在修改前后与任务调度相关的属性是否发生了变化，辅助任务调度设置<br>
	 * 以便在任务启动时间没有发生变化时，不做删除任务再重新生成任务的无谓操作<br>
	 * 检查三项属性的变化情况，如果发生变化，向会议的备用字段中写入标识值"true"：<br>
	 * 1.<b>会前提醒时间</b>是否发生变化，对应任务：提醒与会人员，标识字段：ext1；<br>
	 * 2.<b>会议开始时间</b>是否发生变化，对应任务：将会议状态改为"已开始"，标识字段：ext2；<br>
	 * 3.<b>会议结束时间</b>是否发生变化，对应任务：清空与会资源，将会议状态改为"已结束"，标识字段：ext3；<br>
	 * @param oldMt  修改前的会议，传入时不会为空，状态为新建、暂存或已发起未召开
	 * @param newMt  修改后的会议，传入时不会为空，状态为已发起未召开
	 */
	public void checkIfFields4QuartzChanged(MtMeeting oldMt, MtMeeting newMt) {
		if(oldMt.getState()==null || oldMt.getState()==Constants.DATA_STATE_SAVE) {  //发送新建或暂存的会议
			newMt.setExt1(newMt.isRemindFlag() + "");
			newMt.setExt2("true");
			newMt.setExt3("true");
		} else {  //修改已发送未召开的会议
			//修改前后均提醒，但提醒时间可能有变动
	        if(oldMt.isRemindFlag() && oldMt.getBeforeTime()!=null && newMt.isRemindFlag() && newMt.getBeforeTime()!=null)
	    		newMt.setExt1(!newMt.getRemindTime().equals(oldMt.getRemindTime()) + "");
	        //修改前不提醒，修改后提醒，反之，提醒任务会被取消
	        if(!oldMt.isRemindFlag() && newMt.isRemindFlag())
	        	newMt.setExt1("true");
	        
	    	newMt.setExt2(!newMt.getBeginDate().equals(oldMt.getBeginDate()) + "");
	        newMt.setExt3(!newMt.getEndDate().equals(oldMt.getEndDate()) + "");
		}
		if(log.isDebugEnabled())
			log.debug(newMt.getTitle() + " 修改前后的结果：" + newMt.getExt1() + ", " + newMt.getExt2() + ", " + newMt.getExt3());
	}
	
	private void deleteTask(Long meetingId, Constants.TASK_TYPE taskType) {
		try {
			Scheduler sched = QuartzListener.getScheduler();
			sched.deleteJob(meetingId + taskType.getJobName(), meetingId + taskType.getGroupName());
		} catch (SchedulerException e) {
			log.error("清除会议提前提醒任务调度出现异常：", e);
		}
	}
	
	/**
     * 发起人发起会议时，自动为其增加日程事件，与会人回执参加时，自动为其增加日程事件
     * @param bean		会议
     * @param userId	人员ID
     */
    public void createCalEvent(MtMeeting bean, Long userId) {
    	CalEventCAP calEvent = calEventManagerCAP.isHasCalEventByAppId(bean.getId(), ApplicationCategoryEnum.meeting.getKey(), userId);
        if(calEvent == null) {
        	calEvent = new CalEventCAP();
        }
    	
    	//设置日程对象
        calEvent.setAlarmDate(bean.getBeginDate().getTime() - 1800);
        calEvent.setAlarmFlag(false);
        calEvent.setBeginDate(bean.getBeginDate()); 
        calEvent.setEndDate(bean.getEndDate()); 
        calEvent.setCompleteRateInt(0);
        calEvent.setCreateDate(new Date());
        calEvent.setCreateUserId(userId); 
        calEvent.setEventType(1); // 事件类型 ：自建
        calEvent.setPriorityType(2);// 优先级类型：2.中
        calEvent.setRealEstimateTime(0F);// 实际完成时间
        calEvent.setShareType(1);// 共享类型：1.私人事件
        calEvent.setSignifyType(1);// 重要程度：1.重要紧急
        calEvent.setStates(2);// 事件完成类型： 2.已安排
        calEvent.setSubject(bean.getTitle());// 题目
        calEvent.setWorkType(1);// 工作类型：1.自办
        calEvent.setUpdateDate(new Date());// 更新时间
        calEvent.setEventflag(1);// 事件当前类型标识：1.已安排
        calEvent.setFromId(bean.getId()); // 应用ID：会议ID
        calEvent.setFromType(ApplicationCategoryEnum.meeting.getKey()); // 应用类型：会议
        calEvent.setCalEventType(0);//默认为业务
        calEvent.setBeforendAlarm(0L);
        //设置日程正文
        CalContentCAP calContent = new CalContentCAP();
        calContent.setContent("");
        calContent.setContentType(bean.getDataFormat());
        calContent.setCreateDate(bean.getCreateDate());
        try {
	        Long eventId = calEventManagerCAP.saveOrUpdateCalEventFromOtherApp(calEvent, calContent, userId);
	        IndexInfo index = calEventManagerCAP.getIndexInfo(eventId);
	        indexManager.index(index);
        } catch(Exception e) {
        	log.error("为当前用户[ID=" + userId + "]生成或更新会议对应日程事件出错: ", e);
        }
    }
    
    /**
     * 删除指定用户由会议自动转发的日程事件
     * @param meetingId 	会议ID
     * @param userId		对应的人员ID
     */
    public boolean deleteCalEvent(Long meetingId, Long userId)  {
        try {
        	calEventManagerCAP.deleteCalEventFromOtherAppId(meetingId, ApplicationCategoryEnum.meeting.getKey(), userId);
        } catch (Exception e) {
            log.error("删除会议转发的日程事件出错: ", e);
            return false;
        }
        return true;
    }
	
	/**
	 * 如果会议修改时将提前提醒去掉，此时需清除提前提醒任务调度
	 * @param meetingId
	 */
	private void deleteRemindTask(Long meetingId) {
		this.deleteTask(meetingId, Constants.TASK_TYPE.remindConferees);		
	}
	
	/**
	 * 清楚会议到达开始时间时，更新会议状态为已开始的任务调度
	 * @param meetingId
	 */
	private void deleteUpdate2StartTask(Long meetingId) {
		this.deleteTask(meetingId, Constants.TASK_TYPE.update2Start);
	}
	
	/**
	 * 会议任务调度设置：<br>
	 * 1.提前提醒或准时提醒，给与会人员发送系统消息；<br>
	 * 2.会议开始时，将会议状态更新为"已开始"<br>
	 * 3.会议结束时，将会议状态更新为"已结束"并清空占用资源时段<br>
	 * @param taskType  任务类型：提醒、更新会议状态(会议开始时)、清空会议资源(会议结束时)
	 * @param meeting   会议
	 * @param oper      修改("update")还是新建会议("save")，修改时，如有必要，先将之前的任务取消，再生成新的任务
	 */
	private void setQuartzTask(Constants.TASK_TYPE taskType, MtMeeting meeting, String oper) {
		Long meetingId = meeting.getId();
		String jobName = null;
		String groupName = null;
		try {
			Scheduler sched = QuartzListener.getScheduler();
			Date runTime = null;
			JobDataMap datamap = new JobDataMap();
			datamap.putAsString("meetingId", meetingId);
			
			switch(taskType) {
			case remindConferees :
				runTime = meeting.getRemindTime();
				break;
			case update2Start :
				runTime = meeting.getBeginDate();
				break;
			case clearResources :
				runTime = meeting.getEndDate();
				break;
			}
			datamap.put("actionFlag", taskType.getActionFlag());
			jobName = meetingId + taskType.getJobName();
			groupName = meetingId + taskType.getGroupName();
			
			if("update".equals(oper)) {
				sched.deleteJob(jobName, groupName);
				
				if(log.isDebugEnabled())
					log.debug("为会议 " + meeting.getTitle() + " 删除任务调度：" + Datetimes.format(runTime, Datetimes.datetimeWithoutSecondStyle) + jobName + groupName);
			}
			
			String triggerName = UUIDLong.longUUID() + "";
			SimpleTrigger trigger = new SimpleTrigger(triggerName, groupName, runTime);
			
			JobDetail job = new JobDetail(jobName, groupName, TimeJob.class);
			job.setJobDataMap(datamap);
			sched.scheduleJob(job, trigger);
			
			if(log.isDebugEnabled())
				log.debug("为会议 " + meeting.getTitle() + " 启动任务调度：" + Datetimes.format(runTime, Datetimes.datetimeWithoutSecondStyle) + jobName + groupName);
		} catch (SchedulerException e) {
			log.error("设置会议任务调度出现异常：", e);
		}
	}
	
	/**
	 * 删除会议时，如果会议状态为已发送或召开中，将对应的有效任务调度取消
	 * @param meeting
	 */
	private void deleteQuartzTask(MtMeeting meeting) {
		if(meeting!=null && meeting.getState()>Constants.DATA_STATE_SAVE && meeting.getState()<Constants.DATA_STATE_FINISH) {
			try {
				Long meetingId = meeting.getId();
				Scheduler sched = QuartzListener.getScheduler();
				if(meeting.getState()==Constants.DATA_STATE_SEND) {
					if(meeting.isRemindFlag() && meeting.getRemindTime().getTime()>System.currentTimeMillis())
						sched.deleteJob(meetingId + Constants.TASK_TYPE.remindConferees.getJobName(), meetingId + Constants.TASK_TYPE.remindConferees.getGroupName());
					sched.deleteJob(meetingId + Constants.TASK_TYPE.update2Start.getJobName(), meetingId + Constants.TASK_TYPE.update2Start.getGroupName());
				}
				sched.deleteJob(meetingId + Constants.TASK_TYPE.clearResources.getJobName(), meetingId + Constants.TASK_TYPE.clearResources.getGroupName());
				
				if(log.isDebugEnabled())
					log.debug("为会议 " + meeting.getTitle() + " 删除全部任务调度，于" + Datetimes.format(new Date(), Datetimes.datetimeWithoutSecondStyle));
			} catch (SchedulerException e) {
				log.error("删除会议时，撤销会议对应的任务调度出现异常：", e);
			}
		}
	}
	
	/**
	 * 获取会议创建者、主持人、记录人之外的全体与会人员ID集合，未对代理人进行处理
	 * @param confereesStr   与会人Type|Ids
	 * @param creatUser		 会议发起人
	 * @param emcee			 会议主持人
	 * @param recorder       会议记录人
	 * @see #getMsgReceivers(String, Long, Long, Long)  其中对代理人进行了处理
	 */
	public List<Long>  getConfereeIds(String confereesStr, Long creatUser, Long emcee, Long recorder) {
		List<Long> confereeIds = null;
		if(Strings.isNotBlank(confereesStr)) {
			Set<V3xOrgMember> conferees = null;
			try {
				conferees = this.orgManager.getMembersByTypeAndIds(confereesStr);
			} catch(BusinessException e) {
				log.error("获取与会人员出现错误", e);
			}
			
			if(conferees!=null && conferees.size()>0) {
				confereeIds = new ArrayList<Long>();
				
				for(V3xOrgMember conferee : conferees) {
					if(conferee==null || !conferee.isValid())
						continue;
					Long confereeId = conferee.getId();
//					if(confereeId.equals(creatUser) || confereeId.equals(emcee) || confereeId.equals(recorder))
					if(confereeId.equals(emcee) || confereeId.equals(recorder))
						continue;
					confereeIds.add(confereeId);
				}
			}
		}
		return confereeIds;
	}
	
	public void update(MtMeeting template) throws BusinessException{
		mtMeetingDao.update(template);
	}
	
	/**
	 * 更新会议的状态，用于将已发但未召开的会议删除时，将其状态更新为暂存待发，以便用户对其进行修改
	 * @param meetingId    已发未召开的会议ID
	 * @param state2Update 更新为的状态：暂存待发
	 */
	public void updateState(Long meetingId, int state2Update) {
		this.mtMeetingDao.updateState(meetingId, state2Update);
	}
	
	/**
	 * 将选中的会议状态更新为已归档，实际的归档在Controller中调用知识管理的接口完成
	 */
	public void pigeonhole(List<Long> ids) {
		this.mtMeetingDao.updateState2Pigeonhole4Meetings(ids);
	}

	@SuppressWarnings("unchecked")
	public List<MtMeeting> findByDate(String startdate, String enddate)
			throws MeetingException {
		List<MtMeeting> list = null;
		DetachedCriteria dc = getDetachedCriteraByUser();
		if (StringUtils.isNotBlank(startdate) && StringUtils.isNotBlank(enddate)) {
			startdate += " 00:00:00";
			enddate += " 23:59:59";
			Criterion arg1 = Restrictions.between("beginDate", Datetimes
					.parseDate(startdate), Datetimes.parseDate(enddate));
			Criterion arg2 = Restrictions.between("endDate", Datetimes
					.parseDate(startdate), Datetimes.parseDate(enddate));
			dc.add(Restrictions.or(arg1, arg2));
		} else if (StringUtils.isNotBlank(startdate)
				&& !StringUtils.isNotBlank(enddate)) {
			startdate += " 00:00:00";
			dc.add(Restrictions.ge("beginDate", Datetimes.parseDate(startdate)));
		} else if (!StringUtils.isNotBlank(startdate) && StringUtils.isNotBlank(enddate)) {
			enddate += " 23:59:59";
			dc.add(Restrictions.le("endDate", Datetimes.parseDate(enddate)));
		}
		dc.addOrder(Order.desc("beginDate"));
		list = mtMeetingDao.executeCriteria(dc);
		// bug 8931 8939 initList(list);
		return list;
	}
	
	/**
	 * 为会议与会人员生成待办事项记录
	 */
	@SuppressWarnings("unchecked")
	public void assignedMeeting(MtMeeting mt, String oper) throws BusinessException {
		//先将事项表中关于该会议的数据清空
		if(oper.equals("update")){
			affairManager.deleteByObject(ApplicationCategoryEnum.meeting, mt.getId());
		}
		Map<String, List> result = getMsgReceiversWithAgentMap(mt.getConferees(), mt.getCreateUser(), mt.getEmceeId(), mt.getRecorderId());
		List<Long> ownerList = (List<Long>)result.get(Constants.ReceiverType.Owner.name()); //事件所属人
		if(CollectionUtils.isNotEmpty(ownerList)) {
		    createAffairs(mt, ownerList);
		}
	}
	
	/**
     * 邀请人员参加会议时创建会议待办事项 Meixd 2010-11-16
     * @param mt
     * @param ownerList
     */
    public void createAffairs(MtMeeting mt, List<Long> ownerList)
    {
        List<Affair> affList = new ArrayList<Affair>();
        for (Long conferee : ownerList) {
        	affList.add(createNewAffair(conferee, mt));
        }
        affairManager.createAffairs(affList);
    }
	
	/**
	 * 根据id 和 会议创建 事项
	 */
	private Affair createNewAffair(Long memberId,MtMeeting mt){
		Affair affair = new Affair();
        affair.setIdIfNew();
        affair.setObjectId(mt.getId());
        affair.setState(StateEnum.col_pending.key());
        affair.setSenderId(mt.getCreateUser());
        affair.setSubject(mt.getTitle());
        affair.setApp(ApplicationCategoryEnum.meeting.getKey());
        affair.setMemberId(memberId);
        affair.setCompleteTime(new Timestamp(mt.getEndDate().getTime()));
        affair.setCreateDate(new Timestamp(mt.getBeginDate().getTime()));
        affair.setUpdateDate(new Timestamp(mt.getUpdateDate().getTime()));
        affair.setReceiveTime(new Timestamp(mt.getBeginDate().getTime()));
        affair.setHasAttachments(mt.isHasAttachments());
      //radishlee add 2012-3-5 添加视频会议摄像头显示 用紧急程度9999代表是视频会议。要添加摄像头显示
		if(mt.getMeetingType().equals(Constants.VIDEO_MEETING)){
			affair.setBodyType(Constants.VIDEO_MEETING_TITLE);
		}else{
		    affair.setBodyType(mt.getDataFormat());
		}
		affair.serialExtProperties();
        return affair;
	}

	/*
	 * 根据id取得IndexInfo
	 * @see com.seeyon.v3x.index.share.interfaces.IndexEnable#getIndexInfo(long)
	 */
	@SuppressWarnings("unchecked")
	public IndexInfo getIndexInfo(long id) throws Exception {
	
		MtMeeting meeting=getByMtId(id);
		if(meeting==null) return null;
		IndexInfo indexInfo=new IndexInfo();
		indexInfo.setTitle(meeting.getTitle());
		//处理下附件
		List<Attachment> attachments=new ArrayList<Attachment>();
		List attach=attachmentManager.getByReference(meeting.getId());
		if(attach!=null&&attach.size()>0) attachments.addAll(attach);
		
		StringBuffer buffer=new StringBuffer();
		buffer.append(meeting.getContent());
		List<MtSummaryTemplate> mtSummaryTemplates=mtSummaryTemplateManager.findByPropertyNoInit("meetingId", id);
		if(mtSummaryTemplates!=null&&mtSummaryTemplates.size()>0){
			for(MtSummaryTemplate summary:mtSummaryTemplates){
				buffer.append(summary.getTemplateName());
				buffer.append(summary.getDescription());
				buffer.append(summary.getContent());
				List attachment=attachmentManager.getByReference(summary.getId(),summary.getId());
				if(attachment!=null&&attachment.size()>0) attachments.addAll(attachment);
			}
		}
		
		// -------------------之前没有处理附件格式（开始）-----------------------------
		//		indexInfo.setContent(buffer.toString());
		//		indexInfo.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
		// -------------------之前没有处理附件格式（结束）-----------------------------
		
		// 处理附件的格式
		Partition partition = partitionManager.getPartition(meeting.getCreateDate(), true);
		String formatType = meeting.getDataFormat();
		String contentPath = this.fileManager.getFolder(meeting.getCreateDate(), false);
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML.equals(formatType)){
			indexInfo.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
			String content = meeting.getContent() + meeting.getCreateUserName() + meeting.getConfereesNames() + meeting.getEmceeName() + meeting.getRecorderName();
			indexInfo.setContent(content);
		} 
		else
		{
			if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WORD);
			} else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_XLS);
			}
			else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WPS_Word);
			}
			else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL.equals(formatType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WPS_EXCEL);
			}
			indexInfo.setContentID(Long.parseLong(meeting.getContent()));
			indexInfo.setContentAreaId(partition.getId().toString());
			indexInfo.setContentPath(contentPath.substring(contentPath.length()-11)+System.getProperty("file.separator"));
		}
//		indexInfo.setContentCreateDate(bulData.getPublishDate());
		indexInfo.setEntityID(meeting.getId());
//		indexInfo.setKeyword(meeting.getKeywords());
		indexInfo.setAppType(ApplicationCategoryEnum.meeting);
		indexInfo.setCreateDate(meeting.getCreateDate());//目前设定的是发布日期，此处存疑
		indexInfo.setAuthor(meeting.getCreateUserName());
		if(meeting.getCreateUserName()==null){
			V3xOrgMember member = null;
	        try {
	            member = orgManager.getEntityById(V3xOrgMember.class, meeting.getCreateUser());
	            indexInfo.setAuthor(member.getName());
	        }
	        catch (BusinessException e) {
	        	log.error("", e);
	        }
		}
		//对附件进行处理
		List<Accessory> accessories=IndexUtil.attToAccessory(attachments);
		indexInfo.setAccessories(accessories);
//		if(attachments!=null&&attachments.size()>0)indexInfo.getAttachMap().put(IndexInfo.ATTACH_LIST, attachments);
		//进行权限处理
		List<String> ownerList=new ArrayList<String>();
		String confereeStr=meeting.getConferees();
		String[] conferees=StringUtils.split(confereeStr, ",");
		for(String conferee:conferees){
			ownerList.add(conferee);
		}
		ownerList.add(meeting.getEmceeId().toString());
		ownerList.add(meeting.getRecorderId().toString());
		ownerList.add(meeting.getCreateUser().toString());
		AuthorizationInfo authorizationInfo=new AuthorizationInfo();
		if(ownerList.size()>0)authorizationInfo.setOwner(ownerList);
		
		indexInfo.setAuthorizationInfo(authorizationInfo);
		
		indexInfo.setStartMemberId(meeting.getCreateUser());
		indexInfo.setHasAttachment(meeting.isHasAttachments());
//		Long projectId = meeting.getProjectId();
//		indexInfo.setProjectId(projectId != null ? projectId.toString() : "");
		
		List<MtReply> replys = replyManager.findByPropertyNoInit("meetingId", meeting.getId());
		StringBuffer opinion = new StringBuffer();
		for(MtReply reply : replys){
			opinion.append(reply.getUserName()==null?orgManager.getEntityById(V3xOrgMember.class, reply.getUserId()).getName():reply.getUserName());
			opinion.append(" "+ResourceBundleUtil.getString(MeetingResources, "mt.mtReply.feedback_flag." + reply.getFeedbackFlag()));
			opinion.append(" "+reply.getFeedback()==null?" ":reply.getFeedback());
		}
		indexInfo.setOpinion(opinion.toString());
		StringBuffer comment = new StringBuffer();
		List<MtSummaryTemplate> summarys =  mtSummaryTemplateManager.findByPropertyNoInit("meetingId",meeting.getId());
		for(MtSummaryTemplate summary : summarys){
			comment.append(summary.getContent());
		}
		indexInfo.setComment(comment.toString());
		indexInfo.setStartTime(meeting.getBeginDate());
		indexInfo.setEndTime(meeting.getEndDate());
		indexInfo.setState(ResourceBundleUtil.getString(MeetingResources, "mt.mtMeeting.state." + meeting.getState()));
		
		return indexInfo;
	}
	
	/**
	 * 获取当前用户可以查看的关联项目会议
	 */
	@SuppressWarnings("unchecked")
	public List<MtMeeting> getProjectMeeting(Long projectId, Long phaseId, Long currentUserId) {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("select " + SELECT_FIELD + " from " + MtMeeting.class.getName() + " as mt, ProjectMember as pm ");
		hql.append("where mt.projectId=pm.projectSummary.id and mt.projectId=:projectId and pm.memberid=:memberid and mt.state>=:state ");
		params.put("projectId", projectId);
		params.put("memberid", currentUserId);
		params.put("state", Constants.DATA_STATE_SEND);
		if(phaseId != null && phaseId != 1){
			hql.append("and mt.id in (select ph.eventId from ProjectPhaseEvent as ph" +
					" where ph.phaseId=:phaseId and ph.eventType=" + ApplicationCategoryEnum.meeting.key() + ") ");
			params.put("phaseId", phaseId);
		}
		hql.append("order by mt.beginDate desc");
		return this.parseObjArray2Meetings((List<Object[]>)mtMeetingDao.find(hql.toString(), -1, -1, params));
	}
	
	/**
	 * 条件查询当前用户可以查看的关联项目会议
	 */
	@SuppressWarnings("unchecked")
	public List<MtMeeting> getProjectMeetingByCondition(String condition,Long projectId, Long phaseId, Long currentUserId,Map<String,Object> paramMap) {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("select " + SELECT_FIELD + " from " + MtMeeting.class.getName() + " as mt, ProjectMember as pm ");
		hql.append("where mt.projectId=pm.projectSummary.id and mt.projectId=:projectId and pm.memberid=:memberid and mt.state>=:state ");
		params.put("projectId", projectId);
		params.put("memberid", currentUserId);
		params.put("state", Constants.DATA_STATE_SEND);
		
		if ("title".equals(condition)) {
			hql.append("and mt.title like :title ") ;
			params.put("title", "%" + paramMap.get("title") + "%") ;
		} else if ("author".equals(condition)) {
			hql.append("and mt.createUser in (:author) ") ;
			params.put("author", paramMap.get("author")) ;
		} else if ("newDate".equals(condition)) {
			hql.append("and mt.createDate>=:begin and mt.createDate<=:end ") ;
			params.put("begin", Datetimes.getTodayFirstTime(paramMap.get("newDate").toString())) ;
			params.put("end", Datetimes.getTodayLastTime(paramMap.get("newDate").toString())) ;
		}
		
		if(phaseId != null && phaseId != 1){
			hql.append("and mt.id in (select ph.eventId from ProjectPhaseEvent as ph" +
					" where ph.phaseId=:phaseId and ph.eventType=" + ApplicationCategoryEnum.meeting.key() + ") ");
			params.put("phaseId", phaseId);
		}
		hql.append("order by mt.beginDate desc");
		return this.parseObjArray2Meetings((List<Object[]>)mtMeetingDao.find(hql.toString(), -1, -1, params));
	}
	
	/**
	 * 与会人员、主持人与记录人以回执时间作为已办时间
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<MtMeeting> findMeetingReplied(Long userId, Date startTime, Date endTime, boolean page){	
		StringBuffer sbHql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		sbHql.append("from MtMeeting m where m.id in ( select mr.meetingId from MtReply mr where mr.userId=:userId ");
		if(startTime!=null) {
			sbHql.append(" and mr.readDate>=:startTime ");
			params.put("startTime", startTime);
		}
		if(endTime!=null){
			sbHql.append(" and mr.readDate<:endTime ");
			params.put("endTime", endTime);
		}
		sbHql.append(" ) and m.state!=:state order by m.beginDate desc");
		params.put("userId", userId);
		params.put("state", Constants.DATA_STATE_SAVE);
		if(page)
			return mtMeetingDao.find(sbHql.toString(), params);
		else
			return mtMeetingDao.find(sbHql.toString(), -1, -1, params);
	}
	
	private int getTotalCount4RepliedMeetings(Long userId, Date startTime, Date endTime) {
		StringBuffer sbHql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		sbHql.append("select count(m.id) from MtMeeting m where m.id in ( select mr.meetingId from MtReply mr where mr.userId=:userId ");
		if(startTime!=null) {
			sbHql.append(" and mr.readDate>=:startTime ");
			params.put("startTime", startTime);
		}
		if(endTime!=null){
			sbHql.append(" and mr.readDate<:endTime ");
			params.put("endTime", endTime);
		}
		sbHql.append(" ) and m.state!=:state");
		params.put("userId", userId);
		params.put("state", Constants.DATA_STATE_SAVE);
		
		Object r = mtMeetingDao.findUnique(sbHql.toString(), params);
		
		return r == null ? 0 : ((Integer)r).intValue();
	}
	
	@SuppressWarnings({ "unchecked" })
	private List<MtMeeting> findMeetingSended(Long userId, Date startTime, Date endTime, boolean page){
		DetachedCriteria dc = DetachedCriteria.forClass(MtMeeting.class);
		if(startTime!=null){
			dc.add(Restrictions.ge("createDate", startTime));
		}
		if(endTime!=null){
			dc.add(Restrictions.le("createDate", endTime));
		}		
		
		Criterion arg1 = Restrictions.eq("createUser", userId);     //等于创建者的
		Criterion arg2 = Restrictions.ne("state", Constants.DATA_STATE_SAVE);    //状态不等于草稿的

		dc.add(arg1);
		dc.add(arg2);
		dc.addOrder(Order.desc("beginDate"));
		if(page)
			return mtMeetingDao.executeCriteria(dc);
		else 
			return mtMeetingDao.executeCriteria(dc,-1,-1);
	}
	
	private int getTotalCount4SendedMeetings(Long userId, Date startTime, Date endTime) {
		DetachedCriteria dc = DetachedCriteria.forClass(MtMeeting.class);
		if(startTime!=null){
			dc.add(Restrictions.ge("createDate", startTime));
		}
		if(endTime!=null){
			dc.add(Restrictions.le("createDate", endTime));
		}		
		
		Criterion arg1 = Restrictions.eq("createUser", userId);     //等于创建者的
		Criterion arg2 = Restrictions.ne("state", Constants.DATA_STATE_SAVE);    //状态不等于草稿的

		dc.add(arg1);
		dc.add(arg2);
		return mtMeetingDao.getCountByCriteria(dc);
	}
	
    @SuppressWarnings("unchecked")
	public HashMap<Long, int[]> getUsersMeetingManagerList(List<Long> userIds, Date startTime, Date endTime){
		HashMap<Long, int[]> meetingMangegerMap = new HashMap<Long, int[]>();
    	//计时器
    	java.util.Calendar calendar = new GregorianCalendar();
    	//如果用户列表为空，直接返回
    	if(userIds == null) return null;
        //循环得到所有用户的会议管理列表
    	for(Long userId : userIds){
    		int[] nums = new int[10];
    		List meetings = this.findMeetings4User("10", userId, null, null, null, true);
    		nums[0] = (meetings==null ? 0 : meetings.size());
    		nums[1] = this.getTotalCount4RepliedMeetings(userId, Datetimes.getTodayFirstTime(), Datetimes.getTodayLastTime());
    		nums[2] = this.getTotalCount4SendedMeetings(userId, Datetimes.getTodayFirstTime(), Datetimes.getTodayLastTime());
    		nums[3] = this.getTotalCount4RepliedMeetings(userId,Datetimes.getFirstDayInWeek(calendar.getTime()),Datetimes.getLastDayInWeek(calendar.getTime()));
    		nums[4] = this.getTotalCount4SendedMeetings(userId,Datetimes.getFirstDayInWeek(calendar.getTime()),Datetimes.getLastDayInWeek(calendar.getTime()));
    		nums[5] = this.getTotalCount4RepliedMeetings(userId, Datetimes.getFirstDayInMonth(calendar.getTime()),Datetimes.getLastDayInMonth(calendar.getTime()));
    		nums[6] = this.getTotalCount4SendedMeetings(userId, Datetimes.getFirstDayInMonth(calendar.getTime()),Datetimes.getLastDayInMonth(calendar.getTime()));
    		nums[7] = this.getTotalCount4RepliedMeetings(userId, startTime, endTime);
    		nums[8] = this.getTotalCount4SendedMeetings(userId, startTime, endTime);
    		nums[9] = this.getTotalCount4Pigeonhole(userId);
    		meetingMangegerMap.put(userId, nums);
    	}
    	return meetingMangegerMap;
    }
	
	public String[][] getUsersMeetingManagerListByTime(String[] userIdsArray, String beginDateStr, String endDateStr){		
		if(userIdsArray == null)	return null;
		String[][] usersMeetingManagerList = new String[userIdsArray.length][3];
		Date startTime = Datetimes.parseDatetime(beginDateStr);
		Date endTime = Datetimes.parseDatetime(endDateStr);
		for(int i = 0;i<userIdsArray.length;i++){
			String userIdStr = userIdsArray[i];
			Long userId = Long.parseLong(userIdStr);
			usersMeetingManagerList[i] = new String[3];
			if(userId!=null){
				usersMeetingManagerList[i][0] = userIdStr;
	    		//取得用户时间段内已回执的会议条数                
                usersMeetingManagerList[i][1] = this.getTotalCount4RepliedMeetings(userId, startTime, endTime)+"";
	    		//取得用户时间段内已发送的会议条数
                usersMeetingManagerList[i][2] = this.getTotalCount4SendedMeetings(userId, startTime, endTime)+"";
			}
		}	
		return usersMeetingManagerList;
	}
	
	@SuppressWarnings("unchecked")
	public List<MtMeeting> getUserMeetingByManagerType(Long userId, int type, Date startTime, Date endTime){
		if(userId == null) return null;
    	//计时器
    	java.util.Calendar calendar = new GregorianCalendar();
		if(type == 0){
			//获得累计代办的会议
    		List meetings = this.findMeetings4User("10", userId, null, null, null, true);
    		return meetings;  
		}else if(type == 1){
    		//取得用户本日已回执的会议条数
    		return findMeetingReplied(userId, Datetimes.getTodayFirstTime(), Datetimes.getTodayLastTime(), true);
		}else if(type == 2){
    		//取得用户本日已发送的会议条数
    		return findMeetingSended(userId, Datetimes.getTodayFirstTime(), Datetimes.getTodayLastTime(), true);
		}else if(type == 3){
            //取得用户本周已回执的会议条数
    		return findMeetingReplied(userId, Datetimes.getFirstDayInWeek(calendar.getTime()),Datetimes.getLastDayInWeek(calendar.getTime()), true);			
		}else if(type == 4){
    		//取得用户本周已发送的会议条数
			return findMeetingSended(userId, Datetimes.getFirstDayInWeek(calendar.getTime()),Datetimes.getLastDayInWeek(calendar.getTime()), true);
		}else if(type == 5){
    		//取得用户本月已回执的会议条数
    		return findMeetingReplied(userId, Datetimes.getFirstDayInMonth(calendar.getTime()),Datetimes.getLastDayInMonth(calendar.getTime()), true);
		}else if(type == 6){
    		//取得用户本月已发送的会议条数
    		return findMeetingSended(userId, Datetimes.getFirstDayInMonth(calendar.getTime()),Datetimes.getLastDayInMonth(calendar.getTime()), true);			
		}else if(type == 7){
    		//取得用户时间段内已回执的会议条数
    		return findMeetingReplied(userId, startTime, endTime, true);
		}else if(type == 8){
    		//取得用户时间段内已发送的会议条数
    		return findMeetingSended(userId, startTime, endTime, true);
		}else if(type == 9){
			return findPigeonholeMeetings4NeedCount(userId);
		}
		return null;
	}
	
	/**
	 * TODO 归档查询条件，似乎并未用到?
	 */
	@SuppressWarnings("unchecked")
	public List<MtMeeting> iSearch(ConditionModel cModel){
		String title = cModel.getTitle();
		final Date beginDate = cModel.getBeginDate();
		final Date endDate = cModel.getEndDate();
		Long fromUserId = cModel.getFromUserId();
		Long currentUserId = cModel.getUser().getId(); //如果是选择"发给我的"，则toUserId==currentUserId
		
		StringBuffer hqlStr = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		
		String fromMtAndConferee = " from MtMeeting as mt, MtConferee as mc where mt.id=mc.meetingId" +
		" and (mc.confereeId in (:domainIds) or mt.emceeId=:userId or mt.recorderId=:userId) ";
		
		if(fromUserId!=null) {
			if(currentUserId.equals(fromUserId)) { //自己发起
				hqlStr.append("select" + SELECT_FIELD + " from MtMeeting as mt where mt.createUser=:creator ");
			} else { //他人发给我的
				hqlStr.append("select distinct" + SELECT_FIELD +  fromMtAndConferee + " and mt.createUser=:creator ");
				params.put("domainIds", this.getDomainIds(currentUserId));
				params.put("userId", currentUserId);
			}
			params.put("creator", fromUserId);
		} else {
			hqlStr.append("select distinct" + SELECT_FIELD + fromMtAndConferee);
			params.put("domainIds", this.getDomainIds(currentUserId));
			params.put("userId", currentUserId);
		}
		
		hqlStr.append(" and mt.state>:saveState ");   //未发送和已归档的会议不在查看范围之内?
		params.put("saveState", Constants.DATA_STATE_SAVE);
		
		if(Strings.isNotBlank(title)){
			hqlStr.append(" and mt.title like :title ");
			params.put("title", "%" + SQLWildcardUtil.escape(title) + "%");
		}
		if(beginDate != null){
			hqlStr.append(" and mt.createDate >= :beginDate ");
			params.put("beginDate", beginDate);
		}
		if(endDate != null){
			hqlStr.append(" and mt.createDate <= :endDate ");
			params.put("endDate", endDate);
		}
		hqlStr.append(" order by mt.createDate desc ");
		if(fromUserId!=null && currentUserId.equals(fromUserId))
			return this.parseObjArray2Meetings((List<Object[]>)mtMeetingDao.find(hqlStr.toString(), params));
		else
			return this.parseObjArray2Meetings((List<Object[]>)mtMeetingDao.find(hqlStr.toString(), "mt.id", true, params));
	}

	/**
	  *	1、A在时间段1设置B为会议代理；
	  *	2、在时间段1里，发送给A的会议则不在A的会议管理中显示，而应该在代理事项中显示；
	  *	3、当会议回执参加或不参加了，则在A的会议管理中要显示出来。
	  */
	@SuppressWarnings("unchecked")
	public List<MtMeeting> findAllforAgent() {
		DetachedCriteria dc = getDetachedCriteraByUserforAgent();
		dc.addOrder(Order.desc("beginDate"));
		List<MtMeeting> list = mtMeetingDao.executeCriteria(dc);
		// bug 8931 8939 initList(list);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<MtMeeting> findByDateforAgent(String startdate, String enddate)
			throws MeetingException {
		List<MtMeeting> list = null;
		DetachedCriteria dc = getDetachedCriteraByUserforAgent();
		if (StringUtils.isNotBlank(startdate)
				&& StringUtils.isNotBlank(enddate)) {
			startdate += " 00:00:00";
			enddate += " 23:59:59";
			Criterion arg1 = Restrictions.between("beginDate", Datetimes
					.parseDate(startdate), Datetimes.parseDate(enddate));
			Criterion arg2 = Restrictions.between("endDate", Datetimes
					.parseDate(startdate), Datetimes.parseDate(enddate));
			dc.add(Restrictions.or(arg1, arg2));
		} else if (StringUtils.isNotBlank(startdate) && !StringUtils.isNotBlank(enddate)) {
			startdate += " 00:00:00";
			dc.add(Restrictions.ge("beginDate", Datetimes
							.parseDate(startdate)));
		} else if (!StringUtils.isNotBlank(startdate) && StringUtils.isNotBlank(enddate)) {
			enddate += " 23:59:59";
			dc.add(Restrictions.le("endDate", Datetimes.parseDate(enddate)));
		}
		dc.addOrder(Order.desc("beginDate"));
		list = mtMeetingDao.executeCriteria(dc);
		// bug 8931 8939 initList(list);
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<MtMeeting> findByPropertyNoInitforAgent(String property, Object value) {
		List<MtMeeting> list;
		DetachedCriteria dc = getDetachedCriteraByUserforAgent();
		if (value instanceof String) {
			dc.add(Restrictions.like(property, (String) value, MatchMode.ANYWHERE));
		} else {
			dc.add(Restrictions.eq(property, value));
		}
		dc.addOrder(Order.desc("beginDate"));
		list = mtMeetingDao.executeCriteria(dc);
		return list;
	}

	/**
	 * @deprecated 未进行状态、搜索条件匹配
	 */
	public List<MtMeeting> getAgentedListMeeting(AgentModel agentModel){
		return this.findMeetings(new MeetingSearchModel(Constants.DATA_STATE_SEND, null, null, null, null, agentModel), false);
	}
	
	/**
	 * 获取用户想要查看的会议列表（未召开、已召开(包含了已结束和已总结的会议)、暂存待发），包含了用户作为代理人可以查看的会议列表
	 * @param stateStr   用户想要查看的会议类型：未召开、已召开(包含了已结束和已总结的会议)、暂存待发
	 * @param userId     当前用户
	 * @param condition  查询条件类型：会议主题、召开时段、会议状态
	 * @param textfield  查询条件值1
	 * @param textfield1   查询条件值2
	 */
	public List<MtMeeting> findMeetings4User(String stateStr, Long userId, String condition, String textfield, String textfield1, boolean containCreateUser) {
		int state = NumberUtils.toInt(stateStr, Constants.DATA_STATE_SEND);
		MeetingSearchModel searchModel = new MeetingSearchModel(state, condition, textfield, textfield1, userId, null);
		List<MtMeeting> self_meetings = this.findMeetings(searchModel, containCreateUser);
		
		if(state > Constants.DATA_STATE_SAVE) {
	        List<AgentModel> agentModels = Constants.getMeetingAgents();
	        if(CollectionUtils.isNotEmpty(agentModels)) {
	        	for(AgentModel agent : agentModels) {
	        		if(agent != null && agent.getAgentId().equals(userId)) {
	        			searchModel.setAgentModel(agent);
	        			List<MtMeeting> agent_meetings = this.findMeetings(searchModel, containCreateUser);
	        			CommonTools.addAllIgnoreEmpty(self_meetings, agent_meetings);
	        		} else {
	        			log.warn("用户[id=" + userId + "]代理对象集合中存在无效元素！");
	        		}
	        	}
	        	Collections.sort(self_meetings);
	        }
		}
		return self_meetings;
	}
	
	public List<MtMeeting> findMeetings4User(String stateStr, Long userId, String condition, String textfield, String textfield1) {
		return this.findMeetings4User(stateStr, userId, condition, textfield, textfield1, false);
	}
	
	/** 在查询获取会议列表用于前端展现等场合时，所要获取的字段(避免查询大字段而引起无法在其前加上distinct的问题) */
	private static String SELECT_FIELD = " mt.meetingType,mt.id, mt.title, mt.createUser, mt.emceeId, mt.recorderId, mt.beginDate, " +
			"mt.endDate, mt.state, mt.remindFlag, mt.hasAttachments, mt.accountId, mt.dataFormat, mt.beforeTime, mt.createDate ";
	
	@SuppressWarnings("unchecked")
	private List<MtMeeting> findMeetings(MeetingSearchModel search, boolean containCreateUser) {
		Map<String, Object> params = new HashMap<String, Object>();
		
		int state = search.getState();
		boolean distinct = state != Constants.DATA_STATE_SAVE;
		String select = "select " + (distinct ? " distinct " : " ") + SELECT_FIELD;
		String from = " from " + MtMeeting.class.getName() + " as mt " + (state == Constants.DATA_STATE_SAVE ? "" : ", " + MtConferee.class.getName() + " as mc ");
		String orderBy = " order by mt.beginDate " + (state == Constants.DATA_STATE_SEND ? " asc" : " desc");
		
		AgentModel agentModel = search.getAgentModel();
		boolean isAgent = agentModel != null;
		
		StringBuffer where = new StringBuffer();
		if(state == Constants.DATA_STATE_SAVE) {
			where.append(" where mt.createUser=:userId ");
			params.put("userId", search.getUserId());
		} else {
			where.append(" where mt.id=mc.meetingId and (mc.confereeId in (:domainIds) " +
				"or mt.emceeId=:userId or mt.recorderId=:userId" + ((isAgent || containCreateUser) ? "" : " or mt.createUser=:userId" ) + ") ");
			Long userId = isAgent ? agentModel.getAgentToId() : search.getUserId();
			params.put("userId", userId);
			params.put("domainIds", this.getDomainIds(userId));
		}
		
		if(state >= Constants.DATA_STATE_START) {
			where.append(" and mt.state>:state ");
			params.put("state", Constants.DATA_STATE_SEND);
		}else if(state == Constants.DATA_STATE_SAVE) {
			where.append(" and mt.state=:state ");
			params.put("state", state);
		}else{
			//radishlee add 2012-2-10加入中间会议状态：即将召开(15)   state初始化=-1 
			where.append(" and  ( mt.state=:state1 or mt.state=:state2 ) ");
			params.put("state1", Constants.DATA_STATE_SEND);
			params.put("state2", Constants.DATA_STATE_WILL_START);//即将召开
		}
		
		if(isAgent) {
			where.append(" and mt.beginDate >= :agentBeginDate and mt.endDate <= :agentEndDate ");
			params.put("agentBeginDate", agentModel.getStartDate());
			params.put("agentEndDate", agentModel.getEndDate());
		}
		
		if(MeetingSearchModel.Search_By_Title.equals(search.getCondition())) {
			if(Strings.isNotBlank(search.getValue1())) {
				where.append(" and mt.title like :title ");
				params.put("title", "%" + SQLWildcardUtil.escape(search.getValue1().trim()) + "%");
			}
		} 
		else if(MeetingSearchModel.Search_By_CreateDate.equals(search.getCondition())) {
			if(Strings.isNotBlank(search.getValue1())) {
				where.append(" and mt.beginDate >= :beginDate ");
				params.put("beginDate", Datetimes.getTodayFirstTime(search.getValue1()));
			}
			
			if(Strings.isNotBlank(search.getValue2())) {
				where.append(" and mt.endDate <= :endDate ");
				params.put("endDate", Datetimes.getTodayLastTime(search.getValue2()));
			}
		} 
		else if(MeetingSearchModel.Search_By_CreateUser.equals(search.getCondition())) {
			if(Strings.isNotBlank(search.getValue1()) && Strings.isNotBlank(search.getValue2())) {
				where.append(" and mt.createUser = :createUserId");
				params.put("createUserId", NumberUtils.toLong(search.getValue2()));
			}
		}
		else if(MeetingSearchModel.Search_By_State.equals(search.getCondition())) {
			where.append(" and mt.state=:certainState ");
			params.put("certainState", Integer.valueOf(search.getValue1()));
		}
		else if(MeetingSearchModel.Search_By_MeetingType.equals(search.getCondition())) {
			where.append(" and mt.meetingType=:meetingType ");
			params.put("meetingType", SQLWildcardUtil.escape(search.getValue1().trim()));
		}
		
		String hql = select + from + where.toString() + orderBy;
		if(isAgent) {
			List<MtMeeting> meetings = this.parseObjArray2Meetings(mtMeetingDao.find(hql, -1, -1, params));
			return this.convertMeetings4Agent(meetings,search.getUserId(), agentModel);
		} else {
			return this.parseObjArray2Meetings(mtMeetingDao.find(hql, "mt.id", distinct, params));
		}
	}
	
	/**
	 * 为代理人所能查看的会议加上代理标识
	 */
	private List<MtMeeting> convertMeetings4Agent(List<MtMeeting> meetings,Long currentUserId, AgentModel agent) {
		List<MtMeeting> result = null;
		if(CollectionUtils.isNotEmpty(meetings)) {
			result = new ArrayList<MtMeeting>();
			//判断当前用户是不是被代理人
			boolean isAgentTo = agent.getAgentToId().equals(currentUserId);
			for(MtMeeting mt : meetings) {
				try {
					MtMeeting m = (MtMeeting) mt.clone();
					m.setId(mt.getId());
					m.setProxy(true);
					m.setProxyId(isAgentTo?null:agent.getAgentToId());
					result.add(m);
				} catch (CloneNotSupportedException e) {
					log.error("克隆会议出现异常：", e);
				}
			}
		}
		return result;
	}
	
	
	/**
	 * 获取当前用户组织模型(部门、组、人员。目前与会人员限定在这三种类型)ID集合，配合进行查询(增加了岗位)
	 */
	private List<Long> getDomainIds(Long memberId) {
		List<Long> result = null;
		try {
			result = this.orgManager.getUserDomainIDs(memberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, 
					V3xOrgEntity.ORGENT_TYPE_ACCOUNT, V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, V3xOrgEntity.ORGENT_TYPE_TEAM,
					V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_POST);
		} catch (BusinessException e) {
			log.error("获取当前人员部门、组、人员对应的组织模型实体ID失败", e);
		}
		return result;
	}
	
	/**
	 * 将获取的字段数组组装成为会议集合，配合各种查询的返回结果
	 * @param objlist 字段结果集
	 */
	private List<MtMeeting> parseObjArray2Meetings(List<Object[]> objlist) {
		List<MtMeeting> list = new ArrayList<MtMeeting>();
		if(objlist == null || objlist.size() == 0)
			return list;
		for(Object[] arr : objlist){
			MtMeeting meeting = new MtMeeting();
			int n = 0;
			//radishlee add 加入视频会议类型2012-1-11 默认是普通会议
			Object obj = arr[n++];
			meeting.setMeetingType((String)(obj==null?Constants.ORID_MEETING:obj));
			meeting.setId((Long)arr[n++]);
			meeting.setTitle((String)arr[n++]);
			meeting.setCreateUser((Long)arr[n++]);
			meeting.setEmceeId((Long)arr[n++]);
			meeting.setRecorderId((Long)arr[n++]);
			meeting.setBeginDate((Date)arr[n++]);
			meeting.setEndDate((Date)arr[n++]);
			//radishlee add 加入视频会议中间状态2012-2-10 默认是普通会议 15即将召开
			int state = (Integer)arr[n++];
			if(state==Constants.DATA_STATE_WILL_START){
				state = Constants.DATA_STATE_SEND;
			}
			meeting.setState(state);
			meeting.setRemindFlag((Boolean)arr[n++]);
			meeting.setHasAttachments((Boolean)arr[n++]);
			meeting.setAccountId((Long)arr[n++]);
			meeting.setDataFormat((String)arr[n++]);
			meeting.setBeforeTime((Integer)arr[n++]);
			meeting.setCreateDate((Date)arr[n++]);
			list.add(meeting);
		}
		return list;
	}
	
	/**
	 * 保存与会对象记录
	 * @param conferees  与会对象选人界面返回数据，为Type|ID以","拼接起来的字符串
	 * @param meetingId  与会对象所要参加的会议ID
	 */
	public void saveMeetingConferees(Long meetingId, String conferees) { 
		this.mtConfereeDao.saveConferees(meetingId, conferees);
	}
	
	/**
	 * 会议创建者删除会议记录时，将对应的与会对象记录也一起删除<br>
	 * 会议创建者修改并保存会议记录时，与会对象有变动时，将旧有与会对象先删除（之后再保存新的与会对象记录）<br>
	 * @param meetingId
	 */
	public void deleteConferees(Long meetingId) {
		this.mtConfereeDao.delete(new Object[][]{{"meetingId", meetingId}});
	}
	
	/**
	 * 判断会议是否还可进行修改（已发起未召开的会议如果已经到达开始时间，此时不再允许修改）
	 * 用于前端AJAX调用
	 * @param meetingId
	 */
	public boolean canEditMeeting(Long meetingId) {
		MtMeeting meeting = this.getById(meetingId);
		return meeting!=null && meeting.getState()!=null && meeting.getState()<=Constants.DATA_STATE_SEND;
	}
	
	/**
	 * 
	 * 判断会议是否还可进行修改（再视频会议服务器坏掉情况下，如果是普通会议可以。视频会议不可以）
	 * 用于前端AJAX调用
	 * @param meetingId
	 */
	public boolean validateEdit(Long meetingId) {
		MtMeeting meeting = this.getById(meetingId);
		if(Constants.ORID_MEETING.equals(meeting.getMeetingType()==null?"":meeting.getMeetingType())){
			return true;
		}
		return false;
	}
	
	/**
     * 获取会议消息发送对象集合：主持人、记录人、与会人员，其中的人员如果设定了代理人员，也将代理人员加入
     */
    public List<Long> getMsgReceivers(MtMeeting meeting) throws BusinessException {
    	return this.getMsgReceivers(meeting.getConferees(), meeting.getCreateUser(), meeting.getEmceeId(), meeting.getRecorderId());
    }
    
    /**
     * 获取会议会前提醒消息发送对象集合，包括与会人员及其代理人<br>
     * 回执了不参加会议的与会人员不发送消息，回执为待定和参加或未回执的与会人员才发送消息<br>
     * @param meeting
     */
    @SuppressWarnings("unchecked")
	public Map<String, List> getRemindMsgReceivers(MtMeeting meeting) throws BusinessException {
    	Map<String, List> map = this.getMsgReceiversWithAgentMap(meeting);
    	List<Long> self = (List<Long>)map.get(Constants.ReceiverType.Owner.name());
    	List<Long> agent = (List<Long>)map.get(Constants.ReceiverType.Agent.name());
    	
    	List<Long> unAttendants = this.mtMeetingDao.getUnAttendants(meeting.getId());
    	CommonTools.removeAllIgnoreEmpty(self, unAttendants);
		
		Set<Long> toRemove = Constants.getMeetingAgentId(unAttendants);
		CommonTools.removeAllIgnoreEmpty(agent, toRemove);
		
		if(log.isDebugEnabled()) {
			log.debug("以下人员确定不参加会议[" + MeetingMsgHelper.showMemberName(unAttendants) + "], 不为其发送会前消息提醒");
			
			log.debug("以下代理人员确定不参加会议[" + MeetingMsgHelper.showMemberName(toRemove) + "], 不为其发送会前消息提醒");
		}
    	
    	map.put(Constants.ReceiverType.Owner.name(), self);
    	map.put(Constants.ReceiverType.Agent.name(), agent);
    	return map;
    }
    
	/**
     * 获取会议消息发送对象集合：主持人、记录人、与会人员，其中的人员如果设定了代理人员，也将代理人员加入
     */
	@SuppressWarnings("unchecked")
	public List<Long> getMsgReceivers(String confereesStr, Long creatUser, Long emcee, Long recorder) throws BusinessException {
		Map<String, List> result = getMsgReceiversWithAgentMap(confereesStr, creatUser, emcee, recorder);
		List<Long> msgReceivers = null;
		if(result != null){
			msgReceivers = new ArrayList<Long>();
			CommonTools.addAllIgnoreEmpty(msgReceivers, (List<Long>)result.get(Constants.ReceiverType.Owner.name()));
			CommonTools.addAllIgnoreEmpty(msgReceivers, (List<Long>)result.get(Constants.ReceiverType.Agent.name()));
		}
    	return msgReceivers;
	}
	
	/**
     * 获取会议消息发送对象Map：主持人、记录人、与会人员，其中的人员如果设定了代理人员，也将代理人员加入
     */
	@SuppressWarnings("unchecked")
	public Map<String, List> getMsgReceiversWithAgentMap(MtMeeting bean) throws BusinessException {
		return this.getMsgReceiversWithAgentMap(bean.getConferees(), bean.getCreateUser(), bean.getEmceeId(), bean.getRecorderId());
	}
	
	/**
     * 获取会议消息发送对象Map：主持人、记录人、与会人员，其中的人员如果设定了代理人员，也将代理人员加入
     */
    @SuppressWarnings("unchecked")
	public Map<String, List> getMsgReceiversWithAgentMap(String confereesStr, Long creatUser, Long emcee, Long recorder) throws BusinessException {
    	Map<String, List> result = new HashMap<String, List>();
    	if(Strings.isNotBlank(confereesStr)) {
    		List<Long> ownerReceivers = new ArrayList<Long>();
    		List<Long> agentReceivers = new UniqueList<Long>();
    		List<MeetingAgent> mtAgents = new UniqueList<MeetingAgent>();
    		//与会对象
	    	List<Long> conferees = this.getConfereeIds(confereesStr, creatUser, emcee, recorder);
	    	if (conferees!=null) {
	    		for(Long memberId : conferees) {
		    		ownerReceivers.add(memberId);
		    		Long agent = this.processingAgent(memberId, agentReceivers);
		    		if(agent != null)
		    			mtAgents.add(new MeetingAgent(agent, memberId));
		    	}
			}
	    	//主持人
//	    	if(!emcee.equals(creatUser)) {
	    	if(!ownerReceivers.contains(emcee)) {
	    		ownerReceivers.add(emcee);
	    		Long agent = this.processingAgent(emcee, agentReceivers);
	    		if(agent != null)
	    			mtAgents.add(new MeetingAgent(agent, emcee));
	    	}
	    	
	    	//记录人
//	    	if(!recorder.equals(creatUser) && !recorder.equals(emcee) && 
	    	if(!recorder.equals(emcee) && 
	    			recorder!=com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID) {
	    		ownerReceivers.add(recorder);
	    		Long agent = this.processingAgent(recorder, agentReceivers);
	    		if(agent != null)
	    			mtAgents.add(new MeetingAgent(agent, recorder));
	    	}
	    	result.put(Constants.ReceiverType.Owner.name(), ownerReceivers);
	    	result.put(Constants.ReceiverType.Agent.name(), agentReceivers);
	    	result.put(Constants.ReceiverType.AgentModel.name(), mtAgents);
    	}
    	return result;
    }
    
    private Long processingAgent(Long memberId, List<Long> agentReceivers) {
	    Long agentId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.meeting.key(), memberId);
		if(agentId != null) {
			agentReceivers.add(agentId);
		}
		return agentId;
    }
	
    /***
	 * 根据红杉树传过来的视频会议ID。查询A8系统视频会议实体
	 * @param meetingId
	 * @return MtMeeting
	 * @author radishlee 2011-11-3
	 */
	public List getMeetingByInfowarelabMeetingId(String meetingId) {
		return  mtMeetingDao.getMeetingByInfowarelabMeetingId(meetingId);
	}
	
    /***
	 * 根据协同ID。查询A8系统视频会议实体
	 * @param summaryId
	 * @return MtMeeting
	 * @author radishlee 2012-2-14
	 */
	public List getMeetingBySummaryId(Long summaryId) {
		return  mtMeetingDao.getMeetingBySummaryId(summaryId);
	}

	public List<MtMeeting> findAllMeetings4User(Long userId, Date beginDate, Date endDate) {
		List<MtMeeting> self_meetings = this.findAllMeetings(userId, beginDate, endDate, false);

		List<AgentModel> agentModels = Constants.getMeetingAgents();
		if (CollectionUtils.isNotEmpty(agentModels)) {
			for (AgentModel agent : agentModels) {
				if (agent != null && agent.getAgentId().equals(userId)) {
					List<MtMeeting> agent_meetings = this.findAllMeetings(agent.getAgentToId(), agent.getStartDate(), agent.getEndDate(), true);
					CommonTools.addAllIgnoreEmpty(self_meetings, agent_meetings);
				}
			}
			Collections.sort(self_meetings);
		}
		return self_meetings;
	}

	@SuppressWarnings("unchecked")
	private List<MtMeeting> findAllMeetings(Long userId, Date beginDate, Date endDate, boolean isAgent) {
		Map<String, Object> params = new HashMap<String, Object>();

		String select = "select " + SELECT_FIELD;
		String from = " from " + MtMeeting.class.getName() + " as mt " + ", " + MtConferee.class.getName() + " as mc ";

		StringBuffer where = new StringBuffer();
		where.append(" where mt.id=mc.meetingId and (mc.confereeId in (:domainIds) or mt.emceeId=:userId or mt.recorderId=:userId " + (isAgent ? "" : " or mt.createUser=:userId") + ") ");
		params.put("domainIds", this.getDomainIds(userId));
		params.put("userId", userId);

		where.append(" and mt.state>:state ");
		params.put("state", Constants.DATA_STATE_SAVE);

		if (beginDate != null) {
			where.append(" and mt.beginDate >= :beginDate ");
			params.put("beginDate", Datetimes.getTodayFirstTime(beginDate));
		}

		if (endDate != null) {
			where.append(" and mt.endDate < :endDate ");
			params.put("endDate", Datetimes.getTodayLastTime(endDate));
		}

		String hql = select + from + where.toString();
		return this.parseObjArray2Meetings(mtMeetingDao.find(hql, -1, -1, params));
	}

	public void setMtResourcesManager(MtResourcesManager mtResourcesManager) {
		this.mtResourcesManager = mtResourcesManager;
	}
	public void setMtSummaryTemplateManager(MtSummaryTemplateManager mtSummaryTemplateManager) {
		this.mtSummaryTemplateManager = mtSummaryTemplateManager;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setPartitionManager(PartitionManager partitionManager) {
		this.partitionManager = partitionManager;
	}
}