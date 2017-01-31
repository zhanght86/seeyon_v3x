package com.seeyon.v3x.meetingroom.manager.cap;

import com.seeyon.cap.meetingroom.manager.MeetingRoomManagerCAP;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.meetingroom.manager.MeetingRoomManager;

public class MeetingRoomManagerCAPImpl implements MeetingRoomManagerCAP {

	private MeetingRoomManager meetingRoomManager;

	public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
		this.meetingRoomManager = meetingRoomManager;
	}

	@Override
	public void updateMeetingRoomMangerBatch(long adminIdLong, long adminNewLong, User user) {
		meetingRoomManager.updateMeetingRoomMangerBatch(adminIdLong, adminNewLong, user);
	}
	
}