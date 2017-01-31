package com.seeyon.v3x.meeting.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;


/**
 * This is an object that contains data related to the mt_template table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="mt_template"
 */

public abstract class BaseMtTemplate extends BaseModel  implements Serializable {

	public static String REF = "MtTemplate";
	public static String PROP_REMIND_FLAG = "remindFlag";
	public static String PROP_ROOM = "room";
	public static String PROP_PROJECT_ID = "projectId";
	public static String PROP_RECORDER_ID = "recorderId";
	public static String PROP_BEFORE_TIME = "beforeTime";
	public static String PROP_TEMPLATE_NAME = "templateName";
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
	public static String PROP_STATE = "state";
	public static String PROP_BEGIN_DATE = "beginDate";
	public static String PROP_TEMPLATE_TYPE = "templateType";
	public static String PROP_DATA_FORMAT = "dataFormat";
	public static String PROP_CONFEREES = "conferees";
	public static String PROP_TITLE = "title";
	public static String PROP_TEMPLATE_ID = "templateId";
	public static String PROP_ID = "id";
	public static String PROP_UPDATE_USER = "updateUser";


	// constructors
	public BaseMtTemplate () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMtTemplate (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMtTemplate (
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

		this.setId(id);
		this.setTemplateName(templateName);
		this.setTitle(title);
		this.setEmceeId(emceeId);
		this.setConferees(conferees);
		this.setRecorderId(recorderId);
		this.setBeginDate(beginDate);
		this.setEndDate(endDate);
		this.setRemindFlag(remindFlag);
		this.setDataFormat(dataFormat);
		this.setCreateUser(createUser);
		this.setCreateDate(createDate);
		this.setAccountId(accountId);
		this.setState(state);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	////private java.lang.Long id;

	// fields
	private java.lang.Long templateId;
	private java.lang.String templateName;
	private java.lang.String templateType;
	private java.lang.String title;
	private java.lang.String meetingType;
	private java.lang.Long emceeId;
	private java.lang.String conferees;
	private java.lang.Long recorderId;
	private java.lang.Long projectId;
	private java.util.Date beginDate;
	private java.util.Date endDate;
	private boolean remindFlag;
	private java.lang.Integer beforeTime;
	private java.lang.String address;
	private java.lang.String room;
	private java.lang.String dataFormat;
	private java.lang.String content;
	private java.lang.Long createUser;
	private java.util.Date createDate;
	private java.lang.String pigeonholePath;
	private java.util.Date updateDate;
	private java.lang.Long updateUser;
	private java.lang.Integer state;
	private java.lang.String ext1;
	private java.lang.String ext2;
	private java.lang.String ext3;
	private java.lang.String videoMeetingId;
	private java.lang.String confKey;
	private java.lang.Long accountId;

	// collections
	private java.util.Set<com.seeyon.v3x.meeting.domain.MtTemplateUser> templateUsers;



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
	 * Return the value associated with the column: template_name
	 */
	public java.lang.String getTemplateName () {
		return templateName;
	}

	/**
	 * Set the value related to the column: template_name
	 * @param templateName the template_name value
	 */
	public void setTemplateName (java.lang.String templateName) {
		this.templateName = templateName;
	}



	/**
	 * Return the value associated with the column: template_type
	 */
	public java.lang.String getTemplateType () {
		return templateType;
	}

	/**
	 * Set the value related to the column: template_type
	 * @param templateType the template_type value
	 */
	public void setTemplateType (java.lang.String templateType) {
		this.templateType = templateType;
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
	public java.lang.String getRoom () {
		return room;
	}

	/**
	 * Set the value related to the column: room
	 * @param room the room value
	 */
	public void setRoom (java.lang.String room) {
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
	 * Return the value associated with the column: ext1
	 */
	public java.lang.String getExt1 () {
		return ext1;
	}

	/**
	 * Set the value related to the column: ext1
	 * @param ext1 the ext1 value
	 */
	public void setExt1 (java.lang.String ext1) {
		this.ext1 = ext1;
	}



	/**
	 * Return the value associated with the column: ext2
	 */
	public java.lang.String getExt2 () {
		return ext2;
	}

	/**
	 * Set the value related to the column: ext2
	 * @param ext2 the ext2 value
	 */
	public void setExt2 (java.lang.String ext2) {
		this.ext2 = ext2;
	}



	/**
	 * Return the value associated with the column: ext3
	 */
	public java.lang.String getExt3 () {
		return ext3;
	}

	/**
	 * Set the value related to the column: ext3
	 * @param ext3 the ext3 value
	 */
	public void setExt3 (java.lang.String ext3) {
		this.ext3 = ext3;
	}
	public java.lang.String getVideoMeetingId() {
		return videoMeetingId;
	}

	public void setVideoMeetingId(java.lang.String videoMeetingId) {
		this.videoMeetingId = videoMeetingId;
	}

	public java.lang.String getConfKey() {
		return confKey;
	}

	public void setConfKey(java.lang.String confKey) {
		this.confKey = confKey;
	}

	/**
	 * Return the value associated with the column: templateUsers
	 */
	public java.util.Set<com.seeyon.v3x.meeting.domain.MtTemplateUser> getTemplateUsers () {
		return templateUsers;
	}

	/**
	 * Set the value related to the column: templateUsers
	 * @param templateUsers the templateUsers value
	 */
	public void setTemplateUsers (java.util.Set<com.seeyon.v3x.meeting.domain.MtTemplateUser> templateUsers) {
		this.templateUsers = templateUsers;
	}

	public void addTotemplateUsers (com.seeyon.v3x.meeting.domain.MtTemplateUser mtTemplateUser) {
		if (null == getTemplateUsers()) setTemplateUsers(new java.util.TreeSet<com.seeyon.v3x.meeting.domain.MtTemplateUser>());
		getTemplateUsers().add(mtTemplateUser);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.seeyon.v3x.meeting.domain.MtTemplate)) return false;
		else {
			com.seeyon.v3x.meeting.domain.MtTemplate mtTemplate = (com.seeyon.v3x.meeting.domain.MtTemplate) obj;
			if (null == this.getId() || null == mtTemplate.getId()) return false;
			else return (this.getId().equals(mtTemplate.getId()));
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

	public java.lang.Long getAccountId() {
		return accountId;
	}

	public void setAccountId(java.lang.Long accountId) {
		this.accountId = accountId;
	}


}