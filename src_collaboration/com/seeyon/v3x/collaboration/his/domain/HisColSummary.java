package com.seeyon.v3x.collaboration.his.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.util.IdentifierUtil;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2012-1-7
 */
public class HisColSummary extends BaseModel implements java.io.Serializable {

	private static final long serialVersionUID = 5227451133336398666L;

	private Long archiveId;
	
	
	/**
	 * 表单预归档选择的视图操作id
	 */
	private String archiverFormid;

	/**
	 * 是否允许归档
	 */
	private Boolean canArchive = false;

	/**
	 * 是否周期提醒
	 * 
	 * @deprecated
	 */
	private Boolean canDueReminder = true;

	/**
	 * 是否允许转发
	 */
	private Boolean canForward = false;

	/**
	 * 是否允许改变流程
	 */
	private Boolean canModify = false;

	/**
	 * 是否允许跟踪
	 * 
	 * @deprecated
	 */
	private Boolean canTrack = true;

	/**
	 * 是否允许修改正文
	 */
	private Boolean canEdit = false;
	/**
	 * 是否允许在线编辑附件
	 */
    private boolean canEditAttachment = false;
	
	private Integer colType;

	private java.sql.Timestamp createDate;

	private Long deadline = 0L; // 默认�?

	private java.sql.Timestamp finishDate;

	private Integer importantLevel = 1; // 默认1 - 普�?

	private Boolean isAudited;

	private Long projectId;

	private Long remindInterval = -1L;

	private Long advanceRemind = -1L;

	private Integer resentTime;

	private java.sql.Timestamp startDate;

	private Integer state = Constant.flowState.run.ordinal();

	private String subject;

	private java.util.Set<HisColBody> bodies;

	private java.util.Set<HisColComment> comments;

	private java.util.Set<HisColOpinion> opinions;
	
	private String webServiceCode;
	
	private Long parentformSummaryId;//关联表单时的关联协同id
	
	//是否核定0：默认值;1核定通过;2核定不能过
	private Integer isVouch;
	//运行时长
	private Long runTime ;
	//超时时长
	private Long overTime;
	//运行时长
	private Long runWorkTime ;
	//超时时长
	private Long overWorkTime;
	

	/**
	 * 该属性是ORM的字�?
	 */
	private Long startMemberId;

	// private java.util.Set<Affair> affairs;

	private String processId;

	private Long caseId;
	
	/**
	 * 模版Id
	 */
	private Long templeteId;
	
	private String workflowRule;
	
	/**
	 * 多次转发，转发人用逗号隔开
	 */
	private String forwardMember;

	protected static final int INENTIFIER_SIZE = 20;

	/**
	 * 标志位, 共100位，采用枚举的自然顺序
	 */
	protected static enum INENTIFIER_INDEX {
		HAS_ATTACHMENTS, // 是否有附件
	};

	private String identifier;

	private HisColOpinion senderOpinion;
	private List<HisColOpinion> allSenderOpinion;
	
	private String bodyType;

	private Long formAppId;//表单id 
	
	private Long formId;//表单视图id
	
	private Long formRecordId;//表单主表记录id
	
	private Long orgAccountId;//单位Id
	
	private Long orgDepartmentId;//部门Id
	
	private Boolean worklfowTimeout = false;//流程超期，不持久化到数据库
	
    private String source; //协同来源（用于显示外部系统名称，如NC）
    
    private Integer newflowType = -1; //新流程类型，用于标记新流程
    
    private Long quoteformtemId;  //关联表单模板id
    
    private String quoteformtemName;  //关联表单模板名称

	public HisColSummary() {
	}

	public Long getArchiveId() {
		return this.archiveId;
	}

	public void setArchiveId(Long archiveId) {
		this.archiveId = archiveId;
	}

	public Long getCaseId() {
		return this.caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	public Integer getColType() {
		return this.colType;
	}

	public void setColType(Integer colType) {
		this.colType = colType;
	}

	public java.sql.Timestamp getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}

	public Long getDeadline() {
		return this.deadline;
	}

	public void setDeadline(Long deadline) {
		this.deadline = deadline;
	}

	public java.sql.Timestamp getFinishDate() {
		return this.finishDate;
	}

	public void setFinishDate(java.sql.Timestamp finishDate) {
		this.finishDate = finishDate;
	}

	public Integer getImportantLevel() {
		return this.importantLevel;
	}

	public void setImportantLevel(Integer importantLevel) {
		this.importantLevel = importantLevel;
	}

	public Long getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getRemindInterval() {
		return this.remindInterval;
	}

	public void setRemindInterval(Long remindInterval) {
		this.remindInterval = remindInterval;
	}

	public Integer getResentTime() {
		return this.resentTime;
	}

	public void setResentTime(Integer resentTime) {
		this.resentTime = resentTime;
	}

	public java.sql.Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(java.sql.Timestamp startDate) {
		this.startDate = startDate;
	}

	public Integer getState() {
		return this.state;
	}

