package com.seeyon.v3x.formbizconfig.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigTempletProfile;

/**
 * 表单模板与表单业务配置关系记录Dao
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public class FormBizConfigTempletProfileDao extends BaseHibernateDao<FormBizConfigTempletProfile>{

	/**
	 * 删除单条业务配置与表单模板关系记录
	 * @param bizConfigId
	 */
	public void deleteAll(Long bizConfigId) {
		this.delete(new Object[][]{{"formBizConfigId", bizConfigId}});
	}
	/**
	 * 删除多条业务配置对应的表单模板关系记录
	 * @param bizConfigIds
	 */
	public void deleteAll(List<Long> bizConfigIds) {
		String hql = "delete from " + FormBizConfigTempletProfile.class.getName() + " as p where p.formBizConfigId in (:bizConfigIds)";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("bizConfigIds", bizConfigIds);
		this.bulkUpdate(hql, parameterMap);
	}
	
	/**
	 * 根据业务配置ID获取对应的表单模板中间关系全部记录
	 * @param bizConfigId	业务配置ID
	 */
	@SuppressWarnings("unchecked")
	public List<FormBizConfigTempletProfile> getAll(Long bizConfigId) {
		String hql = "from " + FormBizConfigTempletProfile.class.getName() + " where formBizConfigId=? order by sortId asc";
		return this.find(hql, bizConfigId);
	}
	
}
