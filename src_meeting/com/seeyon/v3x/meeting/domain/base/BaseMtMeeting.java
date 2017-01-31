package com.seeyon.v3x.meeting.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;


/**
 * This is an object that contains data related to the mt_meeting table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="mt_meeting"
 */

public abstract class BaseMtMeeting extends BaseModel  implements Serializable {

	public static String REF = "MtMeeting";
	public static String PROP_ROOM = "room";
	public static String PROP_REMIND_FLAG = "remindFlag";
	public static String PROP_PROJECT_ID = "projectId";
	public static String PROP_RECORDER_ID = "recorderId";
	public static String PROP_BEFORE_TIME = "beforeTime";
	public static String PROP_CREATE_DATE = "createDate";
	public static String PROP_EXT5 = "confKey";
	public static String PROP_PIGEONHOLE_PATH = "pigeonholePath";
	public static String PROP_EXT1 = "ext1";
	public static String PROP_EMCEE_ID = "emceeId";
	public static String PROP_CREATE_USER = "createUser";
	public static String PROP_UPDATE_DATE = "updateDate";
	public static String PROP_EXT2 = "ext2";
	public static String PROP_EXT3 = "ext3";
	public static String PROP_ADDRESS = "address";
	public static String PROP_MEETING_TYPE = "meetingType";
	public static String PROP_CONTENT = "content";
	public static String PROP_END_DATE = "endDate";
	public static String PROP_EXT4 = "videoMeetingId";
	public static String PROP_PIGEONHOLE_USER_ID = "pigeonholeUserId";
	public static String PROP_STATE = "state";
	public static String PROP_PIGEONHOLE_DATE = "pigeonholeDate";
	public static String PROP_BEGIN_DATE = "beginDate";
	public static String PROP_DATA_FORMAT = "dataFormat";
	public static String PROP_CONFEREES = "conferees";
	public static String PROP_TITLE = "title";
	public static String PROP_TEMPLATE_ID = "templateId";
	public static String PROP_ID = "id";
	public static String PROP_UPDATE_USER = "updateUser";
	public static String PROP_MEETING_PASSWORD = "meetingPassword";
	public static String PROP_MEETING_CHARACTER = "meetingCharacter";


