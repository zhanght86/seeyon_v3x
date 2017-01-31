package com.seeyon.v3x.exchange.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

public class EdocSendRecord extends BaseModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String subject; // 公文标题
	private String docType; // 公文种类
	private String docMark; // 公文文号	
	private String secretLevel; // 公文密级
	private String urgentLevel; // 紧急程度 
	private String sendUnit; // 发文单位
	private String issuer; // 签发人
	private Date issueDate; // 公文发起时间
	private int copies; // 印发份数
	private long edocId; // 公文ID	
	private long sendUserId; // 发文人 
	private Timestamp sendTime; // 交换签发日期	
	private long exchangeOrgId; // 交换单位ID|交换部门ID
	private int exchangeType; // 交换类型：单位交换|部门交换
	private Timestamp createTime; // 创建时间
	private int status; // 状态[待发送|已发送]
	private String sendedNames;//xiangfan 保存送往单位的名称字符串（主送+抄送+抄报） G6 SP1公文单位支持手写
	private Set<EdocSendDetail> sendDetails;
	private String sendedTypeIds;

	private String stepBackInfo;// 回退说明

	public String getStepBackInfo() {
		return stepBackInfo;
	}

	public void setStepBackInfo(String stepBackInfo) {
		this.stepBackInfo = stepBackInfo;
	}

	// 用于页面排序需求
	private List<EdocSendDetail> sendDetailList;
	
	private Integer contentNo;
	
	public Integer getContentNo()
	{
		return this.contentNo;
	}
	public String getSendedNames() {
		return sendedNames;
	}

	public void setSendedNames(String sendedNames) {
		this.sendedNames = sendedNames;
	}

	public void setContentNo(Integer contentNo)
	{
		this.contentNo=contentNo;
	}
	/**
	 * 此函数是为了兼容历史数据，以前没用sendedTypeIds字段，后增加的；
	 * sendedTypeIds有值的时候优先使用
	 * @return
	 */	
	public String getSendEntityNames()
	{
		
		String str="";
		if(sendedTypeIds!=null && !"".equals(sendedTypeIds))
		{
			//return getSendEntityNamesByTypeIds();
			str=Functions.showOrgEntities(sendedTypeIds,",");
			return str;
		}
		for(EdocSendDetail ed:sendDetails)
		{
			str+=ed.getRecOrgName();
			str+=V3xOrgEntity.ORG_ID_DELIMITER;
		}
		if(str.length()>0)
		{
			str=str.substring(0,str.length()-1);
		}
		return str;
	}
	
	// 不用持久化，页面显示  发送到单位的名字
	private String sendNames;
	private String sendUserNames;
	private String keywords;
	
	// 公文交换的单位名字显示，即exchangeOrgId对应的名字
	private String exchangeOrgName;
	
	public String getExchangeOrgName() {
		return exchangeOrgName;
	}

	public void setExchangeOrgName(String exchangeOrgName) {
		this.exchangeOrgName = exchangeOrgName;
	}

	public String getSendUserNames() {
		return sendUserNames;
	}

	public void setSendUserNames(String sendUserNames) {
		this.sendUserNames = sendUserNames;
	}

	public String getSendNames() {
		return sendNames;
	}

	public void setSendNames(String sendNames) {
		this.sendNames = sendNames;
	}

	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getDocType() {
		return docType;
	}
	
	public void setDocType(String docType) {
		this.docType = docType;
	}
	
	public String getDocMark() {
		return docMark;
	}
	
	public void setDocMark(String docMark) {
		this.docMark = docMark; 
	}
	
	public String getSecretLevel() {
		return secretLevel;
	}
	
	public void setSecretLevel(String secretLevel) {
		this.secretLevel = secretLevel;
	}
	
	public String getUrgentLevel() {
		return urgentLevel;
	}
	
	public void setUrgentLevel(String urgentLevel) {
		this.urgentLevel = urgentLevel;
	}
	
	public String getSendUnit() {
		return sendUnit;
	}
	
	public void setSendUnit(String sendUnit) {
		this.sendUnit = sendUnit;
	}
	
	public String getIssuer() {
		return issuer;
	}
	
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	
	public int getCopies() {
		return copies;
	}
	
	public void setCopies(int copies) {
		this.copies = copies;
	}
	
	public long getEdocId() {
		return edocId;
	}
	
	public void setEdocId(long edocId) {
		this.edocId = edocId;
	}
	 
	public long getSendUserId() {
		return sendUserId;
	}
	
	public void setSendUserId(long sendUserId) {
		this.sendUserId = sendUserId;
	}
	
	public Timestamp getSendTime() {
		return sendTime;
	}
	
	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}
	
	public Timestamp getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}	
	
	public long getExchangeOrgId() {
		return exchangeOrgId;
	}
	
	public void setExchangeOrgId(long exchangeOrgId) {
		this.exchangeOrgId = exchangeOrgId;
	}
	
	public int getExchangeType() {
		return exchangeType;
	}
	
	public void setExchangeType(int exchangeType) {
		this.exchangeType = exchangeType;
	}
	
	public Set<EdocSendDetail> getSendDetails() {		
		return sendDetails;
	}
	
	public void setSendDetails(Set<EdocSendDetail> sendDetails) {
		this.sendDetails = sendDetails;
	}

	public List<EdocSendDetail> getSendDetailList() {
		return sendDetailList;
	}

	public void setSendDetailList(List<EdocSendDetail> sendDetailList) {
		this.sendDetailList = sendDetailList;
	}
	public String getSendedTypeIds() {
		return sendedTypeIds;
	}
	public void setSendedTypeIds(String sendedTypeIds) {
		this.sendedTypeIds = sendedTypeIds;
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
	
}
