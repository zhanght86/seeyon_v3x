package www.seeyon.com.v3x.form.controller.pageobject;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormAppAuthObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1690193853664228800L;
	
	/**
	 * 业务表单应用授权ID
	 */
    private String id;
	
	/**
	 * 业务表单应用绑定名称
	 */
	private String name;
	
	/**
	 * 列表显示项(输出数据项)
	 */
	private String dataField;
	private String dataFieldValue;
	private String dataTablevalue;
	
	/**
	 * 排序方式
	 */
	private String resultSort;
	private String resultSortValue;
	
	/**
	 * 查询条件
	 */
	private String customQueryField;
	private String customQueryFieldValue;
	private String customQueryTableValue;
	
	/**
	 * 操作授权
	 */
	private Map<String,FormOperAuthObject> appOperAuthObjectMap = new LinkedHashMap<String,FormOperAuthObject>();
	
	private String xmlString;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataField() {
		return dataField;
	}

	public void setDataField(String dataField) {
		this.dataField = dataField;
	}

	public String getDataFieldValue() {
		return dataFieldValue;
	}

	public void setDataFieldValue(String dataFieldValue) {
		this.dataFieldValue = dataFieldValue;
	}

	public String getDataTablevalue() {
		return dataTablevalue;
	}

	public void setDataTablevalue(String dataTablevalue) {
		this.dataTablevalue = dataTablevalue;
	}

	public String getResultSort() {
		return resultSort;
	}

	public void setResultSort(String resultSort) {
		this.resultSort = resultSort;
	}

	public String getResultSortValue() {
		return resultSortValue;
	}

	public void setResultSortValue(String resultSortValue) {
		this.resultSortValue = resultSortValue;
	}

	public String getCustomQueryField() {
		return customQueryField;
	}

	public void setCustomQueryField(String customQueryField) {
		this.customQueryField = customQueryField;
	}

	public String getCustomQueryFieldValue() {
		return customQueryFieldValue;
	}

	public void setCustomQueryFieldValue(String customQueryFieldValue) {
		this.customQueryFieldValue = customQueryFieldValue;
	}

	public String getCustomQueryTableValue() {
		return customQueryTableValue;
	}

	public void setCustomQueryTableValue(String customQueryTableValue) {
		this.customQueryTableValue = customQueryTableValue;
	}

	public Map<String, FormOperAuthObject> getAppOperAuthObjectMap() {
		return appOperAuthObjectMap;
	}

	public void setAppOperAuthObjectMap(
			Map<String, FormOperAuthObject> appOperAuthObjectMap) {
		this.appOperAuthObjectMap = appOperAuthObjectMap;
	}

	public String getXmlString() {
		return xmlString;
	}

	public void setXmlString(String xmlString) {
		this.xmlString = xmlString;
	}

}