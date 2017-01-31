package com.seeyon.v3x.formbizconfig.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.domain.FomObjaccess;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.formbizconfig.dao.FormBizConfigColumnDao;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigColumn;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.webmodel.ColumnNodeModel;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * 专注于栏目挂接项相关的业务逻辑处理，减轻{@link FormBizConfigManagerImpl}的压力并将不易单元测试的部分抽离到此中来
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-04-02
 */
public class FormBizConfigColumnManagerImpl implements FormBizConfigColumnManager {
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private FormBizConfigColumnDao formBizConfigColumnDao;
	private TempleteManager templeteManager;
	private OrgManager orgManager;	
	
	public List<FormBizConfigColumn> getSelectedColumns(List<Long> domainIds, Long bizConfigId) throws DataDefineException, BusinessException {
		List<FormBizConfigColumn> columns = this.formBizConfigColumnDao.getColumnsOfBizConfig(bizConfigId);
		if(CollectionUtils.isNotEmpty(columns)) {
			for (Iterator<FormBizConfigColumn> iter = columns.iterator(); iter.hasNext();) {
				FormBizConfigColumn column = (FormBizConfigColumn) iter.next();
				// 如为表单查询和表单统计模板，需进行权限校验
				if(column.isFormQueryOrStatistic() 
						&& !iOperBase.checkAccess4BizConfig(domainIds, column.getFormId(), column.getName(), column.getFormAuthCheckType())) {
					iter.remove();
				}
			}
		}
		return columns;
	}
	
	public boolean validateAuth4QueryOrReport(Long formId, String queryOrReportName,  String type) throws DataDefineException, BusinessException {
		List<Long> userDomainIds = FormBizConfigUtils.getUserDomainIds(this.orgManager);
		int objectType = "query".equals(type) ? FormBizConfigColumn.AUTH_CHECK_QUERY : FormBizConfigColumn.AUTH_CHECK_STATISTIC;
		return this.iOperBase.checkAccess4BizConfig(userDomainIds, formId, queryOrReportName, objectType);
	}
	
	public List<ColumnNodeModel> getQueryAndStatisticInfo(String formTempIds, String from) throws Exception {		
		List<Long> tempIds = FormBizConfigUtils.parseStr2Ids(formTempIds);
		List<Templete> formTemps = this.templeteManager.getTempletesByIds(tempIds);
		List<ColumnNodeModel> result = null;
		
		if(CollectionUtils.isNotEmpty(formTemps)) {
			List<Long> formAppIdList = FormBizConfigUtils.getFormAppIdList(formTemps);
			if(CollectionUtils.isNotEmpty(formAppIdList)) {
				List<Long> domainIds = FormBizConfigUtils.getUserDomainIds(this.orgManager);
				boolean isFromAdmin = FormBizConfigConstants.FLAG_FROM_ADMIN.equalsIgnoreCase(from);
				
				result = new ArrayList<ColumnNodeModel>();
				int category;
				for(Long formId : formAppIdList) {
					List<FomObjaccess> formObjs = this.iOperBase.getFormQueryOrReportNamesByAppId4User(formId, domainIds, isFromAdmin);
					if(CollectionUtils.isNotEmpty(formObjs)) {
						for(FomObjaccess obj : formObjs) {
							int objecttype = obj.getObjecttype();
							// form_objaccess表中存在objecttype为3、4的"不合法"记录，待跟踪确定其产生的深层原因，此处先增加限制条件
							if(objecttype != FormBizConfigColumn.AUTH_CHECK_QUERY && objecttype != FormBizConfigColumn.AUTH_CHECK_STATISTIC)
								continue;
							
							category = objecttype == FormBizConfigColumn.AUTH_CHECK_QUERY ? 
									FormBizConfigConstants.COLUMN_FORM_QUERY : FormBizConfigConstants.COLUMN_FORM_STATISTIC;
							result.add(new ColumnNodeModel(formId, obj.getObjectname(), category));
						}
					}
				}
			}
		}
		return result;
		
	}
	
	public void cloneColumns(Long orginalBizConfigId, Long newBizConfigId) throws CloneNotSupportedException {
		List<FormBizConfigColumn> oldColumns = this.formBizConfigColumnDao.getColumnsOfBizConfig(orginalBizConfigId);
		List<FormBizConfigColumn> newColumns = new ArrayList<FormBizConfigColumn>(oldColumns.size());
		for(FormBizConfigColumn column : oldColumns) {
			FormBizConfigColumn newColumn = (FormBizConfigColumn)column.clone();
			newColumn.setNewId();
			newColumn.setFormBizConfigId(newBizConfigId);
			newColumns.add(newColumn);
		}
		this.formBizConfigColumnDao.savePatchAll(newColumns);
	}
	
	public void saveColumns(String[] idAndCategorys, String[] names, Long bizConfigId) {
		if(!ArrayUtils.isEmpty(idAndCategorys) && !ArrayUtils.isEmpty(names) ) {
			List<FormBizConfigColumn> columns = new ArrayList<FormBizConfigColumn>();
			for(int i=0; i<idAndCategorys.length; i++) {
				String category = idAndCategorys[i];
				// 表单查询或表单统计模板
				if(category.indexOf(",") != -1) {
					String[] result = category.split(",");
					
					columns.add(new FormBizConfigColumn(FormBizConfigConstants.QUERY_OR_STATISTIC_CHILD, Integer.valueOf(result[1]), 
							names[i], i, Long.valueOf(result[0]), bizConfigId));
				} 
				else {
					// 区分表单流程三项二级栏目（父级分类为表单流程）与其他一级栏目
					int parentCategory = Integer.valueOf(category) < FormBizConfigConstants.COLUMN_FORM_FLOW_WAIT ?
							FormBizConfigConstants.PARENT_IS_ROOT : FormBizConfigConstants.COLUMN_FORM_FLOW;
					
					columns.add(new FormBizConfigColumn(Integer.valueOf(category), parentCategory, names[i], i, null, bizConfigId));
				} 
			}
			this.formBizConfigColumnDao.savePatchAll(columns);
		}
	}
	
	public boolean isQueryOrReportColumnExist(Long bizConfigId, Long formId, String queryOrReportName) {
		boolean paramValid = bizConfigId != null && formId != null && Strings.isNotBlank(queryOrReportName);
		return paramValid && this.formBizConfigColumnDao.isQueryOrReportColumnExist(bizConfigId, formId, queryOrReportName);
	}
	
	public void deleteColumns(Long bizConfigId) {
		this.formBizConfigColumnDao.delete(new Object[][]{{"formBizConfigId", bizConfigId}});		
	}
	
	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
	}
	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}
	public void setFormBizConfigColumnDao(FormBizConfigColumnDao formBizConfigColumnDao) {
		this.formBizConfigColumnDao = formBizConfigColumnDao;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
}