	/**
	 * {@link com.seeyon.v3x.collaboration.Constant.flowState}
	 * @param state
	 */
	public void setState(Integer state) {
		this.state = state;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	// bi-directional many-to-one association to ColBody
	public java.util.Set<HisColBody> getBodies() {
		if (this.bodies == null)
			this.bodies = new HashSet<HisColBody>();
		return this.bodies;
	}

	public HisColBody getFirstBody() {
		if (this.bodies == null || this.bodies.size() == 0)
			return null;
		return this.bodies.iterator().next();
	}

	public void setBodies(java.util.Set<HisColBody> bodies) {
		this.bodies = bodies;
	}

	// bi-directional many-to-one association to ColComment
	public java.util.Set<HisColComment> getComments() {
		return this.comments;
	}

	public void setComments(java.util.Set<HisColComment> comments) {
		this.comments = comments;
	}

	/**
	 * 取得发起人附言 第一条
	 * 
	 * @return
	 */
	public HisColOpinion getSenderOpinion() {
		if (senderOpinion != null) {
			return senderOpinion;
		}

		if (this.getOpinions() != null) {
			for (HisColOpinion opinion : opinions) {
				if (opinion.getLevelId() == 0
						&& opinion.getOpinionType() == HisColOpinion.OpinionType.senderOpinion
								.ordinal()) {
					return opinion;
				}
			}
		}

		return null;
	}
	
	/**
	 * 取得发起人所有的附言
	 * 
	 * @return
	 */
	public List<HisColOpinion> getAllSenderOpinion() {
		if (allSenderOpinion != null) {
			return allSenderOpinion;
		}
		
		allSenderOpinion = new ArrayList<HisColOpinion>();

		if (this.getOpinions() != null) {
			for (HisColOpinion opinion : opinions) {
				if (opinion.getLevelId() == 0
						&& opinion.getOpinionType() == HisColOpinion.OpinionType.senderOpinion.ordinal()) {
					allSenderOpinion.add(opinion);
				}
			}
		}

		return allSenderOpinion;
	}

	/**
	 * 取得所有处理意�?
	 * 
	 * @return
	 */
	public java.util.List<HisColOpinion> getSignOpinion() {
		java.util.List<HisColOpinion> list = new java.util.ArrayList<HisColOpinion>();
		if (this.getOpinions() != null) {
			for (HisColOpinion opinion : opinions) {
				if (opinion.getLevelId() == 0
						&& opinion.getOpinionType() == HisColOpinion.OpinionType.signOpinion
								.ordinal()) {
					list.add(opinion);
				}
				else if (opinion.getLevelId() == 0
						&& opinion.getOpinionType() == HisColOpinion.OpinionType.senderOpinion
								.ordinal()) {
					senderOpinion = opinion;
				}
			}
		}

		return list;
	}

	// bi-directional many-to-one association to ColOpinion
	public java.util.Set<HisColOpinion> getOpinions() {
		if (opinions == null) {
			this.opinions = new HashSet<HisColOpinion>();
		}
		return this.opinions;
	}

	public void setOpinions(java.util.Set<HisColOpinion> opinions) {
		this.opinions = opinions;
		this.senderOpinion = null;
		this.allSenderOpinion = null;
	}

	// //bi-directional many-to-one association to Affair
	// public java.util.Set<Affair> getAffairs() {
	// return this.affairs;
	// }
	// public void setAffairs(java.util.Set<Affair> affairs) {
	// this.affairs = affairs;
	// }

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Boolean getCanArchive() {
		return canArchive;
	}

	public void setCanArchive(Boolean canArchive) {
		this.canArchive = canArchive;
	}

	public Boolean getCanDueReminder() {
		return canDueReminder;
	}

	public void setCanDueReminder(Boolean canDueReminder) {
		this.canDueReminder = canDueReminder;
	}

	public Boolean getCanForward() {
		return canForward;
	}

	public void setCanForward(Boolean canForward) {
		this.canForward = canForward;
	}

	public Boolean getCanModify() {
		return canModify;
	}

	public void setCanModify(Boolean canModify) {
		this.canModify = canModify;
	}

	public Boolean getCanTrack() {
		return canTrack;
	}

	public void setCanTrack(Boolean canTrack) {
		this.canTrack = canTrack;
	}

	public Boolean getIsAudited() {
		return isAudited;
	}

	public void setIsAudited(Boolean audited) {
		isAudited = audited;
	}

	public Long getStartMemberId() {
		return startMemberId;
	}

	public void setStartMemberId(Long startMemberId) {
		this.startMemberId = startMemberId;
	}

	public Boolean getCanEdit() {
		return canEdit;
	}

	public void setCanEdit(Boolean canEdit) {
		this.canEdit = canEdit;
	}

	public Long getAdvanceRemind() {
		return advanceRemind;
	}

	public void setAdvanceRemind(Long advanceRemind) {
		this.advanceRemind = advanceRemind;
	}

	public String getIdentifier() {
		return IdentifierUtil.newIdentifier(this.identifier, INENTIFIER_SIZE,
				'0');
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public boolean isHasAttachments() {
		return IdentifierUtil.lookupInner(identifier,
				HisColSummary.INENTIFIER_INDEX.HAS_ATTACHMENTS.ordinal(), '1');
	}

	public void setHasAttachments(boolean hasAttachments) {
		this.identifier = IdentifierUtil.update(this.getIdentifier(),
				HisColSummary.INENTIFIER_INDEX.HAS_ATTACHMENTS.ordinal(), hasAttachments ? '1' : '0');
	}

	public String getForwardMember() {
		return forwardMember;
	}

	public void setForwardMember(String forwardMember) {
		this.forwardMember = forwardMember;
	}
	
	/**
	 * 转发人员保留的人员个数
	 */
	private static int forwardMemberSize = 1;
	
	public void addForwardMember(long memberId){
		if(this.forwardMember == null || forwardMemberSize < 2){
			this.forwardMember = String.valueOf(memberId);
		}
		else{
			String[] forwardMembers = this.forwardMember.split(",");
			
			StringBuffer sb = new StringBuffer();
			
			int len = forwardMembers.length;
			if(len >= forwardMemberSize){ //只去最后几个
				for (int i = len - forwardMemberSize + 1; i < len; i++) {
					sb.append(forwardMembers[i]).append(",");
				}
			}
			else{
				sb.append(forwardMember).append(",");
			}
			
			sb.append(memberId);
			
			this.forwardMember = sb.toString();
		}
	}
	
	/**
	 * 流程是否结束
	 * @return
	 */
	public boolean isFinshed() {	
		return this.getFinishDate() != null;
	}

	public Long getTempleteId() {
		return templeteId;
	}

	public void setTempleteId(Long templeteId) {
		this.templeteId = templeteId;
	}
	public Boolean getCanEditAttachment() {
		return canEditAttachment;
	}

	public void setCanEditAttachment(Boolean canEditAttachment) {
		this.canEditAttachment = canEditAttachment;
	}
	public String getWebServiceCode() {
		return webServiceCode;
	}

	public void setWebServiceCode(String webServiceCode) {
		this.webServiceCode = webServiceCode;
	}

	public String getWorkflowRule() {
		return workflowRule;
	}

	public void setWorkflowRule(String workflowRule) {
		this.workflowRule = workflowRule;
	}

	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public Long getFormRecordId() {
		return formRecordId;
	}

	public void setFormRecordId(Long formRecordId) {
		this.formRecordId = formRecordId;
	}

	public Long getFormAppId() {
		return formAppId;
	}

	public void setFormAppId(Long formAppId) {
		this.formAppId = formAppId;
	}

	public Long getOrgAccountId() {
		return orgAccountId;
	}

	public void setOrgAccountId(Long orgAccountId) {
		this.orgAccountId = orgAccountId;
	}

	public Long getOrgDepartmentId() {
		return orgDepartmentId;
	}

	public void setOrgDepartmentId(Long orgDepartmentId) {
		this.orgDepartmentId = orgDepartmentId;
	}

	public Boolean getWorklfowTimeout() {
		return worklfowTimeout;
	}

	public void setWorklfowTimeout(Boolean worklfowTimeout) {
		this.worklfowTimeout = worklfowTimeout;
	}

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

	public String getArchiverFormid() {
		return archiverFormid;
	}

	public void setArchiverFormid(String archiverFormid) {
		this.archiverFormid = archiverFormid;
	}

	public Long getParentformSummaryId() {
		return parentformSummaryId;
	}

	public void setParentformSummaryId(Long parentformSummaryId) {
		this.parentformSummaryId = parentformSummaryId;
	}
    public Integer getNewflowType() {
        return newflowType;
    }
    public void setNewflowType(Integer newflowType) {
        this.newflowType = newflowType;
    }

	public Long getQuoteformtemId() {
		return quoteformtemId;
	}

	public void setQuoteformtemId(Long quoteformtemId) {
		this.quoteformtemId = quoteformtemId;
	}

	public String getQuoteformtemName() {
		return quoteformtemName;
	}

	public void setQuoteformtemName(String quoteformtemName) {
		this.quoteformtemName = quoteformtemName;
	}
	public Integer getIsVouch() {
		return isVouch;
	}

	public void setIsVouch(Integer isVouch) {
		this.isVouch = isVouch;
	}
	public Long getRunTime() {
		return runTime;
	}

	public void setRunTime(Long runTime) {
		this.runTime = runTime;
	}

	public Long getOverTime() {
		return overTime;
	}

	public void setOverTime(Long overTime) {
		this.overTime = overTime;
	}

	public Long getRunWorkTime() {
		return runWorkTime;
	}

	public void setRunWorkTime(Long runWorkTime) {
		this.runWorkTime = runWorkTime;
	}

	public Long getOverWorkTime() {
		return overWorkTime;
	}

	public void setOverWorkTime(Long overWorkTime) {
		this.overWorkTime = overWorkTime;
	}
}