	// constructors
	public BaseMtMeeting () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMtMeeting (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMtMeeting (
		java.lang.Long id,
		java.lang.String title,
		java.lang.Long emceeId,
		java.lang.String conferees,
		java.lang.Long recorderId,
		java.util.Date beginDate,
		java.util.Date endDate,
		boolean remindFlag,
		boolean hasAttachments,
		java.lang.String dataFormat,
		java.util.Date createDate,
		java.lang.Long createUser,
		java.lang.Long accountId,
		java.lang.Integer state) {

		this.setId(id);
		this.setTitle(title);
		this.setEmceeId(emceeId);
		this.setConferees(conferees);
		this.setRecorderId(recorderId);
		this.setBeginDate(beginDate);
		this.setEndDate(endDate);
		this.setRemindFlag(remindFlag);
		this.setHasAttachments(hasAttachments);
		this.setDataFormat(dataFormat);
		this.setCreateDate(createDate);
		this.setCreateUser(createUser);
		this.setAccountId(accountId);
		this.setState(state);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	////private java.lang.Long id;

	// fields
	private java.lang.String title;
	private java.lang.String meetingType;
	private java.lang.Long templateId;
	private java.lang.Long emceeId;
	private java.lang.String conferees;
	private java.lang.Long recorderId;
	private java.lang.Long projectId;
	private java.util.Date beginDate;
	private java.util.Date endDate;
	private boolean remindFlag;
	private boolean hasAttachments;
	private java.lang.Integer beforeTime;
	private java.lang.String address;
	private java.lang.Long room;
	private java.lang.String dataFormat;
	private java.lang.String content;
	private java.util.Date createDate;
	private java.lang.Long createUser;
	private java.lang.Long pigeonholeUserId;
	private java.util.Date pigeonholeDate;
	private java.lang.String pigeonholePath;
	private java.util.Date updateDate;
	private java.lang.Long updateUser;
	private java.lang.Integer state;
	/** 标识在会议修改前后，开始时间是否发生了变化，用于辅助任务调度的设置 */
	private java.lang.String ext1;
	/** 标识在会议修改前后，结束时间是否发生了变化，用于辅助任务调度的设置 */
	private java.lang.String ext2;
	/** 标识在会议修改前后，提醒与会人员的时间是否发生了变化，用于辅助任务调度的设置 */
	private java.lang.String ext3;
	/** 视频会议会议confkey */
	private java.lang.String confKey;
	/** 
	 * 视频会议会议id
	 * 
	 * */
	private java.lang.String videoMeetingId;
	private java.lang.Long accountId;

	private java.lang.String meetingCharacter;
	private java.lang.String meetingPassword;

	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="assigned"
     *  column="id"
     */
	public java.lang.Long getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Long id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: title
	 */
	public java.lang.String getTitle () {
		return title;
	}

	/**
	 * Set the value related to the column: title
	 * @param title the title value
	 */
	public void setTitle (java.lang.String title) {
		this.title = title;
	}



	/**
	 * Return the value associated with the column: meeting_type
	 */
	public java.lang.String getMeetingType () {
		return meetingType;
	}

	/**
	 * Set the value related to the column: meeting_type
	 * @param meetingType the meeting_type value
	 */
	public void setMeetingType (java.lang.String meetingType) {
		this.meetingType = meetingType;
	}



	/**
	 * Return the value associated with the column: template_id
	 */
	public java.lang.Long getTemplateId () {
		return templateId;
	}

	/**
	 * Set the value related to the column: template_id
	 * @param templateId the template_id value
	 */
	public void setTemplateId (java.lang.Long templateId) {
		this.templateId = templateId;
	}



	/**
	 * Return the value associated with the column: emcee_id
	 */
	public java.lang.Long getEmceeId () {
		return emceeId;
	}

	/**
	 * Set the value related to the column: emcee_id
	 * @param emceeId the emcee_id value
	 */
	public void setEmceeId (java.lang.Long emceeId) {
		this.emceeId = emceeId;
	}



	/**
	 * Return the value associated with the column: conferees
	 */
	public java.lang.String getConferees () {
		return conferees;
	}

	/**
	 * Set the value related to the column: conferees
	 * @param conferees the conferees value
	 */
	public void setConferees (java.lang.String conferees) {
		this.conferees = conferees;
	}



	/**
	 * Return the value associated with the column: recorder_id
	 */
	public java.lang.Long getRecorderId () {
		return recorderId;
	}

	/**
	 * Set the value related to the column: recorder_id
	 * @param recorderId the recorder_id value
	 */
	public void setRecorderId (java.lang.Long recorderId) {
		this.recorderId = recorderId;
	}



	/**
	 * Return the value associated with the column: project_id
	 */
	public java.lang.Long getProjectId () {
		return projectId;
	}

	/**
	 * Set the value related to the column: project_id
	 * @param projectId the project_id value
	 */
	public void setProjectId (java.lang.Long projectId) {
		this.projectId = projectId;
	}



	/**
	 * Return the value associated with the column: begin_date
	 */
	public java.util.Date getBeginDate () {
		return beginDate;
	}

	/**
	 * Set the value related to the column: begin_date
	 * @param beginDate the begin_date value
	 */
	public void setBeginDate (java.util.Date beginDate) {
		this.beginDate = beginDate;
	}



	/**
	 * Return the value associated with the column: end_date
	 */
	public java.util.Date getEndDate () {
		return endDate;
	}

	/**
	 * Set the value related to the column: end_date
	 * @param endDate the end_date value
	 */
	public void setEndDate (java.util.Date endDate) {
		this.endDate = endDate;
	}



	/**
	 * Return the value associated with the column: remind_flag
	 */
	public boolean isRemindFlag () {
		return remindFlag;
	}

	/**
	 * Set the value related to the column: remind_flag
	 * @param remindFlag the remind_flag value
	 */
	public void setRemindFlag (boolean remindFlag) {
		this.remindFlag = remindFlag;
	}



	/**
	 * Return the value associated with the column: before_time
	 */
	public java.lang.Integer getBeforeTime () {
		return beforeTime;
	}

	/**
	 * Set the value related to the column: before_time
	 * @param beforeTime the before_time value
	 */
	public void setBeforeTime (java.lang.Integer beforeTime) {
		this.beforeTime = beforeTime;
	}



	/**
	 * Return the value associated with the column: address
	 */
	public java.lang.String getAddress () {
		return address;
	}

	/**
	 * Set the value related to the column: address
	 * @param address the address value
	 */
	public void setAddress (java.lang.String address) {
		this.address = address;
	}



	/**
	 * Return the value associated with the column: room
	 */
	public java.lang.Long getRoom () {
		return room;
	}

	/**
	 * Set the value related to the column: room
	 * @param room the room value
	 */
	public void setRoom (java.lang.Long room) {
		this.room = room;
	}



	/**
	 * Return the value associated with the column: data_format
	 */
	public java.lang.String getDataFormat () {
		return dataFormat;
	}

	/**
	 * Set the value related to the column: data_format
	 * @param dataFormat the data_format value
	 */
	public void setDataFormat (java.lang.String dataFormat) {
		this.dataFormat = dataFormat;
	}



	/**
	 * Return the value associated with the column: content
	 */
	public java.lang.String getContent () {
		return content;
	}

	/**
	 * Set the value related to the column: content
	 * @param content the content value
	 */
	public void setContent (java.lang.String content) {
		this.content = content;
	}



	/**
	 * Return the value associated with the column: create_date
	 */
	public java.util.Date getCreateDate () {
		return createDate;
	}

	/**
	 * Set the value related to the column: create_date
	 * @param createDate the create_date value
	 */
	public void setCreateDate (java.util.Date createDate) {
		this.createDate = createDate;
	}



	/**
	 * Return the value associated with the column: create_user
	 */
	public java.lang.Long getCreateUser () {
		return createUser;
	}

	/**
	 * Set the value related to the column: create_user
	 * @param createUser the create_user value
	 */
	public void setCreateUser (java.lang.Long createUser) {
		this.createUser = createUser;
	}



	/**
	 * Return the value associated with the column: pigeonhole_user_id
	 */
	public java.lang.Long getPigeonholeUserId () {
		return pigeonholeUserId;
	}

	/**
	 * Set the value related to the column: pigeonhole_user_id
	 * @param pigeonholeUserId the pigeonhole_user_id value
	 */
	public void setPigeonholeUserId (java.lang.Long pigeonholeUserId) {
		this.pigeonholeUserId = pigeonholeUserId;
	}



	/**
	 * Return the value associated with the column: pigeonhole_date
	 */
	public java.util.Date getPigeonholeDate () {
		return pigeonholeDate;
	}

	/**
	 * Set the value related to the column: pigeonhole_date
	 * @param pigeonholeDate the pigeonhole_date value
	 */
	public void setPigeonholeDate (java.util.Date pigeonholeDate) {
		this.pigeonholeDate = pigeonholeDate;
	}



	/**
	 * Return the value associated with the column: pigeonhole_path
	 */
	public java.lang.String getPigeonholePath () {
		return pigeonholePath;
	}

	/**
	 * Set the value related to the column: pigeonhole_path
	 * @param pigeonholePath the pigeonhole_path value
	 */
	public void setPigeonholePath (java.lang.String pigeonholePath) {
		this.pigeonholePath = pigeonholePath;
	}



	/**
	 * Return the value associated with the column: update_date
	 */
	public java.util.Date getUpdateDate () {
		return updateDate;
	}

	/**
	 * Set the value related to the column: update_date
	 * @param updateDate the update_date value
	 */
	public void setUpdateDate (java.util.Date updateDate) {
		this.updateDate = updateDate;
	}



	/**
	 * Return the value associated with the column: update_user
	 */
	public java.lang.Long getUpdateUser () {
		return updateUser;
	}

	/**
	 * Set the value related to the column: update_user
	 * @param updateUser the update_user value
	 */
	public void setUpdateUser (java.lang.Long updateUser) {
		this.updateUser = updateUser;
	}



	/**
	 * Return the value associated with the column: state
	 */
	public java.lang.Integer getState () {
		return state;
	}

	/**
	 * Set the value related to the column: state
	 * @param state the state value
	 */
	public void setState (java.lang.Integer state) {
		this.state = state;
	}



	/**
	 * 返回标识字段，辅助任务调度设置：<br>
	 * 1.已发送未召开的会议，修改前后，<b>提醒与会人员的时刻</b>有无改变<br>
	 * 2.如果会议从新建或暂存状态进行发送，且会议有提前提醒，字段置为"true"<br>
	 */
	public java.lang.String getExt1 () {
		return ext1;
	}

	/**
	 * 设定标识字段，辅助任务调度设置：<br>
	 * 1.已发送未召开的会议，修改前后，<b>提醒与会人员的时刻</b>有无改变<br>
	 * 2.如果会议从新建或暂存状态进行发送，且会议有提前提醒，字段置为"true"<br>
	 */
	public void setExt1 (java.lang.String ext1) {
		this.ext1 = ext1;
	}

	/**
	 * 返回标识字段，辅助任务调度设置：<br>
	 * 1.已发送未召开的会议，修改前后，<b>会议开始时间</b>有无改变<br>
	 * 2.如果会议从新建或暂存状态进行发送，字段置为"true"<br>
	 */
	public java.lang.String getExt2 () {
		return ext2;
	}

	/**
	 * 设定标识字段，辅助任务调度设置：<br>
	 * 1.已发送未召开的会议，修改前后，<b>会议开始时间</b>有无改变<br>
	 * 2.如果会议从新建或暂存状态进行发送，字段置为"true"<br>
	 */
	public void setExt2 (java.lang.String ext2) {
		this.ext2 = ext2;
	}

	/**
	 * 返回标识字段，辅助任务调度设置：<br>
	 * 1.已发送未召开的会议，修改前后，<b>会议结束时间</b>有无改变<br>
	 * 2.如果会议从新建或暂存状态进行发送，字段置为"true"<br>
	 */
	public java.lang.String getExt3 () {
		return ext3;
	}

	/**
	 * 设定标识字段，辅助任务调度设置：<br>
	 * 1.已发送未召开的会议，修改前后，<b>会议结束时间</b>有无改变<br>
	 * 2.如果会议从新建或暂存状态进行发送，字段置为"true"<br>
	 */
	public void setExt3 (java.lang.String ext3) {
		this.ext3 = ext3;
	}

	/**
	 * Return the value associated with the column: confKey
	 */
	public java.lang.String getConfKey () {
		return confKey;
	}

	/**
	 * Set the value related to the column: confKey
	 * @param confKey the confKey value
	 */
	public void setConfKey (java.lang.String confKey) {
		this.confKey = confKey;
	}



	/**
	 * Return the value associated with the column: videoMeetingId
	 */
	public java.lang.String getVideoMeetingId () {
		return videoMeetingId;
	}

	/**
	 * Set the value related to the column: videoMeetingId
	 * @param videoMeetingId the videoMeetingId value
	 */
	public void setVideoMeetingId (java.lang.String videoMeetingId) {
		this.videoMeetingId = videoMeetingId;
	}




	public java.lang.Long getAccountId() {
		return accountId;
	}

	public void setAccountId(java.lang.Long accountId) {
		this.accountId = accountId;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.seeyon.v3x.meeting.domain.MtMeeting)) return false;
		else {
			com.seeyon.v3x.meeting.domain.MtMeeting mtMeeting = (com.seeyon.v3x.meeting.domain.MtMeeting) obj;
			if (null == this.getId() || null == mtMeeting.getId()) return false;
			else return (this.getId().equals(mtMeeting.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}

	public boolean isHasAttachments() {
		return hasAttachments;
	}

	public void setHasAttachments(boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}

	public java.lang.String getMeetingCharacter() {
		return meetingCharacter;
	}

	public void setMeetingCharacter(java.lang.String meetingCharacter) {
		this.meetingCharacter = meetingCharacter;
	}

	public java.lang.String getMeetingPassword() {
		return meetingPassword;
	}

	public void setMeetingPassword(java.lang.String meetingPassword) {
		this.meetingPassword = meetingPassword;
	}


}