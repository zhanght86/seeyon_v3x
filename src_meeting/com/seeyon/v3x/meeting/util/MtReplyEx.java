package com.seeyon.v3x.meeting.util;

import java.util.List;

import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.meeting.domain.MtReply;

public class MtReplyEx {
	private MtReply mtReply;

	private List<Attachment> atts;

	public List<Attachment> getAtts() {
		return atts;
	}

	public void setAtts(List<Attachment> atts) {
		this.atts = atts;
	}

	public MtReply getMtReply() {
		return mtReply;
	}

	public void setMtReply(MtReply mtReply) {
		this.mtReply = mtReply;
	}
}
