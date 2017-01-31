/**
 * 
 */
package com.seeyon.v3x.collaboration.dao;

import java.util.List;

import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.SuperviseTemplateRole;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class ColSuperviseTemplateRoleDao  extends BaseHibernateDao<SuperviseTemplateRole>{
	
	public List<SuperviseTemplateRole> findRoleByTemplateId(long templateId){
		String hql = "from "+ SuperviseTemplateRole.class.getName() +" as role where role.superviseTemplateId = ?";
		return super.find(hql, null, templateId);
	}
	
	public void deleteAllTemplateRole(long templateId){
		String hql = "delete from " + SuperviseTemplateRole.class.getName() + " as role where role.superviseTemplateId = ?";
		super.bulkUpdate(hql, null, templateId);
	}
	
}
