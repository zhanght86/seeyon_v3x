package com.seeyon.v3x.organization.inexportutil;
/**
 * 
 * @author kyt
 *
 */
public class DataObject {
	//字段名称
	private String fieldName;
	//要求字段长度
	private int length;
	//表名
	private String tableName;
	//匹配中文名 匹配后修改与页面相符
	private String matchCHNName;
	//匹配英文名 匹配后修改与页面相符
	private String matchENGName;
	//对应excel文件中的列数
	private int columnnum = -1;
	
	//匹配的excel列名
	private String matchExcelName;
	
	
	
	
	public String getMatchExcelName() {
		return matchExcelName;
	}
	public void setMatchExcelName(String matchExcelName) {
		this.matchExcelName = matchExcelName;
	}
	public int getColumnnum() {
		return columnnum;
	}
	public void setColumnnum(int columnnum) {
		this.columnnum = columnnum;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getMatchCHNName() {
		return matchCHNName;
	}
	public void setMatchCHNName(String matchCHNName) {
		this.matchCHNName = matchCHNName;
	}
	public String getMatchENGName() {
		return matchENGName;
	}
	public void setMatchENGName(String matchENGName) {
		this.matchENGName = matchENGName;
	}
	
}
