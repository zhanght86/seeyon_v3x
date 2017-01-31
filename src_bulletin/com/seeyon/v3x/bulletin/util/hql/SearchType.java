package com.seeyon.v3x.bulletin.util.hql;

import org.apache.commons.lang.StringUtils;

/**
 * 按照公告各种属性进行搜索的类型枚举
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-7-30
 */
public enum SearchType {
	By_Title("title"),
	By_Publish_User("publishUserId"),
	By_Publish_Date("publishDate"),
	//暂未用到，日后可用于发起公告列表按照创建日期查询
	By_Create_Date("createDate"),
	By_Bul_Type("type"),
	//暂未用到，日后可扩展
	By_Top_Flag("topOrder"),
	None("");

	private String value;

	SearchType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public static SearchType getSearchType(String value) {
		SearchType[] enums = SearchType.values();
		String typeValue = StringUtils.defaultIfEmpty(value, None.value());
		for(SearchType type : enums) {
			if(type.value().equals(typeValue)) {
				return type;
			}
		}
		throw new IllegalArgumentException("不合法的搜索类型：[" + value +"]");
	}

}
