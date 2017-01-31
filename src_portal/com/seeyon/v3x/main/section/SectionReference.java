/**
 * 
 */
package com.seeyon.v3x.main.section;

import com.seeyon.v3x.common.ObjectToXMLBase;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.util.EnumUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-12-24
 */
public final class SectionReference extends ObjectToXMLBase implements java.io.Serializable {

	private static final long serialVersionUID = 4326722580074385266L;

	public static enum ValueType {
		/** 下拉 select */
		singleOption,
		/** 多选 checkbox，必须选择一个才可以 */
		multiOption,
		/** 文本 text */
		text,
		/** 弹出关联系统 */
		dialog,
		/** 图片 */
		image,
		/** 这个配置比较特殊，页签 */
		panel,
		/** 单栏目配置页签 */
		singlePanel,
		/** 文本域textarea */
		textarea,
		/** 日期段-月份选择器 */
		betweenMonth,
	}

	//input Name
	private String name;
	//显示
	private String subject;
	//默认值
	private String defaultValue;
	//是否只读
	private boolean readOnly = false;

	//类型
	private int valueType = ValueType.text.ordinal();
	
	/**
	 * 判断js。用逗号隔开
	 */
	private String validate="";
	
	/**
	 * 判断js所用的参数<br>
	 * 例如：如果validate="maxLength" 那么字段上要指定maxLength属性。<br>
	 * validateValue="maxLength='100' minLength='8'"
	 */
	private String validateValue;
	
	private SectionReferenceValueRange[] valueRanges;
	
	private SectionReference hiddenValue;
	
	private String singleBeanId = "";
	
	public String getSingleBeanId() {
		return singleBeanId;
	}

	public void setSingleBeanId(String singleBeanId) {
		this.singleBeanId = singleBeanId;
	}

	/**
	 * 显示默认值，是否根据版本不同显示不同<br>
	 * 比如空间口号需要根据区分政务版与费政务版 
	 */
	private String editSuffix;
	
	/**
	 * 如果该类型为html,那么必须设置取得html的方法(实现HtmlSectionTemplete.getHTML())
	 */
	private String sectionId;
	
	/**
	 * 特殊页签，选择的url地址
	 */
	private String panelSetUrl="";
	

	//内容发生变化时候，要进行的js事件。
	public String onChange = "";
	
	public String changeValue = "";
	
	/**
	 * 得到当内容发生变化时候，产生的js对象。<br>
	 * 直接在portal-common.js中添加。<br>
	 * eq:checkDefaultValue &nbsp;&nbsp;输出的js为
	 * checkDefaultValue(this,"N_" + fragmentId + "_" + sectionId + "_" + index + "_")<br>
	 * 取得其他参数可以使用"N_" + fragmentId + "_" + sectionId + "_" + index + "_"+paramName
	 * @return
	 */
	public String getOnChange() {
		return onChange;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	public String getChangeValue() {
		return changeValue;
	}

	public void setChangeValue(String changeValue) {
		this.changeValue = changeValue;
	}
	
	public String getPanelSetUrl() {
		return panelSetUrl;
	}

	public void setPanelSetUrl(String panelSetUrl) {
		this.panelSetUrl = panelSetUrl;
	}

	public String getDefaultValue() {
		String result = this.defaultValue;
		if(Strings.isNotBlank(this.editSuffix)){
			try {
				String suffix = (String)SysFlag.valueOf(this.editSuffix).getFlag();
				result +=suffix;
			} catch (Exception e) {
			}
		}
		return result;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	
	/**
	 * 标示
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * 是否只读，用户不可改
	 * 
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

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

	public SectionReferenceValueRange[] getValueRanges() {
		return valueRanges;
	}

	/**
	 * 取值范围
	 * 
	 * ValueType
	 * 
	 * @param valueRange
	 */
	public void setValueRanges(SectionReferenceValueRange[] valueRanges) {
		this.valueRanges = valueRanges;
	}

	public ValueType getValueTypeEnum() {
		return EnumUtil.getEnumByOrdinal(ValueType.class, valueType);
	}

	/**
	 * 取值类型
	 * 
	 * @see SectionReference.ValueType 0-单选，1-多选，2-用户输入，3-弹出对话框选择关联系统
	 * @param valueType
	 */
	public void setValueType(int valueType) {
		this.valueType = valueType;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public SectionReference getHiddenValue() {
		return hiddenValue;
	}

	public void setHiddenValue(SectionReference hiddenValue) {
		this.hiddenValue = hiddenValue;
	}

	public String getEditSuffix() {
		return editSuffix;
	}

	public void setEditSuffix(String editSuffix) {
		this.editSuffix = editSuffix;
	}

	public String getValidate() {
		return validate;
	}

	/**
	 * 输入判断，用逗号隔开，比如notNull,maxLength
	 * @param validate
	 */
	public void setValidate(String validate) {
		this.validate = validate;
	}

	public String getValidateValue() {
		return validateValue;
	}

	public void setValidateValue(String validateValue) {
		this.validateValue = validateValue;
	}


}
