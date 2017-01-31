package com.seeyon.v3x.edoc.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.util.IdentifierUtil;
import com.seeyon.v3x.webmail.util.DateUtil;

/**
 * 公文登记对象
 * @author 唐桂林 2011.09.27
 */
public class EdocRegister extends BaseModel implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 标志位, 共100位，采用枚举的自然顺序
     */
    protected static enum INENTIFIER_INDEX{
        HAS_ATTACHMENTS, // 是否有附件
    };

    /** 标志位 */
    private String identifier;

    /** 签收单ID */
    private long recieveId;

    /** 来文公文ID */
    private long edocId;

    /** 登记单类型 0发文登记 1收文登记 */
    private int edocType;

    /** 登记方式 1电子公文登记 2纸质公文登记 3纸质公文登记 */
    private int registerType;

    /** 创建人Id */
    private long createUserId;

    /** 创建人名字 */
    private String createUserName;

    /** 创建时间 */
    private java.sql.Timestamp createTime;

    /** 修改时间 */
    private java.sql.Timestamp updateTime;

    /** 来文单位 */
    private String sendUnit;

    /** 来文单位id */
    private long sendUnitId;

    /** 来文类型 1内部单位 2外部单位 */
    private int sendUnitType;

    /** 成文单位 */
    private String edocUnit;

    /** 成文单位id */
    private String edocUnitId;

    /** 成文日期 发文最后一个签发节点的处理日期,如果没有签发节点,用封发日期 */
    private java.sql.Date edocDate;

    /** 登记人id */
    private long registerUserId;

    /** 登记人 */
    private String registerUserName;

    /** 登记日期 */
    private java.sql.Date registerDate;

    /** 签发人id */
    private long issuerId;

    /** 签发人 */
    private String issuer;

    /** 签发日期 */
    private java.sql.Date issueDate;

    /** 会签人 */
    private String signer;

    /** 分发人id */
    private long distributerId = -1;
    

	 /** 是否代理 */
    private boolean proxy;
    
    private Long proxyId;

    /** 代理人 */
    private String proxyName;
    
    private String proxyLabel;

    /** 代理人 Id*/
    private Long proxyUserId;
 
	/** 分发人 */
    private String distributer;

    /** 分发时间 */
    private java.sql.Date distributeDate;

    /** 分发状态 0草稿箱 1待分发 2已分发 */
    private int distributeState;
    
    /** 分发关联公文id */
    private long distributeEdocId;

	/** 标题 */
    private String subject;

    /** 公文类型-来自系统枚举值 */
    private String docType;

    /** 行文类型- 来自系统枚举值 */
    private String sendType;

    /** 来文字号 */
    private String docMark;

    /** 收文编号 */
    private String serialNo;

    /** 文件密级 */
    private String secretLevel;

    /** 紧急程度 */
    private String urgentLevel;

    /** 保密期限 */
    private String keepPeriod;

    /** 主送单位 */
    private String sendTo;

    /** 主送单位id */
    private String sendToId;

    /** 抄送单位 */
    private String copyTo;

    /** 主送单位id */
    private String copyToId;

    /** 主题词 */
    private String keywords;

    /** 印发份数 */
    private int copies;

    /** 附注 */
    private String noteAppend;

    /** 附件说明 */
    private String attNote;

    /** 登记状态 0草稿箱 1待登记 2已登记 3退回给签收 4被退回 5删除 */
    private int state;

    /** 登记单位 */
    private long orgAccountId;

    /** 签收时间  */
    private java.sql.Timestamp recTime;
    
    /** 交换机关类型 0部门 1单位(非数据库字段) */
    private int exchangeType;

    /** 交换机关id (非数据库字段) */
    private long exchangeOrgId;
    
    /** 是否有附件 */
    private boolean hasAttachments;

    /** 附件 */
    private List<Attachment> attachmentList = new ArrayList<Attachment>();

    /** 正文 */
    private RegisterBody registerBody = null;
    
    private int isRetreat;//是否被退回
    
    public String getProxyLabel() {
		return proxyLabel;
	}

	public void setProxyLabel(String proxyLabel) {
		this.proxyLabel = proxyLabel;
	}

	public int getIsRetreat() {
		return isRetreat;
	}

	public void setIsRetreat(int isRetreat) {
		this.isRetreat = isRetreat;
	}

	public Long getProxyId() {
		return proxyId;
	}

	public void setProxyId(Long proxyId) {
		this.proxyId = proxyId;
	}

	public void bind(HttpServletRequest request) {
        this.setId(request.getParameter("id") == null ? -1L : Long.parseLong(request.getParameter("id")));
        this.setIdentifier(request.getParameter("identifier") == null ? "00000000000000000000" : request.getParameter("identifier"));
        this.setEdocId(request.getParameter("edocId") == null ? -1L : Long.parseLong(request.getParameter("edocId")));
        this.setEdocType(request.getParameter("edocType") == null ? 1 : Integer.parseInt(request.getParameter("edocType")));
        this.setRecieveId(request.getParameter("recieveId") == null ? -1L : Long.parseLong(request.getParameter("recieveId")));
        this.setRegisterType(request.getParameter("registerType") == null ? 1 : Integer.parseInt(request.getParameter("registerType")));
        this.setCreateUserId(request.getParameter("createUserId") == null ? -1L : Long.parseLong(request.getParameter("createUserId")));
        this.setCreateUserName(request.getParameter("createUserName") == null ? "" : request.getParameter("createUserName"));
        this.setCreateTime(request.getParameter("createTime") == null ? new java.sql.Timestamp(new java.util.Date().getTime()) : Timestamp.valueOf(request.getParameter("createTime")));
        this.setUpdateTime(request.getParameter("updateTime") == null ? null : Timestamp.valueOf(request.getParameter("updateTime")));
        this.setSendUnit(request.getParameter("sendUnit") == null ? "" : request.getParameter("sendUnit"));
        this.setSendUnitId(request.getParameter("sendUnitId") == null ? -1L : Long.parseLong(request.getParameter("sendUnitId")));
        this.setSendUnitType(request.getParameter("sendUnitType") == null ? 1 : Integer.parseInt(request.getParameter("sendUnitType")));
        this.setEdocUnit(request.getParameter("edocUnit") == null ? "" : request.getParameter("edocUnit"));
        this.setEdocUnitId(request.getParameter("edocUnitId") == null ? "" : request.getParameter("edocUnitId"));        
        this.setRegisterUserId(request.getParameter("registerUserId") == null ? -1L : Long.parseLong(request.getParameter("registerUserId")));
        this.setRegisterUserName(request.getParameter("registerUserName") == null ? "" : request.getParameter("registerUserName"));
        this.setIssuerId(request.getParameter("issuerId") == null ? -1L : Long.parseLong(request.getParameter("issuerId")));
        this.setIssuer(request.getParameter("issuer") == null ? "" : request.getParameter("issuer"));
        java.sql.Date date = null;
        if(request.getParameter("edocDate")!=null && !"".equals(request.getParameter("edocDate"))) {
            date = new java.sql.Date(DateUtil.getDate(request.getParameter("edocDate")+" 00:00:00").getTime());
        }
        this.setEdocDate(date);
        date = null;
        if(request.getParameter("registerDate")!=null && !"".equals(request.getParameter("registerDate"))) {
            date = new java.sql.Date(DateUtil.getDate(request.getParameter("registerDate")+" 00:00:00").getTime());
        }
        this.setRegisterDate(date);
        date = null;
        if(request.getParameter("issueDate")!=null && !"".equals(request.getParameter("issueDate"))) {
            date = new java.sql.Date(DateUtil.getDate(request.getParameter("issueDate")+" 00:00:00").getTime());
        }
        this.setIssueDate(date);
        date = null;
        if(request.getParameter("distributeDate")!=null && !"".equals(request.getParameter("distributeDate"))) {
            date = new java.sql.Date(DateUtil.getDate(request.getParameter("distributeDate")+" 00:00:00").getTime());
        }
        this.setDistributeDate(date);
        this.setRecTime(request.getParameter("recTime") == null||"".equals(request.getParameter("recTime"))  ? null : Timestamp.valueOf(request.getParameter("recTime")));
        this.setSigner(request.getParameter("signer") == null ? "" : request.getParameter("signer"));
        this.setDistributerId(request.getParameter("distributerId") == null ? -1L : Long.parseLong(request.getParameter("distributerId")));
        this.setDistributer(request.getParameter("distributer") == null ? "" : request.getParameter("distributer"));
        this.setDistributeState(request.getParameter("distributeState") == null ? 0 : Integer.parseInt(request.getParameter("distributeState")));
        this.setDistributeEdocId(request.getParameter("distributeEdocId") == null ? -1 : Integer.parseInt(request.getParameter("distributeEdocId")));
        this.setSubject(request.getParameter("subject") == null ? "" : request.getParameter("subject"));
        this.setDocType(request.getParameter("docType") == null ? "1" : request.getParameter("docType"));
        this.setSendType(request.getParameter("sendType") == null ? "1" : request.getParameter("sendType"));
        this.setDocMark(request.getParameter("docMark") == null ? "" : request.getParameter("docMark"));
        this.setSerialNo(request.getParameter("serialNo") == null ? "" : request.getParameter("serialNo"));
        this.setSecretLevel(request.getParameter("secretLevel") == null ? "1" : request.getParameter("secretLevel"));
        this.setUrgentLevel(request.getParameter("urgentLevel") == null ? "1" : request.getParameter("urgentLevel"));
        this.setKeepPeriod(request.getParameter("keepPeriod") == null ? "1" : request.getParameter("keepPeriod"));
        this.setSendTo(request.getParameter("sendTo") == null ? "" : request.getParameter("sendTo"));
        this.setSendToId(request.getParameter("sendToId") == null ? "" : request.getParameter("sendToId"));
        this.setCopyTo(request.getParameter("copyTo") == null ? "" : request.getParameter("copyTo"));
        this.setCopyToId(request.getParameter("copyToId") == null ? "" : request.getParameter("copyToId"));
        this.setKeywords(request.getParameter("keywords") == null ? "" : request.getParameter("keywords"));
        this.setCopies(request.getParameter("copies") == null ? 0 : Integer.parseInt(request.getParameter("copies")));
        this.setNoteAppend(request.getParameter("noteAppend") == null ? "" : request.getParameter("noteAppend"));
        this.setAttNote(request.getParameter("attNote") == null ? "" : request.getParameter("attNote"));
        this.setState(request.getParameter("state") == null ? 1 : Integer.parseInt(request.getParameter("state")));
        this.setOrgAccountId(request.getParameter("orgAccountId") == null ? -1L : Long.parseLong(request.getParameter("orgAccountId")));
    }

    /**
     * @return distributeEdocId
     */
    public long getDistributeEdocId() {
		return distributeEdocId;
	}

	/**
	 * @param distributeEdocId
	 */
	public void setDistributeEdocId(long distributeEdocId) {
		this.distributeEdocId = distributeEdocId;
	}
    
    /**
     * @return the recTime
     */
    public java.sql.Timestamp getRecTime() {
        return recTime;
    }

    /**
     * @param recTime the recTime to set
     */
    public void setRecTime(java.sql.Timestamp recTime) {
        this.recTime = recTime;
    }

    /**
     * @return the exchangeType
     */
    public int getExchangeType() {
        return exchangeType;
    }

    /**
     * @param exchangeType the exchangeType to set
     */
    public void setExchangeType(int exchangeType) {
        this.exchangeType = exchangeType;
    }

    /**
     * @return the exchangeOrgId
     */
    public long getExchangeOrgId() {
        return exchangeOrgId;
    }

	  public Long getProxyUserId() {
			return proxyUserId;
		}

		public void setProxyUserId(Long proxyUserId) {
			this.proxyUserId = proxyUserId;
		}

    /**
     * @param exchangeOrgId the exchangeOrgId to set
     */
    public void setExchangeOrgId(long exchangeOrgId) {
        this.exchangeOrgId = exchangeOrgId;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the recieveId
     */
    public long getRecieveId() {
        return recieveId;
    }

    /**
     * @param recieveId the recieveId to set
     */
    public void setRecieveId(long recieveId) {
        this.recieveId = recieveId;
    }

    /**
     * @return the edocId
     */
    public long getEdocId() {
        return edocId;
    }

    /**
     * @param edocId the edocId to set
     */
    public void setEdocId(long edocId) {
        this.edocId = edocId;
    }

    /**
     * @return the edocType
     */
    public int getEdocType() {
        return edocType;
    }

    /**
     * @param edocType the edocType to set
     */
    public void setEdocType(int edocType) {
        this.edocType = edocType;
    }

    /**
     * @return the registerType
     */
    public int getRegisterType() {
        return registerType;
    }

    /**
     * @param registerType the registerType to set
     */
    public void setRegisterType(int registerType) {
        this.registerType = registerType;
    }

    /**
     * @return the createUserId
     */
    public long getCreateUserId() {
        return createUserId;
    }

    /**
     * @param createUserId the createUserId to set
     */
    public void setCreateUserId(long createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * @return the createUserName
     */
    public String getCreateUserName() {
        return createUserName;
    }

    /**
     * @param createUserName the createUserName to set
     */
    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    /**
     * @return the createTime
     */
    public java.sql.Timestamp getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the updateTime
     */
    public java.sql.Timestamp getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime the updateTime to set
     */
    public void setUpdateTime(java.sql.Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the sendUnit
     */
    public String getSendUnit() {
        return sendUnit;
    }

    /**
     * @param sendUnit the sendUnit to set
     */
    public void setSendUnit(String sendUnit) {
        this.sendUnit = sendUnit;
    }

    /**
     * @return the sendUnitId
     */
    public long getSendUnitId() {
        return sendUnitId;
    }

    /**
     * @param sendUnitId the sendUnitId to set
     */
    public void setSendUnitId(long sendUnitId) {
        this.sendUnitId = sendUnitId;
    }

    /**
     * @return the sendUnitType
     */
    public int getSendUnitType() {
        return sendUnitType;
    }

    /**
     * @param sendUnitType the sendUnitType to set
     */
    public void setSendUnitType(int sendUnitType) {
        this.sendUnitType = sendUnitType;
    }

    /**
     * @return the edocUnit
     */
    public String getEdocUnit() {
        return edocUnit;
    }

    /**
     * @param edocUnit the edocUnit to set
     */
    public void setEdocUnit(String edocUnit) {
        this.edocUnit = edocUnit;
    }

    /**
     * @return the edocUnitId
     */
    public String getEdocUnitId() {
        return edocUnitId;
    }

    /**
     * @param edocUnitId the edocUnitId to set
     */
    public void setEdocUnitId(String edocUnitId) {
        this.edocUnitId = edocUnitId;
    }

    /**
     * @return the edocDate
     */
    public java.sql.Date getEdocDate() {
        return edocDate;
    }

    /**
     * @param edocDate the edocDate to set
     */
    public void setEdocDate(java.sql.Date edocDate) {
        this.edocDate = edocDate;
    }

    /**
     * @return the registerUserId
     */
    public long getRegisterUserId() {
        return registerUserId;
    }

    /**
     * @param registerUserId the registerUserId to set
     */
    public void setRegisterUserId(long registerUserId) {
        this.registerUserId = registerUserId;
    }

    /**
     * @return the registerUserName
     */
    public String getRegisterUserName() {
        return registerUserName;
    }

    /**
     * @param registerUserName the registerUserName to set
     */
    public void setRegisterUserName(String registerUserName) {
        this.registerUserName = registerUserName;
    }

    /**
     * @return the registerDate
     */
    public java.sql.Date getRegisterDate() {
        return registerDate;
    }

    /**
     * @param registerDate the registerDate to set
     */
    public void setRegisterDate(java.sql.Date registerDate) {
        this.registerDate = registerDate;
    }

    /**
     * @return the signer
     */
    public String getSigner() {
        return signer;
    }

    /**
     * @param signer the signer to set
     */
    public void setSigner(String signer) {
        this.signer = signer;
    }

    /**
     * @return the issuer
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * @param issuer the issuer to set
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * @return the distributerId
     */
    public long getDistributerId() {
        return distributerId;
    }

    /**
     * @param distributerId the distributerId to set
     */
    public void setDistributerId(long distributerId) {
        this.distributerId = distributerId;
    }

    /**
     * @return the distributer
     */
    public String getDistributer() {
        return distributer;
    }

    /**
     * @param distributer the distributer to set
     */
    public void setDistributer(String distributer) {
        this.distributer = distributer;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the docType
     */
    public String getDocType() {
        return docType;
    }

    /**
     * @param docType the docType to set
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }

    /**
     * @return the sendType
     */
    public String getSendType() {
        return sendType;
    }

    /**
     * @param sendType the sendType to set
     */
    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    /**
     * @return the docMark
     */
    public String getDocMark() {
        return docMark;
    }

    /**
     * @param docMark the docMark to set
     */
    public void setDocMark(String docMark) {
        this.docMark = docMark;
    }

    /**
     * @return the serialNo
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * @param serialNo the serialNo to set
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * @return the secretLevel
     */
    public String getSecretLevel() {
        return secretLevel;
    }

    /**
     * @param secretLevel the secretLevel to set
     */
    public void setSecretLevel(String secretLevel) {
        this.secretLevel = secretLevel;
    }

    /**
     * @return the urgentLevel
     */
    public String getUrgentLevel() {
        return urgentLevel;
    }

    /**
     * @param urgentLevel the urgentLevel to set
     */
    public void setUrgentLevel(String urgentLevel) {
        this.urgentLevel = urgentLevel;
    }

    /**
     * @return the keepPeriod
     */
    public String getKeepPeriod() {
        return keepPeriod;
    }

    /**
     * @param keepPeriod the keepPeriod to set
     */
    public void setKeepPeriod(String keepPeriod) {
        this.keepPeriod = keepPeriod;
    }

    /**
     * @return the sendTo
     */
    public String getSendTo() {
        return sendTo;
    }

    /**
     * @param sendTo the sendTo to set
     */
    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    /**
     * @return the sendToId
     */
    public String getSendToId() {
        return sendToId;
    }

    /**
     * @param sendToId the sendToId to set
     */
    public void setSendToId(String sendToId) {
        this.sendToId = sendToId;
    }

    /**
     * @return the copyTo
     */
    public String getCopyTo() {
        return copyTo;
    }

    /**
     * @param copyTo the copyTo to set
     */
    public void setCopyTo(String copyTo) {
        this.copyTo = copyTo;
    }

    /**
     * @return the copyToId
     */
    public String getCopyToId() {
        return copyToId;
    }

    /**
     * @param copyToId the copyToId to set
     */
    public void setCopyToId(String copyToId) {
        this.copyToId = copyToId;
    }

    /**
     * @return the keywords
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * @param keywords the keywords to set
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * @return the copies
     */
    public int getCopies() {
        return copies;
    }

    /**
     * @param copies the copies to set
     */
    public void setCopies(int copies) {
        this.copies = copies;
    }

    /**
     * @return the noteAppend
     */
    public String getNoteAppend() {
        return noteAppend;
    }

    /**
     * @param noteAppend the noteAppend to set
     */
    public void setNoteAppend(String noteAppend) {
        this.noteAppend = noteAppend;
    }

    /**
     * @return the attNote
     */
    public String getAttNote() {
        return attNote;
    }

    /**
     * @param attNote the attNote to set
     */
    public void setAttNote(String attNote) {
        this.attNote = attNote;
    }

    /**
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * @return the orgAccountId
     */
    public long getOrgAccountId() {
        return orgAccountId;
    }

    /**
     * @return the issuerId
     */
    public long getIssuerId() {
        return issuerId;
    }

    /**
     * @param issuerId the issuerId to set
     */
    public void setIssuerId(long issuerId) {
        this.issuerId = issuerId;
    }

    /**
     * @return the issueDate
     */
    public java.sql.Date getIssueDate() {
        return issueDate;
    }

    /**
     * @param issueDate the issueDate to set
     */
    public void setIssueDate(java.sql.Date issueDate) {
        this.issueDate = issueDate;
    }
    
  
    

	public boolean isProxy() {
    	return proxy;
    }

    public void setProxy(boolean proxy) {
    	this.proxy = proxy;
    }

    public String getProxyName() {
    	return proxyName;
    }

    public void setProxyName(String proxyName) {
    	this.proxyName = proxyName;
    }

    /**
     * @param orgAccountId the orgAccountId to set
     */
    public void setOrgAccountId(long orgAccountId) {
        this.orgAccountId = orgAccountId;
    }

    /**
     * @return the distributeDate
     */
    public java.sql.Date getDistributeDate() {
        return distributeDate;
    }

    /**
     * @param distributeDate the distributeDate to set
     */
    public void setDistributeDate(java.sql.Date distributeDate) {
        this.distributeDate = distributeDate;
    }

    /**
     * @return the distributeState
     */
    public int getDistributeState() {
        return distributeState;
    }

    /**
     * @param distributeState the distributeState to set
     */
    public void setDistributeState(int distributeState) {
        this.distributeState = distributeState;
    }

    /**
     * @return the attachmentList
     */
    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    /**
     * @param attachmentList the attachmentList to set
     */
    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    /**
     * @return the edocBodyList
     */
    public RegisterBody getRegisterBody() {
        return registerBody;
    }

    /**
     * @param edocBody the edocBody to set
     */
    public void setRegisterBody(RegisterBody registerBody) {
        this.registerBody = registerBody;
    }
    
    public boolean getHasAttachments() {
        return IdentifierUtil.lookupInner(identifier, EdocSummary.INENTIFIER_INDEX.HAS_ATTACHMENTS.ordinal(), '1');
    }

    public void setHasAttachments(boolean hasAttachments) {
    	this.hasAttachments = hasAttachments;
        this.identifier = IdentifierUtil.update(this.getIdentifier(), EdocSummary.INENTIFIER_INDEX.HAS_ATTACHMENTS.ordinal(), hasAttachments ? '1' : '0');
    }
    
}
