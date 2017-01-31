package com.seeyon.v3x.formbizconfig.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigColumn;

/**
 * 栏目挂接项Dao
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public class FormBizConfigColumnDao extends BaseHibernateDao<FormBizConfigColumn> {
	/**
	 * 删除多条表单业务配置对应的栏目挂接项
	 * @param bizConfigIds
	 */
	public void deleteWithBizConfigs(List<Long> bizConfigIds) {
		String hql = "delete from " + FormBizConfigColumn.class.getName() + " as m where m.formBizConfigId in (:bizConfigIds)" ;
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("bizConfigIds", bizConfigIds);
		this.bulkUpdate(hql, parameterMap);
	}
	
	/**
	 * 删除单条表单业务配置对应的栏目挂接项
	 * @param bizConfigId
	 */
	public void deleteWithBizConfig(Long bizConfigId) {
		String hql = "delete from " + FormBizConfigColumn.class.getName() + " as m where m.formBizConfigId = ?" ;
		this.bulkUpdate(hql, null, bizConfigId);		
	}
	
	/**
	 * 获取单条表单业务配置对应的全部栏目挂接项
	 * @param bizConfigId
	 */
	@SuppressWarnings("unchecked")
	public List<FormBizConfigColumn> getColumnsOfBizConfig(Long bizConfigId){
		String hql = "from " + FormBizConfigColumn.class.getName() + " as m where m.formBizConfigId=? order by m.sortId ASC";
		return this.find(hql, bizConfigId);
	}
	
	/**
	 * 在进行表单查询和表单统计操作时，判断对应的查询或统计模板是否已被业务配置创建者从已选项中去除
	 * @param bizConfigId       业务配置ID
	 * @param formId			该模板对应的表单ID
	 * @param queryOrReportName 该模板名称
	 */
	public boolean isQueryOrReportColumnExist(Long bizConfigId, Long formId, String queryOrReportName) {
		String hql = "select count(c.id) from " + FormBizConfigColumn.class.getName() + " as c where c.formBizConfigId=? and c.formId=? and c.name=?";
		int count = ((Integer) this.findUnique(hql, null, new Object[]{bizConfigId, formId, queryOrReportName})).intValue();
		return count==1;
	}
	
}