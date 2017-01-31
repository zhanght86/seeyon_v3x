/**
 * 
 */
package com.seeyon.v3x.main.section;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 * 
 */
public class SectionReferenceValueRange implements java.io.Serializable {
	private static final long serialVersionUID = 886534582406373165L;
	
	/**
	 * 页签项，页签配置的值
	 */
	public String panelValue="";
	
	/**
	 * 设置页签的地址。
	 */
	public String panelSetUrl="";
	
	public boolean readOnly = false;
	
	/**
	 * 是否是备选项
	 */
	public boolean isBackUp=false; 
	
	public String subject;

	public String value;


	public String getSubject() {
		return subject;
	}

	/**
	 * 显示名称，如果做国际化，则输入国际化key
	 * 
	 * @see BaseSection#setResourceBundle(String) 在section中统一定义国际化资源
	 * 
	 * @param subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isBackUp() {
		return isBackUp;
	}

	public void setBackUp(boolean isBackUp) {
		this.isBackUp = isBackUp;
	}

	public String getPanelValue() {
		return panelValue;
	}

	public void setPanelValue(String panelValue) {
		this.panelValue = panelValue;
	}

	public String getPanelSetUrl() {
		return panelSetUrl;
	}

	public void setPanelSetUrl(String panelSetUrl) {
		this.panelSetUrl = panelSetUrl;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
