package com.seeyon.v3x.formbizconfig.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigMenuProfile;
/**
 * 菜单挂接项与表单业务配置关系记录Dao
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public class FormBizConfigMenuProfileDao extends BaseHibernateDao<FormBizConfigMenuProfile>{
	
	/**
	 * 删除多条业务配置对应的菜单关系记录
	 * @param bizConfigIds
	 */
	public void deleteAll(List<Long> bizConfigIds) {
		String hql = "delete from " + FormBizConfigMenuProfile.class.getName() + " as p where p.formBizConfigId in (:bizConfigIds)";
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("bizConfigIds", bizConfigIds);
		this.bulkUpdate(hql, parameterMap);
	}
	
	/**
	 * 删除单条业务配置对应的二级菜单关系记录
	 * @param bizConfigId   业务配置ID
	 * @param mainMenuId    业务配置对应的一级菜单ID
	 */
	public void deleteSubMenuProfiles(Long bizConfigId, Long mainMenuId) {
		String hql = "delete from " + FormBizConfigMenuProfile.class.getName() + " as p where p.formBizConfigId=? and p.menuId!=?";
		this.bulkUpdate(hql, null, bizConfigId, mainMenuId);
	}
	
}
