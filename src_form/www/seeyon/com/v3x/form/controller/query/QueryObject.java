package www.seeyon.com.v3x.form.controller.query;        

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import www.seeyon.com.v3x.form.domain.FomObjaccess;
import www.seeyon.com.v3x.form.manager.define.data.inf.ISeeyonDataSource;
import www.seeyon.com.v3x.form.manager.define.query.ConditionListQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.OrderByColum;
import www.seeyon.com.v3x.form.manager.define.query.QueryColum;
import www.seeyon.com.v3x.form.manager.define.query.QuerySlaveTable;
import www.seeyon.com.v3x.form.manager.define.query.SeeyonQueryImpl;
import www.seeyon.com.v3x.form.manager.define.query.inf.ISeeyonQuery;
import www.seeyon.com.v3x.form.utils.StringUtils;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.taglibs.functions.Functions;
/**
* 用于存储查询条件对象,所有与查询条件相关对象都保存在此类中


* 
*/
public class QueryObject extends AbstractQueryObject{
	
	private String  queryId;
	private String queryName;//查询名称
	private String queryArea;//查询数据范围
	private String queryAreaValue;//查询数据范围字符串


	private String queryCondition;//用户输入条件
	private String queryConditionValue;//用户输入条件字符串
	
	private String customQueryField;//自定义查询项
	private String customQueryFieldValue;//自定义查询项字符串


	private String dataField;//查询数据域


	private String dataFieldValue;//查询数据域字符串
	private String resultSort;//查询结果排序
	private String resultSortValue;//查询结果排序字符串


    private String form;//单据名称
	private String operation;//操作
	private String formId;//单据Id
    private String operationId; //操作Id

	private String descritpion;//单据描述
	private String authorName;//授权显示值

  	private String authorValue;//授权实际值

    private String slavename;
	
	private String mastername;
	
	private String filterMasterName = "";//限制条件主表名
	private String filterSlaveName = "";//限制条件从表名
	private String conditionMasterName = "";//用户条件主表名
	private String conditionSlaveName = "";//用户条件从表名
	private String dataFieldMasterName = "";//查询数据域主表名
	private String dataFieldSlaveName = "";//查询数据域从表名
	
	private String formname = "";
	
	private boolean changed = false;
  	
  	private List<FomObjaccess> objAccessList = new ArrayList<FomObjaccess>();//
  	private ISeeyonQuery query;
  	
	private String queryXML;
	
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	
	public String getAuthorValue() {
		return authorValue;
	}
	public void setAuthorValue(String authorValue) {
		this.authorValue = authorValue;
	}

