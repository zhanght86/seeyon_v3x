package com.seeyon.v3x.mobile.webmodel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColComment;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.permission.domain.Permission;

/**
 * 协同细节
 * 
 */
public class Collaboration {
	private Long id;// 个人事项ID
	
	private Long summaryId;//summaryId

	private String title;

	private String content;

	private Long createrOr; // 创建者ID

	private List<ColOpinion> opinions;
	
	List<ColOpinion> senderOpinion = null;

	/**
	 * key是ColOpinion-id
	 */
	private Map<Long, List<ColComment>> comments;

	private FlowChart flowchart;

	private int state;

	private boolean hasAttachments;

	private Date createtime;
	
	private String processId;
	
	private String contentType;
	
	private boolean power;//是否可以处理表单
	
	private String formURL;//表单的URL
	
	private String sign;
	
	private Long templeteId;
	
    //原协同的处理人意�?
    java.util.Map<Integer, List<ColOpinion>> originalSignOpinion = null;
    //原协同的发起人附言
    java.util.Map<Integer, List<ColOpinion>> originalSendOpinion = null;
    
    List<Integer> originalSendOpinionKey = null;
    
    private Map<Long,List<Attachment>> colAttachment = null;
    /**
     * 处理时：是否允许选择态度
     */
    private boolean isAllowAttitude = true;
    /**
     * 处理时：是否允许填写意见
     */
    private boolean isAllowOpinion = true;
    
    /**
     * 节点权限
     */
    private String nodePermissionPolicy;

    private ColBody body;
    private Long orgAccountId;
    private Permission permission;
	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public Long getOrgAccountId() {
		return orgAccountId;
	}

	public void setOrgAccountId(Long orgAccountId) {
		this.orgAccountId = orgAccountId;
	}

	public ColBody getBody() {
		return body;
	}

	public void setBody(ColBody body) {
		this.body = body;
	}

	public String getNodePermissionPolicy() {
		return nodePermissionPolicy;
	}

	public void setNodePermissionPolicy(String nodePermissionPolicy) {
		this.nodePermissionPolicy = nodePermissionPolicy;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public boolean isHasAttachments() {
		return hasAttachments;
	}

	public void setHasAttachments(boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}

	public Map<Long, List<ColComment>> getComments() {
		return comments;
	}

	public void setComments(Map<Long, List<ColComment>> comments) {
		this.comments = comments;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getCreaterOr() {
		return createrOr;
	}

	public void setCreaterOr(Long createrOr) {
		this.createrOr = createrOr;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public FlowChart getFlowchart() {
		return flowchart;
	}

	public void setFlowchart(FlowChart flowchart) {
		this.flowchart = flowchart;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ColOpinion> getOpinions() {
		return opinions;
	}

	public void setOpinions(List<ColOpinion> opinions) {
		this.opinions = opinions;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public java.util.Map<Integer, List<ColOpinion>> getOriginalSendOpinion() {
		return originalSendOpinion;
	}

	public void setOriginalSendOpinion(
			java.util.Map<Integer, List<ColOpinion>> originalSendOpinion) {
		this.originalSendOpinion = originalSendOpinion;
	}

	public List<Integer> getOriginalSendOpinionKey() {
		return originalSendOpinionKey;
	}

	public void setOriginalSendOpinionKey(List<Integer> originalSendOpinionKey) {
		this.originalSendOpinionKey = originalSendOpinionKey;
	}

	public java.util.Map<Integer, List<ColOpinion>> getOriginalSignOpinion() {
		return originalSignOpinion;
	}

	public void setOriginalSignOpinion(
			java.util.Map<Integer, List<ColOpinion>> originalSignOpinion) {
		this.originalSignOpinion = originalSignOpinion;
	}

	public List<ColOpinion> getSenderOpinion() {
		return senderOpinion;
	}

	public void setSenderOpinion(List<ColOpinion> senderOpinion) {
		this.senderOpinion = senderOpinion;
	}

	public boolean isAllowAttitude() {
		return isAllowAttitude;
	}

	public void setAllowAttitude(boolean isAllowAttitude) {
		this.isAllowAttitude = isAllowAttitude;
	}

	public boolean isAllowOpinion() {
		return isAllowOpinion;
	}

	public void setAllowOpinion(boolean isAllowOpinion) {
		this.isAllowOpinion = isAllowOpinion;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFormURL() {
		return formURL;
	}

	public void setFormURL(String formURL) {
		this.formURL = formURL;
	}

	public boolean isPower() {
		return power;
	}

	public void setPower(boolean power) {
		this.power = power;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public Long getTempleteId() {
		return templeteId;
	}

	public void setTempleteId(Long templeteId) {
		this.templeteId = templeteId;
	}

	public Long getSummaryId() {
		return summaryId;
	}

	public void setSummaryId(Long summaryId) {
		this.summaryId = summaryId;
	}

	public Map<Long, List<Attachment>> getColAttachment() {
		return colAttachment;
	}

	public void setColAttachment(Map<Long, List<Attachment>> colAttachment) {
		this.colAttachment = colAttachment;
	}
}