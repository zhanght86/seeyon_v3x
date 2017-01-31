package com.seeyon.v3x.meeting.util;

import com.seeyon.v3x.common.authenticate.domain.AgentModel;
/**
 * 会议搜索条件封装类
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-5-4
 */
public class MeetingSearchModel {
	
	public static final String Search_By_Title = "title";
	public static final String Search_By_State = "state";
	public static final String Search_By_CreateDate = "createDate";
	public static final String Search_By_CreateUser = "createUser";
	public static final String Search_By_MeetingType = "meetingNature";
	
	/** 搜索类型 */
	private String condition;
	/** 搜索值1 */
	private String value1;
	/** 搜索值2 */
	private String value2;
	/** 会议类型：待发、已发、已召开 */
	private int state;
	/** 当前用户ID */
	private Long userId;
	/** 代理信息 */
	private AgentModel agentModel;
	
	/**
	 * 会议搜索条件封装类构造方法
	 * @param state			会议类型：待发、已发、已召开
	 * @param condition		搜索类型
	 * @param value1		搜索值1
	 * @param value2		搜索值2
	 * @param userId		当前用户ID
	 * @param agentModel	代理信息
	 */
	public MeetingSearchModel(int state, String condition, String value1, String value2, Long userId, AgentModel agentModel) {
		super();
		this.state = state;
		this.condition = condition;
		this.value1 = value1;
		this.value2 = value2;
		this.userId = userId;
		this.agentModel = agentModel;
	}
	
	public MeetingSearchModel() {}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public AgentModel getAgentModel() {
		return agentModel;
	}

	public void setAgentModel(AgentModel agentModel) {
		this.agentModel = agentModel;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
