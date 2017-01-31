package com.seeyon.v3x.blog.listener;

import com.seeyon.v3x.blog.manager.BlogManager;
import com.seeyon.v3x.organization.event.AddMemberEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class BlogOrganizationEventListener {

	private BlogManager blogManager;

	public BlogManager getBlogManager() {
		return blogManager;
	}

	public void setBlogManager(BlogManager blogManager) {
		this.blogManager = blogManager;
	}

	@ListenEvent(event = AddMemberEvent.class)
	public void onAddMember(AddMemberEvent evt) throws Exception {
		blogManager.createEmployee(evt.getMember().getId(), evt.getMember()
				.getOrgAccountId());
	}
}
