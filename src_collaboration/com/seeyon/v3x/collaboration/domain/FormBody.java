package com.seeyon.v3x.collaboration.domain;


public class FormBody{
	private String formApp;
	private String form;
	private String operationName;
	private String formDataId;
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	public String getFormApp() {
		return formApp;
	}
	public void setFormApp(String formApp) {
		this.formApp = formApp;
	}
	public String getFormDataId() {
		return formDataId;
	}
	public void setFormDataId(String formDataId) {
		this.formDataId = formDataId;
	}
	public String getOperationName() {
		return operationName;
	}
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
}
