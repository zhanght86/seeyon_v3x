package com.seeyon.v3x.formbizconfig.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;

/**
 * 表单业务配置：栏目挂接项
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public class FormBizConfigColumn extends BaseModel {
	
	private static final long serialVersionUID = -4417895920563856262L;
	
	/** 所属分类：比如表单流程为1，表单查询为3，表单流程下的待办事项为11 */
	private int category;
	/** 所属父级分类：比如待办事项的父级分类为表单流程（1） */
	private int parentCategory;
	/** 栏目挂接项名称 */
	private String name;
	/** 排序号，如表单流程下的三种事项排序先后与首页栏目的展现应当一致 */
	private int sortId;
	/** 只有表单查询或表单统计下的查询或统计模板栏目挂接项才对应一个表单ID，其他的为null */
	private Long formId;
	/** 表单业务配置ID */
	private Long formBizConfigId;
	
	/**
	 * 表单业务配置：栏目挂接项构造方法
	 * @param category  		所属分类：比如表单流程为1，表单查询为3，表单流程下的待办事项为11
	 * @param parentCategory	所属父级分类：比如待办事项的父级分类为表单流程（1）
	 * @param name				栏目挂接项名称
	 * @param sortId			排序号，如表单流程下的三种事项排序先后与首页栏目的展现应当一致
	 * @param formId			只有表单查询或表单统计下的查询或统计模板栏目挂接项才对应一个表单ID，其他的为null
	 * @param formBizConfigId	表单业务配置ID
	 */
	public FormBizConfigColumn(int category, int parentCategory, String name, int sortId, Long formId, Long formBizConfigId) {
		this.setIdIfNew();
		this.setCategory(category);
		this.setParentCategory(parentCategory);
		this.setName(name);
		this.setSortId(sortId);
		this.setFormId(formId);
		this.setFormBizConfigId(formBizConfigId);
	}
	
	public FormBizConfigColumn() {
		
	}
	
	/**
	 * 判断栏目挂接项所属分类是否为表单查询或表单统计<br>
	 */
	public boolean isFormQueryOrStatistic() {
		return this.parentCategory == FormBizConfigConstants.COLUMN_FORM_QUERY ||
			   this.parentCategory == FormBizConfigConstants.COLUMN_FORM_STATISTIC;
	}
	
	/** 校验类型无效，可能是由于意外情况导致 */
	public static final int AUTH_CHECK_INVALID = 0;
	/** 表明对表单查询模板进行权限校验 */
	public static final int AUTH_CHECK_QUERY = 1;
	/** 表明对表单统计模板进行权限校验 */
	public static final int AUTH_CHECK_STATISTIC = 2;
	
	/**
	 * 获取栏目挂接项在表单查询或表单统计中的所属分类，以便获取权限查询所需的参数值
	 */
	public int getFormAuthCheckType() {
		if(this.getParentCategory() == FormBizConfigConstants.COLUMN_FORM_QUERY)
			return AUTH_CHECK_QUERY;
		else if(this.getParentCategory() == FormBizConfigConstants.COLUMN_FORM_STATISTIC)
			return AUTH_CHECK_STATISTIC;
		else
			return AUTH_CHECK_INVALID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(int parentCategory) {
		this.parentCategory = parentCategory;
	}

	public Long getFormBizConfigId() {
		return formBizConfigId;
	}

	public void setFormBizConfigId(Long formBizConfigId) {
		this.formBizConfigId = formBizConfigId;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

}