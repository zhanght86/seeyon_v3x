package com.seeyon.v3x.formbizconfig.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigShareScope;
/**
 * 表单业务配置共享范围Dao
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public class FormBizConfigShareScopeDao extends BaseHibernateDao<FormBizConfigShareScope>{
	
	/**
	 * 获取业务配置的全部共享范围
	 * @param formBizConfigId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FormBizConfigShareScope> findAll(Long formBizConfigId) {
		String hql = "from " + FormBizConfigShareScope.class.getName() + " as bs where bs.formBizConfigId = ?";
		return this.find(hql, formBizConfigId);
	}
	
	/**
	 * 删除多条表单业务配置的全部共享范围
	 * @param formBizConfigId
	 */
	public void deleteAll(List<Long> bizConfigIds) {
		String hql = "delete from " + FormBizConfigShareScope.class.getName() + " as bs where bs.formBizConfigId in (:bizConfigIds)";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("bizConfigIds", bizConfigIds);
		this.bulkUpdate(hql, parameterMap);
	}
	
	/**
	 * 删除单条表单业务配置的全部共享范围
	 * @param formBizConfigId
	 */
	public void deleteAll(Long bizConfigId) {
		this.delete(new Object[][]{{"formBizConfigId", bizConfigId}});
	}

}
