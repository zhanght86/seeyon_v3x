package www.seeyon.com.v3x.form.controller.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SelectPersonOperation;
import www.seeyon.com.v3x.form.base.condition.ConditionListImpl;
import www.seeyon.com.v3x.form.base.condition.DataColumImpl;
import www.seeyon.com.v3x.form.base.condition.inf.ICondition;
import www.seeyon.com.v3x.form.base.hibernate.SeeyonFormPojo;
import www.seeyon.com.v3x.form.controller.pageobject.FormPage;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.pageobject.Operation;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.query.AbstractQueryObject;
import www.seeyon.com.v3x.form.domain.FomObjaccess;
import www.seeyon.com.v3x.form.manager.define.data.inf.ISeeyonDataSource;
import www.seeyon.com.v3x.form.manager.define.query.QueryColum;
import www.seeyon.com.v3x.form.manager.define.query.QuerySlaveTable;
import www.seeyon.com.v3x.form.manager.define.report.ConditionListReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.ReportDataColum;
import www.seeyon.com.v3x.form.manager.define.report.ReportHeadColum;
import www.seeyon.com.v3x.form.manager.define.report.ReportSumDataColum;
import www.seeyon.com.v3x.form.manager.define.report.SeeyonReportImpl;
import www.seeyon.com.v3x.form.manager.define.report.inf.ISeeyonReport;
import www.seeyon.com.v3x.form.utils.StringUtils;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.taglibs.functions.Functions;
public class ReportObject extends AbstractQueryObject{

	private String reportid;

	private String reportName;

	private String reportArea; // 统计输入范围

	private String reportAreaValue; // 统计输入范围xml字符串
	
	private String customReportField;//自定义查询项
	
	private String customReportFieldValue;//自定义查询项字符串

	private String condition;

	private String conditionvalue;

	private String reportType;

	private String rowHeader;

	private String rowheadValue;

	private String columnHeader;

	private String columnheadValue;

	private String reportDataField; // 统计数据域

	private String reportDataFieldValue;

	private String sumDataField; //合计数据域
	
	private String sumDataFieldValue;
	
	private String reportDescriptions;

	private String form;// 单据名称
	private String formId;//单据Id

	private String operation;// 操作
	private String operationId; //操作Id

	private String descritpion;// 单据描述

	private String reportAuthorName; // 授权显示值

	private String reportAuthorValue;

	private String slavename;
	
	private String mastername;
	
	private Map<String,ReportChartInfo> chartInfos = new LinkedHashMap<String, ReportChartInfo>();

	private List<FomObjaccess> objaccesslist = new ArrayList<FomObjaccess>();

	private ISeeyonReport report;
	
	private boolean changed = false ;
	
	private String filterMasterName = "";//限制条件主表名
	private String filterSlaveName = "";//限制条件从表名
	private String conditionMasterName = "";//用户条件主表名
	private String conditionSlaveName = "";//用户条件从表名
	private String rowHeadMasterName = "";//行头主表名
	private String rowHeadSlaveName = "";//行头从表名
	private String colHeadMasterName = "";//列头主表名
	private String colHeadSlaveName = "";//列头从表名
	private String dataFieldMasterName = "";//查询数据域主表名
	private String dataFieldSlaveName = "";//查询数据域从表名
	private String formname = "";
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportArea(String reportArea) {
		this.reportArea = reportArea;
	}

	public String getReportArea() {
		return reportArea;
	}

	public void setReportAreaValue(String reportAreaValue) {
		this.reportAreaValue = reportAreaValue;
	}

	public String getReportAreaValue() {
		return reportAreaValue;
	}

	public String getCustomReportField() {
		return customReportField;
	}

	public void setCustomReportField(String customReportField) {
		this.customReportField = customReportField;
	}

	public String getCustomReportFieldValue() {
		return customReportFieldValue;
	}

	public void setCustomReportFieldValue(String customReportFieldValue) {
		this.customReportFieldValue = customReportFieldValue;
	}

	public void setcondition(String condition) {
		this.condition = condition;
	}

	public String getcondition() {
		return condition;
	}

