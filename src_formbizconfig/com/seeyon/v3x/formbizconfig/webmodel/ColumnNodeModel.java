package com.seeyon.v3x.formbizconfig.webmodel;

import com.seeyon.v3x.common.ObjectToXMLBase;
/**
 * 配合AJAX，返回表单查询或表单统计栏目挂接项节点元素值<br>
 * 避免当查询或统计模板名称中包含特殊字符(比如：与拼接的分隔符相同)时，分割之后出现错误<br>
 * 此前的策略是将表单ID对应的查询模板、统计模板名称拼接在一起，如果模板名称中包含用于分隔的字符，随后在JS中解析所得结果便会有误<br>
 * @see com.seeyon.v3x.formbizconfig.manager.FormBizConfigColumnManagerImpl#getFormQueryAndReportNames
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-04-02
 */
public class ColumnNodeModel extends ObjectToXMLBase {
	/** 对应的表单ID */
	public Long formId;
	/** 模板名称  */
	public String columnName;
	/** 
	 * 模板所属分类，详见{@link FormBizConfigConstants}
	 * 由于此类只是为了表单查询和统计模板解析之用，因而其所属分类只可能为表单查询或表单统计 
	 */
	public int category;

	public int getCategory() {
		return category;
	}
	
	/**
	 * 定义此POJO的构造方法：
	 * @param formId		对应的表单ID
	 * @param columnName	模板名称
	 * @param category		模板所属分类
	 */
	public ColumnNodeModel(Long formId, String columnName, int category) {
		super();
		this.formId = formId;
		this.columnName = columnName;
		this.category = category;
	}
	
	public ColumnNodeModel() {
		
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}
	
}
