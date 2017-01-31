package com.seeyon.v3x.mobile.message.domain;

/**
 * 
 * 
 */
public class MobileMessage {
	
	/**
	 * 短信状态
	 */
	public static enum STATE {
		unsend, //未发送
		success, //发送成功
		failure, //发送失败
		delete, //被删除
	}
	
	public static enum SMSType{
		sms,
		wappush
	}
	
	private Long id;

	private Long objectId; // 事项的ID

	private int type; // 参照ApplicationCategoryEnum
	
	private int smsType;

	private Long uid;// 接收人的ID
	
	private Long senderId;
	
	private String content;
	
	private int messageType; //0 - 系统消息 1 - 个人消息
	
	private java.util.Date time;
	
	private String  senderPhoneNumber;// 只有在发送个人消息时才有发送者的手机号
	
	private String reciverPhoneNumber;// 接收者的手机号
	
	private int state = MobileMessage.STATE.unsend.ordinal();//状态
	
	private int num = 1;//发送的条数
	
	private String featureCode;//特征号
	
	private Long appSubObjectId;//事项的附件Id
	
	private Long departmentId;
	
	private Long accountId;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public java.util.Date getTime() {
		return time;
	}

	public void setTime(java.util.Date time) {
		this.time = time;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getReciverPhoneNumber() {
		return reciverPhoneNumber;
	}

	public void setReciverPhoneNumber(String reciverPhoneNumber) {
		this.reciverPhoneNumber = reciverPhoneNumber;
	}

	public String getSenderPhoneNumber() {
		return senderPhoneNumber;
	}

	public void setSenderPhoneNumber(String senderPhoneNumber) {
		this.senderPhoneNumber = senderPhoneNumber;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Long getAppSubObjectId() {
		return appSubObjectId;
	}

	public void setAppSubObjectId(Long appSubObjectId) {
		this.appSubObjectId = appSubObjectId;
	}

	public String getFeatureCode() {
		return featureCode;
	}

	public void setFeatureCode(String featureCode) {
		this.featureCode = featureCode;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public int getSmsType() {
		return smsType;
	}

	public void setSmsType(int smsType) {
		this.smsType = smsType;
	}
	
	public void setSmsType(MobileMessage.SMSType smsType) {
		this.smsType = smsType.ordinal();
	}

}