	public void setconditionvalue(String conditionvalue) {
		this.conditionvalue = conditionvalue;
	}

	public String getconditionvalue() {
		return conditionvalue;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getReportType() {
		return reportType;
	}

	public void setRowHeader(String rowHeader) {
		this.rowHeader = rowHeader;
	}

	public String getRowHeader() {
		return rowHeader;
	}

	public void setColumnHeader(String columnHeader) {
		this.columnHeader = columnHeader;
	}

	public String getColumnHeader() {
		return columnHeader;
	}

	public void setReportDataField(String reportDataField) {
		this.reportDataField = reportDataField;
	}

	public String getReportDataField() {
		return reportDataField;
	}

	public void setReportDataFieldValue(String reportDataFieldValue) {
		this.reportDataFieldValue = reportDataFieldValue;
	}

	public String getReportDataFieldValue() {
		return reportDataFieldValue;
	}

	public void setReportDescriptions(String reportDescriptions) {
		this.reportDescriptions = reportDescriptions;
	}

	public String getReportDescriptions() {
		return reportDescriptions;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}

	public String getDescritpion() {
		return descritpion;
	}

	public void setDescritpion(String descritpion) {
		this.descritpion = descritpion;
	}

	public void setReportAuthorName(String reportAuthorName) {
		this.reportAuthorName = reportAuthorName;
	}

	public String getReportAuthorName() {
		return reportAuthorName;
	}

	public void setReportAuthorValue(String reportAuthorValue) {
		this.reportAuthorValue = reportAuthorValue;
	}

	public String getReportAuthorValue() {
		return reportAuthorValue;
	}

	public String getColumnheadValue() {
		return columnheadValue;
	}

	public void setColumnheadValue(String columnheadValue) {
		this.columnheadValue = columnheadValue;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
    
	

	public String getSumDataField() {
		return sumDataField;
	}

	public void setSumDataField(String sumDataField) {
		this.sumDataField = sumDataField;
	}

	public String getConditionMasterName() {
		return conditionMasterName;
	}

	public void setConditionMasterName(String conditionMasterName) {
		this.conditionMasterName = conditionMasterName;
	}

	public String getConditionSlaveName() {
		return conditionSlaveName;
	}

	public void setConditionSlaveName(String conditionSlaveName) {
		this.conditionSlaveName = conditionSlaveName;
	}

	public String getConditionvalue() {
		return conditionvalue;
	}

	public void setConditionvalue(String conditionvalue) {
		this.conditionvalue = conditionvalue;
	}

	public String getDataFieldMasterName() {
		return dataFieldMasterName;
	}

	public void setDataFieldMasterName(String dataFieldMasterName) {
		this.dataFieldMasterName = dataFieldMasterName;
	}

	public String getDataFieldSlaveName() {
		return dataFieldSlaveName;
	}

	public void setDataFieldSlaveName(String dataFieldSlaveName) {
		this.dataFieldSlaveName = dataFieldSlaveName;
	}

	public String getFilterMasterName() {
		return filterMasterName;
	}

	public void setFilterMasterName(String filterMasterName) {
		this.filterMasterName = filterMasterName;
	}

	public String getFilterSlaveName() {
		return filterSlaveName;
	}

	public void setFilterSlaveName(String filterSlaveName) {
		this.filterSlaveName = filterSlaveName;
	}


	public String getColHeadMasterName() {
		return colHeadMasterName;
	}

	public void setColHeadMasterName(String colHeadMasterName) {
		this.colHeadMasterName = colHeadMasterName;
	}

	public String getColHeadSlaveName() {
		return colHeadSlaveName;
	}

	public void setColHeadSlaveName(String colHeadSlaveName) {
		this.colHeadSlaveName = colHeadSlaveName;
	}

	public String getRowHeadMasterName() {
		return rowHeadMasterName;
	}

	public void setRowHeadMasterName(String rowHeadMasterName) {
		this.rowHeadMasterName = rowHeadMasterName;
	}

	public String getRowHeadSlaveName() {
		return rowHeadSlaveName;
	}

	public void setRowHeadSlaveName(String rowHeadSlaveName) {
		this.rowHeadSlaveName = rowHeadSlaveName;
	}

	public List<FomObjaccess> getObjaccesslist() {
		return objaccesslist;
	}

	
	
	
	public String getSumDataFieldValue() {
		return sumDataFieldValue;
	}

	public void setSumDataFieldValue(String sumDataFieldValue) {
		this.sumDataFieldValue = sumDataFieldValue;
	}

	public List<FomObjaccess> getObjaccesslist(SessionObject so) throws SeeyonFormException {
		this.objaccesslist.clear();
		genObjAccessList(reportAuthorValue, so.getFormid().longValue(), Integer.valueOf(so.getFormstate()),IPagePublicParam.C_iObjecttype_Report);
		return this.objaccesslist;
	}
	
	public void setObjaccesslist(List<FomObjaccess> objaccesslist) throws BusinessException {
		this.objaccesslist = objaccesslist;
		StringBuffer sbUserValue = new StringBuffer();
		SelectPersonOperation spc = new SelectPersonOperation();
		Map<String,Long> useridmap = new HashMap<String,Long>();
		List<Object[]> list = new ArrayList<Object[]>();//用于userid转换为username 
		for(int i=0;i<objaccesslist.size();i++){
			FomObjaccess foa = (FomObjaccess)objaccesslist.get(i);
			if(foa.getObjectname().equals(StringUtils.Java2XMLStr(this.reportName))){
				String key = foa.getUserid()+ "_" + foa.getObjecttype();
				if(useridmap.size() !=0){
					if(useridmap.get(key) ==null){
						String userName = spc.getNameByTypeIdAndUserId(foa.getUsertype(), foa.getUserid());
						if(userName != null){//如果用户存在
							//用户授权实际值
							if(IPagePublicParam.C_iObjecttype_Report == foa.getObjecttype()){
								sbUserValue.append(spc.getTypeByTypeId(foa.getUsertype())).append("|").append(foa.getUserid()).append(",");
								list.add(new Object[]{spc.getTypeByTypeId(foa.getUsertype()), foa.getUserid()});
							}
							useridmap.put(key, foa.getUserid());
						}
					}else{
						this.objaccesslist.remove(i);
						i = i-1;
					}
				}else{
					String userName = spc.getNameByTypeIdAndUserId(foa.getUsertype(), foa.getUserid());
					if(userName != null){//如果用户存在
						if(IPagePublicParam.C_iObjecttype_Report == foa.getObjecttype()){
							sbUserValue.append(spc.getTypeByTypeId(foa.getUsertype())).append("|").append(foa.getUserid()).append(",");
							list.add(new Object[]{spc.getTypeByTypeId(foa.getUsertype()), foa.getUserid()});
						}
						useridmap.put(key, foa.getUserid());
					}
				}
			}
		}
		
		String authorName1 = Functions.showOrgEntities(list, "、");//进行名称转换,主要用于显示单位简称
		if(!"".equals(sbUserValue.toString())){
			this.reportAuthorValue = sbUserValue.toString().substring(0, sbUserValue.toString().length() - 1);
		}if(authorName1 != null){
			this.reportAuthorName = StringUtils.Java2JavaScriptStr(authorName1);
		}
	}

	public String getRowheadValue() {
		return rowheadValue;
	}

	public void setRowheadValue(String rowheadValue) {
		this.rowheadValue = rowheadValue;
	}

	public String getSlavename() {
		return slavename;
	}

	public void setSlavename(String slavename) {
		this.slavename = slavename;
	}

	public String getReportid() {
		return reportid;
	}

	public void setReportid(String reportid) {
		this.reportid = reportid;
	}

//	产生list,用于数据库form_objaccess存储记录
	/*
	 * user  Member|-7700668784483677330,Team|-8365864659089404545,Post|8944611032511497461
	 * appId 
	 * state 表单状态
	 */
	private void genObjAccessList(String user, long appId, int state, int objectType) throws SeeyonFormException{
		genObjAccessList(user, appId, state, objectType,this.reportName,this.objaccesslist);
	}

	public String getMastername() {
		return mastername;
	}

	public void setMastername(String mastername) {
		this.mastername = mastername;
	}

	public Map<String, ReportChartInfo> getChartInfos() {
		return chartInfos;
	}

	public void setChartInfos(Map<String, ReportChartInfo> chartInfos) {
		this.chartInfos = chartInfos;
	}

	public ISeeyonReport getReport() {
		return report;
	}

//	用于表单查询修改或删除设置条件时使用.给ReportObject的属性赋值

	public void setReport(ISeeyonReport aReport) throws SeeyonFormException {
		SeeyonReportImpl report = (SeeyonReportImpl)aReport;
		ISeeyonDataSource dataSource = report.getDBProvider().getDataSource();//取得数据源
		String masterTableName = report.getQuerySource().getMasterTable();
		
		this.reportid = report.getId();
		this.reportName = report.getReportName();
		this.reportArea = report.getFilter().getDisplay();
		this.reportAreaValue = "<Filter>\r\n" + report.getFilter().getXML() + "</Filter>\r\n";
		for (ICondition fitem  : ((ConditionListImpl)report.getFilter()).getConditionList()){
			if(fitem instanceof DataColumImpl){
				if(((DataColumImpl)fitem).getSys() != null){//如果是审批状态
					this.filterMasterName = masterTableName;
				}else{
					if(SeeyonFormPojo.C_sFieldNamesI18n.contains(getColumName(((DataColumImpl)fitem).getColumName())) || dataSource.findDataAreaByName(getColumName(((DataColumImpl)fitem).getColumName())).getDBTableName().equals(masterTableName)){
						this.filterMasterName = masterTableName;
					}else{
						this.filterSlaveName = dataSource.findDataAreaByName(getColumName(((DataColumImpl)fitem).getColumName())).getDBTableName();
					}
				}
			}
		}		
		
		
		this.condition = report.getUserConditionList().getDisplay();
		this.conditionvalue =  "<UserConditionList>\r\n" + report.getUserConditionList().getXML() + "</UserConditionList>\r\n";
		for (ICondition fitem  : ((ConditionListReportImpl)report.getUserConditionList()).getConditionList()){
			if(fitem instanceof DataColumImpl){
				if(((DataColumImpl)fitem).getSys() != null){//如果是审批状态
					this.conditionMasterName = masterTableName;
				}else{
					if(SeeyonFormPojo.C_sFieldNamesI18n.contains(getColumName(((DataColumImpl)fitem).getColumName())) ||dataSource.findDataAreaByName(getColumName(((DataColumImpl)fitem).getColumName())).getDBTableName().equals(masterTableName)){
						this.conditionMasterName = masterTableName;
					}else{
						this.conditionSlaveName = dataSource.findDataAreaByName(getColumName(((DataColumImpl)fitem).getColumName())).getDBTableName();
					}
				}
			}
		}		
		
		if(report.getSchema().isAcrossReport()){
			//行头赋值
			this.reportType = "1";
			this.rowHeader = "";
			for(ReportHeadColum rhc : report.getSchema().getRowHeadList()){
				if(dataSource.findDataAreaByName(getColumName(rhc.getDataAreaName())).getDBTableName().equals(masterTableName)){
					this.rowHeadMasterName = masterTableName;
				}else{
					this.rowHeadSlaveName = dataSource.findDataAreaByName(getColumName(rhc.getDataAreaName())).getDBTableName();
				}
				this.rowheadValue += rhc.getXml();
				String columName = getColumName(rhc.getDataAreaName());
				if(columName.equals(rhc.getColumTitle())){
					this.rowHeader += getColumName(rhc.getDataAreaName());
				}else{
					this.rowHeader += getColumName(rhc.getDataAreaName());
					this.rowHeader += "(";
					this.rowHeader += rhc.getColumTitle();
					this.rowHeader += ")";
				}
				this.rowHeader +=",";
			}
			if(!rowHeader.equals(""))
				this.rowHeader = this.rowHeader.substring(0,this.rowHeader.length() - 1);
			this.columnheadValue = report.getSchema().getColHead().getXml();
			this.columnHeader = getColumName(report.getSchema().getColHead().getDataAreaName());
			if(dataSource.findDataAreaByName(getColumName(report.getSchema().getColHead().getDataAreaName())).getDBTableName().equals(masterTableName)){
				this.colHeadMasterName = masterTableName;
			}else{
				this.colHeadSlaveName = dataSource.findDataAreaByName(getColumName(report.getSchema().getColHead().getDataAreaName())).getDBTableName();
					
			}
		}else{
			this.reportType = "0";
			this.rowHeader = "";
			for(ReportHeadColum rhc : report.getSchema().getRowHeadList()){
				if(dataSource.findDataAreaByName(getColumName(rhc.getDataAreaName())).getDBTableName().equals(masterTableName)){
					this.rowHeadMasterName = masterTableName;
				}else{
					this.rowHeadSlaveName = dataSource.findDataAreaByName(getColumName(rhc.getDataAreaName())).getDBTableName();
				}
				this.rowheadValue += rhc.getXml();
				String columName = getColumName(rhc.getDataAreaName());
				if(columName.equals(rhc.getColumTitle())){
					this.rowHeader += getColumName(rhc.getDataAreaName());
				}else{
					this.rowHeader += getColumName(rhc.getDataAreaName());
					this.rowHeader += "(";
					this.rowHeader += rhc.getColumTitle();
					this.rowHeader += ")";
				}
				this.rowHeader +=",";
			}
			if(!rowHeader.equals(""))
				this.rowHeader = this.rowHeader.substring(0,this.rowHeader.length() - 1);
		}
		
		
		List<ReportDataColum> dataColumnList = report.getSchema().getDataColumList();
		StringBuffer sbColumn = new StringBuffer();
		StringBuffer sbColumnValue = new StringBuffer();
		sbColumnValue.append("<ShowDataList>\r\n");
		for(ReportDataColum reportColum : dataColumnList){
			if(dataSource.findDataAreaByName(getColumName((reportColum.getDataAreaName()))).getDBTableName().equals(masterTableName)){
				this.dataFieldMasterName = masterTableName;
			}else{
				this.dataFieldSlaveName = dataSource.findDataAreaByName(getColumName((reportColum.getDataAreaName()))).getDBTableName();
			}
			String columName = getColumName(reportColum.getDataAreaName());
			//如果字段名和别名相同,只返回字段名
			if(columName.equals(reportColum.getColumTitle())){
				sbColumn.append(columName); 
			}else{//否则返回 字段名(别名)
				sbColumn.append(columName);
				sbColumn.append("(");
				sbColumn.append(reportColum.getColumTitle());
				sbColumn.append(")");
			}
			sbColumn.append(" ");
			sbColumn.append(ReportDataColum.calctype2Dispy(reportColum.getCalctype()));
			sbColumn.append(",");
			sbColumnValue.append(reportColum.getXml());
		}
		this.reportDataField = sbColumn.toString().substring(0,sbColumn.toString().length() - 1);
		this.reportDataFieldValue = sbColumnValue.append("</ShowDataList>\r\n").toString();
		
		List<QueryColum> reportColumnList = report.getReportColumList();
		if(reportColumnList != null && reportColumnList.size() > 0){
			StringBuffer customColumnValue = new StringBuffer();
			StringBuffer customColumn = new StringBuffer();
			customColumnValue.append("<CustomReportList>\r\n");
			for(QueryColum queryColum : reportColumnList){
				String columName = getColumName(queryColum.getDataAreaName());
				//如果字段名和别名相同,只返回字段名
				if(columName.equals(queryColum.getColumTitle())){
					customColumn.append(columName); 
				}else{//否则返回 字段名(别名)
					customColumn.append(columName);
					customColumn.append("(");
					customColumn.append(queryColum.getColumTitle());
					customColumn.append(")");
				}
				customColumn.append(",");
				customColumnValue.append(queryColum.getXml());
			}
			this.customReportField = customColumn.toString().substring(0,customColumn.toString().length() - 1);
			this.customReportFieldValue = customColumnValue.append("</CustomReportList>\r\n").toString();
		}
		List<ReportSumDataColum> dataSumColumnList = report.getSumDataColumList();
		StringBuffer sbSumColumn = new StringBuffer();
		StringBuffer sbSumColumnValue = new StringBuffer();
		sbSumColumnValue.append("<SumDataList>\r\n");
		for(ReportSumDataColum sumColum : dataSumColumnList){
			
			String columName = getColumName(sumColum.getDataAreaName());
			//如果字段名和别名相同,只返回字段名
			if(columName.equals(sumColum.getColumTitle())){
				sbSumColumn.append(columName); 
			}else{//否则返回 字段名(别名)
				sbSumColumn.append(columName);
				sbSumColumn.append("(");
				sbSumColumn.append(sumColum.getColumTitle());
				sbSumColumn.append(")");
			}
			sbSumColumn.append(" ");
			sbSumColumn.append(ReportDataColum.calctype2Dispy(sumColum.getCalctype()));
			sbSumColumn.append(",");
			sbSumColumnValue.append(sumColum.getXml());
		}
		if(!sbSumColumn.toString().equals(""))
			this.sumDataField = sbSumColumn.toString().substring(0,sbSumColumn.toString().length() - 1);
		else
			this.sumDataField = "";
		this.sumDataFieldValue = sbSumColumnValue.append("</SumDataList>\r\n").toString();
		if(report.getShowDetail() != null){
			this.formId = report.getShowDetail().getFormName();
			this.operationId = report.getShowDetail().getOperName();
		}
		if(report.getDescription() != null){
			this.descritpion = report.getDescription().getDescription();
			this.reportDescriptions = report.getDescription().getDescription();
		}
		this.mastername = report.getQuerySource().getMasterTable();
		if(report.getQuerySource().getSlaveTableList().size() != 0)
		this.slavename = ((QuerySlaveTable)report.getQuerySource().getSlaveTableList().get(0)).getTableName();
	}
	
	
//	public void setQuery(SeeyonReportImpl report) throws SeeyonFormException {
//		
//	}
	
//	去掉数据域名称前面的"my:"
	private String getColumName(String aDataColumName){
		int i = aDataColumName.indexOf(":");
		if(i != -1){
			return aDataColumName.substring(i + 1);
		}else 
			return aDataColumName;
	}
	

	//取得显示明细的显示值
	  /*
	     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
	     * 视图1.操作|视图2.操作  .......
	     * 选择多视图后存入对象中为视图为：视图1|视图2的形式
	     * 操作为：操作1|操作2的形式
	     */ 

	public void getShowDetailDisplay(ReportObject reportObject, List<FormPage> list){

	    String formname = "";
		String opername = "";
	    for(FormPage form: list){
	    	if(reportObject.getFormId() == null) continue;
	    	if(reportObject.getFormId().indexOf("|") ==-1){
	    		if(reportObject.getFormId().equals(form.getFormPageId())){				
	    			reportObject.setForm(form.getName());				
					List<Operation> operation = form.getOperlst();
					for(int i = 0 ; i < operation.size(); i++){
					    Operation oper = (Operation)operation.get(i);
						if(reportObject.getOperationId().equals(oper.getOperationId())){											
							reportObject.setOperation(oper.getName());					
						}							
				    }
			     }
	    	}else{
	    		
	    		for(int i=0;i<reportObject.getFormId().split("\\|").length;i++){
					if(reportObject.getFormId().split("\\|")[i].equals(form.getFormPageId())){
						formname += form.getName() + "|";
						List<Operation> operation = form.getOperlst();
						for(int y = 0 ; y < operation.size(); y++){
						    Operation oper = (Operation)operation.get(y);
						    for(int a=0;a<reportObject.getOperationId().split("\\|").length;a++)
							if(reportObject.getOperationId().split("\\|")[a].equals(oper.getOperationId())){											
								reportObject.setOperation(oper.getName());
                                opername += oper.getName()+ "|";
							}							
					    }
					}
				}
	    		
	    	}
			
	    }
	    if(!"".equals(formname)){
	    	reportObject.setForm(formname);
	    	reportObject.setOperation(opername);	
	    }
	    
	}

	public String getFormname() {
		return formname;
	}

	public void setFormname(String formname) {
		this.formname = formname;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	} 
}
