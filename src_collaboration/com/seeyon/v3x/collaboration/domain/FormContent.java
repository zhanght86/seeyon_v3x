package com.seeyon.v3x.collaboration.domain;

import java.util.List;

public class FormContent extends ColBody {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7012805214910976262L;
	private List<FormBody> forms;

	public List<FormBody> getForms() {
		return forms;
	}

	public void setForms(List<FormBody> forms) {
		this.forms = forms;
	}

}
