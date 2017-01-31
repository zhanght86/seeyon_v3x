package com.seeyon.v3x.main.listener;

import com.seeyon.v3x.main.phrase.CommonPhraseManager;
import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class MainOrganizationEventListener {

	private CommonPhraseManager phraseManager;

	public CommonPhraseManager getPhraseManager() {
		return phraseManager;
	}

	public void setPhraseManager(CommonPhraseManager phraseManager) {
		this.phraseManager = phraseManager;
	}

	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws Exception {
		phraseManager.generateCommonPharse(evt.getAccount().getId());
	}
}
