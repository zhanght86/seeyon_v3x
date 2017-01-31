/**
 * $Id: SalaryTableHelper.java,v 1.2 2007/11/08 10:38:20 wangj Exp $
 * Copyright 2000-2007 北京用友致远软件开发有限公司.
 * All rights reserved.
 *
 *     http://www.seeyon.com
 *
 * SalaryTableHelper.java created by paul at 2007-9-14 下午03:01:29
 *
 */
package com.seeyon.v3x.hr.util;

import java.util.List;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.hr.PagePropertyConstant;
import com.seeyon.v3x.hr.domain.Salary;
import com.seeyon.v3x.hr.webmodel.WebProperty;

/**
 * <tt>SalaryTableHelper</tt>生成每人的工资条，包括<tt>Salary</tt>和<tt>PageProperty</tt>
 * @author paul
 *
 */
public class SalaryTableHelper {
	
	//生成工资条转发协同的标题
	public static String generateSalarySubject(Salary salary) {
		String prefix = ResourceBundleUtil.getString(Constants.RESOURCE_HR, "hr.salary.list.label");
		return prefix +": "+ salary.getName() + "("+ salary.getYear() + "/" + salary.getMonth() + ")";
	}
	
	//生成工资条
	public static String generateSalaryTable(Salary salary, List<WebProperty> properties) {
		StringBuffer table = new StringBuffer();
		StringBuffer th = new StringBuffer();
		StringBuffer tr = new StringBuffer();
		
		//salary TODO 插入工资其他项
		genertateTableTD(th, "姓名");
		genertateTableTD(tr, salary.getName());
		genertateTableTD(th,"工资年月份");
		genertateTableTD(tr, salary.getYear()+"-"+salary.getMonth());
		genertateTableTD(th, "基本工资");
		genertateTableTD(tr, salary.getSalaryBasic());
		genertateTableTD(th, "职位工资");
		genertateTableTD(tr, salary.getSalaryBusiness());
		genertateTableTD(th, "公基金");
		genertateTableTD(tr, salary.getFund());
		genertateTableTD(th, "保险金");
		genertateTableTD(tr, salary.getInsurance());
		genertateTableTD(th, "奖金");
		genertateTableTD(tr, salary.getBonus());
		genertateTableTD(th, "个人所得税");
		genertateTableTD(tr, salary.getIncomeTax());
		genertateTableTD(th, "应发金额");
		genertateTableTD(tr, salary.getSalaryOriginally());
		genertateTableTD(th, "实发金额");
		genertateTableTD(tr, salary.getSalaryActually());
		
		
		//properties
		if(null != properties && !properties.isEmpty()) {
			for(WebProperty property : properties) {
				genertateTableTD(th, property.getLabelName_zh());
				switch(property.getPropertyType()) {
					case PagePropertyConstant.Page_Property_Integer : 
						genertateTableTD(tr, property.getF1());
						break;
					case PagePropertyConstant.Page_Property_Float : 
						genertateTableTD(tr, property.getF2());
						break;
					case PagePropertyConstant.Page_Property_Date : 
						genertateTableTD(tr, property.getF3());
						break;
					case PagePropertyConstant.Page_Property_Varchar : 
						genertateTableTD(tr, property.getF4());
						break;
					default : 
						genertateTableTD(tr, property.getF5());
				}
			}
		}
		
		genertateTableHeader(table);
		genertateTableTR(table, th);
		genertateTableTR(table, tr);
		genertateTableFoot(table);
		return table.toString();
	}
	
	private static void genertateTableHeader(StringBuffer table) {
		table.append("<table>");
	}
	private static void genertateTableTR(StringBuffer table, StringBuffer tr) {
		table.append("<tr>").append(tr).append("</tr>");
	}
	private static void genertateTableTD(StringBuffer tr, Object td) {
		tr.append("<td>").append(td).append("</td>");
	}
	private static void genertateTableFoot(StringBuffer table) {
		table.append("</table>");
	}
}
