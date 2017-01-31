package www.seeyon.com.v3x.form.controller.pageobject;

import www.seeyon.com.v3x.form.manager.define.trigger.EventTemplate;

/**
 * 触发模板类型 显示名称
 * @author Administrator
 *
 */
public class EventTemplateObject extends EventTemplate{
	
	/**
	 * 显示的名称
	 */
	private String flowTemplateName;

	/**
	 * @return
	 */
	public String getFlowTemplateName() {
		return flowTemplateName;
	}

	/**
	 * @param flowTemplateName
	 */
	public void setFlowTemplateName(String flowTemplateName) {
		this.flowTemplateName = flowTemplateName;
	}
}