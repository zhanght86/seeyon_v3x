package com.seeyon.v3x.meeting.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.seeyon.v3x.meeting.domain.base.BaseMtMeeting;
import com.seeyon.v3x.meeting.util.Constants;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;



public class MtMeeting extends BaseMtMeeting implements Comparable<MtMeeting>{
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MtMeeting () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MtMeeting (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MtMeeting (
		java.lang.Long id,
		java.lang.String title,
		java.lang.Long emceeId,//主持人
		java.lang.String conferees,//与会人
		java.lang.Long recorderId,//记录人
		java.util.Date beginDate,
		java.util.Date endDate,
		boolean remindFlag,
		boolean hasAttachments,
		java.lang.String dataFormat,
		java.util.Date createDate,
		java.lang.Long createUser,
		java.lang.Long accountId,
		java.lang.Integer state) {

		super (
			id,
			title,
			emceeId,
			conferees,
			recorderId,
			beginDate,
			endDate,
			remindFlag,
			hasAttachments,
			dataFormat,
			createDate,
			createUser,
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
	private boolean proxy;
	private Long proxyId;
	
	private String beforeTimeStr;
	private String accountName;

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

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
	 * 排序：如果为暂存待发状态，按照创建日期降序排列，如果为已发送未召开状态，按会议开始时间升序排列，如果是其他状态，按照会议开始日期降序排列
	 */
	public int compareTo(MtMeeting o) {
		Integer stateThis = this.getState();
		Integer toCompare = o.getState();
		if(stateThis!=null && toCompare!=null && stateThis.equals(toCompare)) {
			if(stateThis==Constants.DATA_STATE_SAVE) {
				return -(this.getCreateDate().compareTo(o.getCreateDate()));
			} else if(stateThis==Constants.DATA_STATE_SEND) {
				return this.getBeginDate().compareTo(o.getBeginDate());
			} else {
				return -(this.getBeginDate().compareTo(o.getBeginDate()));
			}
		} else {
			return -(this.getBeginDate().compareTo(o.getBeginDate()));
		}
	}
	
	public static void main(String[] args) {
		List<MtMeeting> list = new ArrayList<MtMeeting>();
		//暂存待发状态：按照创建日期降序排列
//		MtMeeting m1 = new MtMeeting(-1l, "排序4", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), 30), null, false, false,
//				"HTML", Datetimes.addDate(new Date(), 10), -1l, -1l, 0);
//		MtMeeting m2 = new MtMeeting(-1l, "排序3", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), 20), null, false, false,
//				"HTML", Datetimes.addDate(new Date(), 20), -1l, -1l, 0);
//		MtMeeting m3 = new MtMeeting(-1l, "排序2", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), 10), null, false, false,
//				"HTML", Datetimes.addDate(new Date(), 30), -1l, -1l, 0);
//		MtMeeting m4 = new MtMeeting(-1l, "排序1", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), -5), null, false, false,
//				"HTML", Datetimes.addDate(new Date(), 40), -1l, -1l, 0);
		
		//已发起未召开：按照会议开始时间升序排列
//		MtMeeting m1 = new MtMeeting(-1l, "排序4", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), 30), null, false, false,
//				"HTML", Datetimes.addDate(new Date(), 10), -1l, -1l, 10);
//		MtMeeting m2 = new MtMeeting(-1l, "排序3", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), 20), null, false, false,
//				"HTML", Datetimes.addDate(new Date(), 20), -1l, -1l, 10);
//		MtMeeting m3 = new MtMeeting(-1l, "排序2", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), 10), null, false, false,
//				"HTML", Datetimes.addDate(new Date(), 30), -1l, -1l, 10);
//		MtMeeting m4 = new MtMeeting(-1l, "排序1", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), -5), null, false, false,
//				"HTML", Datetimes.addDate(new Date(), 40), -1l, -1l, 10);
		
		//其他状态和其他场景：按照会议开始时间降序排列
		MtMeeting m1 = new MtMeeting(-1l, "排序4", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), 30), null, false, false,
				"HTML", Datetimes.addDate(new Date(), 10), -1l, -1l, 20);
		MtMeeting m2 = new MtMeeting(-1l, "排序3", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), 20), null, false, false,
				"HTML", Datetimes.addDate(new Date(), 20), -1l, -1l, 30);
		MtMeeting m3 = new MtMeeting(-1l, "排序2", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), 10), null, false, false,
				"HTML", Datetimes.addDate(new Date(), 30), -1l, -1l, 30);
		MtMeeting m4 = new MtMeeting(-1l, "排序1", -1l, "Member|-1l", -1l, Datetimes.addDate(new Date(), -5), null, false, false,
				"HTML", Datetimes.addDate(new Date(), 40), -1l, -1l, 40);
		
		list.add(m1);
		list.add(m2);
		list.add(m3);
		list.add(m4);
		Collections.sort(list);
		for(MtMeeting m : list) {
			System.out.println(m.getTitle());
		}
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	public Long getProxyId() {
		return proxyId;
	}

	public void setProxyId(Long proxyId) {
		this.proxyId = proxyId;
	}

	/**
	 * 通过获取的记录人ID字符串设定记录人ID，如果获取内容为空，将记录人ID设为-1l，否则按其内容设定
	 */
	public void setRecorderIdFromStr(String recorderIdStr) {
		if(Strings.isBlank(recorderIdStr) || "-1".equalsIgnoreCase(recorderIdStr))
        	this.setRecorderId(com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID);
	}
	
	/**
	 * 会议正文格式是否Word或Excel中的一种
	 */
	public boolean contentIsWordOrExcel() {
		return com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(this.getDataFormat()) ||
			   com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(this.getDataFormat());
	}
	
	/**
	 * 判断会议的开始和结束时间是否均大于当前时间，用于为与会对象生成待办事项等场合(只有条件满足时，才有必要为他们写入待办事项)
	 */
	public boolean beginAndEndTimeGreaterThanNow() {
		return this.getEndDate().getTime()>System.currentTimeMillis() &&
			   this.getBeginDate().getTime()>System.currentTimeMillis();
	}
	
	/**
	 * 获取会议的提醒时间，如果会议未设置提前提醒，返回Null，否则返回对应的提醒时间
	 */
	public Date getRemindTime() {
		Date remindTime = null;
		if(this.isRemindFlag() && this.getBeforeTime()!=null) {
			remindTime = Datetimes.addMinute(this.getBeginDate(), 
					this.getBeforeTime()!= -1 ? -this.getBeforeTime() : 0);
		}
		return remindTime;
	}

}