package www.seeyon.com.v3x.form.controller;

public class FormApp {
	/**
	 * 流程模板:模板ID;无流程表:绑定ID
	 */
	private String id;
	/**
	 * 流程模板:模板名称;无流程表:绑定名称
	 */
	private String name;
	/**
	 * 表单id
	 */
	private String appFormId;
	/**
	 * 来源类型
	 */
	private int sourceType;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAppFormId() {
		return appFormId;
	}
	public void setAppFormId(String appFormId) {
		this.appFormId = appFormId;
	}
	public int getSourceType() {
		return sourceType;
	}
	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

}