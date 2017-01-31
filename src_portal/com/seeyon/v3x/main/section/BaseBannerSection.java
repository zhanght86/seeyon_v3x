package com.seeyon.v3x.main.section;

import java.util.Map;

import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;

public abstract class BaseBannerSection extends BaseSection {

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		return null;
	}

	public abstract String getHTML(String entityId, String ordinal, String spaceType, String ownerId, Long spaceId);
	
}