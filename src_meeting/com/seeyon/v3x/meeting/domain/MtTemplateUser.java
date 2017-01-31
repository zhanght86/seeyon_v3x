package com.seeyon.v3x.meeting.domain;

import com.seeyon.v3x.meeting.domain.base.BaseMtTemplateUser;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;



public class MtTemplateUser extends BaseMtTemplateUser {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MtTemplateUser () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MtTemplateUser (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MtTemplateUser (
		java.lang.Long id,
		java.lang.Long authId,
		java.lang.Integer sort) {

		super (
			id,
			authId,
			sort);
	}

/*[CONSTRUCTOR MARKER END]*/

	private String templateName;
	private String userName;
	private V3xOrgEntity orgEntity;


	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public V3xOrgEntity getOrgEntity() {
		return orgEntity;
	}

	public void setOrgEntity(V3xOrgEntity orgEntity) {
		this.orgEntity = orgEntity;
	}
}