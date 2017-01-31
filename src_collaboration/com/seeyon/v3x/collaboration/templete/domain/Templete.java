package com.seeyon.v3x.collaboration.templete.domain;

import java.io.Serializable;
import java.util.HashSet;

import com.seeyon.v3x.util.IdentifierUtil;

/**
 * The persistent class for the v3x_templete database table.
 * 
 * @author BEA Workshop Studio
 */
public class Templete extends com.seeyon.v3x.common.domain.BaseModel implements Serializable {
	private static final long serialVersionUID = 5789038317284722320L;
	
	public static final String ENTITY_NAME = Templete.class.getName();

	public static final String PROP_body = "body";

	public static final String PROP_categoryId = "categoryId";

	public static final String PROP_createDate = "createDate";

	public static final String PROP_description = "description";

	public static final String PROP_memberId = "memberId";

	public static final String PROP_orgAccountId = "orgAccountId";

	public static final String PROP_sort = "sort";

	public static final String PROP_state = "state";

	public static final String PROP_subject = "subject";

	public static final String PROP_summary = "summary";

	public static final String PROP_type = "type";

	public static final String PROP_bodyType = "bodyType";
	
	public static final String PROP_isSystem = "isSystem";
	
	public static final String PROP_projectId = "projectId";
	
	public static final String PROP_categoryType = "categoryType";
	
	public static final String PROP_standardDuration = "standardDuration";
	
	protected static final int INENTIFIER_SIZE = 20;

	/**
	 * 标志位, 共100位，采用枚举的自然顺序
	 */
	protected static enum INENTIFIER_INDEX {
		HAS_ATTACHMENTS, // 是否有附件
	};

	private String identifier;
	
	/**
	 * 模板类型：正文、流程、模板（正文+流程） 在数据库中存name()
	 *
	 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
	 * @version 1.0 2007-3-28
	 */
	public static enum Type{
		text, workflow, templete
	}
	
	/**
	 * 模板状态: 正常 在数据库中村ordinal值
	 *
	 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
	 * @version 1.0 2007-3-28
	 */
	public static enum State{
		normal, //正常
		invalidation, //不可用
	}

	private String bodyType;

	private String body;

	private Long categoryId;

	private java.sql.Timestamp createDate;

	private String description;

	/**
	 * 系统模板用0,个人模板为人的id
	 */
	private Long memberId;

	private Long orgAccountId;

	private Integer sort = 9999;

	private Integer state = State.normal.ordinal();

	private String subject;

	/**
	 * 序列化成XML
	 */
	private String summary;

	/**
	 * 模板类型：正文、流程、模板（正文+流程）
	 */
	private String type;
	
	/**
	 * 基准时间
	 */
	private Integer standardDuration;
	

	private Boolean isSystem = true;
	
	private String workflowRule;

	private java.util.Set<TempleteAuth> templeteAuths;
	
	private String workflow;
	
	private Long projectId;
	
	private Integer categoryType;
	
    //模板编号（用于外部系统调用）
	private String templeteNumber;
	
	/**
	 * 表单另存个人模版的父模版id
	 */	
	private Long formParentId; 

	private String collSubject = "" ;
	
	//子类型，目前只用于公文发文
	private Long subCategoryType;
	//成发集团项目 流程密级
	private Integer secretLevel;

	
	public Integer getSecretLevel() {
		return secretLevel;
	}

	public void setSecretLevel(Integer secretLevel) {
		this.secretLevel = secretLevel;
	}

	public Long getSubCategoryType() {
		return subCategoryType;
	}

	public void setSubCategoryType(Long subCategoryType) {
		this.subCategoryType = subCategoryType;
	}

	public Long getFormParentId() {
		return formParentId;
	}

	public void setFormParentId(Long formParentId) {
		this.formParentId = formParentId;
	}

	public Templete() {
	}
	
	public Templete(Long id, String subject, Integer sort) {
		super.setId(id);
		this.subject = subject;
		this.sort = sort;
	}
	
	public Templete(Long id, String subject, Long memberId, Integer sort) {
		this(id, subject, sort);
		this.memberId = memberId;
	}
	
	/**
	 * 表单模板对应的表单名称，此属性不持久化
	 */
	private String formAppName;

	public String getFormAppName() {
		return formAppName;
	}
	public void setFormAppName(String formAppName) {
		this.formAppName = formAppName;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Long getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public java.sql.Timestamp getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getMemberId() {
		return this.memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getOrgAccountId() {
		return this.orgAccountId;
	}

	public void setOrgAccountId(Long orgAccountId) {
		this.orgAccountId = orgAccountId;
	}

	public Integer getSort() {
		return this.sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getState() {
		return this.state;
	}
	
	public String getStateLabel(){
		return Templete.State.values()[state].name();
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public java.util.Set<TempleteAuth> getTempleteAuths() {
		if(this.templeteAuths == null){
			this.templeteAuths = new HashSet<TempleteAuth>();
		}
		
		return this.templeteAuths;
	}

	public void setTempleteAuths(java.util.Set<TempleteAuth> templeteAuths) {
		this.templeteAuths = templeteAuths;
	}

	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public Boolean getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(Boolean isSystem) {
		this.isSystem = isSystem;
	}
	
	public String getIdentifier() {
		return IdentifierUtil.newIdentifier(this.identifier, INENTIFIER_SIZE,
				'0');
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public boolean isHasAttachments() {
		return IdentifierUtil.lookupInner(identifier, INENTIFIER_INDEX.HAS_ATTACHMENTS.ordinal(), '1');
	}

	public void setHasAttachments(boolean hasAttachments) {
		this.identifier = IdentifierUtil.update(this.getIdentifier(), INENTIFIER_INDEX.HAS_ATTACHMENTS.ordinal(), '1');
	}

	public String getWorkflowRule() {
		return workflowRule;
	}

	public void setWorkflowRule(String workflowRule) {
		this.workflowRule = workflowRule;
	}
	
	public String getWorkflow() {
		return this.workflow;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Integer getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(Integer categoryType) {
		this.categoryType = categoryType;
	}

    public String getTempleteNumber() {
        return templeteNumber;
    }

    public void setTempleteNumber(String templeteNumber) {
        this.templeteNumber = templeteNumber;
    }

	public String getCollSubject() {
		return collSubject;
	}

	public void setCollSubject(String collSubject) {
		this.collSubject = collSubject;
	}
	public Integer getStandardDuration() {
		return standardDuration;
	}

	public void setStandardDuration(Integer standardDuration) {
		this.standardDuration = standardDuration;
	}

}