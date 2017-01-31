package com.seeyon.v3x.collaboration.listener;

import com.seeyon.v3x.collaboration.event.CollaborationCancelEvent;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.common.isignature.ISignatureHtmlManager;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class CollaborationOperationEventListener {
	private ColManager colManager;
	private ISignatureHtmlManager iSignatureHtmlManager;
	public void setiSignatureHtmlManager(ISignatureHtmlManager iSignatureHtmlManager) {
		this.iSignatureHtmlManager = iSignatureHtmlManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	@ListenEvent(event = CollaborationCancelEvent.class)
	public void onRepeal(CollaborationCancelEvent event)throws Exception {
		colManager.deleteColTrackMembersByObjectId(event.getSummaryId());
		//删除ISIgnatureHTML专业签章。
		iSignatureHtmlManager.deleteAllByDocumentId(event.getSummaryId());
	}
}
