package com.seeyon.v3x.meeting.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.web.login.CurrentUser;

public abstract class Constants {
	/**
	 * 会议类型,视频会议开始提前时间(提前几小时)
	 * 提前用负数
	 * 延后用正数
	 * 
	 */
	public static final int VIDEO_MEETING_START_BEFORE= -2;
	/**
	 * 会议类型,视频会议
	 */
	public static final String VIDEO_MEETING_TITLE="videoConf";
	/**
	 * 会议类型,视频会议
	 */
	public static final String VIDEO_MEETING="2";
	
	/**
	 * 会议类型,普通会议
	 */
	public static final String ORID_MEETING="1";
	/**
	 * 会议性质,公开会议
	 */
	public static final String MEETING_OPEN="true";
	/**
	 * 会议性质,非公开会议
	 */
	public static final String MEETING_NOT_OPEN="false";
	/**
	 * 尚未发送，也就是暂存
	 */
	public static final int DATA_STATE_SAVE=0;
	/**
	 * 已经发送，但未开始
	 */
	public static final int DATA_STATE_SEND=10;
	/**
	 * 已经发送，但未开始
	 */
	public static final int DATA_STATE_WILL_START=15;
	/**
	 * 会议已经开始
	 */
	public static final int DATA_STATE_START=20;
	/**
	 * 会议已经结束
	 */
	public static final int DATA_STATE_FINISH=30;
	/**
	 * 会议已经总结
	 */
	public static final int DATA_STATE_SUMMARY=40;
	/**
	 * 已经归档
	 */	
	public static final int DATA_STATE_PIGEONHOLE=-10;      //12-17 修改为-10   为了查询的时候少加个过滤条件  100;
	
	/**
	 * 任务调度类型：<br>
	 * 1.提前或准时提醒与会人员(remindConferees)<br>
	 * 2.会议开始时将会议状态更改为召开中(update2Start)<br>
	 * 3.会议结束时将会议状态更改为已结束并清空相关资源(clearResources)<br>
	 */
	public static enum TASK_TYPE {
		remindConferees("remindConferees"), /** 提前或准时提醒与会人员 */
		update2Start("update2Start"),    	/** 会议开始时，将会议状态更改为召开中 */
		clearResources("clearResources"); 	/** 会议结束时，将会议状态更改为已结束并清空相关资源 */
		
		private String value;
		
		private TASK_TYPE(String value) {
			this.value = value;
		}
		
		/**
		 * 任务类型所对应的标识值
		 */
		public String getActionFlag() {
			return this.value;
		}
		/**
		 * 生成或删除任务调度时，需获取对应job(任务)的名称
		 */
		public String getJobName() {
			return "_job_" + this.value;
		}
		/**
		 * 生成或删除任务调度时，需获取对应job(任务)所在group(组)的名称
		 */
		public String getGroupName() {
			return "_group_" + this.value;
		}
	}
	
	public static enum ReceiverType{
		Owner, //所属人
		Agent,  //代理人
		AgentModel //含代理人和被代理人信息的简易模型
	}
	
	/**
	 * 未回执
	 */	
	public static final int FEEDBACKFLAG_NOREPLY=-100;
	
	/**
	 * 参加
	 */	
	public static final int FEEDBACKFLAG_ATTEND=1;
	
	/**
	 * 不参加
	 */	
	public static final int FEEDBACKFLAG_UNATTEND=0;
	
	/**
	 * 待定
	 */	
	public static final int FEEDBACKFLAG_PENDING=-1;

	/**
	 * 个人会议模板
	 */
	public static final String MEETING_TEMPLATE_TYPE_PERSON="0";
	
	/**
	 * 系统会议模板 
	 */
	public static final String MEETING_TEMPLATE_TYPE_SYSTEM="1";
	
	/**
	 * 日期格式 2008-01-01
	 */
	public static final String FORMAT_DATE="yyyy-MM-dd";
	/**
	 * 时间格式 14:12:03
	 */
	public static final String FORMAT_TIME="HH:mm:ss";
	/**
	 * 完整的日期时间格式 2008-01-01 14:12:03
	 */
	public static final String FORMAT_DATETIME="yyyy-MM-dd HH:mm";
	
	/**
	 * 新闻发起员标志，在新闻类型-新闻管理员、新闻发起员关联表中使用，保存在ext1字段中
	 */
	public static final String WRITE_FALG="write";
	/**
	 * 新闻管理员标志，在新闻类型-新闻管理员、新闻发起员关联表中使用，保存在ext1字段中
	 */
	public static final String MANAGER_FALG="manager";
	
	/**
	 * 操作成功标志
	 */
	public static final String RESULT_SUCCESS="success";
	/**
	 * 操作失败标志
	 */
	public static final String RESULT_FAILURE="failure";
	
	/**
	 * 是否处于开发测试阶段
	 */
	public static final Boolean IS_TEST=true;
	
	/**
	 * 会议的CATEGORY = 6
	 */
	public static final int MEETING_CATEGORY=6;
	
	/**
	 * 会议国际化资源路径
	 */
	public static final String MT_I18N_RES = "com.seeyon.v3x.meeting.resources.i18n.MeetingResources";
	
	/**
     * 根据被代理人ID取得会议代理人ID
     * @param memberId
     *            被代理人ID
     */
    public static Long getAgentId(Long memberId) {
    	return MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.meeting.key(), memberId);
    }
    
    public static Set<Long> getMeetingAgentId(List<Long> memberIds) {
    	Set<Long> result = null;
    	if(CollectionUtils.isNotEmpty(memberIds)) {
    		result = new HashSet<Long>();
    		for(Long memberId : memberIds) {
    			Long agent = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.meeting.key(), memberId);
    			if(agent != null)
    				result.add(agent);
    		}
    	}
    	return result;
    }
    
    /**
     * 获取当前登录用户的会议被代理人.
     */
    public static List<AgentModel> getMeetingAgents(){
    	List<AgentModel> ids = new ArrayList<AgentModel>();
    	User u = CurrentUser.get();
    	List<AgentModel> agentToList = MemberAgentBean.getInstance().getAgentModelToList(u.getId());
    	List<AgentModel> agentList = MemberAgentBean.getInstance().getAgentModelList(u.getId());
    	if(CollectionUtils.isEmpty(agentToList)){
    		agentToList = agentList;
    	}
    	if(agentToList!=null){
    		Date date = new Date();
    		for(AgentModel m : agentToList){
    			if(m!=null && (m.getAgentOption().indexOf("6")!=-1) && date.after(m.getStartDate()) && date.before(m.getEndDate())){
    				ids.add(m);
    			}
    		}
    	}
    	return ids;
    }
    
    public static final String PASSIVE_AGENT_FLAG = "1";
    
    public static final String Not_Agent = "agentToData_mark";
    
    /**
     * 回执时身份类型：自己、代理人、不区分
     */
    public static enum ReplyType {
    	self,
    	agent,
    	all
    }
	
}
