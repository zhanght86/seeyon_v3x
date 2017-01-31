package com.seeyon.v3x.formbizconfig.webmodel;

import java.util.List;

import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
/**
 * 辅助用户在选择表单模板时，界面中表单模板所属应用类型的树状展现<br>
 * 包含用户选中<b>一个特定应用类型</b>进行查询及用户选中的表单模板包含<b>多个所属应用类型(可能包括"外单位模板")</b>两种情况<br>
 */
public class TempleteCategorysWebModel {
	public static final String SEARCH_BY_CATEGORY = "category";
	public static final String SEARCH_BY_SUBJECT = "subject";
	
	
	/** 用户是否在以表单模板所属应用类型在进行查询 */
	private boolean searchByCategory;
	
	/** 是否包含外单位模板分类并显示 */
	private boolean showOtherAccountCategory;
	
	/** 所得到的表单模板所属应用类型集合 */
	private List<TempleteCategory> categorys;

	public List<TempleteCategory> getCategorys() {
		return categorys;
	}

	public void setCategorys(List<TempleteCategory> categorys) {
		this.categorys = categorys;
	}

	public void setSearchByCategory(boolean searchByCategory) {
		this.searchByCategory = searchByCategory;
	}
	
	public boolean isSearchByCategory() {
		return searchByCategory;
	}

	public boolean isShowOtherAccountCategory() {
		return showOtherAccountCategory;
	}
	
	public void setShowOtherAccountCategory(boolean showOtherAccountCategory) {
		this.showOtherAccountCategory = showOtherAccountCategory;
	}
}
