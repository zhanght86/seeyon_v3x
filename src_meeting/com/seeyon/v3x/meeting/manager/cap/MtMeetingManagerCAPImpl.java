package com.seeyon.v3x.meeting.manager.cap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.cap.meeting.domain.MtMeetingCAP;
import com.seeyon.cap.meeting.manager.MtMeetingManagerCAP;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.manager.MtMeetingManager;

public class MtMeetingManagerCAPImpl implements MtMeetingManagerCAP {

	private MtMeetingManager mtMeetingManager;

	public void setMtMeetingManager(MtMeetingManager mtMeetingManager) {
		this.mtMeetingManager = mtMeetingManager;
	}

	@Override
	public void createCalEvent(MtMeetingCAP bean, Long userId) {
		MtMeeting mtMeeting = new MtMeeting();
		BeanUtils.convert(mtMeeting, bean);
		mtMeetingManager.createCalEvent(mtMeeting, userId);
	}

	@Override
	public boolean deleteCalEvent(Long meetingId, Long userId) {
		return mtMeetingManager.deleteCalEvent(meetingId, userId);
	}

	@Override
	public MtMeetingCAP getById(Long id) {
		MtMeeting mtMeeting = mtMeetingManager.getById(id);
		if (mtMeeting == null) {
			return null;
		}
		MtMeetingCAP mtMeetingCAP = new MtMeetingCAP();
		BeanUtils.convert(mtMeetingCAP, mtMeeting);
		return mtMeetingCAP;
	}

	@Override
	public MtMeetingCAP getByMtId(Long id) {
		MtMeeting mtMeeting = mtMeetingManager.getByMtId(id);
		if (mtMeeting == null) {
			return null;
		}
		MtMeetingCAP mtMeetingCAP = new MtMeetingCAP();
		BeanUtils.convert(mtMeetingCAP, mtMeeting);
		return mtMeetingCAP;
	}

	@Override
	public IndexInfo getIndexInfo(long id) throws Exception {
		return mtMeetingManager.getIndexInfo(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtMeetingCAP> getProjectMeeting(Long projectId, Long phaseId, Long currentUserId) {
		List<MtMeeting> list = mtMeetingManager.getProjectMeeting(projectId, phaseId, currentUserId);
		if (list == null) {
			return null;
		}
		return (List<MtMeetingCAP>) BeanUtils.converts(MtMeetingCAP.class, list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtMeetingCAP> getProjectMeetingByCondition(String condition, Long projectId, Long phaseId, Long currentUserId, Map<String, Object> paramMap) {
		List<MtMeeting> list = mtMeetingManager.getProjectMeetingByCondition(condition, projectId, phaseId, currentUserId, paramMap);
		if (list == null) {
			return null;
		}
		return (List<MtMeetingCAP>) BeanUtils.converts(MtMeetingCAP.class, list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtMeetingCAP> getUserMeetingByManagerType(Long userId, int type, Date startTime, Date endTime) {
		List<MtMeeting> list = mtMeetingManager.getUserMeetingByManagerType(userId, type, startTime, endTime);
		if (list == null) {
			return null;
		}
		return (List<MtMeetingCAP>) BeanUtils.converts(MtMeetingCAP.class, list);
	}

	@Override
	public HashMap<Long, int[]> getUsersMeetingManagerList(List<Long> userIds, Date startTime, Date endTime) {
		return mtMeetingManager.getUsersMeetingManagerList(userIds, startTime, endTime);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtMeetingCAP> getMeetingByInfowarelabMeetingId(String meetingId) {
		return (List<MtMeetingCAP>) BeanUtils.converts(MtMeetingCAP.class, mtMeetingManager.getMeetingByInfowarelabMeetingId(meetingId));
	}

	@Override
	public void updateState(Long meetingId, int state2Update) {
		mtMeetingManager.updateState(meetingId, state2Update);
	}
	
	@SuppressWarnings("unchecked")
	public List<MtMeetingCAP> findAllMeetings4User(Long userId, Date beginDate, Date endDate) {
		List<MtMeeting> list = mtMeetingManager.findAllMeetings4User(userId, beginDate, endDate);
		if (list == null) {
			return null;
		}
		return (List<MtMeetingCAP>) BeanUtils.converts(MtMeetingCAP.class, list);
	}
	
    //政务会议 是否具有审核权限 ，会议CAP已实现 此处暂时空实现 --xiangfan 20120407 
	@Override
	public boolean isMeetingReviewRight(Long accountId, String userIds,
			String mtRightType) {
		// TODO Auto-generated method stub
		return false;
	}
	
}