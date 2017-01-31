package com.seeyon.v3x.formbizconfig.domain;
import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 表单业务配置：所选表单模板与表单业务配置的中心关系
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public class FormBizConfigTempletProfile extends BaseModel {
	
	public static final String PROP_BIZCONFIG_ID = "formBizConfigId";
	public static final String PROP_TEMPLET_ID = "formTempletId";

	private static final long serialVersionUID = -6544457555031993955L;
	
	/** 表单业务配置ID */
	private Long formBizConfigId;
	/** 表单模板ID */
	private Long formTempletId;
	/** 表单模板选择排序号 */
	private int sortId;
	
	/**
	 * 定义构造方法
	 * @param formBizConfigId 表单业务配置ID
	 * @param formTempletId   表单模板ID
	 * @param sortId		     用户选择表单模板时的排序号
	 */
	public FormBizConfigTempletProfile(Long formBizConfigId, Long formTempletId, int sortId) {
		this.setIdIfNew();
		this.formBizConfigId = formBizConfigId;
		this.formTempletId = formTempletId;
		this.sortId = sortId;
	}
	
	public FormBizConfigTempletProfile() {
	}

	public int getSortId() {
		return sortId;
	}
	public void setSortId(int sortId) {
		this.sortId = sortId;
	}
	public Long getFormTempletId() {
		return formTempletId;
	}
	public void setFormTempletId(Long formTempletId) {
		this.formTempletId = formTempletId;
	}
	public Long getFormBizConfigId() {
		return formBizConfigId;
	}
	public void setFormBizConfigId(Long formBizConfigId) {
		this.formBizConfigId = formBizConfigId;
	}

}
