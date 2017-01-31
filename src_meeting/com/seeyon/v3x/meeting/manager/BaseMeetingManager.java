package com.seeyon.v3x.meeting.manager;

import com.seeyon.v3x.meeting.util.MeetingUtils;

/**
 * 新闻模块的Manager的基类，主要是为了增加一个工具类<code>NewsUtils</code>
 * @author wolf
 *
 */
public class BaseMeetingManager {
	private MeetingUtils meetingUtils;

	/**
	 * 获取NewsUtils工具类
	 * @return
	 */
	public MeetingUtils getMeetingUtils() {
		return meetingUtils;
	}

	public void setMeetingUtils(MeetingUtils meetingUtils) {
		this.meetingUtils = meetingUtils;
	}
	
	
}
