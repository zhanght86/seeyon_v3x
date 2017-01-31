package com.seeyon.v3x.meeting.manager.cap;

import java.util.List;

import com.seeyon.cap.meeting.domain.MtReplyCAP;
import com.seeyon.cap.meeting.manager.MtReplyManagerCAP;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.meeting.domain.MtReply;
import com.seeyon.v3x.meeting.manager.MtReplyManager;

public class MtReplyManagerCAPImpl implements MtReplyManagerCAP {

	private MtReplyManager replyManager;


	public void setReplyManager(MtReplyManager replyManager) {
		this.replyManager = replyManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtReplyCAP> findByMeetingIdAndUserId(Long meetingId, Long userId) {
		List<MtReply> list = replyManager.findByMeetingIdAndUserId(meetingId, userId);
		if (list == null) {
			return null;
		}
		return (List<MtReplyCAP>) BeanUtils.converts(MtReplyCAP.class, list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtReplyCAP> findByPropertyNoInit(String property, Object value) {
		List<MtReply> list = replyManager.findByPropertyNoInit(property, value);
		if (list == null) {
			return null;
		}
		return (List<MtReplyCAP>) BeanUtils.converts(MtReplyCAP.class, list);
	}

	@Override
	public void reply(long meetingId, Long uid, String opinion, int attitude) throws BusinessException {
		replyManager.reply(meetingId, uid, opinion, attitude);
	}

	@Override
	public MtReplyCAP save(MtReplyCAP template) throws BusinessException {
		MtReply mtReply = new MtReply();
		BeanUtils.convert(mtReply, template);
		mtReply = replyManager.save(mtReply);
		template.setId(mtReply.getId());
		return template;
	}

}