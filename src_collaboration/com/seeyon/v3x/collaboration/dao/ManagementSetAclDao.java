/**
 * 
 */
package com.seeyon.v3x.collaboration.dao;

import java.util.List;

import com.seeyon.v3x.collaboration.domain.ManagementSet;
import com.seeyon.v3x.collaboration.domain.ManagementSetAcl;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

/**
 * 类描述：
 * 创建日期：
 *
 * @author Mercurial_lin
 * @version 1.0 
 * @since JDK 5.0
 */
public class ManagementSetAclDao extends BaseHibernateDao<ManagementSetAcl>{

	public List<ManagementSetAcl> findBySetId(long setId){
		String hsql = "from ManagementSetAcl as a where a.managementSetId = ?";
		return super.find(hsql, setId);
	}
	
	public void deleteAclsBySetId(long setId){
		Object[] values = new Object[]{Long.valueOf(setId)};
		String hsql = "delete from ManagementSetAcl as a where a.managementSetId = ?";
		this.getHibernateTemplate().bulkUpdate(hsql, values);
	}
}