	public String getDataField() {
		return dataField;
	}
	public void setDataField(String dataField) {
		this.dataField = dataField;
	}
	public String getDescritpion() {
		return descritpion;
	}
	public void setDescritpion(String descritpion) {
		this.descritpion = descritpion;
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

	public String getQueryArea() {
		return queryArea;
	}
	public void setQueryArea(String queryArea) {
		this.queryArea = queryArea;
	}
	public String getQueryCondition() {
		return queryCondition;
	}
	public void setQueryCondition(String queryCondition) {
		this.queryCondition = queryCondition;
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
	public String getQueryName() {
		return queryName;
	}
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	public String getResultSort() {
		return resultSort;
	}
	public void setResultSort(String resultSort) {
		this.resultSort = resultSort;
	}
	public String getDataFieldValue() {
		return dataFieldValue;
	}
	public void setDataFieldValue(String dataFieldValue) {
		this.dataFieldValue = dataFieldValue;
	}
	public String getQueryAreaValue() {
		return queryAreaValue;
	}
	public void setQueryAreaValue(String queryAreaValue) {
		this.queryAreaValue = queryAreaValue;
	}
	public String getQueryConditionValue() {
		return queryConditionValue;
	}
	public void setQueryConditionValue(String queryConditionValue) {
		this.queryConditionValue = queryConditionValue;
	}
	public String getResultSortValue() {
		return resultSortValue;
	}
	public void setResultSortValue(String resultSortValue) {
		this.resultSortValue = resultSortValue;
	}
	
	
	public String getQueryId() {
		return queryId;
	}
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	public String getQueryXML() {
		return queryXML;
	}
	public void setQueryXML(String queryXML) { 
		this.queryXML = queryXML;
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
	
	
	public List<FomObjaccess> getObjAccessList() {
		return objAccessList;
	}
	public void setQuery(ISeeyonQuery query) {
		this.query = query;
	}
	public List<FomObjaccess> getObjAccessList(SessionObject so) throws SeeyonFormException {
		this.objAccessList.clear();
		genObjAccessList(authorValue, so.getFormid().longValue(), Integer.valueOf(so.getFormstate()), IPagePublicParam.C_iObjecttype_Query);
		return this.objAccessList;
		
	}
	
	////用于表单查询修改或删除设置条件时使用.给QueryObject的属性赋值 还原授权信息
	public void setObjAccessList(List<FomObjaccess> objAccessList) throws BusinessException {
		this.objAccessList = objAccessList;
		StringBuffer sbUserValue = new StringBuffer();
		SelectPersonOperation spc = new SelectPersonOperation();
		Map<String,Long> useridmap = new HashMap<String,Long>();
		List<Object[]> list = new ArrayList<Object[]>();//用于userid转换为username 
		for(int i=0;i<objAccessList.size();i++){
			FomObjaccess foa = (FomObjaccess)objAccessList.get(i);
			if(foa.getObjectname().equals(StringUtils.Java2XMLStr(this.queryName))){
				String key = foa.getUserid()+ "_" + foa.getObjecttype();
				if(useridmap.size() !=0){
					if(useridmap.get(key) ==null){
						String userName = spc.getNameByTypeIdAndUserId(foa.getUsertype(), foa.getUserid());
						if(userName != null){//如果用户存在
							//用户授权实际值
							if(IPagePublicParam.C_iObjecttype_Query == foa.getObjecttype()){
								sbUserValue.append(spc.getTypeByTypeId(foa.getUsertype())).append("|").append(foa.getUserid()).append(",");
								list.add(new Object[]{spc.getTypeByTypeId(foa.getUsertype()), foa.getUserid()});								
							}
							useridmap.put(key, foa.getUserid());
						}
					}
				}else{
					String userName = spc.getNameByTypeIdAndUserId(foa.getUsertype(), foa.getUserid());
					if(userName != null){//如果用户存在
						//用户授权实际值
						if(IPagePublicParam.C_iObjecttype_Query == foa.getObjecttype()){
							sbUserValue.append(spc.getTypeByTypeId(foa.getUsertype())).append("|").append(foa.getUserid()).append(",");
							list.add(new Object[]{spc.getTypeByTypeId(foa.getUsertype()), foa.getUserid()});								
						}
						useridmap.put(key, foa.getUserid());
					}
				}	
			}
			
		}
		String authorName = Functions.showOrgEntities(list, "、");//进行名称转换,主要用于显示单位简称
		if(!"".equals(sbUserValue.toString())){
			this.authorValue = sbUserValue.toString().substring(0, sbUserValue.toString().length() - 1);
		}if(authorName != null){
			this.authorName = StringUtils.Java2JavaScriptStr(authorName);
		}	
	}
	
	public ISeeyonQuery getQuery() {
		return query;
	}
	
	
	//用于表单查询修改或删除设置条件时使用.给QueryObject的属性赋值


	public void setQuery(SeeyonQueryImpl query) throws SeeyonFormException {
	
		ISeeyonDataSource dataSource = query.getDBProvider().getDataSource();//取得数据源
		String masterTableName = query.getQuerySource().getMasterTable();
		
		this.queryId = query.getId();
		this.queryName = query.getQueryName();
		this.queryArea = query.getFilter().getDisplay();
		this.queryAreaValue = "<Filter>\r\n" + query.getFilter().getXML() + "</Filter>\r\n";
		for (ICondition fitem  : ((ConditionListImpl)query.getFilter()).getConditionList()){
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
		

		this.queryCondition = query.getUserConditionList().getDisplay();
		this.queryConditionValue =  "<UserConditionList>\r\n" + query.getUserConditionList().getXML() + "</UserConditionList>\r\n";
		for (ICondition fitem  : ((ConditionListQueryImpl)query.getUserConditionList()).getConditionList()){
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
		
		List<QueryColum> dataColumnList = query.getDataColumList();
		StringBuffer sbColumn = new StringBuffer();
		StringBuffer sbColumnValue = new StringBuffer();
		sbColumnValue.append("<ShowDataList>\r\n");
		for(QueryColum queryColum : dataColumnList){
			if(SeeyonFormPojo.C_sFieldNames.contains(queryColum.getDataAreaName()) 
				    || dataSource.findDataAreaByName(getColumName((queryColum.getDataAreaName()))).getDBTableName().equals(masterTableName)){
				this.dataFieldMasterName = masterTableName;
			}else{
				this.dataFieldSlaveName = dataSource.findDataAreaByName(getColumName((queryColum.getDataAreaName()))).getDBTableName();
			}
			String columName = getColumName(queryColum.getDataAreaName());
			//如果字段名和别名相同,只返回字段名
			if(columName.equals(queryColum.getColumTitle())){
				sbColumn.append(columName); 
			}else{//否则返回 字段名(别名)
				sbColumn.append(columName);
				sbColumn.append("(");
				sbColumn.append(queryColum.getColumTitle());
				sbColumn.append(")");
			}
			sbColumn.append(",");
			sbColumnValue.append(queryColum.getXml());
		}
		this.dataField = sbColumn.toString().substring(0,sbColumn.toString().length() - 1);
		this.dataFieldValue = sbColumnValue.append("</ShowDataList>\r\n").toString();
		
		List<QueryColum> queryColumnList = query.getQueryColumList();
		if(queryColumnList != null && queryColumnList.size() > 0){
			StringBuffer customColumnValue = new StringBuffer();
			StringBuffer customColumn = new StringBuffer();
			customColumnValue.append("<CustomQueryList>\r\n");
			for(QueryColum queryColum : queryColumnList){
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
			this.customQueryField = customColumn.toString().substring(0,customColumn.toString().length() - 1);
			this.customQueryFieldValue = customColumnValue.append("</CustomQueryList>\r\n").toString();
			
		}
		List<OrderByColum> orderByList = query.getOrderByList();
		StringBuffer sbOrderBy = new StringBuffer();
		StringBuffer sbOrderByValue = new StringBuffer();
		sbOrderByValue.append("<OrderBy>\r\n");
		for(OrderByColum orderByColum : orderByList){
			if(orderByColum.getType() == 0){
				sbOrderBy.append(getColumName(orderByColum.getColmunName()));
				sbOrderBy.append("↑");
			}else{//否则返回 字段名(别名)
				sbOrderBy.append(getColumName(orderByColum.getColmunName()));
				sbOrderBy.append("↓");
			}
			sbOrderBy.append(",");
			
			sbOrderByValue.append(orderByColum.getXml());
		}
		sbOrderByValue.append("</OrderBy>\r\n");
		this.resultSortValue = sbOrderByValue.toString();
		if(!sbOrderBy.toString().equals(""))
			this.resultSort = sbOrderBy.toString().substring(0,sbOrderBy.toString().length() - 1);
		else
			this.resultSort = "";
		if(query.getShowDetail() != null){
			this.formId = query.getShowDetail().getFormName();
			this.operationId = query.getShowDetail().getOperName();
		}
		if(query.getDescription() != null){
			this.descritpion = query.getDescription().getDescription();
		}
		this.mastername = query.getQuerySource().getMasterTable();
		if(query.getQuerySource().getSlaveTableList().size() != 0)
			this.slavename = ((QuerySlaveTable)query.getQuerySource().getSlaveTableList().get(0)).getTableName();
	}
	
	
	
	
	//产生list,用于数据库form_objaccess存储记录
	/*
	 * user  Member|-7700668784483677330,Team|-8365864659089404545,Post|8944611032511497461
	 * appId 
	 * state 表单状态


	 */
	private void genObjAccessList(String user, long appId, int state,int objectType) throws SeeyonFormException{
		genObjAccessList(user, appId, state, objectType,this.queryName,this.objAccessList);
	}

	//去掉数据域名称前面的"my:"
	private String getColumName(String aDataColumName){
		int i = aDataColumName.indexOf(":");
		if(i != -1){
			return aDataColumName.substring(i + 1);
		}else 
			return aDataColumName;
	}
	public String getMastername() {
		return mastername;
	}
	public void setMastername(String mastername) {
		this.mastername = mastername;
	}
	public String getSlavename() {
		return slavename;
	}
	public void setSlavename(String slavename) {
		this.slavename = slavename;
	}

	//取得显示明细的显示值
	  /*
	     * 对showdetail特殊处理,5235235003193116833.2682886799685403010|3083749983331502103.-8529532647144603086|
	     * 视图1.操作|视图2.操作  .......
	     * 选择多视图后存入对象中为视图为：视图1|视图2的形式
	     * 操作为：操作1|操作2的形式
	     */        
	public void getShowDetailDisplay(QueryObject qo, List<FormPage> list){
		String formname = "";
		String opername = "";
	    for(FormPage form: list){
	    	if(qo.getFormId() == null) continue;
	    	if(qo.getFormId().indexOf("|") ==-1){
	    		if(qo.getFormId().equals(form.getFormPageId())){				
					qo.setForm(form.getName());				
					List<Operation> operation = form.getOperlst();
					for(int i = 0 ; i < operation.size(); i++){
					    Operation oper = (Operation)operation.get(i);
						if(qo.getOperationId().equals(oper.getOperationId())){											
							qo.setOperation(oper.getName());					
						}							
				    }
			     }
	    	}else{
	    		for(int i=0;i<qo.getFormId().split("\\|").length;i++){
					if(qo.getFormId().split("\\|")[i].equals(form.getFormPageId())){
						formname += form.getName() + "|";
						List<Operation> operation = form.getOperlst();
						for(int y = 0 ; y < operation.size(); y++){
						    Operation oper = (Operation)operation.get(y);
						    for(int a=0;a<qo.getOperationId().split("\\|").length;a++)
							if(qo.getOperationId().split("\\|")[a].equals(oper.getOperationId())){											
								qo.setOperation(oper.getName());
                                opername += oper.getName()+ "|";
							}							
					    }
					}
				}
	    		
	    	}
			
	    }
	    if(!"".equals(formname)){
	    	qo.setForm(formname);
			qo.setOperation(opername);	
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
