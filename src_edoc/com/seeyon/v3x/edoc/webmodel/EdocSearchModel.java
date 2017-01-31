package com.seeyon.v3x.edoc.webmodel;

import java.util.Date;

public class EdocSearchModel  extends com.seeyon.v3x.common.ObjectToXMLBase{
	
	private int edocType;	//公文类型,收文,发文,签报
	private String subject;	//公文标题
	private String keywords;
	private String docMark;
	private String serialNo;	//内部文号 (收文编号)
	private String docType;		//公文种类
	private String sendType;	//行文类型
	private String createPerson;	//建文人
	private Date createTimeB;	//建文日期
	private Date createTimeE;	//建文日期
	private String sendUnit;	//发文单位
	private String sendTo;	//主送单位
	private String sendToId;	//主送单位
	private String issuer;	//签发人
	private Date signingDateA;	//签发日期
	private Date signingDateB;	//签发日期

	private String nodePolicy; //节点权限
	private Date createDateB;	//发文日期
	private Date createDateE;	//发文日期
	private String edocUnit; //成文单位
	private Date recieveDateB; //收文  签收时间
	private Date recieveDateE;
	private Date registerDateB;//收文 登记时间
	private Date registerDateE;
	private Date receiveTimeB; //到达时间
	private Date receiveTimeE;
	private String secretLevel; //文件密级
	private String urgentLevel; //紧急程度
	private String startMemberName; //发起人
	
	/**
	 * author wangwei  start
	 */
    private String party; //党务机关
    private String administrative; //政务机关
    
//    /**
//	 * author cy  start
//	 */
//    private String registerDateA;	//页面查询 登记时间
//    private String registerDateB;
    
	public String getParty() {
	return party;
	}
	public void setParty(String party) {
		this.party = party;
	}
	
	public String getAdministrative() {
		return administrative;
	}
	public void setAdministrative(String administrative) {
		this.administrative = administrative;
    }
	/**
	 * wangwei end
	 */

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


	public String getCreatePerson() {
		return createPerson;
	}


	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}


	public Date getCreateTimeB() {
		return createTimeB;
	}


	public void setCreateTimeB(Date createTimeB) {
		this.createTimeB = createTimeB;
	}


	public Date getCreateTimeE() {
		return createTimeE;
	}


	public void setCreateTimeE(Date createTimeE) {
		this.createTimeE = createTimeE;
	}


	public String getDocMark() {
		return docMark;
	}


	public void setDocMark(String docMark) {
		this.docMark = docMark;
	}


	public String getDocType() {
		return docType;
	}


	public void setDocType(String docType) {
		this.docType = docType;
	}


	public int getEdocType() {
		return edocType;
	}


	public void setEdocType(int edocType) {
		this.edocType = edocType;
	}


	public String getIssuer() {
		return issuer;
	}


	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}


	public String getKeywords() {
		return keywords;
	}


	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}


	public String getSendTo() {
		return sendTo;
	}


	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}


	public String getSendType() {
		return sendType;
	}


	public void setSendType(String sendType) {
		this.sendType = sendType;
	}


	public String getSendUnit() {
		return sendUnit;
	}


	public void setSendUnit(String sendUnit) {
		this.sendUnit = sendUnit;
	}


	public String getSerialNo() {
		return serialNo;
	}


	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}


	/**
	 * @return the signingDateA
	 */
	public Date getSigningDateA() {
		return signingDateA;
	}


	/**
	 * @param signingDateA the signingDateA to set
	 */
	public void setSigningDateA(Date signingDateA) {
		this.signingDateA = signingDateA;
	}


	/**
	 * @return the signingDateB
	 */
	public Date getSigningDateB() {
		return signingDateB;
	}


	/**
	 * @param signingDateB the signingDateB to set
	 */
	public void setSigningDateB(Date signingDateB) {
		this.signingDateB = signingDateB;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getSendToId() {
		return sendToId;
	}


	public void setSendToId(String sendToId) {
		this.sendToId = sendToId;
	}
	public String getNodePolicy() {
		return nodePolicy;
	}
	public void setNodePolicy(String nodePolicy) {
		this.nodePolicy = nodePolicy;
	}
	public Date getCreateDateB() {
		return createDateB;
	}
	public void setCreateDateB(Date createDateB) {
		this.createDateB = createDateB;
	}
	public Date getCreateDateE() {
		return createDateE;
	}
	public void setCreateDateE(Date createDateE) {
		this.createDateE = createDateE;
	}
	public String getEdocUnit() {
		return edocUnit;
	}
	public void setEdocUnit(String edocUnit) {
		this.edocUnit = edocUnit;
	}
	public Date getRecieveDateB() {
		return recieveDateB;
	}
	public void setRecieveDateB(Date recieveDateB) {
		this.recieveDateB = recieveDateB;
	}
	public Date getRecieveDateE() {
		return recieveDateE;
	}
	public void setRecieveDateE(Date recieveDateE) {
		this.recieveDateE = recieveDateE;
	}
	public Date getRegisterDateB() {
		return registerDateB;
	}
	public void setRegisterDateB(Date registerDateB) {
		this.registerDateB = registerDateB;
	}
	public Date getRegisterDateE() {
		return registerDateE;
	}
	public void setRegisterDateE(Date registerDateE) {
		this.registerDateE = registerDateE;
	}
	public Date getReceiveTimeB() {
		return receiveTimeB;
	}
	public void setReceiveTimeB(Date receiveTimeB) {
		this.receiveTimeB = receiveTimeB;
	}
	public Date getReceiveTimeE() {
		return receiveTimeE;
	}
	public void setReceiveTimeE(Date receiveTimeE) {
		this.receiveTimeE = receiveTimeE;
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
	public String getStartMemberName() {
		return startMemberName;
	}
	public void setStartMemberName(String startMemberName) {
		this.startMemberName = startMemberName;
	}

}
