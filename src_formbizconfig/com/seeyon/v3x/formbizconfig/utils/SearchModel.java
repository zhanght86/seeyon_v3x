package com.seeyon.v3x.formbizconfig.utils;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.util.Strings;

/**
 * 将业务配置搜索条件进行封装，也适用于其他模块类似场景
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-04-02
 */
public class SearchModel {
	/**
	 * 搜索类型：按照创建者、创建日期、业务配置名称和挂接类型等
	 */
	public static final String SEARCH_BY_CREATOR = "createUser";
	public static final String SEARCH_BY_CREATE_DATE = "createDate";
	public static final String SEARCH_BY_NAME = "name";
	public static final String SEARCH_BY_CONFIG_TYPE = "configType";

	/** 搜索类型 */
	private String searchType;
	/** 搜索值1 */
	private String searchValue1;
	/** 搜索值2 */
	private String searchValue2;
	/** 搜索显示结果是否需要分页，默认需要，在需要获取全部记录时，需将此值设为false */
	private boolean pagination = true;
	
	public SearchModel() {}
	
	/**
	 * 定义业务配置搜索条件模型的构造方法
	 * @param searchType	搜索类型 
	 * @param searchValue1	搜索值1
	 * @param searchValue2	搜索值2
	 */
	public SearchModel(String searchType, String searchValue1, String searchValue2) {
		super();
		this.searchType = searchType;
		this.searchValue1 = searchValue1;
		this.searchValue2 = searchValue2;
	}
	
	/**
	 * 通过最常见的查询场景：一个查询类型，一到两个查询值来封装对应的查询模型
	 * @param request	用户的查询请求
	 * @return	查询模型
	 */
	public static SearchModel getSearchModel(HttpServletRequest request) {
		return new SearchModel(request.getParameter("condition"), 
				 			   request.getParameter("textfield"), 
				 			   request.getParameter("textfield1"));
	}
	
	/**
	 * 当前搜索类型是否为按照名称进行搜索：要求搜索类型匹配、搜索值不为空
	 */
	public boolean searchByName() {
		return SEARCH_BY_NAME.equals(this.searchType) && Strings.isNotBlank(this.searchValue1);
	}
	
	/**
	 * 当前搜索类型是否为按照人员姓名进行搜索：要求搜索类型匹配、搜索值不为空
	 * @param useSelectPeople	是否使用选人界面传递参数(此时包括人员姓名、人员ID两个参数)
	 */
	public boolean searchByCreator(boolean useSelectPeople) {
		boolean valid = SEARCH_BY_CREATOR.equals(this.searchType) && Strings.isNotBlank(this.searchValue1);
		boolean valid2 = useSelectPeople? Strings.isNotBlank(this.searchValue2) : true;
		return valid && valid2;
	}
	
	/**
	 * 当前搜索类型是否为按照人员姓名(使用选人界面)进行搜索：要求搜索类型匹配、搜索值不为空
	 */
	public boolean searchByCreator() {
		return this.searchByCreator(true);
	}
	
	/**
	 * 当前搜索类型是否为按照创建或修改日期进行搜索：要求搜索类型匹配、两个搜索值不全为空
	 */
	public boolean searchByDate() {
		return SEARCH_BY_CREATE_DATE.equals(this.searchType) && 
			   (Strings.isNotBlank(this.searchValue1) || Strings.isNotBlank(this.searchValue2));
	}
	
	/*-----------------------------其他模块扩展使用------------------------------ */
	public static final String SEARCH_BY_VERSION_NUMBER = "versionNumber";
	/**
	 * 当前搜索类型是否为按照文档版本号进行搜索：要求搜索类型匹配、搜索值不为空
	 */
	public boolean searchByVersionNumber() {
		return SEARCH_BY_VERSION_NUMBER.equals(this.searchType) && Strings.isNotBlank(this.searchValue1);
	}

	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public String getSearchValue1() {
		return searchValue1;
	}
	public void setSearchValue1(String searchValue1) {
		this.searchValue1 = searchValue1;
	}
	public String getSearchValue2() {
		return searchValue2;
	}
	public void setSearchValue2(String searchValue2) {
		this.searchValue2 = searchValue2;
	}
	public boolean isPagination() {
		return pagination;
	}
	public void setPagination(boolean pagination) {
		this.pagination = pagination;
	}
	
	@Override
	public String toString() {
		return "[SearchModel :" + this.searchType + ", " + this.searchValue1 + ", " + this.searchValue2 + "]";
	}
	
}
