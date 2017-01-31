package com.seeyon.v3x.meeting.domain;

import com.seeyon.v3x.meeting.domain.base.BaseMtTemplate;
import com.seeyon.v3x.util.Strings;



public class MtTemplate extends BaseMtTemplate {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MtTemplate () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MtTemplate (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MtTemplate (
		java.lang.Long id,
		java.lang.String templateName,
		java.lang.String title,
		java.lang.Long emceeId,
		java.lang.String conferees,
		java.lang.Long recorderId,
		java.util.Date beginDate,
		java.util.Date endDate,
		boolean remindFlag,
		java.lang.String dataFormat,
		java.lang.Long createUser,
		java.util.Date createDate,
		java.lang.Long accountId,
		java.lang.Integer state) {

		super (
			id,
			templateName,
			title,
			emceeId,
			conferees,
			recorderId,
			beginDate,
			endDate,
			remindFlag,
			dataFormat,
			createUser,
			createDate,
			accountId,
			state);
	}

/*[CONSTRUCTOR MARKER END]*/


	private String createUserName;
	private String confereesNames;
	private String emceeName;
	private String recorderName;
	private String projectName;
	private boolean attachmentsFlag;
	
	private String resourcesId;
	private String resourcesName;
	private String roomName;
	
	private String beforeTimeStr;

	public boolean isAttachmentsFlag() {
		return attachmentsFlag;
	}

	public void setAttachmentsFlag(boolean attachmentsFlag) {
		this.attachmentsFlag = attachmentsFlag;
	}

	public String getConfereesNames() {
		return confereesNames;
	}

	public void setConfereesNames(String confereesNames) {
		this.confereesNames = confereesNames;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getEmceeName() {
		return emceeName;
	}

	public void setEmceeName(String emceeName) {
		this.emceeName = emceeName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getRecorderName() {
		return recorderName;
	}

	public void setRecorderName(String recorderName) {
		this.recorderName = recorderName;
	}

	public String getResourcesId() {
		return resourcesId;
	}

	public void setResourcesId(String resourcesId) {
		this.resourcesId = resourcesId;
	}

	public String getResourcesName() {
		return resourcesName;
	}

	public void setResourcesName(String resourcesName) {
		this.resourcesName = resourcesName;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getBeforeTimeStr() {
		this.beforeTimeStr=String.valueOf(this.getBeforeTime());
		return beforeTimeStr;
	}

	public void setBeforeTimeStr(String beforeTimeStr) {
		this.beforeTimeStr = beforeTimeStr;
	}
	
	/**
	 * 通过获取的记录人ID字符串设定记录人ID，如果获取内容为空，将记录人ID设为-1l，否则按其内容设定
	 */
	public void setRecorderIdFromStr(String recorderIdStr) {
		if(Strings.isBlank(recorderIdStr) || "-1".equalsIgnoreCase(recorderIdStr))
        	this.setRecorderId(com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID);
	}
}