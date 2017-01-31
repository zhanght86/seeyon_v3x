package com.seeyon.v3x.bulletin.util.hql;

import java.util.List;
import java.util.Map;

/**
 * Hql结果模型，包括Hql语句，对应命名参数键值Map，是否去重复及去重复时所依赖的列名
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-7-28
 */
public class HqlResult {

	/** Hql语句 */
	private String hql;
	
	/**命名参数键值对Map */
	private Map<String, Object> namedParameter;
	
	/**占位参数  */
	private List<Object> indexParameter;
	
	/** 是否需要去重复 */
	private boolean distinct;
	
	/** 去重复所依赖的列名 */
	private String distinctColumn;
	
	public HqlResult() {}
	
	public HqlResult(String hql, Map<String, Object> namedParameter) {
		super();
		this.hql = hql;
		this.namedParameter = namedParameter;
	}
	
	public HqlResult(String hql, Map<String, Object> namedParameter, boolean distinct, String distinctColumn) {
		this(hql, namedParameter);
		this.distinct = distinct;
		this.distinctColumn = distinctColumn;
	}
	
	public String getHql() {
		return hql;
	}
	public void setHql(String hql) {
		this.hql = hql;
	}
	public Map<String, Object> getNamedParameter() {
		return namedParameter;
	}
	public void setNamedParameter(Map<String, Object> namedParameter) {
		this.namedParameter = namedParameter;
	}
	public boolean isDistinct() {
		return distinct;
	}
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	public String getDistinctColumn() {
		return distinctColumn;
	}
	public void setDistinctColumn(String distinctColumn) {
		this.distinctColumn = distinctColumn;
	}
	public List<Object> getIndexParameter() {
		return indexParameter;
	}
	public void setIndexParameter(List<Object> indexParameter) {
		this.indexParameter = indexParameter;
	}
	
}
