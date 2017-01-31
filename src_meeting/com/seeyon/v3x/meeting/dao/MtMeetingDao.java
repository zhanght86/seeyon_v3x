package com.seeyon.v3x.meeting.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.domain.MtReply;
import com.seeyon.v3x.meeting.util.Constants;

public class MtMeetingDao extends BaseHibernateDao<MtMeeting> {
	
	/**
	 * 更新会议的状态，用于将已发但未召开的会议删除时，将其状态更新为暂存待发，以便用户对其进行修改
	 * @param meetingId    已发未召开的会议ID
	 * @param state2Update 更新为的状态：暂存待发
	 */
	public void updateState(Long meetingId, int state2Update) {
		String hql = "update " + MtMeeting.class.getName() + " as mt set mt.state=? where mt.id=?";
		this.bulkUpdate(hql, null, state2Update, meetingId);
	}
	
	/**
	 * 将选中要归档的会议(其状态为未归档)状态更新为"已归档"
	 * @param meetingIds
	 */
	public void updateState2Pigeonhole4Meetings(List<Long> meetingIds) {
		String hql = "update " + MtMeeting.class.getName() + " as mt set mt.state=:pigeonholeState where " +
				     "mt.state!=:pigeonholeState and mt.id in (:meetingIds)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pigeonholeState", Constants.DATA_STATE_PIGEONHOLE);
		params.put("meetingIds", meetingIds);
		this.bulkUpdate(hql, params);
	}
	
	/**
     * 根据回执记录获取确定不参加会议的与会者ID集合
     * @param meetingId		会议ID
     */
	@SuppressWarnings("unchecked")
	public List<Long> getUnAttendants(Long meetingId) {
    	String hql = "select distinct r.userId from " + MtReply.class.getName() + " as r where r.meetingId=? and r.feedbackFlag=?";
    	return (List<Long>)super.find(hql, -1, -1, null, meetingId, Constants.FEEDBACKFLAG_UNATTEND);
    }

	 /***
	 * 根据红杉树传过来的视频会议ID。查询A8系统视频会议实体
	 * @param meetingId
	 * @return MtMeeting
	 * @author radishlee 2011-11-3
	 */
	public List getMeetingByInfowarelabMeetingId(String meetingId) {
		String hql = " from "+MtMeeting.class.getName()+" where videoMeetingId = :meetingIds";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("meetingIds", meetingId);
    	return  this.find(hql.toString(), params);
	}

	 /***
	 * 根据协同ID。查询A8系统视频会议实体
	 * @param summaryId
	 * @return MtMeeting
	 * @author radishlee 2012-2-14
	 */
	public List getMeetingBySummaryId(Long summaryId) {
		String hql = " from "+MtMeeting.class.getName()+" where summaryId = :summaryId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("summaryId", summaryId);
    	return  this.find(hql.toString(), params);
	}
}
