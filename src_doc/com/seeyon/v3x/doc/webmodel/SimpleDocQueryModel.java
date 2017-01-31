package com.seeyon.v3x.doc.webmodel;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.util.Strings;

/**
 * 简单属性查询模型
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-12-16
 */
public class SimpleDocQueryModel {
	private static final String BEGIN_TIME = "beginTime";
	private static final String END_TIME = "endTime";
	private static final String ORG_NAME = "Name";
	private static final String IS_DEFAULT = "IsDefault";
	
	@Override
	public String toString() {
		return "[" + propertyName + ", " + propertyType + ", " + simple + ", Value=" + value1 + ", " + value2 + "]";
	}
	
	/**
	 * 属性名称
	 */
	private String propertyName;
	/**
	 * 属性类型
	 */
	private int propertyType;
	/**
	 * 是否为简单属性（true：文档基本属性，false：关联元数据属性）
	 */
	private boolean simple;
	/**
	 * 搜索值1
	 */
	private String value1;
	/**
	 * 搜索值2
	 */
	private String value2;
	/**
	 * 搜索值3(备用)
	 */
	private String value3;
	/**
	 * 搜索值变量名1
	 */
	private String paramName1;
	/**
	 * 搜索值变量名2
	 */
	private String paramName2;
	/**
	 * 搜索值变量名3(备用)
	 */
	private String paramName3;
	
	/**
	 * 判断单个属性查询模型是否有效：所要查询的属性名称不为空
	 * @return	单个属性查询模型是否有效
	 */
	public boolean isValid() {
		return Strings.isNotBlank(this.propertyName);
	}
	
	/**
	 * 将用户请求信息解析为简单属性查询模型
	 * @param request	请求
	 */
	public static SimpleDocQueryModel parseRequest(HttpServletRequest request) {
		String nameAndType = request.getParameter("propertyNameAndType");
		return parseRequest(nameAndType, request);
	}
	
	/**
	 * 根据属性名称、类型及用户请求获取对应的简单属性查询模型
	 * @param nameAndType	属性名称、类型
	 * @param request	请求
	 */
	public static SimpleDocQueryModel parseRequest(String nameAndType, HttpServletRequest request) {
		if(Strings.isNotBlank(nameAndType)) {
			String[] p_t = StringUtils.split(nameAndType, '|');
			if(p_t != null && p_t.length > 0) {
				SimpleDocQueryModel sdm = new SimpleDocQueryModel();
				sdm.setPropertyName(p_t[0]);
				sdm.setPropertyType(NumberUtils.toInt(p_t[1]));
				
				if(sdm.getPropertyType() == Constants.DATE || sdm.getPropertyType() == Constants.DATETIME) {
					sdm.setValue1(request.getParameter(sdm.getPropertyName() + BEGIN_TIME));
					sdm.setParamName1(sdm.getPropertyName() + BEGIN_TIME);
					
					sdm.setValue2(request.getParameter(sdm.getPropertyName() + END_TIME));
					sdm.setParamName2(sdm.getPropertyName() + END_TIME);
				}
				else if(sdm.getPropertyType() == Constants.USER_ID || sdm.getPropertyType() == Constants.DEPT_ID) {
					sdm.setValue1(request.getParameter(sdm.getPropertyName()));
					sdm.setParamName1(sdm.getPropertyName());
					
					sdm.setValue2(request.getParameter(sdm.getPropertyName() + ORG_NAME));
					sdm.setParamName2(sdm.getPropertyName() + ORG_NAME);
				}
				else if(sdm.getPropertyType() == Constants.CONTENT_TYPE) {
					// 历史原因，避免与url中的frType变量重名冲突
					sdm.setValue1(request.getParameter(sdm.getPropertyName() + "Value"));
					sdm.setParamName1(sdm.getPropertyName());
				}
				else {
					sdm.setValue1(request.getParameter(sdm.getPropertyName()));
					sdm.setParamName1(sdm.getPropertyName());
				}
				sdm.setSimple(BooleanUtils.toBoolean(request.getParameter(sdm.getPropertyName() + IS_DEFAULT)));
				return sdm;
			}
		}
		return null;
	}

	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public int getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(int propertyType) {
		this.propertyType = propertyType;
	}
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	public String getValue3() {
		return value3;
	}
	public void setValue3(String value3) {
		this.value3 = value3;
	}
	public String getParamName1() {
		return paramName1;
	}
	public void setParamName1(String paramName1) {
		this.paramName1 = paramName1;
	}
	public String getParamName2() {
		return paramName2;
	}
	public void setParamName2(String paramName2) {
		this.paramName2 = paramName2;
	}
	public String getParamName3() {
		return paramName3;
	}
	public void setParamName3(String paramName3) {
		this.paramName3 = paramName3;
	}
	public boolean isSimple() {
		return simple;
	}
	public void setSimple(boolean simple) {
		this.simple = simple;
	}
}
